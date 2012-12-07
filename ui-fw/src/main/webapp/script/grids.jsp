<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@page import="org.intalio.tempo.uiframework.Configuration"%>
<%@page import="org.intalio.tempo.security.ws.TokenClient"%>
<%@page import="org.intalio.tempo.web.ApplicationState"%>
<%@page import="org.intalio.tempo.web.User"%>

<script type="text/javascript">


    function toggleQtip(id) {
	var div = $('#' + id);
	hide(id);
	show(id);
    }

    function hide(id){
      var div = $('#' + id);
      if (div.data('visible')) {
	  div.qtip('hide');
	  div.data('visible', false);
      }
    }
    function show(id){
	var div = $('#' + id);
	var div1 = 'div1';
	var div2 = 'div2';
	if(id == div1){
	  hide(div2); 
	} else if(id == div2){
	  hide(div1); 
	}
	if (!div.data('visible')) {
	  div.qtip('show');
	  div.data('visible', true); 
	}
      }
    $(window).load(function(){
	$('*').live('click', function(){
	    hide('div1');
	    hide('div2');
	});
    });
    
    $(document).ready(function(){ 
	/*changes 05/12/2012---- start*/
	$("#userProfile")
        .button({
		icons: {
		secondary: "ui-icon-triangle-1-s"
	   }	
	})
	.click(function(){
		toggleQtip('div1');
		return false;
	});
	$("#options")
	    .button({
		icons: {
		    primary: "ui-icon-gear",
		    secondary: "ui-icon-triangle-1-s"
		},
		text: false
	    })
	    .click(function () {
	    toggleQtip('div2');
	    return false;
	});
	
	$('.io-footer').each(function () {
	  showMyTT($(this).attr('id'));
	});

	$('.io-footer-options').each(function () {
	  showMyTT($(this).attr('id'));
	});

	$("#btnLogout")
	  .button()
	  .click(function () {
	      submitActionToURL('login.htm', 'logOut');
	      return false;
	});
	$("#btnHelp")
	    .button()
	    .click(function () {
	    window.open("http://wiki.intalio.com", '_blank');
	    window.focus();
	    return false;
	});

	function showMyTT(id) {
	  if(id == "div1")
	  {
		$('#' + id).qtip({
		    content: $('.tooltipContent'),
		    position: {
		        my: 'top center',
		        at: 'bottom right',
		        adjust: {
		            x: -65,
			    y: 45
		        
		        },
			viewport: $(window),
		    },
		    show: false,
		    hide: false,
		});
	  }
	  else if(id == "div2")
	  {
		$('#' + id).qtip({
		    content: $('.optionContent'),
		    position: {
		        my: 'top center',
		        at: 'bottom right',
		        adjust: {
		            x: -165,
			    y:45
		        
		        },
			viewport: $(window),

		    },
		    show: false,
		    hide: false,
		});
	}
      }

	
	/*changes 05/12/2012---- end*/
	//added for new datepicker
	
	$(function() {
        $( "#fromdate" ).datepicker({minDate:0,dateFormat:'dd/mm/yy'});
        $( "#todate" ).datepicker({minDate:0,dateFormat:'dd/mm/yy'});
    });
	//end of new datepicker
	changeButtonText();		
	
    /*********************************************************************
    Load the dynamic JSP code that contains variables defined on the server 
    **********************************************************************/
    <% 
    Configuration conf = Configuration.getInstance();
    String tokenService = conf.getTokenClient().getEndpoint();
    boolean useToolbar = conf.isUseToolbarIcons().booleanValue();
	User currentUser = ApplicationState.getCurrentInstance(request).getCurrentUser();
	String[] taskIconSet = conf.getTaskIconSetByRole(currentUser.getRoles());
	String[] notiIconSet = conf.getNotificationIconSetByRole(currentUser.getRoles());
    %>
    
    /*********************************************************************
    Load the javascript variables on the client
    **********************************************************************/
    var speed = "fast";
    var currentUser = '<%= ((String)request.getAttribute("currentUser")).replace("\\", "\\\\")%>';
    var tokenService = '<%= tokenService %>';
    var tmsService = '<%=conf.getServiceEndpoint()%>';
    var tmpService = '<%=conf.getTMPEndpoint()%>';
    var rbacService = '<%= tokenService.substring(0, tokenService.indexOf("/TokenService"))+"/RBACQueryService" %>';
    var proxy = '/ui-fw/script/proxy.jsp';
    var widthFull = $(window).width()*0.99;
    var width = $(window).width()*0.90;
    var height = 0;
    var current = null;
    var vac_id;
    $.ajaxSetup({timeout: <%= conf.getAjaxTimeout() %>});
	var taskIconSet = new Array(<%=taskIconSet.length%>)
	<% for(int i=0;i<taskIconSet.length;i++) {%>
    	taskIconSet[<%=i%>] = '<%=taskIconSet[i]%>';
    <%}%>
	
	//For Vacation button
	taskIconSet[taskIconSet.length] = "Vacation";
	
	//For View All Task Button
	taskIconSet[taskIconSet.length] = "viewAllTask";
		
	var notiIconSet = new Array(<%=notiIconSet.length%>)
    <% for(int i=0;i<notiIconSet.length;i++) {%>
    	notiIconSet[<%=i%>] = '<%=notiIconSet[i]%>';
    <%}%>    
    /*********************************************************************
    Section to handle resizing of window, and recompute table size and
    display area
    **********************************************************************/

    /*
    Find the browser window size, and adapt to IE
    */
    if($.browser.msie){
      height = $(window).height() - 130;
    }else{
      height = $(window).height() - 130;
    }
    var height2 = height - 80;
    var needResize = false;
		
    /*
    Resize the application when the window is reloaded
    */
    $(window).resize(function() {
      if(navigator.appName != "Microsoft Internet Explorer") {
        var loc = window.frames['taskform'].location;
        try {
          if(loc.toString().indexOf("empty.jsp")>0) {
            location.href=location.href;
          } else {
            needResize = true;
          }
        } catch(err) {
          return;
        }
      }
    });


    /*
    Completely clear the content of the frame, where the forms are usually loaded
    */
    function clearFrame() {
        var loc = window.frames['taskform'].location;
        try {
            if(loc.toString().match("empty.jsp")!=null) return ;
        } catch(err) {
            $('#taskform').animate({height:"0px"},speed);
            window.open("/ui-fw/script/empty.jsp", "taskform");
        }

        $('#taskform').animate({height:"0px"},speed);
        window.open("/ui-fw/script/empty.jsp", "taskform");
    }
		
	/*********************************************************************
	Methods and variablles to handle Session timeout management
	**********************************************************************/
	var time = 0;
	var sessionTimeout = <c:out value="${sessionTimeout}"/>; // in minutes 
	var timeCount = 60000; // 1 minute 
	
	/*
	Method to reset the timer, and the timer notice text
	*/
    function resetTimer() {
        time = 0;
        $("#timer").text("");
    }
		
    /*
    Increase the elapsed time count for the current session, on the client side
    */ 
    $.timer(timeCount,function(timer) {
      if(time>=1) {
        var text = '<fmt:message key="org_intalio_uifw_session_inactivity_1"/>';
        text = text+" "+time+" "+'<fmt:message key="org_intalio_uifw_session_inactivity_2"/>';
        $("#timer").text(text).css('padding-right','225px');
      }
      time = time + 1;

      if ($("#connectionLost").is(":visible")) {timer.stop();}
      if(time > sessionTimeout) { log_me_out(); timer.stop();}
    });
    
    /*
		Erase the session, call logout on the server
    */
	function log_me_out() {
	   $.post("login.htm?actionName=logOut");
	   $("#sessionExpired").dialog('open');
	}
    
	/*
		Convert the icon name to flexigrid code
	*/
    function getToolbarIconsCodes(icons){
	    var iconsetCode = new Array(icons.length);
	    for(i = 0; i < icons.length; i++){
	        switch(icons[i]){
	        case "delete":
	            iconsetCode[i] = {name: '<fmt:message key="org_intalio_uifw_toolbar_button_delete"/>', bclass: 'delete', onpress : clickDelete};
	            break;
	        case "claim":
	            iconsetCode[i] = {name: '<fmt:message key="org_intalio_uifw_toolbar_button_claimrevoke"/>', bclass: 'claim', onpress : claimTask};
	            break;
	        case "reassign":
	        	iconsetCode[i] = {name: '<fmt:message key="org_intalio_uifw_toolbar_button_reassign"/>', bclass: 'reassign', onpress : clickReassign};
	            break;
	        case "update":
	        	iconsetCode[i] = {name: '<fmt:message key="org_intalio_uifw_toolbar_button_update"/>', bclass: 'update', onpress : clickUpdate};
	            break;
	        case "skip":
	        	iconsetCode[i] = {name: '<fmt:message key="org_intalio_uifw_toolbar_button_skip"/>', bclass: 'skip', onpress : skipTask};
	            break;
	        case "export":
	        	iconsetCode[i] = {name: '<fmt:message key="org_intalio_uifw_toolbar_button_export"/>', bclass: 'export', onpress : clickExportTasks};
	            break;
	        case "Vacation":
	        	iconsetCode[i] = {name: '<fmt:message key="org_intalio_uifw_toolbar_button_vacation"/>',  bclass: 'vacationDet', onpress : clickVacation};
	            break; 
	        case "viewAllTask":
	        	iconsetCode[i] = {name: '<fmt:message key="org_intalio_uifw_toolbar_button_view_all_tasks"/>',  bclass: 'viewAllTask', onpress : clickViewAllTasks};
	            break;      
	        }
	    }
	    return iconsetCode;
    }
    /*
    Define needed events for timer
    */
    $("html").mousemove(function(e){resetTimer();});
    $(this).click(function() {resetTimer();});
		

    /*********************************************************************
    Section to handle dialogs code
    *********************************************************************/
		
    /*
    Session expired dialog
    */
    $("#sessionExpired").dialog({
      bgiframe: false,
      autoOpen: false,
      resizable:false,
      height: 200,
      modal: true,		
      buttons: {
        '<fmt:message key="org_intalio_uifw_message.button.ok"/>': function() {location.reload(true);}
      },
      close: function() {location.reload(true);}
    });
    
    /*
    Connection lost dialog
    */
    $("#connectionLost").dialog({
      bgiframe: false,
      autoOpen: false,
      resizable:false,
      autoResize:true,
      height: 200,
      draggable:false,
      modal: true,		
      buttons: {'<fmt:message key="org_intalio_uifw_message.button.ok"/>': function() {location.reload(true);}},
      close: function() {location.reload(true);}
    });

    /*
    Export dialog
    */
    $("#exportdialog").dialog({
      bgiframe: false,
      autoOpen: false,
      resizable:false,
      autoResize:true,
      draggable:false,
      height: 300,
      modal: true,		
      buttons: {'<fmt:message key="org_intalio_uifw_message.button.ok"/>': function() {exportTasksAction();}}
    });
    /*
    	Vacation dialog
    */
    $("#vacation").dialog({
        bgiframe: false,
        autoOpen: false,
        resizable:false,
	autoResize:true,
	draggable:false,
        height: 350,
        width:500,
        modal: true,
        buttons: {'<fmt:message key="org_intalio_uifw_message.button.save"/>': function() {saveVacation();}},
        close: function() {
					$('#ui-datepicker-div').css("display","none");
			 }
      });
	/*
		Message Dialog
	*/
	$("#messageDialog").dialog({
        bgiframe: false,
        autoOpen: false,
        resizable:false,
	autoResize:true,
	draggable:false,
        height: 150,
        width:600,
        modal:true,
        buttons: {
				 Ok: function() {$(this).dialog('close');}
       }
      });	
	/*
		End Vaction Dialog	
	*/ 
	 $("#endVacDialog").dialog({
        bgiframe: false,
        autoOpen: false,
        resizable:false,
	autoResize:true,
	draggable:false,
        height: 190,
        width:600,
        buttons: {
				 End_Vacation: function(){
					 endVacation();
			   	  }
       }
      });
      /*
		Warn Dialog	
	*/ 
	 $("#warnDialog").dialog({
        bgiframe: false,
        autoOpen: false,
        resizable:false,
	autoResize:true,
	draggable:false,
        height: 150,
        width:250,
        buttons: {
					Ok: function() {$(this).dialog('close');}
			}
      });
    /*********************************************************************
    Remote SOAP Calls section.
    
    Note that we call update of the task list on the call back of each SOAP
    request. Which means, most actions would have an immediate effects, and
    we can show it pretty fast 
    *********************************************************************/

    /*
    SOAP for Delete task(s) 
    */
    function deleteTask(com,grid)
    {
	if($('.trSelected',grid).length<=0)
	{
             $('#warnDialog').html('<a>Please select atleast one task</a>');
             $('#warnDialog').dialog('open');                
             return false;
	}
       else if($('.trSelected',grid).length>0) {
        $('.trSelected',grid).each(function() {
          
          // pipa delete
          var pipa = $('a.pipa',$(this));
          if(pipa.html()!=null) {
           var soapBody = new SOAPObject("deletePipa");
           soapBody.ns = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";
           soapBody.appendChild(new SOAPObject("pipaurl")).val(pipa.attr('endpoint'));
           soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
           var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/deletePipa", soapBody);
           SOAPClient.SOAPServer = tmsService;
           SOAPClient.Proxy = proxy;
           SOAPClient.SendRequest(sr, update);
          } // end soap delete pipa
 
         // task delete
         var task = $('a.taskd',$(this));
         if(task.html()!=null) {
           var soapBody = new SOAPObject("delete");
           soapBody.ns = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";
           soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
           soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
           var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/delete", soapBody);
           SOAPClient.SOAPServer = tmsService;
           SOAPClient.Proxy = proxy;
           SOAPClient.SendRequest(sr, update);
         } // end soap delete tasks
                    
        }); // end each
      } // end delete
      $("#deleteDialog").dialog('close');
    }; // end delete function
  
    /*
    SOAP for Claim and revoke
    */
    function claimTask(com,grid)
    {
      var count=0; 
      if($('.trSelected',grid).length<=0)
      {
                  $('#warnDialog').html('<a>Please select atleast one task</a>');
                  $('#warnDialog').dialog('open');                        
                  return false;
      }
      else {
      $('.trSelected',grid).each(function() 
      {
      var task = $('a.taskd',$(this));
	  var istaskowner=task.attr('istaskowner');  
	  if(istaskowner=="true"){
      
        if(task.attr('state') == "READY") {
          // claim
          var soapBody = new SOAPObject("claimTaskRequest");
          soapBody.ns = "http://www.intalio.com/bpms/workflow/ib4p_20051115";
          soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
          soapBody.appendChild(new SOAPObject("claimerUser")).val(currentUser);
          soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
          var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/claimTask", soapBody);
          SOAPClient.Proxy = proxy;
          SOAPClient.SOAPServer = tmpService;
          SOAPClient.SendRequest(sr, update);
          } 
          else 
          {
          // revoke
          var soapBody = new SOAPObject("revokeTaskRequest");
          soapBody.ns = "http://www.intalio.com/bpms/workflow/ib4p_20051115";
          soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
          soapBody.appendChild(new SOAPObject("claimerUser")).val(currentUser);
          soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
          var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/revokeTask", soapBody);
          SOAPClient.Proxy = proxy;
          SOAPClient.SOAPServer = tmpService;
          SOAPClient.SendRequest(sr, update);
        }
	}//end if istaskowner
	else
		count=count+1;
      }); // end each  
    }
	  if (count!=0){
            var errorMessage="<fmt:message key='org_intalio_uifw_toolbar_button_claim_error'/>";
	        jAlert(errorMessage.replace("{0}", count) , '<fmt:message key="com_intalio_bpms_workflow_pageTitle"/>' );
        }
    }; // end function claims
    
    /*
    SOAP to skip a task
    */
    function skipTask(com,grid) {
        var count=0; 
	 if($('.trSelected',grid).length<=0)
	{
                  $('#warnDialog').html('<a>Please select atleast one task</a>');
                  $('#warnDialog').dialog('open');                        
                  return false;
	}
        else
	{
	  $('.trSelected',grid).each(function() 
	  {
            var task = $('a.taskd',$(this));
            var istaskowner=task.attr('istaskowner');  
            if(istaskowner=="true"){
	            var soapBody = new SOAPObject("skipTaskRequest");
	            soapBody.ns = "http://www.intalio.com/bpms/workflow/ib4p_20051115";
	            soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
	            soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
	            var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/skipTask", soapBody);
	            SOAPClient.Proxy = proxy;
	            SOAPClient.SOAPServer = tmpService;
	            SOAPClient.SendRequest(sr, update);
            }else
        		count=count+1;
	  });
	}
        if (count!=0){
            var errorMessage="<fmt:message key='org_intalio_uifw_toolbar_button_skip_error'/>";
	        jAlert(errorMessage.replace("{0}", count) , '<fmt:message key="com_intalio_bpms_workflow_pageTitle"/>' );
        }

    }
        
    /*
    SOAP to update the metadata of a task
    */
    function updateTask(com,grid) {
        $('.trSelected',grid).each(function() 
        {
            var task = $('a.taskd',$(this));
            var soapBody     = new SOAPObject("update");
            soapBody.ns      = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";
            //var taskEl = soapBody.appendChild(new SOAPObject("task"));
            var metaEl = soapBody.appendChild(new SOAPObject("taskMetadata"));
            metaEl.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
            metaEl.appendChild(new SOAPObject("description")).val($('#up_description').val());
            metaEl.appendChild(new SOAPObject("priority")).val($('#up_priority').val());
            soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
            
            var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/update", soapBody);
            SOAPClient.Proxy = proxy;
            SOAPClient.SOAPServer = tmsService;
            SOAPClient.SendRequest(sr, update);
        });
    }
        
    /*
    SOAP to reassign a task
    */
    function reassignTask(com,grid) {
        $('.trSelected',grid).each(function() 
        {
            var task = $('a.taskd',$(this));
            var reassign_user= $('#reassign_user').val();
            var reassign_roles= $('#reassign_roles').val();         
            if ((reassign_user==null || jQuery.trim(reassign_user).length==0) && (reassign_roles==null || jQuery.trim(reassign_roles).length==0) ){
	             jAlert('<fmt:message key="org_intalio_uifw_reassign_error"/>', '<fmt:message key="com_intalio_bpms_workflow_pageTitle"/>');
	             return false;
            }
            
            var soapBody     = new SOAPObject("reassign");
            soapBody.ns      = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";
            soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
            soapBody.appendChild(new SOAPObject("userOwner")).val($('#reassign_user').val());
            soapBody.appendChild(new SOAPObject("roleOwner")).val($('#reassign_roles').val());
            soapBody.appendChild(new SOAPObject("taskState")).val('READY');
            soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
			soapBody.appendChild(new SOAPObject("userAction")).val('REASSIGN');
            
            var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/reassign", soapBody);
            SOAPClient.Proxy = proxy;
            SOAPClient.SOAPServer = tmsService;
            SOAPClient.SendRequest(sr, update);
    	    $("#reassignDialog").dialog('close');
            
        });
    }
        
    /*
    Code to update the current task list after sending some request to the server
    */
    function update(object) 
    {
       $.timer(1000, function (timer) {refresh(true);timer.stop();});
    }
        
    /*********************************************************************
    Support methods for reassign dialog.
    We need to be able to fetch users and roles, so below are a few
    methods that make SOAP calls to the token service and retrieve the 
    proper data.
    *********************************************************************/

    /*
    Populate the html select box with roles coming from the server 
    */
    function populateRoles(data) {
      var firstOption="<option value=''>Select role </option>";
      $("#reassign_dyn").append(firstOption);
      $(data.responseXML).find('*').filterNode("rbac:role").each(function(){
          var option = "<option value=\""+$(this).text()+"\">"+$(this).text()+"</option>";
          $("#reassign_dyn").append(option);
     });
      updateDynamicUsers();
    }
     
    /*
    Call from reassign dialog
    */   
    function updateDynamicRoles() {
      var soapBody     = new SOAPObject("getAssignedRoles");
      soapBody.ns      = "http://tempo.intalio.org/security/RBACQueryService/";
      soapBody.appendChild(new SOAPObject("user")).val(currentUser);
      var sr           = new SOAPRequest("http://tempo.intalio.org/security/RBACQueryService/getAssignedRoles", soapBody);
      SOAPClient.Proxy = proxy;
      SOAPClient.SOAPServer = rbacService;
      SOAPClient.SendRequest(sr, populateRoles);
    }
    
    /*
    Call from reassign dialog
    */     
    function updateDynamicUsers() {
     if($('#reassign_dyn').val() !=null && $('#reassign_dyn').val().length>0 && $('#reassign_dyn').val()!=""){    	
	      var soapBody     = new SOAPObject("getAssignedUsers");
	      soapBody.ns      = "http://tempo.intalio.org/security/RBACQueryService/";
	      soapBody.appendChild(new SOAPObject("role")).val($('#reassign_dyn').val());
	      var sr           = new SOAPRequest("http://tempo.intalio.org/security/RBACQueryService/getAssignedUsers", soapBody);
	      SOAPClient.Proxy = proxy;
	      SOAPClient.SOAPServer = rbacService;
	      SOAPClient.SendRequest(sr, populateDynamicUsers);
     }
	}
        
    /*
    Call from reassign dialog
    */        
    function populateDynamicUsers(data) {
      $('#reassign_dyn_user').empty();
      var firstOption="<option value=''>Select user </option>";
      $("#reassign_dyn_user").append(firstOption);      
      $(data.responseXML).find('*').filterNode("rbac:user").each(function(){  
          var option = "<option value=\""+$(this).text()+"\">"+$(this).text()+"</option>";
          $("#reassign_dyn_user").append(option);
      });
    }
    
    /*
    Detect changes from roles, and load the users
    */
    $('#reassign_dyn').change(function() {
      updateDynamicUsers();
      $('#reassign_roles').val($('#reassign_dyn').val());
      $('#reassign_user').val("");
    });

    $('#reassign_dyn_user').change(function() {
      $('#reassign_user').val($('#reassign_dyn_user').val());
      $('#reassign_roles').val("");
    });
    
    /*********************************************************************
    Support methods for export dialog.
    *********************************************************************/
    
    function exportTasksAction() {
      $('#exportdialog').dialog('close');
      var format = $("input[name='eformat']:checked").val();
      var type = $("input[name='etype']:checked").val();
      var export_url = format+"?";
      export_url += "type="+type;
      var rp = $("div.flexigrid div.pGroup select[name='rp'] option:selected").val();
	  var page = $("div.flexigrid div.pGroup span.pcontrol input").val();
	  export_url += "&rp="+rp+"&page="+page;   
      window.open(export_url,"_new");
    }
    
 /**
 * @Function Name   : saveVacation 
 * @Description     : This function will make an ajax call to save the data of vacation management
 * @param           : 
 * @returns         : 
 * */
    function saveVacation()
    {	
		
	  	if(isValidDate("fromdate","todate","desc"))
    	{
			var data = { action:"insertVacation",fromDate: $('#fromdate').val(), toDate: $('#todate').val(),desc: $('#desc').val()}
			$.ajax({
			url: 'vacation.htm',
			type: 'POST',
			dataType: 'json',
			data: data,
			cache: false,
			async: true,
			error: function (e) {
			},
			success: function (data) {
				if(data.message.indexOf("Inserted")>=0)
				{
					
					$('#vacation').dialog('close');
					$('#messageDialog').html('<a >Vacation details are succesfully saved please make sure you also reassign your task before going on leave.</a>');
					$(".vacationDet").text("End Your Vacation");
				}
				else
				{
					$('#messageDialog').html('<a>Exception occured while saving the Vacation details please see the error log for further details.</a>');
										
				}
				 $('#messageDialog').dialog('open');
		     }
			});
	   }	
}
 /**
 * @Function Name   : saveVacation 
 * @Description     : This function will make an ajax call to end the vacation of a logged in user
 * @param           : 
 * @returns         : 
 * */
function endVacation()
{
	var data = { action:"endVacation",id:vac_id}
    	$.ajax({
        url: './vacation.htm',
        type: 'POST',
        dataType: 'json',
        data: data,
        cache: false,
        async: true,
        error: function (e) {
        },
        success: function (data) 
        {
			if(data.message.indexOf("Deleted")>=0)
			{
				$('#endVacDialog').dialog('close');
				$('#messageDialog').html('<a>Successfully ended your vacation</a>');
				$(".vacationDet").text("Vacation");
			}	
			else
			$('#messageDialog').html('<a>Exception occured while ending vacation please see the error log for further details.</a>');
			
			$('#messageDialog').dialog('open');
		}
	});
}	
    /*********************************************************************
    Support for handling click from the flexigrid toolbars
    *********************************************************************/

    /*
    Code for loading the update dialog after mouse click
    */
    function clickUpdate(com,grid) {

      $('#up_description').empty();
      $('#up_priority').empty();
      $('#up_description').show();
      $('#up_priority').show();
      if($('.trSelected',grid).length<=0)
      {
                  $('#warnDialog').html('<a>Please select one task</a>');
                  $('#warnDialog').dialog('open');                
                  return false;
      }
      else if($('.trSelected',grid).length>1)        
      {
                  $('#warnDialog').html('<a>Please select only one task</a>');
                  $('#warnDialog').dialog('open');                
                  return false;
      }
      else if($('.trSelected',grid).length!=0) {
      var task = $('a.taskd',$('.trSelected:first',grid));
      $('#up_description').val(task.attr('description'));
      $('#up_priority').val(task.attr('priority'));

      $("#updateDialog").dialog({
        bgiframe: false,
        autoOpen: false,
        height: 200,
        modal: true,
        resizable :false,
	draggable:false,
	autoResize: true,
	buttons: {
          Update: function() {updateTask(com,grid); $(this).dialog('close');}
        },
        close: function() {$(this).dialog('close');} //updated line fix for WF-1460
	
      });
      $("#updateDialog").dialog('open');
	}        
}
    /*
    Code for laoding the reassign dialog after mouse click
    */
    function clickReassign(com,grid) {
      if($('.trSelected',grid).length<=0)
      {
                  $('#warnDialog').html('<a>Please select one task</a>');
                  $('#warnDialog').dialog('open');                
                  return false;
      }
      else if($('.trSelected',grid).length>1)        
      {
                  $('#warnDialog').html('<a>Please select only one task</a>');
                  $('#warnDialog').dialog('open');                
                  return false;
      }
      else if($('.trSelected',grid).length!=0) {

        $('#reassign_dyn_user').empty();
        $('#reassign_dyn').empty();

        updateDynamicRoles();

        $("#reassignDialog").dialog({
          bgiframe: false,
          autoOpen: open,
          height: 250,
          modal: true,	
	  resizable :false,
	  autoResize: true,
	  draggable:false,
          buttons: {
            Reassign: function() {reassignTask(com,grid);}
          },
          close: function() {$(this).dialog('close');}
        });
        $("#reassignDialog").dialog('open');
      }
    }

    /*
    Code for the export dialog, after the mouse click
    */        
    function clickExportTasks() {
      $('#exportdialog').dialog('open');
    }
	
	 /*
    Code for laoding the delete dialog after mouse click
    */
    function clickDelete(com,grid) {
		
      if($('.trSelected',grid).length!=0) {
		  $("#deleteDialog").dialog({
          resizable:false,
          bgiframe: false,
          autoOpen: false,
          height: 'auto',
          width: 'auto',
          modal: true,
	  draggable:false,
          title : "Confirmation",
          autoResize: true,
		  buttons: {
            Ok: function() {deleteTask(com,grid);}
          },
          close: function() {}
        });
        
        $("#deleteDialog").html("<a> Are you sure you want to delete "+ $('.trSelected',grid).length + " Task(s) ?</a>");
        $("#deleteDialog").dialog('open');
      }
    }
 /**
 * @Function Name   : clickVacation 
 * @Description     : This function will make an ajax call to Validate earlier user has applied leave / not 
 * @param           : 
 * @returns         : 
 * */
    function clickVacation()
    {
		var data = { action:"Validate"}
    	$.ajax({
        url: 'vacation.htm',
        type: 'POST',
        dataType: 'json',
        data: data,
        cache: false,
        async: true,
        error: function (e) {
        },
        success: function (data) {
			if(data.vacId !="undefined" && data.vacId!="" && data.vacId!=undefined)
			{
					vac_id = parseInt(data.vacId);
					$('#endVacDialog').html('<a> Already applied for a leave <br>From &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: ' +data.vacFromdate+'<br>  To &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:  '+data.vacToDate+'<br> Description  :  ' +data.vacDesc+ '<br>If your vacation is completed please click on End Vacation</a>');
					$('#endVacDialog').dialog('open');
			}
			else if(data.vacation =="undefined")
			$('#vacation').dialog('close');
			else
			{
				$('#fromdate').val("");
				$('#todate').val("");
				$('#desc').val("");
				$('#vacation').dialog('open');
			}	
			  
		}	
		});	
    }
 
    /**
     * @Function Name   : clickViewAllTasks 
     * @Description     : This function will fetch all the tasks
     * @param           : 
     * @returns         : 
     * */
        function clickViewAllTasks()
        {
    		document.getElementById('isViewTask').value="false";
    		resetQueryString("tabTasks");
    		$("#tabTasks").click();
        }
 /**
 * @Function Name   : isValidDate 
 * @Description     : This function will Validate to date,from date & description
 * @param           : Refrence of fromDate , toDate,Description field of vacation
 * @returns         : 
 * */
    function isValidDate(varFrom, varTo,desc) 
    {
            var fromdate, todate, dt1, dt2, mon1, mon2, yr1, yr2, date1, date2;
            var chkFrom = document.getElementById(varFrom);
            var chkTo = document.getElementById(varTo);
            if (document.getElementById(varFrom).value == '') {
                $("#warnDialog").html('<a >From date should not be empty</a>');
                $("#warnDialog").dialog('open');
                return false;
            }
            else if (document.getElementById(varTo).value == '') {
                $("#warnDialog").html('<a >To date should not be empty</a>');
                $("#warnDialog").dialog('open');
                return false;
            }
            else if ($.trim(document.getElementById(desc).value)== '') {
                $("#warnDialog").html('<a >Description should not be empty</a>');
                $("#warnDialog").dialog('open');
                return false;
            }
            if (varFrom != null && document.getElementById(varFrom).value != '' && varTo != null && document.getElementById(varTo).value!= '') {
                if (checkdate(chkFrom) != true) {
                    document.getElementById(varFrom).value = '';
                    return false;
                }
                else if (checkdate(chkTo) != true) {
                    document.getElementById(varTo).value = '';
                    return false;
                }
                else {
                    fromdate = document.getElementById(varFrom).value;
                    todate = document.getElementById(varTo).value;
                    dt1 = parseInt(fromdate.substring(0, 2), 10);
                    mon1 = parseInt(fromdate.substring(3, 5), 10);
                    yr1 = parseInt(fromdate.substring(6, 10), 10);
                    dt2 = parseInt(todate.substring(0, 2), 10);
                    mon2 = parseInt(todate.substring(3, 5), 10);
                    yr2 = parseInt(todate.substring(6, 10), 10);
                    date1 = new Date(yr1, mon1, dt1);
                    date2 = new Date(yr2, mon2, dt2);
 
                    if (date2 < date1) {
                        $("#warnDialog").html('<a >To date Should be greater than From date</a>');
						$("#warnDialog").dialog('open');
                        document.getElementById(varTo).value = '';
                        return false;
                    }
                }
            }
            return true;
   }
 /**
 * @Function Name   : checkdate 
 * @Description     : This function will Validate for special characters in date field
 * @param           : Refrence of Date field
 * @returns         : boolean
 * */ 
    function checkdate(input) {
    var validformat = /^\d{2}\/\d{2}\/\d{4}$/ //Basic check for format validity
    var returnval = false
    if (!validformat.test(input.value))
    {
		$("#warnDialog").html('<a >Invalid Date Format Please correct and submit again.</a>');
		$("#warnDialog").dialog('open');
   } 
    else { //Detailed check for valid date ranges
        var monthfield = input.value.split("/")[1]
        var dayfield = input.value.split("/")[0]
        var yearfield = input.value.split("/")[2]
        var dayobj = new Date(yearfield, monthfield - 1, dayfield)
        if ((dayobj.getMonth() + 1 != monthfield) || (dayobj.getDate() != dayfield) || (dayobj.getFullYear() != yearfield))
        {
            $("#warnDialog").html('<a >Invalid Day, Month, or Year range detected Please correct and submit again</a>');
			$("#warnDialog").dialog('open');
		}	
        else
            returnval = true
    }
    return returnval
}  	

    function changeButtonText()
    {

		var data = { action:'Validate'}
    	$.ajax({
        url: 'vacation.htm',
        type: 'POST',
        dataType: 'json',
        data: data,
        cache: false,
        async: true,
        error: function (e) {
        },
        success: function (data) {
		   if(data.vacId !="undefined" && data.vacId!="" && data.vacId!=undefined)
		   $(".vacationDet").text("End Your Vacation");
		   else 
		   $('.vacationDet').text('Vacation');
			  
		}	
		});	
    }
	/*********************************************************************
    Flexigrid tables handling
    *********************************************************************/
		
    /*
    Common flexigrid properties
    */
    var p = {
      url: 'updates.htm',
      dataType: 'xml',
      showToggleBtn: true,
      width: widthFull,
      pagestat: '<fmt:message key="org_intalio_uifw_flexigrid_displaying"/>',
      procmsg: '<fmt:message key="org_intalio_uifw_flexigrid_processing"/>',
      nomsg: '<fmt:message key="org_intalio_uifw_flexigrid_noitem"/>',
      errormsg: '<fmt:message key="org_intalio_uifw_flexigrid_error"/>',
      height: height2,
      usepager: true,
      searchitems : [{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description'},
                     {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_userOwners"/>', name : '_userOwners'},
                     {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_roleOwners"/>', name : '_roleOwners'}                     

				      <c:forEach items="${newColumnList}" var="newColumn">
				             ,{display: '${newColumn}', name : '_customMetadata'}
				             
				      </c:forEach>
   ]};		

    /*
    Table for activity tasks
    */
	var taskIcons = getToolbarIconsCodes(taskIconSet);
    var t1 = $("#table1").flexigrid($.extend({
      <% if(useToolbar) {%> 
        buttons : taskIcons,
        <%} %>
        params: [
        { name : 'type', value : 'PATask' },
        { name : 'update', value : true }
        ],
        colModel : [
        {
          display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', 
          name : '_description', 
          width : width*0.25, 
          sortable : true, 
          align: 'left'},
        {
          display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_taskState"/>', 
          name : '_state', 
          width : width*0.035, 
          resize : true, 
          sortable : true, 
          align: 'center'},
        {
          display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', 
          name : '_creationDate', 
          width : width*0.15, 
          sortable : true, 
          align: 'left'},
        {
          display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_dueDate"/>', 
          name : '_deadline', 
          width : width*0.15, 
          sortable : true, 
          align: 'left'},
        {
          display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_priority"/>', 
          name : '_priority', 
          width : width*0.070, 
          sortable : true, 
          align: 'center'},
        {
          display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_attachments"/>', 
          name : '_attachments', 
          width : width*0.12, 
          sortable : false, 
          align: 'center'},
        {
          display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_userOwners"/>', 
          name : '_attachments', 
          width : width*0.12, 
          sortable : false, 
          align: 'center'},
        {
          display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_roleOwners"/>', 
          name : '_attachments', 
          width : width*0.12, 
          sortable : false, 
          align: 'center'}
          
          <c:forEach items="${newColumnList}" var="newColumn">
          
          ,{
              display: '${newColumn}', 
              name : '_attachments', 
              width : width*0.12, 
              sortable : false, 
              align: 'center'}
          
          </c:forEach>
          
          
        ]
    },p));
		
	/*
	Table for notifications
	*/
	var notiIcons = getToolbarIconsCodes(notiIconSet);
	var t2 = $("#table2").flexigrid($.extend({
	params: [
		 { name : 'type', value : 'Notification' }
		,{ name : 'update', value : true }
	],
	<% if(useToolbar) {%> 
	buttons : notiIcons,
	<%} %>
	colModel : [
	{
	  display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', 
	  name : '_description', 
	  width : width*0.3, 
	  sortable : true, 
	  align: 'left'},
{
  display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_priority"/>', 
  name : '_priority', 
  width : width*0.2, 
  sortable : true, 
  align: 'left'},
	{
	  display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', 
	  name : '_creationDate', 
	  width : width*0.2, 
	  sortable : true, 
	  align: 'left'},
        {
          display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_userOwners"/>', 
          name : '_attachments', 
          width : width*0.12, 
          sortable : false, 
          align: 'center'},
        {
          display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_roleOwners"/>', 
          name : '_attachments', 
          width : width*0.12, 
          sortable : false, 
          align: 'center'}, 
          {
              display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_view_tasks"/>', 
              name : '_attachments', 
              width : width*0.12, 
              sortable : false, 
              align: 'center'}
	]
	},p));
		
	/*
	Table for PIPA
	*/
	var t3 = $("#table3").flexigrid($.extend({
	params: [
		 { name : 'type', value : 'PIPATask' }
		,{ name : 'update', value : true }
	],
	<% if(useToolbar) {%> 
	buttons : notiIcons, 
	<%} %>
	colModel : [
	{
	  display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', 
	  name : '_description', 
	  width : width*0.3, 
	  sortable : true, 
	  align: 'left'},
	{
	  display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', 
	  name : '_creationDate', 
	  width : width*0.2, 
	  sortable : true, 
	  align: 'left'},
    {
      display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_userOwners"/>', 
      name : '_attachments', 
      width : width*0.2, 
      sortable : false, 
      align: 'center'},
    {
      display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_roleOwners"/>', 
      name : '_attachments', 
      width : width*0.2, 
      sortable : false, 
      align: 'center'}, 
      {
          display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_view_tasks"/>', 
          name : '_viewTasks', 
          width : width*0.2, 
          sortable : false, 
          align: 'center'},
	]},p));		
		
		
    /*********************************************************************
    JQuery Tab handling
    *********************************************************************/

    /*
    tab definition
    */
    $.jtabber({
      mainLinkTag: "#container li a", 
      activeLinkClass: "active", 
      hiddenContentClass: "hiddencontent", 
      showDefaultTab: 0, 
      effect: 'fade', 
      effectSpeed: speed 
    });

    /*
    Support method for refreshing the current tab tabs and hiding the other ones
    */
    function refresh(visible) {
      // visible == hide all
      if(needResize) {
        location.href=location.href;
      }
      if(visible==false){
        t1.parent().parent().hide(speed);
        t2.parent().parent().hide(speed);
        t3.parent().parent().hide(speed);
        current==null;
      }
      else {
        if(current==null) {
          t1.parent().parent().hide(speed);
          t2.parent().parent().hide(speed);
          t3.parent().parent().hide(speed);
        }
        else if(current=='tabTasks') {
          t1.flexReload();
          t1.parent().parent().show(speed);
          t2.parent().parent().hide(speed);
          t3.parent().parent().hide(speed);
        }
        else if(current=='tabNotif') {
          t2.flexReload();
          t1.parent().parent().hide(speed);
          t3.parent().parent().hide(speed);
          t2.parent().parent().show(speed);
        }
        else if(current=='tabPipa') {
          t3.flexReload();
          t1.parent().parent().hide(speed);
          t2.parent().parent().hide(speed);
          t3.parent().parent().show(speed);
        }
      }
    }

		/*
		Change tab on click, refresh frame, refresh task list
		/*/
    $('#tabnav li a').click(function(){
      resetTimer();
      clearFrame();
      if(current==null)  {
        $(".intro").each(function(){ $(this).hide();});
      }
      current = $(this).attr("id");
      if(current == 'tabNotif' || current == 'tabPipa'){
    	  document.getElementById('isViewTask').value="false";
    	  document.getElementById('formURL').value="";
		  document.getElementById('taskType').value="";
		  //resetQueryString(current);
      }
      refresh(true);
    });


    /*********************************************************************
    Handling of the form manager internal iframe
    *********************************************************************/
    $('#taskform').load(function(){
   
      var checkPATask = document.getElementById('taskType').value;
      var loc = window.frames['taskform'].location;
      try {
        if(loc == "about:blank") return;
      } catch(err) {
        $('#taskform').animate({height:height},speed);
        refresh(false);
        return;
      }

      var elo = $('html', window.frames['taskform'].document);
      var visible = $('#taskform').height() != 0;
      
      //if(visible) {
        // TODO: let's find a clever way of checking for content independent of the form manager
        var content = (loc.toString().indexOf('type=PATask')!=-1) || (elo.html().substring(0,6).toLowerCase() == '<head>' && elo.html().length > 700);
        if(!content  && ( loc.toString().indexOf('empty.jsp') > -1) ) {
          clearFrame();
          if(checkPATask){
        	  	  current = "tabTasks";
          		$("#tabTasks").click();
          }else{
        	
        	  refresh(true);
          }
        } else {
          $('#taskform').animate({height:height},speed);
          refresh(false);
        }
      //}
      
      $('#taskform').contents().mousemove(function(e){ resetTimer();});
      $("#taskform").contents().keypress(function (e){ resetTimer();});
    });

		
    /**********************************************************************
    Section to handle auto refresh of the table content.
    We do not reload if any of the dialog is showing up.

    We also check the connection by pinging the server at the same time, 
    and trigger the connectionLost dialog if the server is not responding
    **********************************************************************/

    /*
    Refresh related variables
    */
    var timeout = <c:out value="${refreshTime}"/> * 1000;
    if(timeout == null || timeout < 1000) timeout = 1000;

    /*
    Refresh timer
    */
    $.timer(timeout,function(timer) {
      // don't refresh if showing a dialog
      if ($("#reassignDialog").is(":visible")) return; 
      if ($("#sessionExpired").is(":visible")) return; 
      if ($("#updateDialog").is(":visible")) return; 
      if ($("#connectionLost").is(":visible")) return; 

      ping(timer);

      if(current=='tabTasks') t1.flexReload();
      if(current=='tabNotif') t2.flexReload();
      if(current=='tabPipa')  t3.flexReload();
    });

    /*
    Method to ping the server through fake jquery http call (HEAD)
    */
    function ping(timer) {
      var xhrReq = $.ajax({
        type: "HEAD",
        url: "/ui-fw/images/spacer.gif",
        error: function() {
          $("#connectionLost").dialog('open');
          timer.stop();
        }
      });
    }
		
    /**********************************************************************
    Remaining client side init calls
    **********************************************************************/
    //$.jcorners("#intro",{radius:20});

    $("#reassignDialog").hide();
    $("#updateDialog").hide();
    $("#connectionLost").hide();
    $("#tabTasks").click();

    if (!one_task_page)
    window.open("/ui-fw/script/empty.jsp", "taskform");

    /*
    Ajax activity support call. Show the ajax loading icon
    */
    $('#loadingdiv')
    .hide()  // hide it initially
    .ajaxStart(function() {
      $(this).show();
    })
    .ajaxStop(function() {
      $(this).hide();
    });

		
    }); // end of document ready, which also means the custom jquery code

    $.fn.filterNode = function(name) {
      return this.find('*').filter(function() {
        return this.nodeName === name;
    });
  };
    
    function setSearch(){
  	  
  	  var searchUser = document.getElementById('searchUser').value;
  	  if(searchUser!="" && searchUser != 'null'){
  			  $('#q').val(searchUser);
  			  $("#qtype option[value='_userOwners']").attr("selected", "selected");
  			  setTimeout(function() { $("#q").focus(); }, 100);
  			  //$("#q").focus();
  	  }
     }
    
    /*This function will be used to resetQueryString in the browser address bar*/
    
    function resetQueryString(current){
    	var k = location.search.indexOf("unid");
    	document.getElementById("currTab").value=current;
    	if(k>0){
    	var queryString = location.search.substring(0, location.search.indexOf('?')-1);
		location.search = queryString; // Causes page to reload
	}
    }
    

</script>

 <body onload="setSearch();">

