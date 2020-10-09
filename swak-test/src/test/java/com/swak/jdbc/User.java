package com.swak.jdbc;

import com.swak.annotation.Column;
import com.swak.annotation.Table;
import com.swak.entity.BaseEntity;

/**
 * 用户信息
 * 
 * @author lifeng
 * @date 2020年10月9日 下午9:44:45
 */
@Table(value = "CLOUD_USER")
public class User extends BaseEntity<Long> {

	private static final long serialVersionUID = 1L;

	@Column(value = "NICK_NAME")
	private String name;
	private String address;
	private String[] tests;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String[] getTests() {
		return tests;
	}

	public void setTests(String[] tests) {
		this.tests = tests;
	}
}
