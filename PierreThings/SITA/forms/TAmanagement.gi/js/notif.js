jsx3.lang.Package.definePackage(
  "notif.service",                //the full name of the package to create
  function(service) {          //name the argument of this function

    //call this method to begin the service call (notif.service.callstart();)
    service.callstart = function() {
    
      updateNotifUsers();
      
      var objService = TAmanagement.loadResource("sendNotif_xml");
      objService.setOperation("start");

      //subscribe
      objService.subscribe(jsx3.net.Service.ON_SUCCESS, service.onstartSuccess);
      objService.subscribe(jsx3.net.Service.ON_ERROR, service.onstartError);
      objService.subscribe(jsx3.net.Service.ON_INVALID, service.onstartInvalid);

      //PERFORMANCE ENHANCEMENT: uncomment the following line of code to use XSLT to convert the server response to CDF (refer to the API docs for jsx3.net.Service.compile for implementation details)
      //objService.compile();

      //call the service
      objService.doCall();
    };

    service.onstartSuccess = function(objEvent) {
      //var responseXML = objEvent.target.getInboundDocument();
      objEvent.target.getServer().alert("Success", "The assigned mechanics and avionics have been notified.");
    };

    service.onstartError = function(objEvent) {
      var myStatus = objEvent.target.getRequest().getStatus();
      objEvent.target.getServer().alert("Error","The service call failed. The HTTP Status code is: " + myStatus);
    };

    service.onstartInvalid = function(objEvent) {
      objEvent.target.getServer().alert("Invalid","The following message node just failed validation:\n\n" + objEvent.message);
    };

  }
);

