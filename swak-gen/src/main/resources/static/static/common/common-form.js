﻿/**
 * form表单页面的改进(表单页面的通用)
 * 
 * @param $
 */
var _priceValFmt = function(item) {
	var _val = $(item).val();
	(!!_val && _val.length > 0) && $(item).val(Public.moneysFmt(_val));
};
var _priceValRfmt = function(item) {
	var _val = $(item).val();
	(!!_val && _val.length > 0) && $(item).val(Public.rmoneysFmt(_val));
};

/**
 * 定义表单
 */
function Page(options) {
	
	// 默认值
	var defaults = {
		data : {},
		
		// 初始化
		init : function(params) {
			
		},
		
		// 事件
		event : function() {
			
		},
		
		// 事件
		_event : function() {
			//金额格式化
			$('.price-val').each(function(index, item) {
				_priceValFmt(item);
		    });
			//iSelect 美化
			$('.iSelect').each(function(index, item) {
				Public.combo(item);
		    });
			//iTag 美化
			$('.iTag').each(function(index, item) {
				Public.tags(item);
		    });
			this.event();
		},
		
		// 参数
		params : function() {
			var url = window.document.location.href.toString();
		    var u = url.split("?");
		    if(typeof(u[1]) == "string"){
		        u = u[1].split("&");
		        var get = {};
		        for(var i in u){
		            var j = u[i].split("=");
		            get[j[0]] = j[1];
		        }
		        return get;
		    } else {
		        return {};
		    }
		},
		
		// 校验
		_validator : function() {
			var defaults = {
				submitHandler: function(form){
					$('.price-val').each(function(index, item) {
						_priceValRfmt(item);   
			        });
					Public.loading('正在提交，请稍等...');
					$(form).ajaxSubmit({
						url: $(form).attr('action'),
						dataType:"json",
						beforeSubmit : function(){},  
						async: true,
						success: function(data){
							Public.close();
							Public.success('操作成功！', function() {
								$('#id').val(data.obj.id);
							});
						},
						error : function(x){
							Public.close();
							var msg = $.parseJSON(x.responseText).msg;
							Error.out(msg);
						}
					});
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
			
			// 参数
			var options = $.extend({}, defaults, this.validate());
			if (!!this.validator) {
				this.validator.destroy();
			}
			
			// form 校验
			this.validator = $("#inputForm").validate(options);
		}, 
		
		// 校验
		validate : function() {
			return {}
		},
		
		// 实例
		render : function() {
	    	var template = $('template').html();
			var html = Public.runTemplate(template, this.data);
			$('template').next().remove();
			$('template').after(html);
			this._event();
			this._validator(); 
	    },
	    
	    // 挂载
	    mounted : function() {
	    	this.render();
	    	this.init(this.params());
	    }
	};
    
    // 返回对象
    return $.extend({},defaults,options);
};

/**
 * 初始化页面事件
 * 
 * @param $
 * @returns
 */
(function($) {
	if (!!$('.form-actions').get(0) &&  !($('.form-actions').data('scrollable') == '0') ) {
		$(document).on('scroll', function() {
			var scrollTop = parseInt($(this).scrollTop());
			var scrollHeight = parseInt(document.body.scrollHeight);
			var windowHeight = parseInt($(window).outerHeight());
			if(Math.abs(scrollHeight - ( scrollTop + windowHeight)) <= 50){
			　  $('.wrapper').removeClass('wrapper-fixed');
			}else if(!$('.wrapper').hasClass('wrapper-fixed')){
			   $('.wrapper').addClass('wrapper-fixed');
			} 
		});
		(function() {
			var scrollTop = parseInt($(this).scrollTop());
			var scrollHeight = parseInt(document.body.scrollHeight);
			var windowHeight = parseInt($(window).outerHeight());
			if (!$('.wrapper').hasClass('wrapper-fixed') && Math.abs(scrollHeight - ( scrollTop + windowHeight)) != 0) {
			    $('.wrapper').addClass('wrapper-fixed');
			}
		})();
	}
	//监听
	$(document).on('focus', '.price-val', function() {
		var item = $(this);
		_priceValRfmt(item);
	});
	$(document).on('blur', '.price-val', function() {
		var item = $(this);
		_priceValFmt(item);
	});
	
	//监听页面数据变化
	if (!!$('#inputForm').get(0) && ($('#inputForm').data('monitorable') == '1') ) {
		var _form = $('#inputForm');
		var _INIT_FORM_DATA = $.MD5(JSON.stringify(Public.serialize(_form)));
		
		//系统默认的弹出框才有阻塞功能
		$('#cancelBtn').off().on('click', function(event) {
			var _END_FORM_DATA = $.MD5(JSON.stringify(Public.serialize(_form)));
			if (_INIT_FORM_DATA != _END_FORM_DATA) {
				var _return = window.confirm('页面数据有修改，确认退出页面');
				if(!_return) {
				   event.preventDefault();  
				   event.stopPropagation();
				}
			}
		});
	};
	
	// 上下快捷按钮
	if (!$('body').hasClass('no-fixed-btns')) {
		var html = $('#fixedBtns').html();
		!!html && $(html).appendTo($('body'));
		$('.fixed-btns').off().on('click', 'a', function() {
			var action = $(this).data('action');
			if ('top' == action) {
				$('html,body').animate({scrollTop: '0px'}, 100);
			} else if('bottom' == action) {
				var scrollHeight = parseInt(document.body.scrollHeight);
				$('html,body').animate({scrollTop: scrollHeight}, 100);
			}
		});
	};
	
	// 初始化一些控件的事件
	Public.initTagsEvent();
})(jQuery);