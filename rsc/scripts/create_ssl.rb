#!/usr/bin/env ruby 

JAVA_HOME="/System/Library/Frameworks/JavaVM.framework/Home/"
LOCAL_KEYSTORE="#{JAVA_HOME}/lib/security/cacerts" 
#LOCAL_KEYSTORE="#{JAVA_HOME}/jre/lib/security/cacerts"
CERTIFICATE_FILENAME="tempo.cert"

# THIS IS NOT NEEDED ANYMORE, THE CERTIFICATE IS INCLUDED IN THE TOMCAT STARTUP SCRIPT

# For note, keypass should be 'changeit', to comply with the configuration in tomcat, for each of the following actions.

# If you need to regenerate a new certificate. Use localhost whenever you can. 
# Added: You need to have at least first name last name identical to the hostname, otherwise the certificate might not 
# be valid. 
#
# system "keytool -genkey -alias tomcat -keyalg RSA"

# If you need to export the certificate.
#
# system "keytool -export -alias tomcat -file #{CERTIFICATE_FILENAME}"
 
# Import the generated certificate into the local keystore. You need to have sufficient rights for this.
#
# system "keytool -import -alias tomcat -file #{CERTIFICATE_FILENAME} -keypass changeit -keystore #{LOCAL_KEYSTORE}"

# If you need to delete the tomcat alias. You need to have sufficient rights for this.
#
# system "keytool -delete -alias tomcat -keystore #{LOCAL_KEYSTORE}"