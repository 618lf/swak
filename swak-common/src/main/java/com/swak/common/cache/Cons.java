package com.swak.common.cache;

/**
 * Lua 脚本常量
 * @author lifeng
 */
public class Cons {

	// 普通GET
	public static String GET_LUA = null;
	public static String LIST_GET_LUA = null;
	public static String LIST_PUT_LUA = null;
	public static String MAP_GET_LUA = null;
	public static String MAP_PUT_LUA = null;
	public static String MULTI_MAP_GET_LUA = null;
	public static String MULTI_MAP_PUT_LUA = null;
	
	// 是否存在
	public static String EXISTS_LUA = null;
	
	static {
		GET_LUA = new StringBuilder().append("redis.call(\"EXPIRE\", KEYS[1], KEYS[2]); return redis.call(\"GET\", KEYS[1]);").toString();
		EXISTS_LUA = new StringBuilder().append("redis.call(\"EXPIRE\", KEYS[1], KEYS[2]); return redis.call(\"EXISTS\", KEYS[1]);").toString();
	
		LIST_PUT_LUA = new StringBuilder().append("redis.call(\"EXPIRE\", KEYS[1], KEYS[3]); return redis.call(\"LPUSH\", KEYS[1], KEYS[2]);").toString();
		LIST_GET_LUA = new StringBuilder().append("redis.call(\"EXPIRE\", KEYS[1], KEYS[2]); return redis.call(\"LPOP\", KEYS[1]);").toString();
		
		MAP_PUT_LUA = new StringBuilder().append("redis.call(\"EXPIRE\", KEYS[1], KEYS[4]); return redis.call(\"HSET\", KEYS[1], KEYS[2], KEYS[3]);").toString();
		MAP_GET_LUA = new StringBuilder().append("redis.call(\"EXPIRE\", KEYS[1], KEYS[3]); return redis.call(\"HGET\", KEYS[1], KEYS[2]);").toString();
		
		MULTI_MAP_PUT_LUA = new StringBuilder().append("redis.call(\"EXPIRE\", KEYS[1], KEYS[2]); return redis.call(\"hMSet\", KEYS[1]#KEYS#);").toString();
		MULTI_MAP_GET_LUA = new StringBuilder().append("redis.call(\"EXPIRE\", KEYS[1], KEYS[2]); return redis.call(\"MGETALL\", KEYS[1]);").toString();
	}
}
