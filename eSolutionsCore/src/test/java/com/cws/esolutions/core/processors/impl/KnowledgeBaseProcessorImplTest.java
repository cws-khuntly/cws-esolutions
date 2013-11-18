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

import org.junit.Test;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.apache.commons.lang.RandomStringUtils;

import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.dto.UserSecurity;
import com.cws.esolutions.core.processors.dto.Article;
import com.cws.esolutions.core.processors.enums.ArticleStatus;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.dao.userauth.enums.LoginType;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseRequest;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.dto.KnowledgeBaseResponse;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;
import com.cws.esolutions.security.processors.dto.AuthenticationRequest;
import com.cws.esolutions.security.dao.userauth.enums.AuthenticationType;
import com.cws.esolutions.security.processors.dto.AuthenticationResponse;
import com.cws.esolutions.core.processors.exception.KnowledgeBaseException;
import com.cws.esolutions.core.processors.interfaces.IKnowledgeBaseProcessor;
import com.cws.esolutions.security.processors.impl.AuthenticationProcessorImpl;
import com.cws.esolutions.security.processors.interfaces.IAuthenticationProcessor;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.processors.impl
 * KnowledgeBaseProcessorImpl.java
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
 * kh05451 @ Nov 21, 2012 8:39:26 AM
 *     Created.
 */
public class KnowledgeBaseProcessorImplTest
{
    private UserAccount userAccount = new UserAccount();
    private RequestHostInfo hostInfo = new RequestHostInfo();

    private static final IKnowledgeBaseProcessor kbase = new KnowledgeBaseProcessorImpl();

    @Before
    public final void setUp() throws Exception
    {
        try
        {
            CoreServiceInitializer.initializeService("eSolutionsCore/config/ServiceConfig.xml", "logging/logging.xml");

            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/config/SecurityLogging.xml");

            IAuthenticationProcessor agentAuth = new AuthenticationProcessorImpl();
            hostInfo.setHostAddress("127.0.0.1");
            hostInfo.setHostName("localhost");

            UserAccount account = new UserAccount();
            account.setUsername("khuntly");
            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));

