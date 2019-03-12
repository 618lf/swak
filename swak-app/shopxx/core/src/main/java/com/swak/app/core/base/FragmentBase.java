package com.swak.app.core.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.swak.app.core.tools.AntiShake;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * java类作用描述
 *
 * @Author: 李锋
 * @Date: 2019/3/9 16:33
 * @Version: 1.0
 */
public abstract class FragmentBase extends Fragment {
    protected View rootView;//根视图
    public Context context;
    private Unbinder unbinder;
    protected String TAG;
    protected AntiShake antiShake;//防止重复点击

    /*********************子类实现 -- start *****************************/
    //获取布局文件
    protected abstract int getLayoutId();

    //初始化view
    protected abstract void initView(Bundle savedInstanceState);

    /*********************子类实现 -- end *****************************/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null && getLayoutId() != 0) {
            rootView = inflater.inflate(getLayoutId(), container, false);
            unbinder = ButterKnife.bind(this, rootView);
        }
        antiShake = new AntiShake();
        TAG = getClass().getSimpleName();
        _initView(savedInstanceState);
        return rootView;
    }

    // 方便子类确定初始化顺序
    protected void _initView(Bundle savedInstanceState) {
        initView(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
