package com.swak.mongo.json;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

/**
 * 查询
 * 
 * @author lifeng
 */
public class Query extends Document {

	private static final long serialVersionUID = 1L;

	private Order order;

	public Query() {
		super();
	}

	public Query(final String key, final Object value) {
		super(key, value);
	}

	public <T> Query(T bean) {
		super(bean);
	}

	// ------------- 排序 ----------------

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
		this.filter = Filters.and(bsons);
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