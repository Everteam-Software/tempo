<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
	<xsl:output method="xml" omit-xml-declaration="yes" />
	
	<xsl:template name="timeFromDateTime">
		<xsl:param name="dateTime" />
		<xsl:choose>
			<xsl:when test="hours-from-dateTime($dateTime) &lt; 10">0<xsl:value-of select="hours-from-dateTime($dateTime)" /></xsl:when>
			<xsl:otherwise><xsl:value-of select="hours-from-dateTime($dateTime)" /></xsl:otherwise>							
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="minutes-from-dateTime($dateTime) &lt; 10">:0<xsl:value-of select="minutes-from-dateTime($dateTime)" /></xsl:when>
			<xsl:otherwise>:<xsl:value-of select="minutes-from-dateTime($dateTime)" /></xsl:otherwise>							
		</xsl:choose>:00</xsl:template>
	
	<xsl:template name="dateFromDateTime">
		<xsl:param name="dateTime" />
		<xsl:value-of select="year-from-dateTime($dateTime)" /><xsl:choose>
			<xsl:when test="month-from-dateTime($dateTime) &lt; 10">-0<xsl:value-of select="month-from-dateTime($dateTime)" /></xsl:when>
			<xsl:otherwise>-<xsl:value-of select="month-from-dateTime($dateTime)" /></xsl:otherwise>							
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="day-from-dateTime($dateTime) &lt; 10">-0<xsl:value-of select="day-from-dateTime($dateTime)" /></xsl:when>
			<xsl:otherwise>-<xsl:value-of select="day-from-dateTime($dateTime)" /></xsl:otherwise>							
		</xsl:choose>
	</xsl:template>
	
	<!-- <xsl:template name="tokenize">
 		<xsl:param name="string" />
  		<xsl:choose>
   			<xsl:when test="contains($string, ';')">
   				<xsl:element name="RTR" xmlns="http://www.intalio.com/gi/forms/TAmanagement.gi">
					<xsl:element name="RTRid">
						<xsl:value-of select="substring-before($string, ';')" />
					</xsl:element>
					<xsl:element name="RTRdescription"></xsl:element>
					<xsl:element name="RTRstatus">open</xsl:element>
					<xsl:element name="RTRad">false</xsl:element>
				</xsl:element>
     				<xsl:call-template name="tokenize">
       					<xsl:with-param name="string" select="substring-after($string, ';')" />
     				</xsl:call-template>
   			</xsl:when>
   			<xsl:otherwise>
       			<xsl:element name="RTR" xmlns="http://www.intalio.com/gi/forms/TAmanagement.gi">
					<xsl:element name="RTRid">
						<xsl:value-of select="$string" />
					</xsl:element>
					<xsl:element name="RTRdescription"></xsl:element>
					<xsl:element name="RTRstatus">open</xsl:element>
					<xsl:element name="RTRad">false</xsl:element>
				</xsl:element>
   			</xsl:otherwise>
  		</xsl:choose>
	</xsl:template> -->
	
	<xsl:template match="fmr:FMR" xmlns:fmr="http://www.example.org/fmr">
		<xsl:element name="taskInput"
			xmlns:tns="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109">
			<xsl:element name="FormModel"
				xmlns="http://www.intalio.com/gi/forms/TAmanagement.gi"
				xmlns:tns="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109">
				<xsl:element name="Activity">
					<xsl:element name="AircraftID">
						<xsl:value-of select="substring(fmr:Aircraft, string-length(fmr:Aircraft) - 2, string-length(fmr:Aircraft))" />
					</xsl:element>
					<xsl:element name="startTime"></xsl:element>
					<xsl:element name="finishTime"></xsl:element>
					<xsl:element name="releaseTime"></xsl:element>
					<xsl:element name="late">0</xsl:element>
					<xsl:element name="update">0</xsl:element>
				</xsl:element>
				<xsl:element name="ArrivalDeparture">
					<xsl:if test="fmr:STA!=''">
						<xsl:element name="ScheduledArrivalDate">
							<xsl:call-template name="dateFromDateTime">
								<xsl:with-param name="dateTime" select="fmr:STA" />
							</xsl:call-template>
						</xsl:element>
						<xsl:element name="STA">
							<xsl:call-template name="timeFromDateTime">
								<xsl:with-param name="dateTime" select="fmr:STA" />
							</xsl:call-template>
						</xsl:element>
					</xsl:if>
					<xsl:if test="fmr:STD!=''">
						<xsl:element name="ScheduledDepartureDate">
							<xsl:call-template name="dateFromDateTime">
								<xsl:with-param name="dateTime" select="fmr:STD" />
							</xsl:call-template>
						</xsl:element>	
						<xsl:element name="STD">
							<xsl:call-template name="timeFromDateTime">
								<xsl:with-param name="dateTime" select="fmr:STD" />
							</xsl:call-template>		
						</xsl:element>
					</xsl:if>
	                <xsl:if test="fmr:ETA!=''">
						<xsl:element name="EstimatedArrivalDate">
							<xsl:call-template name="dateFromDateTime">
								<xsl:with-param name="dateTime" select="fmr:ETA" />
							</xsl:call-template>
						</xsl:element>
						<xsl:element name="ETA">
							<xsl:call-template name="timeFromDateTime">
								<xsl:with-param name="dateTime" select="fmr:ETA" />
							</xsl:call-template>
						</xsl:element>
					</xsl:if>
					<xsl:if test="fmr:ETD!=''">
						<xsl:element name="EstimatedDepartureDate">
							<xsl:call-template name="dateFromDateTime">
								<xsl:with-param name="dateTime" select="fmr:ETD" />
							</xsl:call-template>
						</xsl:element>
						<xsl:element name="ETD">
							<xsl:call-template name="timeFromDateTime">
								<xsl:with-param name="dateTime" select="fmr:ETD" />
							</xsl:call-template>
						</xsl:element>
					</xsl:if>
					<xsl:if test="fmr:ATA!=''">
						<xsl:element name="ActualArrivalDate">
							<xsl:call-template name="dateFromDateTime">
								<xsl:with-param name="dateTime" select="fmr:ATA" />
							</xsl:call-template>
						</xsl:element>
						<xsl:element name="ATA">
							<xsl:call-template name="timeFromDateTime">
									<xsl:with-param name="dateTime" select="fmr:ATA" />
							</xsl:call-template>
						</xsl:element>
					</xsl:if>
					<xsl:if test="fmr:ATD!=''">
						<xsl:element name="ActualDepartureDate">
							<xsl:call-template name="dateFromDateTime">
								<xsl:with-param name="dateTime" select="fmr:ATD" />
							</xsl:call-template>
						</xsl:element>
						<xsl:element name="ATD">
							<xsl:call-template name="timeFromDateTime">
								<xsl:with-param name="dateTime" select="fmr:ATD" />
							</xsl:call-template>						
						</xsl:element>
					</xsl:if>
					<xsl:element name="ArrivalFlightNumber">
						<xsl:if test="fmr:ArrivalFlightNumber"><xsl:value-of select="fmr:ArrivalFlightNumber" /></xsl:if>
					</xsl:element>
					<xsl:element name="DepartureFlightNumber">
						<xsl:if test="fmr:DepartureFlightNumber"><xsl:value-of select="fmr:DepartureFlightNumber" /></xsl:if>
					</xsl:element>
				</xsl:element>
				<xsl:element name="Inspection">
					<xsl:element name="assigned">no</xsl:element>
					<xsl:element name="flightStatus"><xsl:value-of select="fmr:FlightStatus" /></xsl:element>
					<xsl:element name="Stand"><xsl:value-of select="fmr:stand" /></xsl:element>
					<xsl:for-each select="fmr:MaintTask">
						<xsl:element name="taTasks">
							<xsl:element name="InspectionType">
								<xsl:choose>
									<xsl:when test="contains(fmr:InspectionType, '+')"><xsl:value-of select="substring-before(fmr:InspectionType, '+')" /><xsl:value-of select="substring-after(fmr:InspectionType, '+')" /></xsl:when>
									<xsl:otherwise><xsl:value-of select="fmr:InspectionType" /></xsl:otherwise>
								</xsl:choose>
							</xsl:element>
							<xsl:element name="MTstartDate">
								<xsl:call-template name="dateFromDateTime">
									<xsl:with-param name="dateTime" select="fmr:MaintStartDate" />
								</xsl:call-template>
							</xsl:element>
							<xsl:element name="MTstartTime">
								<xsl:call-template name="timeFromDateTime">
									<xsl:with-param name="dateTime" select="fmr:MaintStartDate" />
								</xsl:call-template>						
							</xsl:element>
							<xsl:element name="MTendDate">
								<xsl:call-template name="dateFromDateTime">
									<xsl:with-param name="dateTime" select="fmr:MaintEndtDate" />
								</xsl:call-template>
							</xsl:element>
							<xsl:element name="MTendTime">
								<xsl:call-template name="timeFromDateTime">
									<xsl:with-param name="dateTime" select="fmr:MaintEndtDate" />
								</xsl:call-template>						
							</xsl:element>
							<xsl:element name="Remarks"><xsl:value-of select="fmr:Remarks" /></xsl:element>
						</xsl:element>
					</xsl:for-each>
					<!-- <xsl:element name="InspectionType">
						<xsl:if test="@InspectionType!=''">
							<xsl:choose>
								<xsl:when test="contains(@InspectionType, '+')"><xsl:value-of select="substring-before(@InspectionType, '+')" /><xsl:value-of select="substring-after(@InspectionType, '+')" /></xsl:when>
								<xsl:otherwise><xsl:value-of select="@InspectionType" /></xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					</xsl:element> -->
					<xsl:element name="InspectionStatus">open</xsl:element>
					<xsl:element name="resources"></xsl:element>
					<xsl:element name="coordinator"></xsl:element>
					<xsl:for-each select="MaintTask">
						<xsl:for-each select="RTR_ID">
							<xsl:element name="RTR" xmlns="http://www.intalio.com/gi/forms/TAmanagement.gi">
								<xsl:element name="RTRid"><xsl:value-of select="." /></xsl:element>
								<xsl:element name="RTRdescription"></xsl:element>
								<xsl:element name="RTRstatus">open</xsl:element>
								<xsl:element name="RTRad">false</xsl:element>
							</xsl:element>
						</xsl:for-each>
					</xsl:for-each>
					<!-- <xsl:if test="@Rtr-id!=''">
						<xsl:call-template name="tokenize">
							<xsl:with-param name="string" select="@Rtr-id"  />
						</xsl:call-template>
					</xsl:if> -->
				</xsl:element>
				<xsl:element name="DC">
					<xsl:element name="ExpectedStartTime">
						<xsl:call-template name="timeFromDateTime">
							<xsl:with-param name="dateTime"><xsl:value-of select="xs:dateTime('2000-01-01T00:00:00') + xs:dayTimeDuration('PT6M')" /></xsl:with-param>
						</xsl:call-template>
					</xsl:element>
					<xsl:element name="ExpectedFinishTime">		
						<xsl:call-template name="timeFromDateTime">
							<xsl:with-param name="dateTime"><xsl:value-of select="xs:dateTime('2000-01-01T00:00:00') + xs:dayTimeDuration('PT20M')" /></xsl:with-param>
						</xsl:call-template>
					</xsl:element>
					<xsl:element name="ExpectedReleaseTime">
						<xsl:call-template name="timeFromDateTime">
							<xsl:with-param name="dateTime"><xsl:value-of select="xs:dateTime('2000-01-01T00:00:00') + xs:dayTimeDuration('PT20M')" /></xsl:with-param>
						</xsl:call-template>
					</xsl:element>
					<xsl:element name="CompassInspectionType"></xsl:element>
					<xsl:element name="Comments"></xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
