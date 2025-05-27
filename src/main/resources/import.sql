insert into product_category (name, label) values ('WINE', 'Вино'), ('SNACK', 'Снэки'), ('SPIRIT', 'Крепкое'), ('CHAMPAGNE_AND_SPARKLING', 'Шампанское и игристое'), ('LOW_ALCOHOL', 'Слабоалкогольные напитки'), ('ACCESSORIES', 'Аксессуары');
insert into product_country values ('russia'), ('france'), ('usa');
insert into wine_color (name) values ('RED'), ('WHITE'), ('PINK');
insert into wine_type (name) values ('SWEET'), ('SEMISWEET'), ('DRY');

insert into product (id, article, name, initial_price, price, category_name, country_name, created_at) values ('293b85d0-f739-4856-a09b-8495f2157e4d', 'product1', 'product1', 1000, 1500, 'WINE', 'france', date '2024-09-27' + time '14:00');
insert into product (id, article, name, initial_price, price, category_name, country_name, created_at) values ('ccfe12c7-2a16-4482-9c5b-ca57260fc475', 'product2', 'product2', 20000, 20100, 'WINE', 'usa', date '2024-09-25' + time '14:00');
insert into product (id, article, name, initial_price, price, category_name, country_name, created_at) values ('f3e364ff-b656-44a3-a16e-2263e560345c', 'product3', 'product3', 5000, 7500, 'WINE', 'russia', date '2024-08-27' + time '07:00');

-- WINE
-- Наличие в магазинах (checkbox)
insert into filter (id, name, product_category, type) values ('e6064621-31a5-41e7-becd-61b98f0e2ffd', 'Наличие в магазинах', 'WINE', 'CHECKBOX');
insert into checkbox_filter (id) values ('e6064621-31a5-41e7-becd-61b98f0e2ffd');

-- Цвет (multi_select)
insert into filter (id, name, product_category, type) values ('2d2bae7f-2924-4d1e-b0ad-94c1eb979921', 'Цвет', 'WINE', 'MULTISELECT');
insert into multi_select_filter (id) values ('2d2bae7f-2924-4d1e-b0ad-94c1eb979921');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('2d2bae7f-2924-4d1e-b0ad-94c1eb979921', 'WHITE', 'Белое');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('2d2bae7f-2924-4d1e-b0ad-94c1eb979921', 'RED', 'Красное');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('2d2bae7f-2924-4d1e-b0ad-94c1eb979921', 'PINK', 'Розовое');

-- География (multi_select)
insert into filter (id, name, product_category, type) values ('b210deae-e49e-4889-967b-8d2b7934f853', 'География', 'WINE', 'MULTISELECT');
insert into multi_select_filter (id) values ('b210deae-e49e-4889-967b-8d2b7934f853');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b210deae-e49e-4889-967b-8d2b7934f853', 'ITALY', 'Италия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b210deae-e49e-4889-967b-8d2b7934f853', 'FRANCE', 'Фрaнция');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b210deae-e49e-4889-967b-8d2b7934f853', 'RUSSIA', 'Россия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b210deae-e49e-4889-967b-8d2b7934f853', 'SPAIN', 'Испания');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b210deae-e49e-4889-967b-8d2b7934f853', 'CHILE', 'Чили');

-- Сорт винограда (multi_select)
insert into filter (id, name, product_category, type) values ('818fda71-375b-4ef8-9c47-1ef38e92bd68', 'Сорт винограда', 'WINE', 'MULTISELECT');
insert into multi_select_filter (id) values ('818fda71-375b-4ef8-9c47-1ef38e92bd68');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('818fda71-375b-4ef8-9c47-1ef38e92bd68', 'tempranillo', 'Темпранильо');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('818fda71-375b-4ef8-9c47-1ef38e92bd68', 'merlot', 'Мерло');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('818fda71-375b-4ef8-9c47-1ef38e92bd68', 'sauvignon_blanc', 'Совиньон Блан');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('818fda71-375b-4ef8-9c47-1ef38e92bd68', 'chardonnay', 'Шардоне');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('818fda71-375b-4ef8-9c47-1ef38e92bd68', 'caberne_sauvignon', 'Каберне  Совиньон');

