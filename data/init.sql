DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles`
(
	`role_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`name` VARCHAR(128) NOT NULL UNIQUE
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users`
(
	`user_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`badge_id` CHAR(8) NULL UNIQUE,
	`pin` CHAR(6) NULL,
	`first_name` VARCHAR(128) NOT NULL,
	`last_name` VARCHAR(128) NOT NULL,
	`role` INT UNSIGNED NOT NULL,
	
	FOREIGN KEY (`role`) REFERENCES `roles`(`role_id`),
	CONSTRAINT `CONST_unique_pin_login` UNIQUE (`pin`,`first_name`,`last_name`),
	CONSTRAINT `CONST_unique_badge_login` UNIQUE (`badge_id`,`first_name`,`last_name`),
	CONSTRAINT `CONST_unique_name_role` UNIQUE (`first_name`,`last_name`,`role`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `brands`;
CREATE TABLE IF NOT EXISTS `brands`
(
	`brand_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`name` VARCHAR(128) NOT NULL UNIQUE
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `types`;
CREATE TABLE IF NOT EXISTS `types`
(
	`type_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`name` VARCHAR(128) NOT NULL UNIQUE	
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `categories`;
CREATE TABLE IF NOT EXISTS `categories`
(
	`category_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`name` VARCHAR(128) NOT NULL UNIQUE,
	`type` VARCHAR(128) NOT NULL,
	
	FOREIGN KEY (`type`) REFERENCES `types`(`type_id`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products`
(
	`product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`name` VARCHAR(128) NOT NULL,
	`brand` VARCHAR(128) NOT NULL,
	`category` VARCHAR(128) NOT NULL,
	`popularity` INT DEFAULT -1,
	
	FOREIGN KEY (`brand`) REFERENCES `brands`(`brand_id`),
	FOREIGN KEY (`category`) REFERENCES `categories`(`category_id`),
	CONSTRAINT `CONST_unique_products` UNIQUE (`name`,`brand`,`category`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `containers`;
CREATE TABLE IF NOT EXISTS `containers`
(
	`container_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`expiration_date` DATE NULL,
	`product` INT UNSIGNED NOT NULL,
	`max_size` INT NOT NULL,
	`product_count` INT UNSIGNED NOT NULL DEFAULT 0,
	
	FOREIGN KEY (`product`) REFERENCES `products`(`product_id`),
	CHECK (`max_size`>0)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `freezer_products`;
CREATE TABLE IF NOT EXISTS `freezer_products`
(
	`freezer` INT UNSIGNED NOT NULL,
	`product` INT UNSIGNED NOT NULL,
	
	FOREIGN KEY (`freezer`) REFERENCES `containers`(`container_id`),
	FOREIGN KEY (`product`) REFERENCES `products`(`product_id`),
	PRIMARY KEY (`freezer`,`product`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shelves`;
CREATE TABLE IF NOT EXISTS `shelves`
(
	`shelf_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`max_levels` INT UNSIGNED NOT NULL,
	`max_shelfslots_per_level` INT UNSIGNED NOT NULL,
	`max_productboxes_per_shelfslot` INT UNSIGNED NOT NULL,
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shelf_productboxes`;
CREATE TABLE IF NOT EXISTS `shelf_productboxes`
(
	`shelf` INT UNSIGNED NOT NULL,
	`productbox` INT UNSIGNED NOT NULL UNIQUE,
	`shelflevel_index` INT UNSIGNED NOT NULL,
	`shelfslot_index` INT UNSIGNED NOT NULL,
	
	PRIMARY KEY (`shelf`, `productbox`),
	FOREIGN KEY (`shelf`) REFERENCES shelves(`shelf_id`),
	FOREIGN KEY (`productbox`) REFERENCES containers(`container_id`),
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `removallist_states`;
CREATE TABLE IF NOT EXISTS `removallist_states`
(
	`removallist_state_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`name` VARCHAR(128) NOT NULL
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `removallists`;
CREATE TABLE IF NOT EXISTS `removallists`
(
	`removallist_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`liststate` INT UNSIGNED NOT NULL,
	
	FOREIGN KEY (`liststate`) REFERENCES `removallist_states`(`removallist_state_id`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `removallist_productboxes`;
CREATE TABLE IF NOT EXISTS `removallist_productboxes`
(
	`removallist` INT UNSIGNED NOT NULL,
	`productbox` INT UNSIGNED NOT NULL UNIQUE,
	
	FOREIGN KEY (`removallist`) REFERENCES `removallists`(`removallist_id`),
	FOREIGN KEY (`productbox`) REFERENCES `containers`(`container_id`),
	PRIMARY KEY (`removallist`,`productbox`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `manifest_states`;
CREATE TABLE IF NOT EXISTS `manifest_states`
(
	`manifest_state_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`name` VARCHAR(128) NOT NULL
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `manifests`;
CREATE TABLE IF NOT EXISTS `manifests`
(
	`manifest_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`state` INT UNSIGNED NOT NULL,
	`driver_id` INT UNSIGNED NOT NULL,
	`date_ordered` DATE NOT NULL,
	`date_received` DATE NOT NULL,
	
	FOREIGN KEY (`state`) REFERENCES `manifest_states`(`manifest_state_id`),
	CHECK (`date_received`>=`date_ordered`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `manifest_productboxes`;
CREATE TABLE IF NOT EXISTS `manifest_productboxes`
(
	`manifest` INT UNSIGNED NOT NULL,
	`productbox` INT UNSIGNED NOT NULL UNIQUE,
	
	FOREIGN KEY (`manifest`) REFERENCES `manifests`(`manifest_id`),
	FOREIGN KEY (`productbox`) REFERENCES `containers`(`container_id`),
	PRIMARY KEY (`manifest`,`productbox`)
) DEFAULT CHARSET=utf8;


/* ---- EXAMPLE DATA ---- */

INSERT INTO `roles` SET `name`='Administrator';
INSERT INTO `roles` SET `name`='Manager';
INSERT INTO `roles` SET `name`='Logistician';

INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES ('111111','Admin','Test',1);
INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES ('222222','Boss','Test',2);
INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES ('333333','Worker','Test',3);
INSERT INTO `users`(`badge_id`,`first_name`,`last_name`,`role`) VALUES ('12345678','Badger','Testaccount',3);

/* Test Products */
INSERT INTO `brands` SET `name`='Test Brand #1';
INSERT INTO `brands` SET `name`='Test Brand #2';
INSERT INTO `brands` SET `name`='Test Brand #3';
INSERT INTO `brands` SET `name`='Empty Brand';
INSERT INTO `types` SET `name`='Regular';
INSERT INTO `types` SET `name`='Raw';
INSERT INTO `types` SET `name`='Frozen';
INSERT INTO `categories` SET `name`='Regular Stuff', type=1;
INSERT INTO `categories` SET `name`='Raw Food', type=2;
INSERT INTO `categories` SET `name`='Frozen Things', type=3;
INSERT INTO `categories` SET `name`='Weird Stuff', type=1;
INSERT INTO `products` SET `name`='Test Product #1', brand=1, category=1;
INSERT INTO `products` SET `name`='Lonely Product #2', brand=1, category=1;
INSERT INTO `products` SET `name`='Test Product #3', brand=1, category=2;
INSERT INTO `products` SET `name`='Test Product #4', brand=1, category=2;
INSERT INTO `products` SET `name`='Test Product #5', brand=1, category=2;
INSERT INTO `products` SET `name`='Test Product #6', brand=2, category=1;
INSERT INTO `products` SET `name`='Test Product #7', brand=2, category=1;
INSERT INTO `products` SET `name`='Test Product #8', brand=2, category=1;
INSERT INTO `products` SET `name`='Test Product #9', brand=3, category=1;
INSERT INTO `products` SET `name`='Test Product #10', brand=3, category=1;
INSERT INTO `products` SET `name`='Lonely Product #11', brand=2, category=4;
INSERT INTO `products` SET `name`='Lonely Product #12', brand=2, category=4;

/* 1 - Tiny Full Shelf: 1-4*/
INSERT INTO `containers` SET `product`=9, `max_size`=10, `product_count`=10, `expiration_date`='2016-02-18';
INSERT INTO `containers` SET `product`=1, `max_size`=10, `product_count`=10, `expiration_date`='2016-10-30';
INSERT INTO `containers` SET `product`=1, `max_size`=10, `product_count`=10;
INSERT INTO `containers` SET `product`=3, `max_size`=10, `product_count`=10;
INSERT INTO `shelves` SET `max_levels`=1, `max_shelfslots_per_level`=4, `max_productboxes_per_shelfslot`=1;
INSERT INTO `shelf_productboxes` SET `shelf`=1, `productbox`=1, `shelflevel_index`=0, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=1, `productbox`=2, `shelflevel_index`=0, `shelfslot_index`=1;
INSERT INTO `shelf_productboxes` SET `shelf`=1, `productbox`=3, `shelflevel_index`=0, `shelfslot_index`=2;
INSERT INTO `shelf_productboxes` SET `shelf`=1, `productbox`=4, `shelflevel_index`=0, `shelfslot_index`=3;

/* 2 - Slot Full Shelf: 5-9 */
INSERT INTO `containers` SET `product`=1, `max_size`=50, `product_count`=0;
INSERT INTO `containers` SET `product`=3, `max_size`=50, `product_count`=5, `expiration_date`='2005-12-12';
INSERT INTO `containers` SET `product`=6, `max_size`=50, `product_count`=10, `expiration_date`='2008-01-10';
INSERT INTO `containers` SET `product`=6, `max_size`=50, `product_count`=25;
INSERT INTO `containers` SET `product`=1, `max_size`=50, `product_count`=50, `expiration_date`='2020-05-26';
INSERT INTO `shelves` SET `max_levels`=2, `max_shelfslots_per_level`=20, `max_productboxes_per_shelfslot`=4;
INSERT INTO `shelf_productboxes` SET `shelf`=2, `productbox`=5, `shelflevel_index`=0, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=2, `productbox`=6, `shelflevel_index`=0, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=2, `productbox`=7, `shelflevel_index`=0, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=2, `productbox`=8, `shelflevel_index`=0, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=2, `productbox`=9, `shelflevel_index`=1, `shelfslot_index`=19;

/* 3 - One Slot Level: 10-13*/
INSERT INTO `containers` SET `product`=1, `max_size`=20, `product_count`=20;
INSERT INTO `containers` SET `product`=10, `max_size`=20, `product_count`=20, `expiration_date`='2015-03-20';
INSERT INTO `containers` SET `product`=10, `max_size`=30, `product_count`=20;
INSERT INTO `containers` SET `product`=8, `max_size`=30, `product_count`=20;
INSERT INTO `shelves` SET `max_levels`=3, `max_shelfslots_per_level`=1, `max_productboxes_per_shelfslot`=49;
INSERT INTO `shelf_productboxes` SET `shelf`=3, `productbox`=10, `shelflevel_index`=2, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=3, `productbox`=11, `shelflevel_index`=2, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=3, `productbox`=12, `shelflevel_index`=2, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=3, `productbox`=13, `shelflevel_index`=2, `shelfslot_index`=0;

/* 4 - Many Slot Level: 14-20*/
INSERT INTO `containers` SET `product`=1, `max_size`=10, `product_count`=5, `expiration_date`='2030-01-02';
INSERT INTO `containers` SET `product`=9, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET `product`=3, `max_size`=10, `product_count`=5, `expiration_date`='2012-04-18';
INSERT INTO `containers` SET `product`=4, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET `product`=5, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET `product`=6, `max_size`=10, `product_count`=5, `expiration_date`='2016-05-16';
INSERT INTO `containers` SET `product`=7, `max_size`=10, `product_count`=5;
INSERT INTO `shelves` SET `max_levels`=2, `max_shelfslots_per_level`=50, `max_productboxes_per_shelfslot`=1;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=14, `shelflevel_index`=0, `shelfslot_index`=32;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=15, `shelflevel_index`=0, `shelfslot_index`=3;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=16, `shelflevel_index`=0, `shelfslot_index`=5;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=17, `shelflevel_index`=0, `shelfslot_index`=28;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=18, `shelflevel_index`=0, `shelfslot_index`=44;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=19, `shelflevel_index`=0, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=20, `shelflevel_index`=0, `shelfslot_index`=49;

/* 5 - Empty Shelf 2-slot */
INSERT INTO `shelves` SET `max_levels`=1, `max_shelfslots_per_level`=2, `max_productboxes_per_shelfslot`=1;

/* Lonely Containers 21-23*/
INSERT INTO `containers` SET `product`=11, `max_size`=2, `product_count`=2;
INSERT INTO `containers` SET `product`=12, `max_size`=2, `product_count`=2;
INSERT INTO `containers` SET `product`=1, `max_size`=1, `product_count`=0, `expiration_date`='2003-11-30';

/* Containers 24-30 */
INSERT INTO `containers` SET `product`=10, `max_size`=15, `product_count`=10, `expiration_date`='2017-06-17';
INSERT INTO `containers` SET `product`=5, `max_size`=25, `product_count`=0, `expiration_date`='2018-06-20';
INSERT INTO `containers` SET `product`=3, `max_size`=200, `product_count`=120;
INSERT INTO `containers` SET `product`=12, `max_size`=200, `product_count`=96;
INSERT INTO `containers` SET `product`=1, `max_size`=200, `product_count`=2, `expiration_date`='2016-08-30';
INSERT INTO `containers` SET `product`=1, `max_size`=200, `product_count`=10, `expiration_date`='2016-08-11';
INSERT INTO `containers` SET `product`=4, `max_size`=5, `product_count`=0;

/* Removal Lists 1-5 */
INSERT INTO `removallist_states` SET `name`='Active';
INSERT INTO `removallist_states` SET `name`='Canceled';
INSERT INTO `removallist_states` SET `name`='Finished';

INSERT INTO `removallists` SET `liststate`=1;
INSERT INTO `removallist_productboxes` SET `removallist`=1, `productbox`=24;
INSERT INTO `removallist_productboxes` SET `removallist`=1, `productbox`=25;
INSERT INTO `removallist_productboxes` SET `removallist`=1, `productbox`=26;

INSERT INTO `removallists` SET `liststate`=2;
INSERT INTO `removallist_productboxes` SET `removallist`=2, `productbox`=27;
INSERT INTO `removallist_productboxes` SET `removallist`=2, `productbox`=28;

INSERT INTO `removallists` SET `liststate`=3;
INSERT INTO `removallist_productboxes` SET `removallist`=3, `productbox`=29;

INSERT INTO `removallists` SET `liststate`=3;
INSERT INTO `removallist_productboxes` SET `removallist`=4, `productbox`=30;

INSERT INTO `removallists` SET `liststate`=2;

/* Manifests */
INSERT INTO `manifest_states` SET `name`='Stored';
INSERT INTO `manifest_states` SET `name`='Accepted';
INSERT INTO `manifest_states` SET `name`='Received';
INSERT INTO `manifest_states` SET `name`='Rejected';
INSERT INTO `manifest_states` SET `name`='Discharged';

INSERT INTO `manifests` SET `driver_id`=1, `state`=3, `date_ordered`='2016-02-20', `date_received`='2016-02-29';
INSERT INTO `manifests` SET `driver_id`=2, `state`=1, `date_ordered`='2016-01-01', `date_received`='2016-01-01';
INSERT INTO `manifests` SET `driver_id`=3, `state`=2, `date_ordered`='2016-02-04', `date_received`='2016-02-10';
INSERT INTO `manifests` SET `driver_id`=4, `state`=4, `date_ordered`='2015-04-12', `date_received`='2015-07-18';

/* Containers 31-40 */
INSERT INTO `containers` SET `product`=4, `max_size`=1, `product_count`=1, `expiration_date`='2016-06-17';
INSERT INTO `containers` SET `product`=4, `max_size`=20, `product_count`=20, `expiration_date`='2020-06-01';
INSERT INTO `containers` SET `product`=5, `max_size`=200, `product_count`=200, `expiration_date`='2015-12-30';
INSERT INTO `containers` SET `product`=10, `max_size`=340, `product_count`=340, `expiration_date`='2016-03-22';
INSERT INTO `containers` SET `product`=8, `max_size`=60, `product_count`=60, `expiration_date`='2018-10-29';
INSERT INTO `containers` SET `product`=2, `max_size`=87, `product_count`=87, `expiration_date`='2019-03-15';
INSERT INTO `containers` SET `product`=2, `max_size`=99, `product_count`=99, `expiration_date`='2021-01-01';
INSERT INTO `containers` SET `product`=9, `max_size`=110, `product_count`=110;
INSERT INTO `containers` SET `product`=1, `max_size`=205, `product_count`=205;
INSERT INTO `containers` SET `product`=8, `max_size`=55, `product_count`=55;

INSERT INTO `manifest_productboxes` SET `manifest`=1, `productbox`=31;
INSERT INTO `manifest_productboxes` SET `manifest`=1, `productbox`=32;
INSERT INTO `manifest_productboxes` SET `manifest`=1, `productbox`=33;
INSERT INTO `manifest_productboxes` SET `manifest`=1, `productbox`=34;

INSERT INTO `manifest_productboxes` SET `manifest`=2, `productbox`=35;
INSERT INTO `manifest_productboxes` SET `manifest`=2, `productbox`=36;
INSERT INTO `manifest_productboxes` SET `manifest`=2, `productbox`=37;

INSERT INTO `manifest_productboxes` SET `manifest`=4, `productbox`=38;
INSERT INTO `manifest_productboxes` SET `manifest`=4, `productbox`=39;
