package com.swak.vertx.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.swak.utils.Lists;
import com.swak.utils.ReflectUtils;

/**
 * field cache
 * 
 * @author lifeng
 */
public class FieldCache {

	static Map<Class<?>, ClassMeta> CACHES = new ConcurrentHashMap<>();

	/**
	 * 得到field
	 * 
	 * @param method
	 */
	public static ClassMeta set(Class<?> type) {
		CACHES.putIfAbsent(type, new ClassMeta(type));
		return CACHES.get(type);
	}

	/**
	 * 获取
	 * 
	 * @param type
	 * @return
	 */
	public static ClassMeta get(Class<?> type) {
		return CACHES.get(type);
	}

	/**
	 * 类型元数据
	 * 
	 * @author lifeng
	 */
	public static class ClassMeta {
		
		private final List<FieldMeta> fields;

		public ClassMeta(Class<?> type) {
			
			// 所有的字段
			fields = Lists.newArrayList();

			// 声明的字段
			Field[] declaredFields = type.getDeclaredFields();
			
			// 所有的方法，包括父类的
			Method[] methods = type.getMethods();
			for (Method method : methods) {
				
				String methodName = method.getName();
				if (!methodName.startsWith("set") || Modifier.isStatic(method.getModifiers())) {
	                continue;
	            }
				
				// support builder set
	            Class<?> returnType = method.getReturnType();
	            if (!(returnType.equals(Void.TYPE) || returnType.equals(method.getDeclaringClass()))) {
	                continue;
	            }
	            
	            // exclude object method
	            if (method.getDeclaringClass() == Object.class) {
	                continue;
	            }
	            
	            // only set
	            Class<?>[] types = method.getParameterTypes();
	            if (types.length != 1) {
	                continue;
	            }
	            
	            char c3 = methodName.charAt(3);
	            String propertyName;
	            if (Character.isUpperCase(c3) //
	                    || c3 > 512 // for unicode method name
	                    ) {
	            	propertyName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
	            } else if (c3 == '_') {
	                propertyName = methodName.substring(4);
	            } else if (c3 == 'f') {
	                propertyName = methodName.substring(3);
	            } else if (methodName.length() >= 5 && Character.isUpperCase(methodName.charAt(4))) {
	                propertyName = ReflectUtils.propertyName(methodName.substring(3));
	            } else {
	                continue;
	            }
	            
	            // 获得字段，考虑到如果是 boolean字段时的情形
	            Field field = ReflectUtils.getField(type, propertyName, declaredFields);
	            if (field == null && types[0] == boolean.class) {
	                String isFieldName = "is" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
	                field = ReflectUtils.getField(type, isFieldName, declaredFields);
	            }
	            
	            // 设置可以访问
	            method.setAccessible(true);
	            field.setAccessible(true);
	            fields.add(new FieldMeta(type, propertyName, method, field));
			}
		}
		public List<FieldMeta> getFields() {
			return fields;
		}
	}

	/**
	 * 字段元数据
	 * 
	 * @author lifeng
	 */
	public static class FieldMeta {
		private final String propertyName;
		private final Method method;
		private final Field field;
		private Class<?> fieldClass;
		private Type fieldType;

		public FieldMeta(Class<?> clazz, String propertyName, Method method, Field field) {
			this.propertyName = propertyName;
			this.method = method;
			this.field = field;

			// 只有一个参数
			Type fieldType = method.getGenericParameterTypes()[0];
			Class<?> fieldClass = method.getParameterTypes()[0];

			if (clazz != null && fieldClass == Object.class && fieldType instanceof TypeVariable) {
				TypeVariable<?> tv = (TypeVariable<?>) fieldType;
				Type genericFieldType = getInheritGenericType(clazz, clazz, tv);
				if (genericFieldType != null) {
					this.fieldClass = ReflectUtils.getClass(genericFieldType);
					this.fieldType = genericFieldType;
					return;
				}
			}
			
			Type genericFieldType = fieldType;
	        if (!(fieldType instanceof Class)) {
	            genericFieldType = getFieldType(clazz, clazz, fieldType);
	    
	            if (genericFieldType != fieldType) {
	                if (genericFieldType instanceof ParameterizedType) {
	                    fieldClass = ReflectUtils.getClass(genericFieldType);
	                } else if (genericFieldType instanceof Class) {
	                    fieldClass = ReflectUtils.getClass(genericFieldType);
	                }
	            }
	        }
	        
	        this.fieldType = genericFieldType;
	        this.fieldClass = fieldClass;
		}
		
