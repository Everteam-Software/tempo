require "rubygems"
require "buildr"
require "buildr/xmlbeans"
# require "buildr/openjpa"
# require "buildr/cobertura"

# Keep this structure to allow the build system to update version numbers.

# This branch is a copy of Tempo 6.0.85
 

VERSION_NUMBER = "6.5.0.005-SNAPSHOT"
 

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
    def package_as_mar(file_name) #:nodoc:
      Packaging::Java::JarTask.define_task(file_name).tap do |jar|
        jar.with :manifest=>manifest, :meta_inf=>meta_inf
        jar.with [compile.target, resources.target].compact
        jar.path('META-INF').include path_to(:source, :main, :axis2, 'module.xml'), :as=>'module.xml'
      end
    end

    libs = [AXIS2, AXIOM, APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], DOM4J, JAXEN, LOG4J, SERVLET_API, SLF4J, STAX_API]
    compile.with libs 
    resources.filter.using "version" => VERSION_NUMBER
    test.with XMLUNIT, INSTINCT
    unless ENV["LIVE"] == 'yes'
      test.exclude '*RemoteFDSTest*'
    end
    package :war
    package :mar
  end

  desc "Task Attachment Service"
  define "tas-service" do
    compile.with APACHE_COMMONS[:httpclient], APACHE_COMMONS[:io], AXIOM, AXIS2, JAXEN, SLF4J, STAX_API, WEBDAV, SECURITY_WS_CLIENT_ONLY

    test.with SECURITY_WS_COMMON, APACHE_COMMONS[:codec], LOG4J, SUNMAIL, WSDL4J, WS_COMMONS_SCHEMA, WOODSTOX, INSTINCT
    test.exclude '*TestUtils*'

    # require live Axis2 instance
    unless ENV["LIVE"] == 'yes'
      test.exclude '*Axis2TASService*'
      test.exclude '*WDSStorageTest*'
      test.exclude '*OptionalStorage*'
    end

    package :jar
    package(:aar).with(:libs => [ 
        SECURITY_WS_CLIENT, WEB_NUTSNBOLTS, APACHE_COMMONS[:httpclient],JASYPT, APACHE_COMMONS[:codec], JAXEN, SLF4J, SPRING[:core], WEBDAV])
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
    
    test.with APACHE_DERBY, LOG4J, DB_CONNECTOR.values, XMLUNIT, WOODSTOX, INSTINCT, SECURITY_WS_COMMON, APACHE_COMMONS[:pool], APACHE_COMMONS[:collections], APACHE_COMMONS[:httpclient]
    test.exclude '*TestUtils*'
    unless ENV["LIVE"] == 'yes'
      test.exclude '*N3AuthProviderLiveTest*'
    end
    
    package :jar
  end
  
  desc "Task Management Service Client"
  define "tms-client" do
    compile.with projects("tms-axis", "tms-common","tms-service"), APACHE_COMMONS[:httpclient],BPMS_COMMON,
      APACHE_JPA, AXIOM, AXIS2, SLF4J, STAX_API, WSDL4J, WS_COMMONS_SCHEMA, XMLBEANS,SPRING[:core]

    test.with APACHE_COMMONS[:pool],projects("tms-service"), APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], LOG4J, WOODSTOX, SUNMAIL, SECURITY_WS_CLIENT, FREEMARKER, CASTOR, XERCES

    test.exclude '*TestUtils*'

    unless ENV["LIVE"] == 'yes'
      test.exclude '*RemoteTMSClientTest*'
      test.exclude '*RemoteReassginTaskTest*'
      test.exclude '*RemoteAbsenceRequestTest*'
    end
    unless ENV["DESIGNER"] == 'yes'
      test.exclude '*UTFURLTest*'
    end
    package :jar 
  end
  
  desc "Task Management Service"
  define "tms-service" do
    libs = projects("tms-axis", "tms-common", "dao-nutsNbolts"),JASYPT,
     APACHE_JPA, APACHE_COMMONS[:pool], AXIOM, AXIS2, JAXEN, SLF4J, SPRING[:core], STAX_API, XMLBEANS, DB_CONNECTOR.values, DEPLOY_API, SECURITY_WS_CLIENT_ONLY, WEB_NUTSNBOLTS,XALAN,JASYPT, BPMS_COMMON, ASPECTJ 
    compile.with libs
    test.with libs + [REGISTRY, APACHE_DERBY, APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], CASTOR, EASY_B, LOG4J, DB_CONNECTOR.values, SUNMAIL, WSDL4J, WS_COMMONS_SCHEMA, WOODSTOX, XERCES, XMLUNIT, INSTINCT]

    test.using :properties => 
      { 
        "org.intalio.tempo.configDirectory" => _("src/test/resources"),
        "jpa.config.file" => "jpa.properties"
      }

    puts " LIVE Env is  set to ::: " + ENV["LIVE"]
    # require live Axis2 instance
    unless ENV["LIVE"] == 'yes'
      test.exclude '*TMSAxis2RemoteTest*'
      test.exclude '*RemoteReassginTaskTest*'
      test.exclude "*ReassignTaskLiveTest*"
    end
    test.exclude '*TestUtils*'

    package :jar
    package(:aar).with :libs => 
        [ projects("tms-axis", "tms-common", "dao-nutsNbolts"), BPMS_COMMON,JASYPT,OPENSSO_CLIENT_SDK, CAS_CLIENT,CASTOR,APACHE_COMMONS[:pool], APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], APACHE_JPA, SLF4J, SPRING[:core], DEPLOY_API, REGISTRY, SECURITY_WS_CLIENT, WEB_NUTSNBOLTS, ASPECTJ, SPRING[:aop] ] 
  end
  
  desc "User-Interface Framework"

  define "ui-fw" do
    libs = projects("tms-axis", "tms-client", "tms-common","dao-nutsNbolts","tms-service"),
           SECURITY_WS_CLIENT,
           WEB_NUTSNBOLTS,
           #BPMS_COMMON,
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
	   JASYPT,
	   ICU4J,
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
           XMLBEANS,
           SOJO,
           SPRING_JSON,
           BPMS_COMMON
    
    compile.with libs, JSP_API, SERVLET_API, CAS_CLIENT, PORTLET_API

    # use the following command to build the prepared portlet version
    # buildr install -e portlet
    #
    # this just copies over the configured web.xml for portlet 
    web_xml = (ENV['BUILDR_ENV'] == 'portlet' ? 'web-cas.xml' : 'web.xml')
    web_xml = _("src/main/webapp/WEB-INF/"+web_xml) 
    
    resources.filter.using "version" => VERSION_NUMBER
    test.with JAXEN, XMLUNIT, INSTINCT, LOG4J, SPRING_MOCK
	package(:jar)
    package(:war).include(web_xml, :as=>'WEB-INF/web.xml').with(:libs=>libs)
  end

  desc "Workflow Deployment Service"
  define "wds-service" do |project|
    libs = [ projects("dao-nutsNbolts", "tms-client", "tms-axis", "tms-common" ), 
      AXIS2, AXIOM, APACHE_COMMONS[:io], APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], APACHE_COMMONS[:pool], APACHE_JPA, DOM4J, JAXEN, SLF4J, SPRING[:core], STAX_API, WS_COMMONS_SCHEMA, WSDL4J, WOODSTOX, XERCES, XMLBEANS, DEPLOY_API, REGISTRY, SECURITY, WEB_NUTSNBOLTS ]
    test_libs = libs + [SERVLET_API, EASY_B, INSTINCT, DB_CONNECTOR.values]
	compile.with(test_libs + projects("ui-fw"))
    compile { open_jpa_enhance }
    test.with APACHE_DERBY, LOG4J
    resources.filter.using "version" => VERSION_NUMBER
    task "package" => generate_sql([project], "workflow.deployment")
    
    package :jar
	package(:war).with(:libs=> [libs , 'ui-fw/target/*.jar'])
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

  desc "Liferay 606 Integration Portlet"
  define "liferay-606-portlet" do |project|
    web_xml = _("src/main/webapp/WEB-INF/web.xml") 
    libs = LIFERAY, PORTLET_API, CAS_CLIENT, LOG4J, DOM4J
    packlibs=DB_CONNECTOR.values,LOG4J
    compile.with libs
     package(:war).with(:libs=>packlibs)
  end
end
