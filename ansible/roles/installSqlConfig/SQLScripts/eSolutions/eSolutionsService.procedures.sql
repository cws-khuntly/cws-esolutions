DELIMITER //

/*!50003 SET @TEMP_SQL_MODE=@@SQL_MODE, SQL_MODE='STRICT_TRANS_TABLES,NO_AUTO_CREATE_USER' */ //

DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getArticleByAttribute` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`insertRecord` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`retrTopArticles` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`retrArticle` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`addNewArticle` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`updateArticle` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`updateArticleStatus` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getArticleCount` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`retrPendingArticles` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getRecordByAttribute` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`insertApex` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`insertRecord` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getApplicationCount` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`listApplications` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getApplicationData` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`removeApplicationData` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`updateApplicationData` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`insertNewApplication` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getApplicationByAttribute` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getServerByAttribute` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`validateServerHostName` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`insertNewServer` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`updateServerData` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`retireServer` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getServerCount` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getServerList` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`retrServerData` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getRetiredServers` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getDataCenterByAttribute` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`addNewDatacenter` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`removeDataCenter` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getDatacenterCount` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`listDataCenters` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`retrDataCenter` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getMessagesByAttribute` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`submitSvcMessage` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`updateServiceMessage` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`retrServiceMessage` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`retrServiceMessages` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`retrAlertMessages` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getPlatformByAttribute` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`addNewPlatform` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`updatePlatformData` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`removePlatformData` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getPlatformData` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`getPlatformCount` //
DROP PROCEDURE IF EXISTS `ESOLUTIONSSVC`.`listPlatforms` //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getArticleByAttribute`(
    IN searchTerms VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        HITS,
        ID,
        CREATE_DATE,
        AUTHOR,
        KEYWORDS,
        TITLE,
        SYMPTOMS,
        CAUSE,
        RESOLUTION,
        STATUS,
        REVIEWED_BY,
        REVIEW_DATE,
        MODIFIED_DATE,
        MODIFIED_BY,
    MATCH (ID, KEYWORDS, TITLE, SYMPTOMS, CAUSE, RESOLUTION)
    AGAINST (+searchTerms WITH QUERY EXPANSION)
    FROM `ESOLUTIONSSVC`.`articles`
    WHERE MATCH (ID, KEYWORDS, TITLE, SYMPTOMS, CAUSE, RESOLUTION)
    AGAINST (+searchTerms IN BOOLEAN MODE)
    AND STATUS = 'APPROVED'
    LIMIT startRow, 20;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`retrTopArticles`(
)
BEGIN
    SELECT
        HITS,
        ID,
        CREATE_DATE,
        AUTHOR,
        KEYWORDS,
        TITLE,
        SYMPTOMS,
        CAUSE,
        RESOLUTION,
        STATUS,
        REVIEWED_BY,
        REVIEW_DATE,
        MODIFIED_DATE,
        MODIFIED_BY
    FROM `articles`
    WHERE HITS >= 10
    AND STATUS = 'APPROVED'
    LIMIT 15;
END //
COMMIT //

CREATE PROCEDURE `retrArticle`(
    IN articleId VARCHAR(100),
    IN isApproval BOOLEAN
)
BEGIN
    IF (isApproval)
    THEN
        SELECT
            HITS,
            ID,
            CREATE_DATE,
            AUTHOR,
            KEYWORDS,
            TITLE,
            SYMPTOMS,
            CAUSE,
            RESOLUTION,
            STATUS,
            REVIEWED_BY,
            REVIEW_DATE,
            MODIFIED_DATE,
            MODIFIED_BY
        FROM `articles`
        WHERE ID = articleId
        AND STATUS IN ('NEW', 'REVIEW');
    ELSE
        UPDATE `articles`
        SET HITS = HITS + 1
        WHERE ID = articleId;

        COMMIT;

        SELECT
            HITS,
            ID,
            CREATE_DATE,
            AUTHOR,
            KEYWORDS,
            TITLE,
            SYMPTOMS,
            CAUSE,
            RESOLUTION,
            STATUS,
            REVIEWED_BY,
            REVIEW_DATE,
            MODIFIED_DATE,
            MODIFIED_BY
        FROM `articles`
        WHERE ID = articleId
        AND STATUS = 'APPROVED';
    END IF;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`addNewArticle`(
    IN articleId VARCHAR(45),
    IN author VARCHAR(45),
    IN keywords VARCHAR(100),
    IN title VARCHAR(100),
    IN symptoms VARCHAR(100),
    IN cause VARCHAR(100),
    IN resolution TEXT
)
BEGIN
    INSERT INTO `ESOLUTIONSSVC`.`articles`
    (
        HITS, ID, CREATE_DATE, AUTHOR,
        KEYWORDS, TITLE, SYMPTOMS, CAUSE,
        RESOLUTION, STATUS
    )
    VALUES
    (
        0, articleId, UNIX_TIMESTAMP(), author, keywords, title,
        symptoms, cause, resolution, 'NEW'
    );

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`updateArticle`(
    IN articleId VARCHAR(45),
    IN keywords VARCHAR(100),
    IN title VARCHAR(100),
    IN symptoms VARCHAR(100),
    IN cause VARCHAR(100),
    IN resolution TEXT,
    IN modifiedBy VARCHAR(45)
)
BEGIN
    UPDATE `ESOLUTIONSSVC`.`articles`
    SET
        KEYWORDS = keywords,
        TITLE = title,
        SYMPTOMS = symptoms,
        CAUSE = cause,
        RESOLUTION = resolution,
        MODIFIED_BY = modifiedBy,
        MODIFIED_DATE = UNIX_TIMESTAMP(),
        STATUS = 'NEW'
    WHERE ID = articleId;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`updateArticleStatus`(
    IN articleId VARCHAR(45),
    IN modifiedBy VARCHAR(45),
    IN articleStatus VARCHAR(15)
)
BEGIN
    UPDATE `ESOLUTIONSSVC`.`articles`
    SET
        STATUS = articleStatus,
        MODIFIED_BY = modifiedBy,
        MODIFIED_DATE = UNIX_TIMESTAMP(),
        REVIEWED_BY = modifiedBy,
        REVIEW_DATE = UNIX_TIMESTAMP()
    WHERE ID = articleId;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getArticleCount`(
    IN reqType VARCHAR(45)
)
BEGIN
    SELECT COUNT(*)
    FROM `ESOLUTIONSSVC`.`articles`
    WHERE STATUS = reqType
    AND AUTHOR != requestorId;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`retrPendingArticles`(
    IN requestorId VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        HITS,
        ID,
        CREATE_DATE,
        AUTHOR,
        KEYWORDS,
        TITLE,
        SYMPTOMS,
        CAUSE,
        RESOLUTION,
        STATUS,
        REVIEWED_BY,
        REVIEW_DATE,
        MODIFIED_DATE,
        MODIFIED_BY
    FROM `ESOLUTIONSSVC`.`articles`
    WHERE STATUS IN ('NEW', 'REJECTED', 'REVIEW')
    AND AUTHOR != requestorId
    ORDER BY CREATE_DATE DESC
    LIMIT startRow, 20;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getRecordByAttribute`(
    IN attributeName VARCHAR(126)
)
BEGIN
    SELECT
        ZONE_FILE,
        APEX_RECORD,
        ORIGIN,
        TIMETOLIVE,
        HOSTNAME,
        OWNER,
        HOSTMASTER,
        SERIAL,
        REFRESH,
        RETRY,
        EXPIRES,
        CACHETIME,
        CLASS_NAME,
        CLASS_TYPE,
        PORT,
        WEIGHT,
        SERVICE,
        PROTOCOL,
        PRIORITY,
        PRIMARY_TARGET,
        SECONDARY_TARGET,
        TERTIARY_TARGET,
    MATCH (`ZONE_FILE`, `ORIGIN`, `HOSTNAME`, `OWNER`, `CLASS_TYPE`, `SERVICE`, `PRIMARY_TARGET`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `ESOLUTIONSSVC`.`dns_service`
    WHERE MATCH (`ZONE_FILE`, `ORIGIN`, `HOSTNAME`, `OWNER`, `CLASS_TYPE`, `SERVICE`, `PRIMARY_TARGET`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    ORDER BY APEX_RECORD DESC, ORIGIN ASC;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`insertApex`(
    IN zoneFile VARCHAR(128),
    IN origin VARCHAR(126),
    IN timeToLive INTEGER(12),
    IN hostname VARCHAR(126),
    IN masterNameserver VARCHAR(255),
    IN hostmaster VARCHAR(255),
    IN serial INTEGER(11),
    IN refresh INTEGER(12),
    IN retry INTEGER(12),
    IN expiry INTEGER(12),
    IN cacheTime INTEGER(12)
)
BEGIN
    INSERT INTO `ESOLUTIONSSVC`.`dns_service`
    (
        ZONE_FILE, APEX_RECORD, ORIGIN,
        TIMETOLIVE, HOSTNAME, OWNER, HOSTMASTER,
        SERIAL, REFRESH, RETRY, EXPIRES, CACHETIME
    )
    VALUES
    (
        zoneFile, true, origin, timeToLive,
        hostname, masterNameserver, hostmaster, serial,
        refresh, retry, expiry, cacheTime
    );

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`insertRecord`(
    IN zoneFile VARCHAR(128),
    IN origin VARCHAR(126),
    IN hostname VARCHAR(126),
    IN rrClass VARCHAR(8),
    IN rrType VARCHAR(11),
    IN portNumber INTEGER(6),
    IN weight INTEGER(6),
    IN service VARCHAR(10),
    IN protocol VARCHAR(6),
    IN priority INTEGER(6),
    IN target VARCHAR(255),
    IN secondary VARCHAR(255),
    IN tertiary VARCHAR(255)
)
BEGIN
    INSERT INTO `ESOLUTIONSSVC`.`dns_service`
    (
        ZONE_FILE, APEX_RECORD, ORIGIN,
        HOSTNAME, CLASS_NAME, CLASS_TYPE, PORT, WEIGHT,
        SERVICE, PROTOCOL, PRIORITY, PRIMARY_TARGET,
        SECONDARY_TARGET, TERTIARY_TARGET
    )
    VALUES
    (
        zoneFile, false, origin,
        hostname, rrClass, rrType, portNumber,
        weight, service, protocol, priority, target,
        secondary, tertiary
    );

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getApplicationByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME,
    MATCH (`NAME`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `ESOLUTIONSSVC`.`installed_applications`
    WHERE MATCH (`NAME`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    AND APP_OFFLINE_DATE IS NULL
    LIMIT startRow, 20;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`insertNewApplication`(
    IN appGuid VARCHAR(128),
    IN appName VARCHAR(45),
    IN appVersion DECIMAL(30, 2),
    IN installPath TEXT,
    IN packageLocation TEXT,
    IN packageInstaller TEXT,
    IN installerOptions TEXT,
    IN logsDirectory TEXT,
    IN platformGuid TEXT
)
BEGIN
    INSERT INTO `ESOLUTIONSSVC`.`installed_applications`
    (
        GUID, NAME, VERSION, INSTALLATION_PATH, PACKAGE_LOCATION, PACKAGE_INSTALLER,
        INSTALLER_OPTIONS, LOGS_DIRECTORY, PLATFORM_GUID, APP_ONLINE_DATE
    )
    VALUES
    (
        appGuid, appName, appVersion, installPath, packageLocation, packageInstaller,
        installerOptions, logsDirectory, platformGuid, NOW()
    );

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`updateApplicationData`(
    IN appGuid VARCHAR(128),
    IN appName VARCHAR(45),
    IN appVersion DECIMAL(30, 2),
    IN installPath TEXT,
    IN packageLocation TEXT,
    IN packageInstaller TEXT,
    IN installerOptions TEXT,
    IN logsDirectory TEXT,
    IN platformGuid TEXT
)
BEGIN
    UPDATE `ESOLUTIONSSVC`.`installed_applications`
    SET
        NAME = appName,
        VERSION = appVersion,
        INSTALLATION_PATH = installPath,
        PACKAGE_LOCATION = packageLocation,
        PACKAGE_INSTALLER = packageInstaller,
        INSTALLER_OPTIONS = installerOptions,
        LOGS_DIRECTORY = logsDirectory,
        PLATFORM_GUID = platformGuid
    WHERE GUID = appGuid;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`removeApplicationData`(
    IN appGuid VARCHAR(128)
)
BEGIN
    UPDATE `ESOLUTIONSSVC`.`installed_applications`
    SET APP_OFFLINE_DATE = CURRENT_TIMESTAMP()
    WHERE GUID = appGuid;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getApplicationData`(
    IN appGuid VARCHAR(128)
)
BEGIN
    SELECT
        GUID,
        NAME,
        VERSION,
        INSTALLATION_PATH,
        PACKAGE_LOCATION,
        PACKAGE_INSTALLER,
        INSTALLER_OPTIONS,
        LOGS_DIRECTORY,
        PLATFORM_GUID
    FROM `ESOLUTIONSSVC`.`installed_applications`
    WHERE GUID = appGuid
    AND APP_OFFLINE_DATE IS NULL;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getApplicationCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `ESOLUTIONSSVC`.`installed_applications`
    WHERE APP_OFFLINE_DATE IS NULL;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`listApplications`(
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME
    FROM `ESOLUTIONSSVC`.`installed_applications`
    WHERE APP_OFFLINE_DATE IS NULL
    LIMIT startRow, 20;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getServerByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        T1.GUID,
        T1.REGION,
        T1.NWPARTITION,
        T1.OPER_HOSTNAME,
        T2.GUID,
        T2.NAME,
    MATCH (T1.GUID, T1.SYSTEM_OSTYPE, T1.STATUS, T1.REGION, T1.NWPARTITION, T1.DATACENTER_GUID, T1.SYSTYPE, T1.OPER_HOSTNAME, T1.ASSIGNED_ENGINEER, T1.OWNING_DMGR)
    AGAINST (attributeName WITH QUERY EXPANSION)
    FROM `ESOLUTIONSSVC`.`installed_systems` T1
    INNER JOIN `ESOLUTIONSSVC`.`service_datacenters` T2
    ON T1.DATACENTER_GUID = T2.GUID
    WHERE MATCH (T1.GUID, T1.SYSTEM_OSTYPE, T1.STATUS, T1.REGION, T1.NWPARTITION, T1.DATACENTER_GUID, T1.SYSTYPE, T1.OPER_HOSTNAME, T1.ASSIGNED_ENGINEER, T1.OWNING_DMGR)
    AGAINST (attributeName IN BOOLEAN MODE)
    AND DELETE_DATE IS NULL
    LIMIT startRow, 20;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`validateServerHostName`(
    IN operHostname VARCHAR(128)
)
BEGIN
    SELECT COUNT(*)
    FROM `ESOLUTIONSSVC`.`installed_systems`
    WHERE OPER_HOSTNAME = operHostname;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`insertNewServer`(
    IN systemGuid VARCHAR(128),
    IN systemOs VARCHAR(45),
    IN systemStatus VARCHAR(45),
    IN systemRegion VARCHAR(45),
    IN networkPartition VARCHAR(45),
    IN datacenter VARCHAR(45),
    IN systemType VARCHAR(45),
    IN domainName VARCHAR(255),
    IN cpuType VARCHAR(255),
    IN cpuCount INT,
    IN serverModel VARCHAR(255),
    IN serialNumber VARCHAR(255),
    IN installedMemory INT,
    IN operIp VARCHAR(50),
    IN operHostname VARCHAR(100),
    IN mgmtIp VARCHAR(50),
    IN mgmtHostname VARCHAR(100),
    IN backupIp VARCHAR(50),
    IN backupHostname VARCHAR(100),
    IN nasIp VARCHAR(50),
    IN nasHostname VARCHAR(100),
    IN natAddr VARCHAR(50),
    IN systemComments TEXT,
    IN engineer VARCHAR(100),
    IN mgrEntry VARCHAR(128),
    IN dmgrPort INT(5),
    IN serverRack VARCHAR(255),
    IN rackPosition VARCHAR(255),
    IN owningDmgr VARCHAR(255)
)
BEGIN
    IF dmgrPort = 0
    THEN
        INSERT INTO `ESOLUTIONSSVC`.`installed_systems`
        (GUID, SYSTEM_OSTYPE, STATUS, REGION, NWPARTITION, DATACENTER_GUID, SYSTYPE, DOMAIN_NAME, CPU_TYPE, CPU_COUNT, SERVER_RACK, RACK_POSITION, SERVER_MODEL, SERIAL_NUMBER, INSTALLED_MEMORY, OPER_IP, OPER_HOSTNAME, MGMT_IP, MGMT_HOSTNAME, BKUP_IP, BKUP_HOSTNAME, NAS_IP, NAS_HOSTNAME, NAT_ADDR, COMMENTS, ASSIGNED_ENGINEER, ADD_DATE, MGR_ENTRY, OWNING_DMGR)
        VALUES
        (systemGuid, systemOs, systemStatus, systemRegion, networkPartition, datacenter, systemType, domainName, cpuType, cpuCount, serverRack, rackPosition, serverModel, serialNumber, installedMemory, operIp, operHostname, mgmtIp, mgmtHostname, backupIp, backupHostname, nasIp, nasHostname, natAddr, systemComments, engineer, NOW(), mgrEntry, owningDmgr);
    ELSE
        INSERT INTO `ESOLUTIONSSVC`.`installed_systems`
        (GUID, SYSTEM_OSTYPE, STATUS, REGION, NWPARTITION, DATACENTER_GUID, SYSTYPE, DOMAIN_NAME, CPU_TYPE, CPU_COUNT, SERVER_RACK, RACK_POSITION, SERVER_MODEL, SERIAL_NUMBER, INSTALLED_MEMORY, OPER_IP, OPER_HOSTNAME, MGMT_IP, MGMT_HOSTNAME, BKUP_IP, BKUP_HOSTNAME, NAS_IP, NAS_HOSTNAME, NAT_ADDR, COMMENTS, ASSIGNED_ENGINEER, ADD_DATE, MGR_ENTRY, DMGR_PORT, OWNING_DMGR)
        VALUES
        (systemGuid, systemOs, systemStatus, systemRegion, networkPartition, datacenter, systemType, domainName, cpuType, cpuCount, serverRack, rackPosition, serverModel, serialNumber, installedMemory, operIp, operHostname, mgmtIp, mgmtHostname, backupIp, backupHostname, nasIp, nasHostname, natAddr, systemComments, engineer, NOW(), mgrEntry, dmgrPort, owningDmgr);
    END IF;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`updateServerData`(
    IN systemGuid VARCHAR(128),
    IN systemOs VARCHAR(45),
    IN systemStatus VARCHAR(45),
    IN systemRegion VARCHAR(45),
    IN networkPartition VARCHAR(45),
    IN datacenter VARCHAR(45),
    IN systemType VARCHAR(45),
    IN domainName VARCHAR(255),
    IN cpuType VARCHAR(255),
    IN cpuCount INT,
    IN serverModel VARCHAR(255),
    IN serialNumber VARCHAR(255),
    IN installedMemory INT,
    IN operIp VARCHAR(50),
    IN operHostname VARCHAR(100),
    IN mgmtIp VARCHAR(50),
    IN mgmtHostname VARCHAR(100),
    IN backupIp VARCHAR(50),
    IN backupHostname VARCHAR(100),
    IN nasIp VARCHAR(50),
    IN nasHostname VARCHAR(100),
    IN natAddr VARCHAR(50),
    IN systemComments TEXT,
    IN engineer VARCHAR(100),
    IN mgrEntry VARCHAR(128),
    IN dmgrPort INT(5),
    IN serverRack VARCHAR(255),
    IN rackPosition VARCHAR(255),
    IN owningDmgr VARCHAR(255)
)
BEGIN
    UPDATE `ESOLUTIONSSVC`.`installed_systems`
    SET
        SYSTEM_OSTYPE = systemOs,
        STATUS = systemStatus,
        REGION = systemRegion,
        NWPARTITION = networkPartition,
        DATACENTER_GUID = datacenter,
        SYSTYPE = systemType,
        DOMAIN_NAME = domainName,
        CPU_TYPE = cpuType,
        CPU_COUNT = cpuCount,
        SERVER_RACK = serverRack,
        RACK_POSITION = rackPosition,
        SERVER_MODEL = serverModel,
        SERIAL_NUMBER = serialNumber,
        INSTALLED_MEMORY = installedMemory,
        OPER_IP = operIp,
        OPER_HOSTNAME = operHostname,
        MGMT_IP = mgmtIp,
        MGMT_HOSTNAME = mgmtHostname,
        BKUP_IP = backupIp,
        BKUP_HOSTNAME = backupHostname,
        NAS_IP = nasIp,
        NAS_HOSTNAME = nasHostname,
        NAT_ADDR = natAddr,
        COMMENTS = systemComments,
        ASSIGNED_ENGINEER = engineer,
        MGR_ENTRY = mgrEntry,
        DMGR_PORT = dmgrPort,
        OWNING_DMGR = owningDmgr
    WHERE GUID = systemGuid;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getServerCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `ESOLUTIONSSVC`.`installed_systems`
    WHERE DELETE_DATE IS NULL;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getServerList`(
    IN startRow INT
)
BEGIN
    SELECT
        T1.GUID,
        T1.REGION,
        T1.NWPARTITION,
        T1.OPER_HOSTNAME,
        T1.OWNING_DMGR,
        T2.GUID,
        T2.NAME
    FROM `ESOLUTIONSSVC`.`installed_systems`
    INNER JOIN `ESOLUTIONSSVC`.`service_datacenters` T2
    ON T1.DATACENTER_GUID = T2.GUID
    WHERE DELETE_DATE IS NULL
    LIMIT startRow, 20;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`retrServerData`(
    IN serverGuid VARCHAR(128)
)
BEGIN
    SELECT
        T1.GUID,
        T1.SYSTEM_OSTYPE,
        T1.STATUS,
        T1.REGION,
        T1.NWPARTITION,
        T1.SYSTYPE,
        T1.DOMAIN_NAME,
        T1.CPU_TYPE,
        T1.CPU_COUNT,
        T1.SERVER_RACK,
        T1.RACK_POSITION,
        T1.SERVER_MODEL,
        T1.SERIAL_NUMBER,
        T1.INSTALLED_MEMORY,
        T1.OPER_IP,
        T1.OPER_HOSTNAME,
        T1.MGMT_IP,
        T1.MGMT_HOSTNAME,
        T1.BKUP_IP,
        T1.BKUP_HOSTNAME,
        T1.NAS_IP,
        T1.NAS_HOSTNAME,
        T1.NAT_ADDR,
        T1.COMMENTS,
        T1.ASSIGNED_ENGINEER,
        T1.ADD_DATE,
        T1.DELETE_DATE,
        T1.DMGR_PORT,
        T1.OWNING_DMGR,
        T1.MGR_ENTRY,
        T2.GUID,
        T2.NAME
    FROM `ESOLUTIONSSVC`.`installed_systems` T1
    INNER JOIN `ESOLUTIONSSVC`.`service_datacenters` T2
    ON T1.DATACENTER_GUID = T2.GUID
    WHERE T1.GUID = serverGuid
    AND DELETE_DATE IS NULL;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getRetiredServers`(
)
BEGIN
    SELECT GUID
    FROM `ESOLUTIONSSVC`.`installed_systems`
    WHERE DELETE_DATE IS NOT NULL;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`retireServer`(
    IN guid VARCHAR(128)
)
BEGIN
    INSERT INTO eSolutionsArchive.installed_systems
    SELECT *
    FROM ESOLUTIONSSVC.installed_systems
    WHERE GUID = guid
    AND DELETE_DATE IS NOT NULL;

    COMMIT;

    DELETE FROM ESOLUTIONSSVC.installed_systems
    WHERE GUID = guid
    AND DELETE_DATE IS NOT NULL;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getDataCenterByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME,
        STATUS,
        DESCRIPTION,
    MATCH (`GUID`, `NAME`, `STATUS`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `ESOLUTIONSSVC`.`service_datacenters`
    WHERE MATCH (`GUID`, `NAME`, `STATUS`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    LIMIT startRow, 20;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`addNewDatacenter`(
    IN datacenterGuid VARCHAR(128),
    IN datacenterName VARCHAR(45),
    IN datacenterStatus VARCHAR(45),
    IN datacenterDesc TEXT
)
BEGIN
    INSERT INTO `ESOLUTIONSSVC`.`service_datacenters`
    (GUID, NAME, STATUS, DESCRIPTION)
    VALUES
    (datacenterGuid, datacenterName, datacenterStatus, datacenterDesc);

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`removeDataCenter`(
    IN datacenterGuid VARCHAR(128)
)
BEGIN
    UPDATE `ESOLUTIONSSVC`.`service_datacenters`
    SET STATUS = 'INACTIVE'
    WHERE GUID = datacenterGuid;

    COMMIT;
END //
COMMIT //


CREATE PROCEDURE `ESOLUTIONSSVC`.`getDatacenterCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `ESOLUTIONSSVC`.`service_datacenters`
    WHERE STATUS = 'ACTIVE';
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`listDataCenters`(
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME,
        STATUS,
        DESCRIPTION
    FROM `ESOLUTIONSSVC`.`service_datacenters`
    WHERE STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`retrDataCenter`(
    IN datacenterGuid VARCHAR(128)
)
BEGIN
    SELECT
        GUID,
        NAME,
        STATUS,
        DESCRIPTION
    FROM `ESOLUTIONSSVC`.`service_datacenters`
    WHERE GUID = datacenterGuid
    AND STATUS = 'ACTIVE';
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getMessagesByAttribute`(
    IN searchTerms VARCHAR(100)
)
BEGIN
    SELECT
        ID,
        TITLE,
        MESSAGE,
        AUTHOR,
        SUBMIT_DATE,
        ACTIVE ,
        ALERT,
        EXPIRES,
        EXPIRES_ON,
        MODIFIED_ON,
        MODIFIED_BY,
    MATCH (`ID`, `TITLE`, `MESSAGE`, `AUTHOR`)
    AGAINST (+searchTerms WITH QUERY EXPANSION)
    FROM `ESOLUTIONSSVC`.`service_messages`
    WHERE MATCH (`ID`, `TITLE`, `MESSAGE`, `AUTHOR`)
    AGAINST (+searchTerms IN BOOLEAN MODE)
    AND ACTIVE  = TRUE
    AND (EXPIRES_ON > NOW() OR EXPIRES_ON = '0000-00-00 00:00:00' OR EXPIRES = FALSE)
    ORDER BY ID DESC;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`submitSvcMessage`(
    IN messageId VARCHAR(128),
    IN messageTitle VARCHAR(100),
    IN messageText TEXT,
    IN messageAuthor VARCHAR(45),
    IN active BOOLEAN,
    IN isAlert BOOLEAN,
    IN expiry BOOLEAN,
    IN expiryDate BIGINT
)
BEGIN
    INSERT INTO `ESOLUTIONSSVC`.`service_messages`
    (
        ID, TITLE, MESSAGE, AUTHOR, SUBMIT_DATE, ACTIVE , ALERT, EXPIRES, EXPIRES_ON
    )
    VALUES
    (
        messageId, messageTitle, messageText, messageAuthor, NOW(), active, isAlert, expiry, expiryDate
    );

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`updateServiceMessage`(
    IN messageId VARCHAR(128),
    IN messageTitle VARCHAR(100),
    IN messageText TEXT,
    IN active BOOLEAN,
    IN isAlert BOOLEAN,
    IN expiry BOOLEAN,
    IN expiryDate BIGINT,
    IN modifyAuthor VARCHAR(45)
)
BEGIN
    UPDATE `ESOLUTIONSSVC`.`service_messages`
    SET
        TITLE = messageTitle,
        MESSAGE = messageText,
        ACTIVE  = active,
        ALERT = isAlert,
        EXPIRES = expiry,
        EXPIRES_ON = expiryDate,
        MODIFIED_ON = NOW(),
        MODIFIED_BY = modifyAuthor
    WHERE ID = messageId;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`retrServiceMessage`(
    IN requestId VARCHAR(45)
)
BEGIN
    SELECT
        ID,
        TITLE,
        MESSAGE,
        AUTHOR,
        SUBMIT_DATE,
        ACTIVE ,
        ALERT,
        EXPIRES,
        EXPIRES_ON,
        MODIFIED_ON,
        MODIFIED_BY
    FROM `ESOLUTIONSSVC`.`service_messages`
    WHERE ID = requestId;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`retrServiceMessages`(
)
BEGIN
    SELECT
        ID,
        TITLE,
        MESSAGE,
        AUTHOR,
        SUBMIT_DATE,
        ACTIVE ,
        ALERT,
        EXPIRES,
        EXPIRES_ON,
        MODIFIED_ON,
        MODIFIED_BY
    FROM `ESOLUTIONSSVC`.`service_messages`
    WHERE ACTIVE  = TRUE
    AND ALERT = FALSE
    AND (EXPIRES_ON > NOW() OR EXPIRES_ON = '0000-00-00 00:00:00' OR EXPIRES = FALSE)
    ORDER BY ID DESC;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`retrAlertMessages`(
)
BEGIN
    SELECT
        ID,
        TITLE,
        MESSAGE,
        AUTHOR,
        SUBMIT_DATE,
        ACTIVE ,
        ALERT,
        EXPIRES,
        EXPIRES_ON,
        MODIFIED_ON,
        MODIFIED_BY
    FROM `ESOLUTIONSSVC`.`service_messages`
    WHERE ACTIVE  = TRUE
    AND ALERT = TRUE
    AND (EXPIRES_ON > NOW() OR EXPIRES_ON = '0000-00-00 00:00:00' OR EXPIRES = FALSE)
    ORDER BY ID DESC;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getPlatformByAttribute`(
    IN attributeName VARCHAR(100),
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME,
    MATCH (`NAME`, `REGION`, `NWPARTITION`)
    AGAINST (+attributeName WITH QUERY EXPANSION)
    FROM `ESOLUTIONSSVC`.`service_platforms`
    WHERE MATCH (`NAME`, `REGION`, `NWPARTITION`)
    AGAINST (+attributeName IN BOOLEAN MODE)
    AND STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`addNewPlatform`(
    IN guid VARCHAR(128),
    IN name VARCHAR(128),
    IN region VARCHAR(15),
    IN nwpartition VARCHAR(15),
    IN status VARCHAR(50),
    IN servers TEXT,
    IN description TEXT
)
BEGIN
    INSERT INTO `ESOLUTIONSSVC`.`service_platforms` (GUID, NAME, REGION, NWPARTITION, STATUS, SERVERS, DESCRIPTION)
    VALUES (guid, name, region, nwpartition, status, servers, description);

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`updatePlatformData`(
    IN guid VARCHAR(128),
    IN name VARCHAR(128),
    IN region VARCHAR(15),
    IN nwpartition VARCHAR(15),
    IN status VARCHAR(50),
    IN servers TEXT,
    IN description TEXT
)
BEGIN
    UPDATE `ESOLUTIONSSVC`.`service_platforms`
    SET
        NAME = name,
        REGION = region,
        NWPARTITION = nwpartition,
        STATUS = status,
        SERVERS = servers,
        DESCRIPTION = description
    WHERE PROJECT_GUID = platformGuid;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`removePlatformData`(
    IN platformGuid VARCHAR(128)
)
BEGIN
    UPDATE `ESOLUTIONSSVC`.`service_platforms`
    SET STATUS = 'INACTIVE'
    WHERE GUID = platformGuid;

    COMMIT;
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getPlatformData`(
    IN guid VARCHAR(128)
)
BEGIN
    SELECT
        GUID,
        NAME,
        REGION,
        NWPARTITION,
        STATUS,
        SERVERS,
        DESCRIPTION
    FROM `ESOLUTIONSSVC`.`service_platforms`
    WHERE GUID = guid
    AND STATUS = 'ACTIVE';
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`getPlatformCount`(
)
BEGIN
    SELECT COUNT(*)
    FROM `ESOLUTIONSSVC`.`service_platforms`
    WHERE STATUS = 'ACTIVE';
END //
COMMIT //

CREATE PROCEDURE `ESOLUTIONSSVC`.`listPlatforms`(
    IN startRow INT
)
BEGIN
    SELECT
        GUID,
        NAME
    FROM `ESOLUTIONSSVC`.`service_platforms`
    WHERE STATUS = 'ACTIVE'
    LIMIT startRow, 20;
END //
COMMIT //

/*!50003 SET SESSION SQL_MODE=@TEMP_SQL_MODE */ //

COMMIT //

DELIMITER ;
