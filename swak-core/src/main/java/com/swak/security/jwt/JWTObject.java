package com.swak.security.jwt;

import java.util.Map;

import com.alibaba.fastjson.util.TypeUtils;

/**
 * jwt 数据操作
 *
 * @author lifeng
 */
public abstract class JWTObject {

    /**
     * 真实的存储数据
     */
    protected Map<String, Object> map;

    /**
     * 获取数据
     *
     * @param key key
     * @return value
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) map.get(key);
    }

    /**
     * 获取数据 - Long 返回
     *
     * @param key key
     * @return value
     */
    public Long getLong(String key) {
        Object value = this.get(key);
        return TypeUtils.castToLong(value);
    }

    /**
     * 添加项目
     *
     * @param key   key
     * @param value default value
     * @return value
     */
    public <T extends JWTObject> T put(String key, Object value) {
        map.put(key, value);
        return as();
    }

    /**
     * 是否包含属性
     *
     * @param key key
     * @return 是否包含属性
     */
    public Boolean containsKey(String key) {
        return this.map.containsKey(key);
    }

    /**
     * 当前的对象
     *
     * @return 当前的对象
     */
    @SuppressWarnings("unchecked")
    protected <T extends JWTObject> T as() {
        return (T) this;
    }

    /**
     * 实际的数据
     *
     * @return 实际的数据
     */
    public Map<String, Object> getData() {
        return map;
    }

    /**
     * 格式化为JOSN
     *
     * @return 格式化为JOSN
     */
    public String encode() {
        return JWTEncode.encode(this.map);
    }
}
