package ${packageName}.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tmt.core.persistence.BaseDao;
import com.tmt.core.persistence.incrementer.IdGen;
import com.tmt.core.service.BaseService;
import ${packageName}.dao.${className}Dao;
import ${packageName}.entity.${className};

/**
 * @author
 * @date ${date}
 */
@Service("${moduleName}${className}Service")
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
		if(IdGen.isInvalidId(${functionName}.getId())) {
			this.insert(${functionName});
		} else {
			this.update(${functionName});
		}
	}
	
	/**
	 * 删除
	 */
	@Transactional
	public void delete(List<${className}> ${functionName}s) {
		this.batchDelete(${functionName}s);
	}
}