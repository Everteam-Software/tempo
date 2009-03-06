#!/usr/bin/env ruby
load "sample_tms_client"

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