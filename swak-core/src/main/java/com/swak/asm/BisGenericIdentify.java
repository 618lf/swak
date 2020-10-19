package com.swak.asm;

import com.swak.utils.Maps;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * 泛型识别
 *
 * @author lifeng
 * @date 2020年10月7日 下午9:53:38
 */
public interface BisGenericIdentify<T, PK> {

    /**
     * 目标对象 T 的实际类型
     *
     * @return 目标对象 T 的实际类型
     */
    @SuppressWarnings("unchecked")
    default Class<T> getEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 目标对象 T 的实际类型
     *
     * @return 目标对象 T, PK 的实际类型
     */
    @SuppressWarnings("unchecked")
    default Map<Class<T>, Class<PK>> getEntitysClass() {
        Map<Class<T>, Class<PK>> cls = Maps.newOrderMap();
        Type[] tys = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        cls.put((Class<T>) tys[0], (Class<PK>) tys[1]);
        return cls;
    }
}