require "buildr/cobertura"
require "buildr/java"
require "buildr/java/ant"

module Buildr

  # Integrate EasyB and Cobertura 
  module Easyb

    COBERTURA = ["net.sourceforge.cobertura:cobertura:jar:1.9", "log4j:log4j:jar:1.2.9","asm:asm:jar:2.2.1", "asm:asm-tree:jar:2.2.1", "oro:oro:jar:2.0.8"]
    REQUIRES = [EASY_B, COBERTURA]
    
    Java.classpath << REQUIRES

    class << self

      def easyb(options)
        Buildr.ant "easyb" do |ant|
          rake_check_options options, :classpath, :report, :storydir, :output, :format
          artifacts = Buildr.artifacts(options[:classpath]).each { |a| a.invoke }.map(&:to_s) + [options[:output].to_s] 
          fullpath = artifacts.join(File::PATH_SEPARATOR) + File::PATH_SEPARATOR + requires.join(File::PATH_SEPARATOR)

          ant.taskdef :name=>"easyb", :classname=>"org.disco.easyb.ant.SpecificationRunnerTask",:classpath=>requires.join(File::PATH_SEPARATOR)
          ant.easyb :failureProperty=>"easyb.failed" do
            ant.report :location=>options[:report], :format=>options[:format]
            ant.classpath :path=>fullpath
            ant.behaviors :dir=>options[:storydir] do
              ant.include :name=>"**.groovy"
            end
          end

          if ant.project.getProperty('easyb.failed') 
            raise "Easyb tests have failed "
          end
        end
      end

      def requires()
        @requires ||= Buildr.artifacts(REQUIRES).each { |artifact| artifact.invoke }.map(&:to_s)
      end

      def report_to(file = nil)
        File.expand_path(File.join(*["reports/cobertura", file.to_s].compact))
      end

      def data_file()
        File.expand_path("reports/cobertura.ser")
      end

      def path_to_parent(some_project) 
        project = some_project
        while(project.parent!=nil) do 
          project = project.parent
        end
        project.path_to("")
      end

      def run_on_project(project)
        unless project.compile.sources.empty?
          stories = project.path_to("src/test/stories")
          instrumented = project.file(project.path_to(:target, "instrumented"))
          target = project.file(project.path_to(:target, "classes"))
          new_path = project.compile.classpath.unshift target
          new_path.unshift instrumented
          new_path.unshift project.file(project.path_to("src/test/resources"))
          if File.exist? stories
            easyb_options = { 
              :classpath=>new_path,
              :report=>project.file(project.path_to("target/story.txt")), 
              :format=>"txtstory",
              :storydir=>project.file(stories), 
              :output=>project.compile.target
            }
            EasyB.easyb easyb_options
          end
        end
      end
      
      def run_on_projects()
        Buildr.projects.each do |project|
          run_on_project project
        end
      end
      
    end

    namespace "easyb" do

      desc "Run easyb code"
      task "run" do
        original_dir = Rake.application.original_dir
        top_project = Buildr.projects[0]
        top_name = top_project.name

        if(top_project.base_dir == Rake.application.original_dir)
          run_on_projects
        else
          @@org = original_dir[original_dir.rindex('/') + 1,original_dir.length]
          current_project = Buildr.projects[0].project(@@org)
          current_name = current_project.name
          if(top_name == current_name)   
            run_on_projects
          else
            run_on_project current_project
          end
        end
      end

      desc "Integrate with cobertura"
      task "cobertura" => ["cobertura:instrument", "test", "easyb:run"] do
        toppath = path_to_parent(Buildr.projects[0])
        Buildr.ant "cobertura" do |ant|
          ant.taskdef :classpath=>requires.join(File::PATH_SEPARATOR), :resource=>"tasks.properties"
          # the cobertura.ser file at the top is generated from the call to easyb, let's merge it back with the others    
          ant.send "cobertura-merge", :datafile=>data_file do
            ant.fileset(:dir=>toppath) { ant.include :name=>"*cobertura.ser" }
          end if Dir["#{toppath}/*cobertura.ser"].size > 0 # don't merge if only one file, since ant task is failing on this
          
          rm_rf toppath +"/" + "cobertura.ser"
          ant.send "cobertura-report", :destdir=>report_to(:html), :format=>"html", :datafile=>data_file do
            Buildr.projects.map(&:compile).map(&:sources).flatten.each do |src|
              ant.fileset(:dir=>src.to_s) { ant.include :name=>"**/*.java" } if File.exist?(src.to_s)
            end
          end
        end
      end

      task "clean" do
        rm_rf [report_to, data_file], :verbose=>false
      end

    end

    ## Call easy from the compile command in the buildfile
    def run_easyb(options = nil)
      easyb_options = { 
        :classpath=>compile.classpath,
        :report=>_("target/story.txt"), 
        :format=>"txtstory",
        :storydir=>_("src/test/stories"), 
        :output=>compile.target
      }
      EasyB.easyb easyb_options.merge(options || {})
    end  

  end

  class Project
    include EasyB
  end

end