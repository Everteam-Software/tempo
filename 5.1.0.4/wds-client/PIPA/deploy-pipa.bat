@ECHO OFF

java -cp ../target/intalio-bpms-workflow-wds-cli-*.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient -d pipa-deploy.xml store-pipa PIPA/PIPA.xform startform.xform --force