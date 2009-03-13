require 'soap/wsdlDriver'

class SampleTMSClient 

  attr_reader :token, :tms_client
  attr_writer :fake_delete

  def initialize(host="localhost:8080", user="intalio\\admin",password="changeit", fake_delete=false)
    @host = host
    @token = authenticate(user,password)
    @fake_delete = fake_delete
    @tms_client = SOAP::WSDLDriverFactory.new("http://#{@host}/axis2/services/TaskManagementServices?wsdl").create_rpc_driver if not @tms_client
    puts @token
  end
  
  def get_available_tasks query="", task_type="PATask", first="-1", max="-1"
    tasks = @tms_client.getAvailableTasks(:participantToken => @token, :taskType => task_type, :subQuery => query, :first=>first, :max=>max).task
  end

  def get_tasks
    tasks = @tms_client.getTaskList(:participantToken => @token).task
  end

  def get_task id
    @tms_client.getTask(:participantToken => @token, :taskId => id)
  end  

  def get_attachments id
    @tms_client.getAttachments(:participantToken => @token, :taskId => id)
  end

  def remove_attachment task_id, attachment_url
    if @fake_delete then
      puts "Fake delete of:#{attachment_url} for task:#{task_id}"
    else
      @tms_client.removeAttachment(:participantToken => @token, :taskId => task_id, :attachmentUrl=>attachment_url)
    end
  end

  def get_pa_tasks
    tasks = @tms_client.getAvailableTasks(:participantToken => @token, :taskType => "PATask", :subQuery=>"").task
  end

  def delete tasks
    tasks.each do |task|
      puts "Deleting task #{task.taskId}"
      if @fakeDelete then
        puts "Fake delete of task:#{id}"
      else        
        @tms_client.delete(:participantToken => @token, :taskId=> task.taskId)
      end
    end
  end

  def delete_by_ids ids
    ids.each do |id|
      if @fake_delete then
        puts "Fake delete of task:#{id}"
      else       
        @tms_client.delete(:participantToken => @token, :taskId=> id)
      end
    end
  end

  def delete_all task_type="PATask", query=""
    puts "Deleting task matching query:#{query}"
    @tms_client.deleteAll(:participantToken =>@token, :taskType=> task_type, :fakeDelete=> @fake_delete, :subQuery=>query)
  end

  def delete_attachments task_id
    attachments = get_attachments(task_id)
    array = nil
    begin
       array = attachments.attachment
    rescue
      ## no attachments
      return
    end
    if array.instance_of? Array then
      array.each do |att|
        remove_attachment(task_id, att.payloadUrl)  
      end
    else
      remove_attachment(task_id, array.payloadUrl)
    end
  end

  def delete_task task_id
    delete_attachments task_id
    delete_by_ids [task_id]
  end

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
