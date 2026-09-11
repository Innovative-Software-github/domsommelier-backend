#!/bin/bash
# Запустить все тестовые сценарии подряд

SCRIPTS_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPTS_DIR/common.sh"

TOTAL_PASS=0
TOTAL_FAIL=0

run_suite() {
  local script="$1"
  local name="$2"
  bash "$SCRIPTS_DIR/$script"
  local exit_code=$?
  if [ $exit_code -ne 0 ]; then
    echo -e "${RED}  Сценарий '$name' завершился с ошибками${NC}"
    TOTAL_FAIL=$((TOTAL_FAIL + 1))
  else
    TOTAL_PASS=$((TOTAL_PASS + 1))
  fi
}

echo -e "${BLUE}"
echo "============================================"
echo "   DOM SOMMELIER — Полный прогон сценариев  "
echo "============================================"
echo -e "${NC}"

run_suite "test_auth.sh"          "Auth"
run_suite "test_products.sh"      "Products"
run_suite "test_filters.sh"       "Filters"
run_suite "test_events.sh"        "Events"
run_suite "test_event_photos.sh"  "Event Photos"
run_suite "test_wine_stores.sh"   "Wine Stores"
run_suite "test_profile.sh"       "Profile"
run_suite "test_basket.sh"        "Basket"
run_suite "test_saved.sh"         "Saved"
run_suite "test_orders.sh"        "Orders"
run_suite "test_news.sh"          "News"
run_suite "test_discounts.sh"     "Discounts"

echo ""
echo -e "${BLUE}============================================${NC}"
echo -e "  Сценариев завершено успешно: ${GREEN}$TOTAL_PASS${NC}"
echo -e "  Сценариев с ошибками:        ${RED}$TOTAL_FAIL${NC}"
echo -e "${BLUE}============================================${NC}"

if [ "$TOTAL_FAIL" -gt 0 ]; then
  exit 1
fi
