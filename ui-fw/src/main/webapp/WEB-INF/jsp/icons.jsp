<%@ page import="org.intalio.tempo.web.ApplicationState" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% 
ApplicationState state = (ApplicationState)request.getSession().getAttribute(ApplicationState.PARAMETER_NAME);
String mytoken = state.getCurrentUser().getToken();
%>
<html>
	<td>
	<c:choose>  
		<c:when test="${fn:contains(currentUser, 'admin')}">  
		<img  alt="Curent user" title="Curent user" src="images/user_green.png"/>	
	</c:when>  
	<c:otherwise>  
		<img  alt="Curent user" title="Curent user" src="images/user_suit.png"/>	
		</c:otherwise>  
	</c:choose> 
	<a style="font-family: verdana;font-size: 12px;">${currentUser}&nbsp;&nbsp;</a>	
</td>
<td>
	<a href="/ui-fw/ical" title="iCalendar Export"><img border="0" src="/ui-fw/images/ical1.jpg"/></a>
	<a>&nbsp;</a><a>&nbsp;</a>
	<a href="/ui-fw/atom/tasks?token=<%=mytoken%>" title="<fmt:message key="org_intalio_uifw_siteHeader_tooltip_feed_tasks"/>">
	<img border="0" src="/ui-fw/images/tick.png"/></a><a>&nbsp;</a><a>&nbsp;</a>
	<a href="/ui-fw/atom/processes?token=<%=mytoken%>" title="<fmt:message key="org_intalio_uifw_siteHeader_tooltip_feed_processes"/>">
	<img border="0" src="/ui-fw/images/rss.orange.png"/></a><a>&nbsp;</a><a>&nbsp;</a><a>&nbsp;</a>
	<img src="images/house.png" title="Dashboard" onclick="window.location.href = '../main.jsp';" />&nbsp;&nbsp;
	<img class="imagepopupcontext" src="images/information.png" alt="version"  /><td>&nbsp;</td></td>&nbsp;&nbsp;
</td>
</html>
	
