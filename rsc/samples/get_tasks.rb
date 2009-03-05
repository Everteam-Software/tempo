#!/usr/bin/env ruby

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

  private
  def authenticate(user, password)
    tokenService = SOAP::WSDLDriverFactory.new("http://#{@host}/axis2/services/TokenService?wsdl").create_rpc_driver    
    @token = tokenService.authenticateUser(:user => user, :password => password).token
  end

end

def display_tasks tasks
  puts "\tFound #{tasks.size} tasks"
  if tasks.size > 0
    puts "\tHere is the list of tasks"  
    tasks.each do |task|
      puts "#{task.taskType}:\t#{task.taskId}\t#{task.description}"
    end
  end
end

# authenticate and get the tasks for some user
tms_client = SampleTMSClient.new
tasks = tms_client.get_tasks

# display task list
puts "\n\n\n\n"
puts "\tWe got the following authentication ticket: \n#{tms_client.token}"
display_tasks tasks

# display activity taskss
tasks = tms_client.get_pa_tasks
display_tasks tasks
tms_client.delete tasks