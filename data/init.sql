DROP TABLE IF EXISTS `roles`;
CREATE TABLE IF NOT EXISTS `roles`
(
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`name` VARCHAR(128) NOT NULL UNIQUE
) DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users`
(
	`id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`badge_id` INT UNSIGNED UNIQUE,
	`pin` INT(6) UNSIGNED,
	`first_name` VARCHAR(128) NOT NULL,
	`last_name` VARCHAR(128) NOT NULL,
	`role` INT UNSIGNED,
	
	FOREIGN KEY (`role`) REFERENCES roles(`id`)
) DEFAULT CHARSET=utf8;

INSERT INTO `roles` VALUES (1,'Administrator');
INSERT INTO `roles` VALUES (2,'Manager');
INSERT INTO `roles` VALUES (3,'Logistician');

INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES (111111,'Admin','Test',1);
INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES (222222,'Boss','Test',2);
INSERT INTO `users`(`pin`,`first_name`,`last_name`,`role`) VALUES (333333,'Worker','Test',3);