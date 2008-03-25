#!/usr/bin/env ruby
require "rubygems"
require 'net/http'
require 'open-uri' 
require "zip/zip"
require 'yaml'

config_file = if not ARGV[0] == nil 
  then
    ARGV[0]
  else
    "./config.yml"
  end
config = YAML::load( File.open( config_file ) )

TEMPO_SVN = config["tempo_svn"]
DEBUG = config["debug"]
REBUILD_TEMPO = config["rebuild"]
APACHE_MIRROR = config["apache_mirror"]
TOMCAT_5_DOWNLOAD = APACHE_MIRROR + "/tomcat/tomcat-5/v5.5.26/bin/apache-tomcat-5.5.26.zip"
AXIS_DOWNLOAD = APACHE_MIRROR + "/ws/axis2/1_3/axis2-1.3-war.zip"
ODE_DOWNLOAD = APACHE_MIRROR + "/ode/apache-ode-war-1.1.1.zip"

# Unzip a file
def unzip(x, basefolder = ".")
  puts "Unzipping file:#{x}" if DEBUG
  return if not basefolder == "." and File.exist? basefolder
  outdir = basefolder
  Zip::ZipFile::open(x) { |zf|
    zf.each { |e|
      fpath = File.join(outdir, e.name)
      FileUtils.mkdir_p(File.dirname(fpath))
      zf.extract(e, fpath)
    }
  }
end

# Follow redirects
def fetch(uri_str, limit = 10)
  # You should choose better exception.
  raise ArgumentError, 'HTTP redirect too deep' if limit == 0
  response = Net::HTTP.get_response(URI.parse(uri_str))
  case response
  when Net::HTTPSuccess     then response
  when Net::HTTPRedirection then fetch(response['location'], limit - 1)
  else
    response.error!
  end
end

# Download and unzip file
def download_to(filename, url, unzip=true, message="Downloading #{url}") 
  puts message if DEBUG
  open(filename, "wb") { |file|
    file.write(fetch(url).body)
  }
  unzip(filename) if unzip
end

def download_and_copy(url, folder)
  puts "Copying #{url} to #{folder}" if DEBUG
  download( url,false )
  File.copy( filename_from_url(url), folder, DEBUG )
end

def filename_from_url (url)
  url.slice(url.rindex("/")+1, url.length)
end

def download(url, unzip=true)
  filename = filename_from_url url
  download_to(filename,url,unzip) if not File.exist?(filename)
  return filename.slice(0,filename.rindex("."))
end

def build_tempo
  chd_and_execute(TEMPO_SVN) {
     system("buildr clean package")  
  }
end

def chd_and_execute new_dir
  pwd = Dir.pwd
  Dir.chdir new_dir
  yield
  Dir.chdir pwd
end


