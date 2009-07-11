jsx3.lang.Package.definePackage(
  "avionics.service",                //the full name of the package to create
  function(service) {          //name the argument of this function

    //call this method to begin the service call (avionics.service.callgetAllAvis();)
    service.callgetAllAvis = function() {
      var objService = TAmanagement.loadResource("getAvionics_xml");
      objService.setOperation("getAllAvis");

      //subscribe
      objService.subscribe(jsx3.net.Service.ON_SUCCESS, service.ongetAllAvisSuccess);
      objService.subscribe(jsx3.net.Service.ON_ERROR, service.ongetAllAvisError);
      objService.subscribe(jsx3.net.Service.ON_INVALID, service.ongetAllAvisInvalid);

      //PERFORMANCE ENHANCEMENT: uncomment the following line of code to use XSLT to convert the server response to CDF (refer to the API docs for jsx3.net.Service.compile for implementation details)
      //objService.compile();

      //call the service
      objService.doCall();
    };

    service.ongetAllAvisSuccess = function(objEvent) {
      //var responseXML = objEvent.target.getInboundDocument();
      //objEvent.target.getServer().alert("Success","The service call was successful.");
      
      TAmanagement.getJSXByName("availableAvionics-nomap").repaintData();
      
    };

    service.ongetAllAvisError = function(objEvent) {
      var myStatus = objEvent.target.getRequest().getStatus();
      objEvent.target.getServer().alert("Error","The service call failed. The HTTP Status code is: " + myStatus);
    };

    service.ongetAllAvisInvalid = function(objEvent) {
      objEvent.target.getServer().alert("Invalid","The following message node just failed validation:\n\n" + objEvent.message);
    };

  }
);

