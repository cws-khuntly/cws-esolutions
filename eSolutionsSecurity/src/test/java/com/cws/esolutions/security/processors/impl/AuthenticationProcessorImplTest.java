/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.impl
 * File: AuthenticationProcessorImplTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.security.processors.exception.AuthenticationException;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;

public class AuthenticationProcessorImplTest
{
    private static RequestHostInfo hostInfo = null;

    private static final IAuthenticationProcessor agentAuth = new AuthenticationProcessorImpl();

    @Before public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml");

            hostInfo = new RequestHostInfo();
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");
            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test public void processAgentLogon()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

        AuthenticationData userSecurity = new AuthenticationData();
        userSecurity.setPassword("Ariana21*");

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = agentAuth.processAgentLogon(request);

            Assert.assertEquals(response.getRequestStatus(), SecurityRequestStatus.SUCCESS);
        }
        catch (AuthenticationException ax)
        {
            Assert.fail(ax.getMessage());
        }
    }

    @Test public void processOtpLogon()
    {
        UserAccount account = new UserAccount();
        account.setUsername("khuntly");
        account.setGuid("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95");

        hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

        AuthenticationData userSecurity = new AuthenticationData();
        userSecurity.setPassword("Ariana21*");
        userSecurity.setSecret("RHmJrNj6KISffPbnksYZDuKr9pookp0oxThyHa0rqkrID+tX8PTVcTl6D/MoA0FCp2r7lv+HaHrRrR/w/FaGSA==");
        userSecurity.setOtpValue(790269);

        AuthenticationRequest request = new AuthenticationRequest();
        request.setApplicationName("esolutions");
        request.setUserAccount(account);
        request.setUserSecurity(userSecurity);
        request.setHostInfo(hostInfo);

        try
        {
            AuthenticationResponse response = agentAuth.processOtpLogon(request);

            Assert.assertEquals(SecurityRequestStatus.SUCCESS, response.getRequestStatus());
        }
        catch (AuthenticationException ax)
        {
            ax.printStackTrace();
            Assert.fail(ax.getMessage());
        }
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
