package com.swak.test.api;

import java.util.concurrent.CompletableFuture;

import com.swak.annotation.GetMapping;
import com.swak.annotation.Header;
import com.swak.annotation.Json;
import com.swak.annotation.RestApi;
import com.swak.entity.Result;
import com.swak.test.dto.UserDTO;

/**
 * test Api
 * 
 * @ClassName: TestApi
 * @Description:TODO(描述这个类的作用)
 * @author: lifeng
 * @date: Nov 14, 2019 11:31:37 AM
 */
@RestApi(path = { "/api/test", "/api/v2/test" })
public class TestApi {

	/**
	 * 111
	 * 
	 * @author lifeng
	 * @date 2019-11-14 11:34:59
	 * 
	 * @param name 用户名称
	 * @param names 用户名称
	 * @param user 用户名称
	 * @return 异步结果
	 */
	@GetMapping(value = "one")
	public CompletableFuture<Result> one(@Header String name, @Json String names, UserDTO user) {
		return null;
	}
}
