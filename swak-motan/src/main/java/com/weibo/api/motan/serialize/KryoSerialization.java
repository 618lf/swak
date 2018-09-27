package com.weibo.api.motan.serialize;

import java.io.IOException;
import java.util.List;

import com.swak.serializer.KryoPoolSerializer;
import com.swak.serializer.Serializer;
import com.weibo.api.motan.codec.Serialization;
import com.weibo.api.motan.core.extension.SpiMeta;

/**
 * 用 kryo-pool 实现的 序列化方案
 * 
 * @author lifeng
 */
@SpiMeta(name = "kryo")
public class KryoSerialization implements Serialization {

	private Serializer g_ser = null;

	public KryoSerialization() {
		g_ser = new KryoPoolSerializer();
	}

	@Override
	public byte[] serialize(Object obj) throws IOException {
		return g_ser.serialize(obj);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
		return (T) g_ser.deserialize(bytes);
	}

	@Override
	public byte[] serializeMulti(Object[] data) throws IOException {
		return g_ser.serialize(data);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object[] deserializeMulti(byte[] data, Class<?>[] classes) throws IOException {
		List<Object> list = (List<Object>) g_ser.deserialize(data);
		if (list != null) {
            return list.toArray();
        }
		return null;
	}

	@Override
	public int getSerializationNumber() {
		return 0;
	}
}