package com.swak.flux.transport;

import java.nio.charset.Charset;

import com.swak.flux.transport.multipart.MimeType;
import com.swak.flux.transport.server.ResponseStatusException;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;

/**
 * Headers
 * 
 * @author lifeng
 */
public interface HttpConst {

	// 错误
	RuntimeException NOT_FOUND_EXCEPTION = new ResponseStatusException(HttpResponseStatus.NOT_FOUND);

	// 系统级别
	CharSequence X_POWER_BY = AsciiString.cached("X-Powered-By");
	CharSequence VERSION = AsciiString.cached("SWAK-0.1.2_final");

	// 只支持的编码格式
	Charset DEFAULT_CHARSET = Charset.forName("utf-8");

	// 主要的几种返回类型, 更多的见 MimeType
	CharSequence APPLICATION_STREAM = AsciiString.cached(MimeType.getMimeType("bin").toString());
	CharSequence APPLICATION_HTML = AsciiString.cached(MimeType.getMimeType("html").toString());
	CharSequence APPLICATION_TEXT = AsciiString.cached(MimeType.getMimeType("txt").toString());
	CharSequence APPLICATION_JSON = AsciiString.cached(MimeType.getMimeType("json").toString());
	CharSequence APPLICATION_XML = AsciiString.cached(MimeType.getMimeType("xml").toString());
	
	// 某些 重要key
	String ATTRIBUTE_FOR_PATH = "base.matched.pattern.path";
}
