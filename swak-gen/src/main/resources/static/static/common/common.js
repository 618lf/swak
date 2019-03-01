/**
 * 公共函数库，主要是一些JS工具函数，各种插件的公共设置
 * 
 * @author HenryYan
 */
(function($) {
	
	// 添加浏览器兼容判断
	var e = $, g, h;
	e.uaMatch = function(e) {
		e = e.toLowerCase();
		var t = /(chrome)[ \/]([\w.]+)/.exec(e) || /(webkit)[ \/]([\w.]+)/.exec(e) || /(opera)(?:.*version|)[ \/]([\w.]+)/.exec(e) || /(msie) ([\w.]+)/.exec(e) || 0 > e.indexOf("compatible") && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(e) || [];
		return {
			browser: t[1] || "",
			version: t[2] || "0"
		}
	}
	e.browser || (g = e.uaMatch(navigator.userAgent), h = {}, g.browser && (h[g.browser] = !0, h.version = g.version), h.chrome ? h.webkit = !0 : h.webkit && (h.safari = !0), e.browser = h);
	
	// String startWith
	String.prototype.startWith=function(str){     
	  var reg=new RegExp("^"+str);     
	  return reg.test(this);        
	}; 
	
	// String endWith
	String.prototype.endWith=function(str){     
	  var reg=new RegExp(str+"$");     
	  return reg.test(this);        
	};
	// trim
	String.prototype.trim = function() {
	    return this.replace(/(^\s*)|(\s*$)/g,'');
	};
	/**
	 * 获取元素的outerHTML --- 包括html() 都会有一个问题 如果动态改变的值无法copy。需要用到 clone()；
	 */
    $.fn.outerHTML = function() {
        // IE, Chrome & Safari will comply with the non-standard outerHTML, all
		// others (FF) will have a fall-back for cloning
        return (!this.length) ? this : (this[0].outerHTML ||
        (function(el) {
            var div = document.createElement('div');
            div.appendChild(el.cloneNode(true));
            var contents = div.innerHTML;
            div = null;
            return contents;
        })(this[0]));
    };
    
    // 判断:当前元素是否是被筛选元素的子元素
    $.fn.isChildOf = function(b){
        return (this.parents(b).length > 0);
    };

    // 判断:当前元素是否是被筛选元素的子元素或者本身
    $.fn.isChildAndSelfOf = function(b){
        return (this.closest(b).length > 0);
    };
    
    // ajax 全局设置
    $(document).ajaxSend(function(event, jqXHR, ajaxOptions) {
    	jqXHR.setRequestHeader("token", Public.getCookie('token'));
    });
    
    // 普通的表格维护
    STCore = {
    	_clear : function(row) {
    		$(row).find('td').each(function(index, item){
    			if(!($(item).hasClass('options') || $(item).hasClass('index') || $(item).hasClass('options-cus'))) {
    				var hasContent = !!$(item).children().eq(0).get(0);
    				if(!hasContent) {
    					$(item).text('');
    				} else {
    					$(item).find('input:text').val('');
    					$(item).find('input:hidden').val('');
    					$(item).find('select').val('');
    					$(item).find('textarea').val('');
    					$(item).find('a').text('');
    				}
    			}
    			if($(item).hasClass('options') || $(item).hasClass('options-cus')) {
    				$(item).find('[data-id]').data('id','');
    			}
    		});
    		$(row).removeClass('template-row');
    	},
		_add : function(row, t){
			var _row = (typeof(row) === 'function' || !row)?this._template(t):row;
			var newRow = $(_row).clone();
			this._clear(newRow);
			if($(_row).find('.template-row').get(0)) {
				$(this._last(t)).after(newRow);
			} else {
				$(_row).after(newRow);
			}
			if(typeof(row) === 'function') {
			   row(newRow);
			}
			return newRow.show(), newRow;
		},
		_del : function(table,row){
			if(table.rows.length == 2
					&& !$(table).find('.template-row').get(0)){
				// 清空内容
				this._clear(row);
				return false;
			}
			table.deleteRow(row.rowIndex);
			return true;
		},
		_update : function(row,values){
			$.each(values,function(index,value){
				$(row).find("." + index).each(function(index,item){ 
					if( $(item).is(':radio') || $(item).is(':checkbox') || $(item).is(':input') ) {
						$(item).val(value);
					} else if($(item).data('type')== 'html' && value != null) {
						$(item).html(value);
					} else if(value != null) {
						$(item).text(value);
					}
				});
			});
		},
		_reset : function(table){
			for( var i= 1; i< table.rows.length;i++){
			   var row = table.rows[i];
			   for(var j = 0; j<row.cells.length; j++ ) {
				   var cell = row.cells[j]; 
				   if($(cell).hasClass('index')) {
					   cell.innerHTML = i;
				   }
			   }
			} 
		},
		_last : function(table){
			return table.rows[table.rows.length - 1];
		},
		_first : function(table){
			return table.rows[0];
		},
		_template : function(table) {
			var _row = $(table).find('.template-row').get(0);
			return _row|| this._last(table);
		}
	};
	$.fn.simpleTable = function(o, r, v){
		var returnValue = null;
		switch(o){
		  case 'add':returnValue = STCore._add(r, this.get(0));break;
		  case 'del':returnValue = STCore._del(this.get(0), r);break;
		  case 'update':returnValue = STCore._update(r, v);break;
		}
		STCore._reset(this.get(0));
		return returnValue;
	};
	
	// 添加金额格式化的支持
	$.extend({
	    formatFloat:function(src, pos){    
	        var num = parseFloat(src).toFixed(pos);    
	        num = num.toString().replace(/\$|\,/g,'');    
	        if(isNaN(num)) num = "0";    
	        sign = (num == (num = Math.abs(num)));    
	        num = Math.floor(num*100+0.50000000001);    
	        cents = num%100;    
	        num = Math.floor(num/100).toString();    
	        if(cents<10) cents = "0" + cents;    
	        for (var i = 0; i < Math.floor((num.length-(1+i))/3); i++)    
	        num = num.substring(0,num.length-(4*i+3))+','+num.substring(num.length-(4*i+3));    
	        return (((sign)?'':'-') + num + '.' + cents);    
	    },
	    formatDX : function(n) {
	    	var fraction = ['角', '分'];
	    	var digit = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
	    	var unit = [['元', '万', '亿'], ['', '拾', '佰', '仟']];
	    	var head = n < 0 ? '欠': '';
	    	n = Math.abs(n);
	    	var s = '';
	    	for (var i = 0; i < fraction.length; i++) {
	    	    s += (digit[Math.floor(n * 10 * Math.pow(10, i)) % 10] + fraction[i]).replace(/(零.)+/, '');
	    	}
	    	s = s || '整';
	    	n = Math.floor(n);
	    	for (var i = 0; i < unit[0].length && n > 0; i++) {
	    	    var p = '';
	    	    for (var j = 0; j < unit[1].length && n > 0; j++) {
	    	        p = digit[n % 10] + unit[1][j] + p;
	    	        n = Math.floor(n / 10);
	    	    }
	    	    s = p.replace(/(零.)*零$/, '').replace(/^$/, '零') + unit[0][i] + s;
	    	}
	    	return head + s.replace(/(零.)*零元/, '元').replace(/(零.)+/, '零').replace(/^整$/, '零元整');    
	    }
	});
	
	// input占位符
	$.fn.placeholder = function(){
		this.each(function() {
			$(this).focus(function(){
				if($.trim(this.value) == this.defaultValue){
					this.value = '';
				}
				$(this).removeClass('ui-input-ph');
			}).blur(function(){
				var val = $.trim(this.value);
				if(val == '' || val == this.defaultValue){
					$(this).addClass('ui-input-ph');
				}
				val == '' && $(this).val(this.defaultValue);
			});
		});
	};
	
	// 单选框插件
	$.fn.cssRadio = function(opts){
		var opts = $.extend({}, opts);
		var $_radio = $('label.radio', this), $_this = this;
		$_radio.each(function() {
			var self = $(this);
			if (self.find("input")[0].checked) {
				self.addClass("checked");
			};

		}).hover(function() {
			$(this).addClass("over");
		}, function() {
			$(this).removeClass("over");
		}).click(function(event) {
			$_radio.find("input").removeAttr("checked");
			$_radio.removeClass("checked");
			$(this).find("input").attr("checked", "checked");
			$(this).addClass("checked");
			opts.callback($(this));
		});
		return {
			getValue: function() {
				return $_radio.find("input[checked]").val();
			},
			setValue: function(index) {
				return $_radio.eq(index).click();
			}
		}
	};
	
	// 复选框插件
	$.fn.cssCheckbox = function() {
		var $_chk = $(".chk", this);
		$_chk.each(function() {
			if ($(this).find("input")[0].checked) {
				$(this).addClass("checked");
			};
			if ($(this).find("input")[0].disabled) {
				$(this).addClass("dis_check");
			};
		}).hover(function() {
			$(this).addClass("over")
		}, function() {
			$(this).removeClass("over")
		}).click(function(event) {
			if ($(this).find("input")[0].disabled) {
				return;
			};
			$(this).toggleClass("checked");
			$(this).find("input")[0].checked = !$(this).find("input")[0].checked;
			event.preventDefault();
		});
		
		return {
			chkAll:function(){
				$_chk.addClass("checked");
				$_chk.find("input").attr("checked","checked");
			},	
			chkNot:function(){
				$_chk.removeClass("checked");
				$_chk.find("input").removeAttr("checked");
			},
			chkVal:function(){
				var val = [];
				$_chk.find("input:checked").each(function() {
	            	val.push($(this).val());
	        	});
				return val;
			}
		}
	};
	
	// 文本框字符输入
	$.fn.inputLimit = function() {
		return this.each(function() {
			
			// 初始化参数
			var _area = $(this); var _wrap = $(_area).closest('.iInputLimit-wrap');
			var _info = _wrap.find('.word-num'); var _tip = _wrap.find('.-num');
			var _max = parseInt(_wrap.find('.-total').text());
			
			// 校验函数
			var _check = function(obj) {
				var _val = $(obj).val();
			    var _length = !!_val?_val.length:0;
			    if(_length > _max) {
			       !_wrap.hasClass('iInputLimit-error') && _wrap.addClass('iInputLimit-error');
			    } else {
			       _wrap.hasClass('iInputLimit-error') && _wrap.removeClass('iInputLimit-error');
			    }
			    _tip.text(_length);
			};
			
			// 初始化校验
			_check(_area);
			
			// 事件绑定
			_area.bind("input propertychange", function () {
				_check(this);
			});
		});
	 };
})(jQuery);

