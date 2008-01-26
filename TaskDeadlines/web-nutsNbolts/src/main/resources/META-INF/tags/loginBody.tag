<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ attribute name="scripts" required="false" %>
<%@ attribute name="logoPath" required="false" %>
<%@ attribute name="subMenuHeader" required="false" %>
<%@ attribute name="pageTitle" required="false" %>
<%@ attribute name="footer" required="false" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<title>
		${pageTitle}
	</title>
	<!-- link href="style/folding.css" rel="stylesheet" type="text/css" -->
	<!-- link rel="stylesheet" href="style/tree.css" type="text/css" --> 
	<link href="style.css" rel="stylesheet" type="text/css">
	
	${scripts}
	<script src="script/folding.js" language="javascript" type="text/javascript"></script> 
<body>
<form id="form" name="form" method="POST" style="display:inline;" onSubmit="document.getElementById('actionName').value = 'logIn';">
	<input type="hidden" id="actionName" name="actionName" value="logIn"/>

<table width="100%"  border="0" cellspacing="0" cellpadding="0" style="height: 100%; ">
	<tr>
		<td style="height: 79px; " align="center">
			<!-- Site Header b -->
		          <img src="${logoPath}" width="200" height="29" alt="" />
			<!-- Site Header e -->
		</td>
	</tr>
	<tr>
	  	<td class="centerField" align="center"><!-- Center b -->
	    	<table width="100%"  border="0" style="height:100%" cellpadding="20" cellspacing="0">
	      	<tr>
	        	<td valign="top" align="center"><!-- DATA b-->
		            <!-- Sub Header b -->
		            <table width="353" border="0" cellspacing="0" cellpadding="0" class="subHeader_login">
		              <tr>
		                <td id="headerText">&nbsp;&nbsp;<span >${subMenuHeader}</span></td>
		              </tr>
		            </table>
		            <!-- Sub Header e -->
	            	<table class="contentBox_login" cellspacing="0" cellpadding="0" style="width:330px;">
		            	<tr>
			            	<td><jsp:doBody /></td>
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
	    <td style="height:36px; " align="center"><!-- Bottom b -->
	      <table width="100%"  border="0" cellpadding="0" cellspacing="0">
	        <tr>
	          <td height="2" style="background-color:#BDBDBD "><img src="images/spacer.gif" width="10" height="2" alt=""></td>
	        </tr>
	        <tr>
	          <td height="34" style="background-color:#EAEAEA " align="center">
	          	${footer}
	          </td>
	        </tr>
	      </table>
	      <!-- Bottom e -->
	    </td>
	  </tr>
</table>
</form>
</body>
</html>
