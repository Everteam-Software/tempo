
var WSDLS = {};

function WebService(URL1, method1, options1){
	
	this.url = URL1;
	this.method = method1;
	this.options = options1;
	
	if(typeof WebService._initialized == "undefined"){
		WebService.prototype.initialize=function(url,method,options){
			//alert("initialize");
			this.url = url;
			this.method = method;
			this.options = options;
		};
		
		WebService.prototype.request=function()
		{

		var wsdl = WSDLS[this.url];
		//alert("wsdl="+wsdl);
		if(!wsdl) 
		{
			var op = {type:'GET',async: false,url:this.url+ "?wsdl"};
			//var wsdlAjax = new XHR(op).send(this.url + "?wsdl", null);	
			var wsdlAjax = $.ajax(op);					
			wsdl = wsdlAjax.responseXML;			
			WSDLS[this.url] = wsdl;
		}
		//alert(wsdl.responseText);
		//alert("wsdl="+wsdl.responseText+","+wsdl.statusText);
		this.setSoap(wsdl);
		//alert("setSoap leave");
		};
		
		WebService.prototype.setSoap=function(wsdl)
		{
				
		//var paraXML = this.getParaXML(wsdl);
		//alert(paraXML);
		var ns = (wsdl.documentElement.attributes["targetNamespace"] + "" == "undefined") ? wsdl.documentElement.attributes.getNamedItem("targetNamespace").nodeValue : wsdl.documentElement.attributes["targetNamespace"].value;
		var sr = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
				"<soap:Envelope " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
				"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
				"xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
				"<soap:Body>" +
				"<" + this.method + " xmlns=\"" + ns + "\">" +
					  this.options.data +
				"</" + this.method + "></soap:Body></soap:Envelope>";
		//alert("sr="+sr);
		this.options.method = 'post';
		this.options.data = null;
		
		var soapaction = ((ns.lastIndexOf("/") != ns.length - 1) ? ns + "/" : ns) + this.method;
		//alert("soapaction="+soapaction);
		//this.options.url=this.url;
		//this.options.contentType="text/xml; charset=utf-8";
		/*this.options.beforSend= function(XMLHttpRequest){
			alert("set header");
			//XMLHttpRequest.setRequestHeader("SOAPAction", soapaction);
			};*/
		
		var soapAjax = $.ajax({
		url:this.url,
		contentType:"text/xml; charset=utf-8",
		beforeSend:function(XMLHttpRequest){
			//alert("set header");
			XMLHttpRequest.setRequestHeader("SOAPAction", soapaction);
			},		
		error: this.options.onFailure,		
		complete:this.options.onComplete,
		data:sr,
		type:"POST"
		});
		//soapAjax.setHeader("SOAPAction", soapaction);
		//soapAjax.setHeader("Content-type", "text/xml; charset=utf-8");
		//soapAjax.request(sr); 
		//alert("sent");
		};
		
		WebService.prototype.getParaXML = function(wsdl)
		{
		
		var objNode = null;
		var rtnValue = "";
		//java(xfire)
		var ell = this.getElementsByTagName(wsdl,"xsd:element");	
			
		if(ell.length == 0) 
		{
			//c#
			ell = this.getElementsByTagName(wsdl,"s:element");	
		}
		for(var i = 0; i < ell.length; i++)
		{
			if(this.getElementAttrValue(ell[i],"name") == this.method)
			{
				objNode = ell[i];
				break;
			}
		}
	
		if(objNode == null) return rtnValue;
		//java(xfire)
		ell = this.getElementsByTagName(objNode,"xsd:element");	
		if(ell.length == 0) 
		{
			//c#
			ell = this.getElementsByTagName(objNode,"s:element");
		}
		if(ell.length == 0) return rtnValue ;
		
		var hash = new Hash();
		
		if(this.options.data != null && this.options.data.clean != "")
		{
			hash = this.options.data.split("&").toHash("=");
		}
			//alert("getParaXML");
		for(var i = 0; i < ell.length; i++)
		{
			var paraName = this.getElementAttrValue(ell[i],"name");
			rtnValue = rtnValue + this.getSingleXML(paraName,hash);
		}
			
		return rtnValue;
		};
	
			
		WebService.prototype.getSingleXML= function (name,hash)
		{
			name = name.trim();
			
			var rtnValue = "";
			if(hash.hasKey(name))
			{
				rtnValue = hash.get(name);
			}
			rtnValue = "<" + name + ">" + xmlscc(rtnValue) + "</" + name + ">"
			return rtnValue;
		};
		WebService.prototype.getBackData= function(xml)
		{
			var rtnValue = "";
			//java(xfire)
			var soap = this.getElementsByTagName(xml,"ns1:out");	
			if(soap.length == 0)
			{
				//c#
				soap = this.getElementsByTagName(xml,this.method + "Result");
			}
			return soap[0].childNodes[0].nodeValue;		
			
		};
		WebService.prototype.getElementsByTagName = function(objNode,tagName)
		{
			//tagName 形式如 xsd:element ,写出tag的全称
	//alert("getElementsByTagName");
			var ell;
			if(this.isIE())
			{
				ell = objNode.getElementsByTagName(tagName);	
			}
			else
			{
				if(tagName.indexOf(":")>=0) tagName = tagName.split(":")[1];
			//alert("getElementsByTagName3"+tagName+objNode);
				ell = objNode.getElementsByTagName(tagName);
			//	alert("getElementsByTagName4"+tagName);     
			}
			return ell;
		};
		WebService.prototype.getElementAttrValue = function(objNode,attrName)
		{
			var rtnValue = "";
			
			if(objNode == null) return rtnValue;
			
			if(objNode.attributes[attrName] + "" == "undefined")
			{ 
				if(objNode.attributes.getNamedItem(attrName) != null)
					rtnValue = objNode.attributes.getNamedItem(attrName).nodeValue ;
				
			}
			else
			{
				if(objNode.attributes[attrName] != null)
					rtnValue = objNode.attributes[attrName].value;
			}
			return rtnValue;
		};
		WebService.prototype.isIE = function()
		{
			var isMSIE = /*@cc_on!@*/false;
			return isMSIE;
		};
		
	};
	
	

	/*url : '',
	method : '',
	options: 
	{
		method:'GET',
		data: null,
		update: null,
		onComplete: Class.empty,
		onError:Class.empty,
		evalScripts: false,
		evalResponse: false
	},*/
	
	
	
		
	

};

Array.extend({
	
	toHash : function (splitChar)
	{
		var hash = new Hash({});
		for(var i=0;i<this.length;i++)
		{
			
			if(this[i].split(splitChar).length == 1) contrnue;

			var key = this[i].split(splitChar)[0].trim();
			var value = this[i].split(splitChar)[1].trim();
			
			hash.set(key, value);
		}
		
		return hash;
	}
});

function xmlscc(strData)
{

	strData=strData.replace(/&/g, "&amp;");
	strData=strData.replace(/>/g, "&gt;");
	strData=strData.replace(/</g, "&lt;");
	strData=strData.replace(/"/g, "&quot;");
	strData=strData.replace(/'/g, "&apos;");
	return strData;
}