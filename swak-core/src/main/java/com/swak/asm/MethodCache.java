package com.swak.asm;

import com.swak.Constants;
import com.swak.annotation.FluxService;
import com.swak.annotation.RestApi;
import com.swak.annotation.RestPage;
import com.swak.annotation.TimeOut;
import com.swak.exception.BaseRuntimeException;
import com.swak.utils.Maps;
import com.swak.utils.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 方法元数据
 *
 * @author lifeng
 * @date 2020年4月20日 下午6:10:43
 */
public class MethodCache {

    /**
     * 日志
     */
    static Logger logger = LoggerFactory.getLogger(MethodCache.class);

    /**
     * 缓存
     */
    static Map<Class<?>, ClassMeta> CACHES = new ConcurrentHashMap<>();

    /**
     * 设置 field
     *
     * @param type 字段类型
     * @return ClassMeta 类型元数据
     * @author lifeng
     * @date 2020/3/28 17:33
     */
    public static ClassMeta set(Class<?> type) {
        CACHES.computeIfAbsent(type, (key) -> new ClassMeta(type));
        return CACHES.get(type);
    }

    /**
     * 获取元数据
     *
     * @param type 类型
     * @return ClassMeta 类型元数据
     * @author lifeng
     * @date 2020/3/28 17:35
     */
    public static ClassMeta get(Class<?> type) {
        return CACHES.get(type);
    }

    /**
     * 类型元数据
     *
     * @author: lifeng
     * @date: 2020/3/28 17:36
     */
    public static class ClassMeta {

        /**
         * 创建index
         */
        private Map<String, MethodMeta> namedIndex;
        private Map<Method, MethodMeta> methodIndex;

        /**
         * 缓存整个类型的 public方法
         *
         * @param type 类型和接口
         */
        ClassMeta(Class<?> type) {

            // 所有的元数据
            Set<MethodMeta> metas = Sets.newHashSet();

            // 级联处理元数据
            this.cascadeBuildIn(metas, type, null);

            // 类本身是异步方法，不需要生成异步方法， 如果本生是异步方法则不需要生成异步接口，则两种索引方式都需要
            // 在调用时会去做一个补充查询
            if (type.getAnnotation(FluxService.class) != null) {
                this.namedIndex = Maps.newHashMap();
                for (MethodMeta meta : metas) {
                    this.namedIndex.put(meta.getMethodDesc(), meta);
                }
            }
            // 类是API 或者 异步接口
            else if (type.getAnnotation(RestApi.class) != null || type.getAnnotation(RestPage.class) != null
                    || type.isInterface()) {
                this.methodIndex = Maps.newHashMap();
                for (MethodMeta meta : metas) {
                    this.methodIndex.put(meta.getMethod(), meta);
                }
            }
            // 服务+API是同一个类，或者普通的类
            else {
                this.methodIndex = Maps.newHashMap();
                this.namedIndex = Maps.newHashMap();
                for (MethodMeta meta : metas) {
                    this.methodIndex.put(meta.getMethod(), meta);
                    this.namedIndex.put(meta.getMethodDesc(), meta);
                }
            }

            // for gc
            metas.clear();
            metas = null;
        }

        /**
         * 缓存整个类型的 public方法
         *
         * @param type 类型和接口
         * @return 所有的方法元
         */
        private void cascadeBuildIn(Set<MethodMeta> metas, Class<?> type,
                                    Map<TypeVariable<?>, Type> paramVariablesMappers) {

            // 只处理自己声明的方法
            buildIn(metas, type, paramVariablesMappers);

            // 父类
            Type[] extendsTypes = null;
            if (type.isInterface()) {
                extendsTypes = type.getGenericInterfaces();
            } else if (type.getGenericSuperclass() != null && type.getSuperclass() != Object.class) {
                extendsTypes = new Type[]{type.getGenericSuperclass()};
            }

            if (extendsTypes != null) {
                for (Type iType : extendsTypes) {

                    // 普通类型
                    if (iType instanceof Class) {
                        buildIn(metas, (Class<?>) iType, paramVariablesMappers);
                    }

                    // 泛型类型
                    else if (iType instanceof ParameterizedType
                            && ((ParameterizedType) iType).getRawType() instanceof Class) {

                        // 类型映射转换
                        ParameterizedType parameterizedType = (ParameterizedType) iType;
                        Map<TypeVariable<?>, Type> actualMappers = paramVariablesMappers(
                                parameterizedType.getActualTypeArguments(),
                                ((Class<?>) parameterizedType.getRawType()).getTypeParameters(), paramVariablesMappers);

                        // 处理父类型
                        cascadeBuildIn(metas, (Class<?>) parameterizedType.getRawType(), actualMappers);
                    }

                    // 其他不支持
                    else {
                        throw new BaseRuntimeException("Supper Class Type Not Support " + iType);
                    }
                }
            }
        }

