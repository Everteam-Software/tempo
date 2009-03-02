#!/usr/bin/env ruby
require 'rubygems'
require 'net/ldap'
require "pp"

# Authenticate
user = 'uid=admin,ou=system'
password = 'secret'
ldap = Net::LDAP.new :host => "localhost", :port => 10389, :auth => {:method => :simple, :username => user, :password => password}
p (ldap.bind) ? "Authorization Succeeded!" : "Authorization Failed: #{ldap.get_operation_result.message}"

# Search for admin user in tempo users
treebase = "DC=intalio,DC=com"
filter = Net::LDAP::Filter.eq('cn', "admin")
ldap.search(:filter => filter, :base => treebase) {|entry| pp entry}