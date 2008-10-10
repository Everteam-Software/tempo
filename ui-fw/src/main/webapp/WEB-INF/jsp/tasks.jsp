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

		<link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>
		<link href="style.css" rel="stylesheet" type="text/css"/>
		<link href="style/tabs.css" rel="stylesheet" type="text/css"/>

		<link rel="alternate" type="application/atom+xml" title="Personal Task feed" href="/feeds/atom/tasks?token=${participantToken}"/>
		<link rel="alternate" type="application/atom+xml" title="Process feed" href="/feeds/atom/processes?token=${participantToken}"/>
		
		<script src="script/jquery.js" type="text/javascript"></script>
		<script src="script/jquery-timer.js" type="text/javascript"></script>
		<script src="script/ui-fw.js" type="text/javascript"></script>
		<script src="script/jtabber.js" type="text/javascript"></script>
		<script src="script/jquery-filter.js"></script>
		<script src="script/jquery-ui-fw.js" type="text/javascript"></script>
		<script type="text/javascript">var timeout = <c:out value="${refreshTime}"/> * 1000;</script>
		
	</head>
	<body>
		<table height="100%" width="100%">
			<tr width="100%" height="10%">
				<td width="100%">
					<%@ include file="/WEB-INF/jsp/siteHeader.jsp"%>
					<div id="data" class=".hiddencontent"/>
					<div id="container">			
						<ul id="tabnav">
							<li><a href="#" title="pa"><fmt:message key="com_intalio_bpms_workflow_tab_tasks"/></a></li>
							<li><a href="#" title="notif"><fmt:message key="com_intalio_bpms_workflow_tab_notifications"/></a></li>
							<li><a href="#" title="pipa"><fmt:message key="com_intalio_bpms_workflow_tab_processes"/></a></li>
							<li><form id="filter-form">
								<span><fmt:message key="com_intalio_bpms_workflow_tab_filter"/>: </span><input class="filterbox" name="filter" id="filter" value="" maxlength="30" size="30" type="text">
								<img align="bottom" height="15" width="15" src="images/loupe.png"/>
								</form><br/>
							</li>
						</ul>
					</div>
					<div id="tasktable">
						<div class="hiddencontent" id="pa">

							<table class="tasks" id="table1">
								<thead>
									<tr id="headerTr">
										<th width="10%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_taskState"/></th>
										<th width="30%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/></th>
										<th width="20%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/></th>
										<th width="20%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_dueDate"/></th>
										<th width="5%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_priority"/></th>
										<th width="10%"><img height="15" width="15" src="images/trombone.png"/></th>
									</tr>
								</thead>
								<tbody class="line" id="pabody"/>
							</table>
						</div>

						<div class="hiddencontent" id="notif">
							<table class="tasks" id="table3">
								<thead>
									<tr id="headertr">
										<th width="60%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/></th>
										<th width="30%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/></th>
										<th width="10%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_priority"/></th>
									</tr>
								</thead>
								<tbody id="notifbody"/>
							</table>
						</div>

						<div class="hiddencontent" id="pipa">
							<table class="tasks" id="table2">
								<thead>
									<tr id="headertr">
										<th width="65%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/></th>
										<th width="35%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/></th>
									</tr>
								</thead>
								<tbody id="pipabody"/>
							</table>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<div id="taskform_bottom">&nbsp;</div>
				</td>
			</tr>
			<tr height="90%">
				<td width="100%">
					<iframe id="taskform" id="taskform" name="taskform">
						<fmt:message key="com_intalio_bpms_not_suport_frame_msg"/>
					</iframe>
				
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