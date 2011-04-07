<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page contentType="text/xml;charset=UTF-8"%>
<c:set var="iconSize" value="12" />
<rows> <page><%=request.getAttribute("currentPage")%></page> <total><%=request.getAttribute("totalPage")%></total>
<c:choose>
	<c:when test="${param.type == 'Notification'}">
		<c:forEach items="${tasks}" var="taskHolder" varStatus="status">
			<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
			<row id="no${status.index}"> <cell><![CDATA
					    <a class="taskd" href="${taskFullURL}" tid="${taskHolder.task.ID}" target="taskform">
						<c:choose>
							<c:when test="${taskHolder.task.description == ''}">
								<i><fmt:message key="org_intalio_uifw_tasks_notitle"/></i>
							</c:when>
							<c:otherwise>${taskHolder.task.description}</c:otherwise>
						</c:choose>
						</a>
						]]></cell> <cell> <c:choose>
				<c:when test="${taskHolder.task.priority != '0'}">
    								${taskHolder.task.priority}
    							</c:when>
			</c:choose> </cell> <cell><![CDATA[<a href="${taskFullURL}" title="${taskHolder.task.creationDate}" target="taskform"><fmt:formatDate value="${taskHolder.task.creationDate}" type="both" timeStyle="short" dateStyle="short" /></a>]]></cell>
			</row>
		</c:forEach>
	</c:when>
	<c:when test="${param.type == 'PIPATask'}">
		<c:forEach items="${tasks}" var="taskHolder" varStatus="status">
			<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
			<row id="pi${status.index}"> <cell><![CDATA
						<a class="pipa" href="${taskFullURL}" endpoint="${taskHolder.task.processEndpoint}" url="${taskHolder.task.formURL}" id="${taskHolder.task.ID}" target="taskform">
						<c:choose>
							<c:when test="${taskHolder.task.description == ''}">
								<i><fmt:message key="org_intalio_uifw_tasks_notitle"/></i>
							</c:when>
							<c:otherwise>${taskHolder.task.description}</c:otherwise>
						</c:choose>
						</a>
						]]></cell> <cell><![CDATA[<a href="${taskFullURL}" title="${taskHolder.task.creationDate}" target="taskform"><fmt:formatDate value="${taskHolder.task.creationDate}" type="both" timeStyle="short" dateStyle="short" /></a>]]></cell>
			</row>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<c:forEach items="${tasks}" var="taskHolder" varStatus="status">
			<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
			<row id="pa${status.index}"> <cell><![CDATA
						    <a class="taskd" state="${taskHolder.task.state}" href="${taskFullURL}" tid="${taskHolder.task.ID}" target="taskform" priority="${taskHolder.task.priority}" description="${taskHolder.task.description}">
							<c:choose>
								<c:when test="${taskHolder.task.description == ''}">
									<i><fmt:message key="org_intalio_uifw_tasks_notitle"/></i>
								</c:when>
								<c:otherwise>${taskHolder.task.description}</c:otherwise>
							</c:choose>
							</a>
							]]></cell> <cell><![CDATA
						<a href="${taskFullURL}" target="taskform">
						<c:if test="${taskHolder.task.state =='CLAIMED'}">
     						<img height="${iconSize}" width="${iconSize}" title="<fmt:message key="org_intalio_uifw_tasks_claimed"/>" border="0px" src="images/icons/icon.claimed.gif"/>
						</c:if>
						<c:if test="${taskHolder.task.state =='READY'}">
	    					<img height="${iconSize}" width="${iconSize}" title="<fmt:message key="org_intalio_uifw_tasks_ready"/>" border="0px" src="images/icons/icon.notclaimed.gif"/>
						</c:if>
						</a>
						]]> </cell> <cell><![CDATA[<a href="${taskFullURL}" title="${taskHolder.task.creationDate}" target="taskform"><fmt:formatDate value="${taskHolder.task.creationDate}" type="both" timeStyle="short" dateStyle="short" /></a>]]></cell>
			<cell><![CDATA[<a href="${taskFullURL}" title="${taskHolder.task.deadline}" target="taskform"><fmt:formatDate value="${taskHolder.task.deadline}" type="both" timeStyle="short" dateStyle="short" /></a>]]></cell>
			<cell> <c:choose>
				<c:when test="${taskHolder.task.priority != '0'}">
								${taskHolder.task.priority}
							</c:when>
			</c:choose> </cell> <cell><![CDATA
							<c:forEach items="${taskHolder.task.attachments}" var="attachment" varStatus="index">
								<a href="${attachment.payloadURL}" onClick="window.open('${attachment.payloadURL}', 'newwindow'); return false;"><img border="0" height="${iconSize}" width="${iconSize}" title="${attachment.payloadURL}" src="images/icons/icon.attachment.gif"/></a>
							</c:forEach>
						]]> </cell> </row>
		</c:forEach>
	</c:otherwise>
</c:choose> </rows>
