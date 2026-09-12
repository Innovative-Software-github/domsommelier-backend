#!/usr/bin/env python3
"""fetch -> prepare -> import (dry-run by default). Python 3.9+, standard library only."""
import argparse
from contextlib import contextmanager
from datetime import datetime, timezone
import fcntl
import hashlib
import json
import os
from pathlib import Path
import socket
import sys
import time
from urllib.error import HTTPError, URLError
from urllib.parse import urlencode, urlsplit
from urllib.request import Request, build_opener, HTTPRedirectHandler
from core import VERSION, build_plan, collect_references, extract, source_identity, validate_payload

CATEGORIES = ('wine','champagne_and_sparkling','spirit','low_alcohol','snack','accessories')
MAX_BYTES = 8 * 1024 * 1024


def now(): return datetime.now(timezone.utc).isoformat()
def digest(data): return hashlib.sha256(json.dumps(data,sort_keys=True,ensure_ascii=False,allow_nan=False).encode()).hexdigest()
def read(path): return json.loads(Path(path).read_text(encoding='utf-8'))
def write(path, data):
    path=Path(path); path.parent.mkdir(parents=True,exist_ok=True)
    tmp=path.with_name(path.name+'.tmp')
    tmp.write_text(json.dumps(data,ensure_ascii=False,indent=2,allow_nan=False)+'\n',encoding='utf-8')
    os.replace(tmp,path)


class NoRedirect(HTTPRedirectHandler):
    def redirect_request(self, req, fp, code, msg, headers, newurl):
        raise HTTPError(req.full_url, code, 'Redirect rejected', headers, fp)


class ApiError(RuntimeError):
    def __init__(self,status):
        self.status=status
        super().__init__(f'API HTTP {status}')


class Api:
    def __init__(self, base, token):
        p=urlsplit(base)
        if p.username or p.password or p.query or p.fragment or not p.hostname or (p.scheme!='https' and not (p.scheme=='http' and p.hostname in {'localhost','127.0.0.1','::1'})):
            raise ValueError('API: HTTPS либо HTTP localhost; логин/пароль и query в URL запрещены')
        self.base=base.rstrip('/'); self.token=token; self.opener=build_opener(NoRedirect())
    def request(self, method, path, data=None):
        headers={'Accept':'application/json','Authorization':'Bearer '+self.token}
        body=None
        if data is not None:
            body=json.dumps(data,ensure_ascii=False,allow_nan=False).encode();headers['Content-Type']='application/json'
        req=Request(self.base+path,data=body,headers=headers,method=method)
        try:
            with self.opener.open(req,timeout=30) as resp:
                raw=resp.read(MAX_BYTES+1)
                if len(raw)>MAX_BYTES: raise RuntimeError('Ответ API слишком большой')
                return json.loads(raw) if raw else None
        except HTTPError as error: raise ApiError(error.code) from None
        except (URLError,TimeoutError,socket.timeout): raise RuntimeError('Сеть/таймаут API; результат записи может быть неизвестен') from None
    def find_article(self, article):
        matches=[]
        for category in CATEGORIES:
            page=0
            while True:
                response=self.request('GET','/api/v1/admin/products?'+urlencode({'category':category,'search':article,'size':100,'page':page}))
                for product in response['content']:
                    if product.get('article','').strip().casefold()==article.strip().casefold(): matches.append(product)
                if response.get('last',page+1 >= response.get('totalPages',1)): break
                page+=1
                if page>10000: raise RuntimeError('Слишком много страниц при проверке артикула')
        return matches


def load_batch(path):
    batch=read(path)
    if batch.get('schemaVersion')!=VERSION or not isinstance(batch.get('products'),list): raise ValueError('Неподдерживаемый формат batch')
    seen=set(); articles=set()
    for row in batch['products']:
        _, identity=source_identity(row['url'])
        if identity in seen: raise ValueError('Повтор источника в batch: '+identity)
        seen.add(identity)
        if row.get('article'):
            key=row['article'].strip().casefold()
            if key in articles: raise ValueError('Повтор нашего артикула в batch')
            articles.add(key)
    return batch


