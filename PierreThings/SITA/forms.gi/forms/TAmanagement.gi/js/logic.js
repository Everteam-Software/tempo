/* place JavaScript code here */

 function ackUpdate(){
    TAmanagement.getJSXByName("update").setChecked(0);
 }
 
 function updateTime(objSelect){
     var status = objSelect.getText();
     if (status == "Started"){
         TAmanagement.getJSXByName("startTime").setValue(new Date());
     } else if (status == "Stopped"){
         TAmanagement.getJSXByName("finishTime").setValue(new Date());
     } else if (status == "Released"){
         TAmanagement.getJSXByName("releaseTime").setValue(new Date());
     }
 }

 function available(shiftName){
     TAmanagement.getJSXByName("shift-nomap").setValue(shiftName);
     mechanics.service.callgetAllMechs();
     avionics.service.callgetAllAvis();
     coord.service.callgetAllCoord();   
 }

 function updateNotifUsers(){
 
     var userString = "";
 
     var assignedCoord = TAmanagement.getCache().getDocument(TAmanagement.getJSXByName("assignedCoord").getXMLId());
     var coordIter = assignedCoord.getChildIterator(true);
     
     while (coordIter.hasNext()){
         var current = coordIter.next();
         userString += current.getAttribute("jsxAssignedCoordID")+";";
     }
     
     var assignedMechs = TAmanagement.getCache().getDocument(TAmanagement.getJSXByName("assignedMechanics").getXMLId());
     var mechIter = assignedMechs.getChildIterator(true);
     
     while (mechIter.hasNext()){
         var current = mechIter.next();
         userString += current.getAttribute("jsxAssignedMechanicID")+";";
     }
     
     var assignedAvis = TAmanagement.getCache().getDocument(TAmanagement.getJSXByName("assignedAvionics").getXMLId());
     var aviIter = assignedAvis.getChildIterator(true);
     
     while (aviIter.hasNext()){
         var current = aviIter.next();
         userString += current.getAttribute("jsxAssignedAvionicID")+";";
     }
     
     userString = userString.substring(0, userString.length-1);
     
     TAmanagement.getJSXByName("notifUsers-nomap").setValue(userString);
 }
 
  function assignCoord(objRow){

    var Matrix = objRow.getParent().getParent();
    var server = Matrix.getServer();
    var queryString = "/data/record[@jsxid='" + objRow.emGetSession().recordId + "']";
    var coordNode = server.getCache().getDocument(Matrix.getXMLId()).selectSingleNode(queryString);
    var coordName = coordNode.getAttribute("dbCoordName");
    var coordID = coordNode.getAttribute("dbCoordID");
    var coordCert = coordNode.getAttribute("dbCoordCert");

    var assignedCoordTable = server.getJSXByName("assignedCoord");
    var assignedCoords = server.getCache().getDocument(assignedCoordTable.getXMLId());
    var assignedCoordIter = assignedCoords.getChildIterator(true);
    while(assignedCoordIter.hasNext()){
        var current = assignedCoordIter.next();
        if (current.getAttribute("jsxAssignedCoordID") == coordID){
            TAmanagement.alert("Warning", "This coordinator is already assigned to this TA.");
            return;
        }
    }
    var objRecord = new Object(); // new CDF record obj
    objRecord.jsxid = jsx3.CDF.getKey();
    //read user input
    objRecord.jsxAssignedCoordName = coordName;
    objRecord.jsxAssignedCoordID = coordID;
    objRecord.jsxAssignedCoordCert = coordCert;
    
    assignedCoordTable.insertRecord(objRecord, null, true);   
    assignedCoordTable.repaintData();
 }
 
 function unassignCoord(objRow){
     
    var Matrix = objRow.getParent().getParent();
    var server = Matrix.getServer();
    var queryString = "/data/record[@jsxid='" + objRow.emGetSession().recordId + "']";
    var coordNode = server.getCache().getDocument(Matrix.getXMLId()).selectSingleNode(queryString);
    var coordName = coordNode.getAttribute("jsxAssignedCoordName");
    var coordID = coordNode.getAttribute("jsxAssignedCoordID");
    var coordCert = coordNode.getAttribute("jsxAssignedCoordCert");

    var availableCoords = server.getJSXByName("availableCoord-nomap");
    var objRecord = new Object(); // new CDF record obj
    objRecord.jsxid = jsx3.CDF.getKey();
    //read user input
    objRecord.dbCoordName = coordName;
    objRecord.dbCoordID = coordID;
    objRecord.dbCoordCert = coordCert;
    
    availableCoords.insertRecord(objRecord, null, true);   
    availableCoords.repaintData();
 }

 function assignMechanic(objRow){

    var Matrix = objRow.getParent().getParent();
    var server = Matrix.getServer();
    var queryString = "/data/record[@jsxid='" + objRow.emGetSession().recordId + "']";
    var mechNode = server.getCache().getDocument(Matrix.getXMLId()).selectSingleNode(queryString);
    var mechName = mechNode.getAttribute("dbMechanicName");
    var mechID = mechNode.getAttribute("dbMechanicID");
    var mechCert = mechNode.getAttribute("dbMechanicCert");

    var assignedMechTable = server.getJSXByName("assignedMechanics");
    var assignedMechs = server.getCache().getDocument(assignedMechTable.getXMLId());
    var assignedMechIter = assignedMechs.getChildIterator(true);
    while(assignedMechIter.hasNext()){
        var current = assignedMechIter.next();
        if (current.getAttribute("jsxAssignedMechanicID") == mechID){
            TAmanagement.alert("Warning", "This mechanic is already assigned to this TA.");
            return;
        }
    }
    var objRecord = new Object(); // new CDF record obj
    objRecord.jsxid = jsx3.CDF.getKey();
    //read user input
    objRecord.jsxAssignedMechanicName = mechName;
    objRecord.jsxAssignedMechanicID = mechID;
    objRecord.jsxAssignedMechanicCert = mechCert;
    
    assignedMechTable.insertRecord(objRecord, null, true);   
    assignedMechTable.repaintData();
 }
 
 function unassignMechanic(objRow){
     
    var Matrix = objRow.getParent().getParent();
    var server = Matrix.getServer();
    var queryString = "/data/record[@jsxid='" + objRow.emGetSession().recordId + "']";
    var mechNode = server.getCache().getDocument(Matrix.getXMLId()).selectSingleNode(queryString);
    var mechName = mechNode.getAttribute("jsxAssignedMechanicName");
    var mechID = mechNode.getAttribute("jsxAssignedMechanicID");
    var mechCert = mechNode.getAttribute("jsxAssignedMechanicCert");

    var availableMechs = server.getJSXByName("availableMechanics-nomap");
    var objRecord = new Object(); // new CDF record obj
    objRecord.jsxid = jsx3.CDF.getKey();
    //read user input
    objRecord.dbMechanicName = mechName;
    objRecord.dbMechanicID = mechID;
    objRecord.dbMechanicCert = mechCert;
    
    availableMechs.insertRecord(objRecord, null, true);   
    availableMechs.repaintData();
 }

 function assignAvionic(objRow){

    var Matrix = objRow.getParent().getParent();
    var server = Matrix.getServer();
    var queryString = "/data/record[@jsxid='" + objRow.emGetSession().recordId + "']";
    var aviNode = server.getCache().getDocument(Matrix.getXMLId()).selectSingleNode(queryString);
    var aviName = aviNode.getAttribute("dbAvionicName");
    var aviID = aviNode.getAttribute("dbAvionicID");
    var aviCert = aviNode.getAttribute("dbAvionicCert");

    var assignedAvionicsTable = server.getJSXByName("assignedAvionics");
    var assignedAvis = server.getCache().getDocument(assignedAvionicsTable.getXMLId());
    var assignedAviIter = assignedAvis.getChildIterator(true);
    while(assignedAviIter.hasNext()){
        var current = assignedAviIter.next();
        if (current.getAttribute("jsxAssignedAvionicID") == aviID){
            TAmanagement.alert("Warning", "This avionic is already assigned to this TA.");
            return;
        }
    }
    var objRecord = new Object(); // new CDF record obj
    objRecord.jsxid = jsx3.CDF.getKey();
    //read user input
    objRecord.jsxAssignedAvionicName = aviName;
    objRecord.jsxAssignedAvionicID = aviID;
    objRecord.jsxAssignedAvionicCert = aviCert;
    
    assignedAvionicsTable.insertRecord(objRecord, null, true);   
    assignedAvionicsTable.repaintData();
 }
 
 function unassignAvionic(objRow){
     
    var Matrix = objRow.getParent().getParent();
    var server = Matrix.getServer();
    var queryString = "/data/record[@jsxid='" + objRow.emGetSession().recordId + "']";
    var aviNode = server.getCache().getDocument(Matrix.getXMLId()).selectSingleNode(queryString);
    var aviName = aviNode.getAttribute("jsxAssignedAvionicName");
    var aviID = aviNode.getAttribute("jsxAssignedAvionicID");
    var aviCert = aviNode.getAttribute("jsxAssignedAvionicCert");

    var availableAvis = server.getJSXByName("availableAvionics-nomap");
    var objRecord = new Object(); // new CDF record obj
    objRecord.jsxid = jsx3.CDF.getKey();
    //read user input
    objRecord.dbAvionicName = aviName;
    objRecord.dbAvionicID = aviID;
    objRecord.dbAvionicCert = aviCert;
    
    availableAvis.insertRecord(objRecord, null, true);   
    availableAvis.repaintData();
 }
 