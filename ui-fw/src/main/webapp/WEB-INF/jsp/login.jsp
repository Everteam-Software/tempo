 <%--
 Copyright (c) 2005-2008 Intalio inc.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html

 Contributors:
 Intalio inc. - initial API and implementation
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ page import="org.intalio.tempo.web.ApplicationState"%>
<html>
<%
    ApplicationState obj = (ApplicationState) request.getSession().getAttribute("APPLICATION_STATE");
    String prevAction = "/ui-fw";
    if (obj != null) {
        if (obj.getPreviousAction() != null) {
            prevAction = obj.getPreviousAction();
        }
    }
%>
<c:redirect url="../login.htm">
     <c:param name="prevAction"><%=prevAction%></c:param>
</c:redirect>
</html>
