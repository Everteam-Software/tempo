<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
	<xsl:output method="text" omit-xml-declaration="yes" />
	
	<xsl:template match="fmr:FMR" xmlns:fmr="http://www.example.org/fmr">
CreationTime="<xsl:value-of select="@CreationTime" />" Aircraft="<xsl:value-of select="@Aircraft" />" STA="<xsl:value-of select="@STA" />" ArrivalFlightNumber="<xsl:value-of select="@ArrivalFlightNumber" />" ATA="<xsl:value-of select="@ATA" />" STD="<xsl:value-of select="@STD" />" ATD="<xsl:value-of select="@ATD" />" ETA="<xsl:value-of select="@ETA" />" ETD="<xsl:value-of select="@ETD" />"  Stand="<xsl:value-of select="@Stand" />" InspectionType="<xsl:value-of select="@InspectionType" />" DepartureFlightNumber="<xsl:value-of select="@DepartureFlightNumber" />" Rtr-id="<xsl:value-of select="@Rtr-id" />" 
	</xsl:template>
</xsl:stylesheet>