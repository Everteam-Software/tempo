<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ page import="org.intalio.tempo.web.ApplicationState" %>


<% 
ApplicationState as = (ApplicationState)renderRequest.getPortletSession().getAttribute(ApplicationState.PARAMETER_NAME);
%>


<script type="text/javascript">
//for auto update
window.onload = function() {
	<portlet:namespace/>_startTimer(10);
}

//tab create
SimpleTabEx=function(){
    //alias
    var TabPanel=Ext.TabPanel;

    //TabPanel valiable
    var tabPanel;

    return {
        //initialize
        init:function(){
            //create TabPanel
            tabPanel=new TabPanel('tabPanel',{width:600,height:200});
            tabPanel.addTab('tab0','Tasks/Notifications');
            tabPanel.addTab('tab1','Processes');
            tabPanel.activate('tab0');
        }
    };
}();
Ext.onReady(SimpleTabEx.init,SimpleTabEx,true);

//TasksGrid
var GridEx={
	gridobj : '',
	dataurl : '/ui-fw-portlet/json/update?token=' + '<%= as.getCurrentUser().getToken() %>' + '&amp;user=' + '<%= as.getCurrentUser().getName() %>',
    //initialize
    init:function(){
        //create Grid
        this.gridobj=this.makeGrid();
        this.gridobj.dataSource=this.makeDataSource();
        
        //create Layout
        var layout=Ext.BorderLayout.create({
            center:{
                margins:{left:3,top:3,right:3,bottom:3},
                panels :[new Ext.GridPanel(this.gridobj)]
            }
        }, 'pnlGrid');

		this.gridobj.on('rowclick',<portlet:namespace/>_openWindow);
        this.gridobj.render();
    },
    //create DataSource
    makeDataSource:function(){
    
        var ds=new Ext.data.Store({
 		     proxy: new Ext.data.HttpProxy({url: '/ui-fw-portlet/json/update?token=' + '<%= as.getCurrentUser().getToken() %>' + '&user=' + '<%= as.getCurrentUser().getName() %>' +
'&taskType=' + document.getElementById("taskType").value + '&description=' + document.getElementById("description").value}),
             reader: new Ext.data.JsonReader({
            	root :"tasks"
            }, [
            'taskUrl',
			{name: 'description', mapping: 'description'},
			{name: 'creationDate', mapping: 'creationDate'}
            ])
        });
        ds.load();
        return ds;
    },

    //create Grid
    makeGrid:function(){

        //create ColumnModel
        var colModel=new Ext.grid.ColumnModel([
            {
                header   :"Description",  
                width    :260, 
                sortable :true,
                dataIndex:'description'
            },
            {
                header   :"Creation Date/Time",
                width    :200, 
                sortable :true,
                dataIndex:'creationDate'
            }
        ]);

        //create Grid
         this.gridobj=new Ext.grid.Grid('grid',{
            cm:colModel
        });
                return this.gridobj;
        }
    
};

//execute
Ext.onReady(GridEx.init,GridEx,true);



//ProcessGrid
var ProcessGridEx={
	gridobj : '',
	dataurl : '/ui-fw-portlet/json/update?token=' + '<%= as.getCurrentUser().getToken() %>' + '&user=' + '<%= as.getCurrentUser().getName() %>',
    //initialize
    init:function(){
        //create Grid
        this.gridobj=this.makeGrid();
        this.gridobj.dataSource=this.makeDataSource();
        //create Layout
        var layout=Ext.BorderLayout.create({
            center:{
                margins:{left:3,top:3,right:3,bottom:3},
                panels :[new Ext.GridPanel(this.gridobj)]
            }
        }, 'processPnl');
		this.gridobj.on('rowclick',<portlet:namespace/>_openWindow);
        this.gridobj.render();
    },
    //create DataSource
    makeDataSource:function(){
        var ds=new Ext.data.Store({
 		     proxy: new Ext.data.HttpProxy({url: '/ui-fw-portlet/json/update?token=' + '<%= as.getCurrentUser().getToken() %>' + '&user=' + '<%= as.getCurrentUser().getName() %>' +
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
    //create Grid
    makeGrid:function(){

        //create ColumnModel
        var colModel=new Ext.grid.ColumnModel([
            {
                header   :"Description",  
                width    :260, 
                sortable :true,
                dataIndex:'description'
            },
            {
                header   :"Creation Date/Time",
                width    :200, 
                sortable :true,
                dataIndex:'creationDate'
            }
        ]);
        //create Grid
         this.gridobj=new Ext.grid.Grid('processGrid',{
            cm:colModel
        });
        	return this.gridobj;
        }
};

//execute
Ext.onReady(ProcessGridEx.init,ProcessGridEx,true);

//when rowclick,Open an Entry Display. 
function <portlet:namespace/>_openWindow(grid,rowIndex) {
	var url = grid.getDataSource().getAt(rowIndex).data['taskUrl'];
    EntryForm.showDialog(url);
}

function hideMessageBox() {
    Ext.MessageBox.hide();
}

//when finish to entry some data,close window.
function hideWindow() {
	EntryForm.hideWindow();
}

//for auto update.
function <portlet:namespace/>_startTimer(interval) {
	var timer = new PeriodicalExecuter(<portlet:namespace/>_getUpdateData, interval);
}

//filter the tasks
function <portlet:namespace/>_searchTask(){
	//refesh GridEx
	GridEx.gridobj.reconfigure(GridEx.makeDataSource(),GridEx.gridobj.getColumnModel());

	//reflesh ProcessGridEx
	ProcessGridEx.gridobj.reconfigure(ProcessGridEx.makeDataSource(),ProcessGridEx.gridobj.getColumnModel());

}

//get data and refresh GridDatas. 
function <portlet:namespace/>_getUpdateData(){
	//refesh GridEx
    GridEx.gridobj.dataSource.load()
    
	//refresh ProcessGridEx
	ProcessGridEx.gridobj.dataSource.load()
}

</script>
