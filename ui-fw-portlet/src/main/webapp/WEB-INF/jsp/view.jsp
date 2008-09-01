<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="org.intalio.tempo.portlet.ApplicationState" %>
<%@ page session="false" %>

<portlet:defineObjects/>

<link href="/ui-fw-portlet/style/portlet.css" rel="stylesheet" type="text/css"/>
<link href="/ui-fw-portlet/style/custom-rows.css" rel="stylesheet" type="text/css"/>

	<script src="/ui-fw-portlet/script/adapter/ext/ext-base.js" type="text/javascript"></script>
	<script src="/ui-fw-portlet/script/ext-all.js" type="text/javascript"></script>
	<script src="/ui-fw-portlet/script/prototype.js" type="text/javascript"></script>
	<script src="/ui-fw-portlet/script/entry.js" type="text/javascript"></script>

<%@ include file="ui-fw-portlet.jsp" %>

	<table>
		<tr>
			<td>Task Type:</td>
			<td>
			<select id="taskType" size="1" onChange="_searchTask()">
			<option>Task</option>
			<option>PATask</option>
			<option>Notification</option>
			</select></td>
			<td>Description:</td>
			<td><input id="description" type="text" onChange="_searchTask()" /></td>
			<td><input type="submit" onclick="_searchTask()" value="Filter Tasks"/></td>
		</tr>
	</table>




   <!--dialog space-->
    <div id="entry-dlg" style="visibility:hidden;position:absolute;top:0px;">
    <div class="x-dlg-hd">Entry Dialog</div>
	    <div class="x-dlg-bd">
	    	<div id="entryUrl">
	    	</div>
	    </div>
    </div>
   <!--tab space-->
    <div style="width:600px;">
        <div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>
        <div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc">
            <div id="tabPanel">
                <div id="tab0" style={background-color:#ffffff;height:250}>
				    <!--tasks grid-->    
				    <div id="pnlGrid" style="width:570px;height:200px;">
				    <div id="grid"></div>
				    </div>
                </div>
                <div id="tab1" style={background-color:#ffffff;height:250}>
				    <!--process grid-->    
				    <div id="processPnl" style="width:570px;height:200px;">
				    <div id="processGrid"></div>
                <div>
            </div>
            
        </div></div></div>
        <div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>
    </div>
    <table>
	<tr><td>
	<form method="post" action="<portlet:actionURL>
				<portlet:param name="actionName" value="deleteAll"/>
			</portlet:actionURL>">
		<input type="submit" value="Delete all tasks"/>
	</form>
</td>
<td>
	<form method="post" action="<portlet:actionURL>
				<portlet:param name="actionName" value="deletePIPA"/>
			</portlet:actionURL>">
		<input type="submit" value="Delete PIPA tasks"/>
	</form>    
</td>
</tr>
</table>