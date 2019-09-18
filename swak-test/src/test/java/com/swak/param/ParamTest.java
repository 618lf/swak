package com.swak.param;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.swak.test.utils.MultiThreadTest;

/**
 * 参数校验
 * 
 * @author lifeng
 */
public class ParamTest {

	private Pattern OBJECT_PARAM_PATTERN = Pattern.compile("(\\w+)\\[(\\w+)\\](\\[(\\w+)\\])?(\\[(\\w+)\\])?");

	@Test
	public void test() {
		MultiThreadTest.run(() -> {
			for (int i = 0; i < 10000; i++) {
				Matcher found = OBJECT_PARAM_PATTERN.matcher("param[items][0][name]");
				if (found.find()) {
//					String key = found.group(1);
//					String k2 = found.group(2);
//					String k3 = found.group(4);
//					String k4 = found.group(6);
				}
			}
		}, 100, "parse params pattern ");
	}

	@Test
	public void test2() {
		MultiThreadTest.run(() -> {
			for (int i = 0; i < 1; i++) {
//				String[] values = "param[items][0][name]".split("\\[");
//				String key = values[0];
//				String k2 = values.length >= 2 ? values[1].substring(0,values[1].length() - 1) : null;
//				String k3 = values.length == 3 ? values[2].substring(0, values[2].length() - 1) : null;
//				String k4 = values.length == 4 ? values[3].substring(0, values[3].length() - 1): null;
//				System.out.println(k2);
			}
		}, 1, "parse params split ");
	}
	
	@Test
	public void test3() {
		
		MultiThreadTest.run(() -> {
			for (int i = 0; i < 10000; i++) {
				
			}
		}, 1, "parse params split ");
	}
}
