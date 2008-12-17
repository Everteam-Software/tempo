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
    xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <p:param name="instance" type="input"/>
    <p:param name="data" type="output"/>

    <p:processor name="oxf:xslt">
        <p:input name="data" href="#instance"/>
        <p:input name="config">
            <delegation:execute service="tms" operation="getAttachmentsRequest" xsl:version="2.0">
                <tms:taskId>
                    <xsl:value-of select="/*/@taskId"/>
                </tms:taskId>
                <tms:participantToken>
                    <xsl:value-of select="/*/@participantToken"/>
                </tms:participantToken>
            </delegation:execute>
        </p:input>
        <p:output name="data" id="attachmentsRequest"/>
    </p:processor>

    <p:processor name="oxf:delegation">
        <p:input name="interface" href="oxf:/config/services.xml"/>
        <p:input name="call" href="#attachmentsRequest"/>
        <p:output name="data" id="taskAttachments"/>
    </p:processor>

    <p:processor name="oxf:xslt">
        <p:input name="data" href="#taskAttachments"/>
        <p:input name="instance" href="#instance"/>
        <p:input name="config">
            <attachments xsl:version="2.0">
                <xsl:attribute name="taskId">
                    <xsl:value-of select="doc('input:instance')//@taskId"/>
                </xsl:attribute>
                <xsl:attribute name="participantToken">
                    <xsl:value-of select="doc('input:instance')//@participantToken"/>
                </xsl:attribute>
                <xsl:attribute name="formURL">
                    <xsl:value-of select="doc('input:instance')//@formURL"/>
                </xsl:attribute>
                <xsl:attribute name="user">
                    <xsl:value-of select="doc('input:instance')//@user"/>
                </xsl:attribute>

                <xsl:for-each select="/tms:attachment">
                    <attachment>
                        <mime-type>
                            <xsl:value-of select="tms:attachmentMetadata/tms:mimeType"/>
                        </mime-type>
                        <title>
                            <xsl:value-of select="tms:attachmentMetadata/tms:title"/>
                        </title>
                        <hint>
                            <xsl:value-of select="tms:attachmentMetadata/tms:description"/>
                        </hint>
                        <create-date>
                            <xsl:value-of select="tms:attachmentMetadata/tms:creationDate"/>
                        </create-date>
                        <url>
                            <xsl:value-of select="tms:payloadUrl"/>
                        </url>
                    </attachment>
                </xsl:for-each>
                <new>
                    <xsl:attribute name="title">
                        <xsl:value-of select="doc('input:instance')//new/@title"/>
                    </xsl:attribute>
                    <xsl:attribute name="content">
                        <xsl:value-of select="doc('input:instance')//new/@content"/>
                    </xsl:attribute>
                    <file filename="" mediatype="" size="" attachFile=""/>
                    <plaintext attachText="">
                        <xsl:value-of select="doc('input:instance')//new/plaintext"/>
                    </plaintext>
                </new>
                <delete/>
                <xsl:copy-of select="doc('input:instance')//owners"/>
                <action>show</action>
            </attachments>
        </p:input>
        <p:output name="data" ref="data" />
    </p:processor>


</p:config>
