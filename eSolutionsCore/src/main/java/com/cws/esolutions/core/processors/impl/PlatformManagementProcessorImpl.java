/*
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cws.esolutions.core.processors.impl;

import java.util.UUID;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.sql.SQLException;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.core.processors.dto.Platform;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.core.processors.dto.DataCenter;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServerStatus;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.processors.enums.ServiceStatus;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.processors.dto.AccountControlRequest;
import com.cws.esolutions.core.processors.dto.PlatformManagementRequest;
import com.cws.esolutions.security.processors.dto.AccountControlResponse;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.core.processors.dto.PlatformManagementResponse;
import com.cws.esolutions.security.processors.impl.AccountControlProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountControlException;
import com.cws.esolutions.core.processors.exception.PlatformManagementException;
import com.cws.esolutions.security.processors.interfaces.IAccountControlProcessor;
import com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
/**
 * @see com.cws.esolutions.core.dao.processors.interfaces.IKnowledgeBaseDAO
 */
public class PlatformManagementProcessorImpl implements IPlatformManagementProcessor
{
    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#addNewPlatform(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    @Override
    public PlatformManagementResponse addNewPlatform(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#addNewPlatform(final PlatformManagementRequest request) throws PlatformManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PlatformManagementRequest: {}", request);
        }

        PlatformManagementResponse response = new PlatformManagementResponse();

        final Platform platform = request.getPlatform();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Platform: {}", platform);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                if (platform == null)
                {
                    throw new PlatformManagementException("No platform was provided. Cannot continue.");
                }

                // make sure all the platform data is there
                List<String[]> validator = null;

                try
                {
                    validator = platformDao.listPlatformsByAttribute(platform.getPlatformName(), request.getStartPage());
                }
                catch (SQLException sqx)
                {
                    ERROR_RECORDER.error(sqx.getMessage(), sqx);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("Validator: {}", validator);
                }

                if ((validator == null) || (validator.size() == 0))
                {
                    // valid platform
                    List<String> appServerList = new ArrayList<>();
                    for (Server server : platform.getAppServers())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", server);
                        }

