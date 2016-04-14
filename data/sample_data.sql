/*INSERT INTO `roles` SET `name`='Administrator';
INSERT INTO `roles` SET `name`='Manager';
INSERT INTO `roles` SET `name`='Logistician';*/

INSERT INTO `users`(`pin`, `first_name`, `last_name`, `role`) VALUES ('111111','Admin','Test',3);
INSERT INTO `users`(`pin`, `first_name`, `last_name`, `role`) VALUES ('222222','Boss','Test',2);
INSERT INTO `users`(`pin`, `first_name`, `last_name`, `role`) VALUES ('333333','Worker','Test',1);
INSERT INTO `users`(`badge_id`, `first_name`, `last_name`, `role`) VALUES ('12345678','Badger','Testaccount',1);

/* Test Products */
INSERT INTO `brands` SET `brand_id`=1, `name`='Test Brand #1';
INSERT INTO `brands` SET `brand_id`=2, `name`='Test Brand #2';
INSERT INTO `brands` SET `brand_id`=3, `name`='Test Brand #3';
INSERT INTO `brands` SET `brand_id`=4, `name`='Empty Brand';
INSERT INTO `types` SET `type_id`=1, `name`='Regular';
INSERT INTO `types` SET `type_id`=2, `name`='Raw';
INSERT INTO `types` SET `type_id`=3, `name`='Frozen';
INSERT INTO `categories` SET `category_id`=1, `name`='Regular Stuff', `type`=1;
INSERT INTO `categories` SET `category_id`=2, `name`='Raw Food', `type`=2;
INSERT INTO `categories` SET `category_id`=3, `name`='Frozen Things', `type`=3;
INSERT INTO `categories` SET `category_id`=4, `name`='Weird Stuff', `type`=1;
INSERT INTO `products` SET `product_id`=1, `name`='Test Product #1', brand=1, category=1;
INSERT INTO `products` SET `product_id`=2, `name`='Lonely Product #2', brand=1, category=1;
INSERT INTO `products` SET `product_id`=3, `name`='Test Product #3', brand=1, category=2;
INSERT INTO `products` SET `product_id`=4, `name`='Test Product #4', brand=1, category=2;
INSERT INTO `products` SET `product_id`=5, `name`='Test Product #5', brand=1, category=2;
INSERT INTO `products` SET `product_id`=6, `name`='Test Product #6', brand=2, category=1;
INSERT INTO `products` SET `product_id`=7, `name`='Test Product #7', brand=2, category=1;
INSERT INTO `products` SET `product_id`=8, `name`='Test Product #8', brand=2, category=1;
INSERT INTO `products` SET `product_id`=9, `name`='Test Product #9', brand=3, category=1;
INSERT INTO `products` SET `product_id`=10, `name`='Test Product #10', brand=3, category=1;
INSERT INTO `products` SET `product_id`=11, `name`='Lonely Product #11', brand=2, category=4;
INSERT INTO `products` SET `product_id`=12, `name`='Lonely Product #12', brand=2, category=4;

/*		1 - Tiny Full Shelf: 1-4		*/

INSERT INTO `shelves` SET `shelf_id`=1, `levels`=1;

INSERT INTO `shelflevels` SET `level_id`=1, `shelf_id`=1, `shelf_position`=1, `max_shelfslots`=4;

INSERT INTO `shelfslots` SET `slot_id`=1, `level_id`=1, `level_position`=1, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=2, `level_id`=1, `level_position`=2, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=3, `level_id`=1, `level_position`=3, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=4, `level_id`=1, `level_position`=4, `max_productboxes`=1;

INSERT INTO `containers` SET `container_id`=1, `slot_id`=1, `product`=9, `max_size`=10, `product_count`=10, `expiration_date`='2016-02-18';
INSERT INTO `containers` SET `container_id`=2, `slot_id`=2, `product`=1, `max_size`=10, `product_count`=10, `expiration_date`='2016-10-30';
INSERT INTO `containers` SET `container_id`=3, `slot_id`=3, `product`=1, `max_size`=10, `product_count`=10;
INSERT INTO `containers` SET `container_id`=4, `slot_id`=4, `product`=3, `max_size`=10, `product_count`=10;

/*		2 - Slot Full Shelf: 5-9		*/

INSERT INTO `shelves` SET `shelf_id`=2, `levels`=2;

