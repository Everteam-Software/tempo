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
                        height:380,
                        proxyDrag: true,
						shadow: true
                });
                dialog.addKeyListener(27, dialog.hide, dialog);
                dialog.addButton('Close', dialog.hide, dialog);
				dialog.center
            }

            //create iframe
           	var base;
			var obj;
			base = document.getElementById("entryUrl");
			obj = document.getElementById("xForm");
			if (obj == null){
				obj = document.createElement("iframe");
			}
			obj.setAttribute("frameBorder", "0");
			//obj.style.position = "relative";
			obj.style.width = "100%";
			//obj.style.height = "400%";
			//obj.setAttribute("height", "400")
			obj.height="300";
			obj.id="xForm";

			var tempUrl = url.split("&amp;");
			var newUrl = tempUrl.join("&");
			obj.src = newUrl;
			base.appendChild(obj);

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