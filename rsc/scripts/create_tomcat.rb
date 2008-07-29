#!/usr/bin/env ruby

require "rubygems"
gem 'hpricot', '>= 0.6'

require 'net/http'
require 'open-uri' 
require "zip/zip"
require 'yaml'
require 'fileutils'
require 'open-uri'
require "buildr"

script_folder = File.dirname(File.expand_path("#{$0}"))

load "#{script_folder}/../build/dependencies.rb"
load "#{script_folder}/../build/repositories.rb"
load "#{script_folder}/../scripts/build_support.rb"

config = YAML::load( File.open( "#{script_folder}/config.yml" ) )

TEMPO_SVN = "#{script_folder}/../.."
DEBUG = config["debug"]
REBUILD_TEMPO = config["rebuild"]
SERVER = config["server"]
LIFERAY = "liferay_v501"
APACHE_MIRROR = find_apache_mirror
TOMCAT_5_DOWNLOAD = APACHE_MIRROR + "tomcat/tomcat-5/v5.5.26/bin/apache-tomcat-5.5.26.zip"
TOMCAT_6_DOWNLOAD = APACHE_MIRROR + "tomcat/tomcat-6/v6.0.16/bin/apache-tomcat-6.0.16.zip"
TOMCAT_ADMIN_DOWNLOAD = APACHE_MIRROR + "tomcat/tomcat-5/v5.5.26/bin/apache-tomcat-5.5.26-admin.zip"
AXIS_DOWNLOAD = APACHE_MIRROR + "ws/axis2/1_4/axis2-1.4-war.zip"
ODE_DOWNLOAD = APACHE_MIRROR + "ode/apache-ode-war-1.2.zip"
LIFERAY_V501 = "http://internap.dl.sourceforge.net/sourceforge/lportal/liferay-portal-tomcat-5.5-5.0.1.zip"  #CA

title "Changing directory"
install_dir = config["install_dir"]
##
FileUtils.mkdir_p install_dir
Dir.chdir install_dir
##

title "Downloading required files"
explain "Downloading tomcat (java web application engine), ode (process engine) and axis (web service engine)"
##
tomcat_folder = download_unzip( TOMCAT_5_DOWNLOAD ) if SERVER != LIFERAY
ode_folder = download_unzip( ODE_DOWNLOAD )
axis_folder = download_unzip( AXIS_DOWNLOAD, false)
liferay_folder = download_unzip( LIFERAY_V501 ) if SERVER == LIFERAY
unzip2( filename_from_url( AXIS_DOWNLOAD ), axis_folder )
##

title "Define install variables"
explain "Defining variables for webapp folders and services folders"
server_folder = tomcat_folder
server_folder = liferay_folder if SERVER == LIFERAY
##
finder = Finder.new
webapp_folder = server_folder + File::SEPARATOR + "webapps"

ode_war = finder.find_war( ode_folder )
axis_war = finder.find_war( axis_folder )
##

title "Install core components (ode, axis)"
explain "Expand ode and axis war file, since we want to do offline deployment and just copy files"
##
wi = WarInstaller.new webapp_folder, true
ode_war_folder   = wi.install ode_war, "ode.war"
axis2_war_folder = wi.install axis_war, "axis2.war"
FileUtils.rm "#{axis2_war_folder}/WEB-INF/classes/log4j.properties", :force => true
##

title "Replace xml beans implementation"
explain "We're using a different version of xbean in tempo which is incompatible with the one in axis. Replacing with a xbean version that is up to date"
##
axis_war_lib = "#{axis2_war_folder}/WEB-INF/lib"
FileUtils.rm "#{axis_war_lib}/xbean-2.2.0.jar", :force => true
locate_and_copy "xmlbeans:xbean:jar:2.3.0", axis_war_lib
##

title "Build tempo (using svn checkout and buildr)"
explain "Using buildr to build the full suite of tempo components"
##
build_tempo if REBUILD_TEMPO
##

