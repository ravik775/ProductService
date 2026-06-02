/* =========================================================
   LIST DATABASES
   ========================================================= */
SHOW DATABASES; SHOW SCHEMAS;

/* =========================================================
   CREATE AND USE DATABASE
   ========================================================= */
CREATE DATABASE IF NOT EXISTS productservicedb; USE productservicedb; SELECT DATABASE();

/* =========================================================
   LIST ALL MYSQL USERS
   ========================================================= */
SELECT user, host FROM mysql.user;

/* =========================================================
   CREATE LOCAL APPLICATION USER
   ========================================================= */
CREATE USER IF NOT EXISTS 'appuser'@'localhost' IDENTIFIED BY 'App@123';

/* =========================================================
   GRANT DATABASE PERMISSIONS TO LOCAL USER
   ========================================================= */
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, INDEX, REFERENCES ON productservicedb.* TO 'appuser'@'localhost';

/* =========================================================
   CREATE REMOTE APPLICATION USER
   ========================================================= */
CREATE USER IF NOT EXISTS 'appuser'@'%' IDENTIFIED BY 'App@123';

/* =========================================================
   GRANT DATABASE PERMISSIONS TO REMOTE USER
   ========================================================= */
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, DROP, INDEX, REFERENCES ON productservicedb.* TO 'appuser'@'%';

/* =========================================================
   APPLY PRIVILEGE CHANGES
   ========================================================= */
FLUSH PRIVILEGES;

/* =========================================================
   VERIFY USER GRANTS
   ========================================================= */
SHOW GRANTS FOR 'appuser'@'localhost'; SHOW GRANTS FOR 'appuser'@'%';

/* =========================================================
   SHOW CURRENT LOGGED-IN USER
   ========================================================= */
SELECT USER(); SELECT CURRENT_USER();

/* =========================================================
   REVOKE LOCAL USER PRIVILEGES
   ========================================================= */
REVOKE ALL PRIVILEGES ON productservicedb.* FROM 'appuser'@'localhost';

/* =========================================================
   REVOKE REMOTE USER PRIVILEGES
   ========================================================= */
REVOKE ALL PRIVILEGES ON productservicedb.* FROM 'appuser'@'%';

/* =========================================================
   APPLY REVOKE CHANGES
   ========================================================= */
FLUSH PRIVILEGES;

/* =========================================================
   VERIFY REVOKED GRANTS
   ========================================================= */
SHOW GRANTS FOR 'appuser'@'localhost'; SHOW GRANTS FOR 'appuser'@'%';

/* =========================================================
   DELETE USERS
   ========================================================= */
DROP USER IF EXISTS 'appuser'@'localhost'; DROP USER IF EXISTS 'appuser'@'%';

/* =========================================================
   DELETE ALL TABLES
   ========================================================= */
SELECT CONCAT('DROP TABLE IF EXISTS `', table_name, '`;') FROM information_schema.tables
WHERE table_schema = 'productservicedb';

/* =========================================================
   DELETE DATABASE
   ========================================================= */
DROP DATABASE IF EXISTS productservicedb;

/* =========================================================
   VERIFY CLEANUP
   ========================================================= */
SHOW DATABASES; SELECT user, host FROM mysql.user;