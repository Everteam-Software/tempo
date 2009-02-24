ANT = [ "org.apache.ant:ant:jar:1.7.1" ]

APACHE_COMMONS = {
  :beanutils => "commons-beanutils:commons-beanutils:jar:1.7.0",
  :cli => "commons-cli:commons-cli:jar:1.1",
  :codec => "commons-codec:commons-codec:jar:1.3",
  :collections => "commons-collections:commons-collections:jar:3.2", 
  :daemon => "commons-daemon:commons-daemon:jar:1.0.1", 
  :dbcp => "commons-dbcp:commons-dbcp:jar:1.2.2",
  :digester => "commons-digester:commons-digester:jar:1.7",
  :discovery => "commons-discovery:commons-discovery:jar:0.2",
  :fileupload => "commons-fileupload:commons-fileupload:jar:1.2.1",
  :httpclient => "commons-httpclient:commons-httpclient:jar:3.1",
  :io => "commons-io:commons-io:jar:1.2",
  :lang => "commons-lang:commons-lang:jar:2.3",
  :logging => "commons-logging:commons-logging:jar:1.0.4",
  :pool => "commons-pool:commons-pool:jar:1.4",
  :validator => "commons-validator:commons-validator:jar:1.2.0"
}
APACHE_DERBY = "org.apache.derby:derby:jar:10.2.2.0"
APACHE_DERBY_NET = "org.apache.derby:derbynet:jar:10.2.2.0"
APACHE_DERBY_CLIENT = "org.apache.derby:derbyclient:jar:10.2.2.0"
APACHE_DS = "org.apache.apacheds:noarch-installer:jar:1.5.1a"
AXIOM = group("axiom-api", "axiom-dom", "axiom-impl", :under=>"org.apache.ws.commons.axiom", :version=>"1.2.7")

BACKPORT = "backport-util-concurrent:backport-util-concurrent:jar:3.1"

CASTOR = "castor:castor:jar:1.0"
CAS_CLIENT = "cas:casclient:jar:2.1.1"
CSV = "org.ostermiller:utils:jar:1.07.00"

DB_CONNECTOR = {
  :db2 => "com.ibm.db2.jcc:jcc4:jar:9.2",
  :mysql => "com.mysql.mysql-connector:mysql-connector-java:jar:5.1.6",
  :postgresql => "postgresql:postgresql:jar:8.3-603.jdbc3"
}
DSIG = "javax.xml.xmldsig:xmldsig:jar:1.0"
DOM4J = "dom4j:dom4j:jar:1.6.1"
JODATIME = "joda-time:joda-time:jar:1.5.2"
FOP = "fop:fop:jar:0.20.5"

GERONIMO_SPECS = {
  "jta" => "org.apache.geronimo.specs:geronimo-jta_1.1_spec:jar:1.1",
  "jpa" => "org.apache.geronimo.specs:geronimo-jpa_3.0_spec:jar:1.0",
  "jms" => "org.apache.geronimo.specs:geronimo-jms_1.1_spec:jar:1.0.1"
}

ICAL = "ical4j:ical4j:jar:0.9.20"
INTALIO_STATS = "org.intalio.common:intalio-stats:jar:1.0.2" 

JARGS = "jargs:jargs:jar:1.0"
JAVAMAIL = "geronimo-spec:geronimo-spec-javamail:jar:1.3.1-rc5", "geronimo-spec:geronimo-spec-activation:jar:1.0.2-rc4"
JAXEN = "jaxen:jaxen:jar:1.1.1"
JDOM = "jdom:jdom:jar:1.0"
JENCKS = "jencks:jencks-all:jar:1.1.3"
JETTY = [group("jetty", "jetty-util", :under=>"org.mortbay.jetty", :version=>"6.1.10")]
JPA = "javax.persistence:persistence-api:jar:1.0"
JSON = "json:json-taglib:jar:0.4.1"
JSON_NAGGIT = "org.apache:naggit:jar:1.0.20080807"
JSP_API = "javax.servlet:jsp-api:jar:2.0"
JSTL = "javax.servlet:jstl:jar:1.1.2"
JUNIT = "junit:junit:jar:4.4"
JYAML = "org.jyaml:jyaml:jar:1.3"

LIFERAY = group("portal-impl", "portal-kernel", "util-java", "portal-service", :under=>"com.liferay", :version=>"5.1.0")
LIFT = [group("lift-core", "lift-amqp", "lift-facebook", "lift-textile", "lift-webkit", "lift-widgets", "lift-xmpp", :under=>"net.liftweb",:version=>"0.8")]
LOG4J = "log4j:log4j:jar:1.2.15"

NEETHI = "org.apache.neethi:neethi:jar:2.0.4"

PORTLET_API = "portlet-api:portlet-api:jar:1.0"

