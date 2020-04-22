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
import com.swak.exception.NoSuchPropertyException;
import com.swak.persistence.QueryCondition;

/**
 * 通过 Wrapper 自动生成的代码： 需要注意的点： get，set，is，can，has开头的方式默认认为是属性的获取和填充。
 * 不能使用get，is，can，has开头的方法但返回值是void的方法！
 * 
 * @author lifeng
 * @date 2020年4月2日 上午11:24:58
 */
@SuppressWarnings({ "unused", "rawtypes" })
public class WrapperGen extends Wrapper implements Dc {
	public static String[] pns;
	public static Map pts;
	public static String[] mns;
	public static String[] dmns;
	public static Class[] mts0;
	public static Class[] mts1;
	public static Class[] mts2;
	public static Class[] mts3;
	public static Class[] mts4;
	public static Class[] mts5;
	public static Class[] mts6;
	public static Class[] mts7;
	public static Class[] mts8;
	public static Class[] mts9;
	public static Class[] mts10;
	public static Class[] mts11;
	public static Class[] mts12;
	public static Class[] mts13;
	public static Class[] mts14;
	public static Class[] mts15;
	public static Class[] mts16;
	public static Class[] mts17;
	public static Class[] mts18;
	public static Class[] mts19;
	public static Class[] mts20;
	public static Class[] mts21;
	public static Class[] mts22;
	public static Class[] mts23;
	public static Class[] mts24;
	public static Class[] mts25;
	public static Class[] mts26;
	public static Class[] mts27;
	public static Class[] mts28;
	public static Class[] mts29;
	public static Class[] mts30;
	public static Class[] mts31;
	public static Class[] mts32;
	public static Class[] mts33;
	public static Class[] mts34;
	public static Class[] mts35;
	public static Class[] mts36;
	public static Class[] mts37;
	public static Class[] mts38;
	public static Class[] mts39;
	public static Class[] mts40;
	public static Class[] mts41;

	public String[] getPropertyNames() {
		return pns;
	}

	public boolean hasProperty(String var1) {
		return pts.containsKey(var1);
	}

	public Class getPropertyType(String var1) {
		return (Class) pts.get(var1);
	}

	public String[] getMethodNames() {
		return mns;
	}

	public String[] getDeclaredMethodNames() {
		return dmns;
	}

	public void setPropertyValue(Object var1, String var2, Object var3) {
		try {
			OrderDao var4 = (OrderDao) var1;
		} catch (Throwable var6) {
			throw new IllegalArgumentException(var6);
		}

		throw new NoSuchPropertyException(
				"Not found property \"" + var2 + "\" filed or setter method in class com.swak.wrapper.OrderDao.");
	}

	public Object getPropertyValue(Object var1, String var2) {
		OrderDao var3;
		try {
			var3 = (OrderDao) var1;
		} catch (Throwable var5) {
			throw new IllegalArgumentException(var5);
		}

		if (var2.equals("all")) {
			return var3.getAll();
		} else {
			throw new NoSuchPropertyException(
					"Not found property \"" + var2 + "\" filed or setter method in class com.swak.wrapper.OrderDao.");
		}
	}

