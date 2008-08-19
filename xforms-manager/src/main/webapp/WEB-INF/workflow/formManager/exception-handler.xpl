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
            Description: This page serves to catch and handle exceptions.
        -->
<p:config xmlns:p="http://www.orbeon.com/oxf/pipeline"
          xmlns:oxf="http://www.orbeon.com/oxf/processors"
          xmlns:xforms="http://www.w3.org/2002/xforms"
          xmlns:xxforms="http://orbeon.org/oxf/xml/xforms"
          xmlns:saxon="http://saxon.sf.net/"
          xmlns:xhtml="http://www.w3.org/1999/xhtml"
          xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
          xmlns:delegation="http://orbeon.org/oxf/xml/delegation"
          xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
          xmlns:ev="http://www.w3.org/2001/xml-events">


    <p:param name="header" type="input"/>

    <p:param name="data" type="input"/>

    <p:param name="ws-request" type="input"/>
    <!-- name must be data because it's called by the page-flow  -->
    <p:param name="data" type="output"/>


            <p:processor name="oxf:unsafe-xslt">
                <p:input name="data" href="#data"/>
                <p:input name="ws-request" href="#ws-request"/>
                <p:input name="header" href="#header"/>
                <p:input name="config">
                    <xsl:stylesheet version="2.0">
                         <xsl:output method="html" name="ws-request-output"/>

                        <xsl:template match="/exceptions">
                            <xhtml:html>
								<xhtml:head>
									<xhtml:link type="text/css" rel="stylesheet" href="/config/errors.css"/>
								</xhtml:head>
                                <xhtml:body>
                                    <xhtml:center>
                                        <xhtml:h1>Error when calling Web Service:<xsl:copy-of select="doc('input:header')/*"/></xhtml:h1>
                                    </xhtml:center>
                                        <xhtml:div>
                                            <xhtml:b>Delegation in XPL to Web-Service:</xhtml:b><xhtml:br/>
                                            <xsl:copy-of select="saxon:serialize(doc('input:ws-request'), 'ws-request-output')"/>
                                        </xhtml:div>
                                    <xhtml:h1>Exception details:</xhtml:h1>
                                    <xhtml:p>
                                        <xsl:apply-templates select="*"/>
                                    </xhtml:p>
                                </xhtml:body>
                            </xhtml:html>
                        </xsl:template>

                        <xsl:template match="exception">
                            <xhtml:h3>
                                <xsl:value-of select="type"/>
                            </xhtml:h3>
                            <xhtml:h3>
                                    <xsl:value-of select="message"/>
                            </xhtml:h3>
                            <xhtml:p>
                                <xsl:apply-templates select="location"/>
                                <xsl:apply-templates select="stack-trace-elements"/>
                            </xhtml:p>
                        </xsl:template>

                        <xsl:template match="location">
                            <xhtml:h5>File:
                                <xsl:value-of select="system-id"/> -> <xsl:value-of select="line"/>:<xsl:value-of select="column"/> (line:column)
                            </xhtml:h5>
                            <xhtml:p>Description:
                                <xsl:value-of select="description"/>
                                <br/>
                                <xsl:apply-templates select="parameters"/>
                            </xhtml:p>
                        </xsl:template>

                        <xsl:template match="parameters">
                            <xhtml:b>
                                Parameters:
                                <xhtml:i>
                                    <xsl:for-each select="parameter">
                                        <xsl:value-of select="name"/>=<xsl:value-of select="value"/><xsl:text>; </xsl:text>
                                    </xsl:for-each>
                                </xhtml:i>
                            </xhtml:b>

                        </xsl:template>

                        <xsl:template match="stack-trace-elements">
                            <xhtml:h5>Stacktrace:</xhtml:h5>
                            <xhtml:p>
                                <xsl:for-each select="element">
                                    <xsl:value-of select="class-name"/>#<xsl:value-of
                                        select="method-name"/>():<xsl:value-of select="line-number"/>
                                    <br/>
                                </xsl:for-each>
                            </xhtml:p>
                        </xsl:template>
                    </xsl:stylesheet>
                </p:input>
                <p:output name="data" ref="data"/>
            </p:processor>

</p:config>