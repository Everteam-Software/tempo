<!--
  ~ Copyright (c) 2005-2007 Intalio inc.
  ~
  ~ All rights reserved. This program and the accompanying materials
  ~ are made available under the terms of the Eclipse Public License v1.0
  ~ which accompanies this distribution, and is available at
  ~ http://www.eclipse.org/legal/epl-v10.html
  ~
  ~ Contributors:
  ~ Intalio inc. - initial API and implementation
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:b4p="http://www.intalio.com/bpms/workflow/ib4p_20051115"
                xmlns:tms="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/"
                version="2.0">

    <xsl:output method="xml"/>
    <xsl:param name="metadata"/>

   <!-- Change namespace for matching elements -->

    <xsl:template match="tms:userOwner">
        <xsl:call-template name="change">
            <xsl:with-param name="elements" select="$metadata/*:userOwner"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="tms:roleOwner">
        <xsl:call-template name="change">
            <xsl:with-param name="elements" select="$metadata/*:roleOwner"/>
        </xsl:call-template>
    </xsl:template>

   <xsl:template name="change">
      <xsl:param name="elements"/>
      <xsl:for-each select="$elements">
          <xsl:element name="{local-name()}"
                       namespace="http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/">
              <xsl:if test="count(*) > 0">
                  <xsl:call-template name="change">
                      <xsl:with-param name="elements" select="*"/>
                  </xsl:call-template>
              </xsl:if>
              <xsl:apply-templates select="text()"/>
          </xsl:element>
      </xsl:for-each>
   </xsl:template>

    <!-- Identity transform for the rest -->
    <xsl:template match="*|@*|text()">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
