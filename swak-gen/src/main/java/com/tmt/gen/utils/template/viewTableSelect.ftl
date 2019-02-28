<%@ page contentType="text/html;charset=UTF-8" session="false" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/pageHead.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title><sys:site/>管理后台</title>
<%@ include file="/WEB-INF/views/include/header.jsp"%>
</head>
<body>
<div class="wrapper-dialog">
    <div class="top">
		<form name="queryForm" id="queryForm" class="form-horizontal form-inline form-fluid">
	    <#list table.columns as c>
		<#if c.isQuery == 1>
		<div class="control-group">
		   <div class="controls">
		      <div class="input-prepend">
			  <span class="add-on">${c.comments}</span>
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
		</div>
		</#if>
		</#list>
		<div class="form-actions">
	      <input class="btn btn-primary query" type="button" value="查询"/>
	      <input class="btn reset" type="button" value="重置"/>
	   	</div>
	    </form>
	</div>
	<div id="dataGrid" class="autoGrid">
		<table id="grid"></table>
		<div id="page"></div>
	</div> 
</div>
<%@ include file="/WEB-INF/views/include/list-footer.jsp"%>
<script type="text/javascript">
var THISPAGE = {
	_init : function(){
		this.loadGrid();
		this.addEvent();
	},
	loadGrid : function(){
		var init = Public.setGrid();
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
				colNames: ['ID', <#list table.columns as c><#if c.isList == 1>'${c.comments}', </#if></#list>], 
				colModel: [
                    {name:'id', index:'id', width:80,sortable:false,hidden:true},
                    <#list table.columns as c>
                    <#if c.isList == 1>
                    {name:'${c.javaField}', index:'${c.javaField}', width:120, align:'left', sortable:false<#if c.javaField == 'sort'>, editable: !0, edittype:'text'</#if>},
                    </#if>
                    </#list>
				]
			})		
		);
		$('#grid').jqGrid('setFrozenColumns');
	},
	addEvent : function(){
		Public.initBtnMenu();
	}
};
$(function(){
	THISPAGE._init();
});
</script>
</body>
</html>