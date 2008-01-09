#!/usr/bin/env ruby
require "../wds-cli"

wds_client [Pipa.new("PIPA/PIPA.xform", "startform.xform", "pipa-deploy.xml")]
