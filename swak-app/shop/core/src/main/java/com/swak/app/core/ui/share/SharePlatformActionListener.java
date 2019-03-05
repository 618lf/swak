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

import android.os.Handler;
import android.os.Message;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * 平台分享行为监听器
 *
 * @author 曾繁添
 * @version 1.0
 */
public abstract class SharePlatformActionListener implements PlatformActionListener {

    private Handler mShareHandle;
    public final static int ACTION_CANCEL = -1;
    public final static int ACTION_COMPLETE = 0;
    public final static int ACTION_ERROR = 1;

    public final static String RESULT_PLATFORM = "platform";
    public final static String RESULT_CODE = "code";
    public final static String RESULT_REQ_PARAM = "hashMap";
    public final static String RESULT_THROWABLE = "throwable";

    private static final String TAG = SharePlatformActionListener.class.getSimpleName();

    public SharePlatformActionListener(){
        mShareHandle = new MyShareHandler();
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        //组装消息
        HashMap<String,Object> msgData = new HashMap<String,Object>();
        msgData.put(RESULT_PLATFORM,platform);
        msgData.put(RESULT_CODE,i);
        msgData.put(RESULT_REQ_PARAM,hashMap);

        Message msg = mShareHandle.obtainMessage();
        msg.what = ACTION_COMPLETE;
        msg.obj = msgData;
        mShareHandle.sendMessage(msg);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        //组装消息
        HashMap<String,Object> msgData = new HashMap<String,Object>();
        msgData.put(RESULT_PLATFORM,platform);
        msgData.put(RESULT_CODE,i);
        msgData.put(RESULT_THROWABLE,throwable);

        Message msg = mShareHandle.obtainMessage();
        msg.what = ACTION_ERROR;
        msg.obj = msgData;
        mShareHandle.sendMessage(msg);
        if(null != throwable){
            throwable.printStackTrace();
        }
//        Logger.e(TAG, "错误码：" + i + " throwable-->" + (null != throwable ? throwable.getMessage() : ""));
    }

    @Override
    public void onCancel(Platform platform, int i) {
        //组装消息
        HashMap<String,Object> msgData = new HashMap<String,Object>();
        msgData.put(RESULT_PLATFORM,platform);
        msgData.put(RESULT_CODE,i);
        Message msg = mShareHandle.obtainMessage();
        msg.what = ACTION_CANCEL;
        msg.obj = msgData;
        mShareHandle.sendMessage(msg);

    }

    /**
     * 消息处理Handle
     */
    private class MyShareHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            HashMap<String,Object> msgData = (HashMap<String,Object>)msg.obj;
            Platform platform = (Platform)msgData.get(RESULT_PLATFORM);
            int code =  (int)msgData.get(RESULT_CODE);
            switch (msg.what){
                case ACTION_ERROR:
                    Throwable throwable = (Throwable)msgData.get(RESULT_THROWABLE);
                    onFailure(platform, code, throwable);
                    break;
                case ACTION_COMPLETE:
                    HashMap<String, Object> hashMap = (HashMap<String,Object>)msgData.get(RESULT_REQ_PARAM);
                    onSuccess(platform, code, hashMap);
                    break;
                case ACTION_CANCEL:
                    onShareCancel(platform,code);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 分享成功回调
     *
     * @param platform 分享平台
     * @param i 状态码
     * @param hashMap 请求的数据
     */
    public abstract void onSuccess(Platform platform, int i, HashMap<String, Object> hashMap);

    /**
     * 分享失败回调
     *
     * @param platform 分享平台
     * @param i 状态码
     * @param throwable 异常信息
     */
    public abstract void onFailure(Platform platform, int i, Throwable throwable);

    /**
     * 分享取消回调
     *
     * @param platform 分享平台
     * @param i 状态码
     */
    public abstract void onShareCancel(Platform platform, int i);

    /**
     * 分享面板点击回调
     *
     * @param platform 分享平台
     */
    public abstract void onItemClick(Platform platform);
}
