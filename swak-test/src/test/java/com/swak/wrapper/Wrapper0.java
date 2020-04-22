//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.swak.wrapper;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.swak.asm.ClassGenerator.Dc;
import com.swak.asm.Wrapper;
import com.swak.entity.Parameters;
import com.swak.persistence.QueryCondition;

public class Wrapper0 extends Wrapper implements Dc {
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Object invokeMethod(Object var1, String var2, Object[] var3) throws InvocationTargetException, NoSuchMethodException {
        OrderDao var4;
        try {
            var4 = (OrderDao)var1;
        } catch (Throwable var7) {
            throw new IllegalArgumentException(var7);
        }

        try {
            if ("compareVersion(com.swak.wrapper.Order)".equals(var2)) {
                var4.compareVersion((Order)var3[0]);
                return null;
            }

            if ("batchDelete(java.util.List)".equals(var2)) {
                var4.batchDelete((List)var3[0]);
                return null;
            }

            if ("queryForObject(java.lang.String,java.lang.Object)".equals(var2)) {
                return var4.queryForObject((String)var3[0], (Object)var3[1]);
            }

            if ("delete(java.lang.String,com.swak.wrapper.Order)".equals(var2)) {
                return new Integer(var4.delete((String)var3[0], (Order)var3[1]));
            }

            if ("countByCondition(com.swak.persistence.QueryCondition)".equals(var2)) {
                return var4.countByCondition((QueryCondition)var3[0]);
            }

            if ("delete(com.swak.wrapper.Order)".equals(var2)) {
                return new Integer(var4.delete((Order)var3[0]));
            }

            if ("batchInsert(java.util.List)".equals(var2)) {
                var4.batchInsert((List)var3[0]);
                return null;
            }

            if ("queryForLimitList(java.lang.String,java.lang.Object,int)".equals(var2)) {
                return var4.queryForLimitList((String)var3[0], (Object)var3[1], ((Number)var3[2]).intValue());
            }

            if ("queryForLimitList(com.swak.persistence.QueryCondition,int)".equals(var2)) {
                return var4.queryForLimitList((QueryCondition)var3[0], ((Number)var3[1]).intValue());
            }

            if ("countByCondition(java.lang.String,java.util.Map)".equals(var2)) {
                return var4.countByCondition((String)var3[0], (Map)var3[1]);
            }

            if ("queryForObject(java.lang.String)".equals(var2)) {
                return var4.queryForObject((String)var3[0]);
            }

            if ("countByCondition(java.lang.String,java.lang.Object)".equals(var2)) {
                return var4.countByCondition((String)var3[0], (Object)var3[1]);
            }

            if ("update(com.swak.wrapper.Order)".equals(var2)) {
                return new Integer(var4.update((Order)var3[0]));
            }

            if ("queryForGenericsList(java.lang.String,java.lang.Object)".equals(var2)) {
                return var4.queryForGenericsList((String)var3[0], (Object)var3[1]);
            }

            if ("queryForGenericsList(java.lang.String,java.util.Map)".equals(var2)) {
                return var4.queryForGenericsList((String)var3[0], (Map)var3[1]);
            }

            if ("countByCondition(java.lang.String,com.swak.persistence.QueryCondition)".equals(var2)) {
                return var4.countByCondition((String)var3[0], (QueryCondition)var3[1]);
            }

            if ("queryForList(java.lang.String,java.util.Map)".equals(var2)) {
                return var4.queryForList((String)var3[0], (Map)var3[1]);
            }

            if ("batchUpdate(java.lang.String,java.util.List)".equals(var2)) {
                var4.batchUpdate((String)var3[0], (List)var3[1]);
                return null;
            }

            if ("batchUpdate(java.util.List)".equals(var2)) {
                var4.batchUpdate((List)var3[0]);
                return null;
            }

            if ("update(java.lang.String,com.swak.wrapper.Order)".equals(var2)) {
                return new Integer(var4.update((String)var3[0], (Order)var3[1]));
            }

            if ("lock(java.lang.Object)".equals(var2)) {
                return new Boolean(var4.lock((Object)var3[0]));
            }

            if ("queryForPageList(java.lang.String,java.util.Map,com.swak.entity.Parameters)".equals(var2)) {
                return var4.queryForPageList((String)var3[0], (Map)var3[1], (Parameters)var3[2]);
            }

            if ("queryForMapList(java.lang.String,java.util.Map)".equals(var2)) {
                return var4.queryForMapList((String)var3[0], (Map)var3[1]);
            }

            if ("getAll(void)".equals(var2)) {
                return var4.getAll();
            }

            if ("queryForPageList(java.lang.String,com.swak.persistence.QueryCondition,com.swak.entity.Parameters)".equals(var2)) {
                return var4.queryForPageList((String)var3[0], (QueryCondition)var3[1], (Parameters)var3[2]);
            }

            if ("queryForIdList(java.lang.String,java.util.Map)".equals(var2)) {
                return var4.queryForIdList((String)var3[0], (Map)var3[1]);
            }

            if ("queryForObject(com.swak.persistence.QueryCondition)".equals(var2)) {
                return var4.queryForObject((QueryCondition)var3[0]);
            }

            if ("insert(java.lang.String,com.swak.wrapper.Order)".equals(var2)) {
                return var4.insert((String)var3[0], (Order)var3[1]);
            }

            if ("queryForList(java.lang.String)".equals(var2)) {
                return var4.queryForList((String)var3[0]);
            }

            if ("queryForLimitList(java.lang.String,java.util.Map,int)".equals(var2)) {
                return var4.queryForLimitList((String)var3[0], (Map)var3[1], ((Number)var3[2]).intValue());
            }

            if ("queryForAttr(java.lang.String,java.lang.Object)".equals(var2)) {
                return var4.queryForAttr((String)var3[0], (Object)var3[1]);
            }

            if ("queryForPage(com.swak.persistence.QueryCondition,com.swak.entity.Parameters)".equals(var2)) {
                return var4.queryForPage((QueryCondition)var3[0], (Parameters)var3[1]);
            }

            if ("queryForMapPageList(java.lang.String,java.util.Map,com.swak.entity.Parameters)".equals(var2)) {
                return var4.queryForMapPageList((String)var3[0], (Map)var3[1], (Parameters)var3[2]);
            }

            if ("queryForLimitList(java.lang.String,com.swak.persistence.QueryCondition,int)".equals(var2)) {
                return var4.queryForLimitList((String)var3[0], (QueryCondition)var3[1], ((Number)var3[2]).intValue());
            }

            if ("queryByCondition(com.swak.persistence.QueryCondition)".equals(var2)) {
                return var4.queryByCondition((QueryCondition)var3[0]);
            }

            if ("get(java.lang.Long)".equals(var2)) {
                return var4.get((Long)var3[0]);
            }

            if ("batchInsert(java.lang.String,java.util.List)".equals(var2)) {
                var4.batchInsert((String)var3[0], (List)var3[1]);
                return null;
            }

            if ("queryForList(java.lang.String,java.lang.Object)".equals(var2)) {
                return var4.queryForList((String)var3[0], (Object)var3[1]);
            }

            if ("exists(java.lang.Long)".equals(var2)) {
                return new Boolean(var4.exists((Long)var3[0]));
            }

            if ("queryForList(java.lang.String,com.swak.persistence.QueryCondition)".equals(var2)) {
                return var4.queryForList((String)var3[0], (QueryCondition)var3[1]);
            }

            if ("insert(com.swak.wrapper.Order)".equals(var2)) {
                return var4.insert((Order)var3[0]);
            }

            if ("queryForObject(java.lang.String,java.util.Map)".equals(var2)) {
                return var4.queryForObject((String)var3[0], (Map)var3[1]);
            }
        } catch (Throwable var8) {
            throw new InvocationTargetException(var8);
        }

        throw new NoSuchMethodException("Not found method \"" + var2 + "\" in class com.swak.wrapper.OrderDao.");
    }

    public Wrapper0() {
    }
}
