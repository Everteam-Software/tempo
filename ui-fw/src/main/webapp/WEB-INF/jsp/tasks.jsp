<%--
	Copyright (c) 2005-2008 Intalio inc.

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html

	Contributors:
	Intalio inc. - initial API and implementation
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="en" xml:lang="en">
	<head>
		<title>
			<fmt:message key="com_intalio_bpms_workflow_pageTitle"/>
		</title>

		<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon"/>
		<link rel="stylesheet" type="text/css" href="style.css" />
		<link rel="stylesheet" type="text/css" href="style/tabs.css"/>
		<link rel="stylesheet" type="text/css" href="style/flexigrid.css"/>
		<link rel="stylesheet" type="text/css" href="style/modal.css"/>

		<link rel="alternate" type="application/atom+xml" title="Personal Task feed" href="/feeds/atom/tasks?token=${participantToken}"/>
		<link rel="alternate" type="application/atom+xml" title="Process feed" href="/feeds/atom/processes?token=${participantToken}"/>

		<script type="text/javascript" src="script/ui-fw.js"></script>
		<script type="text/javascript" src="script/jquery.js"></script>
		<script type="text/javascript" src="script/jtabber.js"></script>
		<script type="text/javascript" src="script/jquery-timer.js"></script>
		<script type="text/javascript" src="script/flexigrid.js"></script>
		<script type="text/javascript" src="script/jquery.jcorners.js"></script>
		<script type="text/javascript" src="script/jquery.demensions.js"></script>
		<script type="text/javascript" src="script/jquery.string.1.0.js"></script>
		<script src="script/jquery.smartmodal.js" type="text/javascript" charset="utf-8"></script>
		<script type="text/javascript" src="script/soapclient.js"></script>

		<%@ include file="/script/grids.jsp"%>

		<script type="text/javascript">
		function resizeIframe() {
		    var height = document.documentElement.clientHeight;
		    height -= document.getElementById('taskform').offsetTop;

		    // not sure how to get this dynamically
		    height -= 20; /* whatever you set your body bottom margin/padding to be */

		    document.getElementById('taskform').style.height = height +"px";

		};
		window.onresize = resizeIframe;
		
		</script>
		
		
		</head>
		<body width="95%" height="98%">
		    
		    
			<%@ include file="/WEB-INF/jsp/siteHeader.jsp"%>
						<div id="container">			
							<ul id="tabnav">
								<li><a href="#" id="tabTasks" title="com_intalio_bpms_workflow_tab_tasks"><fmt:message key="com_intalio_bpms_workflow_tab_tasks"/></a></li>
								<li><a href="#" id="tabNotif" title="com_intalio_bpms_workflow_tab_notifications"><fmt:message key="com_intalio_bpms_workflow_tab_notifications"/></a></li>
								<li><a href="#" id="tabPipa" title="com_intalio_bpms_workflow_tab_processes"><fmt:message key="com_intalio_bpms_workflow_tab_processes"/></a></li>
								<li>
									<div id="filterdiv" class="filtertext">
										&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
										<fmt:message key="org_intalio_uifw_tasks_filtertext"/> 
										&nbsp;&nbsp;
									<input type="text" id="filter"/>
									    &nbsp;&nbsp;
									<input title="<fmt:message key="org_intalio_uifw_tasks_filtertext"/>" id="filterbutt" type="button" value="<fmt:message key="org_intalio_uifw_tasks_filterbutton"/>"/>
								</div>
							</li>
							<li class="intro" style="position: relative;top:0px"><img height="20px" width="20px" src="images/bouncing.gif"/></li>
						</ul>
					</div>

					<div style="clear: both ; float:left; position:relative; top:0px">
						<div class="hiddencontent" id="com_intalio_bpms_workflow_tab_tasks">
						<table id="table1" style="display:none"></table>
						</div>
						<div class="hiddencontent" id="com_intalio_bpms_workflow_tab_notifications">
						<table id="table2" style="display:none"></table>
						</div>
						<div class="hiddencontent" id="com_intalio_bpms_workflow_tab_processes">
						<table id="table3" style="display:none"></table>
						</div>
					</div>
					
					
					<div class="intro" id="introhelp">
						<div id="intro">
							<%@ include file="/customize/intro.jsp"%>	
						</div>
					</div>
					
					<iframe onLoad="resizeIframe" name="taskform" FRAMEBORDER="0" id="taskform" SCROLLING="auto"></iframe>
					
						<div id="footer">&nbsp;&nbsp;<fmt:message key="com_intalio_bpms_workflow_pageFooter_poweredBy_label" />&nbsp;&nbsp;
							<a href="http://www.intalio.com"><span style="color: #3082A8"><fmt:message key="com_intalio_bpms_workflow_pageFooter_poweredBy_value" /></span></a>
							<a href="versions">
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
							</a>&nbsp;
							<a href="http://bpms.intalio.com"><span style="color: #3082A8"><fmt:message key="com_intalio_bpms_workflow_pageFooter_featureBugRequest"/></span></a>
						</div>
						
						<a href="#" rel="rel_modal_content" id="modal" class="hiddencontent">Click me</a>
						<div id="rel_modal_content" class="hiddencontent">
							<p>Your session has expired.</p>
							<p><a href="/ui-fw">Please click here to log in again</a></p>
						    <!-- Note we can add any kind of HTML code in here for the session timeout -->
						</div>
		</body>
		<script>
            document.getElementById('taskform').onload = resizeIframe;
		</script>
	</html>