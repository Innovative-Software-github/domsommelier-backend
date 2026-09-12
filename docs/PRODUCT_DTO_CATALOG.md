# Каталог новых DTO товаров

Статус: первый этап реализован локально — DTO, хранение характеристик, справочники и форма админки. На продакшен изменения ещё не выложены. Этап 2 — [фильтры расширенного каталога](CATALOG_FILTERS_IMPLEMENTATION.md) — также реализован локально. Контракт записи, совместимость и порядок обновления описаны в [PRODUCT_ATTRIBUTES_IMPLEMENTATION.md](PRODUCT_ATTRIBUTES_IMPLEMENTATION.md). Примеры источников ниже остаются материалом для будущего импорта, а не готовыми запросами API.

Этот файл — единый реестр характеристик по категориям. Новые ссылки добавляются сюда вместе с извлечёнными данными, источником и предлагаемыми изменениями DTO. Схему уточняем по мере изучения реальных карточек.

## Категории

| Категория API | Состояние | Источники |
|---|---|---|
| `wine` — вино | Первая версия расширения и пример | SW-160429 |
| `champagne_and_sparkling` — игристое | Схема и пример; винтаж требует проверки | SW-160963 |
| `spirit` — крепкие напитки | Общая схема + виски и коньяк | SW-141968, SW-156688 |
| `low_alcohol` — слабоалкогольные | Ожидает ссылки | — |
| `snack` — снеки | Ожидает ссылки | — |
| `accessories` — аксессуары | Ожидает ссылки | — |

## Правила ведения и импорта

- Разделяем факты источника, выводы из описания и предложения по схеме.
- Отсутствующее или неподтверждённое значение — `null`; отсутствие данных не равно нулю.
- Для списков `null` означает «не изучено», `[]` — «проверено, значений нет». Это правило реализовано для новых характеристик; существующие поля сохраняют прежнюю семантику.
- Справочники используют стабильные коды и отдельные подписи. Коды не генерируем заново из написания поставщика.
- Единицы фиксированы: объём — литры, алкоголь — проценты, температура — °C, выдержка — месяцы.
- Деньги на бэкенде — BigDecimal; клиентское представление согласуется с текущим API.
- Чужой артикул хранится как внешний идентификатор, а не заменяет наш `article`.
- Цена и наличие у источника не заменяют наши цены и остатки.
- Оценки вкуса сохраняются со шкалой и источником. Не переводим их автоматически в нашу шкалу 1–5, пока шкала источника не проверена.
- Неподтверждённые характеристики из текста остаются черновиком. Для импорта сверяем производителя, название, винтаж и объём.
- Фотографии и полные описания не публикуются автоматически вместе с извлечёнными фактами.

## Общая модель ProductDetailDto

Сохраняем существующие верхнеуровневые поля; расширения категории располагаем в `details`.

| Поле | Тип | Назначение / состояние |
|---|---|---|
| `id` | UUID | Существующий внутренний ID; только чтение |
| `article` | string | Наш артикул |
| `name` | string | Название |
| `productCategoryName` | category code | Категория |
| `productCountry` | string | Существующее представление страны; не переименовываем без миграции |
| `initialPrice` | decimal | Существующее ценовое поле; семантику сохраняем |
| `price` | decimal | Наша цена |
| `salePrice` | decimal или null | Наша акционная цена |
| `description` | string или null | Описание для покупателя |
| `aroma` | string или null | Текст об аромате |
| `taste` | string или null | Текст о вкусе |
| `foodPairing` | string или null | Текст о гастросочетаниях |
| `productPhoto` | ProductPhotoDto[] | Существующие изображения |
| `createdAt` | datetime | Время создания; только чтение |
| `details` | DTO категории | Подробные характеристики |
| `brand` | ReferenceDto или null | Новое: бренд отдельно от производителя |
| `packaging` | PackagingDto или null | Новое: упаковка конкретного варианта товара |

Остатки по винотекам остаются отдельной сущностью. Рекомендации проверяют их перед показом.

### Запись и чтение

Этот документ описывает расширенную модель чтения. Текущий запрос записи отличается: например, использует `category` и `country`, а характеристики передаёт по своему контракту. Новые поля нужно будет явно добавить в write DTO, мапперы и админку. Не отправлять приведённые ниже черновики в текущий API напрямую.

## Вино: WineDetailsDto

### Существующие поля

