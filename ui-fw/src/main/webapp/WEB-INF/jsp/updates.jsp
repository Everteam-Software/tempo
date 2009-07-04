<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
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
        <row id="pa${status.index}">
			<%-- update --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
					<c:if test="${taskHolder.task._update}">
						<img height="${iconSize}" width="${iconSize}" title="<fmt:message key="org_intalio_uifw_tasks_ready"/>" border="0px" src="images/icons/icon.notclaimed.gif"/>
					</c:if>
				</a>]]></cell>
			<%-- aircraft ID --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">${taskHolder.task._AircraftID}
				</a>]]></cell>
			<%-- Arrival Flight Number --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">${taskHolder.task._ArrivalFlightNumber}
				</a>]]></cell>
			<%-- Scheduled Arrival Date --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform"><fmt:formatDate value="${taskHolder.task._ScheduledArrival}" type="date" pattern="dd-MM" />
				</a>]]></cell>
			<%-- STA --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform"><fmt:formatDate value="${taskHolder.task._ScheduledArrival}" type="time" pattern="HH:mm" />
				</a>]]></cell>
			<%-- ATA --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
					<c:set var="date"><fmt:formatDate value="${taskHolder.task._ActualArrival}" type="time" pattern="yyyy" /></c:set>
					<c:if test="${date!=1970}"><fmt:formatDate value="${taskHolder.task._ActualArrival}" type="time" pattern="HH:mm" /></c:if>
				</a>]]>
			</cell>
			<%-- Type --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">${taskHolder.task._InspectionType}
				</a>]]></cell>
			<%-- Departure Flight Number --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">${taskHolder.task._DepartureFlightNumber}
				</a>]]></cell>
			<%-- Scheduled Departure Date --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform"><fmt:formatDate value="${taskHolder.task._ScheduledDeparture}" type="date" pattern="dd-MM" />
				</a>]]></cell>
			<%-- STD --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform"><fmt:formatDate value="${taskHolder.task._ScheduledDeparture}" type="time" pattern="HH:mm" />
				</a>]]></cell>
			<%-- ATD --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
					<c:set var="date"><fmt:formatDate value="${taskHolder.task._ActualDeparture}" type="time" pattern="yyyy" /></c:set>
					<c:if test="${date!=1970}"><fmt:formatDate value="${taskHolder.task._ActualDeparture}" type="time" pattern="HH:mm" /></c:if>
				</a>]]></cell>
			<%-- Stand --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">${taskHolder.task._Stand}
				</a>]]></cell>
			<%-- Coordinator --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
					<c:forEach items="${taskHolder.task._assignedCoord}" var="currentCoord" varStatus="coordStatus">${currentCoord.name}<c:if test="${!coordStatus.last}"><br/></c:if></c:forEach>
				</a>]]></cell>
			<%-- Mechanics --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
					<c:forEach items="${taskHolder.task._assignedMechanics}" var="currentMech" varStatus="mechStatus">
						<c:choose>
							<c:when test="${currentMech._entitledToRelease}"><span style='color:red'>${currentMech.name}</span></c:when>
							<c:otherwise>${currentMech.name}</c:otherwise>
						</c:choose><c:if test="${!mechStatus.last}"><br/></c:if>
					</c:forEach>
				</a>]]></cell>
			<%-- Avionics --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
					<c:forEach items="${taskHolder.task._assignedAvionics}" var="currentAvi" varStatus="aviStatus">${currentAvi.name}<c:if test="${!aviStatus.last}"><br/></c:if></c:forEach>
				</a>]]></cell>
			<%-- HIL --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
					<i>TBD</i>
				</a>]]>
			</cell>
			<%-- RTR --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
					<c:forEach items="${taskHolder.task._RTR}" var="currentRTR" varStatus="rtrStatus">${currentRTR.RTRID}<c:if test="${!rtrStatus.last}"><br/></c:if></c:forEach>
				</a>]]></cell>
			<%-- Comments --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">${taskHolder.task._comments}</a>]]>
			</cell>
			<%-- State --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
					<c:choose>
						<c:when test="${taskHolder.task._InspectionStatus=='problem'}">
							<span style='background-color:red;color:white'>problem</span>
						</c:when>
						<c:when test="${taskHolder.task._InspectionStatus=='stopped'}">
							<span style='background-color:yellow'>stopped</span>
						</c:when>
						<c:when test="${taskHolder.task._InspectionStatus=='released'}">
							<span style='background-color:green;color:white'>released</span>
						</c:when>
						<c:when test="${taskHolder.task._InspectionStatus=='started'}">
							<span style='background-color:skyblue'>started</span>
						</c:when>
						<c:otherwise>${taskHolder.task._InspectionStatus}</c:otherwise>
					</c:choose>
				</a>]]></cell>
			<%-- Resources --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform"><span style='background-color:red;color:white'>${taskHolder.task._resources}</span>
				</a>]]></cell>
			<%-- Start Time --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform"><fmt:formatDate value="${taskHolder.task._startTime}" type="time" pattern="HH:mm" />
				</a>]]></cell>
			<%-- End Time --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform"><fmt:formatDate value="${taskHolder.task._finishTime}" type="time" pattern="HH:mm" />
				</a>]]></cell>
			<%-- Release Time --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform"><fmt:formatDate value="${taskHolder.task._releaseTime}" type="time" pattern="HH:mm" />
				</a>]]></cell>
			<%-- Late --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform">
					<c:if test="${taskHolder.task._late}">
							<img height="${iconSize}" width="${iconSize}" title="<fmt:message key="org_intalio_uifw_tasks_ready"/>" border="0px" src="images/icons/icon.notclaimed.gif"/>
					</c:if>
				</a>]]></cell>
        </row>
      </c:forEach>
    </c:otherwise>
  </c:choose>
</rows>