        /**
         * 创建方法,保证不覆盖子类中的复写方法
         */
        private void buildIn(Set<MethodMeta> metas, Class<?> type, Map<TypeVariable<?>, Type> paramVariablesMappers) {

            // 当前类声明的类型
            Method[] methods = type.getDeclaredMethods();

            // 简单方法的缓存
            for (Method method : methods) {
                if (!method.isBridge() && (method.getModifiers() & Modifier.PUBLIC) > 0) {
                    MethodMeta meta = new MethodMeta(method, paramVariablesMappers);
                    metas.add(meta);
                }
            }
        }

        /**
         * 类型变量和实际类型映射
         */
        private Map<TypeVariable<?>, Type> paramVariablesMappers(Type[] actualTypes, TypeVariable<?>[] typeParameters,
                                                                 Map<TypeVariable<?>, Type> actualMappers) {

            // 类型变量和实际类型映射
            Map<TypeVariable<?>, Type> paramVariablesMappers = Maps.newHashMap();
            for (int i = 0; i < typeParameters.length; i++) {

                // 实际的类型
                Type actualType = actualTypes[i];

                // 如果实际类型直接是类类型
                if (actualType instanceof Class) {
                    actualType = actualTypes[i];
                }
                // 如果实际类型是 参数类型
                else if (actualType instanceof ParameterizedType || actualType instanceof TypeVariable) {
                    actualType = getActualParameterizedType(actualType, actualMappers);
                }
                // 不支持的格式
                else {
                    throw new BaseRuntimeException("Type Parameter Not Support " + actualType);
                }
                paramVariablesMappers.put(typeParameters[i], actualType);
            }
            return paramVariablesMappers;
        }

