package com.swak.validator;

import org.junit.Before;
import org.junit.Test;

import com.swak.asm.FieldCache;
import com.swak.asm.FieldCache.ClassMeta;

/**
 * 校验测试
 * 
 * @author lifeng
 */
public class ValidatorTest {

	Validator validator;
	OrderParam order;

	@Before
	public void init() {
		FieldCache.set(OrderParam.class);
		validator = new SmartValidator();
		order = OrderParam.of().setName("1111").setEmail("618lf1111.com")
				.setPhone("12323323").setPrice(1).setAddress("中国");
	}

	@Test
	public void test() {
		ClassMeta classMeta = FieldCache.get(OrderParam.class);
		classMeta.getFields().values().forEach(field -> {
			try {
				Object value = field.getField().get(order);
				String result = validator.validate(field, value);
				System.out.println("rule:" + field.getAnnotations());
				System.out.println("result:" + result);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}
}