def fetch_batch(batch, folder, refresh=False, delay=2):
    folder=Path(folder);folder.mkdir(parents=True,exist_ok=True)
    report=[]; opener=build_opener(NoRedirect())
    for index,row in enumerate(batch['products']):
        url,identity=source_identity(row['url']);meta_path=folder/(identity+'.json');html_path=folder/(identity+'.html')
        if not refresh and meta_path.exists() and html_path.exists() and read(meta_path).get('status')=='downloaded':
            report.append({'externalId':identity,'status':'cached'});continue
        if index: time.sleep(max(0,delay))
        meta={'externalId':identity,'url':url,'retrievedAt':now(),'status':'failed'}
        try:
            req=Request(url,headers={'User-Agent':'DomsommelierCatalogImporter/1.0','Accept':'text/html'})
            with opener.open(req,timeout=30) as response:
                raw=response.read(MAX_BYTES+1)
                if len(raw)>MAX_BYTES: raise ValueError('Слишком большая страница')
                meta.update({'httpStatus':response.status,'contentType':response.headers.get('Content-Type','')})
                if 'text/html' not in meta['contentType']: raise ValueError('Ответ не HTML')
                html_path.write_bytes(raw);meta['status']='downloaded';meta['sha256']=hashlib.sha256(raw).hexdigest()
        except HTTPError as error:
            meta.update({'httpStatus':error.code,'status':'blocked' if error.code in {403,429} else 'failed'})
            # Keep the error response for diagnosis, never as product HTML.
            (folder/(identity+'.error.html')).write_bytes(error.read(MAX_BYTES))
        except (URLError,TimeoutError,socket.timeout,ValueError) as error:
            meta['error']=type(error).__name__
        write(meta_path,meta);report.append(meta)
    write(folder/'fetch-report.json',report)
    return report


def prepare(batch_path, dictionary_path, raw_dir, out, snapshots=False):
    batch=load_batch(batch_path);dictionary=read(dictionary_path);plans=[]
    for row in batch['products']:
        url,identity=source_identity(row['url'])
        try:
            meta_path=Path(raw_dir)/(identity+'.json');html_path=Path(raw_dir)/(identity+'.html')
            if meta_path.exists() and read(meta_path).get('status')=='downloaded' and html_path.exists():
                raw=html_path.read_bytes(); meta=read(meta_path)
                if hashlib.sha256(raw).hexdigest()!=meta['sha256']: raise ValueError('Изменился сохранённый HTML')
                item=extract(raw.decode('utf-8-sig'),url,row['category'],html=True)
                item['source'].update({'kind':'browser_dom' if meta.get('transport')=='browser' else 'downloaded_html','retrievedAt':meta['retrievedAt']})
            elif snapshots and row.get('snapshot'):
                path=(Path(batch_path).parent/row['snapshot']).resolve()
                item=extract(path.read_text(encoding='utf-8'),url,row['category'],html=False)
                item['source'].update({'kind':row.get('snapshotKind','manual_excerpt'),'retrievedAt':row.get('snapshotRetrievedAt')})
                item['issues'].append('Использована ручная фактическая выдержка, не свежий HTML; требуется сверка')
            else: raise ValueError('Нет успешно загруженного HTML; используйте fetch или явно --allow-snapshots')
            # Operator-supplied notes stay visible even if the page can be fetched later.
            item['issues'].extend(row.get('notes',[]))
            plan=build_plan(item,row,dictionary)
            plan['planHash']=digest({k:plan[k] for k in ['source','payload','reviewed','blockers']})
            plans.append(plan)
        except (ValueError,KeyError,OSError,TypeError) as error:
            plans.append({'source':{'url':url,'externalId':identity},'blockers':[str(error)],'status':'parse_failed'})
    result={'schemaVersion':VERSION,'preparedAt':now(),'products':plans}
    write(out,result)
    render_review(result,Path(out).with_suffix('.md'))
    return result


