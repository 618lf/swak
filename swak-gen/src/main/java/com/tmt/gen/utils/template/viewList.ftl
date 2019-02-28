<%@ page contentType="text/html;charset=UTF-8" session="false" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/pageHead.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title><sys:site/>管理后台</title>
<%@ include file="/WEB-INF/views/include/header.jsp"%>
<#if isImport == 1>
<link href="${ctxStatic}/common/common-file.css" rel="stylesheet" />
</#if>
</head>
<body>
<div class="wrapper">
    <div class="wrapper-inner">
		<div class="top">
		    <form name="queryForm" id="queryForm">
				<div class="fl">
				  <div class="ui-btn-menu">
				      <span class="ui-btn ui-menu-btn">
				         <strong>点击查询</strong><b></b>
				      </span>
				      <div class="dropdown-menu" style="width: 320px;">
				           <#list table.columns as c>
				           <#if c.isQuery == 1>
				           <div class="control-group formSep">
							  <label class="control-label">${c.comments}:</label>
							  <div class="controls">
							    <#if c.showType == 'input' || c.showType == 'textarea'|| c.showType == 'readonly' >
								<input type="text" name="${c.javaField}" maxlength="${c.length}">							    
								</#if>
							    <#if c.showType == 'y_n_r' ||  c.showType == 'y_n_s'>
							     <select name="${c.javaField}" class="iSelect">
							       <option value=""></option>
							       <c:forEach items="${r'${fns:getYesAndNoLabels()}'}" var="item">
							         <option value="${r'${item.value}'}">${r'${item.label}'}</option>
							       </c:forEach>
							     </select>
							    </#if>
							    <#if c.showType == 'n_a_d_r' ||  c.showType == 'n_a_d_s'>
							     <select name="${c.javaField}" class="iSelect">
							       <option value=""></option>
							       <c:forEach items="${r'${fns:getStatusLabels()}'}" var="item">
							         <option value="${r'${item.value}'}">${r'${item.label}'}</option>
							       </c:forEach>
							     </select>
							    </#if>
                                <#if c.showType == 'enum'>
                                 <select name="${c.javaField}">
							        <c:forEach items="${r'${'}${c.javaField}s${r'}'}" var="item">
							        <option value="${r'${item}'}">${r'${item.name}'}</option>
							        </c:forEach>
							     </select>
                                </#if>
							    <#if c.showType == 'date'>
							      <input type="text" name="${c.javaField}" readonly="readonly" maxlength="50" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
							    </#if>
							  </div>
						   </div>
				           </#if>
				           </#list>
					       <div class="ui-btns">
				              <input class="btn btn-primary query" type="button" value="查询"/>
				              <input class="btn reset" type="button" value="重置"/>
				           </div> 
				      </div>
				  </div>
				  <input type="button" class="btn btn-primary" value="&nbsp;刷&nbsp;新&nbsp;" onclick="Public.doQuery()">
				</div>
				<div class="fr">
				   <#if isImport == 1>
				   <input type="button" class="btn btn-primary" id="impBtn" value="&nbsp;导&nbsp;入&nbsp;">&nbsp;
				   <input type="button" class="btn btn-success" id="addBtn" value="&nbsp;添&nbsp;加&nbsp;">&nbsp;
				   </#if>
				   <#if isImport != 1>
				   <input type="button" class="btn btn-primary" id="addBtn" value="&nbsp;添&nbsp;加&nbsp;">&nbsp;
				   </#if>
				   <#if table.hasSort>
                   <input type="button" id="sortBtn" class="btn" value="&nbsp;保存排序&nbsp;">
                   </#if>
                   <#if table.publishColumn != null>
                   <input type="button" class="btn btn-success" id="publishBtn" value="&nbsp;启&nbsp;用&nbsp;">&nbsp;
				   <input type="button" class="btn" id="unpublishBtn" value="&nbsp;停&nbsp;用&nbsp;">&nbsp;
                   </#if>
				   <input type="button" class="btn" id="delBtn" value="&nbsp;删&nbsp;除&nbsp;">&nbsp;
				</div>
			</form>
		</div>
	</div>
	<div id="dataGrid" class="autoGrid">
		<table id="grid"></table>
		<div id="page"></div>
	</div> 
