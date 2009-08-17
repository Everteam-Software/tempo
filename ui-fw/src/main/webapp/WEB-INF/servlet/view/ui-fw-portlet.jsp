<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ page import="org.intalio.tempo.web.ApplicationState" %>
<%@ page import="java.net.URLEncoder" %>

<% 
ApplicationState as = (ApplicationState)request.getSession().getAttribute(ApplicationState.PARAMETER_NAME);
String userName = URLEncoder.encode(as.getCurrentUser().getName());
String token = as.getCurrentUser().getToken().replace("+", "%2B");
%>

<script type="text/javascript">
//<![CDATA[
window.onload = function() {
	_startTimer(10);
}

SimpleTabEx=function(){
    var TabPanel=Ext.TabPanel;
    var tabPanel;
    return {
        init:function(){
            tabPanel=new TabPanel('tabPanel',{width:'100%',height:200});
            tabPanel.addTab('tab0','Tasks/Notifications');
            tabPanel.addTab('tab1','Processes');
            tabPanel.activate('tab0');
        }
    };
}();
Ext.onReady(SimpleTabEx.init,SimpleTabEx,true);

var GridEx={
	gridobj : '',
	dataurl : '/ui-fw/json/update?token=' + '<%= token %>' + '&amp;user=' + '<%= userName %>',
    init:function(){
        this.gridobj=this.makeGrid();
        this.gridobj.dataSource=this.makeDataSource();

        var layout=Ext.BorderLayout.create({
            center:{
                margins:{left:3,top:3,right:3,bottom:3},
                panels :[new Ext.GridPanel(this.gridobj)]
            }
        }, 'pnlGrid');

		this.gridobj.on('rowclick',_openWindow);
        this.gridobj.render();
    },
    makeDataSource:function(){

	var gv = new Ext.grid.GridView({
	            getRowClass : function (row, index) {
	                return 'error-row';
	            }
	        });    

        var ds=new Ext.data.Store({
 		     proxy: new Ext.data.HttpProxy({url: '/ui-fw/json/update?token=' + '<%= token %>' + '&user=' + '<%= userName %>' +
'&taskType=' + document.getElementById("taskType").value + '&description=' + document.getElementById("description").value}),
             reader: new Ext.data.JsonReader({
            	root :"tasks"
            }, [
            'taskUrl',
			{name: 'description', mapping: 'description'},
			{name: 'creationDate', mapping: 'creationDate'},
			'state'
            ])
        });

        ds.load();
        return ds;
    },
    makeGrid:function(){
        var colModel=new Ext.grid.ColumnModel([
            {
                header   :"Description",  
                sortable :true,
                dataIndex:'description'
            },
            {
                header   :"Creation Date/Time",
                sortable :true,
                dataIndex:'creationDate'
            },
			{
                header   :"State",
                sortable :true,
                dataIndex:'state'
            }
        ]);
         this.gridobj=new Ext.grid.Grid('grid',{
            cm:colModel,
			view: new Ext.grid.GridView ({
			getRowClass : function (record, index) {
			if(record.data.state == "CLAIMED")
			   return 'claimed';
			else
			   return 'x-grid-selected-row';
			}
			}),
	        autoSizeColumns: true
        });
                return this.gridobj;
        }
    
};

Ext.onReady(GridEx.init,GridEx,true);

var ProcessGridEx={
	gridobj : '',
	dataurl : '/ui-fw/json/update?token=' + '<%= token %>' + '&user=' + '<%= userName %>',
    init:function(){
        this.gridobj=this.makeGrid();
        this.gridobj.dataSource=this.makeDataSource();
        var layout=Ext.BorderLayout.create({
            center:{
                margins:{left:3,top:3,right:3,bottom:3},
                panels :[new Ext.GridPanel(this.gridobj)]
            }
        }, 'processPnl');
		this.gridobj.on('rowclick',_openWindow);
        this.gridobj.render();
    },
    makeDataSource:function(){
        var ds=new Ext.data.Store({
 		     proxy: new Ext.data.HttpProxy({url: '/ui-fw/json/update?token=' + '<%= token %>' + '&user=' + '<%= userName %>' +
'&amp;taskType=' + document.getElementById("taskType").value+ '&description=' + document.getElementById("description").value}),
             reader: new Ext.data.JsonReader({
            	root :"process"
            }, [
            'taskUrl',
			{name: 'description', mapping: 'description'},
			{name: 'creationDate', mapping: 'creationDate'}
            ])
        });
        ds.load();
        return ds;
    },
    makeGrid:function(){
        var colModel=new Ext.grid.ColumnModel([
            {
                header   :"Description",  
                sortable :true,
                dataIndex:'description'
            },
            {
                header   :"Creation Date/Time",
                sortable :true,
                dataIndex:'creationDate'
            }
        ]);
         this.gridobj=new Ext.grid.Grid('processGrid',{
            cm:colModel,
	        autoSizeColumns:true
        });
        	return this.gridobj;
        }
};
Ext.onReady(ProcessGridEx.init,ProcessGridEx,true);

function hideMessageBox() {
    Ext.MessageBox.hide();
}

function hideWindow() {
	EntryForm.hideWindow();
}

function _openWindow(grid,rowIndex) {
	var url = grid.getDataSource().getAt(rowIndex).data['taskUrl'];
    EntryForm.showDialog(url);
}

function _startTimer(interval) {
	var timer = new PeriodicalExecuter(_getUpdateData, interval);
}

function _searchTask(){
	GridEx.gridobj.reconfigure(GridEx.makeDataSource(),GridEx.gridobj.getColumnModel());
	ProcessGridEx.gridobj.reconfigure(ProcessGridEx.makeDataSource(),ProcessGridEx.gridobj.getColumnModel());
}

function _getUpdateData(){
    GridEx.gridobj.dataSource.load();
    ProcessGridEx.gridobj.dataSource.load();
}
//]]>
</script>
