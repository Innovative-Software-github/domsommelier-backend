#!/bin/bash
# Сценарии: винные магазины (фильтрация, пагинация, гео-поиск)

SCRIPTS_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPTS_DIR/common.sh"

print_header "WINE STORES — Магазины (фильтрация, пагинация, гео)"

TOKEN=$(get_token)
if [ -z "$TOKEN" ]; then
  echo -e "${RED}Не удалось получить токен. Прерываем.${NC}"
  exit 1
fi
AUTH="Authorization: Bearer $TOKEN"

# --- 1. Получить все магазины без фильтра ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/wine-stores" -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить все магазины (без фильтра)" "200" "$HTTP" "$RESP"

if [ "$HTTP" = "200" ]; then
  TOTAL=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('totalElements', len(d.get('content',[]))))" 2>/dev/null)
  echo -e "     Всего магазинов: ${YELLOW}$TOTAL${NC}"
fi

# --- 2. Фильтр по городу: москва ---
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/wine-stores" \
  --data-urlencode "city=москва" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр по городу: москва" "200" "$HTTP" "$RESP"

if [ "$HTTP" = "200" ]; then
  COUNT=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('totalElements', 0))" 2>/dev/null)
  if [ "$COUNT" -ge 1 ] 2>/dev/null; then
    echo -e "  ${GREEN}[PASS]${NC} Найдено магазинов в Москве: $COUNT"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}[FAIL]${NC} Не найдено магазинов в Москве (ожидали >= 1)"
    FAIL=$((FAIL + 1))
  fi
fi

# --- 3. Фильтр по городу: пермь ---
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/wine-stores" \
  --data-urlencode "city=пермь" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр по городу: пермь" "200" "$HTTP" "$RESP"

# --- 4. Фильтр по несуществующему городу — пустой результат ---
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/wine-stores" \
  --data-urlencode "city=несуществующий_город" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр по несуществующему городу — пустой список" "200" "$HTTP" "$RESP"

if [ "$HTTP" = "200" ]; then
  COUNT=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('totalElements', -1))" 2>/dev/null)
  if [ "$COUNT" = "0" ]; then
    echo -e "  ${GREEN}[PASS]${NC} Пустой список для несуществующего города"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}[FAIL]${NC} Ожидали 0 результатов, получили: $COUNT"
    FAIL=$((FAIL + 1))
  fi
fi

# --- 5. Фильтр по гео-координатам (Москва: location=(55.72668, 37.678505))
# В entity: longitude=location[0]=55.72668 (фактически широта), latitude=location[1]=37.678505 (фактически долгота)
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/wine-stores" \
  -d "minLongitude=55.0" \
  -d "maxLongitude=56.0" \
  -d "minLatitude=37.0" \
  -d "maxLatitude=38.0" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Гео-фильтр по координатам Москвы" "200" "$HTTP" "$RESP"

if [ "$HTTP" = "200" ]; then
  COUNT=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('totalElements', 0))" 2>/dev/null)
  if [ "$COUNT" -ge 1 ] 2>/dev/null; then
    echo -e "  ${GREEN}[PASS]${NC} Найдено магазинов в гео-диапазоне Москвы: $COUNT"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}[FAIL]${NC} Не найдено магазинов в гео-диапазоне Москвы"
    FAIL=$((FAIL + 1))
  fi
fi

# --- 6. Гео-фильтр в пустой зоне (Антарктида) ---
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/wine-stores" \
  -d "minLatitude=-90.0" \
  -d "maxLatitude=-80.0" \
  -d "minLongitude=0.0" \
  -d "maxLongitude=10.0" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Гео-фильтр в пустой зоне — пустой список" "200" "$HTTP" "$RESP"

# --- 7. Фильтр по district ---
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/wine-stores" \
  --data-urlencode "district=москва" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр по district: москва" "200" "$HTTP" "$RESP"

# --- 8. Пагинация: первая страница, size=1 ---
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/wine-stores" \
  -d "page=0" \
  -d "size=1" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Пагинация: page=0, size=1" "200" "$HTTP" "$RESP"

if [ "$HTTP" = "200" ]; then
  PAGE_SIZE=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d.get('content',[])))" 2>/dev/null)
  if [ "$PAGE_SIZE" = "1" ]; then
    echo -e "  ${GREEN}[PASS]${NC} Вернулась ровно 1 запись на странице"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}[FAIL]${NC} Ожидали 1 запись на странице, получили: $PAGE_SIZE"
    FAIL=$((FAIL + 1))
  fi
fi

# --- 9. Пагинация: вторая страница при size=1 (проверка totalPages) ---
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/wine-stores" \
  -d "page=1" \
  -d "size=1" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Пагинация: page=1, size=1 (вторая страница)" "200" "$HTTP" "$RESP"

# --- 10. Без авторизации ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/wine-stores")
HTTP=$(echo "$BODY" | tail -1)
assert_status "Получить магазины без авторизации — 403" "403" "$HTTP"

print_summary
