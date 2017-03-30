/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
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
 * File: CertificateRequestProcessorTest.java
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

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.CertificateRequest;
import com.cws.esolutions.security.processors.dto.CertificateResponse;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.exception.CertificateRequestException;
import com.cws.esolutions.security.processors.interfaces.ICertificateRequestProcessor;
/**
 * @author khuntly
 * @version 1.0
 */
public final class CertificateRequestProcessorTest
{
    private static UserAccount userAccount = new UserAccount();
    private static RequestHostInfo hostInfo = new RequestHostInfo();
    private static AuthenticationData userSecurity = new AuthenticationData();
    private static final ICertificateRequestProcessor processor = new CertificateRequestProcessorImpl();

    @Before public void setUp()
    {
        try
        {
            hostInfo.setHostAddress("junit");
            hostInfo.setHostName("junit");

            userAccount.setStatus(LoginStatus.SUCCESS);
            userAccount.setGuid("f42fb0ba-4d1e-1126-986f-800cd2650000");
            userAccount.setUsername("junit");

            userSecurity.setPassword("junit");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", true);
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test public void generateCertificateRequest()
    {
    	CertificateRequest request = new CertificateRequest();
    	request.setHostInfo(hostInfo);
    	request.setUserAccount(userAccount);
    	request.setApplicationId("junit");
    	request.setApplicationName("junit");
    	request.setCommonName("junit.com");
    	request.setOrganizationalUnit("junit");
    	request.setOrganizationName("junit");
    	request.setLocalityName("Buffalo");
    	request.setStateName("NY");
    	request.setCountryName("US");
    	request.setContactEmail("secadm@caspersbox.com");
    	request.setKeySize(2048);
    	request.setValidityPeriod(365);
    	request.setStorePassword("junit");

    	try
    	{
    		CertificateResponse response = processor.generateCertificateRequest(request);
    		Assert.assertNotNull(response.getCsrFile());
    	}
    	catch (CertificateRequestException crx)
    	{
    		Assert.fail(crx.getMessage());
    	}
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
