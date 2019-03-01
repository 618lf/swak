(function(jQuery){
	
	$.fn.news = function(options) {
		var defaults={
			autoplay		:true,
			timer			:3000,
			effect			:'slide'	//fade or slide	 
		};
		
		var settings = $.extend(defaults,options);
		
		return this.each(function(){
			
			var activenewsid = 1;
			
			var _autoPlay = function(pos) {
				if ( pos == "next" ) {
					if ( $(settings.$element).find('ul li').length > activenewsid )
						activenewsid++;
					else
						activenewsid=1;
				} else {
					if (activenewsid-2==-1)
						activenewsid=$(settings.$element).find('ul li').length;
					else
						activenewsid=activenewsid-1;						
				}
				
				if (settings.effect=='fade'){
					$(settings.$element).find('ul li').css({'display':'none'});
					$(settings.$element).find('ul li').eq( parseInt(activenewsid-1) ).fadeIn();
				}else{
					$(settings.$element).find('ul').animate({'marginTop':-($(settings.$element).find('ul li').height())*( activenewsid - 1 )});
				}
			};
			
			var _generateId = function($element) {
				var id = '';
			    if ($element.attr('id') != null) {
			      id = $element.attr('id');
			    } else if ($element.attr('name') != null) {
			      id = $element.attr('name') + '-' + Public.generateChars(2);
			    } else {
			      id = Public.generateChars(4);
			    }
			    id = 'news-' + id;
			    return id;
			};
			
			var id = _generateId($(this));
			var timername = id + "_timer";
			$(this).attr('id', id);
			
			settings.$element = $(this);
			
			if (settings.effect == 'slide') {
			  $(settings.$element).find('ul li').css({'display':'block'});
			} else {
			  $(settings.$element).find('ul li').css({'display':'none'});
			}
			
			$(settings.$element).find('ul li').eq(parseInt(activenewsid-1)).css({'display':'block'});
			
			timername = setInterval(function(){_autoPlay('next')}, settings.timer);					
			$(settings.$element).hover(function(){
			   clearInterval(timername);
			},function(){
			   timername = setInterval(function(){_autoPlay('next')},settings.timer);
			});
		});
	};
	$.fn.hscrnews = function(options) {
		var defaults={
			parentDivId		:'scrlContainer',
			divId			:'scrlContent',
			scrlSpeed		:1	
		};
		
		var settings = $.extend(defaults,options);
		var scrlSpeed=(document.all)? settings.scrlSpeed : Math.max(1, settings.scrlSpeed-1)  ;
		var speed=scrlSpeed;
	  	if ($("#"+settings.parentDivId) != null){  
	        var contObj=$("#"+settings.parentDivId);  
	        var obj=$("#"+settings.divId);  
	        widthContainer = contObj.outerWidth();  
	        obj.css('left',parseInt(widthContainer)+"px");  
	        contObj.mouseover(function(){  
	        	speed=0;  
	        });  
	        contObj.mouseout(function(){  
	        	speed=scrlSpeed;  
	        });  
	        
	        var contObj1=document.getElementById(settings.parentDivId); 
            var obj1=document.getElementById(settings.divId); 
            interval=setInterval( function(){
                widthObject=obj.width(); 
              if (parseInt(obj1.style.left)>(widthObject*(-1))){ 
                obj1.style.left=parseInt(obj1.style.left)-speed+ "px"; 
              } else { 
                obj1.style.left=parseInt(widthContainer)+ "px"; 
              } 
            },20); 

	    }  
			
	};
})(jQuery)