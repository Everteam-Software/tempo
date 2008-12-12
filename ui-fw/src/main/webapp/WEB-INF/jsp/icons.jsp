<%@ page import="org.intalio.tempo.web.ApplicationState" %>
<% 
ApplicationState as = (ApplicationState)request.getSession().getAttribute(ApplicationState.PARAMETER_NAME);
String token = as.getCurrentUser().getToken();
%>

<td>
	<a href="/ui-fw/ical" title="iCalendar Export"><img border="0" src="/ui-fw/images/ical.jpg"/></a>
	<a href="/ui-fw/atom/tasks?token=<%=token%>" title="Personal Task Feed"><img border="0" src="/ui-fw/images/rss.orange.png"/></a>
	<a href="/ui-fw/atom/processes?token=<%=token%>" title="Personal Process Feed"><img border="0" src="/ui-fw/images/rss.green.png"/></a>
</td>