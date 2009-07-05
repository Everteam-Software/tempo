<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" />
	
	<xsl:param name="fieldName" />

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

	<xsl:template match="hht:hht-ta-update" xmlns:hht="http://www.example.org/hht">
		<xsl:element name="{$fieldName}" xmlns="http://www.example.org/TAdata">
			<xsl:call-template name="timeFromDateTime">
				<xsl:with-param name="dateTime"><xsl:value-of select="@datetime" /></xsl:with-param>
			</xsl:call-template>
		</xsl:element>
	</xsl:template>
	
	</xsl:stylesheet>