	@SuppressWarnings({ "unchecked" })
	public Object invokeMethod(Object var1, String var2, Class[] var3, Object[] var4)
			throws InvocationTargetException, NoSuchMethodException {
		OrderDao var5;
		try {
			var5 = (OrderDao) var1;
		} catch (Throwable var8) {
			throw new IllegalArgumentException(var8);
		}

		try {
			if ("lock".equals(var2) && var3.length == 1) {
				return new Boolean(var5.lock((Object) var4[0]));
			}

            if ("get".equals(var2) && var3.length == 1) {
                return var5.get((Long)var4[0]);
            }

            if ("update".equals(var2) && var3.length == 1 && var3[0].getName().equals("java.lang.Object")) {
                return new Integer(var5.update((Order)var4[0]));
            }

            if ("update".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String") && var3[1].getName().equals("java.lang.Object")) {
                return new Integer(var5.update((String)var4[0], (Order)var4[1]));
            }

            if ("delete".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String") && var3[1].getName().equals("java.lang.Object")) {
                return new Integer(var5.delete((String)var4[0], (Order)var4[1]));
            }

            if ("delete".equals(var2) && var3.length == 1 && var3[0].getName().equals("java.lang.Object")) {
                return new Integer(var5.delete((Order)var4[0]));
            }

            if ("insert".equals(var2) && var3.length == 1 && var3[0].getName().equals("java.lang.Object")) {
                return var5.insert((Order)var4[0]);
            }

            if ("insert".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String") && var3[1].getName().equals("java.lang.Object")) {
                return var5.insert((String)var4[0], (Order)var4[1]);
            }

            if ("exists".equals(var2) && var3.length == 1) {
                return new Boolean(var5.exists((Long)var4[0]));
            }

            if ("compareVersion".equals(var2) && var3.length == 1) {
                var5.compareVersion((Order)var4[0]);
                return null;
            }

			if ("queryForLimitList".equals(var2) && var3.length == 3 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("com.swak.persistence.QueryCondition")
					&& var3[2].getName().equals("int")) {
				return var5.queryForLimitList((String) var4[0], (QueryCondition) var4[1],
						((Number) var4[2]).intValue());
			}

			if ("queryForLimitList".equals(var2) && var3.length == 3 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.util.Map") && var3[2].getName().equals("int")) {
				return var5.queryForLimitList((String) var4[0], (Map) var4[1], ((Number) var4[2]).intValue());
			}

			if ("queryForLimitList".equals(var2) && var3.length == 2
					&& var3[0].getName().equals("com.swak.persistence.QueryCondition")
					&& var3[1].getName().equals("int")) {
				return var5.queryForLimitList((QueryCondition) var4[0], ((Number) var4[1]).intValue());
			}

			if ("queryForLimitList".equals(var2) && var3.length == 3 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.lang.Object") && var3[2].getName().equals("int")) {
				return var5.queryForLimitList((String) var4[0], (Object) var4[1], ((Number) var4[2]).intValue());
			}

			if ("queryForGenericsList".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.util.Map")) {
				return var5.queryForGenericsList((String) var4[0], (Map) var4[1]);
			}

			if ("queryForGenericsList".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.lang.Object")) {
				return var5.queryForGenericsList((String) var4[0], (Object) var4[1]);
			}

			if ("queryForMapPageList".equals(var2) && var3.length == 3) {
				return var5.queryForMapPageList((String) var4[0], (Map) var4[1], (Parameters) var4[2]);
			}

			if ("getAll".equals(var2) && var3.length == 0) {
				return var5.getAll();
			}

			if ("queryByCondition".equals(var2) && var3.length == 1) {
				return var5.queryByCondition((QueryCondition) var4[0]);
			}

			if ("queryForList".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.lang.Object")) {
				return var5.queryForList((String) var4[0], (Object) var4[1]);
			}

			if ("queryForList".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("com.swak.persistence.QueryCondition")) {
				return var5.queryForList((String) var4[0], (QueryCondition) var4[1]);
			}

			if ("queryForList".equals(var2) && var3.length == 1 && var3[0].getName().equals("java.lang.String")) {
				return var5.queryForList((String) var4[0]);
			}

			if ("queryForList".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.util.Map")) {
				return var5.queryForList((String) var4[0], (Map) var4[1]);
			}

			if ("queryForMapList".equals(var2) && var3.length == 2) {
				return var5.queryForMapList((String) var4[0], (Map) var4[1]);
			}

			if ("queryForIdList".equals(var2) && var3.length == 2) {
				return var5.queryForIdList((String) var4[0], (Map) var4[1]);
			}

			if ("queryForObject".equals(var2) && var3.length == 1
					&& var3[0].getName().equals("com.swak.persistence.QueryCondition")) {
				return var5.queryForObject((QueryCondition) var4[0]);
			}

			if ("queryForObject".equals(var2) && var3.length == 1 && var3[0].getName().equals("java.lang.String")) {
				return var5.queryForObject((String) var4[0]);
			}

			if ("queryForObject".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.util.Map")) {
				return var5.queryForObject((String) var4[0], (Map) var4[1]);
			}

			if ("queryForObject".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.lang.Object")) {
				return var5.queryForObject((String) var4[0], (Object) var4[1]);
			}

			if ("queryForAttr".equals(var2) && var3.length == 2) {
				return var5.queryForAttr((String) var4[0], (Object) var4[1]);
			}

			if ("queryForPageList".equals(var2) && var3.length == 3 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("com.swak.persistence.QueryCondition")
					&& var3[2].getName().equals("com.swak.entity.Parameters")) {
				return var5.queryForPageList((String) var4[0], (QueryCondition) var4[1], (Parameters) var4[2]);
			}

			if ("queryForPageList".equals(var2) && var3.length == 3 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.util.Map")
					&& var3[2].getName().equals("com.swak.entity.Parameters")) {
				return var5.queryForPageList((String) var4[0], (Map) var4[1], (Parameters) var4[2]);
			}

			if ("queryForPage".equals(var2) && var3.length == 2) {
				return var5.queryForPage((QueryCondition) var4[0], (Parameters) var4[1]);
			}

			if ("countByCondition".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.util.Map")) {
				return var5.countByCondition((String) var4[0], (Map) var4[1]);
			}

			if ("countByCondition".equals(var2) && var3.length == 1
					&& var3[0].getName().equals("com.swak.persistence.QueryCondition")) {
				return var5.countByCondition((QueryCondition) var4[0]);
			}

			if ("countByCondition".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("com.swak.persistence.QueryCondition")) {
				return var5.countByCondition((String) var4[0], (QueryCondition) var4[1]);
			}

			if ("countByCondition".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.lang.Object")) {
				return var5.countByCondition((String) var4[0], (Object) var4[1]);
			}

			if ("batchInsert".equals(var2) && var3.length == 1 && var3[0].getName().equals("java.util.List")) {
				var5.batchInsert((List) var4[0]);
				return null;
			}

			if ("batchInsert".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.util.List")) {
				var5.batchInsert((String) var4[0], (List) var4[1]);
				return null;
			}

			if ("batchUpdate".equals(var2) && var3.length == 1 && var3[0].getName().equals("java.util.List")) {
				var5.batchUpdate((List) var4[0]);
				return null;
			}

			if ("batchUpdate".equals(var2) && var3.length == 2 && var3[0].getName().equals("java.lang.String")
					&& var3[1].getName().equals("java.util.List")) {
				var5.batchUpdate((String) var4[0], (List) var4[1]);
				return null;
			}

			if ("batchDelete".equals(var2) && var3.length == 1) {
				var5.batchDelete((List) var4[0]);
				return null;
			}
		} catch (Throwable var9) {
			throw new InvocationTargetException(var9);
		}

		throw new NoSuchMethodException("Not found method \"" + var2 + "\" in class com.swak.wrapper.OrderDao.");
	}
}
