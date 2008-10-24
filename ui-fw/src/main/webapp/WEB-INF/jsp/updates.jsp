<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/xml" %>
<c:set var="iconSize" value="12"/>
<rows>
	<page><%= request.getAttribute("currentPage") %></page>
	<total><%= request.getAttribute("totalPage") %></total>
	<c:choose>
		<c:when test="${param.type == 'Notification'}">
			<c:forEach items="${tasks}" var="taskHolder" varStatus="status">
				<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
				<row id="${status.index}">
					<cell><![CDATA[<a href="${taskFullURL}" target="taskform">${taskHolder.task.description}</a>]]></cell>
					<cell>${taskHolder.task.creationDate}</cell>
				</row>
			</c:forEach>
		</c:when>
		<c:when test="${param.type == 'PIPATask'}">
			<c:forEach items="${tasks}" var="taskHolder" varStatus="status">
				<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
				<row id="${status.index}">
					<cell><![CDATA[<a href="${taskFullURL}" target="taskform">${taskHolder.task.description}</a>]]></cell>
					<cell>${taskHolder.task.creationDate}</cell>
				</row>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<c:forEach items="${tasks}" var="taskHolder" varStatus="status">
				<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
				<row id="${status.index}">
					<cell><![CDATA[<a href="${taskFullURL}" target="taskform">${taskHolder.task.description}</a>]]></cell>
					<cell>
						<img height="${iconSize}" width="${iconSize}" border="0px" src="images/green-on-48.png"/>
					</cell>
					<cell>${taskHolder.task.creationDate}</cell>
					<cell>${taskHolder.task.deadline}</cell>
					<cell>${taskHolder.task.priority}</cell>
					<cell><![CDATA[
						<c:if test="${fn:length(taskHolder.task.attachments) > 0}">
							<c:forEach items="${taskHolder.task.attachments}" var="attachment" varStatus="index">
								<a href="${attachment.payloadURL}" onClick="window.open('${attachment.payloadURL}', 'newwindow'); return false;"><img border="0" height="${iconSize}" width="${iconSize}" src="http://www.slcc.edu/shared/shared_vcampus/images/icons/mail.jpg"/></a>
							</c:forEach>
						</c:if>	
						]]>
					</cell>
				</row>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</rows>