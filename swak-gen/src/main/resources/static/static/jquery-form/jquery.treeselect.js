/**
 * 树形选择
 * 
 * @param $
 */
(function($) {
	
	var _treeSelectEvent = function(e) {
		var target  = e.target || e.srcElement;
		$('.treeselect').each(function(){
			var menu = $(this);
			if($(target).closest(menu).length == 0){
				 menu.removeClass('treeselect-container-open');
			};
		});
	};
	//定义 TreeSelect
	var TreeSelect = function($element, options) {
		if ( $element.data('treeselect') != null ) {
		     return $element.data('treeselect');
		}
		this.$element = $element;
		this.id = this._generateId($element);
		this.ztreeId = this.id + '-ztree';
		options = options || {};
		this.options = $.extend({}, options, true);
		var $container = this.render();
		
		this._placeContainer($container);
		this._registerDomEvents();
		this._registerEvents();
		
		//设置id
	    this.$container.attr('id', this.id);
	    this.$container.find('.ztree').attr('id',this.ztreeId);
	    
		// Hide the original select
	    $element.addClass('treeselect-offscreen');
		$element.attr('aria-hidden', 'true');
		
		$element.data('treeselect', this);
	};
	
	TreeSelect.prototype._generateId = function($element) {
		var id = '';
	    if ($element.attr('id') != null) {
	      id = $element.attr('id');
	    } else if ($element.attr('name') != null) {
	      id = $element.attr('name') + '-' + Public.generateChars(2);
	    } else {
	      id = Public.generateChars(4);
	    }
	    id = 'treeselect-' + id;
	    return id;
	};
	TreeSelect.prototype._placeContainer = function($container) {
		$container.insertAfter(this.$element);
		var width = this._resolveWidth(this.$element, this.options['width']);
	    if (width != null) {
	      $container.css('width', width);
	    }
	};
	TreeSelect.prototype._resolveWidth = function ($element, method) {
		var WIDTH = /^width:(([-+]?([0-9]*\.)?[0-9]+)(px|em|ex|%|in|cm|mm|pt|pc))/i;
		if (method == 'resolve') {
	      var styleWidth = this._resolveWidth($element, 'style');

	      if (styleWidth != null) {
	        return styleWidth;
	      }

	      return this._resolveWidth($element, 'element');
	    }
		if (method == 'element') {
	      var elementWidth = $element.outerWidth(false);

	      if (elementWidth <= 0) {
	        return 'auto';
	      }

	      return elementWidth + 'px';
	    }
		if (method == 'style') {
	      var style = $element.attr('style');

	      if (typeof(style) !== 'string') {
	        return null;
	      }

	      var attrs = style.split(';');

	      for (var i = 0, l = attrs.length; i < l; i = i + 1) {
	        var attr = attrs[i].replace(/\s/g, '');
	        var matches = attr.match(WIDTH);

	        if (matches !== null && matches.length >= 1) {
	          return matches[1];
	        }
	      }

	      return null;
	    }
		return method;
	};
	TreeSelect.prototype._registerDomEvents = function(){
		var self = this;
		self.$container.on('click.tmt.treeselect', function(e){
			var _obj = e.target;
			if(!$(_obj).closest('.treeselect-dropdown-wrapper').get(0)) {
				var fnc = $(_obj).hasClass('treeselect-chosen-close')?'remove':( $(_obj).closest('.treeselect-container').hasClass('treeselect-container-open')?'close':'open');
				self.trigger(fnc);
			}
		});
		$(document).off('click.tmt.treeselect').on('click.tmt.treeselect', _treeSelectEvent);
	};
	TreeSelect.prototype._registerEvents = function() {
		var self = this;
		self.on('open', function () {
	      if((self.$container.find('.treeselect-dropdown-wrapper').offset().top - $(document).scrollTop() + 250) > $(window).height()) {
	    	  self.$container.attr('dir','up');
	      } else {
	    	  self.$container.attr('dir','down');
	      }
	      self.$container.addClass('treeselect-container-open');
	      if(!self.zTree) {
	    	  self.zTree = Public.tree({
		    	    ztreeDomId: self.ztreeId,
			        remoteUrl:(self.options.remoteUrl),
			        callback:{onClick:function(event, treeId, treeNode){
			        	if(treeNode.id && !treeNode.isParent && treeNode.selectAbled) {
			        		var _chosen = self.$container.find('.treeselect-chosen');
			        		if(_chosen.hasClass('treeselect-default')) {
			        		   _chosen.removeClass('treeselect-default')
			        		}
			        		_chosen.text(treeNode.name);
			        		self.$element.val(treeNode.id);
			        		self.$element.data('name', treeNode.name);
			        		self.trigger('close');
			        	}
			        }}
			  });
	      }
	    });
	    this.on('close', function () {
	       self.$container.removeClass('treeselect-container-open');
	    });
	    this.on('remove', function () {
	       var _chosen = self.$container.find('.treeselect-chosen');
    	   if( !_chosen.hasClass('treeselect-default') ) {
    		   _chosen.addClass('treeselect-default')
    	   }
	       self.$container.find('.treeselect-chosen').addClass('treeselect-default').text('请选择...');
    	   self.$element.val('');
    	   self.$element.data('name', '');
		});
	};
	TreeSelect.prototype.on = function(name, fnc) {
		this.attrs = this.attrs || {};
		this.attrs[name] = fnc;
	};
	TreeSelect.prototype.trigger = function(name, args) {
		var fnc = this.attrs[name];
		fnc.call(args);
	};
	TreeSelect.prototype.render = function () {
		var $container = $(
	       '<span class="treeselect treeselect-container">' +
	         '<span class="treeselect-choice">' +
	           '<span class="treeselect-chosen treeselect-default">请选择...</span>'+
	           '<abbr class="treeselect-chosen-close"></abbr>'+
	           '<span class="treeselect-arrow"><b></b></span>'+
	         '</span>'+
	         '<span class="treeselect-dropdown-wrapper" aria-hidden="true"><span class="treeselect-dropdown"><ul class="ztree"><li class="treeselect-result-loading">数据加载中...</li></ul></span></span>' +
	       '</span>'
	    );
		$container.attr('dir', "ltr");
		
		this.$container = $container;
		
		$container.data('element', this.$element);
		
		var _name = this.$element.data('name');
		if( _name !== undefined && _name !== null) {
			var _chosen = this.$container.find('.treeselect-chosen');
			if(_chosen.hasClass('treeselect-default')) {
			   _chosen.removeClass('treeselect-default')
			}
			_chosen.text(_name); 
		}
		return $container;
	};
	//销毁 -- 需要时在提供
	TreeSelect.prototype.destory = function() {
		
	};
    TreeSelect.prototype.clear = function() {
    	this.trigger('remove');
	};
	//jquery 支持
	$.fn.treeSelect = function (options) {
		options = options || {};
		if ( typeof options === 'object') {
	        this.each(function () {
	          var instanceOptions = $.extend({}, options, true);
	          var instance = new TreeSelect($(this), instanceOptions);
	        });
	        return this;
	     } else if ( typeof options === 'string') {
	        var instance = this.data('treeselect');
	        if (instance == null && window.console && console.error) {
	          console.error( 'The select2(\'' + options + '\') method was called on an ' + 'element that is not using Select2.');
	        }
	        var args = Array.prototype.slice.call(arguments, 1);
	        var ret = instance[options](args);
	        return ret;
	     } else {
	        throw new Error('Invalid arguments for Select2: ' + options);
	     }
	};
	
})(jQuery);