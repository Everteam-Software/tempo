<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page contentType="text/xml;charset=UTF-8"%>
<c:set var="iconSize" value="12"/>
<rows>
	<page><%= request.getAttribute("currentPage") %></page>
	<total><%= request.getAttribute("totalPage") %></total>
	<c:choose>
		<c:when test="${param.type == 'Notification'}">
			<c:forEach items="${tasks}" var="taskHolder" varStatus="status">
				<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
				<row id="no${status.index}">
					<cell><![CDATA[
					    <a class="taskd" href="${taskFullURL}" tid="${taskHolder.task.ID}" target="taskform">
						<c:choose>
							<c:when test="${taskHolder.task.description == ''}">
								<i><fmt:message key="org_intalio_uifw_tasks_notitle"/></i>
							</c:when>
							<c:otherwise>${taskHolder.task.description}</c:otherwise>
						</c:choose>
						</a>
						]]></cell>
						<cell><![CDATA[<a href="${taskFullURL}" title="${taskHolder.task.creationDate}" target="taskform"><fmt:formatDate value="${taskHolder.task.creationDate}" type="both" timeStyle="short" dateStyle="short" /></a>]]></cell>
				</row>
			</c:forEach>
		</c:when>
		<c:when test="${param.type == 'PIPATask'}">
			<c:forEach items="${tasks}" var="taskHolder" varStatus="status">
				<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
				<row id="pi${status.index}">
					<cell><![CDATA[
						<a class="pipa" href="${taskFullURL}" url="${taskHolder.task.formURL}" id="${taskHolder.task.ID}" target="taskform">
						<c:choose>
							<c:when test="${taskHolder.task.description == ''}">
								<i><fmt:message key="org_intalio_uifw_tasks_notitle"/></i>
							</c:when>
							<c:otherwise>${taskHolder.task.description}</c:otherwise>
						</c:choose>
						</a>
						]]></cell>
						<cell><![CDATA[<a href="${taskFullURL}" title="${taskHolder.task.creationDate}" target="taskform"><fmt:formatDate value="${taskHolder.task.creationDate}" type="both" timeStyle="short" dateStyle="short" /></a>]]></cell>
				</row>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<c:forEach items="${tasks}" var="taskHolder" varStatus="status">
				<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
				<c:set var="taskData" value="${taskHolder.task.outputAsXmlString}" />
				<c:if test="${taskData != ''}">
					<x:parse doc="${taskData}" varDom="parsedOutput" />
					<x:set var="FormModel" select="$parsedOutput/*[local-name()='FormModel' and namespace-uri()='http://www.intalio.com/gi/forms/TAmanagement.gi']" />
					<row id="pa${status.index}">
						<%-- update --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='Activity']/*[name()='update']" />
							</a>]]>
						</cell>
						<%-- aircraft ID --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='Activity']/*[name()='AircraftID']" />
							</a>]]>
						</cell>
						<%-- Arrival Flight Number --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='ArrivalDeparture']/*[name()='ArrivalFlightNumber']" />
							</a>]]>
						</cell>
						<%-- Scheduled Arrival Date --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='ArrivalDeparture']/*[name()='ScheduledArrivalDate']" />
							</a>]]>
						</cell>
						<%-- STA --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[name()='ArrivalDeparture']/*[name()='STA'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- ATA --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[name()='ArrivalDeparture']/*[name()='ATA'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- Type --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='Inspection']/*[name()='InspectionType']" />
							</a>]]>
						</cell>
						<%-- Departure Flight Number --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='ArrivalDeparture']/*[name()='DepartureFlightNumber']" />
							</a>]]>
						</cell>
						<%-- Scheduled Departure Date --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='ArrivalDeparture']/*[name()='ScheduledDepartureDate']" />
							</a>]]>
						</cell>
						<%-- STD --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[name()='ArrivalDeparture']/*[name()='STD'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- ATD --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[name()='ArrivalDeparture']/*[name()='ATD'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- Stand --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='Inspection']/*[name()='Stand']" />
							</a>]]>
						</cell>
						<%-- Coordinator --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='Inspection']/*[name()='coordinator']" />
							</a>]]>
						</cell>
						<%-- Mechanics --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<i>TBD</i>
							</a>]]>
						</cell>
						<%-- Avionics --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<i>TBD</i>
							</a>]]>
						</cell>
						<%-- HIL --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<i>TBD</i>
							</a>]]>
						</cell>
						<%-- RTR --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<i>TBD</i>
							</a>]]>
						</cell>
						<%-- State --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='Inspection']/*[name()='InspectionStatus']" />
							</a>]]>
						</cell>
						<%-- Resources --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='Inspection']/*[name()='resources']" />
							</a>]]>
						</cell>
						<%-- Start Time --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[name()='Activity']/*[name()='startTime'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- End Time --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[name()='Activity']/*[name()='finishTime'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- Release Time --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[name()='Activity']/*[name()='releaseTime'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- Late --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[name()='Activity']/*[name()='late']" />
							</a>]]>
						</cell>
					</row>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</rows>