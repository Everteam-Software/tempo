<form id="form" name="form" method="POST">
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
									<td>
										<a href="ical" title="iCalendar export"><img width="20" height="20" border="0" src="images/ical.jpg"/></a>
										<a href="atom/tasks?token=${participantToken}" title="Personal Tasks Feed"><img width="20" height="20" border="0" src="images/rss.orange.png"/></a>
										<a href="atom/processes?token=${participantToken}" title="Personal Process Feed"><img width="20" height="20" border="0" src="images/rss.green.png"/></a>
									</td>
									<td class="menuItemSeparator"><img src="images/spacer.gif" width="10" alt="" height="1"/></td>																
									<td id="user_logged"><img src="images/curent_user.gif" width="20" height="20" title="Curent user" alt="Curent user" style="vertical-align: bottom;" border="0"/>${currentUser}</td>
									<td class="menuItemSeparator"><img src="images/spacer.gif" width="10" alt="" height="1"/></td>																
									<td> <a href="javascript:submitActionToURL('login.htm','logOut')" class="mainMenuItem" ><img border="0px" src="images/logout_icon.png" height="30px" width="30px"/></a> </td>								
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</form>
