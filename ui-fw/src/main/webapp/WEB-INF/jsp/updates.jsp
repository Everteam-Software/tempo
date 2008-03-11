<%@page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>

<json:object>
  <json:array name="tasks" var="taskHolder" items="${activityTasks}">
    <json:object>
      <json:property name="taskUrl" value="${taskHolder.formManagerURL}?id=${taskHolder.task.ID}&url=${taskHolder.task.formURL}&token=${participantToken}&user=${currentUser}" />
      <json:property name="description" value="${taskHolder.task.description}"/>
      <json:property name="creationDate" value="${taskHolder.task.creationDate}"/>
    </json:object>
  </json:array>
  <json:array name="process" var="taskHolder" items="${initTasks}">
    <json:object>
      <json:property name="taskUrl" value="${taskHolder.formManagerURL}?id=${taskHolder.task.ID}&url=${taskHolder.task.formURL}&token=${participantToken}&user=${currentUser}" />
      <json:property name="description" value="${taskHolder.task.description}"/>
      <json:property name="creationDate" value="${taskHolder.task.creationDate}"/>
    </json:object>
  </json:array>
</json:object> 