"""Visible Chrome transport for public product images, without an admin token."""
from io import BytesIO
from urllib.error import HTTPError


class BrowserImageOpener:
    def __enter__(self):
        try:
            from playwright.sync_api import sync_playwright, Error
        except ImportError:
            raise ValueError('Режим --browser требует Python из scripts/simplewine_import/.venv') from None
        self.error_type = Error
        self.runtime = sync_playwright().start()
        try:
            self.browser = self.runtime.chromium.launch(channel='chrome', headless=False)
            self.context = self.browser.new_context(locale='ru-RU')
            self.page = self.context.new_page()
            self.expected_url = None
            # Never follow a navigation redirect to an unreviewed image/domain.
            self.page.route('**/*', self.route)
        except Exception:
            self.runtime.stop()
            raise RuntimeError('Не удалось открыть Google Chrome для скачивания фотографий') from None
        return self

    def route(self, route):
        if route.request.is_navigation_request() and route.request.url != self.expected_url:
            route.abort()
        else:
            route.continue_()

    def __exit__(self, *args):
        try: self.browser.close()
        finally: self.runtime.stop()

    def open(self, request, timeout=30):
        from photos import image_url, MAX_IMAGE_BYTES
        self.expected_url = image_url(request.full_url)
        try:
            response = self.page.goto(self.expected_url, wait_until='load', timeout=timeout * 1000)
            if response is None: raise ValueError('Браузер не вернул ответ с изображением')
            if response.status != 200:
                raise HTTPError(self.expected_url, response.status, 'Image request failed', None, None)
            if self.page.url != self.expected_url: raise ValueError('Перенаправление изображения запрещено')
            length = response.headers.get('content-length')
            if length and int(length) > MAX_IMAGE_BYTES: raise ValueError('Фото больше 10 МиБ — лимита загрузки API')
            raw = response.body()
            if len(raw) > MAX_IMAGE_BYTES: raise ValueError('Фото больше 10 МиБ — лимита загрузки API')
            result = BytesIO(raw)
            result.headers = {'Content-Type': response.headers.get('content-type', '')}
            return result
        except self.error_type:
            raise RuntimeError('Ошибка/таймаут Chrome при скачивании изображения; запрос не повторяется') from None