QOM = "net.sf.qom:qom:jar:0.1alpha3"
  
SUNACTIVATION = ["javax.activation:activation:jar:1.1.1"]
SUNMAIL = ["javax.mail:mail:jar:1.4.1", SUNACTIVATION]
SERVLET_API = "javax.servlet:servlet-api:jar:2.4" 
SHOAL = [ "net.java.dev.shoal:shoal-jxta:jar:1.0.20071114", "net.java.dev.shoal:shoal-gms:jar:1.0.20071114" ]
SLF4J = group(%w{ slf4j-api slf4j-log4j12 jcl104-over-slf4j }, :under=>"org.slf4j", :version=>"1.4.3")
SPRING = {
  :core => "org.springframework:spring:jar:2.5.5",
  :webmvc_portlet => "org.springframework:spring-webmvc-portlet:jar:2.5.5",
  :webmvc => "org.springframework:spring-webmvc:jar:2.5.5"
}
SPRING_MOCK = [
  "org.springframework:spring-test:jar:2.5.5"
]

STAX_API = [ "stax:stax-api:jar:1.0.1" ]

TAGLIBS = [ "taglibs:standard:jar:1.1.2" ]

URLREWRITE = "org.tuckey:urlrewrite:jar:3.1.0"

WOODSTOX = [ "woodstox:wstx-asl:jar:3.2.4" ]
WS_COMMONS_SCHEMA = "org.apache.ws.commons.schema:XmlSchema:jar:1.3.1"
WSDL4J = [ "wsdl4j:wsdl4j:jar:1.6.1" ]

XERCES = [
  "xerces:xercesImpl:jar:2.9.1",
  "xerces:xmlParserAPIs:jar:2.9.0" ]
XMLBEANS = "org.apache.xmlbeans:xmlbeans:jar:2.4.0"
XMLUNIT = ["xmlunit:xmlunit:jar:1.1"]

PLUTO_CONTAINER = "org.apache.pluto:pluto-container:jar:1.1.4"
PLUTO_TAGLIB = "org.apache.pluto:pluto-taglib:jar:1.1.4"
PLUTO_DRIVER = [
  "org.apache.pluto:pluto-portal-driver:jar:1.1.4",
  "org.apache.pluto:pluto-portal-driver-impl:jar:1.1.4"
]
PLUTO_DESCRIPTORS = [
  "org.apache.pluto:pluto-descriptor-api:jar:1.1.4",
  "org.apache.pluto:pluto-descriptor-impl:jar:1.1.4"
]
PLUTO = [
  PLUTO_CONTAINER,
  PLUTO_TAGLIB,
  PLUTO_DRIVER
]
XALAN = "xalan:xalan:jar:2.7.0"
PLUTO_DEPLOY = [
  PLUTO_CONTAINER,
  PLUTO_TAGLIB,
  PLUTO_DESCRIPTORS,
  XALAN
]

WEBDAV = "org.apache.jackrabbit:webdav:jar:1.4"

ORBEON_COMMONS = [
  APACHE_COMMONS[:beanutils],
  APACHE_COMMONS[:cli],
  APACHE_COMMONS[:codec],
  APACHE_COMMONS[:collections],
  APACHE_COMMONS[:digester],
  APACHE_COMMONS[:discovery],
  APACHE_COMMONS[:fileupload],
  APACHE_COMMONS[:httpclient],
  APACHE_COMMONS[:io],
  APACHE_COMMONS[:lang],
  APACHE_COMMONS[:pool],
  APACHE_COMMONS[:validator],
]

ORBEON_MSV = [
  group("msv", "isorelax", "relaxng-datatype","xsdlib", :under => "msv", :version => "20070407")
]

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
  group("orbeon", "orbeon-xforms-filter", "orbeon-resources-public", "orbeon-resources-private", :under=>"orbeon", :version=>"3.7.0beta1.200811030320")
]
ORBEON_CUSTOM = [
  ORBEON_XERCES,
  "orbeon:jakarta-oro-orbeon:jar:2.0.8",
  "orbeon:saxpath:jar:dev_orbeon",
  "orbeon:xsltc-orbeon:jar:2.5.1",
  "orbeon:xalan-orbeon:jar:2.5.1",
  "orbeon:castor-xml:jar:0.9.4.3",
  "orbeon:metadata-extractor:jar:2.1",
  "orbeon:saxon-8_8_orbeon:jar:20080516"
]

ORBEON_LIBS_NO_JAXEN = [
  JAVAMAIL,
  ORBEON_AXIS,
  ORBEON_COMMONS,
  ORBEON_MSV,
  ORBEON_CORE,
  ORBEON_CUSTOM,
  DOM4J,
  GERONIMO_SPECS["jms"],
  "jtidy:jtidy:jar:8.0-20060801.131059-3",
  "batik:batik-all:jar:1.6"
]

