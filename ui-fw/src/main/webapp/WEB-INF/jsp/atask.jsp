<%--
	Copyright (c) 2005-2009 Intalio inc.

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html

	Contributors:
	Intalio inc. - initial API and implementation
--%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="en" xml:lang="en">
  <head>
    <title>
      <fmt:message key="com_intalio_bpms_workflow_pageTitle"/>
    </title>

    <link rel="shortcut icon" href="/favicon.ico" type="image/x-icon"/>

    <link rel="stylesheet" type="text/css" href="style.css" />
    <link rel="stylesheet" type="text/css" href="style/tabs.css"/>
    <link rel="stylesheet" type="text/css" href="style/flexigrid.css"/>
    <link rel="stylesheet" type="text/css" href="style/jqueryui/ui.all.css"/>

    <link rel="alternate" type="application/atom+xml" title="Personal Task feed" href="/feeds/atom/tasks?token=${participantToken}"/>
    <link rel="alternate" type="application/atom+xml" title="Process feed" href="/feeds/atom/processes?token=${participantToken}"/>

      <script type="text/javascript">var one_task_page = true /*Flag to safeguard changes */</script>

    <script type="text/javascript" src="script/ui-fw.js"></script>
    <script type="text/javascript" src="script/jquery-1.3.2.min.js"></script>
    <script type="text/javascript" src="script/jtabber.js"></script>
    <script type="text/javascript" src="script/jquery-timer.js"></script>
    <script type="text/javascript" src="script/jquery.jcorners.js"></script>
    <script type="text/javascript" src="script/jquery.demensions.js"></script>
    <script type="text/javascript" src="script/jquery.string.1.0.js"></script>
    <script type="text/javascript" src="script/soap-1.4beta.js"></script>
    <script type="text/javascript" src="script/ui/ui.core.js"></script>
    <script type="text/javascript" src="script/ui/ui.draggable.js"></script>
    <script type="text/javascript" src="script/ui/ui.resizable.js"></script>
    <script type="text/javascript" src="script/ui/ui.dialog.js"></script>
    <script type="text/javascript" src="script/ui/effects.core.js"></script>
    <script type="text/javascript" src="script/ui/effects.highlight.js"></script>
    <script type="text/javascript" src="script/ui/jquery.bgiframe.js"></script>
    <script type="text/javascript" src="script/flexigrid.js"></script>
    
    <%@ include file="/script/grids.jsp"%>

  </head>
  <body width="95%" height="98%">

    <%@ include file="/WEB-INF/jsp/siteHeader.jsp"%>
    
    <div id="connectionLost" title="<fmt:message key="org_intalio_uifw_session_connection_lost"/>">
    </div>

    <div id="sessionExpired" title="<fmt:message key="org_intalio_uifw_session_expired"/>">
       <p><fmt:message key="org_intalio_uifw_session_expired"/></p>
       <p><a href="<%=request.getRequestURI() + "?" + request.getQueryString()%>"><fmt:message key="org_intalio_uifw_session_login_again"/></a></p>
    </div>

    <iframe src="${taskform}" onLoad="resizeIframe" name="taskform" frameborder="0" id="taskform" scrolling="auto"></iframe>

    <div id="footer" style="margin-left:20px">
      <fmt:message key="com_intalio_bpms_workflow_pageFooter_poweredBy_label" />
      <a href="http://www.intalio.com">
        <span style="color: #3082A8">
          <fmt:message key="com_intalio_bpms_workflow_pageFooter_poweredBy_value" />
        </span>
      </a>
      <a href="http://bpms.intalio.com">
        <span style="color: #3082A8"><fmt:message key="com_intalio_bpms_workflow_pageFooter_featureBugRequest"/></span>
      </a>
    </div>

    <script>
      document.getElementById('taskform').onload = resizeIframe;
      window.onresize = resizeIframe;
    </script>

  </body>
</html>
