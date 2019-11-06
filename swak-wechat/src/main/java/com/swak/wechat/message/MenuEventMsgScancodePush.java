package com.swak.wechat.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3c.dom.Element;

import com.swak.utils.CDataAdapter;
import com.swak.utils.XmlParse;

/**
 * 自定义菜单事件: 扫码推事件的事件推送 scancode_waitmsg 或 scancode_push
 * 
 */
public class MenuEventMsgScancodePush extends MenuEventMsg {
	private static final long serialVersionUID = 1L;

	@XmlElement(name="ScanType")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	private String scanType;
	@XmlElement(name="ScanResult")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	private String scanResult;

	public String getScanType() {
		return scanType;
	}

	public void setScanType(String scanType) {
		this.scanType = scanType;
	}

	public String getScanResult() {
		return scanResult;
	}

	public void setScanResult(String scanResult) {
		this.scanResult = scanResult;
	}
	
	@Override
	public void read(Element element) {
		this.scanType = XmlParse.elementText(element, "ScanType");
		this.scanResult = XmlParse.elementText(element, "ScanResult");
	}
}
