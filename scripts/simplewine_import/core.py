"""Small, dependency-free SimpleWine extraction. No network or writes in this module."""
from html.parser import HTMLParser
from html import unescape
import hashlib
import json
import re
from urllib.parse import urlsplit

VERSION = 1
LABELS = {
    'страна': 'country', 'страна, регион': 'geography', 'регион': 'region',
    'апелласьон': 'appellation', 'аппелласьон': 'appellation', 'вино': 'color',
    'цвет': 'color', 'сахар': 'sugar', 'виноград': 'grapes', 'производитель': 'producer',
    'бренд': 'brand', 'крепость': 'strength', 'объем': 'volume', 'объём': 'volume',
    'год': 'year', 'температура подачи': 'temperature', 'потенциал хранения': 'cellaring',
    'декантация/аэрация': 'aeration', 'выдержка в емкости': 'vessel',
    'выдержка в ёмкости': 'vessel', 'подарочная упаковка': 'gift',
    'тип': 'whiskyType', 'класс': 'cognacClass', 'срок выдержки': 'age',
}
RATINGS = {'сладость': 'sweetness', 'кислотность': 'acidity', 'ароматичность': 'aromaticIntensity',
           'тело': 'body', 'танины': 'tannins', 'пряный': 'spicy', 'фруктовый': 'fruity',
           'цветочный': 'floral', 'древесный': 'woody'}
STOP = {'гастрономия', 'дегустационные характеристики', 'виноградники', 'способ производства',
        'способ выдержки', 'это интересно', 'о бренде', 'смотреть все характеристики',
        'вкус', 'параметры', 'характеристики', 'мнение эксперта', 'стилистика вина', 'винтаж'}


def clean(value):
    return re.sub(r'\s+', ' ', unescape(str(value))).strip(' ,\t\n\r')


def source_identity(url):
    parsed = urlsplit(url)
    match = re.fullmatch(r'/catalog/product/([a-z0-9_]+)_(\d+)/?', parsed.path)
    if parsed.scheme != 'https' or parsed.hostname not in {'simplewine.ru', 'www.simplewine.ru'} or parsed.port not in {None, 443} or parsed.username or parsed.password or not match:
        raise ValueError('Ожидается HTTPS-ссылка на карточку simplewine.ru/catalog/product/..._<артикул>/')
    return 'https://simplewine.ru' + parsed.path.rstrip('/') + '/', match.group(2)


class PageText(HTMLParser):
    """Keep table/block boundaries; discard scripts, styles, navigation and hidden content."""
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.parts, self.stack, self.h1, self.jsonld = [], [], [], []
        self.title_depth = 0
        self.script = None
    def handle_starttag(self, tag, attrs):
        attrs = dict(attrs)
        hidden = tag in {'script', 'style', 'nav', 'footer', 'header', 'noscript'} or 'hidden' in attrs or attrs.get('aria-hidden') == 'true'
        parent_hidden = self.stack[-1][1] if self.stack else False
        if tag == 'script' and attrs.get('type') == 'application/ld+json': self.script = []
        if tag not in {'br', 'hr', 'img', 'meta', 'link', 'input', 'source', 'wbr'}:
            self.stack.append((tag, parent_hidden or hidden))
        if tag == 'h1': self.title_depth += 1
        if not parent_hidden and tag in {'div', 'p', 'li', 'dt', 'dd', 'td', 'th', 'tr', 'h1', 'h2', 'h3', 'br'}: self.parts.append('\n')
    def handle_endtag(self, tag):
        if tag == 'script' and self.script is not None:
            try: self.jsonld.append(json.loads(''.join(self.script)))
            except ValueError: pass
            self.script = None
        if tag == 'h1': self.title_depth = max(0, self.title_depth - 1)
        for i in range(len(self.stack) - 1, -1, -1):
            if self.stack[i][0] == tag:
                del self.stack[i:]; break
        if tag in {'div', 'p', 'li', 'dt', 'dd', 'td', 'th', 'tr', 'h1', 'h2', 'h3'}: self.parts.append('\n')
    def handle_data(self, data):
        if self.script is not None: self.script.append(data)
        if self.stack and self.stack[-1][1]: return
        self.parts.append(data)
        if self.title_depth: self.h1.append(data)