</div>
<%@ include file="/WEB-INF/views/include/list-footer.jsp"%>
<#if isImport == 1>
<script src="${ctxStatic}/common/common-file.js" type="text/javascript"></script>
</#if>
<script type="text/javascript">
<#if table.hasSort>var curRow,curCol;</#if>
var THISPAGE = {
	_init : function(){
		this.loadGrid();
		this.addEvent();
	},
	loadGrid : function(){
		var init = Public.setGrid();
		var optionsFmt = function (cellvalue, options, rowObject) {
			return Public.billsOper(cellvalue, options, rowObject<#if schemeCategory?contains('treeTable')>, true</#if>);
		};
		$('#grid').jqGrid(
			Public.<#if schemeCategory?contains('treeTable')>treeGrid<#else>defaultGrid</#if>({
				url: '${r'${ctx}'}/${moduleName}/${subModuleName}/${functionName}/page?timeid='+ Math.random(),
				height:init.h,
				shrinkToFit:!1, 
                <#if schemeCategory?contains('treeTable')>
                cellsubmit: "clientArray",
				rownumbers: !1,//序号
                <#else>
                rownumbers: !0,//序号
				multiselect:true,//定义是否可以多选
				multiboxonly: false,
                </#if>
				colNames: ['ID', <#list table.columns as c><#if c.isList == 1>'${c.comments}', </#if></#list>''],
				colModel: [
                    {name:'id', index:'id', width:80,sortable:false,hidden:true},
                    <#list table.columns as c>
                    <#if c.isList == 1>
                    {name:'${c.javaField}', index:'${c.javaField}', width:120, align:'left', sortable:false<#if c.javaField == 'sort'>, editable: !0, edittype:'text'</#if>},
                    </#if>
                    </#list>
					{name:'options', index:'options',align:'center',width:80,sortable:false,formatter:optionsFmt}
				]
			})		
		);
		$('#grid').jqGrid('setFrozenColumns');
        <#if table.hasPath>
		$("#grid").jqGrid("setGridParam", { cellEdit: !0  });
	    </#if>
	},
	addEvent : function(){
		Public.initBtnMenu();
		var del${className} = function(checkeds){
			if (!!checkeds) {
				var param = [];
				if(typeof(checkeds) === 'object'){
					$.each(checkeds,function(index,item){
						var userId = $('#grid').getRowData(item).id;
						param.push({name:'idList',value:userId});
					});
				} else {
					param.push({name:'idList',value:checkeds});
				}
				Public.deletex("确定删除选中的${functionNameSimple}？","${r'${ctx}'}/${moduleName}/${subModuleName}/${functionName}/delete", param, function(data){
					if(!!data.success) {
						Public.success('删除${functionNameSimple}成功');
						Public.doQuery();
					} else {
						Public.error(data.msg);
					}
				});
			} else {
				Public.error("请选择要删除的${functionNameSimple}!");
			}
		};
		<#if schemeCategory?contains('treeTable')>
        $('#dataGrid').on('click','.add',function(e){
			var url = "${r'${ctx}'}/${moduleName}/${subModuleName}/${functionName}/form?parentId="+$(this).attr('data-id');
        	Public.openOnTab('${functionName}-add', '添加${functionNameSimple}', url);
		});
        </#if>
		$('#dataGrid').on('click','.edit',function(e){
			var url = "${r'${ctx}'}/${moduleName}/${subModuleName}/${functionName}/form?id="+$(this).data('id');
		    Public.openOnTab('${functionName}-edit', '编辑${functionNameSimple}', url);
		});
		$('#dataGrid').on('click','.delete',function(e){
			del${className}($(this).data('id'));
		});
		$(document).on('click','#addBtn',function(e){
			var url = "${r'${ctx}'}/${moduleName}/${subModuleName}/${functionName}/form";
			Public.openOnTab('${functionName}-add', '添加${functionNameSimple}', url);
		});
		$(document).on('click','#delBtn',function(e){
			var checkeds = $('#grid').getGridParam('selarrrow');
			del${className}(checkeds);
		});
		<#if table.hasSort>
        $(document).on('click','#sortBtn',function(){
			var checkeds = that.getPostData();
			Public.executex("确定保存顺序？","${r'${ctx}'}/${moduleName}/${subModuleName}/${functionName}/sort",{
				postData: JSON.stringify(checkeds)
			},function(data){
				if(!!data.success){
					Public.success('修改成功');
					Public.doQuery();
				} else {
					Public.error(data.msg);
				}
			});
		});
		</#if>
		<#if table.publishColumn != null>
        $(document).on('click','#publishBtn',function(e){
			var params = Public.selectedRowIds();
			if( !!params && params.length >=1 ) {
				Public.executex("确定启用选中${functionNameSimple}？","${r'${ctx}'}/${moduleName}/${subModuleName}/${functionName}/publish", params, function(data){
					if(!!data.success) {
						Public.success('启用${functionNameSimple}成功');
						Public.doQuery();
					} else {
						Public.error(data.msg);
					}
				});
			} else {
				Public.error("请选择要启用的${functionNameSimple}!");
			}
		});
		$(document).on('click','#unpublishBtn',function(e){
			var params = Public.selectedRowIds();
			if( !!params && params.length >=1 ) {
				Public.executex("确定停用选中${functionNameSimple}？","${r'${ctx}'}/${moduleName}/${subModuleName}/${functionName}/unpublish", params, function(data){
					if(!!data.success) {
						Public.success('停用${functionNameSimple}成功');
						Public.doQuery();
					} else {
						Public.error(data.msg);
					}
				});
			} else {
				Public.error("请选择要停用的${functionNameSimple}!");
			}
		});
		</#if>
		<#if isImport == 1>
		//导入
		$(document).on('click','#impBtn',function(e){
			Public.openImportWindow($("#impDiv").html(), "#impForm", '${r'${ctx}'}/${moduleName}/${subModuleName}/${functionName}/doImport', '导入${functionNameSimple}');
		});
		</#if>
	}<#if table.hasSort>,
	getPostData : function(){
		if(curRow !== null && curCol !== null){
			 $("#grid").jqGrid("saveCell", curRow, curCol);
			 curRow = null;
			 curCol = null;
		}
		for (var t = [], e = $("#grid").jqGrid("getDataIDs"), i = 0, a = e.length; a > i; i++) {
			var r, n = e[i],  o = $("#grid").jqGrid("getRowData", n);
			r = {
   	            id: o.id,
   	            sort: o.sort
   	        };
   	        t.push(r);
		}
		return t;
	}
    </#if>
};
$(function(){
	THISPAGE._init();
});
</script>
<#if isImport == 1>
<script type="text/html" id="impDiv">
<div class="row-fluid">
   <form id="impForm" method="post" class="form-horizontal" enctype="multipart/form-data">
      <tags:token/>
      <div class="control-group formSep">
		<label class="control-label">选择文件(*.xls):</label>
		<div class="controls">
		     <input type="file" name="file" class="required selectFile"/>
		</div>
	  </div>
	  <div class="control-group formSep">
		<label class="control-label">选择模版:</label>
		<div class="controls">
		     <select id="templateId" name="templateId">
		         <c:forEach items="${r'${templates}'}" var="template">
			     <option value="${r'${template.id}'}">${r'${template.name}'}</option>
			     </c:forEach>
		     </select>
		</div>
	  </div>
   </form>
</div>
</script>
</#if> 
</body>
</html>