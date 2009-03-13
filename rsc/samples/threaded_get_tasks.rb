#!/usr/bin/env ruby
load File.dirname(File.expand_path("#{$0}"))+"/lib/sample_tms_client.rb"
require "pp"

@@record = File.open("record.csv",  File::WRONLY|File::CREAT||File::APPEND)
@@record << "Name,Round,Clients,Retrieve Time,Time\n"
@@start = Time.now
@@total = 0

class TaskGetter
  def initialize(name, round=10, sleep_interval=0.5)
    @name = name
    @round = round
    @debug = true
    @sleep = sleep_interval
  end
  def run
    tms_client = SampleTMSClient.new
    (1..@round).each do |i|
      t1 = Time.now
      ts = tms_client.get_available_tasks "", "PATask", 0, 5
      t2 = Time.now
      @@record << "#{@name},#{i},#{@@total},#{t2-t1},#{t2-@@start}\n" if @debug
      @@record.flush
      sleep(@sleep) if i < @round
    end
  end
end

class TaskGetterStarter
  def initialize(number=3, round=10, sleep_interval=1, spawn_interval=5)
    @number = number
    @round = round
    @sleep_interval = sleep_interval
    @spawn_interval = spawn_interval
  end
  def run
    clients = Hash.new
    (1..@number).each do |client|
      @@total = client
      clients[client] = Thread.new {g = TaskGetter.new("client#{client}", @round,@sleep_interval) ; g.run}      
      sleep(5+rand(@spawn_interval)) if client < @number
    end
    (1..@number).each do |client|
      clients[client].join
      @@total-=1
    end
  end
end

# tgs = TaskGetterStarter.new(3,10)
tgs = TaskGetterStarter.new(20,10,10)
tgs.run