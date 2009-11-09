<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
	<xsl:output method="text" omit-xml-declaration="yes" />
	
	<xsl:template match="fmr:FMR" xmlns:fmr="http://www.example.org/fmr">
		<xsl:choose>
			<xsl:when test="fmr:ATA!=''">
				<xsl:choose>
					<xsl:when test="xs:dateTime(fmr:ATA) &gt; (current-dateTime() + xs:dayTimeDuration('PT12H'))">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="fmr:ETA!=''">
						<xsl:choose>
							<xsl:when test="xs:dateTime(fmr:ETA) &gt; (current-dateTime() + xs:dayTimeDuration('PT12H'))">true</xsl:when>
							<xsl:otherwise>false</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="fmr:STA!=''">
								<xsl:choose>
									<xsl:when test="xs:dateTime(fmr:STA) &gt; (current-dateTime() + xs:dayTimeDuration('PT12H'))">true</xsl:when>
									<xsl:otherwise>false</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>false</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
