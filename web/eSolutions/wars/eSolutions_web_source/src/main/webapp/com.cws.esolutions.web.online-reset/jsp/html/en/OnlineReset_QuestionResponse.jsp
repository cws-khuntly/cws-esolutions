<%--
/*
 * Copyright (c) 2009 - 2020 CaspersBox Web Services
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
 *
 */
--%>
<%--
/**
 * Project: eSolutions_web_source
 * Package: com.cws.esolutions.web.onlinereset\jsp\html\en
 * File: OnlineReset_QuestionResponse.jsp
 *
 * @author cws-khuntly
 * @version 1.0
 *
 * History
 *
 * Author               Date                            Comments
 * ----------------------------------------------------------------------------
 * cws-khuntly          11/23/2008 22:39:20             Created.
 */
--%>

<script>
<!--
    function validateForm(theForm)
    {
        if (theForm.secAnswerOne.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'You must provide your account username.';
            document.getElementById('txtAnswerOne').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('secAnswerOne').focus();
        }
        else if (theForm.secAnswerTwo.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'You must provide your account username.';
            document.getElementById('txtAnswerTwo').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('secAnswerOne').focus();
        }
        else
        {
            theForm.submit();
        }
    }
//-->
</script>

<div id="sidebar">&nbsp;</div>

<div id="main">
    <h1><spring:message code="olr.forgotpwd.message" /></h1>

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
        <form:form id="submitSecurityQuestion" name="submitSecurityQuestion" action="${pageContext.request.contextPath}/ui/online-reset/submit" method="post" autocomplete="off">
            <form:hidden path="resetType" value="${resetType}" />

            <p>
                <label id="txtQuestionOne"><spring:message code="olr.question" /></label>
                ${command.secQuestionOne}
                <label id="txtAnswerOne"><spring:message code="olr.answer" /></label>
                <form:password path="secAnswerOne" />
                <form:errors path="secAnswerOne" cssClass="error" />
                <label id="txtQuestionTwo"><spring:message code="olr.question" /></label>
                ${command.secQuestionTwo}
                <label id="txtAnswerTwo"><spring:message code="olr.answer" /></label>
                <form:password path="secAnswerTwo" />
                <form:errors path="secAnswerTwo" cssClass="error" />
                <br /><br />
                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); window.location.href = '${pageContext.request.contextPath}/ui/online-reset/cancel';" />
            </p>
        </form:form>
    </p>
</div>

<div id="rightbar">
    <h1><spring:message code="login.user.forgot.info" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/app/help/forgot-questions" title="<spring:message code='olr.forgot.questions' />"><spring:message code="olr.forgot.questions" /></a></li>
    </ul>
</div>