-- Содержание сахара (multi_select)
insert into filter (id, name, product_category, type) values ('abf59578-f3d8-46e6-99d0-4964ce34ab29', 'Содержание сахара', 'WINE', 'MULTISELECT');
insert into multi_select_filter (id) values ('abf59578-f3d8-46e6-99d0-4964ce34ab29');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('abf59578-f3d8-46e6-99d0-4964ce34ab29', 'DRY', 'Сухое');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('abf59578-f3d8-46e6-99d0-4964ce34ab29', 'SEMIDRY', 'Полусухое');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('abf59578-f3d8-46e6-99d0-4964ce34ab29', 'SEMISWEET', 'Полусладкое');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('abf59578-f3d8-46e6-99d0-4964ce34ab29', 'SWEET', 'Сладкое');

-- Производитель (multi_select)
insert into filter (id, name, product_category, type) values ('e1913a59-549a-4cfd-830b-153b7ebf2f55', 'Производитель', 'WINE', 'MULTISELECT');
insert into multi_select_filter (id) values ('e1913a59-549a-4cfd-830b-153b7ebf2f55');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e1913a59-549a-4cfd-830b-153b7ebf2f55', 'frescobaldi', 'Фрескобальди');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e1913a59-549a-4cfd-830b-153b7ebf2f55', 'joseph_drouhin', 'Жозеф Друэн');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e1913a59-549a-4cfd-830b-153b7ebf2f55', 'guigal', 'Гигаль');

-- Объем (multi_select)
insert into filter (id, name, product_category, type) values ('bdf0c025-205e-41c5-93ec-1274de945388', 'Объем', 'WINE', 'MULTISELECT');
insert into multi_select_filter (id) values ('bdf0c025-205e-41c5-93ec-1274de945388');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('bdf0c025-205e-41c5-93ec-1274de945388', '0.75', '0.75 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('bdf0c025-205e-41c5-93ec-1274de945388', '1', '1 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('bdf0c025-205e-41c5-93ec-1274de945388', 'other', 'Другое');

-- Особенности (multi_select)
insert into filter (id, name, product_category, type) values ('02ddd7cf-49f2-45d2-a2e5-a0d3d115af02', 'Особенности', 'WINE', 'MULTISELECT');
insert into multi_select_filter (id) values ('02ddd7cf-49f2-45d2-a2e5-a0d3d115af02');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('02ddd7cf-49f2-45d2-a2e5-a0d3d115af02', 'gift_wrapping', 'Подарочный набор');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('02ddd7cf-49f2-45d2-a2e5-a0d3d115af02', 'сollection', 'Коллекционное');

-- Цена (range)
insert into filter (id, name, product_category, type) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 'Цена', 'WINE', 'RANGE');
insert into range_filter (id, min, max, unit) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 20000, 999999, 'от 20000₽');

-- SPIRIT
-- Наличие в магазинах (checkbox)
insert into filter (id, name, product_category, type) values ('c7c6b6ca-2635-4c9f-9b56-4937e52bb7f4', 'Наличие в магазинах', 'SPIRIT', 'CHECKBOX');
insert into checkbox_filter (id) values ('c7c6b6ca-2635-4c9f-9b56-4937e52bb7f4');

-- География (multi_select)
insert into filter (id, name, product_category, type) values ('d879d38d-e7ce-4b30-bc88-c1e9d1aab413', 'География', 'SPIRIT', 'MULTISELECT');
insert into multi_select_filter (id) values ('d879d38d-e7ce-4b30-bc88-c1e9d1aab413');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('d879d38d-e7ce-4b30-bc88-c1e9d1aab413', 'ITALY', 'Италия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('d879d38d-e7ce-4b30-bc88-c1e9d1aab413', 'RUSSIA', 'Россия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('d879d38d-e7ce-4b30-bc88-c1e9d1aab413', 'FRANCE', 'Франция');

-- Производитель (multi_select)
insert into filter (id, name, product_category, type) values ('e8ea11c1-412e-444d-ac9f-8bac813fcdfa', 'Производитель', 'SPIRIT', 'MULTISELECT');
insert into multi_select_filter (id) values ('e8ea11c1-412e-444d-ac9f-8bac813fcdfa');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e8ea11c1-412e-444d-ac9f-8bac813fcdfa', 'pulltex', 'Pulltex');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e8ea11c1-412e-444d-ac9f-8bac813fcdfa', 'prestigio', 'Prestigio');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e8ea11c1-412e-444d-ac9f-8bac813fcdfa', 'onegin', 'Онегин');

