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
package com.cws.esolutions.security.utils;
/*
 * Project: eSolutionsSecurity
 * Package: com.cws.esolutions.security.utils
 * File: PasswordUtilsTest.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
import java.util.Map;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.Assert;
import java.util.Map.Entry;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;

import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.utils.PasswordUtils;
import com.cws.esolutions.security.listeners.SecurityServiceInitializer;

public class PasswordUtilsTest
{
    private static final SecurityServiceBean bean = SecurityServiceBean.getInstance();

    @Before public void setUp()
    {
        try
        {
            SecurityServiceInitializer.initializeService("SecurityService/config/ServiceConfig.xml", "SecurityService/logging/logging.xml", false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Assert.fail(e.getMessage());

            System.exit(1);
        }
    }

    @Test public void test()
    {
        String var = "rootDirectory = ${user.home}";

        System.out.println(expandEnvVars(var));

        //System.out.println(PasswordUtils.encryptText("MyTextValue", "OwTVX8+4u5!EF$l~eUep$kprFiPNGdU0Of4IS!M(lHgjDC3bK5lTemVPoYIQLTbF",
        //        bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
        //        bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
        //        bean.getConfigData().getSystemConfig().getEncoding()));
    }

    public static final String expandEnvVars(final String value)
    {
        String returnValue = null;

        if (!(StringUtils.contains(value, "$")))
        {
            return null;
        }

        final Properties sysProps = System.getProperties();
        final Map<String, String> envMap = System.getenv();
        final String text = StringUtils.replaceEachRepeatedly(value.split("=")[1].trim(), new String[] {"${", "}" }, new String[] { "", "" });

        for (Entry<Object, Object> property : sysProps.entrySet())
        {
            String key = (String) property.getKey();

            if (StringUtils.equals(key.trim(), text.trim()))
            {
                returnValue = sysProps.getProperty(key.trim());

                break;
            }
        }

        for (Entry<String, String> entry : envMap.entrySet())
        {
            String key = entry.getKey();

            if (StringUtils.equals(key.trim(), text.trim()))
            {
                returnValue = entry.getValue();

                break;
            }
        }

        return returnValue;
    }

    @Test public void encryptText()
    {
        Assert.assertEquals("xdwcvNbTtdBkcxvtn3g5BTHz1naNiq3tZAn255ai1hZtRUPiA0TyoLPs3fP6lC9YcvyNcreuFqEuse10nnyHAg==",
                PasswordUtils.encryptText("TestPasswordValue", "zHnDJVgtiJy3FNFDfSe9ZK1KW97zd1oDmA8awAoW7QnDR6i2wd9AfV2NmXOOVYJO",
                        bean.getConfigData().getSecurityConfig().getAuthAlgorithm(),
                        bean.getConfigData().getSecurityConfig().getIterations(),
                        bean.getConfigData().getSystemConfig().getEncoding()));
    }

    @Test public void testTwoWayHash()
    {
        Assert.assertEquals("41hvglQql38+cr8Et//rFFmrJk3Zfg8Xh5b4SLwtRZd0PGuC1a2Wq83iA/YY5mrOS8eh8ZElOJK4Ba43hiijGzbHo2skKg4UpLNf7zhpCowmJKYUcIeBmaUy7ivro8fEsxaHXW6WYeDmcAbmuENOjWft3q31KHtuGmZhluUk+b2navuW/4doetGtH/D8VoZI",
                PasswordUtils.encryptText("a?.Zd`5ExI%$wm@g/v;L$oq6yqFM$iFAmjVqx72pB|KwG65sd3,ukUDPo;H,|o.O",
                        "wHqqSZI63Et38DRwksM4WanElRHJoZvQkydokLsAo8YkF3NurF5BoTXllwpCd2Ub",
                        bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                        bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                        bean.getConfigData().getSystemConfig().getEncoding()));
    }

    @Test public void decryptText()
    {
        String returned = PasswordUtils.decryptText("G1ZTp/d9pufJ9JfW0tJBBCbyOQ3d8RcNyPcVfQdQ4eQ0qd15g7N04Mb1TzOJORMIU03D/480YgHVl6kiqq4u1tt8xD2qqxyNqEGX7dwMsgg=",
                64,
                bean.getConfigData().getSecurityConfig().getEncryptionAlgorithm(),
                bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                bean.getConfigData().getSystemConfig().getEncoding());

        System.out.println(returned);
        Assert.assertEquals("MyTextValue", returned);
    }

    @Test public void validateOtpValue()
    {
        Assert.assertTrue(PasswordUtils.validateOtpValue(bean.getConfigData().getSecurityConfig().getOtpVariance(),
                bean.getConfigData().getSecurityConfig().getOtpAlgorithm(),
                bean.getConfigData().getSecurityConfig().getEncryptionInstance(),
                "the secret", 0));
    }

    @After public void tearDown()
    {
        SecurityServiceInitializer.shutdown();
    }
}
