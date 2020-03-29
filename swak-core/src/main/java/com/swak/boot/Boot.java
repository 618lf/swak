package com.swak.boot;

/**
 * 系统启动项
 *
 * @author root
 */
public interface Boot {

    /**
     * 系统启动项 -- 启动
     */
    void start();

    /**
     * 系统启动项 -- 关闭
     */
    default void destory() {
    }

    /**
     * 启动描述
     *
     * @return 描述
     */
    String describe();
}