<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="intalio" uri="http://www.intalio.com/tagfiles"%>

<%@ attribute name="hideToolbar" required="false" type="java.lang.Boolean"%>

<%@ attribute name="selectedToolbarItem" required="false"%>

<%@ attribute name="dojoRequired" required="false" type="java.lang.Boolean"%>

<%@ attribute name="dateTimePickerRequired" required="false" type="java.lang.Boolean"%>

<%@ attribute name="subMenuHeader" required="false"%>

<%@ attribute name="toolbar" required="false"%>

<%@ attribute name="scripts" required="false"%>

<%@ attribute name="headerCell" required="false" type="java.lang.String"%>

<%@ attribute name="title" required="false"%>

<%@ attribute name="footer" required="false"%>


${toolbar}
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>
		${title}
	</title>
	<!-- link href="style/folding.css" rel="stylesheet" type="text/css" -->
	<!-- link rel="stylesheet" href="style/tree.css" type="text/css" --> 
	<link href="style.css" rel="stylesheet" type="text/css">
	${scripts}



</head>
<body>
<form id="form" name="form" method="POST" style="display:inline;">
	<input type="hidden" id="actionName" name="actionName" value=""/>
<table width="100%"  border="0" cellspacing="0" cellpadding="0" style="height: 100%; ">
	<tr>
		<td>
			${headerCell}
		</td>
	</tr>
	<tr>
		<td class="centerField" ><!-- Center b -->
			<table width="100%"  border="0" style="height:100%" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
						<!-- Error Messages list b-->
						<intalio:errors/>
						<!-- Error Messages list e-->
					</td>
				</tr>
				<tr>
					<td valign="top">
						<!-- Sub Header b -->
						<table class="subHeader" border="0" cellpadding="0" cellspacing="0" width="100%">
							<tbody>
								<tr>
									<td id="headerText">&nbsp;&nbsp;<span>${subMenuHeader}</span></td>
								</tr>
							</tbody>
						</table>
						<!-- Sub Header e -->
					</td>
				</tr>
				<tr>
					<td valign="top" style="height: 100%;"><!-- DATA b-->
						<table class="contentBox" cellpadding="0" cellspacing="0" style="height: 100%;">
							<tr>
								<td valign="top">
									<jsp:doBody/>
								</td>
							</tr>
						</table>
						<!-- DATA e-->
					</td>
				</tr>
			</table>
			<!-- Center e -->
		</td>
	</tr>
	  <tr>
	    <td style="height:36px; "><!-- Bottom b -->
	      <table width="100%"  border="0" cellpadding="0" cellspacing="0">
	        <tr>
	          <td height="2" style="background-color:#BDBDBD "><img src="images/spacer.gif" width="10" height="2" alt=""></td>
	        </tr>
	        <tr>
	          <td height="34" style="background-color:#EAEAEA ">
							${footer}
	          </td>
	        </tr>
	      </table>
	      <!-- Bottom e -->
	    </td>
	  </tr>
</table>
</form>
<!--hint b-->
<div class="hint" id="hintBox">
  <table cellpadding="0" cellspacing="0" border="0" id="firstRow" >
    <tr>
      <td id="hintMessageCell"><div id="hintMessage" style="width: 300px;">&nbsp;</div></td>
    </tr>
    <tr>
      <td style="height:12px" valign="top"><table cellspacing="0" cellpadding="0" width="100%" border="0">
      	<tr>
	        <td id="hintBottomLeftCell" width="20"><img src="images/spacer.gif" width="20" height="12" alt=""/></td>
            <td width="9" ><img src="images/hint_pointer.gif" alt="" width="9" height="12" /></td>
            <td id="hintBottomRightCell"><img src="images/spacer.gif" width="20" height="12" alt=""/></td>
        </tr>
        </table></td>
    </tr>
  </table>
</div>
<!--hint e-->
</body>
</html>
