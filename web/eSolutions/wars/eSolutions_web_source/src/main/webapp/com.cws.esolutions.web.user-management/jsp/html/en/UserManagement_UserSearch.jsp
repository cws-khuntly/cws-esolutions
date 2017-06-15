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
 * com.cws.us.esolutions.user-management/jsp/html/en
 * UserManagement_UserSearch.jsp
 *
 * $Id$
 * $Author$
 * $Date$
 * $Revision$
 * @author cws-khuntly
 * @version 1.0
 *
 * History
 * ----------------------------------------------------------------------------
 * cws-khuntly @ Jan 16, 2013 11:53:26 AM
 *     Created.
 */
--%>

<script>
<!--
    function validateForm(theForm)
    {
        var i = 0;
        var f = 0;
        var entryFound = false;

        for (f = 0; f < document.forms.length; f++)
        {
            // for each element in each form
            for (i = 0; i < document.forms[f].length; i++)
            {
                // if it's not a hidden element, disabled or a button
                if ((document.forms[f][i].type != "hidden") && (document.forms[f][i].disabled != true) && (document.forms[f][i].type != "button"))
                {
                    // clear it
                    if (document.forms[f][i].value != '')
                    {
                        entryFound = true;

                        break;
                    }
                }
            }
        }

        if (entryFound)
        {
            theForm.submit();
        }
        else
        {
            document.getElementById('validationError').innerHTML = 'A search criterion must be provided.';
            document.getElementById('txtUserName').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('username').focus();
        }
    }
//-->
</script>

<div id="sidebar">
    <h1><spring:message code="user.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/user-management/add-user" title="<spring:message code='user.mgmt.create.user' />"><spring:message code="user.mgmt.create.user" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="theme.search.header" /></h1>

    <div id="error"></div>

    <c:if test="${not empty fn:trim(messageResponse)}">
        <p id="info">${messageResponse}</p>
    </c:if>
    <c:if test="${not empty fn:trim(errorResponse)}">
        <p id="error">${errorResponse}</p>
    </c:if>
    <c:if test="${not empty fn:trim(responseMessage)}">
        <p id="info"><spring:message code="${responseMessage}" /></p>
    </c:if>
    <c:if test="${not empty fn:trim(errorMessage)}">
        <p id="error"><spring:message code="${errorMessage}" /></p>
    </c:if>
    <c:if test="${not empty fn:trim(param.responseMessage)}">
        <p id="info"><spring:message code="${param.responseMessage}" /></p>
    </c:if>
    <c:if test="${not empty fn:trim(param.errorMessage)}">
        <p id="error"><spring:message code="${param.errorMessage}" /></p>
    </c:if>

    <p>
        <form:form id="searchUserAccounts" name="searchUserAccounts" action="${pageContext.request.contextPath}/ui/user-management/search" method="post">
            <label id="txtUserName"><spring:message code="user.mgmt.user.name" /><br /></label>
            <form:input path="username" id="username" />
            <form:errors path="username" cssClass="error" />

            <label id="txtUserGuid"><spring:message code="user.mgmt.user.guid" /><br /></label>
            <form:input path="guid" id="guid" />
            <form:errors path="guid" cssClass="error" />

            <label id="txtEmailAddress"><spring:message code="user.mgmt.user.email" /><br /></label>
            <form:input path="emailAddr" id="emailAddress" />
            <form:errors path="emailAddr" cssClass="error" />

            <label id="txtDisplayName"><spring:message code="user.mgmt.display.name" /><br /></label>
            <form:input path="displayName" id="displayName" />
            <form:errors path="displayName" cssClass="error" />

            <br /><br />
            <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
            <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
        </form:form>

        <c:if test="${not empty fn:trim(requestScope.searchResults)}">
            <h1><spring:message code="theme.search.results" /></h1>
            <table id="userSearchResults">
                <tr>
                    <td><spring:message code="user.mgmt.user.name" /></td>
                    <td><spring:message code="user.mgmt.display.name" /></td>
                </tr>
                <c:forEach var="userResult" items="${requestScope.searchResults}">
                    <tr>
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/user-management/view/account/${userResult.guid}"
                                title="${userResult.username}">${userResult.username}</a>
                        </td>
                        <td>${userResult.displayName}</td>
                    </tr>
                </c:forEach>
            </table>

            <c:if test="${pages gt 1}">
                <br />
                <hr />
                <table>
                    <tr>
                        <c:forEach begin="1" end="${pages}" var="i">
                            <c:choose>
                                <c:when test="${page eq i}">
                                    <td>${i}</td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/user-management/search/terms/${searchTerms}/type/${searchType}/page/${i}" title="{i}">${i}</a>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </tr>
                </table>
            </c:if>
        </c:if>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
