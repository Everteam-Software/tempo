<%--
	Copyright (c) 2005-2008 Intalio inc.

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html

	Contributors:
	Intalio inc. - initial API and implementation
--%>
<?xml version="1.0" encoding="UTF-8"?>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<html>
	<head>
		<title>
			<fmt:message key="com_intalio_bpms_workflow_pageTitle"/>
		</title>
		
		<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon"/>
		<link rel="stylesheet" type="text/css" href="style.css" />
		<link rel="stylesheet" type="text/css" href="style/tabs.css"/>
		<link rel="stylesheet" type="text/css" href="style/flexigrid.css">
		
		<link rel="alternate" type="application/atom+xml" title="Personal Task feed" href="/feeds/atom/tasks?token=${participantToken}"/>
		<link rel="alternate" type="application/atom+xml" title="Process feed" href="/feeds/atom/processes?token=${participantToken}"/>

		<script type="text/javascript" src="script/ui-fw.js"></script>
		<script type="text/javascript" src="script/jquery.js"></script>
		<script type="text/javascript" src="script/jtabber.js"></script>
		<script type="text/javascript" src="script/jquery-timer.js"></script>
		<script type="text/javascript" src="script/flexigrid.js"></script>
		<script type="text/javascript" src="script/jquery.jcorners.js"></script>
		<script type="text/javascript" src="script/jquery.demensions.js"></script>
	
		<%@ include file="/script/grids.jsp"%>
	</head>
	<body>
		<table height="100%" width="100%">
			<tr width="100%" height="10%">
				<td width="100%">
					<%@ include file="/WEB-INF/jsp/siteHeader.jsp"%>
					<div id="container">			
						<ul id="tabnav">
							<li><a href="#" title="pa"><fmt:message key="com_intalio_bpms_workflow_tab_tasks"/></a></li>
							<li><a href="#" title="notif"><fmt:message key="com_intalio_bpms_workflow_tab_notifications"/></a></li>
							<li><a href="#" title="pipa"><fmt:message key="com_intalio_bpms_workflow_tab_processes"/></a></li>
							<li><div id="filterdiv" class="filtertext">Quick filtering on showing items &nbsp;<input type="text" id="filter"/><input id="filterbutt" type="button" value="Filter"/></div></li>
							<li class="intro"><img src="images/bouncing.gif"/></li>
						</ul>
					</div>
				</td>
			<tr>
				<td width="100%">
						<div class="hiddencontent" id="pa"><table id="table1" style="display:none"></table></div>
						<div class="hiddencontent" id="notif"><table id="table2" style="display:none"></table></div>
						<div class="hiddencontent" id="pipa"><table id="table3" style="display:none"></table></div>
				</td>
			</tr>
			<tr height="95%">
				<td width="100%">
					<div class="intro" id="introhelp">
						<div id="intro">
						<%@ include file="/customize/intro.jsp"%>	
						</div>
					</div>
					<iframe name="taskform" height="auto" width="auto" FRAMEBORDER="0" id="taskform" SCROLLING="auto"></iframe>
				</td>
			</tr>
			<tr>
				<td>
					<div id="taskform_bottom">&nbsp;</div>
				</td>
			</tr>
			<tr>
				<td>
					<div id="footer">&nbsp;&nbsp;<fmt:message key="com_intalio_bpms_workflow_pageFooter_poweredBy_label" />&nbsp;&nbsp;
					<a href="http://www.intalio.com"><span style="color: #3082A8"><fmt:message key="com_intalio_bpms_workflow_pageFooter_poweredBy_value" /></span></a>
					<fmt:message key="com_intalio_bpms_workflow_versionInfo">
						<c:choose>
							<c:when test="${!empty version && !empty build}" >
								<fmt:param value="${version}"/>
								<fmt:param value="${build}"/>
							</c:when> 
							<c:otherwise>
								<fmt:param value="unknown"/>
								<fmt:param value="unknown"/>
							</c:otherwise>
						</c:choose>
					</fmt:message>
					<a href="http://bpms.intalio.com"><span style="color: #3082A8"><fmt:message key="com_intalio_bpms_workflow_pageFooter_featureBugRequest"/></span></a>
				</td>
			</tr>
		</table>
	</body>
</html>