<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="text" omit-xml-declaration="yes" />
	
	<xsl:template match="fmr:FMR" xmlns:fmr="http://www.example.org/fmr">
		<xsl:choose>
			<xsl:when test="starts-with(substring(@Aircraft, string-length(@Aircraft) - 2, string-length(@Aircraft)), 'TO')">A-<xsl:value-of select="@STA"/></xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="starts-with(substring(@Aircraft, string-length(@Aircraft) - 2, string-length(@Aircraft)), 'T')">B-<xsl:value-of select="@STA"/></xsl:when>
					<xsl:otherwise>C-<xsl:value-of select="@STA"/></xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
