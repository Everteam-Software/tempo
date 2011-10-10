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
			<c:set var="users" value="${taskHolder.task.userOwners}" />
			<c:set var="roles" value="${taskHolder.task.roleOwners}" />
			<c:set var="usersLength" value="${fn:length(fn:escapeXml(users))}" />
			<c:set var="rolesLength" value="${fn:length(fn:escapeXml(roles))}" />
			<c:set var="showAnchorTag" value="true" />
			<c:set var="showAlert" value="" />
                   <c:if test="${isWorkflowAdmin}"> 
                        <c:forEach var="role" items="${userRoles}">      
							<c:if test="${fn:indexOf(fn:escapeXml(roles),fn:escapeXml(role))!=-1}"> 
								<c:set var="showAnchorTag" value="false" />
							</c:if> 
						</c:forEach>
						<c:if test="${ showAnchorTag && fn:indexOf(fn:escapeXml(users),fn:escapeXml(currentUser))==-1}">
                                 <c:set var="messageKey"><fmt:message key='com_intalio_bpms_workflow_admin_notifications_retrieve_error'/> </c:set> 
			                     <c:set var="title"><fmt:message key='com_intalio_bpms_workflow_pageTitle'/> </c:set> 
	                             <c:set var="showAlert" value="showAlertForNotification('${messageKey}','${title}');return false" /> 
                        </c:if> 
                   </c:if>

			<row id="no${status.index}"> <cell><![CDATA[
					    <a class="taskd" href="${taskFullURL}" tid="${taskHolder.task.ID}" target="taskform" onclick='<c:out value="${showAlert}"/>'> 
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
			</c:choose> </cell> <cell><![CDATA[<a href="${taskFullURL}" title="${taskHolder.task.creationDate}" target="taskform" onclick='<c:out value="${showAlert}"/>' ><fmt:formatDate value="${taskHolder.task.creationDate}" type="both" timeStyle="short" dateStyle="short" /></a>]]></cell>									
						<cell><![CDATA[<c:out value="${fn:substring(fn:escapeXml(users),1,usersLength-1)}" />]]></cell>
						<cell><![CDATA[<c:out value="${fn:substring(fn:escapeXml(roles),1,rolesLength-1)}" />]]></cell>					
			</row>
		</c:forEach>
	</c:when>
	<c:when test="${param.type == 'PIPATask'}">
		<c:forEach items="${tasks}" var="taskHolder" varStatus="status">

			<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
			<c:set var="users" value="${taskHolder.task.userOwners}" />
			<c:set var="roles" value="${taskHolder.task.roleOwners}" />
			<c:set var="usersLength" value="${fn:length(fn:escapeXml(users))}" />
			<c:set var="rolesLength" value="${fn:length(fn:escapeXml(roles))}" />
			<c:set var="showAnchorTag" value="true" />
			<c:set var="showAlert" value="" />
                 <c:if test="${isWorkflowAdmin}"> 
                       <c:forEach var="role" items="${userRoles}">      
							<c:if test="${fn:indexOf(fn:escapeXml(roles),fn:escapeXml(role))!=-1}"> 
								<c:set var="showAnchorTag" value="false" />
							</c:if> 

						</c:forEach>
						<c:if test="${ showAnchorTag && fn:indexOf(fn:escapeXml(users),fn:escapeXml(currentUser))==-1}">
                             <c:set var="messageKey"><fmt:message key='com_intalio_bpms_workflow_admin_processes_retrieve_error'/> </c:set> 
		                     <c:set var="title"><fmt:message key='com_intalio_bpms_workflow_pageTitle'/> </c:set> 
		                     <c:set var="showAlert" value="showAlertForProcess('${messageKey}','${title}');return false" />
                        </c:if> 
                  </c:if>

			<row id="pi${status.index}"> <cell><![CDATA[
						<a class="pipa" href="${taskFullURL}" endpoint="${taskHolder.task.processEndpoint}" url="${taskHolder.task.formURL}" id="${taskHolder.task.ID}" target="taskform" onclick='<c:out value="${showAlert}"/>' >
						<c:choose>
							<c:when test="${taskHolder.task.description == ''}">
								<i><fmt:message key="org_intalio_uifw_tasks_notitle"/></i>
							</c:when>
							<c:otherwise>${taskHolder.task.description}</c:otherwise>
						</c:choose>
						</a>
						]]></cell> <cell><![CDATA[<a href="${taskFullURL}" title="${taskHolder.task.creationDate}" target="taskform" onclick='<c:out value="${showAlert}"/>' ><fmt:formatDate value="${taskHolder.task.creationDate}" type="both" timeStyle="short" dateStyle="short" /></a>]]></cell>

						<cell><![CDATA[<c:out value="${fn:substring(fn:escapeXml(users),1,usersLength-1)}" />]]></cell>
						<cell><![CDATA[<c:out value="${fn:substring(fn:escapeXml(roles),1,rolesLength-1)}" />]]></cell>						
			</row>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<c:forEach items="${tasks}" var="taskHolder" varStatus="status">
			<c:set var="users" value="${taskHolder.task.userOwners}" />
			<c:set var="roles" value="${taskHolder.task.roleOwners}" />
			<c:set var="usersLength" value="${fn:length(fn:escapeXml(users))}" />
			<c:set var="rolesLength" value="${fn:length(fn:escapeXml(roles))}" />
			<c:set var="taskFullURL" value="${taskHolder.formManagerURL}" />
			<c:set var="showAnchorTag" value="true" />
			<c:set var="showAlert" value="" />
                        <c:if test="${isWorkflowAdmin}"> 
                                <c:forEach var="role" items="${userRoles}">      
									<c:if test="${fn:indexOf(fn:escapeXml(roles),fn:escapeXml(role))!=-1}"> 
										<c:set var="showAnchorTag" value="false" />
									</c:if> 
								</c:forEach>
								<c:if test="${ showAnchorTag && fn:indexOf(fn:escapeXml(users),fn:escapeXml(currentUser))==-1}">
			                             <c:set var="messageKey"><fmt:message key='com_intalio_bpms_workflow_admin_tasks_retrieve_error'/> </c:set> 
			                             <c:set var="title"><fmt:message key='com_intalio_bpms_workflow_pageTitle'/> </c:set> 
			                             <c:set var="showAlert" value="showAlertForTask('${messageKey}','${title}');return false" />
					           </c:if> 
                        </c:if>
			<row id="pa${status.index}"> <cell><![CDATA[
					     
					     <a class="taskd" state="${taskHolder.task.state}" href="${taskFullURL}" tid="${taskHolder.task.ID}" target="taskform" priority="${taskHolder.task.priority}" description="${taskHolder.task.description}" onclick='<c:out value="${showAlert}"/>' >
		       				<c:choose>
								<c:when test="${taskHolder.task.description == ''}">
									<i><fmt:message key="org_intalio_uifw_tasks_notitle"/></i>
								</c:when>
								<c:otherwise>${taskHolder.task.description}</c:otherwise>
						</c:choose>
						</a>
							]]></cell> <cell><![CDATA[
						<a href="${taskFullURL}" target="taskform">
						<c:if test="${taskHolder.task.state =='CLAIMED'}">
     						<img height="${iconSize}" width="${iconSize}" title="<fmt:message key="org_intalio_uifw_tasks_claimed"/>" border="0px" src="images/icons/icon.claimed.gif"/>
						</c:if>
						<c:if test="${taskHolder.task.state =='READY'}">
	    					<img height="${iconSize}" width="${iconSize}" title="<fmt:message key="org_intalio_uifw_tasks_ready"/>" border="0px" src="images/icons/icon.notclaimed.gif"/>
						</c:if>
						</a>
						]]> </cell> <cell><![CDATA[<a href="${taskFullURL}" title="${taskHolder.task.creationDate}" target="taskform" onclick='<c:out value="${showAlert}"/>' ><fmt:formatDate value="${taskHolder.task.creationDate}" type="both" timeStyle="short" dateStyle="short" /></a>]]></cell>
			<cell><![CDATA[<a href="${taskFullURL}" title="${taskHolder.task.deadline}" target="taskform"><fmt:formatDate value="${taskHolder.task.deadline}" type="both" timeStyle="short" dateStyle="short" /></a>]]></cell>
			<cell> <c:choose>
				<c:when test="${taskHolder.task.priority != '0'}">
								${taskHolder.task.priority}
							</c:when>
			</c:choose> </cell> <cell><![CDATA[
							<c:forEach items="${taskHolder.task.attachments}" var="attachment" varStatus="index">
<c:if test="${attachment ne null}">
								<a href="${attachment.payloadURL}" onClick="window.open('${attachment.payloadURL}', 'newwindow'); return false;"><img border="0" height="${iconSize}" width="${iconSize}" title="${attachment.payloadURL}" src="images/icons/icon.attachment.gif"/></a>
</c:if>
							</c:forEach>
						]]> </cell> 
						<cell><![CDATA[<c:out value="${fn:substring(fn:escapeXml(users),1,usersLength-1)}" />]]></cell>
						<cell><![CDATA[<c:out value="${fn:substring(fn:escapeXml(roles),1,rolesLength-1)}" />]]></cell>											
						</row>
		</c:forEach>
	</c:otherwise>
</c:choose> </rows>