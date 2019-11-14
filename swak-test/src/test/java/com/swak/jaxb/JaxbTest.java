package com.swak.jaxb;

import org.junit.Test;

import com.swak.utils.JaxbMapper;

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
		System.out.println(JaxbMapper.toXml(o));
	}
}