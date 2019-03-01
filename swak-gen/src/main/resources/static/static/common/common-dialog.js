/**
 * 前后通用的弹框（慢慢整理）
 * 或其他一些组件
 */
var Public = Public || {};

/**
 * 使用layer代替
 */
Public.type = {
  WARNING: 0,
  SUCCESS:1,
  ERROR:2,
  QUESTION:3,
  DENIED:4,
  LOADING:16
};

/**
 * 弹出
 */
Public.alert = function(msg, type, time, end, shade) {
	if(type == 'success') {
		Public.success(msg, time, end, shade);
	} else if(type == 'error') {
		Public.error(msg, time, end, shade);
	}
}
/**
 * 无权的提示
 */
Public.denied = function(msg) {
	layer.msg(msg||'无权操作', {
		icon: Public.type.DENIED,
		shade:0.2
	});	 
};

/**
 * 提示信息
 */
Public.info = function(mess){
	layer.msg(mess||'提示', {
		icon: Public.type.DENIED,
		shade:0.2
	});	  
};

/**
 * 成功
 */
Public.success = function(msg, time, end, shade) {
	var type = typeof time === 'function';
	if(type) end = time;
	if(!end && !shade) {
		Public.toast(msg, time);
	} else {
		layer.msg(msg||'操作成功', {
			icon: Public.type.SUCCESS,
			shade:shade||0.2,
			shadeClose: !1,
			time:(!type?time||3:3) * 1000,
			closeBtn:1
		}, end); 
	}
};

/**
 * 错误
 */
Public.error = function(msg, time, end, shade) {
	var type = typeof time === 'function'
	if(type) end = time;
	layer.msg(msg||'操作失败', {
		icon: Public.type.ERROR,
		shade:shade||0.2,
		shadeClose: !0,
		time:(!type?time||3:3) * 1000,
		closeBtn:1
	}, end); 
};

/**
 * 提示
 */
Public.warning = function(msg) {
	layer.msg(msg||'请输入内容', {
		icon: Public.type.WARNING,
		shade:0.2
	});
};

/**
 * 加载中
 */
Public.loading = function(msg) {
	layer.msg(msg||'加载中...', {
		icon: Public.type.LOADING,
		shade:0.2,
		time:0
	});
};

/**
 * 小提示
 */
Public.toast = function(message, times) {
	$('.ui-toast-container').remove();
	var item = {sn : 'ui-' + Public.generateChars(10), message: message};
	var template = '<div class="ui-toast-container {{=item.sn}}"><div class="ui-toast-message">{{=item.message}}</div></div>';
	var html = Public.runTemplate(template, {item: item});
	$('body').append(html);
	setTimeout(function() {
		var _sn = item.sn;
		$('.' + _sn).remove();
	}, times || 2000);
};

/**
 * 输入一个内容
 */
Public.promptx = function(title, ok, value) {
	layer.prompt({
	    title: title||'请输入内容',
	    value: value||'',
	    formType: 3
	}, ok);
};

/**
 * 确认框
 */
Public.confirmx = function(msg, ok, cancel) {
	layer.confirm(msg||'系统提示？', {icon: 3}, function(index){
	    layer.close(index);
	    if( typeof(ok) === 'function' ) {
			ok();
		}
	}, function(index) {
		layer.close(index);
		if( typeof(cancel) === 'function' ) {
			cancel();
		}
	});
}

/**
 * 执行一个动作
 */
Public.executex = function(mess, url, param, ok, cancel) {
	Public.confirmx(mess, function(){
		Public.loading('正在提交，请稍等...');
		Public.postAjax(url, param, function(data){
    		if(typeof(data) == "string") {
          		 data = $.parseJSON(data);
          	}
    		Public.close();
    		ok(data);
        });
	}, cancel);
};

/**
 * 执行一个动作，不需要提示
 */
Public.executexQuietly = function(url, param, end, async) {
	Public.postAjax(url, param, function(data){
		if(typeof(data) == "string") {
      		 data = $.parseJSON(data);
      	}
		if( typeof(end) === 'function' ) {
			end(data);
		}
    }, async);
};

//删除
Public.deletex = function(mess, url, param, ok) {
	Public.executex(mess, url, param, ok, null);
};

