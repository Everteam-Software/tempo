#!/usr/bin/env ruby
require "rubygems"
gem "buildr", "1.2.10"
require "buildr"
require "hpricot"
require "open-uri"
require "pp"

# Some recent code needed to create an artifact from a file 
# not in current 
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

  def usage
    puts "Usage1:"
    puts "  install_in_maven_repo <spec> [<filepath>]"
    puts "Install a single file in the current local and remote repositories."
    puts "This also generate the related pom file."
    puts
    puts "Usage2:"
    puts "  install_in_maven_repo <group> <version> <remote_url_source> <remote_url_dest>"
    puts "Copy a group of dependencies from one repository to another"
  end

  PKG = ["jar", "war", "pom"]
  
  def sync_repositories
    group_name = ARGV[0]
    group_version = ARGV[1]
    url_source = ARGV[2]
    url_dest = ARGV[3]
    
    repositories.remote << url_source
   
    artifacts = list_artifacts(url_source, group_name)
    artifacts_d = Array.new
    artifacts.each do |artifact|
      PKG.each do |pkg|
        artifact_spec = artifact_spec_from_name group_name, group_version, pkg, artifact
        begin
        verbose(false) do 
          a = artifact(artifact_spec)
          a.invoke
          puts "Processing #{a}"
          artifacts_d << a
        end
        rescue 
        end          
      end
    end
    
    artifacts_d.each do |e|
      install_item url_dest, e
    end
  end
  
  def artifact_spec_from_name group, version, pkg, name
    "#{group}:#{name}:#{pkg}:#{version}"
  end
  
  def list_artifacts url_source, group_name
    r1 = Regexp.new('^[a-z]+')
    artifacts = Array.new

    sub_url = group_name.gsub(".", '/')
    url = url_source + "/" + sub_url
    
    puts "Accessing:" + url
    
    doc = Hpricot(open(url))
     doc.search("//a").each do |e|
       href = e.attributes['href']
       artifacts << href[0..href.length-2] if r1.match href # skip those not starting with a letter (not artifacts)
     end
     
    artifacts
  end
  
  def install_single_artifact repository, spec, from_file
    artifact = 
    if from_file 
      artifact(spec).from(from_file) 
    else
      artifact(spec)
    end

    puts "Installing #{ARGV[1]} to #{artifact.name}"
    artifact.invoke
    install_single_jar repository, artifact   
  end
  
  def install_single_jar repository, artifact
    install_item repository, artifact
    install_item repository, artifact.pom()
  end
  
  def install_item repository, artifact
    path = artifact.group.gsub(".", "/") + "/#{artifact.id}/#{artifact.version}/#{File.basename(artifact.name)}"
    uri = URI repository + "/" + path
    uri.user = ENV['USER'] if ENV['USER']
    uri.password = ENV['PASSWORD'] if ENV['PASSWORD']
    puts "Uploading #{file(artifact.name)} to #{uri}"
    verbose(true) do
      uri.upload file(artifact.name), :permissions => 0664
    end
  end

  # Run script for installing a single artifact  
  if ARGV.length == 2 or ARGV.length == 1
    repository = ENV['REPOSITORY'] if ENV['REPOSITORY']
    install_single_artifact repository, ARGV[0], ARGV[1]
  
  # Copy all artifacts of the specified group for the specified version 
  # from the source repository (http) to the destination (sftp)   
  elsif ARGV.length == 4
    sync_repositories
  
  # output usage  
  else
      usage  
  end
