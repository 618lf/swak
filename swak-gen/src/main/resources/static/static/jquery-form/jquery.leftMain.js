/**
 * 文件选择
 * 
 * @param $
 */
(function($) {
   var iLeftMain = function($element, options) {
	   if($element.data('iLeftMain') != null ) {
		  return $element.data('iLeftMain');
	   }
	   this.$element = $element;
	   options = options || {};
	   this.options = $.extend({}, options, true);
	   this._registerEvents();
	   this._registerDomEvents();
	   $element.data('iLeftMain', this);
	   return this;
   };
   iLeftMain.prototype._registerEvents = function() {
	   var self = this;
	   self.on('open', function (fnc) {
		   var _width = self.$element.width();
		   self.$element.find('.side-wrap').css({width: _width - 50, left: _width}).show();
		   self.$element.find('.side-wrap').css({height: $(window).height() - 50})
		   self.$element.find('.side-wrap').animate({left: 50});
		   self.$element.find('.main-side').animate({left: (_width - 40) * -1});
		   self.scroll = Public.newScroll(self.$element.find('.side-scroll-wrap').get(0));
	   });
	   self.on('close', function (fnc) {
		   self.$element.find('.side-wrap').hide();
		   self.$element.find('.main-side').animate({left: 0});
		   self.$element.find('.side-content').html('');
		   self.scroll.destroy();
		   self.scroll = null;
	   });
	   self.on('refresh', function (fnc) {
		   if(self.scroll != null) {
			  self.scroll.refresh();
		   }
	   });
   };
   iLeftMain.prototype._registerDomEvents = function() {
	   var self = this;
	   self.$element.on('click.tmt.iLeftMain', function(e){
		   var _obj = $(e.target);
		   if(_obj.hasClass('side-close') || !!_obj.closest('.side-close').get(0)) {
			   self.trigger('close');
		   }
	   });
   };
   iLeftMain.prototype.on = function(name, fnc) {
	   this.attrs = this.attrs || {};
       this.attrs[name] = fnc;
   };
   iLeftMain.prototype.trigger = function(name, args) {
       var fnc = this.attrs[name];
       fnc.call(args);
   };
   //jquery 支持
   $.fn.iLeftMain = function (options) {
		options = options || {};
		if ( typeof options === 'object') {
			 var instanceOptions = $.extend({}, options, true);
	         return new iLeftMain($(this), instanceOptions);
	     } else if ( typeof options === 'string') {
	        var instance = this.data('iLeftMain');
	        if (instance == null && window.console && console.error) {
	            console.error( 'The iLeftMain(\'' + options + '\') method was called on an ' + 'element that is not using iLeftMain.');
	        }
	        var args = Array.prototype.slice.call(arguments, 1);
	        return instance.trigger(options, args);
	     } else {
	        throw new Error('Invalid arguments for iLeftMain: ' + options);
	     }
	};
})(jQuery);