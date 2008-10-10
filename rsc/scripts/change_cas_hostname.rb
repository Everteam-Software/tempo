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

DEBUG = true
REGEXP = /localhost/

script_folder = File.dirname(File.expand_path("#{$0}"))
load "#{script_folder}/../scripts/build_support.rb"

puts "Please input the target domain name (such as: www.cas.com) : "
target_domain = gets.strip
puts "Please input the path to the server you need to modify : "
server_home = gets.strip

# Change liferay config file
properties = ["cas.login.url", "cas.logout.url", "cas.validate.url"]

liferay_conf_file = "#{server_home}/webapps/ROOT/WEB-INF/classes/portal-ext.properties"
liferay_cas_properties = JavaProps.new(liferay_conf_file)
properties.each do |property|
  value = liferay_cas_properties.read_property(property)
  liferay_cas_properties.write_property(property, value.gsub(REGEXP, target_domain))
end
liferay_cas_properties.save()


# Change ui-fw web.xml
ui_fw_web_file = "#{server_home}/webapps/ui-fw/WEB-INF/web.xml"
doc = Hpricot.XML(File.new(ui_fw_web_file))
param_name = doc.search("//param-name/text()")
param_value = doc.search("//param-value")

i = 0
param_name.size.times do
  if param_name[i].to_s =~ /loginUrl/ || param_name[i].to_s =~ /validateUrl/ || param_name[i].to_s =~ /logoutUrl/ || param_name[i].to_s =~ /proxyUrl/
    target_url = param_value[i].search("text()").to_s.gsub(REGEXP, target_domain)
    param_value[i].at("<param-value>").swap("<param-value>#{target_url}</param-value>")
  end
  i = i + 1
end

File.open(ui_fw_web_file, 'w') do |f|
  f.puts(doc)
end

# Change the security configuration
security_config_file = "#{server_home}/var/config/securityConfig.xml"
replace_all("localhost", target_domain, security_config_file)