// 基础异常
function BaseException() {}
BaseException.prototype = new Error();  
BaseException.prototype.constructor = BaseException;  
BaseException.prototype.toString = function () {  
    return "[" + this.code + "]" + this.message;  
};

// 无用户异常
function NoUserException() {
	this.code = 40005;
	this.name = "NoUserException";
    this.message = "访问受限,请登录";  
};
NoUserException.prototype = new BaseException();  
NoUserException.prototype.constructor = NoUserException;

// 错误页面
var Error =  {
	dialog : null
};
Error.out = function(msg){
	if (Error.dialog == null || !$('#_error_out').get(0)) {
		Error.dialog = Public.openWindow('系统错误', '<div id="_error_out"><div class="_out">' + msg + '</div></div>', 800, 500, null, function() {
			return true;
		}, null, function(){
			Error.dialog = null;
	    });
	} else {
		$('#_error_out').append('<div class="_out">' + msg + '</div>');
	}
};
/**
 * 工具类
 */
var Public = Public || {};
Public.isIE6 = !window.XMLHttpRequest;

// 快捷键
Public.keyCode = {
	ALT: 18,
	BACKSPACE: 8,
	CAPS_LOCK: 20,
	COMMA: 188,
	COMMAND: 91,
	COMMAND_LEFT: 91, // COMMAND
	COMMAND_RIGHT: 93,
	CONTROL: 17,
	DELETE: 46,
	DOWN: 40,
	END: 35,
	ENTER: 13,
	ESCAPE: 27,
	HOME: 36,
	INSERT: 45,
	LEFT: 37,
	MENU: 93, // COMMAND_RIGHT
	NUMPAD_ADD: 107,
	NUMPAD_DECIMAL: 110,
	NUMPAD_DIVIDE: 111,
	NUMPAD_ENTER: 108,
	NUMPAD_MULTIPLY: 106,
	NUMPAD_SUBTRACT: 109,
	PAGE_DOWN: 34,
	PAGE_UP: 33,
	PERIOD: 190,
	RIGHT: 39,
	SHIFT: 16,
	SPACE: 32,
	TAB: 9,
	UP: 38,
	F7: 118,
	F12: 123,
	S: 83,
	WINDOWS: 91 // COMMAND
};

/**
 * 得到默认的页面
 */
Public.getDefaultPage = function(){
	return window.top;
};

/**
 * 节点赋100%高度
 * 
 * @param {object}
 *            obj 赋高的对象
 */
Public.setAutoHeight = function(obj){
  if(!obj || obj.length < 1){
	return ;
  }
  Public._setAutoHeight(obj);
	$(window).bind('resize', function(){
		Public._setAutoHeight(obj);
  });
};
Public._setAutoHeight = function(obj){
	obj = $(obj);
	var winH = $(window).height();
	var h = winH - obj.offset().top - (obj.outerHeight() - obj.height());
	obj.height(h);
};

// 异步提交时如果提交错误,判断是否是没有用户
Public.isUserLogin = function(data) {
	var _user = {}; if (top.User) {_user = top.User; } else {_user = User}
	return !!_user.assertLogin?_user.assertLogin(data) : true;
};

// Ajax请求，
// url:请求地址， params：传递的参数[{name,value}]， success：请求成功回调
Public.postAjax = function(url, params, success, async){    
	var _async = async== undefined?true:!!async;
	$.ajax({  
	   type: "POST",
	   url: url,  
	   cache: false,  
	   async: _async,
	   dataType: "json",  
	   data: params,  
	   // 当异步请求成功时调用
	   success: function(data, status){  
		   var r = Public.isUserLogin(data);
		   if (r) {
			   success(data);   
		   }
	   },  
	   // 当请求出现错误时调用 只要状态码不是200 都会执行这个
	   error: function(x, s, e){
		    if (!!x.responseText) {
		    	var msg = $.parseJSON(x.responseText).msg;
				Error.out(msg);
		    }
	   }  
	});  
};

// Ajax请求，
// url:请求地址， params：传递的参数{...}， callback：请求成功回调
Public.getAjax = function(url, params, success, async){
	var _async = async==undefined?true:!!async;
	$.ajax({  
	   type: "GET",
	   url: url,  
	   cache: false,  
	   async: _async,
	   dataType: "json",
	   data: params,  
	   // 当异步请求成功时调用
	   success: function(data, status){  
		   var r = Public.isUserLogin(data);
		   if (r) {
			   success(data); 
		   }
	   },  
	   // 当请求出现错误时调用 只要状态码不是200 都会执行这个
	   error: function(x, s, e){
		   if (!!x.responseText) {
	    	   var msg = $.parseJSON(x.responseText).msg;
			   Error.out(msg);
		   }
	   }  
	});  
};

// 同步执行表单, form 表单提交
Public.ajaxSubmit = function(form, url, check, success, async){
	var _async = async== undefined?true:!!async;
	$(form).ajaxSubmit({
		url: url,
		dataType:"json",
		beforeSubmit : check,  
		async: _async,
		success: function(data){
			Public.closeWindow();
			if (typeof(data) == 'string' ) {
				data = $.parseJSON(data);
			}
			if (success) {
				success(data);
			}
		},
		error : function(x){
			Public.loaded();
			var msg = $.parseJSON(x.responseText).msg;
			Error.out(msg);
		}
	});
};
// 列表添加删除
Public.billsOper = function(val, opt, row, add) {
	var text =  "<i class='iconfont icon-edit edit' data-id='"+row.id+"' title='编辑'></i>";
		if(!!add) {
		   text += "<i class='iconfont icon-plus add'  data-id='"+row.id+"' title='添加子节点'></i>";
		}
	    text += "<i class='iconfont icon-remove delete' data-id='"+row.id+"' title='删除'></i>";
	return text;
};
// 金额格式化 -- 还原
Public.rmoneysFmt = function(val, opt, row) {
	return parseFloat(val.replace(/[^\d\.-]/g, ""));
};
// 金额格式化
Public.moneysFmt = function(val, opt, row) {
	return '￥' + $.formatFloat(val, 2);
};
// 列表金额格式化
Public.yesNoFmt = function(val, opt, row) {
	return val == '1'?'是':'否';
};
// 图片
Public.imagesFmt = function(val, opt, row) {
	return '<img src="'+val+'"/>';
};
//
Public.setGrid = function(adjustH, adjustW){
	var adjust = adjustH || 64; 
	var gridW = $("#dataGrid").width() - adjustW || 2, gridH = $(window).height() - $("#dataGrid").offset().top - adjust;
	return Public.initMemoryQc(), {
		w : gridW,
		h : gridH
	};
};

// 重设表格宽高
Public.resizeGrid = function(adjustH, adjustW){
	var grid = $("#grid");
	if (grid.get(0)) {
		Public.delayPerform(function(){
			var gridWH = Public.setGrid(adjustH, 16);
			grid.jqGrid('setGridHeight', gridWH.h);
			// grid.jqGrid('setGridWidth', gridWH.w, true);
		},100).done(function(){
			var gridWH = Public.setGrid(adjustH, 16);
			grid.jqGrid('setGridHeight', gridWH.h);
			// grid.jqGrid('setGridWidth', gridWH.w, true);
		});
	}
};

