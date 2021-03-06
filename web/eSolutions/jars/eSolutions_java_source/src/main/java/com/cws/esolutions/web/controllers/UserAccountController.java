/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
 *
 */
package com.cws.esolutions.web.controllers;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web.controllers
 * File: LoginController.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.util.Enumeration;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cws.esolutions.web.Constants;
import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.web.ApplicationServiceBean;
import com.cws.esolutions.web.validators.PasswordValidator;
import com.cws.esolutions.web.validators.TelephoneValidator;
import com.cws.esolutions.web.validators.EmailAddressValidator;
import com.cws.esolutions.security.enums.SecurityRequestStatus;
import com.cws.esolutions.security.processors.enums.LoginStatus;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AccountChangeData;
import com.cws.esolutions.web.validators.SecurityResponseValidator;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
import com.cws.esolutions.security.processors.dto.AccountResetRequest;
import com.cws.esolutions.security.processors.dto.AccountResetResponse;
import com.cws.esolutions.security.processors.dto.AccountChangeRequest;
import com.cws.esolutions.security.processors.dto.AccountChangeResponse;
import com.cws.esolutions.security.processors.impl.AccountResetProcessorImpl;
import com.cws.esolutions.security.processors.impl.AccountChangeProcessorImpl;
import com.cws.esolutions.security.processors.exception.AccountResetException;
import com.cws.esolutions.security.processors.exception.AccountChangeException;
import com.cws.esolutions.security.processors.interfaces.IAccountResetProcessor;
import com.cws.esolutions.security.processors.interfaces.IAccountChangeProcessor;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see org.springframework.stereotype.Controller
 */
@Controller
@RequestMapping("/user-account")
public class UserAccountController
{
    private String enableOtpPage = null;
    private String myAccountPage = null;
    private String changeEmailPage = null;
    private String changeContactPage = null;
    private String changeSecurityPage = null;
    private String changePasswordPage = null;
    private String messageEnableOtpSuccess = null;
    private TelephoneValidator telValidator = null;
    private ApplicationServiceBean appConfig = null;
    private String messageEmailChangeSuccess = null;
    private String messageKeyGenerationSuccess = null;
    private String messageContactChangeSuccess = null;
    private String messagePasswordChangeSuccess = null;
    private String messageSecurityChangeSuccess = null;
    private PasswordValidator passwordValidator = null;
    private SecurityResponseValidator securityValidator = null;

