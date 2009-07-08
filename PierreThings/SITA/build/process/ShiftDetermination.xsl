<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" />
	
	<xsl:template match="fmr:FMR" xmlns:fmr="http://www.example.org/fmr">
		<xsl:element name="TAdates" xmlns="http://www.intalio.com/gi/forms/TAmanagement.gi">
			<xsl:element name="startDateTime">
				<xsl:choose>
					<xsl:when test="@ATA!=''"><xsl:value-of select="@ATA" /></xsl:when>
					<xsl:otherwise><xsl:value-of select="@STA" /></xsl:otherwise>
				</xsl:choose>
			</xsl:element>
			<xsl:element name="endDateTime">
				<xsl:choose>
					<xsl:when test="@ATD!=''"><xsl:value-of select="@ATD" /></xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="@STD!=''"><xsl:value-of select="@STD" /></xsl:when>
							<xsl:otherwise>2100-01-01T00:00:00</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
