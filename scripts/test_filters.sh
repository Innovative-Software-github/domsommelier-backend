#!/bin/bash
# Сценарии: конфигурация фильтров

SCRIPTS_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPTS_DIR/common.sh"

print_header "FILTERS — Конфигурация фильтров"

TOKEN=$(get_token)
if [ -z "$TOKEN" ]; then
  echo -e "${RED}Не удалось получить токен. Прерываем.${NC}"
  exit 1
fi
AUTH="Authorization: Bearer $TOKEN"

# --- 1. Получить все фильтры ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/filters" -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Получить все фильтры" "200" "$HTTP" "$RESP"

# Проверим что список не пустой и содержит ожидаемые категории
if [ "$HTTP" = "200" ]; then
  # Ответ: {"wine": {...}, "snack": {...}, ...} — объект, ключи = названия категорий
  CATEGORIES=$(echo "$RESP" | python3 -c "
import sys, json
data = json.load(sys.stdin)
print(','.join(sorted(data.keys())) if isinstance(data, dict) else 'unexpected_format')
" 2>/dev/null)
  echo -e "     Категории в ответе: ${YELLOW}$CATEGORIES${NC}"

  for CAT in wine snack spirit champagne_and_sparkling accessories low_alcohol; do
    HAS=$(echo "$RESP" | python3 -c "
import sys, json
data = json.load(sys.stdin)
found = isinstance(data, dict) and '$CAT' in data
print('yes' if found else 'no')
" 2>/dev/null)
    if [ "$HAS" = "yes" ]; then
      echo -e "  ${GREEN}[PASS]${NC} Фильтры для категории '$CAT' присутствуют"
      PASS=$((PASS + 1))
    else
      echo -e "  ${RED}[FAIL]${NC} Фильтры для категории '$CAT' отсутствуют"
      FAIL=$((FAIL + 1))
    fi
  done
fi

# --- 2. Запрос без токена ---
BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/v1/filters")
HTTP=$(echo "$BODY" | tail -1)
assert_status "Получить фильтры без авторизации" "403" "$HTTP"

print_summary
