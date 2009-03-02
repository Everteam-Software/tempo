#!/usr/bin/env ruby

require 'soap/wsdlDriver'

class SampleTMSClient 

  def initialize(host="localhost:8080", user="admin",password="changeit")
    @host = host
    authenticate(user,password)
  end

  def get_tasks
    taskService = SOAP::WSDLDriverFactory.new("http://#{@host}/axis2/services/TaskManagementServices?wsdl").create_rpc_driver
    tasks = taskService.getTaskList(:participantToken => @token).task
  end

  private
  def authenticate(user, password)
    tokenService = SOAP::WSDLDriverFactory.new("http://#{@host}/axis2/services/TokenService?wsdl").create_rpc_driver    
    @token = tokenService.authenticateUser(:user => user, :password => password).token
  end

end

tms = SampleTMSClient.new
tasks = tms.get_tasks
puts "Found #{tasks.size} tasks"
puts "One of the tasks has id:#{tasks[0].taskId}" if tasks.size > 0