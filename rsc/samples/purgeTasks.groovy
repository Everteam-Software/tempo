/************************************************************************

  This script purges workflow tasks (people activity) that are no longer
  associated with any process instance.

  To run this script,

  On Windows:
      C:\...> groovy.bat -cp path\to\jdbc-driver.jar;commons-httpclient-3.1.jar;commons-codec-1.3.jar purgeTasks.groovy

  On Linux, Solaris and other Unix-like:
      % ./groovy.sh -cp path/to-jdbc/driver.jar:commons-httpclient-3.1.jar:commons-codec-1.3.jar purgeTasks.groovy

************************************************************************/ 

//
// NOTE:  BACKUP YOUR DATABASE FIRST!
//

import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import java.text.DateFormat
import java.text.SimpleDateFormat
import org.apache.commons.httpclient.*
import org.apache.commons.httpclient.methods.*

// Change these settings according to your database configuration

def db = Sql.newInstance("jdbc:oracle:thin:@localhost:1521:XE", "user", "password", 
                         "oracle.jdbc.driver.OracleDriver")
                       

// Change this URL to correspond to your Intalio Server Task Management Service 
// URL, if necessary
PostMethod post = new PostMethod("http://localhost:8080/axis2/services/TaskManagementServices");
post.setRequestHeader("Content-Type", "text/xml; charset=utf-8");

// Change the security token to match your administrator token
//
// (You can get security token by logging into http://localhost:8080/ui-fw and
//  using "View Source" option in your browser and searching for 'token' in the
//  page source)
token = 'VE9LRU4mJnVzZXI9PWludGFsaW9cYWRtaW4mJmlzc3VlZD09MTIzOTA3MTQ2MDMzNSYmcm9sZXM9PWV4YW1wbGVzXG1hbmFnZXIsZXhhbXBsZXNcZW1wbG95ZWUsaW50YWxpb1xwcm9jZXNzYWRtaW5pc3RyYXRvcixpbnRhbGlvXHByb2Nlc3NtYW5hZ2VyJiZmdWxsTmFtZT09QWRtaW5pbmlzdHJhdG9yJiZlbWFpbD09YWRtaW5AZXhhbXBsZS5jb20mJm5vbmNlPT02MTA2Mzg0MDcyMjc1NzQxNTc2JiZ0aW1lc3RhbXA9PTEyMzkwNzE0NjAzMzUmJmRpZ2VzdD09QjlNMVVYTC9pRk96blNQS1gvcGhjS24vdlBRPSYmJiZUT0tFTg=='

// Collect all tasks no longer associated with process instances 
task_ids = []
db.eachRow("SELECT * FROM TEMPO_PA pa INNER JOIN TEMPO_TASK task ON (pa.id = task.id) "+
           "WHERE task.tid NOT IN (SELECT value FROM BPEL_CORRELATION_PROP)") { row ->  
  task_ids << row.tid
}

println "task ids: ${task_ids}"

def writer = new StringWriter()
def xml = new MarkupBuilder(writer)
xml.'deleteRequest'('xmlns': 'http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/') {
  task_ids.each {
    taskId(it)
  }
  participantToken(token)
}

// DEBUG
// println "request:\n${writer}"
// return

post.setRequestBody(writer.toString());
int code = (new HttpClient()).executeMethod(post);
if (code == 200) {
  System.out.println(post.getResponseBodyAsString())
} else {
  System.err.println("Bad response code: "+code);
  System.out.println(post.getResponseBodyAsString())
}

