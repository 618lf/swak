package com.swak.persistence;

import java.util.List;
import java.util.Map;

/**
 * 基础Dao
 * 
 * @author root
 *
 */
public interface BaseDao<T, PK> {

	/**
	 * 版本比较用于乐观锁
	 * @param entity
	 */
	public void compareVersion(T entity);

	/**
	 * 插入数据 - 指定sql语句
	 * @param statementName
	 * @param entity
	 * @return
	 */
	public PK insert(String statementName, T entity);

	/**
	 * 插入数据
	 * @param entity
	 * @return
	 */
	public PK insert(T entity);

	/**
	 * 删除数据
	 * @param entity
	 * @return
	 */
	public int delete(T entity);

	/**
	 * 删除数据 - 指定语句
	 * @param statementName
	 * @param entity
	 * @return
	 */
	public int delete(String statementName, T entity);

	/**
	 * 修改
	 * @param statementName
	 * @param entity
	 * @return
	 */
	public int update(String statementName, T entity);

	/**
	 * 默认的修改
	 * @param entity
	 * @return
	 */
	public int update(T entity);

	/**
	 * 根据主键查询
	 * @param id
	 * @return
	 */
	public T get(PK id);

	/**
	 * 根据主键查询 for update
	 * @param id
	 * @return
	 */
	public void lock(T entity);

	/**
	 * 是否存在
	 * 
	 * @param id
	 * @return
	 */
	public boolean exists(PK id);

	/**
	 * 条件查询
	 * @return
	 */
	public List<T> queryByCondition(QueryCondition qc);

	/**
	 * 查询
	 * @return
	 */
	public List<T> queryForList(String statementName);

	/**
	 * 查询
	 * @return
	 */
	public List<T> queryForList(String statementName, Object entity);

	/**
	 * 查询
	 * @return
	 */
	public List<T> queryForList(String statementName, QueryCondition qc);

	/**
	 * 查询
	 * @return
	 */
	public List<T> queryForList(String statementName, Map<String, ?> params);

	/**
	 * 注意：起始位置是从0开始算的约定： 填0 和1 都表示第一条 直接获取前几个
	 * 
	 * @param qc
	 * @param begin
	 *            --- 起始位置从0开始算
	 * @param end
	 * @return
	 */
	public List<T> queryForLimitList(QueryCondition qc, int size);

	/**
	 * 注意：起始位置是从0开始算的约定： 填0 和1 都表示第一条 直接获取前几个
	 * 
	 * @param qc
	 * @param begin
	 * @param end
	 * @return
	 */
	public List<T> queryForLimitList(String sql, QueryCondition qc, int size);

	/**
	 * 注意：起始位置是从0开始算的约定： 填0 和1 都表示第一条 直接获取前几个包含end,包含begin
	 * 
	 * @param qc
	 * @param begin
	 * @param end
	 * @return
	 */
	public List<T> queryForLimitList(String sql, Map<String, ?> qc, int size);

	/**
	 * 注意：起始位置是从0开始算的约定： 填0 和1 都表示第一条 直接获取前几个包含end,包含begin
	 * 
	 * @param qc
	 * @param begin
	 * @param end
	 * @return
	 */
	public List<T> queryForLimitList(String sql, Object qc, int size);

	/**
	 * 查询其他对象
	 * @param statementName
	 * @param params
	 * @return
	 */
	public <E> List<E> queryForGenericsList(String statementName, Map<String, ?> params);

	/**
	 * 查询其他对象
	 * @param statementName
	 * @param params
	 * @return
	 */
	public <E> List<E> queryForGenericsList(String statementName, Object entity);

	/**
	 * 查询Map
	 * @param statementName
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryForMapList(String statementName, Map<String, ?> params);

	/**
	 * 查询String
	 * @param statementName
	 * @param params
	 * @return
	 */
	public List<PK> queryForIdList(String statementName, Map<String, ?> params);

