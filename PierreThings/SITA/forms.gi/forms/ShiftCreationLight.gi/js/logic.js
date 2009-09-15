/* place JavaScript code here */

 function available(shiftName){
     ShiftCreationLight.getJSXByName("shift-nomap").setValue(shiftName);
     mechanics.service.callgetAllMechs();
     avionics.service.callgetAllAvis();
     coord.service.callgetAllCoord();   
 }