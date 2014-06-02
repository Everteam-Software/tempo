# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with this
# work for additional information regarding copyright ownership.  The ASF
# licenses this file to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
# License for the specific language governing permissions and limitations under
# the License.


require 'buildr'

module Buildr

  # Provides Hibernate bytecode enhancement.
  module HibernateJPA

    REQUIRES = [
     APACHE_COMMONS[:collections],
     APACHE_COMMONS[:lang],
     APACHE_COMMONS[:pool],
     LOG4J,
     SLF4J.values ]

    Java.classpath << HIBERNATE_3_X << REQUIRES

    class << self

      def enhance(options)

        Buildr.ant "hibernatejpa" do |ant|
          ant.taskdef :name=>"instrument", :classname=>"org.hibernate.tool.instrument.javassist.InstrumentTask",
            :classpath=>requires.join(File::PATH_SEPARATOR)
          ant.instrument :verbose=>"false" do
            ant.fileset :dir=>options[:output].to_s do
				ant.include :name=>"**/org/intalio/tempo/workflow/task/*.class"
            end
          end
        end
      end

    private

      def requires()
        @requires ||= Buildr.artifacts(REQUIRES).each { |artifact| artifact.invoke }.map(&:to_s)
      end

    end

    def hibernate_jpa_enhance(options = nil)
      jpa_options = { :output=>compile.target, :classpath=>compile.dependencies }
      HibernateJPA.enhance jpa_options.merge(options || {})
    end

  end

  class Project
    include HibernateJPA
  end
end
