<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="text" omit-xml-declaration="yes" />

	<xsl:template match="tms:getAvailableTasksWithInputOutputResponse"
		xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
		xmlns:gi="http://www.intalio.com/gi/forms/TAmanagement.gi">
		<xsl:value-of select="tms:task/tms:output/gi:FormModel/gi:Inspection/gi:flightStatus" />
	</xsl:template>

</xsl:stylesheet>
