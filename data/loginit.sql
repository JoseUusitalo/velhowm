CREATE TABLE IF NOT EXISTS `syslogs`
(
	`time` TIMESTAMP NOT NULL,
	`logger` VARCHAR(1024) NOT NULL,
	`level` VARCHAR(64) NOT NULL,
	`message` TEXT NOT NULL,

	PRIMARY KEY (`time`,`logger`)
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `usrlogs`
(
	`user_id` INT UNSIGNED NOT NULL,
	`time` TIMESTAMP NOT NULL,
	`logger` VARCHAR(1024) NOT NULL,
	`level` VARCHAR(64) NOT NULL,
	`message` TEXT NOT NULL,

	PRIMARY KEY (`user_id`,`time`,`logger`)
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `dblogs`
(
	`time` TIMESTAMP NOT NULL,
	`logger` VARCHAR(1024) NOT NULL,
	`level` VARCHAR(64) NOT NULL,
	`message` TEXT NOT NULL,

	PRIMARY KEY (`time`,`logger`)
) DEFAULT CHARSET=utf8;