//上传组件
(function ($) {
  
	var _defaultUrl = webRoot + "/admin/system/attachment/doUpload";
	var _mergeUrl = webRoot + "/admin/system/attachment/doMerge";
		
	//实例化一个上传组件
	function _uploader(e) {
	   return this.opts = e || {}, this.disabled = e.disabled || !1, e.server = e.server || _defaultUrl, this.label = e.label||'文件', this.action = e.action||'uploadfile', this.init(e)
	};
	
	_uploader.prototype = {
	   init : function(e) {
		   if (this.opts = e, !e ) throw new Error('required param "main" wrong');
		   if (!e.server || !e.action) throw new Error('required param "server" wrong');
		   if (!WebUploader.Uploader.support()) throw alert("您的浏览器版本过低！"), new Error("WebUploader does not support the browser you are using.");
		   this.main = e.main;
		   var t = this.main;
		   this.uploader = null;
		   if (this.opts.auto) i = $('<ul class="filelist"><div class="filePicker2" style="display:none;"></div></ul>');
		   else i = $('<ul class="filelist"></ul>');
		   this.elements = {
				wrap: $(t).find(".uploader").addClass(this.opts.auto?'auto-uploader':''),
				queue: i.appendTo( $(t).find('.queueList') ),
				statusBar: $(t).find(".statusBar"),
				info: $(t).find(".info"),
				upload: $(t).find(".uploadBtn"),
				placeHolder: $(t).find(".placeholder"),
				btnAdder: $(t).find(".filePicker2"),
				progress: $(t).find(".progress").hide()
		   }, this.fileCount = 0, this.fileSize = 0, this.ratio = window.devicePixelRatio || 1, this.thumbnailWidth = 100 * this.ratio, this.thumbnailHeight = 100 * this.ratio, this.state = "pedding", this.percentages = {}, this.uploadedFiles = {}, this.formDatas = [], this.render()
	   },
	   render : function() {
		   var self = this;
		   var e = this.main,
		       t = {
				   pick: {
					  id: $(e).find(".filePicker"),
					  label: "点击选择" + self.label
				   }, 
				   formData: {
					  uid: "catax",
					  'token.ignore': 'true',
					  action: self.action
				   },
				   fileVal: 'upfile',
				   auto: !1,
				   dnd: $(e).find(".uploader"),
				   paste: $(e).find(".uploader"),
				   swf: "/static/webuploader/Uploader.swf",
				   chunked: !1,
				   chunkSize: 2097152,
				   sendAsBinary: !1,
				   disableGlobalDnd: !0,
				   fileNumLimit: 30,
				   fileSizeLimit: 62914560,
				   fileSingleSizeLimit: 62914560,
				   resize : false
		       };
		   if (this.opts = $.extend(!0, {}, t, this.opts), !this.uploader) {
				if (this.uploader = WebUploader.create(this.opts)) this.uploader.addButton({
					id: $(e).find(".filePicker2"),
					label: "继续添加"
				});
				if (this.initUploaderEvent(), this.initDomEvent(), this.elements.upload.addClass("state-" + this.state), this.updateTotalProgress(), !(window.Blob && window.FileReader && window.DataView)) $(e).find(".drag-tip").hide()
		   }
	   },
	   getUploader: function() {
		   return this.uploader
	   },
	   initUploaderEvent: function() {
			var e = this, t = this.uploader;
			t.onUploadProgress = function(t, i) {
				if (!isFinite(i)) i = 1;
				var n = $("#" + t.id),
					o = n.find(".progress .percent"),
					a = n.find(".progress .text"),
					s = parseInt(100 * i, 10) + "%";
				n.find(".progress").show(), a.text(s), o.css("width", s), e.percentages[t.id][1] = i, e.updateTotalProgress()
			}, 
			t.onBeforeFileQueued = function(t) {
				var i = e.uploader.getStats();
				if (i.successNum + i.queueNum + i.uploadFailNum >= e.opts.fileNumLimit) return e.showUploadErrorTip(Q_EXCEED_NUM_LIMIT), !1;
				else return void 0
			}, 
			t.onFileQueued = function(t) {
				if (e.fileCount++, e.fileSize += t.size, 1 === e.fileCount) e.elements.statusBar.show();
				if (e.addFile(t), e.setState("ready"), e.formDatas[t.id] = WebUploader.Base.guid(),e.updateTotalProgress(), e.opts.onFileQueued) e.opts.onFileQueued()
			}, 
			t.onFileDequeued = function(t) {
				if (e.fileCount--, e.fileSize -= t.size, !e.fileCount) e.setState("pedding");
				if (e.removeFile(t), e.updateTotalProgress(), e.opts.onFileDequeued) e.opts.onFileDequeued();
				if(typeof(e.opts.fileDequeued) === 'function') {e.opts.fileDequeued();}
			},
			t.onUploadBeforeSend = function(file, data, header){
			    header['X_Requested_With'] = 'XMLHttpRequest';
			    data.guid = e.formDatas[data.id];
			},
			t.onUploadAccept = function(t, i) {
				if (i && 'SUCCESS' === i.state) {
					e.uploadedFiles[t.file.__hash + '_hash'] = {file: t,info: i}
				} else return !1
			},
			t.onUploadSuccess = function(f) {
				if(e.opts.chunked) {
				   var postData = {
					   guid : e.formDatas[f.id],
					   fileName : f.name,
					   size: f.size,
				   };
				   postData = $.extend(!0, {}, postData, e.opts.formData);
				   Public.postAjax(_mergeUrl, postData, function() {});
				}
			},
			t.on("all", function(t) {
				switch (t) {
				case "uploadFinished":
					e.setState("confirm");
					break;
				case "startUpload":
					e.setState("uploading");
					break;
				case "stopUpload":
					e.setState("paused")
				}
			}), 
			t.onError = function(t) {
				e.showUploadErrorTip(t)
			}
		},
		showUploadErrorTip: function(e) {
			var t = {
			    Q_TYPE_DENIED: "文件类型不支持",
				Q_EXCEED_NUM_LIMIT: "一次最多可选" + this.opts.fileNumLimit + "张哦~",
				Q_EXCEED_SIZE_LIMIT: "文件总大小过大啦~",
				F_EXCEED_SIZE: "文件大小超出限制啦~",
				F_DUPLICATE: "文件不要重复上传哦~"
			};
			Public.error(t[e]);
		},
		initDomEvent: function() {
			var e = this,
				t = this.uploader,
				i = this.elements.info;
			this.elements.upload.on("click", function() {
				var i = e.state;
				if ($(this).hasClass("disabled")) return !1;
				if ("ready" === i) t.upload();
				else if ("paused" === i) t.upload();
				else if ("uploading" === i) t.stop(!0)
			}), i.on("click", ".retry", function() {
				t.retry()
			}), i.on("click", ".ignore", function() {})
		},
		addFile: function(t) {
			function n(code) {
				var t = "";
				switch (code) {
				case "exceed_size":
					t = "文件大小超出";
					break;
				case "interrupt":
					t = "上传暂停";
					break;
				default:
					t = "上传失败"
				}
				v.text(t).appendTo(f)
			}
			var o = this,
				a = this.uploader,
				s = this.percentages,
				l = '<li id="' + t.id + '"><p class="imgWrap"></p><p class="progress" style="display:none;"><span class="text">0%</span><span class="percent"></span></p></li>',
				f = $(l),
				h = '<div class="file-panel"><span class="cancel">X</span><span class="rotate-right">右边</span></div>',
				p = $(h).appendTo(f),
				m = f.find("p.progress"),
				g = f.find("p.imgWrap"),
				v = $('<p class="error"></p>'),
				b = "uploaded" === t.progress ? !0 : !1,
				u = function() {
					var e = new Image,
						t = !0;
					return e.onload = e.onerror = function() {
						if (1 !== this.width || 1 !== this.height) t = !1
					}, e.src = "data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==", t
				}(),
				e = function (e, t) {
					if (e = $(e), f) {
						var i = "rotate(" + t + "deg)";
						e.css({
							"-webkit-transform": i,
							"-mos-transform": i,
							"-o-transform": i,
							transform: i
						})
					} else {
						var n = ~~ (t / 90 % 4 + 4) % 4;
						e.css("filter", "progid:DXImageTransform.Microsoft.BasicImage(rotation=" + n + ")")
					}
				};
			if ("instateid" === t.getStatus()) { n(t.statusText);
			} else if ('|png|jpg|jpeg|bmp|gif|'.indexOf('|'+t.ext.toLowerCase()+'|') == -1) {//非图片的预览
				g.text("预览中");
				g.empty().append('<i class="file-preview file-type-' + t.ext.toLowerCase() + '"></i>' +
	            '<span class="file-title" title="' + t.name + '">' + t.name + '</span>');
			} else {//图片的预览
				g.text("预览中");
				var w = a.options || {};
				a.makeThumb(t, function(e, t) {
					if (e) return void g.text("不能预览");
					if (!u) {
						var n = +new Date,
							o = "kb-img-prev" + n,
							a = i({
								id: o,
								url: w.imgPrevSwf,
								width: 100,
								height: 100,
								wmode: "transparent",
								allowscriptaccess: "always"
							});
						return g.empty().append($(a)), void(d[o] = t)
					}
					var s = $('<img src="' + t + '">');
					g.empty().append(s)
				}, o.thumbnailWidth, o.thumbnailHeight);
			}
			s[t.id] = [t.size, 0], t.rotation = 0;
			if (!b) t.on("statuschange", function(e, i) {
				if ("progress" === i) m.hide().width(0);
				if ("error" === e || "invalid" === e) n(t.statusText), s[t.id][1] = 1;
				else if ("interrupt" === e) n("interrupt");
				else if ("queued" === e) s[t.id][1] = 0;
				else if ("progress" === e) v.remove(), m.css("display", "block");
				else if ("complete" === e) f.append('<span class="success"></span>');
				f.removeClass("state-" + i).addClass("state-" + e)
			});
			else f.append('<span class="success"></span>'), f.addClass("state-complete");
			f.hover(function() {
				if (!f.hasClass("no-del")) p.fadeIn(50)
			}, function() {
				if (!f.hasClass("no-del")) p.fadeOut(50)
			}), p.on("click", "span", function() {
				var i = $(this).index();
				switch (i) {
				case 0:
					if ("complete" === t.getStatus()) o.uploader.request("get-stats").numOfSuccess--, delete o.uploadedFiles[t.__hash + '_hash'];
					return void a.removeFile(t);
				case 1:
					t.rotation += 90;
					break;
				case 2:
					t.rotation -= 90
				}
				e(g, t.rotation)
			}), this.opts.auto?f.insertBefore($(this.elements.queue).find('.filePicker2')):f.appendTo(this.elements.queue)
		},
		removeFile: function(e) {
			var t = $("#" + e.id);
			delete this.percentages[e.id], this.updateTotalProgress(), t.off().find(".file-panel").off().end().remove()
		},
		removeAllFiles: function() {
			this.percentages = {}, this.updateTotalProgress(), this.elements.queue.find("li").off().find(".file-panel").off().end().remove()
		},
		setState: function(e) {
			var t = null,
				i = this.uploader,
				n = this.elements.upload,
				o = this.elements.queue,
				a = this.elements.statusBar,
				p = this.elements.placeHolder,
				b = this.elements.btnAdder,
				s = this.elements.progress;
			if (e !== this.state) {
				n.removeClass("state-" + e), n.addClass("state-" + e), this.state = e;
				var l = $(this.main).find(".filelist li");
				switch (e) {
				case "pedding":
					l.removeClass("no-del"), o.hide(), a.addClass("element-invisible"), b.hide().addClass("element-invisible"), p.removeClass('element-invisible'), i.refresh();
					break;
				case "ready":
					l.removeClass("no-del"), n.text("开始上传").removeClass("disabled"), o.show(), a.removeClass("element-invisible"), b.show().removeClass("element-invisible"), p.addClass('element-invisible'), i.refresh();
					break;
				case "uploading":
					l.addClass("no-del"), s.show(), n.addClass("disabled");
					break;
				case "paused":
					l.addClass("no-del"), s.show(), n.text("继续上传");
					break;
				case "confirm":
					if (l.removeClass("no-del"), s.hide(), n.text("开始上传").addClass("disabled"), t = i.getStats(), t.successNum && !t.uploadFailNum) return void this.setState("finish");
					break;
				case "finish":
					if (l.removeClass("no-del"), t = i.getStats(), !t.successNum) e = "done"
				}
				this.updateStatus()
			}
		},
		updateStatus: function() {
			var e = "",
				t = null,
				i = this.state,
				n = this.uploader,
				r = this.fileCount,
				a = this.fileSize;
			if ("ready" === i) e = "选中" + r + "张图片，共" + WebUploader.formatSize(a) + "。";
			else if ("confirm" === i) {
				if (t = n.getStats(), t.uploadFailNum) e = "成功上传：" + t.successNum + "张，失败：" + t.uploadFailNum + '张，<a class="retry" href="javascript:;">重新上传</a>'
			} else if (t = n.getStats(), e = "共" + r + "张，已上传" + t.successNum + "张", t.uploadFailNum) e += "，失败" + t.uploadFailNum + "张";
			this.elements.info.html(e)
		},
		updateTotalProgress: function() {
			var e = 0,
				t = 0,
				i = this.elements.progress.children(),
				n = 0;
			$.each(this.percentages, function(i, n) {
				t += n[0], e += n[0] * n[1]
			}), n = t ? e / t : 0, i.eq(0).text(Math.round(100 * n) + "%"), i.eq(1).css("width", Math.round(100 * n) + "%"), this.updateStatus()
		},
		getUploadState: function() {
			var e = 1,
				t = this.uploader.getStats(),
				i = t.successNum,
				n = t.queueNum;
			if (0 === i && 0 === n) e = 1;
			else if (n > 0) e = 2;
			else if (i > 0 && 0 === n) e = 3;
			return e
		},
		getUploadedFiles: function() {
			var e = [];
			return $.each(this.uploadedFiles, function(t, i) {
				e.push(i)
			}), e
		},
		getUploadedFileUrls: function() {
			var e = [];
			return $.each(this.uploadedFiles, function(t, i) {
				e.push(i.info.url)
			}), e
		},
		getUploadedFileObjs: function() {//文件url 和 文件名
			var e = [];
			return $.each(this.uploadedFiles, function(t, i) {
				e.push({
					 name: i.info.original,
	                 url: i.info.url
				})
			}), e
		},
		isInProgress: function() {
			return this.isInProgress()
		}
	};
	
	//暴露到外面
	$.fn.uploader = function(e){
		e = e || {}; e.main =  $(this).eq(0);
		var defaults = {
	        action: 'uploadfile',
	    	label : '文件'
		}
		e = $.extend(!0, {}, e, defaults);
		return new _uploader(e);
	};
	//上传图片
	$.fn.imageUploader = function(e) {
		e = e || {}; e.main =  $(this).eq(0);
	    var defaults = {
	        action: 'uploadimage',
	    	label : '图片',
    		accept: {
				title: "Images",
				extensions: "jpg,jpeg,png,gif,bmp",
				mimeTypes: "image/*"
		    }
	    }
	    e = $.extend(!0, {}, e, defaults);
		return new _uploader(e);
	}
})(jQuery);