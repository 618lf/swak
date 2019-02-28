package ${packageName}.${moduleName}.${subModuleName}.entity;

import java.io.Serializable;

<#if schemeCategory?contains('treeTable')>
import com.tmt.common.entity.BaseTreeEntity;
import com.tmt.common.utils.StringUtil3;
<#else>
import com.tmt.common.entity.BaseEntity;
</#if>
<#if table.hasDate>
import com.fasterxml.jackson.annotation.JsonFormat;
</#if>
/**
 * ${functionNameSimple} 管理
 * @author ${author}
 * @date ${date}
 */
public class ${className} extends <#if schemeCategory?contains('treeTable')>BaseTreeEntity<#else>BaseEntity</#if><${pk}> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	<#assign enumField=null>
	<#-- 生成字段属性 -->
	<#list table.columns as c>
	<#-- 如果不是基类属性 -->
	<#if !c.isBaseEntity && c.showType != 'enum'>
	private ${c.javaType} ${c.javaField}; <#if c.comments??>// ${c.comments}</#if>
	<#elseif !c.isBaseEntity && c.showType == 'enum'>
	private ${c.javaField?cap_first} ${c.javaField}; <#if c.comments??>// ${c.comments}</#if>
	</#if>
	<#if c.showType == 'enum'>
	<#assign enumField=c.javaField>
	</#if>
	</#list>
	<#-- 生成辅助查询属性 -->
	<#list table.columns as c>
	<#-- 如果不是基类属性 -->
	<#if !c.isBaseEntity && c.isQuery == 1 && c.queryType == 'between'>
	${'//查询字段'}
	private ${c.javaType} ${c.javaField}1;
	private ${c.javaType} ${c.javaField}2;
	</#if>
    <#if !c.isBaseEntity && c.isEdit == 1 && (c.javaField?ends_with("userId") || c.javaField?ends_with("officeId") || c.javaField?ends_with("areaId"))>
	<#assign hasName=false>
    <#list table.columns as co>
      <#if !co.isBaseEntity && co.javaField == c.javaField?substring(0,c.javaField?index_of("Id"))+"Name">
        <#assign hasName=true>
      </#if>
    </#list>
    <#if !hasName>
	private String ${c.javaField?substring(0,c.javaField?index_of("Id"))}Name;
	</#if>
    </#if>
    <#if !c.isBaseEntity && c.isEdit == 1 && (c.javaField?ends_with("userIds") || c.javaField?ends_with("officeIds") || c.javaField?ends_with("areaIds"))>
    <#assign hasName=false>
    <#list table.columns as co>
      <#if !c.isBaseEntity && co.javaField == c.javaField?substring(0,c.javaField?index_of("Ids"))+'Names'>
        <#assign hasName=true>
      </#if>
    </#list>
    <#if !hasName>	
    private String ${c.javaField?substring(0,c.javaField?index_of("Ids"))}Names;
	</#if>
    </#if>
	</#list>
	<#-- get、set方法 -->
	<#list table.columns as c> 
    <#-- 如果不是基类属性 -->
    <#if !c.isBaseEntity && c.showType != 'enum'>
    <#if c.showType == 'date'>@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")</#if>
    public ${c.javaType} get${c.javaField?cap_first}() {
		return ${c.javaField};
	}
	public void set${c.javaField?cap_first}(${c.javaType} ${c.javaField}) {
		this.${c.javaField} = ${c.javaField};
	}
	<#elseif !c.isBaseEntity && c.showType == 'enum'>
	public ${c.javaField?cap_first} get${c.javaField?cap_first}() {
		return ${c.javaField};
	}
	public String get${c.javaField?cap_first}Name() {
		return ${c.javaField}!=null?${c.javaField}.getName():"";
	}
	public void set${c.javaField?cap_first}(${c.javaField?cap_first} ${c.javaField}) {
		this.${c.javaField} = ${c.javaField};
	}
	</#if>
	</#list>
	<#-- get、set方法 -->
	<#list table.columns as c> 
    <#-- 如果不是基类属性 -->
    <#if !c.isBaseEntity && c.isQuery == 1 && c.queryType == 'between'>
    public ${c.javaType} get${c.javaField?cap_first}1() {
		return ${c.javaField}1;
	}
	public void set${c.javaField?cap_first}1(${c.javaType} ${c.javaField}) {
		this.${c.javaField}1 = ${c.javaField};
	}
	public ${c.javaType} get${c.javaField?cap_first}2() {
		return ${c.javaField}2;
	}
	public void set${c.javaField?cap_first}2(${c.javaType} ${c.javaField}) {
		this.${c.javaField}2 = ${c.javaField};
	}
	</#if>
    <#if !c.isBaseEntity && c.isEdit == 1 && (c.javaField?ends_with("userId") || c.javaField?ends_with("officeId") || c.javaField?ends_with("areaId"))>
	<#assign hasName=false>
    <#list table.columns as co>
      <#if !c.isBaseEntity && co.javaField == c.javaField?substring(0,c.javaField?index_of("Id"))+'Name'>
        <#assign hasName=true>
      </#if>
    </#list>
    <#if !hasName>
	public String get${c.javaField?substring(0,c.javaField?index_of("Id"))?cap_first}Name() {
		return ${c.javaField?substring(0,c.javaField?index_of("Id"))}Name;
	}
	public void set${c.javaField?substring(0,c.javaField?index_of("Id"))?cap_first}Name(String ${c.javaField?substring(0,c.javaField?index_of("Id"))}Name) {
		this.${c.javaField?substring(0,c.javaField?index_of("Id"))}Name = ${c.javaField?substring(0,c.javaField?index_of("Id"))}Name;
	}
	</#if>
	</#if>
    <#if !c.isBaseEntity && c.isEdit == 1 && (c.javaField?ends_with("userIds") || c.javaField?ends_with("officeIds") || c.javaField?ends_with("areaIds"))>
	<#assign hasName=false>
    <#list table.columns as co>
      <#if !c.isBaseEntity && co.javaField == c.javaField?substring(0,c.javaField?index_of("Ids"))+'Names'>
        <#assign hasName=true>
      </#if>
    </#list>
    <#if !hasName>
	public String get${c.javaField?substring(0,c.javaField?index_of("Ids"))?cap_first}Names() {
		return ${c.javaField?substring(0,c.javaField?index_of("Ids"))}Names;
	}
	public void set${c.javaField?substring(0,c.javaField?index_of("Ids"))?cap_first}Names(String ${c.javaField?substring(0,c.javaField?index_of("Ids"))}Names) {
		this.${c.javaField?substring(0,c.javaField?index_of("Ids"))}Names = ${c.javaField?substring(0,c.javaField?index_of("Ids"))}Names;
	}
	</#if>
	</#if>   
	</#list>
    <#if schemeCategory?contains('treeTable') && table.hasPath>
    /**
	 * 将父区域中的属性添加到子区域中
	 * 
	 * @param parent
	 */
    public void fillByParent(${className} parent) {
		super.fillByParent(parent);
		this.setPath((parent == null?"":parent.getPath())  + PATH_SEPARATE + this.getName());
	}
	/**
	 * 父节点的Path修改之后，所有的子节点都要修改
	 * 
	 * @param parent
	 * @param oldParentIds
	 */
	public void updatePathByParent(${className} parent, String oldParentIds, String oldPaths, Integer oldLevel) {
		super.updateIdsByParent(parent, oldParentIds, oldPaths, oldLevel);
		String _paths = (this.getPath()).replace(oldPaths, parent.getPath());
		if(StringUtil3.isBlank(_paths)) {
			_paths = new StringBuilder(PATH_SEPARATE).append(this.getName()).toString() ;
		}
		this.setPath(_paths);
	}
    </#if> 
	<#if enumField != null>
	${'//定义枚举,自行定义枚举具体值'}
	public enum ${enumField?cap_first} {
	  ENUM_1("ENUM_1"), ENUM_2("ENUM_2");
	  private String name;
	  private ${enumField?cap_first}(String name){
		this.name = name;
	  }
	  public String getName() {
		return name;
	  }
	  public void setName(String name) {
		this.name = name;
	  }
	}
	</#if>
}