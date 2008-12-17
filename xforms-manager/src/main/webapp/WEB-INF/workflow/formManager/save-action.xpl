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
        xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
        xmlns:ev="http://www.w3.org/2001/xml-events">


	<!-- name must be instance because it's called by the page-flow  -->
	<p:param name="instance" type="input"/>
	<!-- name must be data because it's called by the page-flow  -->
	<p:param name="data" type="output"/>

	<p:processor name="oxf:xslt">
		<p:input name="data" href="#instance"/>
		<p:input name="config">
            <xsl:stylesheet version="2.0">
                <xsl:import href="oxf:/oxf/xslt/utils/copy.xsl"/>

                <xsl:template match="/">
                    <xsl:copy>
                        <delegation:execute service="tms" operation="setOutput" xsl:version="2.0">
                            <tms:taskId>
                                <xsl:value-of select="/*:output/@taskId"/>
                            </tms:taskId>
                            <tms:data>
                                <xsl:apply-templates select="*"/>
                            </tms:data>
                            <tms:participantToken>
                                <xsl:value-of select="/*:output/@participantToken"/>
                            </tms:participantToken>
                        </delegation:execute>
                    </xsl:copy>
                </xsl:template>

                <xsl:template match="*:output">
                    <xsl:copy>
                        <xsl:apply-templates select="@*[name() != 'taskId' and name() != 'participantToken' and name() != 'formUrl' and name() != 'taskType'] | *"/>
                    </xsl:copy>
                </xsl:template>

            </xsl:stylesheet>
        </p:input>
		<p:output name="data" id="saveTaskInput"/>
	</p:processor>

    <p:processor name="oxf:delegation">
        <p:input name="interface" href="oxf:/config/services.xml"/>
        <p:input name="call" href="#saveTaskInput"/>
        <p:output name="data" id="saveTaskOutput"/>
    </p:processor>

    <p:processor name="oxf:exception-catcher">
        <p:input name="data" href="#saveTaskOutput"/>
        <p:output name="data" id="ws_call_output" />
    </p:processor>

    <p:choose href="#ws_call_output">
        <p:when test="/exceptions">
            <p:processor name="oxf:pipeline">
                <p:input name="config" href="exception-handler.xpl"/>
                <p:input name="data" href="#ws_call_output"/>
                <p:input name="ws-request" href="#saveTaskInput"/>
                <p:input name="header">
                    <b>Save Task</b>
                </p:input>
                <p:output name="data" ref="data"/>
            </p:processor>
        </p:when>
        <p:otherwise>
            <p:processor name="oxf:xslt">
                <p:input name="data" href="#instance"/>
                <p:input name="config">
                    <task xsl:version="2.0">
                        <id><xsl:value-of select="/*:output/@taskId"/></id>
                        <url><xsl:value-of select="/*:output/@formUrl"/></url>
                        <type><xsl:value-of select="/*:output/@taskType"/></type>
                        <token><xsl:value-of select="/*:output/@participantToken"/></token>
                        <user><xsl:value-of select="/*:output/@user"/></user>
                    </task>
                </p:input>
                <p:output name="data" id="savedTask"/>
            </p:processor>

            <p:processor name="oxf:pipeline">
                <p:input name="config" href="act.xpl"/>
                <p:input name="data" href="#savedTask"/>
                <p:output name="data" ref="data"/>
            </p:processor>
        </p:otherwise>
    </p:choose>


</p:config>