| Поле | Тип | Примечание |
|---|---|---|
| `productionYear` | integer | Год урожая; текущая модель требует значение |
| `color` | dictionary code | Цвет |
| `type` | dictionary code или null | Тип по содержанию сахара; сохраняем существующее имя |
| `grapes` | string[] | Существующие коды сортов |
| `producer` | string или null | Производитель |
| `volume` | decimal | Литры |
| `features` | string[] | Существующие особенности |

### Предлагаемые новые поля

На первом этапе все дополнительные поля необязательны, чтобы не блокировать существующий каталог.

| Поле | Тип | Смысл |
|---|---|---|
| `region` | ReferenceDto или null | Винодельческий регион |
| `appellation` | ReferenceDto или null | Апелласьон; отдельное значение от региона |
| `strength` | decimal или null | Алкоголь, % |
| `grapeComposition` | GrapeShareDto[] или null | Сорта и известные доли; дополнение к `grapes` |
| `sensoryProfile` | SensoryProfileDto или null | Структурированные оценки вкуса |
| `aromaTags` | ReferenceDto[] или null | Ароматические группы |
| `flavorTags` | ReferenceDto[] или null | Вкусовые признаки |
| `styleTags` | ReferenceDto[] или null | Стилистика |
| `foodPairingTags` | ReferenceDto[] или null | Гастросочетания |
| `aging` | AgingDto или null | Ёмкости, срок и описание выдержки |
| `productionMethod` | string или null | Способ производства |
| `servingTemperature` | TemperatureRangeDto или null | Рекомендуемая температура |
| `cellaringPotential` | CellaringPotentialDto или null | Потенциал хранения с указанием точки отсчёта |
| `aerationRecommendation` | enum или null | `recommended`, `not_recommended`, `optional` |

### Вложенные DTO — проект типов

```typescript
type ReferenceDto = {
  code: string;
  label: string;
};

type GrapeShareDto = {
  grape: ReferenceDto;
  percent: number | null;
};

type SourceRatingDto = {
  value: number;
  scaleMin: number | null;
  scaleMax: number | null;
  sourceId: string;
};

type SensoryProfileDto = {
  sweetness: SourceRatingDto | null;
  acidity: SourceRatingDto | null;
  aromaticIntensity: SourceRatingDto | null;
  body: SourceRatingDto | null;
  tannins: SourceRatingDto | null;
};

type AgingDto = {
  status: 'aged' | 'not_aged' | 'unknown';
  vessels: ReferenceDto[] | null;
  durationMonths: DurationRangeDto | null;
  description: string | null;
};

type DurationRangeDto = {
  min: number | null;
  max: number | null;
  minInclusive: boolean;
  maxInclusive: boolean;
};

type PackagingDto = {
  giftBox: boolean | null;
  type: ReferenceDto | null;
};

type TemperatureRangeDto = { min: number; max: number };

type CellaringPotentialDto = {
  minYears: number;
  maxYears: number;
  reference: 'vintage' | 'bottling' | 'purchase' | 'unspecified';
};
```

Проверки: минимум диапазона не больше максимума; доли сортов в пределах 0–100 и в сумме не больше 100. Сумма 100 обязательна только для состава, отмеченного как полный. Полноту состава потребуется добавить при реализации. `grapes` и `grapeComposition` не должны расходиться — при наличии состава список сортов выводится из него.

Оценки источника служат исходными данными. Общие внутренние оценки для рекомендаций требуют согласованной шкалы и отдельного преобразования.

## Игристое: SparklingWineDetailsDto

### Существующие поля и совместимость

В текущем фронтенде: `category`, `content`, `color`, `producer`, `volume`, `features`. Админка при чтении использует `details.subcategory`, а при записи — `subcategory` и `sugarContent`. Это расхождение нужно проверить на реальном ответе API и согласовать до реализации; здесь не объявляем молчаливое переименование.

### Предлагаемые новые поля

| Поле | Тип | Назначение |
|---|---|---|
| `region`, `appellation` | ReferenceDto или null | География и наименование происхождения |
| `strength` | decimal или null | Алкоголь, % |
| `productionYear` | integer или null | Только подтверждённый год урожая |
| `vintageStatus` | enum | `vintage`, `non_vintage`, `unknown`; отсутствие года не означает невинтажное |
| `grapes` | string[] или null | Сорта по справочнику |
| `grapeComposition` | GrapeShareDto[] или null | Доли сортов |
| `sparklingMethod` | ReferenceDto или null | Метод получения игристого |
| `sensoryProfile` | SensoryProfileDto или null | Винные шкалы с происхождением оценок |
| `aromaTags`, `flavorTags`, `styleTags`, `foodPairingTags` | ReferenceDto[] или null | Структурированные признаки |
| `aging` | AgingDto или null | Наличие и параметры выдержки |
| `productionMethod` | string или null | Уточнения технологии |
| `servingTemperature` | TemperatureRangeDto или null | Подача |
| `cellaringPotential` | CellaringPotentialDto или null | Хранение |

