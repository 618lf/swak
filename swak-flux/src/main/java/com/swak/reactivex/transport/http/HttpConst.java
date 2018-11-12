package com.swak.reactivex.transport.http;

import java.nio.charset.Charset;

import com.swak.reactivex.transport.http.server.ResponseStatusException;

import io.netty.handler.codec.http.HttpHeaderNames;
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
	CharSequence VERSION = AsciiString.cached("SWAK-0.0.4_final");

	// 只支持的编码格式
	Charset DEFAULT_CHARSET = Charset.forName("utf-8");

	// 只支持这几中种返回类型
	CharSequence APPLICATION_STREAM = AsciiString.cached("application/octet-stream");
	CharSequence APPLICATION_HTML = AsciiString.cached("text/html; charset=UTF-8");
	CharSequence APPLICATION_TEXT = AsciiString.cached("text/plain; charset=UTF-8");
	CharSequence APPLICATION_JSON = AsciiString.cached("application/json; charset=UTF-8");
	CharSequence APPLICATION_XML = AsciiString.cached("application/xml; charset=UTF-8");
	
	// 静态资源
	CharSequence IF_MODIFIED_SINCE = HttpHeaderNames.IF_MODIFIED_SINCE;

	// 某些 key
	String ATTRIBUTE_FOR_PATH = "base.matched.pattern.path";
}
