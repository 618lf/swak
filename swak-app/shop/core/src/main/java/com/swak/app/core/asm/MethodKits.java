package com.swak.app.core.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 *  执行方法
 *
 */
public class MethodKits {

    private static Map<String, Method> sMethodCache = new HashMap<String, Method>();

    private static String getKey(final Class<?> cls, final String methodName, final Class<?>... parameterTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append(cls.toString()).append("#").append(methodName);
        if (parameterTypes != null && parameterTypes.length > 0) {
            for (Class<?> parameterType : parameterTypes) {
                sb.append(parameterType.toString()).append("#");
            }
        } else {
            sb.append(Void.class.toString());
        }
        return sb.toString();
    }


    private static Method getAccessibleMethodFromSuperclass(final Class<?> cls,
                                                            final String methodName, final Class<?>... parameterTypes) {
        Class<?> parentClass = cls.getSuperclass();
        while (parentClass != null) {
            if (Modifier.isPublic(parentClass.getModifiers())) {
                try {
                    return parentClass.getMethod(methodName, parameterTypes);
                } catch (final NoSuchMethodException e) {
                    return null;
                }
            }
            parentClass = parentClass.getSuperclass();
        }
        return null;
    }


    private static Method getAccessibleMethodFromInterfaceNest(Class<?> cls,
                                                               final String methodName, final Class<?>... parameterTypes) {
        // Search up the superclass chain
        for (; cls != null; cls = cls.getSuperclass()) {

            // Check the implemented interfaces of the parent class
            final Class<?>[] interfaces = cls.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                // Is this interface public?
                if (!Modifier.isPublic(interfaces[i].getModifiers())) {
                    continue;
                }
                // Does the method exist on this interface?
                try {
                    return interfaces[i].getDeclaredMethod(methodName,
                            parameterTypes);
                } catch (final NoSuchMethodException e) { // NOPMD
                    /*
                     * Swallow, if no method is found after the loop then this
                     * method returns null.
                     */
                }
                // Recursively check our parent interfaces
                Method method = getAccessibleMethodFromInterfaceNest(interfaces[i],
                        methodName, parameterTypes);
                if (method != null) {
                    return method;
                }
            }
        }
        return null;
    }

    private static Method getAccessibleMethod(Method method) {
        if (!MemberKits.isAccessible(method)) {
            return null;
        }
        // If the declaring class is public, we are done
        final Class<?> cls = method.getDeclaringClass();
        if (Modifier.isPublic(cls.getModifiers())) {
            return method;
        }
        final String methodName = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();

        // Check the implemented interfaces and subinterfaces
        method = getAccessibleMethodFromInterfaceNest(cls, methodName,
                parameterTypes);

        // Check the superclass chain
        if (method == null) {
            method = getAccessibleMethodFromSuperclass(cls, methodName,
                    parameterTypes);
        }
        return method;
    }

    public static Method getAccessibleMethod(final Class<?> cls, final String methodName,
                                             final Class<?>... parameterTypes) throws NoSuchMethodException {
        String key = getKey(cls, methodName, parameterTypes);
        Method method;
        synchronized (sMethodCache) {
            method = sMethodCache.get(key);
        }
        if (method != null) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            return method;
        }

        Method accessibleMethod = getAccessibleMethod(cls.getMethod(methodName,
                parameterTypes));
        synchronized (sMethodCache) {
            sMethodCache.put(key, accessibleMethod);
        }
        return accessibleMethod;

    }

    private static Method getMatchingAccessibleMethod(final Class<?> cls,
                                                      final String methodName, final Class<?>... parameterTypes) {

        String key = getKey(cls, methodName, parameterTypes);
        Method cachedMethod;
        synchronized (sMethodCache) {
            cachedMethod = sMethodCache.get(key);
        }
        if (cachedMethod != null) {
            if (!cachedMethod.isAccessible()) {
                cachedMethod.setAccessible(true);
            }
            return cachedMethod;
        }

        try {
            final Method method = cls.getMethod(methodName, parameterTypes);
            MemberKits.setAccessibleWorkaround(method);
            synchronized (sMethodCache) {
                sMethodCache.put(key, method);
            }
            return method;
        } catch (final NoSuchMethodException e) { // NOPMD - Swallow the exception
        }
        // search through all methods
        Method bestMatch = null;
        final Method[] methods = cls.getMethods();
        for (final Method method : methods) {
            // compare name and parameters
            if (method.getName().equals(methodName) && MemberKits.isAssignable(parameterTypes, method.getParameterTypes(), true)) {
                // get accessible version of method
                final Method accessibleMethod = getAccessibleMethod(method);
                if (accessibleMethod != null && (bestMatch == null || MemberKits.compareParameterTypes(
                        accessibleMethod.getParameterTypes(),
                        bestMatch.getParameterTypes(),
                        parameterTypes) < 0)) {
                    bestMatch = accessibleMethod;
                }
            }
        }
        if (bestMatch != null) {
            MemberKits.setAccessibleWorkaround(bestMatch);
        }
        synchronized (sMethodCache) {
            sMethodCache.put(key, bestMatch);
        }
        return bestMatch;
    }

    public static Object invokeMethod(final Object object, final String methodName,
                                      Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        parameterTypes = ClassKits.nullToEmpty(parameterTypes);
        args = ClassKits.nullToEmpty(args);
        final Method method = getMatchingAccessibleMethod(object.getClass(),
                methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + methodName + "() on object: "
                    + object.getClass().getName());
        }
        return method.invoke(object, args);
    }

    public static Object invokeStaticMethod(final Class clazz, final String methodName,
                                            Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        parameterTypes = ClassKits.nullToEmpty(parameterTypes);
        args = ClassKits.nullToEmpty(args);
        final Method method = getMatchingAccessibleMethod(clazz,
                methodName, parameterTypes);
        if (method == null) {
            throw new NoSuchMethodException("No such accessible method: "
                    + methodName + "() on object: "
                    + clazz.getName());
        }
        return method.invoke(null, args);
    }

    public static Object invokeStaticMethod(final Class clazz, final String methodName,
                                            Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        args = ClassKits.nullToEmpty(args);
        final Class<?>[] parameterTypes = ClassKits.toClass(args);
        return invokeStaticMethod(clazz, methodName, args, parameterTypes);
    }

    public static Object invokeMethod(final Object object, final String methodName,
                                      Object... args) throws NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        args = ClassKits.nullToEmpty(args);
        final Class<?>[] parameterTypes = ClassKits.toClass(args);
        return invokeMethod(object, methodName, args, parameterTypes);
    }

    public static <T> T invokeConstructor(final Class<T> cls, Object... args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        args = ClassKits.nullToEmpty(args);
        final Class<?> parameterTypes[] = ClassKits.toClass(args);
        return invokeConstructor(cls, args, parameterTypes);
    }

    public static <T> T invokeConstructor(final Class<T> cls, Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        args = ClassKits.nullToEmpty(args);
        parameterTypes = ClassKits.nullToEmpty(parameterTypes);
        final Constructor<T> ctor = getMatchingAccessibleConstructor(cls, parameterTypes);
        if (ctor == null) {
            throw new NoSuchMethodException(
                    "No such accessible constructor on object: " + cls.getName());
        }
        return ctor.newInstance(args);
    }

    public static <T> Constructor<T> getMatchingAccessibleConstructor(final Class<T> cls,
                                                                      final Class<?>... parameterTypes) {
        isTrue(cls != null, "class cannot be null");
        // see if we can find the constructor directly
        // most of the time this works and it's much faster
        try {
            final Constructor<T> ctor = cls.getConstructor(parameterTypes);
            MemberKits.setAccessibleWorkaround(ctor);
            return ctor;
        } catch (final NoSuchMethodException e) { // NOPMD - Swallow
        }
        Constructor<T> result = null;
        /*
         * (1) Class.getConstructors() is documented to return Constructor<T> so as
         * long as the array is not subsequently modified, everything's fine.
         */
        final Constructor<?>[] ctors = cls.getConstructors();

        // return best match:
        for (Constructor<?> ctor : ctors) {
            // compare parameters
            if (MemberKits.isAssignable(parameterTypes, ctor.getParameterTypes(), true)) {
                // get accessible version of constructor
                ctor = getAccessibleConstructor(ctor);
                if (ctor != null) {
                    MemberKits.setAccessibleWorkaround(ctor);
                    if (result == null
                            || MemberKits.compareParameterTypes(ctor.getParameterTypes(), result
                            .getParameterTypes(), parameterTypes) < 0) {
                        // temporary variable for annotation, see comment above (1)
                        @SuppressWarnings("unchecked")
                        final
                        Constructor<T> constructor = (Constructor<T>) ctor;
                        result = constructor;
                    }
                }
            }
        }
        return result;
    }

    private static <T> Constructor<T> getAccessibleConstructor(final Constructor<T> ctor) {
        isTrue(ctor != null, "constructor cannot be null");
        return MemberKits.isAccessible(ctor)
                && isAccessible(ctor.getDeclaringClass()) ? ctor : null;
    }

    private static boolean isAccessible(final Class<?> type) {
        Class<?> cls = type;
        while (cls != null) {
            if (!Modifier.isPublic(cls.getModifiers())) {
                return false;
            }
            cls = cls.getEnclosingClass();
        }
        return true;
    }

    static void isTrue(final boolean expression, final String message, final Object... values) {
        if (expression == false) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }
}
