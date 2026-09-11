#!/bin/bash
# Сценарии: персональные скидки клиентов (docs/DISCOUNTS_PLAN.md).
#
# Проверяет главное правило: личная скидка НЕ складывается с акционной ценой —
# начисляется только на позиции без акции, и фиксируется снапшотом в заказе.
#
# Скрипт самодостаточный: заводит своих клиентов (покупателя и админа), ставит акцию
# на товар и в конце возвращает всё как было. Требует поднятый локальный стек:
#   docker compose up -d

SCRIPTS_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPTS_DIR/common.sh"

print_header "DISCOUNTS — Персональные скидки"

BUYER_EMAIL="discount-buyer@test.local"
ADMIN_EMAIL="discount-admin@test.local"
PERCENT=10

DB_USER="$(docker exec db cat /run/secrets/db_user 2>/dev/null)"
DB_NAME="$(docker exec db cat /run/secrets/db_name 2>/dev/null)"
if [ -z "$DB_USER" ]; then
  echo -e "${RED}Контейнер db не запущен. Запустите: docker compose up -d${NC}"
  exit 1
fi

psql_q() { docker exec db psql -U "$DB_USER" -d "$DB_NAME" -tAc "$1"; }

# --- Подготовка -------------------------------------------------------------

# Два товара в наличии: одному ставим акцию, другому нет.
read -r PLAIN_ID PLAIN_PRICE < <(psql_q "
  SELECT p.id || ' ' || p.price FROM product p
   WHERE EXISTS (SELECT 1 FROM product_stock s WHERE s.product_id = p.id AND s.quantity > 2)
     AND p.sale_price IS NULL
   ORDER BY p.price DESC LIMIT 1;")
read -r SALE_ID SALE_BASE < <(psql_q "
  SELECT p.id || ' ' || p.price FROM product p
   WHERE EXISTS (SELECT 1 FROM product_stock s WHERE s.product_id = p.id AND s.quantity > 2)
     AND p.sale_price IS NULL AND p.id <> '$PLAIN_ID'
   ORDER BY p.price DESC LIMIT 1;")

if [ -z "$PLAIN_ID" ] || [ -z "$SALE_ID" ]; then
  echo -e "${RED}Не нашлось двух товаров с остатками. Засейте данные: DB_INIT_MODE=true${NC}"
  exit 1
fi

# Акционная цена — на 10% ниже прайса.
SALE_PRICE=$(python3 -c "print(round(float('$SALE_BASE') * 0.9, 2))")
psql_q "UPDATE product SET sale_price = $SALE_PRICE WHERE id = '$SALE_ID';" > /dev/null
echo -e "  ${YELLOW}Акция:${NC} товар $SALE_ID  $SALE_BASE -> $SALE_PRICE"

cleanup() {
  psql_q "UPDATE product SET sale_price = NULL WHERE id = '$SALE_ID';" > /dev/null
  psql_q "UPDATE customer SET discount_percent = NULL, discount_comment = NULL
           WHERE email = '$BUYER_EMAIL';" > /dev/null
  echo -e "\n  ${YELLOW}Тестовые данные откачены (акция снята, скидка снята).${NC}"
}
trap cleanup EXIT

BUYER_TOKEN=$(bash "$SCRIPTS_DIR/get_token.sh" "$BUYER_EMAIL" 2>/dev/null)
if [ -z "$BUYER_TOKEN" ]; then
  echo -e "${RED}Не удалось авторизовать покупателя. Прерываем.${NC}"
  exit 1
fi
BUYER_AUTH="Authorization: Bearer $BUYER_TOKEN"
BUYER_ID=$(psql_q "SELECT id FROM customer WHERE email = '$BUYER_EMAIL';")

bash "$SCRIPTS_DIR/get_token.sh" "$ADMIN_EMAIL" > /dev/null 2>&1
psql_q "UPDATE customer SET role = 'ROLE_ADMIN' WHERE email = '$ADMIN_EMAIL';" > /dev/null
ADMIN_TOKEN=$(bash "$SCRIPTS_DIR/get_token.sh" "$ADMIN_EMAIL" 2>/dev/null)
ADMIN_AUTH="Authorization: Bearer $ADMIN_TOKEN"

curl -s -X POST "$BASE_URL/api/v1/basket/$BUYER_ID/clear" -H "$BUYER_AUTH" > /dev/null

# --- 1. Админ назначает скидку ---------------------------------------------
BODY=$(curl -s -w "\n%{http_code}" -X PATCH \
  "$BASE_URL/api/v1/admin/customers/$BUYER_ID/discount" \
  -H "$ADMIN_AUTH" -H "Content-Type: application/json" \
  -d "{\"percent\": $PERCENT, \"comment\": \"автотест\"}")
HTTP=$(echo "$BODY" | tail -1); RESP=$(echo "$BODY" | head -1)
assert_status "Админ назначает скидку ${PERCENT}%" "200" "$HTTP" "$RESP"

# --- 2. Обычный пользователь не может назначать скидку ----------------------
HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X PATCH \
  "$BASE_URL/api/v1/admin/customers/$BUYER_ID/discount" \
  -H "$BUYER_AUTH" -H "Content-Type: application/json" -d '{"percent": 90}')
assert_status "Покупателю нельзя менять скидку" "403" "$HTTP" ""

# --- 3. Валидация процента --------------------------------------------------
HTTP=$(curl -s -o /dev/null -w "%{http_code}" -X PATCH \
  "$BASE_URL/api/v1/admin/customers/$BUYER_ID/discount" \
  -H "$ADMIN_AUTH" -H "Content-Type: application/json" -d '{"percent": 150}')
assert_status "percent=150 отклоняется" "400" "$HTTP" ""

# --- 4. Скидка видна в профиле ---------------------------------------------
RESP=$(curl -s "$BASE_URL/customer/profile" -H "$BUYER_AUTH")
GOT=$(echo "$RESP" | python3 -c "import sys,json; print(json.load(sys.stdin).get('discountPercent'))" 2>/dev/null)
assert_status "Профиль отдаёт discountPercent" "$PERCENT" "$GOT" "$RESP"

# --- 5. Главное: скидка не складывается с акцией ----------------------------
curl -s -X POST "$BASE_URL/api/v1/basket/$BUYER_ID/add/$PLAIN_ID?quantity=1" -H "$BUYER_AUTH" > /dev/null
curl -s -X POST "$BASE_URL/api/v1/basket/$BUYER_ID/add/$SALE_ID?quantity=1" -H "$BUYER_AUTH" > /dev/null
BASKET=$(curl -s "$BASE_URL/api/v1/basket/$BUYER_ID" -H "$BUYER_AUTH")

# Корзина передаётся аргументом, а не пайпом: stdin здесь занят самим heredoc-ом с программой.
python3 - "$BASKET" "$PLAIN_PRICE" "$SALE_BASE" "$SALE_PRICE" "$PERCENT" <<'PY'
import sys, json
from decimal import Decimal

basket = json.loads(sys.argv[1])
plain, sale_base, sale_price, percent = (Decimal(a) for a in sys.argv[2:6])

GREEN, RED, NC = '\033[0;32m', '\033[0;31m', '\033[0m'
ok = True

def check(label, expected, actual):
    global ok
    if Decimal(str(actual)) == expected:
        print(f"  {GREEN}[PASS]{NC} {label} = {actual}")
    else:
        print(f"  {RED}[FAIL]{NC} {label}: ожидали {expected}, получили {actual}")
        ok = False

# Скидка начисляется ТОЛЬКО на неакционную позицию.
check("itemsTotal (по прайсу)", plain + sale_base, basket['itemsTotal'])
check("saleDiscountAmount (акция)", sale_base - sale_price, basket['saleDiscountAmount'])
check("personalDiscountAmount (только с неакционного)",
      (plain * percent / 100).quantize(Decimal('0.01')), basket['personalDiscountAmount'])
check("payableTotal", plain + sale_price - (plain * percent / 100).quantize(Decimal('0.01')),
      basket['payableTotal'])

sys.exit(0 if ok else 1)
PY
if [ $? -eq 0 ]; then PASS=$((PASS + 4)); else FAIL=$((FAIL + 1)); fi

# --- 6. Снапшот скидки в заказе не меняется при смене скидки клиента --------
STORE=$(curl -s "$BASE_URL/api/v1/basket/$BUYER_ID/availability" -H "$BUYER_AUTH" \
  | python3 -c "
import sys, json
print(next((s['wineStoreId'] for s in json.load(sys.stdin) if s['available']), ''))")

if [ -n "$STORE" ]; then
  ORDER=$(curl -s -X POST "$BASE_URL/api/v1/basket/$BUYER_ID/checkout/$STORE" \
    -H "$BUYER_AUTH" -H "Content-Type: application/json" \
    -d '{"customerName":"Автотест","customerPhone":"+79990000000","pickupDate":"2030-01-01","paymentMethod":"onsite"}' \
    | tr -d '"')

  # Меняем скидку клиента — заказ должен остаться с прежним процентом.
  curl -s -X PATCH "$BASE_URL/api/v1/admin/customers/$BUYER_ID/discount" \
    -H "$ADMIN_AUTH" -H "Content-Type: application/json" -d '{"percent": 50}' > /dev/null

  RESP=$(curl -s "$BASE_URL/api/v1/orders/$ORDER" -H "$BUYER_AUTH")
  GOT=$(echo "$RESP" | python3 -c "import sys,json; print(json.load(sys.stdin).get('personalDiscountPercent'))" 2>/dev/null)
  assert_status "Заказ хранит скидку на момент оформления" "$PERCENT" "$GOT" "$RESP"

  # Позиции заказа должны быть по эффективной цене (с учётом акции).
  GOT=$(echo "$RESP" | python3 -c "
import sys, json
items = json.load(sys.stdin)['items']
print(min(float(i['price']) for i in items))" 2>/dev/null)
  EXPECTED=$(python3 -c "print(min($SALE_PRICE, $PLAIN_PRICE))")
  assert_status "Позиция заказа по акционной цене" "$EXPECTED" "$GOT" "$RESP"
else
  echo -e "  ${YELLOW}[SKIP]${NC} Нет винотеки с обоими товарами — заказ не проверен"
fi

print_summary
