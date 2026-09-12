import copy
import importlib.util
import json
from pathlib import Path
import sys
import tempfile
import unittest

ROOT=Path(__file__).resolve().parents[1]
sys.path.insert(0,str(ROOT))
from core import extract, build_plan, source_identity
from importer import prepare, validate_plan, run_import, ApiError, digest, locked_state

class FakeApi:
    base='http://localhost:8080'
    def __init__(self): self.posts=[]; self.products={}; self.refs={}; self.lose_response=False
    def find_article(self, article): return [p for p in self.products.values() if p['article']==article]
    def request(self,method,path,data=None):
        if '/attribute-references/' in path:
            kind=path.rsplit('/',1)[1]
            if method=='GET': return self.refs.get(kind,[])
            self.posts.append((path,copy.deepcopy(data)))
            self.refs.setdefault(kind,[]).append(data);return data
        if path.endswith('/reference'):
            return {'countries':['Франция','Италия','Новая Зеландия'],'colors':['Белое'],'types':['Полусухое'],'sugarContents':['Брют'],'subcategories':['Виски','Коньяк','Просекко']}
        if method=='POST':
            self.posts.append((path,copy.deepcopy(data)))
            result={'id':str(len(self.products)+1),'article':data['article'],'productCategoryName':data['category']}
            self.products[result['id']]=result
            if self.lose_response: raise RuntimeError('timeout after commit')
            return result
        if path.startswith('/api/v1/products?id='): return self.products[path.split('=')[1]]
        raise AssertionError((method,path))

