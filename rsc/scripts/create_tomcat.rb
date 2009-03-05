#!/usr/bin/env ruby

require "rubygems"
require "hpricot"
require 'net/http'
require 'open-uri' 
require "zip/zip"
require 'yaml'
require 'fileutils'
require 'open-uri'
require "buildr"

script_folder = File.dirname(File.expand_path("#{$0}"))
load "#{script_folder}/../build/repositories.rb"
load "#{script_folder}/../build/dependencies.rb"
load "#{script_folder}/../scripts/build_support.rb"

title "Loading build configuration"
explain "Loading the build variables"
##
config = YAML::load( File.open( "#{script_folder}/config.yml" ) )
TEMPO_SVN = "#{script_folder}/../.."
DEBUG = config["debug"]
REBUILD_TEMPO = config["rebuild"]
SERVER = config["server"]
ADD_ALFRESCO = config["add_alfresco"]
ADD_LDAP = config["add_ldap"]
ADD_LIFERAY = "liferay_510"
APACHE_MIRROR = find_apache_mirror
TOMCAT_5_DOWNLOAD = APACHE_MIRROR + "tomcat/tomcat-5/v5.5.26/bin/apache-tomcat-5.5.26.zip"
TOMCAT_6_DOWNLOAD = APACHE_MIRROR + "tomcat/tomcat-6/v6.0.16/bin/apache-tomcat-6.0.16.zip"
TOMCAT_ADMIN_DOWNLOAD = APACHE_MIRROR + "tomcat/tomcat-5/v5.5.26/bin/apache-tomcat-5.5.26-admin.zip"
AXIS_DOWNLOAD = APACHE_MIRROR + "ws/axis2/1_4_1/axis2-1.4.1-war.zip"
ODE_RELEASES = {
  :v1_2 => APACHE_MIRROR + "ode/apache-ode-war-1.2.zip",
  :v1_2_snapshot => "http://www.intalio.org/public/ode/apache-ode-1.2-SNAPSHOT-700632.zip",
  :v1_3_snapshot => "http://www.intalio.org/public/ode/apache-ode-1.3-SNAPSHOT-745704.zip"
}
ODE_DOWNLOAD = ODE_RELEASES[:v1_3_snapshot]
# LIFERAY_5 = "http://downloads.sourceforge.net/sourceforge/lportal/liferay-portal-tomcat-5.5-5.1.1.zip"  #CA
LIFERAY_5 = "http://downloads.sourceforge.net/sourceforge/lportal/liferay-portal-tomcat-5.5-5.1.0.zip"
ALFRESCO = {
  :v2_1 => "http://downloads.sourceforge.net/sourceforge/alfresco/alfresco-community-war-2.1.0.zip",
  :v2_9 => "http://downloads.sourceforge.net/sourceforge/alfresco/alfresco-community-war-2.9.0B.zip",
  :v3_0 => "http://downloads.sourceforge.net/sourceforge/alfresco/alfresco-labs-war-3a.1032.zip"
}
INSTALL_DIR = config["install_dir"]
##

title "Changing directory"
##
FileUtils.mkdir_p INSTALL_DIR
Dir.chdir INSTALL_DIR
##

title "Downloading required files"
explain "Downloading tomcat (java web application engine), ode (process engine) and axis (web service engine)"
##
tomcat_folder = download_unzip( TOMCAT_5_DOWNLOAD ) if SERVER != ADD_LIFERAY
ode_folder = download_unzip( ODE_DOWNLOAD )
axis_folder = download_unzip( AXIS_DOWNLOAD, false)
liferay_folder = download_unzip( LIFERAY_5 ) if SERVER == ADD_LIFERAY
unzip2( filename_from_url( AXIS_DOWNLOAD ), axis_folder )
##

