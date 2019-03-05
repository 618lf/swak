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

import java.io.Serializable;

/**
 * 分享平台bean
 *
 */
public class PlatformConfig implements Serializable {

    /**
     * 分享平台
     */
    private String mPlatformName = "";

    /**
     * id
     */
    private String mId = "1";


    /**
     * 九宫格排序顺序
     */
    private String mSortId = "1";

    /**
     * QQ、QQ空间平台接入的AppId
     */
    private String mAppId = "";

    /**
     * 分享平台接入的AppKey
     */
    private String mAppKey = "";


    /**
     * 分享平台接入的AppSecret
     */
    private String mAppSecret = "";

    /**
     * 重定向URL
     */
    private String mRedirectUrl = "http://jr.jd.com/";

    /**
     * 是否通过客户端分享
     */
    private boolean mShareByAppClient = true;

    /**
     * 是否绕过审核权限-微信分享，默认false
     */
    private boolean mBypassApproval = false;

    /**
     * 是否有效
     */
    private boolean mEnable = true;

    public PlatformConfig() {

    }

    public PlatformConfig(String mPlatformName, String mId, String mSortId, String mAppId, String mAppKey, String mAppSecret, String mRedirectUrl) {
        this.mPlatformName = mPlatformName;
        this.mId = mId;
        this.mSortId = mSortId;
        this.mAppId = mAppId;
        this.mAppKey = mAppKey;
        this.mAppSecret = mAppSecret;
        this.mRedirectUrl = mRedirectUrl;
    }

    public PlatformConfig(String mPlatformName, String mId, String mSortId, String mAppKey, String mAppSecret, String mRedirectUrl) {
        this.mPlatformName = mPlatformName;
        this.mId = mId;
        this.mSortId = mSortId;
        this.mAppKey = mAppKey;
        this.mAppSecret = mAppSecret;
        this.mRedirectUrl = mRedirectUrl;
    }

    public PlatformConfig(String mPlatformName, String mId, String mSortId, String mAppId, String mAppKey, String mAppSecret, String mRedirectUrl, boolean mShareByAppClient) {
        this(mPlatformName,mId,mSortId,mAppId,mAppKey,mAppSecret,mRedirectUrl,mShareByAppClient,true);
    }

    public PlatformConfig(String mPlatformName, String mId, String mSortId, String mAppId, String mAppKey, String mAppSecret, String mRedirectUrl, boolean mShareByAppClient, boolean mEnable) {
        this(mPlatformName,mId,mSortId,mAppId,mAppKey,mAppSecret,mRedirectUrl,mShareByAppClient,false,mEnable);
    }

    public PlatformConfig(String mPlatformName, String mId, String mSortId, String mAppId, String mAppKey, String mAppSecret, String mRedirectUrl, boolean mShareByAppClient, boolean mBypassApproval, boolean mEnable) {
        this.mPlatformName = mPlatformName;
        this.mId = mId;
        this.mSortId = mSortId;
        this.mAppId = mAppId;
        this.mAppKey = mAppKey;
        this.mAppSecret = mAppSecret;
        this.mRedirectUrl = mRedirectUrl;
        this.mShareByAppClient = mShareByAppClient;
        this.mBypassApproval = mBypassApproval;
        this.mEnable = mEnable;
    }

    public String getmPlatformName() {
        return mPlatformName;
    }

    public void setmPlatformName(String mPlatformName) {
        this.mPlatformName = mPlatformName;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmSortId() {
        return mSortId;
    }

    public void setmSortId(String mSortId) {
        this.mSortId = mSortId;
    }

    public String getmAppId() {
        return mAppId;
    }

    public void setmAppId(String mAppId) {
        this.mAppId = mAppId;
    }

    public String getmAppKey() {
        return mAppKey;
    }

    public void setmAppKey(String mAppKey) {
        this.mAppKey = mAppKey;
    }

    public String getmAppSecret() {
        return mAppSecret;
    }

    public void setmAppSecret(String mAppSecret) {
        this.mAppSecret = mAppSecret;
    }

    public String getmRedirectUrl() {
        return mRedirectUrl;
    }

    public void setmRedirectUrl(String mRedirectUrl) {
        this.mRedirectUrl = mRedirectUrl;
    }

    public boolean ismShareByAppClient() {
        return mShareByAppClient;
    }

    public void setmShareByAppClient(boolean mShareByAppClient) {
        this.mShareByAppClient = mShareByAppClient;
    }

    public boolean ismBypassApproval() {
        return mBypassApproval;
    }

    public void setmBypassApproval(boolean mBypassApproval) {
        this.mBypassApproval = mBypassApproval;
    }

    public boolean ismEnable() {
        return mEnable;
    }

    public void setmEnable(boolean mEnable) {
        this.mEnable = mEnable;
    }
}
