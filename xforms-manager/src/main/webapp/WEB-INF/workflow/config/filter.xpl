<p:config xmlns:p="http://www.orbeon.com/oxf/pipeline"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:oxf="http://www.orbeon.com/oxf/processors"
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:xxforms="http://orbeon.org/oxf/xml/xforms"
    xmlns="http://www.orbeon.com/oxf/controller">

    <!-- Extract request body -->
    <p:processor name="oxf:servlet-filter-generator">
        <p:output name="data" id="request-body"/>
    </p:processor>

    <p:processor name="oxf:pipeline">
        <p:input name="config" href="epilogue.xpl"/>
        <p:input name="data" href="#request-body"/>
        <p:input name="xforms-model"><dummy/></p:input>
        <p:input name="instance"><dummy/></p:input>
    </p:processor>

</p:config>