title "Define install variables"
explain "Defining variables for webapp folders and services folders"
##
server_folder = tomcat_folder
server_folder = liferay_folder if SERVER == ADD_LIFERAY
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
explain "CAS is the SSO framework so we can integrate with other security services"
##
wi.install_tempo_war( "fds" )
wi.install_tempo_war( "ui-fw" )
wi.install_tempo_war( "wds-service", "wds" )
wi.install_tempo_war( "xforms-manager", "xFormsManager" )
wi.install_tempo_war( "cas-webapp", "cas" )
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
[
  DB_CONNECTOR[:mysql],
  APACHE_COMMONS[:dbcp],
  APACHE_COMMONS[:collections],
  APACHE_COMMONS[:pool],
  STAX_API,
  "stax:stax:jar:1.2.0",
  WSDL4J,
  SLF4J,
  LOG4J,
  XERCES
].each {|lib| 
  locate_and_copy( lib, lib_folder )
}
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
File.copy "#{TEMPO_SVN}/rsc/tomcat/ode-axis2.properties", tomcat_config_folder
File.copy "#{TEMPO_SVN}/rsc/tomcat/axis2.xml", "#{webapp_folder}/axis2/WEB-INF/conf"
##

title "Creating setenv file (java opts and config)"
explain "Settings java options, including memory settings"
##
tomcat_bin_folder = server_folder + File::SEPARATOR + "bin" + File::SEPARATOR
create_mode = File::CREAT|File::TRUNC|File::RDWR
# script for unix
file_path = tomcat_bin_folder + "setenv.sh"
file = File.new file_path,create_mode
if SERVER == ADD_LIFERAY
  file.puts "export JAVA_OPTS=\"-XX:MaxPermSize=256m -server -Djavax.net.ssl.trustStore=$CATALINA_HOME/var/config/intalio-keystore.jks -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=$CATALINA_HOME/var/config -Dorg.apache.ode.configDir=$CATALINA_HOME/var/config -Djava.security.auth.login.config=$CATALINA_HOME/conf/jaas.config\""
else
  file.puts "export JAVA_OPTS=\"-XX:MaxPermSize=256m -server -Djavax.net.ssl.trustStore=$CATALINA_HOME/var/config/intalio-keystore.jks -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=$CATALINA_HOME/var/config -Dorg.apache.ode.configDir=$CATALINA_HOME/var/config\""
end
# script for windows
file_path = tomcat_bin_folder + "setenv.bat"
file = (File.new file_path,create_mode)
if SERVER == ADD_LIFERAY
  file.puts "set JAVA_OPTS=-XX:MaxPermSize=256m -server -Djavax.net.ssl.trustStore=%CATALINA_HOME%/var/config/intalio-keystore.jks -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=%CATALINA_HOME%\\var\\config -Dorg.apache.ode.configDir=%CATALINA_HOME%\\var\\config -Djava.security.auth.login.config=%CATALINA_HOME%\\conf\\jaas.config"
else
  file.puts "set JAVA_OPTS=-XX:MaxPermSize=256m -server -Djavax.net.ssl.trustStore=%CATALINA_HOME%/var/config/intalio-keystore.jks -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=%CATALINA_HOME%\\var\\config -Dorg.apache.ode.configDir=%CATALINA_HOME%\\var\\config"
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
if SERVER == ADD_LIFERAY
  title "Liferay configuration"
  explain "Config liferay portal to use cas authentication"
  File.cp "#{TEMPO_SVN}/rsc/liferay510/web.xml", "#{webapp_folder}/ROOT/WEB-INF"
  Dir.glob("#{TEMPO_SVN}/liferay-ticket-filter/target/*.jar") {|x| File.copy x, "#{webapp_folder}/ROOT/WEB-INF/lib", DEBUG}
  File.cp "#{TEMPO_SVN}/rsc/liferay510/portal-ext.properties", "#{webapp_folder}/ROOT/WEB-INF/classes"
  File.cp "#{TEMPO_SVN}/rsc/liferay510/ROOT.xml", "#{webapp_folder}/../conf/Catalina/localhost/"
  
  explain "Deploy the ui-fw portlet"
  # deploy the ui-fw-portlet file
  # already deployed with ui-fw
  # copy the tld file
  FileUtils.mkdir_p "#{webapp_folder}/ui-fw/WEB-INF/tld"
  File.copy "#{TEMPO_SVN}/rsc/liferay510/liferay-portlet.tld", "#{webapp_folder}/ui-fw/WEB-INF/tld"
  # copy the util jars
  Dir.glob("#{webapp_folder}/ROOT/WEB-INF/lib/util*.jar") {|x| File.copy x, "#{webapp_folder}/ui-fw/WEB-INF/lib", DEBUG}
  # special web.xml with configuration for liferay.
  File.cp "#{webapp_folder}/ui-fw/WEB-INF/web-cas.xml", "#{webapp_folder}/ui-fw/WEB-INF/web.xml" 
