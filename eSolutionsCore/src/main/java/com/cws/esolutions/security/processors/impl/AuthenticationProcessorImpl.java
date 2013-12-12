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
package com.cws.esolutions.security.processors.impl;

import java.util.Map;
import java.util.List;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.SQLException;
import com.unboundid.ldap.sdk.ResultCode;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.enums.Role;
import com.cws.esolutions.security.enums.SaltType;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.audit.dto.AuditEntry;
import com.cws.esolutions.security.audit.enums.AuditType;
import com.cws.esolutions.security.audit.dto.AuditRequest;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.exception.SecurityServiceException;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.usermgmt.enums.SearchRequestType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.audit.exception.AuditServiceException;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
import com.cws.esolutions.security.dao.usermgmt.exception.UserManagementException;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.security.audit.processors.interfaces
 * File: IAuditProcessor.java
 *
 * History
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20             Created.
 */
/**
 * @see com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor
 */
public class AuthenticationProcessorImpl implements IAuthenticationProcessor
{
    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor#processAgentLogon(com.cws.esolutions.security.processors.dto.AuthenticationRequest)
     */
    @Override
    public AuthenticationResponse processAgentLogon(final AuthenticationRequest request) throws AuthenticationException
    {
        final String methodName = IAuthenticationProcessor.CNAME + "#processAgentLogon(final AuthenticationRequest request) throws AuthenticationException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationRequest: {}", request);
        }

        UserAccount userAccount = null;
        AuthenticationResponse response = new AuthenticationResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount authUser = request.getUserAccount();
        final UserSecurity authSec = request.getUserSecurity();
        final int maxAttempts = (secConfig.getMaxAttempts() != 0) ? secConfig.getMaxAttempts() : SecurityConstants.DEF_AUTH_MAX_ATTEMPTS;

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", authUser);
            DEBUGGER.debug("maxAttempts: {}", maxAttempts);
        }

        try
        {
            String password = null;
            List<String[]> userInfo = null;
            UserAccount authAccount = null;

            userInfo = userManager.searchUsers(SearchRequestType.USERNAME, authUser.getUsername());

            if (DEBUG)
            {
                DEBUGGER.debug("User list: {}", userInfo);
            }

            if ((userInfo == null) || (userInfo.size() == 0) || (userInfo.size() > 1))
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                response.setResponse("Invalid authentication data.");
            }
            else
            {
                String[] userData = userInfo.get(0);

                if (DEBUG)
                {
                    for (String str : userData)
                    {
                        DEBUGGER.debug("userData: {}", str);
                    }
                }

                authAccount = new UserAccount();
                authAccount.setGuid(userData[0]);
                authAccount.setUsername(userData[1]);

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", authAccount);
                }

                switch (request.getLoginType())
                {
                    case USERNAME:
                        // set the status flag to success
                        if (request.getAuthType() == AuthenticationType.RESET)
                        {
                            authAccount.setStatus(LoginStatus.RESET);
                        }
                        else
                        {
                            authAccount.setStatus(LoginStatus.SUCCESS);
                        }

                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                        response.setResponse("Successfully loaded user account");
                        response.setUserAccount(authAccount);

                        return response;
                    case OTP:
                        throw new AuthenticatorException("OTP authentication has not yet been implemented.");
                    default:
                        String userSalt = userSec.getUserSalt(authAccount.getGuid(), SaltType.LOGON.name());

                        if (StringUtils.isNotEmpty(userSalt))
                        {
                            password = PasswordUtils.encryptText(
                                    authSec.getPassword(),
                                    userSalt,
                                    svcBean.getConfigData().getSecurityConfig().getAuthAlgorithm(),
                                    svcBean.getConfigData().getSecurityConfig().getIterations());
                        }
                        else
                        {
                            throw new AuthenticationException("Unable to obtain configured user salt. Cannot continue");
                        }

                        break;
                }
            }

            if (DEBUG)
            {
                DEBUGGER.debug("userInfo: {}", userInfo);
            }

            if (authAccount != null)
            {
                List<Object> userData = authenticator.performLogon(authAccount.getGuid(), authAccount.getUsername(), password, request.getApplicationName());

                if (DEBUG)
                {
                    DEBUGGER.debug("UserData: {}", userData);
                }

                if ((userData != null) && (!(userData.isEmpty())))
                {
                    userAccount = new UserAccount();
                    userAccount.setGuid((String) userData.get(0));
                    userAccount.setUsername((String) userData.get(1));
                    userAccount.setGivenName((String) userData.get(2));
                    userAccount.setSurname((String) userData.get(3));
                    userAccount.setDisplayName((String) userData.get(4));
                    userAccount.setEmailAddr((String) userData.get(5));
                    userAccount.setPagerNumber((String) userData.get(6));
                    userAccount.setTelephoneNumber((String) userData.get(7));
                    userAccount.setRole(Role.valueOf((String) userData.get(8)));
                    userAccount.setFailedCount((Integer) userData.get(9));
                    userAccount.setLastLogin(new Date((Long) userData.get(10)));
                    userAccount.setExpiryDate((Long) userData.get(11));
                    userAccount.setSuspended((Boolean) userData.get(12));
                    userAccount.setOlrSetup((Boolean) userData.get(13));
                    userAccount.setOlrLocked((Boolean) userData.get(14));
                    userAccount.setTcAccepted((Boolean) userData.get(15));

                    // list user services
                    List<String> serviceList = svcInfo.listServicesForUser(userAccount.getGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String>: {}", serviceList);
                    }

                    if ((serviceList != null) && (serviceList.size() != 0))
                    {
                        userAccount.setServiceList(serviceList);
                    }

                    List<String> authorizedProjects = svcInfo.returnUserAuthorizedProjects(userAccount.getGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("List<String>: {}", authorizedProjects);
                    }

                    if ((authorizedProjects != null) && (authorizedProjects.size() != 0))
                    {
                        userAccount.setProjectList(authorizedProjects);
                    }

                    // have a user account, run with it
                    if (userAccount.isSuspended())
                    {
                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                        response.setResponse("Requested account is currently suspended.");
                    }
                    else
                    {
                        // user active, keep going
                        if (userAccount.getFailedCount() >= maxAttempts)
                        {
                            // user locked
                            response.setRequestStatus(SecurityRequestStatus.FAILURE);
                            response.setResponse("Requested user account has been locked");
                        }
                        else
                        {
                            // reset the failed count, this is a successful logon
                            try
                            {
                                userManager.modifyUserInformation((String) userData.get(1), (String) userData.get(0), new HashMap<String, Object>()
                                        {
                                            private static final long serialVersionUID = 3026623264042376743L;
    
                                            {
                                                put(authData.getLockCount(), 0);
                                                put(authData.getLastLogin(), System.currentTimeMillis());
                                            }
                                        });
                            }
                            catch (UserManagementException umx)
                            {
                                ERROR_RECORDER.error(umx.getMessage(), umx);
                            }

                            // user not already logged in or concurrent auth is allowed
                            if (System.currentTimeMillis() >= userAccount.getExpiryDate())
                            {
                                userAccount.setStatus(LoginStatus.EXPIRED);

                                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                                response.setResponse("Authentication request successfully processed");
                                response.setUserAccount(userAccount);
                            }
                            else
                            {
                                userAccount.setStatus(LoginStatus.SUCCESS);

                                response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                                response.setResponse("Authentication request successfully processed");
                                response.setUserAccount(userAccount);
                            }
                        }
                    }
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    response.setResponse("Login failed. Please ensure your login information is correct.");
                }
            }
            else
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                response.setResponse("Login failed. Please ensure your login information is correct.");
            }

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationResponse: {}", response);
            }
        }
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            try
            {
                if (ax.getResultCode() == ResultCode.INVALID_CREDENTIALS)
                {
                    // failed authentication, update counter
                    // find out if this is a valid user...
                    List<String[]> userList = userManager.searchUsers(SearchRequestType.USERNAME, authUser.getUsername());

                    // only do the work if the userlist is equal to 1.
                    // if there were 150 users found then we dont want
                    // to shoot them all
                    if ((userList != null) && (userList.size() == 1))
                    {
                        String[] lockInfo = userList.get(0);

                        if (DEBUG)
                        {
                            for (String str : lockInfo)
                            {
                                DEBUGGER.debug("User Info: {}", str);
                            }
                        }

                        String userGuid = lockInfo[0];
                        int currentCount = Integer.parseInt(lockInfo[9]);

                        if (DEBUG)
                        {
                            DEBUGGER.debug("userGuid: {}", userGuid);
                            DEBUGGER.debug("currentCount: {}", currentCount);
                        }

                        // do it
                        authenticator.lockUserAccount(userGuid, currentCount);
                    }
                }
            }
            catch (UserManagementException umx)
            {
                ERROR_RECORDER.error(umx.getMessage(), umx);
            }
            catch (AuthenticatorException ax1)
            {
                ERROR_RECORDER.error(ax1.getMessage(), ax1);
            }

            response.setRequestStatus(SecurityRequestStatus.FAILURE);
            response.setResponse("Authentication request failed.");
        }
        catch (SecurityServiceException ssx)
        {
            ERROR_RECORDER.error(ssx.getMessage(), ssx);

            throw new AuthenticationException(ssx.getMessage(), ssx);
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuthenticationException(sqx.getMessage(), sqx);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.LOGON);
                auditEntry.setUserAccount(authUser);
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
     * @see com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor#obtainUserSecurityConfig(com.cws.esolutions.security.processors.dto.AuthenticationRequest)
     */
    @Override
    public AuthenticationResponse obtainUserSecurityConfig(final AuthenticationRequest request) throws AuthenticationException
    {
        final String methodName = IAuthenticationProcessor.CNAME + "#obtainUserSecurityConfig(final AuthenticationRequest request) throws AuthenticationException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationRequest: {}", request);
        }

        UserAccount resAccount = null;
        AuthenticationResponse response = new AuthenticationResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            List<String[]> userInfo = userManager.searchUsers(SearchRequestType.USERNAME, userAccount.getUsername());

            if (DEBUG)
            {
                DEBUGGER.debug("User list: {}", userInfo);
            }

            if ((userInfo == null) || (userInfo.size() == 0) || (userInfo.size() > 1))
            {
                response.setRequestStatus(SecurityRequestStatus.FAILURE);
                response.setResponse("Invalid authentication data.");
            }
            else
            {
                String[] userData = userInfo.get(0);

                if (DEBUG)
                {
                    for (String str : userData)
                    {
                        DEBUGGER.debug("userData: {}", str);
                    }
                }

                List<Object> accountInfo = userManager.loadUserAccount(userData[0]);

                if (DEBUG)
                {
                    DEBUGGER.debug("List<Object>: {}", accountInfo);
                }

                if ((accountInfo != null) && accountInfo.size() != 0)
                {
                    resAccount = new UserAccount();
                    resAccount.setGuid((String) accountInfo.get(0));
                    resAccount.setUsername((String) accountInfo.get(1));
                    resAccount.setGivenName((String) accountInfo.get(2));
                    resAccount.setSurname((String) accountInfo.get(3));
                    resAccount.setDisplayName((String) accountInfo.get(4));
                    resAccount.setEmailAddr((String) accountInfo.get(5));
                    resAccount.setPagerNumber((String) accountInfo.get(6));
                    resAccount.setTelephoneNumber((String) accountInfo.get(7));
                    resAccount.setRole(Role.valueOf((String) accountInfo.get(8)));
                    resAccount.setFailedCount((Integer) accountInfo.get(9));
                    resAccount.setLastLogin(new Date((Long) accountInfo.get(10)));
                    resAccount.setExpiryDate((Long) accountInfo.get(11));
                    resAccount.setSuspended((Boolean) accountInfo.get(12));
                    resAccount.setOlrSetup((Boolean) accountInfo.get(13));
                    resAccount.setOlrLocked((Boolean) accountInfo.get(14));
                    resAccount.setTcAccepted((Boolean) accountInfo.get(15));

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", resAccount);
                    }

                    List<String> securityData = authenticator.obtainSecurityData(resAccount.getUsername(), resAccount.getGuid());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("UserAccount: {}", userAccount);
                    }

                    if ((securityData != null) && (!(securityData.isEmpty())))
                    {
                        UserSecurity userSecurity = new UserSecurity();
                        userSecurity.setSecQuestionOne(securityData.get(0));
                        userSecurity.setSecQuestionTwo(securityData.get(1));

                        if (DEBUG)
                        {
                            DEBUGGER.debug("UserSecurity: {}", userSecurity);
                        }

                        response.setUserAccount(resAccount);
                        response.setRequestStatus(SecurityRequestStatus.SUCCESS);
                        response.setResponse("Account successfully loaded");
                        response.setUserSecurity(userSecurity);
                    }
                    else
                    {
                        // null data
                        response.setRequestStatus(SecurityRequestStatus.FAILURE);
                        response.setResponse("Unable to load security questions. Cannot continue.");
                    }
                }
                else
                {
                    response.setRequestStatus(SecurityRequestStatus.FAILURE);
                    response.setResponse("Unable to locate provided user account. Cannot continue.");
                }
            }

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationResponse: {}", response);
            }
        }
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new AuthenticationException(ax.getMessage(), ax);
        }
        catch (UserManagementException umx)
        {
            ERROR_RECORDER.error(umx.getMessage(), umx);

            throw new AuthenticationException(umx.getMessage(), umx);
        }
        finally
        {
            if (resAccount != null)
            {
                // audit
                try
                {
                    AuditEntry auditEntry = new AuditEntry();
                    auditEntry.setHostInfo(reqInfo);
                    auditEntry.setAuditType(AuditType.LOADSECURITY);
                    auditEntry.setUserAccount(resAccount);
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
        }

        return response;
    }

    /**
     * @see com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor#verifyUserSecurityConfig(com.cws.esolutions.security.processors.dto.AuthenticationRequest)
     */
    @Override
    public AuthenticationResponse verifyUserSecurityConfig(final AuthenticationRequest request) throws AuthenticationException
    {
        final String methodName = IAuthenticationProcessor.CNAME + "#verifyUserSecurityConfig(final AuthenticationRequest request) throws AuthenticationException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthenticationRequest: {}", request);
        }

        AuthenticationResponse authResponse = new AuthenticationResponse();

        final RequestHostInfo reqInfo = request.getHostInfo();
        final UserAccount userAccount = request.getUserAccount();
        final UserSecurity userSecurity = request.getUserSecurity();

        if (DEBUG)
        {
            DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            DEBUGGER.debug("UserAccount: {}", userAccount);
        }

        try
        {
            final String userSalt = userSec.getUserSalt(userAccount.getGuid(), SaltType.RESET.name());

            if (StringUtils.isNotEmpty(userSalt))
            {
                final List<String> requestList = new ArrayList<>(
                        Arrays.asList(
                                userAccount.getGuid(),
                                userAccount.getUsername(),
                                PasswordUtils.encryptText(userSecurity.getSecAnswerOne(), userSalt,
                                        secConfig.getAuthAlgorithm(), secConfig.getIterations()),
                                PasswordUtils.encryptText(userSecurity.getSecAnswerTwo(), userSalt,
                                        secConfig.getAuthAlgorithm(), secConfig.getIterations())));

                boolean isVerified = authenticator.verifySecurityData(requestList);

                if (DEBUG)
                {
                    DEBUGGER.debug("isVerified: {}", isVerified);
                }

                if (isVerified)
                {
                    authResponse.setRequestStatus(SecurityRequestStatus.SUCCESS);
                    authResponse.setResponse("Successfully validated user security data.");
                }
                else
                {
                    if (request.getCount() >= 3)
                    {
                        try
                        {
                            Map<String, Object> changeRequest = new HashMap<>();
                            changeRequest.put(authData.getOlrLocked(), "true");

                            if (DEBUG)
                            {
                                DEBUGGER.debug("Map<String, Object>: {}", changeRequest);
                            }

                            userManager.modifyUserInformation(userAccount.getUsername(), userAccount.getGuid(), changeRequest);
                        }
                        catch (UserManagementException umx)
                        {
                            ERROR_RECORDER.error(umx.getMessage(), umx);
                        }
                    }

                    authResponse.setCount(request.getCount() + 1);
                    authResponse.setRequestStatus(SecurityRequestStatus.FAILURE);
                    authResponse.setResponse("Failed to validate user security data.");
                }
            }
            else
            {
                throw new AuthenticatorException("Unable to obtain user salt value. Cannot continue.");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);

            throw new AuthenticationException(sqx.getMessage(), sqx);
        }
        catch (AuthenticatorException ax)
        {
            ERROR_RECORDER.error(ax.getMessage(), ax);

            throw new AuthenticationException(ax.getMessage(), ax);
        }
        finally
        {
            // audit
            try
            {
                AuditEntry auditEntry = new AuditEntry();
                auditEntry.setHostInfo(reqInfo);
                auditEntry.setAuditType(AuditType.VERIFYSECURITY);
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

        return authResponse;
    }
}
