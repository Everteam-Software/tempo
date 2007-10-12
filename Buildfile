$LOAD_PATH.unshift "#{ENV['HOME']}/svn/buildr-trunk/lib/"

require "rubygems"
require "buildr"

# Keep this structure to allow the build system to update version numbers.
VERSION_NUMBER = "5.1.0.1-SNAPSHOT"
NEXT_VERSION = "5.1.0.2"

ENV['TEST'] = 'no'

require "dependencies.rb"
require "repositories.rb"

# TODO
# -Synchronize config files w/ integr
# -Make sure tests work

desc "Tempo Workflow"
define "tempo" do
  project.version = VERSION_NUMBER
  project.group = "org.intalio.tempo"

  compile.options.source = "1.5"
  compile.options.target = "1.5"

  desc "Form Dispatcher Servlet"
  define "fds" do
    compile.with AXIS2, COMMONS, LOG4J, SERVLET_API, STAX_API, XOM
    resources.filter.using "version" => VERSION_NUMBER
    package :war
  end  


  desc "Workflow Processes"
  define "processes" do
    define "xpath-extensions" do
      package(:jar)
    end
    
    define "AbsenceRequest" do
      package(:jar)
    end
    
    define "TaskManager" do
      package(:jar)
    end
    
    define "Store" do
      package(:jar)
    end
    
    define "peopleActivity" do
      package(:jar)
    end
  end
  

  desc "Security Framework"
  define "security" do
    compile.with COMMONS, CASTOR, LOG4J, SLF4J, SPRING, XERCES

    test.exclude "*BaseSuite"
    test.exclude "*FuncTestSuite"
    test.exclude "*ldap*"

    package :jar
  end
  
  desc "Security Web-Service Common Library"
  define "security-ws-common" do
    compile.with project("security"), 
                 AXIOM, AXIS2, SLF4J, STAX_API, SPRING
    package(:jar)
  end
  
  desc "Security Web-Service Client"
  define "security-ws-client" do
    compile.with projects("security", "security-ws-common"), 
                 AXIOM, AXIS2, SLF4J, STAX_API, SPRING
    test.with project("security-ws-service").path_to("target/classes"), COMMONS, CASTOR, LOG4J, XERCES, WS_COMMONS_SCHEMA, WSDL4J, WOODSTOX

    # Remember to set JAVA_OPTIONS before starting Jetty
    # export JAVA_OPTIONS=-Dorg.intalio.tempo.configDirectory=/home/boisvert/svn/tempo/security-ws2/src/test/resources
    
    # require live Axis2 instance
    if ENV["LIVE"] == 'yes'
      LIVE_ENDPOINT = "http://localhost:8080/axis2/services/TokenService"
    end
    
    if defined? LIVE_ENDPOINT
      test.junit.using :properties => 
        { "org.intalio.tempo.security.ws.endpoint" => LIVE_ENDPOINT,
          "org.intalio.tempo.configDirectory" => _("src/test/resources") }
    end

    package(:jar).tap do |jar|
      jar.with :meta_inf => project("security-ws-service").path_to("src/main/axis2/*.wsdl")
    end
  end

  
  desc "Security Web-Service"
  define "security-ws-service" do
    compile.with projects("security", "security-ws-common"),
                 AXIOM, AXIS2, SLF4J, SPRING, STAX_API  
    package(:aar).with :libs => [ projects("security", "security-ws-common"), CASTOR, LOG4J, SLF4J, SPRING ]
  end
  
  
  desc "Task Attachment Service"
  define "tas-service" do
    compile.with projects("security", "security-ws-client"), 
                 AXIOM, AXIS2, COMMONS, JUNIT, LOG4J, STAX_API, XOM

    test.with JAVAMAIL, SLF4J, WSDL4J, WS_COMMONS_SCHEMA, WOODSTOX
    test.exclude '*TestUtils*'

    # require live Axis2 instance
    unless ENV["LIVE"] == 'yes'
      test.exclude '*Axis2TASService*'
      test.exclude '*WDSStorageTest*'
    end

    package(:aar).with :libs => [ projects("security", "security-ws-client", "security-ws-common"),
                                  SLF4J, LOG4J ]
  end
  
  desc "Task Management Services Common Library"
  define "tms-common" do
    compile.with projects("security", "security-ws-client"), 
                 AXIOM, LOG4J, SPRING, STAX_API
    test.with SLF4J, WOODSTOX
    test.exclude '*TestUtils*'
    package(:jar)
  end
  
  desc "Task Management Service Client"
  define "tms-client" do
    compile.with project("tms-common"),
                 AXIOM, AXIS2, COMMONS, LOG4J, STAX_API, WS_COMMONS_SCHEMA, WSDL4J

    test.with SLF4J, WOODSTOX
    test.exclude '*TestUtils*'

    unless ENV["LIVE"] == 'yes'
      test.exclude '*RemoteTMSClientTest*'
    end
    package(:jar)
  end
  
  
  desc "Task Management Service"
  define "tms-service" do
    compile.with projects("security", "security-ws-client", "tms-common", "web-nutsNbolts"),
                 AXIOM, AXIS2, COMMONS, LOG4J, SPRING, STAX_API, XOM

    test.with JAVAMAIL, SLF4J, SPRING, WS_COMMONS_SCHEMA, WSDL4J, WOODSTOX

    # require live Axis2 instance
    unless ENV["LIVE"] == 'yes'
      test.exclude '*TMSAxis2RemoteTest*'
    end
    test.exclude '*TestUtils*'
    package(:aar).with :libs => 
        [ projects("security", "security-ws-client", "security-ws-common", "tms-common", "web-nutsNbolts"), 
          LOG4J, SLF4J, SPRING, XOM ] 
  end
  
  
  desc "User-Interface Framework"
  define "ui-fw" do
    libs = projects("security", "security-ws-client", "security-ws-common",
                    "tms-client", "tms-common", "web-nutsNbolts"),
           AXIOM, AXIS2, COMMONS, DOM4J, INTALIO_STATS, JSP_API, JSTL,
           LOG4J, SPRING, SERVLET_API, SLF4J, STAX_API, TAGLIBS, WOODSTOX, 
           WS_COMMONS_SCHEMA, WSDL4J, XERCES, XMLBEANS
    compile.with libs

    dojo = unzip(path_to(compile.target, "dojo") => download(artifact(DOJO)=>DOJO_URL))
    dojo.from_path(DOJO_WIDGET_BASE).include("*").exclude("demos/*", "release/*", "tests/*", "README", "*.txt")

    build dojo
    resources.filter.using "version" => VERSION_NUMBER
    package(:war).with(:libs=>libs).
      include("src/main/config/geronimo/1.0/*", path_to(compile.target, "dojo"))
  end  
  
  desc "Workflow Deployment Service Client"
  define "wds-client" do
    compile.with ANT, COMMONS, JARGS, JUNIT, LOG4J
    package(:jar)
  end

  desc "Workflow Deployment Service"
  define "wds-service" do
    libs = [ project("web-nutsNbolts"), COMMONS, LOG4J, SERVLET_API, SPRING, XERCES ]
    compile.with libs
    resources.filter.using "version" => VERSION_NUMBER
    package(:war).with :libs=>libs
  end

  define "web-nutsNbolts" do
    compile.with project("security"), 
                 COMMONS, INTALIO_STATS, JSP_API, LOG4J, SERVLET_API, SLF4J, SPRING
    package :jar
  end
  
  desc "XForms Manager"
  define "xforms-manager" do
    resources.filter.using "version" => VERSION_NUMBER
    package(:war).with :libs=> ORBEON_LIBS
  end
end
