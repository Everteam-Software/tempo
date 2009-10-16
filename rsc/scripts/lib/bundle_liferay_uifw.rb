def copy_liferay_config_file
  deploy_desc_name = "web.xml"
  if BUILD_CONFIG[:liferay][:v].to_s.index("ee")
    deploy_desc_name = "web-ee.xml"
  end
  if BUILD_CONFIG[:liferay][:server_folder].index("jboss")
    FileUtils.cp "#{TEMPO_SVN}/rsc/LDAP/portal-ext.properties", "#{@@server_folder}/server/default/deploy/ROOT.war/WEB-INF/classes"
    FileUtils.cp "#{TEMPO_SVN}/rsc/liferay/server-liferay-jboss-tomcat.xml", "#{@@server_folder}/server/default/deploy/jboss-web.deployer/server.xml"
    FileUtils.mv "#{@@server_folder}/server/default/deploy/ROOT.war/WEB-INF/lib/casclient.jar", "#{@@server_folder}/server/default/lib", :force => true
    Dir.glob("#{TEMPO_SVN}/liferay-ticket-filter/target/*.jar") {|x| File.copy x, "#{@@server_folder}/server/default/deploy/ROOT.war/WEB-INF/lib"}
    FileUtils.cp "#{TEMPO_SVN}/rsc/liferay/#{deploy_desc_name}", "#{@@server_folder}/server/default/deploy/ROOT.war/WEB-INF/web.xml"
    FileUtils.cp "#{TEMPO_SVN}/rsc/liferay/run_jboss.sh", "#{@@server_folder}/bin/run.sh"
    
  else 
    FileUtils.cp "#{TEMPO_SVN}/rsc/LDAP/portal-ext.properties", "#{@@server_folder}/webapps/ROOT/WEB-INF/classes"
    FileUtils.cp "#{TEMPO_SVN}/rsc/liferay/server-liferay-standalone.xml", "#{@@server_folder}/conf/server.xml"
    FileUtils.cp "#{TEMPO_SVN}/rsc/liferay/ROOT-liferay-standalone.xml", "#{@@server_folder}/conf/Catalina/localhost/ROOT.xml"
    FileUtils.mv "#{@@server_folder}/webapps/ROOT/WEB-INF/lib/casclient.jar", "#{@@server_folder}/common/lib", :force => true
    Dir.glob("#{TEMPO_SVN}/liferay-ticket-filter/target/*.jar") {|x| File.copy x, "#{@@server_folder}/webapps/ROOT/WEB-INF/lib"}
    FileUtils.cp "#{TEMPO_SVN}/rsc/liferay/#{deploy_desc_name}", "#{@@server_folder}/webapps/ROOT/WEB-INF/web.xml"
  end
  FileUtils.cp "#{TEMPO_SVN}/rsc/bundle-config/intalio-keystore.jks", "#{@@server_folder}/var/config"
  FileUtils.cp "#{TEMPO_SVN}/rsc/liferay/tempo-formmanager.xml", "#{@@server_folder}/var/config"
end