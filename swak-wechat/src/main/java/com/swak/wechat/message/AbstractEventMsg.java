package com.swak.wechat.message;

import com.swak.utils.StringUtils;

/**
 * 事件消息
 *
 * @author: lifeng
 * @date: 2020/4/1 11:18
 */
public class AbstractEventMsg extends MsgHeadImpl implements ReqMsg {

    private static final long serialVersionUID = 1L;

    @Override
    public String getMsgId() {
        return StringUtils.EMPTY;
    }
}