-- Особенности (multi_select)
insert into filter (id, name, product_category, type) values ('09f3ad68-6bfd-4dbc-b809-8ba91e4ba023', 'Особенности', 'SPIRIT', 'MULTISELECT');
insert into multi_select_filter (id) values ('09f3ad68-6bfd-4dbc-b809-8ba91e4ba023');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('09f3ad68-6bfd-4dbc-b809-8ba91e4ba023', 'gift_wrapping', 'Подарочный набор');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('09f3ad68-6bfd-4dbc-b809-8ba91e4ba023', 'сollection', 'Коллекционное');

-- Цена (range)
insert into filter (id, name, product_category, type) values ('d6276f43-7283-4d1f-8924-539171565247', 'Цена', 'SPIRIT', 'RANGE');
insert into range_filter (id, min, max, unit) values ('d6276f43-7283-4d1f-8924-539171565247', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 20000, 999999, 'от 20000₽');

-- ACCESSORIES
-- Наличие в магазинах (checkbox)
insert into filter (id, name, product_category, type) values ('dd6fb4fc-7c43-4fb3-8bf0-011e9ff6494c', 'Наличие в магазинах', 'ACCESSORIES', 'CHECKBOX');
insert into checkbox_filter (id) values ('dd6fb4fc-7c43-4fb3-8bf0-011e9ff6494c');

-- География (multi_select)
insert into filter (id, name, product_category, type) values ('caa1eb21-4b02-4a4f-81f8-7e2fb0723db4', 'География', 'ACCESSORIES', 'MULTISELECT');
insert into multi_select_filter (id) values ('caa1eb21-4b02-4a4f-81f8-7e2fb0723db4');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('caa1eb21-4b02-4a4f-81f8-7e2fb0723db4', 'ITALY', 'Италия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('caa1eb21-4b02-4a4f-81f8-7e2fb0723db4', 'RUSSIA', 'Россия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('caa1eb21-4b02-4a4f-81f8-7e2fb0723db4', 'FRANCE', 'Франция');

-- Производитель (multi_select)
insert into filter (id, name, product_category, type) values ('e4e9d3b2-5a91-4ecd-8c31-4e7281e4b2ce', 'Производитель', 'ACCESSORIES', 'MULTISELECT');
insert into multi_select_filter (id) values ('e4e9d3b2-5a91-4ecd-8c31-4e7281e4b2ce');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e4e9d3b2-5a91-4ecd-8c31-4e7281e4b2ce', 'pulltex', 'Pulltex');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e4e9d3b2-5a91-4ecd-8c31-4e7281e4b2ce', 'prestigio', 'Prestigio');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e4e9d3b2-5a91-4ecd-8c31-4e7281e4b2ce', 'onegin', 'Онегин');

-- Особенности (multi_select)
insert into filter (id, name, product_category, type) values ('eedbce23-1eb0-4c6f-a4e6-4b2e5ea8e2a7', 'Особенности', 'ACCESSORIES', 'MULTISELECT');
insert into multi_select_filter (id) values ('eedbce23-1eb0-4c6f-a4e6-4b2e5ea8e2a7');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('eedbce23-1eb0-4c6f-a4e6-4b2e5ea8e2a7', 'gift_wrapping', 'Подарочный набор');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('eedbce23-1eb0-4c6f-a4e6-4b2e5ea8e2a7', 'сollection', 'Коллекционное');

-- Цена (range)
insert into filter (id, name, product_category, type) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 'Цена', 'ACCESSORIES', 'RANGE');
insert into range_filter (id, min, max, unit) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 20000, 999999, 'от 20000₽');

