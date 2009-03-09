#!/usr/bin/env ruby
load File.dirname(File.expand_path("#{$0}"))+"/lib/sample_tms_client.rb"

# create a new tms client
tms_client = SampleTMSClient.new
tms_client.fake_delete = true

# sample to delete task by query
# those task have to be accessible by the authenticated user, since the query use the getAvailableTask method on the server
# tms_client.delete_all "PATask", "", false


# ids = ["9edef9aa63f0e40d:51bb29f8:11fe8f338b8:-7fbf114.48.183.2115602"]
# delete by ids
# task don't have to be accessible by the user, but the user needs to have the rights to delete tasks
# tms_client.delete_by_ids ids

# delete all the tasks and their attachments, using ids from the array below
ids = ["9edef9aa63f0e40d:51bb29f8:11fe8f338b8:-7fbf114.48.183.2115602"]
ids.each do |id|
  @tms_client delete_task id
end
