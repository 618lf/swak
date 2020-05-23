package com.swak.wechat.message;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.utils.CDataAdapter;
import com.swak.wechat.Constants.RespType;

/**
 * 回复视频消息
 *
 * @author lifeng
 */
@XmlRootElement(name = "xml")
public class RespMsgVideo extends AbstractRespMsg {
    private static final long serialVersionUID = 1L;
    private Video video;

    public RespMsgVideo(MsgHead req, String mediaId, String title, String description) {
        super(req, RespType.video.name());
        this.video = new Video();
        this.video.setMedia_id(mediaId);
        this.video.setTitle(title);
        this.video.setDescription(description);
    }

    public RespMsgVideo() {
    }

    @XmlElement(name = "Video")
    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public static class Video implements Serializable {

        private static final long serialVersionUID = 1L;

        public String title;
        public String description;
        public String media_id;

        @XmlElement(name = "Title")
        @XmlJavaTypeAdapter(CDataAdapter.class)
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @XmlElement(name = "Description")
        @XmlJavaTypeAdapter(CDataAdapter.class)
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @XmlElement(name = "MediaId")
        @XmlJavaTypeAdapter(CDataAdapter.class)
        public String getMedia_id() {
            return media_id;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }
    }
}