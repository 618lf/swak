package com.swak.wechat.message.plain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.utils.CDataAdapter;

@XmlRootElement(name="Video")
public class Video implements Serializable {

	private static final long serialVersionUID = 1L;
	public String title;
	public String description;
	public String media_id;
	public String thumb_media_id;

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
	@XmlElement(name="MediaId")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getMedia_id() {
		return media_id;
	}

	public void setMedia_id(String media_id) {
		this.media_id = media_id;
	}
	@XmlElement(name="ThumbMediaId")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getThumb_media_id() {
		return thumb_media_id;
	}

	public void setThumb_media_id(String thumb_media_id) {
		this.thumb_media_id = thumb_media_id;
	}
}