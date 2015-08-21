/*
 * Copyright (c) 2009 - 2014 CaspersBox Web Services
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
package com.cws.esolutions.core.main;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.utils
 * File: SQLUtils.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.CommandLineParser;

import com.cws.esolutions.core.CoreServiceConstants;
import com.cws.esolutions.core.exception.CoreServiceException;
import com.cws.esolutions.core.listeners.CoreServiceInitializer;
/**
 * @author khuntly
 * @version 1.0
 */
@SuppressWarnings("static-access")
public class SQLUtils
{
    private static Options options = null;
    private static OptionGroup sshOptions = null;

    private static final String CNAME = SQLUtils.class.getName();

    private static final Logger DEBUGGER = LoggerFactory.getLogger(CoreServiceConstants.DEBUGGER);
    private static final boolean DEBUG = DEBUGGER.isDebugEnabled();

    public enum LoadType
    {
        INSERT,
        APPEND,
        TRUNCATE,
        REPLACE;
    }

    static
    {
        OptionGroup commandOptions = new OptionGroup();
        commandOptions.addOption(OptionBuilder.withLongOpt("ssh")
            .withDescription("Perform an SSH connection to a target host")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("scp")
            .withDescription("Perform an SCP connection to a target host")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("telnet")
            .withDescription("Perform an telnet connection to a target host")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("tcp")
            .withDescription("Perform an TCP connection to a target host and put data on the request")
            .isRequired(true)
            .create());
        commandOptions.addOption(OptionBuilder.withLongOpt("http")
            .withDescription("Perform an HTTP request to a target host")
            .isRequired(true)
            .create());
    }

    public static final void main(final String[] args)
    {
        final String methodName = SQLUtils.CNAME + "#main(final String[] args)";

        if (DEBUG)
        {
            DEBUGGER.debug(methodName);
            DEBUGGER.debug("Value: {}", (Object) args);
        }

        if (args.length == 0)
        {
            HelpFormatter usage = new HelpFormatter();
            usage.printHelp(SQLUtils.CNAME, options, true);

            return;
        }

        try
        {
            CoreServiceInitializer.initializeService("C:/opt/cws/eSolutions/etc/eSolutionsCore/config/ServiceConfig.xml",
                "C:/opt/cws/eSolutions/etc/eSolutionsCore/logging/logging.xml", false);
        }
        catch (CoreServiceException csx)
        {
            System.err.println("An error occurred while loading configuration data: " + csx.getCause().getMessage());

            System.exit(1);
        }

        Options options = new Options();
        CommandLineParser parser = new PosixParser();

        try
        {
            if (StringUtils.equals(args[0], "ssh"))
            {
                options.addOptionGroup(sshOptions);

                CommandLine commandLine = parser.parse(options, args);

                if (commandLine.getOptions().length >= 1)
                {
                }
                else
                {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp(SQLUtils.CNAME + " ssh", options, true);
                }
            }
        }
        catch (ParseException px)
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SQLUtils.CNAME, options, true);
        }
    }
}
