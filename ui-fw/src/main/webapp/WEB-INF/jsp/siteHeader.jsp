<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<form id="form" name="form" method="POST" border="0" cellpadding="0" cellspacing="0" >
	<input type="hidden" id="actionName" name="actionName" value=""/>
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<td width="10"><img src="images/spacer.gif" alt="" height="1" width="10"/></td>
			<td width="200" valign="bottom"><img src="images/logo.gif" alt="" width="200"/></td>
			<td align="right">
				<table>
					<tr>
						<td><div id="message"/></td>
						<td class="menuItemSeparator"><img src="images/spacer.gif" width="30" alt="" height="1"/></td>																
						<td align="right" valign="bottom">
							<table width="100%" border="0" cellpadding="0" cellspacing="0">
								<tr>
									<%@ include file="icons.jsp" %>
									<td class="menuItemSeparator"><img src="images/spacer.gif" width="10" alt="" height="1"/></td>																
									<td id="user_logged"><img src="images/curent_user.gif" width="20" height="20" title="<fmt:message key="org_intalio_uifw_siteHeader_currentUser"/>" alt="org_intalio_uifw_siteHeader_currentUser" style="vertical-align: bottom;" border="0"/>${currentUser}</td>
									<td class="menuItemSeparator"><img src="images/spacer.gif" width="10" alt="" height="1"/></td>																
									<td> <a href="javascript:submitActionToURL('login.htm','logOut')" class="mainMenuItem" ><img border="0px" alt="<fmt:message key="org_intalio_uifw_siteHeader_tooltip_logout"/>" title="<fmt:message key="org_intalio_uifw_siteHeader_tooltip_logout"/>"  src="images/logout_icon.png" height="30px" width="30px"/></a> </td>								
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</form>
