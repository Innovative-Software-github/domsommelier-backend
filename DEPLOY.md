# Деплой на свой сервер (бэкенд + оба фронтенда)

Всё крутится одним `docker-compose` стеком на одном сервере. Три репозитория —
три независимых GitHub Actions workflow, каждый умеет пересобрать только свой
контейнер после первого полного разворачивания.

Домены: `devdomsomm.ru` (витрина), `admin.devdomsomm.ru` (админка),
`api.devdomsomm.ru` (бэкенд + `/docs` → Swagger UI).

---

## 0. Что нужно купить/завести самому (я это сделать не могу)

- **VPS с публичным IP.** Для этого стека (Spring Boot + Postgres + Redis + MinIO +
  Next.js + статика админки) достаточно 2 vCPU / 4 GB RAM на старте — с запасом
  комфортнее 4 vCPU / 8 GB. Диск — от 20–30 GB (фото товаров растут в MinIO).
  Любой провайдер с Ubuntu 22.04/24.04 подойдёт — reg.ru, Selectel, Timeweb,
  Hetzner, DigitalOcean и т.п.
- **Домен `devdomsomm.ru`** должен быть у тебя в управлении (регистратор,
  где можно поменять DNS-записи).
- После получения IP сервера — прописать в DNS **четыре A-записи**, все на этот IP:
  ```
  devdomsomm.ru        A   <IP сервера>
  www.devdomsomm.ru    A   <IP сервера>
  admin.devdomsomm.ru  A   <IP сервера>
  api.devdomsomm.ru    A   <IP сервера>
  ```
  Подожди, пока они разойдутся (проверить: `dig +short devdomsomm.ru`) — без этого
  Let's Encrypt не сможет подтвердить домены.

---

## 1. Подготовить сервер

SSH на сервер под root'ом и поставить Docker + Compose:

```bash
curl -fsSL https://get.docker.com | sh
apt-get install -y docker-compose-plugin
ln -s /usr/libexec/docker/cli-plugins/docker-compose /usr/local/bin/docker-compose 2>/dev/null || true
```

(Workflow'и вызывают именно `docker-compose` через дефис — убедись, что команда
доступна: `docker-compose version`.)

---

## 2. Секреты в GitHub

В каждом из трёх репозиториев — **Settings → Secrets and variables → Actions**:

**domsommelier-backend** (уже должно быть, если деплоился раньше — проверить, что
значения актуальны, а не от старого сервера):
`HOST`, `SSH_PASSWORD`, `DB_USER`, `DB_PASSWORD`, `DB_NAME`,
`MINIO_ROOT_USER`, `MINIO_ROOT_PASSWORD`, `MINIO_ACCESS_NAME`, `MINIO_ACCESS_SECRET`,
`MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_PROTOCOL`,
`JWT_SECRET`, `VITE_YANDEX_MAPS_API_KEY` (последний — новый, нужен для сборки админки
в fresh-deploy ветке этого же workflow).

**domsommelier-frontend** (новые):
`HOST`, `SSH_PASSWORD` — те же значения, тот же сервер.

**domsommelier-admin-frontend** (новые):
`HOST`, `SSH_PASSWORD`, `VITE_YANDEX_MAPS_API_KEY` — те же значения.

`HOST` — новый IP сервера (не старый, оставшийся от прошлого разработчика).
`SSH_PASSWORD` — пароль root на новом сервере.

⚠️ Так как это новый сервер, а не тот, что был раньше — **обязательно ротировать
`JWT_SECRET` и пароль от почты** (`MAIL_PASSWORD`), раз старые значения когда-то
были закоммичены в `.env` в git-историю (см. прошлый аудит). Сейчас — самый
безопасный момент это сделать, заодно с переездом.

---

## 3. Первый деплой (backend + оба фронтенда, без HTTPS)

Просто запушь (или сделай пустой коммит/re-run workflow) в `main` **domsommelier-backend**.
Его workflow сам:
1. Склонирует себя и оба фронтенда как соседние папки в `~/projects/`.
2. Соберёт и поднимет `domsommelier-app`, `domsommelier-frontend`, `domsommelier-admin-frontend`,
   `db`, `redis`, `minio`, `vault`.
3. **Осознанно не поднимет `nginx`/`certbot`** — сертификатов ещё нет, `nginx.conf`
   их требует и упадёт при старте.

Проверить, что всё поднялось: `docker ps` на сервере должен показать все контейнеры,
кроме `reverse_proxy`.

---

## 4. Выпустить HTTPS-сертификаты (один раз, вручную)

DNS должен уже указывать на сервер (шаг 0). На сервере:

```bash
cd ~/projects/domsommelier-backend
./scripts/init-letsencrypt.sh you@example.com
```

Скрипт сам: поднимет nginx во временном HTTP-режиме → получит сертификат на все
4 домена сразу → переключит nginx на боевой конфиг (HTTP+HTTPS).

В конце — запустить сервис автопродления:
```bash
docker-compose up -d certbot
```
(Продлевает сам каждые 12 часов; после продления nginx нужно перечитать конфиг —
`docker-compose exec nginx nginx -s reload`, можно повесить на cron раз в неделю.)

---

## 5. Проверка

- `https://devdomsomm.ru` — витрина
- `https://admin.devdomsomm.ru` — админка (залогиниться через OTP, назначить
  себе `ROLE_ADMIN` через `scripts/grant_admin.sh <email>` на сервере)
- `https://api.devdomsomm.ru/docs` — Swagger UI

---

## 6. Дальнейшие деплои — уже автоматические

- Пуш в `main` **domsommelier-backend** → пересобирает и рестартит только бэкенд.
- Пуш в `main` **domsommelier-frontend** → пересобирает и рестартит только витрину.
- Пуш в `main` **domsommelier-admin-frontend** → пересобирает и рестартит только админку.

Ни один из этих обычных деплоев не трогает `nginx`/сертификаты/базу — шаги 3–4
нужны один раз, только на новом сервере.
