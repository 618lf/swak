package com.swak.persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.swak.entity.BaseEntity;
import com.swak.entity.IdEntity;
import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.exception.StaleObjectStateException;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;

/**
 * 数据库操作支持类
 * @author TMT
 */
@SuppressWarnings({ "unchecked"})
public class BaseDaoImpl<T, PK> implements BaseDao<T, PK>{

	protected static Logger logger = LoggerFactory.getLogger(BaseDaoImpl.class);
	
	protected final static String EXISTS = "exists";
	protected final static String LOCK = "lock";
	protected final static String INSERT = "insert";
    protected final static String UPDATE = "update";
    protected final static String DELETE = "delete";
    protected final static String VERSION = "version";
    protected final static String GET = "get";
    protected final static String GET_ALL = "getAll";
    protected final static String FIND_BY_CONDITION = "findByCondition";
    protected final static String COUNT_BY_CONDITION = "findByConditionStat";
    protected final static String BATCH_INSERT = "batchInsert";
    protected String NAMESPACE = null;
    
	/**
	 * 注入SqlSessionTemplate实例(要求Spring中进行SqlSessionTemplate的配置).<br/>
	 * 可以调用sessionTemplate完成数据库操作.
	 */
	@Autowired
	private SqlSessionTemplate sessionTemplate;
	
	/**
	 * 默认的命名空间
	 * @return
	 */
    protected String getNamespace(){
    	return NAMESPACE !=null ? NAMESPACE : (NAMESPACE = this.getClass().getName());
    }
    
    /**
	 * 默认的命名空间 + 操作
	 * @return
	 */
    protected String getStatementName(String operate) {
        String namespace = getNamespace();
        return StringUtils.hasText(namespace) ? (new StringBuilder(namespace).append(".").append(operate).toString()) : namespace;
    }

    /**
     * 执行器
     * @return
     */
    protected SqlSessionTemplate getSqlRunner() {
        return this.sessionTemplate;
    }
    
    /**
     * 版本比较
     */
    @Override
    public void compareVersion(T entity) {
    	PK pk = null;Integer myVersion = null;
    	if(entity instanceof BaseEntity) {
    	   pk = ((IdEntity<PK>)entity).getId();
    	   myVersion = ((IdEntity<PK>)entity).getVersion();
    	   myVersion = (myVersion ==null?0:myVersion)+1;
    	   ((IdEntity<PK>)entity).setVersion(myVersion);
    	}
    	Integer version = (Integer) this.getSqlRunner().selectOne(VERSION, pk);
    	version = version==null?0:version;
    	if (!(Integer.valueOf(myVersion).compareTo(version)>0) ) {
    		throw new StaleObjectStateException("数据版本过低");
    	}
    }

    /**
     * 插入数据
     */
    @Override
	public PK insert(String statementName, T entity) {
		PK pk = null;
		if(entity instanceof IdEntity) {
		   pk = ((IdEntity<PK>)entity).prePersist();
		}
		this.getSqlRunner().insert(getStatementName(statementName), entity);
		return pk;
    }
	
    /**
     * 插入数据
     */
	@Override
    public PK insert(T entity) {
    	return this.insert(INSERT, entity);
    }
    
	/**
	 * 删除
	 */
    @Override
    public int delete(T entity) {
    	return this.getSqlRunner().delete(getStatementName(DELETE), entity);
    }
    
    /**
     * 删除
     */
    @Override
    public int delete(String statementName, T entity) {
    	return this.getSqlRunner().delete(getStatementName(statementName),entity);
    }

    /**
     * 修改
     */
    @Override
    public int update(String statementName, T entity) {
    	if(entity instanceof BaseEntity) {
           ((BaseEntity<PK>)entity).preUpdate();
        }
		return this.getSqlRunner().update(getStatementName(statementName), entity);
    }
    
    /**
     * 修改
     */
    @Override
    public int update(T entity) {
    	return this.update(UPDATE,entity);
    }

    /**
     * 主键查询
     */
    @Override
    public T get(PK id) {
    	return (T) this.getSqlRunner().selectOne(getStatementName(GET),id);
    }

    /**
     * 表锁
     */
    @Override
    public boolean lock(Object entity) {
    	Integer count =  this.getSqlRunner().selectOne(getStatementName(LOCK), entity);
    	return count != null && count==1;
    }
    
    /**
     * 是否存在
     * @param id
     * @return
     */
    @Override
    public boolean exists(PK id) {
    	Integer count = this.countByCondition(EXISTS, id);
    	return count != null && count==1;
    }
    
    /**
     * 查询所有数据
     */
    @Override
    public List<T> getAll() {
    	return this.getSqlRunner().selectList(getStatementName(GET_ALL));
    }
    
    /**
     * 条件查询
     */
    @Override
    public List<T> queryByCondition(QueryCondition qc){
    	return this.getSqlRunner().selectList(getStatementName(FIND_BY_CONDITION),qc);
    }
    
