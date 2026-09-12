"""Optional visible Chrome downloader. No stealth, personal profile or challenge solving."""
import hashlib
from pathlib import Path
import time
from core import extract, source_identity


def fetch_browser(batch, folder, refresh=False, delay=2, limit=None):
    try:
        from playwright.sync_api import sync_playwright, Error
    except ImportError:
        raise ValueError('Установите зависимости из requirements-browser.txt и запустите Python из .venv') from None
    from importer import MAX_BYTES, now, read, write
    folder = Path(folder); folder.mkdir(parents=True, exist_ok=True)
    rows = batch['products'][:limit] if limit else batch['products']
    report = []
    with sync_playwright() as playwright:
        try:
            browser = playwright.chromium.launch(channel='chrome', headless=False)
        except Error:
            raise RuntimeError('Не удалось запустить Google Chrome. Проверьте, что он установлен.') from None
        try:
            context = browser.new_context(locale='ru-RU')
            page = context.new_page()
            for index, row in enumerate(rows):
                url, identity = source_identity(row['url'])
                meta_path = folder / (identity + '.json'); html_path = folder / (identity + '.html')
                if not refresh and meta_path.exists() and html_path.exists():
                    cached = read(meta_path)
                    if cached.get('status') == 'downloaded' and cached.get('sha256') == hashlib.sha256(html_path.read_bytes()).hexdigest():
                        report.append(dict(cached, status='cached')); continue
                if index: time.sleep(max(0, delay))
                meta = {'externalId': identity, 'url': url, 'retrievedAt': now(), 'status': 'failed', 'transport': 'browser'}
                try:
                    response = page.goto(url, wait_until='domcontentloaded', timeout=45000)
                    status = response.status if response else None
                    meta['httpStatus'] = status
                    if status in {403, 429}:
                        meta['status'] = 'blocked'
                        raise ValueError('Браузер также получил отказ; автоматические повторы не выполняются')
                    if status is None or status >= 400: raise ValueError('Страница вернула ошибку HTTP')
                    if source_identity(page.url)[0] != url: raise ValueError('Перенаправление на другую карточку')
                    page.locator('h1').wait_for(state='visible', timeout=15000)
                    # Wait for the product identity, not for analytics/networkidle.
                    page.get_by_text('Артикул:', exact=False).first.wait_for(state='visible', timeout=15000)
                    raw = page.content().encode('utf-8')
                    if len(raw) > MAX_BYTES: raise ValueError('Слишком большая страница')
                    extract(raw.decode('utf-8'), url, row['category'], html=True)
                    html_path.write_bytes(raw)
                    meta.update(status='downloaded', sha256=hashlib.sha256(raw).hexdigest(), contentType='text/html; charset=utf-8')
                except (Error, ValueError) as error:
                    meta['error'] = str(error).splitlines()[0][:250]
                write(meta_path, meta); report.append(meta)
                print(identity + ': ' + meta['status'], flush=True)
                if meta['status'] != 'downloaded':
                    # Stop the batch on a challenge/error instead of hammering the site.
                    break
        finally:
            browser.close()
    write(folder / 'fetch-report.json', report)
    return report