-- SNACKS
-- Наличие в магазинах (checkbox)
insert into filter (id, name, product_category, type) values ('05e25457-693b-4a92-8e52-acb2c034e4b0', 'Наличие в магазинах', 'SNACK', 'CHECKBOX');
insert into checkbox_filter (id) values ('05e25457-693b-4a92-8e52-acb2c034e4b0');

-- Категория (multi_select)
insert into filter (id, name, product_category, type) values ('0ebd7e0f-cb8d-4a2e-83af-3d6d6e652478', 'Категория', 'SNACK', 'MULTISELECT');
insert into multi_select_filter (id) values ('0ebd7e0f-cb8d-4a2e-83af-3d6d6e652478');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0ebd7e0f-cb8d-4a2e-83af-3d6d6e652478', 'сheese', 'Сыр');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0ebd7e0f-cb8d-4a2e-83af-3d6d6e652478', 'hamon', 'Хамон');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0ebd7e0f-cb8d-4a2e-83af-3d6d6e652478', 'bresaola', 'Брезаола');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0ebd7e0f-cb8d-4a2e-83af-3d6d6e652478', 'assorted', 'Ассорти');

-- Подбор к напиткам (multi_select)
insert into filter (id, name, product_category, type) values ('c55d7baa-15fc-4930-acee-32234cefcdc9', 'Подбор к напиткам', 'SNACK', 'MULTISELECT');
insert into multi_select_filter (id) values ('c55d7baa-15fc-4930-acee-32234cefcdc9');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c55d7baa-15fc-4930-acee-32234cefcdc9', 'smart_selection_red_wine', 'для Красного вина');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c55d7baa-15fc-4930-acee-32234cefcdc9', 'smart_selection_white_wine', 'для Белого вина');

-- География (multi_select)
insert into filter (id, name, product_category, type) values ('ef50bee7-0608-4150-b3fc-2716932b4f1b', 'География', 'SNACK', 'MULTISELECT');
insert into multi_select_filter (id) values ('ef50bee7-0608-4150-b3fc-2716932b4f1b');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('ef50bee7-0608-4150-b3fc-2716932b4f1b', 'FRANCE', 'Франция');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('ef50bee7-0608-4150-b3fc-2716932b4f1b', 'ITALY', 'Италия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('ef50bee7-0608-4150-b3fc-2716932b4f1b', 'SPAIN', 'Испания');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('ef50bee7-0608-4150-b3fc-2716932b4f1b', 'SWITZERLAND', 'Швейцария');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('ef50bee7-0608-4150-b3fc-2716932b4f1b', 'RUSSIA', 'Россия');

-- Производитель (multi_select)
insert into filter (id, name, product_category, type) values ('6acab7b2-870a-442c-804e-963a39c5a860', 'Производитель', 'SNACK', 'MULTISELECT');
insert into multi_select_filter (id) values ('6acab7b2-870a-442c-804e-963a39c5a860');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('6acab7b2-870a-442c-804e-963a39c5a860', 'marguareis', 'Marguareis');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('6acab7b2-870a-442c-804e-963a39c5a860', 'casademont', 'Казадемонт');

-- Особенности (multi_select)
insert into filter (id, name, product_category, type) values ('b6bd99c3-ca75-4864-ab11-38f7ad0eef09', 'Особенности', 'SNACK', 'MULTISELECT');
insert into multi_select_filter (id) values ('b6bd99c3-ca75-4864-ab11-38f7ad0eef09');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b6bd99c3-ca75-4864-ab11-38f7ad0eef09', 'gift_wrapping', 'Подарочный набор');

-- Цена (range)
insert into filter (id, name, product_category, type) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 'Цена', 'SNACK', 'RANGE');
insert into range_filter (id, min, max, unit) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 20000, 999999, 'от 20000₽');

-- LOW_ALCOHOL
-- Наличие в магазинах (checkbox)
insert into filter (id, name, product_category, type) values ('aad7c339-d8f0-4d72-a9a7-361f5a04a300', 'Наличие в магазинах', 'LOW_ALCOHOL', 'CHECKBOX');
insert into checkbox_filter (id) values ('aad7c339-d8f0-4d72-a9a7-361f5a04a300');

