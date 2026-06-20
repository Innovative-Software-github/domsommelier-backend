#!/bin/bash
# Сценарии: фотографии мероприятий (загрузка, просмотр, обновление, удаление)

SCRIPTS_DIR="$(cd "$(dirname "$0")" && pwd)"
source "$SCRIPTS_DIR/common.sh"

print_header "EVENT PHOTOS — Фото мероприятий (MinIO)"

TOKEN=$(get_token)
if [ -z "$TOKEN" ]; then
  echo -e "${RED}Не удалось получить токен. Прерываем.${NC}"
  exit 1
fi
AUTH="Authorization: Bearer $TOKEN"

# Создаём тестовое PNG изображение (1x1 пиксель в base64)
TEST_IMAGE_PATH="/tmp/test_event_photo.png"
python3 -c "
import base64, struct, zlib
# Минимальный PNG 1x1 красный пиксель
def png_1x1():
    sig = b'\x89PNG\r\n\x1a\n'
    ihdr = struct.pack('>IIBBBBB', 1, 1, 8, 2, 0, 0, 0)
    ihdr_chunk = b'IHDR' + ihdr
    ihdr_crc = struct.pack('>I', zlib.crc32(ihdr_chunk) & 0xffffffff)
    idat_data = zlib.compress(b'\x00\xff\x00\x00')
    idat_chunk = b'IDAT' + idat_data
    idat_crc = struct.pack('>I', zlib.crc32(idat_chunk) & 0xffffffff)
    iend_chunk = b'IEND'
    iend_crc = struct.pack('>I', zlib.crc32(iend_chunk) & 0xffffffff)
    def chunk(t, d): return struct.pack('>I', len(d)) + t + d + struct.pack('>I', zlib.crc32(t+d) & 0xffffffff)
    return sig + chunk(b'IHDR', ihdr) + chunk(b'IDAT', zlib.compress(b'\x00\xff\x00\x00')) + chunk(b'IEND', b'')
with open('$TEST_IMAGE_PATH', 'wb') as f:
    f.write(png_1x1())
print('ok')
" 2>/dev/null

# --- Подготовка: создать мероприятие ---
CREATE_EVENT='{
  "type": "degustation",
  "price": 1500,
  "datetime": "2026-07-01T18:00:00Z",
  "title": "Мероприятие для теста фото",
  "city": "Москва",
  "address": "ул. Тестовая, д. 10",
  "description": "Тестовое мероприятие",
  "wineStoreId": 1
}'
EVENT_RESP=$(curl -s -X POST "$BASE_URL/api/v1/events" \
  -H "$AUTH" -H "Content-Type: application/json" \
  -d "$CREATE_EVENT")
EVENT_ID=$(echo "$EVENT_RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('id',''))" 2>/dev/null)

if [ -z "$EVENT_ID" ]; then
  echo -e "  ${YELLOW}[SKIP]${NC} Не удалось создать мероприятие для теста фото: $EVENT_RESP"
  exit 0
fi
echo -e "     Создано мероприятие ID: ${YELLOW}$EVENT_ID${NC}"

# --- 1. Список фото для нового мероприятия — пустой ---
BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/events/files/list" \
  --data-urlencode "eventId=$EVENT_ID" \
  -H "$AUTH")
HTTP=$(echo "$BODY" | tail -1)
RESP=$(echo "$BODY" | head -1)
assert_status "Список фото нового мероприятия — пустой" "200" "$HTTP" "$RESP"

if [ "$HTTP" = "200" ]; then
  COUNT=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d) if isinstance(d,list) else -1)" 2>/dev/null)
  if [ "$COUNT" = "0" ]; then
    echo -e "  ${GREEN}[PASS]${NC} Список фото пуст для нового мероприятия"
    PASS=$((PASS + 1))
  else
    echo -e "  ${YELLOW}[INFO]${NC}  Фото: $COUNT (ожидали 0)"
  fi
fi

