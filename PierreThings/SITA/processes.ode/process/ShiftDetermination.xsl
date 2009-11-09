<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" />
	
	<xsl:template match="fmr:FMR" xmlns:fmr="http://www.example.org/fmr">
		<xsl:element name="TAdates" xmlns="http://www.intalio.com/gi/forms/TAmanagement.gi">
			<xsl:element name="startDateTime">
				<xsl:choose>
					<xsl:when test="fmr:ATA!=''"><xsl:value-of select="fmr:ATA" /></xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="fmr:ETA!=''"><xsl:value-of select="fmr:ETA" /></xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="fmr:STA!=''"><xsl:value-of select="fmr:STA" /></xsl:when>
									<xsl:otherwise>2008-01-01T00:00:00</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
			<xsl:element name="endDateTime">
				<xsl:choose>
					<xsl:when test="fmr:ATD!=''"><xsl:value-of select="fmr:ATD" /></xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="fmr:ETD!=''"><xsl:value-of select="fmr:ETD" /></xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="fmr:STD!=''"><xsl:value-of select="fmr:STD" /></xsl:when>
									<xsl:otherwise>2100-01-01T00:00:00</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
