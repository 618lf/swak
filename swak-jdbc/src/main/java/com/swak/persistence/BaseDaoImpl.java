package com.swak.persistence;

import com.swak.entity.BaseEntity;
import com.swak.entity.IdEntity;
import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.exception.StaleObjectStateException;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @Author: lifeng
 * @Date: 2020/3/28 12:56
 * @Description: 数据库操作支持类
 */
@SuppressWarnings({"unchecked"})
public class BaseDaoImpl<T, PK> implements BaseDao<T, PK> {

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
    protected String namespace = null;

    /**
     * 注入SqlSessionTemplate实例(要求Spring中进行SqlSessionTemplate的配置).<br/>
     * 可以调用sessionTemplate完成数据库操作.
     */
    @Autowired
    private SqlSessionTemplate sessionTemplate;
    
    /**
	 * 通过注解设置 sessionTemplate
	 */
	public BaseDaoImpl() {

	}

	/**
	 * 支持通过构造的方式设置
	 * 
	 * @param sessionTemplate
	 */
	public BaseDaoImpl(SqlSessionTemplate sessionTemplate) {
		this.sessionTemplate = sessionTemplate;
	}

    /**
     * 默认的命名空间
     *
     * @return 默认的命名空间
     * @author lifeng
     * @date 2020/3/28 13:28
     */
    protected String getNamespace() {
        return namespace != null ? namespace : (namespace = this.getClass().getName());
    }

    /**
     * 默认的命名空间 + 操作
     *
     * @param operate curd
     * @return 命名空间 + 操作
     * @author lifeng
     * @date 2020/3/28 13:28
     */
    protected String getStatementName(String operate) {
        String namespace = getNamespace();
        return StringUtils.hasText(namespace) ? (namespace + "." + operate) : namespace;
    }

    /**
     * 数据库操作模板
     *
     * @return 数据库操作模板
     * @author lifeng
     * @date 2020/3/28 13:26
     */
    protected SqlSessionTemplate getSqlRunner() {
        return this.sessionTemplate;
    }

    /**
     * 版本比较
     *
     * @param entity 主体信息
     * @author lifeng
     * @date 2020/3/28 13:25
     */
    @Override
    public void compareVersion(T entity) {
        PK pk = null;
        Integer myVersion = null;
        if (entity instanceof BaseEntity) {
            pk = ((IdEntity<PK>) entity).getId();
            myVersion = ((IdEntity<PK>) entity).getVersion();
            myVersion = (myVersion == null ? 0 : myVersion) + 1;
            ((IdEntity<PK>) entity).setVersion(myVersion);
        }
        Integer version = this.getSqlRunner().selectOne(VERSION, pk);
        version = version == null ? 0 : version;
        if (myVersion == null || (myVersion.compareTo(version) <= 0)) {
            throw new StaleObjectStateException("数据版本过低");
        }
    }

    /**
     * 添加主体信息
     *
     * @param statementName 设置使用的语句
     * @param entity        主体信息
     * @return 添加的主体信息的主键
     * @author lifeng
     * @date 2020/3/28 13:21
     */
    @Override
    public PK insert(String statementName, T entity) {
        PK pk = null;
        if (entity instanceof IdEntity) {
            pk = ((IdEntity<PK>) entity).prePersist();
        }
        this.getSqlRunner().insert(getStatementName(statementName), entity);
        return pk;
    }

    /**
     * 添加主体信息
     *
     * @param entity 主体信息
     * @return 添加的主体信息的主键
     * @author lifeng
     * @date 2020/3/28 13:21
     */
    @Override
    public PK insert(T entity) {
        return this.insert(INSERT, entity);
    }

    /**
     * 删除主体信息
     *
     * @param entity 主体信息
     * @return 删除的记录数
     * @author lifeng
     * @date 2020/3/28 13:21
     */
    @Override
    public int delete(T entity) {
        return this.getSqlRunner().delete(getStatementName(DELETE), entity);
    }

    /**
     * 删除主体信息
     *
     * @param statementName 设置使用的数据语句
     * @param entity        主体信息
     * @return 删除的记录数
     * @author lifeng
     * @date 2020/3/28 13:21
     */
    @Override
    public int delete(String statementName, T entity) {
        return this.getSqlRunner().delete(getStatementName(statementName), entity);
    }

