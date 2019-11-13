package com.swak.test.api;

import com.swak.annotation.GetMapping;
import com.swak.annotation.Header;
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
	 * one 
	 * 
	 * @param name 名称
	 * @param user 用户
	 */
	@GetMapping(value = "one")
	public void one(@Header String name, UserDTO user) {

	}
}
