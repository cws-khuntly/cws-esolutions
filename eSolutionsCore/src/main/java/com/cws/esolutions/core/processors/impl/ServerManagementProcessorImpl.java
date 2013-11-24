/**
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
import java.lang.reflect.Field;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.agent.Constants;
import com.cws.esolutions.core.utils.MQUtils;
import com.cws.esolutions.agent.dto.AgentRequest;
import com.cws.esolutions.agent.dto.AgentResponse;
import com.cws.esolutions.agent.enums.AgentStatus;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.core.processors.dto.Server;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.core.processors.enums.ServerType;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.ServerStatus;
import com.cws.esolutions.core.processors.enums.ServiceRegion;
import com.cws.esolutions.core.utils.exception.UtilityException;
import com.cws.esolutions.core.processors.enums.NetworkPartition;
import com.cws.esolutions.agent.processors.enums.SystemCheckType;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.agent.processors.dto.SystemManagerRequest;
import com.cws.esolutions.agent.processors.dto.SystemManagerResponse;
import com.cws.esolutions.agent.processors.enums.SystemManagementType;
import com.cws.esolutions.core.processors.dto.ServerManagementRequest;
import com.cws.esolutions.core.processors.dto.ServerManagementResponse;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.core.processors.exception.ServerManagementException;
import com.cws.esolutions.core.processors.interfaces.IServerManagementProcessor;
import com.cws.esolutions.security.access.control.exception.UserControlServiceException;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.impl
 * ServerManagementProcessorImpl.java
 *
 * $Id: $
 * $Author: $
 * $Date: $
 * $Revision: $
 * @author kmhuntly@gmail.com
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Oct 29, 2012 9:44:46 AM
 *     Created.
 */
