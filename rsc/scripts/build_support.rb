require 'net/http'
require 'open-uri' 
require "zip/zip"
require 'yaml'
require 'fileutils'
require 'open-uri'
require "buildr"
require "hpricot"

def find_apache_mirror
  begin
    doc = Hpricot(open("http://www.apache.org/dyn/closer.cgi"))
    doc.search("//div[@='section-content']//strong")[0].inner_html
  rescue SocketError
    puts "WARNING: Cannot find a valid apache mirror" 
    return ""
  end
end


# Unzip a file
def unzip2(x, basefolder = ".", forceextract = false)
  puts "Unzipping file:#{x}" if DEBUG
  return if not basefolder == "." and File.exist? basefolder and !forceextract
  outdir = basefolder
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

# Download and unzip file
def download_to(filename, url, unzip=true, message="Downloading #{url}") 
  puts message if DEBUG
  spec = "org.intalio.tempo.build:#{filename}:zip:1.0"
  ar = Buildr::artifact(spec)
  download(ar=>url)
  ar.invoke
  File.cp repositories.locate(spec), filename
  unzip2(filename) if unzip
end

def download_and_copy(url, folder)
  puts "Copying #{url} to #{folder}" if DEBUG
  download_unzip( url,false )
  File.copy( filename_from_url(url), folder, DEBUG )
end

def locate_and_copy(lib, folder)
  puts "locating #{lib}" if DEBUG
  if(lib.kind_of? Array) then 
    lib.each {|l| locate_and_copy l,folder}
  else
    artifact(lib).invoke
    File.cp repositories.locate(lib), folder
  end
end

def filename_from_url (url)
  url.slice(url.rindex("/")+1, url.length)
end

def download_unzip(url, unzip=true)
  filename = filename_from_url url
  download_to(filename,url,unzip)
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
    File.copy(File.expand_path(war_file), File.expand_path("#{@webapp_folder}/#{war_name}"), DEBUG)
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
    war_dir = File.expand_path("#{to_dir}/#{jar_folder}")
    return war_dir if File.exist? war_dir
    Dir.mkdir war_dir
    chd_and_execute(war_dir) {
      local_jar_file = File.expand_path("../#{jar_file}")
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
     @process_folder = File.expand_path("#{axis_folder}/WEB-INF/services")
     FileUtils.mkdir_p @process_folder
     @finder = Finder.new
  end
  
  def install aar
    File.copy( aar, @process_folder, DEBUG )
  end
  
  def install_tempo_aar service_name
    puts "Installing service: #{service_name}" if DEBUG
    service = @finder.find_tempo_component( service_name, "aar" )
    if ( service != nil)
      install service
    else
      raise "Could not find service to install. Make sure you compile tempo code first, or force rebuild"
    end
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