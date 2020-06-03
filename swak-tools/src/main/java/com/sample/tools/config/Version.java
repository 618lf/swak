package com.sample.tools.config;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 版本
 * 
 * @author lifeng
 */
@XmlRootElement(name = "version")
public class Version {
	private String name;
	private Double version;
	private String describe;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getVersion() {
		return version;
	}

	public void setVersion(Double version) {
		this.version = version;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
}