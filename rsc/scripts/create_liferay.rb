#!/usr/bin/env ruby

require "rubygems"
require "hpricot"
require 'net/http'
require 'open-uri' 
require "zip/zip"
require 'yaml'
require 'fileutils'
require 'open-uri'
gem 'buildr', '1.2.10'
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

###
# 
###
title "Create and init folders variables"
FileUtils.mkdir_p INSTALL_DIR
Dir.chdir INSTALL_DIR
server_folder = download_unzip( LIFERAY_5 )
webapp_folder = server_folder + File::SEPARATOR + "webapps"
liferay_config_folder = "#{TEMPO_SVN}/rsc/liferay510/"
tempo_svn_config_folder = "#{TEMPO_SVN}/config"
tomcat_config_folder = "#{server_folder}/var/config"
FileUtils.mkdir_p tomcat_config_folder
tomcat_bin_folder = server_folder + File::SEPARATOR + "bin" + File::SEPARATOR
create_mode = File::CREAT|File::TRUNC|File::RDWR

###
# 
### 
title "create setenv files"
explain "This is needed because we need a keystore for SSL and we need the tempo configuration folder"
file_path = tomcat_bin_folder + "setenv.sh"
file = File.new file_path,create_mode
file.puts "export JAVA_OPTS=\"-XX:MaxPermSize=256m -server -Djavax.net.ssl.trustStore=$CATALINA_HOME/var/config/intalio-keystore.jks -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=$CATALINA_HOME/var/config -Dorg.apache.ode.configDir=$CATALINA_HOME/var/config -Djava.security.auth.login.config=$CATALINA_HOME/conf/jaas.config\""
file_path = tomcat_bin_folder + "setenv.bat"
file = (File.new file_path,create_mode)
file.puts "set JAVA_OPTS=-XX:MaxPermSize=256m -server -Djavax.net.ssl.trustStore=%CATALINA_HOME%\\var\\config\\intalio-keystore.jks -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=%CATALINA_HOME%\\var\\config -Dorg.apache.ode.configDir=%CATALINA_HOME%\\var\\config -Djava.security.auth.login.config=%CATALINA_HOME%\\conf\\jaas.config"
Dir.glob("#{server_folder}/bin/*.sh") {|x| File.chmod 0755, x, DEBUG}

###
#
###
title "copy server.xml"
explain "This is needed because we want to change the ports liferay is running on"
File.copy "#{liferay_config_folder}/server-liferay-standalone.xml", "#{server_folder}/conf/server.xml", DEBUG

###
# 
###
title "copy web.xml"
explain "this is needed because we need to configure CAS and the CAS filter properly"
File.copy "#{liferay_config_folder}/web.xml", "#{webapp_folder}/ROOT/WEB-INF/web.xml"

###
#
###
title "copy ROOT.xml"
explain "This is needed for database configuration. May be skipped later on"
File.copy "#{liferay_config_folder}/ROOT-liferay-standalone.xml", "#{server_folder}/conf/Catalina/localhost/ROOT.xml", DEBUG

###
#
###
title "add tempo liferay filter"
explain "This is needed because we are using CAS proxy"
Dir.glob("#{TEMPO_SVN}/liferay-ticket-filter/target/*.jar") {|x| File.copy x, "#{webapp_folder}/ROOT/WEB-INF/lib", DEBUG}

###
#
###
title "move casclient jar file"
explain "casclient should not be in a single webapp, otherwise it ends up being loaded by the wrong classlooader, and ends up in a ClassCastException"
begin
File.mv "#{webapp_folder}/ROOT/WEB-INF/lib/casclient.jar", "#{server_folder}/common/lib", DEBUG
rescue
  # must have been moved already, ignore unless file is not there
  throw "Missing casclient.jar file" if not File.exist? "#{server_folder}/common/lib/casclient.jar"
end

###
#
###
title "copy portal-ext.properties"
explain "This is where CAS and LDAP are configured"
File.copy "#{liferay_config_folder}/../LDAP/portal-ext.properties", "#{webapp_folder}/ROOT/WEB-INF/classes", DEBUG

###
#
###
title "copy files in var/config folder"
explain "Those are required tempo files. We load them from the tempo config folder defined in setenv.sh/.bat"
File.copy "#{tempo_svn_config_folder}/tempo-formmanager.xml", tomcat_config_folder, DEBUG
File.copy "#{tempo_svn_config_folder}/tempo-ui-fw-servlet.xml", tomcat_config_folder, DEBUG
File.copy "#{tempo_svn_config_folder}/tempo-ui-fw.xml", tomcat_config_folder, DEBUG
File.copy "#{liferay_config_folder}/intalio-keystore.jks", tomcat_config_folder, DEBUG

###
#
###
title "add portlet version of ui-fw"
explain "This is just the same as the standard, except we replace the web.xml file."
Dir.chdir "#{TEMPO_SVN}/ui-fw"
system "buildr -e portlet clean test=no install" if config['rebuild']
Dir.chdir INSTALL_DIR
Dir.glob("#{TEMPO_SVN}/ui-fw/target/*.war") {|x|  File.copy x, "#{webapp_folder}/ui-fw.war", DEBUG}

title "The customized liferay build is ready"