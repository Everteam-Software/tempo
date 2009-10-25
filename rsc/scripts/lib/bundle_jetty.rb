def configure_jetty
  FileUtils.cp "#{TEMPO_SVN}/rsc/jetty/axis2.xml", "#{@@server_folder}/contexts"
  FileUtils.cp "#{TEMPO_SVN}/rsc/jetty/ode.xml", "#{@@server_folder}/contexts"
  FileUtils.cp "#{TEMPO_SVN}/rsc/jetty/wds.xml", "#{@@server_folder}/contexts"
  
  FileUtils.cp "#{TEMPO_SVN}/rsc/jetty/axis_web.xml", "#{@@webapp_folder}/axis2/WEB-INF/web.xml"
  FileUtils.cp "#{TEMPO_SVN}/rsc/jetty/ode_web.xml", "#{@@webapp_folder}/ode/WEB-INF/web.xml"
  FileUtils.cp "#{TEMPO_SVN}/rsc/jetty/wds_web.xml", "#{@@webapp_folder}/wds/WEB-INF/web.xml"
  
  FileUtils.cp "#{TEMPO_SVN}/rsc/jetty/jetty-ssl.xml", "#{@@server_folder}/etc"
  FileUtils.cp "#{TEMPO_SVN}/rsc/jetty/jetty.sh", "#{@@server_folder}/bin"
  
  FileUtils.cp "#{TEMPO_SVN}/rsc/bundle-config/intalio-keystore.jks", "#{@@server_folder}/var/config"
end

def configure_ui_fw_jetty
  FileUtils.cp "#{TEMPO_SVN}/rsc/jetty/ui-fw_web.xml", "#{@@webapp_folder}/ui-fw/WEB-INF/web.xml"
  locate_and_copy(CAS_CLIENT, @@lib_folder)
end