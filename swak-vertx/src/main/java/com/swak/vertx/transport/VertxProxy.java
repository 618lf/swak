package com.swak.vertx.transport;

import com.swak.vertx.transport.codec.Msg;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 代理处理 vertx 的 相关服务
 *
 * @author: lifeng
 * @date: 2020/3/29 21:27
 */
public interface VertxProxy {

    /**
     * 应用
     *
     * @param apply Vertx
     */
    void apply(Consumer<Vertx> apply);

    /**
     * 销毁
     *
     * @param apply Vertx
     */
    void destroy(Consumer<Vertx> apply);

    /**
     * 发送消息
     *
     * @param address 地址
     * @param request 请求
     * @param timeout 超时时间
     */
    void sentMessage(String address, Msg request, int timeout);

    /**
     * 发送消息
     *
     * @param address      地址
     * @param request      请求
     * @param timeout      超时时间
     * @param replyHandler 回调
     */
    void sentMessage(String address, Msg request, int timeout, Handler<AsyncResult<Message<Msg>>> replyHandler);

    /**
     * 顺序的执行代码
     *
     * @param supplier 代码
     * @return 异步结果
     */
    <T> CompletableFuture<T> order(Supplier<T> supplier);

    /**
     * 无序的执行代码
     *
     * @param supplier 代码
     * @return 异步结果
     */
    <T> CompletableFuture<T> future(Supplier<T> supplier);

    /**
     * 文件系统
     *
     * @return FileSystem
     */
    FileSystem fileSystem();

    /**
     * 实际的Vertx 对象
     *
     * @return Vertx
     */
    Vertx me();
}