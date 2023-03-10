DROP DATABASE IF EXISTS beerorder;

DROP USER IF EXISTS `beer_order`@`%`;

CREATE DATABASE IF NOT EXISTS beerorder CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS `beer_order`@`%` IDENTIFIED WITH mysql_native_password BY 'password';

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, EXECUTE, CREATE VIEW, SHOW VIEW,
CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON `beerorder`.* TO `beer_order`@`%`;

FLUSH PRIVILEGES;