<%--
	Copyright (c) 2005-2008 Intalio inc.

	All rights reserved. This program and the accompanying materials
	are made available under the terms of the Eclipse Public License v1.0
	which accompanies this distribution, and is available at
	http://www.eclipse.org/legal/epl-v10.html

	Contributors:
	Intalio inc. - initial API and implementation
--%>
<form id="form" name="form" method="POST" style="display:inline;">
	<input type="hidden" id="actionName" name="actionName" value=""/>
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<tr>
			<td width="10"><img src="images/spacer.gif" alt="" height="23" width="10"/></td>
			<td width="200" valign="bottom"><img src="images/logo.gif" alt="" height="29" width="200"/></td>
			<td valign="bottom" align="center"/>
			<td align="right" valign="bottom">&nbsp;
				<table cellpadding="0" cellspacing="0" height="30%">
					<tr>
						<td>
							<div id="message"/>
						</td>
						<td align="right" valign="bottom">
							<table width="100%" border="0" cellpadding="0" cellspacing="0" height="90%">
								<tr>
									<td>
										<a href="ical" title="iCalendar export"><img width="20" height="20" border="0" src="images/ical.jpg"/></a>
										<a href="/feeds/atom/tasks?token=${participantToken}" title="Personal Tasks Feed"><img width="20" height="20" border="0" src="images/rss_orange.png"/></a>
										<a href="/feeds/atom/processes?token=${participantToken}" title="Personal Process Feed"><img width="20" height="20" border="0" src="images/rss_blue.png"/></a>
									</td>
									<td class="menuItemSeparator"><img src="images/spacer.gif" width="10" alt="" height="30"/></td>																
									<td ><img src="images/curent_user.gif" width="20" height="20" title="Curent user" alt="Curent user" style="vertical-align: bottom;" border="0"/>${currentUser}</td>
									<td class="menuItemSeparator"><img src="images/spacer.gif" width="10" alt="" height="30"/></td>																
									<td> <a href="javascript:submitActionToURL('login.htm','logOut')" class="mainMenuItem" ><img border="0px" src="https://www.scs.fsu.edu/twiki/pub/TechHelp/EGroupWare/logout_icon.png" height="30px" width="30px"/></a> </td>								
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</form>
