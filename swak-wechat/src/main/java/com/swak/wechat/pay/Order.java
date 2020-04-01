package com.swak.wechat.pay;

import com.swak.wechat.WechatConfig;

/**
 * 微信订单
 *
 * @author: lifeng
 * @date: 2020/4/1 11:59
 */
public interface Order {

    /**
     * 校验参数
     *
     * @param config 配置
     */
    void checkAndSign(WechatConfig config);
}
