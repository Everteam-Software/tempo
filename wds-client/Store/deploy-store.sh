#!/bin/sh
java -cp ../target/intalio-bpms-workflow-wds-cli-*.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient -d store-pipa-deploy.xml store-pipa ChainedExecution/SelectItem.xform SelectItem.xform --force
java -cp ../target/intalio-bpms-workflow-wds-cli-*.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient store-activity ChainedExecution/Address.xform Address.xform --force
java -cp ../target/intalio-bpms-workflow-wds-cli-*.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient store-activity ChainedExecution/Payment.xform Payment.xform --force
