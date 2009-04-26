#/bin/sh
rm -f $BPMS_HOME/repository/com/intalio/bpms/axis2-services/1.1.1/axis2-services-1.1.1.war/WEB-INF/services/tempo-tms-service*
cp -fr target/tempo-tms-service-6.0.0.23-SNAPSHOT.aar  $BPMS_HOME/repository/com/intalio/bpms/axis2-services/1.1.1/axis2-services-1.1.1.war/WEB-INF/services
cp -fr target/tempo-tms-service-6.0.0.23-SNAPSHOT.jar  $BPMS_HOME/repository/com/intalio/bpms/axis2-services/1.1.1/axis2-services-1.1.1.war/WEB-INF/lib/
