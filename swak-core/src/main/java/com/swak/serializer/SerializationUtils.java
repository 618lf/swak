package com.swak.serializer;

import java.io.IOException;

import com.swak.exception.SerializeException;

/**
 * 序列化工具类
 * 
 * @author lifeng
 */
public class SerializationUtils {

	private static Serializer g_ser;

	/**
	 * 设置默认序列化
	 * 
	 * @param serialization
	 */
	public static void setSerializer(String serialization) {
		Serializer g_ser = null;
		if (serialization.equals("java")) {
			g_ser = new JavaSerializer();
		} else if (serialization.equals("kryo")) {
			g_ser = new KryoSerializer();
		} else if (serialization.equals("kryo_pool")) {
			g_ser = new KryoPoolSerializer();
		} else {
			g_ser = new JavaSerializer();
		}

		// 公共引用
		SerializationUtils.g_ser = g_ser;
	}

	// ---------------------调用------------------------
	/**
	 * 调用实际的序列化工具 -- 序列化
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws SerializeException {
		return g_ser.serialize(obj);
	}

	/**
	 * 调用实际的序列化工具 -- 反序列化
	 * 
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static Object deserialize(byte[] bytes) throws SerializeException {
		return bytes != null ? g_ser.deserialize(bytes) : null;
	}

}
