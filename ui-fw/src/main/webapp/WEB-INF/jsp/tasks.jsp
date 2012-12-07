<%--
	Copyright (c) 2005-2009 Intalio inc.

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

    <link rel="shortcut icon" href="favicon.ico" type="image/x-icon"/>

    <link rel="stylesheet" type="text/css" href="style.css" />
    <link rel="stylesheet" type="text/css" href="style/tabs.css"/>
    <link rel="stylesheet" type="text/css" href="style/flexigrid.css"/>
    <link class="include" rel="stylesheet" href="style/jqueryui/jquery-ui.css"/>
    <link class="include" rel="stylesheet" href="style/jqueryui/jquery.ui.theme.css"/>
    <link class="include" rel="stylesheet" href="style/jqueryui/jquery.qtip.css">
    <link rel="stylesheet" type="text/css" href="style/jqueryui/jquery.alerts.css"/>
    <link class="include" rel="stylesheet" href="style/jqueryui/ui.dialog.content.css">
    <link rel="alternate" type="application/atom+xml" title="Personal Task feed" href="/feeds/atom/tasks?token=${participantToken}"/>
    <link rel="alternate" type="application/atom+xml" title="Process feed" href="/feeds/atom/processes?token=${participantToken}"/>

    <script type="text/javascript" src="script/ui-fw.js"></script>
    <script type="text/javascript" src="script/jquery.js"></script>
    <!--<script type="text/javascript" src="script/jquery.cookie.js"></script>-->
    <script type="text/javascript" src="script/jquery-ui.js"></script>
    <script type="text/javascript" src="script/jquery-timer.js"></script>
    <script type="text/javascript" src="script/jquery.jcorners.js"></script>
    <script type="text/javascript" src="script/jquery.demensions.js"></script>
    <script type="text/javascript" src="script/jquery.ui.position.js"></script>
    <script type="text/javascript" src="script/jtabber.js"></script>
    <script type="text/javascript" src="script/jquery.qtip.js"></script>
    <script type="text/javascript" src="script/jquery.string.1.0.js"></script>
    <script type="text/javascript" src="script/jqSoapClient.min.js"></script>
    <script type="text/javascript" src="script/ui/jquery.ui.datepicker.js"></script>
    
    
    <script type="text/javascript" src="script/flexigrid.js"></script>
    <script type="text/javascript" src="script/jquery.alerts.js"></script>
    
    <script type="text/javascript">var one_task_page = true /*Flag to safeguard changes */</script>
    <%@ include file="/script/grids.jsp"%>

  </head>
  <body width="95%" height="98%">
    <%@ include file="/WEB-INF/jsp/siteHeader.jsp"%>
    <div id="container">                        
      <ul id="tabnav">
        <li >
          <a href="#" id="tabTasks" tabtitle="com_intalio_bpms_workflow_tab_tasks" style="width:80px;height:22px;" title="<fmt:message key="com_intalio_bpms_workflow_tab_tasks"/>">
            <fmt:message key="com_intalio_bpms_workflow_tab_tasks"/>
          </a>
        </li>
        <li>
          <a href="#" id="tabNotif" tabtitle="com_intalio_bpms_workflow_tab_notifications" style="width:80px;height:22px;" title="<fmt:message key="com_intalio_bpms_workflow_tab_notifications"/>">
            <fmt:message key="com_intalio_bpms_workflow_tab_notifications"/>
          </a>
        </li>
        <li>
          <a href="#" id="tabPipa" tabtitle="com_intalio_bpms_workflow_tab_processes" style="width:80px;height:22px;" title="<fmt:message key="com_intalio_bpms_workflow_tab_processes"/>">
            <fmt:message key="com_intalio_bpms_workflow_tab_processes"/>
          </a>
        </li>
      </ul>

      <div id ="tasktables">
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
      
    </div>
 <!-- Overlay window for PIPA
   <div id="overlaypipa">
	<div id="taskData"></div>
  </div>-->   


    <div id="connectionLost" title="<fmt:message key="org_intalio_uifw_session_connection_lost"/>">
    </div>
	<div id="deleteDialog">
	</div>
    <div id="updateDialog" class="hiddencontent" title="<fmt:message key="org_intalio_uifw_update_title"/>">
      <form>
        <fieldset>
           <label class="ui-update-descriptionlabel" for="description"><fmt:message key="org_intalio_uifw_update_description"/></label>
           <input type="text" name="description" title="<fmt:message key="org_intalio_uifw_update_description"/>" id="up_description" class="ui-update-descriptioninput" />
           <label class="ui-update-prioritylabel" for="priority"><fmt:message key="org_intalio_uifw_update_priority"/></label>
	   <input type="text" name="priority" title="<fmt:message key="org_intalio_uifw_update_priority"/>" id="up_priority" class="ui-update-priorityinput" />
        </fieldset>
      </form>
    </div>
    
    <div id="exportdialog" title="<fmt:message key="org_intalio_uifw_dialog_export"/>">
    <form>
      <fieldset>
        
        <fmt:message key="org_intalio_uifw_dialog_export_select_format"/><br/>
        
        <input type="radio" value="csv" name="eformat" class="ui-corner-all"><img src="images/icons/export/csv.png" title="csv">
        <input type="radio" value="ical" name="eformat" class="ui-corner-all"><img src="images/icons/export/ical.png" title="ical">
        <input type="radio" value="pdf" name="eformat" class="ui-corner-all" checked><img src="images/icons/export/pdf.png" title="pdf">
        <hr width="100%" align="center">
        
        <fmt:message key="org_intalio_uifw_dialog_export_select_content"/><br/>
        <!-- <label for="etype_pa" ><fmt:message key="com_intalio_bpms_workflow_tab_tasks"/></label> -->
        <input type="radio" value="PATask" id="etype_pa" name="etype" class="ui-corner-all" checked><img src="images/icons/icon.task.png" title="<fmt:message key="com_intalio_bpms_workflow_tab_tasks"/>">
        <!-- <label for="etype_pipa" ><fmt:message key="com_intalio_bpms_workflow_tab_processes"/></label> -->
        <input type="radio" value="PIPATask" name="etype" class="ui-corner-all"><img src="images/icons/icon.process.png" title="<fmt:message key="com_intalio_bpms_workflow_tab_processes"/>" id="etype_pipa">
        <!-- <label for="etype_pipa" ><fmt:message key="com_intalio_bpms_workflow_tab_notifications"/></label> -->
        <input type="radio" value="Notification" name="etype" class="ui-corner-all"><img src="images/icons/icon.notification.png" title="<fmt:message key="com_intalio_bpms_workflow_tab_notifications"/>">
        <!-- <label for="equery"><fmt:message key="org_intalio_uifw_dialog_export_query"/></label>
        <input disabled type="text" name="equery" id="export_query" value="" class="text ui-widget-content ui-corner-all" /><br/> -->
      </fieldset>
    </form>
    </div>

    <div id="reassignDialog" title="<fmt:message key="org_intalio_uifw_reassign_title"/>">
	<form>
        <fieldset>
          <label for="user" class="ui-reassign-userlabel"><fmt:message key="org_intalio_uifw_reassign_user"/></label>
          <input type="text" class="ui-reassign-userinput" name="user" id="reassign_user"/><br/>
          <label for="roles" class="ui-reassign-rolelabel"><fmt:message key="org_intalio_uifw_reassign_roles"/></label>
          <input type="text" name="roles" id="reassign_roles" value="" class="ui-reassign-roleinput" /><br/>
          <label for="reassign_dyn" class="ui-reassign-fetchedRoleslabel"><fmt:message key="org_intalio_uifw_reassign_fetched_roles"/></label>
          <select name="reassign_dyn" id="reassign_dyn" class="ui-reassign-fetchedRolesInput">
          </select><br/>
          <label for="reassign_dyn_user" class="ui-reassign-fetchedUserslabel"><fmt:message key="org_intalio_uifw_reassign_fetched_users"/></label><br/>
          <select name="reassign_dyn_user" id="reassign_dyn_user" class="ui-reassign-fetchedUsersInput">
          </select>
        </fieldset>
      </form>
    </div>
    
    <div id="sessionExpired" title="<fmt:message key="org_intalio_uifw_session_expired"/>">
       <p><fmt:message key="org_intalio_uifw_session_expired"/></p>
       <p><a href="/ui-fw"><fmt:message key="org_intalio_uifw_session_login_again"/></a></p>
    </div>

	<div id="vacation" title="<fmt:message key="org_intalio_uifw_vacation_title"/>">
	<table>
		<tr>
			<td>From:</td>
			<td style="align:left"><input type="text" size="10" maxlength="10" name="fromdate" id="fromdate"></td>
		</tr>
		<tr>
			<td>To:</td>
			<td style="align:left"><input type="text" size="10" maxlength="10" name="todate" id="todate"></td>
		</tr>
		<tr>
			<td>Description:</td>
			<td style="align:left">
				<textarea style="resize: none;" rows="3" cols="25" name="desc" id="desc">
				</textarea>
			</td>
		</tr>
	</table>
	</div>
	<div id="messageDialog" title="Message">
	</div>
    <div id="endVacDialog" title="Message">
	</div>
    <div id="warnDialog" title="Message">
	</div>

    
    <iframe src="script/empty.jsp" onLoad="resizeIframe" name="taskform" frameborder="0" id="taskform" scrolling="auto"></iframe>

      <div id="versionInfo" class="siteFooter">
	<span>&nbsp;&nbsp;<fmt:message key="com_intalio_bpms_workflow_pageFooter_poweredBy_label" />&nbsp;&nbsp;
	<a ><span style="color: #000000"><fmt:message key="com_intalio_bpms_workflow_pageFooter_poweredBy_value" /></span></a>
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
	    </span>
       </div>

  <script>
    document.getElementById('taskform').onload = resizeIframe;
    window.onresize = resizeIframe;
  </script>

  </body>
</html>
