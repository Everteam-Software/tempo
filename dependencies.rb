
ANT = [ "org.apache.ant:ant:jar:1.7.0" ]

AXIOM = group("axiom-api", "axiom-dom", "axiom-impl", :under=>"org.apache.ws.commons.axiom", :version=>"1.2.5")

AXIS2 = [
  group("axis2-adb", "axis2-xmlbeans", :under=>"org.apache.axis2", :version=>"1.3"),
  "org.apache.axis2:axis2-kernel:jar:1.3i1"
]

CASTOR = "castor:castor:jar:1.0"

COMMONS_LOG = [
  "commons-logging:commons-logging:jar:1.0.4"
  ]

CAS_CLIENT = "cas:casclient:jar:2.1.1"

CAS_LIBS = [
  group("cas-server-core",
  :under=>"org.jasig.cas", :version=>"3.1.1"),
  "org.jasig.service:person-directory:jar:1.0.1",
  "org.acegisecurity:acegi-security:jar:1.0.4",
  "net.sf.ehcache:ehcache:jar:1.2.3",
  "quartz:quartz:jar:1.5.2",
  "org.springframework:spring-binding:jar:1.0.3",
  "org.springframework:spring:jar:2.0.6",
  "org.springframework:spring-webflow:jar:1.0.3",  
  "ognl:ognl:jar:2.6.9",
  "org.opensaml:opensaml:jar:1.1b",
  "jdom:jdom:jar:1.0"
  ]
  
COMMONS_POOL = ["commons-pool:commons-pool:jar:1.3"]
COMMONS = [
  "commons-codec:commons-codec:jar:1.3",
  "commons-collections:commons-collections:jar:3.2", 
  "commons-digester:commons-digester:jar:1.7",
  "commons-fileupload:commons-fileupload:jar:1.0",
  "commons-httpclient:commons-httpclient:jar:3.1",
  "commons-io:commons-io:jar:1.2",
  "commons-lang:commons-lang:jar:2.3",
  COMMONS_POOL
]

DOJO_VERSION = "0.2.2"
DOJO_URL = "http://download.dojotoolkit.org/release-#{DOJO_VERSION}/dojo-#{DOJO_VERSION}-widget.zip"
DOJO = "dojo:dojo-widget:zip:#{DOJO_VERSION}"
DOJO_WIDGET_BASE = "dojo-#{DOJO_VERSION}-widget"

DOM4J = [ "dom4j:dom4j:jar:1.6.1" ]

FOP = [ "fop:fop:jar:0.20.5" ]

INTALIO_STATS = [ "org.intalio.common:intalio-stats:jar:1.0.1" ]

JAVAMAIL = "geronimo-spec:geronimo-spec-javamail:jar:1.3.1-rc5", "geronimo-spec:geronimo-spec-activation:jar:1.0.2-rc4"
SUNMAIL = ["javax.mail:mail:jar:1.4.1", "javax.activation:activation:jar:1.1.1"]

JARGS = [ "jargs:jargs:jar:1.0" ]

JSON = [ "json:json-taglib:jar:0.4.1" ]

JAXEN = [ "jaxen:jaxen:jar:1.1.1" ]

JENCKS = [ "jencks:jencks-all:jar:1.1.3" ]

JSP_API = [ "javax.servlet:jsp-api:jar:2.0" ]

JSTL = [ "javax.servlet:jstl:jar:1.1.2" ]

JUNIT = "junit:junit:jar:4.4"

LOG4J = [ "log4j:log4j:jar:1.2.15" ]

JPA = [ "javax.persistence:persistence-api:jar:1.0" ]

GERONIMO_SPECS = {
  "jta" => "org.apache.geronimo.specs:geronimo-jta_1.1_spec:jar:1.1",
  "jpa" => "org.apache.geronimo.specs:geronimo-jpa_3.0_spec:jar:1.0",
  "jms" => "org.apache.geronimo.specs:geronimo-jms_1.1_spec:jar:1.0.1"
}

APACHE_JPA = [
  COMMONS,
  GERONIMO_SPECS.values,
  "org.apache.openjpa:openjpa:jar:1.1.0-svn-620200",
  "commons-logging:commons-logging:jar:1.0.4",
  "commons-lang:commons-lang:jar:2.1",
  "org.apache.derby:derby:jar:10.2.2.0",
  "serp:serp:jar:1.13.1"
]

PORTLET_API = ["portlet-api:portlet-api:jar:1.0"]

QOM = [ "net.sf.qom:qom:jar:0.1alpha3" ]

SERVLET_API = [ "javax.servlet:servlet-api:jar:2.4" ]

SLF4J = group(%w{ slf4j-api slf4j-log4j12 jcl104-over-slf4j }, :under=>"org.slf4j", :version=>"1.4.3")

SPRING = [ "org.springframework:spring:jar:2.5.1","org.springframework:spring-webmvc-portlet:jar:2.5.1","org.springframework:spring-webmvc:jar:2.5.1"]

STAX_API = [ "stax:stax-api:jar:1.0.1" ]

TAGLIBS = [ "taglibs:standard:jar:1.1.2" ]

