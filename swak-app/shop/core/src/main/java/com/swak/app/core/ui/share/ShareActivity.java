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

package com.swak.app.core.ui.share;

import android.os.Bundle;

import com.swak.app.core.ui.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;

/**
 * 原生带分享功能的Activity
 *
 * @author 曾繁添
 * @version 1.0
 */
public abstract class ShareActivity extends BaseActivity {

    /**
     * 分享辅助工具类
     */
    protected ShareSDKHelper mShareSDKHelper;

    @Override
    public void config(Bundle savedInstanceState) {
        super.config(savedInstanceState);
        initShare();
    }

    /**
     * 初始化分享，具体分享平台实现类扩展
     */
    protected void initShare() {
        mShareSDKHelper = ShareSDKHelper.getInstance();
        mShareSDKHelper.initSDK(this);
        mShareSDKHelper.setShareListener(new BasicShareListener());
    }
    /**
     * 打开分享对话框
     *
     * @param imageURL   分享图片地址
     * @param title      分享标题
     * @param strContent 分享内容
     * @param linkURL    点击打开的链接地址
     */
    protected  void openSharePannel(String imageURL, String title, String strContent, String linkURL) {
        if (null == mShareSDKHelper) return;
        mShareSDKHelper.openSharePannel(this, imageURL, title, strContent, linkURL);
    }

    /**
     * 打开分享对话框
     *
     * @param imageURL      分享图片地址
     * @param title         分享标题
     * @param strContent    分享内容
     * @param linkURL       点击打开的链接地址
     * @param sharePlatform 分享平台,见IBaseConstant.PLATFORM_前缀对应的平台枚举，默认全平台
     */
    public void openSharePannel(String imageURL, String title, String strContent, String linkURL, ArrayList<String> sharePlatform) {
        if (null == mShareSDKHelper) return;
        mShareSDKHelper.openSharePannel(this, imageURL, title, strContent, linkURL, sharePlatform);
    }

    /**
     * 打开分享对话框
     *
     * @param imageResId    分享图片地址，工程本地地址
     * @param title         分享标题
     * @param strContent    分享内容
     * @param linkURL       点击打开的链接地址
     * @param sharePlatform 分享平台,见IBaseConstant.PLATFORM_前缀对应的平台枚举，默认全平台
     */
    public void openSharePannel(int imageResId, String title, String strContent, String linkURL, ArrayList<String> sharePlatform) {
        if (null == mShareSDKHelper) return;
        mShareSDKHelper.openSharePannel(this, imageResId, title, strContent, linkURL, sharePlatform);
    }

    /**
     * 分享面板点击回调
     *
     * @param platform 点击分享平台
     */
    protected abstract void onShareItemClick(String platform);

    /**
     * 分享成功回调
     *
     * @param platform 分享平台
     * @param code     返回状态码
     * @param hashMap  额外参数
     */
    protected abstract void onShareSuccess(String platform, int code, HashMap<String, Object> hashMap);

    /**
     * 分享失败回调
     *
     * @param platform  分享平台
     * @param code      返回状态码
     * @param throwable 异常
     */
    protected abstract void onShareFailure(String platform, int code, Throwable throwable);

    /**
     * 取消分享回调
     *
     * @param platform 分享平台
     * @param code     返回状态码
     */
    protected abstract void onShareCancel(String platform, int code);

    /**
     * 分享回调监听器
     */
    protected class BasicShareListener extends SharePlatformActionListener {

        @Override
        public void onItemClick(Platform platform) {
            onShareItemClick(platform.getName());
        }

        @Override
        public void onSuccess(Platform platform, int i, HashMap<String, Object> hashMap) {
            onShareSuccess(platform.getName(), i, hashMap);
        }

        @Override
        public void onFailure(Platform platform, int i, Throwable throwable) {
            onShareFailure(platform.getName(), i, throwable);
        }

        @Override
        public void onShareCancel(Platform platform, int i) {
            ShareActivity.this.onShareCancel(platform.getName(), i);
        }
    }
}
