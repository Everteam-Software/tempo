/*
 *  Copyright (C) 2008, Intalio Inc.
 *
 *  The program(s) herein may be used and/or copied only with the
 *  written permission of Intalio Inc. or in accordance with the terms
 *  and conditions stipulated in the agreement/contract under which the
 *  program(s) have been supplied.
 *
 * Date         Author             Changes
 * Apr 11, 2008   Mark Horton  Created
 */

/////////////////////////////////
// TaskManagementServices WSDL //
/////////////////////////////////
jsx3.lang.Package.definePackage(
  "Intalio.Internal.TMS",
  function(tms) {

    tms.TMS_NS = "xmlns:tms='http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/'";

    /////////////
    // getTask //
    /////////////
    tms.callGetTask = function() {
        Intalio.Internal.Utilities.showLoading();
        
        try {
            Intalio.Internal.Communication.sendRequest("IntalioInternal_TaskManagementServicesMapping_xml",
                "getTask", tms.onGetTaskSuccess, tms.onGetTaskTimeOut);
        } catch (e) {
            Intalio.Internal.Utilities.hideLoading();
            Intalio.Internal.Utilities.displayException(e); 
        }
    };

    tms.onGetTaskSuccess = function(event) {
        Intalio.Internal.Utilities.hideLoading();
        
        var response = event.target.getResponseXML();
        jsx3.log("getTask response: " + Intalio.Internal.Utilities.tidy(response)); 
        var inDoc = Intalio.Internal.Communication.getPayload(response);
        
        if (inDoc == null) {
            return;
        }
        
        Intalio.Internal.Utilities.setGetTaskDocument(inDoc);
        
        var taskType = Intalio.Internal.Utilities.getTaskType();
        if (taskType == null) {
            var msg = "getTask error: no taskType";
            Intalio.Internal.Utilities.displayError(msg);
            return;
        }
        
        var taskState = Intalio.Internal.Utilities.getTaskState();
        
        jsx3.log("getTask taskType: " + taskType);
        jsx3.log("getTask taskState: " + taskState);
        
        // get the data input node
        var mapNode = inDoc.selectSingleNode("//tms:output", tms.TMS_NS);
        if (mapNode == null) {
            mapNode = inDoc.selectSingleNode("//tms:input", tms.TMS_NS);
        }
        
        if (mapNode != null) {
            // create a doc from the node
            var mapDoc = new jsx3.xml.Document();
            mapDoc.loadXML(mapNode.getFirstChild());
            jsx3.log("getTask inbound mapping node: " + Intalio.Internal.Utilities.tidy(mapDoc));
        
            try {
                // invoke the inbound mapping
                var mapService = Intalio.Internal.Utilities.SERVER.loadResource(Intalio.Internal.Utilities.MAPPING);
                mapService.setInboundDocument(mapDoc);
                mapService.doInboundMap();        
                Intalio.Internal.Utilities.SERVER.getRootBlock().repaint();
            } catch (e) {
                Intalio.Internal.Utilities.displayException(e);                
                return;
            }
        }
        
        // now that fields are populated, check the form fields validations
        Intalio.Internal.Utilities.updateValidationStatus();
        
        Intalio.Internal.Utilities.activateButtons();       
        jsx3.log("getTask succeeded.");
        
        Intalio.Internal.Utilities.SERVER.publish({subject:Intalio.Internal.Utilities.GET_TASK_SUCCESS});   
    };
    
    tms.onGetTaskTimeOut = function(event) {
        Intalio.Internal.Utilities.hideLoading();
        var msg = "The operation timed out.";
        Intalio.Internal.Utilities.displayError(msg);
        
        Intalio.Internal.Utilities.SERVER.publish({subject:Intalio.Internal.Utilities.GET_TASK_TIMEOUT});       
    };

    /////////////////////////
    // initProcess (start) //
    /////////////////////////
    tms.callInitProcess = function() {
        // make sure the form is complete
        if (Intalio.Internal.Utilities.validateForm() == false) {
            return;
        }

        Intalio.Internal.Utilities.deactivateButtons();
        Intalio.Internal.Utilities.showLoading();

        try {
            if (window.intalioPreStart) {
                var retval = window.intalioPreStart();
                if (retval != null) {
                    Intalio.Internal.Utilities.hideLoading();
                    Intalio.Internal.Utilities.activateButtons();   
                    Intalio.Internal.Utilities.displayError(retval);                 
                    return;
                }
            } 
                    
            Intalio.Internal.Communication.sendRequest("IntalioInternal_TaskManagementServicesMapping_xml",
                "initProcess", tms.onInitProcessSuccess, Intalio.Internal.Utilities.onTimeOut);            
        } catch (e) {
            Intalio.Internal.Utilities.hideLoading();
            Intalio.Internal.Utilities.activateButtons();
            Intalio.Internal.Utilities.displayException(e);
        }
    };

    tms.onInitProcessSuccess = function(event) {
        Intalio.Internal.Utilities.hideLoading();
        Intalio.Internal.Utilities.hideForm();
        
        var response = event.target.getResponseXML();
        jsx3.log("initProcess response: " + Intalio.Internal.Utilities.tidy(response)); 
        var inDoc = Intalio.Internal.Communication.getPayload(response);        
        
        if (inDoc == null) {
            Intalio.Internal.Utilities.activateButtons(); 
            return;
        }        
        
        Intalio.Internal.Utilities.checkChained(inDoc);
        
        jsx3.log("initProcess succeeded.");
    };

    //////////////////////
    // setOutput (save) //
    //////////////////////
    tms.callSetOutput = function() {
        Intalio.Internal.Utilities.deactivateButtons();
        Intalio.Internal.Utilities.showLoading();
            
        try {
            if (window.intalioPreSave) {
                var retval = window.intalioPreSave();
                if (retval != null) {
                    Intalio.Internal.Utilities.hideLoading();
                    Intalio.Internal.Utilities.activateButtons();   
                    Intalio.Internal.Utilities.displayError(retval);                 
                    return;
                }
            } 
                    
            Intalio.Internal.Communication.sendRequest("IntalioInternal_TaskManagementServicesMapping_xml",
                "setOutput", tms.onSetOutputSuccess, Intalio.Internal.Utilities.onTimeOut);             
        } catch (e) {
            Intalio.Internal.Utilities.hideLoading();
            Intalio.Internal.Utilities.activateButtons();
            Intalio.Internal.Utilities.displayException(e);            
        }
    };

    tms.onSetOutputSuccess = function(event) {
        Intalio.Internal.Utilities.hideLoading();
        Intalio.Internal.Utilities.activateButtons();
            
        var response = event.target.getResponseXML();
        jsx3.log("setOutput response: " + Intalio.Internal.Utilities.tidy(response));         
        var inDoc = Intalio.Internal.Communication.getPayload(response);
        
        if (inDoc == null) {
            Intalio.Internal.Utilities.activateButtons(); 
            return;
        }
        
        Intalio.Internal.Utilities.showSuccess("The task was successfully saved.", true);
        jsx3.log("setOutput succeeded.");
    };

    ////////////////////////
    // complete (dismiss) //
    ////////////////////////
    tms.callComplete = function() {
        Intalio.Internal.Utilities.deactivateButtons();
        Intalio.Internal.Utilities.showLoading();
    
        try {        
            if (window.intalioPreDismiss) {
                var retval = window.intalioPreDismiss();
                if (retval != null) {
                    Intalio.Internal.Utilities.hideLoading();
                    Intalio.Internal.Utilities.activateButtons();   
                    Intalio.Internal.Utilities.displayError(retval);                 
                    return;
                }
            } 
                    
            Intalio.Internal.Communication.sendRequest("IntalioInternal_TaskManagementServicesMapping_xml",
                "complete", tms.onCompleteSuccess, Intalio.Internal.Utilities.onTimeOut); 
        } catch (e) {
            Intalio.Internal.Utilities.hideLoading();
            Intalio.Internal.Utilities.activateButtons();
            Intalio.Internal.Utilities.displayException(e);           
        }     
    };

    tms.onCompleteSuccess = function(event) {
        Intalio.Internal.Utilities.hideLoading();
        Intalio.Internal.Utilities.hideForm();
        
        var response = event.target.getResponseXML();
        jsx3.log("complete response: " + Intalio.Internal.Utilities.tidy(response));         
        var inDoc = Intalio.Internal.Communication.getPayload(response);
        
        if (inDoc == null) {
            Intalio.Internal.Utilities.activateButtons(); 
            return;
        }        
        
        //Intalio.Internal.Utilities.showSuccess("The notification was successfully dismissed.");
        document.location.href = "/ui-fw/script/empty.jsp";
        jsx3.log("complete succeeded.");
    };

    ////////////////////
    // getAttachments //
    ////////////////////
    tms.callGetAttachments = function() {
        try {
            Intalio.Internal.Communication.sendRequest("IntalioInternal_TaskManagementServicesMapping_xml",
                "getAttachments", tms.onGetAttachmentsSuccess, Intalio.Internal.Utilities.onTimeOut); 
        } catch (e) {
            Intalio.Internal.Utilities.displayException(e); 
        }
    };

    tms.onGetAttachmentsSuccess = function(event) {
        var response = event.target.getResponseXML();
        jsx3.log("getAttachments response: " + Intalio.Internal.Utilities.tidy(response));         
        var inDoc = Intalio.Internal.Communication.getPayload(response);
        
        if (inDoc == null) {
            return;
        }
        
        try {
            // create a doc from the node
            var mapDoc = new jsx3.xml.Document();
            mapDoc.loadXML(inDoc);
            
            // invoke the inbound mapping
            var mapService = Intalio.Internal.Utilities.SERVER.loadResource("IntalioInternal_TaskManagementServicesMapping_xml");
            mapService.setOperation("getAttachments");
            mapService.setInboundDocument(mapDoc);
            mapService.doInboundMap();        
        } catch (e) {
            Intalio.Internal.Utilities.displayException(e);                
            return;
        }        

        jsx3.log("getAttachments succeeded.");
        
        // show the attachments list
        Intalio.Internal.Attachments.showAttachments();
    };

    //////////////////////
    // removeAttachment //
    //////////////////////
    tms.callRemoveAttachment = function() {
        try {
            Intalio.Internal.Communication.sendRequest("IntalioInternal_TaskManagementServicesMapping_xml",
                "removeAttachment", tms.onRemoveAttachmentSuccess, Intalio.Internal.Utilities.onTimeOut);   
        } catch (e) {
            Intalio.Internal.Utilities.displayException(e); 
        }        
    };

    tms.onRemoveAttachmentSuccess = function(event) {
        var response = event.target.getResponseXML();
        jsx3.log("removeAttachment response: " + Intalio.Internal.Utilities.tidy(response));         
        var inDoc = Intalio.Internal.Communication.getPayload(response);
        
        if (inDoc == null) {
            return;
        }
        
        jsx3.log("removeAttachment succeeded.");
        
        // refresh the attachments list
        tms.callGetAttachments();
    };
  }
);
// end package