    /**
     * 修改主体信息
     *
     * @param statementName 设置使用的数据语句
     * @param entity        主体信息
     * @return 修改的记录数
     * @author lifeng
     * @date 2020/3/28 13:21
     */
    @Override
    public int update(String statementName, T entity) {
        if (entity instanceof BaseEntity) {
            ((BaseEntity<PK>) entity).preUpdate();
        }
        return this.getSqlRunner().update(getStatementName(statementName), entity);
    }

    /**
     * 修改主体信息
     *
     * @param entity 主体信息
     * @return 修改的记录数
     * @author lifeng
     * @date 2020/3/28 13:21
     */
    @Override
    public int update(T entity) {
        return this.update(UPDATE, entity);
    }

    /**
     * 通过主键获取唯一数据
     *
     * @param id 主键
     * @return 数据
     * @author lifeng
     * @date 2020/3/28 13:20
     */
    @Override
    public T get(PK id) {
        return (T) this.getSqlRunner().selectOne(getStatementName(GET), id);
    }

    /**
     * 基于数据的锁：如果查询的列是索引列则可能是行级锁，否则是表级锁
     *
     * @param entity 主体
     * @return 是否锁住记录
     * @author lifeng
     * @date 2020/3/28 13:19
     */
    @Override
    public boolean lock(Object entity) {
        Integer count = this.getSqlRunner().selectOne(getStatementName(LOCK), entity);
        return count != null && count == 1;
    }

    /**
     * 判断主键是否存在
     *
     * @param id 主键
     * @return 是否存在
     * @author lifeng
     * @date 2020/3/28 13:18
     */
    @Override
    public boolean exists(PK id) {
        Integer count = this.countByCondition(EXISTS, id);
        return count != null && count == 1;
    }

    /**
     * 查询所有数据
     *
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:15
     */
    @Override
    public List<T> getAll() {
        return this.getSqlRunner().selectList(getStatementName(GET_ALL));
    }

    /**
     * 查询数据
     *
     * @param qc 查询的条件
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:15
     */
    @Override
    public List<T> queryByCondition(QueryCondition qc) {
        return this.getSqlRunner().selectList(getStatementName(FIND_BY_CONDITION), qc);
    }

    /**
     * 查询数据
     *
     * @param statementName 查询的语句
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:15
     */
    @Override
    public List<T> queryForList(String statementName) {
        return this.getSqlRunner().selectList(getStatementName(statementName), null);
    }

    /**
     * 查询数据
     *
     * @param statementName 查询的语句
     * @param entity        查询的条件
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:15
     */
    @Override
    public List<T> queryForList(String statementName, Object entity) {
        return this.getSqlRunner().selectList(getStatementName(statementName), entity);
    }

    /**
     * 查询数据
     *
     * @param statementName 查询的语句
     * @param qc            查询的条件
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:15
     */
    @Override
    public List<T> queryForList(String statementName, QueryCondition qc) {
        return this.getSqlRunner().selectList(getStatementName(statementName), qc);
    }

    /**
     * 查询数据
     *
     * @param statementName 查询的语句
     * @param params        查询的条件
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:15
     */
    @Override
    public List<T> queryForList(String statementName, Map<String, ?> params) {
        return this.getSqlRunner().selectList(getStatementName(statementName), params);
    }

    /**
     * 使用默认的查询语句查询前几条数据
     *
     * @param qc   查询的条件
     * @param size 查询的数据大小
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:15
     */
    @Override
    public List<T> queryForLimitList(QueryCondition qc, int size) {
        return this.getSqlRunner().selectList(getStatementName(FIND_BY_CONDITION), qc, new RowBounds(0, size));
    }

    /**
     * 查询前几条数据
     *
     * @param sql  查询的语句
     * @param qc   查询的条件
     * @param size 查询的数据大小
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:15
     */
    @Override
    public List<T> queryForLimitList(String sql, QueryCondition qc, int size) {
        return this.getSqlRunner().selectList(getStatementName(sql), qc, new RowBounds(0, size));
    }

    /**
     * 查询前几条数据
     *
     * @param sql  查询的语句
     * @param qc   查询的条件
     * @param size 查询的数据大小
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:15
     */
    @Override
    public List<T> queryForLimitList(String sql, Map<String, ?> qc, int size) {
        return this.getSqlRunner().selectList(getStatementName(sql), qc, new RowBounds(0, size));
    }