/**
 * 小提示框
 */
Public.tips = function(html, follow, options){
	var defaults = {
	    tips: [1, '#78BA32']	
	};
	var options = $.extend({},defaults,options||{});
	layer.tips(html, follow, options);
};

/**
 * 关闭
 */
Public.close = function(index) {
	if(!!index) {
		layer.close(index);
	} else {
		layer.closeAll();
	}
};

/**
 * 结束进度条
 */
Public.loaded = function(delay) {
	Public.delayPerform(function(){
	   Public.close();
	}, delay||500);
};

/**
 * 关闭窗口
 */
Public.closeWindow = function(wId){
	Public.close(wId);
};

//loading div
Public.loadingInner = function(dom) {
   $(dom).addClass('loading-ui').append('<div class="loading-ui-wrap"><div class="loading-ui-mask"></div><div class="loading-ui-progress">数据加载中...</div></div>');
};

Public.loadedInner = function(dom) {
   $(dom).removeClass('loading-ui').find('.loading-ui-wrap').remove();
};

/**
 * 相册
 * {
 *   "alt":      //"图片名"
 *   "pid": 666, //图片id
 *   "src": "",  //原图地址
 *   "thumb": "" //缩略图地址
 * }
 */
Public.photos = function(title, data) {
   var _options = {
	   data : data ||[]
   };
   var defaults = {
	   id: Public.generateChars(10),
	   title: title|| '相册',
	   start: 0,
	   data:[{
		  alt:'图片1',
		  src:'',
		  thumb:''
	   }]
   };
   var options = $.extend({}, defaults, _options||{});
   layer.photos({
       photos: options
   });
};

/**
 * 消息（右下）
 */
Public.rbMessage = function(title, message, time) {
	Public.openWindow(title||'重要消息', '<div style="padding:10px; font-size:14px; color:#CC0403;">'+message+'</div>', 310, 180, null, null, null, null, {
		offset: 'rb',
		shade: 0,
		time : time ||0,
		skin: 'layui-layer-molv',
		shift:2
	});
};

/**
 * 打开一个对话框
 * 1. ok 为函数则，默认的有按钮： '确定', '取消'
 * 2. ok 为对象则，按照ok制定的对象来初始化弹框
 * 3. ok 为 null, cancel 为 函数，使用closeBtn: 2，并有关闭事件
 * 4. ok 为 null, cancel 为 1（false）则带有 关闭的按钮
 * 5. ok 为 null, cancel 为 2（true）则使用closeBtn: 2，没友关闭事件（可以指定end）
 */
Public.openWindow = function(title, content, width, height, ok, cancel, beging, end, options) {
	var defaults =  {
		type: (!!content && content.startWith("iframe:")?2:1),
	    title: (!!title?title:false),
	    area: [(width||300)+'px', (height||420)+'px'],
	    closeBtn: 1,
	    shift:5,
	    shade: 0.2,
	    shadeClose: false,
	    content: (!!content && content.startWith("iframe:")?(content.substr(7)):content||''),
	    success: (function(){
	    	if (typeof(beging) === 'function') {
	    		return beging;
	    	}
	    	return null;
	    })(),
	    end: (function(){
	    	if (typeof(end) === 'function') {
	    		return end;
	    	}
	    	return null;
	    })()
	};
	
	//按钮组
	var btns = function() {
		if( typeof(ok)==='function' ) {//设置了确定按钮，则取消按钮出现
			return {
				btn: ['确定', '取消'],
			    yes: function(index, layero){
			    	var returnFalg = true;
			    	if( typeof(ok) === 'function' ) {
			    		var iframe = {};
			    		if(!!content && content.startWith("iframe:")) {
			    			iframe = layero.find('iframe').get(0).contentWindow;
			    		}
			    		//执行函数
			    		returnFalg = ok(iframe, index);
			    		returnFalg = returnFalg == undefined?true:returnFalg;
					}
			    	//关闭窗口
			    	returnFalg && Public.close(index);
			    },
			    cancel: function(index, layero){
			    	if( typeof(cancel) === 'function' && layero) {
			    		var iframe = {};
			    		if(!!content && content.startWith("iframe:")) {
			    			iframe = layero.find('iframe').get(0).contentWindow;
			    		}
			    		return cancel(index, iframe);
					}
			    	return true;
			    }
			}
		} else if(!!ok && typeof(ok)==='object') { //自定义按钮
		   return ok;
		} else if(typeof(cancel) === 'function'){
		   return {
			    cancel: function(index, layero){
			    	if( layero ) {
			    		var iframe = {};
			    		if(!!content && content.startWith("iframe:")) {
			    			iframe = layero.find('iframe').get(0).contentWindow;
			    		}
				    	return cancel(index, iframe);
			    	}
			    	return cancel(index);
			    },
			    closeBtn: 2
		   }
		} else if(!(typeof(cancel)==='function')){
		   var num = cancel==null?1:(!!cancel ? 2: 0);
		   return {closeBtn: num}
		}
	}();
	var _options = $.extend({}, defaults, options || {});
	if(layer){
		return layer.open($.extend({}, _options, btns));
	} else {
		return top.layer.open($.extend({}, _options, btns));
	}
};

