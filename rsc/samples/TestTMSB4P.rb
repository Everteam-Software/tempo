require 'soap/header/simplehandler'
require 'soap/wsdlDriver'

HOST = "localhost:8080"
ADMIN_USER = "intalio\\admin"
ADMIN_PWD = "changeit"

TMS_CLIENT = SOAP::WSDLDriverFactory.new("http://#{HOST}/axis2/services/HumanTaskOperationServices?wsdl").create_rpc_driver
TOKEN_SERVICE = SOAP::WSDLDriverFactory.new("http://#{HOST}/axis2/services/TokenService?wsdl").create_rpc_driver

# Helper class for SOAP headers.
class Header < SOAP::Header::SimpleHandler
  def initialize(tag, value)
    super(XSD::QName.new("http://www.intalio.org/WS-HT/", tag))
    @tag = tag
    @value = value
  end
  
  def on_simple_outbound
    @value
  end
end

def disp_task task
  puts "Task ID:" + task.id.to_s
  puts "Task Name:" + task.name.to_s
  puts "createdOn" + task.createdOn.to_s
  #TODO add more properties
end

# Set the soap security header
token = TOKEN_SERVICE.authenticateUser(:user => ADMIN_USER, :password => ADMIN_PWD).token
TMS_CLIENT.headerhandler << Header.new("participantToken", token)

#TMS_CLIENT.wiredump_file_base = "soap-log.txt"

################### Participant Operation
puts "====== Calling operation: create ======"
begin
ret = TMS_CLIENT.create(:in=>"test",
    "humanTaskContext" => #"<htpt:potentialOwners><htd:organizationalEntity><htd:users><htd:user>intalio\manager</htd:user></htd:users></htd:organizationalEntity></htpt:potentialOwners>"
                 {
             "priority" => 1, 
             "peopleAssignments" => {}             
             }
              ) 
rescue
end

puts "return:"
puts "Task ID:" + ret.inspect
puts  "====== End of operation: create ======"
################### Admin Operation

################### Query

################### Task Manapulation
# getTaskInfo
## Returns a data object of type tTask
## Applies to both tasks and notifications.
puts "====== Calling operation: getTaskInfo ======"
taskInfo = TMS_CLIENT.getTaskInfo(:identifier => "066475b8-6374-4198-8d55-bfe7f516146b") #You should replace it with your own id
disp_task taskInfo.task




# blabla