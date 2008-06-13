<%--
 Copyright (c) 2005-2006 Intalio inc.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html

 Contributors:
 Intalio inc. - initial API and implementation
--%>
    <%@ page contentType="text/html; charset=UTF-8"%>
	<link rel="shortcut icon" href="favicon.ico" type="image/x-icon" />
	<!-- TODO: (niko) REMOVE THIS ASAP -->
	<link rel="alternate" type="application/atom+xml" title="Personal Task feed" href="http://localhost:8080/feeds/atom/tasks?token=${participantToken}" />
	<link rel="alternate" type="application/atom+xml" title="Process feed" href="http://localhost:8080/feeds/atom/processes?token=${participantToken}" />
	<!-- TODO: (niko) REMOVE ME ASAP -->
	
	<table style="height: 40px;" border="0" cellpadding="0" cellspacing="0" width="100%">
	<tbody><tr>
	  <td width="10"><img src="images/spacer.gif" alt="" height="23" width="10"></td>
	  <td width="200" valign="bottom"><img src="images/logo.gif" alt="" height="29" width="200"></td>
	  <td valign="bottom" align="center">&nbsp;&nbsp;
		  <!-- Main menu b -->
			<%-- intalio:toolbar items="${toolBarItems}" selected="${selectedToolbarItem}"/--%>
		  <!-- Main menu e --></td>
	  <td align="right" valign="bottom">&nbsp;
		
		<table cellpadding="0" cellspacing="0">
			<tbody><tr>
			<td align="right" valign="bottom" width="310" >
	          <!-- user logout refresh b-->
				<table border="0" cellpadding="0" cellspacing="0" >
					<tr>
						<td >
						<img src="images/icons/curent_user.gif" title="Curent user" alt="Curent user" style="vertical-align: bottom;" border="0">
				            ${currentUser}
				            &nbsp;&nbsp;
						</td>
						<td class="menuItemSeparator"><img src="images/spacer.gif" width="1" alt="" height="30"></td>																
						<td> <a href="javascript:submitActionToURL('login.htm','logOut')" class="mainMenuItem" ><fmt:message key="com_intalio_bpms_workflow_pageHeader_logout"/></a> </td>								
					</tr>
				</table>
	            <!-- user logout refresh e-->				
				</td>
			</tr>
		</tbody></table></td>
	</tr>
	</tbody></table>
