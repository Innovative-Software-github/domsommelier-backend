import sys
from pathlib import Path
from types import SimpleNamespace
import unittest
from urllib.request import Request
from urllib.error import HTTPError
sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
from browser_photos import BrowserImageOpener
from photos import MAX_IMAGE_BYTES

class BrowserPhotoTests(unittest.TestCase):
    def opener(self,status=200,raw=b'\x89PNG\r\n\x1a\n',length=None):
        opener=BrowserImageOpener();opener.error_type=TimeoutError
        headers={'content-type':'image/png'}
        if length:headers['content-length']=str(length)
        self.read_body=False
        def body():self.read_body=True;return raw
        response=SimpleNamespace(status=status,headers=headers,body=body)
        def goto(url,**kwargs):opener.page.url=url;return response
        opener.page=SimpleNamespace(goto=goto)
        return opener
    def test_returns_bytes_and_mime(self):
        opener=self.opener()
        with opener.open(Request('https://static.simplewine.ru/a.png')) as result:
            self.assertEqual(result.read(),b'\x89PNG\r\n\x1a\n')
            self.assertEqual(result.headers['Content-Type'],'image/png')
    def test_refusal_does_not_read_body(self):
        opener=self.opener(status=403)
        with self.assertRaises(HTTPError):opener.open(Request('https://static.simplewine.ru/a.png'))
        self.assertFalse(self.read_body)
    def test_large_content_length_rejected_before_body(self):
        opener=self.opener(length=MAX_IMAGE_BYTES+1)
        with self.assertRaises(ValueError):opener.open(Request('https://static.simplewine.ru/a.png'))
        self.assertFalse(self.read_body)
    def test_redirect_navigation_aborted(self):
        opener=self.opener();opener.expected_url='https://static.simplewine.ru/a.png';actions=[]
        route=SimpleNamespace(request=SimpleNamespace(url='https://other.test/',is_navigation_request=lambda:True),abort=lambda:actions.append('abort'),continue_=lambda:actions.append('continue'))
        opener.route(route);self.assertEqual(actions,['abort'])
