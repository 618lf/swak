package com.swak.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Sets {

	/**
	 * 获取set 中任意一个元素
	 * 
	 * @param s
	 * @return
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
	 * @return
	 */
	public static <E> HashSet<E> newHashSet() {
		return new HashSet<E>();
	}
	
	/**
	 * Hash Set
	 * 
	 * @return
	 */
	public static <E> HashSet<E> newHashSet(int size) {
		return new HashSet<E>(size);
	}

	/**
	 * arrays -》 set 
	 * @param elements
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> HashSet<E> newHashSet(E... elements) {
		HashSet<E> set = newHashSetWithExpectedSize(elements.length);
		Collections.addAll(set, elements);
		return set;
	}

	/**
	 * 期望大小
	 * @param expectedSize
	 * @return
	 */
	public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) {
		return new HashSet<E>(Maps.capacity(expectedSize));
	}
	
	/**
	 * list - 》 set
	 * @param elements
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> HashSet<E> newHashSet(Iterable<? extends E> elements) {
	    return (HashSet<E>) ((elements instanceof Collection)
	        ? new HashSet<E>(CollectionUtils.cast(elements))
	        : newHashSet(elements.iterator()));
	}
	
	/**
	 * 创建一个有序的Set
	 * @return
	 */
	public static <K> LinkedHashSet<K> newOrderSet() {
		return new LinkedHashSet<K>();
	}
}
