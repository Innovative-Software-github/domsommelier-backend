#!/bin/bash
# Получить JWT токен через OTP-авторизацию (для тестирования)
# Использование: ./scripts/get_token.sh [email]
# Вывод: JWT токен в stdout, логи в stderr

EMAIL=${1:-"nosoff.4ndr@yandex.ru"}
REDIS_CONTAINER="domsommelier-backend-redis-1"
REDIS_KEY="auth:otp:${EMAIL}"

# Шаг 1: Инициировать OTP
echo "-> Запрашиваем OTP для ${EMAIL}..." >&2
curl -s -X POST "http://localhost:8080/api/v1/auth/initiate" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"${EMAIL}\"}" > /dev/null

# Шаг 2: Достать код из Redis
echo "-> Читаем код из Redis..." >&2
SESSION=$(docker exec "$REDIS_CONTAINER" redis-cli HGET "$REDIS_KEY" object 2>/dev/null)
CODE=$(echo "$SESSION" | python3 -c "import sys,json; raw=sys.stdin.read().strip(); print(json.loads(json.loads(raw))['code'])")

if [ -z "$CODE" ]; then
  echo "Ошибка: код не найден в Redis" >&2
  exit 1
fi

echo "-> Код: ${CODE}" >&2

# Шаг 3: Подтвердить и получить токен
echo "-> Подтверждаем..." >&2
RESPONSE=$(curl -s -X POST "http://localhost:8080/api/v1/auth/confirm" \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"${EMAIL}\", \"code\": \"${CODE}\"}")

TOKEN=$(echo "$RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])" 2>/dev/null)

if [ -z "$TOKEN" ]; then
  echo "Ошибка авторизации: $RESPONSE" >&2
  exit 1
fi

echo "-> Токен получен" >&2
echo "$TOKEN"
