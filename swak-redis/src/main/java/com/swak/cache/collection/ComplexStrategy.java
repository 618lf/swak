package com.swak.cache.collection;

import com.swak.serializer.SerializationUtils;

/**
 * 复杂对象的序列化方式
 * @author lifeng
 */
public class ComplexStrategy implements SerStrategy {

	@Override
	public <T> byte[] serialize(T t) {
		return SerializationUtils.serialize(t);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(byte[] bytes) {
		return (T)SerializationUtils.deserialize(bytes);
	}
}