def numeric(text):
    match = re.search(r'(?<!\d)(\d+(?:[.,]\d+)?)', text or '')
    return float(match.group(1).replace(',', '.')) if match else None


def interval(text):
    match = re.search(r'(\d+(?:[.,]\d+)?)\s*[-–—]\s*(\d+(?:[.,]\d+)?)', text or '')
    if match:
        return {'min': float(match.group(1).replace(',', '.')), 'max': float(match.group(2).replace(',', '.'))}
    return None


def extract(content, url, category, html=True):
    url, external_id = source_identity(url)
    parser = PageText()
    if html:
        parser.feed(content)
        text, title = ''.join(parser.parts), clean(''.join(parser.h1))
    else:
        text = content
        title = next((clean(s[2:]) for s in content.splitlines() if s.startswith('# ')), '')
    if not title: raise ValueError('Не найден заголовок карточки: возможно, страница ошибки или изменилась разметка')
    lines = [clean(s) for s in text.splitlines() if clean(s)]
    # Do not parse recommendations, reviews or product cards in the footer.
    first = next((i for i, line in enumerate(lines) if title in line), 0)
    lines = lines[first:]
    for i, line in enumerate(lines):
        if i and re.match(r'^(Отзывы|Похожие товары|Вам также|Рекомендуем|В каталоге интернет)', line):
            lines = lines[:i]; break
    body = '\n'.join(lines)
    article = re.search(r'Артикул\s*:\s*(\d+)', body, re.I)
    if not article or article.group(1) != external_id:
        raise ValueError('Артикул в карточке не совпадает со ссылкой или отсутствует')
    expected = {'wine': r'^Вино\s', 'champagne_and_sparkling': r'^(Игристое вино|Шампанское)\s', 'spirit': r'^(Виски|Коньяк)\s'}
    if category not in expected or not re.search(expected[category], title, re.I):
        raise ValueError('Категория не совпадает с заголовком; поддержаны вино, игристое, виски и коньяк')
    values, evidence, issues = {}, {}, []
    for i, line in enumerate(lines):
        head, sep, tail = line.partition(':')
        key = LABELS.get(head.lower())
        if not key: continue
        value = clean(tail) if sep else ''
        if not value:
            chunks = []
            for candidate in lines[i+1:i+5]:
                lower = candidate.lower().rstrip(':')
                if lower in LABELS or lower in STOP or ':' in candidate or re.match(r'^\d.*[₽]', candidate): break
                chunks.append(candidate)
                if not candidate.endswith(',') and key not in {'geography', 'grapes'}: break
            value = clean(', '.join(chunks))
        if not value: continue
        if key in values and values[key].casefold() != value.casefold():
            issues.append('Противоречивое поле ' + key)
        else: values[key], evidence[key] = value, line + ': ' + value
    facts = {'title': title, 'name': re.sub(r'^(Игристое вино|Шампанское|Вино|Виски|Коньяк)\s+', '', title, flags=re.I),
             'category': category, 'country': values.get('country'), 'region': values.get('region'),
             'producer': values.get('producer'), 'brand': values.get('brand'),
             'color': values.get('color'), 'sugar': values.get('sugar'),
             'appellation': values.get('appellation'), 'volume': numeric(values.get('volume')),
             'strength': numeric(values.get('strength')), 'temperature': interval(values.get('temperature')),
             'vessel': values.get('vessel'), 'grapes': [], 'ratings': {}}
    if values.get('geography'):
        geo = [clean(s) for s in values['geography'].split(',') if clean(s)]
        facts['country'] = geo[0]
        if not facts['region'] and len(geo) > 1: facts['region'] = geo[1]
    year = numeric(values.get('year'))
    facts['year'] = int(year) if year and year.is_integer() else None
    if facts['year'] is None and re.search(r'год урожая\s*\d{4}', body, re.I):
        issues.append('Год встречается только в дополнительном тексте; винтаж не подтверждён')
    for field, suffix in [('strength', r'%'), ('volume', r'л(?:итр(?:а|ов)?)?')]:
        raw_value = values.get(field)
        if raw_value and not re.fullmatch(r'\d+(?:[.,]\d+)?\s*' + suffix, raw_value, re.I):
            facts[field] = None
            issues.append('Не удалось однозначно разобрать ' + field + ': ' + raw_value)
    if category == 'spirit':
        facts['subcategory'] = 'Виски' if title.lower().startswith('виски') else 'Коньяк'
        facts['whiskyType'] = values.get('whiskyType') if facts['subcategory'] == 'Виски' else None
        facts['cognacClass'] = values.get('cognacClass') if facts['subcategory'] == 'Коньяк' else None
        # Only explicit tabular age; age in marketing text never becomes a stated age.
        age_text = values.get('age', '')
        age_match = re.fullmatch(r'(\d+)\s*(?:год|года|лет)?', age_text, re.I)
        facts['age'] = int(age_match.group(1)) if age_match else None
        if age_text and not age_match: issues.append('Срок выдержки не является точным заявленным возрастом: ' + age_text)
    if category == 'champagne_and_sparkling':
        facts['subcategory'] = 'Просекко' if re.search(r'prosecco|просекко', title, re.I) else None
        facts['sparklingMethod'] = 'Шарма' if re.search(r'метод\s+Шарма', body, re.I) else None
    facts['giftBox'] = True if values.get('gift', '').lower() == 'да' or 'в подарочной упаковке' in title.lower() else False if values.get('gift', '').lower() == 'нет' else None
    for part in re.split(r'[,;]\s*(?!\d+%)', values.get('grapes', '')):
        if not part: continue
        match = re.fullmatch(r'(.+?)\s+(\d+(?:[.,]\d+)?)\s*%', part)
        facts['grapes'].append({'label': clean(match.group(1)) if match else clean(part), 'percent': float(match.group(2).replace(',', '.')) if match else None})
    for line in lines:
        for label, key in RATINGS.items():
            match = re.fullmatch(re.escape(label) + r'\s*:?\s*(\d+(?:[.,]\d+)?)', line, re.I)
            if match: facts['ratings'][key] = float(match.group(1).replace(',', '.'))
    cellaring = interval(values.get('cellaring'))
    facts['cellaring'] = {'minYears': int(cellaring['min']), 'maxYears': int(cellaring['max']), 'reference': 'unspecified'} if cellaring else None
    facts['aeration'] = {'не рекомендуется': 'not_recommended', 'рекомендуется': 'recommended'}.get(values.get('aeration', '').lower())
    facts['name'] = re.sub(r'\s+в подарочной упаковке$', '', facts['name'], flags=re.I)
    return {'source': {'provider': 'simplewine', 'externalId': external_id, 'url': url,
                       'sha256': hashlib.sha256(content.encode()).hexdigest()},
            'facts': facts, 'evidence': evidence, 'issues': sorted(set(issues))}


