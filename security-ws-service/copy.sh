#/bin/sh
rm -f $BPMS_HOME/repository/com/intalio/bpms/axis2-services/1.1.1/axis2-services-1.1.1.war/WEB-INF/services/tempo-security-ws-service*.aar
cp -fr target/*.aar $BPMS_HOME/repository/com/intalio/bpms/axis2-services/1.1.1/axis2-services-1.1.1.war/WEB-INF/services/

