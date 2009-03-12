#!/usr/bin/env ruby
gem "soap4r"
require 'soap/marshal'
require 'soap/mapping'
require 'soap/soap'
require 'soap/baseData'
require "rexml/document"

include SOAP
load File.dirname(File.expand_path("#{$0}"))+"/lib/sample_tms_client.rb"

# {http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/}taskMetadata
#   taskId - SOAP::SOAPString
#   taskState - SOAP::SOAPToken
#   taskType - SOAP::SOAPToken
#   description - SOAP::SOAPString
#   processId - SOAP::SOAPString
#   creationDate - SOAP::SOAPDateTime
#   deadline - SOAP::SOAPDateTime
#   priority - SOAP::SOAPInt
#   userOwner - SOAP::SOAPString
#   roleOwner - SOAP::SOAPString
#   formUrl - SOAP::SOAPAnyURI
#   failureCode - SOAP::SOAPString
#   failureReason - SOAP::SOAPString
#   userProcessCompleteSOAPAction - SOAP::SOAPString
#   processEndpoint - SOAP::SOAPAnyURI
#   initMessageNamespaceURI - SOAP::SOAPAnyURI
#   initOperationSOAPAction - SOAP::SOAPString
#   isChainedBefore - SOAP::SOAPString
#   previousTaskId - SOAP::SOAPString
#   claimAction - AccessControlType
#   revokeAction - AccessControlType
#   saveAction - AccessControlType
#   completeAction - AccessControlType
#   attachments - Attachments


input = REXML::Document.new "<input><test>inputest-input></test></input>"
s = SOAPREXMLElementWrap.new(input)

st = SampleTMSClient.new
(1..5000).each do |d|
  t = {:metadata=>
    {:taskId=>"001#{d}", :taskType=>"activity", :description=>"Hello#{d}", :processId=>"Fake", :formUrl=>"http://localhost/11", :userProcessCompleteSOAPAction=>"urn:complete", :userOwner=>"intalio\\admin"}, :input=>s, :output=>s}
  
st.tms_client.create(:task=>t, :participantToken=>st.token)
end