package com.swak.app.core.net;

import android.content.Context;

import com.swak.app.core.R;
import com.swak.app.core.base.ActivityBase;
import com.swak.app.core.tools.DialogBuilder;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.Disposable;

public abstract class LoadingObserver<E> extends BaseObserver<E> {
    private DialogBuilder dialog;
    private WeakReference<Context> wrContext;

    /**
     * 显示loading的构造函数
     */
    public LoadingObserver(Context context, boolean showLoading, boolean showErrorTip) {
        super(showErrorTip);
        if (showLoading) {
            wrContext = new WeakReference<>(context);
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        showLoading();
        super.onSubscribe(d);
    }

    @Override
    public void onNext(E o) {
        hideLoading();
        super.onNext(o);
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        super.onError(e);
    }

    /**
     * 显示loading
     */
    private void showLoading() {
        ActivityBase context = ((ActivityBase) wrContext.get());
        dialog = context.creatDialogBuilder().setDialog_message("loading...")
                .setLoadingView(R.color.deeppink)
                .setCancelable(false)
                .setDrawableseat(2)
                .builder().show();
    }

    /**
     * 取消loading
     */
    private void hideLoading() {
        if (dialog != null) {
            dialog.dismissDialog();
            dialog = null;
        }
    }
}
