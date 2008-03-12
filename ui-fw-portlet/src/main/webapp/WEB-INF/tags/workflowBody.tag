<%--
 Copyright (c) 2005-2006 Intalio inc.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html

 Contributors:
 Intalio inc. - initial API and implementation
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="intalio" uri="http://www.intalio.com/tagfiles"%>

<%@ attribute name="headerCell" required="false" type="java.lang.String"%>
<%@ attribute name="portletPrefix" required="false"
	type="java.lang.String"%>

<%--c:set var="toolbar">
	Define toolbar here
</c:set --%>

<c:set var="title">
	<spring:message code="com_intalio_bpms_workflow_pageTitle" />
</c:set>

<c:set var="scripts">
	<script src="${portletPrefix}/script/ui-fw.js" language="javascript"
		type="text/javascript"></script>
</c:set>
<c:set var="footer">
	<span>&nbsp;&nbsp;<spring:message
		code="com_intalio_bpms_workflow_pageFooter_poweredBy_label" />&nbsp;&nbsp;
  <a href="http://www.intalio.com">
	<span style="color: #3082A8">
	<spring:message
		code="com_intalio_bpms_workflow_pageFooter_poweredBy_value" />
	</span>
	</a>
	<spring:message code="com_intalio_bpms_workflow_versionInfo"
	arguments="${version}, ${build}" >
		<!-- 
		<c:choose>
			<c:when test="${!empty version && !empty build}">
				<c:out value="${version}" />
				<c:out value="${build}" />
			</c:when>
			<c:otherwise>
				<c:out value="unknown" />
				<c:out value="unknown" />
			</c:otherwise>
		</c:choose>
		 -->
	</spring:message>
	<a href="http://bpms.intalio.com">
	<span style="color: #3082A8">
	<spring:message
		code="com_intalio_bpms_workflow_pageFooter_featureBugRequest" />
	</span>
	</a>
	</span>
</c:set>
<intalio:body hideToolbar="true" selectedToolbarItem=""
	dojoRequired="false" dateTimePickerRequired="false"
	toolbar="${toolbar}" scripts="${scripts}"
	subMenuHeader="${subMenuHeader}" headerCell="${headerCell}"
	title="${title}" footer="${footer}" portletPrefix="${portletPrefix}">
	<jsp:doBody />
</intalio:body>