    /**
     * 查询前几条数据
     *
     * @param sql  查询的语句
     * @param qc   查询的条件
     * @param size 查询的数据大小
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:15
     */
    @Override
    public List<T> queryForLimitList(String sql, Object qc, int size) {
        return this.getSqlRunner().selectList(getStatementName(sql), qc, new RowBounds(0, size));
    }

    /**
     * 返回泛型的查询结果
     *
     * @param statementName 查询语句
     * @param params        参数
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:09
     */
    @Override
    public <E> List<E> queryForGenericsList(String statementName, Map<String, ?> params) {
        return this.getSqlRunner().selectList(getStatementName(statementName), params);
    }

    /**
     * 返回泛型的查询结果
     *
     * @param statementName 查询语句
     * @param entity        查询参数
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public <E> List<E> queryForGenericsList(String statementName, Object entity) {
        return this.getSqlRunner().selectList(getStatementName(statementName), entity);
    }

    /**
     * 返回 Map 查询结果
     *
     * @param statementName 查询语句
     * @param params        查询参数
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public List<Map<String, Object>> queryForMapList(String statementName, Map<String, ?> params) {
        return this.getSqlRunner().selectList(getStatementName(statementName), params);
    }

    /**
     * 返回 id 查询结果
     *
     * @param statementName 查询语句
     * @param params        查询参数
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public List<PK> queryForIdList(String statementName, Map<String, ?> params) {
        return this.getSqlRunner().selectList(getStatementName(statementName), params);
    }

    /**
     * 返回 查询结果
     *
     * @param statementName 查询语句
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public T queryForObject(String statementName) {
        return this.getSqlRunner().selectOne(getStatementName(statementName), null);
    }

    /**
     * 返回 查询结果
     *
     * @param statementName 查询语句
     * @param entity        查询条件
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public T queryForObject(String statementName, Object entity) {
        return (T) this.getSqlRunner().selectOne(getStatementName(statementName), entity);
    }

    /**
     * 返回 查询结果
     *
     * @param statementName 查询语句
     * @param params        查询条件
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public T queryForObject(String statementName, Map<String, ?> params) {
        return (T) (this.getSqlRunner().selectOne(getStatementName(statementName), params));
    }

    /**
     * 返回 查询结果
     *
     * @param qc 查询条件
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public T queryForObject(QueryCondition qc) {
        return (T) (this.getSqlRunner().selectOne(getStatementName(FIND_BY_CONDITION), qc));
    }

    /**
     * 返回 查询结果
     *
     * @param statementName 查询语句
     * @param entity        查询条件
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public <E> E queryForAttr(String statementName, Object entity) {
        return (E) this.getSqlRunner().selectOne(getStatementName(statementName), entity);
    }

    /**
     * 返回 查询分页结果
     *
     * @param sql   查询语句
     * @param args  查询条件
     * @param param 分页参数
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public Page queryForMapPageList(String sql, Map<String, ?> args, Parameters param) {
        return this.queryForPage(sql, args, param);
    }

    /**
     * 返回 查询分页结果
     *
     * @param sql   查询语句
     * @param args  查询条件
     * @param param 分页参数
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public Page queryForPageList(String sql, Map<String, ?> args, Parameters param) {
        return this.queryForPage(sql, args, param);
    }

    /**
     * 返回 查询分页结果
     *
     * @param sql   查询语句
     * @param args  查询条件
     * @param param 分页参数
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public Page queryForPageList(String sql, QueryCondition args, Parameters param) {
        return this.queryForPage(sql, args, param);
    }

    /**
     * 返回 查询分页结果
     *
     * @param args  查询条件
     * @param param 分页参数
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    @Override
    public Page queryForPage(QueryCondition args, Parameters param) {
        return this.queryForPage(FIND_BY_CONDITION, args, param);
    }

    /**
     * 返回 查询分页结果
     *
     * @param sql   查询语句
     * @param args  查询条件
     * @param param 分页参数
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:13
     */
    protected <E> Page queryForPage(String sql, Object args, Parameters param) {
        int pageNum = param.getPageIndex();
        int pageSize = param.getPageSize();
        Integer count = 0;
        List<E> lst;
        if (pageNum == Parameters.NO_PAGINATION || pageSize == Parameters.NO_PAGINATION) {
            lst = this.getSqlRunner().selectList(sql, args);
        } else {
            count = this.getSqlRunner().selectOne(getStatementName(sql + "Stat"), args);
            count = count == null ? 0 : count;
            if (count == 0) {
                lst = Lists.newArrayList();
            } else {
                int pageCount = getPageCount(count, pageSize);
                if (pageNum > pageCount) {
                    pageNum = pageCount;
                }
                lst = this.getSqlRunner().selectList(getStatementName(sql), args, new RowBounds((pageNum - 1) * pageSize, pageSize));
            }
        }
        param.setRecordCount(count);
        return new Page(param, lst);
    }

