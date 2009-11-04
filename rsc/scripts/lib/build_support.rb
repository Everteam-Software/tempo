require 'net/http'
require 'open-uri'
require 'fileutils'
require 'open-uri'
require "hpricot"

# monkey patching to allow buildr to run without a buildfile (since buildr 1.3.3)
class Buildr::Application 
  def settings
    @settings ||= Settings.new(self) 
  end 
end

module BuildMode
  BPMS = 0
  LIFERAY = 1
  TOMCAT5 = 2
  TOMCAT6 = 3
  LDAP = 4
  ALFRESCO = 5
  OPENSSO = 6
  ZIP = 7
  OPENSSO_SERVER = 8
  UIFW = 9
  TOMCAT = 10
  REMOTE = 11
  AGENT = 12
  TOKEN_SERVICE = 15
  CAS = 16
  RELEASE = 17
  JETTY = 18
end

module BuildActivate
  
  attr_accessor :config
  attr_accessor :show_steps
  
  # only execute the step, if the step_mode 
  # applies to the configuration defined in config.rb
  # this also to have something like
  #
  # BuildMode::OPENSSO|BuildMode::TOMCAT
  #
  # in the config file, and get 
  #  activate_step BuildMode::OPENSSO do
  #    ...
  #  end
  # 
  # executed.
  def activate_step step_mode, msg=""
    if support_step step_mode
      if not show_steps
        puts "--> #{msg}"
        yield 
      else
        puts "--> Planned step:#{msg}"
      end
    end
  end
  
  # check whether the given step will be executed or not
  def support_step step_mode
    return (step_mode & config[:mode]) == step_mode
  end
  
end