INSERT INTO `shelflevels` SET `level_id`=2, `shelf_id`=2, `shelf_position`=1, `max_shelfslots`=20;
INSERT INTO `shelflevels` SET `level_id`=3, `shelf_id`=2, `shelf_position`=2, `max_shelfslots`=20;

INSERT INTO `shelfslots` SET `slot_id`=5, `level_id`=2, `level_position`=1, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=6, `level_id`=2, `level_position`=2, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=7, `level_id`=2, `level_position`=3, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=8, `level_id`=2, `level_position`=4, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=9, `level_id`=2, `level_position`=5, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=10, `level_id`=2, `level_position`=6, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=11, `level_id`=2, `level_position`=7, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=12, `level_id`=2, `level_position`=8, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=13, `level_id`=2, `level_position`=9, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=14, `level_id`=2, `level_position`=10, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=15, `level_id`=2, `level_position`=11, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=16, `level_id`=2, `level_position`=12, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=17, `level_id`=2, `level_position`=13, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=18, `level_id`=2, `level_position`=14, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=19, `level_id`=2, `level_position`=15, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=20, `level_id`=2, `level_position`=16, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=21, `level_id`=2, `level_position`=17, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=22, `level_id`=2, `level_position`=18, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=23, `level_id`=2, `level_position`=19, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=24, `level_id`=2, `level_position`=20, `max_productboxes`=4;

INSERT INTO `shelfslots` SET `slot_id`=25, `level_id`=3, `level_position`=1, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=26, `level_id`=3, `level_position`=2, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=27, `level_id`=3, `level_position`=3, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=28, `level_id`=3, `level_position`=4, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=29, `level_id`=3, `level_position`=5, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=30, `level_id`=3, `level_position`=6, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=31, `level_id`=3, `level_position`=7, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=32, `level_id`=3, `level_position`=8, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=33, `level_id`=3, `level_position`=9, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=34, `level_id`=3, `level_position`=10, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=35, `level_id`=3, `level_position`=11, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=36, `level_id`=3, `level_position`=12, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=37, `level_id`=3, `level_position`=13, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=38, `level_id`=3, `level_position`=14, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=39, `level_id`=3, `level_position`=15, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=40, `level_id`=3, `level_position`=16, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=41, `level_id`=3, `level_position`=17, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=42, `level_id`=3, `level_position`=18, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=43, `level_id`=3, `level_position`=19, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=44, `level_id`=3, `level_position`=20, `max_productboxes`=4;

INSERT INTO `containers` SET `container_id`=5, `slot_id`=5, `product`=1, `max_size`=50, `product_count`=0;
INSERT INTO `containers` SET `container_id`=6, `slot_id`=5, `product`=3, `max_size`=50, `product_count`=5, `expiration_date`='2005-12-12';
INSERT INTO `containers` SET `container_id`=7, `slot_id`=5, `product`=6, `max_size`=50, `product_count`=10, `expiration_date`='2008-01-10';
INSERT INTO `containers` SET `container_id`=8, `slot_id`=5, `product`=6, `max_size`=50, `product_count`=25;
INSERT INTO `containers` SET `container_id`=9, `slot_id`=44, `product`=1, `max_size`=50, `product_count`=50, `expiration_date`='2020-05-26';

/*		3 - One Slot Level: 10-13		*/

INSERT INTO `shelves` SET `shelf_id`=3, `levels`=3;

INSERT INTO `shelflevels` SET `level_id`=4, `shelf_id`=3,`shelf_position`=1, `max_shelfslots`=1;
INSERT INTO `shelflevels` SET `level_id`=5, `shelf_id`=3,`shelf_position`=2, `max_shelfslots`=1;
INSERT INTO `shelflevels` SET `level_id`=6, `shelf_id`=3,`shelf_position`=3, `max_shelfslots`=1;

INSERT INTO `shelfslots` SET `slot_id`=45, `level_id`=4, `level_position`=1, `max_productboxes`=50;
INSERT INTO `shelfslots` SET `slot_id`=46, `level_id`=5, `level_position`=1, `max_productboxes`=50;
INSERT INTO `shelfslots` SET `slot_id`=47, `level_id`=6, `level_position`=1, `max_productboxes`=50;

