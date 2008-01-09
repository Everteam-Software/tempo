#!/usr/bin/env ruby
require "../wds-cli"

wds_client [
  Pipa.new("AbsenceRequest/AbsenceApproval.xform", "AbsenceApproval.xform", "ar-deploy.xml"),
  Activity.new("AbsenceRequest/AbsenceApproval.xform", "AbsenceApproval.xform"),
  Activity.new("AbsenceRequest/Notification.xform", "Notification.xform")]
