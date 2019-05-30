package com.swak.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.swak.entity.IdEntity;
import com.swak.entity.Page;
import com.swak.entity.Parameters;
import com.swak.persistence.BaseDao;
import com.swak.persistence.QueryCondition;
import com.swak.utils.SpringContextHolder;

/**
 * @author TMT 封装了公用操作数据的，但查询的只能在子类内部使用
 *         目的是不让子类外部直接传入sql就可以查询，应在子类内部实现一个sql的方法，在方法中 调用基础的方式来查询数据
 */
public abstract class BaseService<T extends IdEntity<PK>, PK extends Serializable> {

	protected static Logger logger = LoggerFactory.getLogger(BaseService.class);

	/**
	 * 用于提供编程式事务支持
	 */
	@Autowired
	protected PlatformTransactionManager transactionManager;

	/**
	 * 代理类
	 */
	protected Object proxy;

	/**
	 * 在子类实现此函数,为下面的CRUD操作提供DAO.
	 */
	protected abstract BaseDao<T, PK> getBaseDao();

	/**
	 * 提供事务支持 (加入的代码必须在一个线程中)
	 */
	protected <U> U execute(Supplier<U> supplier) {
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus status = transactionManager.getTransaction(def);
		try {
			U u = supplier.get();
			transactionManager.commit(status);
			return u;
		} catch (RuntimeException e) {
			transactionManager.rollback(status);
			throw e;
		}
	}

	/**
	 * 获得代理类
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <U> U getProxy() {
		if (this.proxy == null) {
			Class<?>[] interfacess = this.getClass().getInterfaces();
			if (interfacess != null && interfacess.length > 0) {
				this.proxy = SpringContextHolder.getBean(interfacess[0]);
			}
		}
		return (U) this.proxy;
	}

	/**
	 * 获取单个值
	 * 
	 * @param id
	 * @return
	 */
	public T get(final PK id) {
		return getBaseDao().get(id);
	}

	/**
	 * 获取所有值
	 * 
	 * @return
	 */
	public List<T> getAll() {
		return getBaseDao().getAll();
	}

	/**
	 * 按条件查询
	 * 
	 * @param qc
	 * @return
	 */
	public List<T> queryByCondition(QueryCondition qc) {
		return getBaseDao().queryByCondition(qc);
	}

	/**
	 * 按条件分页查询
	 * 
	 * @param qc
	 * @param param
	 * @return
	 */
	public Page queryForPage(QueryCondition qc, Parameters param) {
		return this.getBaseDao().queryForPage(qc, param);
	}

	/**
	 * 按条件查询数量
	 * 
	 * @param qc
	 * @param param
	 * @return
	 */
	public Integer countByCondition(QueryCondition qc) {
		return this.getBaseDao().countByCondition(qc);
	}

	/**
	 * 按条件查询，指定返回的数量
	 * 
	 * @param qc
	 * @param param
	 * @return
	 */
	public List<T> queryForLimitList(QueryCondition qc, int size) {
		return this.getBaseDao().queryForLimitList(qc, size);
	}

	/**
	 * 校验是否存在
	 * 
	 * @param id
	 * @return
	 */
	public boolean exists(PK id) {
		return this.getBaseDao().exists(id);
	}

	/**
	 * 锁住记录，一定要传递条件， 事务结束后才会释放
	 * 
	 * @param id
	 */
	protected void lock(Object entity) {
		this.getBaseDao().lock(entity);
	}

	/**
	 * 插入数据
	 * 
	 * @param entity
	 * @return
	 */
	protected PK insert(T entity) {
		return getBaseDao().insert(entity);
	}

	/**
	 * 修改数据
	 * 
	 * @param entity
	 * @return
	 */
	protected int update(T entity) {
		return getBaseDao().update(entity);
	}

	/**
	 * 用版本来实现乐观锁
	 * 
	 * @param entity
	 * @return
	 */
	protected int updateVersion(T entity) {
		this.getBaseDao().compareVersion(entity);
		return getBaseDao().update(entity);
	}

	/**
	 * 删除数据
	 * 
	 * @param entity
	 * @return
	 */
	protected int delete(T entity) {
		return getBaseDao().delete(entity);
	}

	/**
	 * 批量更新
	 * 
	 * @param entity
	 * @return
	 */
	protected void batchUpdate(List<T> entities) {
		this.getBaseDao().batchUpdate(entities);
	}

	/**
	 * 批量更新
	 * 
	 * @param entity
	 * @return
	 */
	protected void batchUpdate(String sql, List<T> entities) {
		this.getBaseDao().batchUpdate(sql, entities);
	}

	/**
	 * 批量插入
	 * 
	 * @param entity
	 * @return
	 */
	protected void batchInsert(List<T> entities) {
		this.getBaseDao().batchInsert(entities);
	}

	/**
	 * 批量删除
	 * 
	 * @param entity
	 * @return
	 */
	protected void batchDelete(List<T> entities) {
		this.getBaseDao().batchDelete(entities);
	}

