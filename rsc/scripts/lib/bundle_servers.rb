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
  @@webapp_folder = "#{@@server_folder}/webapps"
  @@wi = WarInstaller.new @@webapp_folder, true, true
  @@lib_folder = "#{@@server_folder}/common/lib" # tomcat5
  @@log_folder = check_folder("#{@@server_folder}/var/logs") # tomcat5
end

def time_now
  Time.now.strftime('%Y.%m.%d')  
end
