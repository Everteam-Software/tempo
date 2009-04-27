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

# Set the soap security header
token = TOKEN_SERVICE.authenticateUser(:user => ADMIN_USER, :password => ADMIN_PWD).token
TMS_CLIENT.headerhandler << Header.new("participantToken", token)

# getTaskInfo
## Returns a data object of type tTask
## Applies to both tasks and notifications.
puts "====== Calling operation: getTaskInfo ======"
taskInfo = TMS_CLIENT.getTaskInfo(:identifier => "d4d45313-4012-4f6e-aaa2-294a9f1c4388") #You should replace it with your own id
p taskInfo