package com.swak.security.filter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.swak.common.utils.Lists;
import com.swak.common.utils.StringUtils;
import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.handler.WebFilterChain;

public class SimpleNamedFilterList implements NamedFilterList {

	private String name;
	private List<WebFilter> backingList;

	public SimpleNamedFilterList(String name) {
		this(name, Lists.newArrayList());
	}

	public SimpleNamedFilterList(String name, List<WebFilter> backingList) {
		if (backingList == null) {
			throw new NullPointerException(
					"backingList constructor argument cannot be null.");
		}
		this.backingList = backingList;
		setName(name);
	}

	protected void setName(String name) {
		if (!StringUtils.hasText(name)) {
			throw new IllegalArgumentException(
					"Cannot specify a null or empty name.");
		}
		this.name = name;
	}
	
	public boolean add(WebFilter filter) {
        return this.backingList.add(filter);
    }

    public void add(int index, WebFilter filter) {
        this.backingList.add(index, filter);
    }

    public boolean addAll(Collection<? extends WebFilter> c) {
        return this.backingList.addAll(c);
    }

    public boolean addAll(int index, Collection<? extends WebFilter> c) {
        return this.backingList.addAll(index, c);
    }

    public void clear() {
        this.backingList.clear();
    }

    public boolean contains(Object o) {
        return this.backingList.contains(o);
    }

    public boolean containsAll(Collection<?> c) {
        return this.backingList.containsAll(c);
    }

    public WebFilter get(int index) {
        return this.backingList.get(index);
    }

    public int indexOf(Object o) {
        return this.backingList.indexOf(o);
    }

    public boolean isEmpty() {
        return this.backingList.isEmpty();
    }

    public Iterator<WebFilter> iterator() {
        return this.backingList.iterator();
    }

    public int lastIndexOf(Object o) {
        return this.backingList.lastIndexOf(o);
    }

    public ListIterator<WebFilter> listIterator() {
        return this.backingList.listIterator();
    }

    public ListIterator<WebFilter> listIterator(int index) {
        return this.backingList.listIterator(index);
    }

    public WebFilter remove(int index) {
        return this.backingList.remove(index);
    }

    public boolean remove(Object o) {
        return this.backingList.remove(o);
    }

    public boolean removeAll(Collection<?> c) {
        return this.backingList.removeAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return this.backingList.retainAll(c);
    }

    public WebFilter set(int index, WebFilter filter) {
        return this.backingList.set(index, filter);
    }

    public int size() {
        return this.backingList.size();
    }

    public List<WebFilter> subList(int fromIndex, int toIndex) {
        return this.backingList.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return this.backingList.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return this.backingList.toArray(a);
    }

    /**
     * 构建一个代理
     */
	@Override
	public WebFilterChain proxy(WebFilterChain filterChain) {
		return new ProxiedFilterChain(filterChain, this.backingList);
	}

	@Override
	public String getName() {
		return this.name;
	}
}
