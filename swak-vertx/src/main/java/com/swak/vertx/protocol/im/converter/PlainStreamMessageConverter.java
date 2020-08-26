package com.swak.vertx.protocol.im.converter;

import com.swak.vertx.protocol.im.ImContext.ImResponse;
import com.swak.vertx.transport.HttpConst;
import com.swak.vertx.transport.multipart.PlainFile;

import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * 原文的输出
 *
 * @author: lifeng
 * @date: 2020/3/29 19:21
 */
public class PlainStreamMessageConverter implements HttpMessageConverter {

    /**
     * 只处理文件流
     */
    @Override
    public boolean canWrite(Class<?> clazz) {
        return PlainFile.class == clazz;
    }

    /**
     * 输出数据
     */
    @Override
    public void write(Object t, ImResponse response) {
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
        PlainFile file = (PlainFile) t;
        response.sendFile(file.file().getAbsolutePath(), (event) -> {
            if (file.accept() != null) {
                file.accept().run();
            }
        });
    }
}