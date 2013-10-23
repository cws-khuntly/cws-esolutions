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
 * com.cws.us.esolutions.system-management/jsp/html/en
 * SystemManagement_RemoteDate.jsp
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
    <div id="breadcrumb" class="lpstartover">
        <a href="${pageContext.request.contextPath}/ui/system-check/telnet"
            title="<spring:message code='select.request.type.telnet' />"><spring:message code='select.request.type.telnet' /></a> / 
    </div>

    <c:if test="${not empty messageResponse}">
        <p id="info">${messageResponse}</p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p id="error"><spring:message code="${errorMessage}" /></p>
    </c:if>

    <spring:message code="telnet.request.enter.information" />

    <p id="validationError" />

    <form:form id="submitRemoteDate" name="submitRemoteDate" action="${pageContext.request.contextPath}/ui/system-check/remote-date" method="post">
        <table id="remoteDate">
            <tr>
                <td><label id="txtTargetHostName"><spring:message code="remotedate.request.provide.hostname" /></label></td>
                <td>
	                <c:choose>
	                    <c:when test="${not empty serverList}">
		                    <form:select path="targetServer">
								<option><spring:message code="select.default" /></option>
								<option><spring:message code="select.spacer" /></option>
		                        <form:options items="${serverList}" />
		                    </form:select>
	                    </c:when>
	                    <c:otherwise>
			                <td><form:input path="targetServer" /></td>
			            </c:otherwise>
			        </c:choose>
			    </td>
		        <td><form:errors path="targetServer" cssClass="validationError" /></td>
            </tr>
        </table>
        <br /><br />
        <table>
            <tr>
				<td>
				    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
				</td>
				<td>
				    <input type="button" name="reset" value="<spring:message code='button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
				</td>
				<td>
				    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
				</td>
            </tr>
        </table>
    </form:form>
</div>
<br /><br />
