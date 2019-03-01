/**
 * 区域选择
 * @param $
 */
(function($) {
	
	var _areaSelectEvent = function(e) {
		var target  = e.target || e.srcElement;
		$('.areaselect').each(function(){
			var menu = $(this);
			if($(target).closest(menu).length == 0){
				 menu.removeClass('areaselect-container-open');
			};
		});
	};
	//定义 AreaSelect
	var AreaSelect = function($element, options) {
		if ( $element.data('areaselect') != null ) {
		     return $element.data('areaselect');
		}
		this.$element = $element;
		this.id = this._generateId($element);
		options = options || {};
		this.options = $.extend({}, options, true);
		this.callback = this.options.callback;
		var $container = this.render();
		this._placeContainer($container);
		this._registerDomEvents();
		this._registerEvents();
		//设置id
	    this.$container.attr('id', this.id);
	    $element.addClass('areaselect-offscreen');
		$element.data('areaselect', this);
	};
	
	AreaSelect.prototype._generateId = function($element) {
		var id = '';
	    if ($element.attr('id') != null) {
	      id = $element.attr('id');
	    } else if ($element.attr('name') != null) {
	      id = $element.attr('name') + '-' + Public.generateChars(2);
	    } else {
	      id = Public.generateChars(4);
	    }
	    id = 'areaselect-' + id;
	    return id;
	};
	AreaSelect.prototype._placeContainer = function($container) {
		$container.insertAfter(this.$element);
		var width = this._resolveWidth(this.$element, this.options['width']);
	    if (width != null) {
	      $container.css('width', width);
	    }
	};
	AreaSelect.prototype._resolveWidth = function ($element, method) {
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
	AreaSelect.prototype._registerDomEvents = function(){
		var self = this;
		self.$container.on('click.tmt.areaselect', function(e){
			var _obj = e.target;
			if(!$(_obj).closest('.areaselect-dropdown-wrapper').get(0)) {
				var fnc = $(_obj).hasClass('areaselect-chosen-close')?'remove':( $(_obj).closest('.areaselect-container').hasClass('areaselect-container-open')?'close':'open');
				self.trigger(fnc);
			}
			if($(_obj).hasClass('area')) {
			   var p = $(_obj).closest('.areaselect-panel');
			   p.find('.area').removeClass('active');
			   $(_obj).addClass('active');
			   var id = $(_obj).data('id');
			   var path = $(_obj).data('path');
			   var _chosen = self.$container.find('.areaselect-chosen');
	       	   if(_chosen.hasClass('areaselect-default')) {
	       		   _chosen.removeClass('areaselect-default')
	       	   }
	       	   _chosen.text(path);
	       	   self.$element.val(id);
	       	   self.$element.data('name', path);
	       	   self._activeNext(id);
	       	   self.callback && typeof(self.callback) === 'function' && self.callback(id, path, self.$element);
			 }
			 if(!!$(_obj).data('area') && !$(_obj).hasClass('active')) {
				var type = $(_obj).data('area');
				self.$container.find('.areaselect-nav li.active').removeClass('active');
				$(_obj).parent('li').addClass('active');
				self.$container.find('.areaselect-panel.active').removeClass('active');
				self.$container.find('.areaselect-panel.area_' + type).addClass('active');
			 }
		});
		$(document).off('click.tmt.areaselect').on('click.tmt.areaselect', _areaSelectEvent);
	};
	AreaSelect.prototype._activeNext = function(id) {
		var self = this;
		var navs = self.$container.find('.areaselect-nav');
		var active = navs.find('li.active');
		var next = active.next();
		if(!!next.get(0)) {
		   var type = next.find('a').data('area');
		   active.removeClass('active');
		   next.addClass('active');
		   self.$container.find('.areaselect-panel.active').removeClass('active');
		   self.$container.find('.areaselect-panel.area_' + type).addClass('active');
		   self._loadArea(type, id);
		}
	};
	AreaSelect.prototype._loadArea = function(type, id) {
		var self = this;
		Public.getAjax(webRoot + '/admin/system/tag/area/' + type, {parentId: id}, function(data) {
			self._render(type, data.obj);
   	    });
	};
	AreaSelect.prototype._render = function(type, areas) {
		var template = '{{ for(var i =0; i< areas.length; i++) { var area = areas[i]; }} <a class="area" data-id="{{=area.id}}" data-path="{{=area.path}}">{{=area.name}}</a> {{ } }}';
		this.$container.find('.areaselect-panel.area_' + type).html(Public.runTemplate(template, {areas : areas}));
	};
	AreaSelect.prototype._registerEvents = function() {
		var self = this;
		self.on('open', function () {
	      if((self.$container.find('.areaselect-dropdown-wrapper').offset().top - $(document).scrollTop() + 250) > $(window).height()) {
	    	  self.$container.attr('dir','up');
	      } else {
	    	  self.$container.attr('dir','down');
	      }
	      self.$container.addClass('areaselect-container-open');
	      if( !self.$container.find('.areaselect-panel.area_2').text()) { // load data
	    	  self._loadArea('2', '');
	      }
	    });
	    this.on('close', function () {
	       self.$container.removeClass('areaselect-container-open');
	    });
	    this.on('remove', function () {
	       var _chosen = self.$container.find('.areaselect-chosen');
    	   if( !_chosen.hasClass('areaselect-default') ) {
    		   _chosen.addClass('areaselect-default')
    	   }
	       self.$container.find('.areaselect-chosen').addClass('areaselect-default').text('请选择...');
    	   self.$element.val('');
		});
	    this.on('update', function (args) {
	    	self.$container.find('.areaselect-chosen').addClass('areaselect-default').text(args[1]);
	    	self.$element.val(args[0]);
	    });
	};
	AreaSelect.prototype.on = function(name, fnc) {
		this.attrs = this.attrs || {};
		this.attrs[name] = fnc;
	};
	AreaSelect.prototype.trigger = function(name, args) {
		var fnc = this.attrs[name];
		fnc.call(this, args);
	};
	AreaSelect.prototype.render = function () {
		var $container = $(
	       '<span class="areaselect areaselect-container">' +
	         '<span class="areaselect-choice">' +
	           '<span class="areaselect-chosen areaselect-default">请选择...</span>'+
	           '<abbr class="areaselect-chosen-close"></abbr>'+
	           '<span class="areaselect-arrow"><b></b></span>'+
	         '</span>'+
	         '<span class="areaselect-dropdown-wrapper"><span class="areaselect-dropdown"><ul class="areaselect-nav"><li class="active"><a data-area="2">省份</a></li><li><a data-area="3">城市</a></li><li><a data-area="4">县区</a></li><li><a data-area="5">街道</a></li></ul>' +
	         '<span class="areaselect-panels"><span class="areaselect-panel active area_2"></span><span class="areaselect-panel area_3"></span><span class="areaselect-panel area_4"></span><span class="areaselect-panel area_5"></span></span>' +
	         '</span></span>' +
	       '</span>'
	    );
		$container.attr('dir', "ltr");
		this.$container = $container;
		$container.data('element', this.$element);
		var _name = this.$element.data('name');
		if( _name !== undefined && _name !== null) {
			var _chosen = this.$container.find('.areaselect-chosen');
			if(_chosen.hasClass('areaselect-default')) {
			   _chosen.removeClass('areaselect-default')
			}
			_chosen.text(_name); 
		}
		return $container;
	};
	AreaSelect.prototype.destory = function() {};
	AreaSelect.prototype.clear = function() {
    	this.trigger('remove');
	};
	AreaSelect.prototype.update = function(args) {
    	this.trigger('update',args);
	};
	$.fn.areaSelect = function (options) {
		options = options || {};
		if ( typeof options === 'object') {
	        this.each(function () {
	          var instanceOptions = $.extend({}, options, true);
	          var instance = new AreaSelect($(this), instanceOptions);
	        });
	        return this;
	     } else if ( typeof options === 'string') {
	        var instance = this.data('areaselect');
	        if (instance == null && window.console && console.error) {
	          console.error( 'The AreaSelect(\'' + options + '\') method was called on an ' + 'element that is not using AreaSelect.');
	        }
	        var args = Array.prototype.slice.call(arguments, 1);
	        var ret = instance[options](args);
	        return ret;
	     } else {
	        throw new Error('Invalid arguments for AreaSelect: ' + options);
	     }
	};
})(jQuery);