// 默认的表格
Public.defaultGrid = function(params){
	var options  = params||{};
	if( !options.form && !$('#queryForm') ) {
		alert("请设置form");
	}
	var formObj = options.form || $('#queryForm');
	var defaults = {
			datatype: "json",// xml，local，json，jsonnp，script，xmlstring，jsonstring，clientside
			mtype:'POST',// POST或者GET，默认GET
			rowNum:100,
			rowList:[15,25,50,100],
			viewrecords:true,// 定义是否显示总记录数
			autoencode:true,// 对url进行编码
			autowidth:true,// 如果为ture时，则当表格在首次被创建时会根据父元素比例重新调整表格宽度。如果父元素宽度改变，为了使表格宽度能够自动调整则需要实现函数：setGridWidth
			loadtext:'数据加载中...',// 当请求或者排序时所显示的文字内容
			multiselect:true,// 定义是否可以多选（复选框）
			multiboxonly: true,
			altRows: true,
			gridview: true,
			rownumbers: !1,// 序号
			cellEdit: !1,// 是否可以编辑
			pager:"#page",
			page:1,// 设置初始的页码
			pagerpos:'left',// 指定分页栏的位置
			recordpos:'right',// 定义了记录信息的位置： left, center, right
			recordtext:'当前显示{0} - {1} 条记录   共 {2} 条记录',// 显示记录数信息。{0}
														// 为记录数开始，{1}为记录数结束。
														// viewrecords为ture时才能起效，且总记录数大于0时才会显示此信息
			shrinkToFit:true,// 此属性用来说明当初始化列宽度时候的计算类型，如果为ture，则按比例初始化列宽度。如果为false，则列宽度使用colModel指定的宽度
			jsonReader : { 
			      root: "data",   
			      page: "param.pageIndex",   
			      total: "param.pageCount",   
			      records: "param.recordCount",   
			      repeatitems: false   
		    },
		    prmNames:{
		    	page:"param.pageIndex",
		    	rows:'param.pageSize',
		    	sort: 'param.sortField',
		    	order: 'param.sortType',
		    	search:'search',
		    	nd:'nd',
		    	npage:null
		    },
		    loadError:function(xhr,status,error){
		    	var msg = $.parseJSON(x.responseText).msg;
				Error.out(msg);
		    },
		    beforeRequest:function(){
		    	var that = $(this);// treeGrid
		    	// var _p = formObj.data('init-page'), _r =
				// formObj.data('init-rows');
		    	// if(!!_p && !!_r) {that.jqGrid('setGridParam', {page:_p, rows:
				// _r});}
		    	that.jqGrid('setGridParam',{postData:(function(form){
		    		var obj = {'param.serializePage':'false'};
		    		$.each(form.serializeArray(),function(index,item){
		    			if(!(item.name in obj)){  
		    	            obj[item.name]=item.value;  
		    	        }  
		    		});
		    		return obj;
		    	})($(formObj))});
		    },
		    loadComplete: function(data) {
		    	Public.isUserLogin(data);
		    },// 数据加载后执行 -- data 直接是返回的数据
		    gridComplete: null // 表格序列化后执行
	};
	return $.extend({},defaults,options);
};
// 可编辑的表格
Public.defaultEditGrid = function(params){
	var options  = params||{};
	var defaults = {
			datatype: "clientSide",
			height: "100%",
            rownumbers: !0,
            gridview: !0,
            onselectrow: !1,
            cmTemplate: { sortable: !1, title: !1},
            forceFit: !0,
            rowNum: 1e3,
            cellEdit: !1,
            cellsubmit: "clientArray",
            localReader: {
                root: "rows",
                records: "records",
                repeatitems: !1,
                id: "id"
            },
            jsonReader: {
                root: "data.entries",
                records: "records",
                repeatitems: !1,
                id: "id"
            },
            loadonce: !0,
            footerrow: !1,
            userDataOnFooter: !0,
            userData: {},
            loadComplete: function (t) {},
            gridComplete: function () {},
            afterEditCell: function (t, e, i, a) {},
            formatCell: function () {},
            beforeSubmitCell: function () {},
            afterSaveCell: function (t, i, a, r, n) {},
            loadError: function (t, e) {}
	};
	return $.extend({},defaults,options);
};

// 树型表格 -- 不支持冻结列
// 第一列必须是 expandColumn 指定的列
Public.treeGrid = function(params){
	var options  = params||{};
	var defaults = {
		treeGrid: true,
        treeGridModel: 'adjacency'
	};
	return Public.defaultGrid($.extend({},defaults,options));
};

/**
 * 单选
 */
Public.selectedRowId = function(grid){
	return $(grid||'#grid').getGridParam('selrow');
};

/**
 * 多选
 */
Public.selectedRowIds = function(grid){
	var param = [];
	var checkeds = $(grid||'#grid').getGridParam('selarrrow');
	if( !!checkeds && checkeds.length ) {
		if(typeof(checkeds) === 'object'){
			$.each(checkeds,function(index,item){
				var id = $(grid||'#grid').getRowData(item).id;
				param.push({name:'idList',value:id});
			});
		} else {
			param.push({name:'idList',value:checkeds});
		}
	}
	return param;
};

// 获取表格选中的Name值
Public.selectedRowNames = function(grid){
	var param = [];
	var checkeds = $(grid||'#grid').getGridParam('selarrrow');
	if( !!checkeds && checkeds.length ) {
		if(typeof(checkeds) === 'object'){
			$.each(checkeds,function(index,item){
				var id = $(grid||'#grid').getRowData(item).name;
				param.push({name:'idList',value:id});
			});
		} else {
			param.push({name:'idList',value:checkeds});
		}
	}
	return param;
};

// 获取表格选中的XX值
Public.selectedRowValues = function(grid, cel){
	var param = [];
	var checkeds = $(grid||'#grid').getGridParam('selarrrow');
	if( !!checkeds && checkeds.length ) {
		if(typeof(checkeds) === 'object'){
			$.each(checkeds,function(index,item){
				var id = $(grid||'#grid').getRowData(item)[cel];
				param.push({name:'idList',value:id});
			});
		} else {
			param.push({name:'idList',value:checkeds});
		}
	}
	return param;
};

