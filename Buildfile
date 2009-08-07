require "rubygems"
require "buildr"
require "buildr/xmlbeans"
# require "buildr/openjpa"
# require "buildr/cobertura"

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "6.0.0.64-SNAPSHOT"

require "rsc/build/dependencies.rb"
require "rsc/build/repositories.rb"
# leave this require after dependencies.rb so the same jpa version is used throughout the whole build
require "rsc/buildr-tasks/openjpa" # slight change from buildr, version of openjpa

require "rsc/buildr-tasks/generate_sql"

ENV['JAVA_OPTS'] ||= '-Dfile.encoding=UTF-8'


desc "Tempo Workflow"
define "tempo" do
  project.version = VERSION_NUMBER
  project.group = "org.intalio.tempo"
  
  compile.options.target = "1.5"

  define "dao-nutsNbolts" do
    compile.with WEB_NUTSNBOLTS, APACHE_JPA, SLF4J
    package :jar
  end

  desc "Form Dispatcher Servlet"
  define "fds" do
    libs = [AXIS2, APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], DOM4J, JAXEN, LOG4J, SERVLET_API, SLF4J, STAX_API]
    compile.with libs 
    resources.filter.using "version" => VERSION_NUMBER
    test.with XMLUNIT, INSTINCT
    unless ENV["LIVE"] == 'yes'
      test.exclude '*RemoteFDSTest*'
    end
    package :war
  end

  desc "Task Attachment Service"
  define "tas-service" do
    compile.with APACHE_COMMONS[:httpclient], AXIOM, AXIS2, JAXEN, SLF4J, STAX_API, WEBDAV, SECURITY_WS_CLIENT_ONLY

    test.with SECURITY_WS_COMMON, APACHE_COMMONS[:codec], LOG4J, SUNMAIL, WSDL4J, WS_COMMONS_SCHEMA, WOODSTOX, INSTINCT
    test.exclude '*TestUtils*'

    # require live Axis2 instance
    unless ENV["LIVE"] == 'yes'
      test.exclude '*Axis2TASService*'
      test.exclude '*WDSStorageTest*'
      test.exclude '*SlingStorage*'
    end

    package :jar
    package(:aar).with(:libs => [ 
        SECURITY_WS_CLIENT, WEB_NUTSNBOLTS, APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], JAXEN, SLF4J, SPRING[:core], WEBDAV])
  end

  desc "Xml Beans generation"
  define "tms-axis" do 
    FileUtils.mkdir_p _('target/classes/') # workaround for bug in buildr when no classes to be compiled.
    compile_xml_beans _("../tms-service/src/main/axis2")
    package(:jar).include _('target/generated/xmlbeans/'), :as=>'.'
    package(:jar).include _('target/classes/'), :as=>'.' 
  end

  desc "Task Management Services Common Library"
  define "tms-common" do |project|
    compile.with projects("tms-axis"), SECURITY_WS_CLIENT_ONLY, APACHE_JPA, APACHE_COMMONS[:pool], APACHE_COMMONS[:collections], AXIS2, AXIOM, DOM4J, JAXEN, SLF4J, SPRING[:core], STAX_API, XERCES, XMLBEANS
    
    compile { open_jpa_enhance }
    task "package" => generate_sql([project], "workflow.tms")
    
    test.with APACHE_DERBY, LOG4J, DB_CONNECTOR.values, XMLUNIT, WOODSTOX, INSTINCT, SECURITY_WS_COMMON
    test.exclude '*TestUtils*'
    unless ENV["LIVE"] == 'yes'
      test.exclude '*N3AuthProviderLiveTest*'
    end
    
    package :jar
  end
  
  desc "Task Management Service Client"
  define "tms-client" do
    compile.with projects("tms-axis", "tms-common"), 
      APACHE_JPA, AXIOM, AXIS2, SLF4J, STAX_API, WSDL4J, WS_COMMONS_SCHEMA, XMLBEANS

    test.with APACHE_COMMONS[:pool], APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], LOG4J, WOODSTOX, SUNMAIL, SECURITY_WS_CLIENT, FREEMARKER

    test.exclude '*TestUtils*'

    unless ENV["LIVE"] == 'yes'
      test.exclude '*RemoteTMSClientTest*'
      test.exclude '*RemoteAbsenceRequestTest*'
    end
    unless ENV["DESIGNER"] == 'yes'
      test.exclude '*UTFURLTest*'
    end
    package :jar 
  end
  
  desc "Task Management Service"
  define "tms-service" do
    libs = projects("tms-axis", "tms-common", "tms-client", "dao-nutsNbolts"),
    APACHE_JPA, APACHE_COMMONS[:pool], AXIOM, AXIS2, JAXEN, SLF4J, SPRING[:core], STAX_API, XMLBEANS, DB_CONNECTOR.values, DEPLOY_API, SECURITY_WS_CLIENT_ONLY, WEB_NUTSNBOLTS
  
    compile.with libs
    test.with libs + [REGISTRY, APACHE_DERBY, APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], CASTOR, EASY_B, LOG4J, DB_CONNECTOR.values, SUNMAIL, WSDL4J, WS_COMMONS_SCHEMA, WOODSTOX, XERCES, XMLUNIT, INSTINCT]

    test.using :properties => 
      { 
        "org.intalio.tempo.configDirectory" => _("src/test/resources"),
        "jpa.config.file" => "jpa.properties"
      }

    # require live Axis2 instance
    unless ENV["LIVE"] == 'yes'
      test.exclude '*TMSAxis2RemoteTest*'
      test.exclude '*RemoteReassginTaskTest*'
      test.exclude "*ReassignTaskLiveTest*"
    end
    test.exclude '*TestUtils*'

    package :jar
    package(:aar).with :libs => 
        [ projects("tms-axis", "tms-common", "dao-nutsNbolts"), APACHE_COMMONS[:pool], APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], APACHE_JPA, SLF4J, SPRING[:core], DEPLOY_API, REGISTRY, SECURITY_WS_CLIENT, WEB_NUTSNBOLTS ] 
  end
  
  desc "User-Interface Framework"
  define "ui-fw" do
    libs = projects("tms-axis", "tms-client", "tms-common"),
           SECURITY_WS_CLIENT,
           WEB_NUTSNBOLTS,
           APACHE_ABDERA,
           APACHE_COMMONS[:io],
           APACHE_COMMONS[:httpclient],
           APACHE_COMMONS[:codec],
           APACHE_JPA,
           AXIOM, 
           AXIS2, 
           CSV,
           CASTOR,
           DOM4J,
           ITEXT,
           ICAL,
           INTALIO_STATS, 
           # JODATIME,
           JSON,
           JSON_NAGGIT,
           JSTL,
           OPENSSO_CLIENT_SDK,
           PLUTO,
           SPRING[:core], 
           SPRING[:webmvc],
           SPRING[:webmvc_portlet],
           SLF4J, 
           STAX_API, 
           TAGLIBS, 
           URLREWRITE,
           WOODSTOX, 
           WSDL4J,
           WS_COMMONS_SCHEMA,
           XERCES, 
           XMLBEANS
    
    compile.with libs, JSP_API, SERVLET_API, CAS_CLIENT, PORTLET_API

    # use the following command to build the prepared portlet version
    # buildr install -e portlet
    #
    # this just copies over the configured web.xml for portlet 
    web_xml = (ENV['BUILDR_ENV'] == 'portlet' ? 'web-cas.xml' : 'web.xml')
    web_xml = _("src/main/webapp/WEB-INF/"+web_xml) 
    
    resources.filter.using "version" => VERSION_NUMBER
    test.with JAXEN, XMLUNIT, INSTINCT, LOG4J
    package(:war).include(web_xml, :as=>'WEB-INF/web.xml').with(:libs=>libs)
  end

  desc "Workflow Deployment Service"
  define "wds-service" do |project|
    libs = [ projects("dao-nutsNbolts", "tms-client", "tms-axis", "tms-common"), 
      AXIS2, AXIOM, APACHE_COMMONS[:io], APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], APACHE_COMMONS[:pool], APACHE_JPA, DOM4J, JAXEN, SLF4J, SPRING[:core], STAX_API, WS_COMMONS_SCHEMA, WSDL4J, WOODSTOX, XERCES, XMLBEANS, DEPLOY_API, REGISTRY, SECURITY, WEB_NUTSNBOLTS ]
    test_libs = libs + [SERVLET_API, EASY_B, INSTINCT, DB_CONNECTOR.values]
    compile.with test_libs
    compile { open_jpa_enhance }
    test.with APACHE_DERBY, LOG4J
    resources.filter.using "version" => VERSION_NUMBER
    task "package" => generate_sql([project], "workflow.deployment")
    
    package :jar
    package(:war).with(:libs=>libs)
  end

  
  desc "Liferay CAS ticket filter"
  define "liferay-ticket-filter" do
  	compile.with(LIFERAY, SERVLET_API, PORTLET_API, CAS_CLIENT)
	package :jar
  end

  desc "Liferay alfresco CAS portlet class"
  define "liferay-alfresco-sso" do
	  compile.with(ALFRESCO, APACHE_COMMONS[:logging], APACHE_COMMONS[:fileupload], SERVLET_API, CAS_CLIENT, SPRING[:core], MY_FACES, PORTLET_API, LIFERAY)
    package :jar
  end
end
