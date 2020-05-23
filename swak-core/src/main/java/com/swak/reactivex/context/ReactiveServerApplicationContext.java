package com.swak.reactivex.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;

/**
 * 响应式服务的启动器 flux、vertx 需要使用这个来启动
 *
 * @author: lifeng
 * @date: 2020/3/29 12:14
 */
public class ReactiveServerApplicationContext extends AnnotationConfigApplicationContext {

    private volatile Server server;

    @Override
    public final void refresh() throws BeansException, IllegalStateException {
        try {
            super.refresh();
        } catch (RuntimeException ex) {
            stopAndReleaseReactiveServer();
            throw ex;
        }
    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
        try {
            createWebServer();
        } catch (Throwable ex) {
            throw new ApplicationContextException("Unable to start reactive web server",
                    ex);
        }
    }

    @Override
    protected void finishRefresh() {
        super.finishRefresh();
        Server localServer = startReactiveServer();
        if (localServer != null) {
            publishEvent(new ReactiveServerInitializedEvent(localServer, this));
        }
    }

    @Override
    protected void onClose() {
        super.onClose();
        stopAndReleaseReactiveServer();
    }

    private void createWebServer() {
        Server localServer = this.server;
        if (localServer == null) {
            this.server = getReactiveServer();
        }
        initPropertySources();
    }

    /**
     * 获得实际的 ReactiveServer
     */
    protected Server getReactiveServer() {
        // Use bean names so that we don't consider the hierarchy
        String[] beanNames = getBeanFactory()
                .getBeanNamesForType(Server.class);
        if (beanNames.length == 0) {
            throw new ApplicationContextException(
                    "Unable to start ReactiveWebApplicationContext due to missing "
                            + "ReactiveServer bean.");
        }
        if (beanNames.length > 1) {
            throw new ApplicationContextException(
                    "Unable to start ReactiveWebApplicationContext due to multiple "
                            + "ReactiveServer beans : "
                            + StringUtils.arrayToCommaDelimitedString(beanNames));
        }
        return getBeanFactory().getBean(beanNames[0], Server.class);
    }

    private Server startReactiveServer() {
        Server localServer = this.server;
        if (localServer != null) {
            localServer.start();
        }
        return localServer;
    }

    private void stopAndReleaseReactiveServer() {
        Server localServer = this.server;
        if (localServer != null) {
            try {
                localServer.stop();
                this.server = null;
            } catch (Exception ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    /**
     * 获得服务器
     */
    public Server getServer() {
        return this.server;
    }
}