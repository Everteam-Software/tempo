#!/usr/bin/env ruby
require "../wds-cli"

wds_client [
  Pipa.new("ChainedExecution/SelectItem.xform", "SelectItem.xform", "store-pipa-deploy.xml"),
  Activity.new("ChainedExecution/Address.xform", "Address.xform"),
  Activity.new("ChainedExecution/Payment.xform", "Payment.xform")
  ]