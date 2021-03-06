package com.swak.utils;

import org.springframework.core.env.Environment;

import java.lang.reflect.Method;

/**
 * 加载 Properties 文件转换为 对象
 *
 * @author: lifeng
 * @date: 2020/4/1 12:35
 */
public class PropertyKit {

    /**
     * Support Spring Boot 2.x
     */
    @SuppressWarnings("unchecked")
    public static <T> T handle(final Environment environment, final String prefix, final Class<T> targetClass) {
        try {
            Class<?> binderClass = Class.forName("org.springframework.boot.context.properties.bind.Binder");
            Method getMethod = binderClass.getDeclaredMethod("get", Environment.class);
            Method bindMethod = binderClass.getDeclaredMethod("bind", String.class, Class.class);
            Object binderObject = getMethod.invoke(null, environment);
            String prefixParam = prefix.endsWith(".") ? prefix.substring(0, prefix.length() - 1) : prefix;
            Object bindResultObject = bindMethod.invoke(binderObject, prefixParam, targetClass);
            Method resultGetMethod = bindResultObject.getClass().getDeclaredMethod("get");
            return (T) resultGetMethod.invoke(bindResultObject);
        } catch (Exception ignored) {
        }
        return null;
    }
}