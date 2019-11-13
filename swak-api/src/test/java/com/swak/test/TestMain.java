package com.swak.test;

import java.util.List;

import org.junit.Test;

import com.swak.api.ApiConfig;
import com.swak.api.model.Api;

/**
 * 测试输出
 * 
 * @author lifeng
 */
public class TestMain {

	@Test
	public void test() {
		List<Api> apis = ApiConfig.of().addSourcePath("src/test/java").build();
		System.out.println(apis);
	}
}