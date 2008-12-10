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

config = YAML::load( File.open( "#{script_folder}/config.yml" ) )
TEMPO_SVN = "#{script_folder}/../.."
LIFERAY_5 = "http://downloads.sourceforge.net/sourceforge/lportal/liferay-portal-tomcat-5.5-5.1.0.zip"
INSTALL_DIR = File.expand_path config["install_dir"]
DEBUG = config["debug"]

title "Creating Liferay release"

# Create and init folders
FileUtils.mkdir_p INSTALL_DIR
Dir.chdir INSTALL_DIR
server_folder = download_unzip( LIFERAY_5 )
webapp_folder = server_folder + File::SEPARATOR + "webapps"
liferay_config_folder = "#{TEMPO_SVN}/rsc/liferay510/"
tempo_svn_config_folder = "#{TEMPO_SVN}/config"
tomcat_config_folder = "#{server_folder}/var/config"
FileUtils.mkdir_p tomcat_config_folder

File.cp "#{TEMPO_SVN}/rsc/liferay510/web.xml", "#{webapp_folder}/ROOT/WEB-INF"
Dir.glob("#{TEMPO_SVN}/liferay-ticket-filter/target/*.jar") {|x| File.copy x, "#{webapp_folder}/ROOT/WEB-INF/lib", DEBUG}
File.cp "#{TEMPO_SVN}/rsc/liferay510/portal-ext.properties", "#{webapp_folder}/ROOT/WEB-INF/classes"
File.cp "#{TEMPO_SVN}/rsc/liferay510/ROOT.xml", "#{webapp_folder}/../conf/Catalina/localhost/"

tomcat_bin_folder = server_folder + File::SEPARATOR + "bin" + File::SEPARATOR
create_mode = File::CREAT|File::TRUNC|File::RDWR
# setenv files
file_path = tomcat_bin_folder + "setenv.sh"
file = File.new file_path,create_mode
file.puts "export JAVA_OPTS=\"-XX:MaxPermSize=256m -server -Djavax.net.ssl.trustStore=$CATALINA_HOME/var/config/tempokeystore -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=$CATALINA_HOME/var/config -Dorg.apache.ode.configDir=$CATALINA_HOME/var/config -Djava.security.auth.login.config=$CATALINA_HOME/conf/jaas.config\""
file_path = tomcat_bin_folder + "setenv.bat"
file = (File.new file_path,create_mode)
file.puts "set JAVA_OPTS=-XX:MaxPermSize=256m -server -Djavax.net.ssl.trustStore=%CATALINA_HOME%\\var\\config\\tempokeystore -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=%CATALINA_HOME%\\var\\config -Dorg.apache.ode.configDir=%CATALINA_HOME%\\var\\config -Djava.security.auth.login.config=%CATALINA_HOME%\\conf\\jaas.config\""

# copy server.xml
File.copy "#{liferay_config_folder}/server-liferay-standalone.xml", "#{server_folder}/conf/server.xml", DEBUG

# copy web.xml
File.copy "#{liferay_config_folder}/web.xml", "#{webapp_folder}/ROOT/WEB-INF/web.xml"

# copy ROOT.xml
File.copy "#{liferay_config_folder}/ROOT-liferay-standalone.xml", "#{server_folder}/conf/Catalina/localhost/ROOT.xml", DEBUG

# add tempo liferay filter
Dir.glob("#{TEMPO_SVN}/liferay-ticket-filter/target/*.jar") {|x| File.copy x, "#{webapp_folder}/ROOT/WEB-INF/lib", DEBUG}

# move casclient jar file
begin
File.mv "#{webapp_folder}/ROOT/WEB-INF/lib/casclient.jar", "#{server_folder}/common/lib"
rescue
  # must have been moved already, ignore
end

# copy portal-ext.properties
File.copy "#{liferay_config_folder}/../LDAP/portal-ext.properties", "#{webapp_folder}/ROOT/WEB-INF/classes", DEBUG

# copy files in var/config folder
File.copy "#{tempo_svn_config_folder}/tempo-formmanager.xml", tomcat_config_folder
File.copy "#{tempo_svn_config_folder}/tempo-ui-fw-servlet.xml", tomcat_config_folder
File.copy "#{tempo_svn_config_folder}/tempo-ui-fw.xml", tomcat_config_folder
File.copy "#{liferay_config_folder}/tempokeystore", tomcat_config_folder

# change sh files settings
Dir.glob("#{server_folder}/bin/*.sh") {|x| File.chmod 0755, x, DEBUG}

# add portlet version of ui-fw
Dir.chdir "#{TEMPO_SVN}/ui-fw"
system "buildr -e portlet clean install" if config['rebuild']
Dir.chdir INSTALL_DIR
Dir.glob("#{TEMPO_SVN}/ui-fw/target/*.war") {|x| 
  File.copy x, "#{webapp_folder}/ui-fw.war", DEBUG
}