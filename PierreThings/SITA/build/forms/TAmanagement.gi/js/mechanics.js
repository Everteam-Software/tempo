jsx3.lang.Package.definePackage(
  "mechanics.service",                //the full name of the package to create
  function(service) {          //name the argument of this function

    //call this method to begin the service call (mechanics.service.callgetAllMechs();)
    service.callgetAllMechs = function() {
      var objService = TAmanagement.loadResource("getMechanics_xml");
      objService.setOperation("getAllMechs");

      //subscribe
      objService.subscribe(jsx3.net.Service.ON_SUCCESS, service.ongetAllMechsSuccess);
      objService.subscribe(jsx3.net.Service.ON_ERROR, service.ongetAllMechsError);
      objService.subscribe(jsx3.net.Service.ON_INVALID, service.ongetAllMechsInvalid);

      //PERFORMANCE ENHANCEMENT: uncomment the following line of code to use XSLT to convert the server response to CDF (refer to the API docs for jsx3.net.Service.compile for implementation details)
      //objService.compile();

      //call the service
      objService.doCall();
    };

    service.ongetAllMechsSuccess = function(objEvent) {
      //var responseXML = objEvent.target.getInboundDocument();
      //objEvent.target.getServer().alert("Success","The service call was successful.");
      
      TAmanagement.getJSXByName("availableMechanics-nomap").repaintData();
      
    };

    service.ongetAllMechsError = function(objEvent) {
      var myStatus = objEvent.target.getRequest().getStatus();
      objEvent.target.getServer().alert("Error","The service call failed. The HTTP Status code is: " + myStatus);
    };

    service.ongetAllMechsInvalid = function(objEvent) {
      objEvent.target.getServer().alert("Invalid","The following message node just failed validation:\n\n" + objEvent.message);
    };

  }
);




