<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
	<xsl:output method="text" omit-xml-declaration="yes" />
	
	<xsl:template match="fmr:FMR" xmlns:fmr="http://www.example.org/fmr">
		<xsl:choose>
			<xsl:when test="fmr:STD!='' and fmr:DepartureFlightNumber!='' and fmr:Aircraft!=''">false</xsl:when>
			<xsl:otherwise>true</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
