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
package com.cws.esolutions.security.dao.userauth.impl;

import java.util.List;
import org.junit.Test;
import org.junit.After;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Assert;
import java.util.ArrayList;

import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.dao.userauth.interfaces.Authenticator;
import com.cws.esolutions.security.dao.userauth.exception.AuthenticatorException;
/**
 * eSolutionsCore
 * com.cws.esolutions.security.dao.userauth.impl
 * SQLAuthenticatorTest.java
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
 * 35033355 @ Apr 5, 2013 2:19:11 PM
 *     Created.
 */
public class SQLAuthenticatorTest
{
    private static final Authenticator authenticator = new SQLAuthenticator();

    @Before
    public static final void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");
        }
        catch (Exception e)
        {
            Assert.fail(e.getMessage());
            System.exit(1);
        }
    }

    @Test
    public static final void testPasswordLogon()
    {
        try
        {
            Assert.assertNotNull(authenticator.performLogon("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95", "khuntly", "4MEK2S5r0fjVfk1wP2mrytpXcaI9m1hNr/zIq8RVdDgvR0VqbBmR1IHhonw124Au", "esolutions"));
        }
        catch (AuthenticatorException e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public static final void testObtainSecurityQuestions()
    {
        try
        {
            Assert.assertNotNull(authenticator.obtainSecurityData("74d9729b-7fb2-4fef-874b-c9ee5d7a5a95", "khuntly"));
        }
        catch (AuthenticatorException e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public static final void testVerifySecurityQuestions()
    {
        List<String> authList = new ArrayList<>(
                Arrays.asList(
                        "74d9729b-7fb2-4fef-874b-c9ee5d7a5a95",
                        "khuntly",
                        "2kAghHhR1Kaq4vuXukaY9NDOnt9J9rZzGvn6Fgg2EIsj2kHjsYv4mIp2SGsATUL8d839aqL27+KOVxh704hk5Q==",
                        "JeqSyqq3trkDkOwMZDf8lIHCfa2NWPSMJ/rFVnPmooGKXcAzUtwR/wJ18uc5wOQtbsMaa3cK3lB2kwqqi/SpWA=="));

        try
        {
            Assert.assertTrue(authenticator.verifySecurityData(authList));
        }
        catch (AuthenticatorException e)
        {
            Assert.fail(e.getMessage());
        }
    }

    @After
    public static final void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
