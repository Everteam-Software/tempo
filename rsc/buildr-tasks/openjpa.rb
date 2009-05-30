require "rsc/buildr-tasks/buildr_version"

module Buildr

  # Provides OpenJPA bytecode enhancement and Mapping tool task. Require explicitly using <code>require "buildr/openjpa"</code>.
  module OpenJPA

    REQUIRES = APACHE_JPA, APACHE_COMMONS[:pool], APACHE_COMMONS[:dbcp], APACHE_DERBY

    update_java_classpath REQUIRES

    class << self

      def enhance(options)
        rake_check_options options, :classpath, :properties, :output
        artifacts = Buildr.artifacts(options[:classpath]).each { |a| a.invoke }.map(&:to_s) + [options[:output].to_s]
        properties = file(options[:properties]).tap { |task| task.invoke }.to_s

        Buildr.ant "openjpa" do |ant|
           # workaround way for openjpa 1.2.1
          ant.path(:id =>"required_classpath"){ |ant|
            ant.pathelement(:path => requires.join(File::PATH_SEPARATOR))
            ant.pathelement(:path => artifacts.join(File::PATH_SEPARATOR).to_s)
          }

          ant.taskdef :name=>"enhancer", :classname=>"org.apache.openjpa.ant.PCEnhancerTask",
          :classpathref=>"required_classpath"
#            :classpath=>requires.join(File::PATH_SEPARATOR)            
            ant.enhancer :directory=>options[:output].to_s, :tmpClassLoader=>"false" do
            ant.config :propertiesFile=>properties
            ant.classpath :refid=>"required_classpath"
#            ant.fileset :dir=>options[:output].to_s, :includes=>"**/taskb4p/*.class"
#            ant.classpath :path=>artifacts.join(File::PATH_SEPARATOR)
          end
        end
      end

      def mapping_tool(options)
        rake_check_options options, :classpath, :properties, :sql, :action
        artifacts = Buildr.artifacts(options[:classpath]).each{ |a| a.invoke }.map(&:to_s)
        properties = file(options[:properties].to_s).tap { |task| task.invoke }.to_s

        Buildr.ant("openjpa") do |ant|
           # workaround way for openjpa 1.2.1
          ant.path(:id =>"required_classpath"){ |ant|
            ant.pathelement(:path => requires.join(File::PATH_SEPARATOR))
            ant.pathelement(:path => artifacts.join(File::PATH_SEPARATOR).to_s)
          }
          
          ant.taskdef :name=>"mapping", :classname=>"org.apache.openjpa.jdbc.ant.MappingToolTask",
            :classpathref=>"required_classpath"
#            :classpath=>requires.join(File::PATH_SEPARATOR)
          ant.mapping :schemaAction=>options[:action], :sqlFile=>options[:sql].to_s, :ignoreErrors=>"true", :tmpClassLoader=>"false" do
            ant.config :propertiesFile=>properties
            ant.classpath :refid=>"required_classpath"
#            ant.classpath :path=>artifacts.join(File::PATH_SEPARATOR)
          end
        end
      end

    private

      def requires()
        @requires ||= Buildr.artifacts(REQUIRES).each { |artifact| artifact.invoke }.map(&:to_s)
      end

    end

    def open_jpa_enhance(options = nil)
      jpa_options = { :output=>compile.target, :classpath=>compile.classpath,
                      :properties=>path_to("src/main/resources/META-INF/persistence.xml") }
      OpenJPA.enhance jpa_options.merge(options || {})
    end

  end

  class Project
    include OpenJPA
  end
end
