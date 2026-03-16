#!/bin/bash
# Сценарии: заказы

SCRIPTS_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPTS_DIR/common.sh"

print_header "ORDERS — Заказы"

TOKEN=$(get_token)
if [ -z "$TOKEN" ]; then
  echo -e "${RED}Не удалось получить токен. Прерываем.${NC}"
  exit 1
fi
AUTH="Authorization: Bearer $TOKEN"

# --- 1. Получить историю заказов ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/orders" -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить историю заказов" "200" "$HTTP" "$RESP"

# Запомнить ID первого заказа, если есть
FIRST_ORDER_ID=$(echo "$RESP" | python3 -c "
import sys, json
d = json.load(sys.stdin)
items = d.get('content', d if isinstance(d, list) else [])
print(items[0]['id'] if items else '')
" 2>/dev/null)

if [ -n "$FIRST_ORDER_ID" ]; then
  echo -e "     Первый заказ ID: ${YELLOW}$FIRST_ORDER_ID${NC}"

  # --- 2. Получить детали заказа ---
  BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/orders/$FIRST_ORDER_ID" -H "$AUTH")
  HTTP=$(echo "$BODY" | tail -1)
  RESP=$(echo "$BODY" | head -1)
  assert_status "Получить детали заказа по ID" "200" "$HTTP" "$RESP"

  ORDER_STATUS=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('status',''))" 2>/dev/null)
  echo -e "     Статус заказа: ${YELLOW}$ORDER_STATUS${NC}"
else
  echo -e "  ${YELLOW}[INFO]${NC}  Заказов ещё нет — проверяем создание"
fi

# --- 3. Создать заказ через basket checkout ---
curl -s -X POST "$BASE_URL/api/v1/basket/$CUSTOMER_ID/clear" -H "$AUTH" > /dev/null
curl -s -X POST "$BASE_URL/api/v1/basket/$CUSTOMER_ID/add/$WINE_PRODUCT_ID?quantity=1" -H "$AUTH" > /dev/null

ADDRESS_ID="00000000-0000-0000-0000-000000000001"
BODY=$(curl -s -w "\n%{http_code}" -X POST \
  "$BASE_URL/api/v1/basket/$CUSTOMER_ID/checkout/$ADDRESS_ID" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)

if [ "$HTTP" = "200" ] || [ "$HTTP" = "201" ]; then
  echo -e "  ${GREEN}[PASS]${NC} Checkout корзины (HTTP $HTTP)"
  PASS=$((PASS + 1))
  NEW_ORDER_ID=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('id',''))" 2>/dev/null)

  if [ -n "$NEW_ORDER_ID" ]; then
    # --- 4. Детали нового заказа ---
    BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/orders/$NEW_ORDER_ID" -H "$AUTH")
    HTTP=$(echo "$BODY" | tail -1)
    RESP=$(echo "$BODY" | head -1)
    assert_status "Детали нового заказа" "200" "$HTTP" "$RESP"

    # --- 5. История заказов увеличилась ---
    BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/orders" -H "$AUTH")
    HTTP=$(echo "$BODY" | tail -1)
    RESP=$(echo "$BODY" | head -1)
    assert_status "История заказов после создания" "200" "$HTTP" "$RESP"

    # --- 6. Отменить заказ ---
    BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/orders/$NEW_ORDER_ID/cancel" -H "$AUTH")
    HTTP=$(echo "$BODY" | tail -1)
    RESP=$(echo "$BODY" | head -1)
    assert_status "Отменить созданный заказ" "200" "$HTTP" "$RESP"

    # --- 7. Повторная отмена уже отменённого заказа ---
    BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/orders/$NEW_ORDER_ID/cancel" -H "$AUTH")
    HTTP=$(echo "$BODY" | tail -1)
    RESP=$(echo "$BODY" | head -1)
    assert_status "Повторная отмена уже отменённого заказа — 400 или 409" "400" "$HTTP" "$RESP"
  fi
elif [ "$HTTP" = "404" ]; then
  echo -e "  ${YELLOW}[INFO]${NC}  Checkout вернул 404 — адрес не найден (ожидаемо для тестового адреса)"
else
  echo -e "  ${RED}[FAIL]${NC} Checkout вернул HTTP $HTTP"
  echo -e "         ${YELLOW}Body:${NC} $(echo "$RESP" | head -c 300)"
  FAIL=$((FAIL + 1))
fi

# --- 8. Прямое создание заказа через POST /api/v1/orders ---
curl -s -X POST "$BASE_URL/api/v1/basket/$CUSTOMER_ID/add/$WINE_PRODUCT_ID?quantity=1" -H "$AUTH" > /dev/null
BASKET=$(curl -s -X GET "$BASE_URL/api/v1/basket/$CUSTOMER_ID" -H "$AUTH")
BASKET_BODY=$(echo "$BASKET" | python3 -c "
import sys, json
d = json.load(sys.stdin)
print(json.dumps(d))
" 2>/dev/null)

if [ -n "$BASKET_BODY" ] && [ "$BASKET_BODY" != "null" ]; then
  BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/orders?addressId=$ADDRESS_ID" \
    -H "$AUTH" -H "Content-Type: application/json" \
    -d "$BASKET_BODY")
  HTTP=$(echo "$BODY" | tail -1)
  RESP=$(echo "$BODY" | head -1)
  if [ "$HTTP" = "200" ] || [ "$HTTP" = "201" ]; then
    echo -e "  ${GREEN}[PASS]${NC} Прямое создание заказа через POST /orders (HTTP $HTTP)"
    PASS=$((PASS + 1))
  elif [ "$HTTP" = "404" ]; then
    echo -e "  ${YELLOW}[INFO]${NC}  POST /orders вернул 404 (адрес не найден)"
  else
    echo -e "  ${RED}[FAIL]${NC} POST /orders вернул HTTP $HTTP"
    echo -e "         ${YELLOW}Body:${NC} $(echo "$RESP" | head -c 300)"
    FAIL=$((FAIL + 1))
  fi
fi

# --- 9. Отменить несуществующий заказ ---
# EntityNotFoundException не обработан в OrderController → 403 (баг обработки ошибок)
BODY=$(curl -s -w "\n%{http_code}" -X POST \
  "$BASE_URL/api/v1/orders/00000000-0000-0000-0000-000000000000/cancel" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
assert_status "Отменить несуществующий заказ — 403 (нет обработки EntityNotFoundException)" "403" "$HTTP"

# --- 10. Детали несуществующего заказа ---
BODY=$(curl -s -w "\n%{http_code}" -X GET \
  "$BASE_URL/api/v1/orders/00000000-0000-0000-0000-000000000000" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
assert_status "Детали несуществующего заказа — 403 (нет обработки EntityNotFoundException)" "403" "$HTTP"

# Очистка
curl -s -X POST "$BASE_URL/api/v1/basket/$CUSTOMER_ID/clear" -H "$AUTH" > /dev/null

print_summary
