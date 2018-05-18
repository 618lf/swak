package com.tmt.shop.web;

import com.swak.reactivex.HttpServerResponse;
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
		response.cache(2592000);
		return "123";
	}
}