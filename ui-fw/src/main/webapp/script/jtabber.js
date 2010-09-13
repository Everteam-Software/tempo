/***************************************/
// jQuery Tabber
// By Jordan Boesch
// www.boedesign.com
// Dec 25, 2007 (Merry Christmas!)
/***************************************/

(function(jQuery){
		  
	$.extend($, {
		jtabber: function(params){
				
				// parameters
				var navDiv = params.mainLinkTag;
				var selectedClass = params.activeLinkClass;
				var hiddenContentDiv = params.hiddenContentClass;
				var showDefaultTab = params.showDefaultTab;
				var showErrors = params.showErrors;
				var effect = params.effect;
				var effectSpeed = params.effectSpeed;
				
				// If error checking is enabled
				if(showErrors){
					if(!$(navDiv).attr('tabtitle')){
						alert("ERROR: The elements in your mainLinkTag paramater need a 'tabtitle' attribute.\n ("+navDiv+")");	
						return false;
					}
					else if(!$("."+hiddenContentDiv).attr('id')){
						alert("ERROR: The elements in your hiddenContentClass paramater need to have an id.\n (."+hiddenContentDiv+")");	
						return false;
					}
				}
				
				// If we want to show the first block of content when the page loads
				if(!isNaN(showDefaultTab)){
					showDefaultTab--;
					$("."+hiddenContentDiv+":eq("+showDefaultTab+")").css('display','block');
					$(navDiv+":eq("+showDefaultTab+")").addClass(selectedClass);	
				}
				
				// each anchor
				$(navDiv).each(function(){
										
					$(this).click(function(){
						// once clicked, remove all classes
						$(navDiv).each(function(){
							$(this).removeClass();
						})
						// hide all content
						$("."+hiddenContentDiv).css('display','none');
						
						// now lets show the desired information
						$(this).addClass(selectedClass);
						var contentDivId = $(this).attr('tabtitle');
						
						if(effect != null){
							
							switch(effect){
								
								case 'slide':
								$("#"+contentDivId).slideDown(effectSpeed);
								break;
								case 'fade':
								$("#"+contentDivId).fadeIn(effectSpeed);
								break;
								
							}
								
						}
						else {
							$("#"+contentDivId).css('display','block');
						}
						return false;
					})
				})
			
			}
	})
	
})(jQuery);