#!/bin/sh
BASE=http://localhost:8080/wds/
CP=../target/intalio-bpms-workflow-wds-cli-*.jar
WDSCC=org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient

java -cp $CP $WDSCC -w $BASE store-activity AbsenceRequest/AbsenceApproval.xform AbsenceApproval.xform --force
java -cp $CP $WDSCC -d ar-deploy.xml -w $BASE store-pipa AbsenceRequest/AbsenceRequest.xform AbsenceRequest.xform --force
java -cp $CP $WDSCC -w $BASE store-activity AbsenceRequest/Notification.xform Notification.xform --force