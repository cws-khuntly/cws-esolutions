/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 */
package com.cws.us.esolutions.controllers;

import org.slf4j.Logger;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.us.esolutions.Constants;
import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.us.esolutions.ApplicationServiceBean;
import com.cws.esolutions.core.processors.dto.EmailMessage;
import com.cws.esolutions.core.processors.dto.ServiceMessage;
import com.cws.esolutions.security.audit.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.interfaces.IMessagingProcessor;
import com.cws.esolutions.core.processors.impl.ServiceMessagingProcessorImpl;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
/**
 * eSolutions_java_source
 * com.cws.us.esolutions.controllers
 * MessagingController.java
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author kh05451
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 16, 2013 11:53:26 AM
 *     Created.
 */
@Controller
@RequestMapping("/messaging")
public class MessagingController
{
    private String serviceId = null;
    private String messageEmailSent = null;
    private String sendEmailMessage = null;
    private String addServiceMessage = null;
    private String editServiceMessage = null;
    private String viewServiceMessages = null;
    private String messageSuccessfullyAdded = null;
    private ApplicationServiceBean appConfig = null;

    private static final String CNAME = MessagingController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setSendEmailMessage(final String value)
    {
        final String methodName = MessagingController.CNAME + "#setSendEmailMessage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.sendEmailMessage = value;
    }

    public final void setAddServiceMessage(final String value)
    {
        final String methodName = MessagingController.CNAME + "#setAddServiceMessage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.addServiceMessage = value;
    }

    public final void setViewServiceMessages(final String value)
    {
        final String methodName = MessagingController.CNAME + "#setViewServiceMessages(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.viewServiceMessages = value;
    }

    public final void setEditServiceMessage(final String value)
    {
        final String methodName = MessagingController.CNAME + "#setEditServiceMessage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.editServiceMessage = value;
    }

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = MessagingController.CNAME + "#setAppConfig(final CoreServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = MessagingController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setMessageEmailSent(final String value)
    {
        final String methodName = MessagingController.CNAME + "#setMessageEmailSent(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageEmailSent = value;
    }

