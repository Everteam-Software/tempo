<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:if test="${!empty errors}">
	<table width="100%" class="errorlist"><tr><td valign="top">
		<div style="overflow:auto; height:100px;">
		<c:forEach items="${errors}" var="error">
		<!--  error item b-->
			<div class="errorListItem" <c:if test="${!empty error.details}">onClick="opencloserrormessage(this);"</c:if> >
				<div id="errorListItemTitle">
					<fmt:message key="com_intalio_errors"/><c:out value="${error.formattedMessage}"/>
				</div>
				<div id="errorListItemText" style="display:none">
					<c:out value="${error.details}"/>
				</div>
			</div>
		<!--  error item e-->
		</c:forEach>
		</div>
	</td></tr></table>
</c:if>