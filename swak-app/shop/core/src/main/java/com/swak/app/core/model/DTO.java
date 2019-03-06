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
import java.util.HashMap;
import java.util.Iterator;

/**
 * 数据传输对象
 *
 * @author 曾繁添
 * @version 1.0
 */
public class DTO<K, V> extends HashMap<K, V> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6377960213315892547L;

    /**
     * 赋值
     *
     * @param objKey   键值
     * @param objValue 对应值
     */
    public V put(K objKey, V objValue) {
        if (readonly) {
            throw new RuntimeException("属性只读");
        } else {
            return super.put(objKey, objValue);
        }

    }

    /**
     * 只读开关
     */
    private boolean readonly = false;

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * 移除空值的Item
     */
    public void removeEmptyValueItem() {
        // 边遍历边移除操作必须使用迭代器方式遍历
        Iterator<Entry<K, V>> it = entrySet().iterator();
        while (it.hasNext()) {
            Entry<K, V> entry = it.next();
            if (null == entry.getValue() || "".equals(String.valueOf(entry.getValue()))) {
                it.remove();
            }
        }
    }

}
