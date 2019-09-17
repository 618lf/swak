package com.swak.wechat.message.plain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.utils.CDataAdapter;

@XmlRootElement(name="Image")
public class Image implements Serializable {
	private static final long serialVersionUID = 1L;
	private String media_id;
	@XmlElement(name="MediaId")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String mediaId) {
		media_id = mediaId;
	}
}