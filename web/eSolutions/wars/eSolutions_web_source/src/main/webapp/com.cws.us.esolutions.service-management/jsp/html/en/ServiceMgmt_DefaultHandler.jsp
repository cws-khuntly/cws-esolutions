<%--
/**
 * Copyright (c) 2009 - 2013 By: CWS, Inc.
 * 
 * All rights reserved. These materials are confidential and
 * proprietary to CaspersBox Web Services N.A and no part of
 * these materials should be reproduced, published in any form
 * by any means, electronic or mechanical, including photocopy
 * or any information storage or retrieval system not should
 * the materials be disclosed to third parties without the
 * express written authorization of CaspersBox Web Services, N.A.
 *
 * eSolutions_web_source
 * com.cws.us.esolutions.service-management/jsp/html/en
 * ServiceMgmt_DefaultHandler.jsp
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author kh05451
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * kh05451 @ Jan 16, 2013 11:53:26 AM
 *     Created.
 */
--%>

<div class="feature">
    <c:if test="${not empty messageResponse}">
        <p id="info"><spring:message code="${messageResponse}" /></p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>

    <spring:message code="select.request.type" />
    <br /><br />
    <table id="selectRequest">
        <tr>
            <td>
                <a href="${pageContext.request.contextPath}/ui/service-management/add-platform"
                    title="<spring:message code='select.request.add.platform' />"><spring:message code="select.request.add.platform" /></a>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/ui/service-management/add-project"
                    title="<spring:message code='select.request.add.project' />"><spring:message code="select.request.add.project" /></a>
            </td>
        </tr>
    </table>
</div>
<br /><br />
