#!/usr/bin/env ruby

require "rubygems"
# ruby superclassmismatch when loading buildr before rubyzip
# so leave this here
# gem 'rubyzip'
# require 'zip/zip'
# require 'zip/zipfilesystem'

gem "buildr",">=1.3.3"
require "buildr"
require "pp"

@@script_folder = File.dirname(File.expand_path("#{$0}"))
load "#{@@script_folder}/../scripts/lib/build_support.rb"
load "#{@@script_folder}/../build/repositories.rb"
load "#{@@script_folder}/../build/dependencies.rb"
load "#{@@script_folder}/../scripts/config.rb"
load "#{@@script_folder}/../scripts/lib/bundle_standalone.rb"

VERSION_NUMBER = '6.0.001'
BASE_PACKAGE = "org.intalio.jettyosgi"
BUILD_CONFIG = {
  :directory => "../tempo.jetty",
  :artifact => "#{BASE_PACKAGE}:tempo-jetty-osgi:zip:#{VERSION_NUMBER}",
  :mode => [BuildMode::JETTY, BuildMode::UIFW, BuildMode::CAS, BuildMode::LDAP], 
  :ode => :v1_3_snapshot,
  :jetty => :v7,
  :osgi_jetty => :v7,
  :tempo => {
    :core => "6.0.0.75",
    :security => "1.0.5",
    :deploy => "1.0.25",
    
    :processes => "6.0.6",
    :formManager => "6.0.0.40",
    :apacheds => "6.0.0.37",
    :cas => "6.0.0.36"
  }
}


TEMPO_SVN="#{@@script_folder}/../.."

repositories.remote << "http://svn.codehaus.org/jetty/jetty/trunk/repository/src/repository"

def install_osgi_jetty build_folder="tempo-osgi-jetty-#{time_now}"
  osgi_jetty_file = filename_from_url BUILD_URI[:osgi_jetty][:v7]
  download_to(osgi_jetty_file, BUILD_URI[:osgi_jetty][:v7], false)
  unpack_file(osgi_jetty_file, build_folder)
  @@server_folder = "#{build_folder}/eclipse/jettyhome"

  @@webapp_folder = "#{build_folder}/eclipse/plugins"
  @@wi = WarInstaller.new @@webapp_folder, true, true
  @@wi.webapp_folder = "#{build_folder}/eclipse/plugins"
  @@lib_folder = "#{build_folder}/eclipse/jettyhome/lib/ext"
  @@log_folder = check_folder("#{build_folder}/eclipse/jettyhome/var/logs")
  @@config_folder = check_folder("#{build_folder}/eclipse/jettyhome/var/config")
end

def osgi_axis2
  check_folder("#{@@webapp_folder}/axis2/contexts")
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/axis2.xml", "#{@@webapp_folder}/axis2/contexts"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/axis2_web.xml", "#{@@webapp_folder}/axis2/WEB-INF/web.xml"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/axis2_MANIFEST.MF", "#{@@webapp_folder}/axis2/META-INF/MANIFEST.MF"
  FileUtils.mv "#{@@webapp_folder}/axis2", "#{@@webapp_folder}/org.intalio.tempo.axis2"
end

def osgi_fds
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/fds_MANIFEST.MF", "#{@@webapp_folder}/fds/META-INF/MANIFEST.MF"
  FileUtils.mv "#{@@webapp_folder}/fds", "#{@@webapp_folder}/org.intalio.tempo.fds"
end

def osgi_ode
  check_folder("#{@@webapp_folder}/ode/contexts")
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/ode.xml", "#{@@webapp_folder}/ode/contexts"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/ode_web.xml", "#{@@webapp_folder}/ode/WEB-INF/web.xml"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/ode_MANIFEST.MF", "#{@@webapp_folder}/ode/META-INF/MANIFEST.MF"
  FileUtils.mv "#{@@webapp_folder}/ode", "#{@@webapp_folder}/org.intalio.tempo.ode"
end

def osgi_uifw
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/uifw_MANIFEST.MF", "#{@@webapp_folder}/ui-fw/META-INF/MANIFEST.MF"
  FileUtils.mv "#{@@webapp_folder}/ui-fw", "#{@@webapp_folder}/org.intalio.tempo.uifw"
end

def osgi_wds
  check_folder("#{@@webapp_folder}/wds/contexts")
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/wds.xml", "#{@@webapp_folder}/wds/contexts"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/wds_web.xml", "#{@@webapp_folder}/wds/WEB-INF/web.xml"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/wds_MANIFEST.MF", "#{@@webapp_folder}/wds/META-INF/MANIFEST.MF"
  FileUtils.mv "#{@@webapp_folder}/wds", "#{@@webapp_folder}/org.intalio.tempo.wds"
end

def osgi_xFormsManager
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/xFormsManager_MANIFEST.MF", "#{@@webapp_folder}/xFormsManager/META-INF/MANIFEST.MF"  
  FileUtils.mv "#{@@webapp_folder}/xFormsManager", "#{@@webapp_folder}/org.intalio.tempo.xformmanager"
end

def osgi_apacheds
  check_folder("#{@@webapp_folder}/apacheds/contexts")
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/apacheds.xml", "#{@@webapp_folder}/apacheds/contexts"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/apacheds_MANIFEST.MF", "#{@@webapp_folder}/apacheds/META-INF/MANIFEST.MF"  
  FileUtils.mv "#{@@webapp_folder}/apacheds", "#{@@webapp_folder}/org.intalio.tempo.apacheds"
end

def osgi_cas
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/cas_MANIFEST.MF", "#{@@webapp_folder}/cas/META-INF/MANIFEST.MF"
  FileUtils.mv "#{@@webapp_folder}/cas", "#{@@webapp_folder}/org.intalio.tempo.cas"
end

def osgi_jetty_config
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/jetty.xml", "#{@@server_folder}/etc"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/jetty-ssl.xml", "#{@@server_folder}/etc"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/config.ini", "#{@@server_folder}/../configuration"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/start.bat", "#{@@server_folder}/../"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/start.sh", "#{@@server_folder}/../"
  FileUtils.cp "#{TEMPO_SVN}/rsc/osgi_jetty/log4j.xml", "#{@@server_folder}/resources"
  FileUtils.cp "#{TEMPO_SVN}/rsc/bundle-config/intalio-keystore.jks", "#{@@server_folder}/var/config"
  missing_libs = [
    "com.atomikos:atomikos-util:jar:3.5.5",
    "com.atomikos:transactions:jar:3.5.5",
    "com.atomikos:transactions-api:jar:3.5.5",
    "com.atomikos:transactions-jdbc:jar:3.5.5",
    "com.atomikos:transactions-jta:jar:3.5.5",
    CAS_CLIENT,
    APACHE_COMMONS[:logging]
  ]
  missing_libs.each {|lib| locate_and_copy( lib, @@lib_folder )}
end

def osgi_config
  osgi_axis2
  osgi_fds
  osgi_ode
  osgi_uifw
  osgi_wds
  osgi_xFormsManager
  osgi_apacheds
  osgi_cas
  osgi_jetty_config
  generate_mysql_file
end

def time_now
  Time.now.strftime('%Y.%m.%d')  
end

build_folder="tempo-osgi-jetty-#{time_now}"
install_osgi_jetty build_folder

setup_axis_and_ode

install_tempo_services
install_tempo_webapps
install_tmp
install_absence_request
install_tempo_uifw
install_embedded_apacheds
install_cas_webapp
copy_missing_lib
copy_tempo_config_files
osgi_config

#ar = artifact(BUILD_CONFIG[:artifact]).from(compress("#{build_folder}"))
#ar.upload
