<%@ page import="org.intalio.tempo.web.ApplicationState" %>
<% 
ApplicationState state = (ApplicationState)request.getSession().getAttribute(ApplicationState.PARAMETER_NAME);
String tok = state.getCurrentUser().getToken();
%>
<td>
	<a href="/ui-fw/ical" title="iCalendar export"><img width="20" height="20" border="0" src="/ui-fw/images/ical.jpg"/></a>
	<a href="/ui-fw/atom/tasks?token=<%=tok%>" title="Personal Tasks Feed"><img width="20" height="20" border="0" src="/ui-fw/images/rss.orange.png"/></a>
	<a href="/ui-fw/atom/processes?token=<%=tok%>" title="Personal Process Feed"><img width="20" height="20" border="0" src="/ui-fw/images/rss.green.png"/></a>
</td>