#!/usr/bin/env ruby
require "rubygems"
require "buildr"

module Buildr
  class Artifact
    def from(path)
      enhance do
        unless File.exist?(name)
          mkpath File.dirname(name)
          cp path, name
          if not type == :pom 
            pom = pom() 
            File.open(pom.name,"a+") do |file| file << pom.pom_xml end
            end
            puts "Installed #{name}" if verbose
          end 
        end
        self 
      end
    end 
  end

  if ARGV.length < 2
    puts "Usage: install_in_maven_repo <spec> <filepath>"
    puts "This also generate the related pom file."
  else
    artifact = artifact(ARGV[0]).from(ARGV[1])
    artifact.invoke
    uri = URI "sftp://nico@www.intalio.org/tmp"
    uri.upload file(artifact.name)
  end
