package com.swak.param;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;

import com.swak.asm.FieldCache;
import com.swak.test.utils.MultiThreadTest;
import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;
import com.swak.vertx.protocol.http.RouterHandlerAdapter;

/**
 * 参数解析
 * 
 * @author lifeng
 */
public class ParamParse {

	private RouterHandlerAdapter handler;

	public ParamParse()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		handler = new RouterHandlerAdapter();
		Field field = handler.getClass().getDeclaredField("conversionService");
		FormattingConversionService service = new DefaultFormattingConversionService();
		field.setAccessible(true);
		field.set(handler, service);

		FieldCache.set(Param.class);
	}

	@Test
	public void test() {
		Map<String, Object> values = this.getValues();
		MultiThreadTest.run(() -> {
			for (int i = 0; i <= 10000; i++) {
				this.getValues();
				handler.resolveObject(Param.class, "param", values, null, false);
			}
		}, 100, "parse params");
	}

	@Test
	public void test2() {
		Map<String, Object> values = this.getValues();
		String json = JsonMapper.toJson(values);
		MultiThreadTest.run(() -> {
			for (int i = 0; i <= 10000; i++) {
				JsonMapper.fromJson(json, HashMap.class);
			}
		}, 100, "parse json");
	}

	private Map<String, Object> getValues() {
		Map<String, String> arguments = Maps.newHashMap();
		arguments.put("param[name]", "lifeng");
		arguments.put("param[items][0][name]", "lifeng0");
		arguments.put("param[items][1][name]", "lifeng1");
		arguments.put("param[items][2][name]", "lifeng2");
		arguments.put("param[items][3][name]", "lifeng3");
		arguments.put("param[items][4][name]", "lifeng4");
		arguments.put("param[items][5][name]", "lifeng5");
		return handler.parseArguments(arguments.entrySet().iterator());
	}
}
