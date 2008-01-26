<%--
 Copyright (c) 2005-2006 Intalio inc.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html

 Contributors:
 Intalio inc. - initial API and implementation
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="intalio" uri="http://www.intalio.com/tagfiles"%>



<%@ attribute name="headerCell" required="false" type="java.lang.String" %>


<%--c:set var="toolbar">
	Define toolbar here
</c:set --%>

<c:set var="title">
	<fmt:message key="com_intalio_bpms_workflow_pageTitle"/>
</c:set>

<c:set var="scripts">
	<script src="script/ui-fw.js" language="javascript" type="text/javascript"></script> 
</c:set>
<c:set var="footer">
  <span>&nbsp;&nbsp;<fmt:message key="com_intalio_bpms_workflow_pageFooter_poweredBy_label" />&nbsp;&nbsp;
  <a href="http://www.intalio.com"><span style="color: #3082A8"><fmt:message key="com_intalio_bpms_workflow_pageFooter_poweredBy_value" /></span></a>
  <fmt:message key="com_intalio_bpms_workflow_versionInfo">
	<c:choose>
		<c:when test="${!empty version && !empty build}" >
			<fmt:param value="${version}"/>
			<fmt:param value="${build}"/>
		</c:when> 
		<c:otherwise>
			<fmt:param value="unknown"/>
			<fmt:param value="unknown"/>
		</c:otherwise>
	</c:choose>
     </fmt:message>
     <a href="http://bpms.intalio.com"><span style="color: #3082A8"><fmt:message key="com_intalio_bpms_workflow_pageFooter_featureBugRequest"/></span></a>
   </span>
</c:set>
<intalio:body 
	hideToolbar="true" 
	selectedToolbarItem="" 
	dojoRequired="false"
	dateTimePickerRequired="false"
	toolbar="${toolbar}"
	scripts="${scripts}"
	
	subMenuHeader="${subMenuHeader}"
	headerCell="${headerCell}"
	title="${title}"
	footer="${footer}"
>			                	
	<jsp:doBody/>
</intalio:body>
