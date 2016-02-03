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
	`badge_id` CHAR(8) UNSIGNED UNIQUE,
	`pin` CHAR(6) UNSIGNED,
	`first_name` VARCHAR(128) NOT NULL,
	`last_name` VARCHAR(128) NOT NULL,
	`role` INT UNSIGNED,
	
	FOREIGN KEY (`role`) REFERENCES roles(`role_id`),
	CONSTRAINT `C_unique_login_details` UNIQUE (`badge_id`,`pin`,`first_name`,`last_name`)
) DEFAULT CHARSET=utf8;

INSERT INTO `roles` VALUES (1,'Administrator');
INSERT INTO `roles` VALUES (2,'Manager');
INSERT INTO `roles` VALUES (3,'Logistician');

INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES ('111111','Admin','Test',1);
INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES ('222222','Boss','Test',2);
INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES ('333333','Worker','Test',3);
INSERT INTO `users`(`badge_id`,`first_name`,`last_name`,`role`) VALUES ('12345678','Badger','Testaccount',3);