		 public static Type getFieldType(final Class<?> clazz, final Type type, Type fieldType) {
		        if (clazz == null || type == null) {
		            return fieldType;
		        }

		        if (fieldType instanceof GenericArrayType) {
		            GenericArrayType genericArrayType = (GenericArrayType) fieldType;
		            Type componentType = genericArrayType.getGenericComponentType();
		            Type componentTypeX = getFieldType(clazz, type, componentType);
		            if (componentType != componentTypeX) {
		                Type fieldTypeX = Array.newInstance(ReflectUtils.getClass(componentTypeX), 0).getClass();
		                return fieldTypeX;
		            }

		            return fieldType;
		        }

		        if (!ReflectUtils.isGenericParamType(type)) {
		            return fieldType;
		        }

		        if (fieldType instanceof TypeVariable) {
		            ParameterizedType paramType = (ParameterizedType) ReflectUtils.getGenericParamType(type);
		            Class<?> parameterizedClass = ReflectUtils.getClass(paramType);
		            final TypeVariable<?> typeVar = (TypeVariable<?>) fieldType;
		            
		            TypeVariable<?>[] typeVariables = parameterizedClass.getTypeParameters();
		            for (int i = 0; i < typeVariables.length; ++i) {
		                if (typeVariables[i].getName().equals(typeVar.getName())) {
		                    fieldType = paramType.getActualTypeArguments()[i];
		                    return fieldType;
		                }
		            }
		        }

		        if (fieldType instanceof ParameterizedType) {
		            ParameterizedType parameterizedFieldType = (ParameterizedType) fieldType;

		            Type[] arguments = parameterizedFieldType.getActualTypeArguments();
		            TypeVariable<?>[] typeVariables;
		            ParameterizedType paramType;
		            if (type instanceof ParameterizedType) {
		                paramType = (ParameterizedType) type;
		                typeVariables = clazz.getTypeParameters();
		            } else if(clazz.getGenericSuperclass() instanceof ParameterizedType) {
		                paramType = (ParameterizedType) clazz.getGenericSuperclass();
		                typeVariables = clazz.getSuperclass().getTypeParameters();
		            } else {
		                paramType = parameterizedFieldType;
		                typeVariables = type.getClass().getTypeParameters();
		            }

		            boolean changed = getArgument(arguments, typeVariables, paramType.getActualTypeArguments());
		            if (changed) {
		                fieldType = new ParameterizedTypeImpl(arguments, parameterizedFieldType.getOwnerType(),
		                                                      parameterizedFieldType.getRawType());
		                return fieldType;
		            }
		        }

		        return fieldType;
		    }

		private static Type getInheritGenericType(Class<?> clazz, Type type, TypeVariable<?> tv) {
			GenericDeclaration gd = tv.getGenericDeclaration();

			Class<?> class_gd = null;
			if (gd instanceof Class) {
				class_gd = (Class<?>) tv.getGenericDeclaration();
			}

			Type[] arguments = null;
			if (class_gd == clazz) {
				if (type instanceof ParameterizedType) {
					ParameterizedType ptype = (ParameterizedType) type;
					arguments = ptype.getActualTypeArguments();
				}
			} else {
				for (Class<?> c = clazz; c != null && c != Object.class && c != class_gd; c = c.getSuperclass()) {
					Type superType = c.getGenericSuperclass();

					if (superType instanceof ParameterizedType) {
						ParameterizedType p_superType = (ParameterizedType) superType;
						Type[] p_superType_args = p_superType.getActualTypeArguments();
						getArgument(p_superType_args, c.getTypeParameters(), arguments);
						arguments = p_superType_args;
					}
				}
			}

			if (arguments == null || class_gd == null) {
				return null;
			}

			Type actualType = null;
			TypeVariable<?>[] typeVariables = class_gd.getTypeParameters();
			for (int j = 0; j < typeVariables.length; ++j) {
				if (tv.equals(typeVariables[j])) {
					actualType = arguments[j];
					break;
				}
			}

			return actualType;
		}

		private static boolean getArgument(Type[] typeArgs, TypeVariable<?>[] typeVariables, Type[] arguments) {
			if (arguments == null || typeVariables.length == 0) {
				return false;
			}

			boolean changed = false;
			for (int i = 0; i < typeArgs.length; ++i) {
				Type typeArg = typeArgs[i];
				if (typeArg instanceof ParameterizedType) {
					ParameterizedType p_typeArg = (ParameterizedType) typeArg;
					Type[] p_typeArg_args = p_typeArg.getActualTypeArguments();
					boolean p_changed = getArgument(p_typeArg_args, typeVariables, arguments);
					if (p_changed) {
						typeArgs[i] = new ParameterizedTypeImpl(p_typeArg_args, p_typeArg.getOwnerType(),
								p_typeArg.getRawType());
						changed = true;
					}
				} else if (typeArg instanceof TypeVariable) {
					for (int j = 0; j < typeVariables.length; ++j) {
						if (typeArg.equals(typeVariables[j])) {
							typeArgs[i] = arguments[j];
							changed = true;
						}
					}
				}
			}

			return changed;
		}

		public String getPropertyName() {
			return propertyName;
		}

		public Method getMethod() {
			return method;
		}

		public Field getField() {
			return field;
		}

		public Class<?> getFieldClass() {
			return fieldClass;
		}

		public Type getFieldType() {
			return fieldType;
		}
	}
}