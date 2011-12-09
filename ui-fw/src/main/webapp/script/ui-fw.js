/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: $
 * $Log:$
 */
function submitAction(action) {
	actionNameObj = document.getElementById('actionName').value = action;
	document.getElementById('form').submit();
}

function submitActionToURL(url, actionName) {
	formObj = document.getElementById('form');
	formObj.action = url;
	document.getElementById('actionName').value = actionName;
	formObj.submit();
}

function resizeIframe() {
	
	 var height = document.documentElement.clientHeight;
	    height -= document.getElementById('taskform').offsetTop;
	    
	    
	    try {
	    	 if(navigator.appName != "Microsoft Internet Explorer")
	    	    {
	    	      
	    	    /* Commented the below line as it always set the height of IFrame to 20 while resizing, due to which gi form gets disappered  */   
	    	   // height = 20; /* whatever you set your body bottom margin/padding to be */
		        height -= 20; /* whatever you set your body bottom margin/padding to be */
	    	    document.getElementById('taskform').style.height = height +"px";
	    	    
	    	    }
	    	 else {
	    		 height -= 20; /* whatever you set your body bottom margin/padding to be */
	    		 document.getElementById('taskform').style.height = height +"px";
	    		    
	        }
	      } catch(err) {
	        return;
      }
  
   }
   
 function showAlertForTask(message,title){       
	jAlert(message, title); 
  }

  function showAlertForProcess(message,title){
	jAlert(message, title); 
  }

  function showAlertForNotification(message,title){
	jAlert(message, title); 
  }
   