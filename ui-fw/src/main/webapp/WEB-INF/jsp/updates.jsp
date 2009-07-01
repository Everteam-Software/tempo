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
			<%-- Scheduled Arrival Date --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform"><c:out value="${taskHolder.task._ScheduledArrivalDate}" /></a>]]></cell>
			<%-- STA --%>
			<cell><![CDATA[<a href="${taskFullURL}" title="" target="taskform"><fmt:formatDate value="${taskHolder.task._STA}" type="both" timeStyle="short" dateStyle="short" /></a>]]>
			</cell>
        </row>
      </c:forEach>
    </c:otherwise>
  </c:choose>
</rows>