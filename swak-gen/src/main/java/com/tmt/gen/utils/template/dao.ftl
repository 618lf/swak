package ${packageName}.${moduleName}.${subModuleName}.dao; 

import org.springframework.stereotype.Repository;
import com.tmt.common.persistence.BaseDaoImpl;
import ${packageName}.${moduleName}.${subModuleName}.entity.${className};

/**
 * ${functionNameSimple} 管理
 * @author ${author}
 * @date ${date}
 */
@Repository("${prefix}${className}Dao")
public class ${className}Dao extends BaseDaoImpl<${className},${pk}> {}