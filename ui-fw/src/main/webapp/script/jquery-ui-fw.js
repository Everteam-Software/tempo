$(document).ready(function(){ 

	function image_notification( image ) {
		$("#message").append("<img src='"+image+"' height='20' widht='20'/>");
		$.timer(10000, function(timer){
			$("#message").html("");
			timer.stop();
		});
	}

	function filter() {
		$.uiTableFilter( $('.tasks'), $('#filter').val() );
	}

	updateTable = function(tbody, data, data1, icons) {
		var newdata = $('<div/>').html(data).find(data1).html();
		if($(tbody).html() != newdata) {
			$(tbody).html(newdata);
			filter();
			if(icons) {
				if(tbody == "#pabody") {image_notification('images/task.png');}
				if(tbody == "#notifbody") {image_notification('images/notification-icon.gif');}
			}
		}
	}

	function clearFrame() {
		window.open("about:blank", "taskform");
	}

	function getTasks( icons ) {
		$.ajax({
			url: 'updates.htm?update=true',
			type: 'POST',
			timeout: 5000,
			error: function(xml){
				image_notification('images/error.png');
			},
			success: function(data){
				updateTable("#pabody", data, "#padata",icons);
				updateTable("#notifbody", data, "#notifdata", icons);
				updateTable("#pipabody", data, "#pipadata", icons);
			}
		});

	};

	$('#tabnav li a').click(function(){
		clearFrame();
		$('#filter').val("")
		filter();
	});

	$("#filter").keyup(function() {
		filter();
	})


	$.jtabber({
		mainLinkTag: "#container li a", 
		activeLinkClass: "active", 
		hiddenContentClass: "hiddencontent", 
		showDefaultTab: 1, 
		effect: 'fade', 
		effectSpeed: 'slow' 
	});

	getTasks(false);
	clearFrame();

	function update() {getTasks(true);}
	if(timeout == null || timeout < 1000) timeout = 1000;
	$.timer(timeout, update);
});