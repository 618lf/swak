/**
 * 文件选择
 * 
 * @param $
 */
(function($) {

   var iAttachment = function($element, options) {
	   if($element.data('iAttachment') != null ) {
		  return $element.data('iAttachment');
	   }
	   this.$element = $element;
	   this.id = this._generateId($element);
	   options = options || {};
	   this.options = $.extend({}, options, true);
	   var $container = this.render();
	   this._placeContainer($container);
	   this._registerDomEvents();
	   this._registerEvents();
	   
	   this.$container.attr('id', this.id);
	   $element.addClass('iAttachment-offscreen');
	   $element.attr('aria-hidden', 'true');
	   $element.data('iAttachment', this);
   };
   iAttachment.prototype._generateId = function($element) {
		var id = '';
	    if ($element.attr('id') != null) {
	      id = $element.attr('id');
	    } else if ($element.attr('name') != null) {
	      id = $element.attr('name') + '-' + Public.generateChars(2);
	    } else {
	      id = Public.generateChars(4);
	    }
	    return 'iAttachment-' + id;;
   };
   iAttachment.prototype._placeContainer = function($container) {
	   $container.insertAfter(this.$element);
   };
   iAttachment.prototype._registerDomEvents = function(){
	   var self = this;
	   self.$container.on('click.tmt.iAttachment', function(e){
		   var _obj = $(e.target);
		   if(_obj.hasClass('iAttachment-OpenBtn')) {
			   self.trigger('open');
		   } else if(_obj.hasClass('iAttachment-DelBtn')) {
			   self.trigger('clear');
		   }
	   });
   };
   iAttachment.prototype._registerEvents = function() {
	   var self = this;
	   self.on('open', function () {
		   //打开文件选择
		   Attachment.selectAttachments(function(files) {
			    if( !!files && files.length != 0 ) {
					var urls = [];
				    for(var i =0;i < files.length; i++) {
				    	urls.push(files[i].src);
				    }
				    self.val(files[0].src);
				}
				return true;
		   });
	   });
	   self.on('clear', function () {
		   self.val('');
	   });
   };
   iAttachment.prototype.on = function(name, fnc) {
	   this.attrs = this.attrs || {};
       this.attrs[name] = fnc;
   };
   iAttachment.prototype.trigger = function(name, args) {
       var fnc = this.attrs[name];
       fnc.call(args);
   };
   iAttachment.prototype.render = function () {
	   var $container = $(
	       '<div class="iAttachment-PreviewWarp">' +
		   '<ol class="iAttachment-Preview"></ol>' +
		   '<div class="iAttachment-ops"><a href="javascript:void(0);" class="iAttachment-OpenBtn btn">选择</a>&nbsp;<a href="javascript:void(0);" class="iAttachment-DelBtn btn">清除</a></div>' +
		   '</div>');
	   this.$container = $container;
	   $container.data('element', this.$element);
	   this.val(this.$element.val());
	   return $container;
   };
   iAttachment.prototype.val = function(val) {
	   var self = this;
	   self.$element.val(val);
	   self.$container.find('.iAttachment-Preview').children().remove();
	   var li, urls = self.$element.val().split("|");
	   for (var i=0; i<urls.length; i++){
			if (urls[i]!=""){
				li = "<li><img src=\""+urls[i]+"\" url=\""+urls[i]+"\" style=\"max-width:220px;max-height:220px;_height:200px;border:0;padding-bottom:3px;\">";
				li += "&nbsp;&nbsp;<a href=\"javascript:\" class=\"imageDel\">×</a></li>";
			}
	   }
	   if ( li == null){
		    li = "<li style='list-style:none;padding-top:5px;'>无</li>";
	   }
	   self.$container.find('.iAttachment-Preview').html(li);
	   if( typeof(self.options.ok) === 'function') {
	       self.options.ok(val);
	   }
   };
   
   //jquery 支持
   $.fn.iAttachment = function (options) {
		options = options || {};
		if ( typeof options === 'object') {
	        this.each(function () {
	          var instanceOptions = $.extend({}, options, true);
	          var instance = new iAttachment($(this), instanceOptions);
	        });
	        return this;
	     } else if ( typeof options === 'string') {
	        var instance = this.data('iAttachment');
	        if (instance == null && window.console && console.error) {
	            console.error( 'The iAttachment(\'' + options + '\') method was called on an ' + 'element that is not using iAttachment.');
	        }
	        var args = Array.prototype.slice.call(arguments, 1);
	        var ret = instance[options](args);
	        return ret;
	     } else {
	        throw new Error('Invalid arguments for iAttachment: ' + options);
	     }
	};
})(jQuery);