/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
USE cwssec;

--
-- Definition of table `cwssec`.`usr_audit`
--
DROP TABLE IF EXISTS `cwssec`.`usr_audit`;
CREATE TABLE `cwssec`.`usr_audit` (
    `usr_audit_sessionid` VARCHAR(100) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_userid` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_userguid` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_role` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_applid` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_applname` VARCHAR(128) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_timestamp` SIGNED BIGINT CHARACTER SET UTF8 NOT NULL,
    `usr_audit_action` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_srcaddr` VARCHAR(45) CHARACTER SET UTF8 NOT NULL,
    `usr_audit_srchost` VARCHAR(100) CHARACTER SET UTF8 NOT NULL,
    FULLTEXT KEY `audit_search` (`usr_audit_userid`, `usr_audit_userguid`, `usr_audit_role`, `usr_audit_srcaddr`, `usr_audit_srchost`, `usr_audit_action`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `cwssec`.`usr_audit`
--
/*!40000 ALTER TABLE `cwssec`.`usr_audit` DISABLE KEYS */;
/*!40000 ALTER TABLE `cwssec`.`usr_audit` ENABLE KEYS */;
COMMIT;

--
-- Definition of procedure `cwssec`.`getAuditEntryByAttribute`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`getAuditEntryByAttribute`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`getAuditEntryByAttribute`(
    IN attributeName VARCHAR(100)
)
BEGIN
    SELECT
        usr_audit_sessionid,
        usr_audit_userid,
        usr_audit_userguid,
        usr_audit_role,
        usr_audit_applid,
        usr_audit_applname,
        usr_audit_timestamp,
        usr_audit_action,
        usr_audit_srcaddr,
        usr_audit_srchost,
    MATCH (`usr_audit_userid`, `usr_audit_userguid`, `usr_audit_role`, `usr_audit_srcaddr`, `usr_audit_srchost`, `usr_audit_action`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `cwssec`.`usr_audit`
    WHERE MATCH (`usr_audit_userid`, `usr_audit_userguid`, `usr_audit_role`, `usr_audit_srcaddr`, `usr_audit_srchost`, `usr_audit_action`)
    AGAINST (+attributeName IN BOOLEAN MODE);
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `getAuditCount`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`getAuditCount`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`getAuditCount`(
    IN userguid VARCHAR(45)
)
BEGIN
    SELECT COUNT(*)
    FROM usr_audit
    WHERE usr_audit_userguid = userguid;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `getAuditInterval`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`getAuditInterval`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`getAuditInterval`(
    IN userguid VARCHAR(45),
    IN startRow INT
)
BEGIN
    SELECT
        usr_audit_sessionid,
        usr_audit_userid,
        usr_audit_userguid,
        usr_audit_role,
        usr_audit_applid,
        usr_audit_applname,
        usr_audit_timestamp,
        usr_audit_action,
        usr_audit_srcaddr,
        usr_audit_srchost
    FROM usr_audit
    WHERE usr_audit_userguid = userguid
    ORDER BY usr_audit_timestamp DESC
    LIMIT startRow, 20;
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

--
-- Definition of procedure `insertAuditEntry`
--
DELIMITER $$
DROP PROCEDURE IF EXISTS `cwssec`.`insertAuditEntry`$$
/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ $$
CREATE DEFINER=`appuser`@`localhost` PROCEDURE `cwssec`.`insertAuditEntry`(
    IN usersessid VARCHAR(100),
    IN username VARCHAR(45),
    IN userguid VARCHAR(45),
    IN userrole VARCHAR(45),
    IN applid VARCHAR(128),
    IN applname VARCHAR(128),
    IN useraction VARCHAR(45),
    IN srcaddr VARCHAR(45),
	IN srchost VARCHAR(128)
)
BEGIN
    INSERT INTO usr_audit (usr_audit_sessionid, usr_audit_userid, usr_audit_userguid, usr_audit_role, usr_audit_applid, usr_audit_applname, usr_audit_timestamp, usr_audit_action, usr_audit_srcaddr, usr_audit_srchost)
    VALUES (usersessid, username, userguid, userrole, applid, applname, UNIX_TIMESTAMP(NOW()), useraction, srcaddr, srchost);
END $$
/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */  $$

DELIMITER ;
COMMIT;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

COMMIT;
