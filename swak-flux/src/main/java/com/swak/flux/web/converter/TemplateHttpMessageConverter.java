package com.swak.flux.web.converter;

import java.io.OutputStreamWriter;

import com.swak.flux.transport.server.HttpServerResponse;
import com.swak.flux.web.template.Model;

import freemarker.template.Configuration;

/**
 * 支持模板消息的处理
 * 
 * @author lifeng
 */
public class TemplateHttpMessageConverter implements HttpMessageConverter {
	
	private Configuration configuration;

	public TemplateHttpMessageConverter(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public boolean canWrite(Class<?> clazz) {
		return Model.class == clazz;
	}

	@Override
	public void write(Object t, HttpServerResponse response) {
		try {
			Model model = (Model) t;
			OutputStreamWriter result = new OutputStreamWriter(response.getOutputStream());
			configuration.getTemplate(model.getView()).process(model.getContext(), result);
		} catch (Exception e) {
		}
	}
}