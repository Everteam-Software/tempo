<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

<%@ attribute name="pagingStart" required="true" type="java.lang.Integer" %>
<%@ attribute name="pagingLength" required="true" type="java.lang.Integer" %>
<%@ attribute name="pageCount" required="true" type="java.lang.Integer" %>
<%@ attribute name="startTime" required="true" type="java.lang.String" %>
<%@ attribute name="endTime" required="true" type="java.lang.String" %>


<%-- Timeline --%>
<table class="time-line2" width="100%" style="height:7px" cellpadding="0"  cellspacing="0" >
<tr>
<td width="17">
<c:choose>
<c:when test="${pagingStart>0}">
	<a href="#" onclick="timelineSubmit('${pagingStart-pagingLength}')"><img src="images/arr_left_blue_indent.gif" width="17" height="13" border="0"></a>		
</c:when>
<c:otherwise>
	<img src="images/arr_left_blue_indent.gif" width="17" height="13" border="0">
</c:otherwise>
</c:choose>
</td>

<c:choose>
<c:when test="${pageCount==0}">
		<td id="curentScrollCell"><img src="images/spacer.gif" width="10" height="10" alt="" /></td>
</c:when>
<c:otherwise>
<c:forEach begin="${0}" end="${pageCount-1}" varStatus="vs">	
	<c:choose>	
	<c:when test="${pagingStart/pagingLength==vs.index}">
		<td id="curentScrollCell" onclick="timelineSubmit('${vs.index*pagingLength}')"><img src="images/spacer.gif" width="10" height="10" alt="" /></td>
	</c:when>
	<c:otherwise>
		<td onclick="timelineSubmit('${vs.index*pagingLength}')"><img src="images/spacer.gif" width="10" height="10" alt="" /></td>
	</c:otherwise>
	</c:choose>
</c:forEach>
</c:otherwise>
</c:choose>


<td width="17">
<c:choose>
	<c:when test="${pagingStart + pagingLength < pageCount * pagingLength}">
	<a href="#" onclick="timelineSubmit('${pagingStart+pagingLength}')"><img src="images/arr_right_blue_indent.gif" width="17" height="13" border="0"></a>			
	</c:when>
	<c:otherwise>
	<img src="images/arr_right_blue_indent.gif" width="17" height="13" border="0">
	</c:otherwise>
</c:choose>
</td>
</tr>
</table>
<input type="hidden" id="pagingStart" name="pagingStart" />
<input type="hidden" id="itemsCount" name="itemsCount" />
<table width="100%" cellpadding="2" cellspacing="2" >
<tr><td>
<c:choose>
<c:when test="${pagingStart>0}">
	<a href="#" onclick="timelineSubmit('${pagingStart-pagingLength}')">${startTime}</a>		
</c:when>
<c:otherwise>
	${startTime}
</c:otherwise>
</c:choose>
</td>

<td align="center">
<c:choose>
<c:when test="${pagingLength==100}">
<strong>100</strong>
</c:when>
<c:otherwise>
<a href="javascript:timelineSetPageSize('100')">100</a>
</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${pagingLength==250}">
<strong>250</strong>
</c:when>
<c:otherwise>
<a href="javascript:timelineSetPageSize('250')">250</a>
</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${pagingLength==500}">
<strong>500</strong>
</c:when>
<c:otherwise>
<a href="javascript:timelineSetPageSize('500')">500</a>
</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${pagingLength==750}">
<strong>750</strong>
</c:when>
<c:otherwise>
<a href="javascript:timelineSetPageSize('750')">750</a>
</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${pagingLength==1000}">
<strong>1000</strong>
</c:when>
<c:otherwise>
<a href="javascript:timelineSetPageSize('1000')">1000</a>
</c:otherwise>
</c:choose>


</td>
<td align="right">
<c:choose>
	<c:when test="${pagingStart + pagingLength < pageCount * pagingLength}">
	<a href="#" onclick="timelineSubmit('${pagingStart+pagingLength}')">${endTime}</a>			
	</c:when>
	<c:otherwise>
	${endTime}
	</c:otherwise>
</c:choose>
</td>
</tr>
</table>
<%-- Timeline --%>
