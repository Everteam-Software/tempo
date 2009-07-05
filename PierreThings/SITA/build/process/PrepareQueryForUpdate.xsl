<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" />

	<xsl:template name="timeFromDateTime">
		<xsl:param name="dateTime" />
		<xsl:choose>
			<xsl:when test="hours-from-dateTime($dateTime) &lt; 10">0<xsl:value-of select="hours-from-dateTime($dateTime)" /></xsl:when>
			<xsl:otherwise><xsl:value-of select="hours-from-dateTime($dateTime)" /></xsl:otherwise>							
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="minutes-from-dateTime($dateTime) &lt; 10">:0<xsl:value-of select="minutes-from-dateTime($dateTime)" /></xsl:when>
			<xsl:otherwise>:<xsl:value-of select="minutes-from-dateTime($dateTime)" /></xsl:otherwise>							
		</xsl:choose>:00</xsl:template>
	
	<xsl:template name="dateFromDateTime">
		<xsl:param name="dateTime" />
		<xsl:value-of select="year-from-dateTime($dateTime)" /><xsl:choose>
			<xsl:when test="month-from-dateTime($dateTime) &lt; 10">-0<xsl:value-of select="month-from-dateTime($dateTime)" /></xsl:when>
			<xsl:otherwise>-<xsl:value-of select="month-from-dateTime($dateTime)" /></xsl:otherwise>							
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="day-from-dateTime($dateTime) &lt; 10">-0<xsl:value-of select="day-from-dateTime($dateTime)" /></xsl:when>
			<xsl:otherwise>-<xsl:value-of select="day-from-dateTime($dateTime)" /></xsl:otherwise>							
		</xsl:choose>
	</xsl:template>

	<xsl:template match="hht:hht-ta-update" xmlns:hht="http://www.example.org/hht">
		<xsl:element name="subQuery" xmlns="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/">T._output like '%ScheduledArrivalDate_<xsl:call-template name="dateFromDateTime">
				<xsl:with-param name="dateTime">
					<xsl:value-of select="@scheduled-arrival-datetime" />
				</xsl:with-param>
			</xsl:call-template>%STA_<xsl:call-template name="timeFromDateTime">
				<xsl:with-param name="dateTime">
					<xsl:value-of select="@scheduled-arrival-datetime" />
				</xsl:with-param>
			</xsl:call-template>%ArrivalFlightNumber_<xsl:value-of select="@scheduled-arrival-flight-number" />%'</xsl:element>
	</xsl:template>
	
	</xsl:stylesheet>

