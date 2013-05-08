/**
 * Copyright (c) 2005-2008 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 *
 * $Id: $
 * $Log:$
 */

    var oTable;
    var vac_id;
    var dayDiff = 0;
    var isSubstituteMandatory = '';
    $(document).ready(function () {
      
	oTable = $('#vacationtable').dataTable({
	    "bFilter": false,
	    "bPaginate": false,
	    "sScrollX": $(window).width() - 440,
	    "sScrollY": $(window).height() - 335,
	    "aaData": [],
	    "bStateSave": true,
	    "bProcessing": true,
	    "bSearchable": true,
	    "bInfo": true,
	    "oLanguage": {
		"sZeroRecords": "No vacations found",
		"sInfo": "Showing _START_ to _END_ of _TOTAL_ vacations",
		"sInfoEmpty": "Showing 0 to 0 of 0 vacations",
		"sInfoFiltered": "(filtered from _MAX_ total vacations)",
		"sEmptyTable": "Fetching vacations.."
	    },
	    "aoColumns": [{
		"bSortable": true,
		"sClass": "hidden_text",
		"bVisible":    true
	    }, {
		"bSearchable": true,
		"bSortable": true,
		"sClass": "left_text"
	    }, {
		"bSearchable": true,
		"bSortable": true,
		"sClass": "left_text"
	    }, {
		"bSearchable": true,
		"bSortable": true,
		"sClass": "left_text"
	    }, {
		"bSearchable": true,
		"bSortable": true,
		"sClass": "left_text"
	    }, {
		"bSearchable": true,
		"bSortable": true,
		"sClass": "left_text"
	    }]

	});
	
	/* Add a click handler to the rows - this could be used as a callback */
	$("#vacationtable tbody").live("click",function(event) {
		$(oTable.fnSettings().aoData).each(function (){
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');
	});
	
	/* Add a change handler to the substitute select */
	$('#substitute_select').change(function() {
	  $('#substitute').val($('#substitute_select').val());
	});
	    
	$('#fromdate').datepicker({
	  minDate:0,
	  dateFormat:'dd/mm/yy',
	  onSelect: function(dateText, inst) { 
	    $('#substitute_select').attr('disabled', 'disabled');
	    var date = $('#fromdate').datepicker('getDate');
	    var today = new Date();
	    dayDiff = Math.ceil((date - today) / (1000 * 60 * 60 * 24));
	    $('#todate').datepicker("option", "minDate", dayDiff);
	    $('#todate').removeAttr('disabled');
	}
	});
	    
	$('#todate').datepicker({
	  minDate:0,
	  dateFormat:'dd/mm/yy',
	  onSelect: function(dateText, inst) { 
		  $('#substitute_select').attr('disabled', 'disabled');
		  updateSubstituteUsers(); 
		  $('#substitute_select').removeAttr('disabled');
	  }
	});
      });

      function clickVacationDetails() {
	  $('#vacationDetails').dialog('open');
	  getVacationData();
      }
    
      function getVacationData()
      {
	
	      var data = {
		      action: "list"
		  }
			      var url = 'vacation.htm';
			      $.ajax({
				      url: url,
				      cache: false,
				      async: false,
				      dataType: 'json',
				      data: data,
				      error: function (e) {
				      },
				      success: function (data) {
						  showVacation(data);
						}	
			      });
      } 
      
      function getMatchedVacationData()
      {
	if($('#fromdate').val() != '' && $('#todate').val() != '')
	  {
	    var data = {
			action: "match",fromDate: $('#fromdate').val(), toDate: $('#todate').val()
		}
	    var url = 'vacation.htm';
	    $.ajax({
		    url: url,
		    cache: false,
		    async: false,
		    dataType: 'json',
		    data: data,
		    error: function (e) {
		    },
		    success: function (data) {
				populateValidSubstitutes(data);
			      }	
	    });
	  }
      } 

      /* * @Function Name : populateValidSubstitutes 
      * @Description      : populates only valid users(who are not on vacation in between selected dates) in substitute 	*		      select box.
      * @param            : data : Response of AJAX call.
      * @returns          :  
      * */
      function populateValidSubstitutes(data)
      {
	      var vacationData = new Array(10);
	      $.each(data.vacs, function (key, value) {
		      var inValidUser = data.vacs[key].user;
	      
		      var isExist = !!$('#substitute_select option').filter(function() {
					return $(this).attr('value').toLowerCase() === inValidUser.toLowerCase();
				    }).length;
		      if (isExist){
			
			$('#substitute_select option').filter(function() {
						return $(this).attr('value').toLowerCase() === inValidUser.toLowerCase();
					    }).remove();
			}
		});
	} 
	
      /* * @Function Name : showVacation 
      * @Description     : Displays the vacation sumamry to admin users.
      * @param           : data : Response of AJAX call.
      * @returns         :  
      * */
      function showVacation(data)
      {
	      var vacationData = new Array(10);
	      var oTable = $('#vacationtable').dataTable();
	      oTable.fnClearTable();
	      var i = 0;
	      $.each(data.vacs, function (key, value) {
		      vacationData[i] = data.vacs[key].id;
		      i++;
		      vacationData[i] = data.vacs[key].user;
		      i++;
		      vacationData[i] = $.format.date(data.vacs[key].fromDate,"dd/MM/yyyy");
		      i++;
		      vacationData[i] = $.format.date(data.vacs[key].toDate,"dd/MM/yyyy");;
		      i++;
		      vacationData[i] = data.vacs[key].substitute;
		      i++;
		      vacationData[i] = data.vacs[key].description;
		      
		      oTable.fnAddData(vacationData, false);
		      i = 0;
		  });
	      oTable.fnDraw(true);
	      isSubstituteMandatory = data.isSubstituteMandatory;
	      if(data.isAbsenceManager != undefined && data.isAbsenceManager == 'true'){
		$('#proxyUserButton').show();
	      }
      }    
      
     /**
      * @Function Name   : clickCreateVacation 
      * @Description     : This function will open vacation dailog 
      * @param           : 
      * @returns         : 
      * */
    function clickCreateVacation()
    {
      updateSubstituteUsers();
      $('#vacationId').val("");
      $('#proxyuser').val("");
      $('#substitute').val("");
      $('#fromdate').val("");
      $('#todate').val("");
      $('#desc').val("");
      $('#substitute_select').attr('disabled', 'disabled');
      $('#todate').attr('disabled', 'disabled');
      $("#proxyUserId").css("display", "none");
      $('#vacation').dialog('open');
    }
    
    /**
      * @Function Name   : clickCreateVacation 
      * @Description     : This function will open vacation dailog 
      * @param           : 
      * @returns         : 
      * */
    function clickCreateProxyVacation()
    {
      updateSubstituteUsers();
      $('#vacationId').val("");
      $('#substitute').val("");
      $('#proxyuser').val("");
      $('#fromdate').val("");
      $('#todate').val("");
      $('#desc').val("");
      $('#substitute_select').attr('disabled', 'disabled');
      $('#todate').attr('disabled', 'disabled');
      $("#proxyUserId").css("display", "");
      $('#vacation').dialog('open');
    }
    
    function clickEndVacation(vacationTable)
    {
	var oTable = $(vacationTable).dataTable();
	var cols = fnGetSelected(oTable);
      
    if(cols.length<=0)
      {
        $('#warnDialog').html('<a>Please select one vacation row to end vacation.</a>');
        $('#warnDialog').dialog('open');
        return false;
      }else {
	//functionality to end vacation.
	vac_id = cols[0];
	$('#endVacDialog').html('<a> Applied for leave <br>From &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: ' +cols[2]+'<br>  To &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:  '+cols[3]+'<br> Description  :  ' +cols[5]+ '<br>If your vacation is completed please click on End Vacation</a>');
	$('#endVacDialog').dialog('open');
    }
    }
    
   function clickUpdateVacation(vacationTable)
    {
      var oTable = $(vacationTable).dataTable();
      var cols = fnGetSelected(oTable);
    if(cols.length<=0)
      {
        $('#warnDialog').html('<a>Please select one row to update vacation.</a>');
        $('#warnDialog').dialog('open');
        return false;
      }
      else {
	  //functionality to update vacation.
	  vac_id = cols[0];
	  $('#vacationId').val(cols[0]);
	  $('#fromdate').val(cols[2]);
	  $('#todate').val(cols[3]);
	  $('#substitute').val(cols[4]);
	  $('#substitute_select').val(cols[4]);
	  $('#desc').val(cols[5]);
	  $('#substitute_select').attr('disabled', 'disabled');
	  $('#todate').attr('disabled', 'disabled');
	  $("#proxyUserId").css("display", "none");
	  $('#proxyuser').val("");
	  updateSubstituteUsers();
      	  $('#vacation').dialog('open');
	}
    }
    
    function fnGetSelected( oTableLocal )
    {
	    var cols = new Array();
	    var aTrs = oTableLocal.fnGetNodes();
	    
	    for ( var i=0 ; i<aTrs.length ; i++ )
	    {
		    if ( $(aTrs[i]).hasClass('row_selected') )
		    {
		      cols = oTableLocal.fnGetData(aTrs[i]);
		    }
	    }
	    return cols;
    }
 
    function saveVacation()
	{
	  if($('#vacationId').val() == undefined || $('#vacationId').val().trim() == ''){
	    if($('#proxyuser').val().trim() == '')
	    insertVacation();
	    else
	    insertProxyVacation();  
	  }else{
	    updateVacation();
	  }
    }
    
    /**
    * @Function Name   : insertVacation 
    * @Description     : This function will make an ajax call to save the data of vacation management
    * @param           : 
    * @returns         : 
    * */
    function insertVacation()
	{	
		    
	  if(isValidDate("fromdate","todate") && isValidSubstitute("substitute") && isValidDesc("desc"))
	    {
			    var data = { action:"insertVacation",fromDate: $('#fromdate').val(), toDate: $('#todate').val(),desc: $('#desc').val(),substitute: $('#substitute').val()}
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
					    getVacationData();
					    $('#vacation').dialog('close');
					    $('#messageDialog').html('<a >Vacation details are succesfully saved please make sure you also reassign your task before going on leave.</a>');
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
    * @Function Name   : insertProxyVacation 
    * @Description     : This function will make an ajax call to save the data of vacation management
    * @param           : 
    * @returns         : 
    * */
    function insertProxyVacation()
	{	
	  if(isValidDate("fromdate","todate") && isValidUser("proxyuser") && isValidSubstitute("substitute") && isValidDesc("desc"))
	    {
			    var data = { action:"insertProxyVacation",fromDate: $('#fromdate').val(), toDate: $('#todate').val(),desc: $('#desc').val(),substitute: $('#substitute').val(),user: $('#proxyuser').val()}
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
				    if(data.message != undefined && data.message.indexOf("Inserted")>=0)
				    {
					    getVacationData();
					    $('#vacation').dialog('close');
					    $('#messageDialog').html('<a >Vacation details are succesfully saved.</a>');
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
    * @Function Name   : updateVacation 
    * @Description     : This function will make an ajax call to update the data of vacation management
    * @param           : 
    * @returns         : 
    * */
	function updateVacation()
	{	
		    
	  if(isValidDate("fromdate","todate") && isValidSubstitute("substitute") && isValidDesc("desc"))
	    {
			    var data = { action:"editVacation",id:vac_id,fromDate: $('#fromdate').val(), toDate: $('#todate').val(),desc: $('#desc').val(),substitute: $('#substitute').val()}
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
				    if(data.message.indexOf("Updated")>=0)
				    {
					    getVacationData();
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
    * @Function Name   : endVacation 
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
				    getVacationData();
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
 
    function isValidDesc(desc) 
    {
      if ($.trim(document.getElementById(desc).value)== '') {
                $("#warnDialog").html('<a >Description should not be empty</a>');
                $("#warnDialog").dialog('open');
                return false;
       }
       return true;
    }
    
    function isValidSubstitute(substitute) 
    {
      if ($.trim(document.getElementById(substitute).value)== '' && isSubstituteMandatory == 'true') {
                $("#warnDialog").html('<a >Please select substitute, should not be empty</a>');
                $("#warnDialog").dialog('open');
                return false;
       }
       return true;
    }
    
    function isValidUser(user) 
    {
      if ($.trim(document.getElementById(user).value)== '') {
                $("#warnDialog").html('<a >Please select user, should not be empty</a>');
                $("#warnDialog").dialog('open');
                return false;
       }
       return true;
    }
     
    /**
    * @Function Name   : isValidDate 
    * @Description     : This function will Validate to date,from date & description
    * @param           : Refrence of fromDate , toDate,Description field of vacation
    * @returns         : 
    * */
    function isValidDate(varFrom, varTo) 
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
            else if (varFrom != null && document.getElementById(varFrom).value != '' && varTo != null && document.getElementById(varTo).value!= '') {
                if (checkdate(chkFrom) != true) {
                    document.getElementById(varFrom).value = '';
                    return false;
                }
                else if (checkdate(chkTo) != true) {
                    document.getElementById(varTo).value = '';
                    return false;
                }
                else 
                return true;
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
    function updateSubstituteUsers() {
      $('#substitute_select').empty();
      var firstOption="<option value=''>Select user </option>";
      $("#substitute_select").append(firstOption);      
      $.each(cUserRoles, function(i, value) {
	      callGetAssignedUsers(value);
	});
    }

    function callGetAssignedUsers(role) {
      
		  var soapBody     = new SOAPObject("getAssignedUsers");
		  soapBody.ns      = "http://tempo.intalio.org/security/RBACQueryService/";
		  soapBody.appendChild(new SOAPObject("role")).val(role);
		  var sr           = new SOAPRequest("http://tempo.intalio.org/security/RBACQueryService/getAssignedUsers", soapBody);
		  SOAPClient.Proxy = proxy;
		  SOAPClient.SOAPServer = rbacService;
		  SOAPClient.SendRequest(sr, populateSubstituteUsers);
    }

    function populateSubstituteUsers(data) {
      $(data.responseXML).find('*').filterNode("rbac:user").each(function(){  
	var user = $(this).text();
	var isExist = !!$('#substitute_select option').filter(function() {
	    return $(this).attr('value').toLowerCase() === user.toLowerCase();
	}).length;
	if (!isExist && user != cuser){
	      var option = "<option value=\""+user+"\">"+user+"</option>";
	      $("#substitute_select").append(option);
	  }
	});
      getMatchedVacationData();
      $('#substitute_select option').filter(function() {
	    return $(this).attr('value').toLowerCase() === $('#substitute').val().toLowerCase();
	}).attr("selected",true);
      
    }