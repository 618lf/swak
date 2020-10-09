package com.swak.async.persistence;

import javax.annotation.PostConstruct;

import com.swak.asm.BisGenericIdentify;
import com.swak.async.persistence.define.SqlMap;
import com.swak.async.persistence.define.TableDefine;

/**
 * 模型注册
 * 
 * @author lifeng
 * @date 2020年10月7日 下午9:44:18
 */
public class ModelRegister<T, PK> implements BisGenericIdentify<T, PK> {

	/**
	 * 表定义
	 */
	protected TableDefine<T> table;

	/**
	 * Sql 映射
	 */
	protected SqlMap<T> sqlMap;

	/**
	 * 启动初始化执行模型注册
	 */
	@PostConstruct
	public void registerModel() {

		// 获得实际的类型
		Class<T> entity = this.getEntityClass();

		// 定义表
		this.table = new TableDefine<T>(entity);

		// 定义映射
		this.sqlMap = new SqlMap<T>(this.table);
	}

	/**
	 * 获得脚本
	 * 
	 * @param <E>
	 * @param namespace
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <E> E getSql(String namespace) {
		return (E) this.sqlMap.sqls.get(namespace);
	}
}