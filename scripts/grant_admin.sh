#!/bin/bash
# Назначает ROLE_ADMIN пользователю по email (локальный Docker dev).
# Использование: ./scripts/grant_admin.sh [email]

set -euo pipefail

EMAIL="${1:-opolittt@gmail.com}"

if ! docker ps --format '{{.Names}}' | grep -qx 'db'; then
  echo "Контейнер db не запущен. Запустите: docker compose up -d db"
  exit 1
fi

DB_USER="$(docker exec db cat /run/secrets/db_user)"
DB_NAME="$(docker exec db cat /run/secrets/db_name)"

docker exec db psql -U "$DB_USER" -d "$DB_NAME" -c \
  "UPDATE customer SET role = 'ROLE_ADMIN' WHERE email = '${EMAIL}';"

echo "Роль ROLE_ADMIN назначена для ${EMAIL}"
