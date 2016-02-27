CREATE TABLE IF NOT EXISTS `syslogs`
(
	`msg_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`time` TIMESTAMP NOT NULL,
	`logger` VARCHAR(1024) NOT NULL,
	`level` VARCHAR(64) NOT NULL,
	`message` TEXT NOT NULL
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `usrlogs`
(
	`msg_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`user_id` INT UNSIGNED NOT NULL,
	`time` TIMESTAMP NOT NULL,
	`logger` VARCHAR(1024) NOT NULL,
	`level` VARCHAR(64) NOT NULL,
	`message` TEXT NOT NULL
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `dblogs`
(
	`msg_id` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	`time` TIMESTAMP NOT NULL,
	`logger` VARCHAR(1024) NOT NULL,
	`level` VARCHAR(64) NOT NULL,
	`message` TEXT NOT NULL
) DEFAULT CHARSET=utf8;