ORBEON_LIBS = [
  ORBEON_LIBS_NO_JAXEN,
  JAXEN
  # "orbeon:jaxen:jar:1.1-beta-1-dev",  
]

# For testing
EASY_B = [
  APACHE_COMMONS[:cli],
  "org.codehaus.groovy:groovy-all:jar:1.5.4",
  "org.disco:easyb:jar:0.6"
]


INSTINCT = [
  "com.googlecode.instinct:instinct:jar:0.1.6",
  "org.jmock:jmock:jar:2.5.0",
  "org.jmock:jmock-legacy:jar:2.5.0",
  "cglib:cglib-nodep:jar:2.1_3",
  "org.hamcrest:hamcrest-all:jar:1.1",
  "org.objenesis:objenesis:jar:1.1",
  JUNIT,
  ANT
]

APACHE_ABDERA = [
  AXIOM,
  APACHE_COMMONS[:beanutils],
  APACHE_COMMONS[:codec],
  APACHE_COMMONS[:collections],
  APACHE_COMMONS[:httpclient],
  APACHE_COMMONS[:lang],
  APACHE_COMMONS[:logging],
  "net.sf.ezmorph:ezmorph:jar:1.0.4",
  STAX_API,
  JAVAMAIL,
  "org.htmlparser:htmlparser:jar:1.0.5",
  JAXEN,
  "net.sf.json-lib:json-lib:jar:2.2.1-jdk15",
  WOODSTOX,
  XALAN,
  "xml-security:xmlsec:jar:1.3.0",
  "apache.incubator:abdera:jar:0.4.0-incubating"  
]

AXIS2 = [
  group("axis2-kernel", "axis2-adb", "axis2-xmlbeans", "axis2-json", :under=>"org.apache.axis2", :version=>"1.4"),
  BACKPORT,
  NEETHI,
  SUNMAIL
]
  
ALFRESCO = [
  "com.alfresco:alfresco-web-client:jar:2.1.0",
  "com.alfresco:alfresco-core:jar:2.1.0",
  "com.alfresco:jta:jar:2.1.0",
  "com.alfresco:acegi-security-0.8.2_patched:jar:2.1.0",
  "com.alfresco:alfresco-repository:jar:2.1.0"
  ]
  
MY_FACES = [
  "org.apache.myfaces.core:myfaces-api:jar:1.1.5",
  "org.apache.myfaces.core:myfaces-impl:jar:1.1.5"
]

APACHE_JPA = [
  APACHE_COMMONS[:lang],
  APACHE_COMMONS[:collections],
  GERONIMO_SPECS.values,
  "org.apache.openjpa:openjpa:jar:1.1.0-645340",
  # "org.apache.openjpa:openjpa:jar:1.2",
  "serp:serp:jar:1.13.1"
]

FREEMARKER = "org.freemarker:freemarker:jar:2.3.14"

CAS_LIBS = [
  group("cas-server-core",:under=>"org.jasig.cas", :version=>"3.2.1.1"),
  group("cas-server-support-ldap",:under=>"org.jasig.cas", :version=>"3.2.1.1"),
  "org.acegisecurity:acegi-security:jar:1.0.6",
  "aopalliance:aopalliance:jar:1.0",
  "aspectj:aspectjrt:jar:1.5.3",
  "aspectj:aspectjweaver:jar:1.5.3",
  JDOM,
  BACKPORT,
  CAS_CLIENT,
  APACHE_COMMONS[:codec],
  APACHE_COMMONS[:collections],
  APACHE_COMMONS[:lang],
  APACHE_COMMONS[:logging],
  "net.sf.ehcache:ehcache:jar:1.4.0-beta2",
  "quartz:quartz:jar:1.5.2",
  "net.sf.jsr107cache:jsr107cache:jar:1.0",
  "inspektr:core:jar:0.6.1",
  "oro:oro:jar:2.0.8",
  "jstl:jstl:jar:1.1.2",
  LOG4J,
  "ognl:ognl:jar:2.6.9",
  "opensaml:opensaml:jar:1.1b",
  "javax.persistence:persistence-api:jar:1.0",
  "person.directory:api:jar:1.1.1",
  "person.directory:impl:jar:1.1.1",
  "quartz:quartz:jar:1.5.2",
  group("spring-aop","spring-beans","spring-context", "spring-context-support", "spring-core", "spring-jdbc","spring-orm", "spring-tx", "spring-web", "spring-webmvc",:under=>"org.springframework", :version=>"2.5.1"),
  group("spring-ldap", "spring-ldap-tiger",:under=>"org.springframework", :version=>"1.2.1"),
  group("spring-binding", "spring-webflow",:under=>"org.springframework", :version=>"1.0.5"), 
  "xml-security:xmlsec:jar:1.4.0",
  "taglibs:standard:jar:1.1.2"
]
