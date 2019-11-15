package com.swak.test.dto;

import java.util.List;
import java.util.Map;

import com.swak.annotation.NotNull;
import com.swak.annotation.Regex;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserDTO {

	/**
	 * 用户主键
	 */
	@NotNull
	private Long id;
	
	/**
	 * 用户名称
	 */
	@NotNull
	@Regex("[A-Z]")
	private String name;
	
	/**
	 * 账户信息
	 */
	private UserAccountDTO account;
	
	/**
	 * 账户信息
	 */
	private List<UserAccountDTO> accounts;
	
	/**
	 * 设置 list 参数
	 */
	private List<String> p3;
	
	/**
	 * 设置 Map 参数
	 */
	private Map<String, Object> p4;
}
