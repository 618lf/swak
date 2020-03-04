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
 * @author lifeng
 */
public interface VertxProxy {

    /**
     * 应用
     *
     * @param apply
     */
    void apply(Consumer<Vertx> apply);

    /**
     * 销毁
     */
    void destroy(Consumer<Vertx> apply);

    /**
     * 发送消息
     *
     * @param address
     * @param request
     */
    void sentMessage(String address, Msg request, int timeout);

    /**
     * 发送消息
     *
     * @param address
     * @param request
     */
    void sentMessage(String address, Msg request, int timeout, Handler<AsyncResult<Message<Msg>>> replyHandler);

    /**
     * 顺序的执行代码
     */
    <T> CompletableFuture<T> order(Supplier<T> supplier);

    /**
     * 无顺序的执行代码
     */
    <T> CompletableFuture<T> future(Supplier<T> supplier);

    /**
     * 文件系统
     *
     * @return
     */
    FileSystem fileSystem();

    /**
     * 实际的Vertx 对象
     *
     * @return
     */
    Vertx me();

    /**
     * 同步执行代码 -- 实际上使用的是协程来执行代码只能在kotlin中执行
     *
     * @param <T>
     * @param supplier
     * @return
     */
    default <T> CompletableFuture<T> sync(Supplier<T> supplier) {
        return null;
    }
}