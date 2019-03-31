package com.tmt.shop.web;

import java.util.List;
import java.util.Map;

import com.swak.entity.Result;
import com.swak.flux.web.annotation.GetMapping;
import com.swak.flux.web.annotation.RestController;
import com.tmt.shop.entity.Shop;

/**
 * 参数测试
 * 
 * @author lifeng
 */
@RestController(path = "/admin/hello/param")
public class ParamController {

	/**
	 * list 参数
	 * @param list
	 * @return
	 */
	@GetMapping("list")
	public List<String> listParam(List<String> name) {
		System.out.println(name);
		return name;
	}
	
	/**
	 * map 参数
	 * @param list
	 * @return
	 */
	@GetMapping("map")
	public Map<String, String> mapParam(Map<String, String> map) {
		System.out.println(map);
		return map;
	}
	
	/**
	 * 基本类型
	 * 
	 * @param list
	 * @return
	 */
	@GetMapping("base")
	public Result baseParam(String name, Integer num) {
		System.out.println(name + ":" + num);
		return Result.success();
	}
	
	/**
	 * 自定义对象
	 * 
	 * @param list
	 * @return
	 */
	@GetMapping("object")
	public Result objectParam(Shop shop) {
		System.out.println(shop);
		return Result.success(shop);
	}
}