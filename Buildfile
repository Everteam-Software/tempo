require "rubygems"
require "buildr"
require "buildr/xmlbeans"
# require "buildr/openjpa"
# require "buildr/cobertura"

# Keep this structure to allow the build system to update version numbers.

# This branch is a copy of Tempo 6.0.85
 
VERSION_NUMBER = "6.5.0.005-SNAPSHOT"
DP_VERSION_NUMBER="1.0.1"

require "rsc/build/repositories.rb"
# We need to download the artifact before we load the same
artifact("org.intalio.common:dependencies:rb:#{DP_VERSION_NUMBER}").invoke

DEPENDENCIES = "#{ENV['HOME']}/.m2/repository/org/intalio/common/dependencies/#{DP_VERSION_NUMBER}/dependencies-#{DP_VERSION_NUMBER}.rb"
unless ENV["M2_REPO"] != ''
DEPENDENCIES = "#{ENV['M2_REPO']}/org/intalio/common/dependencies/#{DP_VERSION_NUMBER}/dependencies-#{DP_VERSION_NUMBER}.rb"
end

load DEPENDENCIES

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
    compile.with SECURITY[:nutbolts], APACHE_JPA, JPA, SLF4J.values
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

    libs = [AXIS2.values, AXIOM, APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], DOM4J, JAXEN, LOG4J, SERVLET_API, SLF4J.values, STAX_API]
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
    compile.with APACHE_COMMONS[:httpclient], APACHE_COMMONS[:io], AXIOM, AXIS2.values, JAXEN, SLF4J.values, STAX_API, WEBDAV, SECURITY.values

    test.with SECURITY[:common], APACHE_COMMONS[:codec], LOG4J, SUNMAIL, WSDL4J, WS_COMMONS_SCHEMA, WOODSTOX, INSTINCT
    test.exclude '*TestUtils*'

    # require live Axis2 instance
    unless ENV["LIVE"] == 'yes'
      test.exclude '*Axis2TASService*'
      test.exclude '*WDSStorageTest*'
      test.exclude '*OptionalStorage*'
    end

    package :jar
    package(:aar).with(:libs => [ 
        SECURITY[:client], SECURITY[:nutbolts], APACHE_COMMONS[:httpclient],JASYPT, APACHE_COMMONS[:codec], JAXEN, SLF4J.values, SPRING[:core], WEBDAV])
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
    compile.with projects("tms-axis"),  SECURITY.values, APACHE_JPA, APACHE_COMMONS[:pool], APACHE_COMMONS[:collections], AXIS2.values, AXIOM, DOM4J, JAXEN, SLF4J.values, SPRING[:core], STAX_API, XERCES[:impl],  XERCES[:parserapi], XMLBEANS.values, JAXP_RI
    
    compile { open_jpa_enhance }
    task "package" => generate_sql([project], "workflow.tms")
    
    test.with APACHE_DERBY, LOG4J, DB_CONNECTOR.values, XMLUNIT, WOODSTOX, INSTINCT, SECURITY[:common], APACHE_COMMONS[:pool], APACHE_COMMONS[:collections], APACHE_COMMONS[:httpclient],JAXP_RI
    test.exclude '*TestUtils*'
    unless ENV["LIVE"] == 'yes'
      test.exclude '*N3AuthProviderLiveTest*'
    end
    
    package :jar
  end
  
  desc "Task Management Service Client"
  define "tms-client" do
    compile.with projects("tms-axis", "tms-common","tms-service"), APACHE_COMMONS[:httpclient],BPMS_COMMON,
      APACHE_JPA, AXIOM, AXIS2.values, SLF4J.values, STAX_API, WSDL4J, WS_COMMONS_SCHEMA, XMLBEANS.values,SPRING[:core]

    test.with APACHE_COMMONS[:pool],projects("tms-service"), APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], LOG4J, WOODSTOX, SUNMAIL,SECURITY[:api], SECURITY[:client], FREEMARKER, CASTOR, XERCES[:impl],  XERCES[:parserapi]

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
    libs = projects("tms-axis", "tms-common", "dao-nutsNbolts"),JASYPT, APACHE_JPA, APACHE_COMMONS[:pool], AXIOM, AXIS2.values, JAXEN, SLF4J.values, SPRING[:core], STAX_API, XMLBEANS.values, DB_CONNECTOR.values, DEPLOY_API,  SECURITY.values,XALAN,JASYPT, BPMS_COMMON, ASPECTJ.values 
    compile.with libs
    test.with libs + [ APACHE_JPA, REGISTRY, APACHE_DERBY, APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], SECURITY.values, CASTOR, EASY_B, LOG4J, DB_CONNECTOR.values, SUNMAIL, WSDL4J, WS_COMMONS_SCHEMA, WOODSTOX, XERCES[:impl],  XERCES[:parserapi], XMLUNIT, INSTINCT]

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
        [ projects("tms-axis", "tms-common", "dao-nutsNbolts"),
BPMS_COMMON,JASYPT,OPENSSO_CLIENT_SDK, CAS_CLIENT,CASTOR,APACHE_COMMONS[:pool],
APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], APACHE_JPA, SERP, SLF4J.values,
SPRING[:core], DEPLOY_API, REGISTRY,SECURITY.values,
ASPECTJ.values, SPRING[:aop] ] 
  end
  
  desc "User-Interface Framework"

  define "ui-fw" do
    libs = projects("tms-axis", "tms-client", "tms-common","dao-nutsNbolts","tms-service"),
           SECURITY.values,
           APACHE_ABDERA,
           APACHE_COMMONS[:io],
           APACHE_COMMONS[:httpclient],
           APACHE_COMMONS[:codec],
           APACHE_JPA,
           AXIOM, 
           AXIS2.values, 
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
           SLF4J.values, 
           STAX_API, 
           TAGLIBS, 
           URLREWRITE,
           WOODSTOX, 
           WSDL4J,
           WS_COMMONS_SCHEMA,
           XERCES[:impl], 
           XERCES[:parserapi], 
           XMLBEANS.values,
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
    test.with JAXEN, XMLUNIT, INSTINCT, LOG4J, SPRING[:mock]
	package(:jar)
    package(:war).include(web_xml, :as=>'WEB-INF/web.xml').with(:libs=>libs)
  end

  desc "Workflow Deployment Service"
  define "wds-service" do |project|
    libs = [ projects("dao-nutsNbolts", "tms-client", "tms-axis", "tms-common" ), 
      AXIS2.values, AXIOM, APACHE_COMMONS[:io], APACHE_COMMONS[:httpclient], APACHE_COMMONS[:codec], APACHE_COMMONS[:pool], APACHE_JPA, DOM4J, JAXEN, SLF4J.values, SPRING[:core], STAX_API, WS_COMMONS_SCHEMA, WSDL4J, WOODSTOX, XERCES[:impl],  XERCES[:parserapi], XMLBEANS.values, DEPLOY_API, REGISTRY, SECURITY.values]
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