INSERT INTO `containers` SET `container_id`=10, `slot_id`=46, `product`=1, `max_size`=20, `product_count`=20;
INSERT INTO `containers` SET `container_id`=11, `slot_id`=46, `product`=10, `max_size`=20, `product_count`=20, `expiration_date`='2015-03-20';
INSERT INTO `containers` SET `container_id`=12, `slot_id`=46, `product`=10, `max_size`=30, `product_count`=20;
INSERT INTO `containers` SET `container_id`=13, `slot_id`=46, `product`=8, `max_size`=30, `product_count`=20;

/*		4 - Many Slot Level: 14-20		*/

INSERT INTO `shelves` SET `shelf_id`=4, `levels`=2;

INSERT INTO `shelflevels` SET `level_id`=7, `shelf_id`=4, `shelf_position`=1, `max_shelfslots`=50;
INSERT INTO `shelflevels` SET `level_id`=8, `shelf_id`=4, `shelf_position`=2, `max_shelfslots`=50;

INSERT INTO `shelfslots` SET `slot_id`=48, `level_id`=7, `level_position`=1, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=49, `level_id`=7, `level_position`=2, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=50, `level_id`=7, `level_position`=3, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=51, `level_id`=7, `level_position`=4, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=52, `level_id`=7, `level_position`=5, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=53, `level_id`=7, `level_position`=6, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=54, `level_id`=7, `level_position`=7, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=55, `level_id`=7, `level_position`=8, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=56, `level_id`=7, `level_position`=9, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=57, `level_id`=7, `level_position`=10, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=58, `level_id`=7, `level_position`=11, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=59, `level_id`=7, `level_position`=12, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=60, `level_id`=7, `level_position`=13, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=61, `level_id`=7, `level_position`=14, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=62, `level_id`=7, `level_position`=15, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=63, `level_id`=7, `level_position`=16, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=64, `level_id`=7, `level_position`=17, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=65, `level_id`=7, `level_position`=18, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=66, `level_id`=7, `level_position`=19, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=67, `level_id`=7, `level_position`=20, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=68, `level_id`=7, `level_position`=21, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=69, `level_id`=7, `level_position`=22, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=70, `level_id`=7, `level_position`=23, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=71, `level_id`=7, `level_position`=24, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=72, `level_id`=7, `level_position`=25, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=73, `level_id`=7, `level_position`=26, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=74, `level_id`=7, `level_position`=27, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=75, `level_id`=7, `level_position`=28, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=76, `level_id`=7, `level_position`=29, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=77, `level_id`=7, `level_position`=30, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=78, `level_id`=7, `level_position`=31, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=79, `level_id`=7, `level_position`=32, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=80, `level_id`=7, `level_position`=33, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=81, `level_id`=7, `level_position`=34, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=82, `level_id`=7, `level_position`=35, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=83, `level_id`=7, `level_position`=36, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=84, `level_id`=7, `level_position`=37, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=85, `level_id`=7, `level_position`=38, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=86, `level_id`=7, `level_position`=39, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=87, `level_id`=7, `level_position`=40, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=88, `level_id`=7, `level_position`=41, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=89, `level_id`=7, `level_position`=42, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=90, `level_id`=7, `level_position`=43, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=91, `level_id`=7, `level_position`=44, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=92, `level_id`=7, `level_position`=45, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=93, `level_id`=7, `level_position`=46, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=94, `level_id`=7, `level_position`=47, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=95, `level_id`=7, `level_position`=48, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=96, `level_id`=7, `level_position`=49, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=97, `level_id`=7, `level_position`=50, `max_productboxes`=1;

