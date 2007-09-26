<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<%@ attribute name="action" required="false" %>
<%@ attribute name="icon" required="false" %>
<%--@ attribute name="checkSelection" required="false" type="java.lang.Boolean"--%>
<%@ attribute name="selectionType" required="false" %>
<%@ attribute name="onClick" required="false" %>
<%@ attribute name="disabled" required="false" %>

<c:if test="${!empty selectionType}">
	<c:choose>
		<c:when test="${selectionType=='one'}">
			<c:set var="checkFunction" value="isOneSelected"/>
		</c:when>
		<c:otherwise>
			<c:set var="checkFunction" value="isAnySelected"/>
		</c:otherwise>
	</c:choose>
</c:if>
<c:choose>
	<c:when test="${!empty action}">
		<c:set var="function" value="submitAction('${action}')"/>
	</c:when>
	<c:otherwise>
		<c:set var="function" value="alert('error')"/>
		<c:if test="${!empty onClick}">
			<c:set var="function" value="${onClick}"/>
		</c:if>
	</c:otherwise>
</c:choose>
<c:set var="check" >
		<c:if test="${!empty checkFunction}" >if(${checkFunction}('selectedID')) </c:if>
</c:set>
<input type="button" <c:if test="${!empty disabled && disabled}">disabled="disabled"</c:if> onclick="${check}${function};" value="<jsp:doBody/>"/>