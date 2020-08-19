package com.swak.redis.lettuce;

import java.util.Map;

import com.swak.SafeEncoder;
import com.swak.redis.RedisCommands;
import com.swak.redis.Scripts;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;

/**
 * 实现同步操作
 * 
 * @author lifeng
 * @date 2020年8月19日 下午4:58:36
 */
public class LettuceRedisCommands implements RedisCommands<byte[], byte[]> {

	/**
	 * 实际的命令处理
	 */
	RedisClusterCommands<byte[], byte[]> connect;

	LettuceRedisCommands(RedisClusterCommands<byte[], byte[]> comms) {
		this.connect = comms;
	}

	/**
	 * ttl
	 * 
	 * @param key
	 * @return
	 */
	public long ttl(String key) {
		return connect.ttl(SafeEncoder.encode(key));
	}

	/**
	 * get
	 * 
	 * @param key	
	 * @return
	 */
	public byte[] get(String key) {
		return connect.get(SafeEncoder.encode(key));
	}

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	public String set(String key, byte[] value) {
		return connect.set(SafeEncoder.encode(key), value);
	}

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	public String set(String key, byte[] value, int expire) {
		String script = Scripts.PUT_LUA;
		byte[][] values = new byte[][] { SafeEncoder.encode(key), value, SafeEncoder.encode(String.valueOf(expire)) };
		this.runScript(script, ScriptOutputType.INTEGER, values);
		return key;
	}

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	public long del(String... keys) {
		return connect.del(SafeEncoder.encodeMany(keys));
	}

	/**
	 * expire
	 * 
	 * @param key
	 * @return
	 */
	public boolean expire(String key, int expire) {
		return connect.expire(SafeEncoder.encode(key), expire);
	}

	/**
	 * exists
	 * 
	 * @param key
	 * @return
	 */
	public Long exists(String... key) {
		return connect.exists(SafeEncoder.encodeMany(key));
	}

	/**
	 * incr
	 * 
	 * @param key
	 * @return
	 */
	public Long incr(String key) {
		return connect.incr(SafeEncoder.encode(key));
	}

	/**
	 * decr
	 * 
	 * @param key
	 * @return
	 */
	public Long decr(String key) {
		return connect.decr(SafeEncoder.encode(key));
	}

	/**
	 * lPush
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long lPush(String key, byte[]... value) {
		return connect.lpush(SafeEncoder.encode(key), value);
	}

	/**
	 * lGet 只获取不删除
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] lGet(String key) {
		return connect.lindex(SafeEncoder.encode(key), 0);
	}

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] lPop(String key) {
		return connect.lpop(SafeEncoder.encode(key));
	}

	/**
	 * rPush
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long rPush(String key, byte[]... value) {
		return connect.rpush(SafeEncoder.encode(key), value);
	}

	/**
	 * rGet 只获取不删除
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] rGet(String key) {
		return connect.lindex(SafeEncoder.encode(key), -1);
	}

	/**
	 * rPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public byte[] rPop(String key) {
		return connect.rpop(SafeEncoder.encode(key));
	}

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public long lLen(String key) {
		return connect.llen(SafeEncoder.encode(key));
	}

	/**
	 * hget
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public byte[] hGet(String key, String field) {
		return connect.hget(SafeEncoder.encode(key), SafeEncoder.encode(field));
	}

	/**
	 * hset
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public boolean hSet(String key, String field, byte[] value) {
		return connect.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value);
	}

	/**
	 * hdel
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public long hDel(String key, String... fields) {
		return connect.hdel(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields));
	}

	/**
	 * hGetAll
	 * 
	 * @param key
	 * @return
	 */
	public Map<byte[], byte[]> hGetAll(String key) {
		return connect.hgetall(SafeEncoder.encode(key));
	}

	/**
	 * hMset
	 * 
	 * @param key
	 * @param map
	 * @return
	 */
	public String hMSet(String key, Map<byte[], byte[]> map) {
		return connect.hmset(SafeEncoder.encode(key), map);
	}

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean sAdd(String key, byte[] value) {
		Long res = connect.sadd(SafeEncoder.encode(key), value);
		return res != null && res > 0;
	}

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean sExists(String key, byte[] value) {
		return connect.sismember(SafeEncoder.encode(key), value);
	}

	/**
	 * sRem
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long sRem(String key, byte[] value) {
		return connect.srem(SafeEncoder.encode(key), value);
	}

	/**
	 * sLen
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Long sLen(String key) {
		return connect.scard(SafeEncoder.encode(key));
	}

	/**
	 * runScript
	 * 
	 * @param key
	 * @return
	 */
	public <T> T runScript(String script, ScriptOutputType type, byte[][] values) {
		return connect.eval(script, type, values, values[0]);
	}
}
