package com.tmt.serializer;

import org.junit.Test;

import com.swak.common.serializer.KryoPoolSerializer;
import com.swak.common.serializer.Serializer;

import redis.clients.util.SafeEncoder;

/**
 * 测试系列化
 * String 等原生类型的转 byte 会比 其他序列化方式快
 * 最重要的是其他组件可以通用
 * @author lifeng
 */
public class TestSerializer {

	@Test
	public void testString() {
		String str = "明 胡应麟 《少室山房笔丛·四部正讹下》：“《穆天子传》六卷，其文典则淳古";
		long t1 = System.currentTimeMillis();
		System.out.println("begin=" + t1);
		for (int i = 0; i < 1000000; i++) {
			byte[] b = SafeEncoder.encode(str + i);
			SafeEncoder.encode(b);
		}
		System.out.println("norma ,use=" + (System.currentTimeMillis() - t1));
	}
	
	@Test
	public void testSObject() {
		Serializer g_ser = new KryoPoolSerializer();
		String str = "1";
		byte[] b = g_ser.serialize(str);
		System.out.println(SafeEncoder.encode(b));
		System.out.println(g_ser.deserialize(b));
	}
}