title "Install tempo web services"
explain "Now we install the different tempo web services"
explain "Deploy is responsible for deployment tempo artifacts, like forms, and people initiated activities"
explain "Task Attachment services is responsible for keeping the task attachments. Could also be stored in a CMS"
explain "TMS is reponsible for managing the different set of tasks related to human activities"
explain "A Token service based security service, that can also be integrated with other components"
##
si = ServiceInstaller.new( axis2_war_folder )
si.install_tempo_aar( "deploy-ws-service" )
si.install_tempo_aar( "tas-service" )
si.install_tempo_aar( "tms-service" )
si.install_tempo_aar( "security-ws-service" )
##

title "Install tempo web applications"
explain "Now we install the different tempo war files"
explain "FDS is the form dispatcher service, responsible mainly for replacing xml namespaces"
explain "UI-FW is the user interface, where user can handle their tasks and complete them"
explain "WDS service, is like a Web Resources services."
explain "XFM is responsible for displaying the forms to be filled when a user is handling a task."
# explain "CAS is the SSO framework so we can integrate with other security services"
# explain "Pluto, is a portlet container"
# explain "UI-FW-PORTLET is a portlet version of UI-FW meant to be running in Pluto"
##
wi.install_tempo_war( "fds" )
wi.install_tempo_war( "ui-fw" )
wi.install_tempo_war( "wds-service", "wds" )
wi.install_tempo_war( "xforms-manager", "xFormsManager" )
wi.install_tempo_war( "tms-feeds", "feeds")
wi.install_tempo_war( "cas-server-webapp", "cas" )
# wi.install_tempo_war( "ui-pluto", "pluto" )
# wi.install_tempo_war( "ui-fw-portlet")
##

title "Install xpath extension in Ode"
explain "Required library for Ode"
##
ode_webinf = File.expand_path("#{ode_war_folder}/WEB-INF")
ode_processes_folder = "#{server_folder}/var/processes"
processes_folder = "#{TEMPO_SVN}/processes/"
opi = OdeProcessInstaller.new ode_processes_folder, processes_folder
xp_jar = finder.search( "#{processes_folder}/xpath-extensions/target", "jar")
File.copy xp_jar, "#{ode_webinf}/lib", DEBUG
##

title "Install TaskManager in Ode"
explain "The task manager process is a regular process running in Ode, responsible for the management of tasks"
##
opi.install_process_from_tempo_trunk "TaskManager"
##


title "Install AbsenceRequest in Ode"
explain "A sample process, that can be started from the UI-FW"
##
opi.install_process_from_tempo_trunk "AbsenceRequest"
##

title "Installing missing libs into Tomcat"
explain "Some libs are missing in tomcat, so we add them here."
##
lib_folder = "#{server_folder}/common/lib" # tomcat5
#lib_folder = "#{server_folder}/lib" # tomcat6
FileUtils.mkdir_p lib_folder
MISSING_LIBS= [
  # Mysql driver
  DB_CONNECTOR[:mysql],
  # Common libraries
  APACHE_COMMONS[:dbcp],
  APACHE_COMMONS[:collections],
  APACHE_COMMONS[:pool],
  APACHE_COMMONS[:logging],
  STAX_API,
  "stax:stax:jar:1.2.0",
  WSDL4J,
  # Logging libraries
  SLF4J,
  LOG4J,
  # Cas library
  # "javax.servlet:jstl:jar:1.1.2",
  # "taglibs:standard:jar:1.1.2",
  # Pluto libraries
  # PLUTO_DEPLOY,
  #   CASTOR,
  
  # "org.apache.geronimo.modules:geronimo-kernel:jar:2.0.1",
  # "org.apache.geronimo.components:geronimo-transaction:jar:2.0.1",
  # "org.apache.geronimo.components:geronimo-connector:jar:2.0.1",
  # "org.apache.geronimo.specs:geronimo-j2ee-connector_1.5_spec:jar:1.1.1",
  # "org.apache.geronimo.specs:geronimo-jta_1.1_spec:jar:1.1",
  
  XERCES
]
MISSING_LIBS.each {|lib| 
  locate_and_copy( lib, lib_folder )
}
#File.cp "#{TEMPO_SVN}/rsc/liferay501/castor-1.0.5.jar", "#{webapp_folder}/ui-fw/WEB-INF/lib"
##

