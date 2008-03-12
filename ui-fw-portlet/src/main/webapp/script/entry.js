var EntryForm = function(){
    // define dialog object.
    var dialog;
    
    // return a public interface
    return {
        init : function(){
                dialog = new Ext.BasicDialog("entry-dlg", { 
                        autoTabs:true,
                        width:850,
                        height:600,
                        proxyDrag: true
                });
                dialog.addKeyListener(27, dialog.hide, dialog);
                dialog.addButton('Close', dialog.hide, dialog);
        },
       
        showDialog : function(url){
            if(!dialog){ // lazy initialize the dialog and only create it once
                dialog = new Ext.BasicDialog("entry-dlg", { 
                        autoTabs:true,
                        width:850,
                        height:600,
                        proxyDrag: true
                });
                dialog.addKeyListener(27, dialog.hide, dialog);
                dialog.addButton('Close', dialog.hide, dialog);
            }

            //create iframe
           	var base;
			var obj;
			base = document.getElementById("entryUrl");
			base.innerHTML = "";
			obj = document.createElement("iframe");
            //set iframe attribute
			obj.setAttribute("frameBorder", "0");
			obj.style.position = "relative";
			obj.style.width = "800";
			obj.style.height = "520";
		
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