-- Категория (multi_select)
insert into filter (id, name, product_category, type) values ('3a82f3e6-6a98-44a9-b687-88b0b354e11b', 'Категория', 'LOW_ALCOHOL', 'MULTISELECT');
insert into multi_select_filter (id) values ('3a82f3e6-6a98-44a9-b687-88b0b354e11b');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('3a82f3e6-6a98-44a9-b687-88b0b354e11b', 'vermouth', 'Вермут');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('3a82f3e6-6a98-44a9-b687-88b0b354e11b', 'aperitifs', 'Аперитив');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('3a82f3e6-6a98-44a9-b687-88b0b354e11b', 'tinctures', 'Настойки');

-- География (multi_select)
insert into filter (id, name, product_category, type) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'География', 'LOW_ALCOHOL', 'MULTISELECT');
insert into multi_select_filter (id) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'FRANCE', 'Франция');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'ITALY', 'Италия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'RUSSIA', 'Россия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'SPAIN', 'Испания');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'GERMANY', 'Германия');

-- Крепость (range)
insert into filter (id, name, product_category, type) values ('b68b6d4c-1f10-4e8b-818a-2064d1d19cb5', 'Крепость', 'LOW_ALCOHOL', 'RANGE');
insert into range_filter (id, min, max, unit) values ('b68b6d4c-1f10-4e8b-818a-2064d1d19cb5', 0, 100, '%');
insert into range_filter_step (range_filter_id, min, max, label) values ('b68b6d4c-1f10-4e8b-818a-2064d1d19cb5', 0, 10, 'до 10%');
insert into range_filter_step (range_filter_id, min, max, label) values ('b68b6d4c-1f10-4e8b-818a-2064d1d19cb5', 10, 20, '10% - 20%');
insert into range_filter_step (range_filter_id, min, max, label) values ('b68b6d4c-1f10-4e8b-818a-2064d1d19cb5', 20, 100, 'Свыше 20%');

-- Производитель (multi_select)
insert into filter (id, name, product_category, type) values ('b9eea026-7ab1-4194-a417-7ea6f4610c5e', 'Производитель', 'LOW_ALCOHOL', 'MULTISELECT');
insert into multi_select_filter (id) values ('b9eea026-7ab1-4194-a417-7ea6f4610c5e');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b9eea026-7ab1-4194-a417-7ea6f4610c5e', 'campari', 'Campari');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b9eea026-7ab1-4194-a417-7ea6f4610c5e', 'martini', 'Martini');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b9eea026-7ab1-4194-a417-7ea6f4610c5e', 'aperol', 'Aperol');

-- Объем (multi_select)
insert into filter (id, name, product_category, type) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016', 'Объем', 'LOW_ALCOHOL', 'MULTISELECT');
insert into multi_select_filter (id) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016', '0.5', '0,5 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016', '0.7', '0,7 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016', '1', '1 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016', 'other', 'Другой');

-- Цена (range)
insert into filter (id, name, product_category, type) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 'Цена', 'LOW_ALCOHOL', 'RANGE');
insert into range_filter (id, min, max, unit) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 20000, 999999, 'от 20000₽');

-- CHAMPAGNE_AND_SPARKLING
-- Наличие в магазинах (checkbox)
insert into filter (id, name, product_category, type) values ('2b1a8c5f-23b1-4c3d-87f6-962272a56236', 'Наличие в магазинах', 'CHAMPAGNE_AND_SPARKLING', 'CHECKBOX');
insert into checkbox_filter (id) values ('2b1a8c5f-23b1-4c3d-87f6-962272a56236');

-- Категория (multi_select)
insert into filter (id, name, product_category, type) values ('5b555b5a-2e91-4834-8c8a-4d9e5d237c80', 'Категория', 'CHAMPAGNE_AND_SPARKLING', 'MULTISELECT');
insert into multi_select_filter (id) values ('5b555b5a-2e91-4834-8c8a-4d9e5d237c80');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('5b555b5a-2e91-4834-8c8a-4d9e5d237c80', 'champagne', 'Шампанское');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('5b555b5a-2e91-4834-8c8a-4d9e5d237c80', 'sparkling', 'Игристое');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('5b555b5a-2e91-4834-8c8a-4d9e5d237c80', 'prosecco', 'Проссеко');