	/**
	 * 单个查询
	 * @param statementName
	 * @param params
	 * @return
	 */
	public T queryForObject(String statementName);

	/**
	 * 单个查询
	 * @param statementName
	 * @param params
	 * @return
	 */
	public T queryForObject(String statementName, Object entity);

	/**
	 * 单个查询
	 * @param statementName
	 * @param params
	 * @return
	 */
	public T queryForObject(String statementName, Map<String, ?> params);

	/**
	 * 单个查询
	 * @param statementName
	 * @param params
	 * @return
	 */
	public T queryForObject(QueryCondition qc);

	/**
	 * 读取 T 中的某个属性,如果多个属性请 queryForMap,所有属性，请使用queryForObject
	 * 
	 * @param statementName
	 * @param entity
	 * @return
	 */
	public <E> E queryForAttr(String statementName, Object entity);

	/**
	 * 提供分页功能的数据查询 注意:本功能的SQL语句需要有对应统计记录数方法 命名规则为:功能SQL语句名称+Stat
	 * 
	 * @param sql
	 * @param args
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	public Page queryForPageList(String sql, Map<String, ?> args, PageParameters pageParameters);

	/**
	 * 查询Map分页
	 * @param sql
	 * @param args
	 * @param pageParameters
	 * @return
	 */
	public Page queryForMapPageList(String sql, Map<String, ?> args, PageParameters pageParameters);

	/**
	 * 提供分页功能的数据查询 注意:本功能的SQL语句需要有对应统计记录数方法 命名规则为:功能SQL语句名称+Stat
	 * 
	 * @param sql
	 * @param args
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	public Page queryForPageList(String sql, QueryCondition args, PageParameters pageParameters);

	/**
	 * 提供分页功能的数据查询 注意:本功能的SQL语句需要有对应统计记录数方法 命名规则为:功能SQL语句名称+Stat
	 * 
	 * @param sql
	 * @param args
	 * @param pageSize
	 * @param pageNum
	 * @return
	 */
	public Page queryForPage(QueryCondition args, PageParameters pageParameters);

	/**
	 * 根据条件查询个数
	 * 
	 * @param qc
	 * @return 个数
	 * @author sea
	 */
	public Integer countByCondition(QueryCondition qc);

	/**
	 * 批量新增
	 * 
	 * @Title: batchInsert
	 * @Description: 批量新增
	 * @return void 返回类型
	 */
	public void batchInsert(final List<T> entitys);
	
	/**
	 * 批量新增
	 * 
	 * @Title: batchInsert
	 * @Description: 批量新增
	 * @return void 返回类型
	 */
	public void batchInsert(final String sql, final List<T> entitys);

	/**
	 * 批量修改
	 * 
	 * @Title: batchInsert
	 * @Description: 批量新增
	 * @return void 返回类型
	 */
	public void batchUpdate(final List<T> entitys);

	/**
	 * 批量修改
	 * 
	 * @Title: batchInsert
	 * @Description: 批量新增
	 * @return void 返回类型
	 */
	public void batchUpdate(final String statementName, final List<T> entitys);

	/**
	 * 批量删除
	 * 
	 * @Title: batchInsert
	 * @Description: 批量新增
	 * @return void 返回类型
	 */
	public void batchDelete(final List<T> entitys);

	/**
	 * 根据条件查询个数
	 * 
	 * @param qc
	 * @return 个数
	 * @author sea
	 */
	public Integer countByCondition(String sqlName, QueryCondition qc);

	/**
	 * 根据条件查询个数
	 * 
	 * @param qc
	 * @return 个数
	 * @author sea
	 */
	public Integer countByCondition(String sqlName, Object qc);

	/**
	 * 根据条件查询个数
	 * 
	 * @param qc
	 * @return 个数
	 * @author sea
	 */
	public Integer countByCondition(String sqlName, Map<String, ?> params);
}