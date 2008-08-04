<%--
 Copyright (c) 2005-2006 Intalio inc.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html

 Contributors:
 Intalio inc. - initial API and implementation
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="custom" tagdir="/WEB-INF/tags"%>


<c:set var="headerCell">
	<%@ include file="/WEB-INF/jsp/siteHeader.jsp"%>
</c:set>


<script type="text/javascript" src="/ui-fw/script/prototype.js"></script>
<script type="text/javascript" src="/ui-fw/script/tasks.js"></script>

<script type="text/javascript">
	window.onload = startTimer("${refreshTime}");
</script>


<custom:workflowBody headerCell="${headerCell}">

<div id="timer"></div>

	    <table id="tabPanel" width="100%" border="0" cellspacing="0" cellpadding="0" >
	      <tr>
	        <td id="ActiveTab" onClick="changetab(this,'tabContainer', 'tab1')"><fmt:message key="com_intalio_bpms_workflow_tab_tasks"/></td>
	        <td id="notActiveTab" onClick="changetab(this,'tabContainer','tab3')" ><fmt:message key="com_intalio_bpms_workflow_tab_notifications"/></td>
	        <td id="notActiveTab" onClick="changetab(this,'tabContainer','tab2')" ><fmt:message key="com_intalio_bpms_workflow_tab_processes"/></td>
	        <td id="tabPanelEnd">&nbsp;</td>
	      </tr>
	    </table>
			<!-- tab panel e -->
			<!-- tab container b-->				  
		  <div class="tabContainer" id="tabContainer">
			<div class="visibletab" id="tab1">
			<!-- Third Level Header b -->
	        <br/>
	        <!-- Third Level Header e -->

	      <div id="taskdiv">
	        <table width="80%"  cellspacing="0" cellpadding="0" id="properties_content">
	            <tr id="headertr">
	              <td width="10%"><strong><fmt:message key="com_intalio_bpms_workflow_taskHolder_taskState"/></strong></td>
	              <td width="35%"><strong><fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/></strong></td>
	              <td width="25%"><strong><fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/></strong></td>
				  <td width="20%"><strong><fmt:message key="com_intalio_bpms_workflow_taskHolder_dueDate"/></strong></td>
				  <td width="10%"><strong><fmt:message key="com_intalio_bpms_workflow_taskHolder_priority"/></strong></td>
	            </tr>
	            
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
		            		<a href="${taskFullURL}" target="taskform"  >${taskHolder.task.state.name}</a>
		            		</td>
		            		<td>
		            			<a href="${taskFullURL}" target="taskform"  >${taskHolder.task.description}</a>
		            		</td>
		            		<td>
		            			<a href="${taskFullURL}" target="taskform"  >${taskHolder.task.creationDate}</a>
		            		</td>
							<td>
							<a href="${taskFullURL}" target="taskform"  >${taskHolder.task.deadline}</a>
							</td>
							<td>
							<a href="${taskFullURL}" target="taskform"  >${taskHolder.task.priority}</a>
							</td>
		            	</tr>
		        	</c:forEach>
	        </table>
	       </div>
	        <!-- Data e -->
	        <br />
			
			</div>
			<div class="notvisibletab" id="tab3">
			<!-- Third Level Header b -->
	        <br/>
	        <!-- Third Level Header e -->
	        <div id="notificationdiv">
		        <table width="80%"  cellspacing="0" cellpadding="0" id="properties_content">
		            <tr id="headertr">
		              <td width="60%"><strong><fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/></strong></td>
		              <td width="30%"><strong><fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/></strong></td>
				  	  <td width="10%"><strong><fmt:message key="com_intalio_bpms_workflow_taskHolder_priority"/></strong></td>
		            </tr>
		            
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
		        </table>
	        </div>
	        <!-- Data e -->
	        <br />
			
			</div>
			<div class="notvisibletab" id="tab2">
				<!-- Third Level Header b -->
		        <br/>
	        <div id="processdiv">
		        <table width="80%"  cellspacing="0" cellpadding="0" id="properties_content">
		            <tr id="headertr">
		              <td width="65%"><strong><fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/></strong></td>
		              <td width="35%"><strong><fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/></strong></td>
		            </tr>
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
			            			<a href="${taskFullURL}" target="taskform"  >${taskHolder.task.description}</a>
			            		</td>
			            		<td>
			            			<a href="${taskFullURL}" target="taskform"  >${taskHolder.task.creationDate}</a>
			            		</td>
			            	</tr>
			            </c:forEach>
		        </table>
		    </div>
		        <br/>
			</div>
	  </div>				  
		<!-- tab container e-->				  
		<br />

    <iframe name="taskform" width="100%" height="500">
      <fmt:message key="com_intalio_bpms_not_suport_frame_msg"/>
    </iframe>
</custom:workflowBody>
