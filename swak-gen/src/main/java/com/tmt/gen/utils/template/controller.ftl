package ${packageName}.${moduleName}.${subModuleName}.web;

import java.util.List;
<#if treeSelect == 1 || schemeCategory?contains('treeTable') || table.hasSort>
import java.util.Map;
</#if>
<#if schemeCategory?contains('treeTable') || table.hasSort>
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
</#if>
<#if !(schemeCategory?contains('treeTable') || table.hasSort) && (isImport == 1 || isExport == 1)>
import javax.servlet.http.HttpServletRequest;
</#if>
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
<#if treeSelect == 1>
import org.springframework.web.bind.annotation.RequestParam;
</#if>
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
<#if isImport == 1>
import org.springframework.web.multipart.MultipartFile;
</#if>
<#if schemeCategory?contains('treeTable') && treeSelect == 1>
import com.tmt.system.entity.TreeVO;
</#if>
<#if schemeCategory?contains('treeTable')>
import com.tmt.system.utils.TreeEntityUtils;
</#if>
<#if table.hasSort || chemeCategory?contains('treeTable')>
import com.tmt.common.utils.JsonMapper;
</#if>
import com.tmt.common.persistence.Page;
<#if !schemeCategory?contains('treeTable')>
import com.tmt.common.persistence.PageParameters;
import com.tmt.common.persistence.QueryCondition;
import com.tmt.common.persistence.QueryCondition.Criteria;
</#if>
import com.tmt.common.utils.Lists;
<#if isExport == 1 || treeSelect == 1 || schemeCategory?contains('treeTable')>
import com.tmt.common.utils.Maps;
</#if>
import com.tmt.common.entity.AjaxResult;
import com.tmt.common.config.Globals;
import com.tmt.common.persistence.incrementer.IdGen;
import com.tmt.common.utils.StringUtil3;
<#if isImport != 1 && isExport != 1>
import com.tmt.common.web.BaseController;
</#if>
<#if isImport == 1 && isExport != 1>
import com.tmt.common.web.BaseImpController;
</#if>
<#if isImport != 1 && isExport == 1>
import com.tmt.common.web.ExportController;
</#if>
<#if isExport_Import == 1>
import com.tmt.common.web.BaseImpExportController;
</#if>
<#if isImport == 1>
import com.tmt.common.utils.TemplateExcelUtil;
import com.tmt.system.entity.ExcelTemplate;
import com.tmt.system.service.ExcelTemplateService;
</#if>

import com.tmt.common.utils.WebUtils;
import ${packageName}.${moduleName}.${subModuleName}.entity.${className};
import ${packageName}.${moduleName}.${subModuleName}.service.${className}Service;
<#list table.columns as c>
<#if !c.isBaseEntity && c.showType == 'enum' && c.isEdit>
import ${packageName}.${moduleName}.${subModuleName}.entity.${className}.${c.javaField?cap_first};
</#if>
</#list>

/**
 * ${functionNameSimple} 管理
 * @author ${author}
 * @date ${date}
 */
