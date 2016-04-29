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
