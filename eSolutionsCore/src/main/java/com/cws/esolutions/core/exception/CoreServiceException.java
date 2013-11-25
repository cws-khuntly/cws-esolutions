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
package com.cws.esolutions.core.exception;

import java.util.Arrays;
import java.util.ArrayList;

import com.cws.esolutions.core.CoreServiceBean;
import com.cws.esolutions.core.utils.EmailUtils;
import com.cws.esolutions.core.config.ExceptionConfig;
import com.cws.esolutions.core.processors.dto.EmailMessage;
/**
 * eSolutionsCore
 * com.cws.esolutions.core.exception
 * CoreServiceException.java
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
 * kh05451 @ Nov 1, 2012 11:42:56 AM
 *     Created.
 */
public class CoreServiceException extends Exception
{
    private static final long serialVersionUID = -4141507100554321719L;

    public CoreServiceException(final String message)
    {
        super(message);

        CoreServiceException.sendExceptionLetter(message);
    }

    public CoreServiceException(final Throwable throwable)
    {
        super(throwable);

        CoreServiceException.sendExceptionLetter(throwable.getMessage());
    }

    public CoreServiceException(final String message, final Throwable throwable)
    {
        super(message, throwable);

        CoreServiceException.sendExceptionLetter(message);
    }

    private static void sendExceptionLetter(final String message)
    {
        final CoreServiceBean bean = CoreServiceBean.getInstance();
        final ExceptionConfig config = bean.getConfigData().getExceptionConfig();

        if (config.getSendExceptionNotifications())
        {
            StringBuilder builder = new StringBuilder()
                .append("A CoreServiceException was thrown: \n")
                .append("Message: " + message + "\n")
                .append("Stacktrace: \n\n");

            StackTraceElement[] elements = Thread.currentThread().getStackTrace();

            for (StackTraceElement element : elements)
            {
                builder.append(element + "\n");
            }

            EmailMessage email = new EmailMessage();
            email.setIsAlert(true);
            email.setMessageSubject("CoreServiceException occurred !");
            email.setEmailAddr(new ArrayList<>(Arrays.asList(config.getEmailFrom())));
            email.setMessageTo(config.getNotificationAddress());
            email.setMessageBody(builder.toString());

            // modified "sendEmailMessage(), removing thrown exceptions
            EmailUtils.sendExceptionLetter(email);
        }
    }
}
