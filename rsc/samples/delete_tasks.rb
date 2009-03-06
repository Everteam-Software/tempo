#!/usr/bin/env ruby
load File.dirname(File.expand_path("#{$0}"))+"/lib/sample_tms_client.rb"

# create a new tms client
tms_client = SampleTMSClient.new

# sample to delete task by query
# those task have to be accessible by the authenticated user, since the query use the getAvailableTask method on the server
# tms_client.delete_all "PATask", "", false

ids = ["657547e5417d0838:-350821f8:11fda5d0085:-7ff010.200.2.713202"]
# delete by ids
# task don't have to be accessible by the user, but the user needs to have the rights to delete tasks
tms_client.delete_by_ids ids