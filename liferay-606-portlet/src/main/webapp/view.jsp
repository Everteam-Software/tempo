<%@ page import="com.liferay.portal.util.*"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<portlet:defineObjects />
<%@ include file="properties.jsp" %>
<%  
/******************** CODE ************************/
String alfa = request.getParameter("alfa");

long userid = PortalUtil.getUserId(request);
String username = realm+PortalUtil.getUserName(userid, null, "user.login.id");
String password = PortalUtil.getUserPassword(request);
String email = PortalUtil.getUser(request).getEmailAddress();

HttpServletRequest httpReq = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));
String abc = httpReq.getParameter("abc");
/******************** CODE ************************/
%>
<style>
.barmenu { margin-bottom:10px; }
span.des {font-size:1.2em; color:#006699; font-weight:bold; cursor:pointer; }
</style>
<!-- container for the existing markup tabs -->
<div id="addButtonCt" class="barmenu"></div><div id="doButton" class="barmenu"></div>
<div id="tabs1"></div>
<input type="hidden" id="usr" value="<%=username  %>" />
<input type="hidden" id="psw" value="<%=password  %>" />
<input type="hidden" id="mail" value="<%=email  %>" />
<input type="hidden" id="alfa" value="<%=alfa  %>" />
<input type="hidden" id="remoteUser" value="<%=request.getRemoteUser()%>" />
<input type="hidden" id="userPrincipal" value="<%=request.getUserPrincipal()%>" />
<liferay-portlet:resourceURL var="url" id="myid" />
<input type="hidden" value="${url}" />
