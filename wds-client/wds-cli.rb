#!/usr/bin/env ruby
require "rubygems"
require "buildr"

# Configurable options
BASE = "http://localhost:8080/wds/"
DEBUG = true

# Non configurable options
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
WDSCC = "org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient"

def wds_deploy deploy_arguments, base=BASE
  base = BASE if base == nil
  wds_command = "#{WDSCC} -w #{base} #{deploy_arguments} --force"
  puts "#{wds_command}" if DEBUG
  command = "java -cp #{CLASSPATH} #{wds_command}"
  system "#{command}"
end

def store_activity args, base=BASE
  wds_deploy "store-activity #{args}", base
end

def store_pipa args, base=BASE
  wds_deploy "store-pipa #{args}", base
end

def delete_pipa args, base=BASE
  wds_deploy "delete-pipa #{args}", base
end

def delete_activity args, base=BASE
  wds_deploy "delete-activity #{args}", base
end

def wds_client items, url=BASE
  items.each do |item|
    item.store url
  end if ARGV and ARGV[0] == "store" 
  
  items.each do |item|
    item.delete url
  end if ARGV and ARGV[0] == "delete" 
end

class Activity
  def initialize(item_url, item_file)
  		@item_url = item_url
  		@item_file = item_file
	end
	
	def to_s
    "#{@item_url} #{@item_file}"
  end
  
  def store base=BASE
     store_activity "#{@item_url} #{@item_file}", base
  end
  
  def delete base=BASE
    delete_activity "#{@item_url}", base
  end
end

class Pipa
  def initialize(item_url, item_file, deployment_file)
  		@item_url = item_url
  		@item_file = item_file
  		@deployment_file = deployment_file
  end
  
  def store base=BASE
    store_pipa to_s, base
  end
  
  def delete base=BASE
    delete_pipa "-d #{@deployment_file} #{@item_url}", base
  end
  
  def to_s
    "-d #{@deployment_file} #{@item_url} #{@item_file}"
  end
end