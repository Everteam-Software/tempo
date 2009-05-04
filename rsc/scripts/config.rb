include BuildSupport
BuildSupport::BUILD_DEBUG = false

BUILD_CONFIG = {
  :directory => "../tempo",
  
  # build with ode, axis2, uifw
  #
  :mode => [BuildMode::BPMS,BuildMode::UIFW],
  #
  
  # build an opensso server
  #
  # :mode =>  [BuildMode::TOMCAT6,BuildMode::OPENSSO_SERVER],
  #
  
  # build with ode, axis2, uifw, ldap and cas
  # :mode => [BuildMode::BPMS,BuildMode::UIFW, BuildMode::CAS],
  
  ### BELOW IS NOT TESTED SO MUCH
  
  # build with ode, axis2, *NO* uifw, and opensso support on the server side
  #
  # :mode => [BuildMode::BPMS,BuildMode::OPENSSO]
  #
  
  #
  # build the above and zip the resulting folder
  # :mode => [BuildMode::TOMCAT,BuildMode::UIFW,BuildMode::ZIP],
  # :mode => [BuildMode::TOMCAT,BuildMode::UIFW],
  
  # build with tomcat6 and uifw 
  #
  # :mode => [BuildMode::TOMCAT6,BuildMode::UIFW],
  #
  
  # build with tomcat6 and uifw, enable for opensso 
  #
  # :mode => [BuildMode::REMOTE, BuildMode::TOMCAT6,BuildMode::TOKEN_SERVICE, BuildMode::UIFW,BuildMode::OPENSSO,BuildMode::AGENT],
  #
  
  :ode => :v1_3_snapshot,
  :tomcat => :v5,
  :liferay => :v5_1_0,
  :alfresco => :v3_0,
  :tempo => {
    :core => "6.0.0.46-SNAPSHOT",
    # not used until we've upgraded to the new deploy and security packages
    :security => "1.0.1",
    :deploy => "1.0.3",
    
    :processes => "6.0.6",
    :formManager => "6.0.0.40",
    :apacheds => "6.0.0.34",
    :cas => "6.0.0.34"
  }
}