def render_review(plan, path):
    lines = ['# Пробная партия SimpleWine', '', 'Это план импорта. Товары ещё не записаны в каталог.', '']
    for item in plan['products']:
        source = item['source']; payload = item.get('payload', {})
        lines += ['## ' + payload.get('name', source['externalId']), '',
                  '[Источник](' + source['url'] + ') · артикул источника ' + source['externalId'],
                  '', 'Способ получения: `' + source.get('kind', 'не получено') + '`.', '',
                  'Наш артикул: **' + str(payload.get('article') or 'не задан') + '**. Начальная / розничная цена: **' + str(payload.get('initialPrice') or 'не задана') + ' / ' + str(payload.get('price') or 'не задана') + '**.', '']
        for issue in item.get('issues', []): lines.append('- ' + issue)
        lines += ['', 'Блокеры:'] + ['- ' + error for error in item.get('blockers', [])]
        if not item.get('blockers'): lines.append('- Нет; перед записью выполняется проверка API и дубликатов.')
        lines += ['', 'DTO:', '', '```json', json.dumps(payload, ensure_ascii=False, indent=2), '```', '']
    Path(path).write_text('\n'.join(lines), encoding='utf-8')


@contextmanager
def locked_state(path):
    path=Path(path);path.parent.mkdir(parents=True,exist_ok=True)
    with open(str(path)+'.lock','a') as lock:
        try: fcntl.flock(lock,fcntl.LOCK_EX|fcntl.LOCK_NB)
        except BlockingIOError: raise RuntimeError('Другой импорт уже использует этот state-файл') from None
        try:
            state=read(path) if path.exists() else {'version':1,'targets':{}}
            yield state, lambda: write(path,state)
        finally: fcntl.flock(lock,fcntl.LOCK_UN)


def validate_plan(plan):
    if plan.get('schemaVersion')!=VERSION: raise ValueError('Неподдерживаемая версия плана')
    seen=set();articles=set();errors=[]
    for item in plan.get('products',[]):
        identity=item['source']['externalId']
        if identity in seen: errors.append('Повтор источника '+identity)
        seen.add(identity)
        errors.extend(f'{identity}: {x}' for x in item.get('blockers',[]))
        if 'payload' not in item: errors.append(identity+': нет payload');continue
        expected=digest({k:item[k] for k in ['source','payload','reviewed','blockers']})
        if item.get('planHash')!=expected: errors.append(identity+': план изменён; выполните prepare заново')
        if item.get('reviewed') is not True: errors.append(identity+': не проверен')
        errors.extend(f'{identity}: {x}' for x in validate_payload(item['payload']))
        article=str(item['payload'].get('article') or '').strip().casefold()
        if article and article in articles: errors.append('Повтор артикула '+article)
        articles.add(article)
    return sorted(set(errors))


def reconcile_references(api, items, apply):
    refs={}
    for item in items:
        for ref in collect_references(item['payload']):
            key=(ref['kind'],ref['code'])
            if key in refs and refs[key]!=ref: raise ValueError('Разные подписи одного кода в партии')
            refs[key]=ref
    missing=[]; substitutions={}
    for kind in sorted({k for k,c in refs}):
        rows=api.request('GET','/api/v1/admin/products/attribute-references/'+kind)
        for (k,code),ref in refs.items():
            if k!=kind: continue
            by_code=[r for r in rows if r['code']==code]
            by_label=[r for r in rows if r['label'].strip().casefold()==ref['label'].strip().casefold()]
            if by_code and by_code[0]['label'].strip().casefold()!=ref['label'].strip().casefold(): raise ValueError('Код справочника занят другим названием: '+kind+':'+code)
            existing=(by_code or by_label)
            if existing: substitutions[(kind,code)]=existing[0]
            else: missing.append(ref)
    if apply:
        for ref in missing:
            path='/api/v1/admin/products/attribute-references/'+ref['kind']
            value={k:ref[k] for k in ['code','label']}
            try: created=api.request('POST',path,value)
            except ApiError as error:
                if error.status!=409: raise
                rows=api.request('GET',path)
                matches=[r for r in rows if r['label'].strip().casefold()==ref['label'].strip().casefold()]
                if len(matches)!=1: raise RuntimeError('Конфликт справочника после параллельного создания') from None
                created=matches[0]
            substitutions[(ref['kind'],ref['code'])]=created
    return substitutions,missing


