package com.tmt.web;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Controller;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.swak.http.Reportable;
import com.swak.mvc.annotation.RequestMapping;
import com.swak.mvc.annotation.RequestMethod;
import com.tmt.cache.CacheUtils;

@Controller
@RequestMapping("/admin/validate")
public class ValidateController implements Reportable {

	private AtomicInteger count = new AtomicInteger();
	
	/**
	 * 输出验证码
	 * 
	 * @return
	 */
	@RequestMapping(value = "/code", method = RequestMethod.GET)
	public String postCode() {
		count.incrementAndGet();
		CacheUtils.sys().wrap().get().getString("lifeng");
		return "";
	}
	
	/**
	 * 输出验证码
	 * 
	 * @return
	 */
	@RequestMapping(value = "/code")
	public String getCode() {
		return "和好";
	}
	
	/**
	 * 打印
	 */
	@Override
	public void report(MetricRegistry registry) {
		registry.register("Validate - count", (Gauge<Integer>) () -> count.get());
	}
}