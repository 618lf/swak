package com.swak.redis.lettuce;

import java.util.Map;
import java.util.concurrent.CompletionStage;

import com.swak.SafeEncoder;
import com.swak.redis.Expiration;
import com.swak.redis.RedisAsyncCommands;
import com.swak.redis.ReturnType;
import com.swak.redis.Scripts;
import com.swak.redis.SetOption;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;

/**
 * 实现异步同步操作
 * 
 * @author lifeng
 * @date 2020年8月19日 下午4:58:36
 */
public class LettuceRedisAsyncCommands implements RedisAsyncCommands<byte[], byte[]> {

	RedisClusterAsyncCommands<byte[], byte[]> connect;

	LettuceRedisAsyncCommands(RedisClusterAsyncCommands<byte[], byte[]> comms) {
		this.connect = comms;
	}

	/**
	 * ttl
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<Long> ttl(String key) {
		return connect.ttl(SafeEncoder.encode(key));
	}

	/**
	 * get
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<byte[]> get(String key) {
		return connect.get(SafeEncoder.encode(key));
	}

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<String> set(String key, byte[] value) {
		return connect.set(SafeEncoder.encode(key), value);
	}

	/**
	 * setnx
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<Boolean> setnx(String key, byte[] value) {
		return connect.setnx(SafeEncoder.encode(key), value);
	}

	/**
	 * set
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<String> set(String key, byte[] value, int expire) {
		String script = Scripts.PUT_LUA;
		byte[][] values = new byte[][] { SafeEncoder.encode(key), value, SafeEncoder.encode(String.valueOf(expire)) };
		return this.runScript(script, ReturnType.INTEGER, values, null).thenApply(s -> key);
	}

	/**
	 * 高级设置，可以代替其他的setNx等
	 */
	@Override
	public CompletionStage<String> set(String key, byte[] value, Expiration expiration, SetOption setOption) {
		SetArgs setArgs = new SetArgs();
		if (setOption != null) {
			switch (setOption) {
			case SET_IF_ABSENT:
				setArgs.nx();
				break;
			case SET_IF_PRESENT:
				setArgs.xx();
				break;
			default:
				break;
			}
		}
		if (expiration != null) {
			setArgs.px(expiration.getExpirationTimeInMilliseconds());
		}
		return this.connect.set(SafeEncoder.encode(key), value, setArgs);
	}

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<Long> del(String... keys) {
		return connect.del(SafeEncoder.encodeMany(keys));
	}

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<Boolean> expire(String key, int expire) {
		return connect.expire(SafeEncoder.encode(key), expire);
	}

	/**
	 * del
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<Long> exists(String... key) {
		return connect.exists(SafeEncoder.encodeMany(key));
	}

	/**
	 * incr
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<Long> incr(String key) {
		return connect.incr(SafeEncoder.encode(key));
	}

	/**
	 * decr
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<Long> decr(String key) {
		return connect.decr(SafeEncoder.encode(key));
	}

	/**
	 * lPush
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public CompletionStage<Long> lPush(String key, byte[]... value) {
		return connect.lpush(SafeEncoder.encode(key), value);
	}

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public CompletionStage<byte[]> lPop(String key) {
		return connect.lpop(SafeEncoder.encode(key));
	}

	/**
	 * lPop
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public CompletionStage<Long> lLen(String key) {
		return connect.llen(SafeEncoder.encode(key));
	}

	/**
	 * hget
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public CompletionStage<byte[]> hGet(String key, String field) {
		return connect.hget(SafeEncoder.encode(key), SafeEncoder.encode(field));
	}

	/**
	 * hset
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public CompletionStage<Boolean> hSet(String key, String field, byte[] value) {
		return connect.hset(SafeEncoder.encode(key), SafeEncoder.encode(field), value);
	}

	/**
	 * hdel
	 * 
	 * @param key
	 * @param field
	 * @return
	 */
	public CompletionStage<Long> hDel(String key, String... fields) {
		return connect.hdel(SafeEncoder.encode(key), SafeEncoder.encodeMany(fields));
	}

	/**
	 * hGetAll
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<Map<byte[], byte[]>> hGetAll(String key) {
		return connect.hgetall(SafeEncoder.encode(key));
	}

	/**
	 * hMset
	 * 
	 * @param key
	 * @param map
	 * @return
	 */
	public CompletionStage<String> hMSet(String key, Map<byte[], byte[]> map) {
		return connect.hmset(SafeEncoder.encode(key), map);
	}

//	/**
//	 * hscan
//	 * 
//	 * @param key
//	 * @param value
//	 * @return
//	 */
//	public CompletionStage<MapScanCursor<byte[], byte[]>> hscan(String key, ScanOptions scanArgs) {
//		connect.hscan(key, null, ScanArgs.Builder.limit(scanArgs.getCount()).match(scanArgs.getPattern()));
//		return connect.hscan(SafeEncoder.encode(key), cursor, scanArgs);
//	}

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public CompletionStage<Boolean> sAdd(String key, byte[] value) {
		return connect.sadd(SafeEncoder.encode(key), value).thenApply(res -> res != null && res > 0);
	}

	/**
	 * sAdd
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public CompletionStage<Boolean> sExists(String key, byte[] value) {
		return connect.sismember(SafeEncoder.encode(key), value);
	}

	/**
	 * sRem
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public CompletionStage<Long> sRem(String key, byte[] value) {
		return connect.srem(SafeEncoder.encode(key), value);
	}

	/**
	 * sLen
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public CompletionStage<Long> sLen(String key) {
		return connect.scard(SafeEncoder.encode(key));
	}

//	/**
//	 * sscan
//	 * 
//	 * @param key
//	 * @param value
//	 * @return
//	 */
//	public CompletionStage<ValueScanCursor<byte[]>> sscan(String key, ScanCursor cursor, ScanArgs scanArgs) {
//		return connect.sscan(SafeEncoder.encode(key), cursor, scanArgs);
//	}

	/**
	 * loadScript
	 * 
	 * @param key
	 * @return
	 */
	public CompletionStage<String> loadScript(String script) {
		return connect.scriptLoad(SafeEncoder.encode(script));
	}

	/**
	 * runScript
	 * 
	 * @param key
	 * @return
	 */
	public <T> CompletionStage<T> runScript(String script, ReturnType type, byte[][] values, byte[][] params) {
		ScriptOutputType outputType = ScriptOutputType.valueOf(type.name());
		return connect.eval(script, outputType, values, params);
	}

	/**
	 * runScript -- 脚本已经通过 loadScript 安装好
	 * 
	 * @param key
	 * @return
	 */
	public <T> CompletionStage<T> runShaScript(String script, ReturnType type, byte[][] values, byte[][] params) {
		ScriptOutputType outputType = ScriptOutputType.valueOf(type.name());
		return connect.evalsha(script, outputType, values, params);
	}
}