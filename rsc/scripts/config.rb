include BuildSupport
BuildSupport::BUILD_DEBUG = false

BUILD_CONFIG = {
  :directory => "../intalio",
  
  # build nothing
  # :mode => BuildMode::EMPTY,
  #
  
  # build with ode, axis2, uifw
  #
  :mode => BuildMode::TOMCAT|BuildMode::UIFW,
  #
  
  # build with tomcat6 and uifw 
  #
  # :mode => BuildMode::REMOTE|BuildMode::UIFW,
  #
  
  # build an opensso server
  #
  # :mode => BuildMode::OPENSSO,
  #
  
  :ode => :v2_1_snapshot,
  :tomcat => :v5,
  :liferay => :v5_1_0,
  :alfresco => :v3_0,
  :tempo => {
    :core => "6.0.0.34",
    :security => "1.0.0",
    :deploy => "1.0.0",
    :formManager => "6.0.0.35",
    :apacheds => "6.0.0.34",
    :cas => "6.0.0.34"
  }
}