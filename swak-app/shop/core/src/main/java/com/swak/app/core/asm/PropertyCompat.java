package com.swak.app.core.asm;

import java.lang.reflect.InvocationTargetException;

/**
 * 安卓系统属性兼容API
 */
public class PropertyCompat {

    private static Class<?> sClass;

    private static Class getMyClass() throws ClassNotFoundException {
        if (sClass == null) {
            sClass = Class.forName("android.os.SystemProperties");
        }
        return sClass;
    }

    private static String getInner(String key, String defaultValue) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        Class clazz = getMyClass();
        return (String) MethodKits.invokeStaticMethod(clazz, "get", key, defaultValue);
    }

    public static String get(String key, String defaultValue) {
        try {
            return getInner(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
