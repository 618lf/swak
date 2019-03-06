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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.swak.app.core.IActivity;
import com.swak.app.core.R;
import com.swak.app.core.model.DTO;


import java.io.Serializable;

/**
 * 基本的操作共通抽取
 *
 * @author 曾繁添
 * @version 1.0
 */
public class Operation {

    /**
     * 激活Activity组件意图
     **/
    private Intent mIntent = new Intent();
    /*** 上下文 **/
    private Activity mContext = null;
    /*** 整个应用Applicaiton **/
    private BaseApplication application = null;
    /***Activity之间数据传输数据对象Key**/
    private String ACTIVITY_DTO_KEY = "ACTIVITY_DTO_KEY";
    /**
     * 日志输出标志
     **/
    private final static String TAG = Operation.class.getSimpleName();

    public Operation(Activity mContext) {
        this.mContext = mContext;
        if (mContext.getApplicationContext() instanceof BaseApplication) {
            application = (BaseApplication) this.mContext.getApplicationContext();
        }
    }

    /**
     * 跳转Activity
     *
     * @param activity 需要跳转至的Activity
     */
    public void forward(Class<? extends Activity> activity) {
        forward(activity.getName());
    }

    /**
     * 跳转Activity
     *
     * @param className 需要跳转至的Activity类全路径名称
     */
    public void forward(String className) {
        forward(className, IActivity.NONE);
    }

    /**
     * 跳转Activity
     *
     * @param activity  需要跳转至的Activity
     * @param animaType 动画类型IActivity.LEFT_RIGHT/TOP_BOTTOM/FADE_IN_OUT
     */
    public void forward(Class<? extends Activity> activity, int animaType) {
        forward(activity.getName(), animaType);
    }

    /**
     * 跳转Activity
     *
     * @param className 需要跳转至的Activity
     * @param animaType 动画类型IActivity.LEFT_RIGHT/TOP_BOTTOM/FADE_IN_OUT
     */
    public void forward(String className, int animaType) {
        mIntent.setClassName(mContext, className);
        mIntent.putExtra(IActivity.ANIMATION_TYPE, animaType);
        mContext.startActivity(mIntent);

        switch (animaType) {
            case IActivity.LEFT_RIGHT:
                mContext.overridePendingTransition(R.anim.anl_slide_right_in, R.anim.anl_slide_left_out);
                break;
            case IActivity.TOP_BOTTOM:
                mContext.overridePendingTransition(R.anim.anl_push_bottom_in, R.anim.anl_push_up_out);
                break;
            case IActivity.FADE_IN_OUT:
                mContext.overridePendingTransition(R.anim.anl_fade_in, R.anim.anl_fade_out);
                break;
            default:
                break;
        }
    }

    /**
     * 跳转Activity
     *
     * @param className 需要跳转至的Activity类全路径名称
     */
    public void forwardForResult(int requestCode, String className) {
        forwardForResult(requestCode, className, IActivity.NONE);
    }

    /**
     * 跳转Activity
     *
     * @param activity 需要跳转至的Activity
     */
    public void forwardForResult(int requestCode, Class<? extends Activity> activity) {
        forwardForResult(requestCode, activity, IActivity.NONE);
    }

    /**
     * 跳转Activity
     *
     * @param requestCode 请求码
     * @param activity    需要跳转至的Activity
     * @param animaType   动画类型IActivity.LEFT_RIGHT/TOP_BOTTOM/FADE_IN_OUT
     */
    public void forwardForResult(int requestCode, Class<? extends Activity> activity, int animaType) {
        forwardForResult(requestCode, activity.getName(), animaType);
    }

