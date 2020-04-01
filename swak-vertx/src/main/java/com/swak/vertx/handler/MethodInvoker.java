package com.swak.vertx.handler;

import com.swak.annotation.Logical;
import com.swak.annotation.RequiresPermissions;
import com.swak.annotation.RequiresRoles;
import com.swak.meters.MethodMetrics;
import com.swak.meters.MetricsFactory;
import com.swak.security.Permission;
import com.swak.security.permission.AndPermission;
import com.swak.security.permission.OrPermission;
import com.swak.security.permission.SinglePermission;
import com.swak.utils.ReflectUtils;
import com.swak.utils.StringUtils;
import com.swak.utils.router.RouterUtils;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 基于 method 的执行器
 *
 * @author: lifeng
 * @date: 2020/3/29 20:14
 */
@SuppressWarnings("rawtypes")
public class MethodInvoker {

    /**
     * 操作符
     */
    final static byte OPERATORS_SYNC = 1;

    private final Object bean;
    private final Class<?> beanType;
    private final String name;
    private final Method method;
    private final MethodParameter[] parameters;
    private volatile Annotation[] annotations;
    protected MethodMetrics metrics;
    protected Permission requiresRoles;
    protected Permission requiresPermissions;
    protected byte operators = 0;

    /**
     * Create an instance from a bean instance and a method.
     */
    public MethodInvoker(Object bean, Method method) {
        this.bean = bean;
        this.beanType = ClassUtils.getUserClass(bean);
        this.method = method;
        this.name = this.beanType.getName() + "." +
                ReflectUtils.getMethodDesc(this.method);
        this.parameters = initMethodParameters();
        this.initOperators();
    }

    private MethodParameter[] initMethodParameters() {
        int count = this.method.getParameterTypes().length;
        MethodParameter[] result = new MethodParameter[count];
        for (int i = 0; i < count; i++) {
            result[i] = new MethodParameter(this.beanType, this.method, i);
        }
        return result;
    }

    private void initOperators() {

    }

