package com.swak.reactivex.transport;

/**
 * 用于处理服务器的一些资源
 *
 * @author: lifeng
 * @date: 2020/3/29 13:04
 */
public interface Disposable {

    /**
     * 关闭
     */
    void dispose();

    /**
     * 是否关闭
     *
     * @return 是否关闭
     */
    default boolean isDisposed() {
        return false;
    }
}
