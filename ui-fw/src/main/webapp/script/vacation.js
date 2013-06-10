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
    var isAbsenceManager = '';
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
		"sZeroRecords": "Fetching vacations..",
		"sInfo": "Showing _START_ to _END_ of _TOTAL_ vacations",
		"sInfoEmpty": "Showing 0 to 0 of 0 vacations",
		"sInfoFiltered": "(filtered from _MAX_ total vacations)",
		"sEmptyTable": "No vacations found"
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

	$('#fromdate').datepicker({
	  minDate:0,
	  dateFormat:'dd/mm/yy',
	  onSelect: function(dateText, inst) {
	    updateUsers();
	    var date = $('#fromdate').datepicker('getDate');
	    var today = new Date();
	    dayDiff = Math.ceil((date - today) / (1000 * 60 * 60 * 24));
	    $('#todate').datepicker("option", "minDate", dayDiff);
	}
	});

	$('#todate').datepicker({
	  minDate:0,
	  dateFormat:'dd/mm/yy',
	  onSelect: function(dateText, inst) { 
	  updateUsers();
	  }
	});
      });

      function clickVacationDetails() {
	  $('#vacationDetails').dialog('open');
	  getVacationData();
      }

      function getVacationData()
      {
	  var data = { action: "list" }
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

		      vacationData[i++] = data.vacs[key].id;
		      vacationData[i++] = data.vacs[key].user;
		      vacationData[i++] = $.format.date(data.vacs[key].fromDate,"dd/MM/yyyy");
		      vacationData[i++] = $.format.date(data.vacs[key].toDate,"dd/MM/yyyy");;
		      vacationData[i++] = data.vacs[key].substitute;
		      vacationData[i++] = data.vacs[key].description;

		      oTable.fnAddData(vacationData, false);
		      i = 0;
		  });
	      oTable.fnDraw(true);
	      isSubstituteMandatory = data.isSubstituteMandatory;
	      isAbsenceManager = data.isAbsenceManager;
	      if(data.isAbsenceManager != undefined && data.isAbsenceManager != 'true'){
		$('#user').combobox('disable', 'disabled');
	      }
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
	      
		      var isExist = !!$('#substitute option').filter(function() {
					return $(this).attr('value').toLowerCase() === inValidUser.toLowerCase();
				    }).length;
		      if (isExist){
			var substituteVal = $('#substitute').combobox('getvalue');
			if(substituteVal == inValidUser){
			  $('#substitute').combobox('autocomplete', '');
			  $('#substitute').val('');
			}
			$('#substitute option').filter(function() {
						return $(this).attr('value').toLowerCase() === inValidUser.toLowerCase();
					    }).remove();
			}
		});
	} 

     /**
      * @Function Name   : clickCreateVacation 
      * @Description     : This function will open vacation dailog 
      * @param           : 
      * @returns         : 
      * */
    function clickCreateVacation()
    {
      updateUsers();
      $('#fromdate').removeAttr('disabled');
      $('#user').combobox('enable');
      $('#substitute').combobox('enable');
      if(isAbsenceManager != undefined && isAbsenceManager != 'true'){
	$('#user').combobox('disable', 'disabled');
      }
      $('#vacationId').val("");
      $('#substitute').val("");
      $('#substitute').combobox('autocomplete', '');
      $('#user').val(cuser);
      $('#user').combobox('autocomplete', cuser);
      $('#fromdate').val("");
      $('#todate').val("");
      $('#desc').val("");
      $('#vacation').dialog('open');
    }

    function clickUpdateVacation( vacationTable )
    {
      updateUsers();
      var oTable = $(vacationTable).dataTable();
      var cols = fnGetSelected(oTable);
      if(cols.length<=0) {
	  $('#warnDialog').html('<a>Please select one row to update vacation.</a>');
	  $('#warnDialog').dialog('open');
	  return false;
	} else {
	    $('#fromdate').removeAttr('disabled');
	    $('#user').combobox('enable');
	    $('#substitute').combobox('enable');
	  //functionality to update vacation.
	  vac_id = cols[0];
	  $('#vacationId').val(cols[0]);
	  $('#fromdate').val(cols[2]);
	  $('#todate').val(cols[3]);
	  $('#substitute').val(cols[4]);
	  $('#substitute').combobox('autocomplete', cols[4]);
	  $('#desc').val(cols[5]);
	  $('#user').val(cols[1]);
	  $('#user').combobox('autocomplete', cols[1]);
	  var date = $('#fromdate').datepicker('getDate');
	  var today = new Date();
	  dayDiff = Math.ceil((date - today) / (1000 * 60 * 60 * 24));
	  if(dayDiff <= 0){
	    $('#fromdate').attr('disabled', 'disabled');
	    $('#user').combobox('disable', 'disabled');
	    $('#substitute').combobox('disable', 'disabled');
	  }
	  if(isAbsenceManager != undefined && isAbsenceManager != 'true'){
	    $('#user').combobox('disable', 'disabled');
	  }
	  $('#vacation').dialog('open');
        }
    }

    function clickEndVacation( vacationTable )
    {
	var oTable = $(vacationTable).dataTable();
	var cols = fnGetSelected(oTable);
	if (cols.length<=0) {
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

    function saveVacation() {
       if($('#substitute').val() == $('#user').val()){
	    $('#warnDialog').html('<a>Please select different substitute. User and substitute can not be same.</a>');
	    $('#warnDialog').dialog('open');
	    return false;
	  } else {
	    if($('#vacationId').val() == undefined || $('#vacationId').val().trim() == ''){
	      insertVacation();
	    } else {
	      updateVacation();
	    }
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
	  if(isValidDate("fromdate","todate") && isValidUser("user") && isValidSubstitute("substitute") && isValidDesc("desc"))
	    {
			    var data = { action:"insertVacation",fromDate: $('#fromdate').val(), toDate: $('#todate').val(),desc: $('#desc').val(),substitute: $('#substitute').val(),user: $('#user').val()}
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
					    $('#messageDialog').html('<a >Vacation details are succesfully saved. please note user claimed task(s) will not be auto assigned to substitute.</a>');
				    } else if(data.message.indexOf("Invalid Vacation Dates")>=0) {
					    $('#messageDialog').html('<a>Invalid vacation Dates , Please change Dates.</a>');
				    } else if(data.message.indexOf("Invalid Substitute")>=0) {
					    $('#messageDialog').html('<a>Substitute not avilable at selected time, Please change Sustitute.</a>');
				    } else {
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
	  if(isValidDate("fromdate","todate") && isValidUser("user") && isValidSubstitute("substitute") && isValidDesc("desc"))
	    {
			    var data = { action:"editVacation",id:vac_id,fromDate: $('#fromdate').val(), toDate: $('#todate').val(),desc: $('#desc').val(),substitute: $('#substitute').val(),user: $('#user').val()}
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
					    $('#messageDialog').html('<a >Vacation details are succesfully saved. please note your claimed task(s) will not be auto assigned to your substitute.</a>');
				    } else if(data.message.indexOf("Invalid Vacation Dates")>=0) {
					    $('#messageDialog').html('<a>Invalid vacation Dates , Please change Dates.</a>');
				    } else if(data.message.indexOf("Invalid Substitute")>=0) {
					    $('#messageDialog').html('<a>Substitute not avilable at selected time, Please change Sustitute.</a>');
				    } else {
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
      var substituteVal = $('#substitute').combobox('getvalue');
      if (($.trim(document.getElementById(substitute).value)== '' || $.trim(substituteVal) == '' ) && isSubstituteMandatory == 'true' ) {
                $("#warnDialog").html('<a >Please select substitute, should not be empty</a>');
                $("#warnDialog").dialog('open');
                return false;
       }
       if ($.trim(document.getElementById(substitute).value) != $.trim(substituteVal) && isSubstituteMandatory == 'true'  ) {
                $("#warnDialog").html('<a >Please select valid substitute.</a>');
                $("#warnDialog").dialog('open');
                return false;
       }
       return true;
    }

    function isValidUser(user) 
    {
      var userVal = $('#user').combobox('getvalue');
      if ($.trim(document.getElementById(user).value)== '' || $.trim(userVal) == '') {
                $("#warnDialog").html('<a >Please select user, should not be empty</a>');
                $("#warnDialog").dialog('open');
                return false;
       }
       if ($.trim(document.getElementById(user).value) != $.trim(userVal) ) {
                $("#warnDialog").html('<a >Please select valid user.</a>');
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
                } else if (validatedates(varFrom, varTo) != true) {
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
		  returnval = true;
	  }
      return returnval									
    }

    /**
    * @Function Name   : validatedates
    * @Description     : This function will Validate for valid date range
    * @param           : from date and to date Refrences
    * @returns         : boolean
    * */
    function validatedates(varFrom, varTo) {
	  var returnval = false
	  var fromdate = $('#'+varFrom).datepicker('getDate');
	  var todate = $('#'+varTo).datepicker('getDate');
	  var diff = Math.ceil((todate - fromdate) / (1000 * 60 * 60 * 24));
	  if (diff >= 0){
	    returnval = true;
	  } else {
	    $("#warnDialog").html('<a >Invalid date range detected, Please correct and submit again</a>');
	    $("#warnDialog").dialog('open');
	  }
      return returnval;
    }

    function updateUsers() {
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
		  SOAPClient.SendRequest(sr, populateUsers);
    }

    function populateUsers(data) {
      $(data.responseXML).find('*').filterNode("rbac:user").each(function(){
	var user = $(this).text();
	var isExist = !!$('#substitute option').filter(function() {
	    return $(this).attr('value').toLowerCase() === user.toLowerCase();
	}).length;
	if (!isExist && user != cuser){
	      var option = "<option value=\""+user+"\">"+user+"</option>";
	      $("#substitute").append(option);
	  }
	var isExistUser = !!$('#user option').filter(function() {
	return $(this).attr('value').toLowerCase() === user.toLowerCase();
	}).length;
	if (!isExistUser && user != cuser){
	      var option = "<option value=\""+user+"\">"+user+"</option>";
	      $("#user").append(option);
	  }
	});
      getMatchedVacationData();
      var substituteVal = $('#substitute').combobox('getvalue');
      $('#substitute option').filter(function() {
	    return $(this).attr('value').toLowerCase() === substituteVal.toLowerCase();
	}).attr("selected",true);
      var userVal = $('#user').combobox('getvalue');
      $('#user option').filter(function() {
	    return $(this).attr('value').toLowerCase() === userVal.toLowerCase();
	}).attr("selected",true);
    }

    (function( $ ) {
		$.widget( "custom.combobox", {
			_create: function() {
				this.wrapper = $( "<span>" )
					.addClass( "custom-combobox" )
					.insertAfter( this.element );

				this.element.hide();
				this._createAutocomplete();
				this._createShowAllButton();
			},

			_createAutocomplete: function() {
				var selected = this.element.children( ":selected" ),
					value = selected.val() ? selected.text() : "";
				this.input = $( "<input>" )
					.appendTo( this.wrapper )
					.val( value )
					.attr( "title", "" )
					.addClass( "custom-combobox-input ui-widget ui-widget-content ui-corner-left" )
					.autocomplete({
						delay: 0,
						minLength: 0,
						max:3,
						source: $.proxy( this, "_source" )
					});

				this._on( this.input, {
					autocompleteselect: function( event, ui ) {
						ui.item.option.selected = true;
						this._trigger( "select", event, {
							item: ui.item.option
						});
					},

					autocompletechange: "_removeIfInvalid"
				});
			},

			_createShowAllButton: function() {
				var input = this.input,
					wasOpen = false;

				$( "<a>" )
					.attr( "id", 'a'+this.element.attr('id') )
					.attr( "tabIndex", -1 )
					.attr( "title", "Show All Items" )
					.appendTo( this.wrapper )
					.button({
						icons: {
							primary: "ui-icon-triangle-1-s"
						},
						text: false
					})
					.removeClass( "ui-corner-all" )
					.addClass( "custom-combobox-toggle ui-corner-right" )
					.mousedown(function() {
						wasOpen = input.autocomplete( "widget" ).is( ":visible" );
					})
					.click(function() {
						input.focus();

						// Close if already visible
						if ( wasOpen ) {
							return;
						}

						// Pass empty string as value to search for, displaying all results
						input.autocomplete( "search", "" );
					});
			},

			_source: function( request, response ) {
				var matcher = new RegExp( $.ui.autocomplete.escapeRegex(request.term), "i" );
				response( this.element.children( "option" ).map(function() {
					var text = $( this ).text();
					if ( this.value && ( !request.term || matcher.test(text) ) )
						return {
							label: text,
							value: text,
							option: this
						};
				}) );
			},
			_removeIfInvalid: function( event, ui ) {
				// Selected an item, nothing to do
				if ( ui.item ) {
					return;
				}
				// Search for a match (case-insensitive)
				var value = this.input.val(),
					valueLowerCase = value.toLowerCase(),
					valid = false;
				this.element.children( "option" ).each(function() {
					if ( $( this ).text().toLowerCase() === valueLowerCase ) {
						this.selected = valid = true;
						return false;
					}
				});

				// Found a match, nothing to do
				if ( valid ) {
				  return;
				}

				// Remove invalid value
				this.input.focus();
				this.element.val( "" );
				this.input.val( "" );
				this.input.autocomplete( "instance" ).term = "";
			},

			_destroy: function() {
				this.wrapper.remove();
				this.element.show();
			},
			autocomplete : function(value) {
			    this.element.val(value);
			    this.input.val(value);
			},
			disable : function(value) {
			  this.input.attr( "disabled", value )
			  .addClass('ui-autocomplete-disabled')
			  .autocomplete({ disabled: true })
			  .autocomplete( "disable" );
			  $('#a'+this.element.attr('id')).unbind()
			  .css("cursor","default")
			  .attr( "title", "" );
			},
			enable : function() {
			  var wasOpen = false;
			  var input = this.input;
			  this.input.button("enable")
			  .propAttr( "disabled", false )
			  .removeClass('ui-autocomplete-disabled')
			  .autocomplete("enable")
			  .autocomplete({ disabled: false });
			  $('#a'+this.element.attr('id')).css("cursor","auto")
			  .button({
						icons: {
							primary: "ui-icon-triangle-1-s"
						},
						text: false
					})
			  .addClass( "custom-combobox-toggle ui-corner-right" )
			  .addClass( "custom-combobox" )
			  .attr( "title", "Show All Items" )
			  .mousedown(function() {
						wasOpen = input.autocomplete( "widget" ).is( ":visible" );
					})
			  .click(function() {
				  input.focus();

				  // Close if already visible
				  if ( wasOpen ) {
					  return;
				  }

				  // Pass empty string as value to search for, displaying all results
				  input.autocomplete( "search", "" );
			  });
			},
			getvalue : function() {
			  return this.input.val();
			}
		});
	})( jQuery );

	$(function() {
		$( "#substitute" ).combobox();
		$( "#user" ).combobox();
	});