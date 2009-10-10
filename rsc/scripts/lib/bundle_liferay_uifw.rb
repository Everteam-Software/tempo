def copy_liferay_config_file
  if BUILD_CONFIG[:liferay][:server_folder].index("jboss")
  else
    FileUtils.cp "#{TEMPO_SVN}/rsc/LDAP/portal-ext.properties", "#{@@server_folder}/webapps/ROOT/WEB-INF/classes"
    FileUtils.cp "#{TEMPO_SVN}/rsc/bundle-config/intalio-keystore.jks", "#{@@server_folder}/var/config"
    FileUtils.cp "#{TEMPO_SVN}/rsc/liferay/tempo-formmanager.xml", "#{@@server_folder}/var/config"
    FileUtils.cp "#{TEMPO_SVN}/rsc/liferay/server-liferay-standalone.xml", "#{@@server_folder}/conf/server.xml"
    FileUtils.cp "#{TEMPO_SVN}/rsc/liferay/ROOT-liferay-standalone.xml", "#{@@server_folder}/conf/Catalina/localhost/ROOT.xml"
    File.mv "#{@@server_folder}/webapps/ROOT/WEB-INF/lib/casclient.jar", "#{@@server_folder}/common/lib"
    Dir.glob("#{TEMPO_SVN}/liferay-ticket-filter/target/*.jar") {|x| File.copy x, "#{@@server_folder}/webapps/ROOT/WEB-INF/lib"}
    FileUtils.cp "#{TEMPO_SVN}/rsc/liferay/web.xml", "#{@@server_folder}/webapps/ROOT/WEB-INF"
  end
end