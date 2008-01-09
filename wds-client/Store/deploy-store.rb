#!/usr/bin/env ruby
require "../wds-cli"

wds_deploy "-d store-pipa-deploy.xml store-pipa ChainedExecution/SelectItem.xform SelectItem.xform"
wds_deploy "store-activity ChainedExecution/Address.xform Address.xform"
wds_deploy "store-activity ChainedExecution/Payment.xform Payment.xform"