/*
 * Copyright (c) 2009 - 2017 CaspersBox Web Services
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.esolutions.web.controllers;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.controllers
 * File: DNSServiceController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly   11/23/2008 22:39:20             Created.
 */
import java.util.List;
import org.slf4j.Logger;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.DNSRecord;
import com.cws.esolutions.core.processors.enums.DNSRequestType;
import com.cws.esolutions.core.processors.dto.DNSServiceRequest;
import com.cws.esolutions.core.processors.dto.DNSServiceResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.exception.DNSServiceException;
import com.cws.esolutions.core.processors.impl.DNSServiceRequestProcessorImpl;
import com.cws.esolutions.core.processors.interfaces.IDNSServiceRequestProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("/dns-service")
public class DNSServiceController
{
    private String serviceId = null;
    private String lookupPage = null;
    private String serviceName = null;
    private String serviceHost = null;
    private String[] searchSuffix = null;
    private String createServicePage = null;
    private List<String> serviceTypes = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = DNSServiceController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setLookupPage(final String value)
    {
        final String methodName = DNSServiceController.CNAME + "#setLookupPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.lookupPage = value;
    }

    public final void setCreateServicePage(final String value)
    {
        final String methodName = DNSServiceController.CNAME + "#setCreateServicePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.createServicePage = value;
    }

    public final void setServiceName(final String value)
    {
        final String methodName = DNSServiceController.CNAME + "#setServiceName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceName = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = DNSServiceController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = DNSServiceController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setServiceHost(final String value)
    {
        final String methodName = DNSServiceController.CNAME + "#setServiceHost(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceHost = value;
    }

    public final void setSearchSuffix(final String[] value)
    {
        final String methodName = DNSServiceController.CNAME + "#setSearchSuffix(final String[] value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);

            if (value != null)
            {
                for (String str : value)
                {
                    DEBUGGER.debug("Value: {}", str);
                }
            }
        }

        this.searchSuffix = value;
    }

    public final void setServiceTypes(final List<String> value)
    {
        final String methodName = DNSServiceController.CNAME + "#setServiceTypes(final List<String> value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceTypes = value;
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage()
    {
        final String methodName = DNSServiceController.CNAME + "#showDefaultPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
        	mView.setViewName(this.appConfig.getUnavailablePage());

        	return mView;
        }

        mView.addObject("serviceTypes", this.serviceTypes);
        mView.addObject(Constants.COMMAND, new DNSRecord());
        mView.setViewName(this.lookupPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/service-lookup", method = RequestMethod.GET)
    public final ModelAndView showLookup()
    {
        final String methodName = DNSServiceController.CNAME + "#showLookup()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }


        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
        	mView.setViewName(this.appConfig.getUnavailablePage());

        	return mView;
        }
            
        mView.addObject("serviceTypes", this.serviceTypes);
        mView.addObject(Constants.COMMAND, new DNSRecord());
        mView.setViewName(this.lookupPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/create-service", method = RequestMethod.GET)
    public final ModelAndView showCreateService()
    {
        final String methodName = DNSServiceController.CNAME + "#showCreateService()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }


        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
        	mView.setViewName(this.appConfig.getUnavailablePage());

        	return mView;
        }
            
        mView.addObject("serviceTypes", this.serviceTypes);
        mView.addObject(Constants.COMMAND, new DNSRecord());
        mView.setViewName(this.createServicePage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/service-lookup", method = RequestMethod.POST)
    public final ModelAndView showLookup(@ModelAttribute("entry") final DNSRecord request, final BindingResult bindResult)
    {
        final String methodName = DNSServiceController.CNAME + "#showLookup(@ModelAttribute(\"entry\") final DNSRecord request, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("DNSRecord: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IDNSServiceRequestProcessor dnsProcessor = new DNSServiceRequestProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<?> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = (String) sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<?> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = (String) requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<?> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = (String) paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }


        if (!(this.appConfig.getServices().get(this.serviceName)))
        {
        	mView.setViewName(this.appConfig.getUnavailablePage());

        	return mView;
        }

        try
        {
            // ensure authenticated access
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setHostName(hRequest.getRemoteHost());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            DNSServiceRequest dnsRequest = new DNSServiceRequest();
            dnsRequest.setRecord(request);
            dnsRequest.setRequestInfo(reqInfo);
            dnsRequest.setUserAccount(userAccount);
            dnsRequest.setServiceId(this.serviceId);
            dnsRequest.setSearchURL(this.serviceHost);
            dnsRequest.setSearchPath(this.searchSuffix);
            dnsRequest.setRequestType(DNSRequestType.LOOKUP);
            dnsRequest.setApplicationId(this.appConfig.getApplicationId());
            dnsRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("DNSServiceRequest: {}", dnsRequest);
            }

            DNSServiceResponse response = dnsProcessor.performLookup(dnsRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("DNSServiceResponse: {}", response);
            }

            if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                if ((response.getDnsRecords() != null) && (response.getDnsRecords().size() != 0))
                {
                    // multiple records were returned
                    mView.addObject("dnsEntries", response.getDnsRecords());
                }
                else if (response.getDnsRecord() != null)
                {
                    mView.addObject("dnsEntry", response.getDnsRecord());
                }
                else
                {
                    mView.addObject(Constants.ERROR_RESPONSE, this.appConfig.getMessageNoSearchResults());
                }
            }
            else
            {
                mView.setViewName(this.appConfig.getErrorResponsePage());
            }

            mView.addObject("serviceTypes", this.serviceTypes);
            mView.addObject(Constants.COMMAND, new DNSRecord());
            mView.setViewName(this.lookupPage);
        }
        catch (DNSServiceException dsx)
        {
            ERROR_RECORDER.error(dsx.getMessage(), dsx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }
 
        return mView;
    }
}
