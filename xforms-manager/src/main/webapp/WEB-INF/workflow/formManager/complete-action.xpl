<?xml version="1.0" encoding="UTF-8"?>
<!--
* Copyright (c) 2005-2008 Intalio inc.
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
* Intalio inc. - initial API and implementation
-->
<!--
	Description: This page implements the Complete Task action.
	It calls the Complete action of the Task Manager Web Service exposed
	by the Task Manager process.
	Author: Jacques-Alexandre Gerber [gerber@intalio.com]
	Creation Date: October, 10th, 2005
	Copyright Intalio,Inc. All rights reserved.
-->
<p:config xmlns:p="http://www.orbeon.com/oxf/pipeline"
	xmlns:oxf="http://www.orbeon.com/oxf/processors"
	xmlns:xforms="http://www.w3.org/2002/xforms"
	xmlns:xhtml="http://www.w3.org/1999/xhtml"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:delegation="http://orbeon.org/oxf/xml/delegation"
	xmlns:b4p="http://www.intalio.com/bpms/workflow/ib4p_20051115"
	xmlns:tas="http://www.intalio.com/BPMS/Workflow/TaskAttachmentService/"
	xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
	xmlns:ev="http://www.w3.org/2001/xml-events">


	<!-- name must be instance because it's called by the page-flow  -->
	<p:param name="instance" type="input"/>
	<!-- name must be data because it's called by the page-flow  -->
	<p:param name="data" type="output"/>

	<!-- Start Upload -->
	<p:for-each href="#instance" select="//*[string-length(normalize-space(@upload-id)) > 0  and string-length(normalize-space(text())) > 0]"
		root="uploads" id="uploads-out">

		<!-- Prepare the SOAP body message -->
		<p:processor name="oxf:xslt">
			<p:input name="data" href="#instance"/>
			<p:input name="widget" href="current()"/>
			<p:input name="config">
				<delegation:execute service="tas" operation="addRequest" xsl:version="2.0">
					<tas:authCredentials>
						<tas:participantToken>
							<xsl:value-of select="/*:output/@participantToken"/>
						</tas:participantToken>
						<tas:authorizedUsers>
							<tas:user>
								<xsl:value-of select="/*:output/@user"/>
							</tas:user>
						</tas:authorizedUsers>
						<tas:authorizedRoles/>
					</tas:authCredentials>
					<tas:attachmentMetadata>
						<tas:mimeType><xsl:value-of select="doc('input:widget')/*/@mediatype"/></tas:mimeType>
						<tas:filename><xsl:value-of select="doc('input:widget')/*/@filename"/></tas:filename>
					</tas:attachmentMetadata>
					<tas:localFileURL>
						<xsl:value-of select="doc('input:widget')/*/text()"/>
					</tas:localFileURL>
				</delegation:execute>
			</p:input>
			<p:output name="data" id="addRequest"/>
		</p:processor>

		<!-- Call TAS.add -->
		<p:processor name="oxf:delegation">
			<p:input name="call" href="#addRequest"/>
			<p:input name="interface" href="oxf:/config/services.xml"/>
			<p:output name="data" id="addResponse"/>
		</p:processor>

		<!-- Call TMS WS for TMS.add operation to add new attachment -->
		<!-- Prepare the SOAP message body -->
		<p:processor name="oxf:xslt">
			<p:input name="data" href="#instance"/>
			<p:input name="widget" href="current()"/>
			<p:input name="tasResponse" href="#addResponse"/>
			<p:input name="config">
				<delegation:execute service="tms" operation="addAttachmentRequest" xsl:version="2.0">
					<tms:taskId>
						<xsl:value-of select="/*/@taskId"/>
					</tms:taskId>
					<tms:attachment>
						<tms:attachmentMetadata>
							<tms:mimeType><xsl:value-of select="doc('input:widget')/*/@mediatype"/></tms:mimeType>
							<tms:fileName><xsl:value-of select="doc('input:widget')/*/@filename"/></tms:fileName>
							<tms:widget><xsl:value-of select="doc('input:widget')/*/@widget"/></tms:widget>
						</tms:attachmentMetadata>
						<tms:payloadUrl><xsl:value-of select="doc('input:tasResponse')/tas:url"/></tms:payloadUrl>
					</tms:attachment>
					<tms:participantToken>
						<xsl:value-of select="/*/@participantToken"/>
					</tms:participantToken>
				</delegation:execute>
			</p:input>
			<p:output name="data" id="addAttachmentTMS"/>
		</p:processor>

		<!-- Call for TMS.addAttachment -->
		<p:processor name="oxf:delegation">
			<p:input name="interface" href="oxf:/config/services.xml"/>
			<p:input name="call" href="#addAttachmentTMS"/>
			<p:output name="data" id="dummy"/>
		</p:processor>

		<!-- Substitute new URL -->
		<p:processor name="oxf:xslt">
			<p:input name="data" href="#instance"/>
			<p:input name="url" href="#addResponse"/>
			<p:input name="widget" href="current()"/>
			<p:input name="dummy" href="#dummy"/>
			<p:input name="config">
				<xsl:stylesheet version="2.0">
					<xsl:variable name="url" select="doc('input:url')"/>
					<xsl:variable name="widget" select="doc('input:widget')"/>
					<xsl:variable name="dummy" select="doc('input:dummy')"/>
					<xsl:template match="/">
						<xsl:if test="count($dummy) > 0"/>
						<xsl:call-template name="replace-url">
							<xsl:with-param name="widget" select="//*[@upload-id = $widget/*/@upload-id]"/>
						</xsl:call-template>
					</xsl:template>

					<xsl:template name="replace-url">
						<xsl:param name="widget"/>
						<xsl:element name="{name($widget)}" namespace="{namespace-uri($widget)}">
							<xsl:copy-of select="$widget/@*"/>
							<xsl:value-of select="$url/tas:url"/>
						</xsl:element>
					</xsl:template>
				</xsl:stylesheet>
			</p:input>
			<p:output name="data" ref="uploads-out"/>
		</p:processor>

	</p:for-each>

	<!-- Replace upload widgets with updated URLs-->
	<p:processor name="oxf:xslt">
		<p:input name="data" href="#instance"/>
		<p:input name="uploads" href="#uploads-out"/>
		<p:input name="config">
			<xsl:stylesheet version="2.0">
				<xsl:variable name="uploads" select="doc('input:uploads')"/>
				<xsl:template match="//*[string-length(normalize-space(@upload-id)) > 0  and string-length(normalize-space(text())) > 0]">
					<xsl:variable name="upload-id" select="@upload-id"/>
					<xsl:copy-of select="$uploads//*[@upload-id = $upload-id]"/>
				</xsl:template>
				<!-- Identity copy -->
				<xsl:template match="*|@*|text()">
					<xsl:copy>
						<xsl:apply-templates select="*|@*|text()"/>
					</xsl:copy>
				</xsl:template>
			</xsl:stylesheet>
		</p:input>
		<p:output name="data" id="instance2"/>
	</p:processor>
	<!-- End Upload -->


	<p:processor name="oxf:xslt">
		<p:input name="data" href="#instance2"/>
		<p:input name="config">
			<xsl:stylesheet version="2.0">
				<xsl:import href="oxf:/oxf/xslt/utils/copy.xsl"/>

				<xsl:template match="/">
					<xsl:copy>
						<delegation:execute service="tmp" operation="completeTaskRequest" xsl:version="2.0">
							<b4p:taskMetaData>
								<b4p:taskId>
									<xsl:value-of select="/*:output/@taskId"/>
								</b4p:taskId>
							</b4p:taskMetaData>
							<b4p:participantToken>
								<xsl:value-of select="/*:output/@participantToken"/>
							</b4p:participantToken>
							<b4p:user>
								<xsl:value-of select="/*:output/@user"/>
							</b4p:user>
							<b4p:taskOutput>
								<xsl:apply-templates select="*"/>
							</b4p:taskOutput>
						</delegation:execute>
					</xsl:copy>
				</xsl:template>

				<xsl:template match="*:output">
					<xsl:copy>
						<xsl:apply-templates select="@*[name() != 'formUrl' and name() != 'taskType' and name() != 'saved'] | *"/>
					</xsl:copy>
				</xsl:template>

			</xsl:stylesheet>
		</p:input>
		<p:output name="data" id="completeTaskInput"/>
	</p:processor>

	<p:processor name="oxf:delegation">
		<p:input name="interface" href="oxf:/config/services.xml"/>
		<p:input name="call" href="#completeTaskInput"/>
		<p:output name="data" id="completeResponse"/>
	</p:processor>
	
	<p:processor name="oxf:exception-catcher">
        <p:input name="data" href="#completeResponse"/>
        <p:output name="data" id="ws_call_output"/>
    </p:processor>

    <p:choose href="#ws_call_output">
        <p:when test="/exceptions">
            <p:processor name="oxf:pipeline">
                <p:input name="config" href="exception-handler.xpl"/>
                <p:input name="data" href="#ws_call_output"/>
                <p:input name="ws-request" href="#completeTaskInput"/>
                <p:input name="header">
                    <b>Complete Task Failed</b>
                </p:input>
                <p:output name="data" ref="data"/>
            </p:processor>
        </p:when>
		<p:when test="string-length(normalize-space(//faultstring))">
			<p:processor name="oxf:identity">
				<p:input name="data" xmlns:xhtml="http://www.w3.org/1999/xhtml">
					<xhtml:html>
						<xhtml:body onLoad="parent.window.hideWindow();">
							<xhtml:center>Failed</xhtml:center>
						</xhtml:body>
					</xhtml:html>
				</p:input>
				<p:output name="data" ref="data"/>
			</p:processor>
		</p:when>
        <p:otherwise>
            <p:choose href="#completeResponse">
                <p:when test="string-length(normalize-space(//b4p:nextTaskId))">
                    <p:processor name="oxf:xslt">
                        <p:input name="data" href="#instance2"/>
                        <p:input name="completeResponse" href="#completeResponse"/>
                        <p:input name="config">
                            <task xsl:version="2.0">
                                <id>
                                    <xsl:value-of select="doc('input:completeResponse')//b4p:nextTaskId"/>
                                </id>
                                <url>
                                    <xsl:value-of select="doc('input:completeResponse')//b4p:nextTaskURL"/>
                                </url>
                                <token>
                                    <xsl:value-of select="/*:output/@participantToken"/>
                                </token>
                                <user>
                                    <xsl:value-of select="/*:output/@user"/>
                                </user>
                            </task>
                        </p:input>
                        <p:output name="data" id="nextShownTask"/>
                    </p:processor>
					<p:processor name="oxf:pipeline">
						<p:input name="config" href="act.xpl"/>
						<p:input name="data" href="#nextShownTask"/>
						<p:output name="data" ref="data"/>
					</p:processor>
                </p:when>
                <p:otherwise>
                    <p:processor name="oxf:identity">
						<p:input name="data" xmlns:xhtml="http://www.w3.org/1999/xhtml">
							<xhtml:html>
								<xhtml:body onLoad="parent.window.hideWindow();">
									<xhtml:center>Complete</xhtml:center>
								</xhtml:body>
							</xhtml:html>
						</p:input>
						<p:output name="data" ref="data"/>
					</p:processor>
                </p:otherwise>
            </p:choose>
        </p:otherwise>
    </p:choose>
</p:config>