/////////////////////////////
// TaskManagerProcess WSDL //
/////////////////////////////
jsx3.lang.Package.definePackage(
  "Intalio.Internal.TMP",
  function(tmp) {
    
    ///////////////////////
    // claimTask (claim) //
    ///////////////////////
    tmp.callClaimTask = function() {
        Intalio.Internal.Utilities.deactivateButtons();
        Intalio.Internal.Utilities.showLoading();
    
        try {        
            if (window.intalioPreClaim) {
                var retval = window.intalioPreClaim();
                if (retval != null) {
                    Intalio.Internal.Utilities.hideLoading();
                    Intalio.Internal.Utilities.activateButtons();   
                    Intalio.Internal.Utilities.displayError(retval);                 
                    return;
                }
            } 
                    
            Intalio.Internal.Communication.sendRequest("IntalioInternal_TaskManagerProcessMapping_xml",
                "claimTask", tmp.onClaimTaskSuccess, Intalio.Internal.Utilities.onTimeOut);              
        } catch (e) {
            Intalio.Internal.Utilities.hideLoading();
            Intalio.Internal.Utilities.activateButtons();
            Intalio.Internal.Utilities.displayException(e);            
        }        
    };

    tmp.onClaimTaskSuccess = function(event) {
        Intalio.Internal.Utilities.hideLoading();
        Intalio.Internal.Utilities.activateButtons();  
        
        var response = event.target.getResponseXML();
        jsx3.log("claimTask response: " + Intalio.Internal.Utilities.tidy(response));         
        var inDoc = Intalio.Internal.Communication.getPayload(response);
        
        if (inDoc == null) {
            Intalio.Internal.Utilities.activateButtons();
            return;
        }        
        
        Intalio.Internal.Utilities.deactivateButtons();
        Intalio.Internal.Utilities.setTaskState("CLAIMED");
        Intalio.Internal.Utilities.activateButtons();
        Intalio.Internal.Utilities.showSuccess("The task was successfully claimed.", true);
        jsx3.log("claimTask succeeded.");
    };

    /////////////////////////
    // revokeTask (revoke) //
    /////////////////////////
    tmp.callRevokeTask = function() {
        Intalio.Internal.Utilities.deactivateButtons();
        Intalio.Internal.Utilities.showLoading();
            
        try {
            if (window.intalioPreRevoke) {
                var retval = window.intalioPreRevoke();
                if (retval != null) {
                    Intalio.Internal.Utilities.hideLoading();
                    Intalio.Internal.Utilities.activateButtons();   
                    Intalio.Internal.Utilities.displayError(retval);                 
                    return;
                }
            } 
                    
            Intalio.Internal.Communication.sendRequest("IntalioInternal_TaskManagerProcessMapping_xml",
                "revokeTask", tmp.onRevokeTaskSuccess, Intalio.Internal.Utilities.onTimeOut);            
        } catch (e) {
            Intalio.Internal.Utilities.hideLoading();
            Intalio.Internal.Utilities.activateButtons();
            Intalio.Internal.Utilities.displayException(e);            
        }        
    };

    tmp.onRevokeTaskSuccess = function(event) {
        Intalio.Internal.Utilities.hideLoading();
        Intalio.Internal.Utilities.activateButtons();  
        
        var response = event.target.getResponseXML();
        jsx3.log("revokeTask response: " + Intalio.Internal.Utilities.tidy(response));         
        var inDoc = Intalio.Internal.Communication.getPayload(response);
        
        if (inDoc == null) {
            Intalio.Internal.Utilities.activateButtons(); 
            return;
        }        
        
        Intalio.Internal.Utilities.deactivateButtons();
        Intalio.Internal.Utilities.setTaskState("READY");
        Intalio.Internal.Utilities.activateButtons();
        Intalio.Internal.Utilities.showSuccess("The task was successfully revoked.", true);
        jsx3.log("revokeTask succeeded.");
    };

    /////////////////////////////
    // completeTask (complete) //
    /////////////////////////////
    tmp.callCompleteTask = function() {
        // make sure the form is complete
        if (Intalio.Internal.Utilities.validateForm() == false) {
            return;
        }

        Intalio.Internal.Utilities.deactivateButtons();
        Intalio.Internal.Utilities.showLoading();
        
        try {
            if (window.intalioPreComplete) {
                var retval = window.intalioPreComplete();
                if (retval != null) {
                    Intalio.Internal.Utilities.hideLoading();
                    Intalio.Internal.Utilities.activateButtons();   
                    Intalio.Internal.Utilities.displayError(retval);                 
                    return;
                }
            }
               
            Intalio.Internal.Communication.sendRequest("IntalioInternal_TaskManagerProcessMapping_xml",
                "completeTask", tmp.onCompleteTaskSuccess, Intalio.Internal.Utilities.onTimeOut);             
        } catch (e) {
            Intalio.Internal.Utilities.hideLoading();
            Intalio.Internal.Utilities.activateButtons();
            Intalio.Internal.Utilities.displayException(e);            
        }        
    };
    
    tmp.onCompleteTaskSuccess = function(event) {
        Intalio.Internal.Utilities.hideLoading();
        Intalio.Internal.Utilities.hideForm();
        
        var response = event.target.getResponseXML();
        jsx3.log("completeTask response: " + Intalio.Internal.Utilities.tidy(response));         
        var inDoc = Intalio.Internal.Communication.getPayload(response);
        
        if (inDoc == null) {
            Intalio.Internal.Utilities.activateButtons(); 
            return;
        }
        
        Intalio.Internal.Utilities.checkChained(inDoc);
        
        jsx3.log("completeTask succeeded.");
    };
  }
);
// end package

