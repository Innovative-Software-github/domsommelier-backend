-- WINE
-- Наличие в магазинах (checkbox)
insert into filter (id, name, field, product_category, type) values ('e6064621-31a5-41e7-becd-61b98f0e2ffd', 'Наличие в магазинах', 'available_in_store' , 'wine', 'checkbox');
insert into checkbox_filter (id) values ('e6064621-31a5-41e7-becd-61b98f0e2ffd');

-- Цвет (multi_select)
insert into filter (id, name, field, product_category, type) values ('2d2bae7f-2924-4d1e-b0ad-94c1eb979921', 'Цвет', 'color', 'wine', 'multi_select');
insert into multi_select_filter (id) values ('2d2bae7f-2924-4d1e-b0ad-94c1eb979921');

-- География (multi_select)
insert into filter (id, name, field, product_category, type) values ('b210deae-e49e-4889-967b-8d2b7934f853', 'География', 'country_name', 'wine', 'multi_select');
insert into multi_select_filter (id) values ('b210deae-e49e-4889-967b-8d2b7934f853');

-- Сорт винограда (multi_select)
insert into filter (id, name, field, product_category, type) values ('818fda71-375b-4ef8-9c47-1ef38e92bd68', 'Сорт винограда', 'grape', 'wine', 'multi_select');
insert into multi_select_filter (id) values ('818fda71-375b-4ef8-9c47-1ef38e92bd68');

-- Производитель (multi_select)
insert into filter (id, name, field, product_category, type) values ('e1913a59-549a-4cfd-830b-153b7ebf2f55', 'Производитель', 'producer', 'wine', 'multi_select');
insert into multi_select_filter (id) values ('e1913a59-549a-4cfd-830b-153b7ebf2f55');

-- Объем (multi_select)
insert into filter (id, name, field, product_category, type) values ('bdf0c025-205e-41c5-93ec-1274de945388', 'Объем', 'volume', 'wine', 'multi_select');
insert into multi_select_filter (id) values ('bdf0c025-205e-41c5-93ec-1274de945388');

-- Особенности (multi_select)
insert into filter (id, name, field, product_category, type) values ('02ddd7cf-49f2-45d2-a2e5-a0d3d115af02', 'Особенности', 'feature', 'wine', 'multi_select');
insert into multi_select_filter (id) values ('02ddd7cf-49f2-45d2-a2e5-a0d3d115af02');

-- Цена (range)
insert into filter (id, name, field, product_category, type) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 'Цена', 'price', 'wine', 'range');
insert into range_filter (id, min, max, unit) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('a1fa77c5-d220-4ca4-a70a-f5a212456ee3', 20000, 999999, 'от 20000₽');

-- SPIRIT
-- Наличие в магазинах (checkbox)
insert into filter (id, name, field, product_category, type) values ('c7c6b6ca-2635-4c9f-9b56-4937e52bb7f4', 'Наличие в магазинах', 'available_in_store' , 'spirit', 'checkbox');
insert into checkbox_filter (id) values ('c7c6b6ca-2635-4c9f-9b56-4937e52bb7f4');

-- География (multi_select)
insert into filter (id, name, field, product_category, type) values ('d879d38d-e7ce-4b30-bc88-c1e9d1aab413', 'География', 'country_name', 'spirit', 'multi_select');
insert into multi_select_filter (id) values ('d879d38d-e7ce-4b30-bc88-c1e9d1aab413');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('d879d38d-e7ce-4b30-bc88-c1e9d1aab413', 'ITALY', 'Италия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('d879d38d-e7ce-4b30-bc88-c1e9d1aab413', 'RUSSIA', 'Россия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('d879d38d-e7ce-4b30-bc88-c1e9d1aab413', 'FRANCE', 'Франция');

-- Производитель (multi_select)
insert into filter (id, name, field, product_category, type) values ('e8ea11c1-412e-444d-ac9f-8bac813fcdfa', 'Производитель', 'producer', 'spirit', 'multi_select');
insert into multi_select_filter (id) values ('e8ea11c1-412e-444d-ac9f-8bac813fcdfa');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e8ea11c1-412e-444d-ac9f-8bac813fcdfa', 'pulltex', 'Pulltex');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e8ea11c1-412e-444d-ac9f-8bac813fcdfa', 'prestigio', 'Prestigio');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e8ea11c1-412e-444d-ac9f-8bac813fcdfa', 'onegin', 'Онегин');

-- Особенности (multi_select)
insert into filter (id, name, field, product_category, type) values ('09f3ad68-6bfd-4dbc-b809-8ba91e4ba023', 'Особенности', 'feature', 'spirit', 'multi_select');
insert into multi_select_filter (id) values ('09f3ad68-6bfd-4dbc-b809-8ba91e4ba023');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('09f3ad68-6bfd-4dbc-b809-8ba91e4ba023', 'gift_wrapping', 'Подарочный набор');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('09f3ad68-6bfd-4dbc-b809-8ba91e4ba023', 'сollection', 'Коллекционное');

