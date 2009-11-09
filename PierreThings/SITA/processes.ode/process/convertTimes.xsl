<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" />
	
	<xsl:template match="fmr:FMR" xmlns:fmr="http://www.example.org/fmr">
		<xsl:for-each select="fmr:MaintTask">
			<xsl:element name="MaintTask" xmlns="http://www.example.org/fmr">
				<xsl:element name="InspectionType"><xsl:value-of select="fmr:InspectionType" /></xsl:element>
				<xsl:element name="MaintStartDate"><xsl:value-of select="fmr:MaintStartDate" /></xsl:element>
				<xsl:element name="MaintEndtDate"><xsl:value-of select="fmr:MaintEndtDate" /></xsl:element>
				<xsl:for-each select="fmr:RTR_ID">
					<xsl:element name="RTR_ID"><xsl:value-of select="." /></xsl:element>
				</xsl:for-each>
				<xsl:element name="Remarks"><xsl:value-of select="fmr:Remarks" /></xsl:element>		
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
