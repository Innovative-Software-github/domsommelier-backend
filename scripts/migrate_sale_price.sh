#!/bin/bash
# Переносит product.discount -> product.sale_price (акционная цена в рублях).
#
# Зачем нужен отдельный скрипт: ddl-auto=update умеет только ДОБАВИТЬ колонку sale_price,
# но не переименовать discount и не перенести данные — старые акции остались бы висеть
# в неиспользуемой колонке, а на витрине пропали бы.
#
# Значения переносятся как есть: фронт и раньше показывал discount как готовую цену
# (useProductPrice: currentPrice = discount), так что витрина не изменится.
# Нули и отрицательные значения превращаются в NULL («акции нет»).
#
# Запускать ОДИН РАЗ после деплоя бэкенда с колонкой sale_price.
# Использование: ./scripts/migrate_sale_price.sh [--drop-old]
#   --drop-old — дополнительно удалить колонку discount (только когда убедились, что всё ок)

set -euo pipefail

DROP_OLD="${1:-}"

if ! docker ps --format '{{.Names}}' | grep -qx 'db'; then
  echo "Контейнер db не запущен. Запустите: docker compose up -d db"
  exit 1
fi

DB_USER="$(docker exec db cat /run/secrets/db_user)"
DB_NAME="$(docker exec db cat /run/secrets/db_name)"

psql_exec() {
  docker exec db psql -U "$DB_USER" -d "$DB_NAME" -v ON_ERROR_STOP=1 -c "$1"
}

# Колонки discount уже может не быть — тогда миграция была выполнена ранее.
HAS_OLD="$(docker exec db psql -U "$DB_USER" -d "$DB_NAME" -tAc \
  "SELECT count(*) FROM information_schema.columns WHERE table_name='product' AND column_name='discount';")"

if [ "$HAS_OLD" = "0" ]; then
  echo "Колонки product.discount нет — миграция уже выполнена."
  exit 0
fi

psql_exec "ALTER TABLE product ADD COLUMN IF NOT EXISTS sale_price numeric(12,2);"

psql_exec "UPDATE product
              SET sale_price = discount
            WHERE discount IS NOT NULL
              AND discount > 0
              AND sale_price IS NULL;"

echo "Перенесено в sale_price:"
docker exec db psql -U "$DB_USER" -d "$DB_NAME" -tAc \
  "SELECT count(*) FROM product WHERE sale_price IS NOT NULL;"

if [ "$DROP_OLD" = "--drop-old" ]; then
  psql_exec "ALTER TABLE product DROP COLUMN discount;"
  echo "Колонка product.discount удалена."
else
  echo "Колонка product.discount оставлена. Удалить: ./scripts/migrate_sale_price.sh --drop-old"
fi
