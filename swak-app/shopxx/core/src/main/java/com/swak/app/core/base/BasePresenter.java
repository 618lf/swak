package com.swak.app.core.base;

import android.content.Context;

import com.swak.app.core.net.BaseObserver;
import com.swak.app.core.net.RequestManager;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 基础的 Presenter
 *
 * @param <T>
 */
public abstract class BasePresenter<T extends BaseView> {
    protected Context context;
    protected T view;
    protected CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void setVM(T v) {
        this.view = v;
    }

    /**
     * 保存RxJava绑定关系
     */
    public void addDisposable(Disposable disposable) {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.add(disposable);
        }
    }

    /**
     * 取消单个RxJava绑定
     */
    public void removeDisposable(Disposable disposable) {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.remove(disposable);
        }
    }

    /**
     * 取消当前Presenter的全部RxJava绑定，置空view
     */
    public void detach() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.clear();
        }
        view = null;
    }

    /**
     * 请求 API 服务
     *
     * @param observable
     * @param observer
     * @param <E>
     */
    protected <E> void executeApi(Observable<E> observable, BaseObserver<E> observer) {
        RequestManager.me().execute(this, observable, observer);
    }
}
