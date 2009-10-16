#!/usr/bin/env ruby

# loading repositories, dependencies files to locate artifacts
@@script_folder = File.dirname(File.expand_path("#{$0}"))
load "#{@@script_folder}/../scripts/tempo_builder_lib.rb"
load "#{@@script_folder}/../scripts/config.rb"

BUILD_CONFIG = {
  :directory => "./target",
  :mode => [BuildMode::LIFERAY, BuildMode::UIFW, BuildMode::CAS, BuildMode::RELEASE],
  # :liferay => {:v => :v5_2_5ee, :base_folder => "liferay-portal-5.2.5", :server_folder => "tomcat-5.5.27"},
  # :liferay => {:v => :v5_2_3, :base_folder => "liferay-portal-5.2.3", :server_folder => "tomcat-5.5.27"},
  :liferay => {:v => :v5_2_5_jbee, :base_folder => "liferay-portal-5.2.5", :server_folder => "jboss-tomcat-4.2.3"},  
  :tempo => {:core => "6.0.0.77-SNAPSHOT"},
  :artifact => "org.intalio.liferay:liferay-jboss-ee:zip:6.0.001"
}
tb = TempoBuilder.new
tb.build BUILD_CONFIG
