package com.swak.vertx.protocol.im.converter;

import com.swak.codec.Encodes;
import com.swak.utils.StringUtils;
import com.swak.vertx.protocol.im.ImContext.ImResponse;
import com.swak.vertx.transport.multipart.MimeType;
import com.swak.vertx.transport.multipart.MultipartFile;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.buffer.Buffer;

/**
 * 数据流输出
 *
 * @author: lifeng
 * @date: 2020/3/29 19:21
 */
public class StreamMessageConverter implements HttpMessageConverter {

    /**
     * 只处理文件流
     */
    @Override
    public boolean canWrite(Class<?> clazz) {
        return MultipartFile.class == clazz;
    }

    /**
     * 输出数据
     */
    @Override
    public void write(Object t, ImResponse response) {
        MultipartFile file = (MultipartFile) t;
        response.putHeader(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaderNames.CONTENT_DISPOSITION);
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, MimeType.getMimeType(file.fileName()) + "; charset=UTF-8");
        response.putHeader(HttpHeaderNames.CONTENT_DISPOSITION, StringUtils.format("%s;%s=%s",
                HttpHeaderValues.ATTACHMENT, HttpHeaderValues.FILENAME, Encodes.urlEncode(file.fileName())));
        if (file.file() != null) {
            response.sendFile(file.file().getAbsolutePath(), (event) -> {
                if (file.accept() != null) {
                    file.accept().run();
                }
            });
        } else if (file.data() != null) {
            Buffer buffer = Buffer.buffer(file.data());
            response.out(buffer);
        }
    }
}
