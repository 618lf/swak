/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.swak.config.jdbc.sharding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.core.env.Environment;

public final class PropertyUtil {

	/**
	 * Spring Boot 2.x
	 */
	@SuppressWarnings("unchecked")
	public static <T> T handle(final Environment environment, final String prefix, final Class<T> targetClass)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> binderClass = Class.forName("org.springframework.boot.context.properties.bind.Binder");
		Method getMethod = binderClass.getDeclaredMethod("get", Environment.class);
		Method bindMethod = binderClass.getDeclaredMethod("bind", String.class, Class.class);
		Object binderObject = getMethod.invoke(null, environment);
		String prefixParam = prefix.endsWith(".") ? prefix.substring(0, prefix.length() - 1) : prefix;
		Object bindResultObject = bindMethod.invoke(binderObject, prefixParam, targetClass);
		Method resultGetMethod = bindResultObject.getClass().getDeclaredMethod("get");
		return (T) resultGetMethod.invoke(bindResultObject);
	}
}
