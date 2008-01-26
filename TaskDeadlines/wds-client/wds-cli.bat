@echo off
set WDS_HOME=%~dp0
java -cp %WDS_HOME%\target\intalio-bpms-workflow-wds-cli-4.0-SNAPSHOT.jar org.intalio.tempo.workflow.wds.cli.WDSCommandLineClient %1 %2 %3 %4 %5 %6 %7 %8 %9