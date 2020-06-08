package ${packageName}.entity;

import java.io.Serializable;

import com.tmt.core.entity.BaseEntity;
<#if table.hasDate>
import com.alibaba.fastjson.annotation.JSONField;
</#if>

/**
 * @author 
 * @date ${date}
 */
public class ${className} extends BaseEntity<${pk}> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	<#-- 生成字段属性 -->
	<#list table.columns as c>
	<#-- 如果不是基类属性 -->
	<#if !c.isBaseEntity>
	<#if c.javaType == 'java.util.Date'>@JSONField(format="yyyy-MM-dd HH:mm:ss")</#if>
	private ${c.javaType} ${c.javaField};
	</#if>
	</#list>
	<#-- get、set方法 -->
	<#list table.columns as c> 
    <#-- 如果不是基类属性 -->
    <#if !c.isBaseEntity>
    public ${c.javaType} get${c.javaField?cap_first}() {
		return ${c.javaField};
	}
	public void set${c.javaField?cap_first}(${c.javaType} ${c.javaField}) {
		this.${c.javaField} = ${c.javaField};
	}
	</#if>
	</#list>
}