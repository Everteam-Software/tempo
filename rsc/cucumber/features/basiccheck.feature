Feature: Check Tempo Preview Build
  In order to deliver a clean build to Shao, I need to check it includes all the necessary components

  Scenario: Check the webapps
    Then I should find the webapp: ui-fw
	Then I should find the webapp: axis2
	Then I should find the webapp: cas
	Then I should find the webapp: alfresco
	Then I should find the webapp: 0apacheds
	Then I should find the webapp: fds
	Then I should find the webapp: ode
	Then I should find the webapp: wds
	Then I should find the webapp: xFormsManager

  Scenario: Check the axis2 services
	Then it should provide the security service
	Then it should provide the tms service
	Then it should provide the deploy service
	Then it should provide the tas service
	
  Scenario: Check the libraries
	Given the /common/lib folder 
	Then it should find the jar file tempo-registry
	Then it should find the jar file mysql-connector
	Then it should find the jar file casclient
	Then it should find the jar file xercesImpl
	Then it should find the jar file xmlParserAPIs

  Scenario: Check alfreso dlls for windows
	Given the /bin folder
	Then it should contains Win32Utils.dll
	Then it should contains Win32NetBIOS.dll

  Scenario: Check the configation files
    Given the / folder
    Then it should contains bpms.sql

    Given the /conf folder
    Then it should contains jaas.config
	Then it should contains axis2.xml
    
    Given the /common/classes folder
    Then it should contains log4j.properties

    Given the /var/config folder 
    Then it should contains fds-config.xml
	Then it should contains ldap.properties
	Then it should contains ode-axis2.properties
	Then it should contains security.xml
	Then it should contains tempo-deploy.properties
	Then it should contains tempo-deploy.xml
	Then it should contains tempo-formmanager.xml
	Then it should contains tempo-registry.properties
	Then it should contains tempo-tas.xml
	Then it should contains tempo-tms.xml
	Then it should contains tempo-ui-fw-servlet.xml
	Then it should contains tempo-ui-fw.xml
	Then it should contains tempo-wds.xml
	Then it should contains tempokeystore
	
	Scenario: Check the processes
    Given the /var/processes folder
    Then it should not contained deployed items
    Then it should contain the process AbsenceRequest
    Then it should contain the process TaskManager

    Scenario: Check the pipa
    Given the /var/deploy folder
    Then it should not contained deployed items

    Given the pipa AbsenceRequest
    Then it should have a property task-role-owners with value intalio\\Sales

    Scenario: Check empty folders
    Given the logs folder 
    Then it should be empty
	Given the temp folder
	Then it should be empty
	Given the work folder
	Then it should be empty
	Given the var/alfresco folder
	Then it should be empty
	
	Scenario: Check liferay portlets
	Given the ROOT portlet
	Then check the Login portlet is disabled
	
	Scenario: Check the UIFW portlet
	Given the ui-fw portlet
	Then it should contain the necessary portlet files
	Then the portlet id should be UifwPortlet and displays in liferay as Intalio|Workflow in category Intalio
	
	Scenario: Check the alfresco portlet
	Given the alfresco portlet
	Then it should contain the necessary portlet files
	
	Scenario: Check url rewriting for feeds
	Given the ui-fw portlet
	Then url rewrites requests for /atom/processes
	Then url rewrites requests for /atom/tasks
	
	Scenario: Check we won't know it's been made on OSX
	Given the / folder
	Then it does not contain any .DS_Store file
	