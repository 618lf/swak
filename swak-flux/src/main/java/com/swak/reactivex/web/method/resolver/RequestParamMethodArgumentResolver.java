package com.swak.reactivex.web.method.resolver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import com.swak.reactivex.transport.http.server.HttpServerRequest;

/**
 * 处理基本的参数 支持最基本的类型和Map类型
 * 
 * @author lifeng
 */
public class RequestParamMethodArgumentResolver extends AbstractMethodArgumentResolver {

	public RequestParamMethodArgumentResolver(ConversionService conversionService) {
		super(conversionService);
	}

	/**
	 * 支持最基本的类型和Map类型
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return Map.class.isAssignableFrom(parameter.nestedIfOptional().getNestedParameterType())
				|| BeanUtils.isSimpleProperty(parameter.getNestedParameterType());
	}

	/**
	 * 返回对应的对象
	 */
	@Override
	public Object resolveArgumentInternal(MethodParameter parameter, HttpServerRequest request){
		if (Map.class.isAssignableFrom(parameter.getParameterType())) {
			Map<String, List<String>> parameterMap = request.getParameterMap();
			Map<String, String> result = new LinkedHashMap<String, String>(parameterMap.size());
			for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
				if (entry.getValue().size() > 0) {
					result.put(entry.getKey(), entry.getValue().get(0));
				}
			}
			return result;
		} else {
			MethodParameter nestedParameter = parameter.nestedIfOptional();
			String resolvedName = nestedParameter.getParameterName();
			List<String> paramValues = request.getParameterValues(resolvedName);
			Object arg = null;
			if (paramValues != null) {
				arg = (paramValues.size() == 1 ? paramValues.get(0) : paramValues);
			}
			return arg == null ? new String() : arg;
		}
	}
}