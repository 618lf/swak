package com.swak.reactivex.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * 重定义服务器启动事件
 *
 * @author: lifeng
 * @date: 2020/3/29 12:15
 */
public class ReactiveServerInitializedEvent extends ApplicationContextEvent {
    private static final long serialVersionUID = 1L;
    private Server server;

    public ReactiveServerInitializedEvent(Server server, ApplicationContext source) {
        super(source);
        this.server = server;
    }

    public Server getServer() {
        return server;
    }
}