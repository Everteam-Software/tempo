/**
 * jCorners - Cross-Browser Corners with JQuery
 *   http://jcorners.culturezoo.com/
 *
 * Copyright (c) 2008 Levi Nunnink, Culturezoo, LLC (http://culturezoo.com)
 * Licensed under the GPL
 *
 * Built on top of the jQuery library
 *   http://jquery.com
 *
 * Inspired by the "Corners Experiment" by Jonathan Snook
 *   http://snook.ca/archives/html_and_css/rounded_corners_experiment_ie/
 */

var defaults = {
	radius: 5
};
jQuery.fn.jcorners = function(o) {
	return this.each(function() {
		new $jc(this, o);
	});
};
jQuery.jcorners = function(e, o) {
	this.options = $.extend({}, defaults, o || {});
	if(this.browser.msie){
		this("body").prepend("<xml:namespace ns='urn:schemas-microsoft-com:vml' prefix='v' />");
		
		if(this.options.bgcolor != undefined){
			var bg = this.options.bgcolor;
		}else{
			if($(e).css("background-color") != undefined){
				var bg = $(e).css("background-color");
			}else{
				var bg = "white";
			}
		}
		var guid = this.guid();

		var padding = this.intval(this(e).css("padding-right"));
		
		var arc = (this.options.radius / this(e).height());
		
		this(e).wrap("<v:roundrect arcsize='"+arc+"' class='corner-wrapper' id='wrapper-"+guid+"' fillcolor='"+bg+"' strokecolor='"+bg+"'></v:roundrect>");
		this("#wrapper-"+guid+"").css("behavior","url(#default#VML)");
		this("#wrapper-"+guid+"").css("background-color","transparent");
		this("#wrapper-"+guid+"").css("padding",this.options.radius+"px");
		this("#wrapper-"+guid+"").css("height","100%");
		this("#wrapper-"+guid+"").css("width",this(e).width()+(padding*2)+"px");
		this("#wrapper-"+guid+"").css("border-color:",bg);
		this("#wrapper-"+guid+"").css("display","inline-block");

	}else if(this.browser.mozilla){
		this(e).css("-moz-border-radius",this.options.radius+"px");
	}else if(this.browser.safari){
		this(e).css("-webkit-border-radius",this.options.radius+"px");
	}else{
		this(e).css("border-radius",this.options.radius+"px");
	}
	return this;
}
jQuery.extend({
	intval: function(v) {
        v = parseInt(v);
        return isNaN(v) ? 0 : v;
    },
    guid: function(){
		var result, i, j;
		result = '';
		for(j=0; j<32; j++)
		{
			if( j == 8 || j == 12|| j == 16|| j == 20)
			result = result + '-';
			i = Math.floor(Math.random()*16).toString(16).toUpperCase();
			result = result + i;
		}
		return result
	} 
});
