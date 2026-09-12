"""Download product images and upload them through the existing product photo API."""
import hashlib
from html.parser import HTMLParser
import json
from pathlib import Path
import time
from urllib.parse import urljoin, urlsplit, urlencode
from urllib.request import Request, build_opener
from urllib.error import HTTPError
import uuid

from core import PageText, source_identity

# Matches the configured backend multipart limit. Never truncate an image.
MAX_IMAGE_BYTES = 10 * 1024 * 1024
MAX_IMAGES = 20


def image_url(value):
    if not isinstance(value, str): raise ValueError('URL фото должен быть строкой')
    value = urljoin('https://simplewine.ru/', value)
    p = urlsplit(value)
    if (p.scheme != 'https' or p.hostname not in {'static.simplewine.ru', 'simplewine.ru', 'www.simplewine.ru'}
            or p.port not in {None, 443} or p.username or p.password or p.fragment):
        raise ValueError('Фото: разрешены только HTTPS URL SimpleWine и static.simplewine.ru')
    return value


class ImageMeta(HTMLParser):
    def __init__(self):
        super().__init__(); self.urls = []
    def handle_starttag(self, tag, attrs):
        a = dict(attrs)
        if tag == 'meta' and a.get('property') == 'og:image' and a.get('content'):
            self.urls.append(a['content'])


def extract_images(html, identity):
    parser = PageText(); parser.feed(html)
    urls = []
    def walk(node):
        if isinstance(node, list):
            for child in node: walk(child)
        elif isinstance(node, dict):
            kind = node.get('@type', [])
            if isinstance(kind, str): kind = [kind]
            if 'Product' in kind:
                if str(node.get('sku', identity)) != identity: return
                images = node.get('image', [])
                if not isinstance(images, list): images = [images]
                for value in images:
                    if isinstance(value, dict): value = value.get('contentUrl') or value.get('url')
                    if value: urls.append(value)
                return  # Do not collect recommendations nested in a product.
            if '@graph' in node: walk(node['@graph'])
    walk(parser.jsonld)
    if not urls:
        meta = ImageMeta(); meta.feed(html); urls = meta.urls
    result = list(dict.fromkeys(image_url(u) for u in urls))
    if len(result) > MAX_IMAGES: raise ValueError('Слишком много фотографий в карточке')
    if not result: raise ValueError('Фото не найдены в Product JSON-LD/og:image; укажите photoUrls в batch')
    return result


def image_type(raw):
    if raw.startswith(b'\xff\xd8\xff'): return 'image/jpeg', 'jpg'
    if raw.startswith(b'\x89PNG\r\n\x1a\n'): return 'image/png', 'png'
    if raw.startswith(b'RIFF') and raw[8:12] == b'WEBP': return 'image/webp', 'webp'
    raise ValueError('Ожидается JPEG, PNG или WebP; получен другой формат')


def download(url, folder, opener):
    from importer import now, read, write
    url = image_url(url); folder = Path(folder); folder.mkdir(parents=True, exist_ok=True)
    key = hashlib.sha256(url.encode()).hexdigest()
    meta_path = folder / (key + '.json'); path = folder / (key + '.image')
    if meta_path.exists() and path.exists():
        meta = read(meta_path); raw = path.read_bytes()
        if meta.get('url') != url or hashlib.sha256(raw).hexdigest() != meta.get('sha256'):
            raise ValueError('Изменился кэш фото; удалите повреждённые файлы и скачайте заново')
    else:
        request = Request(url, headers={'User-Agent': 'DomsommelierCatalogImporter/1.0', 'Accept': 'image/jpeg,image/png,image/webp'})
        with opener.open(request, timeout=30) as response:
            raw = response.read(MAX_IMAGE_BYTES + 1)
            if len(raw) > MAX_IMAGE_BYTES: raise ValueError('Фото больше 10 МиБ — лимита загрузки API')
            mime, _ = image_type(raw)
            if response.headers.get('Content-Type', '').split(';')[0].strip().lower() != mime:
                raise ValueError('Content-Type не соответствует формату фото')
        meta = {'url': url, 'sha256': hashlib.sha256(raw).hexdigest(), 'retrievedAt': now()}
        path.write_bytes(raw); write(meta_path, meta)
    if len(raw) > MAX_IMAGE_BYTES: raise ValueError('Фото больше 10 МиБ')
    mime, ext = image_type(raw)
    return dict(meta, path=str(path.resolve()), mime=mime, filename='simplewine-' + meta['sha256'] + '.' + ext)


