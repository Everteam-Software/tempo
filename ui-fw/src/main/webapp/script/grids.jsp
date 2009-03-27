<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@page import="org.intalio.tempo.uiframework.Configuration"%>

	<script type="text/javascript">

    (function($){
        $.newSelector = function() {
            if(!arguments) { return; }
            $.extend($.expr[':'],typeof(arguments[0])==='object' ? arguments[0]
              : (function(){
                  var newOb = {}; newOb[arguments[0]] = arguments[1];
                  return newOb;
              })()
            );
        }
    })(jQuery);

		$(document).ready(function(){ 

		var speed = "fast";
		var currentUser = '<%= ((String)request.getAttribute("currentUser")).replace("\\", "\\\\")%>';
		var width = $(window).width()-($(window).width()/30);
		var height = 0;
		
		window.open("about:blank", "taskform");
		
		if($.browser.msie){
		     height = $(window).height() - 140;
		  }else{
		     height = $(window).height() - 140;
		  }
		var height2 = height - 80;
		$(window).resize(function() {
			if(navigator.appName != "Microsoft Internet Explorer") {
				location.href=location.href;
		    }
		});

		function preProcess(data) {
    		$("rows row", data).each(function () {
                var elem = $(this);
                var str = $.string(elem.text()).stripTags().strip().str
                if(str.indexOf($("#filter").val())==-1) {
                    // This has a friend in flexigrid.js (line 456)
                    elem.attr("flexi","ignore");
				}
		    });
			return data;
		}

		function clearFrame() {
			$('#taskform').animate({height:"0px"},speed);
            window.open("about:blank", "taskform");
		}
		
		//
		// Session timeout management
		//
		var time = 0;
		var sessionTimeout = <c:out value="${sessionTimeout}"/>; // in minutes 
		var timeCount = 60000; // 1 minute 
		
		function resetTimer() {
		    time = 0;
        $("#timer").text("");
		}
		
		$.timer(timeCount,function(timer) {
			if(time>=1) $("#timer").text("You have been inactive for "+ time +" minute(s)");
			time = time + 1;
			if(time > sessionTimeout) {
				$.post("login.htm?actionName=logOut");		
				$("#modal").click();
			}
		});
		
		
		$(this).click(function() {resetTimer();});
		
		$('#modal').modal({modal_styles: {width:"30%", "height":"30%"}, hide:"location.reload(true);"});


        // make the soap calls to delete the tasks
        function deleteTask(com,grid)
        {
           if (com=='Delete' && $('.trSelected',grid).length>0) {
           if(confirm('Delete ' + $('.trSelected',grid).length + ' tasks?')){

            $('.trSelected',grid).each(function() {

              var pipa = $('a.pipa',$(this));
              if(pipa.html()!=null) {
               var soapBody = new SOAPObject("deletePipa");
               soapBody.ns = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";
               soapBody.appendChild(new SOAPObject("pipaurl")).val(pipa.attr('url'));
               soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
               var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/deletePipa", soapBody);
               SOAPClient.SOAPServer = '<%=Configuration.getInstance().getServiceEndpoint()%>';
               SOAPClient.Proxy = '<%=Configuration.getInstance().getServiceEndpoint()%>';
               SOAPClient.SendRequest(sr, update);
              } // end soap delete pipa
     
             var task = $('a.taskd',$(this));
             if(task.html()!=null) {
               var soapBody = new SOAPObject("delete");
               soapBody.ns = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";
               soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
               soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
               var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/delete", soapBody);
               SOAPClient.SOAPServer = '<%=Configuration.getInstance().getServiceEndpoint()%>';
               SOAPClient.Proxy = '<%=Configuration.getInstance().getServiceEndpoint()%>';
               SOAPClient.SendRequest(sr, update);
             } // end soap delete tasks
                        
            }); // end each
        		      
           } // end confirm
           } // end delete
        }; // end delete function
      
        function claimTask(com,grid)
        {
          $('.trSelected',grid).each(function() 
          {
          var task = $('a.taskd',$(this));
          
            if(task.attr('state') == "READY") {
              // claim
              var soapBody            = new SOAPObject("claimTaskRequest");
              soapBody.ns             = "http://www.intalio.com/bpms/workflow/ib4p_20051115";
              soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
              soapBody.appendChild(new SOAPObject("claimerUser")).val(currentUser);
              soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
              var sr                  = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/claimTask", soapBody);
              SOAPClient.Proxy        = '<%=Configuration.getInstance().getTMPEndpoint()%>';
              SOAPClient.SOAPServer        = '<%=Configuration.getInstance().getTMPEndpoint()%>';
              SOAPClient.SendRequest(sr, update);
              } else {
          
              // revoke
              var soapBody            = new SOAPObject("revokeTaskRequest");
              soapBody.ns             = "http://www.intalio.com/bpms/workflow/ib4p_20051115";
              soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
              soapBody.appendChild(new SOAPObject("claimerUser")).val(currentUser);
              soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
              var sr                  = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/revokeTask", soapBody);
              SOAPClient.Proxy        = '<%=Configuration.getInstance().getTMPEndpoint()%>';
              SOAPClient.SOAPServer        = '<%=Configuration.getInstance().getTMPEndpoint()%>';
              SOAPClient.SendRequest(sr, update);
            }

          }); // end each
                
        }; // end function claims
        
        // update the current task list after sending some request to the server
        function update(object) 
        {
           //refresh(true);
           
           // use below when debugging
           //alert((new XMLSerializer()).serializeToString(object));
           
           $.timer(500, function (timer) {
              refresh(true);
              timer.stop();
           });
        }
        
        $('#loadingdiv')
            .hide()  // hide it initially
            .ajaxStart(function() {
                $(this).show();
            })
            .ajaxStop(function() {
                $(this).hide();
            });
        
        function skipTask(com,grid) {
            $('.trSelected',grid).each(function() 
            {
                var task = $('a.taskd',$(this));
                
                var soapBody     = new SOAPObject("skipTaskRequest");
                soapBody.ns             = "http://www.intalio.com/bpms/workflow/ib4p_20051115";
                soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
                soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
                
                var sr           = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/skipTask", soapBody);
                SOAPClient.Proxy        = '<%=Configuration.getInstance().getTMPEndpoint()%>';
                SOAPClient.SOAPServer        = '<%=Configuration.getInstance().getTMPEndpoint()%>';
                SOAPClient.SendRequest(sr, update);
            });
        }
        
        function reassignTask(com,grid) {
            $('.trSelected',grid).each(function() 
            {
                var task = $('a.taskd',$(this));
                
                var soapBody     = new SOAPObject("reassign");
                soapBody.ns      = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";
                soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
                soapBody.appendChild(new SOAPObject("userOwner")).val($('#reassign_user').val());
                soapBody.appendChild(new SOAPObject("roleOwner")).val($('#reassign_roles').val());
                soapBody.appendChild(new SOAPObject("taskState")).val('READY');
                soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
                
                var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/reassign", soapBody);
                SOAPClient.Proxy = '<%=Configuration.getInstance().getServiceEndpoint()%>';
                SOAPClient.SOAPServer = '<%=Configuration.getInstance().getServiceEndpoint()%>';
                SOAPClient.SendRequest(sr, update);
            });
        }
        
        function populateRoles(data) {
        
            $(data).find("/rbac:getAssignedRolesResponse/rbac:role/").each(function(){
                if(this.nodeName == "rbac:role") {
                 var option = "<option value=\""+$(this).text()+"\">"+$(this).text()+"</option>";
                 $("#reassign_dyn").append(option);
                }
              });
        }
        
        function updateDynamicRoles() {
                var soapBody     = new SOAPObject("getAssignedRoles");
                soapBody.ns      = "http://tempo.intalio.org/security/RBACQueryService/";
                soapBody.appendChild(new SOAPObject("user")).val(currentUser);
                var sr           = new SOAPRequest("http://tempo.intalio.org/security/RBACQueryService/getAssignedRoles", soapBody);
                SOAPClient.Proxy = 'http://localhost:8080/axis2/services/RBACQueryService';
                SOAPClient.SOAPServer = 'http://localhost:8080/axis2/services/RBACQueryService';
                SOAPClient.SendRequest(sr, populateRoles);
        }
        
        function updateDynamicUsers() {
                var soapBody     = new SOAPObject("getAssignedUsers");
                soapBody.ns      = "http://tempo.intalio.org/security/RBACQueryService/";
                soapBody.appendChild(new SOAPObject("role")).val($('#reassign_dyn').val());
                var sr           = new SOAPRequest("http://tempo.intalio.org/security/RBACQueryService/getAssignedUsers", soapBody);
                SOAPClient.Proxy = 'http://localhost:8080/axis2/services/RBACQueryService';
                SOAPClient.SOAPServer = 'http://localhost:8080/axis2/services/RBACQueryService';
                SOAPClient.SendRequest(sr, populateDynamicUsers);
        }
        
        
        
        function populateDynamicUsers(data) {
           $('#reassign_dyn_user').empty();
           $(data).find("/rbac:getAssignedRolesResponse/rbac:role/").each(function(){
               if(this.nodeName == "rbac:user") {
                var option = "<option value=\""+$(this).text()+"\">"+$(this).text()+"</option>";
                $("#reassign_dyn_user").append(option);
               }
             });
           
        }
        
        $('#reassign_dyn').change(function() {
            $('#reassign_roles').text($('#reassign_dyn').val());
            $('#reassign_roles').val($('#reassign_dyn').val());
            $('#reassign_user').text("");
            $('#reassign_user').val("");
            updateDynamicUsers();
        });
        
        $('#reassign_dyn_user').change(function() {
            $('#reassign_user').text($('#reassign_dyn_user').val());
            $('#reassign_user').val($('#reassign_dyn_user').val());
            
            $('#reassign_roles').text("");
            $('#reassign_roles').val("");
        });
        
        function clickReassign(com,grid) {
            if($('.trSelected',grid).length!=0) {
            
                $('#reassign_dyn_user').empty();
                $('#reassign_dyn').empty();
                
                updateDynamicRoles();
            
                $("#reassignDialog").dialog({
                			bgiframe: false,
                			autoOpen: open,
                			height: 300,
                			modal: true,		
                      buttons: {
                				Reassign: function() {reassignTask(com,grid); $(this).dialog('close');},
                				Cancel: function() {$(this).dialog('close');}
                			},
                			close: function() {}
                });
                $("#reassignDialog").dialog('open');
            }
        }
        
		//
		// tab definition
		//
		$.jtabber({
			mainLinkTag: "#container li a", 
			activeLinkClass: "active", 
			hiddenContentClass: "hiddencontent", 
			showDefaultTab: 0, 
			effect: 'fade', 
			effectSpeed: speed 
		});
		
		var t2 = $("#table2").flexigrid({
		url: "updates.htm",
		params: [
			 { name : 'type', value : 'Notification' }
			,{ name : 'update', value : true }
		],
		colModel : [
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.6, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.2, sortable : true, align: 'left'}
		],	
		preProcess: preProcess,
		usepager: true,
		searchitems : [{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description'}],
		showTableToggleBtn: true,
		width: width,
		height: height2,
		<% if(Configuration.getInstance().isUseToolbarIcons()) {%> 
		buttons : [{name: 'Delete', bclass: 'delete', onpress : deleteTask}]
		<%} %>
		}
		);
		
		var t1 = $("#table1").flexigrid({
		url: 'updates.htm',
        dataType: 'xml',
        <% if(Configuration.getInstance().isUseToolbarIcons()) {%> 
        buttons : [
           {name: 'Delete', bclass: 'delete', onpress : deleteTask},
           {name: 'Claim/Revoke', bclass: 'claim', onpress : claimTask},
           {name: 'Reassign', bclass: 'reassign', onpress : clickReassign},
           {name: 'Skip', bclass: 'skip', onpress : skipTask}
        ],
        <%} %>
        params: [
			 { name : 'type', value : 'PATask' }
			,{ name : 'update', value : true }
		],
		colModel : [
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.39, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_taskState"/>', name : '_state', width : width*0.035, resize : true, sortable : true, align: 'center'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.15, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_dueDate"/>', name : '_deadline', width : width*0.15, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_priority"/>', name : '_priority', width : width*0.070, sortable : true, align: 'center'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_attachments"/>', name : '_attachments', width : width*0.09, sortable : false, align: 'center'}
		],
		usepager: true,
		preProcess: preProcess,
		searchitems : [{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description'}],
		showTableToggleBtn: true,
		width: width,
		height: height2
		}
		);
		
		var t3 = $("#table3").flexigrid({
		url: "updates.htm",
		  <% if(Configuration.getInstance().isUseToolbarIcons()) {%> 
		buttons : [{name: 'Delete', bclass: 'delete', onpress : deleteTask}], 
		  <%} %>
		params: [
			 { name : 'type', value : 'PIPATask' }
			,{ name : 'update', value : true }
		],
		colModel : [
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.6, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.38, sortable : true, align: 'left'}
		],	
		usepager: true,
		preProcess: preProcess,
		searchitems : [
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description'}
		],
		showTableToggleBtn: true,
		width: width,
		height: height2
		}
		);
		
		var current = null;

		// visible == hide all
		function refresh(visible) {
		if(visible==false){
			t1.parent().parent().hide(speed);
			t2.parent().parent().hide(speed);
			t3.parent().parent().hide(speed);
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

		//
		// change tab on click, refresh frame, refresh task list
		//
		$('#tabnav li a').click(function(){
            resetTimer();
			clearFrame();
			$("#filter").val("");
			if(current==null)  {
				$(".intro").each(function(){ $(this).hide();});
				$("#filterdiv").show();
			}
			current = $(this).attr("id");
			refresh(true);
		});

		// not supported by IE
		$("#filter").change(function() {
			refresh(true);
		});
		$("#filterbutt").click(function() {
			refresh(true);
		});
		
		$('#taskform').load(function(){
			var elo = $('html', window.frames['taskform'].document);
			var loc = window.frames['taskform'].location;
			if(loc == "about:blank") return;
			
			var visible = $('#taskform').height() != 0;
			if(visible) {
     			// TODO: let's find a clever way of checking for content independent of the form manager
	    		var content = (loc.toString().indexOf('type=PATask')!=-1) || (elo.html().substring(0,6).toLowerCase() == '<head>' && elo.html().length > 500);
			  if(!content) {
     			clearFrame();
				refresh(true);
			  } else {
			    $('#taskform').animate({height:height},speed);
				refresh(false);
			  }
			}
		});
		
		$.jcorners("#intro",{radius:10});
		$("#filterdiv").hide();
		$("#reassignDialog").hide();
		
		var timeout = <c:out value="${refreshTime}"/> * 1000;

		if(timeout == null || timeout < 1000) timeout = 1000;
		$.timer(timeout,function(timer) {
		  if ($("reassignDialog").is(":visible")) return;
		  
      if(current=='tabTasks') t1.flexReload();
      if(current=='tabNotif') t2.flexReload();
      if(current=='tabPipa')  t3.flexReload();
		});
		
		$("#tabTasks").click();
		
		});
	</script>