#!/bin/sh
#m2_repo=/Users/juweihua/.m2/repository
if [$m2_repo == '']; then
	echo 'Please set $m2_repo to your local maven2 repo first'
	exit 
fi
target=com.intalio.www.ws_ht.api.wsdl.TMSServiceTest
param=
cp="target/test/classes:$m2_repo/org/apache/axis2/axis2-adb/1.4.1/axis2-adb-1.4.1.jar:$m2_repo/org/apache/axis2/axis2-kernel/1.4.1/axis2-kernel-1.4.1.jar:$m2_repo/stax/stax-api/1.0.1/stax-api-1.0.1.jar:$m2_repo/org/apache/ws/commons/axiom/axiom-api/1.2.7/axiom-api-1.2.7.jar:$m2_repo/wsdl4j/wsdl4j/1.6.1/wsdl4j-1.6.1.jar:$m2_repo/org/apache/ws/commons/schema/XmlSchema/1.3.1/XmlSchema-1.3.1.jar:$m2_repo/org/slf4j/jcl104-over-slf4j/1.4.3/jcl104-over-slf4j-1.4.3.jar:$m2_repo/org/slf4j/slf4j-api/1.4.3/slf4j-api-1.4.3.jar:$m2_repo/org/slf4j/slf4j-log4j12/1.4.3/slf4j-log4j12-1.4.3.jar:$m2_repo/log4j/log4j/1.2.15/log4j-1.2.15.jar:$m2_repo/org/apache/ws/commons/axiom/axiom-impl/1.2.7/axiom-impl-1.2.7.jar:$m2_repo/backport-util-concurrent/backport-util-concurrent/3.1/backport-util-concurrent-3.1.jar:$m2_repo/org/apache/neethi/neethi/2.0.4/neethi-2.0.4.jar:stax-1.2.0.jar:$m2_repo/commons-httpclient/commons-httpclient/3.1/commons-httpclient-3.1.jar:$m2_repo/javax/activation/activation/1.1.1/activation-1.1.1.jar:$m2_repo/commons-codec/commons-codec/1.3/commons-codec-1.3.jar"
echo $cp
java -classpath  $cp $target $param
