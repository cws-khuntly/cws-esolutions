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
 * ServiceMgmt_ViewProject.jsp
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
<div id="sidebar">
    <h1><spring:message code="svc.mgmt.header" /></h1>
    <ul>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/modify-service/project/${project.projectGuid}" title="<spring:message code='svc.mgmt.update.service' />"><spring:message code="svc.mgmt.update.service" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-datacenters" title="<spring:message code='svc.mgmt.list.datacenters' />"><spring:message code="svc.mgmt.list.datacenters" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/add-datacenter" title="<spring:message code='svc.mgmt.add.datacenter' />"><spring:message code="svc.mgmt.add.datacenter" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-projects" title="<spring:message code='svc.mgmt.list.projects' />"><spring:message code="svc.mgmt.list.projects" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/add-project" title="<spring:message code='svc.mgmt.add.project' />"><spring:message code="svc.mgmt.add.project" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/list-platforms" title="<spring:message code='svc.mgmt.list.platforms' />"><spring:message code="svc.mgmt.list.platforms" /></a></li>
        <li><a href="${pageContext.request.contextPath}/ui/service-management/add-platform" title="<spring:message code='svc.mgmt.add.platform' />"><spring:message code="svc.mgmt.add.platform" /></a></li>
    </ul>
</div>

<div id="main">
    <h1><spring:message code="svc.mgmt.view.project" arguments="${project.projectCode}" /></h1>

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
        <table id="projectDetail">
            <tr>
                <td><label id="txtProjectCode"><spring:message code="svc.mgmt.service.name" /></label></td>
                <td>${project.projectCode}</td>
            </tr>
            <tr>
                <td><label id="txtProjectStatus"><spring:message code="svc.mgmt.service.status" /></label></td>
                <td>${project.projectStatus}</td>
            </tr>
            <tr>
                <td><label id="txtPrimaryContact"><spring:message code="svc.mgmt.project.pcontact" /></label></td>
                <td>
                    <a href="${pageContext.request.contextPath}/ui/user-management/view/account/${project.primaryContact.guid}"
                        title="${project.primaryContact.username}">${project.primaryContact.username}</a>
                </td>
            </tr>
            <c:if test="${not empty project.secondaryContact}">
                <tr>
                    <td><label id="txtSecondaryContact"><spring:message code="svc.mgmt.project.scontact" /></label></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/ui/user-management/view/account/${project.secondaryContact.guid}"
                            title="${project.secondaryContact.username}">${project.secondaryContact.username}</a>
                    </td>
                </tr>
            </c:if>
            <tr>
                <td><label id="txtContactEmail"><spring:message code="svc.mgmt.project.dev.group" /></label></td>
                <td><a href="mailto:${project.devEmail}?Subject=Request for Comments: ${project.projectName}">${project.devEmail}</a></td>
                <td><label id="txtContactEmail"><spring:message code="svc.mgmt.project.prod.group" /></label></td>
                <td><a href="mailto:${project.prodEmail}?Subject=Request for Comments: ${project.projectName}">${project.prodEmail}</a></td>
            </tr>
            <tr>
                <td><label id="txtChangeQueue"><spring:message code="svc.mgmt.project.changeq" /></label></td>
                <td>${project.changeQueue}</td>
                <td><label id="txtIncidentQueue"><spring:message code="svc.mgmt.project.ticketq" /></label></td>
                <td>${project.incidentQueue}</td>
            </tr>
            <tr>
                <td><label id="txtApplications"><spring:message code="svc.mgmt.project.applications" /></label></td>
            </tr>
            <table>
                <tr>
                    <td><spring:message code="app.mgmt.application.name" /></td>
                    <td><spring:message code="app.mgmt.application.version" /></td>
                </tr>
                <c:forEach var="application" items="${project.applicationList}">
                    <tr>
                        <td>
                            <a href="${pageContext.request.contextPath}/ui/application-management/application/${application.applicationGuid}"
                                title="${application.applicationName}">${application.applicationName}</a>
                        </td>
                        <td>${application.applicationVersion}</td>
                    </tr>
                </c:forEach>
            </table>
        </table>
    </p>
</div>

<div id="rightbar">&nbsp;</div>
