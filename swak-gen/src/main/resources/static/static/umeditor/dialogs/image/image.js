(function () {
	var Base = {
		getAllPic : function (self) {
			var $imgs = self.getSelectedFiles();
			var arr = [];
			$.each($imgs, function (index, node) {
                return arr.push({
                    _src: node.src,
                    src: node.src
                });
            });
            return arr;
		}
	};
	UM.registerWidget('image', {
		tpl: "<link rel=\"stylesheet\" type=\"text/css\" href=\"<%=image_url%>image.css?v=1.0\">" +
        "<div class=\"edui-image-wrapper\">" +
        "<iframe class=\"edui-iframe\" src=\""+webRoot+"/f/member/attachment/uploader\" width=\"100%\" height=\"0\"></iframe>"+
        "</div>",
        initContent : function(editor, $dialog) {
        	var lang = editor.getLang('image')["static"],
            opt = $.extend({}, lang, {
                 image_url: UMEDITOR_CONFIG.UMEDITOR_HOME_URL + 'dialogs/image/'
            });
        	this.root().html($.parseTmpl(this.tpl, opt));
        },
        initEvent : function() {
        	$tab = $.eduitab({selector: ".edui-image-wrapper"}).edui().on("beforeshow", function (e) {
                e.stopPropagation();
            });
        	//设置 iframe 的大小
        	$('.edui-iframe').each(function() {
        		var self = $(this);
        		self.css({
        			height : (function(){
        				return self.closest('.edui-modal-body').height() - 30;
        			})()
        		});
        	});
        },
		width: 700,
        height: 408,
        buttons: {
        	 'ok': {
        		 exec : function(editor, $w) {
        			 var self = $('.edui-image-wrapper').find('.edui-iframe').get(0).contentWindow.THISPAGE;
	                 var list = Base.getAllPic(self);
	                 if (list.length != 0) {
	                     editor.execCommand('insertimage', list);
	                 } 
        		 }
        	 },
        	 'cancel': {}
        }
	}, function(editor, $w, url, state) {
		alert(1)
	});
})();