def reference(kind, label, dictionary, refs, issues):
    if not label: return None
    lookup = clean(label).casefold()
    value = dictionary.get(kind, {}).get(lookup)
    if not value:
        issues.append(f'Нужно сопоставить справочник {kind}: {label}')
        return None
    if not re.fullmatch(r'[a-z0-9][a-z0-9_-]{0,79}', value['code']) or not clean(value['label']):
        raise ValueError('Некорректный код/название в справочнике ' + kind)
    refs[(kind, value['code'])] = {'kind': kind, **value}
    return dict(value)


def build_plan(extracted, row, dictionary):
    facts, refs = extracted['facts'], {}
    issues = list(extracted['issues'])
    def ref(kind, value): return reference(kind, value, dictionary, refs, issues)
    payload = {k: row.get(k) for k in ('article', 'initialPrice', 'price')}
    payload.update({k: facts.get(k) for k in ('category', 'name', 'producer', 'country', 'volume')})
    payload['brand'] = ref('brand', facts.get('brand'))
    payload['packaging'] = {'giftBox': facts['giftBox'], 'type': None} if facts['giftBox'] is not None else None
    ext = {'region': ref('region', facts.get('region')), 'appellation': ref('appellation', facts.get('appellation')),
           'servingTemperature': facts.get('temperature')}
    vessel = facts.get('vessel')
    if vessel:
        ext['aging'] = {'status': 'not_aged' if vessel.lower() == 'нет' else 'aged',
                        'vessels': [] if vessel.lower() == 'нет' else [ref('vessel', vessel)], 'durationMonths': None}
    if facts['category'] in {'wine', 'champagne_and_sparkling'}:
        ext['strength'] = facts['strength']
        payload['color'] = (facts.get('color') or '').capitalize() or None
        ext['grapeComposition'] = [{'grape': ref('grape', g['label']), 'percent': g['percent']} for g in facts['grapes']] or None
        shares = [g['percent'] for g in facts['grapes']]
        ext['grapeCompositionComplete'] = bool(shares and all(x is not None for x in shares) and abs(sum(shares) - 100) < 1e-9)
        ext['sensoryProfile'] = {k: {'value': v, 'scaleMin': 1, 'scaleMax': 5, 'sourceId': 'SW-' + extracted['source']['externalId']} for k,v in facts['ratings'].items() if k in {'sweetness','acidity','aromaticIntensity','body','tannins'}} or None
        ext['cellaringPotential'], ext['aerationRecommendation'] = facts.get('cellaring'), facts.get('aeration')
        if facts['category'] == 'wine':
            payload['productionYear'], payload['type'] = facts['year'], (facts.get('sugar') or '').capitalize() or None
        else:
            payload['subcategory'], payload['sugarContent'] = facts.get('subcategory'), (facts.get('sugar') or '').capitalize() or None
            ext.update({'productionYear': facts['year'], 'vintageStatus': 'vintage' if facts['year'] else 'unknown', 'sparklingMethod': ref('sparkling_method', facts.get('sparklingMethod'))})
    else:
        payload['subcategory'], payload['strength'] = facts.get('subcategory'), facts['strength']
        if facts['subcategory'] == 'Виски':
            ext['whiskyDetails'] = {'whiskyType': ref('whisky_type', facts.get('whiskyType')), 'ageStatementYears': facts.get('age'), 'ageStatementStatus': 'stated' if facts.get('age') else 'unknown'}
        else:
            ext['cognacDetails'] = {'ageClassification': ref('age_classification', facts.get('cognacClass')), 'ageStatementYears': facts.get('age')}
        ext['sensoryRatings'] = [{'dimension': ref('sensory_dimension', k), 'rating': {'value': v, 'scaleMin': 1, 'scaleMax': 5, 'sourceId': 'SW-' + extracted['source']['externalId']}} for k,v in facts['ratings'].items()] or None
    payload['extendedDetails'] = ext
    # Overrides are explicit corrections by the operator, never generated by the parser.
    overrides = row.get('overrides', {})
    if overrides: deep_merge(payload, overrides)
    blockers = validate_payload(payload)
    if issues and row.get('reviewed') is not True: blockers.append('Не подтверждена проверка замечаний')
    if row.get('reviewed') is not True: blockers.append('Карточка ещё не проверена: reviewed=false')
    if any(s.startswith('Нужно сопоставить') for s in issues): blockers.append('Есть несопоставленные справочники')
    # Collect every reference, including explicitly supplied overrides, using the same field mapping as the API.
    refs = collect_references(payload)
    return {'source': extracted['source'], 'facts': facts, 'evidence': extracted['evidence'],
            'payload': payload, 'references': refs, 'issues': sorted(set(issues)),
            'blockers': sorted(set(blockers)), 'reviewed': row.get('reviewed') is True}


