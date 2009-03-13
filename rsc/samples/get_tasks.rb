#!/usr/bin/env ruby
load File.dirname(File.expand_path("#{$0}"))+"/lib/sample_tms_client.rb"

# authenticate 
tms_client = SampleTMSClient.new

# get the tasks for the authenticated user
tasks = tms_client.get_tasks

# display task list
puts "\n\n\n\n"
puts "\tWe got the following authentication ticket: \n#{tms_client.token}"
display_tasks tasks

# display onlyactivity taskss
tasks = tms_client.get_pa_tasks
display_tasks tasks