    /**
     * 跳转Activity
     *
     * @param requestCode 请求码
     * @param className   需要跳转至的Activity
     * @param animaType   动画类型IActivity.LEFT_RIGHT/TOP_BOTTOM/FADE_IN_OUT
     */
    public void forwardForResult(int requestCode, String className, int animaType) {
        mIntent.setClassName(mContext, className);
        mIntent.putExtra(IActivity.ANIMATION_TYPE, animaType);
        mContext.startActivityForResult(mIntent, requestCode);

        switch (animaType) {
            case IActivity.LEFT_RIGHT:
                mContext.overridePendingTransition(R.anim.anl_slide_right_in, R.anim.anl_slide_left_out);
                break;
            case IActivity.TOP_BOTTOM:
                mContext.overridePendingTransition(R.anim.anl_push_bottom_in, R.anim.anl_push_up_out);
                break;
            case IActivity.FADE_IN_OUT:
                mContext.overridePendingTransition(R.anim.anl_fade_in, R.anim.anl_fade_out);
                break;
            default:
                break;
        }
    }

    /**
     * 设置传递参数
     *
     * @param value 数据传输对象
     */
    public void addParameter(DTO value) {
        mIntent.putExtra(ACTIVITY_DTO_KEY, value);
    }

    /**
     * 设置传递参数
     *
     * @param key   参数key
     * @param value 数据传输对象
     */
    public void addParameter(String key, DTO value) {
        mIntent.putExtra(key, value);
    }

    /**
     * 设置传递参数
     *
     * @param key   参数key
     * @param value 数据传输对象
     */
    public void addParameter(String key, Bundle value) {
        mIntent.putExtra(key, value);
    }

    /**
     * 设置传递参数
     *
     * @param key   参数key
     * @param value 数据传输对象
     */
    public void addParameter(String key, Serializable value) {
        mIntent.putExtra(key, value);
    }

    /**
     * 设置传递参数
     *
     * @param key   参数key
     * @param value 数据传输对象
     */
    public void addParameter(String key, String value) {
        mIntent.putExtra(key, value);
    }

    /**
     * 获取跳转时设置的参数
     *
     * @param key
     * @return
     */
    public Object getParameter(String key) {
        Bundle extras = mContext.getIntent().getExtras();
        if (null == extras) return null;

        return mContext.getIntent().getExtras().get(key);
    }

    /**
     * 获取跳转参数集合
     *
     * @return
     */
    public DTO getParameters() {
        DTO parms = (DTO) mContext.getIntent().getExtras().getSerializable(ACTIVITY_DTO_KEY);
        return parms;
    }

    /**
     * 设置全局Application传递参数
     *
     * @param strKey 参数key
     * @param value  数据传输对象
     */
    public void addGloableAttribute(String strKey, Object value) {
        if (null != application) {
            application.assignData(strKey, value);
        }
    }

    /**
     * 获取跳转时设置的参数
     *
     * @param strKey
     * @return
     */
    public Object getGloableAttribute(String strKey) {
        if (null != application) {
            return application.gainData(strKey);
        }
        return null;
    }

    /**
     * 弹出等待对话框
     *
     * @param message 提示信息
     */
    public void showLoading(String message) {
        Alerts.loading(mContext, message);
    }

    /**
     * 弹出等待对话框
     *
     * @param message  提示信息
     * @param listener 按键监听器
     */
    public void showLoading(String message, Alerts.ILoadingOnKeyListener listener) {
        Alerts.loading(mContext, message, listener);
    }

    /**
     * 更新等待对话框显示文本
     *
     * @param message 需要更新的文本内容
     */
    public void updateLoadingText(String message) {
        Alerts.updateProgressText(message);
    }

    /**
     * 关闭等待对话框
     */
    public void closeLoading() {
        Alerts.closeLoading();
    }

    /**
     * 获取资源文件id
     *
     * @param mContext 上下文
     * @param resType  资源类型（drawable/string/layout/style/dimen/color/array等）
     * @param resName  资源文件名称
     * @return
     */
    public int gainResId(Context mContext, String resType, String resName) {
        int result = -1;
        try {
            String packageName = mContext.getPackageName();
            result = mContext.getResources().getIdentifier(resName, resType, packageName);
        } catch (Exception e) {
            result = -1;
            Log.e(TAG, "获取资源文件失败，原因：" + e.getMessage());
        }

        return result;
    }
}
