<%--
	Copyright (c) 2005-2008 Intalio inc.

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html

	Contributors:
	Intalio inc. - initial API and implementation
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
	<table class="tasks">
		<tbody class="tasks" id="padata">
			<c:forEach items="${activityTasks}" var="taskHolder" varStatus="status">
				<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
				<c:choose>
					<c:when test="${(status.index%2) == 0}">
						<tr class="oddTr">
						</c:when>
						<c:otherwise>
							<tr class="evenTr">
							</c:otherwise>
						</c:choose>
						<td>
							<a href="${taskFullURL}" target="taskform">
							<c:if test="${(taskHolder.task.state.name) == 'READY'}">
								<img height="15" width="15" border="0px" src="images/ledblue.png"/>
							</c:if>
							<c:if test="${(taskHolder.task.state.name) == 'CLAIMED'}">
								<img height="15" width="15" border="0px" src="images/lock.png"/>
							</c:if>
							</a>
						</td>
						<td>
							<a href="${taskFullURL}" target="taskform">${taskHolder.task.description}</a>
						</td>
						<td>
							<a href="${taskFullURL}" target="taskform">${taskHolder.task.creationDate}</a>
						</td>
						<td>
							<a href="${taskFullURL}" target="taskform">${taskHolder.task.deadline}</a>
						</td>
						<td>
							<a href="${taskFullURL}" target="taskform">${taskHolder.task.priority}</a>
						</td>
						<td>
							<c:if test="${fn:length(taskHolder.task.attachments) > 0}">
								<c:forEach items="${taskHolder.task.attachments}" var="attachment" varStatus="index">
									<a href="${attachment.payloadURL}" onClick="window.open('${attachment.payloadURL}', 'newwindow'); return false;"><img height="15" width="15" src="http://www.slcc.edu/shared/shared_vcampus/images/icons/mail.jpg"/></a>
								</c:forEach>
							</c:if>							
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

		<table>
			<tbody class="tasks" id="notifdata">
				<c:forEach items="${notifications}" var="taskHolder" varStatus="status">
					<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
					<c:choose>
						<c:when test="${(status.index%2) == 0}">
							<tr class="oddTr">
							</c:when>
							<c:otherwise>
								<tr class="evenTr">
								</c:otherwise>
							</c:choose>
							<td>
								<a href="${taskFullURL}" target="taskform"> ${taskHolder.task.description}</a>
							</td>
							<td>
								<a href="${taskFullURL}" target="taskform"> ${taskHolder.task.creationDate}</a>
							</td>
							<td>
								<a href="${taskFullURL}" target="taskform">${taskHolder.task.priority}</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<table>
				<tbody class="tasks" id="pipadata">
					<c:forEach items="${initTasks}" var="taskHolder" varStatus="status">
						<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
						<c:choose>
							<c:when test="${(status.index%2) == 0}">
								<tr class="oddTr">
								</c:when>
								<c:otherwise>
									<tr class="evenTr">
									</c:otherwise>
								</c:choose>
								<td>
									<a href="${taskFullURL}" target="taskform">${taskHolder.task.description}</a>
								</td>
								<td>
									<a href="${taskFullURL}" target="taskform">${taskHolder.task.creationDate}</a>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</html>