/**
 * 打开一个预览窗口(没有标题和按钮)
 * content -- 窗口的内容
 * cancel --- 关闭按钮
 * width,height -- 窗口大小
 */
Public.openViewWindow = function(content, width, height, cancel){
	return Public.openWindow(false, content, width, height, null, cancel);
};

/**
 * 打开一个对话框
 */
Public.openUrlWindow = function(title, url, width, height, ok, cancel, begin, end) {
	return Public.openWindow(title, "iframe:" + url, width, height, ok, cancel, begin, end);
};

/**
 *  弹出导入框,并执行导入 -- Dialog 模式
 *  content 内容
 *  form 内容中的表单
 *  url  提交的url
 *  title 标题
 */
Public.openImportDialog = function(content, form, url, title, success, error, end){
	Public.openWindow(title, content, 500, 230,{
		btn: ['导入', '关闭'],
		yes: function() {
			Public.doImport(form, url, success, error);
		}
	}, null, function() {
		Public.singleFile && Public.singleFile("input[type='file'].selectFile"),typeof(end) == 'function' && end();
	});
};

/**
 *  弹出导入框,并执行导入 --- window 模式
 *  content 内容
 *  form 内容中的表单
 *  url  提交的url
 *  title 标题
 */
Public.openImportWindow = function(content, form, url, title, success, error, end){
	// URL
	Public.openUrlWindow(title, content, 500, 230, {
		btn: ['导入', '关闭'],
		yes: function(index, layero) {
			var iframe = layero.find('iframe').get(0).contentWindow;
			
			// 子窗口的form对象
			var _form = iframe.$(form);
			
			// 在父窗口中执行
			Public.doImport(_form, url, success, error);
		}
	}, null, function(layero, index) {
		var iframe = layero.find('iframe').get(0).contentWindow;
		iframe.Public.singleFile && iframe.Public.singleFile("input[type='file'].selectFile"),typeof(end) == 'function' && end();
	});
};

/**
 * 打开一个对话框(无边框无标题无按钮)
 */
Public.openInnerUrlWindow = function(title, url, width, height, ok, cancel, begin, end) {
	return Public.openWindow(title, "iframe:" + url, width, height, ok, cancel, begin, end, {skin: 'layui-layer-inner'});
};

/**
 * 打开一个对话框(无边框无标题无按钮)
 */
Public.openInnerWindow = function(title, url, width, height, ok, cancel, begin, end) {
	return Public.openWindow(title, url, width, height, ok, cancel, begin, end, {skin: 'layui-layer-inner'});
};

/**
 * 读取cookie 的值
 */
Public.getCookie = function(name) {
	return $.cookie(name);
};

/**
 * 存储cookie 的值
 * expires(天)
 */
Public.setCookie = function(name, value, path, expires) {
	$.cookie(name, value, {path: path||'/', expires: expires||7});
};

/**
 * 删除cookie 的值
 * expires(天)
 */
Public.removeCookie = function(name, path) {
	$.cookie(name, null, {path: path||'/', expires: -1});
};

/**
 * 绑定 Navbar的选择事件
 * more : true 多选，false 单选
 */