def deep_merge(target, source):
    for key, value in source.items():
        if isinstance(value, dict) and isinstance(target.get(key), dict): deep_merge(target[key], value)
        else: target[key] = value


REF_FIELDS = {'brand':'brand','type':'packaging','region':'region','appellation':'appellation','grape':'grape',
 'aromaTags':'aroma','flavorTags':'flavor','foodPairingTags':'food_pairing','styleTags':'style','vessels':'vessel',
 'vessel':'vessel','wood':'wood','previousContents':'previous_contents','servingTags':'serving',
 'dimension':'sensory_dimension','whiskyType':'whisky_type','blendStyle':'blend_style',
 'ageClassification':'age_classification','originArea':'origin_area','sparklingMethod':'sparkling_method'}


def collect_references(payload):
    result = {}
    def walk(value, key=''):
        if isinstance(value, dict):
            if 'code' in value or 'label' in value:
                kind = REF_FIELDS.get(key)
                if not kind or set(value) != {'code','label'} or not re.fullmatch(r'[a-z0-9][a-z0-9_-]{0,79}', str(value.get('code',''))) or not isinstance(value.get('label'), str) or not value['label'].strip():
                    raise ValueError('Некорректная справочная ссылка: ' + key)
                item = {'kind':kind, **value}
                identity = (kind,value['code'])
                if identity in result and result[identity] != item: raise ValueError('Конфликт подписей справочника')
                result[identity] = item
            else:
                for k,v in value.items(): walk(v,k)
        elif isinstance(value,list):
            for v in value: walk(v,key)
    walk(payload)
    return sorted(result.values(), key=lambda r:(r['kind'],r['code']))


