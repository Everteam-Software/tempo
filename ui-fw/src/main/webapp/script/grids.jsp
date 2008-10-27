<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>

	<script type="text/javascript">

		$(document).ready(function(){ 

		var speed = "fast";
		window.open("about:blank", "taskform");
		var width = $(window).width() - 150;

		function clearFrame() {
			$('#taskform').animate({height:"0px"},speed);
		}
		clearFrame();

		//
		// tab definition
		//
		$.jtabber({
			mainLinkTag: "#container li a", 
			activeLinkClass: "active", 
			hiddenContentClass: "hiddencontent", 
			showDefaultTab: 1, 
			effect: 'fade', 
			effectSpeed: speed 
		});
		
		var t1 = $("#table1").flexigrid({
		url: 'updates.htm?update=true&type=PATask',
        dataType: 'xml',
		colModel : [
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.4, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_taskState"/>', name : '_state', width : width*0.05, resize : true, sortable : true, align: 'center'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.2, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_dueDate"/>', name : '_deadline', width : width*0.1, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_priority"/>', name : '_priority', width : width*0.1, sortable : true, align: 'center'}
		],
		usepager: true,
		searchitems : [{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description'}],
		useRp: true,
		rp: 15,
		showTableToggleBtn: true,
		width: width
		}
		);
		
		var t2 = $("#table2").flexigrid({
		url: "updates.htm?update=true&type=Notification",
		colModel : [
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.4, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.2, sortable : true, align: 'left'}
		],	
		usepager: true,
		searchitems : [{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description'}],
		useRp: true,
		rp: 15,
		showTableToggleBtn: true,
		width: width
		}
		);
		
		var t3 = $("#table3").flexigrid({
		url: "updates.htm?update=true&type=PIPATask",
		colModel : [
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.4, sortable : true, align: 'left'},
		{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.2, sortable : true, align: 'left'}
		],	
		usepager: true,
		searchitems : [{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description'}],
		useRp: true,
		rp: 15,
		showTableToggleBtn: true,
		width: width
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
		    t1.flexReload();
		    t2.flexReload();
		if(current==null) {
			t1.parent().parent().hide(speed);
			t2.parent().parent().hide(speed);
			t3.parent().parent().hide(speed);
		}
		else if(current=='pa') {
			t1.parent().parent().show(speed);
			t2.parent().parent().hide(speed);
			t3.parent().parent().hide(speed);
		}
		else if(current=='notif') {
			t2.parent().parent().show(speed);
			t1.parent().parent().hide(speed);
			t3.parent().parent().hide(speed);
		}
		else if(current=='pipa') {
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
			clearFrame();
			if(current==null)  $(".intro").each(function(){ $(this).hide();});
			current = $(this).attr("title");
			refresh(true);
		});


		$('#taskform').load(function(){
			var visible = $('#taskform').height() != 0;
			if(visible) {
			    $('#taskform').animate({height:"0px"},speed);
				refresh(true);
			} else {
			    $('#taskform').animate({height:"100%"},speed);
				refresh(false);
			}
		});
		
		$.jcorners("#intro",{radius:10});
		
		var timeout = <c:out value="${refreshTime}"/> * 1000;

		if(timeout == null || timeout < 1000) timeout = 1000;
		$.timer(timeout,function(timer) {
			t1.flexReload();
	    	t2.flexReload();
		});

		});
	</script>