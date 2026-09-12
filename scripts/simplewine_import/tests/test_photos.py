import hashlib
import json
from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from importer import Api, NoRedirect, read, write
from photos import extract_images, image_url, image_type, download, run_photos, upload, MAX_IMAGE_BYTES

PNG=b'\x89PNG\r\n\x1a\n'+b'test-fixture'
URL='https://static.simplewine.ru/upload/bottle.png'

class Response:
    status=200
    headers={'Content-Type':'image/png'}
    def __enter__(self): return self
    def __exit__(self,*args): pass
    def read(self,n): return PNG[:n]

class Opener:
    def __init__(self): self.requests=[]
    def open(self,request,timeout): self.requests.append(request); return Response()

class PhotoTests(unittest.TestCase):
    def setUp(self):
        self.tmp=tempfile.TemporaryDirectory(); self.root=Path(self.tmp.name)
        self.batch={'products':[{'url':'https://simplewine.ru/catalog/product/test_123/','category':'wine','article':'SW-123','photoUrls':[URL]}]}
        self.api=Api('http://localhost:8080','test-secret')
        self.api.request=lambda *args: {'article':'SW-123','productCategoryName':'wine'}
        self.state=self.root/'state.json'
        write(self.state,{'version':1,'targets':{self.api.base:{'123':{'status':'created','article':'SW-123','productId':'product-id'}}}})
    def tearDown(self): self.tmp.cleanup()
    def run_batch(self,apply=True):
        return run_photos(self.batch,self.root/'raw',self.root/'photos',self.state,self.api,apply,0)
    def test_extract_only_product_images_deduplicate_and_ignore_recommendation(self):
        html='<img src="https://static.simplewine.ru/banner.png"><script type="application/ld+json">'+json.dumps([
            {'@type':'Product','sku':'999','image':'https://static.simplewine.ru/other.png'},
            {'@type':'Product','sku':'123','image':[URL,{'url':URL}]}])+'</script>'
        self.assertEqual(extract_images(html,'123'),[URL])
        self.assertEqual(extract_images('<meta property="og:image" content="'+URL+'">','123'),[URL])
    def test_reject_foreign_hosts_and_non_images(self):
        for url in ['http://static.simplewine.ru/x','https://static.simplewine.ru.evil.test/x','https://user@static.simplewine.ru/x','https://127.0.0.1/x']:
            with self.assertRaises(ValueError): image_url(url)
        with self.assertRaises(ValueError): image_type(b'<html>access denied</html>')
    def test_cache_reuse_tampering_and_no_auth_to_source(self):
        opener=Opener(); photo=download(URL,self.root,opener); download(URL,self.root,opener)
        self.assertEqual(len(opener.requests),1)
        self.assertFalse(opener.requests[0].has_header('Authorization'))
        Path(photo['path']).write_bytes(b'changed')
        with self.assertRaises(ValueError): download(URL,self.root,opener)
    def test_size_limit(self):
        class LargeResponse(Response):
            def read(self,n): return b'x'*(MAX_IMAGE_BYTES+1)
        opener=Opener(); opener.open=lambda *args,**kwargs: LargeResponse()
        with self.assertRaises(ValueError): download(URL,self.root,opener)
    def test_download_only_does_not_upload_or_change_state(self):
        before=self.state.read_bytes()
        with patch('photos.build_opener',return_value=Opener()),patch('photos.upload') as send:
            self.assertEqual(self.run_batch(False)['status'],'downloaded');send.assert_not_called()
        self.assertEqual(before,self.state.read_bytes())
    def test_repeat_apply_skips_uploaded_photo(self):
        with patch('photos.build_opener',return_value=Opener()),patch('photos.upload') as send:
            self.assertEqual(self.run_batch()['status'],'complete')
            self.assertEqual(self.run_batch()['products'][0]['photos'][0]['status'],'skipped_existing')
            self.assertEqual(send.call_count,1)
    def test_uncertain_upload_never_repeated(self):
        with patch('photos.build_opener',return_value=Opener()),patch('photos.upload',side_effect=TimeoutError) as send:
            self.assertEqual(self.run_batch()['status'],'stopped')
            with self.assertRaises(RuntimeError): self.run_batch()
            self.assertEqual(send.call_count,1)
    def test_product_mismatch_prevents_upload(self):
        self.api.request=lambda *args: {'article':'OTHER','productCategoryName':'wine'}
        with patch('photos.build_opener',return_value=Opener()),patch('photos.upload') as send:
            with self.assertRaises(ValueError): self.run_batch()
            send.assert_not_called()
    def test_multipart_matches_backend_contract(self):
        photo=download(URL,self.root,Opener())
        class Uploaded(Response): status=204
        opener=Opener()
        def accept(request,timeout): opener.requests.append(request);return Uploaded()
        opener.open=accept; self.api.opener=opener
        upload(self.api,'product-id',photo)
        request=opener.requests[0]
        self.assertEqual(request.full_url,'http://localhost:8080/products/files/upload?productId=product-id')
        self.assertEqual(request.get_header('Authorization'),'Bearer test-secret')
        self.assertIn(b'name="files"',request.data); self.assertIn(PNG,request.data)

if __name__=='__main__': unittest.main()
