package com.swak.wechat.message.plain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.utils.CDataAdapter;

@XmlRootElement(name="item")
public class Article implements Serializable {

	private static final long serialVersionUID = -6881027431094009765L;
	
	private String title;
	private String description;
	private String picUrl;
	private String url;
	
	public Article(){}
	public Article(String title, String description, String picUrl, String url) {
		this.title = title;
		this.description = description;
		this.picUrl = picUrl;
		this.url = url;
	}
	@XmlElement(name="Title")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	@XmlElement(name="Description")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlElement(name="PicUrl")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	@XmlElement(name="Url")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
