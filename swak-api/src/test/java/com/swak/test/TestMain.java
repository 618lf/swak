package com.swak.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import com.swak.Constants;
import com.swak.api.ApiConfig;
import com.swak.api.ApiGeneratorMojo;
import com.swak.doc.Api;
import com.swak.utils.IOUtils;
import com.swak.utils.StringUtils;

/**
 * 测试输出
 * 
 * @author lifeng
 */
public class TestMain {

	@Test
	public void test() {
		List<Api> apis = ApiConfig.of().addSourcePath("/src/test/java").build();
		System.out.println(apis);
		
		String template = loadTemplate();
		System.out.println(template);
	}
	
	private String loadTemplate() {
		ByteArrayOutputStream buff = null;
		InputStream in = null;
		try {
			buff = new ByteArrayOutputStream();
			in = ApiGeneratorMojo.class.getResourceAsStream("template.jav");
			byte[] _buff = new byte[100];
			int size = 0;
			while ((size = in.read(_buff)) != -1) {
				buff.write(_buff, 0, size);
			}
			return new String(buff.toByteArray(), Constants.DEFAULT_ENCODING);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(buff);
			IOUtils.closeQuietly(in);
		}
		return StringUtils.EMPTY;
	}
}