<%@ page import="org.intalio.tempo.web.ApplicationState" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ page import="java.util.Random" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<% Random r = new Random();
ApplicationState state = (ApplicationState)request.getSession().getAttribute(ApplicationState.PARAMETER_NAME);
String mytoken = state.getCurrentUser().getToken();
%>

<form id="form" name="form" method="POST" border="0" cellpadding="0" cellspacing="0" >
	<input type="hidden" id="actionName" name="actionName" value=""/>
	<input type="hidden" id="formURL" name="formURL" value=""/>
	<input type="hidden" id="taskType" name="taskType" value=""/>
	<input type="hidden" id="isViewTask" name="isViewTask" value=""/>
	<input type="hidden" id="searchUser" name="searchUser" value="<%=request.getParameter("unid")%>"/>
	<input type="hidden" id="currTab" name="currTab" value=""/>
	
	<img src="images/logo.png" alt="Intalio Inc." class="io-header-logo" onclick="gotoDashboard();" title="Return to Home Page">&nbsp;      
	<div id="userButton"  style="margin-top:13px;float:right;margin-right:8px;">
	       <div>
		<button class="select" id="userProfile">${currentUserName}</button>
	      </div>
	      <ul id="userData">
		<li><a href="#" onclick="window.location.href = '/ui-fw/ical'" >&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="com_intalio_bpms_workflow_pageHeader_ical_export"/></a></li>
		<li><a href="#" onclick="window.location.href = '/ui-fw/atom/tasks?token=<%=mytoken%>'" >&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="org_intalio_uifw_siteHeader_tooltip_feed_tasks"/></a></li>
		<li><a href="#" onclick="window.location.href = '/ui-fw/atom/processes?token=<%=mytoken%>'">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="org_intalio_uifw_siteHeader_tooltip_feed_processes"/></a></li>
		<!--<li><a href="#" onclick="window.location.href = '../monitoring'">&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="com_intalio_bpms_workflow_pageHeader_goto_monitoring"/></a></li>-->
		<li class="divider"></li>
		<li><a href="#" onclick="window.open('http://wiki.intalio.com', '_blank');window.focus();" >&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="com_intalio_bpms_workflow_pageHeader_help"/></a></li>
		<li><a href="#" onclick="submitActionToURL('login.htm', 'logOut');" >&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="com_intalio_bpms_workflow_pageHeader_logout"/></a></li>
	      </ul>
	</div>
		
	<div class="siteHeaderIcons">
	<span id="timer"></span>
	</div>
	
</form>

