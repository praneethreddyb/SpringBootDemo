CREATE TABLE `user` (
    `pk_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NULL,
    `email` VARCHAR(160) NULL,
    `password` VARCHAR(60) NULL,
    `phone` VARCHAR(60) NULL,
    PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_current_logged_session` (
  `pk_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `os` varchar(100) DEFAULT NULL,
  `browser` text,
  `ip` varchar(30) DEFAULT NULL,
  `agent` text,
  `session` varchar(100) DEFAULT NULL,
  `token` varchar(1024) DEFAULT NULL,
  `device` varchar(100) DEFAULT NULL,
  `user_id` int(11) unsigned NOT NULL,
  `in_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fb_token` varchar(2000) DEFAULT NULL COMMENT 'firebase token',
  `expiry` timestamp NULL DEFAULT NULL,
  `last_accessed_on` timestamp NULL DEFAULT NULL,
  `active_time` int(11) DEFAULT NULL,
  `data` text,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `access_log` (
  `pk_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `url` varchar(1000) NOT NULL,
  `service` varchar(1000) NOT NULL,
  `method_type` varchar(1000) NOT NULL,
  `header` varchar(1000) NOT NULL,
  `req_data` longtext,
  `res_data` longtext,
  `agent` longtext,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `duration` int(11) DEFAULT NULL,
  `session` varchar(100) DEFAULT NULL,
  `token` varchar(1024) DEFAULT NULL,
  `user_id` int(11) unsigned DEFAULT NULL,
  `ip` varchar(40) NOT NULL,
  `version` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`pk_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

