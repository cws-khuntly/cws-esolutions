/*
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
package com.cws.esolutions.web.controllers;

import org.slf4j.Logger;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.core.utils.dto.EmailMessage;
import com.cws.esolutions.web.validators.EmailAddressValidator;
import com.cws.esolutions.web.validators.EmailMessageValidator;
import com.cws.esolutions.core.processors.dto.MessagingRequest;
import com.cws.esolutions.core.config.xml.CoreConfigurationData;
import com.cws.esolutions.core.processors.dto.MessagingResponse;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.core.processors.enums.CoreServicesStatus;
import com.cws.esolutions.core.processors.interfaces.IMessagingProcessor;
import com.cws.esolutions.core.processors.impl.ServiceMessagingProcessorImpl;
import com.cws.esolutions.core.processors.exception.MessagingServiceException;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.controllers
 * File: CommonController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
@Controller
@RequestMapping("/common")
public class CommonController
{
    private String homePage = null;
    private String serviceId = null;
    private CoreConfigurationData coreConfig = null;
    private ApplicationServiceBean appConfig = null;
    private SimpleMailMessage contactResponseEmail = null;

    private static final String CNAME = CommonController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = CommonController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setCoreConfig(final CoreConfigurationData value)
    {
        final String methodName = CommonController.CNAME + "#setCoreConfig(final CoreConfigurationData value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.coreConfig = value;
    }

    public final void setHomePage(final String value)
    {
        final String methodName = CommonController.CNAME + "#setHomePage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.homePage = value;
    }

    public final void setServiceId(final String value)
    {
        final String methodName = CommonController.CNAME + "#setServiceId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.serviceId = value;
    }

    public final void setContactResponseEmail(final SimpleMailMessage value)
    {
        final String methodName = CommonController.CNAME + "#setContactResponseEmail(final SimpleMailMessage value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.contactResponseEmail = value;
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage()
    {
        final String methodName = CommonController.CNAME + "#showDefaultPage()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();
        mView.setViewName(this.homePage);

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IMessagingProcessor msgProcessor = new ServiceMessagingProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("Session ID: {}", hSession.getId());
            DEBUGGER.debug("UserAccount: {}", userAccount);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        try
        {
            // ensure authenticated access
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setSessionId(hSession.getId());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            MessagingRequest mRequest = new MessagingRequest();
            mRequest.setRequestInfo(reqInfo);
            mRequest.setUserAccount(userAccount);
            mRequest.setServiceId(this.serviceId);
            mRequest.setApplicationId(this.appConfig.getApplicationId());
            mRequest.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingRequest: {}", mRequest);
            }

            MessagingResponse mResponse = msgProcessor.showMessages(mRequest);

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", mResponse);
            }

            if (mResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                mView.addObject("dateFormat", this.appConfig.getDateFormat());
                mView.addObject("messageList", mResponse.getSvcMessages());
            }

            MessagingResponse messageResponse = msgProcessor.showAlertMessages(new MessagingRequest());

            if (DEBUG)
            {
                DEBUGGER.debug("MessagingResponse: {}", messageResponse);
            }

            if (messageResponse.getRequestStatus() == CoreServicesStatus.SUCCESS)
            {
                mView.addObject("alertMessages", messageResponse.getSvcMessages());
            }
        }
        catch (MessagingServiceException msx)
        {
            // log it, but dont redirect or anything
            ERROR_RECORDER.error(msx.getMessage(), msx);
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        // in here, we're going to get all the messages to display and such
        return mView;
    }

    @RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
    public final ModelAndView showUnauthorizedPage()
    {
        final String methodName = CommonController.CNAME + "#showUnauthorizedPage()";

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
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        mView.setViewName(this.appConfig.getUnauthorizedPage());

        return mView;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public final ModelAndView showContactPage()
    {
        final String methodName = CommonController.CNAME + "#showContactPage()";

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
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        mView.addObject("serviceEmail", this.appConfig.getSvcEmailAddr());
        mView.addObject("command", new EmailMessage());
        mView.setViewName(this.appConfig.getContactAdminsPage());

        return mView;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public final ModelAndView doSubmitMessage(@ModelAttribute("message") final EmailMessage message, final BindingResult bindResult)
    {
        final String methodName = CommonController.CNAME + "#doSubmitMessage(@ModelAttribute(\"message\") final EmailMessage message, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("EmailMessage: {}", message);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final EmailMessageValidator messageValidator = this.appConfig.getMessageValidator();
        final EmailAddressValidator addressValidator = this.appConfig.getEmailValidator();
        final String emailId = RandomStringUtils.randomAlphanumeric(16);

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("EmailMessageValidator: {}", messageValidator);
            DEBUGGER.debug("EmailAddressValidator: {}", addressValidator);
            DEBUGGER.debug("emailId: {}", emailId);

            DEBUGGER.debug("Dumping session content:");
            Enumeration<String> sessionEnumeration = hSession.getAttributeNames();

            while (sessionEnumeration.hasMoreElements())
            {
                String element = sessionEnumeration.nextElement();
                Object value = hSession.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request content:");
            Enumeration<String> requestEnumeration = hRequest.getAttributeNames();

            while (requestEnumeration.hasMoreElements())
            {
                String element = requestEnumeration.nextElement();
                Object value = hRequest.getAttribute(element);

                DEBUGGER.debug("Attribute: {}; Value: {}", element, value);
            }

            DEBUGGER.debug("Dumping request parameters:");
            Enumeration<String> paramsEnumeration = hRequest.getParameterNames();

            while (paramsEnumeration.hasMoreElements())
            {
                String element = paramsEnumeration.nextElement();
                Object value = hRequest.getParameter(element);

                DEBUGGER.debug("Parameter: {}; Value: {}", element, value);
            }
        }

        messageValidator.validate(message, bindResult);

        if (bindResult.hasErrors())
        {
            mView.addObject("errors", bindResult.getAllErrors());
            mView.addObject("command", message);
            mView.setViewName(this.appConfig.getContactAdminsPage());

            return mView;
        }

        addressValidator.validate(message.getEmailAddr().get(0), bindResult);

        if (bindResult.hasErrors())
        {
            mView.addObject("errors", bindResult.getAllErrors());
            mView.addObject("command", message);
            mView.setViewName(this.appConfig.getContactAdminsPage());

            return mView;
        }

        try
        {
            EmailUtils.sendEmailMessage(this.coreConfig.getMailConfig(), message, true);

            EmailMessage autoResponse = new EmailMessage();
            autoResponse.setIsAlert(false);
            autoResponse.setMessageSubject(this.contactResponseEmail.getSubject());
            autoResponse.setMessageTo(new ArrayList<>(Arrays.asList(String.format(this.contactResponseEmail.getTo()[0], message.getEmailAddr().get(0)))));
            autoResponse.setEmailAddr(new ArrayList<>(Arrays.asList(String.format(this.contactResponseEmail.getFrom()))));
            autoResponse.setMessageBody(String.format(
                this.contactResponseEmail.getText(),
                message.getEmailAddr(),
                message.getMessageBody()));

            if (DEBUG)
            {
                DEBUGGER.debug("EmailMessage: {}", autoResponse);
            }

            EmailUtils.sendEmailMessage(this.coreConfig.getMailConfig(), autoResponse, true);

            mView = new ModelAndView(new RedirectView());
            mView.setViewName(this.appConfig.getContactAdminsRedirect());
            mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessageEmailSentSuccess());
        }
        catch (MessagingException mx)
        {
            ERROR_RECORDER.error(mx.getMessage(), mx);

            mView = new ModelAndView(new RedirectView());
            mView.setViewName(this.appConfig.getContactAdminsPage());
            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageRequestProcessingFailure());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
