package com.swak.serializer;

import com.swak.exception.SerializeException;

/**
 * 对象序列化接口
 *
 * @author: lifeng
 * @date: 2020/3/29 13:34
 */
public interface Serializer {

    /**
     * 序列化实现方式
     *
     * @return String
     */
    String name();

    /**
     * 调用实际的序列化工具 -- 序列化
     *
     * @param obj 对象
     * @return 字节数组
     * @throws SerializeException 序列化错误
     */
    byte[] serialize(Object obj) throws SerializeException;

    /**
     * 调用实际的序列化工具 -- 反序列化
     *
     * @param bytes 字节数组
     * @return 对象
     * @throws SerializeException 序列化错误
     */
    Object deserialize(byte[] bytes) throws SerializeException;
}
