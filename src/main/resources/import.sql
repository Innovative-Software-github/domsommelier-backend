insert into product_category (name, label) values ('WINE', 'Вино'), ('SNACK', 'Снэки');
insert into product_country values ('russia'), ('france'), ('usa');

insert into product (id, article, name, initial_price, price, category_name, country_name, created_at) values ('293b85d0-f739-4856-a09b-8495f2157e4d', 'product1', 'product1', 1000, 1500, 'WINE', 'france', date '2024-09-27' + time '14:00');
insert into product (id, article, name, initial_price, price, category_name, country_name, created_at) values ('ccfe12c7-2a16-4482-9c5b-ca57260fc475', 'product2', 'product2', 20000, 20100, 'WINE', 'usa', date '2024-09-25' + time '14:00');
insert into product (id, article, name, initial_price, price, category_name, country_name, created_at) values ('f3e364ff-b656-44a3-a16e-2263e560345c', 'product3', 'product3', 5000, 7500, 'WINE', 'russia', date '2024-08-27' + time '07:00');

insert into wine_color (name) values ('RED');
insert into wine_color (name) values ('WHITE');
insert into wine_color (name) values ('PINK');

insert into wine_type (name) values ('SWEET'), ('SEMISWEET'), ('DRY');

insert into wine (id, production_year, color, type) values ('293b85d0-f739-4856-a09b-8495f2157e4d', 1999, 'RED', 'DRY');
insert into wine (id, production_year, color, type) values ('ccfe12c7-2a16-4482-9c5b-ca57260fc475', 1999, 'WHITE', 'SEMISWEET');
insert into wine (id, production_year, color, type) values ('f3e364ff-b656-44a3-a16e-2263e560345c', 1999, 'PINK', 'SWEET');

insert into storage_history (id, product_id, amount, created_at) values ('84644d5b-777a-4f62-bc5b-d2e1e346df6c', '293b85d0-f739-4856-a09b-8495f2157e4d', 200, date '2024-09-27' + time '18:20');
insert into storage_history (id, product_id, amount, created_at) values ('b8049b3a-1e83-4110-b42f-9793c3ca1515', '293b85d0-f739-4856-a09b-8495f2157e4d', 400, date '2024-09-28' + time '14:10');
insert into storage_history (id, product_id, amount, created_at) values ('41bc7b82-e5e8-41f5-98a4-c288150aa061', 'f3e364ff-b656-44a3-a16e-2263e560345c', 150, date '2024-09-28' + time '14:10');
insert into storage_history (id, product_id, amount, created_at) values ('40c626ae-fc44-4e73-bf50-41eca2ddce8f', 'ccfe12c7-2a16-4482-9c5b-ca57260fc475', 9, date '2024-09-28' + time '14:10');
insert into storage_history (id, product_id, amount, created_at) values ('ebd26a40-a4a2-475c-b5e2-9b525e2ab35e', 'f3e364ff-b656-44a3-a16e-2263e560345c', -100, date '2024-09-28' + time '19:15');

insert into customer (id, iiko_id, first_name, second_name) values ('12c6f5bf-a1e2-4244-bea0-6700c7aee95f', 'djsiuiu348748347', 'alex', 'super');

insert into order_status values ('pending'), ('done');
insert into addresses (id, region, city) values ('3c383bd0-e867-40e1-a8ac-c75ee53f7544', 'permskiy kray', 'perm');

insert into orders (id, address_id, order_status, customer_id) values ('d542aea6-34e6-4085-b23f-de41d8ca410f', '3c383bd0-e867-40e1-a8ac-c75ee53f7544', 'done', '12c6f5bf-a1e2-4244-bea0-6700c7aee95f');
insert into orders (id, address_id, order_status, customer_id) values ('88f6f83f-bae6-436b-a891-a44deccc39e2', '3c383bd0-e867-40e1-a8ac-c75ee53f7544', 'done', '12c6f5bf-a1e2-4244-bea0-6700c7aee95f');
insert into orders (id, address_id, order_status, customer_id) values ('e41ad356-2796-4d61-bb2d-553f1d699cb8', '3c383bd0-e867-40e1-a8ac-c75ee53f7544', 'done', '12c6f5bf-a1e2-4244-bea0-6700c7aee95f');


insert into order_item (id, quantity, order_id, product_id) values ('b860d51d-504d-4c05-88e6-1dd45b16c433', 20, 'd542aea6-34e6-4085-b23f-de41d8ca410f', 'f3e364ff-b656-44a3-a16e-2263e560345c');
insert into order_item (id, quantity, order_id, product_id) values ('a36ed3c1-00b8-4f1f-a4ce-4e864b38c1df', 10, 'd542aea6-34e6-4085-b23f-de41d8ca410f', 'ccfe12c7-2a16-4482-9c5b-ca57260fc475');

insert into order_item (id, quantity, order_id, product_id) values ('01d0513c-8a5b-4551-8847-327f271a1827', 30, 'e41ad356-2796-4d61-bb2d-553f1d699cb8', 'ccfe12c7-2a16-4482-9c5b-ca57260fc475');
insert into order_item (id, quantity, order_id, product_id) values ('1f37d332-b9c5-4065-8a19-d69c1cb49058', 30, 'e41ad356-2796-4d61-bb2d-553f1d699cb8', '293b85d0-f739-4856-a09b-8495f2157e4d');
insert into order_item (id, quantity, order_id, product_id) values ('5a63bea2-7d4c-49f3-8b3e-c88ba5de7cfc', 30, 'e41ad356-2796-4d61-bb2d-553f1d699cb8', '293b85d0-f739-4856-a09b-8495f2157e4d');

insert into order_item (id, quantity, order_id, product_id) values ('ec1c25cb-a5eb-495a-970d-43740da55b28', 15, '88f6f83f-bae6-436b-a891-a44deccc39e2', 'ccfe12c7-2a16-4482-9c5b-ca57260fc475');