    public final void setMessageSuccessfullyAdded(final String value)
    {
        final String methodName = MessagingController.CNAME + "#setMessageSuccessfullyAdded(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSuccessfullyAdded = value;
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public ModelAndView showDefaultPage()
    {
        final String methodName = MessagingController.CNAME + "#showDefaultPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IMessagingProcessor msgProcessor = new ServiceMessagingProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

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
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            MessagingRequest request = new MessagingRequest();
            request.setRequestInfo(reqInfo);
            request.setServiceId(this.serviceId);
            request.setUserAccount(userAccount);
            request.setAppName(appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingRequest: {}", request);
            }

            MessagingResponse response = msgProcessor.showMessages(request);

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", response);
            }

            if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                mView.addObject("messageList", response.getSvcMessages());
                mView.setViewName(this.viewServiceMessages);
            }
            else
            {
                // no existing service messages
                mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                mView.setViewName(this.addServiceMessage);
            }
        }
        catch (MessagingServiceException msx)
        {
            ERROR_RECORDER.error(msx.getMessage(), msx);

            mView.setViewName(appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/edit-message/message/{messageId}", method = RequestMethod.GET)
    public ModelAndView showEditMessage(@PathVariable(value = "messageId") final String messageId)
    {
        final String methodName = MessagingController.CNAME + "#showEditMessage(@PathVariable(value = \"messageId\") final String messageId)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("messageId: {}", messageId);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IMessagingProcessor msgProcessor = new ServiceMessagingProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

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
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            ServiceMessage message = new ServiceMessage();
            message.setMessageId(messageId);

            if (DEBUG)
            {
                DEBUGGER.debug("ServiceMessage: {}", message);
            }

            MessagingRequest request = new MessagingRequest();
            request.setRequestInfo(reqInfo);
            request.setServiceId(this.serviceId);
            request.setUserAccount(userAccount);
            request.setSvcMessage(message);
            request.setAppName(appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingRequest: {}", request);
            }

            MessagingResponse response = msgProcessor.showMessage(request);

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", response);
            }

            if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                ServiceMessage responseMessage = response.getSvcMessage();

                if (DEBUG)
                {
                    DEBUGGER.debug("ServiceMessage: {}", responseMessage);
                }

                mView.addObject("message", responseMessage);
                mView.setViewName(this.editServiceMessage);
            }
            else
            {
                // no existing service messages
                mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                mView.setViewName(this.viewServiceMessages);
            }
        }
        catch (MessagingServiceException msx)
        {
            ERROR_RECORDER.error(msx.getMessage(), msx);

            mView.setViewName(appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/send-email", method = RequestMethod.GET)
    public ModelAndView showEmailRequest()
    {
        final String methodName = MessagingController.CNAME + "#showEmailRequest()";

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
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

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
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        EmailMessage email = new EmailMessage();
        email.setMessageFrom(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));

        if (DEBUG)
        {
            DEBUGGER.debug("EmailMessage: {}", email);
        }

        mView.addObject("command", email);
        mView.setViewName(this.sendEmailMessage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/add-message", method = RequestMethod.POST)
    public ModelAndView addServiceMessage(@ModelAttribute("message") final ServiceMessage message, final BindingResult bindResult)
    {
        final String methodName = MessagingController.CNAME + "#addServiceMessage(@ModelAttribute(\"message\") final ServiceMessage message, final BindingResult bindResult";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("ServiceMessage: {}", message);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IMessagingProcessor msgProcessor = new ServiceMessagingProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

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
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        // validate here
        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            MessagingRequest request = new MessagingRequest();
            request.setRequestInfo(reqInfo);
            request.setServiceId(this.serviceId);
            request.setUserAccount(userAccount);
            request.setSvcMessage(message);
            request.setAppName(appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingRequest: {}", request);
            }

            MessagingResponse response = msgProcessor.addNewMessage(request);

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", response);
            }

            if (response.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                mView.addObject(Constants.RESPONSE_MESSAGE, this.messageSuccessfullyAdded);
                mView.setViewName(this.viewServiceMessages);
            }
            else
            {
                // no existing service messages
                mView.addObject(Constants.ERROR_MESSAGE, response.getResponse());
                mView.setViewName(this.viewServiceMessages);
            }
        }
        catch (MessagingServiceException msx)
        {
            ERROR_RECORDER.error(msx.getMessage(), msx);

            mView.setViewName(appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/send-email", method = RequestMethod.POST)
    public ModelAndView sendEmailMessage(@ModelAttribute("request") final EmailMessage request, final BindingResult bindResult)
    {
        final String methodName = MessagingController.CNAME + "#sendEmailMessage(@ModelAttribute(\"request\") final EmailMessage request, final BindingResult bindResult";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("EmailMessage: {}", request);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);

        if (DEBUG)
        {
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("UserAccount: {}", userAccount);

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
        }

        if (userAccount.getStatus() == LoginStatus.EXPIRED)
        {
            // redirect to password page
            mView = new ModelAndView(new RedirectView());
            mView.setViewName(appConfig.getExpiredRedirect());
            mView.addObject(Constants.ERROR_MESSAGE, Constants.PASSWORD_EXPIRED);

            if (DEBUG)
            {
                DEBUGGER.debug("ModelAndView: {}", mView);
            }

            return mView;
        }

        // validate here
        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            request.setFirstName(userAccount.getGivenName());
            request.setLastName(userAccount.getSurname());
            request.setIsAlert(false);
            request.setMessageFrom(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));
            request.setMessageTo(new ArrayList<String>(Arrays.asList(appConfig.getSecEmailAddr())));
            request.setMessageCC(new ArrayList<String>(Arrays.asList(userAccount.getEmailAddr())));

            if (DEBUG)
            {
                DEBUGGER.debug("EmailMessage: {}", request);
            }

            EmailUtils.sendEmailMessage(request);

            mView.addObject(Constants.RESPONSE_MESSAGE, this.messageEmailSent);
            mView.setViewName(appConfig.getHomePage());
        }
        catch (MessagingException mx)
        {
            ERROR_RECORDER.error(mx.getMessage(), mx);

            mView.setViewName(appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
