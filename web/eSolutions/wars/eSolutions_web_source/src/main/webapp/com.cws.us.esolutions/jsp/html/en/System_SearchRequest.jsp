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
 * com.cws.us.esolutions/jsp/html/en
 * System_SearchRequest.jsp
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
    <c:if test="${isHelpSearch eq 'true'}">
        <div id="breadcrumb" class="lpstartover">
		    <a href="${pageContext.request.contextPath}/ui/knowledgebase/create-article"
                title="<spring:message code='kbase.create.article' />"><spring:message code="kbase.create.article" /></a> /
            <c:if test="${sessionScope.userAccount.role eq 'ADMIN' or sessionScope.userAccount.role eq 'SITEADMIN'}">
		        / <a href="${pageContext.request.contextPath}/ui/knowledgebase/show-approvals"
		            title="<spring:message code='kbase.list.pending.approvals' />"><spring:message code='kbase.list.pending.approvals' /></a> /
            </c:if>
        </div>
    </c:if>

    <c:if test="${not empty messageResponse}">
        <p id="info">${messageResponse}</p>
    </c:if>
    <c:if test="${not empty errorResponse}">
        <p id="error">${errorResponse}</p>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <p id="error"><spring:message code="${errorMessage}" /></p>
    </c:if>

    <spring:message code="site.search.submit.request" />

    <p id="validationError" />

    <form:form id="searchRequest" name="searchRequest" action="${pageContext.request.contextPath}${postUrl}" method="post">
        <table id="serverSearch">
            <tr>
                <td>
                    <label id="txtSearchData"><spring:message code="search.data" /><br /></label>
                </td>
                <td>
                    <form:input path="searchTerms" />
                    <form:errors path="searchTerms" cssClass="validationError" />
                </td>
            </tr>
        </table>
        <br /><br />
        <table id="inputItems">
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

    <c:if test="${not empty searchResults}">
        <p id="splitter" />

        <strong><spring:message code="search.results" /></strong>
        <br /><br />
        <table id="searchResults">
            <c:forEach var="result" items="${searchResults}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}${requestUrl}${result.path}" title="${result.title}">${result.title}</a></td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>
<br /><br />