INSERT INTO `shelfslots` SET `slot_id`=98, `level_id`=8, `level_position`=1, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=99, `level_id`=8, `level_position`=2, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=100, `level_id`=8, `level_position`=3, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=101, `level_id`=8, `level_position`=4, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=102, `level_id`=8, `level_position`=5, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=103, `level_id`=8, `level_position`=6, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=104, `level_id`=8, `level_position`=7, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=105, `level_id`=8, `level_position`=8, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=106, `level_id`=8, `level_position`=9, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=107, `level_id`=8, `level_position`=10, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=108, `level_id`=8, `level_position`=11, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=109, `level_id`=8, `level_position`=12, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=110, `level_id`=8, `level_position`=13, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=111, `level_id`=8, `level_position`=14, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=112, `level_id`=8, `level_position`=15, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=113, `level_id`=8, `level_position`=16, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=114, `level_id`=8, `level_position`=17, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=115, `level_id`=8, `level_position`=18, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=116, `level_id`=8, `level_position`=19, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=117, `level_id`=8, `level_position`=20, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=118, `level_id`=8, `level_position`=21, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=119, `level_id`=8, `level_position`=22, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=120, `level_id`=8, `level_position`=23, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=121, `level_id`=8, `level_position`=24, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=122, `level_id`=8, `level_position`=25, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=123, `level_id`=8, `level_position`=26, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=124, `level_id`=8, `level_position`=27, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=125, `level_id`=8, `level_position`=28, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=126, `level_id`=8, `level_position`=29, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=127, `level_id`=8, `level_position`=30, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=128, `level_id`=8, `level_position`=31, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=129, `level_id`=8, `level_position`=32, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=130, `level_id`=8, `level_position`=33, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=131, `level_id`=8, `level_position`=34, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=132, `level_id`=8, `level_position`=35, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=133, `level_id`=8, `level_position`=36, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=134, `level_id`=8, `level_position`=37, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=135, `level_id`=8, `level_position`=38, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=136, `level_id`=8, `level_position`=39, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=137, `level_id`=8, `level_position`=40, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=138, `level_id`=8, `level_position`=41, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=139, `level_id`=8, `level_position`=42, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=140, `level_id`=8, `level_position`=43, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=141, `level_id`=8, `level_position`=44, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=142, `level_id`=8, `level_position`=45, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=143, `level_id`=8, `level_position`=46, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=144, `level_id`=8, `level_position`=47, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=145, `level_id`=8, `level_position`=48, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=146, `level_id`=8, `level_position`=49, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=147, `level_id`=8, `level_position`=50, `max_productboxes`=1;

INSERT INTO `containers` SET `container_id`=14, `slot_id`=48, `product`=1, `max_size`=10, `product_count`=5, `expiration_date`='2030-01-02';
INSERT INTO `containers` SET `container_id`=15, `slot_id`=50, `product`=9, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET `container_id`=16, `slot_id`=55, `product`=3, `max_size`=10, `product_count`=5, `expiration_date`='2012-04-18';
INSERT INTO `containers` SET `container_id`=17, `slot_id`=67, `product`=4, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET `container_id`=18, `slot_id`=90, `product`=5, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET `container_id`=19, `slot_id`=91, `product`=6, `max_size`=10, `product_count`=5, `expiration_date`='2016-05-16';
INSERT INTO `containers` SET `container_id`=20, `slot_id`=86, `product`=7, `max_size`=10, `product_count`=5;

/* 5 - Empty Shelf 2-slot */
INSERT INTO `shelves` SET `shelf_id`=5, `levels`=1;

INSERT INTO `shelflevels` SET `level_id`=9, `shelf_id`=5, `shelf_position`=1, `max_shelfslots`=2;
INSERT INTO `shelflevels` SET `level_id`=10, `shelf_id`=5, `shelf_position`=2, `max_shelfslots`=2;

INSERT INTO `shelfslots` SET `slot_id`=148, `level_id`=9,`level_position`=1, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=149, `level_id`=9,`level_position`=2, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=150, `level_id`=10,`level_position`=1, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=151, `level_id`=10,`level_position`=2, `max_productboxes`=1;

/* Lonely Containers 21-23*/
INSERT INTO `containers` SET `container_id`=21, `product`=11, `max_size`=2, `product_count`=2;
INSERT INTO `containers` SET `container_id`=22, `product`=12, `max_size`=2, `product_count`=2;
INSERT INTO `containers` SET `container_id`=23, `product`=1, `max_size`=1, `product_count`=0, `expiration_date`='2003-11-30';

/* Removal Lists 1-5 */
INSERT INTO `removallist_states` SET `removallist_state_id`=1, `name`='Active';
INSERT INTO `removallist_states` SET `removallist_state_id`=2, `name`='Canceled';
INSERT INTO `removallist_states` SET `removallist_state_id`=3, `name`='Finished';

INSERT INTO `removallists` SET `removallist_id`=1, `liststate`=1;
INSERT INTO `removallists` SET `removallist_id`=2, `liststate`=2;
INSERT INTO `removallists` SET `removallist_id`=3, `liststate`=3;
INSERT INTO `removallists` SET `removallist_id`=4, `liststate`=3;