        // 封装之后的类型
        private Type getActualParameterizedType(Type type, Map<TypeVariable<?>, Type> actualMappers) {
            if (actualMappers == null) {
                return type;
            }
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] aTypes = parameterizedType.getActualTypeArguments();
                Type[] nestTypes = new Type[aTypes.length];
                for (int i = 0; i < aTypes.length; i++) {
                    Type aType = aTypes[i];
                    if (aType instanceof Class) {
                        nestTypes[i] = aType;
                    } else if (aType instanceof TypeVariable) {
                        nestTypes[i] = actualMappers.get(aType);
                    } else {
                        throw new BaseRuntimeException("Actual Type Arguments Not Support " + aType);
                    }
                }
                return new ParameterizedTypeHolder(nestTypes, parameterizedType.getOwnerType(),
                        parameterizedType.getRawType());
            } else if (type instanceof TypeVariable) {
                return actualMappers.get(type);
            }
            throw new BaseRuntimeException("not support");
        }

        /**
         * 返回缓存的方法元数据
         *
         * @return 集合
         */
        public Collection<MethodMeta> getMethods() {
            return namedIndex != null ? namedIndex.values() : methodIndex.values();
        }

        /**
         * 通过方法签名查找
         *
         * @param named 方法签名
         * @return MethodMeta
         */
        public MethodMeta lookup(String named) {
            this.initNamedIndex();
            if (!namedIndex.containsKey(named)) {
                throw new BaseRuntimeException("Please Set Method Public.");
            }
            return namedIndex.get(named);
        }

        /**
         * 通过方法查找
         *
         * @param method 方法
         * @return MethodMeta
         */
        public MethodMeta lookup(Method method) {
            this.initMethodIndex();
            if (!methodIndex.containsKey(method)) {
                throw new BaseRuntimeException("Please Set method Public.");
            }
            return methodIndex.get(method);
        }

        // 初始化索引
        private void initMethodIndex() {
            if (this.methodIndex == null) {
                Map<Method, MethodMeta> methodIndex = Maps.newHashMap();
                for (MethodMeta meta : this.namedIndex.values()) {
                    methodIndex.put(meta.getMethod(), meta);
                }
                this.methodIndex = methodIndex;
            }
        }

        // 初始化索引
        private void initNamedIndex() {
            if (this.namedIndex == null) {
                Map<String, MethodMeta> methodIndex = Maps.newHashMap();
                for (MethodMeta meta : this.methodIndex.values()) {
                    methodIndex.put(meta.getMethodDesc(), meta);
                }
                this.namedIndex = methodIndex;
            }
        }
    }

    /**
     * 这里的返回类型应该获取范型的类型
     *
     * @author lifeng
     */
    public static class MethodMeta {

        public static final String PARAM_CLASS_SPLIT = ",";
        public static final String EMPTY_PARAM = "void";

        private final Method method;
        private final String methodName;
        private final String methodDesc;
        private final Class<?> returnType;
        private final Class<?> nestedReturnType;
        private final Class<?>[] parameterTypes;
        private final Class<?>[] nestedParameterTypes;
        private final int timeOut;

        /**
         * 操作符： 定义类的一些特殊属性
         */
        protected byte operators = 0;

        /**
         * 无泛型的方法
         */
        public MethodMeta(Method method) {
            this(method, null);
        }

        /**
         * 泛型接口中的方法
         */
        public MethodMeta(Method method, Map<TypeVariable<?>, Type> paramVariablesMappers) {
            this.method = method;
            this.methodName = method.getName();
            this.returnType = this.getReturnType(paramVariablesMappers);
            this.parameterTypes = this.getParameterTypes(paramVariablesMappers);
            this.nestedReturnType = this.initNestedReturnType(paramVariablesMappers);
            this.nestedParameterTypes = this.initNestedParameterTypes(paramVariablesMappers);
            this.methodDesc = this.buildMethodDesc();
            TimeOut timeOut = method.getAnnotation(TimeOut.class);
            this.timeOut = timeOut != null ? timeOut.value() : -1;
            this.initOperators(method);
        }

        /**
         * 获得返回值 -- 直接类型
         *
         * @param paramVariablesMappers 泛型类型定义
         * @return 返回值
         */
        private Class<?> getReturnType(Map<TypeVariable<?>, Type> paramVariablesMappers) {
            Type type = method.getGenericReturnType();
            if (paramVariablesMappers != null && type instanceof TypeVariable) {
                return this.getActualType(type, paramVariablesMappers);
            }
            return method.getReturnType();
        }

        /**
         * 参数类型 -- 直接类型
         *
         * @param paramVariablesMappers 泛型类型定义
         * @return 类型类型
         */
        private Class<?>[] getParameterTypes(Map<TypeVariable<?>, Type> paramVariablesMappers) {
            Class<?>[] actualTypes = method.getParameterTypes();
            Type[] parameterTypes = method.getGenericParameterTypes();
            if (paramVariablesMappers != null && actualTypes.length > 0 && parameterTypes != null && parameterTypes.length > 0) {
                actualTypes = this.shallowCopy(actualTypes);
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (parameterTypes[i] instanceof TypeVariable) {
                        actualTypes[i] = this.getActualType(parameterTypes[i], paramVariablesMappers);
                    }
                }
            }
            return actualTypes;
        }

        // 浅拷贝
        private Class<?>[] shallowCopy(Class<?>[] source) {
            Class<?>[] actualTypes = new Class<?>[source.length];
            System.arraycopy(source, 0, actualTypes, 0, source.length);
            return actualTypes;
        }

        /**
         * 内部实际的类型 -- 不需要支持多级泛型
         */
        private Class<?> initNestedReturnType(Map<TypeVariable<?>, Type> paramVariablesMappers) {
            Type type = method.getGenericReturnType();
            if (type instanceof Class) {
                return returnType;
            } else if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType) type;
                Type actualType = pType.getActualTypeArguments()[0];
                if (actualType instanceof Class) {
                    return (Class<?>) actualType;
                } else if (paramVariablesMappers != null && actualType instanceof TypeVariable) {
                    return this.getNestActualType(actualType, paramVariablesMappers);
                }
            } else if (paramVariablesMappers != null && type instanceof TypeVariable) {
                return this.getNestActualType(type, paramVariablesMappers);
            }
            if (logger.isDebugEnabled()) {
                logger.warn("Method【{}】  Might Miss Real Return Type.", this.getMethodDesc());
            }
            return Object.class;
        }

        /**
         * 内部实际的类型 -- 不需要支持多级泛型
         */
        private Class<?>[] initNestedParameterTypes(Map<TypeVariable<?>, Type> paramVariablesMappers) {
            Class<?>[] actualTypes = this.parameterTypes;
            Type[] parameterTypes = method.getGenericParameterTypes();
            if (actualTypes != null && actualTypes.length > 0) {
                actualTypes = this.shallowCopy(actualTypes);
                for (int i = 0; i < parameterTypes.length; i++) {
                    if (parameterTypes[i] instanceof ParameterizedType) {
                        ParameterizedType pType = (ParameterizedType) parameterTypes[i];
                        Type actualType = pType.getActualTypeArguments()[0];
                        if (actualType instanceof Class) {
                            actualTypes[i] = (Class<?>) actualType;
                        } else if (paramVariablesMappers != null && actualType instanceof TypeVariable) {
                            actualTypes[i] = this.getNestActualType(actualType,
                                    paramVariablesMappers);
                        } else {
                            if (logger.isDebugEnabled()) {
                                logger.warn("Method【{}】  Might Miss Real Nested Param Type.", this.getMethodDesc());
                            }
                            actualTypes[i] = Object.class;
                        }
                    } else if (parameterTypes[i] instanceof TypeVariable) {
                        assert paramVariablesMappers != null;
                        actualTypes[i] = this.getNestActualType(parameterTypes[i],
                                paramVariablesMappers);
                    }
                }
            }
            return actualTypes;
        }

        private Class<?> getActualType(Type type, Map<TypeVariable<?>, Type> paramVariablesMappers) {
            Type actualType = paramVariablesMappers.get(type);
            if (actualType instanceof Class) {
                return (Class<?>) actualType;
            } else if (actualType instanceof ParameterizedType) {
                Type ptype = ((ParameterizedType) actualType).getRawType();
                if (ptype instanceof Class) {
                    return (Class<?>) ptype;
                }
            }
            return Object.class;
        }

        private Class<?> getNestActualType(Type type, Map<TypeVariable<?>, Type> paramVariablesMappers) {
            Type actualType = paramVariablesMappers.get(type);
            if (actualType instanceof Class) {
                return (Class<?>) actualType;
            } else if (actualType instanceof ParameterizedType) {
                Type ptype = ((ParameterizedType) actualType).getActualTypeArguments()[0];
                if (ptype instanceof Class) {
                    return (Class<?>) ptype;
                } else if (ptype instanceof TypeVariable) {
                    return this.getActualType(ptype, paramVariablesMappers);
                }
            }
            return Object.class;
        }

        private void initOperators(Method method) {
            if (method.getDeclaringClass().equals(Object.class)) {
                operators |= Constants.OPERATORS_LOCAL;
            }
            if (Future.class.isAssignableFrom(method.getReturnType())) {
                operators |= Constants.OPERATORS_ASYNC;
            }
        }

        public Method getMethod() {
            return method;
        }

        public String getMethodName() {
            return methodName;
        }

        public String getMethodDesc() {
            return methodDesc;
        }

        public Class<?> getReturnType() {
            return returnType;
        }

        public Class<?>[] getParameterTypes() {
            return parameterTypes;
        }

        public int getTimeOut() {
            return timeOut;
        }

        public Class<?> getNestedReturnType() {
            return nestedReturnType;
        }

        public Class<?>[] getNestedParameterTypes() {
            return nestedParameterTypes;
        }

        public boolean isAsync() {
            return (this.operators & Constants.OPERATORS_ASYNC) == Constants.OPERATORS_ASYNC;
        }

        public boolean isLocal() {
            return (this.operators & Constants.OPERATORS_LOCAL) == Constants.OPERATORS_LOCAL;
        }

        private String buildMethodDesc() {
            String methodParamDesc = EMPTY_PARAM;
            if (!(this.parameterTypes == null || this.parameterTypes.length == 0)) {
                StringBuilder builder = new StringBuilder();
                for (Class<?> clz : this.parameterTypes) {
                    String className = buildClassName(clz);
                    builder.append(className).append(PARAM_CLASS_SPLIT);
                }
                methodParamDesc = builder.substring(0, builder.length() - 1);
            }
            return methodName + "(" + methodParamDesc + ")";
        }

        private String buildClassName(Class<?> c) {
            if (c.isArray()) {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append("[]");
                    c = c.getComponentType();
                } while (c.isArray());

                return c.getName() + sb.toString();
            }
            return c.getName();
        }

        @Override
        public int hashCode() {
            return this.methodDesc.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof MethodMeta) {
                MethodMeta ident = (MethodMeta) o;
                return this.methodDesc.equals(ident.methodDesc);
            }
            return false;
        }

        @Override
        public String toString() {
            return this.methodDesc;
        }
    }

    /**
     * 封装的ParameterizedType 处理父父类中的泛型参数
     *
     * @author lifeng
     * @date 2020年4月22日 上午11:21:26
     */
    static class ParameterizedTypeHolder implements ParameterizedType {
        private final Type[] actualTypeArguments;
        private final Type ownerType;
        private final Type rawType;

        public ParameterizedTypeHolder(Type[] actualTypeArguments, Type ownerType, Type rawType) {
            this.actualTypeArguments = actualTypeArguments;
            this.ownerType = ownerType;
            this.rawType = rawType;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ParameterizedTypeHolder that = (ParameterizedTypeHolder) o;

            // Probably incorrect - comparing Object[] arrays with Arrays.equals
            if (!Arrays.equals(actualTypeArguments, that.actualTypeArguments)) {
                return false;
            }
            if (!Objects.equals(ownerType, that.ownerType)) {
                return false;
            }
            return Objects.equals(rawType, that.rawType);
        }

        @Override
        public int hashCode() {
            int result = actualTypeArguments != null ? Arrays.hashCode(actualTypeArguments) : 0;
            result = 31 * result + (ownerType != null ? ownerType.hashCode() : 0);
            result = 31 * result + (rawType != null ? rawType.hashCode() : 0);
            return result;
        }
    }
}