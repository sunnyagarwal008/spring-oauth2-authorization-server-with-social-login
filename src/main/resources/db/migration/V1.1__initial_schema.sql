SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

CREATE SCHEMA IF NOT EXISTS `identity` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

CREATE TABLE IF NOT EXISTS `identity`.`user` (
    `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(256) NOT NULL,
    `password` VARCHAR(256) NOT NULL,
    `enabled` TINYINT(1) NOT NULL DEFAULT 1,
    `last_login_time` DATETIME,
    `password_change_date` DATETIME,
    `failed_login_attempts` INT(10) NOT NULL DEFAULT 0,
    `last_accessed_from` VARCHAR(256),
    `created` DATETIME NOT NULL,
    `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `email_UNIQUE` (`email` ASC))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `identity`.`role` (
    `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(45) NOT NULL,
    `created` DATETIME NOT NULL,
    `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `name_UNIQUE` (`name` ASC))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `identity`.`user_role` (
    `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` INT(10) UNSIGNED NOT NULL,
    `role_id` INT(10) UNSIGNED NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `fk_user_id_idx` (`user_id` ASC),
    INDEX `fk_role_id_idx` (`role_id` ASC),
    CONSTRAINT `fk_user_id`
        FOREIGN KEY (`user_id`)
            REFERENCES `identity`.`user` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE,
    CONSTRAINT `fk_role_id`
        FOREIGN KEY (`role_id`)
            REFERENCES `identity`.`role` (`id`)
            ON DELETE CASCADE
            ON UPDATE CASCADE)
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8;

CREATE TABLE IF NOT EXISTS `identity`.`client` (
    `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
    `client_id` VARCHAR(256) NOT NULL,
    `secret` VARCHAR(256) NOT NULL,
    `authorization_grant_types` VARCHAR(256) NOT NULL,
    `scopes` VARCHAR(256) NOT NULL,
    `auto_approved_scopes` VARCHAR(256) NULL,
    `redirect_uris` VARCHAR(256) NOT NULL,
    `created` DATETIME NOT NULL,
    `updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `client_id_UNIQUE` (`client_id` ASC))
    ENGINE = InnoDB
    DEFAULT CHARACTER SET = utf8;

create table if not exists oauth_access_token (
    token_id VARCHAR(255),
    token LONG VARBINARY,
    authentication_id VARCHAR(255) PRIMARY KEY,
    user_name VARCHAR(255),
    client_id VARCHAR(255),
    authentication LONG VARBINARY,
    refresh_token VARCHAR(255)
);

create table if not exists oauth_refresh_token (
    token_id VARCHAR(255),
    token LONG VARBINARY,
    authentication LONG VARBINARY
);

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
