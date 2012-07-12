<%--
 Copyright (c) 2005-2008 Intalio inc.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html

 Contributors:
 Intalio inc. - initial API and implementation
--%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="intalio" uri="http://www.intalio.com/tagfiles"%>

<%@ taglib prefix="intalio" uri="http://www.intalio.com/tagfiles"%>

<c:set var="logoPath" value="images/logo.png" />
<c:set var="bgimage" value="images/newbg.gif" />
<c:set var="pageTitle" value="Intalio|Workflow" />
<c:set var="footer">
	<span >&nbsp;&nbsp;<fmt:message
		key="com_intalio_bpms_workflow_pageFooter_poweredBy_label" />&nbsp;&nbsp;
	<a href="http://www.intalio.com"><span class="io-login-footer-link"><fmt:message
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
	</fmt:message> <a  href="mailto:support-team@intalio.com?subject=Bug/Feature Request"><span class="io-login-footer-link"><fmt:message
		key="com_intalio_bpms_workflow_pageFooter_featureBugRequest" /></span></a> </span>
</c:set>

<c:set var="scripts">
	<script src="style/ui-fw.js" language="javascript" type="text/javascript"></script>
	<link rel="stylesheet" href="style/login.css" type="text/css">
</c:set>

<intalio:loginBody scripts="${scripts}" pageTitle="${pageTitle}" footer="${footer}">
	
	<div id="header" class="io-login-header" >
		<img src="${logoPath}" class="io-login-header-image" alt="Intalio Inc" />
	</div>
	<div id="loginBox" class="io-login-loginBox">
	<table>
		<tr>
			<spring:bind path="login.username">
				<td style=" position:absolute; left:20px; top:30px; "><c:choose>
					<c:when test="${status.error}">
						<label style="font-size: 12px;"> <fmt:message
							key="com_intalio_bpms_workflow_login_username" /> </label>
					</c:when>
					<c:otherwise>
						<label style="font-size: 12px;"> 
							<fmt:message key="com_intalio_bpms_workflow_login_username" /> </label>
					</c:otherwise>
				</c:choose></td>
				<td class="io-login-loginBox-userName"><input type="text" size=16 name="username"
					value="${login.username}" class="textInput"  style="font-size: 16px; height:25px; width:200px; tabindex=1;" />
				<font color="red">${status.errorMessage}</font></td>
			</spring:bind>
			
			<spring:bind path="login.password">
				<td style=" position:absolute; left:230px; top:30px;"><c:choose>
					<c:when test="${status.error}">
						<label style="font-size: 12px;"> <fmt:message
							key="com_intalio_bpms_workflow_login_password" /> </label>
					</c:when>
					<c:otherwise>
						<label style="font-size: 12px;"> <fmt:message
							key="com_intalio_bpms_workflow_login_password" /> </label>
					</c:otherwise>
				</c:choose></td>
				<td class="io-login-loginBox-password"><input type="password" name="password"
					value="${login.password}" class="textInput" style="font-size: 16px; width: 200px; height: 25px; tabindex=2;" />
				<font color="red">${status.errorMessage}</font></td>
			</spring:bind>
			<td>&nbsp;</td>
			<td class="io-login-loginBox-submit"><input type="submit" 
                                class="submitInput" style="width:80px; height:25px;"
				value="<fmt:message key="com_intalio_bpms_workflow_login_loginBtn"/>" />
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td class="io-login-loginBox-autoLoginchk" style="left:20px; top:95px;">
			<c:choose>
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
			
		</tr>
		<spring:bind path="login">
			<c:forEach items="${status.errorMessages}" var="errorMessage">
				<tr>
					<td style=" position:absolute; left:20px; top:2px; " colspan="3"><font color="red"> ${errorMessage} </font></td>
				</tr>
			</c:forEach>
		</spring:bind>
	</table>
	</div> </div>
</intalio:loginBody>

