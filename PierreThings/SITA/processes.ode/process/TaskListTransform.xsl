<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" />

	<xsl:template match="tms:getAvailableTasksWithInputOutputResponse"
		xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
		xmlns:gi="http://www.intalio.com/gi/forms/TAmanagement.gi">
		<xsl:element name="ta-list" namespace="http://www.example.org/ta">
			<xsl:for-each select="tms:task/tms:output/gi:FormModel">
				<xsl:element name="ta" namespace="http://www.example.org/ta">
					<xsl:attribute name="ta-id">
						<xsl:value-of select="gi:Activity/gi:AircraftID" />|<xsl:value-of select="gi:ArrivalDeparture/gi:ScheduledArrivalDate" />T<xsl:value-of select="substring(gi:ArrivalDeparture/gi:STA, 1, 8)" />Z|<xsl:value-of select="gi:ArrivalDeparture/gi:ArrivalFlightNumber" />
					</xsl:attribute>
					<xsl:attribute name="aircraft-id">
						<xsl:value-of select="gi:Activity/gi:AircraftID" />
					</xsl:attribute>
					<xsl:attribute name="scheduled-arrival-datetime">
						<xsl:value-of select="gi:ArrivalDeparture/gi:ScheduledArrivalDate" />T<xsl:value-of select="substring(gi:ArrivalDeparture/gi:STA, 1, 8)" />Z</xsl:attribute>
					<xsl:attribute name="scheduled-arrival-flight-number"> 
						<xsl:value-of select="gi:ArrivalDeparture/gi:ArrivalFlightNumber" />
					</xsl:attribute>
					<xsl:if test="year-from-date(gi:ArrivalDeparture/gi:EstimatedArrivalDate)!=1970">
						<xsl:attribute name="estimated-arrival-datetime"><xsl:value-of select="gi:ArrivalDeparture/gi:EstimatedArrivalDate" />T<xsl:value-of select="substring(gi:ArrivalDeparture/gi:ETA, 1, 8)" />Z</xsl:attribute>
					</xsl:if>
					<xsl:if test="year-from-date(gi:ArrivalDeparture/gi:ActualArrivalDate)!=1970">
						<xsl:attribute name="actual-arrival-datetime"><xsl:value-of select="gi:ArrivalDeparture/gi:ActualArrivalDate" />T<xsl:value-of select="substring(gi:ArrivalDeparture/gi:ATA, 1, 8)" />Z</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="scheduled-departure-datetime">
						<xsl:value-of select="gi:ArrivalDeparture/gi:ScheduledDepartureDate" />T<xsl:value-of select="substring(gi:ArrivalDeparture/gi:STD, 1, 8)" />Z</xsl:attribute>
					<xsl:attribute name="scheduled-departure-flight-number"> 
						<xsl:value-of select="gi:ArrivalDeparture/gi:DepartureFlightNumber" />
					</xsl:attribute>
					<xsl:if test="year-from-date(gi:ArrivalDeparture/gi:EstimatedDepartureDate)!=1970">
						<xsl:attribute name="estimated-departure-datetime"><xsl:value-of select="gi:ArrivalDeparture/gi:EstimatedDepartureDate" />T<xsl:value-of select="substring(gi:ArrivalDeparture/gi:ETD, 1, 8)" />Z</xsl:attribute>
					</xsl:if>
					<xsl:if test="year-from-date(gi:ArrivalDeparture/gi:ActualDepartureDate)!=1970">
						<xsl:attribute name="actual-departure-datetime"><xsl:value-of select="gi:ArrivalDeparture/gi:ActualDepartureDate" />T<xsl:value-of select="substring(gi:ArrivalDeparture/gi:ATD, 1, 8)" />Z</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="stand">
						<xsl:value-of select="gi:Inspection/gi:Stand" />
					</xsl:attribute>
					<xsl:attribute name="inspection-type"> 
						<xsl:value-of select="gi:Inspection/gi:InspectionType" />
					</xsl:attribute>
					<xsl:attribute name="comment"> 
						<xsl:value-of select="gi:DC/gi:Comments" />
					</xsl:attribute>
					<xsl:attribute name="expected-start-datetime">
						<xsl:value-of select="gi:ArrivalDeparture/gi:ScheduledArrivalDate" />T<xsl:value-of select="substring(gi:DC/gi:ExpectedStartTime, 1, 8)" />Z</xsl:attribute>
					<xsl:attribute name="expected-stop-datetime">
						<xsl:value-of select="gi:ArrivalDeparture/gi:ScheduledArrivalDate" />T<xsl:value-of select="substring(gi:DC/gi:ExpectedFinishTime, 1, 8)" />Z</xsl:attribute>
					<xsl:attribute name="expected-release-datetime">
						<xsl:value-of select="gi:ArrivalDeparture/gi:ScheduledArrivalDate" />T<xsl:value-of select="substring(gi:DC/gi:ExpectedReleaseTime, 1, 8)" />Z</xsl:attribute>
					<xsl:attribute name="late"> 
						<xsl:value-of select="gi:Activity/gi:late" />
					</xsl:attribute>
					<xsl:element name="roster">
						<xsl:for-each select="gi:Inspection/gi:assignedMechanics">
							<xsl:element name="mechanic">
								<xsl:attribute name="user-id"><xsl:value-of select="gi:assignedMechanicID" /></xsl:attribute>
								<xsl:attribute name="user-name"><xsl:value-of select="gi:assignedMechanicName" /></xsl:attribute>
								<xsl:attribute name="certified">
									<xsl:choose>
										<xsl:when test="gi:entitledToRelease='1'">true</xsl:when>
										<xsl:otherwise>false</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
							</xsl:element>
						</xsl:for-each>
						<xsl:for-each select="gi:Inspection/gi:assignedAvionics">
							<xsl:element name="avionic">
								<xsl:attribute name="user-id"><xsl:value-of select="gi:assignedAvionicID" /></xsl:attribute>
								<xsl:attribute name="user-name"><xsl:value-of select="gi:assignedAvionicName" /></xsl:attribute>
							</xsl:element>
						</xsl:for-each>
					</xsl:element>
					<xsl:element name="incidents">
						<xsl:for-each select="gi:Inspection/gi:RTR">
							<xsl:element name="rtr">
								<xsl:attribute name="rtr-id"><xsl:value-of select="gi:RTRid" /></xsl:attribute>
								<xsl:attribute name="limit-date"><xsl:if test="gi:RTRdate"><xsl:value-of select="gi:RTRdate" /></xsl:if></xsl:attribute>
								<xsl:attribute name="description"><xsl:if test="gi:RTRdescription"><xsl:value-of select="gi:RTRdescription" /></xsl:if></xsl:attribute>
								<xsl:attribute name="ad"><xsl:if test="gi:RTRad"><xsl:value-of select="gi:RTRad" /></xsl:if></xsl:attribute>
							</xsl:element>
						</xsl:for-each>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
