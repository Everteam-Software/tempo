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
          xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
          xmlns:delegation="http://orbeon.org/oxf/xml/delegation"
          xmlns:b4p="http://www.intalio.com/bpms/workflow/ib4p_20051115"
          xmlns:ev="http://www.w3.org/2001/xml-events"
          xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/">


    <!-- name must be instance because it's called by the page-flow  -->
    <p:param name="instance" type="input"/>
    <!-- name must be data because it's called by the page-flow  -->
    <p:param name="data" type="output"/>

    <p:processor name="oxf:xslt">
        <p:input name="data" href="#instance"/>
        <p:input name="config">
            <delegation:execute service="tms" operation="completeRequest" xsl:version="2.0">
                <tms:taskId>
                    <xsl:value-of select="/*:output/@taskId"/>
                </tms:taskId>
                <tms:participantToken>
                    <xsl:value-of select="/*:output/@participantToken"/>
                </tms:participantToken>
            </delegation:execute>
        </p:input>
        <p:output name="data" id="completeTaskInput"/>
    </p:processor>

    <p:processor name="oxf:delegation">
        <p:input name="interface" href="oxf:/config/services.xml"/>
        <p:input name="call" href="#completeTaskInput"/>
        <p:output name="data" id="completeTaskOutput"/>
    </p:processor>

    <p:processor name="oxf:exception-catcher">
        <p:input name="data" href="#completeTaskOutput"/>
        <p:output name="data" id="ws-call-output"/>
    </p:processor>

    <p:choose href="#ws-call-output">
        <p:when test="/exceptions">
            <p:processor name="oxf:pipeline">
                <p:input name="config" href="exception-handler.xpl"/>
                <p:input name="data" href="#ws-call-output"/>
                <p:input name="ws-request" href="#completeTaskInput"/>
                <p:input name="header">
                    <b>Complete Task</b>
                </p:input>
                <p:output name="data" ref="data"/>
            </p:processor>
        </p:when>
        <p:otherwise>

            <p:processor name="oxf:identity">
                <p:input name="data" xmlns:xhtml="http://www.w3.org/1999/xhtml">
                    <xhtml:html>
                        <xhtml:body onLoad="parent.window.hideWindow();">
                            <xhtml:center>Dismissed</xhtml:center>
                        </xhtml:body>
                    </xhtml:html>
                </p:input>
                <p:output name="data" ref="data"/>
            </p:processor>

        </p:otherwise>
    </p:choose>

</p:config>
