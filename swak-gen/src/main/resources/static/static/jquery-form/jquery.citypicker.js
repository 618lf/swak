(function($) {
	'use strict';
	var NAMESPACE = 'citypicker';
    var EVENT_CHANGE = 'change.' + NAMESPACE;
    var PROVINCE = 'province';
    var CITY = 'city';
    var DISTRICT = 'district';
    var STREET = 'street';
    
	var CityPicker = function(element, options) {
		this.$element = $(element);
		this.$dropdown = null;
		this.options = $.extend({},CityPicker.DEFAULTS, options);
		this.active = false;
        this.dems = [];
        this.needBlur = false;
        this.init();
	};
	
	CityPicker.prototype = {
		constructor: CityPicker,	
		init: function() {
		   this.defineDems();
	       this.render();
	       this.bind();
	       this.active = true;
		},
		defineDems : function() {
            $.each([PROVINCE, CITY, DISTRICT, STREET], $.proxy(function (i, type) {
            	this.dems.push(type);
            }, this));
		},
		render: function() {
		   var placeholder = this.getPlaceHolder(),
		       offset = this.resolveWidth(this.$element),
		       container = '<div class="city-picker-container"></div>',
		       choice   = '<div class="city-picker-choice">' + 
		                  (placeholder ? '<span class="placeholder">' + placeholder + '</span>' : '') +
		                  '<span class="title"></span><i class="iconfont icon-chevron-down arrow"></i>' +'</div>',
		       dropdown = '<div class="city-picker-dropdown" style="display:none;">' +
		                  '<div class="city-select-wrap">' +
		                  '<div class="city-select-tab">' +
		                  '<a class="active" data-count="province">省份</a>' +
		                  '<a data-count="city">城市</a>' +
		                  '<a data-count="district">区县</a>' + 
		                  (this.level === 'street' ? '<a data-count="street">街道</a>' : '')+ 
		                  '</div>' +
		                  '<div class="city-select-content">' +
		                  '<div class="city-select province" data-count="province"></div>' +
		                  '<div class="city-select city" data-count="city"></div>' + 
		                  '<div class="city-select district" data-count="district"></div>' + 
		                  (this.level === 'street' ? '<div class="city-select street" data-count="street"></div>' : '')+ 
		                  '</div></div>';
		    this.$element.addClass('city-picker-input');
		    this.$container = $(container).insertAfter(this.$element).css('width', offset.width);
		    this.$choice = $(choice).appendTo(this.$container);
            this.$dropdown = $(dropdown).appendTo(this.$container);
            this.$element.data('citypicker', this);
            var $select = this.$dropdown.find('.city-select');
            $.each(this.dems, $.proxy(function (i, type) {
                this['$' + type] = $select.filter('.' + type + '');
            }, this));
            this.refresh();
		},
		refresh: function(force) {
			var $select = this.$dropdown.find('.city-select');
			$select.data('item', null);
			var val = (this.$element.data('name') || '').split('/');
			$.each(this.dems, $.proxy(function (i, type) {
                if (val[i] && i < val.length) {
                    this.options[type] = val[i];
                } else if (force) {
                    this.options[type] = '';
                }
                this.output(type);
            }, this));
			this.tab(PROVINCE);
            this.feedText();
            this.feedVal();
		},
		update: function(val, name) {
			this.$element.val(val);
			this.$element.data('name', name);
			this.refresh();
		},
		resolveWidth: function($dom) {
			var $wrap, $clone, sizes;
            if (!$dom.is(':visible')) {
                $wrap = $("<div />").appendTo($("body"));
                $wrap.css({"position": "absolute !important","visibility": "hidden !important","display": "block !important"});
                $clone = $dom.clone().appendTo($wrap);
                sizes = {
                    width: $clone.outerWidth(),
                    height: $clone.outerHeight()
                };
                $wrap.remove();
            } else {
                sizes = {width: $dom.outerWidth(),height: $dom.outerHeight()};
            }
            return sizes;
		},
		bind : function() {
			var $this = this;
			$(document).on('click', (this._mouteclick = function (e) {
                var $target = $(e.target);
                var $dropdown, $span, $input;
                if ($target.is('.city-picker-choice')) {
                    $span = $target;
                } else if ($target.is('.city-picker-choice *')) {
                    $span = $target.parents('.city-picker-choice');
                }
                if ($target.is('.city-picker-input')) {
                    $input = $target;
                }
                if ($target.is('.city-picker-dropdown')) {
                    $dropdown = $target;
                } else if ($target.is('.city-picker-dropdown *')) {
                    $dropdown = $target.parents('.city-picker-dropdown');
                }
                if ((!$input && !$span && !$dropdown) ||
                    ($span && $span.get(0) !== $this.$choice.get(0)) ||
                    ($input && $input.get(0) !== $this.$element.get(0)) ||
                    ($dropdown && $dropdown.get(0) !== $this.$dropdown.get(0))) {
                    $this.close(true);
                }
            }));
			this.$element.on('change', (this._changeElement = $.proxy(function () {
                this.close(true);
                this.refresh(true);
            }, this))).on('focus', (this._focusElement = $.proxy(function () {
                this.needBlur = true;
                this.open();
            }, this))).on('blur', (this._blurElement = $.proxy(function () {
                if (this.needBlur) {
                    this.needBlur = false;
                    this.close(true);
                }
            }, this)));
            this.$choice.on('click', function (e) {
                var $target = $(e.target), type;
                $this.needBlur = false;
                if ($target.is('.select-item')) {
                    type = $target.data('count');
                    $this.open(type);
                } else {
                    if ($this.$dropdown.is(':visible')) {
                        $this.close();
                    } else {
                        $this.open();
                    }
                }
            }).on('mousedown', function () {
                $this.needBlur = false;
            });
            this.$dropdown.on('click', '.city-select a', function () {
                var $select = $(this).parents('.city-select');
                var $active = $select.find('a.active');
                var last = $select.next().length === 0;
                $active.removeClass('active');
                $(this).addClass('active');
                if ($active.data('code') !== $(this).data('code')) {
                    $select.data('item', {
                        address: $(this).attr('title'), code: $(this).data('code')
                    });
                    $(this).trigger(EVENT_CHANGE);
                    $this.feedText();
                    $this.feedVal();
                    if (last) {
                        $this.close();
                    }
                }
            }).on('click', '.city-select-tab a', function () {
                if (!$(this).hasClass('active')) {
                    var type = $(this).data('count');
                    $this.tab(type);
                }
            }).on('mousedown', function () {
                $this.needBlur = false;
            });
            if(this.$province) {
                this.$province.on(EVENT_CHANGE, (this._changeProvince = $.proxy(function () {
                    this.output(CITY);
                    this.output(DISTRICT);
                    this.output(STREET);
                    this.tab(CITY);
                    this.triggerListen();
                }, this)));
            }
            if(this.$city) {
                this.$city.on(EVENT_CHANGE, (this._changeCity = $.proxy(function () {
                    this.output(DISTRICT);
                    this.output(STREET);
                    this.tab(DISTRICT);
                    this.triggerListen();
                }, this)));
            }
            if(this.$district) {
            	this.$district.on(EVENT_CHANGE, ($.proxy(function () {
            		this.output(STREET);
                    this.tab(STREET);
                    this.triggerListen();
                }, this)));
            }
            if(this.$street) {
            	this.$street.on(EVENT_CHANGE, ($.proxy(function () {
                    this.triggerListen();
                }, this)));
            }
		},
		triggerListen: function() {
			var val = this.getVal(), address = this.getAddress();
			var fnc = this.options.fnc;
			    fnc = (!!fnc && typeof(fnc) === 'function') ? fnc : window[this.options.fnc];
			typeof(fnc) === 'function' && fnc.call(this, val, address);
		},
		open: function (type) {
            type = type || PROVINCE;
            this.$dropdown.show();
            this.$choice.addClass('open').addClass('focus');
            this.$choice.find('.arrow').attr('class', 'iconfont icon-chevron-up arrow')
            this.tab(type);
        },
        close: function (blur) {
            this.$dropdown.hide();
            this.$choice.removeClass('open');
            if (blur) {
                this.$choice.removeClass('focus');
            }
            this.$choice.find('.arrow').attr('class', 'iconfont icon-chevron-down arrow')
        },
        unbind: function () {
            $(document).off('click', this._mouteclick);
            this.$element.off('change', this._changeElement);
            this.$element.off('focus', this._focusElement);
            this.$element.off('blur', this._blurElement);
            this.$choice.off('click');
            this.$choice.off('mousedown');
            this.$dropdown.off('click');
            this.$dropdown.off('mousedown');
            if (this.$province) {
                this.$province.off(EVENT_CHANGE, this._changeProvince);
            }
            if (this.$city) {
                this.$city.off(EVENT_CHANGE, this._changeCity);
            }
        },
        getPlaceHolder: function () {
            return this.$element.attr('placeholder') || this.options.placeholder;
        },
        feedText: function () {
            var text = this.getText();
            if (text) {
                this.$choice.find('>.placeholder').hide();
                this.$choice.find('>.title').html(this.getText()).show();
            } else {
                this.$choice.find('>.placeholder').text(this.getPlaceHolder()).show();
                this.$choice.find('>.title').html('').hide();
            }
        },
        feedVal: function () {
            this.$element.val(this.getVal());
            this.$element.data('name', this.getAddress());
        },
        getText: function () {
            var text = '';
            this.$dropdown.find('.city-select')
                .each(function () {
                    var item = $(this).data('item'),
                        type = $(this).data('count');
                    if (item) {
                        text += ($(this).hasClass('province') ? '' : '/') + '<span class="select-item" data-count="' +
                            type + '" data-code="' + item.code + '">' + item.address + '</span>';
                    }
                });
            return text;
        },
        getVal: function () {
            var text = '';
            this.$dropdown.find('.city-select')
                .each(function () {
                    var item = $(this).data('item');
                    if (item) {
                        text += ($(this).hasClass('province') ? '' : '/') + item.code;
                    }
                });
            return text;
        },
        getAddress: function () {
            var text = '';
            this.$dropdown.find('.city-select')
                .each(function () {
                    var item = $(this).data('item');
                    if (item) {
                        text += ($(this).hasClass('province') ? '' : '/') + item.address;
                    }
                });
            return text;
        },
        output: function (type) {
            var options = this.options;
            var $select = this['$' + type];
            var data = type === PROVINCE ? {} : [];
            var item;
            var districts;
            var code;
            var matched = null;
            var value;
            if (!$select || !$select.length) {
                return;
            }
            item = $select.data('item');
            value = (item ? item.address : null) || options[type];
            code = (
                type === PROVINCE ? 86 :
                    type === CITY ? this.$province && this.$province.find('.active').data('code') :
                        type === DISTRICT ? this.$city && this.$city.find('.active').data('code') : 
                        	type === STREET ? this.$district && this.$district.find('.active').data('code') : code
            );

            districts = $.isNumeric(code) ? ChineseDistricts[code] : null;

            if ($.isPlainObject(districts)) {
                $.each(districts, function (code, address) {
                    var provs;
                    if (type === PROVINCE) {
                        provs = [];
                        for (var i = 0; i < address.length; i++) {
                            if (address[i].address === value) {
                                matched = {
                                    code: address[i].code,
                                    address: address[i].address
                                };
                            }
                            provs.push({
                                code: address[i].code,
                                address: address[i].address,
                                selected: address[i].address === value
                            });
                        }
                        data[code] = provs;
                    } else {
                        if (address === value) {
                            matched = {
                                code: code,
                                address: address
                            };
                        }
                        data.push({
                            code: code,
                            address: address,
                            selected: address === value
                        });
                    }
                });
            }
            $select.html(type === PROVINCE ? this.getProvinceList(data) : this.getList(data, type));
            $select.data('item', matched);
        },
        getProvinceList: function (data) {
            var list = [],
                $this = this,
                simple = this.options.simple;

            $.each(data, function (i, n) {
                list.push('<dl class="clearfix">');
                list.push('<dt>' + i + '</dt><dd>');
                $.each(n, function (j, m) {
                    list.push(
                        '<a' +
                        ' title="' + (m.address || '') + '"' +
                        ' data-code="' + (m.code || '') + '"' +
                        ' class="' +
                        (m.selected ? ' active' : '') +
                        '">' + m.address + '</a>');
                });
                list.push('</dd></dl>');
            });

            return list.join('');
        },
        getList: function (data, type) {
            var list = [],
                $this = this,
                simple = this.options.simple;
            list.push('<dl class="clearfix"><dd>');

            $.each(data, function (i, n) {
                list.push(
                    '<a' +
                    ' title="' + (n.address || '') + '"' +
                    ' data-code="' + (n.code || '') + '"' +
                    ' class="' +
                    (n.selected ? ' active' : '') +
                    '">' + n.address + '</a>');
            });
            list.push('</dd></dl>');
            return list.join('');
        },
        tab: function (type) {
            var $selects = this.$dropdown.find('.city-select');
            var $tabs = this.$dropdown.find('.city-select-tab > a');
            var $select = this['$' + type];
            var $tab = this.$dropdown.find('.city-select-tab > a[data-count="' + type + '"]');
            if ($select) {
                $selects.hide();
                $select.show();
                $tabs.removeClass('active');
                $tab.addClass('active');
            }
        },
        reset: function () {
            this.$element.val(null).trigger('change');
        },
        destroy: function () {
            this.unbind();
            this.$element.removeData(NAMESPACE).removeClass('city-picker-input');
            this.$choice.remove();
            this.$dropdown.remove();
        }
	};
	
	//默认
    CityPicker.DEFAULTS = {
        simple: false,
        responsive: false,
        placeholder: '请选择省/市/区',
        level: 'district',
        province: '',
        city: '',
        district: '',
        fnc:null
    };
    
    CityPicker.setDefaults = function (options) {
        $.extend(CityPicker.DEFAULTS, options);
    };

    CityPicker.other = $.fn.citypicker;
    
    $.fn.citypicker = function (option) {
        var args = [].slice.call(arguments, 1);
        return this.each(function () {
            var $this = $(this);
            var data = $this.data(NAMESPACE);
            var options;
            var fn;
            if (!data) {
                if (/destroy/.test(option)) {
                    return;
                }
                options = $.extend({}, $this.data(), $.isPlainObject(option) && option);
                $this.data(NAMESPACE, (data = new CityPicker(this, options)));
            }
            if (typeof option === 'string' && $.isFunction(fn = data[option])) {
                fn.apply(data, args);
            }
        });
    };
    $.fn.citypicker.Constructor = CityPicker;
    $.fn.citypicker.setDefaults = CityPicker.setDefaults;
    $.fn.citypicker.noConflict = function () {
        $.fn.citypicker = CityPicker.other;
        return this;
    };
    $(function () {
        $('[data-toggle="city-picker"]').citypicker();
    });
})(jQuery);