package com.swak.common.serializer;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.common.config.Globals;

/**
 * 序列化工具类
 * @author lifeng
 */
public class SerializationUtils {

	private final static Logger log = LoggerFactory.getLogger(SerializationUtils.class);
	private static Serializer g_ser;
	
	static {
		
		//缓存序列化实现方式
		String ser = Globals.getConfig(Globals.CACHE_SERIALIZATION);
		
		//初始化
		if (ser == null || "".equals(ser.trim())) {
			g_ser = new JavaSerializer();
		}else {
			if (ser.equals("java")) {
                g_ser = new JavaSerializer();
            } else if (ser.equals("fst")) {
                g_ser = new FSTSerializer();
            } else if (ser.equals("kryo")) {
                g_ser = new KryoSerializer();
            } else if (ser.equals("kryo_pool")){
            	g_ser = new KryoPoolSerializer();
            } else {
                try {
                    g_ser = (Serializer) Class.forName(ser).newInstance();
                } catch (Exception e) {
                    throw new SerializeException("Cannot initialize Serializer named [" + ser + ']', e);
                }
            }
        }
		log.info("Using Serializer -> [" + g_ser.name() + ":" + g_ser.getClass().getName() + ']');
	}
	
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
        return g_ser.deserialize(bytes);
    }
}
