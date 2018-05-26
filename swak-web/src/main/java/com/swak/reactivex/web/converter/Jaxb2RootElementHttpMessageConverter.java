package com.swak.reactivex.web.converter;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.core.annotation.AnnotationUtils;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.utils.JaxbMapper;

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
	public Object read(Class<? extends Object> clazz, HttpServerRequest request){
		return JaxbMapper.fromXml(request.getInputStream(), clazz);
	}

	@Override
	public boolean canWrite(Class<?> clazz) {
		return AnnotationUtils.findAnnotation(clazz, XmlRootElement.class) != null;
	}

	@Override
	public void write(Object t, HttpServerResponse response) {
		JaxbMapper.toXml(t, response.xml().getOutputStream());
	}
}