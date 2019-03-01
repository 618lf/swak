/**
 *  滚动加载 -- 执行回调函数
 */
jQuery(function($) {
	
	var lastScrollTop = 0;
	var scrollDir = "down";
	var content = [];
	var contentTop = [];
	var callbacks = [];
	var contentIndex = 0;
	
	$(window).scroll(function(event){
		var st = $(this).scrollTop();
		if (st > lastScrollTop){
		  scrollDir = 'down';
		} else {
		  scrollDir = 'up';
		}
		lastScrollTop = st;
	});
	
	//监听滚动事件
	$(document).on('scroll', function() {
		var varscroll = parseInt($(document).scrollTop()) + parseInt($(window).height());
		for(var i=0; i < contentIndex; i++) {
			contentTop[i] = $(content[i]).offset().top;
			if(scrollDir == 'down' && varscroll > contentTop[i] + 50) {
				var fnc = callbacks[i];
				if(typeof fnc === 'function') {
					fnc();
					callbacks[i] = null;
				}
			}
		}
	});
	
	//滚动加载
	$.fn.scrollLoad = function( callback ) {
		content[contentIndex] = $(this);
		callbacks[contentIndex++] = callback;
	};
});