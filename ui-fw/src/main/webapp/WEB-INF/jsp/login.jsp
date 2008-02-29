<%--
 Copyright (c) 2005-2006 Intalio inc.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html

 Contributors:
 Intalio inc. - initial API and implementation
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="intalio" uri="http://www.intalio.com/tagfiles"%>

<%@ taglib prefix="intalio" uri="http://www.intalio.com/tagfiles"%>

<%--c:set var="scripts">
</c:set--%>

<fmt:setLocale value="ja" scope="session"/>
<c:set var="logoPath" value="images/logo.gif" />
<c:set var="pageTitle" value="Intalio|Workflow" />
<c:set var="footer">
	<span>&nbsp;&nbsp;<fmt:message
		key="com_intalio_bpms_workflow_pageFooter_poweredBy_label" />&nbsp;&nbsp;
	<a href="http://www.intalio.com"><span style="color: #3082A8"><fmt:message
		key="com_intalio_bpms_workflow_pageFooter_poweredBy_value" /></span></a> <fmt:message
		key="com_intalio_bpms_workflow_versionInfo">
		<c:choose>
			<c:when test="${!empty version && !empty build}">
				<fmt:param value="${version}" />
				<fmt:param value="${build}" />
			</c:when>
			<c:otherwise>
				<fmt:param value="unknown" />
				<fmt:param value="unknown}" />
			</c:otherwise>
		</c:choose>
	</fmt:message> <a href="http://bpms.intalio.com"><span style="color: #3082A8"><fmt:message
		key="com_intalio_bpms_workflow_pageFooter_featureBugRequest" /></span></a> </span>
</c:set>

<c:set var="scripts">
	<script src="script/ui-fw.js" language="javascript"
		type="text/javascript"></script>
</c:set>

<c:set var="subMenuHeader"><fmt:message key="com_intalio_bpms_workflow_login"/></c:set>

<intalio:loginBody subMenuHeader="${subMenuHeader}" logoPath="${logoPath}" scripts="${scripts}" pageTitle="${pageTitle}" footer="${footer}">
	<table width="350" border="0" cellspacing="4" cellpadding="0">
		<tr>
			<spring:bind path="login.username">
				<td valign="top"><c:choose>
					<c:when test="${status.error}">
						<label style="color: red"> <fmt:message
							key="com_intalio_bpms_workflow_login_username" /> </label>
					</c:when>
					<c:otherwise>
						<label> <fmt:message
							key="com_intalio_bpms_workflow_login_username" /> </label>
					</c:otherwise>
				</c:choose></td>
				<td><input type="text" name="username"
					value="${login.username}" class="textInput" style="width: 250px;" />
				<font color="red">${status.errorMessage}</font></td>
			</spring:bind>
		</tr>
		<tr>
			<spring:bind path="login.password">
				<td width="80" valign="top"><c:choose>
					<c:when test="${status.error}">
						<label style="color: red"> <fmt:message
							key="com_intalio_bpms_workflow_login_password" /> </label>
					</c:when>
					<c:otherwise>
						<label> <fmt:message
							key="com_intalio_bpms_workflow_login_password" /> </label>
					</c:otherwise>
				</c:choose></td>
				<td><input type="password" name="password"
					value="${login.password}" class="textInput" style="width: 250px;" />
				<font color="red">${status.errorMessage}</font></td>
			</spring:bind>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><c:choose>
				<c:when test="${login.autoLogin}">
					<input type="checkbox" name="autoLogin" value="true"
						checked="checked" />
				</c:when>
				<c:otherwise>
					<input type="checkbox" name="autoLogin" value="true" />
				</c:otherwise>
			</c:choose> <fmt:message key="com_intalio_bpms_workflow_auto_login" /></td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td><input type="submit" class="submitInput"
				value="<fmt:message key="com_intalio_bpms_workflow_login_loginBtn"/>" />
			</td>
		</tr>
		<spring:bind path="login">
			<c:forEach items="${status.errorMessages}" var="errorMessage">
				<tr>
					<td colspan="3"><font color="red"> ${errorMessage} </font></td>
				</tr>
			</c:forEach>
		</spring:bind>
	</table>

</intalio:loginBody>