    private int getPageCount(int recordCount, int pageSize) {
        if (recordCount == 0) {
            return 0;
        }
        return recordCount % pageSize > 0 ? ((recordCount / pageSize) + 1) : (recordCount / pageSize);
    }

    /**
     * 根据条件查询个数
     *
     * @param qc 查询条件
     * @return 查询结果
     * @author lifeng
     * @date 2020/3/28 13:34
     */
    @Override
    public Integer countByCondition(QueryCondition qc) {
        return (Integer) this.getSqlRunner().selectOne(getStatementName(COUNT_BY_CONDITION), qc);
    }

    /**
     * 批量新增
     *
     * @param entitys 新增的主体集合
     * @author lifeng
     * @date 2020/3/28 13:35
     */
    @Override
    public void batchInsert(List<T> entitys) {
        this.batchInsert(BATCH_INSERT, entitys);
    }

    /**
     * 批量新增
     *
     * @param sql     设置新增语句
     * @param entitys 新增的主体集合
     * @author lifeng
     * @date 2020/3/28 13:35
     */
    @Override
    public void batchInsert(String sql, List<T> entitys) {
        List<T> tempList = Lists.newArrayList();
        for (T t : entitys) {
            if (t instanceof IdEntity) {
                ((IdEntity<PK>) t).prePersist();
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
     *
     * @param entitys 修改的主体集合
     * @author lifeng
     * @date 2020/3/28 13:35
     */
    @Override
    public void batchUpdate(List<T> entitys) {
        this.batchUpdate(UPDATE, entitys);
    }

    /**
     * 批量修改
     *
     * @param statementName 修改的语句
     * @param entitys       修改的主体集合
     * @author lifeng
     * @date 2020/3/28 13:35
     */
    @Override
    public void batchUpdate(String statementName, List<T> entitys) {
        for (T entity : entitys) {
            if (entity instanceof BaseEntity) {
                ((BaseEntity<PK>) entity).preUpdate();
            }
            this.getSqlRunner().update(getStatementName(statementName), entity);
        }
    }

    /**
     * 批量删除
     *
     * @param entitys 删除的条件
     * @author lifeng
     * @date 2020/3/28 13:35
     */
    @Override
    public void batchDelete(final List<T> entitys) {
        for (T entity : entitys) {
            this.getSqlRunner().delete(getStatementName(DELETE), entity);
        }
    }

    /**
     * 查询符合条件的个数
     *
     * @param sqlName 修改的语句
     * @param qc      删除的条件
     * @author lifeng
     * @date 2020/3/28 13:35
     */
    @Override
    public Integer countByCondition(String sqlName, QueryCondition qc) {
        return this.getSqlRunner().selectOne(getStatementName(sqlName), qc);
    }

    /**
     * 查询符合条件的个数
     *
     * @param sqlName 修改的语句
     * @param qc      删除的条件
     * @author lifeng
     * @date 2020/3/28 13:35
     */
    @Override
    public Integer countByCondition(String sqlName, Object qc) {
        return this.getSqlRunner().selectOne(getStatementName(sqlName), qc);
    }

    /**
     * 查询符合条件的个数
     *
     * @param sqlName 修改的语句
     * @param params  删除的条件
     * @author lifeng
     * @date 2020/3/28 13:35
     */
    @Override
    public Integer countByCondition(String sqlName, Map<String, ?> params) {
        return this.getSqlRunner().selectOne(getStatementName(sqlName), params);
    }
}