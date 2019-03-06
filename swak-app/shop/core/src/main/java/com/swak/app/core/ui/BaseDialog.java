/*
 *     Android基础开发个人积累、沉淀、封装、整理共通
 *     Copyright (c) 2016. 曾繁添 <zftlive@163.com>
 *     Github：https://github.com/zengfantian || http://git.oschina.net/zftlive
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.swak.app.core.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

/**
 * 对话框基类
 *
 * @author 曾繁添
 * @version 1.0
 *
 */
public abstract class BaseDialog extends Dialog {

    /**
     * 弹出对话框的Activity
     */
    protected Activity mActivity;

    /**
     * 弹出对话框的Activity
     */
    protected float mDensity = 1.0f;

    /**
     * 关闭事件回调
     */
    protected CancelListener mCancelListener;

    /**
     * 日志输出标识
     */
    protected final String TAG = this.getClass().getSimpleName();

    public BaseDialog(Activity mActivity) {
        super(mActivity);
        init(mActivity);
    }

    public BaseDialog(Activity mActivity, int theme) {
        super(mActivity, theme);
        init(mActivity);
    }

    private void init(Activity mActivity){
        this.mActivity = mActivity;
        if(null != mActivity){
            mDensity = mActivity.getResources().getDisplayMetrics().density;
        }
    }

    @Override
    public void show() {
        if (null == mActivity || mActivity.isFinishing() || isDestroyed(mActivity, false)) {
            return;
        }

        super.show();
    }

    /**
     * 复写关闭对话框方法，对外公开回调取消事件
     */
    @Override
    public void cancel() {
        if(null != mCancelListener){
            mCancelListener.onCancel();
        }
        super.cancel();
    }

    /**
     * 设置取消监听器
     *
     * @param mCancelListener
     */
    public void setCancelListener(CancelListener mCancelListener) {
        this.mCancelListener = mCancelListener;
    }

    /**
     * Dialog关闭取消事件
     */
    public interface CancelListener {
        void onCancel();
    }

    /**
     * activity.isDestroyed()的API兼容方法
     *
     * @param mActivity
     * @return API 17以下为defaultValue，其他情况返回a.isDestroyed()的返回结果
     */
    private boolean isDestroyed(Activity mActivity, boolean defaultValue) {
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            return mActivity.isDestroyed();
        }
        return defaultValue;
    }

    /**
     * 获取单位换算
     * @param dpValue
     * @return
     */
    protected int getPxValueOfDp(float dpValue){
        return (int) (mDensity * dpValue + 0.5f);
    }
}