////////////////////////////////
// TaskAttachmentService WSDL //
////////////////////////////////
jsx3.lang.Package.definePackage(
  "Intalio.Internal.TAS",
  function(tas) {
    
    ////////////
    // delete //
    ////////////
    tas.callDelete = function(attachmentUrl) {
        try {
            Intalio.Internal.Communication.sendRequest("IntalioInternal_TASMapping_xml",
                "delete", tas.onDeleteSuccess, Intalio.Internal.Utilities.onTimeOut);   
        } catch (e) {
            Intalio.Internal.Utilities.displayException(e); 
        }        
    };

    tas.onDeleteSuccess = function(event) {
        var response = event.target.getResponseXML();
        jsx3.log("delete response: " + Intalio.Internal.Utilities.tidy(response));         
        var inDoc = Intalio.Internal.Communication.getPayload(response);
        
        if (inDoc == null) {
            return;
        }

        jsx3.log("delete succeeded.");
    };
  }
);
// end package

/////////////////
// Attachments //
/////////////////
jsx3.lang.Package.definePackage(
  "Intalio.Internal.Attachments",
  function(attach) {
  
    attach.showAttachments = function() {
        // the matrix is already populated, but we still need to calculate the proper block height
        var matrix = Intalio.Internal.Utilities.SERVER.getJSXByName("IntalioInternal_AttachmentsMatrix");
        if (matrix == null) return;
        
        var rows = matrix.getXML().getChildNodes().getLength();
        var rowHeight = matrix.getRowHeight(jsx3.gui.Matrix.DEFAULT_ROW_HEIGHT);
        var headerHeight = matrix.getHeaderHeight(jsx3.gui.Matrix.DEFAULT_HEADER_HEIGHT);        
        var height = (rows * rowHeight) + headerHeight;
        
        var extra = 70;
        if (document.IntalioInternal_AddAttachmentsForm.attachmentText.style.cssText == "") {
            extra = extra + 90;
        }
        
        attach.setAttachmentsMatrixBlockHeight(height);
        attach.setAttachmentsBlockHeight(height + extra);
        matrix.repaint();
    };
  
    attach.displayAttachmentsImageBlock = function(display) {
        var block = Intalio.Internal.Utilities.SERVER.getJSXByName("IntalioInternal_AttachmentsImageBlock");
        if (block == null) return;
        
        if (display) {
            block.setDisplay(jsx3.gui.Block.DISPLAYBLOCK, true);
        } else {
            block.setDisplay(jsx3.gui.Block.DISPLAYNONE, true);
        }
    };

    // called when the user clicks the paper click image on the form
    attach.displayAttachmentsBlock = function(display) {
        var image = Intalio.Internal.Utilities.SERVER.getJSXByName("IntalioInternal_CloseImage");
        if (image == null) return;

        var block = Intalio.Internal.Utilities.SERVER.getJSXByName("IntalioInternal_AttachmentsBlock");
        if (block == null) return;

        if (display) {
            image.setDisplay(jsx3.gui.Block.DISPLAYBLOCK, true);
            block.setDisplay(jsx3.gui.Block.DISPLAYBLOCK, true);
            attach.populateAttachmentsForm();
        } else {
            image.setDisplay(jsx3.gui.Block.DISPLAYNONE, true);
            block.setDisplay(jsx3.gui.Block.DISPLAYNONE, true);            
        }
    };

    // calling this will ultimately load the attachment list into the matrix
    attach.populateAttachmentsForm = function() {
        var label = Intalio.Internal.Utilities.SERVER.getJSXByName("IntalioInternal_AttachmentsFormLabel");
        if (label == null) return;

        // this is the html we want to display (its a form)
        try {
            var doc = Intalio.Internal.Utilities.SERVER.loadResource("IntalioInternal_AttachmentsForm_xml");
            var docRoot = doc.getRootNode();
            label.setText(docRoot, true);
        
            var form = document.IntalioInternal_AddAttachmentsForm;
            form.taskId.value = Intalio.Internal.Utilities.getTaskId();
            form.participantToken.value = Intalio.Internal.Utilities.getParticipantToken();
            form.authorizedUsers.value = Intalio.Internal.Utilities.getUser();
            form.attachmentText.value = "";
        } catch (e) {
            Intalio.Internal.Utilities.displayException(e); 
        }        
    };
    
    attach.setAttachmentsBlockHeight = function(height) {
        var block = Intalio.Internal.Utilities.SERVER.getJSXByName("IntalioInternal_AttachmentsBlock");
        block.setHeight(height, true);
    };

    attach.setAttachmentsMatrixBlockHeight = function(height) {
        var block = Intalio.Internal.Utilities.SERVER.getJSXByName("IntalioInternal_AttachmentsMatrixBlock");
        block.setHeight(height, true);
    };
    
    attach.addAttachment = function() {
        document.IntalioInternal_AddAttachmentsForm.submit();
        
        // clear out the attachment form fields
        document.IntalioInternal_AddAttachmentsForm.attachmentName.value = "";
        document.IntalioInternal_AddAttachmentsForm.attachmentFile.value = "";
        document.IntalioInternal_AddAttachmentsForm.attachmentText.value = "";
    }; 

    attach.removeAttachment = function(url) {
        var urlObj = Intalio.Internal.Utilities.SERVER.getJSXByName("IntalioInternal_AttachmentsUrl");
        if (urlObj == null) return;
        urlObj.setText(url);
        
        Intalio.Internal.TAS.callDelete();
        Intalio.Internal.TMS.callRemoveAttachment();
    };
    
    attach.updateCDFNode = function(context) {
        var url = context.getAttribute("IntalioInternal_PayloadUrl");
        var href = "<img src='/gi/files/images/remove.gif' border='0' " + 
                   "  onClick='Intalio.Internal.Attachments.removeAttachment(\"" + url + "\");' />"; 
        context.setAttribute("IntalioInternal_RemoveImage", href); 
        
        attach.setAttributeHref(url, context, "Title");
        attach.setAttributeHref(url, context, "MimeType");
        attach.setAttributeHref(url, context, "CreationDate");
    };
    
    attach.setAttributeHref = function(url, context, attr) {
        var fullAttr = "IntalioInternal_" + attr;
        var text = context.getAttribute(fullAttr);
        var href = "<a href='" + url + "' target='_blank' style='text-decoration: none; color: #581C90;'>" + text + "</a>";
        context.setAttribute(fullAttr, href);        
    };
    
    attach.displayTextInput = function() {
        var file = document.IntalioInternal_AddAttachmentsForm.attachmentFile;
        var text = document.IntalioInternal_AddAttachmentsForm.attachmentText;
        
        if (text.style.cssText == "") {
            text.style.cssText = "display: none;";
            file.style.cssText = "";
        } else {
            text.style.cssText = "";
            file.style.cssText = "display: none;";        
        }

        attach.showAttachments();        
    };
  }
);
// end package

