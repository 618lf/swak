package com.swak.app.core.net;

import android.widget.Toast;

import com.swak.app.core.base.AppContext;
import com.swak.app.core.net.exception.ExceptionHandler;
import com.swak.app.core.net.exception.ResponseException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public abstract class BaseObserver<E> implements Observer<E> {
    private Disposable disposable;

    private boolean showErrorTip;

    /**
     * @param showErrorTip 发生异常时，是否使用Toast提示
     */
    public BaseObserver(boolean showErrorTip) {
        this.showErrorTip = showErrorTip;
    }

    @Override
    public void onSubscribe(Disposable d) {
        disposable = d;
    }

    @Override
    public void onNext(E data) {
        onSuccess(data);
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        ResponseException responseException = ExceptionHandler.handle(e);
        if (showErrorTip) {
            Toast.makeText(AppContext.me(), responseException.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
        onError(responseException);
    }

    @Override
    public void onComplete() {

    }

    public Disposable getDisposable() {
        return disposable;
    }

    protected abstract void onSuccess(E data);

    protected abstract void onError(ResponseException e);
}