def validate_payload(p):
    errors = []
    cat = p.get('category')
    required = ['article','name','country','volume','initialPrice','price']
    if cat == 'wine': required += ['color','productionYear']
    elif cat == 'champagne_and_sparkling': required += ['color','subcategory','sugarContent']
    elif cat == 'spirit': required += ['subcategory','strength']
    else: errors.append('Неподдерживаемая категория')
    for key in required:
        if p.get(key) is None or p.get(key) == '' or isinstance(p.get(key),str) and not p[key].strip(): errors.append('Не заполнено: ' + key)
    for key in ['article','name','country','producer','color','type','subcategory','sugarContent']:
        if p.get(key) is not None and not isinstance(p[key],str): errors.append('Ожидается строка: '+key)
    for key in ['volume','initialPrice','price'] + (['strength'] if cat == 'spirit' else []):
        value = p.get(key)
        if value is not None and (isinstance(value,bool) or not isinstance(value,(int,float)) or not 0 < value < float('inf')): errors.append('Недопустимое число: '+key)
    if p.get('productionYear') is not None and (type(p['productionYear']) is not int or not 1900 <= p['productionYear'] <= 2100): errors.append('Недопустимый год')
    if cat == 'spirit' and p.get('strength') is not None and p['strength'] > 100: errors.append('Крепость превышает 100%')
    if any(k in p for k in ['stocks','stock','quantity','productPhoto','photos','sourcePrice']): errors.append('Импорт остатков, фото и цены источника не поддержан')
    allowed = {'article','name','category','country','initialPrice','price','salePrice','description','aroma','taste','foodPairing','brand','packaging','extendedDetails','producer','volume','features'}
    allowed |= {'wine':{'productionYear','color','type','grapes'},'champagne_and_sparkling':{'subcategory','color','sugarContent'},'spirit':{'subcategory','strength'}}.get(cat,set())
    for key in p.keys()-allowed: errors.append('Неподдерживаемое поле: '+key)
    ext = p.get('extendedDetails') or {}
    for composition in [ext.get('grapeComposition'), (ext.get('cognacDetails') or {}).get('grapeComposition')]:
        if composition:
            shares=[g.get('percent') for g in composition]
            if any(v is not None and not 0 <= v <= 100 for v in shares) or sum(v for v in shares if v is not None)>100: errors.append('Недопустимые доли сортов')
    def walk(v):
        if isinstance(v,dict):
            if 'min' in v and 'max' in v and v['min'] is not None and v['max'] is not None and v['min']>v['max']: errors.append('Перепутаны границы диапазона')
            for x in v.values(): walk(x)
        elif isinstance(v,list):
            for x in v: walk(x)
    walk(ext)
    return errors