title "Install registry into common lib"
explain "Registry for deployment services. It is stored as a shared resources in tomcat"
##
registry_jar = finder.find_tempo_component("registry")
File.copy registry_jar, lib_folder
##

title "Copying tempo config files"
explain "Tempo has a set of required config files, generally one for each components."
explain "In this build, we focus on a mysql datasource, so we define a JNDI DS in the tomcat config files"
explain "Also copying the ode config files"
##
tomcat_config_folder = "#{server_folder}/var/config"
FileUtils.mkdir_p tomcat_config_folder
tempo_svn_config_folder = "#{TEMPO_SVN}/config"
config_files = File.join(tempo_svn_config_folder,"*.*")
Dir.glob(config_files) {|x| File.copy(x, tomcat_config_folder, DEBUG)}

mysql_ds_config_files = File.join("#{TEMPO_SVN}/rsc/tempo-sql","*.xml")
Dir.glob(mysql_ds_config_files) {|x| File.copy(x, tomcat_config_folder, DEBUG)}

File.copy "#{TEMPO_SVN}/rsc/tomcat/ode-axis2.properties", tomcat_config_folder
##

title "Creating setenv file (java opts and config)"
explain "Settings java options, including memory settings"
##
tomcat_bin_folder = server_folder + File::SEPARATOR + "bin" + File::SEPARATOR
create_mode = File::CREAT|File::TRUNC|File::RDWR
# script for unix
file_path = tomcat_bin_folder + "setenv.sh"
file = File.new file_path,create_mode
if SERVER == LIFERAY
  file.puts "export JAVA_OPTS=\"-XX:MaxPermSize=256m -server -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=$CATALINA_HOME/var/config -Dorg.apache.ode.configDir=$CATALINA_HOME/var/config -Djava.security.auth.login.config=$CATALINA_HOME/conf/jaas.config\""
else
  file.puts "export JAVA_OPTS=\"-XX:MaxPermSize=256m -server -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=$CATALINA_HOME/var/config -Dorg.apache.ode.configDir=$CATALINA_HOME/var/config\""
end
# script for windows
file_path = tomcat_bin_folder + "setenv.bat"
file = (File.new file_path,create_mode)
if SERVER == LIFERAY
  file.puts "set JAVA_OPTS=-XX:MaxPermSize=256m -server -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=%CATALINA_HOME%\\var\\config -Dorg.apache.ode.configDir=%CATALINA_HOME%\\var\\config -Djava.security.auth.login.config=$CATALINA_HOME\\conf\\jaas.config"
else
  file.puts "set JAVA_OPTS=-XX:MaxPermSize=256m -server -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=%CATALINA_HOME%\\var\\config -Dorg.apache.ode.configDir=%CATALINA_HOME%\\var\\config"
end
##

title "Changing file permissions (shell scripts)"
explain "Needed so all the scripts can be exectutable just after the build"
##
shfiles = File.join(tomcat_bin_folder, "*.sh")
Dir.glob(shfiles) {|x| FileUtils.chmod 0755, x, :verbose => DEBUG }
##

title "Creating deploy folder and adding AbsenceRequest"
explain "Adding the forms needed to run the AbsenceRequest example"
##
ar_zip = finder.find_tempo_component( "forms" + File::SEPARATOR + "AbsenceRequest", "zip")
deploy_folder = "#{server_folder}/var/deploy/AbsenceRequest/"
FileUtils.mkdir_p deploy_folder
unzip2(ar_zip, deploy_folder, true)
##

