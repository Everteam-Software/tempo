


    var service ;
    var m;
    var error;
   // function callws(method, p)
    function callws(method)
    {
        error=false;
        m=method;
    	//var url = "http://localhost:8080/axis2/services/TaskManagementServices";
    	var url = "/axis2/services/HumanTaskOperationServices";
        //����webService�������
        //
        //ע��:
        //
        //    ����webservice(�������е�webservicedemo.asmx)
        //           HelloTo(String name)   ���name������xuд��name=wqj ,���и������һ��д,ʹ��&���ŷָ�(name=11&age=20&.....),ʹ������ƥ��
        //           ����Ĳ����������Բ�����(���ڻ�����)����Ҫ��Ĳ���
     // alert(m);  
        
        //var para = p;
        var para = $("#content").val();
     //   alert("para:" +para);
    	
        var op = {
	            data:para,
                    onComplete: showResponse,
                    onFailure:showError,
                    update:'ajaxBack'
                 };

        service = new WebService(url, method, op);
      
         service.request();
       
    //alert($("#"+method +" #input").size());
   
       $("#"+m+" #"+m+"_input" ).css("display", "none");
       $("#"+m+" #"+m+"_output").empty().append("<div style='padding-top:100px;padding-bottom:100px;'><center><img src='ajax_load.gif'/></center></div>");
       $("#"+m+" #"+m+"_output").css("display", "block");
      
        return false;
    }
    function showError(obj)
    {
		//obj ��һ��xmlHttpRequest����
	//	alert("error="+obj.responseText+","+obj.statusText);
		error= true;		
		$("#"+m+" #"+m+"_output").empty().append("Response<br/><textArea id=outputtext  rows=13 cols=60>"+obj.responseText+"</textArea>");
		
    }
    function showResponse(requestText,requestXML)
    {
		//requestText ���ص��ı�
		//requestXML ���ص�XML
		//alert(requestText.responseText);
		//alert(service.getBackData(requestXML));
		    
		if (error)
		return;
        $("#"+m+" #"+m+"_output").empty().append("Response<br/><pre><textArea id=outputtext rows=13 cols=55>"+requestText.responseText+"</textArea></pre>");
    }


//callws('getTaskList0', '<participantToken>LL</participantToken>');