<%@ page contentType="text/html;charset=UTF-8" session="false" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/pageHead.jsp"%>
<!DOCTYPE html>
<html>
<head>
<title><sys:site/>管理后台</title>
<%@ include file="/WEB-INF/views/include/header.jsp"%>
</head>
<body>
<div class="row-fluid wrapper">
   <div class="bills">
    <div class="page-header">
		<h3>${functionNameSimple}<small> &gt;&gt; 编辑</small></h3>
	</div>
	<form:form id="inputForm" modelAttribute="${functionName}" action="${r'${ctx}'}/${moduleName}/${subModuleName}/${functionName}/save" method="post" class="form-horizontal">
		<tags:token/>
		<form:hidden path="id"/>
		<#if schemeCategory?contains('treeTable')>
        <form:hidden path="level"/>
		<form:hidden path="parentIds"/>
        </#if>
		<#list table.columns as c>
        <#if c.isEdit == 1>
        <div class="control-group formSep">
			<label class="control-label">${c.comments}<#if c.isNull == '0'><span class="red">*</span></#if>:</label>
			<div class="controls">
		    <#if c.showType == 'input' || c.showType == 'readonly'>
		      <form:input path="${c.javaField}" htmlEscape="false" maxlength="${c.length}" class="<#if c.isNull == '0'>required</#if> ${c.checkType}" <#if c.showType == 'readonly'>readonly="true"</#if>/>
		    <#elseif c.showType == 'textarea' || c.showType == 'richtext'>
		      <form:textarea path="${c.javaField}"  maxlength="${c.length}" cssStyle="width:100%;" rows="4"  class="<#if c.isNull == '0'>required</#if> ${c.checkType}"/>
		    <#elseif c.showType == 'y_n_r'>
		      <label class='radio inline'>
		       <form:radiobutton path="${c.javaField}" value="1" data-form='uniform'/>&nbsp;是
		      </label>
		      <label class='radio inline'>
		       <form:radiobutton path="${c.javaField}" value="0" data-form='uniform'/>&nbsp;否
		      </label>
		    <#elseif c.showType == 'y_n_s'>
		      <form:select path="${c.javaField}" items="${r'${fns:getYesAndNoLabels()}'}" itemLabel="label" itemValue="value" class="iSelect"></form:select>
		    <#elseif c.showType == 'n_a_d_r'>
		      <label class='radio inline'>
		       <form:radiobutton path="${c.javaField}" value="0" data-form='uniform'/>&nbsp;正常
		      </label>
		      <label class='radio inline'>
		       <form:radiobutton path="${c.javaField}" value="2" data-form='uniform'/>&nbsp;审核
		      </label>
		      <label class='radio inline'>
		       <form:radiobutton path="${c.javaField}" value="1" data-form='uniform'/>&nbsp;删除
		      </label>
		    <#elseif c.showType == 'n_a_d_s'>
		      <form:select path="${c.javaField}" items="${r'${fns:getStatusLabels()}'}" itemLabel="label" itemValue="value" class="iSelect"></form:select>
		    <#elseif c.showType == 'date'>
		      <input type="text" name="${c.javaField}" value="<fmt:formatDate value="${r'${'}${functionName}.${c.javaField}${r'}'}" pattern="yyyy-MM-dd HH:mm:ss"/>" readonly="readonly" maxlength="50" class="Wdate ${c.checkType}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});">
		    <#elseif c.showType == 'enum'>
		      <select name="${c.javaField}">
		        <c:forEach items="${r'${'}${c.javaField}s${r'}'}" var="item">
		        <option value="${r'${item}'}" <c:if test="${r'${'}${functionName}.${c.javaField} == item ${r'}'}">selected="selected"</c:if>>${r'${item.name}'}</option>
		        </c:forEach>
		      </select>
		    <#elseif c.showType == 'select' || c.showType == 'treeselect' || c.showType == 'tanselect'>
              <#if c.javaField?ends_with("userIds")||c.javaField?ends_with("userId")><#assign url='/system/user/treeSelect'></#if>
			  <#if c.javaField?ends_with("officeIds")||c.javaField?ends_with("officeId")><#assign url='/system/office/treeSelect'></#if>
			  <#if c.javaField?ends_with("areaIds")||c.javaField?ends_with("areaId")><#assign url='/system/area/treeSelect'></#if>
              <#if c.javaField?ends_with("parentIds")||c.javaField?ends_with("parentId")><#assign url='/${moduleName}/${subModuleName}/${functionName}/treeSelect'></#if>
			  <#if c.javaField?ends_with("Ids")>
              <tags:multiselect labelName="${(c.javaField?substring(0,c.javaField?index_of("Ids"))+'Names')}" labelValue="${r'${'}${functionName}.${(c.javaField?substring(0,c.javaField?index_of("Ids"))+'Names')}${r'}'}" name="${c.javaField}"
			        value="${r'${'}${functionName}.${c.javaField}${r'}'}" id="${c.javaField}Tag" defaultText="输入${c.comments} ..." selectUrl="${r'${ctx}'}${url}" title="${c.comments}" checked="true"></tags:multiselect>
              <#elseif c.javaField?ends_with("Id") && c.showType == 'tanselect'>
              <tags:treeselect id="${c.javaField}Tag" name="${c.javaField}" value="${r'${'}${functionName}.${c.javaField}${r'}'}" labelName="${(c.javaField?substring(0,c.javaField?index_of("Id"))+'Name')}" labelValue="${r'${'}${functionName}.${(c.javaField?substring(0,c.javaField?index_of("Id"))+'Name')}${r'}'}"
				    title="所属${c.comments}" url="${r'${ctx}'}${url}" extId="${r'${'}${functionName}.id${r'}'}" cssClass="required"></tags:treeselect>
              <#elseif c.javaField?ends_with("Id") && c.showType != 'tanselect'>
              <input type="text" name="${c.javaField}" value="${r'${'}${functionName}.${c.javaField}${r'}'}" maxlength="${c.length}" data-name="${r'${'}${functionName}.${(c.javaField?substring(0,c.javaField?index_of("Id"))+'Name')}${r'}'}">
              <#else>
              <form:input path="${c.javaField}" htmlEscape="false" maxlength="${c.length}" class="<#if c.isNull == '0'>required</#if>" <#if c.showType == 'readonly'>readonly="true"</#if>/>
              </#if>
            <#elseif c.showType == 'singleimg'>
              <tags:attachment name="${c.javaField}" value="${r'${'}${functionName}.${c.javaField}${r'}'}"/>
		    </#if>
			</div>
		</div>
        </#if>
        </#list>
		<div class="form-actions">
			<input id="submitBtn" class="btn btn-primary" type="submit" value="保 存"/>
			<input id="cancelBtn" class="btn" type="button" value="关闭"/>
		</div>
	</form:form>
  </div>
</div>
<%@ include file="/WEB-INF/views/include/form-footer.jsp"%>
<#if table.richtexts != null>
<script src="${ctxStatic}/ueditor/ueditor.config.js" type="text/javascript"></script>
<script src="${ctxStatic}/ueditor/ueditor.all.min.js" type="text/javascript"></script>
<script src="${ctxStatic}/ueditor/lang/zh-cn/zh-cn.js" type="text/javascript"></script>
</#if>
<script type="text/javascript">
var THISPAGE = {
	_init : function(){
		this.bindValidate();
		this.addEvent();
	},
	bindValidate : function() {
		$("#title").focus();
		$("#inputForm").validate(
			Public.validate()
		);
	},
	addEvent : function() {
	    <#if table.richtexts != null><#list table.richtexts as rich>Public.uEditor('${rich}');</#list></#if>    
        $(document).on('click','#cancelBtn',function(){
			Public.closeTab();
		});
	}
};
$(function(){
	THISPAGE._init();
});
</script>
</body>
</html>