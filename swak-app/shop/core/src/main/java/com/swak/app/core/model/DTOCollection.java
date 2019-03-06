/*
 *     Android基础开发个人积累、沉淀、封装、整理共通
 *     Copyright (c) 2016. 曾繁添 <zftlive@163.com>
 *     Github：https://github.com/zengfantian || http://git.oschina.net/zftlive
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.swak.app.core.model;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 数据传输对象集合
 * 
 * @author 曾繁添
 * @version 1.0
 */
public class DTOCollection<E extends DTO<?, ?>> extends AbstractCollection<E>
		implements Serializable, Cloneable {
	/**
     * 
     */
	private static final long serialVersionUID = 2303371883543057514L;

	public Iterator<E> iterator() {
		try {
			return new DTOIterator();
		} catch (Throwable cause) {
			throw new RuntimeException("创建迭代器失败", cause);
		}
	}

	public int size() {
		return this.transferData.size();
	}

	/**
	 * 赋值
	 * 
	 * @exception RuntimeException
	 *                操作DTO失败异常
	 * @param object
	 *            值
	 */
	public boolean add(E object) {
		if (!DTO.class.isInstance(object)) {
			throw new RuntimeException("要添加的对象不是DTO类或其子类的实例");
		}
		if (readonly) {
			throw new RuntimeException("该DTOCollection不可写");
		} else {
			return this.transferData.add(object);
		}
	}

	protected class DTOIterator implements Iterator<E> {

		public boolean hasNext() {
			return cursor != transferData.size();
		}

		public E next() {
			E dtoObject = transferData.get(cursor++);
			dtoObject.setReadonly(readonly);
			return dtoObject;
		}

		public void remove() {
			if (readonly) {
				throw new RuntimeException("该DTOIterator不可写");
			} else {
				transferData.remove(--cursor);
			}

		}

		public int cursor = 0;
	}

	/**
	 * 传递数据
	 */
	private List<E> transferData = new ArrayList<E>();

	protected List<E> getTransferData() {
		return this.transferData;

	}

	/**
	 * 只读开关
	 */
	private boolean readonly = false;

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

}