                        appServerList.add(server.getServerGuid());
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("appServerList: {}", appServerList);
                    }

                    List<String> webServerList = new ArrayList<>();
                    for (Server server : platform.getWebServers())
                    {
                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", server);
                        }

                        webServerList.add(server.getServerGuid());
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("webServerList: {}", webServerList);
                    }

                    List<String> insertData = new ArrayList<>(
                            Arrays.asList(
                                    UUID.randomUUID().toString(),
                                    platform.getPlatformName(),
                                    platform.getPlatformRegion().name(),
                                    platform.getPlatformDmgr().getServerGuid(),
                                    appServerList.toString(),
                                    webServerList.toString(),
                                    platform.getStatus().name(),
                                    platform.getDescription()));

                    if (DEBUG)
                    {
                        for (Object str : insertData)
                        {
                            DEBUGGER.debug("Value: {}", str);
                        }
                    }

                    boolean isComplete = platformDao.addNewPlatform(insertData);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (isComplete)
                    {
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setResponse("Successfully added " + platform.getPlatformName() + " to the asset datasource");
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                        response.setResponse("Failed to add " + platform.getPlatformName() + " to the asset datasource");
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                    response.setResponse("Platform " + platform.getPlatformName() + " already exists in the asset datasource.");
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new PlatformManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.ADDPLATFORM);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#updatePlatformData(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    @Override
    public PlatformManagementResponse updatePlatformData(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#updatePlatformData(final PlatformManagementRequest request) throws PlatformManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PlatformManagementRequest: {}", request);
        }

        PlatformManagementResponse response = new PlatformManagementResponse();

        final Platform platform = request.getPlatform();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Platform: {}", platform);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                List<String> appServerList = new ArrayList<>();
                for (Server server : platform.getAppServers())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", server);
                    }

                    appServerList.add(server.getServerGuid());
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("appServerList: {}", appServerList);
                }

                List<String> webServerList = new ArrayList<>();
                for (Server server : platform.getWebServers())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", server);
                    }

                    webServerList.add(server.getServerGuid());
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("webServerList: {}", webServerList);
                }

                List<String> insertData = new ArrayList<>(
                        Arrays.asList(
                                platform.getPlatformGuid(),
                                platform.getPlatformName(),
                                platform.getPlatformRegion().name(),
                                platform.getPlatformDmgr().getServerGuid(),
                                appServerList.toString(),
                                webServerList.toString(),
                                platform.getStatus().name(),
                                platform.getDescription()));

                if (DEBUG)
                {
                    for (Object str : insertData)
                    {
                        DEBUGGER.debug("Value: {}", str);
                    }
                }

                boolean isComplete = platformDao.updatePlatformData(insertData);

                if (DEBUG)
                {
                    DEBUGGER.debug("isComplete: {}", isComplete);
                }

                if (isComplete)
                {
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setResponse("Successfully added " + platform.getPlatformName() + " to the asset datasource");
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                    response.setResponse("Failed to add " + platform.getPlatformName() + " to the asset datasource");
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new PlatformManagementException(ucsx.getMessage(), ucsx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.UPDATEPLATFORM);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#listPlatforms(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    @Override
    public PlatformManagementResponse listPlatforms(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#listPlatforms(final PlatformManagementRequest request) throws PlatformManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PlatformManagementRequest: {}", request);
        }

        PlatformManagementResponse response = new PlatformManagementResponse();

        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                int count = platformDao.getPlatformCount();

                if (DEBUG)
                {
                    DEBUGGER.debug("count: {}", count);
                }

                List<String[]> platformData = platformDao.listAvailablePlatforms(request.getStartPage());

                if (DEBUG)
                {
                    DEBUGGER.debug("platformData: {}", platformData);
                }

                if ((platformData != null) && (platformData.size() != 0))
                {
                    List<Platform> platformList = new ArrayList<>();

                    for (String[] data : platformData)
                    {
                        Platform platform = new Platform();
                        platform.setPlatformGuid(data[0]);
                        platform.setPlatformName(data[1]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Platform: {}", platform);
                        }

                        platformList.add(platform);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformList: {}", platformList);
                    }

                    response.setEntryCount(count);
                    response.setPlatformList(platformList);
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setResponse("Successfully loaded platform list");
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                    response.setResponse("No platforms were located in the asset datasource.");
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new PlatformManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LISTPLATFORMS);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#listPlatformsByAttribute(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    @Override
    public PlatformManagementResponse listPlatformsByAttribute(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#listPlatformsByAttribute(final PlatformManagementRequest request) throws PlatformManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PlatformManagementRequest: {}", request);
        }

        PlatformManagementResponse response = new PlatformManagementResponse();

        final Platform reqPlatform = request.getPlatform();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Platform: {}", reqPlatform);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                List<String[]> platformData = platformDao.listPlatformsByAttribute(reqPlatform.getPlatformName(), request.getStartPage());

                if (DEBUG)
                {
                    DEBUGGER.debug("platformData: {}", platformData);
                }

                if ((platformData != null) && (platformData.size() != 0))
                {
                    List<Platform> platformList = new ArrayList<>();

                    for (String[] data : platformData)
                    {
                        Platform platform = new Platform();
                        platform.setPlatformGuid(data[0]);
                        platform.setPlatformName(data[1]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Platform: {}", platform);
                        }

                        platformList.add(platform);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformList: {}", platformList);
                    }

                    response.setPlatformList(platformList);
                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setResponse("Successfully loaded platform list");
                }
                else
                {
                    throw new PlatformManagementException("No platforms were located in the asset datasource.");
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new PlatformManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LISTPLATFORMS);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.core.processors.interfaces.IPlatformManagementProcessor#getPlatformData(com.cws.esolutions.core.processors.dto.PlatformManagementRequest)
     */
    @Override
    public PlatformManagementResponse getPlatformData(final PlatformManagementRequest request) throws PlatformManagementException
    {
        final String methodName = IPlatformManagementProcessor.CNAME + "#getPlatformData(final PlatformManagementRequest request) throws PlatformManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("PlatformManagementRequest: {}", request);
        }

        UserAccount svcAccount = null;
        UserAccount searchAccount = null;
        AccountControlRequest searchRequest = null;
        AccountControlResponse searchResponse = null;
        PlatformManagementResponse response = new PlatformManagementResponse();

        final Platform platform = request.getPlatform();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final IAccountControlProcessor acctControl = new AccountControlProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("Platform: {}", platform);
            DEBUGGER.debug("UserAccount: {}", userAccount);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
        }

        try
        {
            boolean isServiceAuthorized = userControl.isUserAuthorizedForService(userAccount, request.getServiceId());

            if (DEBUG)
            {
                DEBUGGER.debug("isServiceAuthorized: {}", isServiceAuthorized);
            }

            if (isServiceAuthorized)
            {
                List<Server> appServerList = null;
                List<Server> webServerList = null;

                if (platform != null)
                {
                    List<Object> platformData = platformDao.getPlatformData(platform.getPlatformGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("platformData: {}", platformData);
                    }

                    if ((platformData != null) && (platformData.size() != 0))
                    {
                        DataCenter dataCenter = new DataCenter();
                        dataCenter.setDatacenterGuid((String) platformData.get(31));
                        dataCenter.setDatacenterName((String) platformData.get(32));
                        dataCenter.setDatacenterStatus(ServiceStatus.valueOf((String) platformData.get(33)));
                        dataCenter.setDatacenterDesc((String) platformData.get(34));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("DataCenter: {}", dataCenter);
                        }

                        Server dmgr = new Server();
                        dmgr.setServerGuid((String) platformData.get(6)); // T2.SYSTEM_GUID
                        dmgr.setOsName((String) platformData.get(7)); // T2.SYSTEM_OSTYPE
                        dmgr.setServerStatus(ServerStatus.valueOf((String) platformData.get(8))); // T2.SYSTEM_STATUS
                        dmgr.setServerRegion(ServiceRegion.valueOf((String) platformData.get(2))); // T1.PLATFORM_REGION
                        dmgr.setNetworkPartition(NetworkPartition.valueOf((String) platformData.get(9))); // T2.NETWORK_PARTITION
                        dmgr.setDatacenter(dataCenter); // datacenter as earlier obtained
                        dmgr.setServerType(ServerType.DMGRSERVER);
                        dmgr.setDomainName((String) platformData.get(10)); // T2.DOMAIN_NAME
                        dmgr.setCpuType((String) platformData.get(11)); // T2.CPU_TYPE
                        dmgr.setCpuCount((Integer) platformData.get(12)); // T2.CPU_COUNT
                        dmgr.setServerRack((String) platformData.get(13)); // T2.SERVER_RACK
                        dmgr.setRackPosition((String) platformData.get(14)); // T2.RACK_POSITION
                        dmgr.setServerModel((String) platformData.get(15)); // T2.SERVER_MODEL
                        dmgr.setSerialNumber((String) platformData.get(16)); // T2.SERIAL_NUMBER
                        dmgr.setInstalledMemory((Integer) platformData.get(17)); // T2.INSTALLED_MEMORY
                        dmgr.setOperIpAddress((String) platformData.get(18)); // T2.OPER_IP
                        dmgr.setOperHostName((String) platformData.get(19)); // T2.OPER_HOSTNAME
                        dmgr.setMgmtIpAddress((String) platformData.get(20)); // T2.MGMT_IP
                        dmgr.setMgmtHostName((String) platformData.get(21)); // T2.MGMT_HOSTNAME
                        dmgr.setBkIpAddress((String) platformData.get(22)); // T2.BKUP_IP
                        dmgr.setBkHostName((String) platformData.get(23)); // T2.BKUP_HOSTNAME
                        dmgr.setNasIpAddress((String) platformData.get(24)); // T2.NAS_IP
                        dmgr.setNasHostName((String) platformData.get(25)); // T2.NAS_HOSTNAME
                        dmgr.setNatAddress((String) platformData.get(26)); // T2.NAT_ADDR
                        dmgr.setServerComments((String) platformData.get(27)); // T2.COMMENTS
                        dmgr.setDmgrPort((Integer) platformData.get(29)); // T2.DMGR_PORT
                        dmgr.setMgrUrl((String) platformData.get(30)); // T2.MGR_ENTRY

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", dmgr);
                        }

                        searchAccount = new UserAccount();
                        searchAccount.setGuid((String) platformData.get(28));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", searchAccount);
                        }

                        svcAccount = new UserAccount();
                        svcAccount.setUsername(serviceAccount.get(0));
                        svcAccount.setGuid(serviceAccount.get(1));
                        svcAccount.setRole(Role.valueOf(serviceAccount.get(2)));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserAccount: {}", svcAccount);
                        }

                        searchRequest = new AccountControlRequest();
                        searchRequest.setHostInfo(request.getRequestInfo());
                        searchRequest.setUserAccount(searchAccount);
                        searchRequest.setApplicationName(request.getApplicationName());
                        searchRequest.setApplicationId(request.getApplicationId());
                        searchRequest.setSearchType(SearchRequestType.GUID);
                        searchRequest.setRequestor(svcAccount);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AccountControlRequest: {}", searchRequest);
                        }

                        try
                        {
                            searchResponse = acctControl.loadUserAccount(searchRequest);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("AccountControlResponse: {}", searchResponse);
                            }

                            if (searchResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                            {
                                dmgr.setAssignedEngineer(searchResponse.getUserAccount()); // ASSIGNED_ENGINEER
                            }
                        }
                        catch (AccountControlException acx)
                        {
                            ERROR_RECORDER.error(acx.getMessage(), acx);
                        }

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", dmgr);
                        }

                        // appservers
                        String appTmp = StringUtils.remove((String) platformData.get(3), "["); // T1.PLATFORM_APPSERVERS
                        String platformApps = StringUtils.remove(appTmp, "]");

                        if (DEBUG)
                        {
                            DEBUGGER.debug("String: {}", platformApps);
                        }

                        if (platformApps.split(",").length >= 1)
                        {
                            appServerList = new ArrayList<>();

                            for (String serverGuid : platformApps.split(","))
                            {
                                List<Object> serverData = serverDao.getInstalledServer(StringUtils.trim(serverGuid));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("serverData: {}", serverData);
                                }

                                if ((serverData != null) && (serverData.size() != 0))
                                {
                                    Server server = new Server();
                                    server.setServerGuid((String) serverData.get(0)); // SYSTEM_GUID
                                    server.setOsName((String) serverData.get(1)); // SYSTEM_OSTYPE
                                    server.setServerStatus(ServerStatus.valueOf((String) serverData.get(2))); // SYSTEM_STATUS
                                    server.setServerRegion(ServiceRegion.valueOf((String) serverData.get(3))); // SYSTEM_REGION
                                    server.setNetworkPartition(NetworkPartition.valueOf((String) serverData.get(4))); // NETWORK_PARTITION
                                    server.setDatacenter(dataCenter); // datacenter as earlier obtained
                                    server.setServerType(ServerType.valueOf((String) serverData.get(6))); // SYSTEM_TYPE
                                    server.setDomainName((String) serverData.get(7)); // DOMAIN_NAME
                                    server.setCpuType((String) serverData.get(8)); // CPU_TYPE
                                    server.setCpuCount((Integer) serverData.get(9)); // CPU_COUNT
                                    server.setServerRack((String) serverData.get(10)); // SERVER_RACK
                                    server.setRackPosition((String) serverData.get(11)); // RACK_POSITION
                                    server.setServerModel((String) serverData.get(12)); // SERVER_MODEL
                                    server.setSerialNumber((String) serverData.get(13)); // SERIAL_NUMBER
                                    server.setInstalledMemory((Integer) serverData.get(14)); // INSTALLED_MEMORY
                                    server.setOperIpAddress((String) serverData.get(15)); // OPER_IP
                                    server.setOperHostName((String) serverData.get(16)); // OPER_HOSTNAME
                                    server.setMgmtIpAddress((String) serverData.get(17)); // MGMT_IP
                                    server.setMgmtHostName((String) serverData.get(18)); // MGMT_HOSTNAME
                                    server.setBkIpAddress((String) serverData.get(19)); // BKUP_IP
                                    server.setBkHostName((String) serverData.get(20)); // BKUP_HOSTNAME
                                    server.setNasIpAddress((String) serverData.get(21)); // NAS_IP
                                    server.setNasHostName((String) serverData.get(22)); // NAS_HOSTNAME
                                    server.setNatAddress((String) serverData.get(23)); // NAT_ADDR
                                    server.setServerComments((String) serverData.get(24)); // COMMENTS
                                    server.setDmgrPort((Integer) serverData.get(28)); // DMGR_PORT
                                    server.setOwningDmgr(platform.getPlatformDmgr()); // OWNING_DMGR
                                    server.setMgrUrl((String) serverData.get(30)); // MGR_ENTRY

                                    searchAccount = new UserAccount();
                                    searchAccount.setGuid((String) serverData.get(25));

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("UserAccount: {}", searchAccount);
                                    }

                                    svcAccount = new UserAccount();
                                    svcAccount.setUsername(serviceAccount.get(0));
                                    svcAccount.setGuid(serviceAccount.get(1));
                                    svcAccount.setRole(Role.valueOf(serviceAccount.get(2)));

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("UserAccount: {}", svcAccount);
                                    }

                                    searchRequest = new AccountControlRequest();
                                    searchRequest.setHostInfo(request.getRequestInfo());
                                    searchRequest.setUserAccount(searchAccount);
                                    searchRequest.setApplicationName(request.getApplicationName());
                                    searchRequest.setApplicationId(request.getApplicationId());
                                    searchRequest.setSearchType(SearchRequestType.GUID);
                                    searchRequest.setRequestor(svcAccount);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("AccountControlRequest: {}", searchRequest);
                                    }

                                    try
                                    {
                                        searchResponse = acctControl.loadUserAccount(searchRequest);

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("AccountControlResponse: {}", searchResponse);
                                        }

                                        if (searchResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                                        {
                                            server.setAssignedEngineer(searchResponse.getUserAccount()); // ASSIGNED_ENGINEER
                                        }
                                    }
                                    catch (AccountControlException acx)
                                    {
                                        ERROR_RECORDER.error(acx.getMessage(), acx);
                                    }

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Server: {}", server);
                                    }

                                    appServerList.add(server);
                                }
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("appServerList: {}", appServerList);
                            }
                        }

                        // appservers
                        String webTmp = StringUtils.remove((String) platformData.get(4), "["); // T1.PLATFORM_WEBSERVERS
                        String platformWebs = StringUtils.remove(webTmp, "]");

                        if (DEBUG)
                        {
                            DEBUGGER.debug("String: {}", platformWebs);
                        }

                        if (platformWebs.split(",").length >= 1)
                        {
                            webServerList = new ArrayList<>();

                            for (String serverGuid : platformWebs.split(","))
                            {
                                List<Object> serverData = serverDao.getInstalledServer(StringUtils.trim(serverGuid));

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("serverData: {}", serverData);
                                }

                                if ((serverData != null) && (serverData.size() != 0))
                                {
                                    Server server = new Server();
                                    server.setServerGuid((String) serverData.get(0)); // SYSTEM_GUID
                                    server.setOsName((String) serverData.get(1)); // SYSTEM_OSTYPE
                                    server.setServerStatus(ServerStatus.valueOf((String) serverData.get(2))); // SYSTEM_STATUS
                                    server.setServerRegion(ServiceRegion.valueOf((String) serverData.get(3))); // SYSTEM_REGION
                                    server.setNetworkPartition(NetworkPartition.valueOf((String) serverData.get(4))); // NETWORK_PARTITION
                                    server.setDatacenter(dataCenter); // datacenter as earlier obtained
                                    server.setServerType(ServerType.valueOf((String) serverData.get(6))); // SYSTEM_TYPE
                                    server.setDomainName((String) serverData.get(7)); // DOMAIN_NAME
                                    server.setCpuType((String) serverData.get(8)); // CPU_TYPE
                                    server.setCpuCount((Integer) serverData.get(9)); // CPU_COUNT
                                    server.setServerRack((String) serverData.get(10)); // SERVER_RACK
                                    server.setRackPosition((String) serverData.get(11)); // RACK_POSITION
                                    server.setServerModel((String) serverData.get(12)); // SERVER_MODEL
                                    server.setSerialNumber((String) serverData.get(13)); // SERIAL_NUMBER
                                    server.setInstalledMemory((Integer) serverData.get(14)); // INSTALLED_MEMORY
                                    server.setOperIpAddress((String) serverData.get(15)); // OPER_IP
                                    server.setOperHostName((String) serverData.get(16)); // OPER_HOSTNAME
                                    server.setMgmtIpAddress((String) serverData.get(17)); // MGMT_IP
                                    server.setMgmtHostName((String) serverData.get(18)); // MGMT_HOSTNAME
                                    server.setBkIpAddress((String) serverData.get(19)); // BKUP_IP
                                    server.setBkHostName((String) serverData.get(20)); // BKUP_HOSTNAME
                                    server.setNasIpAddress((String) serverData.get(21)); // NAS_IP
                                    server.setNasHostName((String) serverData.get(22)); // NAS_HOSTNAME
                                    server.setNatAddress((String) serverData.get(23)); // NAT_ADDR
                                    server.setServerComments((String) serverData.get(24)); // COMMENTS
                                    server.setDmgrPort((Integer) serverData.get(28)); // DMGR_PORT
                                    server.setOwningDmgr(platform.getPlatformDmgr()); // OWNING_DMGR
                                    server.setMgrUrl((String) serverData.get(30)); // MGR_ENTRY

                                    searchAccount = new UserAccount();
                                    searchAccount.setGuid((String) serverData.get(25));

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("UserAccount: {}", searchAccount);
                                    }

                                    svcAccount = new UserAccount();
                                    svcAccount.setUsername(serviceAccount.get(0));
                                    svcAccount.setGuid(serviceAccount.get(1));
                                    svcAccount.setRole(Role.valueOf(serviceAccount.get(2)));

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("UserAccount: {}", svcAccount);
                                    }

                                    searchRequest = new AccountControlRequest();
                                    searchRequest.setHostInfo(request.getRequestInfo());
                                    searchRequest.setUserAccount(searchAccount);
                                    searchRequest.setApplicationName(request.getApplicationName());
                                    searchRequest.setApplicationId(request.getApplicationId());
                                    searchRequest.setSearchType(SearchRequestType.GUID);
                                    searchRequest.setRequestor(svcAccount);

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("AccountControlRequest: {}", searchRequest);
                                    }

                                    try
                                    {
                                        searchResponse = acctControl.loadUserAccount(searchRequest);

                                        if (DEBUG)
                                        {
                                            DEBUGGER.debug("AccountControlResponse: {}", searchResponse);
                                        }

                                        if (searchResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                                        {
                                            server.setAssignedEngineer(searchResponse.getUserAccount()); // ASSIGNED_ENGINEER
                                        }
                                    }
                                    catch (AccountControlException acx)
                                    {
                                        ERROR_RECORDER.error(acx.getMessage(), acx);
                                    }

                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Server: {}", server);
                                    }

                                    webServerList.add(server);
                                }
                            }

                            if (DEBUG)
                            {
                                DEBUGGER.debug("webServerList: {}", webServerList);
                            }
                        }

                        Platform resPlatform = new Platform();
                        resPlatform.setPlatformGuid((String) platformData.get(0));
                        resPlatform.setPlatformName((String) platformData.get(1));
                        resPlatform.setPlatformRegion(ServiceRegion.valueOf((String) platformData.get(2)));
                        resPlatform.setDescription((String) platformData.get(6));
                        resPlatform.setAppServers(appServerList);
                        resPlatform.setWebServers(webServerList);
                        resPlatform.setPlatformDmgr(dmgr);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Platform: {}", resPlatform);
                        }

                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setResponse("Successfully loaded platform information.");
                        response.setPlatformData(resPlatform);
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                        response.setResponse("No platform was located with the provided information");
                    }
                }
                else
                {
                    throw new PlatformManagementException("No platform search data was provided. Cannot continue");
                }
            }
            else
            {
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
                response.setResponse("The requested user was not authorized to perform the operation");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new PlatformManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new PlatformManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LOADPLATFORM);
                auditEntry.setUserAccount(userAccount);
                auditEntry.setApplicationId(request.getApplicationId());
                auditEntry.setApplicationName(request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditEntry: {}", auditEntry);
                }

                AuditRequest auditRequest = new AuditRequest();
                auditRequest.setAuditEntry(auditEntry);

                if (DEBUG)
                {
                    DEBUGGER.debug("AuditRequest: {}", auditRequest);
                }

                auditor.auditRequest(auditRequest);
            }
            catch (AuditServiceException asx)
            {
                ERROR_RECORDER.error(asx.getMessage(), asx);
            }
        }

        return response;
    }
}
