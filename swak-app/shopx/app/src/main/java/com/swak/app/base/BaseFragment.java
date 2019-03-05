package com.swak.app.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.veni.tools.base.BasePresenter;
import com.veni.tools.base.FragmentBase;
import com.veni.tools.base.TUtil;
import com.veni.tools.baserx.RxManager;
import com.veni.tools.interfaces.AntiShake;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:基类fragment
 */
public abstract class BaseFragment<T extends BasePresenter> extends FragmentBase {

    protected View rootView;//根视图
    public T mPresenter;//Presenter 对象
    public RxManager mRxManager;//Rxjava管理
    private Unbinder unbinder;
    protected String TAG;
    protected AntiShake antiShake;//防止重复点击

    //获取布局文件
    protected abstract int getLayoutId();

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    // mPresenter.setVM(this);
    public abstract void initPresenter();

    //初始化view
    protected abstract void initView(Bundle savedInstanceState);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null && getLayoutId() != 0) {
            rootView = inflater.inflate(getLayoutId(), container, false);
            unbinder = ButterKnife.bind(this, rootView);
        }
        mRxManager = new RxManager();
        context = getContext();
        antiShake = new AntiShake();
        mPresenter = TUtil.getT(this, 0);
        if (mPresenter != null) {
            mPresenter.mContext = this.getActivity();
        }
        TAG = getClass().getSimpleName();
        initPresenter();
        initView(savedInstanceState);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        if (mRxManager != null) {
            mRxManager.clear();
        }
    }
}
