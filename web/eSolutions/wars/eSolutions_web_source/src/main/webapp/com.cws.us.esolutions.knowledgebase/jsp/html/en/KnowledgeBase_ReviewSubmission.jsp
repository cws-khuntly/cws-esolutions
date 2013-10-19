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
 * com.cws.us.esolutions.knowledgebase/jsp/html/en
 * KnowledgeBase_ReviewSubmission.jsp
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
    <form:form id="reviewArticleData" name="reviewArticleData" action="" method="post" commandName="article">
        <form:hidden path="author" value="${sessionScope.userAccount.username}" />
        <form:hidden path="authorEmail" value="${sessionScope.userAccount.emailAddr}" />

        <table id="ShowArticle">
            <tr>
                <td id="txtArticleId"><strong><em><spring:message code="kbase.view-article.article-id" /></em></strong></td>
                <td><form:input path="articleId" readonly="true" /></td>
            </tr>
            <tr>
                <td id="txtArticleTitle"><strong><em><spring:message code="kbase.view-article.article-title" /></em></strong></td>
                <td><form:input path="title" readonly="true" /></td>
            </tr>
            <tr>
                <td id="txtArticleSymptoms"><strong><em><spring:message code="kbase.view-article.article-symptoms" /></em></strong></td>
                <td><form:input path="symptoms" readonly="true" /></td>
            </tr>
            <tr>
                <td id="txtArticleCause"><strong><em><spring:message code="kbase.view-article.article-cause" /></em></strong></td>
                <td><form:input path="cause" readonly="true" /></td>
            </tr>
            <tr>
                <td id="txtArticleKeywords"><strong><em><spring:message code="kbase.create-article.article-keywords" /></em></strong></td>
                <td><form:input path="keywords" readonly="true" /></td>
            </tr>
        </table>
        <br />
        <label id="txtArticleResolution"><strong><spring:message code="kbase.view-article.article-resolution" /></strong></label>
        <br />
        <form:textarea path="resolution" cols="90" rows="10" readonly="true" />
        <br /><br />
        <table class="kbauth">
            <tr>
                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-author" /></strong></td>
                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-created" /></strong></td>
                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-modifier" /></strong></td>
                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-modified" /></strong></td>
                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-approver" /></strong></td>
                <td id="top" align="center" valign="middle"><strong><spring:message code="kbase.view-article.article-approved" /></strong></td>
            </tr>
            <tr>
                <td align="center" valign="middle"><em>${sessionScope.userAccount.username}</em></td>
                <td align="center" valign="middle"><em>${createDate}</em></td>
                <td align="center" valign="middle"><em>${modifiedBy}</em></td>
                <td align="center" valign="middle"><em>${modifiedOn}</em></td>
                <td align="center" valign="middle"><em>${reviewedBy}</em></td>
                <td align="center" valign="middle"><em>${reviewedOn}</em></td>
            </tr>
        </table>
        <br /><br />
        <table id="inputItems">
            <tr>
                <td>
                    <input type="button" name="execute" value="<spring:message code='button.execute.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event, '${pageContext.request.contextPath}');" />
                </td>
                <td>
                    <input type="button" name="cancel" value="<spring:message code='button.cancel.text' />" id="cancel" class="submit" onclick="javascript:history.go(-1);" />
                </td>
                <td>
                    <input type="button" name="reset" value="<spring:message code='button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                </td>
            </tr>
        </table>
    </form:form>
</div>
