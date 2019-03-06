package com.swak.app.core;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * Activity 基类
 */
public interface IActivity extends IConstant{

    /**
     * 在setContentView之前的一些window配置
     *
     * @param savedInstanceState
     */
    void config(Bundle savedInstanceState);

    /**
     * 绑定渲染视图的布局文件
     *
     * @return 布局文件资源id
     */
    int bindLayout();

    /**
     * 绑定渲染View
     *
     * @return
     */
    View bindView();

    /**
     * 初始化界面参数
     *
     * @param parms
     */
    void initParams(Bundle parms);

    /**
     * 初始化控件
     */
    void initView(final View view);

    /**
     * 业务处理操作（onCreate方法中调用）
     *
     * @param mContext 当前Activity对象
     */
    void doBusiness(Context mContext);
}
