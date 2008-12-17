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
    Description: This page implements the Form Manager.
    This page is typically called by the task list that provides task meta data in the HTTP request.
    It retrieves the task input from the Task Management Services WSDL API,
    dynamically loads the XForms form associated with the task
    and provides the controls that support all task actions.
    Author: Jacques-Alexandre Gerber [gerber@intalio.com]
    Creation Date: October, 10th, 2005
    Copyright Intalio,Inc. All rights reserved.
-->
<p:config xmlns:p="http://www.orbeon.com/oxf/pipeline"
	xmlns:oxf="http://www.orbeon.com/oxf/processors"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xforms="http://www.w3.org/2002/xforms">

    <!-- name must be data because it's passed from model4task.xpl  -->
	<p:param name="data" type="input"/>

    <p:param name="instance" type="input"/>
	<!-- name must be data because it's called by the page-flow  -->
	<p:param name="data" type="output"/>

    <p:processor name="oxf:pipeline">
        <p:input name="config" href="act.xpl"/>
        <p:input name="data" href="#data"/>
        <p:output name="data" id="form"/>
    </p:processor>

    <!-- Following XSLT helps to restore taskInput and taskOutput instances as they were in the form
    at the moment add-attachment command was called, i.e. it keeps all User input and does not allow
    to override it by data from DB. And of course it updates taskAttachments instance --> 
    <p:processor name="oxf:xslt">
        <p:input name="data" href="#form"/>
        <p:input name="task" href="#data"/>
        <p:input name="attachments" href="#instance"/>
        <p:input name="config">
            <xsl:stylesheet version="2.0">
                <xsl:import href="oxf:/oxf/xslt/utils/copy.xsl"/>

                <xsl:template match="xforms:instance[@id = 'taskinput']">
                    <xsl:copy>
                        <xsl:copy-of select="@* | doc('input:task')/task/taskInput/*"/>
                    </xsl:copy>
                </xsl:template>

                <xsl:template match="xforms:instance[@id = 'taskoutput']">
                    <xsl:copy>
                        <xsl:copy-of select="@* | doc('input:task')/task/taskOutput/*"/>
                    </xsl:copy>
                </xsl:template>

                <xsl:template match="xforms:instance[@id = 'taskAttachments']/attachments">
                    <xsl:copy>
                        <xsl:copy-of select="@* | doc('input:attachments')/attachments/*"/>
                    </xsl:copy>
                </xsl:template>

            </xsl:stylesheet>
        </p:input>
        <p:output name="data" ref="data"/>
    </p:processor>

</p:config>
