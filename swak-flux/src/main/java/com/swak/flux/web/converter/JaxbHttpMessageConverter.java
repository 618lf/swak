package com.swak.flux.web.converter;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.core.annotation.AnnotationUtils;

import com.swak.flux.transport.server.HttpServerResponse;
import com.swak.utils.JaxbMapper;

/**
 * 处理xml
 * 需要对象中包含 @XmlRootElement 或者 @XmlType
 * @author lifeng
 */
public class JaxbHttpMessageConverter implements HttpMessageConverter {

	@Override
	public boolean canWrite(Class<?> clazz) {
		return AnnotationUtils.findAnnotation(clazz, XmlRootElement.class) != null;
	}

	@Override
	public void write(Object t, HttpServerResponse response) {
		JaxbMapper.toXml(t, response.xml().getOutputStream());
	}
}