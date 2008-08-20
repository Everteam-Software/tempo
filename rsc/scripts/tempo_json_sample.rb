#!/usr/bin/env ruby
require 'net/http'
require 'cobravsmongoose'
require "pp"

# Useful reference sites: 
# http://cobravsmongoose.rubyforge.org/
# http://badgerfish.ning.com/
# http://wso2.org/library/768

#
# Make a post to a tempo service, with the given json formatted data
#
def call_tempo service, data
  res = Net::HTTP.new('localhost', 8080)
  # res.set_debug_output $stderr
  res.start do |http|
    headers = {"Content-Type" => 'text/javascript'} # <-- tell axis we want some json back. text/javascript allows usage of namespaces in axis.
    # puts data
    post = http.post(service, data, headers)
    call = post.body
    return JSON.parse(call)
  end
end

# Make a call to the tempo security service, to authenticate and retrieve a token
auth_xml = "<authenticateUser><user xmlns=\"http://tempo.intalio.org/security/tokenService/\">admin</user><password xmlns=\"http://tempo.intalio.org/security/tokenService/\">changeit</password></authenticateUser>"
auth_json = CobraVsMongoose.xml_to_json(auth_xml)
call1 = call_tempo "/axis2/services/TokenService", auth_json
token = call1["tokenws:authenticateUserResponse"]["tokenws:token"]["$"]

# Get the task list from the task management service, using the token retrieved before.
task_xml = "<getTaskList><participantToken xmlns=\"http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/\">#{token}</participantToken></getTaskList>"
task_json = CobraVsMongoose.xml_to_json(task_xml)
call2 = call_tempo '/axis2/services/TaskManagementServices/getTaskList', task_json
pp call2["tms:getTaskListResponse"]["tms:task"][0]["tms:taskId"]["$"]