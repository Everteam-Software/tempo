#!/usr/bin/env ruby
require "rubygems"
require "buildr"

LIBS = [
  "commons-codec:commons-codec:jar:1.3",
  "commons-httpclient:commons-httpclient:jar:3.1",
  "jargs:jargs:jar:1.0",
  "log4j:log4j:jar:1.2.15",
  "org.slf4j:slf4j-api:jar:1.4.3",
  "org.slf4j:slf4j-log4j12:jar:1.4.3",
  "org.slf4j:jcl104-over-slf4j:jar:1.4.3"
]

CLASSPATH = Buildr.artifacts(LIBS.flatten).uniq.join(":") + ":" + Dir.glob("../target/tempo-wds-client-*.jar").to_s 

BASE = "http://localhost:8080/wds/"

WDSCC = "org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient"

def self.wds_deploy deploy_arguments, base=BASE
  system "java -cp #{CLASSPATH} #{WDSCC} -w #{base} #{deploy_arguments} --force"  
end