-- Цена (range)
insert into filter (id, name, field, product_category, type) values ('d6276f43-7283-4d1f-8924-539171565247', 'Цена', 'price', 'spirit', 'range');
insert into range_filter (id, min, max, unit) values ('d6276f43-7283-4d1f-8924-539171565247', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d6276f43-7283-4d1f-8924-539171565247', 20000, 999999, 'от 20000₽');

-- ACCESSORIES
-- Наличие в магазинах (checkbox)
insert into filter (id, name, field, product_category, type) values ('dd6fb4fc-7c43-4fb3-8bf0-011e9ff6494c', 'Наличие в магазинах', 'available_in_store' , 'accessories', 'checkbox');
insert into checkbox_filter (id) values ('dd6fb4fc-7c43-4fb3-8bf0-011e9ff6494c');

-- География (multi_select)
insert into filter (id, name, field, product_category, type) values ('caa1eb21-4b02-4a4f-81f8-7e2fb0723db4', 'География', 'country_name', 'accessories', 'multi_select');
insert into multi_select_filter (id) values ('caa1eb21-4b02-4a4f-81f8-7e2fb0723db4');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('caa1eb21-4b02-4a4f-81f8-7e2fb0723db4', 'ITALY', 'Италия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('caa1eb21-4b02-4a4f-81f8-7e2fb0723db4', 'RUSSIA', 'Россия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('caa1eb21-4b02-4a4f-81f8-7e2fb0723db4', 'FRANCE', 'Франция');

-- Производитель (multi_select)
insert into filter (id, name, field, product_category, type) values ('e4e9d3b2-5a91-4ecd-8c31-4e7281e4b2ce', 'Производитель', 'producer', 'accessories', 'multi_select');
insert into multi_select_filter (id) values ('e4e9d3b2-5a91-4ecd-8c31-4e7281e4b2ce');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e4e9d3b2-5a91-4ecd-8c31-4e7281e4b2ce', 'pulltex', 'Pulltex');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e4e9d3b2-5a91-4ecd-8c31-4e7281e4b2ce', 'prestigio', 'Prestigio');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e4e9d3b2-5a91-4ecd-8c31-4e7281e4b2ce', 'onegin', 'Онегин');

-- Особенности (multi_select)
insert into filter (id, name, field, product_category, type) values ('eedbce23-1eb0-4c6f-a4e6-4b2e5ea8e2a7', 'Особенности', 'feature', 'accessories', 'multi_select');
insert into multi_select_filter (id) values ('eedbce23-1eb0-4c6f-a4e6-4b2e5ea8e2a7');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('eedbce23-1eb0-4c6f-a4e6-4b2e5ea8e2a7', 'gift_wrapping', 'Подарочный набор');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('eedbce23-1eb0-4c6f-a4e6-4b2e5ea8e2a7', 'сollection', 'Коллекционное');

-- Цена (range)
insert into filter (id, name, field, product_category, type) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 'Цена', 'price', 'accessories', 'range');
insert into range_filter (id, min, max, unit) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('5ef0cce6-d562-43a0-91a4-b29226b1fd14', 20000, 999999, 'от 20000₽');

-- SNACKS
-- Наличие в магазинах (checkbox)
insert into filter (id, name, field, product_category, type) values ('05e25457-693b-4a92-8e52-acb2c034e4b0', 'Наличие в магазинах', 'available_in_store' , 'snack', 'checkbox');
insert into checkbox_filter (id) values ('05e25457-693b-4a92-8e52-acb2c034e4b0');

-- Категория (multi_select)
insert into filter (id, name, field, product_category, type) values ('0ebd7e0f-cb8d-4a2e-83af-3d6d6e652478', 'Категория', 'category', 'snack', 'multi_select');
insert into multi_select_filter (id) values ('0ebd7e0f-cb8d-4a2e-83af-3d6d6e652478');

-- Подбор к напиткам (multi_select)
insert into filter (id, name, field, product_category, type) values ('c55d7baa-15fc-4930-acee-32234cefcdc9', 'Подбор к напиткам', 'smart_selection', 'snack', 'multi_select');
insert into multi_select_filter (id) values ('c55d7baa-15fc-4930-acee-32234cefcdc9');

-- География (multi_select)
insert into filter (id, name, field, product_category, type) values ('ef50bee7-0608-4150-b3fc-2716932b4f1b', 'География', 'country_name', 'snack', 'multi_select');
insert into multi_select_filter (id) values ('ef50bee7-0608-4150-b3fc-2716932b4f1b');

