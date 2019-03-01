!function(a) {
	a.fn.combo = function(b) {
		if (0 == this.length) return this;
		var c, d = arguments;
		return this.each(function() {
			var e = a(this).data("_combo");
			if ("string" == typeof b) {
				if (!e) return;
				"function" == typeof e[b] && (d = Array.prototype.slice.call(d, 1), c = e[b].apply(e, d))
			} else e || (e = new a.Combo(a(this), b), a(this).data("_combo", e))
		}), void 0 === c ? this : c
	}, a.fn.getCombo = function() {
		return a.Combo.getCombo(this)
	}, a.Combo = function(b, c) {
		this.obj = b, this.opts = a.extend(!0, {}, a.Combo.defaults, c), this.dataOpt = this.opts.data, this._selectedIndex = -1, this.addQuery = !0, this._disabled = "undefined" != typeof this.opts.disabled ? !! this.opts.disabled : !! this.obj.attr("disabled"), a.extend(this, this.opts.callback), this._init()
	}, a.Combo.getCombo = function(b) {
		if (b = a(b), 0 != b.length) {
			if (1 == b.length) return b.data("_combo");
			if (b.length > 1) {
				var c = [];
				return b.each(function(b) {
					c.push(a(this).data("_combo"))
				}), c
			}
		}
	}, a.Combo.prototype = {
		constructor: a.Combo,
		_init: function() {
			var a = this.opts;
			"select" == this.obj[0].tagName.toLowerCase() && (this.originSelect = this.obj, this.dataOpt = this._getDataFromSelect()), this._createCombo(), this.loadData(this.dataOpt, a.defaultSelected, a.defaultFlag), this._handleDisabled(this._disabled), this._bindEvent()
		},
		loadData: function(a, b, c) {
			this.xhr && this.xhr.abort(), this.empty(!1), this.dataOpt = a, this.mode = this._getRenderMode(), this.mode && ("local" == this.mode ? (this._formatData(), this._populateList(this.formattedData), this._setDefaultSelected(b, c)) : "remote" == this.mode && this._loadAjaxData(b, c))
		},
		activate: function() {
			this.focus || this.input.focus(), this.wrap.addClass(this.opts.activeCls), this.active = !0
		},
		_blur: function() {
			this.active && (this.collapse(), this.opts.editable && this.opts.forceSelection && (this.selectByText(this.input.val()), -1 == this._selectedIndex && this.input.val("")), this.wrap.removeClass(this.opts.activeCls), this.active = !1, "function" == typeof this.onBlur && this.onBlur())
		},
		blur: function() {
			this.focus && this.input.blur(), this._blur()
		},
		_bindEvent: function() {
			var b = this,
				c = this.opts,
				d = "." + c.listItemCls;
			b.list.on("click", d, function(d) {
				a(this).hasClass(c.selectedCls) || b.selectByItem(a(this)), b.collapse(), b.input.focus(), "function" == typeof c.callback.onListClick && c.callback.onListClick.call(b)
			}).on("mouseover", d, function(b) {
				a(this).addClass(c.hoverCls).siblings().removeClass(c.hoverCls)
			}).on("mouseleave", d, function(b) {
				a(this).removeClass(c.hoverCls)
			}), b.input.on("focus", function(a) {
				b.wrap.addClass(c.activeCls), b.focus = !0, b.active = !0, "function" == typeof b.onFocus && b.onFocus()
			}).on("blur", function(a) {
				b.focus = !1
			}), c.editable ? b.input.on("click", function(a) {}) : b.input.on("click", function(a) {
				b._onTriggerClick()
			}), b.trigger && b.trigger.on("click", function(a) {
				b._onTriggerClick()
			}), a(document).on("click", function(c) {
				var d = c.target || c.srcElement;
				0 == a(d).closest(b.wrap).length && 0 == a(d).closest(b.listWrap).length && b.blur()
			}), this.listWrap.on("click", function(a) {
				a.stopPropagation()
			}), a(window).on("resize", function() {
				b._setListPosition()
			}), this._bindKeyEvent()
		},
		_bindKeyEvent: function() {
			var b = this,
				c = this.opts,
				d = {
					backSpace: 8,
					esc: 27,
					f7: 118,
					up: 38,
					down: 40,
					tab: 9,
					enter: 13,
					home: 36,
					end: 35,
					pageUp: 33,
					pageDown: 34,
					space: 32
				};
			this.input.on("keydown", function(e) {
				switch (e.keyCode) {
				case d.tab:
					b._blur();
					break;
				case d.down:
				case d.up:
					if (b.isExpanded) {
						var f = e.keyCode == d.down ? "next" : "prev";
						b._setItemFocus(f)
					} else b._onTriggerClick();
					e.preventDefault();
					break;
				case d.enter:
					if (b.queryDelay && window.clearTimeout(b.queryDelay), b.isExpanded) {
						var g = b.list.find("." + c.hoverCls);
						g.length > 0 && b.selectByItem(g), b.collapse()
					} else {
						var h = a.trim(b.input.val());
						b.selectByText(h)
					}
					"function" == typeof c.callback.onEnter && c.callback.onEnter(e);
					break;
				case d.home:
				case d.end:
					if (b.isExpanded) {
						var g = e.keyCode == d.home ? b.list.find("." + c.listItemCls).eq(0) : b.list.find("." + c.listItemCls).filter(":last");
						b._scrollToItem(g), e.preventDefault()
					}
					break;
				case d.pageUp:
				case d.pageDown:
					if (b.isExpanded) {
						var f = e.keyCode == d.pageUp ? "up" : "down";
						b._scrollPage(f), e.preventDefault()
					}
				}
			}).on("keyup", function(a) {
				if (c.editable) {
					var e = a.which,
						f = 8 == e || 9 == e || 13 == e || 27 == e || e >= 16 && 20 >= e || e >= 33 && 40 >= e || e >= 44 && 46 >= e || e >= 112 && 123 >= e || 144 == e || 145 == e,
						g = b.input.val();
					f && e != d.backSpace || b.doDelayQuery(g)
				}
			}), a(document).on("keydown", function(a) {
				a.keyCode == d.esc && b.collapse()
			})
		},
		distory: function() {},
		enable: function() {
			this._handleDisabled(!1)
		},
		disable: function(a) {
			a = "undefined" == typeof a ? !0 : !! a, this._handleDisabled(a)
		},
		_handleDisabled: function(a) {
			var b = this.opts;
			this._disabled = a, 1 == a ? this.wrap.addClass(b.disabledCls) : this.wrap.removeClass(b.disabledCls), this.input.attr("disabled", a)
		},
		_createCombo: function() {
			var b, c, d, e = this.opts,
				f = parseInt(this.opts.width);
			this.originSelect && this.originSelect.hide(), "input" == this.obj[0].tagName.toLowerCase() ? this.input = this.obj : (c = this.obj.find("." + e.inputCls), this.input = c.length > 0 ? c : a('<input type="text" class="' + e.inputCls + '"/>')), this.input.attr({
				autocomplete: "off",
				readOnly: !e.editable
			}).css({
				cursor: e.editable ? "" : "default"
			}), d = a(this.obj).find("." + e.triggerCls), d.length > 0 ? this.trigger = d : e.trigger !== !1 && (this.trigger = a('<span class="' + e.triggerCls + '"></span>')), b = this.obj.hasClass(e.wrapCls) ? this.obj : this.obj.find("." + e.wrapCls), b.length > 0 ? this.wrap = b.append(this.input, this.trigger) : this.trigger && (this.wrap = a('<span class="' + e.wrapCls + '"></span>').append(this.input, this.trigger), this.originSelect && this.obj[0] == this.originSelect[0] || this.obj[0] == this.input[0] ? this.obj.next().length > 0 ? this.wrap.insertBefore(this.obj.next()) : this.wrap.appendTo(this.obj.parent()) : this.wrap.appendTo(this.obj)), this.wrap && e.id && this.wrap.attr("id", e.id), this.wrap || (this.wrap = this.input), this._setComboLayout(f), this.list = a("<div />").addClass(e.listCls).css({
				position: "relative",
				overflow: "auto"
			}), this.listWrap = a("<div />").addClass(e.listWrapCls).attr("id", e.listId).hide().append(this.list).css({
				position: "absolute",
				top: 0,
				zIndex: e.zIndex
			}), e.extraListHtml && a("<div />").addClass(e.extraListHtmlCls).append(e.extraListHtml).appendTo(this.listWrap), e.listRenderToBody ? (a.Combo.allListWrap || (a.Combo.allListWrap = a('<div id="COMBO_WRAP"/>').appendTo("body")), this.listWrap.appendTo(a.Combo.allListWrap)) : this.wrap.after(this.listWrap)
		},
		_setListLayout: function() {
			var a, b, c = this.opts,
				d = parseInt(c.listHeight),
				e = 0,
				f = this.trigger ? this.trigger.outerWidth() : 0,
				g = parseInt(c.minListWidth),
				h = parseInt(c.maxListWidth);
			if (this.listWrap.width("auto"), this.list.height("auto"), this.listWrap.show(), this.isExpanded = !0, b = this.list.height(), !isNaN(d) && d >= 0 && (d = Math.min(d, b), this.list.height(d)), "auto" == c.listWidth || "auto" == c.width ? (a = this.listWrap.outerWidth(), b < this.list.height() && (e = 20, a += e)) : (a = parseInt(c.listWidth), isNaN(a) ? a = this.wrap.outerWidth() : null), "auto" == c.width) {
				var i = this.listWrap.outerWidth() + Math.max(f, e);
				this._setComboLayout(i)
			}
			g = isNaN(g) ? this.wrap.outerWidth() : Math.max(g, this.wrap.outerWidth()), !isNaN(g) && g > a && (a = g), !isNaN(h) && a > h && (a = h), a -= this.listWrap.outerWidth() - this.listWrap.width(), this.listWrap.width(a), this.listWrap.hide(), this.isExpanded = !1
		},
		_setComboLayout: function(a) {
			if (a) {
				var b = this.opts,
					c = parseInt(b.maxWidth),
					d = parseInt(b.minWidth);
				!isNaN(c) && a > c && (a = c), !isNaN(d) && d > a && (a = d);
				var e;
				a -= this.wrap.outerWidth() - this.wrap.width(), this.wrap.width(a), this.wrap[0] != this.input[0] && (e = a - (this.trigger ? this.trigger.outerWidth() : 0) - (this.input.outerWidth() - this.input.width()), this.input.width(e))
			}
		},
		_setListPosition: function() {
			if (this.isExpanded) {
				var b, c, d = (this.opts, a(window)),
					e = this.wrap.offset().top,
					f = this.wrap.offset().left,
					g = d.height(),
					h = d.width(),
					i = d.scrollTop(),
					j = d.scrollLeft(),
					k = this.wrap.outerHeight(),
					l = this.wrap.outerWidth(),
					m = this.listWrap.outerHeight(),
					n = this.listWrap.outerWidth(),
					o = parseInt(this.listWrap.css("border-top-width"));
				b = e - i + k + m > g && e > m ? e - m + o : e + k - o, c = f - j + n > h ? f + l - n : f, this.listWrap.css({
					top: b,
					left: c
				})
			}
		},
		_getRenderMode: function() {
			var b, c = this.dataOpt;
			return a.isFunction(c) && (c = c()), a.isArray(c) ? (this.rawData = c, b = "local") : "string" == typeof c && (this.url = c, b = "remote"), b
		},
		_loadAjaxData: function(b, c, d) {
			var e = this,
				f = e.opts,
				g = f.ajaxOptions,
				h = a("<div />").addClass(f.loadingCls).text(g.loadingText);
			e.list.append(h), e.list.find(f.listTipsCls).remove(), e._setListLayout(), e._setListPosition(), e.xhr = a.ajax({
				url: e.url,
				type: g.type,
				dataType: g.dataType,
				timeout: g.timeout,
				success: function(f) {
					h.remove(), a.isFunction(g.success) && g.success(f), a.isFunction(g.formatData) && (f = g.formatData(f)), f && (e.rawData = f, e._formatData(), e._populateList(e.formattedData), "" === b ? (e.lastQuery = d, e.filterData = e.formattedData, e.expand()) : e._setDefaultSelected(b, c), e.xhr = null, e.mode = e._getRenderMode())
				},
				error: function(b, c, d) {
					h.remove(), a("<div />").addClass(f.tipsCls).text(g.errorText).appendTo(e.list), e.xhr = null
				}
			})
		},
		getDisabled: function() {
			return this._disabled
		},
		getValue: function() {
			return this._selectedIndex > -1 ? this.formattedData[this._selectedIndex].value : this.opts.forceSelection ? "" : this.input.val()
		},
		getText: function() {
			return this._selectedIndex > -1 ? this.formattedData[this._selectedIndex].text : this.opts.forceSelection ? "" : this.input.val()
		},
		getSelectedIndex: function() {
			return this._selectedIndex
		},
		getSelectedRow: function() {
			return this._selectedIndex > -1 ? this.rawData[this._selectedIndex] : void 0
		},
		getDataRow: function() {
			return this._selectedIndex > -1 ? this.rawData[this._selectedIndex] : void 0
		},
		getAllData: function() {
			return this.formattedData
		},
		getAllRawData: function() {
			return this.rawData
		},
		_setDefaultSelected: function(b, c) {
			var d = this.opts;
			if ("function" == typeof b && (defaultSelected = defaultSelected.call(this, this.rawData)), isNaN(parseInt(b))) if (a.isArray(b)) this.selectByKey(b[0], b[1], c);
			else if (this.originSelect) {
				var e = this.originSelect[0].selectedIndex;
				this._setSelected(e, c)
			} else d.autoSelect && this._setSelected(0, c);
			else {
				var e = parseInt(b);
				this._setSelected(e, c)
			}
		},
		selectByIndex: function(a, b) {
			this._setSelected(a, b)
		},
		selectByText: function(a, b) {
			if (this.formattedData) {
				for (var c = this.formattedData, d = -1, e = 0, f = c.length; f > e; e++) if (c[e].text === a) {
					d = e;
					break
				}
				this._setSelected(d, b)
			}
		},
		selectByValue: function(a, b) {
			if (this.formattedData) {
				for (var c = this.formattedData, d = -1, e = 0, f = c.length; f > e; e++) if (c[e].value === a) {
					d = e;
					break
				}
				this._setSelected(d, b)
			}
		},
		selectByKey: function(a, b, c) {
			if (this.rawData) {
				var d = this,
					e = d.opts,
					f = this.rawData,
					g = -1;
				if (e.addOptions || e.emptyOptions) {
					f = this.formattedData;
					for (var h = 0, i = f.length; i > h; h++) if (f[h].value === b) {
						g = h;
						break
					}
				} else for (var h = 0, i = f.length; i > h; h++) if (f[h][a] === b) {
					g = h;
					break
				}
				this._setSelected(g, c)
			}
		},
		selectByItem: function(a, b) {
			if (a && a.parent()[0] == this.list[0]) {
				var c = a.text();
				this.selectByText(c, b)
			}
		},
		_setSelected: function(a, b) {
			var c = this.opts,
				a = parseInt(a),
				b = "undefined" != typeof b ? !! b : !0;
			if (!isNaN(a)) {
				if (!this.formattedData || 0 == this.formattedData.length) return void(this._selectedIndex = -1);
				var d = this.formattedData.length;
				if ((-1 > a || a >= d) && (a = -1), this._selectedIndex != a) {
					var e = -1 == a ? null : this.formattedData[a],
						f = -1 == a ? null : e.rawData,
						g = -1 == a ? "" : e.text;
					this.list.find("." + c.listItemCls);
					(!b || "function" != typeof this.beforeChange || this.beforeChange(f)) && (c.editable && -1 == a && this.focus || this.input.val(g), this._selectedIndex = a, b && "function" == typeof this.onChange && this.onChange(f), this.originSelect && (this.originSelect[0].selectedIndex = a))
				}
			}
		},
		removeSelected: function(a) {
			this.input.val(""), this._setSelected(-1, a)
		},
		_triggerCallback: function(a, b) {},
		_getDataFromSelect: function() {
			var b = this.opts,
				c = [];
			return a.each(this.originSelect.find("option"), function(d) {
				var e = a(this),
					f = {};
				f[b.text] = e.text(), f[b.value] = e.attr("value"), c.push(f)
			}), c
		},
		_formatData: function() {
			if (a.isArray(this.rawData)) {
				var b = this,
					c = b.opts;
				b.formattedData = [], c.emptyOptions && b.formattedData.push({
					text: "(空)",
					value: 0
				}), c.addOptions && b.formattedData.push(c.addOptions), a.each(this.rawData, function(d, e) {
					var f = {};
					f.text = a.isFunction(c.formatText) ? c.formatText(e) : e[c.text], f.value = a.isFunction(c.formatValue) ? c.formatValue(e) : e[c.value], f.rawData = e, b.formattedData.push(f)
				}), b.formattedLen = b.formattedData.length
			}
		},
		_filter: function(b) {
			function c() {
				this._formatData(), this.filterData = this.formattedData, this.lastQuery = b, this.list.empty(), this._populateList(this.filterData), this.expand()
			}
			b = "undefined" == typeof b ? "" : b, this.input.val() != this.getText() && this.selectByText(this.input.val());
			var d = this.opts,
				e = this;
			d.maxFilter;
			if (this.opts.cache || ("local" == this.mode && a.isFunction(this.dataOpt) && (this.rawData = this.dataOpt()), this._formatData()), a.isArray(this.formattedData)) {
				if ("" == b) this.filterData = this.formattedData;
				else {
					this.filterData = [];
					var f = [];
					a.each(e.formattedData, function(c, g) {
						var h = g.text;
						if (a.isFunction(d.customMatch)) {
							if (!d.customMatch(g, b)) return
						} else {
							var i = d.caseSensitive ? "" : "i",
								j = new RegExp(b.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&"), i);
							if (-1 == h.search(j)) return
						}
						return e.filterData.push(g), f.push({
							i: c,
							val: g.value
						}), e.filterData.length == d.maxFilter ? !1 : void 0
					})
				}
				for (var g = {}, h = [], i = 0, j = this.filterData.length; j > i; i++) {
					var k = this.filterData[i];
					g[k.value] || (g[k.value] = !0, h.push(k))
				}
				this.filterData = h, h = [], g = {}, a.isFunction(this.incrementalSearch) && 100 === e.formattedLen && e.filterData.length < d.maxFilter ? e.addQuery === !0 && this.incrementalSearch(f, c) : (this.lastQuery = b, this.list.empty(), this._populateList(this.filterData), this.expand())
			}
		},
		doDelayQuery: function(a) {
			var b = this,
				c = b.opts,
				d = parseInt(c.queryDelay);
			isNaN(d) && (d = 0), b.queryDelay && window.clearTimeout(b.queryDelay), b.queryDelay = window.setTimeout(function() {
				b.doQuery(a)
			}, d)
		},
		doQuery: function(a) {
			"local" == this.mode || "remote" == this.mode && this.opts.loadOnce ? this._filter(a) : this._loadAjaxData("", !1, a)
		},
		_populateList: function(b) {
			if (b) {
				var c = this,
					d = c.opts;
				if (0 == b.length) d.forceSelection && (a("<div />").addClass(d.tipsCls).html(d.noDataText).appendTo(c.list), this._setListLayout());
				else {
					for (var e = 0, f = b.length; f > e; e++) {
						var g = b[e],
							h = g.text,
							i = g.value,
							j = a("<div />").attr({
								"class": d.listItemCls + (e == this._selectedIndex ? " " + d.selectedCls : ""),
								"data-value": i
							});
						d.disStrict ? j.html(h).appendTo(c.list) : j.text(h).appendTo(c.list)
					}
					this._setListLayout()
				}
			}
		},
		expand: function() {
			var b = this.opts;
			if (!this.active || this.isExpanded || 0 == this.filterData.length && !b.noDataText && !b.extraListHtmlCls) return void this.listWrap.hide();
			this.isExpanded = !0, this.listWrap.show(), this._setListPosition(), a.isFunction(this.onExpand) && this.onExpand();
			var c = this.list.find("." + b.listItemCls);
			if (0 != c.length) {
				var d = c.filter("." + b.selectedCls);
				0 == d.length && (d = c.eq(0), b.autoSelectFirst && d.addClass(b.hoverCls)), this._scrollToItem(d)
			}
		},
		collapse: function() {
			if (this.isExpanded) {
				var b = this.opts;
				this.listWrap.hide(), this.isExpanded = !1, this.listItems && this.listItems.removeClass(b.hoverCls), a.isFunction(this.onCollapse) && this.onCollapse()
			}
		},
		_onTriggerClick: function() {
			this._disabled || (this.active = !0, this.input.focus(), this.isExpanded ? this.collapse() : this._filter())
		},
		_scrollToItem: function(a) {
			if (a && 0 != a.length) {
				var b = this.list.scrollTop(),
					c = b + a.position().top,
					d = b + this.list.height(),
					e = c + a.outerHeight();
				(b > c || e > d) && this.list.scrollTop(c)
			}
		},
		_scrollPage: function(a) {
			var b, c = this.list.scrollTop(),
				d = this.list.height();
			"up" == a ? b = c - d : "down" == a && (b = c + d), this.list.scrollTop(b)
		},
		_setItemFocus: function(a) {
			var b, c, d = this.opts,
				e = this.list.find("." + d.listItemCls);
			if (0 != e.length) {
				var f = e.filter("." + d.hoverCls).eq(0);
				0 == f.length && (f = e.filter("." + d.selectedCls).eq(0)), 0 == f.length ? b = 0 : (b = e.index(f), b = "next" == a ? b == e.length - 1 ? 0 : b + 1 : 0 == b ? e.length - 1 : b - 1), c = e.eq(b), e.removeClass(d.hoverCls), c.addClass(d.hoverCls), this._scrollToItem(c)
			}
		},
		empty: function(a) {
			this._setSelected(-1, !1), this.input.val(""), this.list.empty(), this.rawData = null, this.formattedData = null
		},
		setEdit: function() {}
	}, a.Combo.defaults = {
		data: null,
		text: "text",
		value: "value",
		formatText: null,
		formatValue: null,
		defaultSelected: void 0,
		defaultFlag: !0,
		autoSelect: !0,
		disabled: void 0,
		editable: !1,
		caseSensitive: !1,
		forceSelection: !0,
		cache: !0,
		queryDelay: 100,
		maxFilter: 20,
		minChars: 0,
		customMatch: null,
		addQuery: "",
		noDataText: "没有匹配的选项",
		autoSelectFirst: !0,
		width: void 0,
		minWidth: void 0,
		maxWidth: void 0,
		listWidth: void 0,
		listHeight: 150,
		maxListWidth: void 0,
		maxListWidth: void 0,
		zIndex: 1e3,
		listRenderToBody: !0,
		extraListHtml: void 0,
		disStrict: !1,
		ajaxOptions: {
			type: "post",
			dataType: "json",
			queryParam: "query",
			timeout: 1e4,
			formatData: null,
			loadingText: "Loading...",
			success: null,
			error: null,
			errorText: "数据加载失败"
		},
		loadOnce: !0,
		id: void 0,
		listId: void 0,
		wrapCls: "ui-combo-wrap",
		focusCls: "ui-combo-focus",
		disabledCls: "ui-combo-disabled",
		activeCls: "ui-combo-active",
		inputCls: "input-txt",
		triggerCls: "trigger",
		listWrapCls: "ui-droplist-wrap",
		listCls: "droplist",
		listItemCls: "list-item",
		selectedCls: "selected",
		hoverCls: "on",
		loadingCls: "loading",
		tipsCls: "tips",
		extraListHtmlCls: "extra-list-ctn",
		callback: {
			onFocus: null,
			onBlur: null,
			beforeChange: null,
			onChange: null,
			onExpand: null,
			onCollapse: null,
			onEnter: null,
			onListClick: null
		}
	}
}(jQuery)