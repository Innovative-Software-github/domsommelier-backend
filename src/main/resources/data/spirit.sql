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

-- Расширенный ассортимент: все виды крепкого, разная крепость и объём.
insert into spirit (id, subcategory, strength, producer, volume) values ('87118d8d-91ff-53cb-98ed-d1a91833c861', 'Виски', 40, 'The Macallan', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('df2b2b76-f934-50cf-b649-af6dd6348b7c', 'Виски', 40, 'Laphroaig', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('1583ad38-9939-51e0-9798-c9608f83ad60', 'Виски', 45.8, 'Talisker', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('c5669599-6ec7-54a8-ac9d-94beab780599', 'Виски', 40, 'Chivas Brothers', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('0460744e-83d8-5d6e-8543-cfe0132c440a', 'Виски', 40, 'Jim Beam', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('593a4a5c-cf5e-51eb-a51c-459a1de72e3e', 'Виски', 45, 'Maker''s Mark', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('82901210-1f60-5ab2-ad43-7087ecb9e697', 'Виски', 43, 'Suntory', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('02f97f47-1bbc-52a5-8dcd-efc18f33a635', 'Виски', 40, 'Bushmills', 1.0);
insert into spirit (id, subcategory, strength, producer, volume) values ('349af839-02a4-5c99-ba18-05732542e4c2', 'Коньяк', 40, 'Martell', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('d5f923e6-5595-503c-9417-56a46b0c8263', 'Коньяк', 40, 'Rémy Martin', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('261609ef-e30e-5be0-8e1a-992b469b9460', 'Коньяк', 40, 'Courvoisier', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('f5c5d0c1-7dce-55cc-bf3e-a36355757c30', 'Коньяк', 40, 'Арарат', 0.5);
insert into spirit (id, subcategory, strength, producer, volume) values ('7ff2ba09-d546-597f-a76d-3cf6a435ebe3', 'Бренди', 38, 'Torres', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('7a82d076-24ec-5762-aa92-f31b7cb00d10', 'Бренди', 40, 'Metaxa', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('8feee7d2-2423-58db-b174-16d19416aab4', 'Ром', 40, 'Havana Club', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('24a71c8b-dc2e-5fb7-b802-80189755a4ff', 'Ром', 35, 'Captain Morgan', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('6f02d6c0-2f41-5522-b701-86529bb41178', 'Ром', 40, 'Diplomático', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('fe14ba1f-08c9-5021-9510-23a1216b596a', 'Ром', 40, 'Zacapa', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('20f83569-7b5a-5e34-8e71-64e30225ceb0', 'Текила', 38, 'Olmeca', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('cb39c487-5059-52df-a7c4-d116bca574a8', 'Текила', 40, 'Patrón', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('60778ed6-2e1c-53f8-951f-d7feb003daa4', 'Текила', 38, 'Don Julio', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('ee8909c1-dbbf-5c1a-9972-03db28546ace', 'Джин', 40, 'Bombay Sapphire', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('df94f294-1f81-5d01-bc25-ec6dee914b8c', 'Джин', 41.4, 'Hendrick''s', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('ba3c3835-f0b0-5900-97bb-18418a4e5ccc', 'Джин', 43.1, 'Tanqueray', 1.0);
insert into spirit (id, subcategory, strength, producer, volume) values ('1f360717-e89a-5ed2-9694-3f689cd523a4', 'Водка', 40, 'Grey Goose', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('4bd16e73-887a-55fa-880f-c66dd407e721', 'Водка', 40, 'Русский Стандарт', 0.5);
insert into spirit (id, subcategory, strength, producer, volume) values ('c078750c-89ed-5d97-b1f2-dd1b70b57739', 'Водка', 40, 'Finlandia', 1.0);
insert into spirit (id, subcategory, strength, producer, volume) values ('344fa352-f969-59d0-827c-b2e151f30cba', 'Ликер', 17, 'Baileys', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('6445dcdb-6da5-517b-9b98-8332ac9dd697', 'Ликер', 35, 'Mast-Jägermeister', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('b3fad7ef-e59e-5d54-9674-45aa1aa429ef', 'Ликер', 40, 'Cointreau', 0.7);
insert into spirit (id, subcategory, strength, producer, volume) values ('5f205600-c637-5548-a1d8-131c77beb6d5', 'Абсент', 70, 'Xenta', 0.7);

insert into spirit_feature (spirit_id, feature) values ('87118d8d-91ff-53cb-98ed-d1a91833c861', 'collectible');
insert into spirit_feature (spirit_id, feature) values ('c5669599-6ec7-54a8-ac9d-94beab780599', 'gift_set');
insert into spirit_feature (spirit_id, feature) values ('261609ef-e30e-5be0-8e1a-992b469b9460', 'gift_set');
insert into spirit_feature (spirit_id, feature) values ('6f02d6c0-2f41-5522-b701-86529bb41178', 'award_winning');
insert into spirit_feature (spirit_id, feature) values ('fe14ba1f-08c9-5021-9510-23a1216b596a', 'collectible');
insert into spirit_feature (spirit_id, feature) values ('60778ed6-2e1c-53f8-951f-d7feb003daa4', 'limited_edition');
insert into spirit_feature (spirit_id, feature) values ('df94f294-1f81-5d01-bc25-ec6dee914b8c', 'award_winning');