-- Производитель (multi_select)
insert into filter (id, name, field, product_category, type) values ('6acab7b2-870a-442c-804e-963a39c5a860', 'Производитель', 'producer', 'snack', 'multi_select');
insert into multi_select_filter (id) values ('6acab7b2-870a-442c-804e-963a39c5a860');

-- Цена (range)
insert into filter (id, name, field, product_category, type) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 'Цена', 'price', 'snack', 'range');
insert into range_filter (id, min, max, unit) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('7d5017fb-6631-4b7b-b5a3-c0244240fc0a', 20000, 999999, 'от 20000₽');

-- LOW_ALCOHOL
-- Наличие в магазинах (checkbox)
insert into filter (id, name, field, product_category, type) values ('aad7c339-d8f0-4d72-a9a7-361f5a04a300', 'Наличие в магазинах', 'available_in_store' , 'low_alcohol', 'checkbox');
insert into checkbox_filter (id) values ('aad7c339-d8f0-4d72-a9a7-361f5a04a300');

-- Категория (multi_select)
insert into filter (id, name, field, product_category, type) values ('3a82f3e6-6a98-44a9-b687-88b0b354e11b', 'Категория', 'category', 'low_alcohol', 'multi_select');
insert into multi_select_filter (id) values ('3a82f3e6-6a98-44a9-b687-88b0b354e11b');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('3a82f3e6-6a98-44a9-b687-88b0b354e11b', 'vermouth', 'Вермут');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('3a82f3e6-6a98-44a9-b687-88b0b354e11b', 'aperitifs', 'Аперитив');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('3a82f3e6-6a98-44a9-b687-88b0b354e11b', 'tinctures', 'Настойки');

-- География (multi_select)
insert into filter (id, name, field, product_category, type) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'География', 'country_name', 'low_alcohol', 'multi_select');
insert into multi_select_filter (id) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'FRANCE', 'Франция');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'ITALY', 'Италия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'RUSSIA', 'Россия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'SPAIN', 'Испания');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('092ebf93-af16-4f5a-8c7b-72e0cc0f5950', 'GERMANY', 'Германия');

-- Крепость (range)
insert into filter (id, name, field, product_category, type) values ('b68b6d4c-1f10-4e8b-818a-2064d1d19cb5', 'Крепость', 'alcohol_strength', 'low_alcohol', 'range');
insert into range_filter (id, min, max, unit) values ('b68b6d4c-1f10-4e8b-818a-2064d1d19cb5', 0, 100, '%');
insert into range_filter_step (range_filter_id, min, max, label) values ('b68b6d4c-1f10-4e8b-818a-2064d1d19cb5', 0, 10, 'до 10%');
insert into range_filter_step (range_filter_id, min, max, label) values ('b68b6d4c-1f10-4e8b-818a-2064d1d19cb5', 10, 20, '10% - 20%');
insert into range_filter_step (range_filter_id, min, max, label) values ('b68b6d4c-1f10-4e8b-818a-2064d1d19cb5', 20, 100, 'Свыше 20%');

-- Производитель (multi_select)
insert into filter (id, name, field, product_category, type) values ('b9eea026-7ab1-4194-a417-7ea6f4610c5e', 'Производитель', 'producer', 'low_alcohol', 'multi_select');
insert into multi_select_filter (id) values ('b9eea026-7ab1-4194-a417-7ea6f4610c5e');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b9eea026-7ab1-4194-a417-7ea6f4610c5e', 'campari', 'Campari');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b9eea026-7ab1-4194-a417-7ea6f4610c5e', 'martini', 'Martini');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b9eea026-7ab1-4194-a417-7ea6f4610c5e', 'aperol', 'Aperol');

-- Объем (multi_select)
insert into filter (id, name, field, product_category, type) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016', 'Объем', 'volume', 'low_alcohol', 'multi_select');
insert into multi_select_filter (id) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016', '0.5', '0,5 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016', '0.7', '0,7 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016', '1', '1 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('b96132da-ef6d-4349-bb96-0d3f4b2b5016', 'other', 'Другой');

-- Цена (range)
insert into filter (id, name, field, product_category, type) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 'Цена', 'price', 'low_alcohol', 'range');
insert into range_filter (id, min, max, unit) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('d1bdbb2e-733f-4ea5-8290-5e63eea7470a', 20000, 999999, 'от 20000₽');

-- CHAMPAGNE_AND_SPARKLING
-- Наличие в магазинах (checkbox)
insert into filter (id, name, field, product_category, type) values ('2b1a8c5f-23b1-4c3d-87f6-962272a56236', 'Наличие в магазинах', 'available_in_store' , 'champagne_and_sparkling', 'checkbox');
insert into checkbox_filter (id) values ('2b1a8c5f-23b1-4c3d-87f6-962272a56236');

