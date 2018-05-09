package com.swak.common.serializer;

import java.io.IOException;

/**
 * 序列化工具类
 * @author lifeng
 */
public class SerializationUtils {

	public static Serializer g_ser;
	
	//  ---------------------调用------------------------
	/**
	 * 调用实际的序列化工具 -- 序列化
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] serialize(Object obj) throws SerializeException {
        return g_ser.serialize(obj);
    }

	/**
	 * 调用实际的序列化工具 -- 反序列化
	 * @param obj
	 * @return
	 * @throws IOException
	 */
    public static Object deserialize(byte[] bytes) throws SerializeException {
        return bytes != null ? g_ser.deserialize(bytes) : null;
    }
}
