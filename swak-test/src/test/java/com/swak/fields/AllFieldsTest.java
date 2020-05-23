package com.swak.fields;

import java.lang.reflect.Field;
import java.util.Map;

import com.swak.utils.ReflectUtils;

public class AllFieldsTest {

	public static void main(String[] args) {
		Map<String, Field> fields = ReflectUtils.getBeanPropertyFields(OrderItem.class);
		for (Field field : fields.values()) {
			System.out.println(field.getName());
		}
	}
}
