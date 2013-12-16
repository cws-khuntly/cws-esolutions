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
package com.cws.esolutions.security.keymgmt.interfaces;

import org.slf4j.Logger;
import java.security.KeyPair;
import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.security.config.xml.AuthRepo;
import com.cws.esolutions.security.config.xml.AuthData;
import com.cws.esolutions.security.config.xml.KeyConfig;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
import com.cws.esolutions.security.keymgmt.exception.KeyManagementException;
import com.cws.esolutions.security.dao.reference.impl.SecurityReferenceDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.ISecurityReferenceDAO;
import com.cws.esolutions.security.dao.reference.impl.UserServiceInformationDAOImpl;
import com.cws.esolutions.security.dao.reference.interfaces.IUserServiceInformationDAO;
/*
 * Project: eSolutionsCore
 * Package: com.cws.esolutions.core.dao.interfaces
 * File: IPackageDataDAO.java
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 4, 2013 3:36:54 PM
 *     Created.
 */
public interface KeyManager
{
    static final ISecurityReferenceDAO secRef = new SecurityReferenceDAOImpl();
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final ResourceControllerBean resBean = svcBean.getResourceBean();
    static final AuthRepo authRepo = svcBean.getConfigData().getAuthRepo();
    static final AuthData authData = svcBean.getConfigData().getAuthData();
    static final KeyConfig keyConfig = svcBean.getConfigData().getKeyConfig();

    static final IUserServiceInformationDAO userSvcs = new UserServiceInformationDAOImpl();
    
    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + KeyManager.class.getName());

    KeyPair returnKeys(final String guid) throws KeyManagementException;

    boolean createKeys(final String guid) throws KeyManagementException;

    boolean removeKeys(final String guid) throws KeyManagementException;
}
