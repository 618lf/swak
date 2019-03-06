package com.veni.tools.base;

import android.content.Context;

import com.veni.tools.baserx.RxManager;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * 基类presenter
 */
public abstract class BasePresenter<T>{
    public Context mContext;
    public T mView;
    public RxManager mRxManage = new RxManager();

    public void setVM(T v) {
        this.mView = v;
    }
    public void onDestroy() {
        mRxManage.clear();
    }
}
