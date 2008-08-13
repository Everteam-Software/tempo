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
		
		<script type="text/javascript">
			$(document).ready(function(){ 
			
 			function image_notification( image ) {
				$("#message").append("<img src='"+image+"' height='20' widht='20'/>");
				$.timer(10000, function(timer){
				   $("#message").html("");
				   timer.stop();
				});
			}

			updateTable = function(tbody, data, data1, icons) {
			    var newdata = $('<div/>').html(data).find(data1).html();
			    if(icons) {
				   if($(tbody).html().length < newdata.length) {
			       if(tbody == "#pabody") {image_notification('images/task.png');}
				   if(tbody == "#notifbody") {image_notification('http://www.neille.com/test/images/notification-icon.gif');}
				}
	     		} 
				if($(tbody).html().length != newdata.length) {
					$(tbody).html(newdata);
				}
			}
			
			function clearFrame() {
				window.open("about:blank", "taskform");
			}
			
			function getTasks( icons ) {
			$.ajax({
			    url: 'updates.htm?update=true',
			    type: 'POST',
			    timeout: 5000,
			    error: function(xml){
			        image_notification('http://www.clker.com/cliparts/7/d/b/0/11954453151817762013molumen_red_square_error_warning_icon.svg.med.png');
			    },
			    success: function(data){
				    updateTable("#pabody", data, "#padata",icons);
				    updateTable("#notifbody", data, "#notifdata", icons);
				    updateTable("#pipabody", data, "#pipadata", icons);
			    }
			});
			
			};
			
			$('#tabnav li a').click(function(){
				clearFrame();
			});
			

			$.jtabber({
			mainLinkTag: "#container li a", 
			activeLinkClass: "active", 
			hiddenContentClass: "hiddencontent", 
			showDefaultTab: 1, 
			effect: 'slide', 
			effectSpeed: 'slow' 
			});

            getTasks(false);
			clearFrame();

            function update() {getTasks(true);}
			var timeout = <c:out value="${refreshTime}"/> * 1000;
			if(timeout < 1000) timeout = 1000;
			$.timer(timeout, update);

			});
		</script>

	</head>
	<body height="100%">

		<%@ include file="/WEB-INF/jsp/siteHeader.jsp"%>
		<span id="container">
		<ul id="tabnav">
			<li><a href="#" title="pa"><fmt:message key="com_intalio_bpms_workflow_tab_tasks"/></a></li>
			<li><a href="#" title="notif"><fmt:message key="com_intalio_bpms_workflow_tab_notifications"/></a></li>
			<li><a href="#" title="pipa"><fmt:message key="com_intalio_bpms_workflow_tab_processes"/></a></li>
		</ul>
		</span>
		
		<div style="clear:both; line-height:0; height:0">&nbsp;</div>
		
		<div id="tasktable">
			<div class="hiddencontent" id="pa">

				<table class="tasks" id="table1">
					<thead>
						<tr id="headerTr">
							<th width="10%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_taskState"/></th>
							<th width="35%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/></th>
							<th width="25%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/></th>
							<th width="20%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_dueDate"/></th>
							<th width="10%"><fmt:message key="com_intalio_bpms_workflow_taskHolder_priority"/></th>
						</tr>
					</thead>
					<tbody id="pabody">

					</tbody>
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
					<tbody id="notifbody">
					</tbody>
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
					<tbody id="pipabody"></tbody>
				</table>
			</div>
		</div>
		
		<iframe id="taskform" name="taskform">
			<fmt:message key="com_intalio_bpms_not_suport_frame_msg"/>
		</iframe>
		
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
	</body>
</html>