def explain(txt, col = 80)
  puts txt.gsub(/(.{1,#{col}})( +|$\n?)|(.{1,#{col}})/,"-- \\1\\3\n") 
end


def title title
  puts "===================================================================================================="
  puts "\t#{title}"
  puts "===================================================================================================="
end

class Finder 
  
  def initialize
  end
  
  def search base_folder, extension, filename="*"
    Dir["#{base_folder}/#{filename}.#{extension}"][0]
  end
  
  def find_war(base_folder)
    search( base_folder, "war" )
  end

  def find_aar(base_folder)
    search( base_folder, "aar" )
  end
  
  def find_tempo_component filename, ext="*ar"
    location = TEMPO_SVN + File::SEPARATOR + filename + File::SEPARATOR + "target"
    puts "Searching component in #{location}" if DEBUG
    search( location, ext )
  end
  
end

class WarInstaller
  def initialize(webapp_folder, extract=false)
    @webapp_folder = webapp_folder
    @extract = extract
    @finder = Finder.new
  end
  
  def install(war_file, war_name) 
    puts "Installing #{war_name} to #{war_name}" if DEBUG
    puts "Currently in folder: #{Dir.pwd}" if DEBUG
    File.copy(war_file, @webapp_folder+File::SEPARATOR+war_name, DEBUG)
    if @extract
      return extract_war(war_name,@webapp_folder) 
    else
      return find_war_folder(war_file)
    end
  end
  
  def find_war_folder war_file
    war_file.slice(0,war_file.rindex(".war"))
  end
  
  def extract_war(jar_file, to_dir) 
    puts "Extracting #{jar_file}" if DEBUG
    jar_folder = find_war_folder(jar_file)
    war_dir = to_dir + File::SEPARATOR + jar_folder
    return war_dir if File.exist? war_dir
    Dir.mkdir war_dir
    chd_and_execute(war_dir) {
      local_jar_file = ".." + File::SEPARATOR + "#{jar_file}"
      puts "Extracting #{local_jar_file}" if DEBUG
      system "jar xf #{local_jar_file}"
    }
    return war_dir
  end
  
  def install_tempo_war service_name, war_name=service_name
    install @finder.find_tempo_component( service_name, "war"), "#{war_name}.war"
  end
end

class ServiceInstaller
  def initialize(axis_folder)
     @process_folder = axis_folder + File::SEPARATOR + "WEB-INF" + File::SEPARATOR + "services"
     FileUtils.mkdir_p @process_folder
     @finder = Finder.new
  end
  
  def install aar
    File.copy( aar, @process_folder, DEBUG )
  end
  
  def install_tempo_aar service_name
    puts "Installing service: #{service_name}" if DEBUG
    install @finder.find_tempo_component( service_name, "aar" )
  end
end

class OdeProcessInstaller
  def initialize(base_dir, tempo_trunk_process_folder)
    @ode_processes_folder = base_dir
    @processes_folder = tempo_trunk_process_folder
  end
  def install_process_from_tempo_trunk process_name
    FileUtils.mkdir_p "#{@ode_processes_folder}/#{process_name}"  
    FileUtils.cp_r( Dir.glob("#{@processes_folder}/#{process_name}/src/main/resources/*.*"), "#{@ode_processes_folder}/#{process_name}" )  
  end
end



title "Downloading required files"
explain "Downloading tomcat (java web application engine), ode (process engine) and axis (web service engine)"
##
tomcat_folder = download( TOMCAT_5_DOWNLOAD )
ode_folder = download( ODE_DOWNLOAD )
axis_folder = download( AXIS_DOWNLOAD, false)
unzip( filename_from_url( AXIS_DOWNLOAD ), axis_folder )
##

title "Define install variables"
explain "Defining variables for webapp folders and services folders"
##
finder = Finder.new
webapp_folder = tomcat_folder + File::SEPARATOR + "webapps"
ode_war = finder.find_war( ode_folder )
axis_war = finder.find_war( axis_folder )
##

title "Install core components (ode, axis)"
explain "Expand ode and axis war file, since we want to do offline deployment and just copy files"
##
wi = WarInstaller.new webapp_folder, true
ode_war_folder   = wi.install ode_war, "ode.war"
axis2_war_folder = wi.install axis_war, "axis2.war"
FileUtils.rm "#{axis2_war_folder}/WEB-INF/classes/log4j.properties", :force => true
##

title "Replace xml beans implementation"
explain "We're using a different version of xbean in tempo which is incompatible with the one in axis. Replacing with a xbean version that is up to date"
##
axis_war_lib = "#{axis2_war_folder}/WEB-INF/lib"
FileUtils.rm "#{axis_war_lib}/xbean-2.2.0.jar", :force => true
download_and_copy "http://www.intalio.org/public/maven2/xmlbeans/xbean/2.3.0/xbean-2.3.0.jar", axis_war_lib
##

title "Build tempo (using svn checkout and buildr)"
explain "Using buildr to build the full suite of tempo components"
##
build_tempo if REBUILD_TEMPO
##

title "Install tempo web services"
explain "Now we install the different tempo web services"
explain "Deploy is responsible for deployment tempo artifacts, like forms, and people initiated activities"
explain "Task Attachment services is responsible for keeping the task attachments. Could also be stored in a CMS"
explain "TMS is reponsible for managing the different set of tasks related to human activities"
explain "A Token service based security service, that can also be integrated with other components"
##
si = ServiceInstaller.new( axis2_war_folder )
si.install_tempo_aar( "deploy-ws-service" )
si.install_tempo_aar( "tas-service" )
si.install_tempo_aar( "tms-service" )
si.install_tempo_aar( "security-ws-service" )
##

title "Install tempo web applications"
explain "Now we install the different tempo war files"
explain "FDS is the form dispatcher service, responsible mainly for replacing namespaces"
explain "UI-FW is the user interface, where user can handle their tasks and complete them"
explain "WDS service, is like a Web Resources services."
explain "XFM is responsible for displaying the forms to be filled when a user is handling a task."
##
wi.install_tempo_war( "fds" )
wi.install_tempo_war( "ui-fw" )
wi.install_tempo_war( "wds-service", "wds" )
wi.install_tempo_war( "xforms-manager", "xFormsManager" )
##

title "Install xpath extension in Ode"
explain "Required library for Ode"
##
ode_webinf = File.expand_path("#{ode_war_folder}/WEB-INF")
ode_processes_folder = "#{ode_webinf}/processes"
processes_folder = "#{TEMPO_SVN}/processes/"
opi = OdeProcessInstaller.new ode_processes_folder, processes_folder
xp_jar = finder.search( processes_folder + "xpath-extensions" + File::SEPARATOR + "target", "jar")
File.copy xp_jar, ode_webinf + "/lib", DEBUG
##

title "Install TaskManager in Ode"
explain "The task manager process is a regular process running in Ode, responsible for the management of tasks"
##
opi.install_process_from_tempo_trunk "TaskManager"
##


title "Install AbsenceRequest in Ode"
explain "A sample process, that can be started from the UI-FW"
##
opi.install_process_from_tempo_trunk "AbsenceRequest"
##

title "Installing missing libs into Tomcat"
explain "Some libs are missing in tomcat, so we add them here."
##
lib_folder = tomcat_folder + File::SEPARATOR + "common" + File::SEPARATOR + "lib"
MISSING_LIBS= [
  "http://www.intalio.org/public/maven2/com/mysql/mysql-connector/mysql-connector-java/5.1.6/mysql-connector-java-5.1.6.jar",
  "http://www.intalio.org/public/maven2/commons-dbcp/commons-dbcp/1.2.1/commons-dbcp-1.2.1.jar",
  "http://www.intalio.org/public/maven2/commons-collections/commons-collections/3.2/commons-collections-3.2.jar",
  "http://www.intalio.org/public/maven2/commons-pool/commons-pool/1.3/commons-pool-1.3.jar",
  
  "http://www.intalio.org/public/maven2/org/slf4j/slf4j-api/1.4.3/slf4j-api-1.4.3.jar",
  "http://www.intalio.org/public/maven2/org/slf4j/slf4j-log4j12/1.4.3/slf4j-log4j12-1.4.3.jar",
  "http://www.intalio.org/public/maven2/org/slf4j/slf4j-log4j12/1.4.3/slf4j-log4j12-1.4.3.jar",
  "http://www.intalio.org/public/maven2/log4j/log4j/1.2.15/log4j-1.2.15.jar"
  ]
MISSING_LIBS.each {|lib| 
  download_and_copy( lib, lib_folder )
}
##

title "Install registry into common lib"
explain "Registry for deployment services. It is stored as a shared resources in tomcat"
##
registry_jar = finder.find_tempo_component("registry")
File.copy registry_jar, lib_folder
##

title "Copying tempo config files"
explain "Tempo has a set of required config files, generally one for each components."
explain "In this build, we focus on a mysql datasource, so we define a JNDI DS in the tomcat config files"
explain "Also copying the ode config files"
##
tomcat_config_folder = tomcat_folder + File::SEPARATOR + "var" + File::SEPARATOR + "config"
FileUtils.mkdir_p tomcat_config_folder
tempo_svn_config_folder = TEMPO_SVN+File::SEPARATOR+"config"
# TODO:
# Copying registry and deploy properties file result in not being able to access JNDI properly, 
# filtering out and copying only xml files for the moment
# 
# config_files = File.join(tempo_svn_config_folder,"*.*")
config_files = File.join(tempo_svn_config_folder,"*.xml")
Dir.glob(config_files) {|x| File.copy(x, tomcat_config_folder, DEBUG)}

mysql_ds_config_files = File.join("#{TEMPO_SVN}/rsc/tempo-sql","*.xml")
Dir.glob(mysql_ds_config_files) {|x| File.copy(x, tomcat_config_folder, DEBUG)}

File.copy "#{TEMPO_SVN}/rsc/tomcat/ode-axis2.properties", tomcat_config_folder
##

title "Creating setenv file (java opts and config)"
explain "Settings java options, including memory settings"
##
tomcat_bin_folder = tomcat_folder + File::SEPARATOR + "bin" + File::SEPARATOR
create_mode = File::CREAT|File::TRUNC|File::RDWR
if `uname`
  file_path = tomcat_bin_folder + "setenv.sh"
  file = File.new file_path,create_mode
  file.puts "export JAVA_OPTS=\"-XX:MaxPermSize=256m -Xms64m -Xmx512m -Dorg.intalio.tempo.configDirectory=$CATALINA_HOME/var/config -Dorg.apache.ode.configDir=$CATALINA_HOME/var/config\""
else
  file_path = tomcat_bin_folder + "setenv.bat"
  file = (File.new file_path,create_mode)
  file.puts "set JAVA_OPTS=-XX:MaxPermSize=256m -Xms64m -Xmx512m -Dorg.intalio.tempo.configDirectory=$CATALINA_HOME\\var\\config -Dorg.apache.ode.configDir=$CATALINA_HOME\\var\\config"
end
##

title "Changing file permissions (shell scripts)"
explain "Needed so all the scripts can be exectutable just after the build"
##
shfiles = File.join(tomcat_bin_folder, "*.sh")
Dir.glob(shfiles) {|x| FileUtils.chmod 0755, x, :verbose => DEBUG }
##

title "Creating deploy folder and adding AbsenceRequest"
explain "Adding the forms needed to run the AbsenceRequest example"
##
ar_zip = finder.find_tempo_component( "forms" + File::SEPARATOR + "AbsenceRequest", "zip")
deploy_folder = tomcat_folder + File::SEPARATOR + "var" + File::SEPARATOR + "deploy"
FileUtils.mkdir_p deploy_folder
File.copy(ar_zip, deploy_folder, DEBUG )
##

title "Copying tomcat config xml files (JNDI resources)"
explain "Making the deploy registry and the mysql DS available to all tomcat application"
##
Dir.glob(File.join("#{TEMPO_SVN}/rsc/tomcat", "*.*")) {|x| File.copy(x,"#{tomcat_folder}/conf", DEBUG)}
##

title "Expanding tomcat classpath"
explain "Final touch, so the logging is done properly"
##
file_cp = tomcat_bin_folder + File::SEPARATOR + "setclasspath.sh"
File.open(file_cp, File::WRONLY|File::APPEND) {|file| file << "export CLASSPATH=$CLASSPATH:$CATALINA_HOME/conf"}
##

title "Deleting war files from webapp folder"
Dir.glob(File.join("#{webapp_folder}", "*.war")) {|x| File.delete x}

title "Almost done !"
explain "Now create a mysql database named \"bpms\" with access to user <root> and no password"
explain "Load the ode schema into mysql from the file #{TEMPO_SVN}/rsc/tempo-sql/ode-mysql.sql"
explain "Once this is done, start tomcat with the following command:"
explain "./catalina.sh run"
explain "Now you can browse http://localhost:8080/ui-fw/ and login with user <admin> and password <changeit>"

title  "Enjoy!!"