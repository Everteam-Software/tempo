
repositories.remote = [ "http://www.intalio.org/public/maven2", "http://dist.codehaus.org/mule/dependencies/maven2/", "http://repo1.maven.org/maven2" , "http://developer.ja-sig.org/maven2"]

repositories.release_to[:username] ||= "release"
repositories.release_to[:url] ||= "sftp://www.intalio.org/var/www/public/maven2"
repositories.release_to[:permissions] ||= 0664