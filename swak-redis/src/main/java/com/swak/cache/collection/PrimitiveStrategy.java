package com.swak.cache.collection;

import com.swak.cache.SafeEncoder;

/**
 * 针对基本类型的序列化方式
 * @author lifeng
 */
public class PrimitiveStrategy implements SerStrategy {

	@Override
	public <T> byte[] serialize(T t) {
		return SafeEncoder.encode((String)t);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(byte[] bytes) {
		return (T)SafeEncoder.encode(bytes);
	}
}