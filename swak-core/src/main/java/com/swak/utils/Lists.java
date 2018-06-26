package com.swak.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;

/**
 * list 相关
 * 
 * @author lifeng
 * 
 */
public class Lists {

	/**
	 * 默认值
	 * 
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static <T> List<T> defaultList(List<T> l1, List<T> l2) {
		if (l1 != null && l1.size() != 0) {
			return l1;
		} else if (l2 != null) {
			return l2;
		}
		return newArrayList();
	}

	/**
	 * 创建默认的list
	 * 
	 * @return
	 */
	public static <E> ArrayList<E> newArrayList() {
		return new ArrayList<E>();
	}

	/**
	 * 创建默认长度的list
	 * 
	 * @return
	 */
	public static <E> ArrayList<E> newArrayList(int size) {
		return new ArrayList<E>(size);
	}

	@SuppressWarnings("unchecked")
	public static <E> ArrayList<E> newArrayList(E... elements) {
		int capacity = computeArrayListCapacity(elements.length);
		ArrayList<E> list = new ArrayList<E>(capacity);
		Collections.addAll(list, elements);
		return list;
	}

	public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
		return (elements instanceof Collection) ? new ArrayList<E>(CollectionUtils.cast(elements))
				: newArrayList(elements.iterator());
	}

	public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
		ArrayList<E> list = newArrayList();
		while (elements.hasNext()) {
			list.add(elements.next());
		}
		return list;
	}

	static int computeArrayListCapacity(int arraySize) {
		return Ints.saturatedCast(5L + arraySize + (arraySize / 10));
	}

	/**
	 * 列表是否为空
	 * 
	 * @param items
	 * @return
	 */
	public static <T> Boolean isEmpty(List<T> items) {
		return (items == null || items.size() == 0) ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * 将列表按照固定大小分割
	 * 
	 * @param list
	 * @param size
	 * @return
	 */
	public static <T> List<List<T>> partition(List<T> list, int size) {
		return (list instanceof RandomAccess) ? new RandomAccessPartition<T>(list, size) : new Partition<T>(list, size);
	}

	private static class RandomAccessPartition<T> extends Partition<T> implements RandomAccess {
		RandomAccessPartition(List<T> list, int size) {
			super(list, size);
		}
	}

	private static class Partition<T> extends AbstractList<List<T>> {
		final List<T> list;
		final int size;

		Partition(List<T> list, int size) {
			this.list = list;
			this.size = size;
		}

		@Override
		public List<T> get(int index) {
			int start = index * size;
			int end = Math.min(start + size, list.size());
			return list.subList(start, end);
		}

		@Override
		public int size() {
			int result = list.size() / size;
			if (result * size != list.size()) {
				result++;
			}
			return result;
		}

		@Override
		public boolean isEmpty() {
			return list.isEmpty();
		}
	}
}
