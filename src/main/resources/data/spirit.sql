insert into spirit_category (name) values ('Виски'), ('Коньяк'), ('Водка'), ('Текила'), ('Ром'), ('Джин'), ('Бренди'), ('Самогон'), ('Абсент'), ('Ликер');
--insert into spirit_strength (name) values (20), (30), (38), (40), (50), (60), (70), (80), (90), (100)
--insert into spirit_volume (name) values (0.5), (0.7), (1), (1.5), (2), (2.5), (3)

insert into spirit (id, subcategory, strength, producer, volume) values ('3a4b85d0-f739-4856-a09b-8495f2157e4d', 'Виски', 40, 'Johnnie Walker', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('dcfe12c7-2a16-4482-9c5b-ca57260fc475', 'Виски', 40, 'Jack Daniel''s', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('f4e364ff-b656-44a3-a16e-2263e560345c', 'Водка', 40, 'Beluga', 0.5);
insert into spirit (id, subcategory, strength, producer, volume) values ('4f614d9a-bb8c-4bdb-bd46-0be893b2e1f1', 'Виски', 40, 'Glenfiddich', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('9bb175b1-2680-4b48-b29f-3a8bf2b7afd5', 'Коньяк', 40, 'Hennessy', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('3e187c2a-4adb-4e2d-9d17-8b6d4c9e2f22', 'Текила', 38, 'Jose Cuervo', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('2a503e87-9a80-4dd9-8107-c6d42f3d7580', 'Ром', 40, 'Bacardi', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('1e225edf-b7b8-4c86-9c7f-c7b863ea13de', 'Водка', 40, 'Absolut', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('86a5e2c3-3e4b-4b25-9af3-35d1c5cbe7c7', 'Виски', 40, 'Jameson', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('c8c9670b-23ae-42fb-9d8b-6e56f0dda6ea', 'Джин', 40, 'Beefeater', 0.7);

insert into spirit_feature (spirit_id, feature) values ('3a4b85d0-f739-4856-a09b-8495f2157e4d', 'gift_set');
insert into spirit_feature (spirit_id, feature) values ('4f614d9a-bb8c-4bdb-bd46-0be893b2e1f1', 'collectible');
insert into spirit_feature (spirit_id, feature) values ('9bb175b1-2680-4b48-b29f-3a8bf2b7afd5', 'limited_edition');
insert into spirit_feature (spirit_id, feature) values ('c8c9670b-23ae-42fb-9d8b-6e56f0dda6ea', 'award_winning');