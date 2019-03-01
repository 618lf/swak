/**
 * Tree 
 */
Public.tree = function(options){ return (new Public.Tree(options));};
Public.Tree = function(options){
	var setting = {
		remoteUrl:'', ztreeDomId:'',
		view:{selectedMulti:false},
		check:{enable:false, nocheckInherit:true},
		data:{simpleData:{enable:true}},
		view:{
			fontCss:function(treeId, treeNode) {
				return (!!treeNode.highlight) ? {"font-weight":"bold"} : {"font-weight":"normal"};
			}
		},
		callback:{
			beforeClick:function(id, node){ }, 
		    onClick:function(event, treeId, treeNode){},
		    onDblClick:function(){}
        },
        expandAll: true
	};
	this.options = $.extend({},setting,options);
	this._init();
};
Public.Tree.prototype = {
	_init : function(){
		var self = this,opts = this.options,time;
		if(opts.remoteUrl) {
		   Public.postAjax(opts.remoteUrl,'',function(data){
			 var zTreeNodes = [];
			 $.each(data,function(index,item){
				zTreeNodes.push({id:item.id,pId:item.pId,name:item.name,chkDisabled:(!!item.chkDisabled), selectAbled:(!!item.selectAbled)});
			 });
			 $.fn.zTree.init($("#" + opts.ztreeDomId), opts, zTreeNodes);
			 var tree = $.fn.zTree.getZTreeObj(opts.ztreeDomId);
			 tree.expandAll(opts.expandAll);
		   });
		}
	},
	_getZTreeObj : function(){
		return $.fn.zTree.getZTreeObj(this.options.ztreeDomId);
	},
	_destroy : function() {
		var self = this,opts = this.options;
		$.fn.zTree.destroy(opts.ztreeDomId);
	}
};