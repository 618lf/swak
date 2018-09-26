package com.swak.vertx.handler.converter;

import com.swak.utils.StringUtils;
import com.swak.vertx.transport.multipart.MimeType;
import com.swak.vertx.transport.multipart.MultipartFile;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;

/**
 * 数据流输出
 * 
 * @author lifeng
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
	public void write(Object t, HttpServerResponse response) {
		MultipartFile file = (MultipartFile) t;
		response.putHeader(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaderNames.CONTENT_DISPOSITION);
		response.putHeader(HttpHeaderNames.CONTENT_TYPE, MimeType.getMimeType(file.getFileName()));
		response.putHeader(HttpHeaderNames.CONTENT_DISPOSITION, StringUtils.format("%s;%s=%s",
				HttpHeaderValues.ATTACHMENT, HttpHeaderValues.FILENAME, file.getFileName()));
		if (file.getFile() != null) {
			response.sendFile(file.getFile().getAbsolutePath(), (event) -> {
				if (file.getAccept() != null) {
					file.getAccept().accept(null);
				}
			});
		} else if (file.getData() != null) {
			Buffer buffer = Buffer.buffer(file.getData());
			response.end(buffer);
		}
	}
}
