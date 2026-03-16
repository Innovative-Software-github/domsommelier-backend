#!/bin/bash
# Сценарии: новости (CRUD)

SCRIPTS_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPTS_DIR/common.sh"

print_header "NEWS — Новости (CRUD)"

TOKEN=$(get_token)
if [ -z "$TOKEN" ]; then
  echo -e "${RED}Не удалось получить токен. Прерываем.${NC}"
  exit 1
fi
AUTH="Authorization: Bearer $TOKEN"

# --- 1. Получить список новостей ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/news" -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить список новостей" "200" "$HTTP" "$RESP"

# --- 2. Создать новость ---
CREATE_BODY='{
  "title": "Тестовая новость",
  "description": "Описание тестовой новости для проверки API",
  "reference": "https://example.com/test-news"
}'
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/news" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d "$CREATE_BODY")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Создать новость" "200" "$HTTP" "$RESP"

# POST /api/v1/news возвращает сырую UUID-строку (не JSON-объект)
NEWS_ID=$(echo "$RESP" | tr -d '"' | tr -d ' ' | tr -d '\n')

if [ -n "$NEWS_ID" ]; then
  echo -e "     Создана новость ID: ${YELLOW}$NEWS_ID${NC}"

  # --- 3. Получить новость по ID ---
  BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/news/id" \
    --data-urlencode "id=$NEWS_ID" \
    -H "$AUTH")
  HTTP=$(echo "$BODY" | tail -1)
  RESP=$(echo "$BODY" | head -1)
  assert_status "Получить новость по ID" "200" "$HTTP" "$RESP"

  # --- 4. Проверить поля созданной новости ---
  if [ "$HTTP" = "200" ]; then
    TITLE=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('title',''))" 2>/dev/null)
    if [ "$TITLE" = "Тестовая новость" ]; then
      echo -e "  ${GREEN}[PASS]${NC} Заголовок новости совпадает"
      PASS=$((PASS + 1))
    else
      echo -e "  ${RED}[FAIL]${NC} Заголовок не совпадает: '$TITLE'"
      FAIL=$((FAIL + 1))
    fi
  fi

  # --- 5. Обновить новость ---
  UPDATE_BODY="{
    \"id\": \"$NEWS_ID\",
    \"title\": \"Тестовая новость (обновлено)\",
    \"description\": \"Обновлённое описание\",
    \"reference\": \"https://example.com/updated-news\",
    \"files\": []
  }"
  BODY=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/api/v1/news" \
    -H "$AUTH" -H "Content-Type: application/json" \
    -d "$UPDATE_BODY")
  HTTP=$(echo "$BODY" | tail -1)
  RESP=$(echo "$BODY" | head -1)
  assert_status "Обновить новость" "200" "$HTTP" "$RESP"

  # --- 6. Проверить что заголовок обновился ---
  BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/news/id" \
    --data-urlencode "id=$NEWS_ID" \
    -H "$AUTH")
  HTTP=$(echo "$BODY" | tail -1)
  RESP=$(echo "$BODY" | head -1)
  assert_status "Получить обновлённую новость" "200" "$HTTP" "$RESP"

  if [ "$HTTP" = "200" ]; then
    TITLE=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('title',''))" 2>/dev/null)
    if [[ "$TITLE" == *"обновлено"* ]]; then
      echo -e "  ${GREEN}[PASS]${NC} Заголовок обновлён корректно"
      PASS=$((PASS + 1))
    else
      echo -e "  ${RED}[FAIL]${NC} Заголовок не обновился: '$TITLE'"
      FAIL=$((FAIL + 1))
    fi
  fi

  # --- 7. Обновить несуществующую новость — нет проверки существования → 200 (баг) ---
  BODY=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/api/v1/news" \
    -H "$AUTH" -H "Content-Type: application/json" \
    -d '{"id":"00000000-0000-0000-0000-000000000000","title":"x","description":"x","files":[]}')
  HTTP=$(echo "$BODY" | tail -1)
  assert_status "Обновить несуществующую новость — 200 (нет проверки существования)" "200" "$HTTP"

  # --- 8. Удалить новость ---
  BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/news/id" \
    --request DELETE \
    --data-urlencode "id=$NEWS_ID" \
    -H "$AUTH")
  HTTP=$(echo "$BODY" | tail -1)
  assert_status "Удалить новость по ID" "200" "$HTTP"

  # --- 9. Получить удалённую новость — EntityNotFoundException не обработан → 403 ---
  BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/news/id" \
    --data-urlencode "id=$NEWS_ID" \
    -H "$AUTH")
  HTTP=$(echo "$BODY" | tail -1)
  assert_status "Получить удалённую новость — 403 (EntityNotFoundException не обработан)" "403" "$HTTP"

  # --- 10. Повторное удаление — нет проверки существования → 200 (идемпотентность) ---
  BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/news/id" \
    --request DELETE \
    --data-urlencode "id=$NEWS_ID" \
    -H "$AUTH")
  HTTP=$(echo "$BODY" | tail -1)
  assert_status "Повторное удаление новости — 200 (идемпотентный DELETE)" "200" "$HTTP"
else
  echo -e "  ${YELLOW}[SKIP]${NC} Не удалось извлечь ID созданной новости"
fi

# --- 11. Создать новость с пустым заголовком — нет @Valid валидации → 200 (баг) ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/news" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d '{"title": "", "description": "описание"}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Создать новость с пустым заголовком — 200 (нет @Valid)" "200" "$HTTP" "$RESP"

# --- 12. Получить несуществующую новость ---
# EntityNotFoundException не обработан → 403 (баг обработки ошибок)
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/api/v1/news/id" \
  --data-urlencode "id=00000000-0000-0000-0000-000000000000" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
assert_status "Получить несуществующую новость — 403 (нет обработки EntityNotFoundException)" "403" "$HTTP"

print_summary
