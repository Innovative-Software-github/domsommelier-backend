create database domsommelier owner postgres;

insert into product_category VALUES ('food');
insert into product (id, article, name, description, country_id, category_id, quantity, price, initial_price, discount)
 VALUES (gen_random_uuid(), 'product1', 'product1', 'booga', 'russia', 'food', 100, 1000, 10000, 10);