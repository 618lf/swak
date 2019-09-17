package com.swak.wechat.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.utils.CDataAdapter;
import com.swak.wechat.Constants.RespType;

/**
 * 回复语音消息
 * @author lifeng
 */
@XmlRootElement(name="xml")
public class RespMsgVoice extends RespMsg {
	private static final long serialVersionUID = 1L;

	private Voice voice;
	public RespMsgVoice(MsgHead req, String mediaId) {
		super(req, RespType.voice.name());
		this.voice = new Voice();
		this.voice.setMediaId(mediaId);
	}
	public RespMsgVoice() {}
	
	@XmlElement(name="Voice")
	public Voice getVoice() {
		return voice;
	}
	public void setVoice(Voice voice) {
		this.voice = voice;
	}

	public static class Voice {
		private String mediaId;
		@XmlElement(name="MediaId")
		@XmlJavaTypeAdapter(CDataAdapter.class)
		public String getMediaId() {
			return mediaId;
		}
		public void setMediaId(String mediaId) {
			this.mediaId = mediaId;
		}
	}
}