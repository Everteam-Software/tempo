<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ page import="javax.portlet.PortletSession" %>
<%@ page import="javax.portlet.PortletURL" %>
<%@ page import="org.intalio.tempo.web.ApplicationState" %>
<%@ page session="false" %>

<portlet:defineObjects/>
    <!--js Liblary-->
    <script type="text/javascript" src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/script/adapter/ext/ext-base.js") %>'></script>
    <script type="text/javascript" src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/script/ext-all.js") %>'></script>
    <script type="text/javascript" src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/script/prototype.js") %>'></script>
    <script type="text/javascript" src='<%=renderResponse.encodeURL(renderRequest.getContextPath() + "/script/entry.js") %>'></script>


<% 
	ApplicationState as = (ApplicationState)renderRequest.getAttribute(ApplicationState.PARAMETER_NAME);
%>
<div id="token" style="visibility:hidden;">
<%= as.getCurrentUser().getToken() %>
</div>
<div id="user" style="visibility:hidden;">
<%= as.getCurrentUser().getName() %>
</div>
<script type="text/javascript" >
	var ptoken = document.getElementById("token").innerText;
	var puser = document.getElementById("user").innerText;
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
	dataurl : '/ui-fw/json/update?token=' + ptoken + '&user=' + puser,
    //initialize
    init:function(){
    	ptoken = document.getElementById("token").innerText;
		puser = document.getElementById("user").innerText;
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
 		     proxy: new Ext.data.HttpProxy({url: this.dataurl}),
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
	dataurl : '/ui-fw/json/update?token=' + ptoken + '&user=' + puser,
    //initialize
    init:function(){
    	ptoken = document.getElementById("token").innerText;
    	puser = document.getElementById("user").innerText;
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
 		     proxy: new Ext.data.HttpProxy({url: this.dataurl}),
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
	//Ext.MessageBox.wait('Now Loading....');
    EntryForm.showDialog(url);
    //Ext.MessageBox.updateProgress(0.15);
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

//get data and reflesh GridDatas. 
function <portlet:namespace/>_getUpdateData(){
	//refesh GridEx
    GridEx.gridobj.reconfigure(GridEx.makeDataSource(),GridEx.gridobj.getColumnModel());
    
	//reflesh ProcessGridEx
    ProcessGridEx.gridobj.reconfigure(ProcessGridEx.makeDataSource(),ProcessGridEx.gridobj.getColumnModel());
}

</script>

<style type="text/css">
html,body,div,dl,dt,dd,ul,ol,li,h1,h2,h3,h4,h5,h6,pre,form,fieldset,input,p,blockquote,th,td{margin:0;padding:0;}
img,body,html{border:0;}
address,caption,cite,code,dfn,em,strong,th,var{font-style:normal;font-weight:normal;}
ol,ul{list-style:none;}
caption,th{text-align:left;}
h1,h2,h3,h4,h5,h6{font-size:100%;}
q:before,q:after{content:'';}

.ext-el-mask{z-index:20000;position:absolute;top:0;left:0;-moz-opacity:0.5;opacity:.50;filter:alpha(opacity=50);background-color:#CCC;width:100%;height:100%;zoom:1;}
.ext-el-mask-msg{z-index:20001;position:absolute;top:0;left:0;border:1px solid #6593cf;background:#c3daf9 url(/ui-fw/resources/images/default/box/tb-blue.gif) repeat-x 0 -16px;padding:2px;}
.ext-el-mask-msg div{padding:5px 10px 5px 10px;background:#eee;border:1px solid #a3bad9;color:#333;font:normal 12px tahoma,arial,helvetica,sans-serif;cursor:wait;}
.ext-shim{position:absolute;visibility:hidden;left:0;top:0;overflow:hidden;}
.ext-ie .ext-shim{filter:alpha(opacity=0);}
.x-mask-loading div{padding:5px 10px 5px 25px;background:#eee url( '/ui-fw/resources/images/default/grid/loading.gif' ) no-repeat 5px 5px;line-height:16px;}
.x-hidden{position:absolute;left:-10000px;top:-10000px;}
.x-masked{overflow:hidden!important;}
.x-masked select,.x-masked object,.x-masked embed{visibility:hidden;}
.x-layer{visibility:hidden;}
.x-unselectable,.x-unselectable *{-moz-user-select:none;-khtml-user-select:none;}
.x-repaint{zoom:1;background-color:transparent;-moz-outline:none;}
.x-item-disabled{color:gray;cursor:default;opacity:.6;-moz-opacity:.6;filter:alpha(opacity=60);}
.x-item-disabled *{color:gray;cursor:default!important;}
.x-splitbar-proxy{position:absolute;visibility:hidden;z-index:20001;background:#aaa;zoom:1;line-height:1px;font-size:1px;overflow:hidden;}
.x-splitbar-h,.x-splitbar-proxy-h{cursor:e-resize;cursor:col-resize;}
.x-splitbar-v,.x-splitbar-proxy-v{cursor:s-resize;cursor:row-resize;}
.x-color-palette{width:150px;height:92px;cursor:pointer;}
.x-color-palette a{border:1px solid #fff;float:left;padding:2px;text-decoration:none;-moz-outline:0 none;outline:0 none;cursor:pointer;}
.x-color-palette a:hover,.x-color-palette a.x-color-palette-sel{border:1px solid #8BB8F3;background:#deecfd;}
.x-color-palette em{display:block;border:1px solid #ACA899;}
.x-color-palette em span{cursor:pointer;display:block;height:10px;line-height:10px;width:10px;}
.x-ie-shadow{display:none;position:absolute;overflow:hidden;left:0;top:0;background:#777;zoom:1;}
.x-shadow{display:none;position:absolute;overflow:hidden;left:0;top:0;}
.x-shadow *{overflow:hidden;}
.x-shadow *{padding:0;border:0;margin:0;clear:none;zoom:1;}
.x-shadow .xstc,.x-shadow .xsbc{height:6px;float:left;}
.x-shadow .xstl,.x-shadow .xstr,.x-shadow .xsbl,.x-shadow .xsbr{width:6px;height:6px;float:left;}
.x-shadow .xsc{width:100%;}
.x-shadow .xsml,.x-shadow .xsmr{width:6px;float:left;height:100%;}
.x-shadow .xsmc{float:left;height:100%;background:transparent url( /ui-fw/resources/images/default/shadow-c.png );}
.x-shadow .xst,.x-shadow .xsb{height:6px;overflow:hidden;width:100%;}
.x-shadow .xsml{background:transparent url( /ui-fw/resources/images/default/shadow-lr.png ) repeat-y 0 0;}
.x-shadow .xsmr{background:transparent url( /ui-fw/resources/images/default/shadow-lr.png ) repeat-y -6px 0;}
.x-shadow .xstl{background:transparent url( /ui-fw/resources/images/default/shadow.png ) no-repeat 0 0;}
.x-shadow .xstc{background:transparent url( /ui-fw/resources/images/default/shadow.png ) repeat-x 0 -30px;}
.x-shadow .xstr{background:transparent url( /ui-fw/resources/images/default/shadow.png ) repeat-x 0 -18px;}
.x-shadow .xsbl{background:transparent url( /ui-fw/resources/images/default/shadow.png ) no-repeat 0 -12px;}
.x-shadow .xsbc{background:transparent url( /ui-fw/resources/images/default/shadow.png ) repeat-x 0 -36px;}
.x-shadow .xsbr{background:transparent url( /ui-fw/resources/images/default/shadow.png ) repeat-x 0 -6px;}
.loading-indicator{font-size:11px;background-image:url( '/ui-fw/resources/images/default/grid/loading.gif' );background-repeat:no-repeat;background-position:left;padding-left:20px;line-height:16px;margin:3px;}
.x-text-resize{position:absolute;left:-1000px;top:-1000px;visibility:hidden;zoom:1;}
.x-drag-overlay{width:100%;height:100%;display:none;position:absolute;left:0;top:0;background:white;z-index:20000;-moz-opacity:0;opacity:0;filter:alpha(opacity=0);}
.x-clear{clear:both;height:0;overflow:hidden;line-height:0;font-size:0;}

.x-tabs-wrap{border-bottom:1px solid #6593cf;padding-top:2px;}
.x-tabs-strip-wrap{width:100%;}
.x-tabs-wrap table{position:relative;top:0;left:0;}
.x-tabs-strip td{padding:0;padding-left:2px;}
.x-tabs-strip a,.x-tabs-strip span,.x-tabs-strip em{display:block;}
.x-tabs-strip a{text-decoration:none!important;-moz-outline:none;outline:none;cursor:pointer;}
.x-tabs-strip .x-tabs-text{font:bold 11px tahoma,arial,helvetica;color:#666;overflow:hidden;white-space:nowrap;cursor:pointer;text-overflow:ellipsis;}
.x-tabs-strip .on .x-tabs-text{cursor:default;color:#083772;}
.x-tabs-strip .disabled .x-tabs-text{cursor:default;color:#aaa;}
.x-tabs-strip .x-tabs-inner{padding:4px 10px;}
.x-tabs-strip .on .x-tabs-right{background:url(/ui-fw/resources/images/default/tabs/tab-sprite.gif) no-repeat right 0;}
.x-tabs-strip .on .x-tabs-left{background:url(/ui-fw/resources/images/default/tabs/tab-sprite.gif) no-repeat 0 -100px;}
.x-tabs-strip .x-tabs-right{background:url(/ui-fw/resources/images/default/tabs/tab-sprite.gif) no-repeat right -50px;}
.x-tabs-strip .x-tabs-left{background:url(/ui-fw/resources/images/default/tabs/tab-sprite.gif) no-repeat 0 -150px;}
.x-tabs-strip a{position:relative;top:1px;left:0;}
.x-tabs-strip .on a{position:relative;}
.x-tabs-strip .on .x-tabs-inner{padding-bottom:5px;}
.x-tabs-strip .x-tabs-closable .x-tabs-inner{padding-right:22px;position:relative;}
.x-tabs-strip .x-tabs-closable .close-icon{line-height:1px;font-size:1px;background-image:url(/ui-fw/resources/images/default/layout/tab-close.gif);display:block;position:absolute;right:5px;top:4px;width:11px;height:11px;cursor:pointer;}
.x-tabs-strip .on .close-icon{background-image:url(/ui-fw/resources/images/default/layout/tab-close-on.gif);}
.x-tabs-strip .x-tabs-closable .close-over{background-image:url(/ui-fw/resources/images/default/layout/tab-close-on.gif);}
.x-tabs-body{border:1px solid #6593cf;border-top:0 none;}
.x-tabs-bottom .x-tabs-wrap{border-top:1px solid #6593cf;border-bottom:0 none;padding-top:0;padding-bottom:2px;}
.x-tabs-bottom .x-tabs-strip .x-tabs-right{background:url(/ui-fw/resources/images/default/tabs/tab-btm-inactive-right-bg.gif) no-repeat bottom left;}
.x-tabs-bottom .x-tabs-strip .x-tabs-left{background:url(/ui-fw/resources/images/default/tabs/tab-btm-inactive-left-bg.gif) no-repeat bottom right;}
.x-tabs-bottom .x-tabs-strip .on .x-tabs-right{background:url(/ui-fw/resources/images/default/tabs/tab-btm-right-bg.gif) no-repeat bottom left;}
.x-tabs-bottom .x-tabs-strip .on .x-tabs-left{background:url(/ui-fw/resources/images/default/tabs/tab-btm-left-bg.gif) no-repeat bottom right;}
.x-tabs-bottom .x-tabs-strip a{position:relative;top:0;left:0;}
.x-tabs-bottom .x-tabs-strip .on a{margin-top:-1px;}
.x-tabs-bottom .x-tabs-strip .on .x-tabs-inner{padding-top:5px;}
.x-tabs-bottom .x-tabs-body{border:1px solid #6593cf;border-bottom:0 none;}

 .x-form-field{margin:0;font:normal 12px tahoma,arial,helvetica,sans-serif;}
.x-form-text,textarea.x-form-field{padding:1px 3px;background:#fff url(/ui-fw/resources/images/default/form/text-bg.gif) repeat-x 0 0;border:1px solid #B5B8C8;}
.x-form-text{height:22px;line-height:18px;vertical-align:middle;}
.ext-ie .x-form-text{margin-top:-1px;margin-bottom:-1px;height:22px;line-height:18px;}
.ext-strict .x-form-text{height:18px;}
.ext-safari .x-form-text{height:20px;}
.ext-gecko .x-form-text{padding-top:2px;padding-bottom:0;}
.x-form-select-one{height:20px;line-height:18px;vertical-align:middle;background-color:#fff;border:1px solid #B5B8C8;}
.x-form-field-wrap{position:relative;zoom:1;white-space:nowrap;}
.x-editor .x-form-check-wrap{background:#fff;}
.x-form-field-wrap .x-form-trigger{width:17px;height:21px;border:0;background:transparent url(/ui-fw/resources/images/default/form/trigger.gif) no-repeat 0 0;cursor:pointer;border-bottom:1px solid #B5B8C8;position:absolute;top:0;}
.ext-safari .x-form-field-wrap .x-form-trigger{height:19px;}
.x-form-field-wrap .x-form-date-trigger{background-image:url(/ui-fw/resources/images/default/form/date-trigger.gif);cursor:pointer;}
.x-form-field-wrap .x-form-clear-trigger{background-image:url(/ui-fw/resources/images/default/form/clear-trigger.gif);cursor:pointer;}
.x-form-field-wrap .x-form-search-trigger{background-image:url(/ui-fw/resources/images/default/form/search-trigger.gif);cursor:pointer;}
.ext-safari .x-form-field-wrap .x-form-trigger{right:0;}
.x-form-field-wrap .x-form-twin-triggers .x-form-trigger{position:static;top:auto;vertical-align:top;}
.x-form-field-wrap .x-form-trigger-over{background-position:-17px 0;}
.x-form-field-wrap .x-form-trigger-click{background-position:-34px 0;}
.x-trigger-wrap-focus .x-form-trigger{background-position:-51px 0;}
.x-trigger-wrap-focus .x-form-trigger-over{background-position:-68px 0;}
.x-trigger-wrap-focus .x-form-trigger-click{background-position:-85px 0;}
.x-trigger-wrap-focus .x-form-trigger{border-bottom:1px solid #7eadd9;}
.x-item-disabled .x-form-trigger-over{background-position:0 0!important;border-bottom:1px solid #B5B8C8;}
.x-item-disabled .x-form-trigger-click{background-position:0 0!important;border-bottom:1px solid #B5B8C8;}
.x-form-focus,textarea.x-form-focus{border:1px solid #7eadd9;}
.x-form-invalid,textarea.x-form-invalid{background:#fff url(/ui-fw/resources/images/default/grid/invalid_line.gif) repeat-x bottom;border:1px solid #dd7870;}
.ext-safari .x-form-invalid{background-color:#fee;border:1px solid #ff7870;}
.x-editor{visibility:hidden;padding:0;margin:0;}
.x-form-check-wrap{line-height:18px;}
.ext-ie .x-form-check-wrap input{width:15px;height:15px;}
.x-editor .x-form-check-wrap{padding:3px;}
.x-editor .x-form-checkbox{height:13px;border:0 none;}
.x-form-grow-sizer{font:normal 12px tahoma,arial,helvetica,sans-serif;left:-10000px;padding:8px 3px;position:absolute;visibility:hidden;top:-10000px;white-space:pre-wrap;white-space:-moz-pre-wrap;white-space:-pre-wrap;white-space:-o-pre-wrap;word-wrap:break-word;zoom:1;}
.x-form-grow-sizer p{margin:0!important;border:0 none!important;padding:0!important;}
.x-form-item{font:normal 12px tahoma,arial,helvetica,sans-serif;display:block;margin-bottom:4px;}
.x-form-item label{display:block;float:left;width:100px;padding:3px;padding-left:0;clear:left;z-index:2;position:relative;}
.x-form-element{padding-left:105px;position:relative;}
.x-form-invalid-msg{color:#e00;padding:2px;padding-left:18px;font:normal 11px tahoma,arial,helvetica,sans-serif;background:transparent url(/ui-fw/resources/images/default/shared/warning.gif) no-repeat 0 2px;line-height:16px;width:200px;}
.x-form-label-right label{text-align:right;}
.x-form-label-top .x-form-item label{width:auto;float:none;clear:none;display:inline;margin-bottom:4px;position:static;}
.x-form-label-top .x-form-element{padding-left:0;padding-top:4px;}
.x-form-label-top .x-form-item{padding-bottom:4px;}
.x-form fieldset{border:1px solid #B5B8C8;padding:10px 10px 5px 10px;margin-bottom:10px;}
.x-form fieldset legend{font:bold 11px tahoma,arial,helvetica,sans-serif;color:#15428b;}
.ext-ie .x-form fieldset legend{margin-bottom:10px;}
.ext-ie .x-form fieldset{padding-top:0;}
.x-form-empty-field{color:gray;}
.x-small-editor .x-form-field{font:normal 11px arial,tahoma,helvetica,sans-serif;}
.x-small-editor .x-form-text{height:20px;line-height:16px;vertical-align:middle;}
.ext-ie .x-small-editor .x-form-text{margin-top:-1px!important;margin-bottom:-1px!important;height:20px!important;line-height:16px!important;}
.ext-strict .x-small-editor .x-form-text{height:16px!important;}
.ext-safari .x-small-editor .x-form-field{font:normal 12px arial,tahoma,helvetica,sans-serif;}
.ext-ie .x-small-editor .x-form-text{height:20px;line-height:16px;}
.ext-border-box .x-small-editor .x-form-text{height:20px;}
.x-small-editor .x-form-select-one{height:20px;line-height:16px;vertical-align:middle;}
.x-small-editor .x-form-num-field{text-align:right;}
.x-small-editor .x-form-field-wrap .x-form-trigger{height:19px;}
.x-form-clear{clear:both;height:0;overflow:hidden;line-height:0;font-size:0;}
.x-form-clear-left{clear:left;height:0;overflow:hidden;line-height:0;font-size:0;}
.x-form-cb-label{width:'auto'!important;float:none!important;clear:none!important;display:inline!important;margin-left:4px;}
.x-form-column{float:left;padding:0;margin:0;width:48%;overflow:hidden;zoom:1;}
.x-form .x-form-btns-ct .x-btn{float:right;clear:none;}
.x-form .x-form-btns-ct .x-form-btns td{border:0;padding:0;}
.x-form .x-form-btns-ct .x-form-btns-right table{float:right;clear:none;}
.x-form .x-form-btns-ct .x-form-btns-left table{float:left;clear:none;}
.x-form .x-form-btns-ct .x-form-btns-center{text-align:center;}
.x-form .x-form-btns-ct .x-form-btns-center table{margin:0 auto;}
.x-form .x-form-btns-ct table td.x-form-btn-td{padding:3px;}
.x-form .x-form-btns-ct .x-btn-focus .x-btn-left{background-position:0 -147px;}
.x-form .x-form-btns-ct .x-btn-focus .x-btn-right{background-position:0 -168px;}
.x-form .x-form-btns-ct .x-btn-focus .x-btn-center{background-position:0 -189px;}
.x-form .x-form-btns-ct .x-btn-click .x-btn-center{background-position:0 -126px;}
.x-form .x-form-btns-ct .x-btn-click .x-btn-right{background-position:0 -84px;}
.x-form .x-form-btns-ct .x-btn-click .x-btn-left{background-position:0 -63px;}
.x-form-invalid-icon{width:16px;height:18px;visibility:hidden;position:absolute;left:0;top:0;display:block;background:transparent url(/ui-fw/resources/images/default/form/exclamation.gif) no-repeat 0 2px;}
.ext-ie td .x-form-text{position:relative;top:-1px;}

.x-btn{font:normal 11px tahoma,verdana,helvetica;cursor:pointer;white-space:nowrap;}
.x-btn button{border:0 none;background:transparent;font:normal 11px tahoma,verdana,helvetica;padding-left:3px;padding-right:3px;cursor:pointer;margin:0;overflow:visible;width:auto;-moz-outline:0 none;outline:0 none;}
* html .ext-ie .x-btn button{width:1px;}
.ext-gecko .x-btn button{padding-left:0;padding-right:0;}
.ext-ie .x-btn button{padding-top:2px;}
.x-btn-icon .x-btn-center .x-btn-text{background-position:center;background-repeat:no-repeat;height:16px;width:16px;cursor:pointer;white-space:nowrap;padding:0;}
.x-btn-icon .x-btn-center{padding:1px;}
.x-btn em{font-style:normal;font-weight:normal;}
.x-btn-text-icon .x-btn-center .x-btn-text{background-position:0 2px;background-repeat:no-repeat;padding-left:18px;padding-top:3px;padding-bottom:2px;padding-right:0;}
.x-btn-left,.x-btn-right{font-size:1px;line-height:1px;}
.x-btn-left{width:3px;height:21px;background:url(/ui-fw/resources/images/default/basic-dialog/btn-sprite.gif) no-repeat 0 0;}
.x-btn-right{width:3px;height:21px;background:url(/ui-fw/resources/images/default/basic-dialog/btn-sprite.gif) no-repeat 0 -21px;}
.x-btn-left i,.x-btn-right i{display:block;width:3px;overflow:hidden;font-size:1px;line-height:1px;}
.x-btn-center{background:url(/ui-fw/resources/images/default/basic-dialog/btn-sprite.gif) repeat-x 0 -42px;vertical-align:middle;text-align:center;padding:0 5px;cursor:pointer;white-space:nowrap;}
.x-btn-over .x-btn-left{background-position:0 -63px;}
.x-btn-over .x-btn-right{background-position:0 -84px;}
.x-btn-over .x-btn-center{background-position:0 -105px;}
.x-btn-click .x-btn-center,.x-btn-menu-active .x-btn-center{background-position:0 -126px;}
.x-btn-disabled *{color:gray!important;cursor:default!important;}
.x-btn-menu-text-wrap .x-btn-center{padding:0 3px;}
.ext-gecko .x-btn-menu-text-wrap .x-btn-center{padding:0 1px;}
.x-btn-menu-arrow-wrap .x-btn-center{padding:0;}
.x-btn-menu-arrow-wrap .x-btn-center button{width:12px!important;height:21px;padding:0!important;display:block;background:transparent url(/ui-fw/resources/images/default/basic-dialog/btn-arrow.gif) no-repeat left 3px;}
.x-btn-with-menu .x-btn-center{padding-right:2px!important;}
.x-btn-with-menu .x-btn-center em{display:block;background:transparent url(/ui-fw/resources/images/default/toolbar/btn-arrow.gif) no-repeat right 0;padding-right:10px;}
.x-btn-text-icon .x-btn-with-menu .x-btn-center em{display:block;background:transparent url(/ui-fw/resources/images/default/toolbar/btn-arrow.gif) no-repeat right 3px;padding-right:10px;}

.x-toolbar{border-top:1px solid #eaf0f7;border-bottom:1px solid #a9bfd3;display:block;padding:2px;background:#d0def0 url(/ui-fw/resources/images/default/layout/panel-title-light-bg.gif) repeat-x;position:relative;zoom:1;}
.x-toolbar .x-item-disabled .x-btn-icon{opacity:.35;-moz-opacity:.35;filter:alpha(opacity=35);}
.x-toolbar td{vertical-align:middle;}
.mso .x-toolbar,.x-grid-mso .x-toolbar{border:0 none;background:url(/ui-fw/resources/images/default/grid/mso-hd.gif);}
.x-toolbar td,.x-toolbar span,.x-toolbar input,.x-toolbar div,.x-toolbar select,.x-toolbar label{white-space:nowrap;font:normal 11px tahoma,arial,helvetica,sans-serif;}
.x-toolbar .x-item-disabled{color:gray;cursor:default;opacity:.6;-moz-opacity:.6;filter:alpha(opacity=60);}
.x-toolbar .x-item-disabled *{color:gray;cursor:default;}
.x-toolbar .x-btn-left{background:none;}
.x-toolbar .x-btn-right{background:none;}
.x-toolbar .x-btn-center{background:none;padding:0;}
.x-toolbar .x-btn-menu-text-wrap .x-btn-center button{padding-right:2px;}
.ext-gecko .x-toolbar .x-btn-menu-text-wrap .x-btn-center button{padding-right:0;}
.x-toolbar .x-btn-menu-arrow-wrap .x-btn-center button{padding:0 2px;}
.x-toolbar .x-btn-menu-arrow-wrap .x-btn-center button{width:12px;background:transparent url(/ui-fw/resources/images/default/toolbar/btn-arrow.gif) no-repeat 0 3px;}
.x-toolbar .x-btn-text-icon .x-btn-menu-arrow-wrap .x-btn-center button{width:12px;background:transparent url(/ui-fw/resources/images/default/toolbar/btn-arrow.gif) no-repeat 0 3px;}
.x-toolbar .x-btn-over .x-btn-menu-arrow-wrap .x-btn-center button{background-position:0 -47px;}
.x-toolbar .x-btn-over .x-btn-left{background:url(/ui-fw/resources/images/default/toolbar/tb-btn-sprite.gif) no-repeat 0 0;}
.x-toolbar .x-btn-over .x-btn-right{background:url(/ui-fw/resources/images/default/toolbar/tb-btn-sprite.gif) no-repeat 0 -21px;}
.x-toolbar .x-btn-over .x-btn-center{background:url(/ui-fw/resources/images/default/toolbar/tb-btn-sprite.gif) repeat-x 0 -42px;}
.x-toolbar .x-btn-click .x-btn-left,.x-toolbar .x-btn-pressed .x-btn-left,.x-toolbar .x-btn-menu-active .x-btn-left{background:url(/ui-fw/resources/images/default/toolbar/tb-btn-sprite.gif) no-repeat 0 -63px;}
.x-toolbar .x-btn-click .x-btn-right,.x-toolbar .x-btn-pressed .x-btn-right,.x-toolbar .x-btn-menu-active .x-btn-right{background:url(/ui-fw/resources/images/default/toolbar/tb-btn-sprite.gif) no-repeat 0 -84px;}
.x-toolbar .x-btn-click .x-btn-center,.x-toolbar .x-btn-pressed .x-btn-center,.x-toolbar .x-btn-menu-active .x-btn-center{background:url(/ui-fw/resources/images/default/toolbar/tb-btn-sprite.gif) repeat-x 0 -105px;}
.x-toolbar .x-btn-with-menu .x-btn-center em{padding-right:8px;}
.x-toolbar .ytb-text{padding:2px;}
.x-toolbar .ytb-sep{background-image:url(/ui-fw/resources/images/default/grid/grid-split.gif);background-position:center;background-repeat:no-repeat;display:block;font-size:1px;height:16px;width:4px;overflow:hidden;cursor:default;margin:0 2px 0;border:0;}
.x-toolbar .ytb-spacer{width:2px;}
.mso .x-toolbar .ytb-sep,.x-grid-mso .x-toolbar .ytb-sep{background-image:url(/ui-fw/resources/images/default/grid/grid-blue-split.gif);}
.x-grid-page-number{width:24px;height:14px;}
.x-grid-page-first .x-btn-text{background-image:url(/ui-fw/resources/images/default/grid/page-first.gif);}
.x-grid-loading .x-btn-text{background-image:url(/ui-fw/resources/images/default/grid/done.gif);}
.x-grid-page-last .x-btn-text{background-image:url(/ui-fw/resources/images/default/grid/page-last.gif);}
.x-grid-page-next .x-btn-text{background-image:url(/ui-fw/resources/images/default/grid/page-next.gif);}
.x-grid-page-prev .x-btn-text{background-image:url(/ui-fw/resources/images/default/grid/page-prev.gif);}
.x-item-disabled .x-grid-loading .x-btn-text{background-image:url(/ui-fw/resources/images/default/grid/loading.gif);}
.x-item-disabled .x-grid-page-first .x-btn-text{background-image:url(/ui-fw/resources/images/default/grid/page-first-disabled.gif);}
.x-item-disabled .x-grid-page-last .x-btn-text{background-image:url(/ui-fw/resources/images/default/grid/page-last-disabled.gif);}
.x-item-disabled .x-grid-page-next .x-btn-text{background-image:url(/ui-fw/resources/images/default/grid/page-next-disabled.gif);}
.x-item-disabled .x-grid-page-prev .x-btn-text{background-image:url(/ui-fw/resources/images/default/grid/page-prev-disabled.gif);}
.x-paging-info{position:absolute;top:8px;right:8px;color:#15428b;}

.x-resizable-handle{position:absolute;z-index:100;font-size:1px;line-height:6px;overflow:hidden;background:white;filter:alpha(opacity=0);opacity:0;zoom:1;}
.x-resizable-handle-east{width:6px;cursor:e-resize;right:0;top:0;height:100%;}
.ext-ie .x-resizable-handle-east{margin-right:-1px;}
.x-resizable-handle-south{width:100%;cursor:s-resize;left:0;bottom:0;height:6px;}
.ext-ie .x-resizable-handle-south{margin-bottom:-1px;}
.x-resizable-handle-west{width:6px;cursor:w-resize;left:0;top:0;height:100%;}
.x-resizable-handle-north{width:100%;cursor:n-resize;left:0;top:0;height:6px;}
.x-resizable-handle-southeast{width:6px;cursor:se-resize;right:0;bottom:0;height:6px;z-index:101;}
.x-resizable-handle-northwest{width:6px;cursor:nw-resize;left:0;top:0;height:6px;z-index:101;}
.x-resizable-handle-northeast{width:6px;cursor:ne-resize;right:0;top:0;height:6px;z-index:101;}
.x-resizable-handle-southwest{width:6px;cursor:sw-resize;left:0;bottom:0;height:6px;z-index:101;}
.x-resizable-over .x-resizable-handle,.x-resizable-pinned .x-resizable-handle{filter:alpha(opacity=100);opacity:1;}
.x-resizable-over .x-resizable-handle-east,.x-resizable-pinned .x-resizable-handle-east{background:url(/ui-fw/resources/images/default/sizer/e-handle.gif);background-position:left;}
.x-resizable-over .x-resizable-handle-west,.x-resizable-pinned .x-resizable-handle-west{background:url(/ui-fw/resources/images/default/sizer/e-handle.gif);background-position:left;}
.x-resizable-over .x-resizable-handle-south,.x-resizable-pinned .x-resizable-handle-south{background:url(/ui-fw/resources/images/default/sizer/s-handle.gif);background-position:top;}
.x-resizable-over .x-resizable-handle-north,.x-resizable-pinned .x-resizable-handle-north{background:url(/ui-fw/resources/images/default/sizer/s-handle.gif);background-position:top;}
.x-resizable-over .x-resizable-handle-southeast,.x-resizable-pinned .x-resizable-handle-southeast{background:url(/ui-fw/resources/images/default/sizer/se-handle.gif);background-position:top left;}
.x-resizable-over .x-resizable-handle-northwest,.x-resizable-pinned .x-resizable-handle-northwest{background:url(/ui-fw/resources/images/default/sizer/nw-handle.gif);background-position:bottom right;}
.x-resizable-over .x-resizable-handle-northeast,.x-resizable-pinned .x-resizable-handle-northeast{background:url(/ui-fw/resources/images/default/sizer/ne-handle.gif);background-position:bottom left;}
.x-resizable-over .x-resizable-handle-southwest,.x-resizable-pinned .x-resizable-handle-southwest{background:url(/ui-fw/resources/images/default/sizer/sw-handle.gif);background-position:top right;}
.x-resizable-proxy{border:1px dashed #6593cf;position:absolute;overflow:hidden;display:none;left:0;top:0;z-index:50000;}
.x-resizable-overlay{width:100%;height:100%;display:none;position:absolute;left:0;top:0;background:white;z-index:200000;-moz-opacity:0;opacity:0;filter:alpha(opacity=0);}

 .x-grid{position:relative;overflow:hidden;background-color:#fff;}
.x-grid-scroller{overflow:auto;}
.x-grid-viewport,.x-grid-locked{position:absolute;left:0;top:0;z-index:2;overflow:hidden;visibility:hidden;}
.x-grid-cell-inner,.x-grid-hd-inner{overflow:hidden;-o-text-overflow:ellipsis;text-overflow:ellipsis;}
.x-grid-hd-row td,.x-grid-row td{font:normal 11px arial,tahoma,helvetica,sans-serif;line-height:13px;white-space:nowrap;vertical-align:top;-moz-outline:none;-moz-user-focus:normal;}
.x-grid-hd-row td{line-height:14px;}
.x-grid-col{border-right:1px solid #ebebeb;border-bottom:1px solid #ebebeb;}
.x-grid-locked .x-grid-body td{background-color:#FBFDFF;border-right:1px solid #deecfd;border-bottom:1px solid #deecfd!important;}
.x-grid-locked .x-grid-body td .x-grid-cell-inner{border-top:0 none;}
.x-grid-locked .x-grid-row-alt td{background-color:#F5FAFE;}
.x-grid-locked .x-grid-header table{border-right:1px solid transparent;}
.x-grid-locked .x-grid-body table{border-right:1px solid #c3daf9;}
.x-grid-row{cursor:default;}
.x-grid-row-alt{background-color:#f1f1f1;}
.x-grid-row-over td{background-color:#d9e8fb;}
.x-grid-resize-proxy{width:3px;background-color:#ccc;cursor:e-resize;cursor:col-resize;position:absolute;top:0;height:100px;overflow:hidden;visibility:hidden;border:0 none;z-index:7;}
.x-grid-focus{position:absolute;top:0;-moz-outline:0 none;outline:0 none;-moz-user-select:normal;-khtml-user-select:normal;}
.x-grid-header{background:#ebeadb url(/ui-fw/resources/images/default/grid/grid-hrow.gif) repeat-x;overflow:hidden;position:relative;cursor:default;width:100%;}
.x-grid-hd-row{height:22px;}
.x-grid-hd{padding-right:1px;}
.x-grid-hd-over .x-grid-hd-inner{border-bottom:1px solid #c3daf9;}
.x-grid-hd-over .x-grid-hd-text{background:#fafafa url(/ui-fw/resources/images/default/grid/grid-hrow.gif) repeat-x 0 1px;padding-bottom:1px;border-bottom:1px solid #b3cae9;}
.x-grid-sort-icon{background-repeat:no-repeat;display:none;height:4px;width:13px;margin-left:3px;vertical-align:middle;}
.x-grid-header .sort-asc .x-grid-sort-icon{background-image:url(/ui-fw/resources/images/default/grid/sort_asc.gif);display:inline;}
.x-grid-header .sort-desc .x-grid-sort-icon{background-image:url(/ui-fw/resources/images/default/grid/sort_desc.gif);display:inline;}
.x-grid-body{overflow:hidden;position:relative;width:100%;zoom:1;}
.x-grid-cell-text,.x-grid-hd-text{display:block;padding:3px 5px 3px 5px;-moz-user-select:none;-khtml-user-select:none;color:black;}
.x-grid-hd-text{padding-top:4px;}
.x-grid-split{background-image:url(/ui-fw/resources/images/default/grid/grid-split.gif);background-position:center;background-repeat:no-repeat;cursor:e-resize;cursor:col-resize;display:block;font-size:1px;height:16px;overflow:hidden;position:absolute;top:2px;width:6px;z-index:3;}
.x-grid-hd-text{color:#15428b;}
.x-dd-drag-proxy .x-grid-hd-inner{background:#ebeadb url(/ui-fw/resources/images/default/grid/grid-hrow.gif) repeat-x;height:22px;width:120px;}
.col-move-top,.col-move-bottom{width:9px;height:9px;position:absolute;top:0;line-height:1px;font-size:1px;overflow:hidden;visibility:hidden;z-index:20000;}
.col-move-top{background:transparent url(/ui-fw/resources/images/default/grid/col-move-top.gif) no-repeat left top;}
.col-move-bottom{background:transparent url(/ui-fw/resources/images/default/grid/col-move-bottom.gif) no-repeat left top;}
.x-grid-row-selected td,.x-grid-locked .x-grid-row-selected td{background-color:#316ac5!important;color:white;}
.x-grid-row-selected span,.x-grid-row-selected b,.x-grid-row-selected div,.x-grid-row-selected strong,.x-grid-row-selected i{color:white!important;}
.x-grid-row-selected .x-grid-cell-text{color:white;}
.x-grid-cell-selected{background-color:#316ac5!important;color:white;}
.x-grid-cell-selected span{color:white!important;}
.x-grid-cell-selected .x-grid-cell-text{color:white;}
.x-grid-locked td.x-grid-row-marker,.x-grid-locked .x-grid-row-selected td.x-grid-row-marker{background:#ebeadb url(/ui-fw/resources/images/default/grid/grid-hrow.gif) repeat-x 0 bottom!important;vertical-align:middle!important;color:black;padding:0;border-top:1px solid white;border-bottom:none!important;border-right:1px solid #6fa0df!important;text-align:center;}
.x-grid-locked td.x-grid-row-marker div,.x-grid-locked .x-grid-row-selected td.x-grid-row-marker div{padding:0 4px;color:#15428b!important;text-align:center;}
.x-grid-dirty-cell{background:transparent url(/ui-fw/resources/images/default/grid/dirty.gif) no-repeat 0 0;}
.x-grid-row-alt .x-grid-dirty-cell{background-color:#f1f1f1;}
.x-grid-topbar,.x-grid-bottombar{font:normal 11px arial,tahoma,helvetica,sans-serif;overflow:hidden;display:none;zoom:1;position:relative;}
.x-grid-topbar .x-toolbar{border-right:0 none;}
.x-grid-bottombar .x-toolbar{border-right:0 none;border-bottom:0 none;border-top:1px solid #a9bfd3;}
.x-props-grid .x-grid-cell-selected .x-grid-cell-text{background-color:#316ac5!important;}
.x-props-grid .x-grid-col-value .x-grid-cell-text{background-color:white;}
.x-props-grid .x-grid-col-name{background-color:#c3daf9;}
.x-props-grid .x-grid-col-name .x-grid-cell-text{background-color:white;margin-left:10px;}
.x-props-grid .x-grid-split-value{visibility:hidden;}
.xg-hmenu-sort-asc .x-menu-item-icon{background-image:url(/ui-fw/resources/images/default/grid/hmenu-asc.gif);}
.xg-hmenu-sort-desc .x-menu-item-icon{background-image:url(/ui-fw/resources/images/default/grid/hmenu-desc.gif);}
.xg-hmenu-lock .x-menu-item-icon{background-image:url(/ui-fw/resources/images/default/grid/hmenu-lock.gif);}
.xg-hmenu-unlock .x-menu-item-icon{background-image:url(/ui-fw/resources/images/default/grid/hmenu-unlock.gif);}
.x-dd-drag-ghost .x-grid-dd-wrap{padding:1px 3px 3px 1px;}

.x-layout-container{width:100%;height:100%;overflow:hidden;background-color:#c3daf9;}
.x-layout-container .x-layout-tabs-body{border:0 none;}
.x-layout-collapsed{position:absolute;left:-10000px;top:-10000px;visibility:hidden;background-color:#c3daf9;width:20px;height:20px;overflow:hidden;border:1px solid #98c0f4;z-index:20;}
.ext-border-box .x-layout-collapsed{width:22px;height:22px;}
.x-layout-collapsed-over{cursor:pointer;background-color:#d9e8fb;}
.x-layout-collapsed-west .x-layout-collapsed-tools,.x-layout-collapsed-east .x-layout-collapsed-tools{position:absolute;top:0;left:0;width:20px;height:20px;}
.x-layout-collapsed-north .x-layout-collapsed-tools,.x-layout-collapsed-south .x-layout-collapsed-tools{position:absolute;top:0;right:0;width:20px;height:20px;}
.x-layout-collapsed .x-layout-tools-button{margin:0;}
.x-layout-collapsed .x-layout-tools-button-inner{width:16px;height:16px;}
.x-layout-inactive-content{position:absolute;left:-10000px;top:-10000px;visibility:hidden;}
.x-layout-active-content{visibility:visible;}
.x-layout-panel{position:absolute;border:1px solid #98c0f4;overflow:hidden;background-color:white;}
.x-layout-panel-east,.x-layout-panel-west{z-index:10;}
.x-layout-panel-north,.x-layout-panel-south{z-index:11;}
.x-layout-collapsed-north,.x-layout-collapsed-south,.x-layout-collapsed-east,.x-layout-collapsed-west{z-index:12;}
.x-layout-panel-body{overflow:hidden;}
.x-layout-split{position:absolute;height:5px;width:5px;line-height:1px;font-size:1px;z-index:3;background-color:#c3daf9;}
.x-layout-panel-hd{background-image:url(/ui-fw/resources/images/default/layout/panel-title-light-bg.gif);color:black;border-bottom:1px solid #98c0f4;position:relative;}
.x-layout-panel-hd-text{font:normal 11px tahoma,verdana,helvetica;padding:4px;padding-left:4px;display:block;white-space:nowrap;}
.x-layout-panel-hd-tools{position:absolute;right:0;top:0;text-align:right;padding-top:2px;padding-right:2px;width:60px;}
.x-layout-tools-button{z-index:6;padding:2px;cursor:pointer;float:right;}
.x-layout-tools-button-over{padding:1px;border:1px solid #98c0f4;background-color:white;}
.x-layout-tools-button-inner{height:12px;width:12px;line-height:1px;font-size:1px;background-repeat:no-repeat;background-position:center;}
.x-layout-close{background-image:url(/ui-fw/resources/images/default/layout/panel-close.gif);}
.x-layout-stick{background-image:url(/ui-fw/resources/images/default/layout/stick.gif);}
.x-layout-collapse-west,.x-layout-expand-east{background-image:url(/ui-fw/resources/images/default/layout/collapse.gif);}
.x-layout-expand-west,.x-layout-collapse-east{background-image:url(/ui-fw/resources/images/default/layout/expand.gif);}
.x-layout-collapse-north,.x-layout-expand-south{background-image:url(/ui-fw/resources/images/default/layout/ns-collapse.gif);}
.x-layout-expand-north,.x-layout-collapse-south{background-image:url(/ui-fw/resources/images/default/layout/ns-expand.gif);}
.x-layout-split-h{background-image:url(/ui-fw/resources/images/default/sizer/e-handle.gif);background-position:left;}
.x-layout-split-v{background-image:url(/ui-fw/resources/images/default/sizer/s-handle.gif);background-position:top;}
.x-layout-panel .x-tabs-wrap{background:url(/ui-fw/resources/images/default/layout/gradient-bg.gif);}
.x-layout-panel .x-tabs-body{background-color:white;overflow:auto;height:100%;}
.x-layout-component-panel,.x-layout-nested-layout{position:relative;padding:0;overflow:hidden;width:200px;height:200px;}
.x-layout-nested-layout .x-layout-panel{border:0 none;}
.x-layout-nested-layout .x-layout-panel-north{border-bottom:1px solid #98c0f4;}
.x-layout-nested-layout .x-layout-panel-south{border-top:1px solid #98c0f4;}
.x-layout-nested-layout .x-layout-panel-east{border-left:1px solid #98c0f4;}
.x-layout-nested-layout .x-layout-panel-west{border-right:1px solid #98c0f4;}
.x-layout-panel-dragover{border:2px solid #6593cf;}
.x-layout-panel-proxy{background-image:url(/ui-fw/resources/images/default/layout/gradient-bg.gif);background-color:#c3daf9;border:1px dashed #6593cf;z-index:10001;overflow:hidden;position:absolute;left:0;top:0;}
.x-layout-slider{z-index:15;overflow:hidden;position:absolute;}
.x-scroller-up,.x-scroller-down{background-color:#c3daf9;border:1px solid #6593cf;border-top-color:#fff;border-left-color:#fff;border-right:0 none;cursor:pointer;overflow:hidden;line-height:16px;}
.x-scroller-down{border-bottom:0 none;border-top:1px solid #6593cf;}
.x-scroller-btn-over{background-color:#d9e8f8;}
.x-scroller-btn-click{background-color:#AECEF7;}
.x-scroller-btn-disabled{cursor:default;background-color:#c3daf9;-moz-opacity:0.3;opacity:.30;filter:alpha(opacity=30);}
.x-reader .x-layout-panel-north{border:0 none;}
.x-reader .x-layout-panel-center{border:0 none;}
.x-reader .x-layout-nested-layout .x-layout-panel-center{border:1px solid #99bbe8;border-top:0 none;}
.x-reader .x-layout-nested-layout .x-layout-panel-south{border:1px solid #99bbe8;}

.x-dlg-proxy{background-image:url(/ui-fw/resources/images/default/gradient-bg.gif);background-color:#c3daf9;border:1px solid #6593cf;z-index:10001;overflow:hidden;position:absolute;left:0;top:0;}
.x-dlg-shadow{background:#aaa;position:absolute;left:0;top:0;}
.x-dlg-focus{-moz-outline:0 none;outline:0 none;width:0;height:0;overflow:hidden;position:absolute;top:0;left:0;}
.x-dlg-mask{z-index:10000;display:none;position:absolute;top:0;left:0;-moz-opacity:0.5;opacity:.50;filter:alpha(opacity=50);background-color:#CCC;}
body.x-body-masked select{visibility:hidden;}
body.x-body-masked .x-dlg select{visibility:visible;}
.x-dlg{z-index:10001;overflow:hidden;position:absolute;left:300;top:0;}
.x-dlg .x-dlg-hd{background:url(/ui-fw/resources/images/default/basic-dialog/hd-sprite.gif) repeat-x 0 -82px;background-color:navy;color:#FFF;font:bold 12px "sans serif",tahoma,verdana,helvetica;overflow:hidden;padding:5px;white-space:nowrap;}
.x-dlg .x-dlg-hd-left{background:url(/ui-fw/resources/images/default/basic-dialog/hd-sprite.gif) no-repeat 0 -41px;padding-left:3px;margin:0;}
.x-dlg .x-dlg-hd-right{background:url(/ui-fw/resources/images/default/basic-dialog/hd-sprite.gif) no-repeat right 0;padding-right:3px;}
.x-dlg .x-dlg-dlg-body{background:url(/ui-fw/resources/images/default/layout/gradient-bg.gif);border:1px solid #6593cf;border-top:0 none;padding:10px;position:absolute;top:24px;left:0;z-index:1;overflow:hidden;}
.x-dlg-collapsed .x-resizable-handle{display:none;}
.x-dlg .x-dlg-bd{overflow:hidden;}
.x-dlg .x-dlg-ft{overflow:hidden;padding:5px;padding-bottom:0;}
.x-dlg .x-tabs-body{background:white;overflow:auto;}
.x-dlg .x-tabs-top .x-tabs-body{border:1px solid #6593cf;border-top:0 none;}
.x-dlg .x-tabs-bottom .x-tabs-body{border:1px solid #6593cf;border-bottom:0 none;}
.x-dlg .x-layout-container .x-tabs-body{border:0 none;}
.x-dlg .inner-tab{margin:5px;}
.x-dlg .x-dlg-ft .x-btn{margin-right:5px;float:right;clear:none;}
.x-dlg .x-dlg-ft .x-dlg-btns td{border:0;padding:0;}
.x-dlg .x-dlg-ft .x-dlg-btns-right table{float:right;clear:none;}
.x-dlg .x-dlg-ft .x-dlg-btns-left table{float:left;clear:none;}
.x-dlg .x-dlg-ft .x-dlg-btns-center{text-align:center;}
.x-dlg .x-dlg-ft .x-dlg-btns-center table{margin:0 auto;}
.x-dlg .x-dlg-ft .x-dlg-btns .x-btn-focus .x-btn-left{background-position:0 -147px;}
.x-dlg .x-dlg-ft .x-dlg-btns .x-btn-focus .x-btn-right{background-position:0 -168px;}
.x-dlg .x-dlg-ft .x-dlg-btns .x-btn-focus .x-btn-center{background-position:0 -189px;}
.x-dlg .x-dlg-ft .x-dlg-btns .x-btn-click .x-btn-center{background-position:0 -126px;}
.x-dlg .x-dlg-ft .x-dlg-btns .x-btn-click .x-btn-right{background-position:0 -84px;}
.x-dlg .x-dlg-ft .x-dlg-btns .x-btn-click .x-btn-left{background-position:0 -63px;}
.x-dlg-draggable .x-dlg-hd{cursor:move;}
.x-dlg-closable .x-dlg-hd{padding-right:22px;}
.x-dlg-toolbox{position:absolute;top:4px;right:4px;z-index:6;width:40px;cursor:default;height:15px;background:transparent;}
.x-dlg .x-dlg-close,.x-dlg .x-dlg-collapse{float:right;height:15px;width:15px;margin:0;margin-left:2px;padding:0;line-height:1px;font-size:1px;background-repeat:no-repeat;cursor:pointer;visibility:inherit;}
.x-dlg .x-dlg-close{background-image:url(/ui-fw/resources/images/default/basic-dialog/close.gif);}
.x-dlg .x-dlg-collapse{background-image:url(/ui-fw/resources/images/default/basic-dialog/collapse.gif);}
.x-dlg-collapsed .x-dlg-collapse{background-image:url(/ui-fw/resources/images/default/basic-dialog/expand.gif);}
.x-dlg div.x-resizable-handle-east{background-image:url(/ui-fw/resources/images/default/basic-dialog/e-handle.gif);border:0;background-position:right;margin-right:0;}
.x-dlg div.x-resizable-handle-south{background-image:url(/ui-fw/resources/images/default/sizer/s-handle-dark.gif);border:0;height:6px;}
.x-dlg div.x-resizable-handle-west{background-image:url(/ui-fw/resources/images/default/basic-dialog/e-handle.gif);border:0;background-position:1px;}
.x-dlg div.x-resizable-handle-north{background-image:url(/ui-fw/resources/images/default/s.gif);border:0;}
.x-dlg div.x-resizable-handle-northeast,.xtheme-gray .x-dlg div.x-resizable-handle-northeast{background-image:url(/ui-fw/resources/images/default/s.gif);border:0;}
.x-dlg div.x-resizable-handle-northwest,.xtheme-gray .x-dlg div.x-resizable-handle-northwest{background-image:url(/ui-fw/resources/images/default/s.gif);border:0;}
.x-dlg div.x-resizable-handle-southeast{background-image:url(/ui-fw/resources/images/default/basic-dialog/se-handle.gif);background-position:bottom right;width:8px;height:8px;border:0;}
.x-dlg div.x-resizable-handle-southwest{background-image:url(/ui-fw/resources/images/default/sizer/sw-handle-dark.gif);background-position:top right;margin-left:1px;margin-bottom:1px;border:0;}
#x-msg-box .x-dlg-ft .x-btn{float:none;clear:none;margin:0 3px;}
#x-msg-box .x-dlg-bd{padding:5px;overflow:hidden!important;font:normal 13px verdana,tahoma,sans-serif;}
#x-msg-box .ext-mb-input{margin-top:4px;width:95%;}
#x-msg-box .ext-mb-textarea{margin-top:4px;font:normal 13px verdana,tahoma,sans-serif;}
#x-msg-box .ext-mb-progress-wrap{margin-top:4px;border:1px solid #6593cf;}
#x-msg-box .ext-mb-progress{height:18px;background:#e0e8f3 url(/ui-fw/resources/images/default/qtip/bg.gif) repeat-x;}
#x-msg-box .ext-mb-progress-bar{height:18px;overflow:hidden;width:0;background:#8BB8F3;border-top:1px solid #B2D0F7;border-bottom:1px solid #65A1EF;border-right:1px solid #65A1EF;}
#x-msg-box .x-msg-box-wait{background:transparent url(/ui-fw/resources/images/default/grid/loading.gif) no-repeat left;display:block;width:300px;padding-left:18px;line-height:18px;}

.x-dd-drag-proxy{position:absolute;left:0;top:0;visibility:hidden;z-index:15000;}
.x-dd-drag-ghost{color:black;font:normal 11px arial,helvetica,sans-serif;-moz-opacity:0.85;opacity:.85;filter:alpha(opacity=85);border-top:1px solid #ddd;border-left:1px solid #ddd;border-right:1px solid #bbb;border-bottom:1px solid #bbb;padding:3px;padding-left:20px;background-color:white;white-space:nowrap;}
.x-dd-drag-repair .x-dd-drag-ghost{-moz-opacity:0.4;opacity:.4;filter:alpha(opacity=40);border:0 none;padding:0;background-color:transparent;}
.x-dd-drag-repair .x-dd-drop-icon{visibility:hidden;}
.x-dd-drop-icon{position:absolute;top:3px;left:3px;display:block;width:16px;height:16px;background-color:transparent;background-position:center;background-repeat:no-repeat;z-index:1;}
.x-dd-drop-nodrop .x-dd-drop-icon{background-image:url(/ui-fw/resources/images/default/dd/drop-no.gif);}
.x-dd-drop-ok .x-dd-drop-icon{background-image:url(/ui-fw/resources/images/default/dd/drop-yes.gif);}
.x-dd-drop-ok-add .x-dd-drop-icon{background-image:url(/ui-fw/resources/images/default/dd/drop-add.gif);}

.x-tree-icon,.x-tree-ec-icon,.x-tree-elbow-line,.x-tree-elbow,.x-tree-elbow-end,.x-tree-elbow-plus,.x-tree-elbow-minus,.x-tree-elbow-end-plus,.x-tree-elbow-end-minus{border:0 none;height:18px;margin:0;padding:0;vertical-align:middle;width:16px;background-repeat:no-repeat;}
.x-tree-node-collapsed .x-tree-node-icon,.x-tree-node-expanded .x-tree-node-icon,.x-tree-node-leaf .x-tree-node-icon{border:0 none;height:18px;margin:0;padding:0;vertical-align:middle;width:16px;background-position:center;background-repeat:no-repeat;}
.x-tree-node-collapsed .x-tree-node-icon{background-image:url(/ui-fw/resources/images/default/tree/folder.gif);}
.x-tree-node-expanded .x-tree-node-icon{background-image:url(/ui-fw/resources/images/default/tree/folder-open.gif);}
.x-tree-node-leaf .x-tree-node-icon{background-image:url(/ui-fw/resources/images/default/tree/leaf.gif);}
.ext-ie input.x-tree-node-cb{width:15px;height:15px;}
input.x-tree-node-cb{margin-left:1px;}
.ext-ie input.x-tree-node-cb{margin-left:0;}
.x-tree-noicon .x-tree-node-icon{width:0;height:0;}
.x-tree-node-loading .x-tree-node-icon{background-image:url(/ui-fw/resources/images/default/tree/loading.gif)!important;}
.x-tree-node-loading a span{font-style:italic;color:#444;}
.x-tree-lines .x-tree-elbow{background-image:url(/ui-fw/resources/images/default/tree/elbow.gif);}
.x-tree-lines .x-tree-elbow-plus{background-image:url(/ui-fw/resources/images/default/tree/elbow-plus.gif);}
.x-tree-lines .x-tree-elbow-minus{background-image:url(/ui-fw/resources/images/default/tree/elbow-minus.gif);}
.x-tree-lines .x-tree-elbow-end{background-image:url(/ui-fw/resources/images/default/tree/elbow-end.gif);}
.x-tree-lines .x-tree-elbow-end-plus{background-image:url(/ui-fw/resources/images/default/tree/elbow-end-plus.gif);}
.x-tree-lines .x-tree-elbow-end-minus{background-image:url(/ui-fw/resources/images/default/tree/elbow-end-minus.gif);}
.x-tree-lines .x-tree-elbow-line{background-image:url(/ui-fw/resources/images/default/tree/elbow-line.gif);}
.x-tree-no-lines .x-tree-elbow{background:transparent;}
.x-tree-no-lines .x-tree-elbow-plus{background-image:url(/ui-fw/resources/images/default/tree/elbow-plus-nl.gif);}
.x-tree-no-lines .x-tree-elbow-minus{background-image:url(/ui-fw/resources/images/default/tree/elbow-minus-nl.gif);}
.x-tree-no-lines .x-tree-elbow-end{background:transparent;}
.x-tree-no-lines .x-tree-elbow-end-plus{background-image:url(/ui-fw/resources/images/default/tree/elbow-end-plus-nl.gif);}
.x-tree-no-lines .x-tree-elbow-end-minus{background-image:url(/ui-fw/resources/images/default/tree/elbow-end-minus-nl.gif);}
.x-tree-no-lines .x-tree-elbow-line{background:transparent;}
.x-tree-elbow-plus,.x-tree-elbow-minus,.x-tree-elbow-end-plus,.x-tree-elbow-end-minus{cursor:pointer;}
.ext-ie ul.x-tree-node-ct{font-size:0;line-height:0;}
.x-tree-node{color:black;font:normal 11px arial,tahoma,helvetica,sans-serif;white-space:nowrap;}
.x-tree-node a,.x-dd-drag-ghost a{text-decoration:none;color:black;-khtml-user-select:none;-moz-user-select:none;-kthml-user-focus:normal;-moz-user-focus:normal;-moz-outline:0 none;outline:0 none;}
.x-tree-node a span,.x-dd-drag-ghost a span{text-decoration:none;color:black;padding:1px 3px 1px 2px;}
.x-tree-node .x-tree-node-disabled a span{color:gray!important;}
.x-tree-node .x-tree-node-disabled .x-tree-node-icon{-moz-opacity:0.5;opacity:.5;filter:alpha(opacity=50);}
.x-tree-node .x-tree-node-inline-icon{background:transparent;}
.x-tree-node a:hover,.x-dd-drag-ghost a:hover{text-decoration:none;}
.x-tree-node div.x-tree-drag-insert-below{border-bottom:1px dotted #36c;}
.x-tree-node div.x-tree-drag-insert-above{border-top:1px dotted #36c;}
.x-tree-dd-underline .x-tree-node div.x-tree-drag-insert-below{border-bottom:0 none;}
.x-tree-dd-underline .x-tree-node div.x-tree-drag-insert-above{border-top:0 none;}
.x-tree-dd-underline .x-tree-node div.x-tree-drag-insert-below a{border-bottom:2px solid #36c;}
.x-tree-dd-underline .x-tree-node div.x-tree-drag-insert-above a{border-top:2px solid #36c;}
.x-tree-node .x-tree-drag-append a span{background:#ddd;border:1px dotted gray;}
.x-tree-node .x-tree-selected a span{background:#36c;color:white;}
.x-dd-drag-ghost .x-tree-node-indent,.x-dd-drag-ghost .x-tree-ec-icon{display:none!important;}
.x-tree-drop-ok-append .x-dd-drop-icon{background-image:url(/ui-fw/resources/images/default/tree/drop-add.gif);}
.x-tree-drop-ok-above .x-dd-drop-icon{background-image:url(/ui-fw/resources/images/default/tree/drop-over.gif);}
.x-tree-drop-ok-below .x-dd-drop-icon{background-image:url(/ui-fw/resources/images/default/tree/drop-under.gif);}
.x-tree-drop-ok-between .x-dd-drop-icon{background-image:url(/ui-fw/resources/images/default/tree/drop-between.gif);}

.x-tip{position:absolute;top:0;left:0;visibility:hidden;z-index:20000;border:0 none;}
.x-tip .x-tip-close{background-image:url(/ui-fw/resources/images/default/qtip/close.gif);height:15px;float:right;width:15px;margin:0 0 2px 2px;cursor:pointer;display:none;}
.x-tip .x-tip-top{background:transparent url(/ui-fw/resources/images/default/qtip/tip-sprite.gif) no-repeat 0 -12px;height:6px;overflow:hidden;}
.x-tip .x-tip-top-left{background:transparent url(/ui-fw/resources/images/default/qtip/tip-sprite.gif) no-repeat 0 0;padding-left:6px;zoom:1;}
.x-tip .x-tip-top-right{background:transparent url(/ui-fw/resources/images/default/qtip/tip-sprite.gif) no-repeat right 0;padding-right:6px;zoom:1;}
.x-tip .x-tip-ft{background:transparent url(/ui-fw/resources/images/default/qtip/tip-sprite.gif) no-repeat 0 -18px;height:6px;overflow:hidden;}
.x-tip .x-tip-ft-left{background:transparent url(/ui-fw/resources/images/default/qtip/tip-sprite.gif) no-repeat 0 -6px;padding-left:6px;zoom:1;}
.x-tip .x-tip-ft-right{background:transparent url(/ui-fw/resources/images/default/qtip/tip-sprite.gif) no-repeat right -6px;padding-right:6px;zoom:1;}
.x-tip .x-tip-bd{border:0 none;font:normal 11px tahoma,arial,helvetica,sans-serif;}
.x-tip .x-tip-bd-left{background:#fff url(/ui-fw/resources/images/default/qtip/tip-sprite.gif) no-repeat 0 -24px;padding-left:6px;zoom:1;}
.x-tip .x-tip-bd-right{background:transparent url(/ui-fw/resources/images/default/qtip/tip-sprite.gif) no-repeat right -24px;padding-right:6px;zoom:1;}
.x-tip h3{font:bold 11px tahoma,arial,helvetica,sans-serif;margin:0;padding:2px 0;color:#444;}
.x-tip .x-tip-bd-inner{font:normal 11px tahoma,arial,helvetica,sans-serif;margin:0!important;line-height:14px;color:#444;padding:0;float:left;}
.x-form-invalid-tip .x-tip-top{background-image:url(/ui-fw/resources/images/default/form/error-tip-corners.gif);}
.x-form-invalid-tip .x-tip-top-left{background-image:url(/ui-fw/resources/images/default/form/error-tip-corners.gif);}
.x-form-invalid-tip .x-tip-top-right{background-image:url(/ui-fw/resources/images/default/form/error-tip-corners.gif);}
.x-form-invalid-tip .x-tip-ft{background-image:url(/ui-fw/resources/images/default/form/error-tip-corners.gif);}
.x-form-invalid-tip .x-tip-ft-left{background-image:url(/ui-fw/resources/images/default/form/error-tip-corners.gif);}
.x-form-invalid-tip .x-tip-ft-right{background-image:url(/ui-fw/resources/images/default/form/error-tip-corners.gif);}
.x-form-invalid-tip .x-tip-bd-left{background-image:url(/ui-fw/resources/images/default/form/error-tip-corners.gif);}
.x-form-invalid-tip .x-tip-bd-right{background-image:url(/ui-fw/resources/images/default/form/error-tip-corners.gif);}
.x-form-invalid-tip .x-tip-bd .x-tip-bd-inner{padding-left:24px;background:transparent url(/ui-fw/resources/images/default/form/exclamation.gif) no-repeat 2px 2px;}
.x-form-invalid-tip .x-tip-bd-inner{padding:2px;}

.x-date-picker{border:1px solid #1b376c;border-top:0 none;background:#fff;position:relative;}
.x-date-picker a{-moz-outline:0 none;outline:0 none;}
.x-date-inner,.x-date-inner td,.x-date-inner th{border-collapse:separate;}
.x-date-middle,.x-date-left,.x-date-right{background:url(/ui-fw/resources/images/default/basic-dialog/hd-sprite.gif) repeat-x 0 -83px;color:#FFF;font:bold 11px "sans serif",tahoma,verdana,helvetica;overflow:hidden;}
.x-date-middle .x-btn-left,.x-date-middle .x-btn-center,.x-date-middle .x-btn-right{background:transparent!important;vertical-align:middle;}
.x-date-middle .x-btn .x-btn-text{color:#fff;}
.x-date-middle .x-btn-with-menu .x-btn-center em{background:transparent url(/ui-fw/resources/images/default/toolbar/btn-arrow-light.gif) no-repeat right 0;}
.x-date-right,.x-date-left{width:18px;}
.x-date-right{text-align:right;}
.x-date-middle{padding-top:2px;padding-bottom:2px;}
.x-date-right a,.x-date-left a{display:block;width:16px;height:16px;background-position:center;background-repeat:no-repeat;cursor:pointer;-moz-opacity:0.6;opacity:.6;filter:alpha(opacity=60);}
.x-date-right a:hover,.x-date-left a:hover{-moz-opacity:1;opacity:1;filter:alpha(opacity=100);}
.x-date-right a{background-image:url(/ui-fw/resources/images/default/shared/right-btn.gif);margin-right:2px;text-decoration:none!important;}
.x-date-left a{background-image:url(/ui-fw/resources/images/default/shared/left-btn.gif);margin-left:2px;text-decoration:none!important;}
table.x-date-inner{width:100%;table-layout:fixed;}
.x-date-inner th{width:25px;}
.x-date-inner th{background:#dfecfb url(/ui-fw/resources/images/default/shared/glass-bg.gif) repeat-x left top;text-align:right!important;border-bottom:1px solid #a3bad9;font:normal 10px arial,helvetica,tahoma,sans-serif;color:#233d6d;cursor:default;padding:0;border-collapse:separate;}
.x-date-inner th span{display:block;padding:2px;padding-right:7px;}
.x-date-inner td{border:1px solid #fff;text-align:right;padding:0;}
.x-date-inner a{padding:2px 5px;display:block;font:normal 11px arial,helvetica,tahoma,sans-serif;text-decoration:none;color:black;text-align:right;zoom:1;}
.x-date-inner .x-date-active{cursor:pointer;color:black;}
.x-date-inner .x-date-selected a{background:#dfecfb url(/ui-fw/resources/images/default/shared/glass-bg.gif) repeat-x left top;border:1px solid #8db2e3;padding:1px 4px;}
.x-date-inner .x-date-today a{border:1px solid darkred;padding:1px 4px;}
.x-date-inner .x-date-selected span{font-weight:bold;}
.x-date-inner .x-date-prevday a,.x-date-inner .x-date-nextday a{color:#aaa;text-decoration:none!important;}
.x-date-bottom{padding:4px;border-top:1px solid #a3bad9;background:#dfecfb url(/ui-fw/resources/images/default/shared/glass-bg.gif) repeat-x left top;}
.x-date-inner a:hover,.x-date-inner .x-date-disabled a:hover{text-decoration:none!important;color:black;background:#ddecfe;}
.x-date-inner .x-date-disabled a{cursor:default;background:#eee;color:#bbb;}
.x-date-mmenu{background:#eee!important;}
.x-date-mmenu .x-menu-item{font-size:10px;padding:1px 24px 1px 4px;white-space:nowrap;color:#000;}
.x-date-mmenu .x-menu-item .x-menu-item-icon{width:10px;height:10px;margin-right:5px;background-position:center -4px!important;}
.x-date-mp{position:absolute;left:0;top:0;background:white;display:none;}
.x-date-mp td{padding:2px;font:normal 11px arial,helvetica,tahoma,sans-serif;}
td.x-date-mp-month,td.x-date-mp-year,td.x-date-mp-ybtn{border:0 none;text-align:center;vertical-align:middle;width:25%;}
.x-date-mp-ok{margin-right:3px;}
.x-date-mp-btns button{text-decoration:none;text-align:center;text-decoration:none!important;background:#083772;color:white;border:1px solid;border-color:#36c #005 #005 #36c;padding:1px 3px 1px;font:normal 11px arial,helvetica,tahoma,sans-serif;cursor:pointer;}
.x-date-mp-btns{background:#dfecfb url(/ui-fw/resources/images/default/shared/glass-bg.gif) repeat-x left top;}
.x-date-mp-btns td{border-top:1px solid #c5d2df;text-align:center;}
td.x-date-mp-month a,td.x-date-mp-year a{display:block;padding:2px 4px;text-decoration:none;text-align:center;color:#15428b;}
td.x-date-mp-month a:hover,td.x-date-mp-year a:hover{color:#15428b;text-decoration:none;cursor:pointer;background:#ddecfe;}
td.x-date-mp-sel a{padding:1px 3px;background:#dfecfb url(/ui-fw/resources/images/default/shared/glass-bg.gif) repeat-x left top;border:1px solid #8db2e3;}
.x-date-mp-ybtn a{overflow:hidden;width:15px;height:15px;cursor:pointer;background:transparent url(/ui-fw/resources/images/default/panel/tool-sprites.gif) no-repeat;display:block;margin:0 auto;}
.x-date-mp-ybtn a.x-date-mp-next{background-position:0 -120px;}
.x-date-mp-ybtn a.x-date-mp-next:hover{background-position:-15px -120px;}
.x-date-mp-ybtn a.x-date-mp-prev{background-position:0 -105px;}
.x-date-mp-ybtn a.x-date-mp-prev:hover{background-position:-15px -105px;}
.x-date-mp-ybtn{text-align:center;}
td.x-date-mp-sep{border-right:1px solid #c5d2df;}

.x-menu{border:1px solid #718bb7;z-index:15000;background:#fff url(/ui-fw/resources/images/default/menu/menu.gif) repeat-y;}
.ext-ie .x-menu{zoom:1;overflow:hidden;}
.x-menu-list{border:1px solid #fff;}
.x-menu li{line-height:100%;}
.x-menu li.x-menu-sep-li{font-size:1px;line-height:1px;}
.x-menu-list-item{font:normal 11px tahoma,arial,sans-serif;white-space:nowrap;-moz-user-select:none;-khtml-user-select:none;display:block;padding:1px;}
.x-menu-item-arrow{background:transparent url(/ui-fw/resources/images/default/menu/menu-parent.gif) no-repeat right;}
.x-menu-sep{display:block;font-size:1px;line-height:1px;height:1px;background:#c3daf9;margin:3px 3px 3px 32px;}
.x-menu-focus{position:absolute;left:0;top:-5px;width:0;height:0;line-height:1px;}
.x-menu-item{display:block;line-height:14px;padding:3px 21px 3px 3px;white-space:nowrap;text-decoration:none;color:#233d6d;-moz-outline:0 none;outline:0 none;cursor:pointer;}
.x-menu-item-active{color:#233d6d;background:#c3daf9;border:1px solid #8BB8F3;padding:0;}
.x-menu-item-icon{border:0 none;height:16px;padding:0;vertical-align:middle;width:16px;margin:0 11px 0 0;background-position:center;}
.x-menu-check-item .x-menu-item-icon{background:transparent url(/ui-fw/resources/images/default/menu/unchecked.gif) no-repeat center;}
.x-menu-item-checked .x-menu-item-icon{background-image:url(/ui-fw/resources/images/default/menu/checked.gif);}
.x-menu-group-item .x-menu-item-icon{background:transparent;}
.x-menu-item-checked .x-menu-group-item .x-menu-item-icon{background:transparent url(/ui-fw/resources/images/default/menu/group-checked.gif) no-repeat center;}
.x-menu-plain{background:#fff;}
.x-menu-date-item{padding:0;}
.x-menu .x-color-palette,.x-menu .x-date-picker{margin-left:32px;margin-right:4px;}
.x-menu .x-date-picker{border:1px solid #a3bad9;margin-top:2px;margin-bottom:2px;}
.x-menu-plain .x-color-palette,.x-menu-plain .x-date-picker{margin:0;border:0 none;}
.x-date-menu{padding:0!important;}

 .x-box-tl{background:transparent url(/ui-fw/resources/images/default/box/corners.gif) no-repeat 0 0;zoom:1;}
.x-box-tc{height:8px;background:transparent url(/ui-fw/resources/images/default/box/tb.gif) repeat-x 0 0;overflow:hidden;}
.x-box-tr{background:transparent url(/ui-fw/resources/images/default/box/corners.gif) no-repeat right -8px;}
.x-box-ml{background:transparent url(/ui-fw/resources/images/default/box/l.gif) repeat-y 0;padding-left:4px;overflow:hidden;zoom:1;}
.x-box-mc{background:#eee url(/ui-fw/resources/images/default/box/tb.gif) repeat-x 0 -16px;padding:4px 10px;font-family:"Myriad Pro","Myriad Web","Tahoma","Helvetica","Arial",sans-serif;color:#393939;font-size:12px;}
.x-box-mc h3{font-size:14px;font-weight:bold;margin:0 0 4 0;zoom:1;}
.x-box-mr{background:transparent url(/ui-fw/resources/images/default/box/r.gif) repeat-y right;padding-right:4px;overflow:hidden;}
.x-box-bl{background:transparent url(/ui-fw/resources/images/default/box/corners.gif) no-repeat 0 -16px;zoom:1;}
.x-box-bc{background:transparent url(/ui-fw/resources/images/default/box/tb.gif) repeat-x 0 -8px;height:8px;overflow:hidden;}
.x-box-br{background:transparent url(/ui-fw/resources/images/default/box/corners.gif) no-repeat right -24px;}
.x-box-tl,.x-box-bl{padding-left:8px;overflow:hidden;}
.x-box-tr,.x-box-br{padding-right:8px;overflow:hidden;}
.x-box-blue .x-box-bl,.x-box-blue .x-box-br,.x-box-blue .x-box-tl,.x-box-blue .x-box-tr{background-image:url(/ui-fw/resources/images/default/box/corners-blue.gif);}
.x-box-blue .x-box-bc,.x-box-blue .x-box-mc,.x-box-blue .x-box-tc{background-image:url(/ui-fw/resources/images/default/box/tb-blue.gif);}
.x-box-blue .x-box-mc{background-color:#c3daf9;}
.x-box-blue .x-box-mc h3{color:#17385b;}
.x-box-blue .x-box-ml{background-image:url(/ui-fw/resources/images/default/box/l-blue.gif);}
.x-box-blue .x-box-mr{background-image:url(/ui-fw/resources/images/default/box/r-blue.gif);}

#x-debug-browser .x-tree .x-tree-node a span{color:#222297;font-size:12px;padding-top:2px;font-family:"courier","courier new";line-height:18px;}
#x-debug-browser .x-tree a i{color:#FF4545;font-style:normal;}
#x-debug-browser .x-tree a em{color:#999;}
#x-debug-browser .x-tree .x-tree-node .x-tree-selected a span{background:#c3daf9;}
#x-debug-browser pre,.x-debug-browser pre xmp{font:normal 11px tahoma,arial,helvetica,sans-serif!important;white-space:-moz-pre-wrap;white-space:-pre-wrap;white-space:-o-pre-wrap;word-wrap:break-word;}
#x-debug-browser pre{display:block;padding:5px!important;border-bottom:1px solid #eee!important;}
#x-debug-browser pre xmp{padding:0!important;margin:0!important;}
#x-debug-console .x-layout-panel-center,#x-debug-inspector .x-layout-panel-center{border-right:1px solid #98c0f4;}
#x-debug-console textarea{border:0 none;font-size:12px;font-family:"courier","courier new";padding-top:4px;padding-left:4px;}
.x-debug-frame{background:#eee;border:1px dashed #aaa;}

.x-combo-list{border:1px solid #98c0f4;background:#ddecfe;zoom:1;overflow:hidden;}
.x-combo-list-inner{overflow:auto;background:white;position:relative;zoom:1;overflow-x:hidden;}
.x-combo-list-hd{font:bold 11px tahoma,arial,helvetica,sans-serif;color:#15428b;background-image:url(/ui-fw/resources/images/default/layout/panel-title-light-bg.gif);border-bottom:1px solid #98c0f4;padding:3px;}
.x-resizable-pinned .x-combo-list-inner{border-bottom:1px solid #98c0f4;}
.x-combo-list-item{font:normal 12px tahoma,arial,helvetica,sans-serif;padding:2px;border:1px solid #fff;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}
.x-combo-list .x-combo-selected{background-color:#c3daf9!important;cursor:pointer;border:1px solid #369;}
.x-combo-noedit{cursor:pointer;}
.x-combo-list-small .x-combo-list-item{font:normal 11px tahoma,arial,helvetica,sans-serif;}

.x-html-editor-wrap{border:1px solid #a9bfd3;background:white;}
.x-html-editor-tb .x-btn-text{background:transparent url(/ui-fw/resources/images/default/editor/tb-sprite.gif) no-repeat;}
.x-html-editor-tb .x-edit-bold .x-btn-text{background-position:0 0;}
.x-html-editor-tb .x-edit-italic .x-btn-text{background-position:-16px 0;}
.x-html-editor-tb .x-edit-underline .x-btn-text{background-position:-32px 0;}
.x-html-editor-tb .x-edit-forecolor .x-btn-text{background-position:-160px 0;}
.x-html-editor-tb .x-edit-backcolor .x-btn-text{background-position:-176px 0;}
.x-html-editor-tb .x-edit-justifyleft .x-btn-text{background-position:-112px 0;}
.x-html-editor-tb .x-edit-justifycenter .x-btn-text{background-position:-128px 0;}
.x-html-editor-tb .x-edit-justifyright .x-btn-text{background-position:-144px 0;}
.x-html-editor-tb .x-edit-insertorderedlist .x-btn-text{background-position:-80px 0;}
.x-html-editor-tb .x-edit-insertunorderedlist .x-btn-text{background-position:-96px 0;}
.x-html-editor-tb .x-edit-increasefontsize .x-btn-text{background-position:-48px 0;}
.x-html-editor-tb .x-edit-decreasefontsize .x-btn-text{background-position:-64px 0;}
.x-html-editor-tb .x-edit-sourceedit .x-btn-text{background-position:-192px 0;}
.x-html-editor-tb .x-edit-createlink .x-btn-text{background-position:-208px 0;}
.x-html-editor-tip .x-tip-bd .x-tip-bd-inner{padding:5px;padding-bottom:1px;}
.x-html-editor-tb .x-toolbar{position:static!important;}

/*
 * Ext JS Library 1.1.1
 * Copyright(c) 2006-2007, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://www.extjs.com/license
 */

/* menus */
.x-menu {
	border: 1px solid #718bb7;
	z-index: 15000;
	zoom: 1;
	background: #f0f0f0 url(/ui-fw/resources/images/aero/menu/menu.gif) repeat-y;
	padding: 2px;
}

.x-menu-list{
	background:transparent;
	border:0 none;
}

.x-menu-item-icon {
	margin-right: 8px;
}

.x-menu-sep {
	background-color:#e0e0e0;
	border-bottom:1px solid #fff;
	margin-left:3px;
}
.x-menu-item {
	color:#222;
}
.x-menu-item-active {
	color: #233d6d;
	background: #ebf3fd url(/ui-fw/resources/images/aero/menu/item-over.gif) repeat-x left bottom;
	border:1px solid #aaccf6;
	padding: 0;
}

.x-date-mmenu .x-menu-list{
	padding: 0;
}

.x-date-mmenu .x-menu-list{
	border: 0 none;
}

.x-menu .x-color-palette, .x-menu .x-date-picker{
	margin-left: 26px;
}

.x-menu-plain .x-color-palette, .x-menu-plain .x-date-picker{
	margin: 0;
	border: 0 none;
}


.x-menu-check-item .x-menu-item-icon{
	background-image: url(/ui-fw/resources/images/aero/menu/unchecked.gif);
}

.x-menu-item-checked .x-menu-item-icon{
	background-image:url(/ui-fw/resources/images/aero/menu/checked.gif);
}

.x-menu-group-item .x-menu-item-icon{
	background: transparent;
}

.x-menu-item-checked .x-menu-group-item .x-menu-item-icon{
    background: transparent url(/ui-fw/resources/images/default/menu/group-checked.gif) no-repeat center;
}
/**
* Tabs
*/
.x-tabs-wrap, .x-layout-panel .x-tabs-top .x-tabs-wrap {
	background: #deecfd;
	border: 1px solid #8db2e3;
	padding-bottom: 2px;
	padding-top: 0;
}

.x-tabs-strip-wrap{
	padding-top: 1px;
	background: url(/ui-fw/resources/images/aero/tabs/tab-strip-bg.gif) #cedff5 repeat-x bottom;
	border-bottom: 1px solid #8db2e3;
}

.x-tabs-strip .x-tabs-text {
	color: #15428b;
	font: bold 11px tahoma,arial,verdana,sans-serif;
}

.x-tabs-strip .on .x-tabs-text {
	cursor: default;
	color: #15428b;
}

.x-tabs-top .x-tabs-strip .on .x-tabs-right {
	background: url(/ui-fw/resources/images/aero/tabs/tab-sprite.gif) no-repeat right 0;
}

.x-tabs-top .x-tabs-strip .on .x-tabs-left,.x-tabs-top .x-tabs-strip .on a:hover .x-tabs-left{
	background: url(/ui-fw/resources/images/aero/tabs/tab-sprite.gif) no-repeat 0 -100px;
}

.x-tabs-top .x-tabs-strip .x-tabs-right {
	background: transparent url(/ui-fw/resources/images/aero/tabs/tab-sprite.gif) no-repeat right -50px;
}

.x-tabs-top .x-tabs-strip .x-tabs-left {
	background: transparent url(/ui-fw/resources/images/aero/tabs/tab-sprite.gif) no-repeat 0 -150px;
}

.x-tabs-top .x-tabs-body {
	border: 1px solid #8db2e3;
	border-top: 0 none;
}

.x-tabs-bottom .x-tabs-wrap, .x-layout-panel .x-tabs-bottom .x-tabs-wrap {
	background: #deecfd;
	border: 1px solid #8db2e3;
	padding-top: 2px;
	padding-bottom: 0;
}

.x-tabs-bottom .x-tabs-strip-wrap{
	padding-top: 0;
	padding-bottom: 1px;
	background: url(/ui-fw/resources/images/aero/tabs/tab-strip-btm-bg.gif) #cedff5 repeat-x top;
	border-top: 1px solid #8db2e3;
	border-bottom: 0 none;
}

.x-tabs-bottom .x-tabs-strip .x-tabs-right {
	background: transparent url(/ui-fw/resources/images/aero/tabs/tab-btm-inactive-right-bg.gif) no-repeat bottom right;
}

.x-tabs-bottom .x-tabs-strip .x-tabs-left {
	background: transparent url(/ui-fw/resources/images/aero/tabs/tab-btm-inactive-left-bg.gif) no-repeat bottom left;
}

.x-tabs-bottom .x-tabs-strip .on .x-tabs-right,.x-tabs-bottom .x-tabs-strip .on a:hover {
	background: url(/ui-fw/resources/images/aero/tabs/tab-btm-right-bg.gif) no-repeat bottom left;
}

.x-tabs-bottom .x-tabs-strip .on .x-tabs-left,.x-tabs-bottom .x-tabs-strip .on a:hover .x-tabs-left {
	background: url(/ui-fw/resources/images/aero/tabs/tab-btm-left-bg.gif) no-repeat bottom right;
}

.x-tabs-bottom .x-tabs-body {
	border: 1px solid #8db2e3;
	border-bottom: 0 none;
}

/*
* Basic-Dialog 
*/
.x-dlg-proxy {
	background: #c7dffc;
	border: 1px solid #a5ccf9;
}

.x-dlg-shadow{
	background: #cccccc;
	opacity: .3;
	-moz-opacity: .3;
	filter: alpha(opacity=30);
}

.x-dlg {
	background: transparent;
}

.x-dlg .x-dlg-hd {
	background: url(/ui-fw/resources/images/aero/basic-dialog/hd-sprite.gif) repeat-x 0 -82px;
	background-color: #aabaca;
	color: #15428b;
	zoom: 1;
	padding-top: 7px;
}

.x-dlg .x-dlg-hd-left {
	opacity: .85;
	-moz-opacity: .85;
	filter: alpha(opacity=80);
	background: url(/ui-fw/resources/images/aero/basic-dialog/hd-sprite.gif) no-repeat 0 -41px;
	zoom: 1;
}

.x-dlg-modal .x-dlg-hd-left {
	opacity: .75;
	-moz-opacity: .75;
	filter: alpha(opacity=70);
}

.x-dlg .x-dlg-hd-right {
	background: url(/ui-fw/resources/images/aero/basic-dialog/hd-sprite.gif) no-repeat right 0;
	zoom: 1;
}

.x-dlg .x-dlg-dlg-body{
	padding: 0 0 0;
	position: absolute;
	top: 24px;
	left: 0;
	z-index: 1;
	border: 0 none;
	background: transparent;
}

.x-dlg .x-dlg-bd{
	background: #ffffff;
	border: 1px solid #96b9e6;
}

.x-dlg .x-dlg-ft{
	border: 0 none;
	background: transparent;
	padding-bottom: 8px;
}

.x-dlg .x-dlg-bg{
	filter: alpha(opacity=80);
	opacity: .85;
	-moz-opacity: .85;
	zoom: 1;
}

.x-dlg-modal .x-dlg-bg {
	opacity: .75;
	-moz-opacity: .75;
	filter: alpha(opacity=70);
}

.x-dlg .x-dlg-bg-center {
	padding: 2px 7px 7px 7px;
	background: transparent url(/ui-fw/resources/images/aero/basic-dialog/bg-center.gif) repeat-x bottom;
	zoom: 1;
}

.x-dlg .x-dlg-bg-left{
	padding-left: 7px;
	background: transparent url(/ui-fw/resources/images/aero/basic-dialog/bg-left.gif) no-repeat bottom left;
	zoom: 1;
}

.x-dlg .x-dlg-bg-right{
	padding-right: 7px;
	background: transparent url(/ui-fw/resources/images/aero/basic-dialog/bg-right.gif) no-repeat bottom right;
	zoom: 1;
}

.x-dlg-auto-tabs .x-dlg-dlg-body, .x-dlg-auto-layout .x-dlg-dlg-body{
	background: transparent;
	border: 0 none;
}

.x-dlg-auto-tabs .x-dlg-bd, .x-dlg-auto-layout .x-dlg-bd{
	background: #ffffff;
	border: 1px solid #e9f3f5;
}

.x-dlg-auto-tabs .x-tabs-top .x-tabs-body,.x-dlg-auto-tabs .x-tabs-bottom .x-tabs-body{
	border-color: #8db2e3;
}

.x-dlg-auto-tabs .x-tabs-top .x-tabs-wrap,.x-dlg-auto-tabs .x-tabs-bottom .x-tabs-wrap{
	border-color: #8db2e3;
}

.x-dlg .x-dlg-toolbox {
	width: 50px;
	height: 20px;
	right: 5px;
	top: 5px;
}

.x-dlg .x-dlg-close, .x-dlg .x-dlg-collapse {
	width: 21px;
	height: 20px;
	margin: 0;
}

.x-dlg .x-dlg-close {
	background-image: url(/ui-fw/resources/images/aero/basic-dialog/aero-close.gif);
}

.x-dlg .x-dlg-collapse {
	background-image: url(/ui-fw/resources/images/aero/basic-dialog/collapse.gif);
}

.x-dlg-collapsed {
	border-bottom: 1px solid #96b9e6;
}

.x-dlg .x-dlg-close-over {
	background-image: url(/ui-fw/resources/images/aero/basic-dialog/aero-close-over.gif);
}

.x-dlg .x-dlg-collapse-over {
	background-image: url(/ui-fw/resources/images/aero/basic-dialog/collapse-over.gif);
}

.x-dlg-collapsed .x-dlg-collapse {
	background-image: url(/ui-fw/resources/images/aero/basic-dialog/expand.gif);
}

.x-dlg-collapsed .x-dlg-collapse-over {
	background-image: url(/ui-fw/resources/images/aero/basic-dialog/expand-over.gif);
}

.x-dlg div.x-resizable-handle-east{
	background-image: url(/ui-fw/resources/images/aero/s.gif);
	border: 0 none;
}

.x-dlg div.x-resizable-handle-south{
	background-image: url(/ui-fw/resources/images/aero/s.gif);
	border: 0 none;
}

.x-dlg div.x-resizable-handle-west{
	background-image: url(/ui-fw/resources/images/aero/s.gif);
	border: 0 none;
}

.x-dlg div.x-resizable-handle-southeast{
	background-image: url(/ui-fw/resources/images/aero/basic-dialog/se-handle.gif);
	background-position: bottom right;
	width: 9px;
	height: 9px;
	border: 0;
	right: 2px;
	bottom: 2px;
}

.x-dlg div.x-resizable-handle-southwest{
	background-image: url(/ui-fw/resources/images/aero/s.gif);
	background-position: top right;
	margin-left: 1px;
	margin-bottom: 1px;
	border: 0;
}

.x-dlg div.x-resizable-handle-north{
	background-image: url(/ui-fw/resources/images/aero/s.gif);
	border: 0 none;
}

#x-msg-box .x-dlg-bd{
	background: #cfe0f5;
	border: 0 none;
}

body.x-masked #x-msg-box .x-dlg-bd, body.x-body-masked #x-msg-box .x-dlg-bd{
	background: #c4d2e3;
	border: 0 none;
}

/* BorderLayout */
.x-layout-container{
	background: #deecfd;
}

.x-layout-collapsed{
	background-color: #deecfd;
	border: 1px solid #99bbe8;
}

.x-layout-collapsed-over{
	background-color: #f5f9fe;
}

.x-layout-panel{
	border: 1px solid #99bbe8;
}

.x-layout-nested-layout .x-layout-panel {
	border: 0 none;
}

.x-layout-split{
	background-color: #deecfd;
}

.x-layout-panel-hd{
	background: url(/ui-fw/resources/images/aero/grid/grid-hrow.gif) #ebeadb repeat-x;
	border-bottom: 1px solid #99bbe8;
}

.x-layout-panel-hd-text {
	color: #15428b;
	font: bold 11px tahoma,arial,verdana,sans-serif;
}

.x-layout-split-h{
	background: #deecfd;
}

.x-layout-split-v{
	background: #deecfd;
}

.x-layout-panel .x-tabs-top .x-tabs-wrap{
	border: 0 none;
	border-bottom: 1px solid #8db2e3;
}

.x-layout-panel .x-tabs-bottom .x-tabs-wrap{
	border: 0 none;
	border-top: 1px solid #8db2e3;
}

.x-layout-nested-layout .x-layout-panel-north {
	border-bottom: 1px solid #99bbe8;
}

.x-layout-nested-layout .x-layout-panel-south {
	border-top: 1px solid #99bbe8;
}

.x-layout-nested-layout .x-layout-panel-east {
	border-left: 1px solid #99bbe8;
}

.x-layout-nested-layout .x-layout-panel-west {
	border-right: 1px solid #99bbe8;
}

.x-layout-panel-dragover {
	border: 2px solid #99bbe8;
}

.x-layout-panel-proxy {
	background-image: url(/ui-fw/resources/images/aero/layout/gradient-bg.gif);
	background-color: #f3f2e7;
	border: 1px dashed #99bbe8;
}

.x-layout-container .x-layout-tabs-body{
	border: 0 none;
}

/** Resizable */
.x-resizable-proxy{
	border: 1px dashed #3b5a82;
}

/* grid */
.x-grid-hd-text {
	color: #15428b;
	font-weight: bold;
}

.x-grid-locked .x-grid-body td {
	background: #fbfdff;
	border-right: 1px solid #deecfd;
	border-bottom: 1px solid #deecfd !important;
}

.x-grid-locked .x-grid-body td .x-grid-cell-inner {
	border-top: 0 none;
}

.x-grid-locked .x-grid-row-alt td{
	background: #f5fafe;
}

.x-grid-locked .x-grid-row-selected td{
	color: #ffffff !important;
	background-color: #316ac5 !important;
}

.x-grid-hd{
	border-bottom: 0;
	background: none;
}

.x-grid-hd-row{
	height: auto;
}

.x-grid-hd-over {
	border-bottom: 0 none;
}

.x-grid-hd-over .x-grid-hd-body{
	background: none;
	border-bottom: 0 none;
}

.x-grid-hd-over .x-grid-hd-body{
	background-color: transparent;
	border-bottom: 0;
}

.x-grid-split {
	background-image: url(/ui-fw/resources/images/aero/grid/grid-blue-split.gif);
}

.x-grid-header{
	background: url(/ui-fw/resources/images/aero/grid/grid-hrow.gif);
	border: 0 none;
	border-bottom: 1px solid #6f99cf;
}

.x-grid-row-alt{
	background-color: #f5f5f5;
}

.x-grid-row-over td, .x-grid-locked .x-grid-row-over td{
	background-color: #d9e8fb;
}

.x-grid-col {
	border-right: 1px solid #eeeeee;
	border-bottom: 1px solid #eeeeee;
}

.x-grid-header .x-grid-hd-inner {
	padding-bottom: 1px;
}

.x-grid-header  .x-grid-hd-text {
	padding-bottom: 3px;
}

.x-grid-hd-over .x-grid-hd-inner {
	border-bottom: 1px solid #316ac5;
	padding-bottom: 0;
}

.x-grid-hd-over .x-grid-hd-text {
	background: #d5e4f5;
	border-bottom: 1px solid #ffffff;
	padding-bottom: 2px;
}

.x-grid-header .sort-asc .x-grid-hd-inner, .x-grid-header .sort-desc .x-grid-hd-inner {
	border-bottom: 1px solid #316ac5;
	padding-bottom: 0;
}

.x-grid-header .sort-asc  .x-grid-hd-text, .x-grid-header .sort-desc .x-grid-hd-text {
	border-bottom: 0 none;
	padding-bottom: 3px;
}

.x-grid-header .sort-asc .x-grid-sort-icon {
	background-image: url(/ui-fw/resources/images/aero/grid/sort_asc.gif);
}

.x-grid-header .sort-desc .x-grid-sort-icon {
	background-image: url(/ui-fw/resources/images/aero/grid/sort_desc.gif);
}

.x-dd-drag-proxy .x-grid-hd-inner{
	background: url(/ui-fw/resources/images/aero/grid/grid-hrow.gif) #ebeadb repeat-x;
	height: 22px;
	width: 120px;
}

.x-grid-locked td.x-grid-row-marker, .x-grid-locked .x-grid-row-selected td.x-grid-row-marker{
	background: url(/ui-fw/resources/images/aero/grid/grid-hrow.gif) #ebeadb repeat-x 0 0 !important;
	vertical-align: middle !important;
	color: #000000;
	padding: 0;
	border-top: 1px solid #ffffff;
	border-bottom: 1px solid #6f99cf !important;
	border-right: 1px solid #6f99cf !important;
	text-align: center;
}

.x-grid-locked td.x-grid-row-marker div, .x-grid-locked .x-grid-row-selected td.x-grid-row-marker div{
	padding: 0 4px;
	color: #15428b !important;
	text-align: center;
}

/** Toolbar */
.x-toolbar{
	padding: 2px 2px 2px 2px;
	background: url(/ui-fw/resources/images/default/toolbar/tb-bg.gif) #d0def0 repeat-x;
}

.x-toolbar .ytb-sep{
	background-image: url(/ui-fw/resources/images/aero/grid/grid-blue-split.gif);
}

.x-toolbar .x-btn-over .x-btn-left{
	background: url(/ui-fw/resources/images/aero/toolbar/tb-btn-sprite.gif) no-repeat 0 0;
}

.x-toolbar .x-btn-over .x-btn-right{
	background: url(/ui-fw/resources/images/aero/toolbar/tb-btn-sprite.gif) no-repeat 0 -21px;
}

.x-toolbar .x-btn-over .x-btn-center{
	background: url(/ui-fw/resources/images/aero/toolbar/tb-btn-sprite.gif) repeat-x 0 -42px;
}

.x-toolbar .x-btn-click .x-btn-left, .x-toolbar .x-btn-pressed .x-btn-left, .x-toolbar .x-btn-menu-active .x-btn-left{
	background: url(/ui-fw/resources/images/aero/toolbar/tb-btn-sprite.gif) no-repeat 0 -63px;
}

.x-toolbar .x-btn-click .x-btn-right, .x-toolbar .x-btn-pressed .x-btn-right, .x-toolbar .x-btn-menu-active .x-btn-right{
	background: url(/ui-fw/resources/images/aero/toolbar/tb-btn-sprite.gif) no-repeat 0 -84px;
}

.x-toolbar .x-btn-click .x-btn-center, .x-toolbar .x-btn-pressed .x-btn-center, .x-toolbar .x-btn-menu-active .x-btn-center{
	background: url(/ui-fw/resources/images/aero/toolbar/tb-btn-sprite.gif) repeat-x 0 -105px;
}

/*************** TABS 2 *****************/
/**
* Tabs
*/
.x-tab-panel-header, .x-tab-panel-footer {
	background: #deecfd;
	border: 1px solid #8db2e3;
}

.x-tab-panel-header {
	background: #deecfd;
	border: 1px solid #8db2e3;
	padding-bottom: 2px;
}

.x-tab-panel-footer {
	background: #deecfd;
	border: 1px solid #8db2e3;
	padding-top: 2px;
}

.x-tab-strip-top{
	padding-top: 1px;
	background: url(/ui-fw/resources/images/aero/tabs/tab-strip-bg.gif) #cedff5 repeat-x bottom;
	border-bottom: 1px solid #8db2e3;
}

.x-tab-strip-bottom{
	padding-bottom: 1px;
	background: url(/ui-fw/resources/images/aero/tabs/tab-strip-btm-bg.gif) #cedff5 repeat-x top;
	border-top: 1px solid #8db2e3;
	border-bottom: 0 none;
}

.x-tab-strip .x-tab-strip-text {
	color: #15428b;
	font: bold 11px tahoma,arial,verdana,sans-serif;
}

.x-tab-strip .x-tab-strip-active .x-tab-text {
	cursor: default;
	color: #15428b;
}

.x-tab-strip-top .x-tab-strip-active .x-tab-right {
	background: url(/ui-fw/resources/images/aero/tabs/tab-sprite.gif) no-repeat right 0;
}

.x-tab-strip-top .x-tab-strip-active .x-tab-left {
	background: url(/ui-fw/resources/images/aero/tabs/tab-sprite.gif) no-repeat 0 -100px;
}

.x-tab-strip-top .x-tab-right {
	background: url(/ui-fw/resources/images/aero/tabs/tab-sprite.gif) no-repeat right -50px;
}

.x-tab-strip-top .x-tab-left {
	background: url(/ui-fw/resources/images/aero/tabs/tab-sprite.gif) no-repeat 0 -150px;
}

.x-tab-strip-bottom .x-tab-right {
	background: url(/ui-fw/resources/images/aero/tabs/tab-btm-inactive-right-bg.gif) no-repeat bottom right;
}

.x-tab-strip-bottom .x-tab-left {
	background: url(/ui-fw/resources/images/aero/tabs/tab-btm-inactive-left-bg.gif) no-repeat bottom left;
}

.x-tab-strip-bottom .x-tab-strip-active .x-tab-right {
	background: url(/ui-fw/resources/images/aero/tabs/tab-btm-right-bg.gif) no-repeat bottom left;
}

.x-tab-strip-bottom .x-tab-strip-active .x-tab-left {
	background: url(/ui-fw/resources/images/aero/tabs/tab-btm-left-bg.gif) no-repeat bottom right;
}

.x-tab-panel-body-top {
	border: 1px solid #8db2e3;
	border-top: 0 none;
}

.x-tab-panel-body-bottom {
	border: 1px solid #8db2e3;
	border-bottom: 0 none;
}

</style>

   <!--dialog space-->
    <div id="entry-dlg" style="visibility:hidden;position:absolute;top:0px;">
    <div class="x-dlg-hd">Entry Dialog</div>
	    <div class="x-dlg-bd">
	    	<div id="entryUrl">
	    	</div>
	    </div>
    </div>
   <!--tab space-->
    <div style="width:600px;">
        <div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>
        <div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc">
            <div id="tabPanel">
                <div id="tab0" style={background-color:#ffffff;height:250}>
				    <!--tasks grid-->    
				    <div id="pnlGrid" style="width:520px;height:200px;">
				    <div id="grid"></div>
				    </div>
                </div>
                <div id="tab1" style={background-color:#ffffff;height:250}>
				    <!--process grid-->    
				    <div id="processPnl" style="width:520px;height:200px;">
				    <div id="processGrid"></div>
                <div>
            </div>
            
        </div></div></div>
        <div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>
    </div>

    
 