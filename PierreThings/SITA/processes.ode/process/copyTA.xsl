<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" />

	<xsl:template match="tms:getAvailableTasksWithInputOutputResponse"
		xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
		xmlns:gi="http://www.intalio.com/gi/forms/TAmanagement.gi">
		<xsl:for-each select="tms:task/tms:output/gi:FormModel">
			<xsl:element name="FormModel" xmlns="http://www.intalio.com/gi/forms/TAmanagement.gi">
				<xsl:copy-of select="gi:Activity" />
				<xsl:copy-of select="gi:ArrivalDeparture" />
				<xsl:element name="Inspection">
					<xsl:element name="assigned"><xsl:value-of select="gi:Inspection/gi:assigned" /></xsl:element>
					<xsl:element name="flightStatus"><xsl:value-of select="gi:Inspection/gi:flightStatus" /></xsl:element>
					<xsl:element name="Stand"><xsl:value-of select="gi:Inspection/gi:Stand" /></xsl:element>
					<xsl:for-each select="gi:Inspection/gi:taTasks">
						<xsl:element name="taTasks">
							<xsl:element name="InspectionType"><xsl:value-of select="gi:InspectionType" /></xsl:element>
							<xsl:element name="MTstartDate"><xsl:value-of select="gi:MTstartDate" /></xsl:element>
							<xsl:element name="MTstartTime"><xsl:value-of select="gi:MTstartTime" /></xsl:element>
							<xsl:element name="MTendDate"><xsl:value-of select="gi:MTendDate" /></xsl:element>
							<xsl:element name="MTendTime"><xsl:value-of select="gi:MTendTime" /></xsl:element>
							<xsl:element name="Remarks"><xsl:value-of select="gi:Remarks" /></xsl:element>
						</xsl:element>
					</xsl:for-each>
					<xsl:element name="InspectionStatus"><xsl:value-of select="gi:Inspection/gi:InspectionStatus" /></xsl:element>
					<xsl:element name="resources">
						<xsl:if test="gi:Inspection/gi:resources!=''"><xsl:value-of select="gi:Inspection/gi:resources" /></xsl:if>
					</xsl:element>
					<xsl:for-each select="gi:Inspection/gi:RTR">
						<xsl:copy-of select="." />
					</xsl:for-each>
					<xsl:for-each select="gi:Inspection/gi:assignedCoord">
						<xsl:copy-of select="." />
					</xsl:for-each>
					<xsl:for-each select="gi:Inspection/gi:assignedMechanics">
						<xsl:copy-of select="." />
					</xsl:for-each>
					<xsl:for-each select="gi:Inspection/gi:assignedAvionics">
						<xsl:copy-of select="." />
					</xsl:for-each>
				</xsl:element>
				<xsl:copy-of select="gi:HIL" />
				<xsl:copy-of select="gi:DC" />
			</xsl:element>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
