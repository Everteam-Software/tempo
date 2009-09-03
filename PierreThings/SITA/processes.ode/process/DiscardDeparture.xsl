<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
	<xsl:output method="text" omit-xml-declaration="yes" />
	
	<xsl:template match="fmr:FMR" xmlns:fmr="http://www.example.org/fmr">
		<xsl:choose>
			<xsl:when test="@ATD!=''">
				<xsl:choose>
					<xsl:when test="xs:dateTime(@ATD) &lt; (current-dateTime() - xs:dayTimeDuration('PT2H'))">true</xsl:when>
					<xsl:otherwise>false</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="@ETD!=''">
						<xsl:choose>
							<xsl:when test="xs:dateTime(@ETD) &lt; (current-dateTime() - xs:dayTimeDuration('PT2H'))">true</xsl:when>
							<xsl:otherwise>false</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="@STD!=''">
								<xsl:choose>
									<xsl:when test="xs:dateTime(@STD) &lt; (current-dateTime() - xs:dayTimeDuration('PT2H'))">true</xsl:when>
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
