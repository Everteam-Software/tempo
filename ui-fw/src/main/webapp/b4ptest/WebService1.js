
var WSDLS = {};

var WebService = new Class({

	url : '',
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
	},
	
	initialize: function(url,method,options)
	{		
		this.url = url;
		this.method = method;
		this.options = options;
	},
	
	request : function()
	{
		var wsdl = WSDLS[this.url];
		alert("wsdl="+wsdl);
		if(!wsdl) 
		{
			var op = {method:'GET',async: false};
			var wsdlAjax = new XHR(op).send(this.url + "?wsdl", null);			
			wsdl = wsdlAjax.transport.responseXML;
			WSDLS[this.url] = wsdl;
		}
		alert("wsdl="+wsdl);
		this.setSoap(wsdl);
	},
		
	setSoap : function(wsdl)
	{
		var paraXML = this.getParaXML(wsdl);
		alert(paraXML);
		var ns = (wsdl.documentElement.attributes["targetNamespace"] + "" == "undefined") ? wsdl.documentElement.attributes.getNamedItem("targetNamespace").nodeValue : wsdl.documentElement.attributes["targetNamespace"].value;
		var sr = 
				"<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
				"<soap:Envelope " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
				"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
				"xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
				"<soap:Body>" +
				"<" + this.method + " xmlns=\"" + ns + "\">" +
					paraXML  +
				"</" + this.method + "></soap:Body></soap:Envelope>";
		
		this.options.method = 'post';
		this.options.data = null;
		
		var soapaction = ((ns.lastIndexOf("/") != ns.length - 1) ? ns + "/" : ns) + this.method;

		var soapAjax = new Ajax(this.url,this.options);
		soapAjax.setHeader("SOAPAction", soapaction);
		soapAjax.setHeader("Content-type", "text/xml; charset=utf-8");
		soapAjax.request(sr); 
	},
	getParaXML : function(wsdl)
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
		
		for(var i = 0; i < ell.length; i++)
		{
			var paraName = this.getElementAttrValue(ell[i],"name");
			rtnValue = rtnValue + this.getSingleXML(paraName,hash);
		}
		
		return rtnValue;
	},
	
	getSingleXML : function (name,hash)
	{
		name = name.trim();
		
		var rtnValue = "";
		if(hash.hasKey(name))
		{
			rtnValue = hash.get(name);
		}
		rtnValue = "<" + name + ">" + xmlscc(rtnValue) + "</" + name + ">"
		return rtnValue;
	},
	getBackData: function(xml)
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
		
	},
	getElementsByTagName : function(objNode,tagName)
	{
		//tagName 形式如 xsd:element ,写出tag的全称

		var ell;
		if(this.isIE())
		{
			ell = objNode.getElementsByTagName(tagName);	
		}
		else
		{
			if(tagName.contains(":")) tagName = tagName.split(":")[1];
			ell = objNode.getElementsByTagName(tagName);	     
		}
		return ell;
	},
	getElementAttrValue : function(objNode,attrName)
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
	},
	isIE : function()
	{
		var isMSIE = /*@cc_on!@*/false;
		return isMSIE;
	}
});

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