Вторичная ферментация в резервуаре не должна автоматически означать выдержку. Признак веганского продукта можно хранить в существующем `features`, согласовав код справочника; не создаём дублирующий флаг.

## Крепкие напитки: SpiritDetailsDto

Виски и коньяк остаются подкатегориями `spirit`, а не новыми верхнеуровневыми категориями. Текущие `category`, `strength`, `producer`, `volume`, `features` сохраняются. Предлагаемые структуры ниже необязательны.

### Общие новые поля

| Поле | Тип | Назначение |
|---|---|---|
| `region`, `appellation` | ReferenceDto или null | География; регион не выводится из адреса бренда |
| `aging` | AgingDto или null | Тип ёмкости и известные границы срока |
| `agingStages` | AgingStageDto[] или null | Основная и дополнительная выдержка отдельно |
| `productionMethod` | string или null | Технология |
| `servingTemperature` | TemperatureRangeDto или null | Подача |
| `servingTags` | ReferenceDto[] или null | Способы подачи отдельно от еды |
| `aromaTags`, `flavorTags`, `foodPairingTags` | ReferenceDto[] или null | Аромат, вкус и гастросочетания |
| `sensoryRatings` | SensoryDimensionDto[] или null | Именованные шкалы крепких напитков |
| `whiskyDetails` | WhiskySpecificDto или null | Только для виски |
| `cognacDetails` | CognacSpecificDto или null | Только для коньяка |

`brand` и `packaging` находятся в общей модели товара. Слово «бренд» в источнике не подтверждает юридического производителя.

```typescript
type SensoryDimensionDto = {
  dimension: ReferenceDto;
  rating: SourceRatingDto;
};

type AgingStageDto = {
  order: number;
  purpose: 'primary' | 'finish' | 'unspecified';
  vessel: ReferenceDto | null;
  wood: ReferenceDto | null;
  previousContents: ReferenceDto | null;
  durationMonths: DurationRangeDto | null;
};

type WhiskySpecificDto = {
  whiskyType: ReferenceDto | null;
  blendStyle: ReferenceDto | null;
  componentCount: number | null;
  ageStatementYears: number | null;
  ageStatementStatus: 'stated' | 'nas' | 'unknown';
};

type CognacSpecificDto = {
  ageClassification: ReferenceDto | null;
  ageStatementYears: number | null;
  grapeComposition: GrapeShareDto[] | null;
  originArea: ReferenceDto | null;
};
```

Не подставляем отсутствующий срок как ноль. NAS сохраняется как классификация источника, а срок из описания — отдельно; эти поля не взаимозаменяемы. Название релиза не доказывает отдельную стадию финиша. Виски не получает винную шкалу кислотности по умолчанию.

## Изменения общей схемы после трёх новых карточек

- В `AgingDto` добавлен статус: «не выдерживалось» отличаем от отсутствия информации.
- `DurationRangeDto` поддерживает открытые границы и строгие неравенства, чтобы не превращать «больше N» в ровно N.
- `brand` отделён от `producer`; упаковка — свойство конкретного SKU.
- Шкалы крепких напитков описываются именованными измерениями; значения разных шкал нельзя напрямую смешивать.
- Все дополнения здесь — проект, не миграция действующего API.

## Источники и извлечённые примеры

### SW-160429 — вино

