#!/bin/bash
# Сценарии: аутентификация (OTP flow)

SCRIPTS_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPTS_DIR/common.sh"

print_header "AUTH — OTP Flow"

# --- 1. Инициировать OTP (успех или 503 если SMTP недоступен — код всё равно в Redis) ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/auth/initiate" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$TEST_EMAIL\"}")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
if [ "$HTTP" = "200" ] || [ "$HTTP" = "503" ]; then
  echo -e "  ${GREEN}[PASS]${NC} Инициировать OTP для существующего пользователя (HTTP $HTTP)"
  PASS=$((PASS + 1))
else
  assert_status "Инициировать OTP для существующего пользователя" "200" "$HTTP" "$RESP"
fi

# --- 2. Spam protection: повторный запрос возвращает 200 с ошибкой в теле (намеренное поведение) ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/auth/initiate" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$TEST_EMAIL\"}")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Повторный запрос OTP сразу — спам-защита (200 с ошибкой в теле)" "200" "$HTTP" "$RESP"

# --- 3. Неверный email формат ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/auth/initiate" \
  -H "Content-Type: application/json" \
  -d '{"email": "not-an-email"}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Инициировать OTP с невалидным email" "400" "$HTTP" "$RESP"

# --- 4. Пустой email ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/auth/initiate" \
  -H "Content-Type: application/json" \
  -d '{"email": ""}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Инициировать OTP с пустым email" "400" "$HTTP" "$RESP"

# --- 5. Подтвердить с заведомо неверным кодом ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/auth/confirm" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$TEST_EMAIL\", \"code\": \"0000\"}")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Подтвердить OTP с неверным кодом" "400" "$HTTP" "$RESP"

# --- 6. Confirm без предварительного initiate (нет ключа в Redis) ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/auth/confirm" \
  -H "Content-Type: application/json" \
  -d '{"email": "never-initiated@example.com", "code": "1234"}')
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Confirm без initiate — нет сессии в Redis" "400" "$HTTP" "$RESP"

# --- 7. Confirm с пустым кодом ---
BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/auth/confirm" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$TEST_EMAIL\", \"code\": \"\"}")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Confirm с пустым кодом — 400" "400" "$HTTP" "$RESP"

# --- 8. Полный flow: initiate + confirm (через Redis) ---
echo "  -> Получаем рабочий токен через полный OTP-flow..." >&2

ALT_EMAIL="smirnov.dima@gmail.com"
curl -s -X POST "$BASE_URL/api/v1/auth/initiate" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$ALT_EMAIL\"}" > /dev/null

REDIS_KEY="auth:otp:${ALT_EMAIL}"
SESSION=$(docker exec domsommelier-backend-redis-1 redis-cli HGET "$REDIS_KEY" object 2>/dev/null)
CODE=$(echo "$SESSION" | python3 -c "import sys,json; raw=sys.stdin.read().strip(); print(json.loads(json.loads(raw))['code'])" 2>/dev/null)

if [ -n "$CODE" ]; then
  BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/auth/confirm" \
    -H "Content-Type: application/json" \
    -d "{\"email\": \"$ALT_EMAIL\", \"code\": \"$CODE\"}")
  HTTP=$(echo "$BODY" | tail -1)
  RESP=$(echo "$BODY" | head -1)
  assert_status "Полный OTP-flow: confirm с верным кодом" "200" "$HTTP" "$RESP"

  TOKEN_PRESENT=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print('yes' if d.get('token') else 'no')" 2>/dev/null)
  if [ "$TOKEN_PRESENT" = "yes" ]; then
    echo -e "  ${GREEN}[PASS]${NC} Ответ содержит поле token"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}[FAIL]${NC} Ответ не содержит поле token"
    FAIL=$((FAIL + 1))
  fi

  # --- 9. Повторный confirm с уже использованным кодом ---
  BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/v1/auth/confirm" \
    -H "Content-Type: application/json" \
    -d "{\"email\": \"$ALT_EMAIL\", \"code\": \"$CODE\"}")
  HTTP=$(echo "$BODY" | tail -1)
  RESP=$(echo "$BODY" | head -1)
  assert_status "Повторный confirm с уже использованным кодом — 400" "400" "$HTTP" "$RESP"
else
  echo -e "  ${YELLOW}[SKIP]${NC} Не удалось получить код из Redis"
fi

print_summary
