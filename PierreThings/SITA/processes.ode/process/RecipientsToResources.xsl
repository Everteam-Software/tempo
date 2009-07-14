<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" />

	<xsl:template match="hht:hht-ta-update" xmlns:hht="http://www.example.org/hht">
		<xsl:element name="resources" xmlns="http://www.example.org/TAdata">
			<xsl:for-each select="hht:recipients/hht:recipient"><xsl:value-of select="@profile" /><xsl:if test = "not(position()=last())" >, </xsl:if></xsl:for-each>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
