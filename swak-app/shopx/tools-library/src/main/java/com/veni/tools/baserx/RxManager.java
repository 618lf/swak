package com.veni.tools.baserx;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.observers.LambdaObserver;
import io.reactivex.observers.DisposableObserver;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * 用于管理单个presenter的RxBus的事件和Rxjava相关代码的生命周期处理
 * //监听菜单显示或隐藏
 *  mRxManager.post(AppConstant.MENU_SHOW_HIDE,true);
 * mRxManager.on(AppConstant.MENU_SHOW_HIDE, new Action1<Boolean>() {
 * @Override public void call(Boolean hideOrShow) {
 * }
 * });
 */
public class RxManager {
    public RxBus mRxBus = RxBus.getInstance();
    //管理rxbus订阅
    private Map<String, Observable<?>> mObservables = new HashMap<>();
    /*管理Observables 和 Subscribers订阅*/
    private CompositeDisposable mCompositeSubscription = new CompositeDisposable();

    /**
     * RxBus注入监听
     *
     * @param eventName
     * @param action1
     */
    public <T> void on(String eventName, Consumer<T> action1) {
        Observable<T> mObservable = mRxBus.register(eventName);
        mObservables.put(eventName, mObservable);
        /*订阅管理*/
        mCompositeSubscription.add(mObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }

                }));
    }

    /**
     * 单纯的Observables 和 Subscribers管理
     *
     * @param m
     */
    public void add(Disposable m) {
        /*订阅管理*/
        mCompositeSubscription.add(m);
    }

    /**
     * 单个presenter生命周期结束，取消订阅和所有rxbus观察
     */
    public void clear() {
        mCompositeSubscription.dispose();// 取消所有订阅
        for (Map.Entry<String, Observable<?>> entry : mObservables.entrySet()) {
            mRxBus.unregister(entry.getKey(), entry.getValue());// 移除rxbus观察
        }
    }

    //发送rxbus
    public void post(Object tag, Object content) {
        mRxBus.post(tag, content);
    }
}
