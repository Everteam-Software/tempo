<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

<%@ attribute name="items" required="true" type="java.util.Collection" %>
<%@ attribute name="selected" required="true" %>

<%-- Toolbar --%>
	<table border="0" cellspacing="0" cellpadding="0" width="300">
	<tr>
	<td class="menuItemSeparator"><img src="images/spacer.gif" width="1" alt="" height="30"></td>	
	<c:forEach var="item" items="${items}">
		<td>
		<c:choose>
			<c:when test="${item.id==selected}">
				<a href="${item.address}" class="mainMenuCurentItem">
					<fmt:message key="${item.messageKey}"/>
				</a>
			</c:when>
			<c:otherwise>
				<a href="${item.address}" class="mainMenuItem">
					<fmt:message key="${item.messageKey}"/>
				</a>
			</c:otherwise>
		</c:choose>
		</td>
		<td class="menuItemSeparator"><img src="images/spacer.gif" width="1" alt="" height="30"></td>
	</c:forEach>
	</tr>
	</table>
<%-- Toolbar --%>