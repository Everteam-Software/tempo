#!/bin/sh
CLASSPATH=../../target/classes:.
CLASSPATH=$CLASSPATH:$M2_REPO/log4j/log4j/1.2.9/log4j-1.2.9.jar
CLASSPATH=$CLASSPATH:$M2_REPO/mx4j/mx4j-jmx/2.1.1/mx4j-jmx-2.1.1.jar
CLASSPATH=$CLASSPATH:$M2_REPO/commons-logging/commons-logging/1.0.4/commons-logging-1.0.4.jar
CLASSPATH=$CLASSPATH:$M2_REPO/junit/junit/3.8.1/junit-3.8.1.jar
CLASSPATH=$CLASSPATH:$M2_REPO/xalan/xalan/2.7.0/xalan-2.7.0.jar
CLASSPATH=$CLASSPATH:$M2_REPO/org/springframework/spring/1.2.6/spring-1.2.6.jar
CLASSPATH=$CLASSPATH:$M2_REPO/xfire/xfire-jsr181-api/1.0-M1/xfire-jsr181-api-1.0-M1.jar
CLASSPATH=$CLASSPATH:$M2_REPO/xerces/xercesImpl/2.4.0/xercesImpl-2.4.0.jar
CLASSPATH=$CLASSPATH:$M2_REPO/castor/castor/1.0/castor-1.0.jar
CLASSPATH=$CLASSPATH:$M2_REPO/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar
java -cp $CLASSPATH org.intalio.tempo.security.util.Browser "$@"