    /**
     * 列表查询
     */
    @Override
    public List<T> queryForList(String statementName) {
    	return this.getSqlRunner().selectList(getStatementName(statementName), null);
    }
    
    /**
     * 列表查询
     */
    @Override
    public List<T> queryForList(String statementName, Object entity) {
    	return this.getSqlRunner().selectList(getStatementName(statementName), entity);
    }
    
    /**
     * 列表查询
     */
    @Override
    public List<T> queryForList(String statementName, QueryCondition qc) {
    	return this.getSqlRunner().selectList(getStatementName(statementName), qc);
    }
    
    /**
     * 列表查询
     */
    @Override
    public List<T> queryForList(String statementName, Map<String,?> params) {
    	return this.getSqlRunner().selectList(getStatementName(statementName), params);
    }
    
    /**
     * 获取前几个
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public List<T> queryForLimitList(QueryCondition qc, int size) {
        return this.getSqlRunner().selectList(getStatementName(FIND_BY_CONDITION), qc, new RowBounds(0, size));
    }
    
    /**
     * 获取前几个
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public List<T> queryForLimitList(String sql, QueryCondition qc, int size) {
		return this.getSqlRunner().selectList(getStatementName(sql), qc, new RowBounds(0, size));
    }
    
    /**
     * 获取前几个
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public List<T> queryForLimitList(String sql, Map<String,?> qc, int size) {
        return this.getSqlRunner().selectList(getStatementName(sql), qc, new RowBounds(0, size));
    }
    
    /**
     * 获取前几个
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public List<T> queryForLimitList(String sql, Object qc, int size) {
        return this.getSqlRunner().selectList(getStatementName(sql), qc, new RowBounds(0, size));
    }
    
    /**
     * 查询
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public <E> List<E> queryForGenericsList(String statementName, Map<String,?> params) {
    	return this.getSqlRunner().selectList(getStatementName(statementName), params);
    }
    
    /**
     * 查询
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public <E> List<E> queryForGenericsList(String statementName, Object entity) {
    	return this.getSqlRunner().selectList(getStatementName(statementName), entity);
    }
    
    /**
     * 查询
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public List<Map<String,Object>> queryForMapList(String statementName, Map<String,?> params) {
    	return this.getSqlRunner().selectList(getStatementName(statementName), params);
    }
    
    /**
     * 查询ID
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public List<PK> queryForIdList(String statementName, Map<String,?> params) {
    	return this.getSqlRunner().selectList(getStatementName(statementName), params);
    }
    
    /**
     * 查询单个数据
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public T queryForObject(String statementName) {
    	return this.getSqlRunner().selectOne(getStatementName(statementName), null);
    }
    
    /**
     * 查询单个数据
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public T queryForObject(String statementName, Object entity) {
    	return (T) this.getSqlRunner().selectOne(getStatementName(statementName), entity);
    }
    
    /**
     * 查询单个数据
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public T queryForObject(String statementName, Map<String,?> params) {
    	return (T)(this.getSqlRunner().selectOne( getStatementName(statementName), params));
    }
    
    /**
     * 查询单个数据
     * @param qc
     * @param size   
     * @return
     */
    @Override
    public T queryForObject(QueryCondition qc) {
    	return (T)(this.getSqlRunner().selectOne(getStatementName(FIND_BY_CONDITION), qc));
    }
    
    /**
     * 读取属性
     * @param statementName
     * @param entity
     * @return
     */
    @Override
    public <E> E queryForAttr(String statementName, Object entity) {
    	return (E) this.getSqlRunner().selectOne(getStatementName(statementName), entity);
    }
    
    /**
     * 分页查询
     * @param statementName
     * @param entity
     * @return
     */
    @Override
    public Page queryForMapPageList(String sql, Map<String,?> args, Parameters param) {
    	return this.queryForPage(sql, args, param);
    }

    /**
     * 分页查询
     * @param statementName
     * @param entity
     * @return
     */
    @Override
    public Page queryForPageList(String sql, Map<String,?> args, Parameters param) {
    	return this.queryForPage(sql, args, param);
    }
    
    /**
     * 分页查询
     * @param statementName
     * @param entity
     * @return
     */
    @Override
    public Page queryForPageList(String sql, QueryCondition args, Parameters param) {
    	return this.queryForPage(sql, args, param);
    }
    
    /**
     * 分页查询
     * @param statementName
     * @param entity
     * @return
     */
    @Override
    public Page queryForPage(QueryCondition args, Parameters param) {
        return this.queryForPage(FIND_BY_CONDITION, args, param);
    }
    