@Controller("${prefix}${className}Controller")
@RequestMapping(value = "${r'${adminPath}'}/${moduleName}/${subModuleName}/${functionName}")
public class ${className}Controller extends <#if isExport_Import == 1>BaseImpExportController<${className}></#if><#if isImport == 1 && isExport != 1>BaseImpController</#if><#if isImport != 1 && isExport == 1>ExportController</#if><#if isImport != 1 && isExport != 1>BaseController</#if>{
	
	@Autowired
	private ${className}Service ${functionName}Service;
	<#if isImport == 1>
	@Autowired
	private ExcelTemplateService templateService;
	</#if>
	
	
	/**
	 * 列表初始化页面
	 * @param model
	 */
	@RequestMapping("list")
	public String list(${className} ${functionName}, Model model){
	    <#if isImport == 1>
	    List<ExcelTemplate> templates = this.templateService.queryByTargetClass(${className}.class.getName());
		model.addAttribute("templates", templates);
		</#if>
        <#list table.columns as c>
		<#if !c.isBaseEntity && c.showType == 'enum' && c.isEdit>
		model.addAttribute("${c.javaField}s", ${c.javaField?cap_first}.values());
		</#if>
		</#list>
        <#if schemeCategory?contains('treeTable')>
        if(${functionName} != null && !IdGen.isInvalidId(${functionName}.getId())) {
			model.addAttribute("id", ${functionName}.getId());
		}
        </#if>
		return "/${moduleName}/${subModuleName}/${className}List";
	}
	
	/**
	 * 列表页面的数据 
	 * @param ${functionName}
	 * @param model
	 * @param page
	 * @return Page Json
	 */
	@ResponseBody
	@RequestMapping("page")
	public Page page(${className} ${functionName}, Model model<#if !schemeCategory?contains('treeTable')>, Page page</#if>){
	    <#if schemeCategory?contains('treeTable')>
        Map<String,Object> params = Maps.newHashMap();
		if(${functionName}!=null && !StringUtil3.isBlank(${functionName}.getName())) {
		   params.put("NAME", ${functionName}.getName());
		}
		if(!params.isEmpty()) {
		   List<${className}> menus = this.${functionName}Service.findByCondition(params);
		   if( menus != null && menus.size() != 0 ) {
			  StringBuffer sb = new StringBuffer(100);
			  for( ${className} menuItem: menus ) {
				 sb.append(menuItem.getParentIds());
				 sb.append(menuItem.getId()).append(",");
			  }
			  sb.append("-1");
			  params.clear();
			  params.put("IDS", sb.toString());
		   }
		}
		if(${functionName}!=null && ${functionName}.getId() != null){
		   ${functionName} = this.${functionName}Service.get(${functionName}.getId());
		}
		List<${className}> ${functionName}s = this.${functionName}Service.findByCondition(params);
		if( ${functionName}s != null) {
			for(${className} categoryItem : ${functionName}s){
				categoryItem.setId(categoryItem.getId());
				categoryItem.setParent(categoryItem.getParentId());
				categoryItem.setLevel(categoryItem.getLevel());
				categoryItem.setExpanded(Boolean.TRUE);
				categoryItem.setLoaded(Boolean.TRUE);
				categoryItem.setIsLeaf(Boolean.TRUE);
				if( categoryItem!=null && categoryItem.getId() != null && ( (","+categoryItem.getParentIds()+",").indexOf(","+categoryItem.getId()+",") != -1)) {
					categoryItem.setExpanded(Boolean.TRUE);
				}
			}
		}
		List<${className}> copyMenus = TreeEntityUtils.sort(${functionName}s);
		if( copyMenus != null && copyMenus.size() != 0 && !(${functionName}!=null && ${functionName}.getId() != null)) {
			copyMenus.get(0).setExpanded(Boolean.TRUE);
		}
		Page page = new Page();
        page.setData(copyMenus);
        return page;
        <#else>
        QueryCondition qc = new QueryCondition();
		PageParameters param = page.getParam();
		Criteria c = qc.getCriteria();
		             this.initQc(${functionName}, c);
		return ${functionName}Service.queryForPage(qc, param);
        </#if>
	}
	
	/**
	 * 表单
	 * @param ${functionName}
	 * @param model
	 */
	@RequestMapping("form")
	public String form(${className} ${functionName}, Model model) {
	    if(${functionName} != null && !IdGen.isInvalidId(${functionName}.getId())) {
		   ${functionName} = this.${functionName}Service.get(${functionName}.getId());
		} else {
		   if(${functionName} == null) {
			  ${functionName} = new ${className}();
		   }
		   ${functionName}.setId(IdGen.INVALID_ID);
		   <#if schemeCategory?contains('treeTable')>
           if(${functionName}.getParentId() == null){
			  ${functionName}.setParentId(IdGen.ROOT_ID);
		   }
		   </#if>
		}
		<#if schemeCategory?contains('treeTable')>
		${className} parent = this.${functionName}Service.get(${functionName}.getParentId());
  		${functionName}.setParentId(parent.getId());
  		${functionName}.setParentName(parent.getName());
        </#if>
		<#list table.columns as c>
		<#if !c.isBaseEntity && c.showType == 'enum' && c.isEdit>
		model.addAttribute("${c.javaField}s", ${c.javaField?cap_first}.values());
		</#if>
		</#list>
		model.addAttribute("${functionName}", ${functionName});
		return "/${moduleName}/${subModuleName}/${className}Form";
	}
	
	/**
	 * 保存
	 * @param category
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping("save")
	public String save(${className} ${functionName}, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, ${functionName})){
			return form(${functionName}, model);
		}
		this.${functionName}Service.save(${functionName});
		addMessage(redirectAttributes, StringUtil3.format("%s'%s'%s", "修改${functionNameSimple}", ${functionName}.get${firstStringField?cap_first}(), "成功"));
		redirectAttributes.addAttribute("id", ${functionName}.getId());
		return WebUtils.redirectTo(Globals.getAdminPath(), "/${moduleName}/${subModuleName}/${functionName}/form");
	}
	
	/**
	 * 删除
	 * @param idList
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@ResponseBody
	@RequestMapping("delete")
	public AjaxResult delete(Long[] idList) {
		List<${className}> ${functionName}s = Lists.newArrayList();
		for(Long id: idList) {
            <#if schemeCategory?contains('treeTable')>
			${className} ${functionName} = new ${className}();
			${functionName}.setId(id);
			int iCount = this.${functionName}Service.delete${className}Check(${functionName});
			if(iCount > 0) {
			   return AjaxResult.error("选择的栏目中包含子栏目,不能删除");
			}
			//验证
			${functionName}s.add(${functionName});
			<#else>
			${className} ${functionName} = new ${className}();
			${functionName}.setId(id);
			${functionName}s.add(${functionName});
			</#if>
		}
		this.${functionName}Service.delete(${functionName}s);
		return AjaxResult.success();
	}
	
	<#if table.publishColumn != null>
	/**
	 * 启用
	 * @param idList
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@ResponseBody
	@RequestMapping("publish")
	public AjaxResult publish(Long[] idList, Model model, HttpServletResponse response) {
		List<${className}> ${functionName}s = Lists.newArrayList();
		for(Long id: idList) {
            ${className} ${functionName} = new ${className}();
			${functionName}.setId(id);
			${functionName}.set${table.publishColumn.javaField?cap_first}(${className}.YES);
			${functionName}s.add(${functionName});
		}
		this.${functionName}Service.updatePublish(${functionName}s);
		return AjaxResult.success();
	}
	
	/**
	 * 停用
	 * @param idList
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@ResponseBody
	@RequestMapping("unpublish")
	public AjaxResult unpublish(Long[] idList, Model model, HttpServletResponse response) {
		List<${className}> ${functionName}s = Lists.newArrayList();
		for(String id: idList) {
            ${className} ${functionName} = new ${className}();
			${functionName}.setId(id);
			${functionName}.set${table.publishColumn.javaField?cap_first}(${className}.NO);
			${functionName}s.add(${functionName});
		}
		this.${functionName}Service.updatePublish(${functionName}s);
		return AjaxResult.success();
	}
	</#if>

    <#if table.hasSort>
    /**
	 * 批量修改栏目排序
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("sort")
	public AjaxResult updateSort(Model model, HttpServletRequest request, HttpServletResponse response) {
		String postData = request.getParameter("postData");
		List<Map<String,String>> maps = JsonMapper.fromJson(postData, ArrayList.class);
		if(maps != null && maps.size() != 0){
			List<${className}> menus = Lists.newArrayList();
			for(Map<String,String> map: maps) {
				${className} menu = new ${className}();
				menu.setId(Long.parseLong(map.get("id")));
				menu.setSort(Integer.parseInt(map.get("sort")));
				menus.add(menu);
			}
			this.${functionName}Service.updateSort(menus);
			return AjaxResult.success();
		}
		return AjaxResult.error("没有需要保存的数据");
	}
    </#if>

	<#if !schemeCategory?contains('treeTable')>
	//查询条件
	private void initQc(${className} ${functionName}, Criteria c) {
	   <#list table.columns as c>
	   <#if c.isQuery == 1>
	   <#if c.javaType != 'java.util.Date' && c.showType != 'enum'> 
	   <#if c.javaType == 'String'> 
        if(StringUtil3.isNotBlank(${functionName}.get${c.javaField?cap_first}())) {
       </#if>
       <#if c.javaType != 'String'> 
        if(${functionName}.get${c.javaField?cap_first}() != null) {
       </#if>
           <#if c.queryType == '＝'>
           c.andEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '!＝'>
           c.andNotEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＞'>
           c.andGreaterThan("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＞＝'>
           c.andGreaterThanOrEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＜'>
           c.andLessThan("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＜＝'>
           c.andLessThanOrEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == 'between'>
           c.andBetween("${c.name}", ${functionName}.get${c.javaField?cap_first}1(), ${functionName}.get${c.javaField?cap_first}2());
           </#if>
           <#if c.queryType == 'like'>
           c.andLike("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == 'left_like'>
           c.andLeftLike("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == 'right_Like'>
           c.andRightLike("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
        }
	   <#elseif c.javaType == 'java.util.Date'> 
        if(${functionName}.get${c.javaField?cap_first}() != null) {
           <#if c.queryType == '＝'>
           c.andDateEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '!＝'>
           c.andDateNotEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＞'>
           c.andDateGreaterThan("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＞＝'>
           c.andDateGreaterThanOrEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＜'>
           c.andDateLessThan("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＜＝'>
           c.andDateLessThanOrEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == 'between'>
           c.andBetween("${c.name}", ${functionName}.get${c.javaField?cap_first}1(), ${functionName}.get${c.javaField?cap_first}2());
           </#if>
        }
       <#else> 
        if(${functionName}.get${c.javaField?cap_first}() != null) {
           <#if c.queryType == '＝'>
           c.andEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '!＝'>
           c.andNotEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＞'>
           c.andGreaterThan("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＞＝'>
           c.andGreaterThanOrEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＜'>
           c.andLessThan("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == '＜＝'>
           c.andLessThanOrEqualTo("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == 'between'>
           c.andBetween("${c.name}", ${functionName}.get${c.javaField?cap_first}1(), ${functionName}.get${c.javaField?cap_first}2());
           </#if>
           <#if c.queryType == 'like'>
           c.andLike("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == 'left_like'>
           c.andLeftLike("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
           <#if c.queryType == 'right_Like'>
           c.andRightLike("${c.name}", ${functionName}.get${c.javaField?cap_first}());
           </#if>
        }
	   </#if>
	   </#if>
	   </#list>
	}
    </#if>
	<#if isImport == 1>
	
	//---------------导入---------------
	/**
	 * 导入
	 * @param templateId
	 * @param file
	 * @return
	 */
	 @Override
    public AjaxResult doImport(Long templateId, HttpServletRequest request, MultipartFile file) {
		AjaxResult result = TemplateExcelUtil.fetchObjectFromTemplate(templateId, file);
		if(result != null && result.getSuccess()) {
		   List<${className}> items = result.getObj();
		   this.${functionName}Service.batchImport(items);
		}
		return result;
	}
    </#if>
    <#if isExport == 1>
    
    //---------------导出（暂时有问题）---------------
    /**
	 * 导出数据封装
	 */
    @Override
	public Map<String, Object> doExport(${className} ${functionName}, HttpServletRequest request) {
		Map<String, Object> datas = Maps.newHashMap();
		
		return datas;
	}

	/**
	 * 导出实体数据
	 */
	@Override
	protected Class<${className}> getTargetClass() {
		return ${className}.class;
	}
    </#if>
    <#if treeSelect == 1>
    <#if !schemeCategory?contains('treeTable')>
    
    /**
	 * 树组件支持
	 */
	@ResponseBody
	@RequestMapping("treeSelect")
	public List<Map<String, Object>> treeSelect(@RequestParam(required=false)String extId, HttpServletResponse response) {
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<${className}> ${functionName}s = this.${functionName}Service.getAll();
		for(int i=0; i< ${functionName}s.size(); i++){
			${className} e = ${functionName}s.get(i);
			Map<String, Object> map = Maps.newHashMap();
			map.put("id", e.getId());
			map.put("pId", "O_-1");
			map.put("name", e.getName());
			mapList.add(map);
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("id", "O_-1");
		map.put("pId", "O_-2");
		map.put("name", "${functionNameSimple}");
		mapList.add(map);
		return mapList;
	}
	<#else>
    /**
	 * 树组件支持
	 */
	@ResponseBody
	@RequestMapping("treeSelect")
	public List<Map<String, Object>> treeSelect(@RequestParam(required=false)String extId, HttpServletResponse response) {
		Map<String,Object> params = Maps.newHashMap();
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<TreeVO> trees = this.${functionName}Service.findTreeList(params);
		for(int i=0; i<trees.size(); i++){
			TreeVO e = trees.get(i);
			if (extId == null || (extId!=null && !extId.equals(e.getId()) && e.getParentIds().indexOf(","+extId+",")==-1)){
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent());
				map.put("name", e.getTreeName());
				map.put("module", e.getTreeType());
				mapList.add(map);
			}
		}
		return mapList;
	}
	</#if>
	</#if>
	
	<#if tableSelect == 1>
	/**
	 * 表组件支持
	 */
	@RequestMapping("tableSelect")
	public String tableSelect() {
	   return "/${moduleName}/${subModuleName}/${className}TableSelect";
	}
	</#if>
}