///////////////
// Utilities //
///////////////
jsx3.lang.Package.definePackage(
  "Intalio.Internal.Utilities",
  function(util) {

    util.SERVER = HILmanagement;
    util.MAPPING = "IntalioInternal_FormModelMapping_xml";
    
    util.GET_TASK_SUCCESS = "get-task-success";
    util.GET_TASK_TIMEOUT = "get-task-timeout";    
    
    // get a URL parameter 
    util.getRequestParameter = function(key) {
        var uri = new jsx3.net.URI(window.location.href);
        var val = uri.getQueryParam(key);
        return val;
    };    

    // get the task id
    util.getTaskId = function() {
        return util.getRequestParameter("id");
    };

    // get the participant token
    util.getParticipantToken = function() {
        return util.getRequestParameter("token");
    };

    // get the user
    util.getUser = function() {
        return util.getRequestParameter("user");
    };
    
    // get the type
    util.getType = function() {
        return util.getRequestParameter("type");
    };    
    
    // get the form url
    util.getFormUrl = function() {
        return util.getRequestParameter("url");
    };       

    util.appendOutboundFormModelNode = function(node) {
        try {
            var service = util.SERVER.loadResource(util.MAPPING);
            service.setOperationName("");
                
            var svcMsg = service.getServiceMessage();
            jsx3.log("Outbound mapping node: " + Intalio.Internal.Utilities.tidy(svcMsg));
        
            node.appendChild(svcMsg.cloneNode(true));
        } catch (e) {
            Intalio.Internal.Utilities.displayException(e); 
        }        
    };
    
    // displays buttons based on task type, but doesnt enable them
    util.processTaskType = function(taskType) {
        if (taskType == null) return;
      
        if (taskType == "PIPATask") {
            util.SERVER.getJSXByName("IntalioInternal_StartButton").
                setDisplay(jsx3.gui.Block.DISPLAYBLOCK, true);
        }
        else if (taskType == "PATask") {
            util.SERVER.getJSXByName("IntalioInternal_SaveButton").
                setDisplay(jsx3.gui.Block.DISPLAYBLOCK, true);
            util.SERVER.getJSXByName("IntalioInternal_ClaimButton").
                setDisplay(jsx3.gui.Block.DISPLAYBLOCK, true);
            util.SERVER.getJSXByName("IntalioInternal_RevokeButton").
                setDisplay(jsx3.gui.Block.DISPLAYBLOCK, true);
            util.SERVER.getJSXByName("IntalioInternal_CompleteButton").
                setDisplay(jsx3.gui.Block.DISPLAYBLOCK, true);
            
            // display the image block
            Intalio.Internal.Attachments.displayAttachmentsImageBlock(true);                
        }
        else if (taskType == "Notification") {
            util.SERVER.getJSXByName("IntalioInternal_DismissButton").
                setDisplay(jsx3.gui.Block.DISPLAYBLOCK, true);            
        }
    };
    
    util.activateButtons = function() {
        var taskState = util.getTaskState();
        var taskType = util.getTaskType();
        
        if (taskType == "NOTIFICATION") {
            if (taskState == "READY") {
                if (util.getAuthorizedAction("dismiss")) {
                    util.SERVER.getJSXByName("IntalioInternal_DismissButton").setEnabled(true, true);
                }
            }
        } 
        else {        
            if (taskState == "NONE") {
                util.SERVER.getJSXByName("IntalioInternal_StartButton").setEnabled(true, true);
            }
            else {
                if (util.getAuthorizedAction("save")) {
                    util.SERVER.getJSXByName("IntalioInternal_SaveButton").setEnabled(true, true);
                }

                if (util.getAuthorizedAction("complete")) {
                    util.SERVER.getJSXByName("IntalioInternal_CompleteButton").setEnabled(true, true);
                }            
        
                if (taskState == "READY") {
                    if (util.getAuthorizedAction("claim")) {
                        util.SERVER.getJSXByName("IntalioInternal_ClaimButton").setEnabled(true, true);
                    }
                }
        
                if (taskState == "CLAIMED") {
                    if (util.getAuthorizedAction("revoke")) {
                        util.SERVER.getJSXByName("IntalioInternal_RevokeButton").setEnabled(true, true);
                    }
                }
            }
        }
    };
    
    util.deactivateButtons = function() {
        util.SERVER.getJSXByName("IntalioInternal_StartButton").setEnabled(false, true);
        util.SERVER.getJSXByName("IntalioInternal_SaveButton").setEnabled(false, true);
        util.SERVER.getJSXByName("IntalioInternal_ClaimButton").setEnabled(false, true);
        util.SERVER.getJSXByName("IntalioInternal_CompleteButton").setEnabled(false, true);            
        util.SERVER.getJSXByName("IntalioInternal_RevokeButton").setEnabled(false, true);
        util.SERVER.getJSXByName("IntalioInternal_DismissButton").setEnabled(false, true);
    };    
    
    // this is typically called only once, when a page first loads
    // this does not return a value or generate an alert
    util.updateValidationStatus = function() {
        var root = util.SERVER.getRootBlock();
        var children = root.getDescendantsOfType("jsx3.gui.Form", false);

        if (children != null) {        
            for (var i = 0; i < children.length; i++) {
                var element = children[i];
                
                // certain form elements do not implement the doValidate() method,
                // they will throw an exception
                try {
                    if (element.doValidate()) {
                        var image = util.getValidateImage(element);
                        if (image != null) {
                            image.setDisplay(jsx3.gui.Block.DISPLAYNONE, true);            
                        }
                    }
                } catch (Exception) {
                    // nothing to do    
                }
            }
        }    
    };
    
    // iterate over the form elements and validate them
    // returns true if all are valid, false otherwise (and an alert)
    util.validateForm = function() {
        var root = util.SERVER.getRootBlock();
        var children = root.getDescendantsOfType("jsx3.gui.Form", false);
        
        if (children != null) {
            var error = null;
            for (var i = 0; i < children.length; i++) {
                var element = children[i];
                
                // certain form elements do not implement the doValidate() method,
                // they will throw an exception
                try {
                    if (element.doValidate() == jsx3.gui.Form.STATEINVALID) {
                        var txt = null;
                        var field = element.getAncestorOfType("com.intalio.ria.Field");
                        if (field != null) {
                            txt = field.getErrorMessageText();
                            if (txt != null) {
                                txt = util.trim(txt);
                                if (txt.length == 0) {
                                    txt = null;
                                }
                            }
                        }
                        
                        if (txt == null) {
                            txt = "Incomplete field: " + element.getName();  
                        }
                        
                        txt = "<li id='IntalioInternal_error'>" + txt + "</li>";
                        
                        if (error == null) {
                            error = txt;
                        } else {
                            error = error + txt;   
                        }
                    }
                } catch (Exception) {
                    // nothing to do    
                }
            }
            
            if (error != null) {
                util.displayError(error);   
                return false;
            }
        }
        
        return true;
    };
    
    // initialize the form
    util.initApp = function() {
        var taskType = util.getType();
        jsx3.log("task type = " + taskType);
        util.processTaskType(taskType);
        Intalio.Internal.TMS.callGetTask();
    };

    util.checkChained = function(inDoc) {
        var nextId = "";
        var nextUrl = "";
        var nextIdNode = inDoc.selectSingleNode("//*[local-name()='nextTaskId']");
        if (nextIdNode != null) {
            nextId = nextIdNode.getValue();
            if (nextId == null) nextId = "";
            nextId = nextId.trim();            
        }
        
        var nextUrlNode = inDoc.selectSingleNode("//*[local-name()='nextTaskURL']");
        if (nextUrlNode != null) {
            nextUrl = nextUrlNode.getValue();
            if (nextUrl == null) nextUrl = "";
            nextUrl = nextUrl.trim();
        }

        if (nextId != "" && nextUrl != "") {
            var fullUrl = nextUrl + 
                "?id=" + nextId + 
                "&type=PATask" + 
                "&url=" + escape(nextUrl) +  
                "&token=" + util.getParticipantToken() + 
                "&user=" + escape(util.getUser());
                
            document.location.href = fullUrl;
        } else {
            document.location.href = "/ui-fw/script/empty.jsp";
        }        
    };    
    
    // set a color pickers value
    util.setColorPickerValue = function(colorPicker, value) {
        var valueObj = util.getValueObject(colorPicker);
        if (valueObj == null || value == null) return;
        
        var str = value.toString(16).toUpperCase();
        while (str.length < 6) {
            str = "0" + str;
        }
        
        var rgb = str.substring(0, 2) + " " + 
                  str.substring(2, 4) + " " + 
                  str.substring(4, 6);
        valueObj.setText(rgb, true);
    };
    
    // set a sliders value
    util.setSliderValue = function(slider, value) {        
        var valueObj = util.getValueObject(slider);
        if (valueObj == null || value == null) return;
        
        valueObj.setText(value.toFixed(2), true);
    };
    
    // check the validation of a form element
    util.validateElement = function(element) {
        var image = util.getValidateImage(element);
        if (image == null) return;

        // radio button group is always valid once selected         
        jsx3.require("jsx3.gui.RadioButton");
        if (element instanceof jsx3.gui.RadioButton) {
            image.setDisplay(jsx3.gui.Block.DISPLAYNONE, true);
            return;
        }
        
        if (element.doValidate()) {
            image.setDisplay(jsx3.gui.Block.DISPLAYNONE, true);
        }
        else {
            image.setDisplay(jsx3.gui.Block.DISPLAYBLOCK, true);
        }
    };
    
    // find an associated validation image, if any
    util.getValidateImage = function(element) {
        var name = util.getElementName(element);       
        if (name == null) return null;
        
        jsx3.require("jsx3.gui.RadioButton");
        if (element instanceof jsx3.gui.RadioButton) {
            name = element.getGroupName();
            if (name == null) return null;
        }
        
        var retval = util.SERVER.getJSXByName(name + "-validate");
        if (retval != null) {
            jsx3.require("jsx3.gui.Image");
            if (retval instanceof jsx3.gui.Image) {
                return retval;
            }
        }
        
        return null;                
    };
    
    // find an associated value object, if any
    util.getValueObject = function(element) {
        var name = util.getElementName(element);       
        if (name == null) return null;
        
        var retval = util.SERVER.getJSXByName(name + "-value");
        if (retval == null) {
            retval = util.SERVER.getJSXByName(name + "-value-nomap");
        }
        
        return retval;
    };

    // find the name of the given element    
    util.getElementName = function(element) {
        if (element == null) return null;
        
        var name = element.getName();        
        if (name == null) return null;
        
        // remove '-nomap' if it ends with it
        var idx = name.indexOf("-nomap");
        if (idx > 0) {
            if (idx == name.length - 6) {
                name = name.substring(0, idx);
            }
        }
        
        return name;        
    };
    
    util.getTaskType = function() {
        var taskType = null;
        var inDoc = util.getGetTaskDocument();
        if (inDoc != null) {
            var taskTypeNode = inDoc.selectSingleNode("//tms:metadata/tms:taskType", Intalio.Internal.TMS.TMS_NS);
            if (taskTypeNode != null) {
                taskType = taskTypeNode.getValue(); 
            }
        }
        
        return taskType;       
    };
    
    util.getTaskState = function() {
        var taskState = "NONE";
        var inDoc = util.getGetTaskDocument();
        if (inDoc != null) {
            var taskStateNode = inDoc.selectSingleNode("//tms:metadata/tms:taskState", Intalio.Internal.TMS.TMS_NS);
            if (taskStateNode != null) {
                taskState = taskStateNode.getValue();
            }
        }
        
        return taskState;
    };

    util.setTaskState = function(taskState) {
        var inDoc = util.getGetTaskDocument();
        if (inDoc != null) {
            var taskStateNode = inDoc.selectSingleNode("//tms:metadata/tms:taskState", Intalio.Internal.TMS.TMS_NS);
            if (taskStateNode != null) {
                taskStateNode.setValue(taskState);
            }
        }          
    };
    
    util.getAuthorizedAction = function(action) {
        var auth = false;
        
        var inDoc = util.getGetTaskDocument();
        if (inDoc != null) {
            var authNode = inDoc.selectSingleNode("//tms:metadata/tms:" + action + "Action/tms:authorized", Intalio.Internal.TMS.TMS_NS);
            // if node doesn't exist then authorized
            if (authNode == null) {
                auth = true;
            } else {
                // if node exists and is not 'false' then authorized
                if (authNode.getValue().toLowerCase() != "false") {
                    auth = true;    
                }
            }
        }   
        
        return auth;
    };    
    
    util.setGetTaskDocument = function(inDoc) {
        window.IntalioInternal_getTaskDoc = inDoc;
    };
    
    util.getGetTaskDocument = function() {
        return IntalioInternal_getTaskDoc;
    };
    
    util.onError = function(event) {
        util.hideLoading();
        
        var opname = event.target.getOperationName();
        if (opname !== "getTask") {
            util.activateButtons();            
        }
        
        var status = event.target.getRequest().getStatus();
        var msg = "Operation '" + opname + "' failed.  The HTTP Status code is: " + status;
        util.displayError(msg);        
    };

    util.onInvalid = function(event) {
        util.hideLoading();
        
        var opname = event.target.getOperationName();
        if (opname !== "getTask") {
            util.activateButtons();            
        }
        
        var msg = "Operation '" + opname + "' failed.  The message failed validation.";
        util.displayError(msg);        
    };
    
    util.onTimeOut = function(event) {
        util.hideLoading();
            
        var opname = event.target.getOperationName();
        if (opname !== "getTask") {
            util.activateButtons();            
        }
        
        var msg = "Operation '" + opname + "' failed.  The operation timed out.";
        util.displayError(msg);        
    };    
    
    util.closeError = function() {
        document.getElementById("IntalioInternal_error").style.visibility = "hidden";     
        util.undimForm();
    };
    
    util.displayError = function(msg, isException) {
        util.dimForm();
        jsx3.log(msg);
        
        if (isException === true) {
            msg = "<pre id='IntalioInternal_error'>" + msg + "</pre>";                  
        }
        
        document.getElementById("IntalioInternal_error_text").innerHTML = msg;
        
        var div = document.getElementById("IntalioInternal_error");
        util.centerDiv(div);
        div.style.visibility = "visible"; 
    };
    
    util.displayException = function(e) {
        var msg = "Exception occurred logging exception.";
        
        try {
            msg = jsx3.NativeError.wrap(e).printStackTrace(); 
        } catch (ex) {;}
            
        util.displayError(msg, true);
    };
    
    util.showLoading = function() {
        util.hideSuccess();
        util.dimForm();
        var div = document.getElementById("IntalioInternal_loading");
        util.centerDiv(div);
        div.style.visibility = "visible";
    };
    
    util.hideLoading = function() {
        document.getElementById("IntalioInternal_loading").style.visibility = "hidden";
        util.undimForm();
    };
    
    util.dimForm = function() {
        util.hideForm();
        //var mainDiv = document.getElementById("IntalioInternal_jsxmain");
        //var div = document.getElementById("IntalioInternal_filter");
        //div.style.height = mainDiv.offsetHeight;
        //div.style.width = mainDiv.offsetWidth;
        //div.style.visibility = "visible";
    };

    util.undimForm = function() {
        util.showForm();
        //var div = document.getElementById("IntalioInternal_filter");
        //div.style.visibility = "hidden";
    };
    
    util.hideForm = function() {
        window.scroll(0,0);
        document.getElementById("IntalioInternal_jsxmain").style.visibility = "hidden";
    };

    util.showForm = function() {
        document.getElementById("IntalioInternal_jsxmain").style.visibility = "visible";
    };    
    
    util.showSuccess = function(msg, autoClose) {
        clearTimeout(window.IntalioInternal_success_timer);
        
        var div = document.getElementById("IntalioInternal_success");
        div.innerHTML = msg;
        util.centerDiv(div);
        div.style.visibility = "visible";
        
        if (autoClose === true) {
            window.IntalioInternal_success_timer = setTimeout("Intalio.Internal.Utilities.hideSuccess()", 5000);
        }
    };
    
    util.hideSuccess = function() {
        document.getElementById("IntalioInternal_success").style.visibility = "hidden";
    };

    util.centerDiv = function(div) {
        var divWidth = document.getElementById("IntalioInternal_jsxmain").offsetWidth;
        var locX = (divWidth - div.offsetWidth) / 2;
        div.style.left = locX;
    };
    
    // simple XSL to tidy up the network in/out XML, mostly for logging 
    util.tidy = function(xmlDoc) {
        if (xmlDoc == null) return "null";
        
        var strXSLCacheId = "IntalioInternal_Tidy_xsl";
        var strXSLURL = util.SERVER.resolveURI("IntalioInternal/Tidy.xsl");
        var objXSL = util.SERVER.getCache().getOrOpenDocument(strXSLURL, strXSLCacheId);
 
        var objTemplate = new jsx3.xml.Template(objXSL); 
        return "\n" + objTemplate.transformToObject(xmlDoc) + "\n";
    };
    
    util.trim = function(str, chars) {
        return util.ltrim(util.rtrim(str, chars), chars);
    };

    util.ltrim = function(str, chars) {
        chars = chars || "\\s";
        return str.replace(new RegExp("^[" + chars + "]+", "g"), "");
    };

    util.rtrim = function(str, chars) {
        chars = chars || "\\s";
        return str.replace(new RegExp("[" + chars + "]+$", "g"), "");
    };    
    
    // form data sent to the server
    util.fixDateOutput = function(objDate) {
        if (objDate == null) {
            return "";
        }
        
        var date = objDate.getDate();
        if (date == null) {
            return "";
        }
        
        var stamp = date.valueOf();
        
        return util.fixDateOutputMatrix(stamp);
    };
    
    // form data received from the server
    util.fixDateInput = function(objDate, str) {
        if (objDate == null || str == null) {
            return;   
        }
        
        var stamp = util.fixDateInputMatrix(str);
        var date = new Date();
        date.setTime(stamp);
        
        objDate.setDate(date);
    };
    
    // form data sent to the server
    util.fixDateOutputMatrix = function(dateValue) {
        if (dateValue == null) {
            return "";   
        }
        
        var date = new Date();
        date.setTime(dateValue);
        
        var year = date.getFullYear();
        var month = parseInt(date.getMonth()) + 1;
        var day = parseInt(date.getDate());
        
        if (month < 10) {
            month = "0" + month;   
        }
        
        if (day < 10) {
            day = "0" + day;   
        }
        
        return year + "-" + month + "-" + day;
    };
    
    // form data received from the server
    util.fixDateInputMatrix = function(dateValue) {
        if (dateValue == null) {
            return 0;
        }
        
        var parts = dateValue.split("-");
        var year = parseInt(parts[0]);
        var month = parseInt(parts[1]) - 1;
        var day = parseInt(parts[2]);
        
        var date = new Date();
        date.setFullYear(year);
        date.setMonth(month);
        date.setDate(day);
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);
        date.setMilliseconds(0);
        
        return date.valueOf();
    };
    
    // form data sent to the server
    util.fixTimeOutput = function(objTime) {
        if (objTime == null) {
            return "";
        }
        
        var date = objTime.getDate();
        if (date == null) {
            return "";   
        }
        
        var stamp = date.valueOf();
        
        return util.fixTimeOutputMatrix(stamp);
    };

    // form data sent to the server
    util.fixTimeOutputMatrix = function(timeInt) {
        if (timeInt == null) {
            return "";   
        }
        
        var date = new Date();
        date.setTime(timeInt);
        
        var hour = date.getHours();
        var mins = date.getMinutes();
        var secs = date.getSeconds();
        var millis = date.getMilliseconds();
        
        if (hour < 10) {
            hour = "0" + hour;   
        }
        
        if (mins < 10) {
           mins = "0" + mins;   
        }
        
        if (secs < 10) {
           secs = "0" + secs;   
        }
      
        return hour + ":" + mins + ":" + secs + "." + millis;
    };
    
    // form data received from the server
    util.fixTimeInput = function(objTime, timeStr) {        
        if (objTime == null || timeStr == null) {
            return;   
        }

        var stamp = util.fixTimeInputMatrix(timeStr);
        var date = new Date();
        date.setTime(stamp);
        
        objTime.setDate(date);
    };
    
    // form data received from the server
    util.fixTimeInputMatrix = function(timeStr) {
        if (timeStr == null) {
            return 0;
        }
        
        var hour = 0;
        var mins = 0;
        var secs = 0;
        var millis = 0;
        
        var parts = timeStr.split(":");
        
        // hour
        if (parts.length > 0) {
            hour = parts[0];
        }
        
        // minutes
        if (parts.length > 1) {
            mins = parts[1];   
        }
        
        // seconds
        if (parts.length > 2) {
            secs = parts[2];
            var secParts = secs.split(".");
            if (secParts.length > 0) {
                secs = secParts[0];    
            }
            
            // millis
            if (secParts.length > 1) {
                millis = secParts[1];
            }            
        }
        
        var date = new Date();
        date.setHours(hour);
        date.setMinutes(mins);
        date.setSeconds(secs);
        date.setMilliseconds(millis);
        
        return date.valueOf();        
    };
  }
);
// end package

