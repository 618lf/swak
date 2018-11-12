package com.tmt.shop.web;

import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.reactivex.web.annotation.GetMapping;
import com.swak.reactivex.web.annotation.RestController;

/**
 * 测试的 demo
 * @author lifeng
 */
@RestController("/")
public class FaviconController {
	
	@GetMapping("favicon.ico")
	public String icon(HttpServerResponse response) {
		return "123";
	}
}