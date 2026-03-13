# CLAUDE.md — domsommelier-backend

## Обзор проекта

Backend для интернет-магазина вина и мероприятий **Dom Sommelier**.
REST API на Spring Boot 3.3.3, Java 17, сборка через Maven.

---

## Технологический стек

| Компонент       | Технология                                      |
|-----------------|-------------------------------------------------|
| Язык            | Java 17                                         |
| Фреймворк       | Spring Boot 3.3.3                               |
| Сборка          | Maven                                           |
| БД              | PostgreSQL 15                                   |
| ORM             | Spring Data JPA + Hibernate 6                   |
| Кэш/OTP         | Redis (Jedis 5 + Spring Data Redis)             |
| Хранилище файлов| MinIO 8.5                                       |
| Безопасность    | Spring Security + JWT (jjwt 0.11.5)             |
| Email           | Spring Mail (Yandex SMTP, SSL/STARTTLS)         |
| Маппинг         | Lombok, MapStruct 1.5, ModelMapper 3.2          |
| Валидация       | Hibernate Validator 8                           |
| Документация    | SpringDoc OpenAPI 2 (Swagger UI)                |
| Гео-типы        | Hibernate Spatial + JTS                         |
| Секреты         | HashiCorp Vault 1.14 (MPL 2.0) + Spring Cloud Vault |
| Тесты           | JUnit 5, MockMvc, H2, embedded-postgres         |

---

## Архитектура пакетов

Базовый пакет: `com.innovativesoftware.domsommelier_backend`

Структура — domain-driven, каждый домен в пакете `*_management`:

```
auth_management/          # OTP-аутентификация + JWT
customer_management/      # Клиенты, адреса, рекомендации
order_management/         # Заказы, позиции заказов, промокоды
product_management/       # Продукты (вино, крепкий алкоголь, игристое, снеки, аксессуары), склад
event_management/         # Мероприятия (Дегустации, Винные казино)
news_management/          # Новости
filter_management/        # Система фильтров продуктов
file_management/          # Общие модели для работы с файлами
infrastructure/           # Утилиты, ScheduleConfig, BucketRegistry, RedisService
exceptions/               # ApiErrorResponse, InvalidValueException, InvalidIdException
validators/               # Кастомные jakarta.validation аннотации
```

Внутри каждого домена подпакеты: `entity/`, `model/`, `repository/`, `service/`, `controller/`, `enums/`, `util/`

---

## Соглашения по коду

### Entities
- Аннотации Lombok: `@Getter` + `@Setter` (не `@Data`)
- ID: `UUID`, генерируется через `UUID.randomUUID()`
- Таблицы именуются явно через `@Table(name = "...")`
- Столбцы объявляются через `@Column(name = "...")`

### Services
- `@Service` + `@RequiredArgsConstructor`
- Зависимости через constructor injection (Lombok)
- Исключение "не найдено" — `NoSuchElementException` или кастомный `InvalidIdException`

### Controllers
- `@RestController` + `@CrossOrigin` + `@RequestMapping("/api/v1/...")`
- `@RequiredArgsConstructor` (предпочтительно) или `@Autowired` на поле
- Swagger: `@Tag` на классе, `@Operation` на методах, `@Parameter` на аргументах
- Скрытые/временные эндпоинты помечаются `@Hidden`
- Ответы через `ResponseEntity<T>` или напрямую тип (для простых GET)

### Маппинг
- Статические Mapper-классы (например `EventMapper`) — для простых случаев
- MapStruct интерфейсы — для сложного маппинга
- ModelMapper — для универсального маппинга product details

### Фильтрация
- JPA Specifications (`Specification<T>`) — стандартный способ построения динамических запросов
- `BaseSpecification` — базовый класс для спецификаций продуктов
- Конфигурация фильтров — JSON-файлы в `src/main/resources/filters/`

### Валидация
- `@Valid` + `@Validated` на входных DTO
- Кастомные аннотации в пакете `validators/`

---

## Аутентификация

Двухшаговый OTP-flow:

1. `POST /api/v1/auth/initiate` — генерирует 4-значный код, сохраняет `OtpSession` в Redis (TTL 5 мин), отправляет на email
2. `POST /api/v1/auth/confirm` — проверяет код, возвращает JWT токен