class ImportTests(unittest.TestCase):
    def setUp(self):
        self.tmp=tempfile.TemporaryDirectory();self.dir=Path(self.tmp.name)
        self.plan=prepare(ROOT/'examples/batch.json',ROOT/'examples/references.json',self.dir/'raw',self.dir/'plan.json',True)
    def tearDown(self): self.tmp.cleanup()
    def approved(self,index=0):
        plan=copy.deepcopy(self.plan);plan['products']=[plan['products'][index]]
        row=plan['products'][0]
        row['payload'].update(article='TEST-ONLY-1',initialPrice=1000,price=1500)
        row['reviewed']=True;row['blockers']=[]
        row['planHash']=digest({k:row[k] for k in ['source','payload','reviewed','blockers']})
        return plan
    def test_four_snapshots_map_categories_and_preserve_unknowns(self):
        wine,sparkling,whisky,cognac=[p['payload'] for p in self.plan['products']]
        self.assertEqual(wine['extendedDetails']['grapeComposition'][0]['percent'],100)
        self.assertIsNone(sparkling['extendedDetails']['productionYear'])
        self.assertEqual(sparkling['extendedDetails']['vintageStatus'],'unknown')
        self.assertEqual(sparkling['extendedDetails']['aging']['status'],'not_aged')
        self.assertIsNone(whisky['extendedDetails']['whiskyDetails']['ageStatementYears'])
        self.assertIsNone(cognac['extendedDetails']['cognacDetails']['ageStatementYears'])
        self.assertEqual(wine['extendedDetails']['sensoryProfile']['body']['scaleMin'],1)
        self.assertEqual(wine['extendedDetails']['sensoryProfile']['body']['scaleMax'],5)
        self.assertNotIn('sourcePrice',wine)
    def test_missing_prices_and_review_block_entire_batch_before_api_calls(self):
        api=FakeApi();report=run_import(self.plan,api,self.dir/'state.json',True)
        self.assertEqual(report['status'],'blocked');self.assertEqual(api.posts,[])
    def test_dry_run_makes_no_posts(self):
        api=FakeApi();report=run_import(self.approved(),api,self.dir/'state.json')
        self.assertEqual(report['status'],'ready');self.assertTrue(report['referencesToCreate'])
        self.assertEqual(api.posts,[]);self.assertFalse((self.dir/'state.json').exists())
    def test_second_apply_skips_product_and_reference_posts(self):
        api=FakeApi();plan=self.approved();state=self.dir/'state.json'
        self.assertEqual(run_import(plan,api,state,True)['status'],'complete')
        count=len(api.posts)
        report=run_import(plan,api,state,True)
        self.assertEqual(len(api.posts),count);self.assertEqual(report['products'][0]['status'],'skipped_existing')
    def test_lost_post_response_never_causes_automatic_second_creation(self):
        api=FakeApi();api.lose_response=True;state=self.dir/'state.json';plan=self.approved()
        self.assertEqual(run_import(plan,api,state,True)['status'],'stopped')
        count=len(api.posts)
        with self.assertRaisesRegex(RuntimeError,'Неопределённый'):run_import(plan,api,state,True)
        self.assertEqual(len(api.posts),count);self.assertEqual(len(api.products),1)
    def test_same_article_without_journal_is_not_overwritten_or_duplicated(self):
        api=FakeApi();api.products['existing']={'id':'existing','article':'TEST-ONLY-1'}
        with self.assertRaisesRegex(ValueError,'Артикул уже'):run_import(self.approved(),api,self.dir/'state.json',True)
        self.assertEqual(api.posts,[])
    def test_existing_dictionary_label_with_different_code_is_reused(self):
        api=FakeApi();api.refs['region']=[{'code':'existing_marlborough','label':'Мальборо'}]
        run_import(self.approved(),api,self.dir/'state.json',True)
        product=[data for path,data in api.posts if path=='/api/v1/admin/products'][0]
        self.assertEqual(product['extendedDetails']['region']['code'],'existing_marlborough')
        self.assertFalse(any(path.endswith('/region') for path,data in api.posts))
    def test_conflicting_dictionary_code_blocks_before_writes(self):
        api=FakeApi();api.refs['region']=[{'code':'marlborough','label':'Другой регион'}]
        with self.assertRaisesRegex(ValueError,'занят'):run_import(self.approved(),api,self.dir/'state.json',True)
        self.assertEqual(api.posts,[])
    def test_changed_plan_and_duplicate_source_are_rejected(self):
        plan=self.approved();plan['products'][0]['payload']['price']=123
        self.assertTrue(any('план изменён' in e for e in validate_plan(plan)))
        plan=self.approved();plan['products'].append(copy.deepcopy(plan['products'][0]))
        self.assertTrue(any('Повтор источника' in e for e in validate_plan(plan)))
    def test_local_lock_excludes_parallel_import(self):
        with locked_state(self.dir/'state.json'):
            with self.assertRaisesRegex(RuntimeError,'Другой импорт'):
                with locked_state(self.dir/'state.json'): pass
    def test_html_block_layout_and_article_validation(self):
        url='https://simplewine.ru/catalog/product/test_160429/'
        html='<nav>Вино: красное</nav><h1>Вино Test</h1><p>Артикул: 160429</p><dl><dt>Крепость</dt><dd>12,5%</dd><dt>Объем</dt><dd>0.75 л</dd><dt>Вино</dt><dd>белое</dd></dl><script>Крепость:99</script>'
        row=extract(html,url,'wine')
        self.assertEqual(row['facts']['strength'],12.5);self.assertEqual(row['facts']['color'],'белое')
        with self.assertRaisesRegex(ValueError,'Артикул'):extract(html.replace('160429','999'),url,'wine')
        with self.assertRaises(ValueError):extract('<h1>Access denied</h1>',url,'wine')
    def test_age_range_is_not_turned_into_exact_age(self):
        text = '# Виски Test\nАртикул: 123\nСрок выдержки: свыше 3 лет\nКрепость: 40%\nОбъем: 0.7 л'
        row=extract(text,'https://simplewine.ru/catalog/product/test_123/','spirit',False)
        self.assertIsNone(row['facts']['age']);self.assertTrue(row['issues'])
    def test_urls_do_not_accept_other_domains_or_review_pages(self):
        for url in ['http://simplewine.ru/catalog/product/test_1/','https://evil.test/catalog/product/test_1/','https://simplewine.ru/catalog/product/test_1/reviews/','https://user:pass@simplewine.ru/catalog/product/test_1/']:
            with self.assertRaises(ValueError):source_identity(url)
    def test_empty_download_cannot_silently_become_snapshot(self):
        plan=prepare(ROOT/'examples/batch.json',ROOT/'examples/references.json',self.dir/'raw',self.dir/'missing.json',False)
        self.assertTrue(all(p.get('status')=='parse_failed' for p in plan['products']))
    def test_existing_id_missing_on_server_does_not_recreate(self):
        api=FakeApi();plan=self.approved();state=self.dir/'state.json'
        run_import(plan,api,state,True);api.products.clear();count=len(api.posts)
        with self.assertRaises(KeyError):run_import(plan,api,state,True)
        self.assertEqual(len(api.posts),count)

if __name__=='__main__':unittest.main()
