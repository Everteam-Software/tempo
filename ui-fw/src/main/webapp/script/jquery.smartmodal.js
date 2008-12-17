jQuery.fn.extend({
  modal: function(params){
    var jQ = jQuery;
		var params=params;
    return this.each(function(){
      jQ(this).click(function(){
        var trig = $(this);
        ie6=jQ.browser.msie&&(jQ.browser.version == "6.0")
				if(!jQ('#modal_overlay').length){ //only add the div if its not there already
        	jQ("body").append('<div id="modal_overlay"></div>');
				}
        jQ("#modal_overlay").css({height:'100%',width:'100%',position:'fixed',left:0,top:0,'z-index':1000,opacity:50/100});
				if(!jQ('#modal_content').length){ //only add the div if its not there already
        	jQ("body").append('<div id="modal_content"></div>');
				}
				c = "<div class='modal_close'><p>x</p></div>";
				if(trig.attr("rel")){ //if rel exists
					div_id = $('#'+trig.attr('rel'));
					div_class = $('.'+trig.attr('rel'));	
					if(div_id.length){
						c = c + div_id.html();
					}else if(div_class.length){
						c = c + div_class.html();
					}
				}else if(trig.attr('href')){ //if it has a href but no rel then insert the href as image src
					if(trig.attr('title')){ //if it has a title tag, use it
						title = trig.attr('title');
						c = c +"<h3 class='modal_title'>"+title+"</h3><img src='"+trig.attr('href')+"' alt='"+title+"' />"; //make the content
					}else{
						c = c+"<img src='"+trig.attr('href')+"' alt='modal' />"; //no title tag
					}
				}else{ //if no rel or no href then use the content inside the trigger
					c = c + trig.html();
				}
				//if style info has been passed in then apply that as well as defaults
				if(params && params['modal_styles']){
					styling = params['modal_styles'];
					jQ("#modal_content").html(c).css(styling).css({display:"block", zIndex:1001});
				}
				jQ("#modal_content").html(c).css({display:"block", zIndex:1001});	
				//on load stuff			
        jQ("#modal_content").load(function() {
          o = jQ("#modal_overlay");
          w = jQ("#modal_content");
          w.css({width:$(this).css("width"), height:$(this).css("height") });
          if(ie6) {
            $('html,body').css({height:'100%',width:'100%'});
            i=$('<iframe src="javascript:false;document.write(\'\');" class="overlay"></iframe>').css({opacity:0});
            o.html('<p style="width:100%;height:100%"/>').prepend(i)
            o = o.css({position:'absolute'})[0];
            for(var y in {Top:1,Left:1}) o.style.setExpression(y.toLowerCase(),"(_=(document.documentElement.scroll"+y+" || document.body.scroll"+y+"))+'px'");
          }					
        });
				//if show param is been passed then eval it
				if(params && params['show']){
					eval(params['show']);
				}	
				//close modals			
        jQ("#modal_overlay, .modal_close").click(function(){
          jQ("#modal_content").remove();
					jQ("#modal_overlay").remove();
					if(params && params['hide']){ //hide param
						eval(params['hide']);
					}
        });
        return false;
      });
    });
  }
});

