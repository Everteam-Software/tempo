<%@ page isErrorPage="true" %>
<%
out.println("<pre>");
PrintWriter pw = response.getWriter();
exception.printStackTrace(pw);
out.println("</pre>");
%> 
