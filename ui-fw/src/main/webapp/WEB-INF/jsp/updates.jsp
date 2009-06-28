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
								<c:set var="update"><x:out select="$FormModel/*[local-name()='Activity']/*[local-name()='update']" /></c:set>
								<c:if test="${update=='1'}">
									<img height="${iconSize}" width="${iconSize}" title="<fmt:message key="org_intalio_uifw_tasks_ready"/>" border="0px" src="images/icons/icon.notclaimed.gif"/>
								</c:if>
							</a>]]>
						</cell>
						<%-- aircraft ID --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[local-name()='Activity']/*[local-name()='AircraftID']" />
							</a>]]>
						</cell>
						<%-- Arrival Flight Number --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[local-name()='ArrivalDeparture']/*[local-name()='ArrivalFlightNumber']" />
							</a>]]>
						</cell>
						<%-- Scheduled Arrival Date --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[local-name()='ArrivalDeparture']/*[local-name()='ScheduledArrivalDate'], 6, 10)" />
							</a>]]>
						</cell>
						<%-- STA --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[local-name()='ArrivalDeparture']/*[local-name()='STA'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- ATA --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<c:set var="ActualArrivalDate"><x:out select="$FormModel/*[local-name()='ArrivalDeparture']/*[local-name()='ActualArrivalDate']" /></c:set>
								<c:if test="${ActualArrivalDate!='1970-01-01'}"><x:out select="substring($FormModel/*[local-name()='ArrivalDeparture']/*[local-name()='ATA'], 1, 5)" /></c:if>
							</a>]]>
						</cell>
						<%-- Type --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[local-name()='Inspection']/*[local-name()='InspectionType']" />
							</a>]]>
						</cell>
						<%-- Departure Flight Number --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[local-name()='ArrivalDeparture']/*[local-name()='DepartureFlightNumber']" />
							</a>]]>
						</cell>
						<%-- Scheduled Departure Date --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[local-name()='ArrivalDeparture']/*[local-name()='ScheduledDepartureDate'], 6, 10)" />
							</a>]]>
						</cell>
						<%-- STD --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[local-name()='ArrivalDeparture']/*[local-name()='STD'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- ATD --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<c:set var="ActualDepartureDate"><x:out select="$FormModel/*[local-name()='ArrivalDeparture']/*[local-name()='ActualDepartureDate']" /></c:set>
								<c:if test="${ActualDepartureDate!='1970-01-01'}"><x:out select="substring($FormModel/*[local-name()='ArrivalDeparture']/*[local-name()='ATD'], 1, 5)" /></c:if>
							</a>]]>
						</cell>
						<%-- Stand --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[local-name()='Inspection']/*[local-name()='Stand']" />
							</a>]]>
						</cell>
						<%-- Coordinator --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[local-name()='Inspection']/*[local-name()='coordinator']" />
							</a>]]>
						</cell>
						<%-- Mechanics --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:forEach var="mech" select="$FormModel/*[local-name()='Inspection']/*[local-name()='assignedMechanics']" varStatus="mechStatus">
									<c:set var="releaser"><x:out select="$mech/*[local-name()='entitledToRelease']" /></c:set>
									<c:if test="${releaser=='1'}"><span style='color:red'></c:if><x:out select="$mech/*[local-name()='assignedMechanicName']" /><c:if test="${releaser=='1'}"></span></c:if><c:if test="${!mechStatus.last}"><br/></c:if>
								</x:forEach>
							</a>]]>
						</cell>
						<%-- Avionics --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:forEach var="avi" select="$FormModel/*[local-name()='Inspection']/*[local-name()='assignedAvionics']" varStatus="aviStatus">
									<x:out select="$avi/*[local-name()='assignedAvionicName']" /><c:if test="${!aviStatus.last}"><br/></c:if>
								</x:forEach>
							</a>]]>
						</cell>
						<%-- HIL --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<i>TBD</i>
							</a>]]>
						</cell>
						<%-- RTR --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:forEach var="rtr" select="$FormModel/*[local-name()='Inspection']/*[local-name()='RTR']" varStatus="rtrStatus">
									<x:out select="$rtr/*[local-name()='RTRid']" /><c:if test="${!rtrStatus.last}"><br/></c:if>
								</x:forEach>
							</a>]]>
						</cell>
						<%-- Comments --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="$FormModel/*[local-name()='DC']/*[local-name()='Comments']" />
							</a>]]>
						</cell>
						<%-- State --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<c:set var="TAstatus"><x:out select="$FormModel/*[local-name()='Inspection']/*[local-name()='InspectionStatus']"/></c:set>
								<c:choose>
									<c:when test="${TAstatus=='problem'}">
										<span style='background-color:red;color:white'><c:out value="${TAstatus}" /></span>
									</c:when>
									<c:when test="${TAstatus=='stopped'}">
										<span style='background-color:yellow'><c:out value="${TAstatus}" /></span>
									</c:when>
									<c:when test="${TAstatus=='released'}">
										<span style='background-color:green;color:white'><c:out value="${TAstatus}" /></span>
									</c:when>
									<c:when test="${TAstatus=='started'}">
										<span style='background-color:skyblue'><c:out value="${TAstatus}" /></span>
									</c:when>
									<c:otherwise>
										<c:out value="${TAstatus}" />
									</c:otherwise>
								</c:choose>
							</a>]]>
						</cell>
						<%-- Resources --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<span style='background-color:red;color:white'><x:out select="$FormModel/*[local-name()='Inspection']/*[local-name()='resources']" /></span>
							</a>]]>
						</cell>
						<%-- Start Time --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[local-name()='Activity']/*[local-name()='startTime'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- End Time --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[local-name()='Activity']/*[local-name()='finishTime'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- Release Time --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
								<x:out select="substring($FormModel/*[local-name()='Activity']/*[local-name()='releaseTime'], 1, 5)" />
							</a>]]>
						</cell>
						<%-- Late --%>
						<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
							</a>]]>
						</cell>
					</row>
				</c:if>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</rows>