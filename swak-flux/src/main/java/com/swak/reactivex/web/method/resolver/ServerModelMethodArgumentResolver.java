package com.swak.reactivex.web.method.resolver;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;

import com.swak.asm.FieldCache;
import com.swak.asm.FieldCache.ClassMeta;
import com.swak.asm.FieldCache.FieldMeta;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.web.method.MethodParameter;
import com.swak.utils.StringUtils;

/**
 * 托底解析 非原始类型的类型数据 解析--对象
 * 
 * @author lifeng
 */
public class ServerModelMethodArgumentResolver extends AbstractMethodArgumentResolver {

	private Pattern OBJECT_PARAM_PATTERN = Pattern.compile("\\w+[\\.\\[]{1}(\\w+)[\\]]?");

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
	protected Object resolveArgumentInternal(MethodParameter parameter, HttpServerRequest request) {
		ClassMeta classMeta = FieldCache.get(parameter.getParameterType());
		if (classMeta == null) {
			return null;
		}
		try {
			Map<String, String> arguments = this.getArguments(request);
			Object obj = parameter.getParameterType().newInstance();
			if (!arguments.isEmpty()) {
				classMeta = FieldCache.get(parameter.getParameterType());
				this.fillObjectValue(obj, classMeta.getFields(), parameter.getParameterName(), arguments);
			}
			return obj;
		} catch (Exception e) {
		}
		return null;
	}

	// 循环 arguments 更好一点
	private void fillObjectValue(Object obj, Map<String, FieldMeta> fields, String paramName,
			Map<String, String> arguments) throws IllegalArgumentException, IllegalAccessException {
		Iterator<String> it = arguments.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object value = arguments.get(key);

			// 值不能为空
			if (value == null || StringUtils.NULL.equals(value)) {
				continue;
			}

			// 是否可以获取配置
			FieldMeta field = fields.get(key);
			Matcher found = null;

			// name[n]=xxx 的型式，如果 name 和 参数名称一致也填充进去
			// name.n=xxx 的型式，如果 name 和 参数名称一致也填充进去
			if (field == null && StringUtils.startsWith(key, paramName)
					&& (found = OBJECT_PARAM_PATTERN.matcher(key)).find()) {
				key = found.group(1);
				field = fields.get(key);
			}

			// 设置值
			try {
				if (field != null) {
					field.getField().set(obj, this.doConvert(value, field.getFieldClass()));
				}
			} catch (Exception e) {
				logger.error("Set obj field faile:field[{}]-value[{}]", field.getPropertyName(), value);
			}
		}
	}
}
