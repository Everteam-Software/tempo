// JavaScript Document
//Singleton SOAP Client
var SOAPClient = {
	Proxy: "",
	SOAPServer: "",
	ContentType: "text/xml",
	CharSet: "utf-8",
	ResponseXML: null,
	ResponseText: "",
	Status: 0,
	ContentLength: 0,
	Namespace: function(name, uri) {
		return {"name":name, "uri":uri};
	},
	SendRequest: function(soapReq, callback) {		
		if(SOAPClient.Proxy != null) {
			SOAPClient.ResponseText = "";
			SOAPClient.ResponseXML = null;
			SOAPClient.Status = 0;
			
			var content = soapReq.toString();
			SOAPClient.ContentLength = content.length;
			
			function getResponse(xData) {
				if(callback != null) {
					SOAPClient.Status = xData.status;
					SOAPClient.ResponseText = xData.responseText;
					SOAPClient.ResponseXML = xData.responseXML;
					var jsOut = $.xmlToJSON(xData.responseXML);
					callback(jsOut);
				}
			}
			$.ajax({
				 type: "POST",
				 url: SOAPClient.Proxy,
				 dataType: "xml",
				 processData: false,
				 data: content,
				 complete: getResponse,
				 beforeSend: function(req) {
					req.setRequestHeader("Method", "POST");
				 	req.setRequestHeader("Content-Length", SOAPClient.ContentLength);
					req.setRequestHeader("Content-Type", SOAPClient.ContentType + "; charset=\"" + SOAPClient.CharSet + "\"");
					req.setRequestHeader("SOAPServer", SOAPClient.SOAPServer);
					req.setRequestHeader("SOAPAction", soapReq.Action);
				 }
			});
		}
	},	
	ToXML: function(soapObj) {
		var out = "";
		var isNSObj=false;
		try {
			if(soapObj!=null&&typeof(soapObj)=="object"&&soapObj.typeOf=="SOAPObject") {								
				//Namespaces
				if(soapObj.ns!=null) {
					if(typeof(soapObj.ns)=="object") {
						isNSObj=true;
						out+="<"+soapObj.ns.name+":"+soapObj.name;
						out+=" xmlns:"+soapObj.ns.name+"=\""+soapObj.ns.uri+"\"";
					} else  {
						out+="<"+soapObj.name;
						out+=" xmlns=\""+soapObj.ns+"\"";
					}
				} else {
					out+="<"+soapObj.name;
				}
				//Node Attributes
				if(soapObj.attributes.length > 0) {
					 var cAttr;
					 var aLen=soapObj.attributes.length-1;
					 do {
						 cAttr=soapObj.attributes[aLen];
						 if(isNSObj) {
						 	out+=" "+soapObj.ns.name+":"+cAttr.name+"=\""+cAttr.value+"\"";
						 } else {
							out+=" "+cAttr.name+"=\""+cAttr.value+"\"";
						 }
					 } while(aLen--);					 					 
				}
				out+=">";
				//Node children
				if(soapObj.hasChildren()) {					
					var cPos, cObj;
					for(cPos in soapObj.children){
						cObj = soapObj.children[cPos];
						if(typeof cObj == "object"){out+=SOAPClient.ToXML(cObj);}
					}
				}
				//Node Value
				if(soapObj.value != null){out+=soapObj.value;}
				//Close Tag
				if(isNSObj){out+="</"+soapObj.ns.name+":"+soapObj.name+">";}
				else {out+="</"+soapObj.name+">";}
				return out;
			}
		} catch(e){alert("Unable to process SOAPObject! Object must be an instance of SOAPObject");}
	}
}
//Soap request - this is what being sent using SOAPClient.SendRequest
var SOAPRequest=function(action, soapObj) {
	this.Action=action;	
	var nss=[];
	var headers=[];
	var bodies=(soapObj!=null)?[soapObj]:[];
	this.addNamespace=function(ns, uri){nss.push(new SOAPClient.Namespace(ns, uri));}	
	this.addHeader=function(soapObj){headers.push(soapObj);};
	this.addBody=function(soapObj){bodies.push(soapObj);}
	this.toString=function() {
		var soapEnv = new SOAPObject("soapenv:Envelope");
			soapEnv.attr("xmlns:soapenv","http://schemas.xmlsoap.org/soap/envelope/");
		//Add Namespace(s)
		if(nss.length>0){
			var tNs, tNo;
			for(tNs in nss){tNo=nss[tNs];if(typeof(tNo)=="object"){soapEnv.attr("xmlns:"+tNo.name, tNo.uri)}}
		}
		//Add Header(s)
		if(headers.length>0) {
			var soapHeader = soapEnv.appendChild(new SOAPObject("soapenv:Header"));
			var tHdr;
			for(tHdr in headers){soapHeader.appendChild(headers[tHdr]);}
		}
		//Add Body(s)
		if(bodies.length>0) {
			var soapBody = soapEnv.appendChild(new SOAPObject("soapenv:Body"));
			var tBdy;
			for(tBdy in bodies){soapBody.appendChild(bodies[tBdy]);}
		}
		return soapEnv.toString();		
	}
}

//Soap Object - Used to build body envelope and other structures
var SOAPObject = function(name) {
	this.typeOf="SOAPObject";
	this.ns=null;
	this.name=name;
	this.attributes=[];
	this.children=[]
	this.value=null;
	this.attr=function(name, value){this.attributes.push({"name":name, "value":value});return this;};
	this.appendChild=function(obj){this.children.push(obj);return obj;};
	this.hasChildren=function(){return (this.children.length > 0)?true:false;};
	this.val=function(v){if(v==null){return this.value}else{this.value=v;return this;}};	
	this.toString=function(){return SOAPClient.ToXML(this);}		
}