title "Copying tomcat config xml files (JNDI resources)"
explain "Making the deploy registry and the mysql DS available to all tomcat application"
##
Dir.glob(File.join("#{TEMPO_SVN}/rsc/tomcat", "*.*")) {|x| File.copy(x,"#{server_folder}/conf", DEBUG)}
Dir.glob(File.join("#{server_folder}/conf", "log4j.properties")) {|x| File.move(x,"#{server_folder}/common/classes", DEBUG)}
##

## For liferay specific
if SERVER == LIFERAY
  title "For liferay 5.0.1 specific configuration"
  explain "Config liferay portal to use cas authentication"
  File.cp "#{TEMPO_SVN}/rsc/liferay501/web.xml", "#{webapp_folder}/ROOT/WEB-INF"
  File.cp "#{TEMPO_SVN}/rsc/liferay501/forliferay-ticketfilter-1.0.1.jar", "#{webapp_folder}/ROOT/WEB-INF/lib"
  File.cp "#{TEMPO_SVN}/rsc/liferay501/portal-ext.properties", "#{webapp_folder}/ROOT/WEB-INF/classes"
  
  explain "Deploy the ui-fw-portlet"
  # deploy the ui-fw-portlet file
  wi.install_tempo_war( "ui-fw-portlet" )
  # copy the tld file
  FileUtils.mkdir_p "#{webapp_folder}/ui-fw-portlet/WEB-INF/tld"
  File.copy "#{TEMPO_SVN}/rsc/liferay501/liferay-portlet.tld", "#{webapp_folder}/ui-fw-portlet/WEB-INF/tld"
  # copy the util jars
  Dir.glob("#{webapp_folder}/ROOT/WEB-INF/lib/util*.jar") {|x| File.copy x, "#{webapp_folder}/ui-fw-portlet/WEB-INF/lib"}
  # delete some conflict jar files
  Dir.glob(File.join("#{webapp_folder}/ui-fw-portlet/WEB-INF/lib", "portlet*.jar")) {|x| File.delete x}
end

title "Set up CAS server and client"
explain "All the webapp should use the same version of casclient"
##
File.copy "#{TEMPO_SVN}/rsc/liferay501/tempokeystore", tomcat_config_folder
Dir.glob(File.join("#{TEMPO_SVN}/rsc/liferay501", "server.xml")) {|x| File.copy(x,"#{server_folder}/conf", DEBUG)}
Dir.glob(File.join("#{webapp_folder}/cas/WEB-INF/lib", "casclient*.jar")) {|x| File.cp x, "#{server_folder}/common/lib"}
Dir.glob(File.join("#{webapp_folder}", "**/casclient*.jar")) {|x| File.delete x}
File.copy "#{TEMPO_SVN}/rsc/liferay501/xmldsig.jar", "#{webapp_folder}/cas/WEB-INF/lib"
##
  
title "Deleting war files from webapp folder"
explain "Make some space in the webapp folder by removing unused war files"
##
Dir.glob(File.join("#{webapp_folder}", "*.war")) {|x| File.delete x}
##

title "Delete conflicting jar files"
explain "Delete files conflicting with tomcat common jar files"
##
Dir.glob(File.join("#{axis2_war_folder}", "**/dom4j*.jar")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "**/servlet-api-2.4.jar")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "**/jsp-api-2.0.jar")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "**/log4j-*.jar")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "**/slf4j*.jar")) {|x| File.delete x}
#Dir.glob(File.join("#{webapp_folder}", "**/xerces*.jar")) {|x| File.delete x}
Dir.glob(File.join("#{server_folder}/common/endorsed", "*.jar")) {|x| File.delete x}
##

title "Almost done !"
explain "Now \ a mysql database named \"bpms\" with access to user <root> and no password"
explain "Load the ode schema into mysql from the file #{TEMPO_SVN}/rsc/tempo-sql/ode-mysql.sql"
explain "Once this is done, start tomcat with the following command:"
explain "./catalina.sh run"
explain "Now you can browse http://localhost:8080/ui-fw/ and login with user <admin> and password <changeit>"

title  "Enjoy!!"