#!/bin/bash
# Общие утилиты для тестовых скриптов

BASE_URL="http://localhost:8080"
SCRIPTS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Тестовые данные
WINE_PRODUCT_ID="293b85d0-f739-4856-a09b-8495f2157e4d"
SNACK_PRODUCT_ID="82e5f3b9-0459-4e13-afe7-2ed190dca000"
CUSTOMER_ID="6c5f985d-b0bf-4bb5-ba0e-1d87fc93d5c9"
TEST_EMAIL="nosoff.4ndr@yandex.ru"

# Цвета
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

PASS=0
FAIL=0

print_header() {
  echo -e "\n${BLUE}==============================${NC}"
  echo -e "${BLUE} $1${NC}"
  echo -e "${BLUE}==============================${NC}"
}

assert_status() {
  local label="$1"
  local expected="$2"
  local actual="$3"
  local body="$4"

  if [ "$actual" = "$expected" ]; then
    echo -e "  ${GREEN}[PASS]${NC} $label (HTTP $actual)"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}[FAIL]${NC} $label — ожидали HTTP $expected, получили HTTP $actual"
    if [ -n "$body" ]; then
      echo -e "         ${YELLOW}Body:${NC} $(echo "$body" | head -c 300)"
    fi
    FAIL=$((FAIL + 1))
  fi
}

# Получить токен (кэшируем в переменную)
get_token() {
  "$SCRIPTS_DIR/get_token.sh" "$TEST_EMAIL"
}

print_summary() {
  echo ""
  echo -e "${BLUE}------------------------------${NC}"
  echo -e "  Итого: ${GREEN}$PASS passed${NC}, ${RED}$FAIL failed${NC}"
  echo -e "${BLUE}------------------------------${NC}"
  if [ "$FAIL" -gt 0 ]; then
    exit 1
  fi
}
