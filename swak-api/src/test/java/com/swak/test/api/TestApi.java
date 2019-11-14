package com.swak.test.api;

import com.swak.annotation.GetMapping;
import com.swak.annotation.Header;
import com.swak.annotation.Json;
import com.swak.annotation.RestController;
import com.swak.test.dto.UserDTO;

/**
 * test Api
 * 
 * @ClassName: TestApi
 * @Description:TODO(描述这个类的作用)
 * @author: lifeng
 * @date: Nov 14, 2019 11:31:37 AM
 */
@RestController(path = { "/api/test", "/api/v2/test" })
public class TestApi {

	/**
	 * 111
	 * 
	 * @Title: one
	 * @Description: TODO(描述)
	 * @param name
	 * @param names
	 * @param user
	 * @return
	 * @author lifeng
	 * @date 2019-11-14 11:34:59
	 */
	@GetMapping(value = "one")
	public UserDTO one(@Header String name, @Json String names, UserDTO user) {
		return null;
	}

	/**
	 * 測試的參數传递形式
	 * 
	 * @param id
	 *            主键
	 */
	@GetMapping(value = "two/:id")
	public void two(String id) {

	}
}
