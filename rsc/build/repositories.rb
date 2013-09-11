
repositories.remote = [ 
  "http://www.intalio.org/public/maven2", 
  "http://dist.codehaus.org/mule/dependencies/maven2/", 
  "http://repo1.maven.org/maven2",
  "http://scala-tools.org/repo-releases",
  "http://spring-json.sourceforge.net/repository/"
]

repositories.release_to[:username] ||= "ubuntu"
repositories.release_to[:password] ||= "ubuntu"
repositories.release_to[:url] ||= "sftp://www.intalio.org/var/www-org/public/maven2"
repositories.release_to[:permissions] ||= 0664
