package com.swak.utils;

import com.swak.entity.SortAble;

import java.util.*;

/**
 * list 相关
 *
 * @author: lifeng
 * @date: 2020/3/29 14:20
 */
public class Lists {

    /**
     * The index value when an element is not found in a list or array: {@code -1}.
     * This value is returned by methods in this class and can also be used in
     * comparisons with values returned by various method from
     * {@link java.util.List}.
     */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * 默认值
     *
     * @param l1 集合
     * @param l2 集合
     * @return 默认值
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
     * @return 默认的list
     */
    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }

    /**
     * 创建默认长度的list
     *
     * @param size 长度
     * @return 默认的list
     */
    public static <E> ArrayList<E> newArrayList(int size) {
        return new ArrayList<>(size);
    }

    @SuppressWarnings("unchecked")
    public static <E> ArrayList<E> newArrayList(E... elements) {
        int capacity = computeArrayListCapacity(elements.length);
        ArrayList<E> list = new ArrayList<>(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
        return (elements instanceof Collection) ? new ArrayList<>(CollectionUtils.cast(elements))
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
     * @param items 集合
     * @return 是否为空
     */
    public static <T> Boolean isEmpty(List<T> items) {
        return (items == null || items.size() == 0) ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 将列表按照固定大小分割
     *
     * @param list 集合
     * @param size 大小
     * @return 分割集合
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        return (list instanceof RandomAccess) ? new RandomAccessPartition<>(list, size) : new Partition<>(list, size);
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

    /**
     * 排序
     *
     * @param list 集合
     */
    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        list.sort(null);
    }

    /**
     * 如果实现了 SortAble 可以通过一个sort 来排序
     *
     * @param list 集合
     */
    public static <T extends SortAble> void sortAble(List<T> list) {
        list.sort((Comparator<SortAble>) (o1, o2) -> o1.getSort().compareTo(o2.getSort()));
    }

    /**
     * 排序
     *
     * @param list 集合
     * @param c    比较方式
     */
    public static <T> void sort(List<T> list, Comparator<? super T> c) {
        list.sort(c);
    }

    /**
     * <p>
     * Checks if the object is in the given array.
     * </p>
     *
     * <p>
     * The method returns {@code false} if a {@code null} array is passed in.
     * </p>
     *
     * @param array        the array to search through
     * @param objectToFind the object to find
     * @return {@code true} if the array contains the object
     */
    public static boolean contains(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind) != INDEX_NOT_FOUND;
    }

    // -----------------------------------------------------------------------

    /**
     * <p>
     * Finds the index of the given object in the array.
     * </p>
     *
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
     * input array.
     * </p>
     *
     * @param array        the array to search through for the object, may be {@code null}
     * @param objectToFind the object to find, may be {@code null}
     * @return the index of the object within the array, {@link #INDEX_NOT_FOUND}
     * ({@code -1}) if not found or {@code null} array input
     */
    public static int indexOf(Object[] array, Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }

    /**
     * <p>
     * Finds the index of the given object in the array starting at the given index.
     * </p>
     *
     * <p>
     * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
     * input array.
     * </p>
     *
     * <p>
     * A negative startIndex is treated as zero. A startIndex larger than the array
     * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
     * </p>
     *
     * @param array        the array to search through for the object, may be {@code null}
     * @param objectToFind the object to find, may be {@code null}
     * @param startIndex   the index to start searching at
     * @return the index of the object within the array starting at the index,
     * {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null}
     * array input
     */
    public static int indexOf(Object[] array, Object objectToFind, int startIndex) {
        if (array == null) {
            return INDEX_NOT_FOUND;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return INDEX_NOT_FOUND;
    }
}
