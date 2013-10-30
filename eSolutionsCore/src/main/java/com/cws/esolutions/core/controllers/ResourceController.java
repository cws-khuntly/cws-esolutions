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
package com.cws.esolutions.core.controllers;

import java.util.Map;
import java.util.Locale;
import org.slf4j.Logger;
import java.util.HashMap;
import javax.naming.Context;
import javax.sql.DataSource;
import java.sql.SQLException;
import org.slf4j.LoggerFactory;
import java.util.ResourceBundle;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.naming.NamingException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.MissingResourceException;
import org.apache.commons.lang.StringUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPConnection;
import org.apache.commons.dbcp.BasicDataSource;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;

import com.cws.esolutions.core.Constants;
import com.cws.esolutions.security.config.AuthRepo;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.core.config.DataSourceManager;
import com.cws.esolutions.security.enums.AuthRepositoryType;
import com.cws.esolutions.core.exception.CoreServiceException;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.controllers
 * ResourceController.java
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
 * kh05451 @ Nov 23, 2012 8:21:09 AM
 *     Created.
 */
public class ResourceController
{
    private static final String DS_CONTEXT = "java:comp/env/";
    private static final String CNAME = ResourceController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + ResourceController.CNAME);

    public synchronized static void configureAndCreateAuthConnection(final AuthRepo authRepo, final boolean isContainer, final ServletContext sContext, final ResourceControllerBean resBean) throws CoreServiceException
    {
        String methodName = CNAME + "#configureAndCreateAuthConnection(final AuthRepo authRepo, final boolean isContainer, final ServletContext sContext, final ResourceControllerBean resBean) throws CoreServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthRepo: {}", authRepo);
            DEBUGGER.debug("isContainer: {}", isContainer);
            DEBUGGER.debug("ServletContext: {}", sContext);
            DEBUGGER.debug("ResourceControllerBean: {}", resBean);
        }

        final AuthRepositoryType authType = AuthRepositoryType.valueOf(authRepo.getRepoType());

        if (DEBUG)
        {
            DEBUGGER.debug("AuthRepositoryType: {}", authType);
        }

        switch (authType)
        {
            case LDAP:
                LDAPConnection ldapConn = null;
                LDAPConnectionOptions connOpts = new LDAPConnectionOptions();

                try
                {
                    connOpts.setAbandonOnTimeout(true);
                    connOpts.setAutoReconnect(true);
                    connOpts.setBindWithDNRequiresPassword(true);

                    if (isContainer)
                    {
                        connOpts.setConnectTimeoutMillis(Integer.valueOf(sContext.getInitParameter("connTimeout")));
                        connOpts.setResponseTimeoutMillis(Integer.valueOf(sContext.getInitParameter("readTimeout")));

                        ldapConn = new LDAPConnection(connOpts,
                                sContext.getInitParameter("ldapHost"),
                                Integer.valueOf(sContext.getInitParameter("ldapPort")),
                                sContext.getInitParameter("ldapUser"),
                                PasswordUtils.decryptText(
                                        sContext.getInitParameter("ldapPass"),
                                        sContext.getInitParameter("ldapSalt").length()));

                        if (ldapConn.isConnected())
                        {
                            LDAPConnectionPool connPool = new LDAPConnectionPool(ldapConn,
                                    Integer.valueOf(sContext.getInitParameter("minConnections")),
                                    Integer.valueOf(sContext.getInitParameter("maxConnections")));

                            if (!(connPool.isClosed()))
                            {
                                resBean.setAuthDataSource(connPool);
                            }
                            else
                            {
                                throw new LDAPException(ResultCode.CONNECT_ERROR, "Failed to create LDAP connection pool");
                            }
                        }
                        else
                        {
                            throw new LDAPException(ResultCode.CONNECT_ERROR, "Failed to establish an LDAP connection");
                        }
                    }
                    else
                    {
                        connOpts.setConnectTimeoutMillis(authRepo.getRepositoryConnTimeout());
                        connOpts.setResponseTimeoutMillis(authRepo.getRepositoryReadTimeout());

                        ldapConn = new LDAPConnection(connOpts,
                                authRepo.getRepositoryHost(),
                                authRepo.getRepositoryPort(),
                                authRepo.getRepositoryUser(),
                                PasswordUtils.decryptText(
                                        authRepo.getRepositoryPass(),
                                        authRepo.getRepositorySalt().length()));

                        if (ldapConn.isConnected())
                        {
                            LDAPConnectionPool connPool = new LDAPConnectionPool(ldapConn,
                                    authRepo.getMinConnections(), authRepo.getMaxConnections());

                            if (!(connPool.isClosed()))
                            {
                                resBean.setAuthDataSource(connPool);
                            }
                            else
                            {
                                throw new LDAPException(ResultCode.CONNECT_ERROR, "Failed to create LDAP connection pool");
                            }
                        }
                        else
                        {
                            throw new LDAPException(ResultCode.CONNECT_ERROR, "Failed to establish an LDAP connection");
                        }
                    }
                }
                catch (LDAPException lx)
                {
                    ERROR_RECORDER.error(lx.getMessage(), lx);

                    throw new CoreServiceException(lx.getMessage(), lx);
                }

                break;
            case SQL:
                // the isContainer only matters here
                if (isContainer)
                {
                    try
                    {
                        Context initContext = new InitialContext();
                        Context envContext = (Context) initContext.lookup(ResourceController.DS_CONTEXT);

                        resBean.setAuthDataSource((DataSource) envContext.lookup(authRepo.getRepositoryHost()));
                    }
                    catch (NamingException nx)
                    {
                        ERROR_RECORDER.error(nx.getMessage(), nx);

                        throw new CoreServiceException(nx.getMessage(), nx);
                    }
                }
                else
                {
                    BasicDataSource dataSource = new BasicDataSource();
                    dataSource.setDriverClassName(authRepo.getRepositoryDriver());
                    dataSource.setUrl(authRepo.getRepositoryHost());
                    dataSource.setUsername(authRepo.getRepositoryUser());
                    dataSource.setPassword(PasswordUtils.decryptText(
                            authRepo.getRepositoryPass(),
                            authRepo.getRepositorySalt().length()));

                    resBean.setAuthDataSource(dataSource);
                }

                break;
            default:
                throw new CoreServiceException("Unhandled ResourceType");
        }
    }

    public synchronized static void closeAuthConnection(final AuthRepo authRepo, final boolean isContainer, final ResourceControllerBean resBean) throws CoreServiceException
    {
        String methodName = CNAME + "#closeAuthConnection(final AuthRepo authRepo, final boolean isContainer, final ResourceControllerBean resBean) throws CoreServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("AuthRepo: {}", authRepo);
            DEBUGGER.debug("isContainer: {}", isContainer);
            DEBUGGER.debug("ResourceControllerBean: {}", resBean);
        }

        final AuthRepositoryType authType = AuthRepositoryType.valueOf(authRepo.getRepoType());

        if (DEBUG)
        {
            DEBUGGER.debug("AuthRepositoryType: {}", authType);
        }

        try
        {
            switch (authType)
            {
                case LDAP:
                    LDAPConnectionPool ldapPool = (LDAPConnectionPool) resBean.getAuthDataSource();

                    if (DEBUG)
                    {
                        DEBUGGER.debug("LDAPConnectionPool: {}", ldapPool);
                    }

                    if ((ldapPool != null) && (!(ldapPool.isClosed())))
                    {
                        ldapPool.close();
                    }

                    break;
                case SQL:
                    // the isContainer only matters here
                    if (!(isContainer))
                    {
                        BasicDataSource dataSource = (BasicDataSource) resBean.getAuthDataSource();

                        if (DEBUG)
                        {
                            DEBUGGER.debug("BasicDataSource: {}", dataSource);
                        }

                        if ((dataSource != null ) && (!(dataSource.isClosed())))
                        {
                            dataSource.close();
                        }
                    }

                    break;
                default:
                    throw new CoreServiceException("Unhandled ResourceType");
            }
        }
        catch (SQLException sqx)
        {
            ERROR_RECORDER.error(sqx.getMessage(), sqx);
        }
    }

    /**
     * Sets up an application datasource connection
     * via JNDI to the requested resource reference
     *
     * @param identifier
     * @return Connection
     * @throws NamingException
     * @throws SQLException
     */
    public synchronized static void configureAndCreateDataConnection(final DataSourceManager dsManager, final ResourceControllerBean resBean) throws CoreServiceException
    {
        final String methodName = ResourceController.CNAME + "#configureAndCreateDataConnection(final DataSourceManager dsManager, final ResourceControllerBean resBean) throws CoreServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DataSourceManager: {}", dsManager);
            DEBUGGER.debug("ResourceControllerBean: {}", resBean);
        }

        Map<String, DataSource> dsMap = resBean.getDataSource();

        if (DEBUG)
        {
            DEBUGGER.debug("dsMap: {}", dsMap);
        }

        if (dsMap == null)
        {
            dsMap = new HashMap<String, DataSource>();
        }

        if (!(dsMap.containsKey(dsManager.getDsName())))
        {
            if (StringUtils.isNotEmpty(dsManager.getDriver()))
            {
                StringBuilder sBuilder = new StringBuilder()
                    .append("connectTimeout=" + dsManager.getConnectTimeout() + ";")
                    .append("socketTimeout=" + dsManager.getConnectTimeout() + ";")
                    .append("autoReconnect=" + dsManager.getAutoReconnect() + ";")
                    .append("zeroDateTimeBehavior=convertToNull");

                BasicDataSource dataSource = new BasicDataSource();
                dataSource.setDriverClassName(dsManager.getDriver());
                dataSource.setUrl(dsManager.getDataSource());
                dataSource.setUsername(dsManager.getDsUser());
                dataSource.setConnectionProperties(sBuilder.toString());

                // handle both encrypted and non-encrypted passwords
                // prefer encrypted
                if (StringUtils.isNotEmpty(dsManager.getSalt()))
                {
                    dataSource.setPassword(PasswordUtils.decryptText(
                            dsManager.getDsPass(),
                            dsManager.getSalt().length()));
                }
                else
                {
                    dataSource.setPassword(dsManager.getDsPass());
                }

                dsMap.put(dsManager.getDsName(), dataSource);
            }
            else
            {
                try
                {
                    Context initContext = new InitialContext();
                    Context envContext = (Context) initContext.lookup(DS_CONTEXT);

                    DataSource dataSource = (DataSource) envContext.lookup(dsManager.getDataSource());

                    dsMap.put(dsManager.getDsName(), dataSource);
                }
                catch (NamingException nx)
                {
                    ERROR_RECORDER.error(nx.getMessage(), nx);

                    throw new CoreServiceException(nx.getMessage(), nx);
                }
            }
        }

        resBean.setDataSource(dsMap);
    }

    /**
     * Retrieves and returns a system property housed in an application
     * configuration file
     *
     * @param pkgName
     * @param reqProperty
     * @param classLoader
     * @throws MissingResourceException
     * @return String
     */
    public static String returnSystemPropertyValue(final String pkgName, final String reqProperty, final ClassLoader classLoader) throws CoreServiceException
    {
        final String methodName = ResourceController.CNAME + "#returnSystemPropertyValue(final String pkgName, final String reqProperty, final ClassLoader classLoader) throws CoreServiceException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug(pkgName);
            DEBUGGER.debug(reqProperty);
        }

        String sProperty = null;
        ResourceBundle resourceBundle = null;

        try
        {
            resourceBundle = ResourceBundle.getBundle(pkgName, Locale.getDefault(), classLoader);
            sProperty = resourceBundle.getString(reqProperty);
        }
        catch (MissingResourceException mrx)
        {
            ERROR_RECORDER.error(mrx.getMessage(), mrx);

            throw new CoreServiceException(mrx.getMessage(), mrx);
        }
        finally
        {
            resourceBundle = null;
        }

        return sProperty;
    }
}