end

## Add alfresco portlet if needed
if ADD_ALFRESCO && SERVER == ADD_LIFERAY
  title "Installing Alfresco portlet"
  explain "Install alfresco community to Liferay"
  alfresco_folder = download_and_unzip(:url => ALFRESCO[:v3_0], :base_folder => 'alfresco')
  alfresco_war = finder.find_war(alfresco_folder)
  
  explain "Deploy the alfresco war"
  alfresco_war_folder   = wi.install alfresco_war, "alfresco.war"
  # copy the tld file
  FileUtils.mkdir_p "#{webapp_folder}/alfresco/WEB-INF/tld"
  Dir.glob("#{TEMPO_SVN}/rsc/liferay510/tld/*.*") {|x| File.copy x, "#{webapp_folder}/alfresco/WEB-INF/tld", DEBUG}
  # copy the config files
  Dir.glob("#{TEMPO_SVN}/rsc/alfresco/f*.xml") {|x| File.copy x, "#{webapp_folder}/alfresco/WEB-INF", DEBUG}
  Dir.glob("#{TEMPO_SVN}/rsc/alfresco/l*.xml") {|x| File.copy x, "#{webapp_folder}/alfresco/WEB-INF", DEBUG}
  Dir.glob("#{TEMPO_SVN}/rsc/alfresco/portlet.xml") {|x| File.copy x, "#{webapp_folder}/alfresco/WEB-INF", DEBUG}
  Dir.glob("#{TEMPO_SVN}/rsc/alfresco/web.xml") {|x| File.copy x, "#{webapp_folder}/alfresco/WEB-INF", DEBUG}
  # copy the portlet class
  Dir.glob("#{TEMPO_SVN}/liferay-alfresco-sso/target/*.jar") {|x| File.copy x, "#{webapp_folder}/alfresco/WEB-INF/lib", DEBUG}
  # copy the util jars
  Dir.glob("#{webapp_folder}/ROOT/WEB-INF/lib/util*.jar") {|x| File.copy x, "#{webapp_folder}/alfresco/WEB-INF/lib", DEBUG}
  # delete some conflict jar files
  Dir.glob(File.join("#{webapp_folder}/alfresco/WEB-INF/lib", "portlet*.jar")) {|x| File.delete x}
  # copy alfresco repository location
  File.copy "#{TEMPO_SVN}/rsc/alfresco/repository.properties", "#{webapp_folder}/alfresco/WEB-INF/classes/alfresco"
  # disable open office and imagemagick, probably not installed on the server
  Dir.glob("#{TEMPO_SVN}/rsc/alfresco/custom/*.xml") {|x| File.copy x, "#{webapp_folder}/alfresco/WEB-INF/classes/alfresco", DEBUG}
end

## Add LDAP embbeded server and config liferay & alfresco to use that
if ADD_LDAP
  title "Install ApacheDS embbeded server"
  explain "Install ApacheDS 1.5.1"
  
  explain "Deploy the apache ds war"
  apacheds_war_folder = wi.install_tempo_war('apacheds-webapp', '0apacheds')
  if SERVER == ADD_LIFERAY
    explain "Server is Liferay, config it to use Apache DS as LDAP server"
    # copy the config files
    Dir.glob("#{TEMPO_SVN}/rsc/LDAP/portal-ext.properties") {|x| File.copy x, "#{webapp_folder}/ROOT/WEB-INF/classes", DEBUG}
    File.copy "#{TEMPO_SVN}/rsc/LDAP/"+config["ldif"], "#{apacheds_war_folder}/WEB-INF/classes/intalio-apacheds.ldif", DEBUG
  end
  
  if ADD_ALFRESCO
    explain "Also need to config Alfresco to use apacheds"
    Dir.glob("#{TEMPO_SVN}/rsc/alfresco/extension/*.*") {|x| File.copy x, "#{webapp_folder}/alfresco/WEB-INF/classes/alfresco/extension", DEBUG}
    
    # those are 32 bits specific, may need to add the 64bits one depending on the machine.
    download_and_copy("http://svn.alfresco.com/repos/alfresco-open-mirror/alfresco/HEAD/root/projects/alfresco-jlan/jni/Win32NetBIOS.dll", "#{server_folder}/bin")
    download_and_copy("http://svn.alfresco.com/repos/alfresco-open-mirror/alfresco/HEAD/root/projects/alfresco-jlan/jni/Win32Utils.dll", "#{server_folder}/bin")
  end
