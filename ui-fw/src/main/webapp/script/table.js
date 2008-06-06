function setCurrentPage(tableID, number) {
	document.getElementById(tableID+'_currentPage').value = number;
	submitAction('default');
}
function setPageSize(tableID, size) {
	document.getElementById(tableID+'_pageSize').value = size;
	submitAction('default');
}
function sortBySubmit(fieldName) {
	sortByObj = document.getElementById('sortBy');
	orderByObj = document.getElementById('orderBy');
	if(fieldName==sortByObj.value) {
		if(orderByObj.value=='asc') {
			orderByObj.value = 'desc';
		} else {
			orderByObj.value = 'asc';
		}
	} else {
		if (fieldName == 'name' ) orderByObj.value = 'asc';
		if (fieldName == 'status' ) orderByObj.value = 'asc';		
		if (fieldName == 'started' ) orderByObj.value = 'desc';
		if (fieldName == 'last-active' ) orderByObj.value = 'desc';

	}
	
	sortByObj.value=fieldName;
	submitAction('default');
}

function sortStatistics(fieldName) {
	sortByObj = document.getElementById('sortBy');
	orderByObj = document.getElementById('orderBy');
	if(fieldName==sortByObj.value) {
		if(orderByObj.value=='asc') {
			orderByObj.value = 'desc';
		} else {
			orderByObj.value = 'asc';
		}
	}
	sortByObj.value=fieldName;
	document.getElementById('form').submit();
}

function changeStatisticsEnabled(statisticsEnabled) {
	if (statisticsEnabled.checked) {
		document.getElementById('enabled').value = "enable";
	} else {
		document.getElementById('enabled').value = "disable";
	}
	
	document.getElementById('form').submit();
}