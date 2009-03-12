#!/usr/bin/env ruby
load File.dirname(File.expand_path("#{$0}"))+"/lib/sample_tms_client.rb"
require "pp"

class TaskGetter
  def initialize(round=10, sleep_interval=0.5)
    @round = round
    @debug = true
    @sleep = sleep_interval
  end
  def run
    tms_client = SampleTMSClient.new
    (1..@round).each do |i|
      t1 = Time.now
      ts = tms_client.get_available_tasks "", "PATask"
      t2 = Time.now
      puts t2-t1 if @debug
      sleep(@sleep)
    end
  end
end

class TaskGetterStarter
  def initialize(number=3, round=10, sleep_interval=1)
    @number = number
    @round = round
    @sleep_interval = sleep_interval
  end
  def run
    clients = Hash.new
    (0..@number).each do |client|
      puts "Spawning client:#{client}"
      clients[client] = Thread.new {g = TaskGetter.new(@round,@sleep_interval) ; g.run}      
    end
    (1..@number).each do |client|
      clients[client].join
    end
  end
end

# tgs = TaskGetterStarter.new(3,10)
tgs = TaskGetterStarter.new(1,1)
tgs.run