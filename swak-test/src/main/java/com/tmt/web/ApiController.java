package com.tmt.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.swak.common.cache.Cache;
import com.swak.common.entity.Result;
import com.swak.common.utils.JsonMapper;
import com.swak.reactivex.web.annotation.RequestMapping;
import com.tmt.api.IHelloService;
import com.tmt.cache.CacheUtils;

@Controller
@RequestMapping("/api/v1")
public class ApiController {

	/**
	 * 测试是否能自动注册服务
	 */
	@Autowired
	private IHelloService helloService;
	
	/**
	 * 输出广告
	 * @return
	 */
	@RequestMapping("ad")
	public Result ad() {
		// 数据
    	Theme theme = Theme.newTheme();
    	theme.setName("李锋5");
    	
    	// 存储到缓存
    	Cache<Object> cache = CacheUtils.sys().get();
    	cache.putString("ab", JsonMapper.toJson(theme));
    	
    	System.out.println("来后端获取数据");
		return Result.success(JsonMapper.toJson(theme));
	}
	
	/**
	 * 清除缓存
	 * @return
	 */
	@RequestMapping("clear")
	public Result clear(String name) {
		System.out.println("参数："  + name);
		Cache<Object> cache = CacheUtils.sys().get();
		cache.delete("ab");
		return Result.success();
	}
	
	/**
	 * 清除缓存
	 * @return
	 */
	@RequestMapping("call")
	public Result call(String name) {
		helloService.sayHello();
		return Result.success();
	}
}
