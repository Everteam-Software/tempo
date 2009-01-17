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

<p:config xmlns:p="http://www.orbeon.com/oxf/pipeline"
	xmlns:oxf="http://www.orbeon.com/oxf/processors"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:delegation="http://orbeon.org/oxf/xml/delegation"
        xmlns:tas="http://www.intalio.com/BPMS/Workflow/TaskAttachmentService/"
        xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <p:param name="instance" type="input"/>
    <p:param name="data" type="output"/>
    <p:param name="instance" type="output"/>

    <!-- Call TAS WS for TAS.add operation to add new attachment -->

    <p:choose href="#instance">
        <!-- We'll make the attachment handling only if it's defined properly -->
        <p:when test="string-length(normalize-space(/attachments/new/file)) > 0">
            <!-- Prepare the SOAP body message -->
            <p:processor name="oxf:xslt">
                <p:input name="data" href="#instance"/>
                <p:input name="config">
                    <delegation:execute service="tas" operation="addRequest" xsl:version="2.0">
                        <tas:authCredentials>
                            <tas:participantToken>
                                <xsl:value-of select="/*/@participantToken"/>
                            </tas:participantToken>
                            <tas:authorizedUsers>
                                <xsl:for-each select="/attachments/owners/userOwner">
                                    <tas:user><xsl:value-of select="."/></tas:user>
                                </xsl:for-each>
                            </tas:authorizedUsers>
                            <tas:authorizedRoles>
                                <xsl:for-each select="/attachments/owners/roleOwner">
                                    <tas:role><xsl:value-of select="."/></tas:role>
                                </xsl:for-each>
                            </tas:authorizedRoles>
                        </tas:authCredentials>
                        <tas:attachmentMetadata>
                            <tas:mimeType><xsl:value-of select="/attachments/new/file/@mediatype"/></tas:mimeType>
	                        <tas:filename><xsl:value-of select="/attachments/new/file/@filename"/></tas:filename>
                        </tas:attachmentMetadata>
                        <tas:localFileURL>
                            <xsl:value-of select="/attachments/new/file"/>
                        </tas:localFileURL>
                    </delegation:execute>
                </p:input>
                <p:output name="data" id="addRequest"/>
            </p:processor>

            <!-- Call TAS.add -->
            <p:processor name="oxf:delegation">
				<p:input name="interface" href="oxf:/config/services.xml"/>
				<p:input name="call" href="#addRequest"/>
                <p:output name="data" id="addResponse"/>
            </p:processor>

            <!-- Call TMS WS for TMS.add operation to add new attachment -->
            <!-- Prepare the SOAP message body -->
            <p:processor name="oxf:xslt">
                <p:input name="data" href="#instance"/>
                <p:input name="tasResponse" href="#addResponse"/>
                <p:input name="config">
                    <delegation:execute service="tms" operation="addAttachmentRequest" xsl:version="2.0">
                        <tms:taskId>
                            <xsl:value-of select="/*/@taskId"/>
                        </tms:taskId>
                        <tms:attachment>
                            <tms:attachmentMetadata>
                                <tms:mimeType><xsl:value-of select="/attachments/new/file/@mediatype"/></tms:mimeType>
                                <tms:fileName><xsl:value-of select="/attachments/new/file/@filename"/></tms:fileName>
                                <tms:title><xsl:value-of select="/attachments/new/@title"/></tms:title>
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
                <!-- This output is never used, but should be here as otherwise Orbeon XPL engine consider that calling
                processor which output is not referenced further does not makes sense -->
                <p:output name="data" id="okResponse"/>
            </p:processor>

            <p:processor name="oxf:null-serializer">
               <p:input name="data" href="#okResponse"/>
            </p:processor>

        </p:when>
    </p:choose>

    <p:processor name="oxf:xslt">
        <p:input name="data" href="#instance"/>
        <p:input name="config">
            <task xsl:version="2.0">
                <id><xsl:value-of select="/attachments/@taskId"/></id>
                <url><xsl:value-of select="/attachments/@formURL"/></url>
                <user><xsl:value-of select="/attachments/@user"/></user>
                <token><xsl:value-of select="/attachments/@participantToken"/></token>
                <taskInput><xsl:copy-of select="/attachments/action/following-sibling::*[1]"/></taskInput>
                <taskOutput><xsl:copy-of select="/attachments/action/following-sibling::*[2]"/></taskOutput>
            </task>
        </p:input>
        <p:output name="data" ref="data"/>
    </p:processor>

    <p:processor name="oxf:pipeline">
        <p:input name="config" href="retrieve-attachments.xpl"/>
        <p:input name="instance" href="#instance"/>
        <p:output name="data" ref="instance"/>
    </p:processor>

</p:config>

