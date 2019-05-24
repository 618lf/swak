package com.swak.fields;

import com.swak.asm.FieldCache;
import com.swak.asm.FieldCache.ClassMeta;
import com.swak.asm.FieldCache.FieldMeta;

public class FieldTest {

	public static void main(String[] args) {
		FieldCache.set(Order.class);
		ClassMeta classMeta = FieldCache.get(Order.class);
		FieldMeta fieldMeta = classMeta.getFields().get("id");
		System.out.println(fieldMeta.getPropertyName());
		System.out.println(fieldMeta.getFieldClass());
		System.out.println(fieldMeta.getNestedFieldClass());
	}

}