-- География (multi_select)
insert into filter (id, name, product_category, type) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'География', 'CHAMPAGNE_AND_SPARKLING', 'MULTISELECT');
insert into multi_select_filter (id) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'FRANCE', 'Франция');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'ITALY', 'Италия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'RUSSIA', 'Россия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'SPAIN', 'Испания');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'GERMANY', 'Германия');

-- Содержание сахара (multi_select)
insert into filter (id, name, product_category, type) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'Содержание сахара', 'CHAMPAGNE_AND_SPARKLING', 'MULTISELECT');
insert into multi_select_filter (id) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'brut_nature', 'Brut Nature');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'extra_brut', 'Extra Brut');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'brut', 'Brut');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'extra_dry', 'Extra Dry');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'demi_sec', 'Demi-Sec');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'doux', 'Doux');

-- Производитель (multi_select)
insert into filter (id, name, product_category, type) values ('c2bcfa4e-912e-4ccd-b14c-3fae5fc1e638', 'Производитель', 'CHAMPAGNE_AND_SPARKLING', 'MULTISELECT');
insert into multi_select_filter (id) values ('c2bcfa4e-912e-4ccd-b14c-3fae5fc1e638');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c2bcfa4e-912e-4ccd-b14c-3fae5fc1e638', 'moet_chandon', 'Moet & Chandon');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c2bcfa4e-912e-4ccd-b14c-3fae5fc1e638', 'perrier_jouet', 'Perrier-Jouet');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c2bcfa4e-912e-4ccd-b14c-3fae5fc1e638', 'castelvecchio', 'Castelvecchio');

-- Цвет (multi_select)
insert into filter (id, name, product_category, type) values ('e6de4592-ea17-4671-b901-ddcd774e64c7', 'Цвет', 'CHAMPAGNE_AND_SPARKLING', 'MULTISELECT');
insert into multi_select_filter (id) values ('e6de4592-ea17-4671-b901-ddcd774e64c7');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e6de4592-ea17-4671-b901-ddcd774e64c7', 'WHITE', 'Белое');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e6de4592-ea17-4671-b901-ddcd774e64c7', 'PINK', 'Розовое');

-- Объем (multi_select)
insert into filter (id, name, product_category, type) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', 'Объем', 'CHAMPAGNE_AND_SPARKLING', 'MULTISELECT');
insert into multi_select_filter (id) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', '0.5', '0,5 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', '0.7', '0,7 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', '1', '1 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', '1.75', '1,75 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', 'other', 'Другой');

-- Особенности (multi_select)
insert into filter (id, name, product_category, type) values ('9cda7ec3-5fc7-4404-9ce3-6aeae90cabf2', 'Особенности', 'CHAMPAGNE_AND_SPARKLING', 'MULTISELECT');
insert into multi_select_filter (id) values ('9cda7ec3-5fc7-4404-9ce3-6aeae90cabf2');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('9cda7ec3-5fc7-4404-9ce3-6aeae90cabf2', 'gift_wrapping', 'Подарочный набор');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('9cda7ec3-5fc7-4404-9ce3-6aeae90cabf2', 'сollection', 'Коллекционное');

-- Цена (range)
insert into filter (id, name, product_category, type) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 'Цена', 'CHAMPAGNE_AND_SPARKLING', 'RANGE');
insert into range_filter (id, min, max, unit) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 20000, 999999, 'от 20000₽');

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

insert into event (id, description, started_at, finished_at) values ('00aa6ec9-e08f-4b1d-a1da-6b6cceec809f', 'Дегустация пивка и винца', date '2024-09-27' + time '14:00', date '2024-09-27' + time '18:00')
insert into event (id, description, started_at, finished_at) values ('688784be-c2ab-411a-ac21-27bf7f7a99e9', 'Масленница', date '2024-11-27' + time '14:00', date '2024-12-04' + time '18:00')
insert into event (id, description, started_at, finished_at) values ('89b96e1a-52f0-4816-89dc-1077c51d8942', 'Подготовка к Новому году!', date '2024-12-05' + time '14:00', date '2024-12-06' + time '18:00')
