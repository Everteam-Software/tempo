def install_server server_zip_url, build_folder="tempo-build-#{time_now}"
  @@server_folder = rename_folder(download_unzip(server_zip_url, true), build_folder)
  @@webapp_folder = "#{@@server_folder}/webapps"
  @@wi = WarInstaller.new @@webapp_folder, true, true
  @@lib_folder = "#{@@server_folder}/common/lib" # tomcat5
  @@log_folder = check_folder("#{@@server_folder}/var/logs") # tomcat5
end

def install_tomcat6 build_folder="tempo-tomcat6-#{time_now}"
  install_server BUILD_URI[:tomcat][:v6], build_folder
  @@lib_folder = "#{@@server_folder}/lib"
end

def install_tomcat5 build_folder="tempo-tomcat5-#{time_now}"
  install_server BUILD_URI[:tomcat][:v5], build_folder
end

def install_tomcat build_folder="tempo-tomcat-#{time_now}"
  if BUILD_CONFIG[:tomcat] == :v5 then
    install_tomcat5 build_folder
  else
    install_tomcat6 build_folder
  end
end

def install_liferay build_folder="tempo-liferay-#{time_now}"
  download_unzip(BUILD_URI[:liferay][BUILD_CONFIG[:liferay][:v]], true)
  @@server_folder = rename_folder(BUILD_CONFIG[:liferay][:base_folder], build_folder)
  @@server_folder = "#{@@server_folder}/#{BUILD_CONFIG[:liferay][:server_folder]}"
  if BUILD_CONFIG[:liferay][:v] == :v5_2_5_jb
    @@webapp_folder = "#{@@server_folder}/server/default/deploy/"
    @@wi = WarInstaller.new @@webapp_folder, true, true
    @@wi.webapp_folder = "#{@@server_folder}/server/default/deploy/"
    @@lib_folder = "#{@@server_folder}/lib"
  else
    @@webapp_folder = "#{@@server_folder}/webapps"
    @@wi = WarInstaller.new @@webapp_folder, true, true
    @@lib_folder = "#{@@server_folder}/common/lib" # tomcat5
  end
  
  @@deploy_folder = "#{@@server_folder}/server/default/deploy/" # TODO: Please check this. 
  
  @@log_folder = check_folder("#{@@server_folder}/var/logs") # tomcat5
  @@config_folder = check_folder("#{@@server_folder}/var/config")
end

def install_jetty build_folder="tempo-jetty-#{time_now}"
  @@server_folder = rename_folder(download_unzip(BUILD_URI[:jetty][:v7], true), build_folder)
  @@webapp_folder = "#{@@server_folder}/webapps"
  @@wi = WarInstaller.new @@webapp_folder, true, true
  @@lib_folder = "#{@@server_folder}/lib/ext"
  @@log_folder = check_folder("#{@@server_folder}/var/logs")
  @@config_folder = check_folder("#{@@server_folder}/var/config")
end

def install_osgi_jetty build_folder="tempo-osgi-jetty-#{time_now}"
  osgi_jetty_file = filename_from_url BUILD_URI[:osgi_jetty][:v7]
  download_to(osgi_jetty_file, BUILD_URI[:osgi_jetty][:v7], false)
  unpack_file(osgi_jetty_file, build_folder)
  @@server_folder = build_folder

  @@webapp_folder = "#{@@server_folder}/eclipse/plugins"
  @@wi = WarInstaller.new @@webapp_folder, true, true
  @@lib_folder = "#{@@server_folder}/eclipse/jettyhome/lib/ext"
  @@log_folder = check_folder("#{@@server_folder}/eclipse/jettyhome/var/logs")
  @@config_folder = check_folder("#{@@server_folder}/eclipse/jettyhome/var/config")
end

def time_now
  Time.now.strftime('%Y.%m.%d')  
end
