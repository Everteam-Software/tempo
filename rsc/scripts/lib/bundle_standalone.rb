# Install and copy axis war file and ode war file
# Downloaded from the urls defined in build_support.rb
# Versions are defined in the config file
def setup_axis_and_ode
  @@axis_folder = download_unzip(BUILD_URI[:axis2],false)
  unzip2(filename_from_url(BUILD_URI[:axis2]),@@axis_folder)

  @@ode_folder = download_unzip(BUILD_URI[:ode][BUILD_CONFIG[:ode]])
  @@finder = Finder.new

  ode_war = @@finder.find_war( @@ode_folder )
  axis_war = @@finder.find_war( @@axis_folder )

  @@ode_war_folder   = @@wi.install ode_war, "ode.war"
  @@axis2_war_folder = @@wi.install axis_war, "axis2.war"

  # copy required xpath extension for ode
  # someone explain me why this is in tempo
  locate_and_copy("org.intalio.tempo:tempo-processes-xpath-extensions:jar:#{BUILD_CONFIG[:tempo][:core]}", "#{@@ode_war_folder}/WEB-INF/lib")

  # copy ode and axis2 only related configuration
  @@config_folder = check_folder("#{@@server_folder}/var/config")
  File.copy "#{TEMPO_SVN}/rsc/bundle-config/ode-axis2.properties", @@config_folder
  File.copy "#{TEMPO_SVN}/rsc/bundle-config/axis2.xml", "#{@@webapp_folder}/axis2/WEB-INF/conf"
end

# Install and copy tempo axis services
# the version values are taken from the config file
def install_tempo_services 
  @@si = ServiceInstaller.new( @@axis2_war_folder )
  # @@si.install_artifact_aar("org.intalio.deploy:deploy-ws-service:aar:#{BUILD_CONFIG[:tempo][:deploy]}")
  # @@si.install_artifact_aar("org.intalio.security:security-ws-service:aar:#{BUILD_CONFIG[:tempo][:security]}")
  @@si.install_artifact_aar("org.intalio.tempo:tempo-deploy-ws-service:aar:#{BUILD_CONFIG[:tempo][:core]}")
  @@si.install_artifact_aar("org.intalio.tempo:tempo-security-ws-service:aar:#{BUILD_CONFIG[:tempo][:core]}")
  @@si.install_artifact_aar("org.intalio.tempo:tempo-tas-service:aar:#{BUILD_CONFIG[:tempo][:core]}")
  @@si.install_artifact_aar("org.intalio.tempo:tempo-tms-service:aar:#{BUILD_CONFIG[:tempo][:core]}")
end

# Install and copy tempo web applications
# the version numbers are taken directly from the config file
def install_tempo_webapps
  @@wi.install_war_artifact("org.intalio.tempo:tempo-wds-service:war:#{BUILD_CONFIG[:tempo][:core]}","wds")
  @@wi.install_war_artifact("org.intalio.tempo:tempo-fds:war:#{BUILD_CONFIG[:tempo][:core]}","fds")
  @@wi.install_war_artifact("org.intalio.tempo:xforms-manager:war:#{BUILD_CONFIG[:tempo][:formManager]}","xFormsManager")
end

def install_tempo_uifw portlet=false
  @@wi.install_war_artifact("org.intalio.tempo:tempo-ui-fw:war:#{BUILD_CONFIG[:tempo][:core]}","ui-fw")
end

# Install the TMP process, and the absence request process into the ode services folders
def install_tmp
  opi = OdeProcessInstaller.new @@server_folder
  opi.install_artifact "org.intalio.tempo:tempo-processes-TaskManager:jar:#{BUILD_CONFIG[:tempo][:core]}", "TaskManager"
end

# Copy missing tomcat libs, including the tempo registry
def copy_missing_lib
  @@lib_folder = "#{@@server_folder}/common/lib" # tomcat5
  FileUtils.mkdir_p @@lib_folder
  missing_libs = [
    DB_CONNECTOR[:mysql],
    APACHE_COMMONS[:dbcp],
    APACHE_COMMONS[:collections],
    APACHE_COMMONS[:pool],
    STAX_API,
    STAX_LIB,
    WSDL4J,
    SLF4J,
    LOG4J,
    XERCES,
    "org.intalio.tempo:tempo-registry:jar:#{BUILD_CONFIG[:tempo][:core]}"
  ]
  missing_libs.each {|lib| locate_and_copy( lib, @@lib_folder )}
end

# install the absence request process
# should be turn into a separate installer once we have more processes to install
def install_absence_request
  opi = OdeProcessInstaller.new @@server_folder
  # copy the process
  opi.install_artifact "org.intalio.tempo:tempo-processes-AbsenceRequest:jar:#{BUILD_CONFIG[:tempo][:core]}", "AbsenceRequest"
  # copy the forms and the pipa
  abs_deploy_folder = check_folder("#{@@server_folder}/var/deploy/AbsenceRequest")
  ar_zip = locate_artifact("org.intalio.tempo:tempo-forms-AbsenceRequest:zip:#{BUILD_CONFIG[:tempo][:core]}")
  unzip2(ar_zip, abs_deploy_folder, true)
