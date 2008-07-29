
def replace_text(inputFile, substitutions, outputFile)
  subCount = 0
  File.open(inputFile, "r") do |input|
    lines = []
    while (line = input.gets)  
      substitutions.keys.each do |prop|
        regex = Regexp.compile(prop)
        if regex.match(line)
          # puts "  match #{line}"
          line = line.gsub(prop, substitutions[prop])
          # puts "replace #{line}"
          subCount += 1
        end
      end
      lines << line
    end
    raise "Missing substitutions" if subCount != substitutions.size
    File.open(outputFile, "w") { |output| output << lines }
  end
end

def generate_sql(classpath, schemaname="db.schema", tempDir="/tmp")
    schemas = []
    %w{ Derby MySQL Oracle SQLServer Sybase DB2 Postgres}.each do |db|
      persistence = _("src/main/resources/META-INF/persistence.xml")
      persistence_db = file("target/persistence-#{db}.xml" => persistence) do |task|
        new_properties = <<END
          <properties>
              <property name="openjpa.Log" value="DefaultLevel=WARN"/>
              <property name="openjpa.ConnectionDriverName" value="org.apache.commons.dbcp.BasicDataSource"/>
              <property name="openjpa.jdbc.DBDictionary" value="org.apache.openjpa.jdbc.sql.#{db}Dictionary"/>
              <property name="openjpa.ConnectionProperties"
                        value="DriverClassName=org.apache.derby.jdbc.EmbeddedDriver,Url=jdbc:derby:#{tempDir}/database/openjpa-test-database;create=true"/>
          </properties>
END
        mkpath _("#{tempDir}/target"), :verbose=>false
        replace_text(persistence, { "<properties />" => new_properties }, task.name) 
      end
      
      sql = file("target/#{db.downcase}.#{schemaname.downcase}.sql"=>persistence_db) do |task|
        Buildr::OpenJPA.mapping_tool :properties=>persistence_db, :action=>"build", :sql=>task.name, :classpath => classpath
        FileUtils.rm_f "#{Dir.pwd}/derby.log" # delete derby log file
      end
      schemas << sql
    end
    schemas
end
