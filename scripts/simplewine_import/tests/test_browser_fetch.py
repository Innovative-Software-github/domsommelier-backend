import hashlib
import json
from pathlib import Path
import sys
import tempfile
import types
import unittest
from unittest.mock import patch
sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
from browser_fetch import fetch_browser

class BrowserTests(unittest.TestCase):
    def run_fetch(self,status=200,html=None):
        self.calls=[]
        page=types.SimpleNamespace()
        page.url='https://simplewine.ru/catalog/product/test_123/'
        def goto(url,**kwargs): self.calls.append(url); return types.SimpleNamespace(status=status)
        page.goto=goto
        locator=types.SimpleNamespace(wait_for=lambda **kwargs:None)
        locator.first=locator
        page.locator=lambda *args:locator
        page.get_by_text=lambda *args,**kwargs:locator
        page.content=lambda: html or '<h1>Вино Test</h1><p>Артикул: 123</p>'
        self.closed=False
        def close(): self.closed=True
        browser=types.SimpleNamespace(new_context=lambda **kwargs:types.SimpleNamespace(new_page=lambda:page),close=close)
        runtime=types.SimpleNamespace(chromium=types.SimpleNamespace(launch=lambda **kwargs:browser))
        class Context:
            def __enter__(self):return runtime
            def __exit__(self,*args):pass
        module=types.ModuleType('playwright.sync_api');module.sync_playwright=Context;module.Error=RuntimeError
        batch={'products':[{'url':page.url,'category':'wine'},{'url':'https://simplewine.ru/catalog/product/test_456/','category':'wine'}]}
        with tempfile.TemporaryDirectory() as tmp,patch.dict(sys.modules,{'playwright.sync_api':module}):
            report=fetch_browser(batch,tmp,delay=0,limit=1 if status==200 else None)
            if report[0]['status']=='downloaded':
                raw=(Path(tmp)/'123.html').read_bytes()
                self.assertEqual(hashlib.sha256(raw).hexdigest(),report[0]['sha256'])
            else:self.assertFalse((Path(tmp)/'123.html').exists())
        self.assertTrue(self.closed)
        return report
    def test_browser_saves_verified_html(self):
        self.assertEqual(self.run_fetch()[0]['status'],'downloaded')
    def test_block_stops_batch_without_saving_error_as_html(self):
        self.assertEqual(self.run_fetch(403)[0]['status'],'blocked');self.assertEqual(len(self.calls),1)
    def test_wrong_product_is_rejected(self):
        self.assertEqual(self.run_fetch(html='<h1>Вино Other</h1><p>Артикул: 999</p>')[0]['status'],'failed')
