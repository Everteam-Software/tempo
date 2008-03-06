<%--
 Copyright (c) 2005-2006 Intalio inc.

 All rights reserved. This program and the accompanying materials
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html

 Contributors:
 Intalio inc. - initial API and implementation
--%>
<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" session="false"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="javax.portlet.PortletURL" %>

<portlet:defineObjects/>

	<link rel="shortcut icon" href='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "favicon.ico") %>' type="image/x-icon" />
	
	<table style="height: 40px;" border="0" cellpadding="0" cellspacing="0" width="100%">
	<tbody><tr>
	  <td width="10"><img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/images/spacer.gif") %>' alt="" height="23" width="10"></td>
	  <td width="200" valign="bottom"><img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/images/logo.gif") %>' alt="" height="29" width="200"></td>
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
						<img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/images/icons/curent_user.gif") %>' title="Curent user" alt="Curent user" style="vertical-align: bottom;" border="0">
				            ${currentUser}
				            &nbsp;&nbsp;
						</td>
						<td class="menuItemSeparator"><img src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/images/spacer.gif") %>' width="1" alt="" height="30"></td>																
						<td> <a href="javascript:submitActionToURL('login.htm','logOut')" class="mainMenuItem" ><spring:message code="com_intalio_bpms_workflow_pageHeader_logout"/></a> </td>								
					</tr>
				</table>
	            <!-- user logout refresh e-->				
				</td>
			</tr>
		</tbody></table></td>
	</tr>
	</tbody></table>
