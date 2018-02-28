package com.swak.common.serializer;

import java.io.IOException;

/**
 * 对象序列化接口
 * @author lifeng
 */
public interface Serializer {

	/**
	 * 序列化实现方式
	 * @return
	 */
	public String name();

	/**
	 * 序列化
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public byte[] serialize(Object obj) throws SerializeException ;
	
	/**
	 * 反序列化
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public Object deserialize(byte[] bytes) throws SerializeException ;
}
