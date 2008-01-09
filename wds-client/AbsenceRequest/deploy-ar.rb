#!/usr/bin/env ruby
require "../wds-cli"

wds_deploy "store-activity AbsenceRequest/AbsenceApproval.xform AbsenceApproval.xform"
wds_deploy "-d ar-deploy.xml store-pipa AbsenceRequest/AbsenceRequest.xform AbsenceRequest.xform"
wds_deploy "store-activity AbsenceRequest/Notification.xform Notification.xform"