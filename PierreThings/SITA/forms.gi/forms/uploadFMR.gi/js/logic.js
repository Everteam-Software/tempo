/* place JavaScript code here */

jsx3.lang.Package.definePackage(
  "FMR.service",                //the full name of the package to create
  function(service) {          //name the argument of this function

    //call this method to begin the service call (FMR.service.callImportFMR();)
    service.callImportFMR = function() {
      var objService = uploadFMR.loadResource("PopulateFMRfields_xml");
      objService.setOperation("ImportFMR");

      //subscribe
      objService.subscribe(jsx3.net.Service.ON_SUCCESS, service.onImportFMRSuccess);
      objService.subscribe(jsx3.net.Service.ON_ERROR, service.onImportFMRError);
      objService.subscribe(jsx3.net.Service.ON_INVALID, service.onImportFMRInvalid);

      //PERFORMANCE ENHANCEMENT: uncomment the following line of code to use XSLT to convert the server response to CDF (refer to the API docs for jsx3.net.Service.compile for implementation details)
      //objService.compile();

      //call the service
      objService.doCall();
    };

    service.onImportFMRSuccess = function(objEvent) {
      var responseXML = objEvent.target.getInboundDocument().getFirstChild().getFirstChild();
      var server = objEvent.target.getServer();
      server.getJSXByName("aircraft").setValue(responseXML.selectSingleNode("aircraft").getValue());
      server.getJSXByName("ScheduledArrivalDate").setValue(responseXML.selectSingleNode("ScheduledArrivalDate").getValue());
      server.getJSXByName("STA").setValue(responseXML.selectSingleNode("STA").getValue());
      server.getJSXByName("ArrivalFlightNumber").setValue(responseXML.selectSingleNode("ArrivalFlightNumber").getValue());
      var receivedAAD = responseXML.selectSingleNode("ActualArrivalDate").getValue();
      if (receivedAAD!=''){
          server.getJSXByName("ActualArrivalDate").setValue(receivedAAD);
      } else {
          server.getJSXByName("ActualArrivalDate").setValue('1970-01-01');
      }
      server.getJSXByName("ATA").setValue(responseXML.selectSingleNode("ATA").getValue());
      server.getJSXByName("ScheduledDepartureDate").setValue(responseXML.selectSingleNode("ScheduledDepartureDate").getValue());
      server.getJSXByName("STD").setValue(responseXML.selectSingleNode("STD").getValue());
      var receivedADD = responseXML.selectSingleNode("ActualDepartureDate").getValue();
      if (receivedADD!=''){
          server.getJSXByName("ActualDepartureDate").setValue(receivedADD);
      } else {
          server.getJSXByName("ActualDepartureDate").setValue('1970-01-01');
      }
      server.getJSXByName("ATD").setValue(responseXML.selectSingleNode("ATD").getValue());
      server.getJSXByName("Stand").setValue(responseXML.selectSingleNode("Stand").getValue());
      server.getJSXByName("InspectionType").setValue(responseXML.selectSingleNode("InspectionType").getValue());
      server.getJSXByName("DepartureFlightNumber").setValue(responseXML.selectSingleNode("DepartureFlightNumber").getValue());
      server.getJSXByName("RTRid").setValue(responseXML.selectSingleNode("RTRid").getValue());
      server.getJSXByName("filepath").setValue("");
    };

    service.onImportFMRError = function(objEvent) {
      var myStatus = objEvent.target.getRequest().getStatus();
      objEvent.target.getServer().alert("Error","The service call failed. The HTTP Status code is: " + myStatus);
    };

    service.onImportFMRInvalid = function(objEvent) {
      objEvent.target.getServer().alert("Invalid","The following message node just failed validation:\n\n" + objEvent.message);
    };

  }
);

