/**
 *  加载菜单
 */
var tab = {};
$(function(){
	
	//Tab 的大小
	setTabLayout();
	
	//tab事件
	Public.pageTab();
	
	//加载菜单
	userMenu('');
	
	//tab初始化
	initTab();
	
	//add 首页
	tab = $("#page-tab").ligerGetTabManager();
	tab.addTabItem({tabid: 'index', text: '首页', url: webRoot + '/shop/home.html', showClose: false});
    
	//IE 版本
	if($.browser.msie && parseInt($.browser.version) <= 8) {
		var Oldbrowser = {
			msie: $.browser.msie,
			version: parseInt($.browser.version),
			init: function(){
				if($.cookie('Oldbrowser') === 'onlyOne') {
					return;
				};
				this.addDom();
			},
			addDom: function() {
				$('<div id="browser">您使用的浏览器版本过低，影响网页性能，建议您换用<a href="http://www.google.cn/chrome/intl/zh-CN/landing_chrome.html" target="_blank">谷歌</a>、<a href="http://download.microsoft.com/download/4/C/A/4CA9248C-C09D-43D3-B627-76B0F6EBCD5E/IE9-Windows7-x86-chs.exe" target="_blank">IE9</a>、或<a href=http://firefox.com.cn/"  target="_blank">火狐浏览器</a>，以便更好的使用！<a id="bClose" title="关闭">x</a></div>').insertBefore('#header').slideDown(500);	this._colse();
			},
			_colse: function() {
				var that = this;
				$('#bClose').click(function(){
					$('#browser').remove();
					if(that.version === 7) {
						$.cookie('Oldbrowser', 'onlyOne', {expires: 1000});
					}
				});
			}
		};
		Oldbrowser.init();
	};
	//个人信息
	$(document).on('click','#persionInfo',function(){
		var tabid = 'persionInfo';
		if( tab.isTabItemExist(tabid)){ 
			tab.selectTabItem(tabid);
			tab.reload(tabid);
		} else {
			tab.addTabItem({tabid: tabid, text: '个人信息', url: webRoot + '/admin/system/self/info', showClose: true});
		}
	});
	//切换用户
	$(document).on('click','#switchto',function(){
		var template = '<div id="switch_users"> {{ for(var i = 0; i< users.length; i++ ){ var user = users[i]; }} <label class="radio inline"><input type="radio" name="switchtoUser" value="{{=user.userId}}" class="switchtoUser">{{=user.userName}}</label>{{ } }} </div>'
		Public.postAjax(webRoot + '/admin/system/user/runas/runs', null, function(data) {
			var html = Public.runTemplate(template, {users: data.obj});
			Public.openWindow('切换到', html, 350, 210, function() {
				var userId = $('#switch_users').find('.switchtoUser:checked').val();
				if (!!userId) {
					switchto(userId);
				}
				return true;
			});
		}, false);
	});
	//退出切换
	$(document).on('click','#runas-exit',function(){
		Public.postAjax(webRoot + '/admin/system/user/runas/release', null, function(data) {
			window.location.reload();
		}, false);
	});
	//站内信
	$(document).on('click','#messageInfo',function(){
		var tabid = 'messageInfo';
		if (tab.isTabItemExist(tabid)){ 
			tab.selectTabItem(tabid);
			tab.reload(tabid);
		} else {
			tab.addTabItem({tabid: tabid, text: '收件箱', url: webRoot + '/admin/system/message/inBox', showClose: true});
		}
	});
	
	Public.click_event = $.fn.tap ? "tap" : "click";
	
	//自适应大小
	$(window).on("resize", function(a) {
		setTabLayout(); setMenuLayout(); setSubmenuLayout();
	});
});

/**
 * 切换到
 * @param userId
 */
function switchto(userId) {
	 Public.postAjax(webRoot + '/admin/system/user/runas/switch', {userId: userId}, function(data) {
	   if (data.success) {
		   window.location.reload();
	   } else {
		   Public.error(data.msg);
	   }
	 }, false);
};

/**
 *  加载菜单
 */
function userMenu(parentId) {
	$("div#sidebar[data-href]").each(function(index,item){
		var that = $(item); var version = that.data('version');
		var url = that.data('href');
		    url = ((url.indexOf("?")>-1)?(url+"&_"):(url+"?_")) +"timeid=" + Math.random();
		var param  = "&id=" + parentId;
		    param += "&screenSize=" + $("#content").height() +"  #userMenuNav";//取 #userMenuNav的数据
		if( !!url ) {
			Public.getAjax(url + param, {}, function(data) {
				
				// 设置页面
				that.html(Public.runTemplate($('#menuTemplate').html(), {menus: data.obj}));
				
				// 初始化大小
				$('.group-submenu').each(function() {
					var that = $(this);
					var width = that.children('.group-item').eq(0).width() * that.children('.group-item').length + 10 ;
					that.width(width);
				});
				
				// 自适应大小
				setMenuLayout();
				
				// 设置二级菜单的高度
				setSubmenuLayout();
				
				$('<img id="icon-vension" src="'+version+'">').prependTo(that);
			});
		}
	});
};

/**
 * 加载Tab
 */
function initTab(){
	$('#page-tab').ligerTab({
		height: '100%', 
		changeHeightOnResize : true,
		onBeforeAddTabItem : function(tabid){
		},
		onAfterSelectTabItem : function(tabid){
		},
		onBeforeRemoveTabItem : function(tabid){
		},
		onAfterLeaveTabItem: function(tabid){
		}
	});
}

/**
 * Tab 的大小
 */
function setTabLayout(){
	var e = $(window).height(),
	t = $("#content-body"),
	i = e - t.offset().top;
    t.height(i)
    $('#sidebar').height(e);
};

/**
 * 一级菜单的高度
 * @returns
 */
function setMenuLayout() {
	var themes = [72, 60]; //0 、1、2、3、4
	var e = $('#sidebar').height();
	var $li = $('#userMenuNav > li:last');
	if ($li.get(0) && ($li.offset().top + $li.height()) > e) {
		var size = $('#userMenuNav > li').length;
		var heigth = e - 46;
		var one_size = parseInt(heigth /size);
		if (one_size < 80) {
			var c = 2;
			for(var i in themes) {
				if (themes[i]<=one_size) {
					c = i;
					break;
				}
			}
			// 使用1号主题
			$('#sidebar').attr('class', 'sidebar sidebar-' + c);
		}
	}
};

/**
 * 二级菜单的高度
 */
function setSubmenuLayout(){
	$('.top-catalog').each(function() {
		var submenu = $(this).next();
		var height = $(window).height();
		if(!!submenu.get(0)) {
		   var top = $(this).offset().top;
		   var h = submenu.outerHeight();
		   if(height < top + h) {
			  submenu.css({top: (top + h - height) * -1}) 
		   } else {
			  submenu.css({top: -2})
		   }
		}
	});
};