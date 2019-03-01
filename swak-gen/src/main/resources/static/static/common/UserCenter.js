/**
 * 提供用户异步登录一系列操作,判断当前用户等 --- 后台
 * version:1.0
 */
var User = User || {
	dialog : null,
};

/**
 * 锁屏幕
 */
User.lockScreen = function() {
	Public.confirmx('锁屏会退出系统，用密码解锁之后会回到当前工作页面', function() {
		Public.postAjax(webRoot + '/admin/logout', {}, function(){
			User.loginDialog();
		}, false);
	});
};

/**
 * 展示login对话框
 */
User.loginDialog = function(){
	if (User.dialog == null) {
		User.dialog = Public.openInnerUrlWindow('登录', webRoot+'/admin/login?isDialog=1', 440, 500, null, function(){
			return false;
		}, null, function(){
			User.dialog = null;
	   });
	}
};

/**
 * 展示login对话框
 */
User.logoutReason = function(msg){
	if (User.dialog == null) {
		User.dialog = Public.openViewWindow('<div class="logout-reason-wrap"><div class="logout-reason">'+ msg +'</div><a class="btn" href="'+ webRoot +'/admin/login">确定</a></div>', 400, 320, false);
	}
};

/**
 * 断言登录
 */
User.assertLogin = function(data) {
	if (!!data && data.code == 40005) {
	    !!data.reason ? User.logoutReason(data.reason): User.loginDialog(); 
	    return false;
	}
	return true;
};

/**
 * 刷新用户状态
 */
User.refreshUserBar = function() {
	Public.close(User.dialog);
	Public.close();
};

/*
 * 系统待办
 */
$(function() {
	
	var timer = null;
	
	// 加载待办
	var count_unread_msg = function(tip) {
		Public.postAjax(webRoot + '/api/todo', {}, function(data) {
		   var newData = data.obj;
		   var target = $('#messageInfo').find('b');
		   var old = target.text();
		       old = parseInt(old);
		   var num = data;
		   if (!!tip && old < newData) {
			   // 新消息提醒
			   var nNum = newData - old;
			   Public.toast('收到' + nNum + '条新消息');
		   }
		   
		   // 显示数据
		   target.text(newData); 
	    });
	};
	
	//50 秒查询一次
	//timer = Public.setInterval(function() {
	//	count_unread_msg(true);
	//}, 50000); 
	
	// 加载时需要查询一次
	count_unread_msg(false);
});