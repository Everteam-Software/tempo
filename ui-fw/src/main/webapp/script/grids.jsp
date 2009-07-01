<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@page import="org.intalio.tempo.uiframework.Configuration"%>
<%@page import="org.intalio.tempo.security.ws.TokenClient"%>
 
<script type="text/javascript">
 
  $(document).ready(function(){
    
    <%
    Configuration conf = Configuration.getInstance();
    String tokenService = conf.getTokenClient().getEndpoint();
    boolean useToolbar = conf.isUseToolbarIcons().booleanValue();
    %>
    
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
    $.ajaxSetup({timeout: <%= conf.getAjaxTimeout() %>});
    
    
    window.open("/ui-fw/script/empty.jsp", "taskform");
    
    if($.browser.msie){
     height = $(window).height() - 130;
     }else{
     height = $(window).height() - 130;
     }
    var height2 = height - 80;
    var needResize = false;
    
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
      if(time>=1) {
        var text = '<fmt:message key="org_intalio_uifw_session_inactivity_1"/>';
        text = text+" "+time+" "+'<fmt:message key="org_intalio_uifw_session_inactivity_2"/>';
        $("#timer").text(text);
      }
      time = time + 1;
      
      if ($("#connectionLost").is(":visible")) {
       timer.stop();
      }
      
      if(time > sessionTimeout) {
        log_me_out();
        timer.stop();
      }
    });
    
    
    $(this).click(function() {resetTimer();});
    
    function log_me_out() {
     $.post("login.htm?actionName=logOut");
     $("#sessionExpired").dialog('open');
    }
    
    $("#sessionExpired").dialog({
     bgiframe: false,
     autoOpen: false,
     height: 200,
         modal: true,    
              buttons: {
            '<fmt:message key="org_intalio_uifw_message.button.ok"/>': function() {location.reload(true);}
         },
       close: function() {location.reload(true);}
         });
    
         $("#connectionLost").dialog({
     bgiframe: false,
     autoOpen: false,
     height: 200,
       modal: true,    
              buttons: {'<fmt:message key="org_intalio_uifw_message.button.ok"/>': function() {location.reload(true);}},
         close: function() {location.reload(true);}
          });
          
          $("#exportdialog").dialog({
       bgiframe: false,
       autoOpen: false,
       height: 300,
         modal: true,    
            buttons: {'<fmt:message key="org_intalio_uifw_message.button.ok"/>': function() {exportTasksAction();}}
            });
 
        // make the soap calls to delete the tasks
        function deleteTask(com,grid)
        {
           if ($('.trSelected',grid).length>0) {
           if(confirm('Delete ' + $('.trSelected',grid).length + ' tasks?')){
 
            $('.trSelected',grid).each(function() {
 
              var pipa = $('a.pipa',$(this));
              if(pipa.html()!=null) {
               var soapBody = new SOAPObject("deletePipa");
               soapBody.ns = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";
               soapBody.appendChild(new SOAPObject("pipaurl")).val(pipa.attr('url'));
               soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
               var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/deletePipa", soapBody);
               SOAPClient.SOAPServer = tmsService;
               SOAPClient.Proxy = proxy;
               SOAPClient.SendRequest(sr, update);
              } // end soap delete pipa
     
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
              var soapBody = new SOAPObject("claimTaskRequest");
              soapBody.ns = "http://www.intalio.com/bpms/workflow/ib4p_20051115";
              soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
              soapBody.appendChild(new SOAPObject("claimerUser")).val(currentUser);
              soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
              var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/claimTask", soapBody);
              SOAPClient.Proxy = proxy;
              SOAPClient.SOAPServer = tmpService;
              SOAPClient.SendRequest(sr, update);
              } else {
          
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
 
          }); // end each
                
        }; // end function claims
        
        // update the current task list after sending some request to the server
        function update(object)
        {
           //refresh(true);
           
           // use below when debugging
           //alert((new XMLSerializer()).serializeToString(object));
           
           $.timer(1000, function (timer) {
              refresh(true);
              timer.stop();
           });
        }
        
        $('#loadingdiv')
            .hide() // hide it initially
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
                
                var soapBody = new SOAPObject("skipTaskRequest");
                soapBody.ns = "http://www.intalio.com/bpms/workflow/ib4p_20051115";
                soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
                soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
                
                var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/skipTask", soapBody);
                SOAPClient.Proxy = proxy;
                SOAPClient.SOAPServer = tmpService;
                SOAPClient.SendRequest(sr, update);
            });
        }
        
        function reassignTask(com,grid) {
            $('.trSelected',grid).each(function()
            {
                var task = $('a.taskd',$(this));
                
                var soapBody = new SOAPObject("reassign");
                soapBody.ns = "http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/";
                soapBody.appendChild(new SOAPObject("taskId")).val(task.attr('tid'));
                soapBody.appendChild(new SOAPObject("userOwner")).val($('#reassign_user').val());
                soapBody.appendChild(new SOAPObject("roleOwner")).val($('#reassign_roles').val());
                soapBody.appendChild(new SOAPObject("taskState")).val('READY');
                soapBody.appendChild(new SOAPObject("participantToken")).val('${participantToken}');
                
                var sr = new SOAPRequest("http://www.intalio.com/BPMS/Workflow/TaskManagementServices-20051109/reassign", soapBody);
                SOAPClient.Proxy = proxy;
                SOAPClient.SOAPServer = tmsService;
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
                var soapBody = new SOAPObject("getAssignedRoles");
                soapBody.ns = "http://tempo.intalio.org/security/RBACQueryService/";
                soapBody.appendChild(new SOAPObject("user")).val(currentUser);
                var sr = new SOAPRequest("http://tempo.intalio.org/security/RBACQueryService/getAssignedRoles", soapBody);
                SOAPClient.Proxy = proxy;
                SOAPClient.SOAPServer = rbacService;
                SOAPClient.SendRequest(sr, populateRoles);
        }
        
        function updateDynamicUsers() {
                var soapBody = new SOAPObject("getAssignedUsers");
                soapBody.ns = "http://tempo.intalio.org/security/RBACQueryService/";
                soapBody.appendChild(new SOAPObject("role")).val($('#reassign_dyn').val());
                var sr = new SOAPRequest("http://tempo.intalio.org/security/RBACQueryService/getAssignedUsers", soapBody);
                SOAPClient.Proxy = proxy;
                SOAPClient.SOAPServer = rbacService;
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
            updateDynamicUsers();
            $('#reassign_roles').val($('#reassign_dyn').val());
            $('#reassign_user').val("");
        });
        
        $('#reassign_dyn_user').change(function() {
            $('#reassign_user').val($('#reassign_dyn_user').val());
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
        
        function exportTasks() {
           $('#exportdialog').dialog('open');
        }
        
        function exportTasksAction() {
           $('#exportdialog').dialog('close');
           var format = $("input[name='eformat']:checked").val();
           var type = $("input[name='etype']:checked").val();
           var export_url = format+"?";
           export_url += "type="+type;
           //export_url += "&query="+$('#export_query').val();
           window.open(export_url,"_new");
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
    
    //
    // Common flexigrid properties
    //
    var p = {
        url: 'updates.htm',
        dataType: 'xml',
        showTableToggleBtn: true,
        width: widthFull,
        pagestat: '<fmt:message key="org_intalio_uifw_flexigrid_displaying"/>',
        procmsg: '<fmt:message key="org_intalio_uifw_flexigrid_processing"/>',
        nomsg: '<fmt:message key="org_intalio_uifw_flexigrid_noitem"/>',
        errormsg: '<fmt:message key="org_intalio_uifw_flexigrid_error"/>',
        height: height2,
        preProcess: preProcess,
        usepager: true,
        searchitems : [{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description'}]
    };    
 
    var t1 = $("#table1").flexigrid($.extend(
    {
        <% if(useToolbar) {%>
        buttons : [
           {name: '<fmt:message key="org_intalio_uifw_toolbar_button_delete"/>', bclass: 'delete', onpress : deleteTask},
           {name: '<fmt:message key="org_intalio_uifw_toolbar_button_claimrevoke"/>', bclass: 'claim', onpress : claimTask},
           {name: '<fmt:message key="org_intalio_uifw_toolbar_button_reassign"/>', bclass: 'reassign', onpress : clickReassign},
           {name: '<fmt:message key="org_intalio_uifw_toolbar_button_skip"/>', bclass: 'skip', onpress : skipTask},
           {name: '<fmt:message key="org_intalio_uifw_toolbar_button_export"/>', bclass: 'export', onpress : exportTasks}
        ],
        <%} %>
        params: [
       { name : 'type', value : 'PATask' }
      ,{ name : 'update', value : true }
    ],
    colModel : [
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_update"/>', name : '_update', width : width*0.015, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_aircraft"/>', name : '_aircraft', width : width*0.020, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_afn"/>', name : '_afn', width : width*0.025, sortable : false, align: 'center'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_sad"/>', name : '_sad', width : width*0.040, sortable : false, align: 'center'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_sta"/>', name : '_sta', width : width*0.025, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_eta"/>', name : '_eta', width : width*0.025, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_ata"/>', name : '_ata', width : width*0.035, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_type"/>', name : '_type', width : width*0.025, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_dfn"/>', name : '_dfn', width : width*0.025, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_sdd"/>', name : '_sdd', width : width*0.040, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_std"/>', name : '_std', width : width*0.025, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_etd"/>', name : '_etd', width : width*0.025, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_atd"/>', name : '_atd', width : width*0.035, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_stand"/>', name : '_stand', width : width*0.020, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_coord"/>', name : '_coord', width : width*0.045, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_mechanics"/>', name : '_mechanics', width : width*0.05, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_avionics"/>', name : '_avionics', width : width*0.05, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_hil"/>', name : '_hil', width : width*0.040, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_rtr"/>', name : '_rtr', width : width*0.040, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_comments"/>', name : '_comments', width : width*0.040, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_state"/>', name : '_state', width : width*0.030, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_resources"/>', name : '_resources', width : width*0.050, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_start"/>', name : '_start', width : width*0.025, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_end"/>', name : '_end', width : width*0.025, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_release"/>', name : '_release', width : width*0.025, sortable : false, align: 'center'},
		// {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_late"/>', name : '_late', width : width*0.025, sortable : false, align: 'center'}
    ]
    },p)
    );
    
    
    var t2 = $("#table2").flexigrid($.extend({
    params: [
       { name : 'type', value : 'Notification' }
      ,{ name : 'update', value : true }
    ],
    <% if(useToolbar) {%>
    buttons : [{name: '<fmt:message key="org_intalio_uifw_toolbar_button_delete"/>', bclass: 'delete', onpress : deleteTask}],
    <%} %>
    colModel : [
    {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.6, sortable : true, align: 'left'},
    {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.2, sortable : true, align: 'left'}
    ]
    },p));
    
    var t3 = $("#table3").flexigrid($.extend({
    <% if(useToolbar) {%>
    buttons : [{name: '<fmt:message key="org_intalio_uifw_toolbar_button_delete"/>', bclass: 'delete', onpress : deleteTask}],
    <%} %>
    params: [
       { name : 'type', value : 'PIPATask' }
      ,{ name : 'update', value : true }
    ],
    colModel : [
    {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.6, sortable : true, align: 'left'},
    {display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.4, sortable : true, align: 'left'}
    ]},p));
    
    var current = null;
 
    // visible == hide all
    function refresh(visible) {
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
 
    //
    // change tab on click, refresh frame, refresh task list
    //
    $('#tabnav li a').click(function(){
      resetTimer();
      clearFrame();
      $("#filter").val("");
      if(current==null) {
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
      if(visible) {
           // TODO: let's find a clever way of checking for content independent of the form manager
       var content = (loc.toString().indexOf('type=PATask')!=-1) || (elo.html().substring(0,6).toLowerCase() == '<head>' && elo.html().length > 700);
       if(!content) {
           clearFrame();
         refresh(true);
       } else {
       $('#taskform').animate({height:height},speed);
         refresh(false);
       }
      }
    });
    
    $.jcorners("#intro",{radius:20});
    $("#filterdiv").hide();
    $("#reassignDialog").hide();
    $("#connectionLost").hide();
    
    var timeout = <c:out value="${refreshTime}"/> * 1000;
    if(timeout == null || timeout < 1000) timeout = 1000;
    
    $.timer(timeout,function(timer) {
     // don't refresh if showing a dialog
     if ($("#reassignDialog").is(":visible")) return;
     if ($("#sessionExpired").is(":visible")) return;
     if ($("#connectionLost").is(":visible")) return;
    
     ping(timer);
    
          if(current=='tabTasks') t1.flexReload();
          if(current=='tabNotif') t2.flexReload();
          if(current=='tabPipa') t3.flexReload();
    });
    
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
    
    $("#tabTasks").click();
    
    });
 
</script>