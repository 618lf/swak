package com.swak.asm;

import com.swak.exception.InvokeException;
import com.swak.utils.ClassHelper;
import com.swak.utils.ReflectUtils;
import javassist.Modifier;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Method Wrapper -- 为每一个方法生成一个动态类:其实不可取
 *
 * @author: lifeng
 * @date: 2020/3/28 17:37
 */
@Deprecated
public abstract class Invoker<T> {

    private static AtomicLong INVOKER_COUNTER = new AtomicLong(0);
    private static Map<Method, Invoker<?>> INVOKER_MAP = new ConcurrentHashMap<>();

    /**
     * get the invoker
     *
     * @param service 对象
     * @param method  方法
     * @return Invoker
     */
    public static Invoker<?> getInvoker(Object service, Method method) {
        Invoker<?> invoker = INVOKER_MAP.get(method);
        if (invoker == null) {
            invoker = generateInvoker(service, method);
            INVOKER_MAP.put(method, invoker);
        }
        return invoker;
    }

    /**
     * gen invoke
     *
     * @param service 对象
     * @param method  方法
     * @return Invoker
     */
    private static Invoker<?> generateInvoker(Object service, Method method) {

        long id = INVOKER_COUNTER.getAndIncrement();
        ClassLoader cl = ClassHelper.getClassLoader(service.getClass());
        ClassGenerator cc = ClassGenerator.newInstance(cl);
        cc.setClassName(Invoker.class.getName() + id);
        cc.setSuperClass(Invoker.class);
        cc.addConstructor(Modifier.PUBLIC, new Class<?>[]{service.getClass()}, "{$0.service = $1;}");
        cc.addField("service", Modifier.PRIVATE | Modifier.FINAL, service.getClass());

        StringBuilder methodBuilder = new StringBuilder();
        StringBuilder resultBuilder = new StringBuilder();
        methodBuilder.append("public <T> T invoke(Object[] params) {\r\n");
        methodBuilder.append("  return ");
        resultBuilder.append("(T)service.");
        resultBuilder.append(method.getName());
        resultBuilder.append("(");
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> paramType = parameterTypes[i];
            resultBuilder.append("((");
            resultBuilder.append(ReflectUtils.forceCast(paramType));
            resultBuilder.append(")params[");
            resultBuilder.append(i);
            resultBuilder.append("])");
            resultBuilder.append(ReflectUtils.unbox(paramType));
        }
        resultBuilder.append(")");
        String resultStr = ReflectUtils.box(method.getReturnType(), resultBuilder.toString());
        methodBuilder.append(resultStr);
        methodBuilder.append(";\r\n}");
        cc.addMethod(methodBuilder.toString());

        try {
            Class<?> invokerClass = cc.toClass();
            return (Invoker<?>) invokerClass.getConstructor(service.getClass()).newInstance(service);
        } catch (Exception e) {
            throw new InvokeException(e);
        } finally {
            cc.release();
        }
    }

    /**
     * 通过生成的类调用
     *
     * @param params 参数
     * @return 执行结果
     * @author lifeng
     * @date 2020/3/28 17:39
     */
    public abstract T invoke(Object... params);
}