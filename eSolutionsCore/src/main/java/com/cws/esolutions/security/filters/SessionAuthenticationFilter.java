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
package com.cws.esolutions.security.filters;

import org.slf4j.Logger;
import java.io.IOException;
import javax.servlet.Filter;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import java.util.ResourceBundle;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.UnavailableException;
import java.util.MissingResourceException;
import org.apache.commons.lang.StringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityConstants;
/**
 * SSLEnforcementFilter
 * Provides consistent SSL enforcement within the application
 * where required.
 *
 * History
 *
 * Author               Date                           Comments
 * ----------------------------------------------------------------------------
 * Kevin Huntly         11/23/2008 22:39:20            Created.
 */
public class SessionAuthenticationFilter implements Filter
{
    private String loginURI = null;
    private String[] ignoreURIs = null;

    private static final String LOGIN_URI = "login.uri";
    private static final String IGNORE_URI_LIST = "ignore.uri.list";
    private static final String FILTER_CONFIG_PARAM_NAME = "filter-config";
    private static final String FILTER_CONFIG_FILE_NAME = "config/FilterConfig";
    private static final String CNAME = SessionAuthenticationFilter.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger WARN_RECORDER = LoggerFactory.getLogger(SecurityConstants.WARN_LOGGER + CNAME);
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException
    {
        final String methodName = SessionAuthenticationFilter.CNAME + "#init(final FilterConfig filterConfig) throws ServletException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("FilterConfig: {}", filterConfig);
        }

        ResourceBundle rBundle = null;

        try
        {
            if (filterConfig.getInitParameter(SessionAuthenticationFilter.FILTER_CONFIG_PARAM_NAME) == null)
            {
                WARN_RECORDER.warn("Filter configuration not found. Using default !");

                rBundle = ResourceBundle.getBundle(SessionAuthenticationFilter.FILTER_CONFIG_FILE_NAME);
            }
            else
            {
                rBundle = ResourceBundle.getBundle(filterConfig.getInitParameter(SessionAuthenticationFilter.FILTER_CONFIG_PARAM_NAME));
            }

            this.loginURI = rBundle.getString(SessionAuthenticationFilter.LOGIN_URI);
            this.ignoreURIs = (StringUtils.isNotEmpty(rBundle.getString(SessionAuthenticationFilter.IGNORE_URI_LIST))) ?
                    rBundle.getString(SessionAuthenticationFilter.IGNORE_URI_LIST).trim().split(",") : null;
                    
            if (DEBUG)
            {
                if (this.ignoreURIs != null)
                {
                    for (String str : ignoreURIs)
                    {
                        DEBUGGER.debug(str);
                    }
                }
            }
        }
        catch (MissingResourceException mre)
        {
            ERROR_RECORDER.error(mre.getMessage(), mre);

            throw new UnavailableException(mre.getMessage());
        }
    }

    @Override
    public void doFilter(final ServletRequest sRequest, final ServletResponse sResponse, final FilterChain filterChain) throws IOException, ServletException
    {
        final String methodName = SessionAuthenticationFilter.CNAME + "#doFilter(final ServletRequest sRequest, final ServletResponse sResponse, final FilterChain filterChain) throws IOException, ServletException";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServletRequest: {}", sRequest);
            DEBUGGER.debug("ServletResponse: {}", sResponse);
        }

        UserAccount userAccount = null;

        final HttpServletRequest hRequest = (HttpServletRequest) sRequest;
        final HttpServletResponse hResponse = (HttpServletResponse) sResponse;
        final HttpSession hSession = hRequest.getSession();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpServletResponse: {}", hResponse);
            DEBUGGER.debug("HttpSession: {}", hSession);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String sessionElement = sessionEnumeration.nextElement();
                Object sessionValue = hSession.getAttribute(sessionElement);

                DEBUGGER.debug("Attribute: " + sessionElement + "; Value: " + sessionValue);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String requestElement = requestEnumeration.nextElement();
                Object requestValue = hRequest.getAttribute(requestElement);

                DEBUGGER.debug("Attribute: " + requestElement + "; Value: " + requestValue);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String requestElement = paramsEnumeration.nextElement();
                Object requestValue = hRequest.getParameter(requestElement);

                DEBUGGER.debug("Parameter: " + requestElement + "; Value: " + requestValue);
            }
        }

        if (StringUtils.equals(hRequest.getContextPath() + this.loginURI, hRequest.getRequestURI()))
        {
            filterChain.doFilter(sRequest, sResponse);

            return;
        }

        if ((this.ignoreURIs != null) && (this.ignoreURIs.length != 0))
        {
            // hostname isnt in ignore list
            for (String uri : this.ignoreURIs)
            {
                String requestURI = hRequest.getContextPath().trim() + uri.trim();

                if (DEBUG)
                {
                    DEBUGGER.debug(uri);
                    DEBUGGER.debug(requestURI);
                }

                if (StringUtils.contains(hRequest.getRequestURI().trim(), requestURI))
                {
                    // ignore
                    if (DEBUG)
                    {
                        DEBUGGER.debug("URI matched to ignore list - breaking out");
                    }

                    filterChain.doFilter(sRequest, sResponse);
    
                    return;
                }
            }
        }

        Enumeration<String> sessionAttributes = hSession.getAttributeNames();

        if (DEBUG)
        {
            DEBUGGER.debug("Enumeration<String>: {}", sessionAttributes);
        }

        while (sessionAttributes.hasMoreElements())
        {
            String sessionElement = sessionAttributes.nextElement();

            if (DEBUG)
            {
                DEBUGGER.debug("sessionElement: {}", sessionElement);
            }

            Object sessionValue = hSession.getAttribute(sessionElement);

            if (DEBUG)
            {
                DEBUGGER.debug("sessionValue: {}", sessionValue);
            }

            if (sessionValue instanceof UserAccount)
            {
                userAccount = (UserAccount) sessionValue;
            }

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", userAccount);
            }
        }

        if (userAccount != null)
        {
            if ((StringUtils.equals(userAccount.getSessionId(), hSession.getId())))
            {
                filterChain.doFilter(sRequest, sResponse);
            }
            else
            {
                // no user account was found
                if (DEBUG)
                {
                    DEBUGGER.debug("Redirecting request to " + hRequest.getContextPath() + this.loginURI);
                }

                // invalidate the session
                hSession.invalidate();

                hResponse.sendRedirect(hRequest.getContextPath() + this.loginURI);
            }
        }
        else
        {
            // no user account in the session
            if (DEBUG)
            {
                DEBUGGER.debug("Redirecting request to " + hRequest.getContextPath() + this.loginURI);
            }

            // invalidate the session
            hSession.invalidate();

            hResponse.sendRedirect(hRequest.getContextPath() + this.loginURI);
        }
    }

    @Override
    public void destroy()
    {
        final String methodName = SessionAuthenticationFilter.CNAME + "#destroy()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.ignoreURIs = null;
    }
}