///////////////////
// Communication //
///////////////////
jsx3.lang.Package.definePackage(
  "Intalio.Internal.Communication",
  function(com) {
      
      com.sendRequest = function(mapping, operation, onSuccess, onTimeOut) {
          var path = com.getPath();
          if (path == null) {
              var msg = "Path not found.";
              Intalio.Internal.Utilities.displayError(msg);               
              return;    
          }
          
          var assembly = com.getAssembly(path);
          if (assembly == null) {
              var msg = "Assembly not found.";
              Intalio.Internal.Utilities.displayError(msg);
              return;    
          }

          var form = com.getForm(path);
          if (form == null) {
              var msg = "Form not found.";
              Intalio.Internal.Utilities.displayError(msg);
              return;
          }
          
          var service = Intalio.Internal.Utilities.SERVER.loadResource(mapping);
          service.setOperationName(operation);
          var message = service.getServiceMessage();
          jsx3.log(operation + " request: " + Intalio.Internal.Utilities.tidy(message));          
          message = "assembly=" + assembly + "&form=" + form + "&message=" + message;
          
          var request = new jsx3.net.Request();
          request.subscribe(jsx3.net.Request.EVENT_ON_RESPONSE, onSuccess);
          request.subscribe(jsx3.net.Request.EVENT_ON_TIMEOUT, onTimeOut);
          
          request.open("POST", "/gi/validation", true);
          request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");          
          request.send(message, 60000);
      };
      
      com.getPath = function() {
          var giAppPath = "/gi/apppath/";
          var url = Intalio.Internal.Utilities.getFormUrl();
          var uri = new jsx3.net.URI(url);
          var path = uri.getPath();
          
          if (path.indexOf(giAppPath) == 0 && path.length >= giAppPath.length) {
              return path.substr(giAppPath.length);
          }

          return null;
      };
      
      com.getAssembly = function(path) {
          var idx = path.indexOf("/");
          if (idx > 0) {
              return path.substring(0, idx);
          }
          
          return null;
      }
      
      com.getForm = function(path) {
          var idx_1 = path.indexOf("/");
          if (idx_1 > 0) {
              var idx_2 = path.indexOf("/IntalioInternal/", idx_1 + 1);
              if (idx_2 > idx_1 + 1) {
                  return path.substring(idx_1 + 1, idx_2);
              }
          }
          
          return null;
      };
      
      com.getPayload = function(response) {
          var payload = null;
          var invalid = false;
          
          var statusNode = response.selectSingleNode("/response/status");
          if (statusNode == null) {
              invalid = true;
          } else {
              var status = statusNode.getValue();
              if (status == "success") {
                  var node = response.selectSingleNode("/response/payload");
                  if (node == null) {
                      invalid = true;
                  } else {
                      payload = new jsx3.xml.Document();
                      payload.loadXML(node.getFirstChild());
                  }
              } else {
                  var messageNode = response.selectSingleNode("/response/message");
                  if (messageNode == null) {
                      invalid = true;
                  } else {
                      var msg = messageNode.getValue();
                      Intalio.Internal.Utilities.displayError(msg);
                  }
              }
          }
          
          if (invalid) {
              var msg = "Invalid response from server.";
              Intalio.Internal.Utilities.displayError(msg);              
          }
          
          return payload;
      };
  }
);
// end package

