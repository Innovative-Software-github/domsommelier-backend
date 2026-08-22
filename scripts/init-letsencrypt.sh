#!/bin/bash
# Первый выпуск Let's Encrypt сертификатов для devdomsomm.ru + поддомены.
# Запускать один раз на новом сервере, ПОСЛЕ того как DNS A-записи для всех
# доменов ниже уже указывают на этот сервер (иначе ACME-challenge не пройдёт).
#
# Использование: ./scripts/init-letsencrypt.sh you@example.com
#
# Что делает:
#   1. Поднимает nginx во временном HTTP-only режиме (nginx.bootstrap.conf).
#   2. Запрашивает у Let's Encrypt один сертификат на все домены сразу.
#   3. Переключает nginx на боевой конфиг (nginx.conf, HTTP+HTTPS) и перезапускает.
#
# Дальнейшее продление — само, сервис `certbot` в docker-compose.yaml
# каждые 12 часов проверяет срок и продлевает; после продления нужно
# перечитать конфиг nginx: docker compose exec nginx nginx -s reload
# (можно повесить отдельным cron на сервере).

set -euo pipefail

EMAIL="${1:-}"
DOMAINS=(devdomsomm.ru www.devdomsomm.ru admin.devdomsomm.ru api.devdomsomm.ru)

if [ -z "$EMAIL" ]; then
  echo "Использование: $0 you@example.com"
  exit 1
fi

DOMAIN_ARGS=()
for d in "${DOMAINS[@]}"; do
  DOMAIN_ARGS+=(-d "$d")
done

echo "==> Поднимаем nginx во временном HTTP-only режиме..."
NGINX_CONF=nginx.bootstrap.conf docker compose up -d nginx

echo "==> Ждём, пока nginx поднимется..."
sleep 3

echo "==> Запрашиваем сертификат у Let's Encrypt для: ${DOMAINS[*]}"
# --entrypoint certbot обязателен: у сервиса certbot в docker-compose.yaml
# задан свой entrypoint (бесконечный цикл renew раз в 12ч, для автопродления).
# docker compose run переопределяет только command, а не entrypoint — без
# --entrypoint команда certonly ниже молча игнорируется, и вместо неё
# выполняется тот самый renew-цикл (который ничего не делает, если
# сертификата ещё не было ни разу, и просто "виснет" на 12 часов).
docker compose run --rm --entrypoint certbot certbot certonly \
  --webroot -w /var/www/certbot \
  --email "$EMAIL" \
  --agree-tos --no-eff-email \
  "${DOMAIN_ARGS[@]}"

echo "==> Сертификат получен. Переключаем nginx на боевой конфиг..."
docker compose up -d nginx

echo "==> Готово. Проверить: https://devdomsomm.ru, https://admin.devdomsomm.ru, https://api.devdomsomm.ru"
echo "==> Не забудь запустить сервис certbot для автопродления: docker compose up -d certbot"
