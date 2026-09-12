#!/usr/bin/env bash
# Additive catalog upgrade with a database backup. Run from the backend repository.
set -euo pipefail
umask 077
if docker compose version >/dev/null 2>&1; then
  compose=(docker compose)
else
  compose=(docker-compose)
fi
backup_dir="${CATALOG_BACKUP_DIR:-$HOME/backups/domsommelier}"
mkdir -p "$backup_dir"
backup_file="$backup_dir/catalog-$(date -u +%Y%m%dT%H%M%SZ)-$$.dump"
"${compose[@]}" exec -T db sh -c 'exec pg_dump -Fc -U "$(cat /run/secrets/db_user)" -d "$(cat /run/secrets/db_name)"' > "$backup_file"
test -s "$backup_file"
"${compose[@]}" exec -T db pg_restore --list < "$backup_file" > /dev/null
printf 'Database backup: %s\n' "$backup_file"
for migration in \
  infrastructure/db/migrations/20260912_product_attributes.sql \
  infrastructure/db/migrations/20260912_catalog_filter_indexes.sql; do
  "${compose[@]}" exec -T db sh -c 'exec psql -v ON_ERROR_STOP=1 -U "$(cat /run/secrets/db_user)" -d "$(cat /run/secrets/db_name)"' < "$migration"
done
