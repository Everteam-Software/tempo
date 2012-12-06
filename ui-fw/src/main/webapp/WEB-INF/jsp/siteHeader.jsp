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
	
	<img src="images/logo.png" alt="Intalio Inc." class="io-header-logo">&nbsp;      
	
	<div id="div1" class="io-footer" style="float:right"></div>
	<div id="div2" class="io-footer-options" style="float:right;"></div>
	<div style="margin-top:13px;float:right;margin-right:8px;">
		<button id="options" style="width:50px;">Options</button>                        
		<button id="userProfile">${currentUser}</button>
	</div>
	 <div class="tooltipContent" style="float:center;display:none;height:80px;width:85px;margin-left:8px;margin-top:5px;">
               <button id="btnHelp" style="width:80px">
                   <fmt:message key="com_intalio_bpms_workflow_pageHeader_help"/>
               </button>
               <br/><br/>
               <button id="btnLogout" style="width:80px">
                     <fmt:message key="com_intalio_bpms_workflow_pageHeader_logout"/>
               </button>
        </div>
	<div class="optionContent" style="float:center;display:none;margin-left:-10px;margin-right:-10px;margin-top:-2px;margin-bottom:-2px;"> 
	      <table id="optionTable" cellpadding="5">
		    <tr class="headerlink" onClick="window.location.href = '/ui-fw/ical'"><td>
		      &nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="com_intalio_bpms_workflow_pageHeader_ical_export"/>&nbsp;&nbsp;&nbsp;&nbsp;
		    </td></tr>
		    <tr class="headerlink" onClick="window.location.href = '/ui-fw/atom/tasks?token=<%=mytoken%>'"><td>
		      &nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="org_intalio_uifw_siteHeader_tooltip_feed_tasks"/>&nbsp;&nbsp;&nbsp;&nbsp;
		    </td></tr>
		    <tr class="headerlink" onClick="window.location.href = '/ui-fw/atom/processes?token=<%=mytoken%>'"><td>
		      &nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="org_intalio_uifw_siteHeader_tooltip_feed_processes"/>&nbsp;&nbsp;&nbsp;&nbsp;
		    </td></tr>
		    <tr class="headerlink" onClick="window.location.href = '../main.jsp'"><td>
		      &nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="com_intalio_bpms_workflow_pageHeader_home"/>&nbsp;&nbsp;&nbsp;&nbsp;
		    </td></tr>
	    </table>
	</div>
	<div class="siteHeaderIcons">
	<span id="timer"></span>
	</div>
	
</form>

