<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<div class="siteHeader">
<form id="form" name="form" method="POST" border="0" cellpadding="0" cellspacing="0" >
	<input type="hidden" id="actionName" name="actionName" value=""/>
	<img src="images/logo.gif" alt="" width="200"/>
	<div class="siteHeaderIcons">
	<%@ include file="icons.jsp" %>
    
	<img src="images/curent_user_icon.gif" title="<fmt:message key="org_intalio_uifw_siteHeader_currentUser"/>" alt="org_intalio_uifw_siteHeader_currentUser" border="0"></img>
	${currentUser}
	<a href="javascript:submitActionToURL('login.htm','logOut')">
		<img border="0px" alt="<fmt:message key="org_intalio_uifw_siteHeader_tooltip_logout"/>" title="<fmt:message key="org_intalio_uifw_siteHeader_tooltip_logout"/>"  src="images/logout_icon.png"/>
    </a>
	</div>
	</div>
</form>
</div>
