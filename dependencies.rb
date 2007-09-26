
ANT = [ "ant:ant:jar:1.6.5" ]

AXIOM = group("axiom-api", "axiom-dom", "axiom-impl", :under=>"org.apache.ws.commons.axiom", :version=>"1.2.5")

AXIS2 = group("axis2-adb", "axis2-kernel", "axis2-xmlbeans", :under=>"org.apache.axis2", :version=>"1.3")

CASTOR = "castor:castor:jar:1.0"

COMMONS = [
  "commons-codec:commons-codec:jar:1.3",
  "commons-collections:commons-collections:jar:3.1",
  "commons-digester:commons-digester:jar:1.7",
  "commons-fileupload:commons-fileupload:jar:1.1.1",
  "commons-httpclient:commons-httpclient:jar:3.0",
  "commons-io:commons-io:jar:1.2",
  "commons-lang:commons-lang:jar:2.1",
  "commons-pool:commons-pool:jar:1.2" ]

DOJO_VERSION = "0.2.2"
DOJO_URL = "http://download.dojotoolkit.org/release-#{DOJO_VERSION}/dojo-#{DOJO_VERSION}-widget.zip"
DOJO = "dojo:dojo-widget:zip:#{DOJO_VERSION}"
DOJO_WIDGET_BASE = "dojo-#{DOJO_VERSION}-widget"

DOM4J = [ "dom4j:dom4j:jar:1.6.1" ]

FOP = [ "fop:fop:jar:0.20.5" ]

INTALIO_STATS = [ "org.intalio.common:intalio-stats:jar:1.0.1" ]

JAVAMAIL = "geronimo-spec:geronimo-spec-javamail:jar:1.3.1-rc5"

JARGS = [ "jargs:jargs:jar:1.0" ]

JENCKS = [ "jencks:jencks-all:jar:1.1.3" ]

JSP_API = [ "javax.servlet:jsp-api:jar:2.0" ]

JSTL = [ "javax.servlet:jstl:jar:1.1.2" ]

JUNIT = "junit:junit:jar:3.8.1"

LOG4J = [ "log4j:log4j:jar:1.2.15" ]

JPA = [ "javax.persistence:persistence-api:jar:1.0" ]

QOM = [ "net.sf.qom:qom:jar:0.1alpha3" ]

SERVLET_API = [ "javax.servlet:servlet-api:jar:2.4" ]

SLF4J = group(%w{ slf4j-api slf4j-log4j12 jcl104-over-slf4j }, :under=>"org.slf4j", :version=>"1.4.3")

SPRING = [ "org.springframework:spring:jar:1.2.8" ]

STAX_API = [ "stax:stax-api:jar:1.0" ]

TAGLIBS = [ "taglibs:standard:jar:1.1.2" ]

TEMPO_SECURITY = [ "org.intalio.tempo.security:tempo-security:jar:1.0.6-SNAPSHOT" ]

TEMPO_SECURITY_WS_TOKEN_CLIENT =[ "org.intalio.tempo.security:tempo-security-ws-token-client:jar:1.1.5" ]

TEMPO_WORKFLOW_TOM = [ "org.intalio.tempo.workflow:intalio-tempo-workflow-tom:jar:5.0.0.8" ]

TEMPO_WORKFLOW_TMS_CLIENT = [ "org.intalio.tempo.workflow:intalio-tempo-workflow-tms-client:jar:5.0.0.3" ]

WOODSTOX = [ "woodstox:wstx-asl:jar:3.2.1" ]

WS_COMMONS_SCHEMA = "org.apache.ws.commons.schema:XmlSchema:jar:1.3.1"

WSDL4J = [ "wsdl4j:wsdl4j:jar:1.5.2" ]

XERCES = [
  "xerces:xercesImpl:jar:2.9.0",
  "xerces:xmlParserAPIs:jar:2.9.0" ]

XMLBEANS = [
  "xmlbeans:xbean:jar:2.1.0",
  "xmlbeans:xbean_xpath:jar:2.1.0",
  "xmlbeans:xmlpublic:jar:2.1.0" ]

XOM = [ "xom:xom:jar:1.1" ]

ORBEON_LIBS = [
  "commons-codec:commons-codec:jar:1.3",
  "commons-collections:commons-collections:jar:3.1",
  "commons-digester:commons-digester:jar:1.7",
  "commons-fileupload:commons-fileupload:jar:1.0",
  "commons-httpclient:commons-httpclient:jar:3.0",
  "commons-io:commons-io:jar:1.2",
  "commons-lang:commons-lang:jar:2.1",
  "commons-pool:commons-pool:jar:1.2",
  "commons-beanutils:commons-beanutils:jar:1.7.0",
  "commons-validator:commons-validator:jar:1.1.4",
  "commons-discovery:commons-discovery:jar:0.2",
  JAVAMAIL,
  FOP,
  "orbeon:avalon-framework:jar:cvs-20020315",
  "orbeon:axis-orbeon:jar:1.2.1",
  "orbeon:axis-jaxrpc:jar:1.2.1",
  "orbeon:axis-saaj:jar:1.2.1",
  "orbeon:axis-wsdl4j:jar:1.2.1-1.5.1",
  "orbeon:dom4j-orbeon:jar:1.6.1",
  "orbeon:isorelax:jar:20031020",
  "orbeon:jakarta-oro-orbeon:jar:2.0.8",
  "orbeon:jaxen:jar:1.1-beta-1-dev",
  "orbeon:jdom:jar:b9",
  "orbeon:msv:jar:20031020",
  "orbeon:ops:jar:20061103i",
  "orbeon:ops-resources-public:jar:20061103",
  "orbeon:ops-resources-private:jar:20061103",
  "orbeon:relaxng-datatype:jar:20031020",
  "orbeon:resolver-xerces-orbeon:jar:2.6.2",
  "orbeon:saxon-orbeon:jar:8.8",
  "orbeon:saxpath-orbeon:jar:dev",
  "orbeon:struts:jar:1.1",
  "orbeon:jtidy:jar:04aug2000r7",
  "orbeon:xalan-orbeon:jar:2.5.1",
  "orbeon:xercesimpl-orbeon:jar:2.6.2",
  "orbeon:xml-apis-xerces-orbeon:jar:2.6.2",
  "orbeon:xsdlib:jar:20031020",
  "orbeon:xsltc-orbeon:jar:2.5.1",
  "org.apache.geronimo.specs:geronimo-activation_1.0.2_spec:jar:1.1",
  "portlet-api:portlet-api:jar:1.0" ]

