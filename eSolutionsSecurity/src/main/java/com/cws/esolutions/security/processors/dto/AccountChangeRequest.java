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
 */
package com.cws.esolutions.security.processors.dto;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.processors.dto
 * File: AccountChangeRequest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.io.Serializable;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;

import com.cws.esolutions.security.dto.UserAccount;
import com.cws.esolutions.security.SecurityServiceConstants;
import com.cws.esolutions.security.processors.dto.RequestHostInfo;
import com.cws.esolutions.security.processors.dto.AuthenticationData;
/**
 * @author cws-khuntly
 * @version 1.0
 * @see java.io.Serializable
 */
public class AccountChangeRequest implements Serializable
{
    private boolean isReset = false;
    private String applicationId = null;
    private UserAccount requestor = null;
    private String applicationName = null;
    private UserAccount userAccount = null;
    private RequestHostInfo hostInfo = null;
    private AuthenticationData userSecurity = null;

    private static final long serialVersionUID = -7265044360536886625L;
    private static final String CNAME = AccountChangeRequest.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityServiceConstants.ERROR_LOGGER);

    public final void setHostInfo(final RequestHostInfo value)
    {
        final String methodName = AccountChangeRequest.CNAME + "#setBaseDN(final RequestHostInfo value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hostInfo = value;
    }

    public final void setUserAccount(final UserAccount value)
    {
        final String methodName = AccountChangeRequest.CNAME + "#setLoginType(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.userAccount = value;
    }

    public final void setUserSecurity(final AuthenticationData value)
    {
        final String methodName = AccountChangeRequest.CNAME + "#setLoginType(final AuthenticationData value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        this.userSecurity = value;
    }

    public final void setApplicationName(final String value)
    {
        final String methodName = AccountChangeRequest.CNAME + "#setApplicationName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationName = value;
    }

    public final void setApplicationId(final String value)
    {
        final String methodName = AccountChangeRequest.CNAME + "#setApplicationId(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.applicationId = value;
    }

    public final void setRequestor(final UserAccount value)
    {
        final String methodName = AccountChangeRequest.CNAME + "#setRequestor(final UserAccount value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.requestor = value;
    }

    public final void setIsReset(final boolean value)
    {
        final String methodName = AccountChangeRequest.CNAME + "#setIsReset(final boolean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.isReset = value;
    }

    public final RequestHostInfo getHostInfo()
    {
        final String methodName = AccountChangeRequest.CNAME + "#getHostInfo()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hostInfo);
        }

        return this.hostInfo;
    }

    public final UserAccount getUserAccount()
    {
        final String methodName = AccountChangeRequest.CNAME + "#getLoginType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.userAccount);
        }

        return this.userAccount;
    }

    public final AuthenticationData getUserSecurity()
    {
        final String methodName = AccountChangeRequest.CNAME + "#getUserSecurity()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        return this.userSecurity;
    }

    public final String getApplicationName()
    {
        final String methodName = AccountChangeRequest.CNAME + "#getApplicationName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationName);
        }

        return this.applicationName;
    }

    public final String getApplicationId()
    {
        final String methodName = AccountChangeRequest.CNAME + "#getApplicationId()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.applicationId);
        }

        return this.applicationId;
    }

    public final UserAccount getRequestor()
    {
        final String methodName = AccountChangeRequest.CNAME + "#getRequestor()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.requestor);
        }

        return this.requestor;
    }

    public final boolean isReset()
    {
        final String methodName = AccountChangeRequest.CNAME + "#isReset()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.isReset);
        }

        return this.isReset;
    }

    @Override
    public final String toString()
    {
        final String methodName = AccountChangeRequest.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + SecurityServiceConstants.LINE_BREAK + "{" + SecurityServiceConstants.LINE_BREAK);

        for (Field field : this.getClass().getDeclaredFields())
        {
            if (DEBUG)
            {
                DEBUGGER.debug("field: {}", field);
            }

            if (!(field.getName().equals("methodName")) &&
                    (!(field.getName().equals("CNAME"))) &&
                    (!(field.getName().equals("DEBUGGER"))) &&
                    (!(field.getName().equals("DEBUG"))) &&
                    (!(field.getName().equals("ERROR_RECORDER"))) &&
                    (!(field.getName().equals("userSecurity"))) &&
                    (!(field.getName().equals("serialVersionUID"))))
            {
                try
                {
                    if (field.get(this) != null)
                    {
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + SecurityServiceConstants.LINE_BREAK);
                    }
                }
                catch (IllegalAccessException iax)
                {
                    ERROR_RECORDER.error(iax.getMessage(), iax);
                }
            }
        }

        sBuilder.append('}');

        if (DEBUG)
        {
            DEBUGGER.debug("sBuilder: {}", sBuilder);
        }

        return sBuilder.toString();
    }
}
