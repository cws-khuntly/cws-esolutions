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
 * Package: com.cws.esolutions.web.useraccount\jsp\html\en
 * File: UserAccount_ChangeSecurity.jsp
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
        if (theForm.secQuestionOne.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A security question must be selected.';
            document.getElementById('txtQuestionOne').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('secQuestionOne').focus();
        }
        else if (theForm.secQuestionTwo.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A security question must be selected.';
            document.getElementById('txtQuestionTwo').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('secQuestionOne').focus();
        }
        if (theForm.secAnswerOne.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A security answer must be selected.';
            document.getElementById('txtAnswerOne').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('secQuestionOne').focus();
        }
        else if (theForm.secAnswerTwo.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'A security answer must be selected.';
            document.getElementById('txtAnswerTwo').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('secQuestionOne').focus();
        }
        else if (theForm.currentPassword.value == '')
        {
            clearText(theForm);

            document.getElementById('validationError').innerHTML = 'Your current password must be provided.';
            document.getElementById('txtPassword').style.color = '#FF0000';
            document.getElementById('execute').disabled = false;
            document.getElementById('secQuestionOne').focus();
        }
        else
        {
            if (theForm.secQuestionOne.value == theForm.secQuestionTwo.value) // make sure the questions arent the same
            {
                clearText(theForm);

                document.getElementById('validationError').innerHTML = 'The security questions must be different.';
                document.getElementById('txtQuestionOne').style.color = '#FF0000';
                document.getElementById('execute').disabled = false;
                document.getElementById('secQuestionOne').focus();
            }
            else if (theForm.secAnswerOne.value == theForm.secAnswerTwo.value) // make sure the answers arent the same
            {
                clearText(theForm);

                document.getElementById('validationError').innerHTML = 'The security answers must be different.';
                document.getElementById('txtQuestionOne').style.color = '#FF0000';
                document.getElementById('execute').disabled = false;
                document.getElementById('secQuestionOne').focus();
            }
            else if (theForm.secQuestionOne.value == theForm.secAnswerOne.value) // make sure question 1 doesnt match answer 1
            {
                clearText(theForm);

                document.getElementById('validationError').innerHTML = 'A security question must be selected.';
                document.getElementById('txtQuestionTwo').style.color = '#FF0000';
                document.getElementById('execute').disabled = false;
                document.getElementById('secQuestionOne').focus();
            }
            else if (theForm.secQuestionTwo.value == theForm.secAnswerTwo.value) // make sure question 2 doesnt match answer 2
            {
                clearText(theForm);

                document.getElementById('validationError').innerHTML = 'A security question must be selected.';
                document.getElementById('txtQuestionTwo').style.color = '#FF0000';
                document.getElementById('execute').disabled = false;
                document.getElementById('secQuestionOne').focus();
            }
            else if (theForm.secQuestionOne.value == theForm.secAnswerTwo.value) // make sure question 1 doesnt match answer 2
            {
                clearText(theForm);

                document.getElementById('validationError').innerHTML = 'A security question must be selected.';
                document.getElementById('txtQuestionTwo').style.color = '#FF0000';
                document.getElementById('execute').disabled = false;
                document.getElementById('secQuestionOne').focus();
            }
            else if (theForm.secQuestionTwo.value == theForm.secAnswerOne.value)
            {
                clearText(theForm);

                document.getElementById('validationError').innerHTML = 'A security question must be selected.';
                document.getElementById('txtQuestionTwo').style.color = '#FF0000';
                document.getElementById('execute').disabled = false;
                document.getElementById('secQuestionOne').focus();
            }
            else
            {
                theForm.submit();
            }
        }
    }
//-->
</script>

<div id="sidebar">
    <h1><spring:message code="user.account.update.security" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/email" title="<spring:message code='user.account.change.email' />"><spring:message code="user.account.change.email" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/contact" title="<spring:message code='user.account.change.contact' />"><spring:message code="user.account.change.contact" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/password" title="<spring:message code='user.account.change.password' />"><spring:message code="user.account.change.password" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/user-account/regenerate-keys" title="<spring:message code='user.account.change.keys' />"><spring:message code="user.account.change.keys" /></a></li>
    </ul>
</div>

<div id="main">
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

    <h1><spring:message code="user.account.update.security" /></h1>
    <p>
        <form:form name="submitSecurityInformationChange" id="submitSecurityInformationChange" action="${pageContext.request.contextPath}/ui/user-account/security" method="post" autocomplete="off">
            <p>
                <label id="txtQuestionOne"><spring:message code="user.account.update.security.question" /></label>
                <form:select path="secQuestionOne">
                    <option><spring:message code="theme.option.select" /></option>
                    <option><spring:message code="theme.option.spacer" /></option>
                    <form:options items="${questionList}" />
                </form:select>
                <form:errors path="secQuestionOne" cssClass="error" />

                <label id="txtAnswerOne"><spring:message code="user.account.update.security.answer" /></label>
                <form:password path="secAnswerOne" />
                <form:errors path="secAnswerOne" cssClass="error" />

                <label id="txtQuestionTwo"><spring:message code="user.account.update.security.question" /></label>
                <form:select path="secQuestionTwo">
                    <option><spring:message code="theme.option.select" /></option>
                    <option><spring:message code="theme.option.spacer" /></option>
                    <form:options items="${questionList}" />
                </form:select>
                <form:errors path="secQuestionTwo" cssClass="error" />            

                <label id="txtAnswerTwo"><spring:message code="user.account.update.security.answer" /></label>
                <form:password path="secAnswerTwo" />
                <form:errors path="secAnswerTwo" cssClass="error" />

                <label id="txtPassword"><spring:message code="user.account.update.password.current" /></label>
                <form:password path="currentPassword" />
                <form:errors path="currentPassword" cssClass="error" />

                <br /><br />
                <input type="button" name="execute" value="<spring:message code='theme.button.submit.text' />" id="execute" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
                <input type="button" name="reset" value="<spring:message code='theme.button.reset.text' />" id="reset" class="submit" onclick="clearForm();" />
                <input type="button" name="cancel" value="<spring:message code='theme.button.cancel.text' />" id="cancel" class="submit" onclick="disableButton(this); validateForm(this.form, event);" />
            </p>
        </form:form>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
