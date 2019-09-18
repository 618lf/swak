package com.tmt.api.web;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.swak.annotation.Json;

/**
 * 参数测试
 * 
 * @author lifeng
 */
@XmlRootElement
public class Param {

	private Integer p1;
	private String p2;
	private List<String> p3;
	@Json
	private Map<String, Object> p4;
	
	public Integer getP1() {
		return p1;
	}
	public String getP2() {
		return p2;
	}
	public List<String> getP3() {
		return p3;
	}
	public Map<String, Object> getP4() {
		return p4;
	}
	public void setP1(Integer p1) {
		this.p1 = p1;
	}
	public void setP2(String p2) {
		this.p2 = p2;
	}
	public void setP3(List<String> p3) {
		this.p3 = p3;
	}
	public void setP4(Map<String, Object> p4) {
		this.p4 = p4;
	}
}
