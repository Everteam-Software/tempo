#!/usr/bin/env ruby

# loading repositories, dependencies files to locate artifacts
@@script_folder = File.dirname(File.expand_path("#{$0}"))
load "#{@@script_folder}/../scripts/tempo_builder_lib.rb"
load "#{@@script_folder}/../scripts/config.rb"

BASE_PACKAGE = "org.intalio.jetty"
VERSION_NUMBER = '6.0.003'
TEMPO_VERSION = "6.0.0.78"

BUILD_CONFIG = {
  :directory => "./target",
  # :mode => [BuildMode::JETTY, BuildMode::UIFW, BuildMode::CAS, BuildMode::LDAP, BuildMode::RELEASE],
  :mode => [BuildMode::JETTY, BuildMode::UIFW, BuildMode::CAS, BuildMode::LDAP],

  :ode => :v1_3_snapshot,
  :jetty => :v7,
  :tempo => {
    :core => TEMPO_VERSION,
    :security => "1.0.5",
    :deploy => "1.0.25",
    :processes => "6.0.6",
    :formManager => "6.0.0.40",
    :apacheds => "6.0.0.37",
    :cas => "6.0.0.35"
  },
  :artifact => "org.intalio.jetty:tempo-jetty7:zip:#{VERSION_NUMBER}"
}

tb = TempoBuilder.new
tb.build BUILD_CONFIG