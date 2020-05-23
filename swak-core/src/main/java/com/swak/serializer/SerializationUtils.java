package com.swak.serializer;

import com.swak.exception.SerializeException;

/**
 * 序列化工具类
 *
 * @author: lifeng
 * @date: 2020/3/29 13:31
 */
public class SerializationUtils {

    private static Serializer g_ser;

    /**
     * 设置默认序列化
     *
     * @param serialization 序列化方式
     */
    public static void setSerializer(String serialization) {
        Serializer gSer;
		switch (serialization) {
			case "kryo":
				gSer = new KryoSerializer();
				break;
			case "kryo_pool":
				gSer = new KryoPoolSerializer();
				break;
			default:
				gSer = new JavaSerializer();
				break;
		}

        // 公共引用
        SerializationUtils.g_ser = gSer;
    }

    // ---------------------调用------------------------

    /**
     * 调用实际的序列化工具 -- 序列化
     *
     * @param obj 对象
     * @return 字节数组
     * @throws SerializeException 序列化错误
     */
    public static byte[] serialize(Object obj) throws SerializeException {
        return g_ser.serialize(obj);
    }

    /**
     * 调用实际的序列化工具 -- 反序列化
     *
     * @param bytes 字节数组
     * @return 对象
     * @throws SerializeException 序列化错误
     */
    public static Object deserialize(byte[] bytes) throws SerializeException {
        return bytes != null ? g_ser.deserialize(bytes) : null;
    }

}
