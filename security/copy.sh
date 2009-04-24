#/bin/sh
rm -f $BPMS_HOME/repository/com/intalio/bpms/axis2-services/1.1.1/axis2-services-1.1.1.war/WEB-INF/lib/tempo-security-6*j.ar
rm -f $BPMS_HOME/repository/com/intalio/bpms/axis2-services/1.1.1/axis2-services-1.1.1.war/WEB-INF/lib/tempo-security-5*j.ar

cp -fr target/*.jar $BPMS_HOME/repository/com/intalio/bpms/axis2-services/1.1.1/axis2-services-1.1.1.war/WEB-INF/lib/

