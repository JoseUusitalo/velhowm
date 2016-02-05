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
	`product_count` INT NOT NULL DEFAULT 0,
	
	FOREIGN KEY (`product`) REFERENCES products(`product_id`),
	CHECK (`max_size`>0)
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `container_products`;
CREATE TABLE IF NOT EXISTS `container_products`
(
	`container` INT UNSIGNED NOT NULL,
	`product` INT UNSIGNED NOT NULL,
	
	FOREIGN KEY (`container`) REFERENCES containers(`container_id`),
	FOREIGN KEY (`product`) REFERENCES products(`product_id`),
	PRIMARY KEY (`container`,`product`)
) DEFAULT CHARSET=utf8;

INSERT INTO `roles` SET `name`='Administrator';
INSERT INTO `roles` SET `name`='Manager';
INSERT INTO `roles` SET `name`='Logistician';

INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES ('111111','Admin','Test',1);
INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES ('222222','Boss','Test',2);
INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES ('333333','Worker','Test',3);
INSERT INTO `users`(`badge_id`,`first_name`,`last_name`,`role`) VALUES ('12345678','Badger','Testaccount',3);
