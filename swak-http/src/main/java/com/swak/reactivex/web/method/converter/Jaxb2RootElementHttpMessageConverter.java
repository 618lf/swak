package com.swak.reactivex.web.method.converter;

import java.io.IOException;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.core.annotation.AnnotationUtils;

import com.swak.common.utils.JaxbMapper;
import com.swak.reactivex.HttpConst;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * 处理xml
 * 需要对象中包含 @XmlRootElement 或者 @XmlType
 * @author lifeng
 */
public class Jaxb2RootElementHttpMessageConverter implements HttpMessageConverter<Object> {

	@Override
	public boolean canRead(Class<?> clazz) {
		return clazz.isAnnotationPresent(XmlRootElement.class) || clazz.isAnnotationPresent(XmlType.class);
	}

	@Override
	public Object read(Class<? extends Object> clazz, HttpServerRequest request) throws IOException {
		return JaxbMapper.fromXml(request.getInputStream(), clazz);
	}

	@Override
	public boolean canWrite(Class<?> clazz) {
		return AnnotationUtils.findAnnotation(clazz, XmlRootElement.class) != null;
	}

	@Override
	public void write(Object t, HttpServerResponse response) throws IOException {
		if (t== null) {return;}
		response.header(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_XML);
		JaxbMapper.toXml(t, response.getOutputStream());
	}
}