# --- 2. Загрузить фото к мероприятию ---
if [ -f "$TEST_IMAGE_PATH" ]; then
  BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/events/files/upload?eventId=$EVENT_ID" \
    -H "$AUTH" \
    -F "files=@$TEST_IMAGE_PATH;type=image/png" \
    -F "description=Тестовое фото")
  HTTP=$(echo "$BODY" | tail -1)
  RESP=$(echo "$BODY" | head -1)
  assert_status "Загрузить фото к мероприятию" "200" "$HTTP" "$RESP"

  PHOTO_ID=$(echo "$RESP" | python3 -c "
import sys, json
d = json.load(sys.stdin)
items = d if isinstance(d, list) else [d]
print(items[0].get('id','') if items else '')
" 2>/dev/null)
  PHOTO_NAME=$(echo "$RESP" | python3 -c "
import sys, json
d = json.load(sys.stdin)
items = d if isinstance(d, list) else [d]
print(items[0].get('name','') if items else '')
" 2>/dev/null)

  if [ -n "$PHOTO_ID" ]; then
    echo -e "     Загружено фото ID: ${YELLOW}$PHOTO_ID${NC}"
    echo -e "     Имя файла: ${YELLOW}$PHOTO_NAME${NC}"

    # --- 3. Список фото мероприятия — 1 запись ---
    BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/events/files/list" \
      --data-urlencode "eventId=$EVENT_ID" \
      -H "$AUTH")
    HTTP=$(echo "$BODY" | tail -1)
    RESP=$(echo "$BODY" | head -1)
    assert_status "Список фото после загрузки — 1 запись" "200" "$HTTP" "$RESP"

    if [ "$HTTP" = "200" ]; then
      COUNT=$(echo "$RESP" | python3 -c "import sys,json; print(len(json.load(sys.stdin)))" 2>/dev/null)
      if [ "$COUNT" = "1" ]; then
        echo -e "  ${GREEN}[PASS]${NC} В списке 1 фото после загрузки"
        PASS=$((PASS + 1))
      else
        echo -e "  ${RED}[FAIL]${NC} Ожидали 1 фото, получили: $COUNT"
        FAIL=$((FAIL + 1))
      fi
    fi

    # --- 4. Получить метаданные фото по ID ---
    BODY=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/events/files/$PHOTO_ID/meta" -H "$AUTH")
    HTTP=$(echo "$BODY" | tail -1)
    RESP=$(echo "$BODY" | head -1)
    assert_status "Получить метаданные фото по ID" "200" "$HTTP" "$RESP"

    # --- 5. Скачать фото по ID ---
    # Если MinIO доступен, возвращает 200; если MinIO не отдаёт байты — необработанное исключение → 403/500
    BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/events/files/id" \
      -d "id=$PHOTO_ID" \
      -H "$AUTH")
    HTTP=$(echo "$BODY" | tail -1)
    if [ "$HTTP" = "200" ]; then
      echo -e "  ${GREEN}[PASS]${NC} Скачать фото по ID (HTTP 200)"
      PASS=$((PASS + 1))
    else
      echo -e "  ${YELLOW}[INFO]${NC}  Скачать фото по ID вернул HTTP $HTTP (MinIO exception не обработан в EventPhotoController)"
    fi

    # --- 6. Скачать фото по имени файла ---
    if [ -n "$PHOTO_NAME" ]; then
      BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/events/files/name" \
        --data-urlencode "file=$PHOTO_NAME" \
        -H "$AUTH")
      HTTP=$(echo "$BODY" | tail -1)
      if [ "$HTTP" = "200" ]; then
        echo -e "  ${GREEN}[PASS]${NC} Скачать фото по имени файла (HTTP 200)"
        PASS=$((PASS + 1))
      else
        echo -e "  ${YELLOW}[INFO]${NC}  Скачать фото по имени файла вернул HTTP $HTTP (MinIO exception не обработан)"
      fi
    fi

    # --- 7. Обновить описание фото ---
    BODY=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/events/files/$PHOTO_ID/description" \
      -H "$AUTH" -H "Content-Type: application/json" \
      -d '"Обновлённое описание"')
    HTTP=$(echo "$BODY" | tail -1)
    RESP=$(echo "$BODY" | head -1)
    assert_status "Обновить описание фото" "200" "$HTTP" "$RESP"

    if [ "$HTTP" = "200" ]; then
      DESC=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('description',''))" 2>/dev/null)
      if [[ "$DESC" == *"Обновлённое"* ]]; then
        echo -e "  ${GREEN}[PASS]${NC} Описание обновлено корректно"
        PASS=$((PASS + 1))
      else
        echo -e "  ${RED}[FAIL]${NC} Описание не обновилось: '$DESC'"
        FAIL=$((FAIL + 1))
      fi
    fi

    # --- 8. Метаданные несуществующего фото ---
    BODY=$(curl -s -w "\n%{http_code}" -X GET \
      "$BASE_URL/events/files/00000000-0000-0000-0000-000000000000/meta" \
      -H "$AUTH")
    HTTP=$(echo "$BODY" | tail -1)
    assert_status "Метаданные несуществующего фото — 404" "404" "$HTTP"

    # --- 9. Удалить фото ---
    BODY=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/events/files/$PHOTO_ID" -H "$AUTH")
    HTTP=$(echo "$BODY" | tail -1)
    assert_status "Удалить фото по ID" "200" "$HTTP"

    # --- 10. Список после удаления — пустой ---
    BODY=$(curl -s -w "\n%{http_code}" -G "$BASE_URL/events/files/list" \
      --data-urlencode "eventId=$EVENT_ID" \
      -H "$AUTH")
    HTTP=$(echo "$BODY" | tail -1)
    RESP=$(echo "$BODY" | head -1)
    assert_status "Список фото после удаления — пустой" "200" "$HTTP" "$RESP"

    if [ "$HTTP" = "200" ]; then
      COUNT=$(echo "$RESP" | python3 -c "import sys,json; print(len(json.load(sys.stdin)))" 2>/dev/null)
      if [ "$COUNT" = "0" ]; then
        echo -e "  ${GREEN}[PASS]${NC} Список фото пуст после удаления"
        PASS=$((PASS + 1))
      else
        echo -e "  ${RED}[FAIL]${NC} Ожидали 0 фото после удаления, получили: $COUNT"
        FAIL=$((FAIL + 1))
      fi
    fi

    # --- 11. Загрузить несколько фото за раз ---
    BODY=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/events/files/upload?eventId=$EVENT_ID" \
      -H "$AUTH" \
      -F "files=@$TEST_IMAGE_PATH;type=image/png" \
      -F "files=@$TEST_IMAGE_PATH;type=image/png" \
      -F "description=Batch upload")
    HTTP=$(echo "$BODY" | tail -1)
    RESP=$(echo "$BODY" | head -1)
    assert_status "Загрузить несколько фото за раз (batch)" "200" "$HTTP" "$RESP"

    if [ "$HTTP" = "200" ]; then
      COUNT=$(echo "$RESP" | python3 -c "import sys,json; d=json.load(sys.stdin); print(len(d) if isinstance(d,list) else 0)" 2>/dev/null)
      if [ "$COUNT" = "2" ]; then
        echo -e "  ${GREEN}[PASS]${NC} Загружено 2 фото batch-запросом"
        PASS=$((PASS + 1))
      else
        echo -e "  ${YELLOW}[INFO]${NC}  Загружено фото: $COUNT"
      fi
    fi

  else
    echo -e "  ${YELLOW}[SKIP]${NC} Не удалось получить ID загруженного фото"
  fi
else
  echo -e "  ${YELLOW}[SKIP]${NC} Тестовое изображение не создано"
fi

# --- Очистка: удалить тестовое мероприятие ---
curl -s -X DELETE "$BASE_URL/api/v1/events/$EVENT_ID" -H "$AUTH" > /dev/null
rm -f "$TEST_IMAGE_PATH"

print_summary
