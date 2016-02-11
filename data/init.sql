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
	
	FOREIGN KEY (`role`) REFERENCES roles(`role_id`),
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
	
	FOREIGN KEY (`type`) REFERENCES types(`type_id`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `products`;
CREATE TABLE IF NOT EXISTS `products`
(
	`product_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`name` VARCHAR(128) NOT NULL,
	`expiration_date` DATE NULL,
	`brand` VARCHAR(128) NOT NULL,
	`category` VARCHAR(128) NOT NULL,
	`popularity` INT DEFAULT -1,
	
	FOREIGN KEY (`brand`) REFERENCES brands(`brand_id`),
	FOREIGN KEY (`category`) REFERENCES categories(`category_id`),
	CONSTRAINT `CONST_unique_products` UNIQUE (`expiration_date`,`name`,`brand`,`category`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `containers`;
CREATE TABLE IF NOT EXISTS `containers`
(
	`container_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`product` INT UNSIGNED NOT NULL,
	`max_size` INT NOT NULL,
	`product_count` INT UNSIGNED NOT NULL DEFAULT 0,
	
	FOREIGN KEY (`product`) REFERENCES products(`product_id`),
	CHECK (`max_size`>0)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `freezer_products`;
CREATE TABLE IF NOT EXISTS `freezer_products`
(
	`freezer` INT UNSIGNED NOT NULL,
	`product` INT UNSIGNED NOT NULL,
	
	FOREIGN KEY (`freezer`) REFERENCES containers(`container_id`),
	FOREIGN KEY (`product`) REFERENCES products(`product_id`),
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
	`productbox` INT UNSIGNED NOT NULL,
	`shelflevel_index` INT UNSIGNED NOT NULL,
	`shelfslot_index` INT UNSIGNED NOT NULL,
	
	PRIMARY KEY (`shelf`, `productbox`),
	FOREIGN KEY (`shelf`) REFERENCES shelves(`shelf_id`),
	FOREIGN KEY (`productbox`) REFERENCES containers(`container_id`),
	CONSTRAINT `CONST_no_duplicate_boxes` UNIQUE (`shelf`,`productbox`,`shelflevel_index`,`shelfslot_index`)
) DEFAULT CHARSET=utf8;

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
INSERT INTO `products` SET `name`='Test Product #7', brand=2, category=2;
INSERT INTO `products` SET `name`='Test Product #8', brand=2, category=3;
INSERT INTO `products` SET `name`='Test Product #9', brand=3, category=3;
INSERT INTO `products` SET `name`='Test Product #10', brand=3, category=3;


/* Tiny Full Shelf: 1-4*/
INSERT INTO `containers` SET product=9, `max_size`=10, `product_count`=10;
INSERT INTO `containers` SET product=1, `max_size`=10, `product_count`=10;
INSERT INTO `containers` SET product=1, `max_size`=10, `product_count`=10;
INSERT INTO `containers` SET product=3, `max_size`=10, `product_count`=10;
INSERT INTO `shelves` SET `max_levels`=1, `max_shelfslots_per_level`=4, `max_productboxes_per_shelfslot`=1;
INSERT INTO `shelf_productboxes` SET `shelf`=1, `productbox`=1, `shelflevel_index`=1, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=1, `productbox`=2, `shelflevel_index`=1, `shelfslot_index`=1;
INSERT INTO `shelf_productboxes` SET `shelf`=1, `productbox`=3, `shelflevel_index`=1, `shelfslot_index`=2;
INSERT INTO `shelf_productboxes` SET `shelf`=1, `productbox`=4, `shelflevel_index`=1, `shelfslot_index`=3;

/* Slot Full Shelf: 5-9 */
INSERT INTO `containers` SET product=1, `max_size`=50, `product_count`=0;
INSERT INTO `containers` SET product=3, `max_size`=50, `product_count`=5;
INSERT INTO `containers` SET product=6, `max_size`=50, `product_count`=10;
INSERT INTO `containers` SET product=6, `max_size`=50, `product_count`=25;
INSERT INTO `containers` SET product=1, `max_size`=50, `product_count`=50;
INSERT INTO `shelves` SET `max_levels`=2, `max_shelfslots_per_level`=20, `max_productboxes_per_shelfslot`=4;
INSERT INTO `shelf_productboxes` SET `shelf`=2, `productbox`=5, `shelflevel_index`=1, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=2, `productbox`=6, `shelflevel_index`=1, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=2, `productbox`=7, `shelflevel_index`=1, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=2, `productbox`=8, `shelflevel_index`=1, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=2, `productbox`=9, `shelflevel_index`=2, `shelfslot_index`=19;

/* One Slot Level: 10-13*/
INSERT INTO `containers` SET product=1, `max_size`=20, `product_count`=20;
INSERT INTO `containers` SET product=10, `max_size`=20, `product_count`=20;
INSERT INTO `containers` SET product=10, `max_size`=30, `product_count`=20;
INSERT INTO `containers` SET product=8, `max_size`=30, `product_count`=20;
INSERT INTO `shelves` SET `max_levels`=3, `max_shelfslots_per_level`=1, `max_productboxes_per_shelfslot`=49;
INSERT INTO `shelf_productboxes` SET `shelf`=3, `productbox`=10, `shelflevel_index`=3, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=3, `productbox`=11, `shelflevel_index`=3, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=3, `productbox`=12, `shelflevel_index`=3, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=3, `productbox`=13, `shelflevel_index`=3, `shelfslot_index`=0;

/* Many Slot Level: 14-20*/
INSERT INTO `containers` SET product=1, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET product=9, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET product=3, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET product=4, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET product=5, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET product=6, `max_size`=10, `product_count`=5;
INSERT INTO `containers` SET product=7, `max_size`=10, `product_count`=5;
INSERT INTO `shelves` SET `max_levels`=2, `max_shelfslots_per_level`=50, `max_productboxes_per_shelfslot`=1;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=14, `shelflevel_index`=1, `shelfslot_index`=32;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=15, `shelflevel_index`=1, `shelfslot_index`=3;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=16, `shelflevel_index`=1, `shelfslot_index`=5;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=17, `shelflevel_index`=1, `shelfslot_index`=28;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=18, `shelflevel_index`=1, `shelfslot_index`=44;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=19, `shelflevel_index`=1, `shelfslot_index`=0;
INSERT INTO `shelf_productboxes` SET `shelf`=4, `productbox`=20, `shelflevel_index`=1, `shelfslot_index`=49;

/*
DROP TABLE IF EXISTS `shelfslots`;
CREATE TABLE IF NOT EXISTS `shelfslots`
(
	`shelfslot_db_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`productboxes_max` INT UNSIGNED NOT NULL,
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shelflevels`;
CREATE TABLE IF NOT EXISTS `shelflevels`
(
	`shelflevel_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`shelfslots_max` INT UNSIGNED NOT NULL,
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shelf_shelflevels`;
CREATE TABLE IF NOT EXISTS `shelf_shelflevels`
(
	`shelf` INT UNSIGNED NOT NULL,
	`shelflevel` INT UNSIGNED NOT NULL,
	
	PRIMARY KEY (`shelf`, `shelflevel`),
	FOREIGN KEY (`shelf`) REFERENCES shelves(`shelf_id`),
	FOREIGN KEY (`shelflevel`) REFERENCES shelflevels(`shelflevel_id`)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shelflevel_shelfslots`;
CREATE TABLE IF NOT EXISTS `shelflevel_shelfslots`
(
	`shelflevel` INT UNSIGNED NOT NULL,
	`shelfslot` INT UNSIGNED NOT NULL,
	
	PRIMARY KEY (`shelflevel`, `shelfslot`),
	FOREIGN KEY (`shelflevel`) REFERENCES shelflevels(`shelflevel_id`),
	FOREIGN KEY (`shelfslot`) REFERENCES shelfslots(`shelfslot_db_id`)
) DEFAULT CHARSET=utf8;
*/