module BuildSupport
  
  
  # Replace all the strings in a file
  def replace_all(src_string, target_string, src_file, target_file=src_file)
    f = IO.read(src_file)
    File.open(target_file, "w") { |io|
      io << f.gsub(src_string, target_string)
    }
  end
  
  def replace_all_in_folder(src_string, target_string, src_folder)
    Dir.entries(src_folder).each do |file1|
      f = File.expand_path(file1, src_folder)
      replace_all(src_string,target_string,f) if File.file? f
    end
  end
  
  def replace_all_with_map_in_folder(map, src_folder)
    map.each { |key,value|
      replace_all_in_folder(key,value, src_folder)
    }
  end

  # use the above to apply replace on a multiple string on the same file
  def replace_all_with_map(map, src_file, target_file=src_file)
    map.each { |key,value|
      replace_all(key,value, src_file, target_file)
    }
  end
  
  # ensure all the sh files in the /bin folder 
  # of the server folder have the proper unix rights
  # for execution
  def sh_files server_folder
    shfiles = File.join("#{server_folder}/bin", "*")
    Dir.glob(shfiles) {|x| FileUtils.chmod 0755, x, :verbose => BUILD_DEBUG }
  end
  
  # check folders are propery all created
  def check_folder path
    FileUtils.mkdir_p path
    return path
  end

  # Find the closest/fastest apache mirror
  # this calls the apache cgi script
  def find_apache_mirror
    begin
      doc = Hpricot(open("http://www.apache.org/dyn/closer.cgi"))
      doc.search("//div[@='section-content']//strong")[0].inner_html
    rescue SocketError
      puts "WARNING: Cannot find a valid apache mirror"
      return ""
    end
  end
  
  # compress a full folderusing zip
  # the file name is the name of the folder
  # with a .zip extension appended
  def compress(path)
    path.sub!(%r[/$],'')
    archive = File.expand_path(File.join(path,"../"+File.basename(path))+'.zip')
    pp archive if BUILD_DEBUG
    FileUtils.rm archive, :force=>true

    Zip::ZipFile.open(archive, 'w') do |zipfile|
      Dir["#{path}/**/**"].reject{|f|f==archive}.each do |file|
        zipfile.add(file.sub(path+'/',''),file)
      end
    end
    
    archive
  end

  # Unzip a file
  def unzip2(x, basefolder = ".", forceextract = false)
    puts "Unzipping file:#{x}" if BUILD_DEBUG
    if not basefolder == "." and File.exist? basefolder and !forceextract
      return 
    end
    outdir = check_folder(basefolder)
    Zip::ZipFile::open(x) { |zf|
      zf.each { |e|
        fpath = File.join(outdir, e.name)
        if (not File.exist?(fpath)) then
          FileUtils.mkdir_p(File.dirname(fpath))
          zf.extract(e, fpath)
        end
      }
    }
  end
  
  def unzip_artifact(artifact, folder)
    puts "Unzipping artifact:#{artifact} to #{folder}" if BUILD_DEBUG
    local = locate_artifact artifact
    unzip2(local,folder,true)
  end

  # Download and unzip file
  def download_to(filename, url, unzip=true, message="Downloading #{url}")
    puts message if BUILD_DEBUG
    spec = "org.intalio.tempo.build:#{filename}:zip:1.0"
    ar = Buildr::artifact(spec)
    download(ar=>url)
    ar.invoke
    FileUtils.cp repositories.locate(spec), filename
    unzip2(filename) if unzip
  end
  # download a file from a given url and copy it to the given folder
  def download_and_copy(url, folder)
    puts "Copying #{url} to #{folder}" if BUILD_DEBUG
    download_unzip( url,false )
    FileUtils.cp( filename_from_url(url), folder)
  end
  def download_unzip(url, unzip=true)
    filename = filename_from_url url
    ret = filename.slice(0,filename.rindex("."))
    download_to(filename,url,unzip) if not File.exist?(filename.gsub(".zip",""))
    ret
  end
  def download_and_unzip(arg)
    filename = filename_from_url(arg[:url])
    spec = "org.intalio.tempo.build:#{filename}:zip:1.0"
    ar = Buildr::artifact(spec)
    download(ar=>arg[:url])
    ar.invoke
    FileUtils.cp repositories.locate(spec), filename
    unzip2(filename, arg[:base_folder])
    arg[:base_folder]
  end
  
  
  
  # Returns the local path of an artifact
  def locate_artifact lib
    Buildr::artifact(lib).invoke
    Buildr::repositories.locate(lib)
  end

  # Uses buildr, to retrieve the artifact
  # then locate it in the local repository and then
  # copy it afterwards
  def locate_and_copy(lib, folder)
    puts "locating #{lib}" if BUILD_DEBUG
    if(lib.kind_of? Array) then
      lib.each {|l| locate_and_copy l,folder}
    else
      FileUtils.cp(locate_artifact(lib),folder)
    end
  end

  # get the file name, from the full url
  # this is used after a  download has successfully 
  # completed
  def filename_from_url (url)
    url.slice(url.rindex("/")+1, url.length)
  end

  
  # rename a folder 
  # this uses the rename method from file 
  # but also returns the path to the newly 
  # renamed folder, which the original method
  # doesn't do
  def rename_folder org_name, new_name
    parent = File.dirname(org_name)
    begin 
      FileUtils.mv(org_name,new_name) 
    rescue 
      print "An error occurred: ",$!, "\n"
    end
    return "#{parent}/#{new_name}"
  end

  # make a clean build of tempo, directly from this method. 
  # This doesn't spawn another process
  def build_tempo
    chd_and_execute(TEMPO_SVN) {
      system("buildr clean package test=no")
    }
  end

  # change directory and execute a command, then go back to the 
  # original directory
  def chd_and_execute new_dir
    pwd = Dir.pwd
    Dir.chdir new_dir
    yield
    Dir.chdir pwd
  end

  # this ensure that the setenv.sh/.bat files are in sync
  # this takes some java opts defined the unix way
  # then do some regexp so that we have the equivalent 
  # windows version in the .bat file
  def setenv bin_folder,opts
    File.open("#{bin_folder}/setenv.sh", "a") { |file|
      file << "export JAVA_OPTS=\"#{opts}\""
    }
    File.open("#{bin_folder}/setenv.bat", "a") { |file|
      file << "set JAVA_OPTS=#{opts.gsub('$CATALINA_HOME', '%CATALINA_HOME%').gsub('/','\\')}"
    }
  end

  # simply put out a section message, meant at describing the current step
  def title title
    puts "===================================================================================================="
    puts "\t#{title}"
    puts "===================================================================================================="
  end
  
  # simply put a minor message, meant at explaining the current step   
  def explain(txt, col = 80)
    puts txt.gsub(/(.{1,#{col}})( +|$\n?)|(.{1,#{col}})/,"-- \\1\\3\n")
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
      puts "Searching component in #{location}" if BUILD_DEBUG
      search( location, ext )
    end

  end

  class WarInstaller
    attr_accessor :webapp_folder
    
    def initialize(folder, extract=false, clean=false)
      if folder =~ /webapps$/ then
        @webapp_folder = folder
      else
        @webapp_folder = File.expand_path("#{folder}/webapps")
      end
      @extract = extract
      @clean = clean
    end

    def install(war_file, war_name)
      puts "Installing #{war_name} to #{war_name}" if BUILD_DEBUG
      puts "Currently in folder: #{Dir.pwd}" if BUILD_DEBUG
      FileUtils.cp(File.expand_path(war_file), File.expand_path("#{@webapp_folder}/#{war_name}"))
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
      puts "Extracting #{jar_file}" if BUILD_DEBUG
      jar_folder = find_war_folder(jar_file)
      war_dir = File.expand_path("#{to_dir}/#{jar_folder}")
      return war_dir if File.exist? war_dir
      Dir.mkdir war_dir
      chd_and_execute(war_dir) {
        local_jar_file = File.expand_path("../#{jar_file}")
        puts "Extracting #{local_jar_file}" if BUILD_DEBUG
        system "jar xf #{local_jar_file}"
        FileUtils.rm local_jar_file if @clean
      }
      return war_dir
    end
    
    # download, locate the war file corresponding to the artifact description, 
    # and copy to the server webapp folder
    def install_war_artifact artifact_name, war_name
      war_name += ".war"
      install locate_artifact(artifact_name), war_name
    end
    
    def copy_war_artifact artifact_name, target_folder, war_name
      war_name += ".war"
      FileUtils.cp locate_artifact(artifact_name), File.expand_path("#{target_folder}/#{war_name}")
    end
  end

  class ServiceInstaller
    def initialize(axis_folder)
      @process_folder = File.expand_path("#{axis_folder}/WEB-INF/services")
      FileUtils.mkdir_p @process_folder
      @finder = Finder.new
    end

    def install aar
      FileUtils.cp( aar, @process_folder)
    end
    
    def install_artifact_aar artifact
      install locate_artifact(artifact)
    end

    def install_tempo_aar service_name
      puts "Installing service: #{service_name}" if BUILD_DEBUG
      service = @finder.find_tempo_component( service_name, "aar" )
      if ( service != nil)
        install service
      else
        raise "Could not find service to install. Make sure you compile tempo code first, or force rebuild"
      end
    end
  
  end

  class OdeProcessInstaller
    def initialize(base_dir)
      check_folder(File.expand_path("#{base_dir}/var/processes")) # deprecated but still needed
      @ode_processes_folder = check_folder(File.expand_path("#{base_dir}/var/deploy"))
    end
    def install_artifact artifact, processName
      unzip_artifact(artifact,"#{@ode_processes_folder}/#{processName}/#{processName}.ode")
    end
  end

  class JavaProps
    attr :file, :properties

    #Takes a file and loads the properties in that file
    def initialize file
      @file = file
      @properties = {}
      IO.foreach(file) do |line|
        line.strip!
        if (line[0] != ?# and line[0] != ?=)
          i = line.index('=')
          if (i) 
            @properties[line[0..i - 1].strip] = line[i + 1..-1].strip
          else
            @properties[line] = '' if line.length > 0
          end
        end
      end
    end

    def to_s
      output = "File Name #{@file} \n"
      @properties.each {|key,value| output += " #{key}= #{value} \n" }
      output
    end

    def write_property (key,value)
      @properties[key] = value
      puts "Change the property '#{key}' with '#{value}'." if BUILD_DEBUG
    end

    def read_property(key)
      @properties[key]
    end

    def save(target_file=@file)
      file = File.new(target_file,"w+")
      @properties.sort.each {|key,value| file.puts "#{key}=#{value}\n" }
    end

  end

end

include BuildSupport

APACHE_MIRROR = find_apache_mirror

BUILD_URI = {
	:liferay => {
		:v5_1_0 => "http://downloads.sourceforge.net/sourceforge/lportal/liferay-portal-tomcat-5.5-5.1.0.zip",
		:v5_2_3 => "http://downloads.sourceforge.net/sourceforge/lportal/liferay-portal-tomcat-5.5-5.2.3.zip",
		:v5_2_5ee => "http://files.liferay.com/public/3mDkoitQ/ee/portal/5.2.5/liferay-portal-tomcat-5.5-5.2.5.zip",
		:v5_2_5_jbee => "http://files.liferay.com/public/3mDkoitQ/ee/portal/5.2.5/liferay-portal-jboss-tomcat-4.2-5.2.5.zip",
	},
	:tomcat => {
		:v5 => APACHE_MIRROR + "tomcat/tomcat-5/v5.5.27/bin/apache-tomcat-5.5.27.zip",
		:v6 => APACHE_MIRROR + "tomcat/tomcat-6/v6.0.18/bin/apache-tomcat-6.0.18.zip"
	},  
	:axis2 => APACHE_MIRROR + "ws/axis2/1_4_1/axis2-1.4.1-war.zip",
	:ode => {
    :v1_2_snapshot => "http://www.intalio.org/public/ode/apache-ode-1.2-SNAPSHOT-700632.zip",
    # :v1_3_snapshot => "http://www.intalio.org/public/ode/apache-ode-1.3-SNAPSHOT-745704.zip",
    :v1_3_snapshot => "http://www.intalio.org/public/ode/apache-ode-war-1.3.2-SNAPSHOT.zip",
    :v2_1_snapshot => "http://www.intalio.org/public/ode/apache-ode-war-2.1-SNAPSHOT-20090303-768496.zip"
	}, 
	:alfresco => {
	  :v2_1 => "http://downloads.sourceforge.net/sourceforge/alfresco/alfresco-community-war-2.1.0.zip",
    :v2_9 => "http://downloads.sourceforge.net/sourceforge/alfresco/alfresco-community-war-2.9.0B.zip",
    :v3_0 => "http://downloads.sourceforge.net/sourceforge/alfresco/alfresco-labs-war-3a.1032.zip"  
	},
	:jetty => {
	  :v7 => "http://dist.codehaus.org/jetty/jetty-7.0.0/jetty-hightide-7.0.0.v20091005.zip"
	}, 
	:opensso_agent => "http://download.java.net/general/opensso/nightly/latest/j2eeagents/tomcat_v6_agent_3.zip"
}