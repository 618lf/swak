package com.veni.tools.base;

import java.lang.reflect.ParameterizedType;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 *      类转换初始化
 */
public class TUtil {
    public static <T> T getT(Object o, int i) {
        try {
            return ((Class<T>) ((ParameterizedType) (o.getClass()
                    .getGenericSuperclass())).getActualTypeArguments()[i])
                    .newInstance();
        } catch (InstantiationException ignored) {
        } catch (IllegalAccessException ignored) {
        } catch (ClassCastException ignored) {
        }
        return null;
    }

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }
}
