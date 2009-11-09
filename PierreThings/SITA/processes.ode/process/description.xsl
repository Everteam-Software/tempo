<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="text" omit-xml-declaration="yes" />
	
	<xsl:template match="fmr:FMR" xmlns:fmr="http://www.example.org/fmr">
		<xsl:choose>
			<xsl:when test="starts-with(substring(fmr:Aircraft, string-length(fmr:Aircraft) - 2, string-length(fmr:Aircraft)), 'TO')">A-<xsl:value-of select="fmr:STA"/></xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="starts-with(substring(fmr:Aircraft, string-length(fmr:Aircraft) - 2, string-length(fmr:Aircraft)), 'T')">B-<xsl:value-of select="fmr:STA"/></xsl:when>
					<xsl:otherwise>C-<xsl:value-of select="fmr:STA"/></xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
