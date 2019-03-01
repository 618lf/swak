/**
 * 扩展文件上传组件
 */
(function(e, c) {
	var d = "multiple" in document.createElement("INPUT");
	var j = "FileList" in window;
	var b = "FileReader" in window;
	var f = function(l, m) {
		var k = this;
		this.settings = e.extend({}, e.fn.ace_file_input.defaults, m);
		this.$element = e(l);
		this.element = l;
		this.disabled = false;
		this.can_reset = true;
		this.$element.on("change.ace_inner_call", function(o, n) {
			if (n === true) {
				return
			}
			return a.call(k);
		});
		this.$element.wrap('<div class="ace-file-input" />');
		this.apply_settings();
	};
	f.error = {
		FILE_LOAD_FAILED : 1,
		IMAGE_LOAD_FAILED : 2,
		THUMBNAIL_FAILED : 3
	};
	f.prototype.apply_settings = function() {
		var l = this;
		var k = !!this.settings.icon_remove;
		this.multi = this.$element.attr("multiple") && d;
		this.well_style = this.settings.style == "well";
		if (this.well_style) {
			this.$element.parent().addClass("ace-file-multiple")
		} else {
			this.$element.parent().removeClass("ace-file-multiple")
		}
		this.$element.parent().find(":not(input[type=file])").remove();
		this.$element.after('<label class="file-label" data-title="'
				+ this.settings.btn_choose
				+ '"><span class="file-name" data-title="'
				+ this.settings.no_file
				+ '">'
				+ (this.settings.no_icon ? '<i class="' + this.settings.no_icon
						+ '"></i>' : "")
				+ "</span></label>"
				+ (k ? '<a class="remove" href="#"><i class="'
						+ this.settings.icon_remove + '"></i></a>' : ""));
		this.$label = this.$element.next();
		this.$label.on("click", function() {
			if (!this.disabled && !l.element.disabled
					&& !l.$element.attr("readonly")) {
				l.$element.click()
			}
		});
		if (k) {
			this.$label.next("a").on(ace.click_event, function() {
				if (!l.can_reset) {
					return false
				}
				var m = true;
				if (l.settings.before_remove) {
					m = l.settings.before_remove.call(l.element)
				}
				if (!m) {
					return false
				}
				return l.reset_input()
			})
		}
		if (this.settings.droppable && j) {
			g.call(this)
		}
	};
	f.prototype.show_file_list = function(k) {
		var n = typeof k === "undefined" ? this.$element
				.data("ace_input_files") : k;
		if (!n || n.length == 0) {
			return
		}
		if (this.well_style) {
			this.$label.find(".file-name").remove();
			if (!this.settings.btn_change) {
				this.$label.addClass("hide-placeholder")
			}
		}
		this.$label.attr("data-title", this.settings.btn_change).addClass(
				"selected");
		for (var p = 0; p < n.length; p++) {
			var l = typeof n[p] === "string" ? n[p] : e.trim(n[p].name);
			var q = l.lastIndexOf("\\") + 1;
			if (q == 0) {
				q = l.lastIndexOf("/") + 1
			}
			l = l.substr(q);
			var m = "icon-file";
			if ((/\.(jpe?g|png|gif|svg|bmp|tiff?)$/i).test(l)) {
				m = "icon-picture"
			} else {
				if ((/\.(mpe?g|flv|mov|avi|swf|mp4|mkv|webm|wmv|3gp)$/i)
						.test(l)) {
					m = "icon-film"
				} else {
					if ((/\.(mp3|ogg|wav|wma|amr|aac)$/i).test(l)) {
						m = "icon-music"
					}
				}
			}
			if (!this.well_style) {
				this.$label.find(".file-name").attr({
					"data-title" : l
				}).find('[class*="icon-"]').attr("class", m)
			} else {
				this.$label.append('<span class="file-name" data-title="' + l
						+ '"><i class="' + m + '"></i></span>');
				var r = e.trim(n[p].type);
				var o = b
						&& this.settings.thumbnail
						&& ((r.length > 0 && r.match("image")) || (r.length == 0 && m == "icon-picture"));
				if (o) {
					var s = this;
					e.when(i.call(this, n[p])).fail(function(t) {
						if (s.settings.preview_error) {
							s.settings.preview_error.call(s, l, t.code)
						}
					})
				}
			}
		}
		return true
	};
	f.prototype.reset_input = function() {
		this.$label.attr({
			"data-title" : this.settings.btn_choose,
			"class" : "file-label"
		}).find(".file-name:first").attr({
			"data-title" : this.settings.no_file,
			"class" : "file-name"
		}).find('[class*="icon-"]').attr("class", this.settings.no_icon).prev(
				"img").remove();
		if (!this.settings.no_icon) {
			this.$label.find('[class*="icon-"]').remove()
		}
		this.$label.find(".file-name").not(":first").remove();
		if (this.$element.data("ace_input_files")) {
			this.$element.removeData("ace_input_files");
			this.$element.removeData("ace_input_method")
		}
		this.reset_input_field();
		return false
	};
	f.prototype.reset_input_field = function() {
		this.$element.wrap("<form>").closest("form").get(0).reset();
		this.$element.unwrap()
	};
	f.prototype.enable_reset = function(k) {
		this.can_reset = k
	};
	f.prototype.disable = function() {
		this.disabled = true;
		this.$element.attr("disabled", "disabled").addClass("disabled")
	};
	f.prototype.enable = function() {
		this.disabled = false;
		this.$element.removeAttr("disabled").removeClass("disabled")
	};
	f.prototype.files = function() {
		return e(this).data("ace_input_files") || null
	};
	f.prototype.method = function() {
		return e(this).data("ace_input_method") || ""
	};
	f.prototype.update_settings = function(k) {
		this.settings = e.extend({}, this.settings, k);
		this.apply_settings()
	};
	var g = function() {
		var l = this;
		var k = this.element.parentNode;
		e(k).on("dragenter", function(m) {
			m.preventDefault();
			m.stopPropagation()
		}).on("dragover", function(m) {
			m.preventDefault();
			m.stopPropagation()
		}).on("drop", function(q) {
			q.preventDefault();
			q.stopPropagation();
			var p = q.originalEvent.dataTransfer;
			var o = p.files;
			if (!l.multi && o.length > 1) {
				var n = [];
				n.push(o[0]);
				o = n
			}
			var m = true;
			if (l.settings.before_change) {
				m = l.settings.before_change.call(l.element, o, true)
			}
			if (!m || m.length == 0) {
				return false
			}
			if (m instanceof Array || (j && m instanceof FileList)) {
				o = m
			}
			l.$element.data("ace_input_files", o);
			l.$element.data("ace_input_method", "drop");
			l.show_file_list(o);
			l.$element.triggerHandler("change", [ true ]);
			return true
		})
	};
	var a = function() {
		var l = true;
		if (this.settings.before_change) {
			l = this.settings.before_change.call(this.element,
					this.element.files || [ this.element.value ], false)
		}
		if (!l || l.length == 0) {
			if (!this.$element.data("ace_input_files")) {
				this.reset_input_field()
			}
			return false
		}
		var m = !j ? null : ((l instanceof Array || l instanceof FileList) ? l
				: this.element.files);
		this.$element.data("ace_input_method", "select");
		if (m && m.length > 0) {
			this.$element.data("ace_input_files", m)
		} else {
			var k = e.trim(this.element.value);
			if (k && k.length > 0) {
				m = [];
				m.push(k);
				this.$element.data("ace_input_files", m)
			}
		}
		if (!m || m.length == 0) {
			return false;
		}
		this.show_file_list(m);
		return true;
	};
	var i = function(o) {
		var n = this;
		var l = n.$label.find(".file-name:last");
		var m = new e.Deferred;
		var k = new FileReader();
		k.onload = function(q) {
			l.prepend("<img class='middle' style='display:none;' />");
			var p = l.find("img:last").get(0);
			e(p)
					.one(
							"load",
							function() {
								var t = 50;
								if (n.settings.thumbnail == "large") {
									t = 150;
								} else {
									if (n.settings.thumbnail == "fit") {
										t = l.width();
									}
								}
								l.addClass(t > 50 ? "large" : "");
								var s = h(p, t, o.type);
								if (s == null) {
									e(this).remove();
									m.reject({
										code : f.error.THUMBNAIL_FAILED
									});
									return
								}
								var r = s.w, u = s.h;
								if (n.settings.thumbnail == "small") {
									r = u = t;
								}
								e(p)
										.css(
												{
													"background-image" : "url("
															+ s.src + ")",
													width : r,
													height : u
												})
										.data("thumb", s.src)
										.attr(
												{
													src : "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQImWNgYGBgAAAABQABh6FO1AAAAABJRU5ErkJggg=="
												}).show();
								m.resolve();
							}).one("error", function() {
						l.find("img").remove();
						m.reject({
							code : f.error.IMAGE_LOAD_FAILED
						});
					});
			p.src = q.target.result;
		};
		k.onerror = function(p) {
			m.reject({
				code : f.error.FILE_LOAD_FAILED
			});
		};
		k.readAsDataURL(o);
		return m.promise();
	};
	var h = function(n, s, q) {
		var r = n.width, o = n.height;
		if (r > s || o > s) {
			if (r > o) {
				o = parseInt(s / r * o);
				r = s;
			} else {
				r = parseInt(s / o * r);
				o = s;
			}
		}
		var m;
		try {
			var l = document.createElement("canvas");
			l.width = r;
			l.height = o;
			var k = l.getContext("2d");
			k.drawImage(n, 0, 0, n.width, n.height, 0, 0, r, o);
			m = l.toDataURL();
		} catch (p) {
			m = null;
		}
		if (!(/^data\:image\/(png|jpe?g|gif);base64,[0-9A-Za-z\+\/\=]+$/
				.test(m))) {
			m = null;
		}
		if (!m) {
			return null;
		}
		return {
			src : m,
			w : r,
			h : o
		};
	};
	e.fn.ace_file_input = function(m, n) {
		var l = '';
		var k = this.each(function() {
			var q = e(this);
			var p = q.data("ace_file_input");
			var o = typeof m === "object" && m;
			if (!p) {
				q.data("ace_file_input", (p = new f(this, o)));
			}
			if (typeof m === "string") {
				l = p[m](n);
			}
		});
		return (l === c) ? k : l;
	};
	e.fn.ace_file_input.defaults = {
		style : false,
		no_file : "请选择文件 ...",
		no_icon : "icon-upload-alt",
		btn_choose : "选择...",
		btn_change : "选择...",
		icon_remove : "",
		droppable : false,
		thumbnail : false,
		before_change : null,
		before_remove : null,
		preview_error : null
	};
})(window.jQuery);

//扩展文件选择组件
var Public = Public ||{};
//选择单个文件
Public.singleFile = function(dom, options){
	if($.browser.msie && parseInt($.browser.version) <= 9){ return;} //IE不支持
	var _options = options||{};
	$(dom).ace_file_input( _options );
};