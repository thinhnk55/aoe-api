CREATE DATABASE aoefan CHARACTER SET utf8 COLLATE utf8_general_ci;
CREATE USER 'aoefan'@'localhost' IDENTIFIED BY 'NJ$!@8943kjalq';
GRANT ALL ON aoefan.* TO 'aoefan'@'localhost';
CREATE USER 'aoefan'@'%' IDENTIFIED BY 'NJ$!@8943kjalq';
GRANT ALL ON aoefan.* TO 'aoefan'@'%';