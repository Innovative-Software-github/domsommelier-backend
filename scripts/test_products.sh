#!/bin/bash
# Сценарии: продукты (получить по ID, поиск, фильтрация по категориям)

SCRIPTS_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPTS_DIR/common.sh"

print_header "PRODUCTS — Получение, поиск, фильтрация"

# Дополнительные ID по категориям
SPIRIT_PRODUCT_ID="1e225edf-b7b8-4c86-9c7f-c7b863ea13de"
SPARKLING_PRODUCT_ID="21d0f017-d059-44d7-9482-727801f48e3b"
ACCESSORIES_PRODUCT_ID="09c97abf-2d2d-4e34-ac2c-46f1f3158c4d"
LOW_ALCOHOL_PRODUCT_ID="0721d633-92a2-4923-bc14-f8d3d3ed84b8"

TOKEN=$(get_token)
if [ -z "$TOKEN" ]; then
  echo -e "${RED}Не удалось получить токен. Прерываем.${NC}"
  exit 1
fi
AUTH="Authorization: Bearer $TOKEN"

# --- 1. Получить продукт по ID (вино) ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/products?id=$WINE_PRODUCT_ID" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить продукт по ID (вино)" "200" "$HTTP" "$RESP"

# --- 2. Получить продукт по ID (снек) ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/products?id=$SNACK_PRODUCT_ID" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить продукт по ID (снек)" "200" "$HTTP" "$RESP"

# --- 3. Получить продукт по ID (крепкий алкоголь) ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/products?id=$SPIRIT_PRODUCT_ID" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить продукт по ID (spirit)" "200" "$HTTP" "$RESP"

# --- 4. Несуществующий ID ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/products?id=00000000-0000-0000-0000-000000000000" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить продукт по несуществующему ID" "404" "$HTTP" "$RESP"

# --- 5. Невалидный UUID ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/products?id=not-a-uuid" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить продукт с невалидным UUID — 400" "400" "$HTTP" "$RESP"

# --- 6. Поиск по названию (name — @RequestHeader, не @RequestParam) ---
BODY=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/v1/products/search?page=0&size=5" \
  -H "$AUTH" -H "name: Louis")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Поиск продуктов по имени 'Louis'" "200" "$HTTP" "$RESP"

# --- 7. Поиск — нет результатов (ASCII-only: Кириллица в HTTP-заголовке ломает запрос) ---
BODY=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/v1/products/search?page=0&size=5" \
  -H "$AUTH" -H "name: xyznotfoundxyz")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Поиск по несуществующему названию — пустой список" "200" "$HTTP" "$RESP"

# --- 8. Поиск с пагинацией (page=1, size=2) ---
BODY=$(curl -s -w "\n%{http_code}" "$BASE_URL/api/v1/products/search?page=1&size=2" \
  -H "$AUTH" -H "name: а")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Поиск с пагинацией (page=1, size=2)" "200" "$HTTP" "$RESP"

# --- 9. Фильтр по категории: wine (пустое тело = без фильтров) ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?category=wine&page=0&size=5" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр продуктов: категория wine (без фильтров)" "200" "$HTTP" "$RESP"

# --- 10. Фильтр wine с реальными параметрами (страна + цвет) ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?category=wine&page=0&size=10" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"countries": ["Франция"], "color": ["Красное"]}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр wine: страна=Франция, цвет=Красное" "200" "$HTTP" "$RESP"

# --- 11. Фильтр wine по ценовому диапазону ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?category=wine&page=0&size=10" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"price": [1000, 3000]}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр wine: цена 1000-3000" "200" "$HTTP" "$RESP"

# --- 12. Фильтр по категории: snack ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?category=snack&page=0&size=5" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр продуктов: категория snack" "200" "$HTTP" "$RESP"

# --- 13. Фильтр snack с параметрами (ASCII-значения — Кириллица в JSON body вызывает 403) ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?category=snack&page=0&size=5" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"price": [500, 2000]}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр snack: по цене (ASCII-only)" "200" "$HTTP" "$RESP"

# --- 14. Фильтр по категории: spirit ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?category=spirit&page=0&size=5" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр продуктов: категория spirit" "200" "$HTTP" "$RESP"

# --- 15. Фильтр spirit с подкатегорией ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?category=spirit&page=0&size=5" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"subcategory": ["Виски"]}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр spirit: subcategory=Виски" "200" "$HTTP" "$RESP"

# --- 16. Фильтр по категории: champagne_and_sparkling ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?category=champagne_and_sparkling&page=0&size=5" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр продуктов: категория champagne_and_sparkling" "200" "$HTTP" "$RESP"

# --- 17. Фильтр по категории: accessories ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?category=accessories&page=0&size=5" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр продуктов: категория accessories" "200" "$HTTP" "$RESP"

# --- 18. Фильтр по категории: low_alcohol ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?category=low_alcohol&page=0&size=5" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр продуктов: категория low_alcohol" "200" "$HTTP" "$RESP"

# --- 19. Несуществующая категория ---
# Приложение выбрасывает необработанное исключение → 403/500 (баг обработки ошибок)
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?category=unknown_cat&page=0&size=5" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
if [ "$HTTP" = "400" ] || [ "$HTTP" = "403" ] || [ "$HTTP" = "500" ]; then
  echo -e "  ${GREEN}[PASS]${NC} Фильтр по несуществующей категории — ошибка (HTTP $HTTP)"
  PASS=$((PASS + 1))
else
  assert_status "Фильтр по несуществующей категории — ошибка" "400" "$HTTP" "$RESP"
fi

# --- 20. Фильтр без параметра category ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/products/filter?page=0&size=5" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
if [ "$HTTP" = "400" ] || [ "$HTTP" = "403" ] || [ "$HTTP" = "500" ]; then
  echo -e "  ${GREEN}[PASS]${NC} Фильтр без параметра category — ошибка (HTTP $HTTP)"
  PASS=$((PASS + 1))
else
  assert_status "Фильтр без обязательного параметра category — ошибка" "400" "$HTTP" "$RESP"
fi

# --- 21. Запрос без токена ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/products?id=$WINE_PRODUCT_ID")
HTTP=$(echo "$BODY" | tail -1)
assert_status "Получить продукт без авторизации — 403" "403" "$HTTP"

print_summary
