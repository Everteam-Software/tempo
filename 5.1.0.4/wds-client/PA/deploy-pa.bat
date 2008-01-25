@ECHO OFF

java -cp ../target/intalio-bpms-workflow-wds-cli-*.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient store-activity PeopleActivity/PA.xform PA.xform --force