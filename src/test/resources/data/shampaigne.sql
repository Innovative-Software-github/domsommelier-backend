insert into sparkling_wine_category(name) values ('Шампанское'), ('Игристое вино'), ('Просекко'), ('Кава');
insert into sugar_content(name) values ('Brut Nature'), ('Extra Brut'), ('Brut'), ('Extra Dry'), ('Sec'), ('Demi-Sec'), ('Doux');
insert into sparkling_wine_color(name) values ('Белое'), ('Розовое'), ('Красное');
insert into wine_volume(name) values ('0.187'), ('0.375'), ('0.5'), ('0.75'), ('1'), ('1.5'), ('3'), ('6');

insert into sparkling_wine (id, subcategory, sugar_content, producer, color, volume) values ('e101a5eb-7c5f-47c3-b706-5a1a9211fd65', 'Шампанское', 'Brut', 'Moet & Chandon', 'Белое', '0.75');
insert into sparkling_wine (id, subcategory, sugar_content, producer, color, volume) values ('5614868b-6d92-4055-bdcc-beb1b70d9dc9', 'Шампанское', 'Brut', 'Veuve Clicquot', 'Белое', '0.75');
insert into sparkling_wine (id, subcategory, sugar_content, producer, color, volume) values ('cda029a7-0c77-4f2c-82b8-38d6e1b22f62', 'Игристое вино', 'Demi-Sec', 'Martini & Rossi', 'Белое', '0.75');
insert into sparkling_wine (id, subcategory, sugar_content, producer, color, volume) values ('fcb33c4b-31d2-4412-b2b3-03b77a62f731', 'Кава', 'Brut', 'Freixenet', 'Белое', '0.75');
insert into sparkling_wine (id, subcategory, sugar_content, producer, color, volume) values ('ee65bbc6-13ef-4f8c-a55b-39eecac18b91', 'Кава', 'Sec', 'Codorniu', 'Белое', '0.75');
insert into sparkling_wine (id, subcategory, sugar_content, producer, color, volume) values ('5d2ba3f6-9058-44d3-863c-6f4ca81784e7', 'Игристое вино', 'Demi-Sec', 'Bosca', 'Белое', '0.75');
insert into sparkling_wine (id, subcategory, sugar_content, producer, color, volume) values ('21d0f017-d059-44d7-9482-727801f48e3b', 'Шампанское', 'Brut', 'Mumm', 'Белое', '0.75');
insert into sparkling_wine (id, subcategory, sugar_content, producer, color, volume) values ('98e2aeb6-4ddc-4533-991a-2e3e24de7d8a', 'Просекко', 'Extra Dry', 'Fratelli', 'Белое', '0.75');
insert into sparkling_wine (id, subcategory, sugar_content, producer, color, volume) values ('3b3d8b6b-5a7b-4b99-9489-3bd3d23ad0fb', 'Игристое вино', 'Brut', 'Chandon', 'Розовое', '0.75');
insert into sparkling_wine (id, subcategory, sugar_content, producer, color, volume) values ('69c2ef9b-9e4e-44ff-99ff-8e6fdf6e2c7f', 'Игристое вино', 'Brut', 'Louis Bouillot', 'Белое', '0.75');

insert into sparkling_wine_feature (sparkling_wine_id, feature) values ('e101a5eb-7c5f-47c3-b706-5a1a9211fd65', 'Традиционный метод');
insert into sparkling_wine_feature (sparkling_wine_id, feature) values ('e101a5eb-7c5f-47c3-b706-5a1a9211fd65', 'Автохтонные сорта');
insert into sparkling_wine_feature (sparkling_wine_id, feature) values ('5614868b-6d92-4055-bdcc-beb1b70d9dc9', 'В выдержанных погребах');
insert into sparkling_wine_feature (sparkling_wine_id, feature) values ('cda029a7-0c77-4f2c-82b8-38d6e1b22f62', 'Идеально для десертов');
insert into sparkling_wine_feature (sparkling_wine_id, feature) values ('fcb33c4b-31d2-4412-b2b3-03b77a62f731', 'Популярно в Испании');
insert into sparkling_wine_feature (sparkling_wine_id, feature) values ('ee65bbc6-13ef-4f8c-a55b-39eecac18b91', 'Свежий фруктовый вкус');
insert into sparkling_wine_feature (sparkling_wine_id, feature) values ('5d2ba3f6-9058-44d3-863c-6f4ca81784e7', 'Легкая сладость');
insert into sparkling_wine_feature (sparkling_wine_id, feature) values ('21d0f017-d059-44d7-9482-727801f48e3b', 'Премиальное шампанское');
insert into sparkling_wine_feature (sparkling_wine_id, feature) values ('98e2aeb6-4ddc-4533-991a-2e3e24de7d8a', 'Идеально для аперитива');
insert into sparkling_wine_feature (sparkling_wine_id, feature) values ('3b3d8b6b-5a7b-4b99-9489-3bd3d23ad0fb', 'Цитрусовые ноты');
insert into sparkling_wine_feature (sparkling_wine_id, feature) values ('69c2ef9b-9e4e-44ff-99ff-8e6fdf6e2c7f', 'Выдержка на осадке');