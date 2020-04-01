package com.swak.wechat.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.swak.wechat.Constants.RespType;
import com.swak.wechat.message.plain.Image;

/**
 * 回复图片消息
 *
 * @author lifeng
 */
@XmlRootElement(name = "xml")
public class RespMsgImage extends AbstractRespMsg {

    private static final long serialVersionUID = 1L;
    private Image image;

    public RespMsgImage() {
    }

    public RespMsgImage(MsgHead req, String mediaId) {
        super(req, RespType.image.name());
        this.image = new Image();
        this.image.setMedia_id(mediaId);
    }

    @XmlElement(name = "Image")
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}