require "java/java"
require "java/ant"

module Buildr

  # By using a specification based Domain Specific Language, easyb aims to enable executable, yet readable documentation.
  module EasyB

    REQUIRES = EASY_B

    Java.wrapper.classpath << REQUIRES

    class << self

      def easyb(options)
        Buildr.ant "easyb" do |ant|
          rake_check_options options, :classpath, :report, :storydir, :output, :format
          artifacts = Buildr.artifacts(options[:classpath]).each { |a| a.invoke }.map(&:to_s) + [options[:output].to_s]
          ant.taskdef :name=>"easyb", :classname=>"org.disco.easyb.ant.SpecificationRunnerTask",:classpath=>requires.join(File::PATH_SEPARATOR)
          
          ant.easyb do
            ant.report :location=>options[:report], :format=>options[:format]
            ant.classpath :path=>artifacts.join(File::PATH_SEPARATOR)
            ant.behaviors :dir=>options[:storydir] do
              ant.include :name=>"**.groovy"
            end
          end
        end
      end

      private
      def requires
        @requires ||= Buildr.artifacts(REQUIRES).each { |artifact| artifact.invoke }.map(&:to_s)
      end
    end
  end

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

  class Project
    include EasyB
  end

end