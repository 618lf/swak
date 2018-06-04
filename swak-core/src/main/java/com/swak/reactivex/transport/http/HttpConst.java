package com.swak.reactivex.transport.http;

import java.nio.charset.Charset;

import com.swak.exception.BaseRuntimeException;

import io.netty.util.AsciiString;

/**
 * Headers
 * @author lifeng
 */
public interface HttpConst {
	
	// 错误
	Exception HANDLER_NOT_FOUND_EXCEPTION = new BaseRuntimeException("No matching handler");

	// 系统级别
	CharSequence X_POWER_BY = AsciiString.cached("X-Powered-By");
	CharSequence VERSION = AsciiString.cached("SWAK-0.0.4_final");
	
	// 只支持的编码格式
	Charset DEFAULT_CHARSET = Charset.forName("utf-8");
	
	// 只支持这三种返回类型
	CharSequence APPLICATION_HTML = AsciiString.cached("text/html; charset=UTF-8");
	CharSequence APPLICATION_TEXT = AsciiString.cached("text/plain; charset=UTF-8");
	CharSequence APPLICATION_JSON = AsciiString.cached("application/json; charset=UTF-8");
	CharSequence APPLICATION_XML = AsciiString.cached("application/xml; charset=UTF-8");
	
	// 某些 key
	String ATTRIBUTE_FOR_PATH = "base.matched.pattern.path";
}
