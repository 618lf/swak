package com.swak.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Set 工具类
 *
 * @author: lifeng
 * @date: 2020/3/29 15:17
 */
public class Sets {

    /**
     * 获取set 中任意一个元素
     *
     * @param s 集合
     * @return 任意一个值
     */
    public static <T> T first(Set<T> s) {
        if (s == null) {
            return null;
        }
        return s.iterator().next();
    }

    /**
     * Hash Set
     *
     * @return HashSet
     */
    public static <E> HashSet<E> newHashSet() {
        return new HashSet<>();
    }

    /**
     * Hash Set
     *
     * @param size 大小
     * @return HashSet
     */
    public static <E> HashSet<E> newHashSet(int size) {
        return new HashSet<>(size);
    }

    /**
     * arrays -》 set
     *
     * @param elements arrays
     * @return HashSet
     */
    @SuppressWarnings("unchecked")
    public static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = newHashSetWithExpectedSize(elements.length);
        Collections.addAll(set, elements);
        return set;
    }

    /**
     * 期望大小
     *
     * @param expectedSize 期望大小
     * @return HashSet
     */
    public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) {
        return new HashSet<>(Maps.capacity(expectedSize));
    }

    /**
     * list - 》 set
     *
     * @param elements 集合
     * @return newHashSet
     */
    @SuppressWarnings("unchecked")
    public static <E> HashSet<E> newHashSet(Iterable<? extends E> elements) {
        return (HashSet<E>) ((elements instanceof Collection)
                ? new HashSet<>(CollectionUtils.cast(elements))
                : newHashSet(elements.iterator()));
    }

    /**
     * 创建一个有序的Set
     *
     * @return LinkedHashSet
     */
    public static <K> LinkedHashSet<K> newOrderSet() {
        return new LinkedHashSet<>();
    }
}
