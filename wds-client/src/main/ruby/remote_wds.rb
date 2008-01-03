require "net/http"
require 'uri'

FILE_WE_WANT_TO_STORE = "http://tempo.intalio.org/tempo/trunk/wds-client/AbsenceRequest/AbsenceApproval.xform"
FILE_URL = URI.parse FILE_WE_WANT_TO_STORE
FILE_DATA = Net::HTTP.get(FILE_URL.host, FILE_URL.path, FILE_URL.port)
url = URI.parse('http://localhost:8080/wds/item')

def helper_display_result
  puts 'Code = ' + @resp.code
  puts 'Message = ' + @resp.message
  @resp.each {|key, val| puts key + ' = ' + val}
  puts @data
end

def assert
  raise "Assertion failed !" unless yield
end

@http = Net::HTTP.new url.host, url.port
@headers = {'Content-Type' => 'application/octet-stream'}

# Store-lifecycle of  some generic data
@resp, @data = @http.put(url.path, FILE_DATA, @headers)
assert {@resp.code == "200"}
@resp, @data = @http.get(url.path)
  ## there should be an item at this time
assert {@resp.code != "404"}
@resp, @data = @http.delete(url.path)
assert {@resp.code == "200"}
@resp, @data = @http.get(url.path)
  ## the item should be deleted at this time
assert {@resp.code == "404"}

# Store of an xform
@headers["Is-XForm"] = "true"
@resp,@data = @http.put(url.path, FILE_DATA, @headers)
assert {@resp.code == "200"}
@resp,@data = @http.delete(url.path)
assert {@resp.code == "200"}
