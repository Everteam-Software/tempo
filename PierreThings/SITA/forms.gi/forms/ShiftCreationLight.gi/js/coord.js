jsx3.lang.Package.definePackage(
  "coord.service",                //the full name of the package to create
  function(service) {          //name the argument of this function

    //call this method to begin the service call (coord.service.callgetAllCoord();)
    service.callgetAllCoord = function() {
      var objService = ShiftCreationLight.loadResource("getCoord_xml");
      objService.setOperation("getAllCoord");

      //subscribe
      objService.subscribe(jsx3.net.Service.ON_SUCCESS, service.ongetAllCoordSuccess);
      objService.subscribe(jsx3.net.Service.ON_ERROR, service.ongetAllCoordError);
      objService.subscribe(jsx3.net.Service.ON_INVALID, service.ongetAllCoordInvalid);

      //PERFORMANCE ENHANCEMENT: uncomment the following line of code to use XSLT to convert the server response to CDF (refer to the API docs for jsx3.net.Service.compile for implementation details)
      //objService.compile();

      //call the service
      objService.doCall();
    };

    service.ongetAllCoordSuccess = function(objEvent) {
      //var responseXML = objEvent.target.getInboundDocument();
      // objEvent.target.getServer().alert("Success","The service call was successful.");
      
      ShiftCreationLight.getJSXByName("coordinators").repaintData();
    };

    service.ongetAllCoordError = function(objEvent) {
      var myStatus = objEvent.target.getRequest().getStatus();
      objEvent.target.getServer().alert("Error","The service call failed. The HTTP Status code is: " + myStatus);
    };

    service.ongetAllCoordInvalid = function(objEvent) {
      objEvent.target.getServer().alert("Invalid","The following message node just failed validation:\n\n" + objEvent.message);
    };

  }
);

