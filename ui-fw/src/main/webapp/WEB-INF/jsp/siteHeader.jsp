<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ page import="java.util.Random" %>
<% Random r = new Random(); %>

<div class="siteHeader">
  <img src="images/logo.png"  class="siteHeader-image" alt="Intalio Inc" />
  <form id="form" name="form" method="POST" border="0" cellpadding="0" cellspacing="0" >
	<input type="hidden" id="actionName" name="actionName" value=""/>
	<input type="hidden" id="formURL" name="formURL" value=""/>
	<input type="hidden" id="taskType" name="taskType" value=""/>
	<input type="hidden" id="isViewTask" name="isViewTask" value=""/>
	<input type="hidden" id="searchUser" name="searchUser" value="<%=request.getParameter("unid")%>"/>
	<input type="hidden" id="currTab" name="currTab" value=""/>
	<div class="siteHeaderIcons">
	<span id="timer"></span>
	<%@ include file="icons.jsp" %>
	<!-- hiding current user image
	<img height="20" width="20" src="images/icons/user/<%=1+r.nextInt(4)%>.png" title="<fmt:message key="org_intalio_uifw_siteHeader_currentUser"/>" alt="org_intalio_uifw_siteHeader_currentUser" border="0"></img> -->
	
	<a href="javascript:submitActionToURL('login.htm','logOut')">
		<img border="0px" alt="<fmt:message key="org_intalio_uifw_siteHeader_tooltip_logout"/>" title="<fmt:message key="org_intalio_uifw_siteHeader_tooltip_logout"/>" src="images/icons/icon.logout.gif"/>
    </a>
	</div>
	</div>
</form>
</div>
