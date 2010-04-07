#!/usr/bin/env ruby

# loading repositories, dependencies files to locate artifacts
@@script_folder = File.dirname(File.expand_path("#{$0}"))
load "#{@@script_folder}/../scripts/tempo_builder_lib.rb"
load "#{@@script_folder}/../scripts/config.rb"

BASE_PACKAGE = "org.intalio.liferay"
VERSION_NUMBER = '6.0.4.004'
TEMPO_VERSION = "6.0.4.004"

BUILD_CONFIG = {
  :directory => "./target",
  # :liferay => {:v => :v5_2_5ee, :base_folder => "liferay-portal-5.2.5", :server_folder => "tomcat-5.5.27"},
  # :liferay => {:v => :v5_2_3, :base_folder => "liferay-portal-5.2.3", :server_folder => "tomcat-5.5.27"},
  # :liferay => {:v => :v5_2_5_jbee, :base_folder => "liferay-portal-5.2.5", :server_folder => "jboss-tomcat-4.2.3"},
  :mode => [BuildMode::LIFERAY, BuildMode::UIFW, BuildMode::CAS, BuildMode::RELEASE],
  :tempo => {:core => TEMPO_VERSION},
  # :artifact => "org.intalio.liferay:liferay-jboss-ee:zip:6.0.001"
}

 BUILD_CONFIG[:artifact] = "#{BASE_PACKAGE}:liferay-jboss-ee:zip:#{VERSION_NUMBER}"
 BUILD_CONFIG[:liferay] = {:v => :v5_2_5_jbee, :base_folder => "liferay-portal-5.2.5", :server_folder => "jboss-tomcat-4.2.3"}
# tb = TempoBuilder.new
# tb.build BUILD_CONFIG

#BUILD_CONFIG[:artifact] = "#{BASE_PACKAGE}:liferay-tomcat-ee:zip:#{VERSION_NUMBER}"
#BUILD_CONFIG[:liferay] = {:v => :v5_2_5ee, :base_folder => "liferay-portal-5.2.5", :server_folder => "tomcat-5.5.27"}
tb = TempoBuilder.new
tb.build BUILD_CONFIG
