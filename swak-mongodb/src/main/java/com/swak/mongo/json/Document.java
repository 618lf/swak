package com.swak.mongo.json;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.swak.utils.Maps;

/**
 * 挂名
 * 
 * @author lifeng
 */
public class Document extends JSONObject {
	public static final String ID_FIELD = "_id";
	public static final String _ID_FIELD = "id";
	public static final String DATE_FIELD = "$date";
	public static final String BINARY_FIELD = "$binary";
	public static final String TYPE_FIELD = "$type";
	public static final String OID_FIELD = "$oid";
	public static final String TIMESTAMP_FIELD = "$timestamp";
	public static final String TIMESTAMP_TIME_FIELD = "t";
	public static final String TIMESTAMP_INCREMENT_FIELD = "i";
	private static final long serialVersionUID = 1L;

	public Document() {
		super();
	}

	public <T> Document(T bean) {
		super(parseBean(bean));
	}

	/**
	 * id 的转换
	 * 
	 * @param bean
	 * @return
	 */
	private static <T> Map<String, Object> parseBean(T bean) {
		Map<String, Object> values = Maps.toMap(bean);
		if (values.containsKey(_ID_FIELD)) {
			values.put(ID_FIELD, values.remove(_ID_FIELD));
		}
		return values;
	}
}