TEMPO_SECURITY = [ "org.intalio.tempo.security:tempo-security:jar:1.0.6-SNAPSHOT" ]

TEMPO_SECURITY_WS_TOKEN_CLIENT =[ "org.intalio.tempo.security:tempo-security-ws-token-client:jar:1.1.5" ]

TEMPO_WORKFLOW_TOM = [ "org.intalio.tempo.workflow:intalio-tempo-workflow-tom:jar:5.0.0.8" ]

TEMPO_WORKFLOW_TMS_CLIENT = [ "org.intalio.tempo.workflow:intalio-tempo-workflow-tms-client:jar:5.0.0.3" ]

WOODSTOX = [ "woodstox:wstx-asl:jar:3.2.4" ]

WS_COMMONS_SCHEMA = "org.apache.ws.commons.schema:XmlSchema:jar:1.3.1"

WSDL4J = [ "wsdl4j:wsdl4j:jar:1.6.1" ]

XERCES = [
  "xerces:xercesImpl:jar:2.9.1",
  "xerces:xmlParserAPIs:jar:2.9.0" ]

XMLBEANS = [
  "xmlbeans:xbean:jar:2.3.0",
  "xmlbeans:xbean_xpath:jar:2.3.0",
  "xmlbeans:xmlpublic:jar:2.3.0" ]

XMLUNIT = ["xmlunit:xmlunit:jar:1.1"]

ORBEON_AXIS = [
 "orbeon:axis-orbeon:jar:1.2.1",
 "orbeon:axis-jaxrpc:jar:1.2.1",
 "orbeon:axis-saaj:jar:1.2.1",
 "orbeon:axis-wsdl4j:jar:1.2.1-1.5.1"
]
ORBEON_XERCES = [
  group("xerces-resolver", "xerces-serializer", "xerces-xml-apis", "xerces-xercesImpl", :under => "orbeon" , :version => "2_9_orbeon_20070711")
]
ORBEON_CORE = [
  group("orbeon", "orbeon-xforms-filter", "orbeon-resources-public", "orbeon-resources-private", :under=>"ops", :version=>"3.6.0.200712061930"),
]
ORBEON_CUSTOM = [
  ORBEON_XERCES,
  "orbeon:jakarta-oro-orbeon:jar:2.0.8",
  #"orbeon:saxon-orbeon:jar:8_8_orbeon_20070817" is buggy, 
  # the saxon-orbeon jar below is a slightly modified version
  # that works
  "orbeon-saxon:orbeon-saxon:jar:8.8-intalio-2",
  "orbeon:saxpath:jar:dev_orbeon",
  "orbeon:xsltc-orbeon:jar:2.5.1",
  "orbeon:xalan-orbeon:jar:2.5.1",
  
  #  Not needed unless we want to force server caching for xforms
  # "orbeon:xmldb-exist:jar:1_1_1",
  # "orbeon:exist:jar:1.1.1",
  # "orbeon:xmlrpc:jar:1.2-patched-exist_1_1_1",
  # "orbeon:antlr:jar:antlr-2.7.6-exist_1_1_1",
  # "orbeon:exist-optional:jar:1.1.1"
]

COMMONS_DISCOVERY = [  "commons-discovery:commons-discovery:jar:0.2"]

PLUTO = [
  "org.apache.pluto:pluto-container:jar:1.1.4",
  "org.apache.pluto:pluto-taglib:jar:1.1.4",
  "org.apache.pluto:pluto-portal-driver:jar:1.1.4",
  "org.apache.pluto:pluto-portal-driver-impl:jar:1.1.4",
  PORTLET_API
]

ORBEON_COMMONS = [
  COMMONS,
  COMMONS_DISCOVERY,
  "commons-beanutils:commons-beanutils:jar:1.7.0",
  "commons-validator:commons-validator:jar:1.1.4"
]

ORBEON_MSV = [
  group("msv", "isorelax", "relaxng-datatype","xsdlib", :under => "msv", :version => "20070407")
]

ORBEON_LIBS = [
     JAVAMAIL,
     ORBEON_AXIS,
     ORBEON_COMMONS,
     ORBEON_CORE,
     ORBEON_CUSTOM,
     ORBEON_MSV,
  DOM4J,
  GERONIMO_SPECS["jms"],
  "orbeon:jaxen:jar:1.1-beta-1-dev",
  "jdom:jdom:jar:b9",
  "struts:struts:jar:1.2.9",
  "jtidy:jtidy:jar:8.0-20060801.131059-3",
  PORTLET_API
]

# For testing
EASY_B = [
  "commons-cli:commons-cli:jar:1.1",
  "org.codehaus.groovy:groovy-all:jar:1.5.4",
  "org.disco:easyb:jar:0.6"
]

INSTINCT = [
  "com.googlecode.instinct:instinct:jar:0.1.6",
  "org.jmock:jmock:jar:2.4.0",
  "org.jmock:jmock-legacy:jar:2.4.0",
  "cglib:cglib-nodep:jar:2.1_3",
  "org.hamcrest:hamcrest-all:jar:1.1",
  "org.objenesis:objenesis:jar:1.0",
  JUNIT,
  ANT
]

