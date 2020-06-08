package ${packageName}.dao; 

import org.springframework.stereotype.Repository;
import com.tmt.core.persistence.BaseDaoImpl;
import ${packageName}.entity.${className};

/**
 * @author
 * @date ${date}
 */
@Repository("${moduleName}${className}Dao")
public class ${className}Dao extends BaseDaoImpl<${className},${pk}> {}