end

# remove
# - war files
# - duplicate log libraries
# - duplicate log definition
# - DS_Store files
# - servlet-api jar files (just in case)
def clean_unused_files
  ["temp", "work", "LICENSE", "RELEASE-NOTES", "NOTICE", "RUNNING.txt"].each { |x| FileUtils.rm_r "#{@@server_folder}/#{x}", :force => true}
  Dir.glob(File.join("#{@@webapp_folder}", "*.war")) {|x| File.delete x}
  Dir.glob(File.join("#{@@lib_folder}", "commons-logging*.jar")) {|x| File.delete x}
  Dir.glob(File.join("#{@@webapp_folder}", "commons-logging*.jar")) {|x| File.delete x}
  Dir.glob(File.join("#{@@lib_folder}", "jcl104*.jar")) {|x| File.delete x}
  Dir.glob(File.join("#{@@webapp_folder}", "jcl104*.jar")) {|x| File.delete x}
  Dir.glob(File.join("#{@@webapp_folder}", "**/servlet-api-*")) {|x| File.delete x}
  Dir.glob(File.join("#{@@webapp_folder}", "**/jsp-api-*")) {|x| File.delete x}
  Dir.glob(File.join("#{@@webapp_folder}", "**/casclient*")) {|x| File.delete x}
  Dir.glob(File.join("#{@@webapp_folder}", "**/log4j-*.jar")) {|x| File.delete x}
  Dir.glob(File.join("#{@@webapp_folder}", "**/log4j.properties")) {|x| File.delete x}
  Dir.glob(File.join("#{@@webapp_folder}", "**/slf4j*.jar")) {|x| File.delete x}
  Dir.glob(File.join("#{@@server_folder}/common/endorsed", "*.jar")) {|x| File.delete x}
  Dir.glob(File.join("#{@@server_folder}/", "**/.DS_Store")) {|x| File.delete x}

  check_folder "#{@@server_folder}/temp"
end

# generate a single mysql script file for both ode and tempo
def generate_mysql_file
  f = File.new("#{@@server_folder}/bpms.sql",  "w")
  Dir.glob(File.join("#{TEMPO_SVN}/db-schema/mysql",'*.sql')) {|x| 
    f.write("-- file:#{x}\n")
    f.write(File.open(x).read)
  }
  ode_mysql = "#{TEMPO_SVN}/rsc/tempo-sql/#{BUILD_CONFIG[:ode]}/ode-mysql.sql"
  f.write("-- file:#{ode_mysql}\n")
  f.write(File.open(ode_mysql).read)
  f.close
end

# add tomcat (5) configuration files
def configure_tomcat
  Dir.glob(File.join("#{TEMPO_SVN}/rsc/tomcat", "*.*")) {|x| File.copy(x,"#{@@server_folder}/conf", BUILD_DEBUG)}
  FileUtils.cp "#{TEMPO_SVN}/rsc/bundle-config/log4j.properties", "#{@@server_folder}/common/classes"
end

# copy some tempo configuration files
# note that you can restrict the files being copied, by given an array of files
def copy_tempo_config_files filter="*.*"
  config_folder = check_folder("#{@@server_folder}/var/config")
  config_files = File.join("#{TEMPO_SVN}/config",filter)
  Dir.glob(config_files) {|x| File.copy(x, config_folder, BUILD_DEBUG)}
end

# define standard tomcat only java options
def setup_java_options
  options = "-XX:MaxPermSize=256m -server"
  options << " -Djavax.net.ssl.trustStore=$CATALINA_HOME/var/config/intalio-keystore.jks"
  options << " -Dfile.encoding=UTF-8"
  options << " -Xms128m -Xmx1024m"
  options << " -Dorg.intalio.tempo.configDirectory=$CATALINA_HOME/var/config"
  options << " -Dorg.apache.ode.configDir=$CATALINA_HOME/var/config"
  options << " -Dorg.intalio.deploy.configDirectory=$CATALINA_HOME/var/deploy"
  setenv "#{@@server_folder}/bin", options
end

# properly set the unix flag on the sh files
def chmod_sh_files
  sh_files @@server_folder
end

def set_tomcat_ports map, conf_file="#{@@server_folder}/conf/server.xml"
  replace_all_with_map(map,conf_file)
end

def install_cas_webapp
  @@wi.install_war_artifact("org.intalio.tempo:tempo-cas-webapp:war:#{BUILD_CONFIG[:tempo][:cas]}","cas")
end

def install_ldap_support

end