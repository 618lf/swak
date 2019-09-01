package com.swak.mongo.json;

import com.alibaba.fastjson.JSONObject;

/**
 * 挂名
 * 
 * @author lifeng
 */
public class Document extends JSONObject {
	public static final String ID_FIELD = "_id";
	public static final String DATE_FIELD = "$date";
	public static final String BINARY_FIELD = "$binary";
	public static final String TYPE_FIELD = "$type";
	public static final String OID_FIELD = "$oid";
	public static final String TIMESTAMP_FIELD = "$timestamp";
	public static final String TIMESTAMP_TIME_FIELD = "t";
	public static final String TIMESTAMP_INCREMENT_FIELD = "i";
	private static final long serialVersionUID = 1L;
}