-- Остатки по винотекам (вариант А). Демо-наполнение:
--   Москва — весь ассортимент, Пермь — вино и крепкое (сейчас работает только Пермь,
--   и без остатков крепкое не видно в каталоге).
-- Город товара выводится через винотеку, поэтому в самом товаре город не хранится.

insert into product_stock (id, product_id, wine_store_id, quantity)
select gen_random_uuid(), p.id, (select id from wine_store where city = 'moscow' order by id limit 1), 50
from product p;

insert into product_stock (id, product_id, wine_store_id, quantity)
select gen_random_uuid(), p.id, (select id from wine_store where city = 'perm' order by id limit 1), 30
from product p
where p.category_name in ('wine', 'spirit');
