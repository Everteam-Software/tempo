#!/bin/sh
java -cp ../target/intalio-bpms-workflow-wds-cli-*.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient delete-activity AbsenceRequest/AbsenceApproval.xform
java -cp ../target/intalio-bpms-workflow-wds-cli-*.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient -d ar-deploy.xml delete-pipa AbsenceRequest/AbsenceRequest.xform
java -cp ../target/intalio-bpms-workflow-wds-cli-*.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient delete-activity AbsenceRequest/Notification.xform