def substitute(payload, mapping):
    from core import REF_FIELDS
    def walk(value,key=''):
        if isinstance(value,dict):
            if 'code' in value: return dict(mapping.get((REF_FIELDS[key],value['code']),value))
            return {k:walk(v,k) for k,v in value.items()}
        if isinstance(value,list): return [walk(v,key) for v in value]
        return value
    return walk(payload)


def check_legacy(api,payload):
    ref=api.request('GET','/api/v1/admin/products/'+payload['category']+'/reference')
    for field,list_key in [('country','countries'),('color','colors'),('type','types'),('subcategory','subcategories'),('sugarContent','sugarContents')]:
        if payload.get(field) and payload[field] not in (ref.get(list_key) or []):
            raise ValueError('Нет базового справочного значения '+field+': '+str(payload[field])+'; исправьте overrides по справочнику сервера')


def run_import(plan,api,state_path,apply=False):
    errors=validate_plan(plan)
    if errors: return {'mode':'apply' if apply else 'dry-run','status':'blocked','errors':errors,'products':[]}
    items=plan['products']; report=[]
    with locked_state(state_path) as (state,save):
        target=state['targets'].setdefault(api.base,{})
        actions=[]
        # Preflight all products BEFORE any write to the server.
        for item in items:
            source=item['source']['externalId'];payload=item['payload'];old=target.get(source)
            check_legacy(api,payload)
            matches=api.find_article(payload['article'])
            if old and old.get('article')!=payload['article']: raise ValueError('Артикул источника изменён; автоматическое создание запрещено')
            if old and old['status']=='created':
                detail=api.request('GET','/api/v1/products?'+urlencode({'id':old['productId']}))
                if detail['article']!=payload['article'] or detail['productCategoryName']!=payload['category']: raise ValueError('Сохранённый ID больше не соответствует источнику')
                report.append({'externalId':source,'status':'skipped_existing','productId':old['productId'],'changedPlan':old.get('planHash')!=item['planHash']});continue
            if old and old['status']=='pending':
                # After an uncertain POST NEVER send a second POST automatically, even if search is empty.
                raise RuntimeError('Неопределённый результат прошлой записи '+source+'; найдите товар по артикулу и восстановите productId в state после проверки')
            if matches: raise ValueError('Артикул уже существует на сервере: '+payload['article']+'; импорт не создаёт копию и не перезаписывает товар')
            actions.append(item)
        mapping,missing=reconcile_references(api,actions,apply=False)
        if not apply:
            report += [{'externalId':i['source']['externalId'],'status':'would_create','payload':substitute(i['payload'],mapping)} for i in actions]
            return {'mode':'dry-run','status':'ready','referencesToCreate':missing,'products':report}
        mapping,_=reconcile_references(api,actions,apply=True)
        for item in actions:
            source=item['source']['externalId'];payload=substitute(item['payload'],mapping)
            target[source]={'status':'pending','article':payload['article'],'planHash':item['planHash'],'attemptedAt':now()};save()
            try:
                created=api.request('POST','/api/v1/admin/products',payload)
                if not created or not created.get('id'): raise RuntimeError('Создание вернуло ответ без ID')
                target[source].update({'status':'created','productId':created['id']});save()
                report.append({'externalId':source,'status':'created','productId':created['id']})
            except Exception as error:
                # Preserve pending for failures: a 5xx can occur after the create transaction commits.
                report.append({'externalId':source,'status':'uncertain','error':str(error)})
                return {'mode':'apply','status':'stopped','products':report}
        return {'mode':'apply','status':'complete','products':report}


