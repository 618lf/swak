package com.swak.test.api;

import com.swak.annotation.GetMapping;
import com.swak.annotation.Header;
import com.swak.annotation.Json;
import com.swak.annotation.RestController;
import com.swak.test.dto.UserDTO;

/**
 * test Api
 * 
 * @author lifeng
 */
@RestController(path = { "/api/test", "/api/v2/test" })
public class TestApi {

	/**
	 * 測試的參數传递形式 
	 * 
	 * @param name 名称
	 * @param names 名称的json 形式
	 * @param user 用户
	 */
	@GetMapping(value = "one")
	public void one(@Header String name, @Json String names, UserDTO user) {

	}
}
