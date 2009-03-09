def install_opensso
  setenv("#{@@server_folder}/bin","-XX:MaxPermSize=256m -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m")
  @@wi = WarInstaller.new(@@server_folder, true, true)
  @@wi.install_war_artifact(OPENSSO_SERVER,"opensso")
  sh_files @@server_folder
end