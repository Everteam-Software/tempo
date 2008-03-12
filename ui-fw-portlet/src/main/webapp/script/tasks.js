var startTaskDataTagString = "<taskdata>";
var endTaskDataTagString = "</taskdata>";

var startNotificationDataTagString = "<notificationdata>";
var endNotificationDataTagString = "</notificationdata>";

var startProcessDataTagString = "<processdata>";
var endProcessDataTagString = "</processdata>";


function startTimer(interval) {
	var timer = new PeriodicalExecuter(getUpdateData, interval);
}

function getUpdateData(){

	var param = 'update=true';
    new Ajax.Request("/ui-fw/updates.htm", { method: 'post', parameters: param, onComplete: updateData });

}

function updateData(httpObj){

    var data = httpObj.responseText;
    
    var startStr = data.indexOf(startTaskDataTagString);
    var endStr = data.indexOf(endTaskDataTagString);
    var taskStr = data.substring(startStr + startTaskDataTagString.length,endStr);
    $('taskdiv').innerHTML  =  taskStr;    

    startStr = data.indexOf(startNotificationDataTagString);
    endStr = data.indexOf(endNotificationDataTagString);
    var notificationStr = data.substring(startStr + startNotificationDataTagString.length,endStr);
    $('notificationdiv').innerHTML  =  notificationStr;    

    startStr = data.indexOf(startProcessDataTagString);
    endStr = data.indexOf(endProcessDataTagString);
    var processStr = data.substring(startStr + startProcessDataTagString.length,endStr);
    $('processdiv').innerHTML  =  processStr;    
}