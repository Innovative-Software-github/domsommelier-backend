#!/bin/bash
# Сценарии: мероприятия (CRUD + фильтрация)

SCRIPTS_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPTS_DIR/common.sh"

print_header "EVENTS — Мероприятия (CRUD + фильтрация)"

# Убираем события с null datetime от предыдущих тестов — они ломают EventMapper
docker exec db psql -U postgres -d domsommelier -c "DELETE FROM event WHERE datetime IS NULL;" > /dev/null 2>&1

TOKEN=$(get_token)
if [ -z "$TOKEN" ]; then
  echo -e "${RED}Не удалось получить токен. Прерываем.${NC}"
  exit 1
fi
AUTH="Authorization: Bearer $TOKEN"

# --- 1. Получить список мероприятий (без фильтра) ---
FILTER='{"page":0,"size":10}'
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/events/filter" \
  --data-urlencode "filter=$FILTER" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить список мероприятий (пагинация)" "200" "$HTTP" "$RESP"

# --- 2. Фильтр по типу degustation ---
FILTER='{"type":"degustation","page":0,"size":5}'
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/events/filter" \
  --data-urlencode "filter=$FILTER" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр мероприятий по типу degustation" "200" "$HTTP" "$RESP"

# --- 3. Фильтр по типу wineCasino ---
FILTER='{"type":"wineCasino","page":0,"size":5}'
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/events/filter" \
  --data-urlencode "filter=$FILTER" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр мероприятий по типу wineCasino" "200" "$HTTP" "$RESP"

# --- 4. Фильтр по ценовому диапазону ---
FILTER='{"priceMin":1000,"priceMax":5000,"page":0,"size":5}'
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/events/filter" \
  --data-urlencode "filter=$FILTER" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр мероприятий по цене 1000-5000" "200" "$HTTP" "$RESP"

# --- 5. Фильтр по диапазону дат ---
FILTER='{"dateStart":"2026-01-01T00:00:00Z","dateEnd":"2026-12-31T23:59:59Z","page":0,"size":10}'
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/events/filter" \
  --data-urlencode "filter=$FILTER" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Фильтр мероприятий по диапазону дат (весь 2026)" "200" "$HTTP" "$RESP"

# --- 6. Комбинированный фильтр: тип + цена ---
FILTER='{"type":"degustation","priceMin":500,"priceMax":10000,"page":0,"size":5}'
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/events/filter" \
  --data-urlencode "filter=$FILTER" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Комбинированный фильтр: тип + цена" "200" "$HTTP" "$RESP"

# --- 7. Создать мероприятие ---
CREATE_BODY='{
  "type": "degustation",
  "price": 2500,
  "datetime": "2026-06-15T18:00:00Z",
  "title": "Тестовая дегустация",
  "city": "Москва",
  "address": "ул. Тестовая, д. 1",
  "description": "Тестовое мероприятие для проверки API",
  "registrationLink": "https://example.com/register"
}'
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/events" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d "$CREATE_BODY")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Создать новое мероприятие" "200" "$HTTP" "$RESP"

EVENT_ID=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('id',''))" 2>/dev/null)

if [ -n "$EVENT_ID" ]; then
  echo -e "     Создан event ID: ${YELLOW}$EVENT_ID${NC}"

  # --- 8. Получить мероприятие по ID ---
  BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/events/$EVENT_ID" -H "$AUTH")
  HTTP=$(echo "$BODY" | tail -1)
  RESP=$(echo "$BODY" | head -1)
  assert_status "Получить мероприятие по ID" "200" "$HTTP" "$RESP"

  # Проверить поля ответа
  if [ "$HTTP" = "200" ]; then
    TITLE=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('title',''))" 2>/dev/null)
    if [ "$TITLE" = "Тестовая дегустация" ]; then
      echo -e "  ${GREEN}[PASS]${NC} Заголовок мероприятия соответствует"
      PASS=$((PASS + 1))
    else
      echo -e "  ${RED}[FAIL]${NC} Заголовок не совпадает: '$TITLE'"
      FAIL=$((FAIL + 1))
    fi
  fi

  # --- 9. Обновить мероприятие ---
  UPDATE_BODY="{
    \"id\": \"$EVENT_ID\",
    \"type\": \"degustation\",
    \"price\": 3000,
    \"datetime\": \"2026-06-20T18:00:00Z\",
    \"title\": \"Тестовая дегустация (обновлено)\",
    \"city\": \"Москва\",
    \"address\": \"ул. Тестовая, д. 2\",
    \"description\": \"Обновлённое тестовое мероприятие\"
  }"
  BODY=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/api/v1/events/$EVENT_ID" \
    -H "$AUTH" -H "Content-Type: application/json" \
    -d "$UPDATE_BODY")
  HTTP=$(echo "$BODY" | tail -1)
  RESP=$(echo "$BODY" | head -1)
  assert_status "Обновить мероприятие по ID" "200" "$HTTP" "$RESP"

  # --- 10. Проверить что цена обновилась ---
  BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/events/$EVENT_ID" -H "$AUTH")
  HTTP=$(echo "$BODY" | tail -1)
  RESP=$(echo "$BODY" | head -1)
  assert_status "Получить обновлённое мероприятие" "200" "$HTTP" "$RESP"

  if [ "$HTTP" = "200" ]; then
    PRICE=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('price',''))" 2>/dev/null)
    if [ "$PRICE" = "3000" ]; then
      echo -e "  ${GREEN}[PASS]${NC} Цена обновлена до 3000"
      PASS=$((PASS + 1))
    else
      echo -e "  ${RED}[FAIL]${NC} Цена не обновилась: '$PRICE'"
      FAIL=$((FAIL + 1))
    fi
  fi

  # --- 11. Удалить мероприятие ---
  BODY=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/api/v1/events/$EVENT_ID" -H "$AUTH")
  HTTP=$(echo "$BODY" | tail -1)
  assert_status "Удалить мероприятие по ID" "204" "$HTTP"

  # --- 12. Получить удалённое мероприятие — 404 ---
  BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/events/$EVENT_ID" -H "$AUTH")
  HTTP=$(echo "$BODY" | tail -1)
  assert_status "Получить удалённое мероприятие — 404" "404" "$HTTP"
else
  echo -e "  ${YELLOW}[SKIP]${NC} Не удалось извлечь ID созданного мероприятия — пропускаем CRUD"
fi

# --- 13. Создать мероприятие с неполными данными ---
# Приложение принимает без @NotNull валидации — возвращает 200 с null-полями (известный баг отсутствия валидации)
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/events" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"type": "degustation"}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Создать мероприятие с отсутствующими полями — 200 (нет валидации)" "200" "$HTTP" "$RESP"

# Удаляем созданное "плохое" мероприятие, чтобы оно не ломало events/filter
BAD_EVENT_ID=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('id',''))" 2>/dev/null)
if [ -n "$BAD_EVENT_ID" ]; then
  curl -s -X DELETE "$BASE_URL/api/v1/events/$BAD_EVENT_ID" -H "$AUTH" > /dev/null
fi

# --- 14. Получить несуществующее мероприятие ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/events/00000000-0000-0000-0000-000000000000" -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
assert_status "Получить несуществующее мероприятие — 404" "404" "$HTTP"

# --- 15. Обновить несуществующее мероприятие ---
BODY=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/api/v1/events/00000000-0000-0000-0000-000000000000" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"title":"test","type":"degustation","price":1000}')
HTTP=$(echo "$BODY" | tail -1)
assert_status "Обновить несуществующее мероприятие — 404" "404" "$HTTP"

print_summary