- Redis ключ: `auth:otp:{email}`
- Защита от спама: повторная отправка не ранее чем через 1 минуту
- JWT: jjwt, срок действия 86400000 мс (24 часа), секрет из `JWT_SECRET`

---

## Профили Spring

| Профиль  | Использование              |
|----------|----------------------------|
| `local`  | Разработка локально        |
| `docker` | Docker Compose продакшн    |
| `test`   | Тесты (embedded-postgres)  |

Активация через `SPRING_PROFILES_ACTIVE`.

---

## Инфраструктура (Docker)

`docker-compose.yaml` поднимает:
- `vault` — HashiCorp Vault 1.14 (хранение секретов, UI на :8200)
- `vault-init` — одноразовый контейнер: инициализация, unseal, создание policy/token
- `domsommelier-app` — Spring Boot приложение (порт 8080)
- `nginx` — reverse proxy (порт 80)
- `db` — PostgreSQL 15-alpine (healthcheck через `pg_isready`)
- `redis` — Redis (healthcheck через `redis-cli ping`)
- `minio` — MinIO для хранения фото (порт 9000/9001)

Все секреты хранятся в Vault (`secret/domsommelier`). Docker profile получает секреты через Spring Cloud Vault.
Для инфраструктурных контейнеров (db, minio) секреты передаются через shared Docker volume `/run/secrets/`.

---

## Тесты

- Расположены в `src/test/java/`
- Аннотации: `@SpringBootTest`, `@ActiveProfiles("test")`, `@AutoConfigureMockMvc`
- Используют MockMvc для HTTP-запросов к эндпоинтам
- Тестовые данные: SQL-скрипты в `src/test/resources/data/`
- Тест-профиль: `src/test/resources/application-test.properties`
- Покрыты: фильтры по категориям (вино, крепкий, игристое, слабоалкогольное, снеки, аксессуары), SparklingWine репозиторий

Запуск тестов:
```bash
./mvnw test
```

---

## Сборка и запуск

```bash
# Локальный запуск
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Сборка JAR
./mvnw clean package -DskipTests

# Docker
docker compose up --build
```

---

## Важные файлы

| Путь | Назначение |
|------|------------|
| `src/main/resources/application.yml` | Общая конфигурация (все профили) |
| `src/main/resources/application-local.yml` | Конфиг для локальной разработки (gitignored) |
| `src/main/resources/application-docker.yml` | Конфиг для Docker (Vault, сервисные хосты) |
| `src/main/resources/filters/` | JSON-конфиги фильтров продуктов по категориям |
| `src/main/resources/data/` | SQL-скрипты инициализации данных |
| `src/main/resources/init_photos/` | Начальные фото для MinIO |
| `infrastructure/docker/Dockerfile` | Docker-образ приложения |
| `infrastructure/nginx/nginx.conf` | Конфигурация reverse proxy |
| `infrastructure/db/create_db.sql` | SQL-скрипт создания БД (для Docker) |
| `vault/` | Конфигурация и скрипты Vault |
| `vault/config/vault.hcl` | Конфиг сервера Vault |
| `vault/scripts/init-vault.sh` | Скрипт инициализации Vault |
| `vault/init-secrets.env.example` | Шаблон секретов (коммитится в git) |
| `scripts/` | Bash-скрипты для API-тестирования |
| `docker-compose.yaml` | Docker инфраструктура |
| `pom.xml` | Зависимости Maven |
| `swagger.json` | Экспорт OpenAPI спецификации |

---

## DDL стратегия

В `application.yml` стоит `ddl-auto: create-drop` по умолчанию — схема пересоздаётся при каждом старте.
В продакшене при необходимости менять через переменную `SPRING_JPA_HIBERNATE_DDL-AUTO`.

---

## API

Все эндпоинты с префиксом `/api/v1/`.
Swagger UI доступен по `/swagger-ui.html` (SpringDoc OpenAPI 2).

Основные группы:
- `/api/v1/auth` — аутентификация
- `/api/v1/products` — продукты (поиск, фильтры, детали)
- `/api/v1/events` — мероприятия
- `/api/v1/filters` — получение конфигурации фильтров
- `/api/v1/news` — новости
