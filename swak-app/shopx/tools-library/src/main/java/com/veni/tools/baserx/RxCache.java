package com.veni.tools.baserx;

import android.content.Context;

import com.veni.tools.ACache;

import java.io.Serializable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 *      处理服务器数据的缓存
 */
//################################使用例子#############################
/*
Observable<LoginData> fromNetwork = HttpManager.getDefault()
        .login(phone, password)
        .compose(RxHelper.handleResult());

        RxCache.load(context,cacheKey,1000*60*30,fromNetwork,false)
        .subscribe(new RxSubscribe<LoginData>(context, "登录中...") {
@Override
protected void _onNext(LoginData data) {
        showToast(R.string.login_success);
        }

@Override
protected void _onError(String message) {
        showToast(message);
        }
        });
 */


public class RxCache {
    /**
     *
     * @param context
     * @param cacheKey
     * @param expireTime
     * @param fromNetwork
     * @param forceRefresh
     * @param <T>
     * @return
     */
    public static <T> Observable<T> load(final Context context,
                                         final String cacheKey,
                                         final int expireTime,
                                         Observable<T> fromNetwork,
                                         boolean forceRefresh) {
        Observable<T> fromCache = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                //获取缓存
                T cache = (T) ACache.get(context).getAsObject(cacheKey);
                if (cache != null) {
                    subscriber.onNext(cache);
                } else {
                    subscriber.onComplete();
                }
            }

        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

        /**
         * 这里的fromNetwork 不需要指定Schedule,在handleRequest中已经变换了
         */
        fromNetwork = fromNetwork.map(new Function<T, T>() {
            @Override
            public T apply(T result) throws Exception {
                //保存缓存
                ACache.get(context).put(cacheKey, (Serializable) result, expireTime);
                return result;
            }

        });
        //强制刷新则返回接口数据
        if (forceRefresh) {
            return fromNetwork;
        } else {
            //优先返回缓存
            return Observable.concat(fromCache, fromNetwork).firstElement().toObservable();
        }
    }
}