            try
            {
                AuthenticationRequest userRequest = new AuthenticationRequest();
                userRequest.setApplicationName("esolutions");
                userRequest.setAuthType(AuthenticationType.LOGIN);
                userRequest.setLoginType(LoginType.USERNAME);
                userRequest.setUserAccount(account);
                userRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");

                AuthenticationResponse userResponse = agentAuth.processAgentLogon(userRequest);

                if (userResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                {
                    UserAccount authUser = userResponse.getUserAccount();

                    if (authUser.getStatus() == LoginStatus.SUCCESS)
                    {
                        UserSecurity userSecurity = new UserSecurity();
                        userSecurity.setPassword("Ariana16*");

                        AuthenticationRequest passRequest = new AuthenticationRequest();
                        passRequest.setApplicationName("esolutions");
                        passRequest.setAuthType(AuthenticationType.LOGIN);
                        passRequest.setLoginType(LoginType.PASSWORD);
                        passRequest.setUserAccount(authUser);
                        passRequest.setUserSecurity(userSecurity);
                        passRequest.setApplicationId("B760E92F-827A-42E7-9E8D-64334657BA83");

                        AuthenticationResponse passResponse = agentAuth.processAgentLogon(passRequest);

                        if (passResponse.getRequestStatus() == SecurityRequestStatus.SUCCESS)
                        {
                            userAccount = passResponse.getUserAccount();
                            hostInfo.setSessionId(RandomStringUtils.randomAlphanumeric(32));
                        }
                        else
                        {
                            Assert.fail("Account login failed");
                        }
                    }
                    else
                    {
                        Assert.fail("Account login failed");
                    }
                }
                else
                {
                    Assert.fail("Account login failed");
                }
            }
            catch (Exception e)
            {
                Assert.fail(e.getMessage());
            }
        }
        catch (Exception ex)
        {
            Assert.fail(ex.getMessage());

            System.exit(-1);
        }
    }

    @Test
    public final void testListTopArticles()
    {
        
    }

    @Test
    public final void testAddNewArticle()
    {
        Article article = new Article();
        article.setArticleId("KB" + RandomStringUtils.randomNumeric(8));
        article.setArticleStatus(ArticleStatus.NEW);
        article.setAuthor("kmhuntly");
        article.setAuthorEmail("kmhuntly@caspersbox.com");
        article.setCause("Caused By a Test");
        article.setKeywords("keywords");
        article.setPageHits(1);
        article.setResolution("untest");
        article.setSymptoms("test");
        article.setTitle("test article");

        KnowledgeBaseRequest request = new KnowledgeBaseRequest();
        request.setArticle(article);
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("4B081972-92C3-455B-9403-B81E68C538B6");

        try
        {
            KnowledgeBaseResponse response = kbase.addNewArticle(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (KnowledgeBaseException kbx)
        {
            Assert.fail(kbx.getMessage());
        }
    }

    @Test
    public final void testUpdateArticle()
    {
        Article article = new Article();
        article.setArticleId("KB40975607");
        article.setArticleStatus(ArticleStatus.NEW);
        article.setAuthor("kmhuntly");
        article.setAuthorEmail("kmhuntly@caspersbox.com");
        article.setCause("This is another test");
        article.setKeywords("keywords");
        article.setPageHits(1);
        article.setResolution("untest");
        article.setSymptoms("test");
        article.setTitle("test article");
        article.setModifiedBy("khuntly");

        KnowledgeBaseRequest request = new KnowledgeBaseRequest();
        request.setArticle(article);
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("4B081972-92C3-455B-9403-B81E68C538B6");

        try
        {
            KnowledgeBaseResponse response = kbase.updateArticle(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (KnowledgeBaseException kbx)
        {
            Assert.fail(kbx.getMessage());
        }
    }

    @Test
    public final void testApproveArticle()
    {
        Article article = new Article();
        article.setArticleId("KB40975607");
        article.setArticleStatus(ArticleStatus.APPROVED);

        KnowledgeBaseRequest request = new KnowledgeBaseRequest();
        request.setArticle(article);
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("4B081972-92C3-455B-9403-B81E68C538B6");

        try
        {
            KnowledgeBaseResponse response = kbase.updateArticleStatus(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (KnowledgeBaseException kbx)
        {
            Assert.fail(kbx.getMessage());
        }
    }

    @Test
    public final void testRejectArticle()
    {
        Article article = new Article();
        article.setArticleId("KB99991");
        article.setArticleStatus(ArticleStatus.REJECTED);

        KnowledgeBaseRequest request = new KnowledgeBaseRequest();
        request.setArticle(article);
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("4B081972-92C3-455B-9403-B81E68C538B6");

        try
        {
            KnowledgeBaseResponse response = kbase.updateArticleStatus(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (KnowledgeBaseException kbx)
        {
            Assert.fail(kbx.getMessage());
        }
    }

    @Test
    public final void testDeleteArticle()
    {
        Article article = new Article();
        article.setArticleId("KB99991");
        article.setArticleStatus(ArticleStatus.DELETED);

        KnowledgeBaseRequest request = new KnowledgeBaseRequest();
        request.setArticle(article);
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("4B081972-92C3-455B-9403-B81E68C538B6");

        try
        {
            KnowledgeBaseResponse response = kbase.updateArticleStatus(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (KnowledgeBaseException kbx)
        {
            Assert.fail(kbx.getMessage());
        }
    }

    @Test
    public final void testGetArticle()
    {
        Article article = new Article();
        article.setArticleId("KB40975607");

        KnowledgeBaseRequest request = new KnowledgeBaseRequest();
        request.setArticle(article);
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("4B081972-92C3-455B-9403-B81E68C538B6");

        try
        {
            KnowledgeBaseResponse response = kbase.getArticle(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (KnowledgeBaseException kbx)
        {
            Assert.fail(kbx.getMessage());
        }
    }

    @Test
    public final void testGetPendingArticles()
    {
        KnowledgeBaseRequest request = new KnowledgeBaseRequest();
        request.setRequestInfo(hostInfo);
        request.setUserAccount(userAccount);
        request.setServiceId("4B081972-92C3-455B-9403-B81E68C538B6");

        try
        {
            KnowledgeBaseResponse response = kbase.getPendingArticles(request);

            Assert.assertEquals(CoreServicesStatus.SUCCESS, response.getRequestStatus());
        }
        catch (KnowledgeBaseException kbx)
        {
            Assert.fail(kbx.getMessage());
        }
    }

    @After
    public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
