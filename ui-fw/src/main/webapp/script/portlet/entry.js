var EntryForm = function(){
    // define dialog object.
    var dialog;
    
    // return a public interface
    return {
        init : function(){
                // dialog = new Ext.BasicDialog("entry-dlg", { 
                //                         autoTabs:true,
                //                         width:600,
                //                         height:400,
                //                         proxyDrag: true
                //                 });
                //                 dialog.addKeyListener(27, dialog.hide, dialog);
                //                 dialog.addButton('Close', dialog.hide, dialog);
        },
       
        showDialog : function(url){
            if(!dialog){ // lazy initialize the dialog and only create it once
                dialog = new Ext.BasicDialog("entry-dlg", { 
                        //autoTabs:true,
                        width:600,
                        height:600,
                        proxyDrag: true,
						shadow: true
                });
                dialog.addKeyListener(27, dialog.hide, dialog);
                dialog.addButton('Close', dialog.hide, dialog);
				dialog.center
            }

            //create iframe
           	var base = document.getElementById("entryUrl");
			var obj = document.getElementById("taskform");
			if (obj == null){
				obj = document.createElement("iframe");
				base.appendChild(obj);
			}
			obj.setAttribute("frameBorder", "0");
			//obj.style.position = "relative";
			obj.style.width = "100%";
			//obj.style.height = "400%";
			//obj.setAttribute("height", "400")
			obj.height="520";
			obj.id="taskform";

			var tempUrl = url.split("&amp;");
			var newUrl = tempUrl.join("&");
			obj.src = newUrl;

			//show dialog
            dialog.show();

        },
        
        //hide dialog
        hideWindow : function() {
        	if(dialog) {
        		dialog.hide();
        	}
        }
    };
}();

Ext.onReady(EntryForm.init, EntryForm, true);