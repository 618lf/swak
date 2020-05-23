package com.swak.vertx.protocol.http.converter;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;

import com.swak.entity.Model;
import com.swak.vertx.transport.HttpConst;

import freemarker.template.Configuration;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;

/**
 * 支持模板消息的处理
 *
 * @author: lifeng
 * @date: 2020/3/29 19:23
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
        try (ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            Model model = (Model) t;
            configuration.getTemplate(model.getView()).process(model.getContext(), new OutputStreamWriter(result));
            response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_HTML);
            response.end(Buffer.buffer(result.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}