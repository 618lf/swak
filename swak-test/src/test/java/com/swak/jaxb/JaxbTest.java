package com.swak.jaxb;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.swak.test.utils.MultiThreadTest;
import com.swak.utils.IOUtils;
import com.swak.utils.JaxbMapper;
import com.swak.utils.JsonMapper;

/**
 * 测试 jaxb 的输出
 * 
 * @ClassName: JaxbTest
 * @Description:TODO(描述这个类的作用)
 * @author: lifeng
 * @date: Nov 14, 2019 11:49:55 AM
 */
public class JaxbTest {

	@Test
	public void test() {
		Order o = new Order();
		o.setId("1");
		String xml = JaxbMapper.toXml(o);
		System.out.println(xml);
		o = JaxbMapper.fromXml(xml, Order.class);
		System.out.println(JsonMapper.toJson(o));
		MultiThreadTest.run(() -> {
			ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
			Order osString = JaxbMapper.fromXml(bis, Order.class);
			if (!osString.getId().equals("1")) {
				System.out.println("错误");
			}
			IOUtils.closeQuietly(bis);
		}, 1000, "xml解析并发测试");
	}
}