end

title "Set up CAS server and client"
explain "All the webapp should use the same version of casclient"
##
File.copy "#{TEMPO_SVN}/rsc/liferay510/intalio-keystore.jks", tomcat_config_folder
Dir.glob(File.join("#{TEMPO_SVN}/rsc/liferay510", "server.xml")) {|x| File.copy(x,"#{server_folder}/conf", DEBUG)}
Dir.glob(File.join("#{webapp_folder}/cas/WEB-INF/lib", "casclient*.jar")) {|x| File.cp x, "#{lib_folder}"}
Dir.glob(File.join("#{webapp_folder}", "**/casclient*.jar")) {|x| File.delete x}
# locate_and_copy( DSIG , "#{webapp_folder}/cas/WEB-INF/lib" )
##
  
title "Deleting unused files"
explain "Make some space in the server folder by removing unused files"
##
Dir.glob(File.join("#{webapp_folder}", "*.war")) {|x| File.delete x}
["temp", "work", "LICENSE", "RELEASE-NOTES", "NOTICE", "RUNNING.txt"].each do |x| 
FileUtils.rm_r "#{server_folder}/#{x}", :force => true
end
##

title "Delete conflicting jar files"
explain "Delete files conflicting with tomcat common jar files"
##
Dir.glob(File.join("#{lib_folder}", "commons-logging*.jar")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "commons-logging*.jar")) {|x| File.delete x}
Dir.glob(File.join("#{lib_folder}", "jcl104*.jar")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "jcl104*.jar")) {|x| File.delete x}
Dir.glob(File.join("#{axis2_war_folder}", "**/dom4j*.jar")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "**/servlet-api-*")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "**/jsp-api-*")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "**/casclient*")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "**/log4j-*.jar")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "**/log4j.properties")) {|x| File.delete x}
Dir.glob(File.join("#{webapp_folder}", "**/slf4j*.jar")) {|x| File.delete x}
Dir.glob(File.join("#{server_folder}/common/endorsed", "*.jar")) {|x| File.delete x}
Dir.glob(File.join("#{server_folder}/", "**/.DS_Store")) {|x| File.delete x}
##

title "Adapt absence request to LDAP if needed"
explain "Replace the user/role assignment in bpel files, and in pipa"
##
if ADD_LDAP then
  replace_all_with_map({"examples\\employee" => "intalio\\Sales","examples\\manager" => "intalio\\Manager"}, "#{ode_processes_folder}/AbsenceRequest/AbsenceRequest-AbsenceRequest.bpel")
  replace_all("examples\\\\employee","intalio\\\\\\\\Sales","#{deploy_folder}/AbsenceRequest.pipa/AbsenceRequest/AbsenceRequest.pipa")
end
##

title "Generating concatenated sql file"
explain "Generating a single sql file containing the necessary"
##
f = File.new("#{server_folder}/bpms.sql",  "w")
Dir.glob(File.join("#{TEMPO_SVN}/db-schema/mysql",'*.sql')) {|x| 
  f.write("-- file:#{x}\n")
  f.write(File.open(x).read)
}
ode_release = ODE_RELEASES.index(ODE_DOWNLOAD)
ode_mysql = "#{TEMPO_SVN}/rsc/tempo-sql/#{ode_release}/ode-mysql.sql"
f.write("-- file:#{ode_mysql}\n")
f.write(File.open(ode_mysql).read)
f.close
##

if config["sanity_check"] then
  title "Build sanity check"
  explain "Running a set of BDD checks to assume build is not completely broken"
  Dir.chdir TEMPO_SVN
  system "cucumber rsc/cucumber"
  explain "Include the build report in the build itself"
  system "cucumber rsc/cucumber -f html > #{INSTALL_DIR}/#{server_folder}/report.html"
end

title "Almost done !"
explain "Now create \ a mysql database named \"bpms\" with access to user <root> and no password"
explain "Load the bpms.sql script into this database. The file can be found at the root of the generated server folder"
explain "Once this is done, start tomcat with the following command:"
explain "./catalina.sh run"
explain "windows users can just use the startup.bat script to start the server"
explain "Now you can browse http://localhost:8080/ui-fw/ and login with user <admin> and password <changeit>"

title  "Enjoy!!"