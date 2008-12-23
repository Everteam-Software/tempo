<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

	<script type="text/javascript">

		$(document).ready(function(){ 

		var speed = "fast";
		window.open("about:blank", "taskform");
		var width = $(window).width()-($(window).width()/30);
		var height = 0;
		if($.browser.msie){
		     height = $(window).height() - 140;
		  }else{
		     height = $(window).height() - 100;
		  }
		var height2 = height - 80;
		$(window).resize(function() {
			location.reload(true);
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
		}
		
		//
		// Session timeout management
		//
		var time = 0;
		var sessionTimeout = 10; // 10 minutes 
		var timeCount = 60000; // 1 minute 
		$.timer(timeCount,function(timer) {
			$("#timer").text("You have been inactive for "+ time +" minute(s)");
			time = time + 1;
			if(time > sessionTimeout) {
				$.post("login.htm?actionName=logOut");		
				$("#modal").click();
			}
		});
		$(this).click(function() {time = 0;});
		$('#modal').modal({modal_styles: {width:"30%", "height":"30%"}});

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
		url: "updates.htm?update=true&type=Notification",
		colModel : [
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.6, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.2, sortable : true, align: 'left'}
		],	
		preProcess: preProcess,
		usepager: true,
		searchitems : [{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description'}],
		showTableToggleBtn: true,
		width: width,
		height: height2
		}
		);
		
		var t1 = $("#table1").flexigrid({
		url: 'updates.htm?update=true&type=PATask',
        dataType: 'xml',
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
		url: "updates.htm?update=true&type=PIPATask",
		colModel : [
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.6, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.2, sortable : true, align: 'left'}
		],	
		usepager: true,
		preProcess: preProcess,
		searchitems : [
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate'}
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
            time = 0;
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
			var visible = $('#taskform').height() != 0;
			if(visible) {
     			// TODO: let's find a clever way of checking for content independent of the form manager
	    		var content = (loc.toString().indexOf('type=PATask')!=-1) || (elo.html().substring(0,6).toLowerCase() == '<head>' && elo.html().length > 500)
				if(!content) {
			    $('#taskform').animate({height:"0px"},speed);
				refresh(true);
				}
			} else {
			    $('#taskform').animate({height:height},speed);
				refresh(false);
			}

		});
		
		$.jcorners("#intro",{radius:10});
		$("#filterdiv").hide();
		
		var timeout = <c:out value="${refreshTime}"/> * 1000;

		if(timeout == null || timeout < 1000) timeout = 1000;
		$.timer(timeout,function(timer) {
		    if(current=='tabTasks') t1.flexReload();
	    	if(current=='tabNotif') t2.flexReload();
			if(current=='tabPipa') t3.flexReload();
		});
		
		$("#tabTasks").click();
		
		});
	</script>