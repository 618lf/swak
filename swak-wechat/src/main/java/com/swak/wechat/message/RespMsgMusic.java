package com.swak.wechat.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.swak.wechat.Constants.RespType;
import com.swak.wechat.message.plain.Music;

/**
 * 回复音乐消息
 *
 * @author lifeng
 */
@XmlRootElement(name = "xml")
public class RespMsgMusic extends AbstractRespMsg {
    private static final long serialVersionUID = 1L;
    private Music music;

    public RespMsgMusic() {
    }

    public RespMsgMusic(MsgHead req, Music music) {
        super(req, RespType.music.name());
        this.music = music;
    }

    @XmlElement(name = "Music")
    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }
}
