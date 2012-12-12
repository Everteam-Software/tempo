/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/


var lfLocal = location.protocol + '//' + location.host
var liferayModalwindow = "";
 
///////////////// MODEL   *************************
 Ext.define('taskListModel',{
        extend: 'Ext.data.Model',
        fields: [
            // set up the fields mapping into the xml doc
            // The first needs mapping, the others are very basic
            {name: 'id'},
			{name: 'description', type: 'string'},
			{name: 'userOwner', type: 'string'},
			{name: 'roleOwner', type: 'string'},
			{name: 'creationDate', type: 'date'},
			{name: 'attachment', type: 'string'},
			{name: 'bpmsUrl', type: 'string'},
			{name: 'formUrl', type: 'string'},
			{name: 'token', type: 'string'},
        ]
    });
	
	Ext.define('notiListModel',{
        extend: 'Ext.data.Model',
        fields: [
            // set up the fields mapping into the xml doc
            // The first needs mapping, the others are very basic
            {name: 'id'},
			{name: 'description', type: 'string'},
			{name: 'userOwner', type: 'string'},
			{name: 'roleOwner', type: 'string'},
			{name: 'creationDate', type: 'date'},
			{name: 'attachment', type: 'string'},
			{name: 'bpmsUrl', type: 'string'},
			{name: 'formUrl', type: 'string'},
			{name: 'token', type: 'string'},
        ]
    });
	
	Ext.define('pipaListModel',{
        extend: 'Ext.data.Model',
        fields: [
            // set up the fields mapping into the xml doc
            // The first needs mapping, the others are very basic
            {name: 'id'},
			{name: 'description', type: 'string'},
			{name: 'userOwner', type: 'string'},
			{name: 'roleOwner', type: 'string'},
			{name: 'creationDate', type: 'date'},
			{name: 'attachment', type: 'string'},
			{name: 'bpmsUrl', type: 'string'},
			{name: 'formUrl', type: 'string'},
			{name: 'token', type: 'string'},
        ]
    });
	

///////////////// STORE   --------------------------	
var taskListStore = Ext.create('Ext.data.Store', {
		
        model: 'taskListModel',
        proxy: {
            // load using HTTP
            type: 'ajax',
            url: lfLocal+'/taskviewer/taskList.jsp',
			actionMethods: { read: 'POST' },
            // the return will be XML, so lets set up a reader
            reader: {
                type: 'xml',
                record: 'Item',
				root: 'Items'
            }
        }
    });
	
var notiListStore = Ext.create('Ext.data.Store', {
		
        model: 'notiListModel',
        proxy: {
            // load using HTTP
            type: 'ajax',
            url: lfLocal+'/taskviewer/taskList.jsp',
			actionMethods: { read: 'POST' },
            // the return will be XML, so lets set up a reader
            reader: {
                type: 'xml',
                record: 'Item',
				root: 'Items'
            }
        }
    });
	
var pipaListStore = Ext.create('Ext.data.Store', {
		
        model: 'pipaListModel',
        proxy: {
            // load using HTTP
            type: 'ajax',
            url: lfLocal+'/taskviewer/taskList.jsp',
			actionMethods: { read: 'POST' },
            // the return will be XML, so lets set up a reader
            reader: {
                type: 'xml',
                record: 'Item',
				root: 'Items'
            }
        }
    });
	

	
	
	

