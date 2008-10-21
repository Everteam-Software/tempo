<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<script>
		
	$(document).ready(function(){ 
	
	function clearFrame() {
		$('#taskform').hide("medium");
	}
	
	//
	// tab definition
	//
	$.jtabber({
		mainLinkTag: "#container li a", 
		activeLinkClass: "active", 
		hiddenContentClass: "hiddencontent", 
		showDefaultTab: 1, 
		effect: 'fade', 
		effectSpeed: 'slow' 
	});

	window.open("about:blank", "taskform");
	
	var width = $(window).width() - 150;

	var t1 = $("#table1").flexigrid({
	url: "updates.htm?update=true&type=PATask",
	colModel : [
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.4, sortable : true, align: 'left'},
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_taskState"/>', name : '_state', width : width*0.05, resize : true, sortable : true, align: 'center'},
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.2, sortable : true, align: 'left'},
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_dueDate"/>', name : '_deadline', width : width*0.1, sortable : true, align: 'left', hide: false},
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_priority"/>', name : '_priority', width : width*0.1, sortable : true, align: 'center', hide: false},
	{display: 'Attachments', name : 'atts', sortable : false, width : width*0.13, align: 'center'}
	],	
	searchitems : [
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', isdefault: true}
	],
	}
	);

	var t2 = $("#table2").flexigrid({
	url: "updates.htm?update=true&type=Notification",
	colModel : [
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.4, sortable : true, align: 'left'},
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.2, sortable : true, align: 'left'},
	],	
	searchitems : [
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', isdefault: true}
	],
	}
	);
	
	var t3 = $("#table3").flexigrid({
	url: "updates.htm?update=true&type=PIPATask",
	colModel : [
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', width : width*0.4, sortable : true, align: 'left'},
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_creationDateTime"/>', name : '_creationDate', width : width*0.2, sortable : true, align: 'left'},
	],	
	searchitems : [
	{display: '<fmt:message key="com_intalio_bpms_workflow_taskHolder_description"/>', name : '_description', isdefault: true}
	],
	}
	);

	var timeout = <c:out value="${refreshTime}"/> * 1000;

	if(timeout == null || timeout < 1000) timeout = 1000;
	$.timer(timeout,function(timer) {
		t1.flexReload();
		t2.flexReload();
	});

	var current = null;
	
	// visible == hide all
	function refresh(visible) {
	if(visible==false){
	t1.parent().parent().hide();
	t2.parent().parent().hide();
	t3.parent().parent().hide();
	}
	else {
	t1.flexReload();
    t2.flexReload();

	if(current=='pa') {
		t1.parent().parent().show();
		t2.parent().parent().hide();
		t3.parent().parent().hide();
	}
	else if(current=='notif') {
		t2.parent().parent().show();
		t1.parent().parent().hide();
		t3.parent().parent().hide();
	}
	else if(current=='pipa') {
		t1.parent().parent().hide();
		t2.parent().parent().hide();
		t3.parent().parent().show();
	}
	}
	
	}

	//
	// change tab on click, refresh frame, refresh task list
	//
	$('#tabnav li a').click(function(){
		if(current==null)  $(".intro").each(function(){ $(this).hide();});
		current = $(this).attr("title");
		refresh(true);
		clearFrame();
	});


	$('#taskform').load(function(){
		var visible = $('#taskform').is(":visible")
		$('#taskform').toggle("slow");
		refresh(visible);
	});
	
	refresh(false);
	
	$.jcorners("#intro",{radius:10});

	});
</script>