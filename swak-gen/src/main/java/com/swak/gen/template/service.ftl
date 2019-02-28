package ${packageName}.${moduleName}.${subModuleName}.service;

import java.util.List;
<#if schemeCategory?contains('treeTable')>
import java.util.Map;
</#if>

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

<#if schemeCategory?contains('treeTable')>
import com.tmt.common.utils.Maps;
import com.tmt.system.entity.TreeVO;
</#if>
import com.tmt.common.persistence.BaseDao;
import com.tmt.common.persistence.incrementer.IdGen;
import com.tmt.common.service.BaseService;
import ${packageName}.${moduleName}.${subModuleName}.dao.${className}Dao;
import ${packageName}.${moduleName}.${subModuleName}.entity.${className};

/**
 * ${functionNameSimple} 管理
 * @author ${author}
 * @date ${date}
 */
@Service("${prefix}${className}Service")
public class ${className}Service extends BaseService<${className},${pk}> {
	
	@Autowired
	private ${className}Dao ${functionName}Dao;
	
	@Override
	protected BaseDao<${className}, ${pk}> getBaseDao() {
		return ${functionName}Dao;
	}
	
	/**
	 * 保存
	 */
	@Transactional
	public void save(${className} ${functionName}) {
	    <#if schemeCategory?contains('treeTable')>
        ${className} parent = this.get(${functionName}.getParentId());
        String oldParentIds = ${functionName}.getParentIds();
		Integer oldLevel = ${functionName}.getLevel(); 
		String oldPath = ${functionName}.getPath();
		${functionName}.fillByParent(parent);
        </#if>
		if(IdGen.isInvalidId(${functionName}.getId())) {
			this.insert(${functionName});
		} else {
			this.update(${functionName});
            <#if schemeCategory?contains('treeTable')>
            List<${className}> children = this.findByParent(${functionName});
			for( ${className} e : children ) {
				e.updateIdsByParent(${functionName}, oldParentIds, oldPath, oldLevel);
			}
			this.batchUpdate(children);
			</#if>
		}
	}
	
    <#if schemeCategory?contains('treeTable')>
    public List<${className}> findByParent(${className} parent){
		Map<String,Object> params = Maps.newHashMap();
		params.put("PARENT_IDS",  parent.getId());
		return this.queryForList("findByCondition", params);
	}
    public List<${className}> findByCondition(Map<String,Object> params) {
		return this.queryForList("findByCondition", params);
	}
	public List<TreeVO> findTreeList(Map<String,Object> params) {
		return this.queryForGenericsList("findTreeList", params);
	}
	public int delete${className}Check(${className} ${functionName}){
		return this.countByCondition("delete${className}Check", ${functionName});
	}
    </#if>
	
    <#if table.hasSort>
    @Transactional
	public void updateSort(List<${className}> ${functionName}s ) {
		this.batchUpdate("updateSort", ${functionName}s);
	}
    </#if>
	/**
	 * 删除
	 */
	@Transactional
	public void delete(List<${className}> ${functionName}s) {
		this.batchDelete(${functionName}s);
	}
	<#if table.publishColumn != null>
	/**
	 * 修改发布状态
	 */
	@Transactional
	public void updatePublish(List<${className}> ${functionName}s) {
		this.batchUpdate("updatePublish", ${functionName}s);
	}
	</#if>
	
	<#if isImport == 1>
	/**
	 * 批量导入
	 * @param Business
	 */
	@Transactional
	public void batchImport(List<${className}> ${functionName}s){
		if( ${functionName}s != null && ${functionName}s.size() != 0) {
			List<${className}> olds = this.getAll();
			this.batchDelete(olds);
			this.batchInsert(${functionName}s);
		}
	}
	</#if>
}