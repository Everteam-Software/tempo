<%@ page import="org.intalio.tempo.web.ApplicationState" %>
<% 
ApplicationState state = (ApplicationState)request.getSession().getAttribute(ApplicationState.PARAMETER_NAME);
String mytoken = state.getCurrentUser().getToken();
%>
<td>
	<img src="images/user_suit.png" title="Curent user" alt="Curent user">	
	<a style="font-family: verdana;font-size: 12px;">${currentUser}&nbsp;&nbsp;</a>	
</td>
<td>
	<a href="/ui-fw/ical" title="iCalendar Export"><img border="0" src="/ui-fw/images/ical1.jpg"/></a>
	<a>&nbsp;</a><a>&nbsp;</a>
	<a href="/ui-fw/atom/tasks?token=<%=mytoken%>" title="<fmt:message key="org_intalio_uifw_siteHeader_tooltip_feed_tasks"/>">
	<img border="0" src="/ui-fw/images/tick.png"/></a><a>&nbsp;</a><a>&nbsp;</a>
	<a href="/ui-fw/atom/processes?token=<%=mytoken%>" title="<fmt:message key="org_intalio_uifw_siteHeader_tooltip_feed_processes"/>">
	<img border="0" src="/ui-fw/images/rss.orange.png"/></a><a>&nbsp;</a><a>&nbsp;</a><a>&nbsp;</a>
	<img src="images/house.png" title="Dashboard" onclick="window.location.href = '../main.jsp';" /></td>&nbsp;&nbsp;&nbsp;
</td>
