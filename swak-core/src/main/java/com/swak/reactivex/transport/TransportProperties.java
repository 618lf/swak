package com.swak.reactivex.transport;

import com.swak.OS;

/**
 * 协议的通用配置
 *
 * @author: lifeng
 * @date: 2020/3/29 13:07
 */
public class TransportProperties {

    private TransportMode mode = TransportMode.OS;

    public TransportMode getMode() {
        if (TransportMode.OS == mode) {
            return this.getModeByOs();
        }
        return mode;
    }

    private TransportMode getModeByOs() {
        OS os = OS.me();
        if (os == OS.linux) {
            return TransportMode.EPOLL;
        }
        return TransportMode.NIO;
    }

    public void setMode(TransportMode mode) {
        this.mode = mode;
    }
}