// 回车事件
Public.bindEnterDo = function(obj, func){
	var args = arguments;
	$(obj).on('keydown', 'input[type="text"]:visible:not(:disabled)', function(e){
		if (e.keyCode == '13') {
			if (typeof func == 'function') {
				var _args = Array.prototype.slice.call(args, 2 );
				func.apply(e, _args);
			}
		}
	});
};
// 初始化公用事件
Public.initPublicEvent = function(){
	// widget 事件
	$(document).on('click.tmt.widget','[data-action]',function(e){
		e.preventDefault();
		var n = $(this);
		var p = n.data("action");
		var b = n.closest(".widget-box");
		if (p == "collapse") { // 下拉事件
			var j = b.hasClass("collapsed") ? "show" : "hide";
			var f = j == "show" ? "shown" : "hidden";
			var c;
			b.trigger(c = $.Event(j + ".tmt.widget"));
			if (c.isDefaultPrevented()) {
				return
			}
			var g = b.find(".widget-body");
			var m = n.find("[class*=fa-]").eq(0);
			var h = m.attr("class").match(/fa\-(.*)\-(up|down)/);
			var d = "fa-" + h[1] + "-down";
			var i = "fa-" + h[1] + "-up";
			var l = g.find(".widget-body-inner");
			if (l.length == 0) {
				g = g.wrapInner( '<div class="widget-body-inner"></div>').find(":first-child").eq(0);
			} else {
				g = l.eq(0);
			}
			var e = 300;
			var k = 200;
			if (j == "show") {
				if (m) {
					m.addClass(i).removeClass(d);
				}
				b.removeClass("collapsed");
				g.slideUp(0, function() {
					g.slideDown(e, function() {
						b.trigger(c = $.Event(f + ".tmt.widget"));
					});
				});
			} else {
				if (m) {
					m.addClass(d).removeClass(i);
				}
				g.slideUp(k, function() {
					b.addClass("collapsed");
					b.trigger(c = $.Event(f + ".tmt.widget"));
				});
			}
		}
	});
	
	// tab 切换 -- 取消默认的事件
	$(document).on('click.tmt.widget','[data-toggle]',function(e){
		e.preventDefault();
		var n = $(this);
		var p = n.data("toggle"), selector = n.data('target') , previous  , $target , e;
		// var b = n.closest(".widget-box");
		if (p == "tab") { // tab 轮询事件
			 if (!selector) {
		        selector = n.attr('href');
		        selector = selector && selector.replace(/.*(?=#[^\s]*$)/, ''); // strip
																				// for
																				// ie7
	         }
			 if ( n.parent('li').hasClass('active') ) return;
			 var $ul = n.closest('ul.nav-tabs:not(.dropdown-menu)');
			 previous = $ul.find('.active:last a')[0];
			 e = $.Event('show', {
		        relatedTarget: previous
		     });
		     n.trigger(e);
		     if (e.isDefaultPrevented()) return;
		     $target = $(selector);
		     var activate = function(element, container, callback){
		    	 $active = container.find('> .active');
		    	 transition = callback && $.support.transition && $active.hasClass('fade');
			     var next = function(){
			    	 $active.removeClass('active');
				     element.addClass('active');
				     
				     if (transition) {
				         element[0].offsetWidth; // reflow for transition
				         element.addClass('in');
			         } else {
			             element.removeClass('fade');
			         }
				     callback && callback();
			     };
			     transition ? $active.one($.support.transition.end, next) : next();
			     $active.removeClass('in');
		     };
		     activate(n.parent('li'), $ul);
		     activate($target, $target.parent(), function () {
		         n.trigger({  type: 'shown' , relatedTarget: previous });
		     });
		}
	});
	
	// setting-box 事件
	$(document).on('click.tmt.widget','.settings-btn',function(e){
		$(this).toggleClass("open");
		$(this).closest('.settings-container').find('.settings-box').toggleClass("open");
	});
};
// 初始化查询组键事件
Public.initBtnMenu = function(){
	// 菜单按钮
	$(document).on('click.tmt.menu-btn','.ui-btn-menu .ui-menu-btn',function(e){
		if($(this).hasClass("ui-btn-dis")) {
			return false;
		}
		$(this).parent().toggleClass('ui-btn-menu-cur');
		$(this).blur();
		e.preventDefault();
	});
	// 组合按钮
	$(document).on('click.tmt.group-btn','.ui-btn-menu .ui-menu-btn',function(e){
		if($(this).hasClass("ui-btn-dis")) {
			return false;
		}
		$(this).parent().toggleClass('open');
		$(this).blur();
		e.preventDefault();
	});
	// 其他按钮
	$(document).bind('click.menu',function(e){
		var target  = e.target || e.srcElement;
		// 下面两个是点击页面其他地方关闭下拉菜单
		$('.ui-btn-menu').each(function(){
			var menu = $(this);
			if($(target).closest(menu).length == 0 && $('.dropdown-menu',menu).is(':visible')){
				 menu.removeClass('ui-btn-menu-cur');
			};
		});
		$('.ui-btn-group').each(function(){
			var menu = $(this);
			if($(target).closest(menu).length == 0 && $('.dropdown-menu',menu).is(':visible')){
				 menu.removeClass('open');
			};
		});
		// 查询事件
		if ($(target).hasClass("query")) {
			$(target).closest(".ui-btn-menu").removeClass('ui-btn-menu-cur');
			Public.doQuery();
		}
		// 记忆查询事件
		if ($(target).hasClass("mquery")) {
			$(target).closest(".ui-btn-menu").removeClass('ui-btn-menu-cur');
			Public.doMemoryQuery();
		}
		// 重置事件
		if ($(target).hasClass("reset")) {
			Public.resetQuery();
		}
		// 更多条件
		if ($(target).attr('id') == 'conditions-trigger') {
			  e.preventDefault();
			  if (!$(target).hasClass('conditions-expand')) {
					$('#more-conditions').stop().slideDown(200, function(){
					   $('#conditions-trigger').addClass('conditions-expand').html('收起更多<b></b>');
					   $('#filter-reset').css('display', 'inline');
				 	 });
			  } else {
				  	$('#more-conditions').stop().slideUp(200, function(){
					  $('#conditions-trigger').removeClass('conditions-expand').html('更多条件<b></b>');
					  $('#filter-reset').css('display', 'none');
				  	});
			  };	
		}
	});
	// 默认的回车事件
	Public.bindEnterDo('#queryForm', function(e) {
		Public.doQuery();
	});
	// iSelect 美化
	$('.iSelect').each(function(index, item) {
		Public.combo(item);
    });
	// jgrid 的 jBox引发窗口变化问题
	$(window).resize(function(){
		Public.resizeGrid();
	});
};
// 初始化记忆查询条件（请在grid初始化前调用）
Public.initMemoryQc = function() {
	// cookie记忆
	var _cookieFnc = function() {
		var _cookie = JSON.parse(Public.getCookie('_rc'));
		var formObj = $('#queryForm');
		if(!!_cookie) {
		   $.each(_cookie, function(index, item) {
			  // if('page' === item.name || 'rows' === item.name) {
			  // $(formObj).data('init-' + item.name, item.value);
			  // } else {
				   $(formObj).find('[name="'+item.name+'"]').val(item.value).data('name', item.dname);
			  // }
		   });
		}
	};
	_cookieFnc();
};

/**
 * 注册 tags 的事件
 */
Public.initTagsEvent = function() {
	
	// 图标选择
	$(document).on('click', '.iconselect', function() {
		var $wrap = $(this).closest('.iconselect-wrap');
		var val = $wrap.find('input').val();
		Public.openWindow("选择图标", "iframe:/admin/system/tag/iconselect?value=" + val, 750, $(document).height() - 280, {
			btn: ['确定', "关闭", "清除"],
			yes: function(index, layero) {
				var icon = layero.find("iframe")[0].contentWindow.$("#icon").val();
				$wrap.find('i').attr("class", icon);
				$wrap.find('label').text(icon);
				$wrap.find('input').val(icon);
	            Public.close(index);
			},
			cancel: function(){},
			btn3: function(index, layero) {
	            $wrap.find('i').attr("class", "");
				$wrap.find('label').text("无");
				$wrap.find('input').val("");
	            Public.close(index);
			}
		});
	});
	
	// 树形选择
    $(document).on('click', '.treeselect', function() {
		var disabled = $(this).hasClass('disabled');
		if (disabled) {
			return;
		}
		var $t = $(this);
		var $wrap = $(this).closest('.treeselect-wrap');
		var _title = $(this).data('title');
		var _url = $(this).data('url');
		var _checked = $(this).data('checked');
		var _extId = $(this).data('ext');
		var _rootS = $(this).data('roots');
		var _parentS = $(this).data('parents');
		var _clear = $(this).data('clear');
		var _fnc = $(this).data('fnc');
		var _selectIds = $wrap.find('.treeselect-ids').val();
		var _fnc = $(this).data('fnc'); var fnc = null; !!_fnc & (fnc = eval(_fnc));
		Public.treeSelect(_url, _title, 300, 420, function(iframe, ids, names) {
			$wrap.find('.treeselect-ids').val(ids);
			$wrap.find('.treeselect-names').val(names);
			if (typeof(fnc) === 'function') {
				fnc($t, ids, names);
			}
			return true;
		}, !!_clear ? (function() {
			$wrap.find('.treeselect-ids').val('');
			$wrap.find('.treeselect-names').val('');
			if (typeof(fnc) === 'function') {
				fnc($t);
			}
			return true;
		}) : false, true, _extId, _selectIds, _checked, !_rootS, !_parentS);
	});
    
    // 表格选择
    $(document).on('click', '.tableselect', function() {
		var disabled = $(this).hasClass('disabled');
		if (disabled) {
			return;
		}
		var $t = $(this);
		var $wrap = $(this).closest('.tableselect-wrap');
		var _title = $(this).data('title');
		var _url = $(this).data('url');
		var _clear = $(this).data('clear');
		var _fnc = $(this).data('fnc'); var fnc = null; !!_fnc & (fnc = eval(_fnc));
		Public.tableSelect(_url, _title, 650, 460, function(iframe, ids, names){
			$wrap.find('.tableselect-ids').val(ids);
			$wrap.find('.tableselect-names').val(names);
			if (typeof(fnc) === 'function') {
				fnc($t, ids, names);
			}
		}, !!_clear ? (function() {
			$wrap.find('.tableselect-ids').val('');
			$wrap.find('.tableselect-names').val('');
			if (typeof(fnc) === 'function') {
				fnc($t);
			}
			return true;
		}) : false, null);
	});
    
    // 附件选择
    $('.attachment-wrap').each(function() {
    	var $wrap = $(this); var multi = $wrap.data('multi');
    	var $target = $wrap.find('input'); var $preview = $wrap.find('.preview'); var readonly = !!$wrap.data('readonly');
    	
    	// 多选
    	var multiSel = function(files) {
    		var images = [];
    		
    		// 新加的
    		if (!!files) {
    			for(var i =0;i < files.length; i++) {
    				images.push({
    					src : files[i].src
    				});
     		    }
    		}
    		
    		// 已有的
    		$preview.find('img').each(function(n, e) {
    			images.push({
					src : $(e).attr('src'),
					href : $(e).attr('href'),
				});
    		});
 		    $target.val(JSON.stringify(images));
    	};
    	
    	// 预览
    	// [{id:'', src:'', title:'', href:''}]
    	var preview = function() {
    		var _images = $target.val();
    		var li, images = multi && !!_images ? jQuery.parseJSON(_images): [{src : _images, href: ''}]; $preview.html('');
    		for(var i=0; i<images.length; i++){
    			var _u = images[i];
    			if(!_u.src) {continue;}
    			li = '<li><img src="'+_u.src+'" href="'+(_u.href||'')+'">';
    			if (!!readonly) {
    				li += '</li>';
    			} else {
    				li += '<a href="javascript:" class="-del">×</a><a href="javascript:" class="-sets">e</a></li>';
    			}
    			$preview.append(li);
    		}
    		if ($preview.text() == ""){
    			$preview.html("<li style='list-style:none;padding-top:5px;'>无</li>");
    		}
    	};
    	
    	// 默认初始化
    	preview();
    	
    	// 注册事件
    	$wrap.on('click', 'a', function() {
    		if ($(this).hasClass('-select')) {
        		Attachment.selectAttachments(function(files) {
        			if (!!files && files.length != 0) {
        				if (!!multi) {
        					multiSel(files);
        				} else {
        					$target.val(files[0].src);
        				}
        		    	preview();
        			}
        		});
        	} else if($(this).hasClass('-clear')) {
        		$target.val("");
    	    	preview();
        	} else if($(this).hasClass('-sets') && !!multi) {
        		var $img = $(this).parent().find('img').eq(0); var href = !!$img.attr('href')?$img.attr('href'):'';
        		Public.openWindow('设置链接', '<div style="padding:20px;">链接地址：<input class="_attachment-image_href" type="text" value="'+href+'"style="margin: 0; width: 220px;"></div>', 400, 180, function() {
        			$img.attr('href', $('._attachment-image_href').val());
        			multiSel(); 
        		})
        	} else if($(this).hasClass('-del')) {
        		$(this).prev().remove();
        		if (!!multi) {
        			multiSel();
        		} else {
        			$target.val('');
        		}
    	    	preview();
        	}
    	});
    });
    
    // 多选
    $('.tags-treeselect').each(function(n, e) {
        var defaultText = $(e).data('defaultText') || 'add Tag...';
        var $wrap = $(e).closest('.tags-wrap'); var $t = $(e); var $tn = $wrap.find('.treeselect-names');
    	$(e).tagsInput({interactive:false,unique:false, width: '90%', defaultText:defaultText, searcherF:function(){
    		var _title = $(e).data('title');
    		var _url = $(e).data('url');
    		var _checked = $(e).data('checked');
    		var _extId = $(e).data('ext');
    		var _rootS = $(e).data('roots');
    		var _parentS = $(e).data('parents');
    		var _clear = $(e).data('clear');
    		var _selectIds = $wrap.find('.treeselect-ids').val();
    		Public.treeSelect(_url, _title, 300, 420, function(iframe, ids, names) {
    			var id = $t.attr('id');
    			$('#'+id+'_tagsinput .tag').remove();
    			$t.val(ids).attr('data-tags', names);
    			$.fn.tagsInput.importTags($t);
    			$tn.val(names);
    			return true;
    		}, !!_clear ? (function() {
    			var id = $t.attr('id');
    			$('#'+id+'_tagsinput .tag').remove();
    			$t.val('').attr('data-tags', '');
    			$tn.val('');
    			return true;
    		}) : false, true, _extId, _selectIds, _checked, !_rootS, !_parentS);
    	}});
    });
    
    // 区域选择
    $('.areaselect').each(function(n, e) {
    	var $t = $(e).closest('.areaselect-wrap').find('.areaselect-name');
    	$(e).citypicker({
    		fnc : function(val, address) {
    			$t.val(address);
    		}
    	});
    });
    
    // 表格
    $('.datatable-wrap').each(function(n, e) {
    	$(e).DataTable();
    });
};

// 打开一个窗体
Public.windowOpen = function(url, name, width, height){
	var top=parseInt((window.screen.height-height)/2,10),left=parseInt((window.screen.width-width)/2,10),
		options="location=no,menubar=no,toolbar=no,dependent=yes,minimizable=no,modal=yes,alwaysRaised=yes,"+
		"resizable=yes,scrollbars=yes,"+"width="+width+",height="+height+",top="+top+",left="+left;
	window.open(url ,name , options);
};

/**
 * 加载页面 url 导出的地址 param
 * 参数对象:[{name:'id',value:'1'},{name:'id',value:'2'},{name:'name',value:'11'},{name:'name',value:'22'}]
 * --- 请使用这样的格式
 */
Public.refreshWindow = function(url, param, target){
	var htmlStr = [];
	var formName = 'form'+Math.random();
	htmlStr.push('<form method="post" name="'+formName);
	htmlStr.push('" action="'+url);
	htmlStr.push('" style="visibility: hidden"');
	htmlStr.push('>');
	$.each(param||[],function(i,item){
		htmlStr.push('<input type="hidden" name="'+item.name);
		htmlStr.push('" value="'+item.value);
		htmlStr.push('"/>');
	});
	htmlStr.push('<input type="hidden" name="token.ignore" value="true"/>');
	htmlStr.push('</form>');
	$('body').append(htmlStr.join(''));
	var randomForm = $(document.getElementsByName(formName).item(0));
	randomForm.attr('target',target||'_self');
	randomForm.submit();
	randomForm.remove();
};
// 重置查询条件框(问题修改)
Public.resetQuery = function(){
	
	// 列表的动态查询区域
	$(".dropdown-menu").find("[tabindex]").each(function(index,item){// 格式化为select2的
		$(item).select2('val', '');
	});
	$(".dropdown-menu").find("select").each(function(index,item){
		if(!$(item).attr('tabindex')) {$(item).val('');}
	});
	$(".dropdown-menu").find('.treeselect-offscreen').each(function(index,item){// 格式化为select2的
		$(item).treeSelect('clear')
	});
	$(".dropdown-menu").find("input[type='text']").each(function(index,item){
		if(!$(item).attr('tabindex') && !$(item).hasClass('treeselect-offscreen') && !$(item).hasClass('select2-offscreen')) {
			$(item).val('');
		}
	});
	$(".dropdown-menu").find("textarea").val("");
	
	// 弹出框的查询区域
	$(".dialog-menu").find("[tabindex]").each(function(index,item){// 格式化为select2的
		$(item).select2('val', '');
	});
	$(".dialog-menu").find("select").each(function(index,item){
		if(!$(item).attr('tabindex')) {$(item).val('');}
	});
	$(".dialog-menu").find('.treeselect-offscreen').each(function(index,item){// 格式化为select2的
		$(item).treeSelect('clear')
	});
	$(".dialog-menu").find("input[type='text']").each(function(index,item){
		if(!$(item).attr('tabindex') && !$(item).hasClass('treeselect-offscreen') && !$(item).hasClass('select2-offscreen')) {
			$(item).val('');
		}
	});
	$(".dialog-menu").find("textarea").val("");
	
	// 删除记忆cookie
	Public.removeCookie('_rc', document.location.pathname);
};
// 列表框的查询
Public.doQuery = function(gridName){
	if($.fn.jqGrid) {
	  $('#' + (gridName||'grid')).jqGrid('setGridParam',{page:1}).trigger("reloadGrid");
	} else {
	  window.location.reload();
	}
};

// 列表框的刷新
Public.doRefresh = function(gridName){
	if($.fn.jqGrid) {
	  $('#' + (gridName||'grid')).trigger("reloadGrid");
	} else {
	  window.location.reload();
	}
};

// 有记忆的查询
Public.doMemoryQuery = function(gridName) {
	var formObj = $('#queryForm');
	var _cookie = (function(form) {
		var obj = [];
		$.each(form.serializeArray(),function(index,item){
			if (!(item.name in obj) && !!item.value){  
	            obj[item.name]=item.value;  
	            obj.push({
	            	name : item.name,
	            	value: item.value,
	            	dname: $(form).find('[name="'+item.name+'"]').data('name')
	            })
	        }  
		});
		// 添加分页支持
		// obj[{name: 'page', value: parseInt($('#' +
		// (gridName||'grid')).jqGrid('getGridParam', 'page'))}];
		// obj[{name: 'rows', value: parseInt($('#' +
		// (gridName||'grid')).jqGrid('getGridParam', 'rows'))}];
		return obj;
	})(formObj);
	if( !!_cookie ) {
		Public.setCookie('_rc', JSON.stringify(_cookie), document.location.pathname, 7);
	}
	// 默认的查询
	Public.doRefresh(gridName);
};
// 重置form
Public.resetForm = function(formName){
	var _formName = 'inputForm'||formName;
	$('#'+_formName).find('.select2-offscreen').each(function(index,item){// 格式化为select2的
		$(item).select2('val','');
	});
	$('#'+_formName).find("select").each(function(index,item){
		if(!$(item).hasClass('select2-offscreen')) {
			$(item).val('');
		}
	});
	$('#'+_formName).find("input[type='text']").each(function(index,item){
		if(!$(item).hasClass('select2-offscreen')) {
			$(item).val('');
		}
	});
	$('#'+_formName).find("textarea").val("");
};
// 表单列表的添加删除事件
Public.billsEvent = function(obj, type, flag){
	var _self = obj;
	// 新增row
	$('#dataGrid').on('click', '.ui-icon-plus', function(e){
		var rowId = $(this).parent().data('id');
		// var newId = $('#grid tbody tr').length;
		var datarow = { id: _self.newId };
		var su = $("#grid").jqGrid('addRowData', _self.newId, datarow, 'after', rowId);
		if(su) {
			$(this).parents('td').removeAttr('class');
			$(this).parents('tr').removeClass('selected-row ui-state-hover');
			$("#grid").jqGrid('resetSelection');
			_self.newId++;
		}
	});
	// 删除row
	$('#dataGrid').on('click', '.ui-icon-trash', function(e){
		if($('#grid tbody tr').length === 2) {
			Public.error("至少保留一条分录！");
			return false;
		}
		var rowId = $(this).parent().data('id');
		$("#grid").jqGrid('delRowData', rowId);
	});
	// 取消row编辑状态
	$(document).bind('click.cancel', function(e){
		if(!$(e.target).closest(".ui-jqgrid-bdiv").length > 0 && curRow !== null && curCol !== null){
		   $("#grid").jqGrid("saveCell", curRow, curCol);
		   curRow = null;
		   curCol = null;
		};
	});
};

/* 批量绑定页签打开 */
Public.pageTab = function() {
	$(document).on('click', '[rel=pageTab]', function(e){
		e.preventDefault(); e.stopPropagation();
		var tabid = $(this).data('id'), url = $(this).attr('href') || $(this).data('href'), showClose = $(this).data('showclose'), text = $(this).attr('title') || $(this).children(":first").text() || $(this).text(), parentOpen = $(this).data('parentopen');
		if(!(url === 'javascript:void(0)' || url === '#' || url === '/admin/')){
			// 特殊处理
			var isMenu = $(this).hasClass('-menu');
			    isMenu && (tabid = $(this).parent().data('id') + '-sub');
			var sTab = {};
			if (parentOpen){ sTab = parent.tab; } else { sTab = tab; }
			if (sTab.isTabItemExist(tabid)){
				sTab.selectTabItem(tabid);
				sTab.reload(tabid);
			} else {
				sTab.addTabItem({tabid: tabid, text: text, url: url, showClose: showClose});
			}
		} else {
			Public.toast("没设置地址");
		}
	});
};

/* 打开某个地址 -- url应该是已经编码过 */
Public.openOnTab = function(tabid, title, url){
	var sTab = {};
	if(parent.tab){ sTab = parent.tab; } else { sTab = tab; }
	if(sTab.isTabItemExist(tabid)){
		sTab.selectTabItem(tabid);
		sTab.removeTabItem(tabid);
	}
	sTab.addTabItem({tabid: tabid, text: title, url: url, showClose: 'true'});
};
/* 打开某个地址 */
Public.closeTab = function(tabid){
	var sTab = {};
	if(parent.tab){ sTab = parent.tab; } else { sTab = tab; }
	tabid = tabid || sTab.getSelectedTabItemID();
	if( sTab.isTabItemExist(tabid)){
		sTab.selectTabItem(tabid);
		sTab.removeTabItem(tabid);
	};
};
/* 选择某个tab的内容对象,通过名称 */
Public.getTabWindowByName = function(tabName){
	var sTab = {};
	if(parent.tab){ sTab = parent.tab; } else { sTab = tab; }
	var tabs = sTab.getTabidList();
	var _window = null;
	if( !! tabs ) {
		parent.$.each(tabs,function(index,item){
			if( !_window && parent.$("li[tabid=" + item + "]").children('a').text() == tabName) {
				_window = parent.$("iframe", ".l-tab-content-item[tabid=" + item + "]");
			}
		});
	}
	if(!!(_window.get(0))) {
		return _window.get(0).contentWindow;
	}
	return null;
};

/**
 * 得到打开的Tab的window对象
 */
Public.getSelectedTabWindow = function(){
	var sTab = {};
	if(parent.tab){ sTab = parent.tab; } else { sTab = tab; }
	var selectedId = sTab.getSelectedTabItemID();
	var _window = null;
	if( !! selectedId ) {
		_window = parent.$("iframe", ".l-tab-content-item[tabid=" + selectedId + "]");
	}
	if(!!(_window.get(0))) {
		return _window.get(0).contentWindow;
	}
	return null;
};

/**
 * 表单的验证
 */
Public.validate = function(options){
	var defaults = {
		submitHandler: function(form){
			Public.loading('正在提交，请稍等...');
			form.submit();
		},
		errorContainer: "#messageBox",
		errorPlacement: function(error, element) {
			Public.success('输入有误，请先更正');
			if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
				error.appendTo(element.closest(".controls"));
			} else {
				error.insertAfter(element);
			};
		}
	};
	return $.extend({},defaults,options);
};

/**
 * combo data 可以是数组，或对象（默认为远程取数据）
 */
Public.combo = function(t, data, options) {
	var defaults = {
		placeholder: "请选择..."
	};
	var _options = $.extend({},defaults,options);
	var _t = (typeof(t) == 'string')?(t.startWith('#')?t:'#'+t):t;
	$(_t).select2(_options);
	return _t;
};

/**
 * 级联 自动完成 用query获取数据 param 指向其他参数 relaObj 是dom对象 relaAs 别名
 */
Public.autoCascadeCombo = function(t, url, param, relaObj, relaAs, options) {
	var _relaObj = (typeof(relaObj) == 'string')?(relaObj.startWith('#')?relaObj:'#'+relaObj):relaObj;
	if($(_relaObj).get(0) == undefined) {
		Public.error('级联对象设置错误!');
		return false;
	}
	var defaults = {
		data: function (term, page) {
			return $.extend({},{
            	name: term,
                'param.pageSize': 10,
                'param.pageIndex': page,
                apikey: "ju6z9mjyajq2djue3gbvv26t"
            },param||{});
        },
        results: function (page) {
            var more = (page.param.pageIndex * page.param.pageSize) < page.param.recordCount;
            var values = [];
            $.each(page.data,function(index,item){
            	values.push({'id':item.id,'text':item.name});
            });
            return {results: values, more: more};
        }
	};
	var _ajax = $.extend({},defaults,{url:url});
	var _options = $.extend({}, options||{}, {query:function(settings){
		var _param = {};
		$(_relaObj).each(function(index,item){
			_param[relaAs||item.name] = item.value;
		});
		_param = $.extend({},_param,_ajax.data(settings.term,settings.page));
		// 获取数据
		Public.postAjax(_ajax.url, _param , function(page){
			settings.callback(_ajax.results(page));
		});
	}, initSelection: function(element, callback){
		var id=$(element).val();var name = $(element).data('name');
		callback({id:id,text:name});
	}});
	
	var _t = Public.combo(t, null, _options); 
	
	// 给级联的对象注册select 事件
	$(_relaObj).on('change',function(e){
		$(_t).select2("val", "");
	});
};

/**
 * 自动完成 有分页 param 指向其他参数
 */
Public.autoCombo = function(t, url, param, options) {
	var defaults = {
		data: function (term, page) {
			return $.extend({},{
            	name: term,
                'param.pageSize': 10,
                'param.pageIndex': page,
                apikey: "ju6z9mjyajq2djue3gbvv26t"
            },param||{});
        },
        results: function (page) {
            var more = (page.param.pageIndex * page.param.pageSize) < page.param.recordCount;
            var values = [];
            $.each(page.data,function(index,item){
            	values.push({'id':item.id,'text':item.name});
            });
            return {results: values, more: more};
        }
	};
	var _ajax = $.extend({},defaults,{url:url});
	var _options = $.extend({}, options||{}, {ajax:_ajax, initSelection: function(element, callback){
		var id=$(element).val();var name = $(element).data('name');
		callback({id:id,text:name});
	}});
	Public.combo(t, null, _options);
};

/**
 * tags t -- input tags -- 默认可选的项 options -- 属性扩展
 */
Public.tags = function(t, tags, options){
	var _tags = $.merge([], tags||[]); 
	var _options = $.extend({},{tags:_tags, placeholder: "请输入..."});
	Public.combo(t, null, _options);
};

/**
 * 自动完成的tags
 */
Public.autotags = function(t, url, param){
	var _options = $.extend({},{tags:true, placeholder: "请输入..."});
	Public.autoCombo(t, url, param, _options);
};

/**
 * multCombo
 */
Public.multCombo = function(t, data, options) {
	var _t = (typeof(t) == 'string')?(t.startWith('#')?t:'#'+t):t;
	$(_t).each(function(index,item){
		$(item).attr('multiple','multiple');// 添加属性
	});
	Public.combo(t, data, options);
};

/**
 * 延时执行 -- 返回延时对象,可以添加回调函数
 */
Public.delayPerform = function(task, delay){
	var dtd = $.Deferred();
	var tasks = function(){
		task();
		dtd.resolve();
	};
	setTimeout(tasks,delay||0);
	return dtd.promise();
};

/**
 * 使用artTemplate 模版技术(js原生) artTemplate 模版技术 content 可以是对象,元素id,元素,字符串,对象格式{}
 * context js对象 上下文 escape 是否格式化html，默认是true --
 * 会格式化包含的html代码（如果字符串中包含html代码，将转换，页面不能解析）
 * 
 */
Public.runTemplate = function(content, context, escape){
	var _escape = (escape != undefined)?escape:true;// 默认为true
	template.config('escape',_escape);
	var _get = template.get,_r = null;
	if( !(/^[a-zA-Z-\d]+$/g).test(content) ) {// 替换get实现
		template.get = function(a){
			return template.compile(content.replace(/^\s*|\s*$/g, ''), { filename: '_TEMP', cache: false, openTag: '{{', closeTag: '}}'});
		};
	}
	_r = template(content, context);
	template.get = _get;// 还原
	template.config('escape',true);
	return _r;
};

/**
 * 添加函数
 */
Public.registerTemplateFunction = function(name, fnc) {
	template.helper(name, fnc);
};

/**
 * 初始化普通表格的事件: 简单的添加一行, 删除一行(作为参考) table tableId
 */
Public.initSimpleTableEvent = function(table){
	$(document).on('click',"table[id='"+table+"'] .add",function(){
		var row = this.parentNode.parentNode;
		$('#' + table).simpleTable('add',row);
	});
    $(document).on('click',"table[id='"+table+"'] .delete",function(){
    	var row = this.parentNode.parentNode;
    	$('#' + table).simpleTable('del',row);
	});
    $(document).on('click',"table[id='"+table+"'] tr",function(e){
    	var src = e.target || e.srcElement;
    	if( src.nodeName != 'INPUT') {
    		$(this).find('.check input[type="checkbox"]').each(function() {
        		if( $(this).attr('checked') == 'checked') {
        			$(this).removeAttr('checked');
        		} else {
        			$(this).attr('checked', 'checked');
        		}
        	});
    	}
	});
};

/**
 * 检查导入的文件的格式,及正确性 uploadForm 表单,对表单进行验证 suffix 后缀默认".xls,.xlsx" 不区分大小写
 */
Public.checkFile = function(uploadForm, suffix){
	var flag = true;
	var _suffix = suffix||'.xls,.xlsx'.toUpperCase();
	$(uploadForm).find('.required').each(function(index,item){// 必填项验证
		if( flag && !$(item).val() && !$(item).is(":file")){
			$(item).addClass('text_error');
			flag = false;
		} else if( flag && !$(item).val()){
			Public.error('请选择文件');
			flag = false;
		}
	});
	$(uploadForm).find("[type='file']").each(function(index,item){// 文件验证
		var filename = $(item).val();
		if( flag && (!filename || !filename.lastIndexOf('.') < 0) ) {
			Public.error('请选择有效的文件');
			flag = false;
		}
		var prifix = filename.substring(filename.lastIndexOf('.'),filename.length).toUpperCase();
		if(flag && _suffix.indexOf(prifix) < 0) {
			Public.error('请选择有效的文件');
			flag = false;
		}
	});
	return flag;
};
/**
 * 执行导入操作 IE 不支持ajax上传文件，会用iframe来模拟，所以会比较慢， form 导入的表单 url 导入的url
 */
Public.doImport = function(form, url, success, error){
	var _url = url|| $(form).attr('action');
	$(form).ajaxSubmit({
		url: _url,
		dataType:"json",
		iframe: true,
		beforeSubmit:function(){
			var bflag = Public.checkFile($(form));
			if(bflag) {
				Public.loading('数据导入中...');
			}
			return bflag;
		},
		success: function(data){
			if (typeof(data) == "string") {
         	    data = $.parseJSON(data);
         	}
			Public.closeWindow();
			if (data.success) {
				Public.success('导入数据成功');
				if(typeof(success) === 'function') {
					success(data.obj);
				} else {
					Public.doQuery();
				}
			} else if(!!error){ // 定义了自定义的错误处理方式
				error(data.obj);
			} else { // 错误,但没定义错误的处理方式,默认是弹出来
				if (!data.obj && data.msg) { // 没有错误的明细
					Public.error(data.msg);
				} else if(data.obj) {// 有错误的明细
					var template='<div style="padding:20px 5px 5px 10px;">{{var errors = obj}}<table id="sample-table" class="table sample-table table-striped table-bordered table-hover"><thead><tr><th class="tc">序号</th><th>工作表</th><th>错误行</th><th>错误列</th><th>错误描述</th></tr></thead><tbody>{{for( var i = 0,j = errors.length;i < j;i++){var error = errors[i];}}<tr><td class="tc">{{=i+1}}</td><td class="tc">{{=error.sheet}}</td><td class="tc">{{=error.row}}</td><td class="tc">{{=error.column}}</td><td>{{=error.msg}}</td></tr>{{ } }}</tbody></table></div>';
					var msg = Public.runTemplate(template, data);
					Public.openWindow("导入数据错误(最多显示20条)", msg, 600,430);
				}
			}
		}
	});
};
/**
 * 导出 url 导出的地址 param
 * 参数对象:[{name:'id',value:'1'},{name:'id',value:'2'},{name:'name',value:'11'},{name:'name',value:'22'}]
 * --- 请使用这样的格式
 */
Public.doExport = function(url, param){
	var htmlStr = [];
	var formName = 'form'+ Math.random();
	htmlStr.push('<form method="post" name="'+formName);
	htmlStr.push('" action="'+url);
	htmlStr.push('" style="visibility: hidden"');
	htmlStr.push('>');
	$.each(param||[],function(i,item){
		htmlStr.push('<input type="hidden" name="export.'+item.name);
		htmlStr.push('" value="'+item.value);
		htmlStr.push('"/>');
	});
	htmlStr.push('<input type="hidden" name="token" value="'+$.cookie('token')+'"/>');
	htmlStr.push('<input type="hidden" name="holdToken" value="true"/>');
	htmlStr.push('</form>');
	$('body').append(htmlStr.join(''));
	$('.export-iframe').remove();
	var iframeName = 'iframe' + Math.random();
	$('body').append('<iframe name="'+iframeName+'" src="" width="0" height="0" style="display: none;" class="export-iframe"></iframe>');
	var randomForm = $(document.getElementsByName(formName).item(0));
	randomForm.attr('target', iframeName);
	randomForm.submit();
	randomForm.remove();
};
/**
 * 引入js和css文件
 */
Public.include = function(id, path, file){
	if (document.getElementById(id)==null){
        var files = typeof file == "string" ? [file] : file;
        for (var i = 0; i < files.length; i++){
            var name = files[i].replace(/^\s|\s$/g, "");
            var att = name.split('.');
            var ext = att[att.length - 1].toLowerCase();
            var isCSS = ext == "css";
            var tag = isCSS ? "link" : "script";
            var attr = isCSS ? " type='text/css' rel='stylesheet' " : " type='text/javascript' ";
            var link = (isCSS ? "href" : "src") + "='" + path + name + "'";
            document.write("<" + tag + (i==0?" id="+id:"") + attr + link + "></" + tag + ">");
        }
	}
};

/**
 * 获取选中的文本
 */
Public.getSelectText = function(){
	var an = null,ao;
	if($.browser.msie) {
		an = document.selection.createRange();
	} else {
		if ((ao = window.getSelection()) && ao.rangeCount) {
			an = ao.getRangeAt(0);
		}
	}
	return !an ? false : ($.browser.msie? an.text : an.toString());
};
/**
 * 选中文本
 */
Public.selectNodeText = function(parentNode) {
	var an = null;
	if($.browser.msie) {
		an = document.selection;
	} else {
		an = window.getSelection();
	}
	!!an&&an.selectAllChildren(parentNode);
};

/**
 * 是否是函数
 */
Public.isFunction = function(fun){
	return !!fun && typeof(fun) === 'function';
};
/**
 * 上一和下一事件(天) #prev 和 #next
 */
Public.initPreAndNextDayEvent = function(callback){
	// 上一条 和下一条的事件
	$(document).on('click','#prev',function(){
		var dom = "#"+$(this).data("dom");
		var value = $(dom).val();
		var dates = value.split('-');
		var date = new Date(dates[0],dates[1]-1,dates[2]);
		date.setDate(date.getDate() - 1);
		value = Public.formatDate(date, "yyyy-MM-dd");
		$(dom).val(value);
		Public.doQuery();
		if(Public.isFunction(callback)) {
			callback();
		}
	});
	// 上一条 和下一条的事件
	$(document).on('click','#next',function(){
		var dom = "#"+$(this).data("dom");
		var value = $(dom).val();
		var dates = value.split('-');
		var date = new Date(dates[0],dates[1]-1,dates[2]);
		date.setDate(date.getDate() + 1);
		value = Public.formatDate(date, "yyyy-MM-dd");
		$(dom).val(value);
		Public.doQuery();
		
		if(Public.isFunction(callback)) {
			callback();
		}
	});
};

// 格式化日期
Public.formatDate = function(date, format) {
	  var paddNum = function(num){ num += ""; return num.replace(/^(\d)$/,"0$1"); };
	  // 指定格式字符
	  var cfg = {
	     yyyy : date.getFullYear(), // 年 : 4位
	     yy : date.getFullYear().toString().substring(2),// 年 : 2位
	     M  : date.getMonth() + 1,  // 月 : 如果1位的时候不补0
	     MM : paddNum(date.getMonth() + 1), // 月 : 如果1位的时候补0
	     d  : date.getDate(),   // 日 : 如果1位的时候不补0
	     dd : paddNum(date.getDate()),// 日 : 如果1位的时候补0
	     hh : date.getHours(),  // 时
	     mm : date.getMinutes(), // 分
	     ss : date.getSeconds() // 秒
	  };
	  format || (format = "yyyy-MM-dd hh:mm:ss");
	  return format.replace(/([a-z])(\1)*/ig,function(m){return cfg[m];});
};

/**
 * 普通的编辑器
 */
Public.uEditor = function(dom, options){
	return UE.getEditor(dom, options || {});
};

/**
 * 简单的编辑器
 */
Public.simpleUEditor = function(dom, options){
	var defaults = {
		toolbars: [['fullscreen', 'source', '|', 'undo', 'redo', '|',
		            'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'forecolor', 'backcolor', '|',
		            'fontfamily', 'fontsize', '|',
		            'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', 'insertimage'
		]]
	};
	var options = $.extend({}, defaults, options || {});
	return UE.getEditor(dom, options || {});
};

/**
 * POST 提交用到 将表单序列化为如下形式[ { name: a value: 1 }, { name: b value: 2 },...]
 */
Public.serialize = function(form){
	return $(form || '#queryForm').serializeArray();
}

/**
 * GET 提交用到 将表单序列化为如下形式a=1&b=2&c=3&d=4&e=5
 */
Public.serializeGet = function(form){
	return $(form || '#queryForm').serialize();
}

/**
 * 取一个随机的ID（全部是数字组成）
 */
Public.random = function(){
	return Math.random().toString().substr(2);
}

/**
 * 截取两位小数
 */
Public.round = function(num, scale) {
   return num.toFixed(scale);
};

/**
 * 取一个随机的字符，长度可以自定义
 */
Public.generateChars = function (length) {
    var chars = '';
    for (var i = 0; i < length; i++) {
         var randomChar = Math.floor(Math.random() * 36);
         chars += randomChar.toString(36);
    }
    return chars;
};

/**
 * fnc -- 执行的函数 time -- 时间间隔 times -- 最大执行次数
 */
Public.setInterval = function(fnc, time, times) {
	var _times = 0;
	var timer = setInterval(function() {
		fnc();
		_times ++
		if(_times >=times) {
		   clearTimeout(timer);
		}
	}, time || 1000);
	return timer;
}

// 临时放入
/**
 * 读取cookie 的值
 */
Public.getCookie = function(name) {
	return $.cookie(name);
};

/**
 * 存储cookie 的值 expires(天)
 */
Public.setCookie = function(name, value, path, expires) {
	$.cookie(name, value, {path: path||'/', expires: expires||7});
};

/**
 * 删除cookie 的值 expires(天)
 */
Public.removeCookie = function(name, path) {
	$.cookie(name, null, {path: path||'/', expires: -1});
};

/**
 * 显示二维码
 */
Public.showQrcode = function(url, title, copy) {
	var _url = encodeURIComponent(url);
	Public.openUrlWindow(title||'微信扫描二维码预览', webRoot + '/admin/system/tag/qrcode?title='+(title||'预览二维码')+'&url=' + _url, 200, 280, null, null);
};

/**
 * 简单的二维码显示组件，需要引入qrcode库
 */
Public.simpleQrcode = function(dom, text, width, height, options) {
	var defaults = {
		text: text,
		width: width || 180,
		height: height || 180
	};
	var _options = $.extend({},defaults, options||{});
	$(dom).qrcode(_options);
};

/**
 * 初始化复制功能
 */
Public.initCopyEvent = function(dom, fuc) {
	(new ZeroClipboard($(dom))).on( "copy", function (event) {
	  var text = fuc();
	  if(!!text) {
		  var clipboard = event.clipboardData;
	      clipboard.setData( "text/plain", text);
	      top.Public.info('数据已复制到剪贴板');  
	  } else {
		  top.Public.error('没有需要复制的数据'); 
	  }
	});
};

/**
 * 树形选择组建
 */
Public.treeSelect = function(url, title, width, height, ok, clear, cancel, extId, selectIds, checked, rootS, parentS){
	var _url = webRoot + '/admin/system/tag/treeselect?url=' + encodeURIComponent(url) + '&checked='+checked+'&extId='+extId+'&selectIds='+selectIds ;
	var $rootS = !!rootS; var $parentS = !!parentS;
	Public.openWindow(title, "iframe:"+_url, width, height, {
		btn: (function(){
			if(typeof(clear) === 'function') {
			   return ['确定', "关闭", '清除'];
			} else {
			   return ['确定', "关闭"];
			}
		})(),
		yes: function(index, layero) {
			var returnFalg = true;
			var ids = [], names = [], nodes = [];
			var tree = layero.find('iframe').get(0).contentWindow.tree;
			if (!!checked){
				nodes = tree.getCheckedNodes(true);
			}else{
				nodes = tree.getSelectedNodes();
			}
			for(var i=0; i<nodes.length; i++) {
				if (!!checked) {
					if (nodes[i].isParent){
						continue; // 如果为复选框选择，则过滤掉父节点
					}
				}
				if (nodes[i].level == 0 && !$rootS){
					Public.error("不能选择根节点（"+nodes[i].name+"）请重新选择。")
					returnFalg = false;
					return;
				}
				if (nodes[i].isParent && !$parentS){
					Public.error("不能选择父节点（"+nodes[i].name+"）请重新选择。")
					returnFalg = false;
					return;
				}
				ids.push(nodes[i].id);
				names.push(nodes[i].name);
				if (!checked) {// 不是多选，则返回
					break;
				}
			}
			if (!!returnFalg && !!ok && typeof(ok) === 'function') {
				var iframe = layero.find('iframe').get(0).contentWindow;
				returnFalg = ok(iframe, ids, names);
			}
			// 如果是关闭
			if (returnFalg) {
				Public.close(index);
			}
		},
		cancel : function(index, layero) {
			if(typeof(cancel) === 'function') {
				var iframe = layero.find('iframe').get(0).contentWindow;
				return cancel(iframe);
			}
		},
		btn3: function(index, layero) {
			var ids = [], names = [];
			var returnFalg = true;
			if (!!clear && typeof(clear) === 'function') {
				var iframe = layero.find('iframe').get(0).contentWindow;
				returnFalg = clear(iframe,ids, names);
				if (returnFalg == undefined) {
					returnFalg = true;
				}
			}
			// 如果是关闭
			if (returnFalg) {
				Public.close(index);
			}
		}
	}, null, null);
};

/**
 * 表选择组建
 */
Public.tableSelect = function(url, title, width, height, ok, clear, cancel, checked) {
	Public.openWindow(title, "iframe:"+url, width, height, {
		btn: ['确定', "关闭", '清除'],
		yes: function(index, layero) {
			var returnFalg = true;
			var ids = [], names = [];
			var _ids = layero.find("iframe")[0].contentWindow.Public.selectedRowIds();
			var _names = layero.find("iframe")[0].contentWindow.Public.selectedRowNames();
			$.each(_ids, function(index, item){
				ids.push(item.value);
			});
			$.each(_names, function(index, item){
				names.push(item.value);
			});
			if( !!returnFalg && ids.length != 0 && !!ok && typeof(ok) === 'function') {
				var iframe = layero.find('iframe').get(0).contentWindow;
				returnFalg = ok(iframe, ids, names);
				if (returnFalg == undefined) {
					returnFalg = true;
				}
			}
			// 如果是关闭
			if( returnFalg) {
				Public.close(index);
			}
		},
		cancel : function() {
			if(typeof(cancel) === 'function') {
				return cancel();
			}
		},
		btn3: function(index, layero) {
			var ids = [], names = [];
			var returnFalg = true;
			if( !!clear && typeof(clear) === 'function' ) {
				var iframe = layero.find('iframe').get(0).contentWindow;
				returnFalg = clear(iframe,ids, names);
				if( returnFalg == undefined ) {
					returnFalg = true;
				}
			}
			// 如果是关闭
			if( returnFalg) {
				Public.close(index);
			}
		}
	}, null, null);
};

/**
 * 树 - 单选
 */
Public.singleTreeSelect = function(url, title, width, height, ok, clear, cancel){
	Public.treeSelect(url, title, width, height, ok, clear, cancel, false);
};

/**
 * 树 - 多选
 */
Public.multiTreeSelect = function(url, title, width, height, ok, clear, cancel){
	Public.treeSelect(url, title, width, height, ok, clear, cancel, true);
};

/**
 * 附件选择 属性 name src
 */
var Attachment = Attachment||{};
Attachment.selectAttachments = function(success) {
 Public.openUrlWindow("选择文件", webRoot + '/admin/system/attachment/select', 700, 450, function(iframe, index) {
	var selectedFiles = iframe.THISPAGE.getSelectedFiles();
	var checked = false;
	!!selectedFiles, selectedFiles.length>=1, checked = (function() {
		var _checked = true;
		$.each(selectedFiles, function(index, item){
			if(item.type == 'DIR') {
			   _checked = false;
			}
		});
		return _checked;
	})();
	if(!checked) {
		Public.error('不要选择文件夹');
	} else {
		return typeof(success) === 'function'? success(selectedFiles):true;
	}
 }, null);
};

/**
 * 没有图片
 */
Attachment.noImage = function() {
	var img = event.srcElement;
	$(img).attr('src', webRoot + '/static/img/ no_image.png');
};

// 用户
var User = User || {};

/**
 * 用户没有头像
 */
User.notHeadimg = function() {
	var img = event.srcElement;
	$(img).attr('src', webRoot + '/static/img/default_user.jpg');
};

/**
 * 创建一个滚动实例(适合局部滚动)
 */
Public.newScroll = function(dom, options) {
	var _options = $.extend({},{
		scrollX: false, 
	    freeScroll: false, 
	    preventDefault: false,
	    scrollbars: false,
	    mouseWheel: true,
	    fadeScrollbars: false,
	    disableMouse: false,
	    disablePointer: true, 
	    bounce:true,
	    momentum:false,
	    probeType:1
	}, options||{});
	return new IScroll(dom, _options);
};