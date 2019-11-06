package com.swak.wechat.message.plain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.utils.CDataAdapter;

@XmlRootElement(name="Music")
public class Music implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String title;
	public String description;
	public String musicurl;
	public String hqmusicurl;
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
	@XmlElement(name="MusicUrl")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getMusicurl() {
		return musicurl;
	}
	public void setMusicurl(String musicurl) {
		this.musicurl = musicurl;
	}
	@XmlElement(name="HQMusicUrl")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getHqmusicurl() {
		return hqmusicurl;
	}
	public void setHqmusicurl(String hqmusicurl) {
		this.hqmusicurl = hqmusicurl;
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
