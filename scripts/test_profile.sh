#!/bin/bash
# Сценарии: профиль клиента

SCRIPTS_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPTS_DIR/common.sh"

print_header "PROFILE — Профиль клиента"

TOKEN=$(get_token)
if [ -z "$TOKEN" ]; then
  echo -e "${RED}Не удалось получить токен. Прерываем.${NC}"
  exit 1
fi
AUTH="Authorization: Bearer $TOKEN"

# --- 1. Получить профиль ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/customer/profile" -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить профиль текущего пользователя" "200" "$HTTP" "$RESP"

if [ "$HTTP" = "200" ]; then
  EMAIL_IN_RESP=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('email',''))" 2>/dev/null)
  echo -e "     Email в профиле: ${YELLOW}$EMAIL_IN_RESP${NC}"

  if [ "$EMAIL_IN_RESP" = "$TEST_EMAIL" ]; then
    echo -e "  ${GREEN}[PASS]${NC} Email совпадает с тестовым"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}[FAIL]${NC} Email не совпадает: ожидали $TEST_EMAIL, получили $EMAIL_IN_RESP"
    FAIL=$((FAIL + 1))
  fi
fi

# --- 2. Обновить профиль ---
UPDATE_BODY='{"firstName":"Андрей","secondName":"Носов","middleName":"Тестович"}'
BODY=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/customer/profile" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d "$UPDATE_BODY")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Обновить профиль" "200" "$HTTP" "$RESP"

# --- 3. Проверить что обновление применилось ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/customer/profile" -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить профиль после обновления" "200" "$HTTP" "$RESP"

if [ "$HTTP" = "200" ]; then
  MIDDLE=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('middleName',''))" 2>/dev/null)
  if [ "$MIDDLE" = "Тестович" ]; then
    echo -e "  ${GREEN}[PASS]${NC} Отчество обновлено корректно"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}[FAIL]${NC} Отчество не обновилось, получили: '$MIDDLE'"
    FAIL=$((FAIL + 1))
  fi
fi

# --- 4. Откатить отчество ---
REVERT_BODY='{"firstName":"Андрей","secondName":"Носов","middleName":""}'
curl -s -X PUT "$BASE_URL/customer/profile" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d "$REVERT_BODY" > /dev/null

# --- 5. Получить рекомендации ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/customer/$CUSTOMER_ID/recommendations" -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить рекомендации для клиента" "200" "$HTTP" "$RESP"

# --- 6. Рекомендации для несуществующего клиента — нет валидации, возвращает 200 ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/customer/00000000-0000-0000-0000-000000000000/recommendations" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
assert_status "Рекомендации для несуществующего клиента — 200 (нет валидации)" "200" "$HTTP"

print_summary
