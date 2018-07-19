package com.swak.reactivex.web.method.resolver;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.utils.StringUtils;

/**
 * 托底解析 非原始类型的类型数据 解析--对象
 * 
 * @author lifeng
 */
public class ServerModelMethodArgumentResolver extends AbstractMethodArgumentResolver {

	public ServerModelMethodArgumentResolver(ConversionService conversionService) {
		super(conversionService);
	}

	/**
	 * 只要不是基础类型
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return !BeanUtils.isSimpleProperty(parameter.getParameterType());
	}

	/**
	 * 只做第一层解析 调用 set 方法来初始化
	 */
	@Override
	protected Object resolveArgumentInternal(MethodParameter parameter, HttpServerRequest webRequest) {
		Class<?> paramtype = parameter.getParameterType();
		Object model = null;
		try {
			model = paramtype.newInstance();
			if (!webRequest.getParameterMap().isEmpty()) {
				Method[] methods = paramtype.getMethods();
				for (Method method : methods) {
					if (method.isAccessible() && StringUtils.startsWith(method.getName(), "set")
							&& method.getParameterCount() == 1) {
						Object value = this.getParamValue(webRequest, method);
						method.invoke(model, value);
					}
				}
			}
		} catch (Exception e) {}
		return model;
	}

	private Object getParamValue(HttpServerRequest request, Method setMethod) {
		String name = StringUtils.substring(setMethod.getName(), 3).toLowerCase();
		List<String> paramValues = request.getParameterValues(name);
		Object value = null;
		if (paramValues != null) {
			value = (paramValues.size() == 1 ? paramValues.get(0) : paramValues);
		}
		value = value == null ? StringUtils.EMPTY : value;
		return this.doConvert(value, setMethod.getParameterTypes()[0]);
	}
}
