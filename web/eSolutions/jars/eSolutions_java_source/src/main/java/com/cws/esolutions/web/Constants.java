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
package com.cws.esolutions.web;
/*
 * Project: eSolutions_java_source
 * Package: com.cws.esolutions.web
 * File: Constants.java
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * kmhuntly@gmail.com   11/23/2008 22:39:20             Created.
 */
public class Constants
{
    public static final String INFO_LOGGER = "INFO_RECORDER.";
    public static final String ERROR_LOGGER = "ERROR_RECORDER.";
    public static final String AUDIT_LOGGER = "AUDIT_RECORDER.";
    public static final String DEBUGGER = "ESOLUTIONS_DEBUGGER";

    public static final String USER_ACCOUNT = "userAccount";
    public static final String USER_SECURITY = "userSecurity";
    public static final String ALLOW_RESET = "allowUserReset";
    public static final String ERROR_MESSAGE = "errorMessage"; // returned from controllers
    public static final String ERROR_RESPONSE = "errorResponse"; // returned from the getResponse() method on DTO objects from esol core
    public static final String SEARCH_RESULTS = "searchResults";
    public static final String RESPONSE_MESSAGE = "responseMessage"; // returned from controllers
    public static final String MESSAGE_RESPONSE = "messageResponse"; // returned from the getResponse() method on DTO objects from esol core
    public static final String LINE_BREAK = System.getProperty("line.separator");
}
