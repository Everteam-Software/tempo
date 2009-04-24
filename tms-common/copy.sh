#/bin/sh
rm -fr $BPMS_HOME/repository/com/intalio/bpms/axis2-sevices/1.1.1/axis2-services-1.1.1.war/WEB-INF/lib/tempo-tms-common-*.jar

cp -fr target/tempo-tms-common-6.0.0.23-SNAPSHOT.jar  $BPMS_HOME/repository/com/intalio/bpms/axis2-services/1.1.1/axis2-services-1.1.1.war/WEB-INF/lib/
