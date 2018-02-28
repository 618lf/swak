package com.swak.common.config;

import java.util.Map;

import com.swak.common.utils.Maps;
import com.swak.common.utils.PropertiesLoader;

/**
 * 默认加载 application.properties 配置文件 获取配置文件的内容
 * 
 * @author lifeng
 */
public class Globals {

	/** 用户密码加密 **/
	public static final String HASH_ALGORITHM = "SHA-1";

	/** 用户密码加密 **/
	public static final int HASH_INTERATIONS = 1024;

	/** 用户密码加密 **/
	public static final int SALT_SIZE = 8;

	/** 超级管理员 **/
	public static final Long ROOT = 0L;

	/** 默认的编码格式 **/
	public static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * 日期
	 */
	public static final String[] DATE_PATTERNS = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
			"yyyy-MM-dd HH", "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy", "yyyyMM", "yyyy/MM",
			"yyyyMMddHHmmss", "yyyyMMdd" };

	/**
	 * 保存全局属性值
	 */
	private static Map<String, String> map = Maps.newHashMap();
	/**
	 * 属性文件加载对象
	 */
	private static PropertiesLoader propertiesLoader = new PropertiesLoader("application.properties");

	/*  配置项目 */
	/**
	 * 服务器序列号
	 */
	public static String SERVER_SN = "server.sn";
	
	/**
	 * 序列化方案
	 */
	public static String CACHE_SERIALIZATION = "cache.serialization";
	
	/**
	 * 获取配置
	 */
	public static String getConfig(String key) {
		String value = map.get(key);
		if (value == null) {
			value = propertiesLoader.getProperty(key);
			map.put(key, value);
		}
		return value;
	}
}