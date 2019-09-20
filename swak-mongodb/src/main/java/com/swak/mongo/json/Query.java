package com.swak.mongo.json;

import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.swak.entity.Parameters;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;

/**
 * 查询
 * 
 * @author lifeng
 */
public class Query {

	/**
	 * 无条件
	 */
	public Query() {
	}

	/**
	 * 写入条件
	 * 
	 * @param bean
	 */
	public <T> Query(T bean) {
		Document filter = new Document(bean);
		if (!filter.isEmpty()) {
			this.filter = Filters.and(filter);
		}
	}

	// ------------- 排序 ----------------
	private Order order;

	/**
	 * 排序
	 * 
	 * @param key
	 * @return
	 */
	public Query orderBy(Parameters param) {
		if (StringUtils.isNotBlank(param.getSortField())) {
			return this.orderBy(param.getSortField(), StringUtils.equals("ascending", param.getSortType()) ? 1 : -1);
		}
		return this;
	}

	/**
	 * 排序
	 * 
	 * @param key
	 * @return
	 */
	public Query orderBy(String key) {
		return this.orderBy(key, 1);
	}

	/**
	 * 排序
	 * 
	 * @param key
	 * @return
	 */
	public Query orderBy(String key, int order) {
		if (this.order == null) {
			this.order = new Order();
		}
		this.order.put(key, order);
		return this;
	}

	/**
	 * 获得排序条件
	 * 
	 * @return
	 */
	public Order getOrder() {
		return order;
	}

	// ------------- 条件 --------------------
	private Bson filter;

	/**
	 * 添加条件
	 * 
	 * @param bson
	 * @return
	 */
	public Query and(Bson... bsons) {
		if (this.filter == null) {
			this.filter = Filters.and(bsons);
		} else {
			List<Bson> list = Lists.newArrayList(bsons);
			list.add(this.filter);
			this.filter = Filters.and(list.toArray(bsons));
		}
		return this;
	}

	/**
	 * 添加条件
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Query and(String key, Object value) {
		if (this.filter == null || this.filter instanceof Document) {
			Document filter = this.filter == null ? new Document() : (Document) this.filter;
			filter.put(key, value);
			this.filter = filter;
		} else {
			Document filter = new Document();
			filter.put(key, value);
			return this.and(filter);
		}
		return this;
	}

	/**
	 * 获得条件
	 * 
	 * @return
	 */
	public Bson getFilter() {
		return filter;
	}

	// ------------- 字段 --------------------
	private Bson fields;

	/**
	 * 添加条件
	 * 
	 * @param bson
	 * @return
	 */
	public Query fields(Bson... bsons) {
		this.fields = Projections.fields(bsons);
		return this;
	}

	/**
	 * 返回字段
	 * 
	 * @return
	 */
	public Bson getFields() {
		return fields;
	}
}