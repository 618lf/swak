package com.swak.freemarker;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import com.swak.exception.BaseRuntimeException;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 模板工具
 * 
 * @author lifeng
 */
public class TemplateMarker {

	private static TemplateMarker ME = null;
	private Configuration configuration;

	public TemplateMarker(Configuration configuration) {
		ME = this;
		this.configuration = configuration;
	}

	/**
	 * 通过模板目录下的模板来生成
	 * 
	 * @param template
	 * @param model
	 * @return
	 */
	public String useTemplate(String template, Map<String, ?> model) {
		try {
			StringWriter result = new StringWriter();
			configuration.getTemplate(template).process(model, result);
			return result.toString();
		} catch (Exception localIOException) {
			throw new BaseRuntimeException("生成模版错误", localIOException);
		}
	}

	/**
	 * 通过动态的内容来生成
	 * 
	 * @param template
	 * @param model
	 * @return
	 */
	public String useContent(String template, Map<String, ?> model) {
		StringWriter out = new StringWriter();
		try {
			new Template("template", new StringReader(template), configuration).process(model, out);
		} catch (Exception localIOException) {
			throw new BaseRuntimeException("生成数据错误", localIOException);
		}
		return out.toString();
	}

	/**
	 * 返回模板
	 * 
	 * @return
	 */
	public static TemplateMarker me() {
		assert ME != null;
		return ME;
	}
}
