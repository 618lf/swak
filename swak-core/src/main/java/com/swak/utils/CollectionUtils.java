package com.swak.utils;

import java.util.Collection;

/**
 * 集合操作类,补充
 *
 * @author lifeng
 */
public final class CollectionUtils extends org.springframework.util.CollectionUtils {

    /**
     * 转换
     */
    public static <T> Collection<T> cast(Iterable<T> iterable) {
        return (Collection<T>) iterable;
    }
}