    /**
     * 通用的分页处理
     * @param sql
     * @param args
     * @param param
     * @return
     */
    protected <E> Page queryForPage(String sql, Object args, Parameters param) {
    	 int pageNum = param.getPageIndex();
         int pageSize = param.getPageSize();
         Integer count = 0;
         List<E> lst = null;
         if (pageNum == Parameters.NO_PAGINATION  || pageSize == Parameters.NO_PAGINATION) {
             lst = this.getSqlRunner().selectList(sql, args);
         } else {
        	 count = (Integer) this.getSqlRunner().selectOne(getStatementName(new StringBuilder(sql).append("Stat").toString()),args);
         	 count = count==null?0:count;
             if(count == 0) {
                lst = Lists.newArrayList();
             } else {
                int pageCount = getPageCount(count, pageSize);
                if(pageNum > pageCount){ pageNum = pageCount;}
                lst = this.getSqlRunner().selectList(getStatementName(sql), args, new RowBounds((pageNum - 1) * pageSize, pageSize));
             }
         }
         param.setRecordCount(count);
         return new Page(param, lst);
    }
    
    private int getPageCount(int recordCount, int pageSize) {
        if(recordCount == 0) return 0;
        return recordCount%pageSize > 0?((recordCount/pageSize)+1): (recordCount/pageSize);
    }
    
    /** 
     * 根据条件查询个数
	 * @param qc
	 * @return 个数
	 * @author sea
	 */
    @Override
	public Integer countByCondition(QueryCondition qc){
    	return (Integer) this.getSqlRunner().selectOne(getStatementName(COUNT_BY_CONDITION),qc);
	}
	
	/**
	 * 批量新增 
	 * @Title: batchInsert 
	 * @Description: 批量新增 
	 * @return void    返回类型 
	 */
	@Override
	public void batchInsert(final List<T> entitys){
		final List<T> tempList = Lists.newArrayList();
    	for(T t: entitys) {
    		if (t instanceof IdEntity) {
			   ((IdEntity<PK>)t).prePersist();
			}
    		tempList.add(t);
    		if (tempList.size() == 15) {
    		    this.getSqlRunner().insert(getStatementName(BATCH_INSERT), tempList);
    		    tempList.clear();
    		}
    	}
    	if (tempList.size() > 0) {
    	    this.getSqlRunner().insert(getStatementName(BATCH_INSERT), tempList);
    	}
	}
	
	/**
	 * 批量新增 
	 * @Title: batchInsert 
	 * @Description: 批量新增 
	 * @return void    返回类型 
	 */
	@Override
	public void batchInsert(final String sql, final List<T> entitys){
		final List<T> tempList = Lists.newArrayList();
    	for(T t: entitys) {
    		if (t instanceof IdEntity) {
 			    ((IdEntity<PK>)t).prePersist();
 			}
    		tempList.add(t);
    		if (tempList.size() == 15) {
    		    this.getSqlRunner().insert(getStatementName(sql), tempList);
    		    tempList.clear();
    		}
    	}
    	if (tempList.size() > 0) {
    	    this.getSqlRunner().insert(getStatementName(sql), tempList);
    	}
	}
	
	/**
	 * 批量修改
	 * @Title: batchUpdate 
	 * @Description: 批量新增 
	 * @return void    返回类型 
	 */
	@Override
	public void batchUpdate(final List<T> entitys){
		for (int index = 0; index < entitys.size(); index ++){
			 T entity = entitys.get(index);
			 if (entity instanceof BaseEntity) {
	             ((BaseEntity<PK>)entity).preUpdate();
	         }
	   		 this.getSqlRunner().update(getStatementName(UPDATE), entity);
	   	}
	}
	
	/**
	 * 批量修改
	 * @Title: batchUpdate 
	 * @Description: 批量新增 
	 * @return void    返回类型 
	 */
	@Override
	public void batchUpdate(final String statementName, final List<T> entitys){
		for (int index = 0; index < entitys.size(); index ++){
			 T entity = entitys.get(index);
			 if (entity instanceof BaseEntity) {
	             ((BaseEntity<PK>)entity).preUpdate();
	         }
	   		 this.getSqlRunner().update(getStatementName(statementName), entity);
	   	}
	}
	
	/**
	 * 批量删除
	 * @Title: batchDelete 
	 * @Description: 批量新增 
	 * @return void    返回类型 
	 */
	@Override
	public void batchDelete(final List<T> entitys) {
		for (int index = 0; index < entitys.size(); index ++){
			 T entity = entitys.get(index);
	   		 this.getSqlRunner().delete(getStatementName(DELETE), entity);
	   	}
	}
	
    /** 
     * 根据条件查询个数
	 * @param qc
	 * @return 个数
	 * @author sea
	 */
	@Override
	public Integer countByCondition(String sqlName, QueryCondition qc){
		return (Integer) this.getSqlRunner().selectOne(getStatementName(sqlName),qc);
	}
	
	/** 根据条件查询个数
	 * @param qc
	 * @return 个数
	 * @author sea
	 */
	@Override
	public Integer countByCondition(String sqlName, Object qc){
		return (Integer) this.getSqlRunner().selectOne(getStatementName(sqlName),qc);
	}
	
	 /** 根据条件查询个数
	 * @param qc
	 * @return 个数
	 * @author sea
	 */
	@Override
	public Integer countByCondition(String sqlName, Map<String,?> params){
		return (Integer) this.getSqlRunner().selectOne(getStatementName(sqlName),params);
	}
}