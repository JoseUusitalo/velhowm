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
	CONSTRAINT `CONST_unique_pin_login` UNIQUE (`pin`, `first_name`, `last_name`),
	CONSTRAINT `CONST_unique_badge_login` UNIQUE (`badge_id`, `first_name`, `last_name`),
	CONSTRAINT `CONST_unique_name_role` UNIQUE (`first_name`, `last_name`, `role`)
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
	`type` INT NOT NULL,

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
	CONSTRAINT `CONST_unique_products` UNIQUE (`name`, `brand`, `category`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `containers`;
CREATE TABLE IF NOT EXISTS `containers`
(
	`container_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`expiration_date` DATE NULL,
	`product` INT UNSIGNED NOT NULL,
	`max_size` INT UNSIGNED NOT NULL,
	`product_count` INT UNSIGNED NOT NULL DEFAULT 0,

	FOREIGN KEY (`product`) REFERENCES `products`(`product_id`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `freezer_products`;
CREATE TABLE IF NOT EXISTS `freezer_products`
(
	`freezer` INT UNSIGNED NOT NULL,
	`product` INT UNSIGNED NOT NULL,

	FOREIGN KEY (`freezer`) REFERENCES `containers`(`container_id`),
	FOREIGN KEY (`product`) REFERENCES `products`(`product_id`),
	PRIMARY KEY (`freezer`, `product`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shelves`;
CREATE TABLE IF NOT EXISTS `shelves`
(
	`shelf_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`levels` INT UNSIGNED NOT NULL
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shelflevels`;
CREATE TABLE IF NOT EXISTS `shelflevels`
(
	`level_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`shelf_position` INT UNSIGNED NOT NULL,
	`max_shelfslots` INT UNSIGNED NOT NULL
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shelfslots`;
CREATE TABLE IF NOT EXISTS `shelfslots`
(
	`slot_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`level_position` INT UNSIGNED NOT NULL,
	`max_productboxes` INT UNSIGNED NOT NULL,
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shelf_shelflevels`;
CREATE TABLE IF NOT EXISTS `shelf_shelflevels`
(
	`shelf` INT UNSIGNED NOT NULL,
	`shelflevel` INT UNSIGNED NOT NULL,

	PRIMARY KEY (`shelf`, `shelflevel`),
	FOREIGN KEY (`shelf`) REFERENCES shelves(`shelf_id`),
	FOREIGN KEY (`shelflevel`) REFERENCES shelflevels(`level_id`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shelflevel_shelfslots`;
CREATE TABLE IF NOT EXISTS `shelflevel_shelfslots`
(
	`shelflevel` INT UNSIGNED NOT NULL,
	`shelfslot` INT UNSIGNED NOT NULL,

	PRIMARY KEY (`shelflevel`, `shelfslot`),
	FOREIGN KEY (`shelflevel`) REFERENCES shelflevels(`level_id`),
	FOREIGN KEY (`shelfslot`) REFERENCES shelfslots(`slot_id`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shelfslot_productboxes`;
CREATE TABLE IF NOT EXISTS `shelfslot_productboxes`
(
	`shelfslot` INT UNSIGNED NOT NULL,
	`productbox` INT UNSIGNED NOT NULL UNIQUE,

	PRIMARY KEY (`shelfslot`, `productbox`),
	FOREIGN KEY (`shelfslot`) REFERENCES shelfslots(`slot_id`),
	FOREIGN KEY (`productbox`) REFERENCES containers(`container_id`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `removallist_states`;
CREATE TABLE IF NOT EXISTS `removallist_states`
(
	`removallist_state_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`name` VARCHAR(128) UNIQUE NOT NULL
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
	PRIMARY KEY (`removallist`, `productbox`)
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
	`driver_id` INT UNSIGNED,
	`date_ordered` DATE,
	`date_received` DATE,

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
	PRIMARY KEY (`manifest`, `productbox`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `removalplatforms`;
CREATE TABLE IF NOT EXISTS `removalplatforms`
(
	`platform_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`free_space` DOUBLE NOT NULL DEFAULT 1.0,
	`free_space_warning` DOUBLE NOT NULL DEFAULT 0.10,
) DEFAULT CHARSET=utf8;

/* ---- EXAMPLE DATA ---- */

INSERT INTO `roles` SET `name`='Administrator';
INSERT INTO `roles` SET `name`='Manager';
INSERT INTO `roles` SET `name`='Logistician';

INSERT INTO `users`(`pin`, `first_name`, `last_name`, `role`) VALUES ('111111','Admin','Test',1);
INSERT INTO `users`(`pin`, `first_name`, `last_name`, `role`) VALUES ('222222','Boss','Test',2);
INSERT INTO `users`(`pin`, `first_name`, `last_name`, `role`) VALUES ('333333','Worker','Test',3);
INSERT INTO `users`(`badge_id`, `first_name`, `last_name`, `role`) VALUES ('12345678','Badger','Testaccount',3);

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

/*		1 - Tiny Full Shelf: 1-4		*/

INSERT INTO `containers` SET `product`=9, `max_size`=10, `product_count`=10, `expiration_date`='2016-02-18';
INSERT INTO `containers` SET `product`=1, `max_size`=10, `product_count`=10, `expiration_date`='2016-10-30';
INSERT INTO `containers` SET `product`=1, `max_size`=10, `product_count`=10;
INSERT INTO `containers` SET `product`=3, `max_size`=10, `product_count`=10;

INSERT INTO `shelves` SET `shelf_id`=1, `levels`=1;

INSERT INTO `shelflevels` SET `level_id`=1, `shelf_position`=1, `max_shelfslots`=4;

INSERT INTO `shelfslots` SET `slot_id`=1, `level_position`=1, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=2, `level_position`=2, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=3, `level_position`=3, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=4, `level_position`=4, `max_productboxes`=1;

INSERT INTO `shelf_shelflevels` SET `shelf`=1, `shelflevel`=1;

INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=1, `shelfslot`=1;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=1, `shelfslot`=2;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=1, `shelfslot`=3;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=1, `shelfslot`=4;

INSERT INTO `shelfslot_productboxes` SET `shelfslot`=1, `productbox`=1;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=2, `productbox`=2;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=3, `productbox`=3;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=4, `productbox`=4;

/*		2 - Slot Full Shelf: 5-9		*/

INSERT INTO `containers` SET `product`=1, `max_size`=50, `product_count`=0;
INSERT INTO `containers` SET `product`=3, `max_size`=50, `product_count`=5, `expiration_date`='2005-12-12';
INSERT INTO `containers` SET `product`=6, `max_size`=50, `product_count`=10, `expiration_date`='2008-01-10';
INSERT INTO `containers` SET `product`=6, `max_size`=50, `product_count`=25;
INSERT INTO `containers` SET `product`=1, `max_size`=50, `product_count`=50, `expiration_date`='2020-05-26';

INSERT INTO `shelves` SET `shelf_id`=2, `levels`=2;

INSERT INTO `shelflevels` SET `level_id`=2, `shelf_position`=1, `max_shelfslots`=20;
INSERT INTO `shelflevels` SET `level_id`=3, `shelf_position`=2, `max_shelfslots`=20;

INSERT INTO `shelfslots` SET `slot_id`=5, `level_position`=1, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=6, `level_position`=2, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=7, `level_position`=3, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=8, `level_position`=4, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=9, `level_position`=5, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=10, `level_position`=6, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=11, `level_position`=7, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=12, `level_position`=8, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=13, `level_position`=9, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=14, `level_position`=10, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=15, `level_position`=11, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=16, `level_position`=12, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=17, `level_position`=13, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=18, `level_position`=14, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=19, `level_position`=15, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=20, `level_position`=16, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=21, `level_position`=17, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=22, `level_position`=18, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=23, `level_position`=19, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=24, `level_position`=20, `max_productboxes`=4;

INSERT INTO `shelfslots` SET `slot_id`=25, `level_position`=1, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=26, `level_position`=2, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=27, `level_position`=3, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=28, `level_position`=4, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=29, `level_position`=5, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=30, `level_position`=6, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=31, `level_position`=7, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=32, `level_position`=8, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=33, `level_position`=9, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=34, `level_position`=10, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=35, `level_position`=11, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=36, `level_position`=12, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=37, `level_position`=13, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=38, `level_position`=14, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=39, `level_position`=15, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=40, `level_position`=16, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=41, `level_position`=17, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=42, `level_position`=18, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=43, `level_position`=19, `max_productboxes`=4;
INSERT INTO `shelfslots` SET `slot_id`=44, `level_position`=20, `max_productboxes`=4;

INSERT INTO `shelf_shelflevels` SET `shelf`=2, `shelflevel`=2;
INSERT INTO `shelf_shelflevels` SET `shelf`=2, `shelflevel`=3;

INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=5;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=6;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=7;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=8;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=9;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=10;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=11;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=12;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=13;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=14;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=15;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=16;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=17;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=18;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=19;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=20;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=21;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=22;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=23;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=2, `shelfslot`=24;

INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=25;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=26;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=27;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=28;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=29;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=30;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=31;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=32;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=33;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=34;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=35;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=36;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=37;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=38;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=39;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=40;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=41;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=42;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=43;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=3, `shelfslot`=44;

INSERT INTO `shelfslot_productboxes` SET `shelfslot`=5, `productbox`=5;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=5, `productbox`=6;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=5, `productbox`=7;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=5, `productbox`=8;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=44, `productbox`=9;

/*		3 - One Slot Level: 10-13		*/

INSERT INTO `containers` SET `product`=1, `max_size`=20, `product_count`=20;
INSERT INTO `containers` SET `product`=10, `max_size`=20, `product_count`=20, `expiration_date`='2015-03-20';
INSERT INTO `containers` SET `product`=10, `max_size`=30, `product_count`=20;
INSERT INTO `containers` SET `product`=8, `max_size`=30, `product_count`=20;

INSERT INTO `shelves` SET `shelf_id`=3, `levels`=3;

INSERT INTO `shelflevels` SET `level_id`=4, `shelf_position`=1, `max_shelfslots`=1;
INSERT INTO `shelflevels` SET `level_id`=5, `shelf_position`=2, `max_shelfslots`=1;
INSERT INTO `shelflevels` SET `level_id`=6, `shelf_position`=3, `max_shelfslots`=1;

INSERT INTO `shelfslots` SET `slot_id`=45, `level_position`=1, `max_productboxes`=50;
INSERT INTO `shelfslots` SET `slot_id`=46, `level_position`=1, `max_productboxes`=50;
INSERT INTO `shelfslots` SET `slot_id`=47, `level_position`=1, `max_productboxes`=50;

INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=4, `shelfslot`=45;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=5, `shelfslot`=46;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=6, `shelfslot`=47;

INSERT INTO `shelfslot_productboxes` SET `shelfslot`=46, `productbox`=10;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=46, `productbox`=11;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=46, `productbox`=12;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=46, `productbox`=13;

/*		4 - Many Slot Level: 14-20		*/

INSERT INTO `containers` SET `product`=1, `max_size`=10, `product_count`=5, `expiration_date`='2030-01-02';
INSERT INTO `containers` SET `product`=9, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET `product`=3, `max_size`=10, `product_count`=5, `expiration_date`='2012-04-18';
INSERT INTO `containers` SET `product`=4, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET `product`=5, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET `product`=6, `max_size`=10, `product_count`=5, `expiration_date`='2016-05-16';
INSERT INTO `containers` SET `product`=7, `max_size`=10, `product_count`=5;

INSERT INTO `shelves` SET `shelf_id`=4, `levels`=2;

INSERT INTO `shelflevels` SET `level_id`=7, `shelf_position`=1, `max_shelfslots`=50;
INSERT INTO `shelflevels` SET `level_id`=8, `shelf_position`=2, `max_shelfslots`=50;

INSERT INTO `shelfslots` SET `slot_id`=48, `level_position`=1, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=49, `level_position`=2, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=50, `level_position`=3, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=51, `level_position`=4, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=52, `level_position`=5, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=53, `level_position`=6, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=54, `level_position`=7, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=55, `level_position`=8, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=56, `level_position`=9, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=57, `level_position`=10, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=58, `level_position`=11, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=59, `level_position`=12, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=60, `level_position`=13, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=61, `level_position`=14, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=62, `level_position`=15, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=63, `level_position`=16, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=64, `level_position`=17, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=65, `level_position`=18, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=66, `level_position`=19, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=67, `level_position`=20, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=68, `level_position`=21, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=69, `level_position`=22, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=70, `level_position`=23, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=71, `level_position`=24, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=72, `level_position`=25, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=73, `level_position`=26, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=74, `level_position`=27, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=75, `level_position`=28, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=76, `level_position`=29, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=77, `level_position`=30, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=78, `level_position`=31, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=79, `level_position`=32, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=80, `level_position`=33, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=81, `level_position`=34, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=82, `level_position`=35, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=83, `level_position`=36, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=84, `level_position`=37, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=85, `level_position`=38, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=86, `level_position`=39, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=87, `level_position`=40, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=88, `level_position`=41, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=89, `level_position`=42, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=90, `level_position`=43, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=91, `level_position`=44, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=92, `level_position`=45, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=93, `level_position`=46, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=94, `level_position`=47, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=95, `level_position`=48, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=96, `level_position`=49, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=97, `level_position`=50, `max_productboxes`=1;

INSERT INTO `shelfslots` SET `slot_id`=98, `level_position`=1, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=99, `level_position`=2, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=100, `level_position`=3, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=101, `level_position`=4, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=102, `level_position`=5, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=103, `level_position`=6, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=104, `level_position`=7, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=105, `level_position`=8, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=106, `level_position`=9, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=107, `level_position`=10, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=108, `level_position`=11, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=109, `level_position`=12, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=110, `level_position`=13, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=111, `level_position`=14, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=112, `level_position`=15, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=113, `level_position`=16, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=114, `level_position`=17, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=115, `level_position`=18, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=116, `level_position`=19, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=117, `level_position`=20, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=118, `level_position`=21, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=119, `level_position`=22, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=120, `level_position`=23, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=121, `level_position`=24, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=122, `level_position`=25, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=123, `level_position`=26, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=124, `level_position`=27, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=125, `level_position`=28, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=126, `level_position`=29, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=127, `level_position`=30, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=128, `level_position`=31, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=129, `level_position`=32, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=130, `level_position`=33, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=131, `level_position`=34, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=132, `level_position`=35, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=133, `level_position`=36, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=134, `level_position`=37, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=135, `level_position`=38, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=136, `level_position`=39, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=137, `level_position`=40, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=138, `level_position`=41, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=139, `level_position`=42, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=140, `level_position`=43, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=141, `level_position`=44, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=142, `level_position`=45, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=143, `level_position`=46, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=144, `level_position`=47, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=145, `level_position`=48, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=146, `level_position`=49, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=147, `level_position`=50, `max_productboxes`=1;

INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=48;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=49;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=50;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=51;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=52;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=53;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=54;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=55;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=56;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=57;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=58;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=59;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=60;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=61;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=62;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=63;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=64;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=65;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=66;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=67;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=68;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=69;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=70;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=71;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=72;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=73;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=74;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=75;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=76;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=77;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=78;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=79;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=80;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=81;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=82;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=83;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=84;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=85;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=86;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=87;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=88;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=89;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=90;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=91;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=92;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=93;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=94;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=95;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=96;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=7, `shelfslot`=97;

INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=98;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=99;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=100;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=101;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=102;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=103;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=104;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=105;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=106;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=107;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=108;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=109;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=110;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=111;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=112;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=113;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=114;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=115;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=116;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=117;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=118;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=119;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=120;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=121;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=122;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=123;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=124;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=125;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=126;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=127;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=128;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=129;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=130;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=131;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=132;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=133;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=134;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=135;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=136;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=137;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=138;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=139;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=140;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=141;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=142;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=143;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=144;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=145;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=146;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=8, `shelfslot`=147;

INSERT INTO `shelfslot_productboxes` SET `shelfslot`=147, `productbox`=14;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=100, `productbox`=15;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=101, `productbox`=16;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=105, `productbox`=17;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=110, `productbox`=18;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=130, `productbox`=19;
INSERT INTO `shelfslot_productboxes` SET `shelfslot`=132, `productbox`=20;

/* 5 - Empty Shelf 2-slot */
INSERT INTO `shelves` SET `shelf_id`=5, `levels`=1;

INSERT INTO `shelflevels` SET `level_id`=9, `shelf_position`=1, `max_shelfslots`=2;
INSERT INTO `shelflevels` SET `level_id`=10, `shelf_position`=2, `max_shelfslots`=2;

INSERT INTO `shelfslots` SET `slot_id`=148, `level_position`=1, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=149, `level_position`=2, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=150, `level_position`=1, `max_productboxes`=1;
INSERT INTO `shelfslots` SET `slot_id`=151, `level_position`=2, `max_productboxes`=1;

INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=9, `shelfslot`=148;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=9, `shelfslot`=149;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=10, `shelfslot`=150;
INSERT INTO `shelflevel_shelfslots` SET `shelflevel`=10, `shelfslot`=151;

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

/* The removal platform, only one is supported for now. */
INSERT INTO `removalplatforms` SET `platform_id`=1;