Ext.onReady(function(){
	
	/**
     * Custom function used for column renderer
     * @param {Object} val
     */
	 
	 
    function change(val) {
        if (val > 0) {
            return '<span style="color:green;">' + val + '</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '</span>';
        }
        return val;
    }
	
	
		function setVars(record,typ) {
			var des = record.get('description');
			var bpmsurl = record.get('bpmsUrl');
			var url = record.get('formUrl'); 
			var taskId = record.get('id');
			var tkn = record.get('token');
			var usr = Ext.get('usr').dom.value;
			openModal(true,bpmsurl, url,des,taskId,tkn,usr,typ);
			
		}
		
		function doWithSelected() {
		
		var s = gridTaskList.getSelectionModel().getSelection();
			// And then you can iterate over the selected items, e.g.: 
			selected = [];
			Ext.each(s, function (item) {
			  alert(item.data.id);
			});	
			
		}
    
	
     function openModal(closable,bpmsurl, url,des,taskId,tkn,usr,typ) {
       
     	
        // create the window on the first click and reuse on subsequent clicks
     liferayModalwindow = new Ext.Window({
	
     html: "<iframe name='liferayIframe' id='liferayIframe' src='"+lfLocal+"/taskviewer/callerhtml.jsp?bpmsUrl="+bpmsurl+"&url="+url+"&id="+taskId+"&tkn="+tkn+"&usr="+usr+"&typ="+typ+"&isIframe=true' width='100%' height='100%' frameborder='0'></iframe>",
	 title : des,
	 layout : 'fit',
	 resizable: false,
	 autoScroll:true,
	 closable: true,
	 closeAction: 'hide',
	 collapsible: true,
	 bodyStyle: 'padding:5px;',
	 modal : true,
	 maximizable : false,
	 width:800,
	 height:600,
	 maxWidth:screen.width/1.5,
	 maxHeight:screen.height/1.5,
	 listeners: {
	  'close': function(){
	           Ext.QuickTips.destroy();  
	           window.location.reload(); 
	           },
	  'render': function(c){
	      Ext.tip.QuickTipManager.init();
	      Ext.tip.QuickTipManager.register({
	      target: c.tools['close'],
	      text: 'Close'
          });
         }
	}
      }).show();    
    }
    
  function closeActiveTab() {
	var parent = Ext.get("tabpanel");
	var activeTab = parent.getActiveTab();
	var elems = parent.select("a.x-tab-close-btn").elements;
	alert(elems);
	}
	
	function eleList(listType) {
		var usuario = Ext.get('usr').dom.value;
		var password = Ext.get('psw').dom.value;
		switch (listType) {
			case 'ACTIVITY':
			taskListStore.load( { params: { 'user': usuario, 'pass': password, 'tipoList':'ACTIVITY' } });
			break;
			case 'NOTIFICATION':
			notiListStore.load( { params: { 'user': usuario, 'pass': password, 'tipoList':'NOTIFICATION' } });
			break;
			case 'INIT':
			pipaListStore.load( { params: { 'user': usuario, 'pass': password, 'tipoList':'INIT' } });
			break;
			
		}
		taskListStore.load( { params: { 'user': usuario, 'pass': password, 'tipoList':'ACTIVITY' } });
		return;
	}

/*
	Ext.createWidget('button', {
        renderTo: 'doButton',
        text: 'Open Form',
        handler: function () {
            //doWithSelected();
			//alert('asdf');
			closeActiveTab();
        },
        iconCls:'new-tab'
    });
*/	

	///////////////// VIEW   --------------------------	
var smT = Ext.create('Ext.selection.CheckboxModel');
var smN = Ext.create('Ext.selection.CheckboxModel');
var smP = Ext.create('Ext.selection.CheckboxModel');
 var gridTaskList = Ext.create('Ext.grid.Panel', {
        store: taskListStore,
		selModel: smT,
		height: 500,
		layout: 'fit',
		autoScroll: true,
		listeners : {
		   itemclick : function(view,record,item,index,eventObj) { 
		   setVars(record,'PATask');
		   }
		},
        columns: [
					 {
						 text     : 'Task Id',
						 width	:0,
						// flex     : 1,
						 sortable : false, 
						 dataIndex: 'id'
					 },
					 {
						 text     : 'Description', 
						 //width    : 75, 
						 flex     : 1,
						 sortable : true, 
						 //renderer : 'usMoney', 
						 dataIndex: 'description'
					 },
					 {
						 text     : 'User Owner', 
						 width    : 140, 
						 sortable : true, 
						//renderer : change, 
						 dataIndex: 'userOwner'
					 },
					 {
						 text     : 'Role Owner', 
						 width    : 140, 
						 sortable : true, 
						 //renderer : pctChange, 
						 dataIndex: 'roleOwner'
					 },
					 {
						 text     : 'Creation Date', 
						 width    : 160, 
						 sortable : true, 
						 renderer : Ext.util.Format.dateRenderer('M d Y h:i:s A'), 
						 dataIndex: 'creationDate'
					 },
					 {
						 text     : 'Attachment', 
						 width    : 85, 
						 sortable : true, 
						 //renderer : Ext.util.Format.dateRenderer('m/d/Y'), 
						 dataIndex: 'attachment'
					 },
					 {
						 text     : 'URL', 
						 width    : 0, 
						 sortable : true, 
						 //renderer : Ext.util.Format.dateRenderer('m/d/Y'), 
						 dataIndex: 'formUrl'
					 },
					 {
						 text     : 'BPMS URL', 
						 width    : 0, 
						 sortable : true, 
						 //renderer : Ext.util.Format.dateRenderer('m/d/Y'), 
						 dataIndex: 'bpmsUrl'
					 },
					 {
						 text     : 'Token', 
						 width    : 0, 
						 sortable : true, 
						 //renderer : Ext.util.Format.dateRenderer('m/d/Y'), 
						 dataIndex: 'token'
					 }
				 ],
				 stripeRows: true
    });
	

 var gridNotificationList = Ext.create('Ext.grid.Panel', {
        store: notiListStore,
		selModel: smN,
		height: 500,
		layout: 'fit',
		autoScroll: true,
		listeners : {
		   itemclick : function(view,record,item,index,eventObj) { 
		   setVars(record,'Notification');
		   }
		},
        columns: [
					 {
						 text     : 'Task Id',
						 width	:0,
						// flex     : 1,
						 sortable : false, 
						 dataIndex: 'id'
					 },
					 {
						 text     : 'Description', 
						 //width    : 75, 
						 flex     : 1,
						 sortable : true, 
						 //renderer : 'usMoney', 
						 dataIndex: 'description'
					 },
					 {
						 text     : 'User Owner', 
						 width    : 140, 
						 sortable : true, 
						//renderer : change, 
						 dataIndex: 'userOwner'
					 },
					 {
						 text     : 'Role Owner', 
						 width    : 140, 
						 sortable : true, 
						 //renderer : pctChange, 
						 dataIndex: 'roleOwner'
					 },
					 {
						 text     : 'Creation Date', 
						 width    : 160, 
						 sortable : true, 
						 renderer : Ext.util.Format.dateRenderer('M d Y h:i:s A'), 
						 dataIndex: 'creationDate'
					 },
					 {
						 text     : 'URL', 
						 width    : 0, 
						 sortable : true, 
						 //renderer : Ext.util.Format.dateRenderer('m/d/Y'), 
						 dataIndex: 'formUrl'
					 },
					 {
						 text     : 'BPMS URL', 
						 width    : 0, 
						 sortable : true, 
						 //renderer : Ext.util.Format.dateRenderer('m/d/Y'), 
						 dataIndex: 'bpmsUrl'
					 },
					 {
						 text     : 'Token', 
						 width    : 0, 
						 sortable : true, 
						 //renderer : Ext.util.Format.dateRenderer('m/d/Y'), 
						 dataIndex: 'token'
					 }
				 ],
				 stripeRows: true
    });
	
	var gridPipaList = Ext.create('Ext.grid.Panel', {
        store: pipaListStore,
		selModel: smP,
		height: 500,
		layout: 'fit',
		autoScroll: true,
		listeners : {
		itemclick : function(view,record,item,index,eventObj) { 
		   setVars(record,'PIPATask');
		   }
		},
        columns: [
					 {
						 text     : 'Task Id',
						 width	:0,
						// flex     : 1,
						 sortable : false, 
						 dataIndex: 'id'
					 },
					 {
						 text     : 'Description', 
						 //width    : 75, 
						 flex     : 1,
						 sortable : true, 
						 //renderer : 'usMoney', 
						 dataIndex: 'description'
					 },
					 {
						 text     : 'User Owner', 
						 width    : 140, 
						 sortable : true, 
						//renderer : change, 
						 dataIndex: 'userOwner'
					 },
					 {
						 text     : 'Role Owner', 
						 width    : 140, 
						 sortable : true, 
						 //renderer : pctChange, 
						 dataIndex: 'roleOwner'
					 },
					 {
						 text     : 'Creation Date', 
						 width    : 160, 
						 sortable : true, 
						 renderer : Ext.util.Format.dateRenderer('M d Y h:i:s A'), 
						 dataIndex: 'creationDate'
					 },
					 {
						 text     : 'URL', 
						 width    : 0, 
						 sortable : true, 
						 //renderer : Ext.util.Format.dateRenderer('m/d/Y'), 
						 dataIndex: 'formUrl'
					 },
					 {
						 text     : 'BPMS URL', 
						 width    : 0, 
						 sortable : true, 
						 //renderer : Ext.util.Format.dateRenderer('m/d/Y'), 
						 dataIndex: 'bpmsUrl'
					 },
					 {
						 text     : 'Token', 
						 width    : 0, 
						 sortable : true, 
						 //renderer : Ext.util.Format.dateRenderer('m/d/Y'), 
						 dataIndex: 'token'
					 }
				 ],
				 stripeRows: true
    });
    
  
    // second tabs built from JS
    var tabs2 = Ext.createWidget('tabpanel', {
	id: 'tabpanel',
        renderTo: 'tabs1',
        activeTab: 0,
       // width: 1080,
        height: 550,
        plain: true,
        defaults :{
            autoScroll: true,
            bodyPadding: 10
        },
        items: [{
        		id:'idTaskTab',
                title: 'Tasks List',
                listeners: {
                    activate: function(tab){
                       eleList('ACTIVITY');
                    }},
				items: gridTaskList
            },{
            	id:'idNotifTab',
                title: 'Notifications',
                listeners: {
                    activate: function(tab){
                       eleList('NOTIFICATION');
                    }
                },
                items: gridNotificationList
            },{
            	id:'idProcessTab',
				title: 'Processes',
                listeners: {
                    activate: function(tab){
                       eleList('INIT');
                    }
                },
                items: gridPipaList
            }
        ]
    });
    
    function getTabId(typ){
    	var tab = "";
    	if(typ == 'PATask'){
    		tab = 'idTaskTab';
    	}else if(typ == 'Notification'){
    		tab = 'idNotifTab';
    	}else if(typ == 'PIPATask'){
    		tab= 'idProcessTab'
    	}
    	return tab;
    }
	

});
