package com.swak.async.persistence;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.swak.asm.FieldCache.FieldMeta;
import com.swak.async.persistence.define.ColumnDefine;
import com.swak.async.persistence.define.TableDefine;
import com.swak.persistence.QueryCondition;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

import net.sf.cglib.beans.BeanMap;

/**
 * 执行参数
 * 
 * @author lifeng
 * @date 2020年10月13日 下午3:00:43
 */
public class SqlParam<T> {

	/**
	 * 表定义
	 */
	public TableDefine<T> table;

	/**
	 * 实体对象
	 */
	public T entity;

	/**
	 * 查询条件
	 */
	public QueryCondition query;

	/**
	 * 设置表定义
	 * 
	 * @param table
	 */
	public SqlParam<T> setTable(TableDefine<T> table) {
		this.table = table;
		return this;
	}

	/**
	 * 设置实体
	 * 
	 * @param entity
	 * @return
	 */
	public SqlParam<T> setEntity(T entity) {
		this.entity = entity;
		return this;
	}

	/**
	 * 设置查询
	 * 
	 * @param entity
	 * @return
	 */
	public SqlParam<T> setQuery(QueryCondition query) {
		this.query = query;
		return this;
	}

	/**
	 * 通过cglib 高效的转换, 不能使用lombok的链式功能
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> BeantoMap(T bean) {
		Map<String, Object> map = Maps.newHashMap();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);
			Set<String> keys = beanMap.keySet();
			for (Object key : keys) {
				map.put(String.valueOf(key), beanMap.get(key));
			}
		}
		return map;
	}

	/**
	 * 解析参数
	 */
	public List<Object> getEntityIdValues() {
		List<ColumnDefine> pks = this.table.getPkColumns();
		List<Object> params = Lists.newArrayList(pks.size());
		if (entity != null) {
			try {
				Map<String, Object> maps = this.BeantoMap(entity);
				for (ColumnDefine column : pks) {
					Object value = maps.get(column.javaProperty);
					params.add(value);
				}
			} catch (Exception e) {
			}
		}
		return params;
	}

	/**
	 * 解析参数
	 */
	public List<Object> getEntityValues() {
		List<ColumnDefine> pks = this.table.columns;
		List<Object> params = Lists.newArrayList(pks.size());
		if (entity != null) {
			try {
				Map<String, Object> maps = this.BeantoMap(entity);
				for (ColumnDefine column : pks) {
					Object value = maps.get(column.javaProperty);
					params.add(value);
				}
			} catch (Exception e) {
			}
		}
		return params;
	}

	/**
	 * 获取属性的值
	 * 
	 * @param fieldName
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public <U> U getFieldValue(String fieldName) {
		FieldMeta meta = this.table.meta.getFields().get(fieldName);
		if (meta == null) {
			return null;
		}

		Object value = null;

		// 获取实体中的值
		if (this.entity != null) {
			try {
				value = meta.getField().get(entity);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		// 获取查询条件中的值
		if (value == null && query != null) {
			String column = StringUtils.convertProperty2Column(fieldName);
			value = query.getColumnValue(column);
		}

		// 返回获取的值
		return (U) value;
	}
}