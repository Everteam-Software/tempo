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
            Description: This page implements the Init Manager.
            This page is typically called by the task list that provides task meta data in the HTTP request.
            It dynamically loads the XForms form associated with the task
            and provides the controls that support all task actions.
            Author: Jacques-Alexandre Gerber [gerber@intalio.com]
            Creation Date: October, 14th, 2005
            Copyright Intalio,Inc. All rights reserved.
        -->
<p:config xmlns:p="http://www.orbeon.com/oxf/pipeline"
          xmlns:oxf="http://www.orbeon.com/oxf/processors"
          xmlns:xforms="http://www.w3.org/2002/xforms"
          xmlns:xxforms="http://orbeon.org/oxf/xml/xforms"
          xmlns:xhtml="http://www.w3.org/1999/xhtml"
          xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
          xmlns:delegation="http://orbeon.org/oxf/xml/delegation"
          xmlns:ev="http://www.w3.org/2001/xml-events">

    <!-- name must be instance because it's called by the page-flow  -->
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
                <p:input name="data" href="#form"/>
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
                        <xsl:template match="xforms:model[xforms:instance/@id='taskoutput']">
                            <xsl:copy>
                                <xsl:apply-templates select="@* | *"/>

                                <!-- Declares the Init Task action -->
                                <xforms:submission id="initSubmission" ref="instance('taskoutput')" validate="false"
                                                   action="/formManager/init" method="post" replace="all">

                                    <xforms:toggle ev:event="xforms-submit" case="submit-wait"/>
                                    <xforms:action ev:event="xforms-submit-error">
                                        <xforms:toggle case="viewTask"/>
                                        <xforms:message level="modal">Can not start process because the form is not correctly filled out</xforms:message>
                                    </xforms:action>
                                </xforms:submission>

                            </xsl:copy>
                        </xsl:template>

                        <!-- copy this XPL input (task data) to the 'taskMetaData' instance -->
                        <xsl:template match="xforms:instance[@id = 'taskmetadata']">
                            <xsl:copy>
                                <xsl:copy-of select="@* | doc('input:xpl-input')/*"/>
                            </xsl:copy>
                        </xsl:template>

                        <!-- adds meta data information to the 'taskoutput' instance

                            The form is responsible for putting the actual data output but
                            it is the Form Manager responsibility to add the meta data element
                            that are required to invoke the Complete Task action
                        -->
                        <xsl:template match="xforms:instance[@id = 'taskoutput']/*">
                            <xsl:copy>
                                <xsl:apply-templates select="@*"/>
                                <xsl:attribute name="taskId">
                                    <xsl:value-of select="doc('input:xpl-input')/task/id"/>
                                </xsl:attribute>
                                <xsl:attribute name="participantToken">
                                    <xsl:value-of select="doc('input:xpl-input')/task/token"/>
                                </xsl:attribute>
                                <xsl:attribute name="user">
                                    <xsl:value-of select="doc('input:xpl-input')/task/user"/>
                                </xsl:attribute>
                                <xsl:attribute name="url">
                                    <xsl:value-of select="doc('input:xpl-input')/task/url"/>
                                </xsl:attribute>
                                <xsl:apply-templates select="*"/>
                            </xsl:copy>
                        </xsl:template>

                        <!-- Adds the controls for Task Actions and handle the logic -->
                        <xsl:template xmlns:xhtml="http://www.w3.org/1999/xhtml" match="xhtml:body">
                            <xsl:copy>
                                <!-- display the proper page -->
                                <xforms:switch>
                                    <!-- Display the form and adds task action controls-->
                                    <xforms:case id="viewTask">

                                        <xsl:apply-templates select="@*|*"/>

                                        <xhtml:div>
											<span class="button" title="Start The Process">
                                            <xforms:submit submission="initSubmission">
                                                <xforms:label>Start</xforms:label>
                                            </xforms:submit>
											</span>
                                        </xhtml:div>
                                    </xforms:case>

                                    <!-- Display while submission - wait page -->
                                    <xforms:case id="submit-wait">
                                        <xhtml:center>Sending request, please wait</xhtml:center>
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

</p:config>