Public.initNavbarSelectionEvent = function(more) {
	var _more = !!more; 
	//激活事件
	$(document).on('click.active', '[data-toggle="active"]', function(e) {
		e.preventDefault();
		var target  = e.target || e.srcElement;
		var $li = target.nodeName=='LI'?$(target):$(target).closest('li');
		if(!!$li.get(0)) {
			var hasActive = $li.hasClass('active');
			if( _more && hasActive) {
				$li.removeClass('active');
			} else if(_more && !hasActive) {
				$li.addClass('active');
			} else if(!_more && !hasActive) {
				$(this).children('li').removeClass('active');
				$li.addClass('active');
			}
			
			//得到选择的值
			var t = [];
			$(this).children('li.active').each(function() {
				t.push($(this).data('type'));
			});
			$('#' + $(this).data('target')).val(t.join(','));
			
			//删除错误提示
			$(this).closest('.form-group').children('label.error').remove();
		}
	});
	
	//初始化值
	$('[data-toggle="active"]').each(function() {
	  var that = this;
	  var type = $(this).data('type');
	  if(!!type) {
		  var types = type.split(',');
		  $.each(types, function(index, item) {
			$(that).children('[data-type="'+item+'"]').addClass('active');
		  });
	  }
	});
};

/**
 * 拖动排序
 */
Public.sortable = function(dom, options, sortFnc) {
	var _sortFnc = typeof(options) === 'function'? options: sortFnc;
	var _doSort = function($item, $list, sthis) {
		var _url = $list.data('sort-url');
		if (!!_url) {
			var postData = sthis.toArray();
			var ids = []; $.each(postData, function(index, item) {ids.push({ name: 'items.id', value: item});});
			Public.postAjax(_url, ids, function(data) {
				if(!!_sortFnc && typeof(_sortFnc) == 'function') {
					_sortFnc($item, $list, data.obj)
				}
			});
		} else if(!!_sortFnc && typeof(_sortFnc) == 'function') {
			_sortFnc($item, $list, sthis)
		}
	};
	var defaults = { 
		draggable: '.sortable-item',
		filter: '.disabled',
		onSort: function(evt) {
			_doSort($(evt.item), $(evt.item).closest('.sortable'), this);
		}
	};
	var _options = $.extend({}, defaults, options || {});
	return new Sortable($(dom).get(0), _options);
};

/**
 * ajax 加载的page 列表
 */
Public.ajaxPage = function(target, url, callback, options) {
	var self = this;
	var defaults =  {
	      state: 0,// 状态0：可运行，1运行中，2最大数据
	      pageIndex:1, // 当前页码
	      pageCount:-1,// 页面数
	      hasNext:function(){
			if(this.pageCount == -1 || this.pageCount >= this.pageIndex) {
				return true;
			}
			return false;
		  },
		  next: function(pageCount){
			this.pageCount = pageCount;
			this.pageIndex++;
		  },
		  showLoading : function() {
			$(target||'body').append('<div class="page-loader"><img src="'+ webRoot +'/static/img/loading.gif">数据加载中...</div>');
		  },
		  removeLoading: function() {
			$(target||'body').find('.page-loader').remove();
			$(target||'body').find('.page-null').remove();
		  }
	};
	
	this.loader = $.extend({}, defaults, options||{});
	
	//加载
	var loadPage = function() {
		var loader = self.loader;
		if( loader.state == 0 && loader.hasNext() ) {
			loader.showLoading();
			loader.state = 1;
			var param = [];
			    param.push({'name':'param.pageIndex', 'value':loader.pageIndex});
			//加载数据(需要能动态获取参数，通过指定form表单)
			Public.postAjax(url, param, function(data) {
				loader.removeLoading();
				loader.state = 0;
				callback(data);
			});
		}
	};
	
	//绑定事件
	$(target||document).on('scroll', function() {
		var scrollTop = parseInt($(this).scrollTop());
		var scrollHeight = parseInt($(this).get(0).scrollHeight);
		var windowHeight = parseInt($(this).height() + 20);
		if( scrollTop + windowHeight == scrollHeight){
		　  self.loadPage();
		}
	});
	
	//初始化
	loadPage();
};

/**
 * 滑动加载数据
 */
