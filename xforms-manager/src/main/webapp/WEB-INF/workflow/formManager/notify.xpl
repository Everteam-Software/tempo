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

            OZ: This particular page serves Notification type of Workflow tasks.

            Author: Jacques-Alexandre Gerber [gerber@intalio.com]
            Creation Date: October, 10th, 2005
            Copyright Intalio,Inc. All rights reserved.
        -->
<p:config xmlns:p="http://www.orbeon.com/oxf/pipeline"
          xmlns:oxf="http://www.orbeon.com/oxf/processors"
          xmlns:xforms="http://www.w3.org/2002/xforms"
          xmlns:xxforms="http://orbeon.org/oxf/xml/xforms"
          xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
          xmlns:delegation="http://orbeon.org/oxf/xml/delegation"
          xmlns:xhtml="http://www.w3.org/1999/xhtml"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xmlns:xs="http://www.w3.org/2001/XMLSchema"
          xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
          xmlns:ev="http://www.w3.org/2001/xml-events">

    <!-- name must be data because it's passed from model4task.xpl  -->
    <p:param name="data" type="input"/>
    <!-- name must be data because it's called by the page-flow  -->
    <p:param name="data" type="output"/>

    <!-- Maps input from the HTTP request as defined in taskIdFromTaskList.xml to URL-Generator input -->
    <p:processor name="oxf:xslt">
        <p:input name="data" href="#data"/>
        <p:input name="config">
            <config xsl:version="2.0">
                <url>
                    <xsl:value-of select="/task/url"/>
                </url>
                <content-type>application/xml</content-type>
                <handle-xinclude>false</handle-xinclude>
            </config>
        </p:input>
        <p:output name="data" id="urlGeneratorConfig"/>
    </p:processor>

    <!-- Dynamically loads the form defined at the given URL that is previously retrieved from the HTTP request -->
    <p:processor name="oxf:url-generator">
        <p:input name="config" href="#urlGeneratorConfig"/>
        <p:output name="data" id="fetch-form"/>
    </p:processor>

    <p:processor name="oxf:exception-catcher">
        <p:input name="data" href="#fetch-form"/>
        <p:output name="data" id="form"/>
    </p:processor>

    <p:choose href="#form">
        <p:when test="/exceptions">
            <p:processor name="oxf:xslt">
                <p:input name="data" href="#data"/>
                <p:input name="config" href="oxf:/config/formNotFound.xml"/>
                <p:output name="data" ref="data"/>
            </p:processor>
        </p:when>
        <p:otherwise>
            <p:processor name="oxf:xslt">
                <p:input name="data" href="#data"/>
                <p:input name="config">
                    <delegation:execute service="tms" operation="getTaskRequest" xsl:version="2.0">
                        <tms:taskId>
                            <xsl:value-of select="/task/id"/>
                        </tms:taskId>
                        <tms:participantToken>
                            <xsl:value-of select="/task/token"/>
                        </tms:participantToken>
                    </delegation:execute>
                </p:input>
                <p:output name="data" id="getTaskRequest"/>
            </p:processor>

            <p:processor name="oxf:delegation">
                <p:input name="interface" href="oxf:/config/services.xml"/>
                <p:input name="call" href="#getTaskRequest"/>
                <p:output name="data" id="getTaskResponse"/>
            </p:processor>

            <p:processor name="oxf:exception-catcher">
                <p:input name="data" href="#getTaskResponse"/>
                <p:output name="data" id="ws-call-output"/>
            </p:processor>

            <p:choose href="#ws-call-output">
                <p:when test="/exceptions">
                    <p:processor name="oxf:pipeline">
                        <p:input name="config" href="exception-handler.xpl"/>
                        <p:input name="data" href="#ws-call-output"/>
                        <p:input name="delegation" href="#getTaskRequest"/>
                        <p:input name="header">
                            <b>Get Task</b>
                        </p:input>
                        <p:output name="data" ref="data"/>
                    </p:processor>
                </p:when>
                <p:otherwise>
                    <!-- Wraps the form with everything that is required to handle task actions -->
                    <p:processor name="oxf:xslt">
                        <p:input name="data" href="#form"/>
                        <p:input name="getTaskResponse" href="#getTaskResponse"/>
                        <p:input name="xpl-input" href="#data"/>
                        <p:input name="config">
                            <xsl:stylesheet version="2.0">
                                <xsl:import href="oxf:/oxf/xslt/utils/copy.xsl"/>

                                <xsl:template xmlns:xhtml="http://www.w3.org/1999/xhtml" match="xhtml:head">
                                    <xsl:copy>
                                        <xsl:apply-templates select="@*|*"/>
                                        <xhtml:link type="text/css" rel="stylesheet" href="/config/intalioForms.css"/>
                                    </xsl:copy>
                                </xsl:template>

                                <!-- Adds necessary instances -->
                                <xsl:template match="xforms:model[xforms:instance/@id='taskinput']">
                                    <xsl:copy>
                                        <xsl:apply-templates select="@*|*"/>

                                        <!-- Declares the Complete Task action -->
                                        <xforms:submission id="dismissSubmission" ref="instance('taskoutput')"
                                                           validate="false"
                                                           action="/formManager/dismissNotification" method="post"
                                                           replace="all">

                                            <xforms:toggle case="submit-wait" ev:event="xforms-submit"/>
                                            <xforms:message ev:event="xforms-submit-error" level="modal">
                                                Error during notification dismiss
                                            </xforms:message>
                                        </xforms:submission>
                                    </xsl:copy>
                                </xsl:template>

                                <!-- Copy the input of the task while preserving some input that was coded in the form -->
								<xsl:template match="xforms:instance[@id = 'taskinput']/*:input">
									<xsl:variable name="xinput" select="."/>
									<xsl:variable name="input" select="doc('input:getTaskResponse')/tms:task/*:input/*"/>
									<!-- Create an input element that will basically receive all the nodes coming from the task response -->
									<xsl:element name="input" namespace="{namespace-uri($input)}">
										<xsl:for-each select="$input/*">
											<xsl:variable name="current" select="."/>
											<xsl:choose>
												<!-- 
													if the value of the node from TMS is empty, we copy the one from the form.
													In the best case, the form has a value so we preserve it.
													In the worst case, the form had no value so we keep the same element.
												-->
												<xsl:when test="not(normalize-space(.))">
													<xsl:variable name="same" select="$xinput//*[local-name( ) = local-name($current)]"/>
													<xsl:copy-of select="$same"/>
												</xsl:when>
												<xsl:otherwise>
													<!-- Copy all the nodes containing data as is -->
													<xsl:copy-of select="."/>
												</xsl:otherwise>
											</xsl:choose>
										</xsl:for-each>
									</xsl:element>
								</xsl:template>

                                <!-- copy the task meta data to the 'taskMetaData' instance -->
                                <xsl:template match="xforms:instance[@id = 'taskmetadata']">
                                    <xsl:copy>
                                        <xsl:copy-of select="@* | doc('input:xpl-input')/*"/>
                                    </xsl:copy>
                                </xsl:template>

                                <xsl:template match="xforms:instance[@id = 'taskoutput']/*">
                                    <output>
                                        <xsl:attribute name="taskId">
                                            <xsl:value-of select="doc('input:xpl-input')/task/id"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="participantToken">
                                            <xsl:value-of select="doc('input:xpl-input')/task/token"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="user">
                                            <xsl:value-of select="doc('input:xpl-input')/task/user"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="formUrl">
                                            <xsl:value-of select="doc('input:xpl-input')/task/url"/>
                                        </xsl:attribute>

                                        <xsl:apply-templates select="./*"/>

                                    </output>
                                </xsl:template>

                                <!-- Adds the controls for Task Actions and handle the logic -->
                                <xsl:template xmlns:xhtml="http://www.w3.org/1999/xhtml" match="xhtml:body">
                                    <xsl:copy>
                                        <!-- display the proper page -->
                                        <xforms:switch>
                                            <!-- Display the form and adds task action controls-->
                                            <xforms:case id="viewTask">
                                                <xsl:apply-templates select="@* | *"/>
                                                <xhtml:div>
												<span class="button" title="Dismiss Notification">
                                                    <xforms:submit submission="dismissSubmission">
                                                        <xforms:label>Dismiss</xforms:label>
                                                    </xforms:submit>
												</span>
                                                </xhtml:div>
                                            </xforms:case>
                                            <!-- Display while submission - wait page -->
                                            <xforms:case id="submit-wait">
                                                <xhtml:center>
                                                    <xhtml:img src="/images/loading.gif" alt="..."/>
                                                </xhtml:center>
                                            </xforms:case>
                                        </xforms:switch>
                                    </xsl:copy>
                                </xsl:template>
                            </xsl:stylesheet>
                        </p:input>
                        <p:output name="data" ref="data"/>
                    </p:processor>
                </p:otherwise>
            </p:choose>
        </p:otherwise>
    </p:choose>
</p:config>