public class ServerManagementProcessorImpl implements IServerManagementProcessor
{
    @Override
    public ServerManagementResponse addNewServer(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#addNewServer(final ServerManagementRequest request) throws ServerManagementException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: {}", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final Server requestServer = request.getTargetServer();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", requestServer);
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
                if (requestServer == null)
                {
                    throw new ServerManagementException("No server was provided. Cannot continue.");
                }

                // make sure all the platform data is there
                int validator = 0;

                try
                {
                    validator = serverDAO.validateServerHostName(requestServer.getOperHostName());
                }
                catch (SQLException sqx)
                {
                    ERROR_RECORDER.error(sqx.getMessage(), sqx);
                }

                if (DEBUG)
                {
                    DEBUGGER.debug("Validator: {}", validator);
                }

                if (validator == 0)
                {
                    // valid server
                    if ((requestServer.getServerType() == ServerType.VIRTUALHOST) || (requestServer.getServerType() == ServerType.DMGRSERVER))
                    {
                        if (StringUtils.isEmpty(requestServer.getMgrUrl()))
                        {
                            throw new ServerManagementException("Server type provided was " + requestServer.getServerType() + " but additional information is required.");
                        }
                        else if ((requestServer.getServerType() == ServerType.DMGRSERVER) && (requestServer.getDmgrPort() == 0))
                        {
                            throw new ServerManagementException("Server type provided was " + requestServer.getServerType() + " but additional information is required.");
                        }
                    }

                    List<Object> insertData = new ArrayList<Object>(
                            Arrays.asList(
                                    UUID.randomUUID().toString(),
                                    requestServer.getOsName(),
                                    requestServer.getServerStatus().name(),
                                    requestServer.getServerRegion().name(),
                                    requestServer.getNetworkPartition().name(),
                                    requestServer.getDatacenter(),
                                    requestServer.getServerType().name(),
                                    requestServer.getDomainName(),
                                    requestServer.getCpuType(),
                                    requestServer.getCpuCount(),
                                    requestServer.getServerModel(),
                                    requestServer.getSerialNumber(),
                                    requestServer.getInstalledMemory(),
                                    requestServer.getOperIpAddress(),
                                    requestServer.getOperHostName(),
                                    (StringUtils.isNotEmpty(requestServer.getMgmtIpAddress())) ? requestServer.getMgmtIpAddress() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getMgmtHostName())) ? requestServer.getMgmtHostName() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getBkIpAddress())) ? requestServer.getBkIpAddress() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getBkHostName())) ? requestServer.getBkHostName() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getNasIpAddress())) ? requestServer.getNasIpAddress() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getNasHostName())) ? requestServer.getNasHostName() : Constants.NOT_SET,
                                    (StringUtils.isNotEmpty(requestServer.getNatAddress())) ? requestServer.getNatAddress() : Constants.NOT_SET,
                                    requestServer.getServerComments(),
                                    userAccount.getUsername(),
                                    (StringUtils.isNotBlank(requestServer.getMgrUrl())) ? requestServer.getMgrUrl() : Constants.NOT_SET,
                                    requestServer.getDmgrPort(),
                                    (StringUtils.isNotBlank(requestServer.getServerRack())) ? requestServer.getServerRack() : Constants.NOT_SET,
                                    (StringUtils.isNotBlank(requestServer.getRackPosition())) ? requestServer.getRackPosition() : Constants.NOT_SET,
                                    (StringUtils.isNotBlank(requestServer.getOwningDmgr())) ? requestServer.getOwningDmgr() : Constants.NOT_SET));

                    if (DEBUG)
                    {
                        for (Object str : insertData)
                        {
                            DEBUGGER.debug("Value: {}", str);
                        }
                    }

                    boolean isComplete = serverDAO.addNewServer(insertData);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (isComplete)
                    {
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setResponse("Successfully added " + requestServer.getOperHostName() + " to the asset datasource");
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                        response.setResponse("Failed to add " + requestServer.getOperHostName() + " to the asset datasource");
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                    response.setResponse("Server " + requestServer.getOperHostName() + " already exists in the asset datasource.");
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

            throw new ServerManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new ServerManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.ADDSERVER);
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

    @Override
    public ServerManagementResponse updateServerData(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#updateServerData(final ServerManagementRequest request) throws ServerManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: {}", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final Server requestServer = request.getTargetServer();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", requestServer);
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
                if ((requestServer.getServerType() == ServerType.VIRTUALHOST) || (requestServer.getServerType() == ServerType.DMGRSERVER))
                {
                    if (StringUtils.isEmpty(requestServer.getMgrUrl()))
                    {
                        throw new ServerManagementException("Server type provided was " + requestServer.getServerType() + " but additional information is required.");
                    }
                    else if ((requestServer.getServerType() == ServerType.DMGRSERVER) && (requestServer.getDmgrPort() == 0))
                    {
                        throw new ServerManagementException("Server type provided was " + requestServer.getServerType() + " but additional information is required.");
                    }
                }

                // load the server
                List<Object> serverData = serverDAO.getInstalledServer(requestServer.getServerGuid());

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
                    server.setDatacenter((String) serverData.get(5)); // datacenter as earlier obtained
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
                    server.setAssignedEngineer((String) serverData.get(25)); // ASSIGNED_ENGINEER
                    server.setDmgrPort((Integer) serverData.get(28)); // DMGR_PORT
                    server.setOwningDmgr((String) serverData.get(29)); // OWNING_DMGR
                    server.setMgrUrl((String) serverData.get(30)); // MGR_ENTRY

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", server);
                    }

                    for (Field field : requestServer.getClass().getDeclaredFields())
                    {
                        field.setAccessible(true); // private fields, make them accessible

                        if (DEBUG)
                        {
                            DEBUGGER.debug("field: {}", field);
                        }

                        if (!(field.getName().equals("methodName")) &&
                                (!(field.getName().equals("CNAME"))) &&
                                (!(field.getName().equals("DEBUGGER"))) &&
                                (!(field.getName().equals("DEBUG"))) &&
                                (!(field.getName().equals("ERROR_RECORDER"))) &&
                                (!(field.getName().equals("serialVersionUID"))))
                        {
                            try
                            {
                                if (field.get(requestServer) != null)
                                {
                                    if (DEBUG)
                                    {
                                        DEBUGGER.debug("Value: {}", field.get(requestServer));
                                    }

                                    if (!(field.get(requestServer).equals(field.get(server))))
                                    {
                                        field.set(server, field.get(requestServer));
                                    }

                                    break;
                                }
                            }
                            catch (IllegalAccessException iax)
                            {
                                ERROR_RECORDER.error(iax.getMessage(), iax);
                            }
                        }
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("Server: {}", server);
                    }

                    List<Object> insertData = new ArrayList<Object>(
                            Arrays.asList(
                                    server.getOsName(),
                                    server.getServerStatus().name(),
                                    server.getServerRegion().name(),
                                    server.getNetworkPartition().name(),
                                    server.getDatacenter(),
                                    server.getServerType().name(),
                                    server.getDomainName(),
                                    server.getCpuType(),
                                    String.valueOf(server.getCpuCount()),
                                    server.getServerModel(),
                                    server.getSerialNumber(),
                                    String.valueOf(server.getInstalledMemory()),
                                    server.getOperIpAddress(),
                                    server.getOperHostName(),
                                    server.getMgmtIpAddress(),
                                    server.getMgmtHostName(),
                                    server.getBkIpAddress(),
                                    server.getBkHostName(),
                                    server.getNasIpAddress(),
                                    server.getNasHostName(),
                                    server.getNatAddress(),
                                    requestServer.getServerComments(),
                                    requestServer.getAssignedEngineer(),
                                    requestServer.getMgrUrl(),
                                    String.valueOf(requestServer.getDmgrPort()),
                                    requestServer.getServerRack(),
                                    requestServer.getRackPosition(),
                                    requestServer.getOwningDmgr()));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String>: {}", insertData);
                    }

                    boolean isComplete = serverDAO.modifyServerData(server.getServerGuid(), insertData);

                    if (DEBUG)
                    {
                        DEBUGGER.debug("isComplete: {}", isComplete);
                    }

                    if (isComplete)
                    {
                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setResponse("Successfully updated " + requestServer.getOperHostName() + " in the asset datasource");
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                        response.setResponse("Failed to update " + requestServer.getOperHostName() + " in the asset datasource");
                    }
                }
                else
                {
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                    response.setResponse("No server was located with the provided information");
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

            throw new ServerManagementException(ucsx.getMessage(), ucsx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServerManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.UPDATESERVER);
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

    @Override
    public ServerManagementResponse listServersByType(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#listServersByType(final ServerManagementRequest request) throws ServerManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: {}", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final Server requestServer = request.getTargetServer();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", requestServer);
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
                List<Object[]> serverData = serverDAO.getServersByAttribute(requestServer.getServerType().name(), request.getStartPage());

                if (DEBUG)
                {
                    DEBUGGER.debug("serverList: {}", serverData);
                }

                if ((serverData != null) && (serverData.size() != 0))
                {
                    List<Server> serverList = new ArrayList<>();

                    for (Object[] data : serverData)
                    {
                        if (DEBUG)
                        {
                            for (Object obj : data)
                            {
                                DEBUGGER.debug("Value: {}", obj);
                            }
                        }

                        Server server = new Server();
                        server.setServerGuid((String) data[0]); // SYSTEM_GUID
                        server.setOsName((String) data[1]); // SYSTEM_OSTYPE
                        server.setServerStatus(ServerStatus.valueOf((String) data[2])); // SYSTEM_STATUS
                        server.setServerRegion(ServiceRegion.valueOf((String) data[3])); // SYSTEM_REGION
                        server.setNetworkPartition(NetworkPartition.valueOf((String) data[4])); // NETWORK_PARTITION
                        server.setDatacenter((String) data[5]); // datacenter as earlier obtained
                        server.setServerType(ServerType.valueOf((String) data[6])); // SYSTEM_TYPE
                        server.setDomainName((String) data[7]); // DOMAIN_NAME
                        server.setCpuType((String) data[8]); // CPU_TYPE
                        server.setCpuCount((Integer) data[9]); // CPU_COUNT
                        server.setServerRack((String) data[10]); // SERVER_RACK
                        server.setRackPosition((String) data[11]); // RACK_POSITION
                        server.setServerModel((String) data[12]); // SERVER_MODEL
                        server.setSerialNumber((String) data[13]); // SERIAL_NUMBER
                        server.setInstalledMemory((Integer) data[14]); // INSTALLED_MEMORY
                        server.setOperIpAddress((String) data[15]); // OPER_IP
                        server.setOperHostName((String) data[16]); // OPER_HOSTNAME
                        server.setMgmtIpAddress((String) data[17]); // MGMT_IP
                        server.setMgmtHostName((String) data[18]); // MGMT_HOSTNAME
                        server.setBkIpAddress((String) data[19]); // BKUP_IP
                        server.setBkHostName((String) data[20]); // BKUP_HOSTNAME
                        server.setNasIpAddress((String) data[21]); // NAS_IP
                        server.setNasHostName((String) data[22]); // NAS_HOSTNAME
                        server.setNatAddress((String) data[23]); // NAT_ADDR
                        server.setServerComments((String) data[24]); // COMMENTS
                        server.setAssignedEngineer((String) data[25]); // ASSIGNED_ENGINEER
                        server.setDmgrPort((Integer) data[26]); // DMGR_PORT
                        server.setOwningDmgr((String) data[27]); // OWNING_DMGR
                        server.setMgrUrl((String) data[28]); // MGR_ENTRY

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", server);
                        }

                        serverList.add(server);
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("serverList: {}", serverList);
                    }

                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setResponse("Successfully loaded installed server information.");
                    response.setServerList(serverList);
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

            throw new ServerManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new ServerManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LISTSERVERS);
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

    @Override
    public ServerManagementResponse listServersByDmgr(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#listServersByDmgr(final ServerManagementRequest request) throws ServerManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: {}", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final Server sourceServer = request.getSourceServer(); // dmgr
        final Server requestServer = request.getTargetServer(); // search server type
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", sourceServer);
            DEBUGGER.debug("Server: {}", requestServer);
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
                List<Object[]> serverData = serverDAO.getServersByAttribute(requestServer.getServerType().name(), request.getStartPage());

                if (DEBUG)
                {
                    DEBUGGER.debug("serverList: {}", serverData);
                }

                if ((serverData != null) && (serverData.size() != 0))
                {
                    List<Server> serverList = new ArrayList<>();

                    for (Object[] data : serverData)
                    {
                        if (StringUtils.equals((String) data[29], sourceServer.getServerGuid()))
                        {
                            if (DEBUG)
                            {
                                for (Object obj : data)
                                {
                                    DEBUGGER.debug("Value: {}", obj);
                                }
                            }

                            Server server = new Server();
                            server.setServerGuid((String) data[0]); // SYSTEM_GUID
                            server.setOsName((String) data[1]); // SYSTEM_OSTYPE
                            server.setServerStatus(ServerStatus.valueOf((String) data[2])); // SYSTEM_STATUS
                            server.setServerRegion(ServiceRegion.valueOf((String) data[3])); // SYSTEM_REGION
                            server.setNetworkPartition(NetworkPartition.valueOf((String) data[4])); // NETWORK_PARTITION
                            server.setDatacenter((String) data[5]); // datacenter as earlier obtained
                            server.setServerType(ServerType.valueOf((String) data[6])); // SYSTEM_TYPE
                            server.setDomainName((String) data[7]); // DOMAIN_NAME
                            server.setCpuType((String) data[8]); // CPU_TYPE
                            server.setCpuCount((Integer) data[9]); // CPU_COUNT
                            server.setServerRack((String) data[10]); // SERVER_RACK
                            server.setRackPosition((String) data[11]); // RACK_POSITION
                            server.setServerModel((String) data[12]); // SERVER_MODEL
                            server.setSerialNumber((String) data[13]); // SERIAL_NUMBER
                            server.setInstalledMemory((Integer) data[14]); // INSTALLED_MEMORY
                            server.setOperIpAddress((String) data[15]); // OPER_IP
                            server.setOperHostName((String) data[16]); // OPER_HOSTNAME
                            server.setMgmtIpAddress((String) data[17]); // MGMT_IP
                            server.setMgmtHostName((String) data[18]); // MGMT_HOSTNAME
                            server.setBkIpAddress((String) data[19]); // BKUP_IP
                            server.setBkHostName((String) data[20]); // BKUP_HOSTNAME
                            server.setNasIpAddress((String) data[21]); // NAS_IP
                            server.setNasHostName((String) data[22]); // NAS_HOSTNAME
                            server.setNatAddress((String) data[23]); // NAT_ADDR
                            server.setServerComments((String) data[24]); // COMMENTS
                            server.setAssignedEngineer((String) data[25]); // ASSIGNED_ENGINEER
                            server.setDmgrPort((Integer) data[26]); // DMGR_PORT
                            server.setOwningDmgr((String) data[27]); // OWNING_DMGR
                            server.setMgrUrl((String) data[28]); // MGR_ENTRY

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Server: {}", server);
                            }

                            serverList.add(server);
                        }
                    }

                    if (DEBUG)
                    {
                        DEBUGGER.debug("serverList: {}", serverList);
                    }

                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setResponse("Successfully loaded installed server information.");
                    response.setServerList(serverList);
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

            throw new ServerManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new ServerManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LISTSERVERS);
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

    @Override
    public ServerManagementResponse getServerData(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#getServerData(final ServerManagementRequest request) throws ServerManagementException";
        
        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: {}", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final Server requestServer = request.getTargetServer();
        final UserAccount userAccount = request.getUserAccount();
        final RequestHostInfo reqInfo = request.getRequestInfo();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", requestServer);
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
                if (requestServer != null)
                {
                    List<Object> serverData = serverDAO.getInstalledServer(requestServer.getServerGuid());

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
                        server.setDatacenter((String) serverData.get(5)); // datacenter as earlier obtained
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
                        server.setAssignedEngineer((String) serverData.get(25)); // ASSIGNED_ENGINEER
                        server.setDmgrPort((Integer) serverData.get(26)); // DMGR_PORT
                        server.setOwningDmgr((String) serverData.get(27)); // OWNING_DMGR
                        server.setMgrUrl((String) serverData.get(28)); // MGR_ENTRY

                        if (DEBUG)
                        {
                            DEBUGGER.debug("Server: {}", server);
                        }

                        response.setRequestStatus(CoreServicesStatus.SUCCESS);
                        response.setResponse("Successfully loaded installed server information.");
                        response.setServer(server);
                    }
                    else
                    {
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                        response.setResponse("No server was located with the provided information");
                    }
                }
                else
                {
                    throw new ServerManagementException("No server search data was provided. Cannot continue");
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

            throw new ServerManagementException(sqx.getMessage(), sqx);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);
            
            throw new ServerManagementException(ucsx.getMessage(), ucsx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.GETSERVER);
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

    @Override
    public ServerManagementResponse runNetstatCheck(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#runNetstatCheck(final ServerManagementRequest request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: ", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            SystemManagerRequest systemReq = new SystemManagerRequest();
            systemReq.setMgmtType(SystemManagementType.SYSTEMCHECK);
            systemReq.setRequestType(SystemCheckType.NETSTAT);
            systemReq.setPortNumber(request.getPortNumber());

            if (DEBUG)
            {
                DEBUGGER.debug("SystemManagerRequest: {}", request);
            }

            Server sourceServer = request.getSourceServer();

            if (DEBUG)
            {
                DEBUGGER.debug("Server: {}", sourceServer);
            }

            AgentRequest agentRequest = new AgentRequest();
            agentRequest.setAppName(appConfig.getAppName());
            agentRequest.setRequestPayload(systemReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AgentRequest: {}", agentRequest);
            }

            // always make the tcp conn to the oper hostname - thats where the agent should be listening
            String correlator = MQUtils.sendMqMessage(agentRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("correlator: {}", correlator);
            }

            if (StringUtils.isNotEmpty(correlator))
            {
                AgentResponse agentResponse = (AgentResponse) MQUtils.getMqMessage(correlator);

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentResponse: {}", agentResponse);
                }

                if (agentResponse.getRequestStatus() == AgentStatus.SUCCESS)
                {
                    SystemManagerResponse systemRes = (SystemManagerResponse) agentResponse.getResponsePayload();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SystemManagerResponse: {}", systemRes);
                    }

                    response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                    response.setResponse(systemRes.getResponse());
                    response.setResponseObject(systemRes.getResponseData());
                }
                else
                {
                    response.setResponse(agentResponse.getResponse());
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
                response.setResponse("Failed to send message to configured request queue for action");
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new ServerManagementException(ux.getMessage(), ux);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.NETSTAT);
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

    @Override
    public ServerManagementResponse runTelnetCheck(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#runTelnetCheck(final ServerManagementRequest request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: ", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final Server sourceServer = request.getSourceServer();
        final Server targetServer = request.getTargetServer();
        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("Server: {}", sourceServer);
            DEBUGGER.debug("Server: {}", targetServer);
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
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
                if (sourceServer != null)
                {
                    List<Object> serverData = serverDAO.getInstalledServer(sourceServer.getServerGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("serverData: {}", serverData);
                    }

                    if ((serverData != null) && (serverData.size() != 0))
                    {
                        SystemManagerRequest systemReq = new SystemManagerRequest();
                        systemReq.setMgmtType(SystemManagementType.SYSTEMCHECK);
                        systemReq.setRequestType(SystemCheckType.TELNET);
                        systemReq.setPortNumber(request.getPortNumber());
                        systemReq.setTargetServer(request.getTargetServer().getOperHostName());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("SystemManagerRequest: {}", request);
                        }

                        AgentRequest agentRequest = new AgentRequest();
                        agentRequest.setAppName(appConfig.getAppName());
                        agentRequest.setRequestPayload(systemReq);
                        agentRequest.setHostname(sourceServer.getOperHostName());

                        if (DEBUG)
                        {
                            DEBUGGER.debug("AgentRequest: {}", agentRequest);
                        }

                        // always make the tcp conn to the oper hostname - thats where the agent should be listening
                        String correlator = MQUtils.sendMqMessage(agentRequest);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("correlator: {}", correlator);
                        }

                        if (StringUtils.isNotEmpty(correlator))
                        {
                            AgentResponse agentResponse = (AgentResponse) MQUtils.getMqMessage(correlator);

                            if (DEBUG)
                            {
                                DEBUGGER.debug("AgentResponse: {}", agentResponse);
                            }

                            if (agentResponse.getRequestStatus() == AgentStatus.SUCCESS)
                            {
                                SystemManagerResponse systemRes = (SystemManagerResponse) agentResponse.getResponsePayload();

                                if (DEBUG)
                                {
                                    DEBUGGER.debug("SystemManagerResponse: {}", systemRes);
                                }

                                response.setRequestStatus(CoreServicesStatus.SUCCESS);
                                response.setResponse(systemRes.getResponse());
                                response.setResponseObject(systemRes.getResponseData());
                            }
                            else
                            {
                                response.setResponse(agentResponse.getResponse());
                                response.setRequestStatus(CoreServicesStatus.FAILURE);
                            }
                        }
                        else
                        {
                            response.setResponse("Failed to send message to configured request queue for action");
                            response.setRequestStatus(CoreServicesStatus.FAILURE);
                        }
                    }
                    else
                    {
                        response.setResponse("Failed to locate a source server with the given information.");
                        response.setRequestStatus(CoreServicesStatus.FAILURE);
                    }
                }
                else
                {
                    throw new ServerManagementException("No source server was provided. Cannot continue.");
                }
            }
            else
            {
                response.setResponse("The requesting user was not authorized to perform the operation. Cannot continue.");
                response.setRequestStatus(CoreServicesStatus.UNAUTHORIZED);
            }
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new ServerManagementException(ux.getMessage(), ux);
        }
        catch (UserControlServiceException ucsx)
        {
            ERROR_RECORDER.error(ucsx.getMessage(), ucsx);

            throw new ServerManagementException(ucsx.getMessage(), ucsx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new ServerManagementException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.TELNET);
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

    @Override
    public ServerManagementResponse runRemoteDateCheck(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#runRemoteDateCheck(final ServerManagementRequest request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: ", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            SystemManagerRequest systemReq = new SystemManagerRequest();
            systemReq.setMgmtType(SystemManagementType.SYSTEMCHECK);
            systemReq.setRequestType(SystemCheckType.REMOTEDATE);

            if (DEBUG)
            {
                DEBUGGER.debug("SystemManagerRequest: {}", request);
            }

            AgentRequest agentRequest = new AgentRequest();
            agentRequest.setAppName(appConfig.getAppName());
            agentRequest.setRequestPayload(systemReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AgentRequest: {}", agentRequest);
            }

            // always make the tcp conn to the oper hostname - thats where the agent should be listening
            String correlator = MQUtils.sendMqMessage(agentRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("correlator: {}", correlator);
            }

            if (StringUtils.isNotEmpty(correlator))
            {
                AgentResponse agentResponse = (AgentResponse) MQUtils.getMqMessage(correlator);

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentResponse: {}", agentResponse);
                }

                if (agentResponse.getRequestStatus() == AgentStatus.SUCCESS)
                {
                    SystemManagerResponse systemRes = (SystemManagerResponse) agentResponse.getResponsePayload();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SystemManagerResponse: {}", systemRes);
                    }

                    response.setRequestStatus(CoreServicesStatus.SUCCESS);
                    response.setResponse(systemRes.getResponse());
                    response.setResponseObject(systemRes.getResponseData());
                }
                else
                {
                    response.setResponse(agentResponse.getResponse());
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
                response.setResponse("Failed to send message to configured request queue for action");
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new ServerManagementException(ux.getMessage(), ux);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.REMOTEDATE);
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

    @Override
    public ServerManagementResponse runProcessListCheck(final ServerManagementRequest request) throws ServerManagementException
    {
        final String methodName = IServerManagementProcessor.CNAME + "#runProcessListCheck(final ServerManagementRequest request)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServerManagementRequest: ", request);
        }

        ServerManagementResponse response = new ServerManagementResponse();

        final RequestHostInfo reqInfo = request.getRequestInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            SystemManagerRequest systemReq = new SystemManagerRequest();
            systemReq.setMgmtType(SystemManagementType.SYSTEMCHECK);
            systemReq.setRequestType(SystemCheckType.PROCESSLIST);
            systemReq.setProcessName(request.getProcessName());

            if (DEBUG)
            {
                DEBUGGER.debug("SystemManagerRequest: {}", request);
            }

            Server sourceServer = request.getSourceServer();

            if (DEBUG)
            {
                DEBUGGER.debug("Server: {}", sourceServer);
            }

            AgentRequest agentRequest = new AgentRequest();
            agentRequest.setAppName(appConfig.getAppName());
            agentRequest.setRequestPayload(systemReq);

            if (DEBUG)
            {
                DEBUGGER.debug("AgentRequest: {}", agentRequest);
            }

            // always make the tcp conn to the oper hostname - thats where the agent should be listening
            String correlator = MQUtils.sendMqMessage(agentRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("correlator: {}", correlator);
            }

            if (StringUtils.isNotEmpty(correlator))
            {
                AgentResponse agentResponse = (AgentResponse) MQUtils.getMqMessage(correlator);

                if (DEBUG)
                {
                    DEBUGGER.debug("AgentResponse: {}", agentResponse);
                }

                if (agentResponse.getRequestStatus() == AgentStatus.SUCCESS)
                {
                    SystemManagerResponse systemRes = (SystemManagerResponse) agentResponse.getResponsePayload();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("SystemManagerResponse: {}", systemRes);
                    }

                    response.setRequestStatus(CoreServicesStatus.valueOf(systemRes.getRequestStatus().name()));
                    response.setResponse(systemRes.getResponse());
                    response.setResponseObject(systemRes.getResponseData());
                }
                else
                {
                    response.setResponse(agentResponse.getResponse());
                    response.setRequestStatus(CoreServicesStatus.FAILURE);
                }
            }
            else
            {
                response.setResponse("Failed to send message to configured request queue for action");
                response.setRequestStatus(CoreServicesStatus.FAILURE);
            }
        }
        catch (UtilityException ux)
        {
            ERROR_RECORDER.error(ux.getMessage(), ux);

            throw new ServerManagementException(ux.getMessage(), ux);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.NETSTAT);
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
