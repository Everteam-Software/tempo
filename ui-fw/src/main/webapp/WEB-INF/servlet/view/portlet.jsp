<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="org.intalio.tempo.web.ApplicationState" %>

<portlet:defineObjects/>

	<script src="/ui-fw/script/portlet/adapter/ext/ui-fw.js" type="text/javascript"></script>
    <script src="/ui-fw/script/portlet/adapter/ext/ext-base.js" type="text/javascript"></script>
      
	<script src="/ui-fw/script/portlet/ext-all.js" type="text/javascript"></script>
	<script src="/ui-fw/script/portlet/prototype.js" type="text/javascript"></script>
	<script src="/ui-fw/script/portlet/entry.js" type="text/javascript"></script>

    <link href="/ui-fw/style/portlet.css" rel="stylesheet" type="text/css"/>
    <link href="/ui-fw/style/custom-rows.css" rel="stylesheet" type="text/css"/>

    <%@ include file="ui-fw-portlet.jsp" %>

	<table>
		<tr>
			<td>Task Type:
			<select id="taskType" size="1" onChange="_searchTask()">
			<option>Task</option>
			<option>PATask</option>
			<option>Notification</option>
			</select>
			</td>
			<td>Description:
			<input id="description" type="text" onChange="_searchTask()" />
			<input type="submit" onclick="_searchTask()" value="Filter Tasks"/>
		    </td>
			<td>
			<%@ include file="../../jsp/icons.jsp" %>
     		</td>
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
    <div style="width:100%">
        <div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>
        <div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc">
            <div id="tabPanel">
                <div id="tab0" style="width:100%;background-color:#ffffff;height:250">
				    <!--tasks grid-->    
				    <div id="pnlGrid" style="width:100%;height:200px;">
				    <div id="grid"></div>
				    </div>
                </div>
                <div id="tab1" style="width:100%;background-color:#ffffff;height:250">
				    <!--process grid-->    
				    <div id="processPnl" style="width:100%;height:200px;">
				    <div id="processGrid"></div>
                <div>
            </div>
            
        </div></div></div>
        <div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>
    </div></div></div>

    <!-- <table>
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
</table> -->