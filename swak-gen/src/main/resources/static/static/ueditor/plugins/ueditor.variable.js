/**
 *  添加一个变量的按钮
 */
$(function() {
	
	var _vars = ['A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q'];
	var _count = 0;
	/**
	 *  变量
	 */
	UE.registerUI("varbtn", function(editor, uiName) {
		var btn = new UE.ui.Button({
			//按钮的名字
	        name: uiName,
	        //提示
	        title: '插入${X}',
	        //点击时执行的命令
	        onclick: function() {
	            //这里可以不用执行命令,做你自己的操作也可
	        	var _var = "${"+_vars[_count]+"}";
	            editor.execCommand('inserthtml', _var);
	            _count = (_count >= _vars.length - 1? 0: ++_count);
	        }
		});
		//当点到编辑内容上时，按钮要做的状态反射
	    editor.addListener('selectionchange', function() {
	        var state = editor.queryCommandState(uiName);
	        if (state == -1) {
	            btn.setDisabled(true);
	            btn.setChecked(false);
	        } else {
	            btn.setDisabled(false);
	            btn.setChecked(state);
	        }
	    });
	    //因为你是添加button,所以需要返回这个button
	    return btn;
	})
});