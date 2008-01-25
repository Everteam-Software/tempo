@ECHO OFF

java -cp ../target/intalio-bpms-workflow-wds-cli-*.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient -d ar-deploy.xml store-pipa AbsenceRequest/AbsenceRequest.xform AbsenceRequest.xform --force

java -cp ../target/intalio-bpms-workflow-wds-cli-*.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient store-activity AbsenceRequest/AbsenceApproval.xform AbsenceApproval.xform --force

java -cp ../target/intalio-bpms-workflow-wds-cli-*.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient store-activity AbsenceRequest/Notification.xform Notification.xform --force
