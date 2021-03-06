package com.swak.reactor.publisher;

import java.util.function.LongConsumer;

import reactor.core.Disposable;
import reactor.core.publisher.MonoSink;
import reactor.util.context.Context;

/**
 * 相当于一个代理
 *
 * @author: lifeng
 * @date: 2020/3/29 13:08
 */
public class DisposableMonoSink<T> implements MonoSink<T>, Disposable {

    MonoSink<T> delegate;
    Disposable d;

    public DisposableMonoSink(MonoSink<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Context currentContext() {
        return delegate.currentContext();
    }

    @Override
    public void success() {
        delegate.success();
    }

    @Override
    public void success(T value) {
        delegate.success(value);
    }

    @Override
    public void error(Throwable e) {
        delegate.error(e);
    }

    @Override
    public MonoSink<T> onRequest(LongConsumer consumer) {
        return delegate.onRequest(consumer);
    }

    @Override
    public MonoSink<T> onCancel(Disposable d) {
        this.d = d;
        return delegate.onCancel(d);
    }

    @Override
    public MonoSink<T> onDispose(Disposable d) {
        this.d = d;
        return delegate.onDispose(d);
    }

    @Override
    public boolean isDisposed() {
        if (this.d != null) {
            d.isDisposed();
        }
        return false;
    }

    @Override
    public void dispose() {
        if (this.d != null) {
            d.dispose();
        }
    }
}