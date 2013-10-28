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
package com.cws.esolutions.security.dao.audit.interfaces;

import java.util.List;

import org.slf4j.Logger;

import javax.sql.DataSource;

import java.sql.SQLException;

import org.slf4j.LoggerFactory;

import com.cws.esolutions.security.SecurityConstants;
import com.cws.esolutions.security.SecurityServiceBean;
import com.cws.esolutions.core.controllers.ResourceControllerBean;
/**
 * SecurityService
 * com.cws.esolutions.security.audit.dao.interfaces
 * IAuditDAO.java
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
 * kh05451 @ Oct 30, 2012 10:38:04 AM
 *     Created.
 */
public interface IAuditDAO
{
    static final String CNAME = IAuditDAO.class.getName();
    static final SecurityServiceBean svcBean = SecurityServiceBean.getInstance();
    static final ResourceControllerBean resBean = svcBean.getResourceBean();
    static final DataSource dataSource = resBean.getDataSource().get(SecurityConstants.INIT_AUDITDS_MANAGER);

    static final Logger DEBUGGER = LoggerFactory.getLogger(SecurityConstants.DEBUGGER);
    static final boolean DEBUG = DEBUGGER.isDebugEnabled();
    static final Logger AUDIT_RECORDER = LoggerFactory.getLogger(SecurityConstants.AUDIT_LOGGER + CNAME);
    static final Logger ERROR_RECORDER = LoggerFactory.getLogger(SecurityConstants.ERROR_LOGGER + CNAME);

    void auditRequestedOperation(final List<String> auditRequest) throws SQLException;

    List<String[]> getAuditInterval(final String username, final int startRow) throws SQLException;

    int getAuditCount(final String guid) throws SQLException;
}