    public Object getBean() {
        return bean;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getBeanType() {
        return beanType;
    }

    public MethodParameter[] getParameters() {
        return parameters;
    }

    public Annotation[] getAnnotations() {
        if (this.annotations == null) {
            this.annotations = this.method.getAnnotations();
        }
        return this.annotations;
    }

    public <A extends Annotation> Annotation getAnnotation(Class<A> annotationType) {
        Annotation[] paramAnns = this.getAnnotations();
        if (paramAnns != null) {
            for (Annotation ann : paramAnns) {
                if (annotationType.isInstance(ann)) {
                    return ann;
                }
            }
            return null;
        }
        return null;
    }

    public <A extends Annotation> boolean hasAnnotation(Class<A> annotationType) {
        Annotation[] paramAnns = this.getAnnotations();
        if (paramAnns != null) {
            for (Annotation ann : paramAnns) {
                if (annotationType.isInstance(ann)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 角色
     */
    public Permission getRequiresRoles() {
        if (this.requiresRoles == null) {
            RequiresRoles roles = (RequiresRoles) this.getAnnotation(RequiresRoles.class);
            if (roles == null || roles.value().length == 0) {
                this.requiresRoles = Permission.NONE;
            } else if (roles.value().length == 1) {
                this.requiresRoles = new SinglePermission(roles.value()[0]);
            } else if (roles.logical() == Logical.AND) {
                this.requiresRoles = new AndPermission(roles.value());
            } else if (roles.logical() == Logical.OR) {
                this.requiresRoles = new OrPermission(roles.value());
            }
        }
        return this.requiresRoles;
    }

    /**
     * 权限
     */
    public Permission getRequiresPermissions() {
        if (this.requiresPermissions == null) {
            RequiresPermissions roles = (RequiresPermissions) this.getAnnotation(RequiresPermissions.class);
            if (roles == null || roles.value().length == 0) {
                this.requiresPermissions = Permission.NONE;
            } else if (roles.value().length == 1) {
                this.requiresPermissions = new SinglePermission(roles.value()[0]);
            } else if (roles.logical() == Logical.AND) {
                this.requiresPermissions = new AndPermission(roles.value());
            } else if (roles.logical() == Logical.OR) {
                this.requiresPermissions = new OrPermission(roles.value());
            }
        }
        return this.requiresPermissions;
    }

    /**
     * 设置监控
     *
     * @param metricsFactory 指标框架
	 */
    public void applyMetrics(MetricsFactory metricsFactory) {
        if (metricsFactory != null) {
            metrics = metricsFactory.createMethodMetrics(this.method, name);
        }
	}

    /**
     * 调用
     *
     * @param args 参数
     * @return 直接结果
     */
    public Object doInvoke(Object[] args) throws Throwable {
        return this.getMethod().invoke(this.getBean(), args);
    }

    /**
     * 参考 org.springframework.core.MethodParameter 不需要所有的情况都支持
     *
     * @author lifeng
     */
    public static class MethodParameter {

        private Class<?> clazz;
        private Method method;
        private int parameterIndex;
        private volatile String parameterName;
        private volatile Class<?> parameterType;
        private volatile Type genericParameterType;
        private volatile Class<?> nestedParameterType;
        private volatile Type nestedGenericParameterType;
        private volatile Annotation[] parameterAnnotations;

        public MethodParameter(Class<?> clazz, Method method, int parameterIndex) {
            this.clazz = clazz;
            this.method = method;
            this.parameterIndex = parameterIndex;
            String[] parameterNames = RouterUtils.getParameterNames(method);
            if (parameterNames != null) {
                this.parameterName = parameterNames[parameterIndex];
            }
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public int getParameterIndex() {
            return parameterIndex;
        }

        public void setParameterIndex(int parameterIndex) {
            this.parameterIndex = parameterIndex;
        }

        public String getParameterName() {
            if (parameterName == null) {
                String[] parameterNames = RouterUtils.getParameterNames(method);
                if (parameterNames != null) {
                    this.parameterName = parameterNames[parameterIndex];
                } else {
                    this.parameterName = StringUtils.EMPTY;
                }
            }
            return parameterName;
        }

        public void setParameterName(String parameterName) {
            this.parameterName = parameterName;
        }

        public Class<?> getParameterType() {
            if (parameterType == null) {
                if (this.parameterIndex < 0) {
                    Method method = getMethod();
                    parameterType = (method != null ? method.getReturnType() : void.class);
                } else {
                    parameterType = this.method.getParameterTypes()[this.parameterIndex];
                }
            }
            return parameterType;
        }

        public Annotation[] getParameterAnnotations() {
            Annotation[] paramAnns = this.parameterAnnotations;
            if (paramAnns == null) {
                Annotation[][] annotationArray = this.method.getParameterAnnotations();
                int index = this.parameterIndex;
                paramAnns = (index >= 0 && index < annotationArray.length ? annotationArray[index] : null);
                this.parameterAnnotations = paramAnns;
            }
            return paramAnns;
        }

        public <A extends Annotation> Annotation getParameterAnnotation(Class<A> annotationType) {
            Annotation[] paramAnns = this.getParameterAnnotations();
            if (paramAnns != null) {
                for (Annotation ann : paramAnns) {
                    if (annotationType.isInstance(ann)) {
                        return ann;
                    }
                }
                return null;
            }
            return null;
        }

        public <A extends Annotation> boolean hasParameterAnnotation(Class<A> annotationType) {
            Annotation[] paramAnns = this.getParameterAnnotations();
            if (paramAnns != null) {
                for (Annotation ann : paramAnns) {
                    if (annotationType.isInstance(ann)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        public void setParameterType(Class<?> parameterType) {
            this.parameterType = parameterType;
        }

        public Type getGenericParameterType() {
            if (this.genericParameterType == null) {
                if (this.parameterIndex < 0) {
                    Method method = getMethod();
                    genericParameterType = (method != null ? method.getGenericReturnType() : void.class);
                } else {
                    Type[] genericParameterTypes = this.method.getGenericParameterTypes();
                    int index = this.parameterIndex;
                    genericParameterType = (index >= 0 && index < genericParameterTypes.length
                            ? genericParameterTypes[index]
                            : getParameterType());
                }
            }
            return genericParameterType;
        }

        public void setGenericParameterType(Type genericParameterType) {
            this.genericParameterType = genericParameterType;
        }

        public Class<?> getNestedParameterType() {
            if (nestedParameterType == null) {
                this.initNestedParameter();
            }
            return nestedParameterType;
        }

        public void setNestedParameterType(Class<?> nestedParameterType) {
            this.nestedParameterType = nestedParameterType;
        }

        public Type getNestedGenericParameterType() {
            if (nestedGenericParameterType == null) {
                this.initNestedParameter();
            }
            return nestedGenericParameterType;
        }

        public void setNestedGenericParameterType(Type nestedGenericParameterType) {
            this.nestedGenericParameterType = nestedGenericParameterType;
        }

        private void initNestedParameter() {
            Type fieldType = this.getGenericParameterType();
            Class<?> fieldClass = this.getParameterType();
            if (fieldType instanceof ParameterizedType) {
                Type[] args = ((ParameterizedType) fieldType).getActualTypeArguments();
                fieldType = args[0];
            }
            if (fieldType instanceof Class) {
                fieldClass = (Class<?>) fieldType;
            } else if (fieldType instanceof ParameterizedType) {
                Type arg = ((ParameterizedType) fieldType).getRawType();
                if (arg instanceof Class) {
                    fieldClass = (Class<?>) arg;
                }
            } else {
                fieldClass = Object.class;
            }
            this.nestedGenericParameterType = fieldType;
            this.nestedParameterType = fieldClass;
        }
    }
}