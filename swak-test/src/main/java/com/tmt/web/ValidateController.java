package com.tmt.web;

import org.springframework.stereotype.Controller;

import com.swak.mvc.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/validate")
public class ValidateController {

	/**
	 * 输出验证码
	 * @return
	 */
	@RequestMapping("/code")
	public String code() {
		return "哈哈";
	}
}
