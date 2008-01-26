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

<%@ taglib prefix="custom" tagdir="/WEB-INF/tags"%>

<c:set var="headerCell">
	<%@ include file="/WEB-INF/jsp/siteHeader.jsp"%>
</c:set>

<custom:workflowBody headerCell="${headerCell}">
	    <table id="tabPanel" width="100%" border="0" cellspacing="0" cellpadding="0" >
	      <tr>
	        <td id="ActiveTab" onClick="changetab(this,'tabContainer', 'tab1')">Tasks</td>
	        <td id="notActiveTab" onClick="changetab(this,'tabContainer','tab3')" >Notifications</td>
	        <td id="notActiveTab" onClick="changetab(this,'tabContainer','tab2')" >Processes</td>
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
	        <table width="100%" border="0" cellspacing="0" cellpadding="4" style="margin-left: 20px; " id="properties_content">
	            <tr>
	              <td width="10%"><strong>Task State</strong></td>
	              <td width="35%"><strong>Description</strong></td>
	              <td width="10%"><strong>Creation Date/Time</strong></td>
	              <td width="10%"><strong>Due Date/</strong></td>
	              <td width="10%"><strong>Priority</strong></td>
	            </tr>
	            <c:forEach items="${activityTasks}" var="taskHolder">
	            	<tr>
	            		<td>
	            			<a href="${taskHolder.formManagerURL}?id=${taskHolder.task.ID}&url=${taskHolder.task.formURL}&token=${participantToken}&user=${currentUser}" target="taskform"  >${taskHolder.task.state.name}</a>
	            		</td>
	            		<td>
	            			<a href="${taskHolder.formManagerURL}?id=${taskHolder.task.ID}&url=${taskHolder.task.formURL}&token=${participantToken}&user=${currentUser}" target="taskform"  >${taskHolder.task.description}</a>
	            		</td>
	            		<td>
	            			<a href="${taskHolder.formManagerURL}?id=${taskHolder.task.ID}&url=${taskHolder.task.formURL}&token=${participantToken}&user=${currentUser}" target="taskform"  >${taskHolder.task.creationDate}</a>
	            		</td>
	            		<td>
	            			<a href="${taskHolder.formManagerURL}?id=${taskHolder.task.ID}&url=${taskHolder.task.formURL}&token=${participantToken}&user=${currentUser}" target="taskform"  >${taskHolder.task.deadline}</a>
	            		</td>
	            		<td>
	            			<a href="${taskHolder.formManagerURL}?id=${taskHolder.task.ID}&url=${taskHolder.task.formURL}&token=${participantToken}&user=${currentUser}" target="taskform"  >${taskHolder.task.priority}</a>
	            		</td>
	            	</tr>
	        	</c:forEach>
	        </table>
	        <!-- Data e -->
	        <br />
			
			</div>
			<div class="notvisibletab" id="tab3">
			<!-- Third Level Header b -->
	        <br/>
	        <!-- Third Level Header e -->
	        <table width="600" border="0" cellspacing="0" cellpadding="4" style="margin-left: 20px; " id="properties_content">
	            <tr>
	              <td width="65%"><strong>Description</strong></td>
	              <td width="35%"><strong>Creation Date/Time</strong></td>
	            </tr>
	            <c:forEach items="${notifications}" var="taskHolder">
	            	<tr>
	            		<td>
	            			<a href="${taskHolder.formManagerURL}?id=${taskHolder.task.ID}&url=${taskHolder.task.formURL}&token=${participantToken}" target="taskform"  >${taskHolder.task.description}</a>
	            		</td>
	            		<td>
	            			<a href="${taskHolder.formManagerURL}?id=${taskHolder.task.ID}&url=${taskHolder.task.formURL}&token=${participantToken}" target="taskform"  >${taskHolder.task.creationDate}</a>
	            		</td>
	            	</tr>
	        	</c:forEach>
	        </table>
	        <!-- Data e -->
	        <br />
			
			</div>
			<div class="notvisibletab" id="tab2">
				<!-- Third Level Header b -->
	        <table width="600" border="0" cellspacing="0" cellpadding="4" style="margin-left: 20px; " id="properties_content">
	            <tr>
	              <td width="65%"><strong>Description</strong></td>
	              <td width="35%"><strong>Creation Date/Time</strong></td>
	            </tr>
	            <c:forEach items="${initTasks}" var="taskHolder">
	            	<tr>
	            		<td>
	            			<a href="${taskHolder.formManagerURL}?id=${taskHolder.task.ID}&url=${taskHolder.task.formURL}&token=${participantToken}&user=${currentUser}" target="taskform"  >${taskHolder.task.description}</a>
	            		</td>
	            		<td>
	            			<a href="${taskHolder.formManagerURL}?id=${taskHolder.task.ID}&url=${taskHolder.task.formURL}&token=${participantToken}&user=${currentUser}" target="taskform"  >${taskHolder.task.creationDate}</a>
	            		</td>
	            	</tr>
	            </c:forEach>
	        </table>
		        <br/>
			</div>
	  </div>				  
		<!-- tab container e-->				  
		<br />

    <iframe name="taskform" width="100%" height="500">
        If you see this text then your browser does not suport HTML internal frame.
        Normally - this is placeholder for the form you are to fulfil.
    </iframe>
</custom:workflowBody>
    