-- Категория (multi_select)
insert into filter (id, name, field, product_category, type) values ('5b555b5a-2e91-4834-8c8a-4d9e5d237c80', 'Категория', 'category', 'champagne_and_sparkling', 'multi_select');
insert into multi_select_filter (id) values ('5b555b5a-2e91-4834-8c8a-4d9e5d237c80');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('5b555b5a-2e91-4834-8c8a-4d9e5d237c80', 'champagne', 'Шампанское');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('5b555b5a-2e91-4834-8c8a-4d9e5d237c80', 'sparkling', 'Игристое');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('5b555b5a-2e91-4834-8c8a-4d9e5d237c80', 'prosecco', 'Проссеко');

-- География (multi_select)
insert into filter (id, name, field, product_category, type) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'География', 'country_name', 'champagne_and_sparkling', 'multi_select');
insert into multi_select_filter (id) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'FRANCE', 'Франция');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'ITALY', 'Италия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'RUSSIA', 'Россия');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'SPAIN', 'Испания');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('8fc2deeb-25c1-4cc7-8397-63f0d8c1b1ef', 'GERMANY', 'Германия');

-- Содержание сахара (multi_select)
insert into filter (id, name, field, product_category, type) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'Содержание сахара', 'sugar_content', 'champagne_and_sparkling', 'multi_select');
insert into multi_select_filter (id) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'brut_nature', 'Brut Nature');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'extra_brut', 'Extra Brut');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'brut', 'Brut');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'extra_dry', 'Extra Dry');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'demi_sec', 'Demi-Sec');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c098ed6e-8a3d-4efe-8a09-f512d1c7f8e6', 'doux', 'Doux');

-- Производитель (multi_select)
insert into filter (id, name, field, product_category, type) values ('c2bcfa4e-912e-4ccd-b14c-3fae5fc1e638', 'Производитель', 'producer', 'champagne_and_sparkling', 'multi_select');
insert into multi_select_filter (id) values ('c2bcfa4e-912e-4ccd-b14c-3fae5fc1e638');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c2bcfa4e-912e-4ccd-b14c-3fae5fc1e638', 'moet_chandon', 'Moet & Chandon');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c2bcfa4e-912e-4ccd-b14c-3fae5fc1e638', 'perrier_jouet', 'Perrier-Jouet');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('c2bcfa4e-912e-4ccd-b14c-3fae5fc1e638', 'castelvecchio', 'Castelvecchio');

-- Цвет (multi_select)
insert into filter (id, name, field, product_category, type) values ('e6de4592-ea17-4671-b901-ddcd774e64c7', 'Цвет', 'color', 'champagne_and_sparkling', 'multi_select');
insert into multi_select_filter (id) values ('e6de4592-ea17-4671-b901-ddcd774e64c7');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e6de4592-ea17-4671-b901-ddcd774e64c7', 'WHITE', 'Белое');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('e6de4592-ea17-4671-b901-ddcd774e64c7', 'PINK', 'Розовое');

-- Объем (multi_select)
insert into filter (id, name, field, product_category, type) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', 'Объем', 'volume', 'champagne_and_sparkling', 'multi_select');
insert into multi_select_filter (id) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', '0.5', '0,5 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', '0.7', '0,7 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', '1', '1 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', '1.75', '1,75 л');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('0b06e9b7-22d4-46e1-bdcd-1de905d274be', 'other', 'Другой');

-- Особенности (multi_select)
insert into filter (id, name, field, product_category, type) values ('9cda7ec3-5fc7-4404-9ce3-6aeae90cabf2', 'Особенности', 'features', 'champagne_and_sparkling', 'multi_select');
insert into multi_select_filter (id) values ('9cda7ec3-5fc7-4404-9ce3-6aeae90cabf2');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('9cda7ec3-5fc7-4404-9ce3-6aeae90cabf2', 'gift_wrapping', 'Подарочный набор');
insert into multi_select_filter_option (multi_select_filter_id, value, label) values ('9cda7ec3-5fc7-4404-9ce3-6aeae90cabf2', 'сollection', 'Коллекционное');

-- Цена (range)
insert into filter (id, name, field, product_category, type) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 'Цена', 'price', 'champagne_and_sparkling', 'range');
insert into range_filter (id, min, max, unit) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 0, 9999999, '₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 0, 1000, 'До 1000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 1000, 3000, '1000-3000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 3000, 6000, '3000-6000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 6000, 10000, '6000-10000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 10000, 20000, '10000-20000₽');
insert into range_filter_step (range_filter_id, min, max, label) values ('45d6ec7c-0c6c-482b-bdb7-1e22a05ef567', 20000, 999999, 'от 20000₽');