#/bin/sh
rm -f $BPMS_HOME/repository/com/intalio/bpms/axis2-services/1.1.1/axis2-services-1.1.1.war/WEB-INF/lib/tempo-security-ws-client*.jar
cp -fr target/*.jar $BPMS_HOME/repository/com/intalio/bpms/axis2-services/1.1.1/axis2-services-1.1.1.war/WEB-INF/lib/

