#!/bin/sh
set -e

export VAULT_ADDR="http://vault:8200"
KEYS_DIR="/vault/keys"
SECRETS_DIR="/run/secrets"
INIT_SECRETS="/vault-config/init-secrets.env"

mkdir -p "$KEYS_DIR" "$SECRETS_DIR"

# ── Wait for Vault to become reachable ──
echo "==> Waiting for Vault to start..."
while true; do
    # vault status returns: 0=unsealed, 1=error, 2=sealed
    # || true prevents set -e from killing the script
    rc=0
    vault status > /dev/null 2>&1 || rc=$?
    if [ $rc -eq 0 ] || [ $rc -eq 2 ]; then
        break
    fi
    sleep 1
done
echo "==> Vault is reachable."

# ── Initialize if needed ──
FIRST_INIT=false
if vault status 2>&1 | grep -q "Initialized.*false"; then
    echo "==> Initializing Vault (1 key share, 1 key threshold)..."
    INIT_OUTPUT=$(vault operator init -key-shares=1 -key-threshold=1)

    UNSEAL_KEY=$(echo "$INIT_OUTPUT" | grep "Unseal Key 1:" | awk '{print $NF}')
    ROOT_TOKEN=$(echo "$INIT_OUTPUT" | grep "Initial Root Token:" | awk '{print $NF}')

    echo "$UNSEAL_KEY" > "$KEYS_DIR/unseal-key"
    echo "$ROOT_TOKEN" > "$KEYS_DIR/root-token"
    FIRST_INIT=true
    echo "==> Vault initialized (first run)."
else
    echo "==> Vault already initialized."
    UNSEAL_KEY=$(cat "$KEYS_DIR/unseal-key")
    ROOT_TOKEN=$(cat "$KEYS_DIR/root-token")
fi

# ── Unseal if needed ──
if vault status 2>&1 | grep -q "Sealed.*true"; then
    echo "==> Unsealing Vault..."
    vault operator unseal "$UNSEAL_KEY" > /dev/null
    echo "==> Vault unsealed."
else
    echo "==> Vault already unsealed."
fi

# ── Authenticate ──
export VAULT_TOKEN="$ROOT_TOKEN"

# ── Enable KV v2 secrets engine (idempotent) ──
vault secrets enable -path=secret -version=2 kv > /dev/null 2>&1 || true

# ── Create restricted policy for the application ──
echo "==> Creating application policy and token..."
vault policy write domsommelier-app - > /dev/null <<EOF
path "secret/data/domsommelier" {
  capabilities = ["read", "list"]
}
path "secret/data/domsommelier/*" {
  capabilities = ["read", "list"]
}
path "secret/metadata/domsommelier" {
  capabilities = ["read", "list"]
}
path "secret/metadata/domsommelier/*" {
  capabilities = ["read", "list"]
}
EOF

# Create a periodic token (auto-renewable by Spring Cloud Vault)
APP_TOKEN=$(vault token create -policy=domsommelier-app -period=768h -field=token)
echo -n "$APP_TOKEN" > "$SECRETS_DIR/vault_token"

# ══════════════════════════════════════════════════════════════
# Populate Vault from init-secrets.env ONLY on first init.
# On subsequent restarts — read existing secrets from Vault.
# ══════════════════════════════════════════════════════════════

if [ "$FIRST_INIT" = "true" ]; then
    # ── First run: seed Vault from init-secrets.env ──
    if [ ! -f "$INIT_SECRETS" ]; then
        echo ""
        echo "============================================"
        echo "  ERROR: First init but no init-secrets.env"
        echo "============================================"
        echo ""
        echo "  Create vault/init-secrets.env with:"
        echo "    DB_USER, DB_PASSWORD, DB_NAME,"
        echo "    MINIO_ROOT_USER, MINIO_ROOT_PASSWORD,"
        echo "    MINIO_ACCESS_NAME, MINIO_ACCESS_SECRET,"
        echo "    MAIL_HOST, MAIL_PORT, MAIL_USERNAME,"
        echo "    MAIL_PASSWORD, MAIL_PROTOCOL,"
        echo "    JWT_SECRET"
        echo ""
        echo "============================================"
        exit 1
    fi

    echo "==> First init: writing secrets from init-secrets.env to Vault..."
    . "$INIT_SECRETS"

    vault kv put secret/domsommelier \
        spring.datasource.username="$DB_USER" \
        spring.datasource.password="$DB_PASSWORD" \
        spring.mail.host="$MAIL_HOST" \
        spring.mail.port="$MAIL_PORT" \
        spring.mail.username="$MAIL_USERNAME" \
        spring.mail.password="$MAIL_PASSWORD" \
        spring.mail.protocol="$MAIL_PROTOCOL" \
        spring.mail.from="$MAIL_USERNAME" \
        minio.access.name="$MINIO_ACCESS_NAME" \
        minio.access.secret="$MINIO_ACCESS_SECRET" \
        minio.access-key="$MINIO_ACCESS_NAME" \
        minio.secret-key="$MINIO_ACCESS_SECRET" \
        minio.root.user="$MINIO_ROOT_USER" \
        minio.root.password="$MINIO_ROOT_PASSWORD" \
        db.name="$DB_NAME" \
        jwt.secret="$JWT_SECRET" > /dev/null

    echo "==> Generating secret files for infrastructure containers..."
    echo -n "$DB_USER" > "$SECRETS_DIR/db_user"
    echo -n "$DB_PASSWORD" > "$SECRETS_DIR/db_password"
    echo -n "$DB_NAME" > "$SECRETS_DIR/db_name"
    echo -n "$MINIO_ROOT_USER" > "$SECRETS_DIR/minio_root_user"
    echo -n "$MINIO_ROOT_PASSWORD" > "$SECRETS_DIR/minio_root_password"
    chmod 644 "$SECRETS_DIR"/*

else
    # ── Subsequent run: read secrets from Vault ──
    echo "==> Reading existing secrets from Vault..."

    if ! vault kv get secret/domsommelier > /dev/null 2>&1; then
        echo ""
        echo "============================================"
        echo "  SECRETS NOT FOUND IN VAULT"
        echo "============================================"
        echo ""
        echo "  Vault is initialized but has no secrets."
        echo "  To re-seed, delete the vault-data volume"
        echo "  and restart with init-secrets.env:"
        echo ""
        echo "    docker compose down -v"
        echo "    docker compose up"
        echo ""
        echo "  Or add secrets manually via Vault UI:"
        echo "    http://localhost:8200"
        echo "    Root token: $ROOT_TOKEN"
        echo ""
        echo "============================================"
        exit 1
    fi

    echo "==> Generating secret files from Vault..."
    vault kv get -field=spring.datasource.username secret/domsommelier > "$SECRETS_DIR/db_user"
    vault kv get -field=spring.datasource.password secret/domsommelier > "$SECRETS_DIR/db_password"
    vault kv get -field=db.name secret/domsommelier > "$SECRETS_DIR/db_name"
    vault kv get -field=minio.root.user secret/domsommelier > "$SECRETS_DIR/minio_root_user"
    vault kv get -field=minio.root.password secret/domsommelier > "$SECRETS_DIR/minio_root_password"
    chmod 644 "$SECRETS_DIR"/*
fi

echo ""
echo "============================================"
echo "==> Vault setup complete!"
echo "==> Secrets path: secret/domsommelier"
echo "==> Vault UI: http://localhost:8200"
echo "==> Root token: $ROOT_TOKEN"
echo "============================================"
