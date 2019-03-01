/**
 * 简单选择
 * 
 * @param $
 */
(function($) {
	
	var _iSelectEvent = function(e) {
		var target  = e.target || e.srcElement;
		$('.iSelect').each(function(){
			var menu = $(this);
			if($(target).closest(menu).length == 0){
				 menu.removeClass('iSelect-container-open');
			};
		});
	};
	//定义 TreeSelect
	var iSelect = function($element, options) {
		if ( $element.data('iSelect') != null ) {
		     return $element.data('iSelect');
		}
		this.$element = $element;
		this.id = this._generateId($element);
		this.itemsId = this.id + '-items';
		options = options || {};
		this.options = $.extend({}, options, true);
		var $container = this.render();
		
		this._placeContainer($container);
		this._registerDomEvents();
		this._registerEvents();
		
		//设置id
	    this.$container.attr('id', this.id);
	    this.$container.find('.iSelect-dropdown-items').attr('id',this.itemsId);
	    
		// Hide the original select
	    $element.addClass('iSelect-offscreen');
		$element.attr('aria-hidden', 'true');
		
		$element.data('iSelect', this);
		
		this.options.hasLoad = false;
	};
	
	iSelect.prototype._generateId = function($element) {
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
	iSelect.prototype._placeContainer = function($container) {
		$container.insertAfter(this.$element);
		var width = this._resolveWidth(this.$element, this.options['width']);
	    if (width != null) {
	      $container.css('width', width);
	    }
	};
	iSelect.prototype._resolveWidth = function ($element, method) {
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
	iSelect.prototype._registerDomEvents = function(){
		var self = this;
		self.$container.on('click.tmt.iSelect', function(e){
			var _obj = e.target;
			if(!!$(_obj).closest('.iSelect-add').get(0)) {
				self.trigger('add');
			}else if(!$(_obj).closest('.iSelect-dropdown-wrapper').get(0)) {
				var fnc = $(_obj).hasClass('iSelect-chosen-close')?'remove':( $(_obj).closest('.iSelect-container').hasClass('iSelect-container-open')?'close':'open');
				self.trigger(fnc);
			}else{
				var _value = $(_obj).data('value');
				var _name =  $(_obj).text();
				if(self.$element.get(0).nodeName == 'SELECT') {
				   self.$element.val(_value);
				} else if(self.$element.get(0).nodeName == 'INPUT') {
				   self.$element.val(_name);
				   $('#' + self.$element.data('tag')).val(_value);
				}
				self.$container.find('.iSelect-chosen').text(_name);
				self.$items.find('li').each(function(){
					$(this).removeClass('active');
				});
				$(_obj).parent().addClass('active');
				self.trigger('close');
			}
		});
		$(document).off('click.tmt.iSelect').on('click.tmt.iSelect', _iSelectEvent);
	};
	iSelect.prototype._registerEvents = function() {
		var self = this;
		self.on('open', function () {
	      if((self.$container.find('.iSelect-dropdown-wrapper').offset().top - $(document).scrollTop()) > $(window).height()) {
	    	  self.$container.attr('dir','up');
	      } else {
	    	  self.$container.attr('dir','down');
	      }
	      self.$container.addClass('iSelect-container-open');
	      var url = self.options.loadUrl;
	      if(!!url && !self.options.hasLoad) {
	    	 self.reload();
	    	 self.options.hasLoad = true;
	      }
	    });
	    this.on('close', function () {
	       self.$container.removeClass('iSelect-container-open');
	    });
	    this.on('remove', function () {
	       var _chosen = self.$container.find('.iSelect-chosen');
    	   if( !_chosen.hasClass('iSelect-default') ) {
    		   _chosen.addClass('iSelect-default')
    	   }
	       self.$container.find('.iSelect-chosen').addClass('iSelect-default').text('请选择...');
    	   self.$element.val('');
		});
	    this.on('add', function () {
	       var fnc = self.options.addFnc;
	       if( !!fnc && typeof(fnc) === 'function') {
	    	   fnc(self);
	       }
	    });
	};
	iSelect.prototype.on = function(name, fnc) {
		this.attrs = this.attrs || {};
		this.attrs[name] = fnc;
	};
	iSelect.prototype.trigger = function(name, args) {
		var fnc = this.attrs[name];
		fnc.call(args);
	};
	iSelect.prototype.render = function () {
		var $container = $(
	       '<span class="iSelect iSelect-container">' +
	         '<span class="iSelect-choice">' +
	           '<span class="iSelect-chosen iSelect-default">请选择...</span>'+
	           '<span class="iSelect-arrow"><b></b></span>'+
	         '</span>'+
	         '<span class="iSelect-dropdown-wrapper" aria-hidden="true"><span class="iSelect-dropdown"><ul class="nav iSelect-dropdown-items"></ul></span></span>' +
	       '</span>'
	    );
		$container.attr('dir', "ltr");
		
		var self = this;
		var _select = "";
		var _items = (function(){
			var _li = [];
			self.$element.children('option').each(function(index, item){
				var _value = $(item).val()||'';
				var _name = $(item).text()||'请选择...';
				if($(item).attr('selected') == 'selected') {
					_li.push('<li class="active"><a data-value='+_value+'>'+_name+'</a></li>')
					_select = _name;
				} else {
					_li.push('<li><a data-value='+_value+'>'+_name+'</a></li>')
				}
			});
			return _li.join('');
		})();
		if(self.$element.get(0).nodeName == 'INPUT') {
		   _select = self.$element.val();
		}
		var $items = $container.find('.iSelect-dropdown-items').html(_items);
		this.$container = $container;
		this.$items = $items;
		$container.data('element', this.$element);
		if(!!self.options.addFnc) {
		   $container.find('.iSelect-dropdown').addClass('iSelect-add-wrap').append('<div class="iSelect-add"><i class="fa fa-plus"></i>添加</div>');
		}
		$container.find('.iSelect-chosen').text(_select||'请选择...');
		return $container;
	};
	//销毁 -- 需要时在提供
	iSelect.prototype.destory = function() {
		
	};
	//取消选择项
	iSelect.prototype.clear = function() {
    	this.trigger('remove');
	};
	//加载数据
	iSelect.prototype.reload = function() {
		var self = this;
		var url = self.options.loadUrl;
		if( !!url ) {
			self.$container.find('.iSelect-dropdown-items').html('');
			var _li = [];
			Public.postAjax(url, null, function(data) {
				$.each(data, function(index, item) {
					_li.push('<li><a data-value='+item.id+'>'+(item.code + '-' + item.name)+'</a></li>')
				});
				self.$container.find('.iSelect-dropdown-items').html(_li.join(''));
			});
		}
	};
	//jquery 支持
	$.fn.iSelect = function (options) {
		options = options || {};
		if ( typeof options === 'object') {
	        this.each(function () {
	          var instanceOptions = $.extend({}, options, true);
	          var instance = new iSelect($(this), instanceOptions);
	        });
	        return this;
	     } else if ( typeof options === 'string') {
	        var instance = this.data('treeselect');
	        if (instance == null && window.console && console.error) {
	            console.error( 'The iSelect(\'' + options + '\') method was called on an ' + 'element that is not using Select2.');
	        }
	        var args = Array.prototype.slice.call(arguments, 1);
	        var ret = instance[options](args);
	        return ret;
	     } else {
	        throw new Error('Invalid arguments for Select2: ' + options);
	     }
	};
	
})(jQuery);