    private static final String CNAME = UserAccountController.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER + CNAME);

    public final void setAppConfig(final ApplicationServiceBean value)
    {
        final String methodName = UserAccountController.CNAME + "#setAppConfig(final ApplicationServiceBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.appConfig = value;
    }

    public final void setPasswordValidator(final PasswordValidator value)
    {
        final String methodName = UserAccountController.CNAME + "#setPasswordValidator(final PasswordValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.passwordValidator = value;
    }

    public final void setSecurityValidator(final SecurityResponseValidator value)
    {
        final String methodName = UserAccountController.CNAME + "#setSecurityValidator(final SecurityResponseValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.securityValidator = value;
    }

    public final void setTelValidator(final TelephoneValidator value)
    {
        final String methodName = UserAccountController.CNAME + "#setTelValidator(final TelephoneValidator value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.telValidator = value;
    }

    public final void setMyAccountPage(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setMyAccountPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.myAccountPage = value;
    }

    public final void setEnableOtpPage(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setEnableOtpPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.enableOtpPage = value;
    }

    public final void setChangeEmailPage(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setChangeEmailPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.changeEmailPage = value;
    }

    public final void setChangeSecurityPage(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setChangeSecurityPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.changeSecurityPage = value;
    }

    public final void setChangePasswordPage(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setChangePasswordPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.changePasswordPage = value;
    }

    public final void setChangeContactPage(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setChangeContactPage(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.changeContactPage = value;
    }

    public final void setMessageKeyGenerationSuccess(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setMessageKeyGenerationSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageKeyGenerationSuccess = value;
    }

    public final void setMessageEmailChangeSuccess(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setMessageEmailChangeSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageEmailChangeSuccess = value;
    }

    public final void setMessageContactChangeSuccess(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setMessageContactChangeSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageContactChangeSuccess = value;
    }

    public final void setMessagePasswordChangeSuccess(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setMessagePasswordChangeSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messagePasswordChangeSuccess = value;
    }

    public final void setMessageEnableOtpSuccess(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setMessageEnableOtpSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageEnableOtpSuccess = value;
    }

    public final void setMessageSecurityChangeSuccess(final String value)
    {
        final String methodName = UserAccountController.CNAME + "#setMessageSecurityChangeSuccess(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.messageSecurityChangeSuccess = value;
    }

    @RequestMapping(value = "/default", method = RequestMethod.GET)
    public final ModelAndView showDefaultPage()
    {
        final String methodName = UserAccountController.CNAME + "#showDefaultPage()";

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

        mView.setViewName(this.myAccountPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/password", method = RequestMethod.GET)
    public final ModelAndView showPasswordChange()
    {
        final String methodName = UserAccountController.CNAME + "#showPasswordChange()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();
        AccountChangeData changeReq = new AccountChangeData();

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

        if ((userAccount.getStatus() == LoginStatus.RESET) || (userAccount.getStatus() == LoginStatus.EXPIRED))
        {
            if (userAccount.getStatus() == LoginStatus.RESET)
            {
                changeReq.setIsReset(true);
            }

            mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessagePasswordExpired());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("UserChangeRequest: {}", changeReq);
        }

        mView.addObject(Constants.COMMAND, changeReq);
        mView.setViewName(this.changePasswordPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/enable-otp", method = RequestMethod.GET)
    public final ModelAndView showEnableOtp()
    {
        final String methodName = UserAccountController.CNAME + "#showEnableOtp()";

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

        mView.addObject(Constants.COMMAND, new AccountChangeData());
        mView.setViewName(this.enableOtpPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/security", method = RequestMethod.GET)
    public final ModelAndView showSecurityChange()
    {
        final String methodName = UserAccountController.CNAME + "#showSecurityChange()";

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

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostName(hRequest.getRemoteHost());
            reqInfo.setHostAddress(hRequest.getRemoteAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            AccountResetRequest request = new AccountResetRequest();
            request.setHostInfo(reqInfo);
            request.setUserAccount(userAccount);
            request.setApplicationId(this.appConfig.getApplicationId());
            request.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetRequest: {}", request);
            }

            IAccountResetProcessor resetProcess = new AccountResetProcessorImpl();

            AccountResetResponse response = resetProcess.obtainUserSecurityConfig(request);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountResetResponse: {}", response);
            }

            if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                mView.addObject("questionList", response.getQuestionList());
                mView.addObject(Constants.COMMAND, new AccountChangeData());
                mView.setViewName(this.changeSecurityPage);
            }
            else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
            {
                mView.setViewName(this.appConfig.getUnauthorizedPage());
            }
            else
            {
                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
        }
        catch (AccountResetException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/email", method = RequestMethod.GET)
    public final ModelAndView showEmailChange()
    {
        final String methodName = UserAccountController.CNAME + "#showEmailChange()";

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

        mView.addObject(Constants.COMMAND, new AccountChangeData());
        mView.setViewName(this.changeEmailPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public final ModelAndView showContactChange()
    {
        final String methodName = UserAccountController.CNAME + "#showContactChange()";

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

        mView.addObject(Constants.COMMAND, new AccountChangeData());
        mView.setViewName(this.changeContactPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public final ModelAndView doCancelRequest()
    {
        final String methodName = UserAccountController.CNAME + "#doCancelRequest()";

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

        switch (userAccount.getStatus())
        {
            case EXPIRED:
                hSession.removeAttribute(Constants.USER_ACCOUNT);
                hSession.invalidate();

                mView = new ModelAndView(new RedirectView());
                mView.setViewName(this.appConfig.getLogonRedirect());

                break;
            case RESET:
                hSession.removeAttribute(Constants.USER_ACCOUNT);
                hSession.invalidate();

                mView = new ModelAndView(new RedirectView());
                mView.setViewName(this.appConfig.getLogonRedirect());

                break;
            default:
                break;
        }

        mView.addObject(Constants.RESPONSE_MESSAGE, this.appConfig.getMessageRequestCanceled());
        mView.setViewName(this.myAccountPage);

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/regenerate-keys", method = RequestMethod.GET)
    public final ModelAndView doRegenerateKeys()
    {
        final String methodName = UserAccountController.CNAME + "#doRegenerateKeys()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountChangeProcessor processor = new AccountChangeProcessorImpl();

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
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setHostName(hRequest.getRemoteHost());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            // reset keys !
            AccountChangeRequest req = new AccountChangeRequest();
            req.setApplicationId(this.appConfig.getApplicationId());
            req.setApplicationName(this.appConfig.getApplicationName());
            req.setHostInfo(reqInfo);
            req.setUserAccount(userAccount);
            req.setRequestor(userAccount);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeRequest: {}", req);
            }

            AccountChangeResponse response = processor.changeUserKeys(req);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeResponse: {}", response);
            }

            if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                mView.addObject(Constants.RESPONSE_MESSAGE, this.messageKeyGenerationSuccess);
            }
            else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
            {
                mView.setViewName(this.appConfig.getUnauthorizedPage());
            }
            else
            {
                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
        }
        catch (AccountChangeException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public final ModelAndView doPasswordChange(@ModelAttribute("changeReq") final AccountChangeData changeReq, final BindingResult bindResult)
    {
        final String methodName = UserAccountController.CNAME + "#doPasswordChange(@ModelAttribute(\"changeReq\") final UserChangeRequest changeReq, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", changeReq);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        AuthenticationData userSecurity = null;
        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountChangeProcessor processor = new AccountChangeProcessorImpl();

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

        // validate our new password here
        this.passwordValidator.validate(changeReq, bindResult);

        if (bindResult.hasErrors())
        {
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            AccountChangeData command = new AccountChangeData();
            changeReq.setIsReset(changeReq.isReset());

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, command);
            mView.setViewName(this.changePasswordPage);

            return mView;
        }

        try
        {
            if (userAccount.getStatus() == LoginStatus.RESET)
            {
                userSecurity = new AuthenticationData();
                userSecurity.setNewPassword(changeReq.getConfirmPassword());
            }
            else
            {
                userSecurity = new AuthenticationData();
                userSecurity.setPassword(changeReq.getCurrentPassword());
                userSecurity.setNewPassword(changeReq.getConfirmPassword());
            }

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationData: {}", userSecurity);
            }

            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setHostName(hRequest.getRemoteHost());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            AccountChangeRequest request = new AccountChangeRequest();
            request.setHostInfo(reqInfo);
            request.setRequestor(userAccount);
            request.setUserAccount(userAccount);
            request.setUserSecurity(userSecurity);
            request.setIsReset(changeReq.isReset());
            request.setApplicationId(this.appConfig.getApplicationId());
            request.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeRequest: {}", request);
            }

            AccountChangeResponse response = processor.changeUserPassword(request);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeResponse: {}", response);
            }

            if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                // yay
                if (changeReq.isReset())
                {
                    if (DEBUG)
                    {
                        DEBUGGER.debug("Invalidating existing session object...");
                    }

                    hRequest.getSession().removeAttribute(Constants.USER_ACCOUNT);
                    hRequest.getSession().invalidate();

                    // redirect to logon page
                    mView = new ModelAndView(new RedirectView());
                    mView.addObject(Constants.RESPONSE_MESSAGE, this.messagePasswordChangeSuccess);
                    mView.setViewName(this.appConfig.getLogonRedirect());

                    if (DEBUG)
                    {
                        DEBUGGER.debug("ModelAndView: {}", mView);
                    }

                    return mView;
                }

                UserAccount resAccount = response.getUserAccount();

                if (DEBUG)
                {
                    DEBUGGER.debug("UserAccount: {}", resAccount);
                }

                hSession.removeAttribute(Constants.USER_ACCOUNT);
                hSession.setAttribute(Constants.USER_ACCOUNT, resAccount);

                mView.addObject(Constants.RESPONSE_MESSAGE, this.messagePasswordChangeSuccess);
                mView.setViewName(this.myAccountPage);
            }
            else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
            {
                mView.setViewName(this.appConfig.getUnauthorizedPage());
            }
            else
            {
                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
        }
        catch (AccountChangeException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            mView.setViewName(this.appConfig.getErrorResponsePage());

            return mView;
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/enable-otp", method = RequestMethod.POST)
    public final ModelAndView doEnableOtp(@ModelAttribute("changeReq") final AccountChangeData changeReq, final BindingResult bindResult)
    {
        final String methodName = UserAccountController.CNAME + "#doEnableOtp(@ModelAttribute(\"changeReq\") final UserChangeRequest changeReq, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", changeReq);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountChangeProcessor processor = new AccountChangeProcessorImpl();

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

        // validate our new password here
        this.passwordValidator.validate(changeReq, bindResult);

        if (bindResult.hasErrors())
        {
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, new AccountChangeData());
            mView.setViewName(this.enableOtpPage);
        }

        try
        {
            AuthenticationData userSecurity = new AuthenticationData();
            userSecurity.setPassword(changeReq.getCurrentPassword());

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationData: {}", userSecurity);
            }

            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setHostName(hRequest.getRemoteHost());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            AccountChangeRequest request = new AccountChangeRequest();
            request.setHostInfo(reqInfo);
            request.setRequestor(userAccount);
            request.setUserAccount(userAccount);
            request.setUserSecurity(userSecurity);
            request.setApplicationId(this.appConfig.getApplicationId());
            request.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeRequest: {}", request);
            }

            AccountChangeResponse response = processor.enableOtpAuth(request);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeResponse: {}", response);
            }

            if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                mView.addObject(Constants.RESPONSE_MESSAGE, this.messageEnableOtpSuccess);
                mView.setViewName(this.myAccountPage);
            }
            else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
            {
                mView.setViewName(this.appConfig.getUnauthorizedPage());
            }
            else
            {
                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
        }
        catch (AccountChangeException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            mView.setViewName(this.appConfig.getErrorResponsePage());

            return mView;
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/security", method = RequestMethod.POST)
    public final ModelAndView doSecurityChange(@ModelAttribute("changeReq") final AccountChangeData changeReq, final BindingResult bindResult)
    {
        final String methodName = UserAccountController.CNAME + "#doSecurityChange(@ModelAttribute(\"changeReq\") final UserChangeRequest changeReq, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", changeReq);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountChangeProcessor processor = new AccountChangeProcessorImpl();

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

        // validate our new password here
        this.securityValidator.validate(changeReq, bindResult);

        if (bindResult.hasErrors())
        {
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, new AccountChangeData());
            mView.setViewName(this.changeSecurityPage);

            return mView;
        }

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setHostName(hRequest.getRemoteHost());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            AuthenticationData userSecurity = new AuthenticationData();
            userSecurity.setSecQuestionOne(changeReq.getSecQuestionOne());
            userSecurity.setSecQuestionTwo(changeReq.getSecQuestionTwo());
            userSecurity.setSecAnswerOne(changeReq.getSecQuestionOne());
            userSecurity.setSecAnswerTwo(changeReq.getSecAnswerTwo());
            userSecurity.setPassword(changeReq.getCurrentPassword());

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationData: {}", userSecurity);
            }

            AccountChangeRequest request = new AccountChangeRequest();
            request.setHostInfo(reqInfo);
            request.setRequestor(userAccount);
            request.setUserAccount(userAccount);
            request.setUserSecurity(userSecurity);
            request.setApplicationId(this.appConfig.getApplicationId());
            request.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeRequest: {}", request);
            }

            AccountChangeResponse response = processor.changeUserSecurity(request);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeResponse: {}", response);
            }

            if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                mView.addObject(Constants.RESPONSE_MESSAGE, this.messageSecurityChangeSuccess);
                mView.setViewName(this.myAccountPage);
            }
            else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
            {
                mView.setViewName(this.appConfig.getUnauthorizedPage());
            }
            else
            {
                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
        }
        catch (AccountChangeException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    public final ModelAndView doEmailChange(@ModelAttribute("changeReq") final AccountChangeData changeReq, final BindingResult bindResult)
    {
        final String methodName = UserAccountController.CNAME + "#doEmailChange(@ModelAttribute(\"changeReq\") final UserChangeRequest changeReq, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", changeReq);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final EmailAddressValidator emailValidator = this.appConfig.getEmailValidator();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountChangeProcessor processor = new AccountChangeProcessorImpl();

        if (DEBUG)
        {
            DEBUGGER.debug("ServletRequestAttributes: {}", requestAttributes);
            DEBUGGER.debug("HttpServletRequest: {}", hRequest);
            DEBUGGER.debug("HttpSession: {}", hSession);
            DEBUGGER.debug("EmailAddressValidator: {}", emailValidator);
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

        emailValidator.validate(changeReq.getEmailAddr(), bindResult);

        if (bindResult.hasErrors())
        {
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, new AccountChangeData());
            mView.setViewName(this.changeEmailPage);

            return mView;
        }

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setHostName(hRequest.getRemoteHost());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            AuthenticationData userSecurity = new AuthenticationData();
            userSecurity.setPassword(changeReq.getCurrentPassword());

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationData: {}", userSecurity);
            }

            UserAccount modAccount = userAccount;
            modAccount.setEmailAddr(changeReq.getEmailAddr());

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", modAccount);
            }

            AccountChangeRequest request = new AccountChangeRequest();
            request.setHostInfo(reqInfo);
            request.setRequestor(userAccount);
            request.setUserAccount(userAccount);
            request.setUserSecurity(userSecurity);
            request.setApplicationId(this.appConfig.getApplicationId());
            request.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeRequest: {}", request);
            }

            AccountChangeResponse response = processor.changeUserEmail(request);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeResponse: {}", response);
            }

            if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                // yay
                hSession.removeAttribute(Constants.USER_ACCOUNT);
                hSession.setAttribute(Constants.USER_ACCOUNT, response.getUserAccount());

                mView.addObject(Constants.RESPONSE_MESSAGE, this.messageEmailChangeSuccess);
                mView.setViewName(this.myAccountPage);
            }
            else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
            {
                mView.setViewName(this.appConfig.getUnauthorizedPage());
            }
            else
            {
                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
        }
        catch (AccountChangeException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public final ModelAndView doContactChange(@ModelAttribute("changeReq") final AccountChangeData changeReq, final BindingResult bindResult)
    {
        final String methodName = UserAccountController.CNAME + "#doEmailChange(@ModelAttribute(\"changeReq\") final UserChangeRequest changeReq, final BindingResult bindResult)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("UserChangeRequest: {}", changeReq);
            DEBUGGER.debug("BindingResult: {}", bindResult);
        }

        ModelAndView mView = new ModelAndView();

        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        final HttpServletRequest hRequest = requestAttributes.getRequest();
        final HttpSession hSession = hRequest.getSession();
        final UserAccount userAccount = (UserAccount) hSession.getAttribute(Constants.USER_ACCOUNT);
        final IAccountChangeProcessor processor = new AccountChangeProcessorImpl();

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

        // validate our new password here
        this.telValidator.validate(changeReq, bindResult);

        if (bindResult.hasErrors())
        {
            ERROR_RECORDER.error("Errors: {}", bindResult.getAllErrors());

            mView.addObject(Constants.ERROR_MESSAGE, this.appConfig.getMessageValidationFailed());
            mView.addObject(Constants.BIND_RESULT, bindResult.getAllErrors());
            mView.addObject(Constants.COMMAND, new AccountChangeData());
            mView.setViewName(this.changeContactPage);

            return mView;
        }

        try
        {
            RequestHostInfo reqInfo = new RequestHostInfo();
            reqInfo.setHostAddress(hRequest.getRemoteAddr());
            reqInfo.setHostName(hRequest.getRemoteHost());

            if (DEBUG)
            {
                DEBUGGER.debug("RequestHostInfo: {}", reqInfo);
            }

            AuthenticationData userSecurity = new AuthenticationData();
            userSecurity.setPassword(changeReq.getCurrentPassword());

            if (DEBUG)
            {
                DEBUGGER.debug("AuthenticationData: {}", userSecurity);
            }

            UserAccount modAccount = userAccount;
            modAccount.setPagerNumber(changeReq.getPagerNumber());
            modAccount.setTelephoneNumber(changeReq.getTelNumber());

            if (DEBUG)
            {
                DEBUGGER.debug("UserAccount: {}", modAccount);
            }

            AccountChangeRequest request = new AccountChangeRequest();
            request.setHostInfo(reqInfo);
            request.setRequestor(userAccount);
            request.setUserAccount(userAccount);
            request.setUserSecurity(userSecurity);
            request.setApplicationId(this.appConfig.getApplicationId());
            request.setApplicationName(this.appConfig.getApplicationName());

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeRequest: {}", request);
            }

            AccountChangeResponse response = processor.changeUserContact(request);

            if (DEBUG)
            {
                DEBUGGER.debug("AccountChangeResponse: {}", response);
            }

            if (response.getRequestStatus() == SecurityRequestStatus.SUCCESS)
            {
                // yay
                hSession.removeAttribute(Constants.USER_ACCOUNT);
                hSession.setAttribute(Constants.USER_ACCOUNT, response.getUserAccount());

                mView.addObject(Constants.RESPONSE_MESSAGE, this.messageContactChangeSuccess);
                mView.setViewName(this.myAccountPage);
            }
            else if (response.getRequestStatus() == SecurityRequestStatus.UNAUTHORIZED)
            {
                mView.setViewName(this.appConfig.getUnauthorizedPage());
            }
            else
            {
                mView.setViewName(this.appConfig.getErrorResponsePage());
            }
        }
        catch (AccountChangeException acx)
        {
            ERROR_RECORDER.error(acx.getMessage(), acx);

            mView.setViewName(this.appConfig.getErrorResponsePage());
        }

        if (DEBUG)
        {
            DEBUGGER.debug("ModelAndView: {}", mView);
        }

        return mView;
    }
}
