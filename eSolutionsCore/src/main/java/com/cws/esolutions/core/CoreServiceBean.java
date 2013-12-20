/*
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
package com.cws.esolutions.core;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core
 * File: CoreServiceBean.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import java.lang.reflect.Field;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.core.config.xml.CoreConfigurationData;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
/**
 * Interface for the Application Data DAO layer. Allows access
 * into the asset management database to obtain, modify and remove
 * application information.
 *
 * @author khuntly
 * @version 1.0
 */
public class CoreServiceBean
{
    private String osType = null;
    private String hostName = null;
    private CoreConfigurationData configData = null;
    private ResourceControllerBean resourceBean = null;

    private static CoreServiceBean instance = null;

    private static final String CNAME = CoreServiceBean.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(Constants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    private static final Logger ERROR_RECORDER = LoggerFactory.getLogger(Constants.ERROR_LOGGER);

    /**
     * Returns a static instance of this bean
     *
     * @return InitializerBean
     */
    public static final CoreServiceBean getInstance()
    {
        final String method = CNAME + "#getInstance()";

        if (DEBUG)
        {
            DEBUGGER.debug(method);
            DEBUGGER.debug("instance: {}", CoreServiceBean.instance);
        }

        if (CoreServiceBean.instance == null)
        {
            CoreServiceBean.instance = new CoreServiceBean();
        }

        return CoreServiceBean.instance;
    }

    /**
     * Sets a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @param value - The complete copy of application configuration information
     */
    public final void setConfigData(final CoreConfigurationData value)
    {
        final String methodName = CoreServiceBean.CNAME + "#setConfigData(final CoreConfigurationData value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.configData = value;
    }

    public final void setResourceBean(final ResourceControllerBean value)
    {
        final String methodName = CoreServiceBean.CNAME + "#setResourceBean(final ResourceControllerBean value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.resourceBean = value;
    }

    /**
     * Sets a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @param value - The system-provided Operating System name that the service
     * is running on
     */
    public final void setOsType(final String value)
    {
        final String methodName = CoreServiceBean.CNAME + "#setOsType(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.osType = value;
    }

    /**
     * Sets a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @param value - The system-provided hostname that the service is running
     * on
     */
    public final void setHostName(final String value)
    {
        final String methodName = CoreServiceBean.CNAME + "#setHostName(final String value)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", value);
        }

        this.hostName = value;
    }

    /**
     * Returns a static copy of the Application configuration as defined in the
     * configuration xml files.
     *
     * @return CoreConfigurationData - A complete copy of the application
     * configuration data.
     */
    public final CoreConfigurationData getConfigData()
    {
        final String methodName = CoreServiceBean.CNAME + "#getConfigData()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.configData);
        }

        return this.configData;
    }

    public final ResourceControllerBean getResourceBean()
    {
        final String methodName = CoreServiceBean.CNAME + "#getResourceBean()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.resourceBean);
        }

        return this.resourceBean;
    }

    /**
     * Returns the string representation of the system-provided Operating
     * System name.
     *
     * @return String - The string representation of the system-provided
     * Operating System name.
     */
    public final String getOsType()
    {
        final String methodName = CoreServiceBean.CNAME + "#getOsType()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.osType);
        }

        return this.osType;
    }

    /**
     * Returns the string representation of the system-provided hostname.
     *
     * @return String - The system-provided hostname
     */
    public final String getHostName()
    {
        final String methodName = CoreServiceBean.CNAME + "#getHostName()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", this.hostName);
        }

        return this.hostName;
    }

    @Override
    public final String toString()
    {
        final String methodName = CoreServiceBean.CNAME + "#toString()";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
        }

        final StringBuilder sBuilder = new StringBuilder()
            .append("[" + this.getClass().getName() + "]" + Constants.LINE_BREAK + "{" + Constants.LINE_BREAK);

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
                    (!(field.getName().equals("instance"))) &&
                    (!(field.getName().equals("serialVersionUID"))))
            {
                try
                {
                    if (field.get(this) != null)
                    {
                        sBuilder.append("\t" + field.getName() + " --> " + field.get(this) + Constants.LINE_BREAK);
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
