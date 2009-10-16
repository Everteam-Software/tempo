#!/usr/bin/env ruby

# loading repositories, dependencies files to locate artifacts
@@script_folder = File.dirname(File.expand_path("#{$0}"))
puts @@script_folder
load "#{@@script_folder}/../scripts/tempo_builder_lib.rb"
load "#{@@script_folder}/../scripts/config.rb"

tb = TempoBuilder.new
tb.build BUILD_CONFIG