/* Empty */
INSERT INTO `removallists` SET `removallist_id`=5, `liststate`=2;

/* Containers 24-30 */
INSERT INTO `containers` SET `container_id`=24, `removallist`=1, `product`=10, `max_size`=15, `product_count`=10, `expiration_date`='2017-06-17';
INSERT INTO `containers` SET `container_id`=25, `removallist`=1, `product`=5, `max_size`=25, `product_count`=0, `expiration_date`='2018-06-20';
INSERT INTO `containers` SET `container_id`=26, `removallist`=1, `product`=3, `max_size`=200, `product_count`=120;
INSERT INTO `containers` SET `container_id`=27, `removallist`=2, `product`=12, `max_size`=200, `product_count`=96;
INSERT INTO `containers` SET `container_id`=28, `removallist`=2, `product`=1, `max_size`=200, `product_count`=2, `expiration_date`='2016-08-30';
INSERT INTO `containers` SET `container_id`=29, `removallist`=3, `product`=1, `max_size`=200, `product_count`=10, `expiration_date`='2016-08-11';
INSERT INTO `containers` SET `container_id`=30, `removallist`=4, `product`=4, `max_size`=5, `product_count`=0;

/* Manifests */
INSERT INTO `manifest_states` SET `manifest_state_id`=1, `name`='Stored';
INSERT INTO `manifest_states` SET `manifest_state_id`=2, `name`='Accepted';
INSERT INTO `manifest_states` SET `manifest_state_id`=3, `name`='Received';
INSERT INTO `manifest_states` SET `manifest_state_id`=4, `name`='Rejected';
INSERT INTO `manifest_states` SET `manifest_state_id`=5, `name`='Discharged';

INSERT INTO `manifests` SET `manifest_id`=1, `driver_id`=1, `state`=3, `date_ordered`='2016-02-20', `date_received`='2016-02-29';
INSERT INTO `manifests` SET `manifest_id`=2, `driver_id`=2, `state`=1, `date_ordered`='2016-01-01', `date_received`='2016-01-01';
INSERT INTO `manifests` SET `manifest_id`=3, `driver_id`=3, `state`=2, `date_ordered`='2016-02-04', `date_received`='2016-02-10';
INSERT INTO `manifests` SET `manifest_id`=4, `driver_id`=4, `state`=4, `date_ordered`='2015-04-12', `date_received`='2015-07-18';

/* Containers 31-40 */
INSERT INTO `containers` SET `container_id`=31, `manifest`=1, `product`=4, `max_size`=1, `product_count`=1, `expiration_date`='2016-06-17';
INSERT INTO `containers` SET `container_id`=32, `manifest`=1, `product`=4, `max_size`=20, `product_count`=20, `expiration_date`='2020-06-01';
INSERT INTO `containers` SET `container_id`=33, `manifest`=1, `product`=5, `max_size`=200, `product_count`=200, `expiration_date`='2015-12-30';
INSERT INTO `containers` SET `container_id`=34, `manifest`=1, `product`=10, `max_size`=340, `product_count`=340, `expiration_date`='2016-03-22';
INSERT INTO `containers` SET `container_id`=35, `manifest`=2, `product`=8, `max_size`=60, `product_count`=60, `expiration_date`='2018-10-29';
INSERT INTO `containers` SET `container_id`=36, `manifest`=2, `product`=2, `max_size`=87, `product_count`=87, `expiration_date`='2019-03-15';
INSERT INTO `containers` SET `container_id`=37, `manifest`=2, `product`=2, `max_size`=99, `product_count`=99, `expiration_date`='2021-01-01';
INSERT INTO `containers` SET `container_id`=38, `manifest`=4, `product`=9, `max_size`=110, `product_count`=110;
INSERT INTO `containers` SET `container_id`=39, `manifest`=4, `product`=1, `max_size`=205, `product_count`=205;
INSERT INTO `containers` SET `container_id`=40, `product`=8, `max_size`=55, `product_count`=55;

/* The removal platform, only one is supported for now. */
INSERT INTO `removalplatforms` SET `platform_id`=1, `free_space`=1.0, `free_space_warning`=0.1;