def upload(api, product_id, photo):
    from importer import ApiError
    raw = Path(photo['path']).read_bytes()
    if hashlib.sha256(raw).hexdigest() != photo['sha256']: raise ValueError('Фото изменилось перед загрузкой')
    boundary = 'Domsomm' + uuid.uuid4().hex
    body = (f'--{boundary}\r\nContent-Disposition: form-data; name="files"; filename="{photo["filename"]}"\r\n'
            f'Content-Type: {photo["mime"]}\r\n\r\n').encode() + raw + f'\r\n--{boundary}--\r\n'.encode()
    request = Request(api.base + '/products/files/upload?' + urlencode({'productId': product_id}), data=body,
                      headers={'Authorization': 'Bearer ' + api.token, 'Content-Type': 'multipart/form-data; boundary=' + boundary}, method='POST')
    try:
        with api.opener.open(request, timeout=30) as response:
            if response.status != 204: raise RuntimeError('Неожиданный ответ API фото; проверьте результат вручную')
    except HTTPError as error: raise ApiError(error.code) from None


def run_photos(batch, raw_dir, photo_dir, state_path, api=None, apply=False, delay=2, browser=False):
    if browser:
        from browser_photos import BrowserImageOpener
        with BrowserImageOpener() as opener:
            return _run_photos(batch, raw_dir, photo_dir, state_path, api, apply, delay, opener)
    return _run_photos(batch, raw_dir, photo_dir, state_path, api, apply, delay)


def _run_photos(batch, raw_dir, photo_dir, state_path, api=None, apply=False, delay=2, opener=None):
    from importer import NoRedirect, locked_state, now, read
    if apply and api is None: raise ValueError('Нужен ADMIN_TOKEN существующей админской сессии')
    opener = opener or build_opener(NoRedirect()); products = []; requests = 0
    # Resolve and download the whole batch before uploading anything.
    for row in batch['products']:
        url, identity = source_identity(row['url'])
        urls = row.get('photoUrls')
        if urls is None:
            meta = read(Path(raw_dir) / (identity + '.json'))
            raw = (Path(raw_dir) / (identity + '.html')).read_bytes()
            if meta.get('status') != 'downloaded' or meta.get('url') != url or hashlib.sha256(raw).hexdigest() != meta.get('sha256'):
                raise ValueError('Нужен проверенный HTML из fetch для ' + identity)
            urls = extract_images(raw.decode('utf-8-sig'), identity)
        if not isinstance(urls, list) or not urls or len(urls) > MAX_IMAGES:
            raise ValueError('photoUrls: ожидается от 1 до 20 ссылок')
        photos = []; seen = set()
        for value in dict.fromkeys(image_url(u) for u in urls):
            if requests: time.sleep(max(0, delay))
            photo = download(value, Path(photo_dir) / identity, opener); requests += 1
            if photo['sha256'] not in seen: photos.append(photo); seen.add(photo['sha256'])
        products.append({'externalId': identity, 'article': row.get('article'), 'category': row['category'], 'photos': photos})
    report = {'status': 'downloaded', 'mode': 'apply' if apply else 'download-only', 'products': products}
    if not apply: return report
    with locked_state(state_path) as (state, save):
        target = state['targets'].get(api.base, {})
        for product in products:
            old = target.get(product['externalId'], {})
            if old.get('status') != 'created' or old.get('article') != product['article']:
                raise ValueError('Сначала импортируйте товар с тем же state: ' + product['externalId'])
            detail = api.request('GET', '/api/v1/products?' + urlencode({'id': old['productId']}))
            if detail['article'] != product['article'] or detail['productCategoryName'] != product['category']:
                raise ValueError('Товар в state не соответствует карточке')
            for entry in old.get('photos', {}).values():
                if entry.get('status') == 'pending': raise RuntimeError('Неопределённый результат загрузки фото; проверьте фото в админке и восстановите state')
        for product in products:
            old = target[product['externalId']]; journal = old.setdefault('photos', {})
            for photo in product['photos']:
                key = photo['sha256']
                if journal.get(key, {}).get('status') == 'uploaded':
                    photo['status'] = 'skipped_existing'; continue
                journal[key] = {'status': 'pending', 'filename': photo['filename'], 'sourceUrl': photo['url'], 'attemptedAt': now()}; save()
                try:
                    upload(api, old['productId'], photo)
                    journal[key]['status'] = 'uploaded'; save(); photo['status'] = 'uploaded'
                except Exception as error:
                    photo['error'] = str(error)
                    photo['status'] = 'uncertain'; report['status'] = 'stopped'; return report
    report['status'] = 'complete'
    return report
