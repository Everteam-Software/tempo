require 'soap/wsdlDriver'

HOST = "localhost:8080"
ADMIN_USER = "intalio\\admin"
ADMIN_PWD = "changeit"

TMS_CLIENT = SOAP::WSDLDriverFactory.new("http://#{HOST}/axis2/services/HumanTaskOperationServices?wsdl").create_rpc_driver
TOKEN_SERVICE = SOAP::WSDLDriverFactory.new("http://#{HOST}/axis2/services/TokenService?wsdl").create_rpc_driver

token = TOKEN_SERVICE.authenticateUser(:user => ADMIN_USER, :password => ADMIN_PWD).token
#TMS_CLIENT.getTaskInfo(:identifier => "d4d45313-4012-4f6e-aaa2-294a9f1c4388")
TMS_CLIENT.create(:in => "test", :humanTaskContext => {
  :priority => 1, :peopleAssignments => {
     :potentialOwners => {
       :organizationalEntity => {
         :users => {
           :user => "intalio\\manager"
           }
         }
       }
     }, :isSkipable => false, :expirationTime => "", :attachments => ""})
 