Public.scrollLoader = null;
Public.initScrollLoad = function(url, template, options) {
	var self = this;  
	var defaults = {
	    state: 0,// 状态0：可运行，1运行中，2最大数据
	    pageIndex:1, // 当前页码
	    pageSize: 15, // 每页数量
	    pageCount:-1,// 页面数
	    hasNext:function(){
			if(this.pageCount == -1 || this.pageCount >= this.pageIndex) {
				return true;
			}
			return false;
		},
		next: function(pageCount){
			this.pageCount = pageCount;
			this.pageIndex++;
		},
		showLoading : function() {
			if( !$('#ajax-load-page').find('.ajax-load-more-loding').get(0)) {
				$('#ajax-load-page').append('<div class="ajax-load-more-loding"><img alt="加载中..." src="'+webRoot+'/static/img/loading.gif"></div>');
			}
		},
		removeLoading: function() {
			$('#ajax-load-page .ajax-load-more-loding').remove();
		},
		showResults : function(data) {
			var _data = {datas:data.data, start: ((this.pageIndex - 1) * this.pageSize)};
			var html = Public.runTemplate(template, _data);
			$('#ajax-load-page .ajax-load-items').append(html);
			$('#ajax-load-page-count').text(data.param.recordCount);
			if(data.param.recordCount >=1) {
			   $('#ajax-load-page .ajax-load-null').remove();
			}
			
			//金额格式化
			$('[data-money]').each(function() {
			  $(this).html('¥' + $.formatFloat($(this).data('money'), 2));
		    });
		},
		reset : function() {
			this.state = 0;
			this.pageIndex = 1;
			this.pageSize = 15;
			this.pageCount = -1;
			$('#ajax-load-page .ajax-load-items').html('');
			$('#ajax-load-page').find('.ajax-load-nomore').remove();
			$('#ajax-load-page').find('.ajax-load-null').remove();
			this.load();
		},
		load : function() {
			var _loader = this;
			_loader.begin(function() {
			   var param = (function() {
					var $form = $('.ajax-load-form');
					if(!!$form.get(0)) {
						return $form.serializeArray();
					}
					return [];
			   })();
			   param.push({'name':'param.pageIndex', 'value':(_loader.pageIndex)});
			   param.push({'name':'param.pageSize', 'value':(_loader.pageSize)});
			   Public.postAjax(url, param, function(data){
				   _loader.end(data);
			   });
			});
		},
		begin : function(fnc) {
			var _loader = this;
			if( this.state == 0 && this.hasNext() ) {
				this.showLoading();
				this.state = 1;
				fnc();
			} else if( this.state == 0 ) {
				this.showLoading();
				setTimeout(function(){
					_loader.removeLoading();
					_loader.state = 1;
					//if( !$('#ajax-load-page').find('.ajax-load-nomore').get(0)) {
					//	$('#ajax-load-page').append('<div class="ajax-load-nomore">没有更多了...</div>')
					//}
				},300);
			}
		},
		end : function(data) {
			this.removeLoading();
			var pageCount = data.param.pageCount;
			this.showResults(data);
		    this.next(pageCount);
		    this.state = 0;
		}
	};
	
	//scrollLoad对象
	Public.scrollLoader = $.extend({}, defaults, options||{});
	
	//加载事件
	$(document).on('scroll', function() {
		var scrollTop = parseInt($(this).scrollTop());
		var scrollHeight = parseInt(document.body.scrollHeight);
		var windowHeight = parseInt($(window).outerHeight());
		if( scrollHeight - ( scrollTop + windowHeight) < 10 ){
		　 Public.scrollLoader.load();
		}
	});
	
	//加载
	Public.scrollLoader.load();
};

/**
 * 选择文本服务
 */
