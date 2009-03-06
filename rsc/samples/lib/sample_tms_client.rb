require 'soap/wsdlDriver'

class SampleTMSClient 

  attr_reader :token

  def initialize(host="localhost:8080", user="admin",password="changeit")
    @host = host
    @token = authenticate(user,password)
    @tms_client = SOAP::WSDLDriverFactory.new("http://#{@host}/axis2/services/TaskManagementServices?wsdl").create_rpc_driver if not @tms_client
  end

  def get_tasks
    tasks = @tms_client.getTaskList(:participantToken => @token).task
  end

  def get_pa_tasks
    tasks = @tms_client.getAvailableTasks(:participantToken => @token, :taskType => "PATask", :subQuery=>"").task
  end

  def delete tasks
    tasks.each do |task|
      puts "Deleting task #{task.taskId}"
      @tms_client.delete(:participantToken => @token, :taskId=> task.taskId)
    end
  end

  def delete_by_ids ids
    ids.each do |id|
      @tms_client.delete(:participantToken => @token, :taskId=> id)
    end
  end

  def delete_all task_type="PATask", query="", fake_delete=false
    puts "Deleting task matching query:#{query}"
    @tms_client.deleteAll(:participantToken =>@token, :taskType=> task_type, :fakeDelete=> fake_delete, :subQuery=>query)
  end
  # 
  #   <xsd:element name="taskType" type="xsd:string" maxOccurs="1" minOccurs="0"/>
  # <xsd:element name="subQuery" type="xsd:string" maxOccurs="1" minOccurs="0"/>
  # <xsd:element name="fakeDelete" type="xsd:boolean" maxOccurs="1" minOccurs="1"/>
  # <xsd:element name="participantToken" type="xsd:string" maxOccurs="1" minOccurs="1"/>

  private
  def authenticate(user, password)
    tokenService = SOAP::WSDLDriverFactory.new("http://#{@host}/axis2/services/TokenService?wsdl").create_rpc_driver    
    @token = tokenService.authenticateUser(:user => user, :password => password).token
  end

end

# short hand method to display a list of tasks
def display_tasks tasks
  puts "\tFound #{tasks.size} tasks"
  if tasks.size > 0
    puts "\tHere is the list of tasks"  
    tasks.each do |task|
      puts "#{task.taskType}:\t#{task.taskId}\t#{task.description}"
    end
  end
end