def main():
    parser=argparse.ArgumentParser(description=__doc__);sub=parser.add_subparsers(dest='command',required=True)
    fetch=sub.add_parser('fetch');fetch.add_argument('--input',required=True);fetch.add_argument('--raw-dir',default='work/raw');fetch.add_argument('--refresh',action='store_true');fetch.add_argument('--delay',type=float,default=2)
    fetch.add_argument('--browser',action='store_true',help='Открывать страницы в установленном Google Chrome');fetch.add_argument('--limit',type=int)
    prep=sub.add_parser('prepare');prep.add_argument('--input',required=True);prep.add_argument('--dictionary',required=True);prep.add_argument('--raw-dir',default='work/raw');prep.add_argument('--out',default='work/plan.json');prep.add_argument('--allow-snapshots',action='store_true')
    push=sub.add_parser('import');push.add_argument('--plan',default='work/plan.json');push.add_argument('--api',default='http://localhost:8080');push.add_argument('--state',default='work/import-state.json');push.add_argument('--report',default='work/import-report.json')
    mode=push.add_mutually_exclusive_group();mode.add_argument('--apply',action='store_true');mode.add_argument('--dry-run',action='store_true')
    photos=sub.add_parser('photos',help='Скачать фотографии; --apply загружает их к импортированным товарам')
    photos.add_argument('--input',required=True);photos.add_argument('--raw-dir',default='work/raw');photos.add_argument('--photo-dir',default='work/photos')
    photos.add_argument('--api',default='http://localhost:8080');photos.add_argument('--state',default='work/import-state.json');photos.add_argument('--report',default='work/photo-report.json')
    photos.add_argument('--apply',action='store_true');photos.add_argument('--delay',type=float,default=2)
    photos.add_argument('--browser',action='store_true',help='Скачивать фотографии через видимый Google Chrome')
    args=parser.parse_args()
    try:
        if args.command=='photos':
            from photos import run_photos
            token=os.environ.get('ADMIN_TOKEN')
            api=Api(args.api,token) if args.apply and token else None
            report=run_photos(load_batch(args.input),args.raw_dir,args.photo_dir,args.state,api,args.apply,args.delay,args.browser)
            write(args.report,report); print(f'{report["status"]}: {args.report}')
            return 0 if report['status'] in {'downloaded','complete'} else 2
        if args.command=='fetch':
            batch=load_batch(args.input)
            if args.limit is not None and args.limit < 1: raise ValueError('--limit должен быть положительным')
            if args.browser:
                from browser_fetch import fetch_browser
                report=fetch_browser(batch,args.raw_dir,args.refresh,args.delay,args.limit)
            else:
                if args.limit: batch=dict(batch,products=batch['products'][:args.limit])
                report=fetch_batch(batch,args.raw_dir,args.refresh,args.delay)
            print(json.dumps([{'externalId':r['externalId'],'status':r['status']} for r in report],ensure_ascii=False,indent=2))
            return 0 if all(r['status'] in {'downloaded','cached'} for r in report) else 2
        if args.command=='prepare':
            result=prepare(args.input,args.dictionary,args.raw_dir,args.out,args.allow_snapshots)
            print(f'План: {args.out}; карточек: {len(result["products"])}; с блокерами: {sum(bool(p.get("blockers")) for p in result["products"])}')
            return 0
        plan=read(args.plan);errors=validate_plan(plan)
        if errors:
            report={'mode':'apply' if args.apply else 'dry-run','status':'blocked','errors':errors,'products':[]}
        elif not os.environ.get('ADMIN_TOKEN'):
            if args.apply: raise ValueError('Нужен ADMIN_TOKEN существующей админской сессии')
            report={'mode':'dry-run','status':'offline_valid','note':'API не проверен: ADMIN_TOKEN не задан','products':[{'externalId':i['source']['externalId'],'payload':i['payload']} for i in plan['products']]}
        else: report=run_import(plan,Api(args.api,os.environ['ADMIN_TOKEN']),args.state,args.apply)
        write(args.report,report); print(f'{report["status"]}: {args.report}')
        return 0 if report['status'] in {'ready','complete','offline_valid'} else 2
    except (ValueError,RuntimeError,OSError,KeyError,TypeError) as error:
        report={'status':'failed','error':str(error)}
        if args.command in {'import','photos'}: write(args.report,report)
        print('Ошибка: '+str(error),file=sys.stderr);return 1

if __name__=='__main__': sys.exit(main())