	/**
	 * 版本保护修改
	 * 
	 * @param entity
	 * @return
	 */
	protected int updateVersion(String statementName, final T entity) {
		getBaseDao().compareVersion(entity);
		return getBaseDao().update(statementName, entity);
	}

	/**
	 * 修改
	 * 
	 * @param entity
	 * @return
	 */
	protected int update(String statementName, T entity) {
		return getBaseDao().update(statementName, entity);
	}

	/**
	 * 分页查询
	 * 
	 * @param entity
	 * @return
	 */
	protected Page queryForPageList(String sql, QueryCondition qc, Parameters param) {
		return this.getBaseDao().queryForPageList(sql, qc, param);
	}

	/**
	 * 分页查询
	 * 
	 * @param entity
	 * @return
	 */
	protected Page queryForPageList(String sql, Map<String, ?> qc, Parameters param) {
		return this.getBaseDao().queryForPageList(sql, qc, param);
	}

	/**
	 * 分页查询
	 * 
	 * @param entity
	 * @return
	 */
	protected Page queryForMapPageList(String sql, Map<String, ?> qc, Parameters param) {
		return this.getBaseDao().queryForMapPageList(sql, qc, param);
	}

	/**
	 * 根据条件查询个数
	 * 
	 * @param qc
	 * @return 个数
	 * @author sea
	 */
	protected Integer countByCondition(String sql, Object qc) {
		return this.getBaseDao().countByCondition(sql, qc);
	}

	/**
	 * 根据条件查询个数
	 * 
	 * @param qc
	 * @return 个数
	 * @author sea
	 */
	protected Integer countByCondition(String sql, QueryCondition qc) {
		return this.getBaseDao().countByCondition(sql, qc);
	}

	/**
	 * 根据条件查询个数
	 * 
	 * @param qc
	 * @return 个数
	 * @author sea
	 */
	protected Integer countByCondition(String sql, Map<String, ?> params) {
		return this.getBaseDao().countByCondition(sql, params);
	}

	/**
	 * 查询列表
	 * 
	 * @param entity
	 * @return
	 */
	protected List<T> queryForList(String statementName, Map<String, ?> params) {
		return this.getBaseDao().queryForList(statementName, params);
	}

	/**
	 * 查询列表
	 * 
	 * @param entity
	 * @return
	 */
	protected List<T> queryForList(String statementName, QueryCondition qc) {
		return this.getBaseDao().queryForList(statementName, qc);
	}

	/**
	 * 查询列表
	 * 
	 * @param entity
	 * @return
	 */
	protected List<T> queryForList(String statementName, Object o) {
		return this.getBaseDao().queryForList(statementName, o);
	}

	/**
	 * 查询列表
	 * 
	 * @param entity
	 * @return
	 */
	protected List<T> queryForLimitList(String statementName, QueryCondition qc, int size) {
		return this.getBaseDao().queryForLimitList(statementName, qc, size);
	}

	/**
	 * 查询列表
	 * 
	 * @param entity
	 * @return
	 */
	protected List<T> queryForLimitList(String statementName, Map<String, ?> params, int size) {
		return this.getBaseDao().queryForLimitList(statementName, params, size);
	}

	/**
	 * 查询列表
	 * 
	 * @param entity
	 * @return
	 */
	protected List<T> queryForLimitList(String statementName, Object params, int size) {
		return this.getBaseDao().queryForLimitList(statementName, params, size);
	}

	/**
	 * 查询列表
	 * 
	 * @param entity
	 * @return
	 */
	protected <E> List<E> queryForGenericsList(String statementName, Object o) {
		return this.getBaseDao().queryForGenericsList(statementName, o);
	}

	/**
	 * 查询列表
	 * 
	 * @param entity
	 * @return
	 */
	protected <E> List<E> queryForGenericsList(String statementName, Map<String, ?> params) {
		return this.getBaseDao().queryForGenericsList(statementName, params);
	}

	/**
	 * 查询列表
	 * 
	 * @param entity
	 * @return
	 */
	protected List<Map<String, Object>> queryForMapList(String statementName, Map<String, ?> params) {
		return this.getBaseDao().queryForMapList(statementName, params);
	}

	/**
	 * 查询单个值
	 * 
	 * @param entity
	 * @return
	 */
	protected T queryForObject(String statementName, Object params) {
		return this.getBaseDao().queryForObject(statementName, params);
	}

	/**
	 * 查询单个值
	 * 
	 * @param entity
	 * @return
	 */
	protected T queryForObject(String statementName, Map<String, ?> params) {
		return this.getBaseDao().queryForObject(statementName, params);
	}

	/**
	 * 查询单个值
	 * 
	 * @param entity
	 * @return
	 */
	protected T queryForObject(String statementName, QueryCondition qc) {
		return this.getBaseDao().queryForObject(qc);
	}

	/**
	 * 查询属性
	 * 
	 * @param entity
	 * @return
	 */
	protected <E> E queryForAttr(String statementName, Object entity) {
		return this.getBaseDao().queryForAttr(statementName, entity);
	}
}