[Карточка SimpleWine: Paddle Creek Sauvignon Blanc 2025](https://simplewine.ru/catalog/product/paddle_creek_sauvignon_blanc_2025_075_160429/)

Получено: 2026-09-12. Способ: чтение публичной страницы. Статус: извлечено, не импортировано, не проверено редактором. Эта запись получена при разовом чтении; скрипт подготовки импорта теперь находится в [scripts/simplewine_import](../scripts/simplewine_import/README.md), но прямое скачивание сейчас блокируется HTTP 403.

Ниже — факты страницы в промежуточном формате. Это не готовый запрос записи товара: внутренних кодов справочников и нашего артикула ещё нет.

```json
{
  "sourceId": "SW-160429",
  "externalArticle": "160429",
  "category": "wine",
  "name": "Paddle Creek Sauvignon Blanc",
  "producer": "Lake Road",
  "productionYear": 2025,
  "countryLabel": "Новая Зеландия",
  "regionLabel": "Мальборо",
  "appellationLabel": "Marlborough",
  "colorLabel": "белое",
  "sugarTypeLabel": "полусухое",
  "grapes": [{ "label": "совиньон блан", "percent": 100 }],
  "strength": 12.5,
  "volume": 0.75,
  "sourceRatings": {
    "sweetness": 1,
    "acidity": 3,
    "aromaticIntensity": 4,
    "body": 3,
    "tannins": null,
    "scaleMin": null,
    "scaleMax": null
  },
  "aging": {
    "status": "aged",
    "vesselLabel": "нейтральная ёмкость",
    "durationMonths": null
  },
  "servingTemperature": { "min": 10, "max": 12 },
  "cellaringPotential": {
    "minYears": 2,
    "maxYears": 3,
    "reference": "unspecified"
  },
  "aerationRecommendation": "not_recommended"
}
```

Предлагаемые теги из описания, пока без кодов справочников:

- Аромат: яблоко, грейпфрут, крыжовник, гуава, маракуйя.
- Гастросочетания: лёгкие закуски, рыба, птица, азиатская кухня.
- Стилистика: освежающее, цветочно-фруктовое, без дубовой выдержки.

Не определены: числовой срок выдержки, танины, границы сенсорной шкалы, точка отсчёта потенциала хранения. Не восстанавливаем эти значения догадкой.

### SW-160963 — игристое

[Источник SimpleWine](https://simplewine.ru/catalog/product/prosecco_brut_075_160963/)

Получено: 2026-09-12. Чтение индексированной страницы. Статус: `needs_review`; данные не импортированы.

```json
{
  "sourceId": "SW-160963",
  "externalArticle": "160963",
  "category": "champagne_and_sparkling",
  "name": "Bruni Prosecco Brut",
  "producerLabel": "Bruni",
  "countryLabel": "Италия",
  "regionLabel": "Венето",
  "appellationLabel": "Prosecco DOC",
  "subcategoryLabel": "Просекко",
  "colorLabel": "белое",
  "sugarTypeLabel": "брют",
  "strength": 11,
  "volume": 0.75,
  "productionYear": null,
  "vintageStatus": "unknown",
  "grapes": [
    {
      "label": "глера",
      "percent": 100
    }
  ],
  "sparklingMethodLabel": "Шарма",
  "featureLabels": [
    "веганское"
  ],
  "aging": {
    "status": "not_aged",
    "durationMonths": null
  },
  "sourceRatings": {
    "sweetness": 1,
    "acidity": 3,
    "aromaticIntensity": 3,
    "body": 2,
    "scaleMin": null,
    "scaleMax": null
  },
  "servingTemperature": {
    "min": 8,
    "max": 10
  },
  "cellaringPotential": {
    "minYears": 1,
    "maxYears": 2,
    "reference": "unspecified"
  }
}
```

Проверить: в дополнительном товарном тексте указан урожай 2025, но в основных характеристиках винтаж отсутствует. Сохраняем кандидата 2025 только как замечание, до сверки бутылки/партии. Теги из описания на проверку: яблоко, цветы, груша; сочетания — рыба, ризотто, овощи, мягкий сыр. Границы сенсорной шкалы не подтверждены.

### SW-141968 — виски

[Источник SimpleWine](https://simplewine.ru/catalog/product/bellevoye_finition_grain_fin_07_gift_141968/)

Получено: 2026-09-12. Прямое открытие вернуло HTTP 403; характеристики извлечены из индексированного поискового представления именно этой ссылки и артикула. Статус: `needs_review`; актуальность требует повторной проверки.

```json
{
  "sourceId": "SW-141968",
  "externalArticle": "141968",
  "category": "spirit",
  "subcategoryLabel": "виски",
  "name": "Bellevoye Finition Grain Fin",
  "brandLabel": "Bellevoye",
  "producer": null,
  "countryLabel": "Франция",
  "regionLabel": null,
  "strength": 40,
  "volume": 0.7,
  "packaging": {
    "giftBox": true,
    "type": null
  },
  "whiskyDetails": {
    "whiskyTypeLabel": "солодовый",
    "blendStyleLabel": "triple malt",
    "componentCount": 3,
    "ageStatementYears": null,
    "ageStatementStatus": "unknown"
  },
  "aging": {
    "status": "aged",
    "vesselLabel": "дубовая бочка",
    "woodLabel": "французский дуб",
    "durationMonths": null
  },
  "agingStages": null,
  "servingTemperature": {
    "min": 16,
    "max": 20
  },
  "sensoryRatings": null
}
```

Производитель не нормализован: в дополнительном тексте есть сокращённая запись LES BIENHEUREUX, требующая сверки. Теги из описания на проверку: зерновые, специи, мёд, цветы, яблоко; сочетания — сыры, рыба, морепродукты, орехи, сухофрукты. Срок, числовой профиль и отдельный финиш не подтверждены; данные соседних релизов не переносились.

### SW-156688 — коньяк

[Источник SimpleWine](https://simplewine.ru/catalog/product/camus_ile_de_re_fine_island_07_gift_156688/)

Получено: 2026-09-12. Чтение индексированной страницы. Статус: `needs_review`; есть расхождение сведений о выдержке.

```json
{
  "sourceId": "SW-156688",
  "externalArticle": "156688",
  "category": "spirit",
  "subcategoryLabel": "коньяк",
  "name": "Camus Ile de Re Fine Island",
  "brandLabel": "Camus",
  "producer": null,
  "countryLabel": "Франция",
  "regionLabel": "Коньяк",
  "appellationLabel": "Cognac AOC",
  "strength": 40,
  "volume": 0.7,
  "packaging": {
    "giftBox": true,
    "type": null
  },
  "cognacDetails": {
    "ageClassificationLabel": "NAS",
    "ageStatementYears": null,
    "originAreaLabel": "остров Ре",
    "grapeComposition": null
  },
  "aging": {
    "status": "aged",
    "vesselLabel": "дубовая бочка",
    "durationMonths": null
  },
  "servingTemperature": {
    "min": 18,
    "max": 20
  },
  "sourceRatings": {
    "spicy": 2,
    "fruity": 4,
    "floral": 5,
    "woody": 3,
    "scaleMin": null,
    "scaleMax": null
  }
}
```

Конфликт: описание говорит о сроке свыше 3 лет, дополнительный текст называет напиток трёхлетним; число в DTO оставлено пустым. Там же упомянут 2021 год, но винтаж не подтверждён — не переносим. Теги из описания на проверку: йодистый, дубовый, сухофрукты, солоноватый; сочетания — морепродукты и рыба. Подача охлаждённым в экспертном совете — альтернативный сценарий, не замена табличной температуры. Границы шкалы не подтверждены.

## Метаданные импорта — отдельно от публичной карточки

Для будущего черновика импорта предлагаем `ProductImportDraftDto`:

| Поле | Назначение |
|---|---|
| `sourceId`, `sourceUrl`, `externalArticle` | Происхождение записи |
| `fetchedAt` | Фактическое время извлечения |
| `matchedProductId` | Наш товар после подтверждения соответствия, иначе null |
| `rawFacts` | Значения источника до приведения к справочникам |
| `normalizedCandidate` | Кандидат для записи в нашу модель |
| `fieldProvenance` | Для каждого поля: источник, исходное значение, способ извлечения |
| `reviewStatus` | `extracted`, `needs_review`, `approved`, `rejected` |
| `warnings` | Пропуски, неоднозначности, конфликты |

Реализация этих DTO отложена до согласования схемы.

## Как добавлять следующие ссылки

1. Определить категорию, производителя, винтаж и объём конкретного товара.
2. Извлечь только характеристики этой карточки, исключив меню, соседние товары и отзывы.
3. Добавить источник и фактические значения в раздел примеров.
4. Сопоставить поля со схемой категории; новые поля пометить как предложения.
5. Сохранить неизвестные значения и вопросы явно, не заполнять их предположениями.
6. Обновить таблицу категорий и журнал ниже.

## Журнал изменений

| Дата | Изменение |
|---|---|
| 2026-09-12 | Создан реестр; добавлены общая модель, расширение вина и пример SW-160429 |
| 2026-09-12 | Добавлены SW-160963, SW-141968, SW-156688; схемы игристого и крепких напитков, упаковка, бренд, шкалы и диапазоны выдержки |
