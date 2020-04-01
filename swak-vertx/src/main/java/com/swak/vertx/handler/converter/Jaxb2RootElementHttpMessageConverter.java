package com.swak.vertx.handler.converter;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.core.annotation.AnnotationUtils;

import com.swak.utils.JaxbMapper;
import com.swak.vertx.transport.HttpConst;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.http.HttpServerResponse;

/**
 * 处理xml 需要对象中包含 @XmlRootElement
 *
 * @author: lifeng
 * @date: 2020/3/29 19:21
 */
public class Jaxb2RootElementHttpMessageConverter implements HttpMessageConverter {

    @Override
    public boolean canWrite(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, XmlRootElement.class) != null;
    }

    @Override
    public void write(Object t, HttpServerResponse response) {
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_XML);
        String xml = JaxbMapper.toXml(t);
        response.end(xml);
    }
}