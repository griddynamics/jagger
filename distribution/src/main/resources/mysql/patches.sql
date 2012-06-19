--  that file should be run manually for mysql database using.

CREATE USER 'jagger'@'localhost' IDENTIFIED BY 'rocks';
GRANT ALL PRIVILEGES ON *.* TO 'jagger'@'localhost' WITH GRANT OPTION;

CREATE USER 'jagger'@'%' IDENTIFIED BY 'rocks';
GRANT ALL PRIVILEGES ON *.* TO 'jagger'@'%' WITH GRANT OPTION;

CREATE DATABASE jaggerdb;
