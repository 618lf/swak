/**
 * 自定义模板
 */
$(function() {
	
	/**
	 *  变量
	 */
	UE.registerUI("msgTemplate", function(editor, uiName) {
		 //创建dialog
		var dialog = new UE.ui.Dialog({
			//指定弹出层中页面的路径，这里只能支持页面,因为跟addCustomizeDialog.js相同目录，所以无需加路径
			iframeUrl : webRoot + '/admin/system/template/tableSelect',
			//需要指定当前的编辑器实例
			editor : editor,
			//指定dialog的名字
			name : uiName,
			//dialog的标题
			title : "选择消息模板",
			//指定dialog的外围样式
			cssRules : "width:600px;height:300px;",
			//如果给出了buttons就代表dialog有确定和取消
			buttons : [ {
				className : 'edui-okbutton',
				label : '确定',
				onclick : function() {
					var template = $('.edui-for-msgTemplate').find("iframe")[0].contentWindow.THISPAGE.getSelectTemplate();
					if(!!template && !!template.id) {
						editor.execCommand('inserthtml', template.content);
					}
					dialog.close(true);
				}
			}, {
				className : 'edui-cancelbutton',
				label : '取消',
				onclick : function() {
					dialog.close(false);
				}
			} ]
		});
		
		var btn = new UE.ui.Button({
			//按钮的名字
	        name: uiName + 'btn',
	        //提示
	        title: '选择消息模板',
	        //点击时执行的命令
	        onclick: function() {
	        	//渲染dialog
				dialog.render();
				dialog.open();
	        }
		});
		
		return btn;
	})
});