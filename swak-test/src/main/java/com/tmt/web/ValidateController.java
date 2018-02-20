package com.tmt.web;

import org.springframework.stereotype.Controller;

import com.swak.mvc.annotation.RequestMapping;
import com.swak.mvc.annotation.RequestMethod;

@Controller
@RequestMapping("/admin/validate")
public class ValidateController {

	/**
	 * 输出验证码
	 * 
	 * @return
	 */
	@RequestMapping(value = "/code", method = RequestMethod.GET)
	public String postCode() {
		return "哈哈";
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
}