Public.initSelectText = function(target, fnc) {
	var buttons = ['<div id="reader-helper-el" style="z-index: 9001;display: block; position: absolute; width: auto; height: 49px; line-height: 32px; text-align: center; border-radius: 1px; cursor: pointer; z-index: 1000; right: auto!important; font-size: 14px;display: none;" data-on="0">',
	               '<div class="tips-wrap"> <span class="trangle"></span><div class="inner" style="box-shadow: 0 0 3px #e6e6e6; margin: 5px; background: #fff; border: 1px solid #c4c4c4; border-radius: 2px;">',
	               '<a href="javascript:void(0)" id="reader-search-name" style="text-decoration: none; color: #6ea084; padding: 0 10px;">标题搜索</a><span> | </span>',
	               '<a href="javascript:void(0)" id="reader-search-num" style="text-decoration: none; color: #6ea084; padding: 0 10px;">文号搜索</a><span> | </span>',
	               '<a href="javascript:void(0)" id="reader-search-content" style="text-decoration: none; color: #6ea084; padding: 0 10px;">内容搜索</a>',
	               '</div></div></div>'
	              ].join("");
	
	var an = $(buttons).appendTo($('body'));
	//文本选择事件
	$(document).on("mouseup", target, function(e){
		if (typeof e.button === "undefined" || e.button === 2) {
			return;
		}
		var _event = event;
		setTimeout(function() {
			var text = Public.getSelectText();
			if(!!text) {
				an.show();
				var left = _event.pageX;
				var top = _event.pageY;
				if( left + $('#reader-helper-el').width() > $(window).width()) {
					left = left - $('#reader-helper-el').width() + 20;
				}
				an.get(0).style.top = top + 10 + "px";
				an.get(0).style.left = left - 20 +"px";
				an.data('on','1');
			}
		},30);
	});
	
    //点击其他地关闭
    $(document).bind('click.menu',function(e){
    	var target  = e.target || e.srcElement;
    	$('#reader-helper-el').each(function(){
			var an = $(this);
			if( $(target).closest(an).length == 0 && an.data('on') == '1'){
				an.get(0).style.top = "-99999px";
				an.get(0).style.left = "-99999px";
				an.data('on','0');
				an.hide();
			};
		});
    	
    	var text = encodeURI(Public.getSelectText());
    	if( !!text && typeof(fnc) === 'function') {
    		fnc(target, text);
    	}
    });
};

/**
 * icheck 默认的实现
 */
Public.iCheck = function(doms, fnc){
	$(doms).iCheck({
	    checkboxClass: 'icheckbox_square-green',
	    radioClass: 'iradio_square-green'
	});
	if(typeof(fnc) === 'function') {
		$(doms).on('ifClicked', function(event){
			fnc(this, event);
		});
	}
};

/**
 * icheck 列表模式
 */
Public.iCheckList = function(doms, fnc) {
	//分为选择所有或选择普通
	Public.iCheck(doms, function(obj,e) {
		if( $(obj).hasClass('checkedAll') ) {
			var _checked = !$(obj).is(':checked');
			$(doms).each(function(index, item){
			  $(item).iCheck(_checked?'check':'uncheck');
			});
			if(typeof(fnc) === 'function') {
			   fnc(_checked, obj, e, true);
			}
		} else {
			var _checked = !$(obj).is(':checked');
			$(obj).iCheck(_checked?'check':'uncheck');
			if(typeof(fnc) === 'function') {
			   fnc(_checked, obj, e);
			}
			var _allChecked = true,  _checkAll = null;
			$(doms).each(function(index, item){
				if(!$(item).hasClass('checkedAll')) {
					if( _allChecked && !$(item).is(':checked') ) {
						_allChecked = false;
					}
				} else {
					_checkAll = item;
				}
			});
			if(_allChecked && _checkAll) {
			   $(_checkAll).iCheck('check');
			} else if(_checkAll) {
			   $(_checkAll).iCheck('uncheck');
			}
		}
	});
};

/**
 * 是否支持Canvas
 */
Public.isSupportCanvas = function() {
	var support = true;
	try{ document.createElement('canvas').getContext('2d');}catch(e){ support = false;}
	return support;
}

/**
 * html 保存图片并上传服务器
 */
Public.html2Image = function(dom, options) {
	var fnc = typeof(options) === 'function' ? options: null;
	var defaults = {
		onrendered: function(canvas) {
			var image = canvas.toDataURL();
			typeof(fnc) === 'function' && fnc(image);
		}	
	};
	var options = $.extend({}, defaults, options||{});
	var _dom = $(dom).get(0);
	_dom && html2canvas && html2canvas(_dom, options);
};