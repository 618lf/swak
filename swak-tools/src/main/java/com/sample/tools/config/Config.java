package com.sample.tools.config;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 系统配置
 * 
 * @author lifeng
 */
@XmlRootElement(name = "settings")
public class Config {

	private String accessIps;
	private Boolean accessAble;
	private Mode mode = Mode.client;

	public String getAccessIps() {
		return accessIps;
	}

	public void setAccessIps(String accessIps) {
		this.accessIps = accessIps;
	}

	public Boolean getAccessAble() {
		return accessAble;
	}

	public void setAccessAble(Boolean accessAble) {
		this.accessAble = accessAble;
	}

	public boolean accessAble() {
		return accessAble != null && accessAble;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public boolean server() {
		return this.mode != null && this.mode == Mode.server;
	}
}