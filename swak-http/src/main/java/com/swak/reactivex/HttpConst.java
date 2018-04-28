package com.swak.reactivex;

import java.nio.charset.Charset;

import io.netty.util.AsciiString;

/**
 * Headers
 * @author lifeng
 */
public interface HttpConst {
	
	// 系统级别
	CharSequence X_POWER_BY = AsciiString.cached("X-Powered-By");
	CharSequence VERSION = AsciiString.cached("netty-1.0");
	
	// 只支持的编码格式
	Charset DEFAULT_CHARSET = Charset.forName("utf-8");
	
	// 只支持这三种返回类型
	CharSequence APPLICATION_TEXT = AsciiString.cached("text/plain; charset=UTF-8");
	CharSequence APPLICATION_JSON = AsciiString.cached("application/json; charset=UTF-8");
	CharSequence APPLICATION_XML = AsciiString.cached("application/xml; charset=UTF-8");
}
