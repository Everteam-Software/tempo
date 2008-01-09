#!/usr/bin/env ruby
require "../wds-cli"

system "java -cp #{CLASSPATH} #{WDSCC} -w #{BASE} store-activity PeopleActivity/PA.xform PA.xform --force"