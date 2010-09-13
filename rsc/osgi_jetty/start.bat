@echo Starting equinox in a separate window
@echo on
set OSGI_HOME=%cd%
Set JETTY_HOME=%OSGI_HOME%/jettyhome
set JAVA_OPTS=-XX:MaxPermSize=256m -Djetty.etc.files=etc/jetty.xml;etc/jetty-ssl.xml -Djavax.net.ssl.trustStore=%JETTY_HOME%/var/config/intalio-keystore.jks -Dfile.encoding=UTF-8 -Xms128m -Xmx1024m -Dorg.intalio.tempo.configDirectory=%JETTY_HOME%/var/config -Dorg.apache.ode.configDir=%JETTY_HOME%/var/config -Dorg.intalio.deploy.configDirectory=%JETTY_HOME%/var/config -Dlogback.configurationFile=%JETTY_HOME%/resources/log4j.xml  -Djava.naming.factory.url.pkgs=org.eclipse.jetty.jndi -Djava.naming.factory.initial=org.eclipse.jetty.jndi.InitialContextFactory
java %JAVA_OPTS% -jar plugins/org.eclipse.equinox.launcher_1.0.201.R35x_v20090715.jar -console