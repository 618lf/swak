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

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.system.text.ShortMessage;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * ShareSDK分享帮助类
 *
 * @author 曾繁添
 * @version 1.0
 */
public class ShareSDKHelper {

    private SharePlatformPannel mShareDialog;

    private static ShareSDKHelper instance = null;

    private static SharePlatformActionListener mShareListener;

    private ShareSDKHelper(){

    }

    public static ShareSDKHelper getInstance() {
//        if (instance == null) {
//            synchronized (ShareSDKHelper.class){
        instance = new ShareSDKHelper();
//            }
//        }
        return instance;
    }

    /**
     * 初始化SDK，建立分享统计资源通道
     *
     * @param mContext
     */
    public static void initSDK(Context mContext){
        ShareSDK.initSDK(mContext);
    }

    /**
     * 初始化SDK，建立分享统计资源通道
     *
     * @param mContext
     */
    public static void initSDK(Context mContext, String appKey){
        initSDK(mContext, appKey, false);
    }

    /**
     * 初始化SDK，建立分享统计资源通道
     *
     * @param mContext
     */
    public static void initSDK(Context mContext, String appKey, boolean enableStatistics){
        ShareSDK.initSDK(mContext, appKey, enableStatistics);
    }

    /**
     * 断开shareSDK相关连接通道，断开之后不能使用分享API，需要再次initSDK
     */
    public static void stopSDK(){
        ShareSDK.stopSDK();
        instance = null;
        mShareListener = null;
    }

    /**
     * 注册分享平台,在initSDK方法之后调用，Application注册即可
     *
     * @param config 分享平台接入相关配置
     */
    public static void registerSharePlatform(PlatformConfig config){
        if(null == config){
            return;
        }

        //代码动态注册分享平台，无需在asset目录放置ShareSDK.xml
        HashMap<String,Object> sharePlatform = new HashMap<String,Object>();
        sharePlatform.put("Id",config.getmId());
        sharePlatform.put("SortId",config.getmSortId());
        if(!TextUtils.isEmpty(config.getmAppId())){
            sharePlatform.put("AppId",config.getmAppId());
        }
        if(!TextUtils.isEmpty(config.getmAppKey())){
            sharePlatform.put("AppKey",config.getmAppKey());
        }
        if(!TextUtils.isEmpty(config.getmAppSecret())){
            sharePlatform.put("AppSecret",config.getmAppSecret());
        }
        if(!TextUtils.isEmpty(config.getmRedirectUrl())){
            sharePlatform.put("RedirectUrl",config.getmRedirectUrl());
        }
        sharePlatform.put("BypassApproval", String.valueOf(config.ismBypassApproval()));
        sharePlatform.put("ShareByAppClient", String.valueOf(config.ismShareByAppClient()));
        sharePlatform.put("Enable", String.valueOf(config.ismEnable()));
        ShareSDK.setPlatformDevInfo(config.getmPlatformName(), sharePlatform);
    }

    /**
     * 分享新浪微博，如果imagePath和imageUrl同时存在，imageUrl将被忽略
     * @param text 分享文本
     * @param imagePath 分享图片的本地路径
     * @param imageURL 分享图片的网络地址
     */
    public void shareSinaWeibo(String text, String imagePath, String imageURL){
        shareSinaWeibo(text, imagePath, imageURL, null);
    }

    /**
     * 分享新浪微博链接，如果imagePath和imageUrl同时存在，imageUrl将被忽略
     * @param text 分享文本
     * @param imagePath 分享图片的本地路径
     * @param imageURL 分享图片的网络地址
     * @param url 链接地址
     */
    public void shareSinaWeibo(String text, String imagePath, String imageURL, String url){
        SinaWeibo.ShareParams shareParams = new SinaWeibo.ShareParams();
        shareParams.setText(text);
        if(!TextUtils.isEmpty(imagePath)){
            shareParams.setImagePath(imagePath);
        }
        if(!TextUtils.isEmpty(imageURL)){
            shareParams.setImageUrl(imageURL);
        }
        if(!TextUtils.isEmpty(url)){
            shareParams.setUrl(url);
        }
        shareSinaWeibo(shareParams);
    }

    /**
     *
     * 分享新浪微博
     * 新浪微博支持分享文字、本地图片、网络图片和经纬度信息 新浪微博使用客户端分享不会正确回调<br>
     * 参数说明<br>
     * text：小于2000字<br>
     * image：图片最大5M，仅支持JPEG、GIF、PNG格式<br>
     * latitude：有效范围:-90.0到+90.0，+表示北纬<br>
     * longitude：有效范围：-180.0到+180.0，+表示东经<br>
     * 如果imagePath和imageUrl同时存在，imageUrl将被忽略。<br>
     *
     * @param shareParams 分享内容
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-0
     */
    public void shareSinaWeibo(Platform.ShareParams shareParams){
        share(SinaWeibo.NAME, shareParams);
    }

    /**
     *
     * 分享微信好友，shareParams一定要设置分享类型
     *
     * @param shareParams 分享内容
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWeChatFriend(Platform.ShareParams shareParams){
        share(Wechat.NAME, shareParams);
    }

    /**
     *
     * 分享微信好友，分享文本
     *
     * @param title 分享标题
     * @param text 分享文本
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWeChatFriendText(String title, String text){
        Wechat.ShareParams shareParams = new Wechat.ShareParams();
        shareParams.setShareType(Platform.SHARE_TEXT);
        shareParams.setTitle(title);
        shareParams.setText(text);
        share(Wechat.NAME, shareParams);
    }

    /**
     *
     * 分享微信朋友圈，分享文本
     *
     * @param title 分享标题
     * @param text 分享文本
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWechatMomentsText(String title, String text){
        Wechat.ShareParams shareParams = new Wechat.ShareParams();
        shareParams.setShareType(Platform.SHARE_TEXT);
        shareParams.setTitle(title);
        shareParams.setText(text);
        share(WechatMoments.NAME, shareParams);
    }

    /**
     *
     * 分享微信好友，分享图片,imagePath、imageURL、imageData三者任选一
     *
     * @param title 分享标题,512Bytes以内
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWeChatFriendImage(String title, String imagePath, String imageURL, Bitmap imageData){
        shareWeChatForImage(Wechat.NAME, title, imagePath, imageURL, imageData);
    }

    /**
     *
     * 分享微信朋友圈，分享图片,imagePath、imageURL、imageData三者任选一
     *
     * @param title 分享标题,512Bytes以内
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWechatMomentsImage(String title, String imagePath, String imageURL, Bitmap imageData){
        shareWeChatForImage(WechatMoments.NAME, title, imagePath, imageURL, imageData);
    }

    /**
     *
     * 分享微信好友，分享音乐,imagePath、imageURL、imageData三者任选一
     *
     * @param title 分享标题,512Bytes以内
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @param musicUrl 音乐地址
     * @param url 消息点击后打开的页面
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWeChatFriendMusic(String title, String imagePath, String imageURL, Bitmap imageData, String musicUrl, String url){
        shareWechatForMusic(Wechat.NAME, title, imagePath, imageURL, imageData, musicUrl, url);
    }

    /**
     *
     * 分享微信朋友圈，分享音乐,imagePath、imageURL、imageData三者任选一
     *
     * @param title 分享标题,512Bytes以内
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @param musicUrl 音乐地址
     * @param url 消息点击后打开的页面
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWechatMomentsMusic(String title, String imagePath, String imageURL, Bitmap imageData, String musicUrl, String url){
        shareWechatForMusic(WechatMoments.NAME, title, imagePath, imageURL, imageData, musicUrl, url);
    }

    /**
     *
     * 分享微信好友，分享视频,imagePath、imageURL、imageData三者任选一
     *
     * @param title 分享标题,512Bytes以内
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @param url 视频网页地址
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWeChatFriendVideo(String title, String imagePath, String imageURL, Bitmap imageData, String url){
        shareWechatForVideo(Wechat.NAME, title, imagePath, imageURL, imageData, url);
    }

    /**
     *
     * 分享微信朋友圈，分享视频,imagePath、imageURL、imageData三者任选一
     *
     * @param title 分享标题,512Bytes以内
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @param url 视频网页地址
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWechatMomentsVideo(String title, String imagePath, String imageURL, Bitmap imageData, String url){
        shareWechatForVideo(WechatMoments.NAME, title, imagePath, imageURL, imageData, url);
    }

    /**
     *
     * 分享微信好友，分享网页,imagePath、imageURL、imageData三者任选一
     *
     * @param title 分享标题,512Bytes以内
     * @param comment 评论
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @param url 视频网页地址
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWeChatFriendWebPage(String title, String comment, String imagePath, String imageURL, Bitmap imageData, String url){
        shareWechatForWebPage(Wechat.NAME, title, comment, imagePath, imageURL, imageData, url);
    }

    /**
     *
     * 分享微信朋友圈，分享网页,imagePath、imageURL、imageData三者任选一
     *
     * @param title 分享标题,512Bytes以内
     * @param comment 评论
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @param url 视频网页地址
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWechatMomentsWebPage(String title, String comment, String imagePath, String imageURL, Bitmap imageData, String url){
        shareWechatForWebPage(WechatMoments.NAME, title, comment, imagePath, imageURL, imageData, url);
    }

    /**
     *
     * 分享微信好友，分享图片,imagePath、imageURL、imageData三者任选一
     * @param platform 分享平台，Wechat.NAME 或者 WechatMoments.NAME
     * @param title 分享标题,512Bytes以内
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    private void shareWeChatForImage(String platform, String title, String imagePath, String imageURL, Bitmap imageData){
        Wechat.ShareParams shareParams = new Wechat.ShareParams();
        shareParams.setShareType(Platform.SHARE_IMAGE);
        shareParams.setTitle(title);
        if(!TextUtils.isEmpty(imagePath)){
            shareParams.setImagePath(imagePath);
        }
        if(!TextUtils.isEmpty(imageURL)){
            shareParams.setImageUrl(imageURL);
        }
        if(null != imageData){
            shareParams.setImageData(imageData);
        }
        share(platform, shareParams);
    }

    /**
     *
     * 分享微信好友、朋友圈，音乐类型数据,imagePath、imageURL、imageData三者任选一
     * @param platform 分享平台，Wechat.NAME 或者 WechatMoments.NAME
     * @param title 分享标题,512Bytes以内
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @param url 视频网页地址
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    private void shareWechatForMusic(String platform, String title, String imagePath, String imageURL, Bitmap imageData, String musicUrl, String url){
        Wechat.ShareParams shareParams = new Wechat.ShareParams();
        shareParams.setShareType(Platform.SHARE_MUSIC);
        shareParams.setTitle(title);
        if(!TextUtils.isEmpty(imagePath)){
            shareParams.setImagePath(imagePath);
        }
        if(!TextUtils.isEmpty(imageURL)){
            shareParams.setImageUrl(imageURL);
        }
        if(null != imageData){
            shareParams.setImageData(imageData);
        }
        shareParams.setMusicUrl(musicUrl);
        shareParams.setUrl(url);
        share(platform, shareParams);
    }

    /**
     *
     * 分享微信好友、朋友圈，视频类型数据,imagePath、imageURL、imageData三者任选一
     * @param platform 分享平台，Wechat.NAME 或者 WechatMoments.NAME
     * @param title 分享标题,512Bytes以内
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @param url 视频网页地址
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    private void shareWechatForVideo(String platform, String title, String imagePath, String imageURL, Bitmap imageData, String url){
        Platform.ShareParams shareParams = new Platform.ShareParams();
        shareParams.setShareType(Platform.SHARE_VIDEO);
        shareParams.setTitle(title);
        if(!TextUtils.isEmpty(imagePath)){
            shareParams.setImagePath(imagePath);
        }
        if(!TextUtils.isEmpty(imageURL)){
            shareParams.setImageUrl(imageURL);
        }
        if(null != imageData){
            shareParams.setImageData(imageData);
        }
        shareParams.setUrl(url);
        share(platform, shareParams);
    }

    /**
     *
     * 分享微信好友、朋友圈，网页类型数据,imagePath、imageURL、imageData三者任选一
     * @param platform 分享平台，Wechat.NAME 或者 WechatMoments.NAME
     * @param title 分享标题,512Bytes以内
     * @param comment 评论
     * @param imagePath 分享图片本地地址,10M以内(传递的imagePath路径不能超过10KB)
     * @param imageURL 分享图片的网络地址,10KB以内
     * @param imageData 分享图片bitmap,10M以内
     * @param url 视频网页地址
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    private void shareWechatForWebPage(String platform, String title, String comment, String imagePath, String imageURL, Bitmap imageData, String url){
        Platform.ShareParams shareParams = new Platform.ShareParams();
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        shareParams.setTitle(title);
        shareParams.setText(comment);
        shareParams.setComment(comment);
        if(!TextUtils.isEmpty(imagePath)){
            shareParams.setImagePath(imagePath);
        }
        if(!TextUtils.isEmpty(imageURL)){
            shareParams.setImageUrl(imageURL);
        }
        if(null != imageData){
            shareParams.setImageData(imageData);
        }
        shareParams.setUrl(url);
        share(platform, shareParams);
    }

    /**
     *
     * 分享微信朋友圈，shareParams一定要设置分享类型
     *
     * @param shareParams 分享内容
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-3
     */
    public void shareWechatMoments(Platform.ShareParams shareParams){
        share(WechatMoments.NAME, shareParams);
    }

    /**
     *
     * QQ分享支持图文和音乐分享 参数说明 title：最多30个字符 text：最多40个字符
     * QQ分享图文和音乐，在PC版本的QQ上可能只看到一条连接，因为PC版本的QQ只会对其白名单的连接作截图，如果不在此名单中，则只是显示连接而已. 如果只分享图片在PC端看不到图片的，只会显示null，在手机端会显示图片和null字段。
     * @param shareParams 分享内容
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-4
     */
    public void shareQQ(Platform.ShareParams shareParams){
        shareParams.setSite("京东金融");
        share(QQ.NAME, shareParams);
    }

    /**
     * QQ分享图片，imagePath、imageURL二者任选一
     *
     * @param imagePath 本地图片
     * @param imageURL 网络图片
     */
    public void shareQQImage(String imagePath, String imageURL){
        QQ.ShareParams shareParams = new QQ.ShareParams();
        shareParams.setShareType(Platform.SHARE_IMAGE);
        if(!TextUtils.isEmpty(imagePath)){
            shareParams.setImagePath(imagePath);
        }
        if(!TextUtils.isEmpty(imageURL)){
            shareParams.setImageUrl(imageURL);
        }
        shareQQ(shareParams);
    }

    /**
     * QQ分享链接，imagePath、imageURL二者任选一
     * @param title 标题，最多200个字符
     * @param titleUrl 点击标题跳转链接
     * @param text 分享文本内容，最多600个字符
     * @param imagePath 本地图片地址
     * @param imageURL 网络图片地址
     */
    public void shareQQLink(String title, String titleUrl, String text, String imagePath, String imageURL){
        QQ.ShareParams shareParams = new QQ.ShareParams();
        shareParams.setShareType(Platform.SHARE_WEBPAGE);
        shareParams.setTitle(title);
        shareParams.setTitleUrl(titleUrl);
        shareParams.setText(text);
        if(!TextUtils.isEmpty(imagePath)){
            shareParams.setImagePath(imagePath);
        }
        if(!TextUtils.isEmpty(imageURL)){
            shareParams.setImageUrl(imageURL);
        }
        shareQQ(shareParams);
    }

    /**
     * QQ分享音频，imagePath、imageURL二者任选一
     * @param title 标题，最多200个字符
     * @param titleUrl 点击标题跳转链接
     * @param text 分享文本内容，最多600个字符
     * @param imagePath 本地图片地址
     * @param imageURL 网络图片地址
     * @param musicURL 音乐链接地址
     */
    public void shareQQMusic(String title, String titleUrl, String text, String imagePath, String imageURL, String musicURL){
        QQ.ShareParams shareParams = new QQ.ShareParams();
        shareParams.setShareType(Platform.SHARE_MUSIC);
        shareParams.setTitle(title);
        shareParams.setTitleUrl(titleUrl);
        shareParams.setText(text);
        if(!TextUtils.isEmpty(imagePath)){
            shareParams.setImagePath(imagePath);
        }
        if(!TextUtils.isEmpty(imageURL)){
            shareParams.setImageUrl(imageURL);
        }
        shareParams.setMusicUrl(musicURL);
        shareQQ(shareParams);
    }

    /**
     *
     * QQ空间支持分享文字和图文
     * 参数说明 <br>
     * title：最多200个字符 <br>
     * text：最多600个字符<br>
     * QQ空间分享时一定要携带title、titleUrl、site、siteUrl<br>
     * QQ空间本身不支持分享本地图片，因此如果想分享本地图片，图片会先上传到ShareSDK的文件服务器，得到连接以后才分享此链接。<br>
     * 由于本地图片更耗流量，因此imageUrl优先级高于imagePath。<br>
     * site是分享此内容的网站名称，仅在QQ空间使用；<br>
     * siteUrl是分享此内容的网站地址，仅在QQ空间使用；<br>
     *
     * @param shareParams 分享内容
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5%88%86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-2
     */
    public void shareQQZone(Platform.ShareParams shareParams){
        shareParams.setSite("京东金融");
        share(QZone.NAME, shareParams);
    }

    /**
     * 分享QQ空间文本
     *
     * @param title 标题，最多200个字符
     * @param titleUrl 点击标题跳转链接
     * @param text 分享文本内容，最多600个字符
     * @param site 分享此内容的网站名称
     * @param siteUrl 分享此内容的网站地址，仅在QQ空间使用
     */
    public void shareQQZoneText(String title, String titleUrl, String text, String site, String siteUrl){
        QZone.ShareParams shareParams = new QZone.ShareParams();
        shareParams.setShareType(Platform.SHARE_TEXT);
        shareParams.setTitle(title);
        shareParams.setTitleUrl(titleUrl);
        shareParams.setText(text);
        shareParams.setSite(site);
        shareParams.setSiteUrl(siteUrl);
        shareQQZone(shareParams);
    }

    /**
     * 分享QQ空间说说，imagePath、imageURL二者任选一
     *
     * @param text 分享文本内容，最多600个字符
     * @param imagePath 本地图片地址
     * @param imageURL 网络图片地址，QQ空间本身不支持分享本地图片，因此如果想分享本地图片，图片会先上传到ShareSDK的文件服务器，得到连接以后才分享此链接
     * @param site 分享此内容的网站名称
     * @param siteUrl 分享此内容的网站地址，仅在QQ空间使用
     */
    public void shareQQZoneSay(String text, String imagePath, String imageURL, String site, String siteUrl){
        QZone.ShareParams shareParams = new QZone.ShareParams();
        shareParams.setText(text);
        if(!TextUtils.isEmpty(imagePath)){
            shareParams.setImagePath(imagePath);
        }
        if(!TextUtils.isEmpty(imageURL)){
            shareParams.setImageUrl(imageURL);
        }
        shareParams.setSite(site);
        shareParams.setSiteUrl(siteUrl);
        shareQQZone(shareParams);
    }

    /**
     * 分享QQ空间图文，imagePath、imageURL二者任选一
     *
     * @param title 标题，最多200个字符
     * @param titleUrl 标题点击跳转地址
     * @param imagePath 本地图片地址
     * @param imageURL 网络图片地址，QQ空间本身不支持分享本地图片，因此如果想分享本地图片，图片会先上传到ShareSDK的文件服务器，得到连接以后才分享此链接
     * @param site 分享此内容的网站名称
     * @param siteUrl 分享此内容的网站地址，仅在QQ空间使用
     */
    public void shareQQZoneImageText(String title, String titleUrl, String text, String imagePath, String imageURL, String site, String siteUrl){
        QZone.ShareParams shareParams = new QZone.ShareParams();
        shareParams.setTitle(title);
        shareParams.setTitleUrl(titleUrl);
        shareParams.setText(text);
        if(!TextUtils.isEmpty(imagePath)){
            shareParams.setImagePath(imagePath);
        }
        if(!TextUtils.isEmpty(imageURL)){
            shareParams.setImageUrl(imageURL);
        }
        shareParams.setSite(site);
        shareParams.setSiteUrl(siteUrl);
        shareQQZone(shareParams);
    }

    /**
     * 分享短信
     * @param address 邮件地址
     * @param title 短信主题
     * @param text 短信内容
     */
    public void shareShortMessage(String address, String title, String text){
        shareShortMessage(address, title, text, "", "");
    }

    /**
     * 分享彩信，imagePath、imageURL二者任选一
     * @param address 邮件地址
     * @param title 短信主题
     * @param text 短信内容
     * @param imagePath 本地图片
     * @param imageURL 网络图片
     */
    public void shareShortMessage(String address, String title, String text, String imagePath, String imageURL){
        ShortMessage.ShareParams shareParams = new ShortMessage.ShareParams();
        if(!TextUtils.isEmpty(address)){
            shareParams.setAddress(address);
        }
        if(!TextUtils.isEmpty(title)){
            shareParams.setTitle(title);
        }
        if(!TextUtils.isEmpty(text)){
            shareParams.setText(text);
        }
        if(!TextUtils.isEmpty(imagePath)){
            shareParams.setImagePath(imagePath);
        }
        if(!TextUtils.isEmpty(imageURL)){
            shareParams.setImageUrl(imageURL);
        }
        shareShortMessage(shareParams);
    }

    /**
     * 信息分短信和彩信，如果设置了标题或者图片，会直接当作彩信发送。发送信息的时候使用手机的信息软件<br>
     * @param shareParams
     * @link http://wiki.mob.com/%E4%B8%8D%E5%90%8C%E5%B9%B3%E5%8F%B0%E5% %86%E4%BA%AB%E5%86%85%E5%AE%B9%E7%9A%84%E8%AF%A6%E7%BB%86%E8%AF%B4%E6%98%8E/#h1-10
     */
    public void shareShortMessage(Platform.ShareParams shareParams){
        share(ShortMessage.NAME, shareParams);
    }

    public void resetShareLinkURL(String shareURL){
        if(null != mShareDialog){
            mShareDialog.linkURL = shareURL;
        }
    }

    /**
     * 打开分享对话框
     * @param mContext 触发对话框的Activity
     * @param imageURL 分享图片地址
     * @param title 分享标题
     * @param strContent 分享内容
     * @param linkURL 点击打开的链接地址
     */
    public void openSharePannel(Activity mContext, String imageURL, String title, String strContent, String linkURL){
        if (mShareDialog == null || mShareDialog.getOwnerActivity() != mContext) {
            mShareDialog = new SharePlatformPannel(mContext,this,imageURL,title,strContent,linkURL,null);
            mShareDialog.setOwnerActivity(mContext);
            mShareDialog.setPlatformClickListener(mInnerShareListener);
        }
        if (mShareDialog.isShowing()) {
            mShareDialog.cancel();
        }
        mShareDialog.initShareData(null, imageURL, title, strContent,linkURL);
        mShareDialog.initSharePlatform(null);
        mShareDialog.show();
    }

    /**
     * 打开分享对话框
     * @param mContext 触发对话框的Activity
     * @param imageURL 分享图片地址
     * @param title 分享标题
     * @param strContent 分享内容
     * @param linkURL 点击打开的链接地址
     * @param sharePlatform 分享平台，v3.7.0增加js控制分享平台，默认为0123，微信朋友圈-0 微信好友-1 新浪微博-2 短信-3 QQ好友-4 QQ空间-5
     */
    public void openSharePannel(Activity mContext, String imageURL, String title, String strContent, String linkURL, ArrayList<String> sharePlatform){
        if (mShareDialog == null || mShareDialog.getOwnerActivity() != mContext) {
            mShareDialog = new SharePlatformPannel(mContext,this,imageURL,title,strContent,linkURL,sharePlatform);
            mShareDialog.setOwnerActivity(mContext);
            mShareDialog.setPlatformClickListener(mInnerShareListener);
        }
        if (mShareDialog.isShowing()) {
            mShareDialog.cancel();
        }
        mShareDialog.initShareData(null, imageURL, title, strContent,linkURL);
        mShareDialog.initSharePlatform(sharePlatform);
        mShareDialog.show();
    }

    /**
     * 打开分享对话框
     * @param mContext 触发对话框的Activity
     * @param imageResId 分享图片地址，工程本地地址
     * @param title 分享标题
     * @param strContent 分享内容
     * @param linkURL 点击打开的链接地址
     * @param sharePlatform 分享平台，v3.7.0增加js控制分享平台，默认为0123，微信朋友圈-0 微信好友-1 新浪微博-2 短信-3 QQ好友-4 QQ空间-5
     */
    public void openSharePannel(Activity mContext, int imageResId, String title, String strContent, String linkURL, ArrayList<String> sharePlatform){
        if (mShareDialog == null || mShareDialog.getOwnerActivity() != mContext) {
            mShareDialog = new SharePlatformPannel(mContext,this,imageResId,title,strContent,linkURL,sharePlatform);
            mShareDialog.setOwnerActivity(mContext);
            mShareDialog.setPlatformClickListener(mInnerShareListener);
        }
        if (mShareDialog.isShowing()) {
            mShareDialog.cancel();
        }
        Bitmap imageData = null;
        try{
            imageData = BitmapFactory.decodeResource(mContext.getResources(), imageResId);
        }catch (Exception e){
            e.printStackTrace();
        }
        mShareDialog.initShareData(imageData,null,title,strContent,linkURL);
        mShareDialog.initSharePlatform(sharePlatform);
        mShareDialog.show();
    }

    /**
     * 分享内容
     * @param platform 分享平台
     * @param shareParams 分享参数
     */
    private void share(String platform, Platform.ShareParams shareParams){
        if(TextUtils.isEmpty(platform)){
            return;
        }
        //获取平台对象
        Platform mPlatform = ShareSDK.getPlatform(platform);
        // 设置分享事件回调
        mPlatform.setPlatformActionListener(mInnerShareListener);
        // 执行分享
        mPlatform.share(shareParams);
    }

    /**
     * 分享监听器
     */
    private SharePlatformActionListener mInnerShareListener = new SharePlatformActionListener(){

        @Override
        public void onItemClick(Platform platform) {
//            Logger.d(TAG, "点击平台：" + platform.getName());
            if(null != mShareListener){
                mShareListener.onItemClick(platform);
            }
        }

        @Override
        public void onSuccess(Platform platform, int i, HashMap<String, Object> hashMap) {
//            Logger.d(TAG,"分享成功："+platform.getName());
            if(null != mShareListener){
                mShareListener.onSuccess(platform, i, hashMap);
            }
        }

        @Override
        public void onFailure(Platform platform, int i, Throwable throwable) {
//            Logger.d(TAG,"分享失败："+platform.getName());
            if(null != mShareListener){
                mShareListener.onFailure(platform, i, throwable);
            }
        }

        @Override
        public void onShareCancel(Platform platform, int i) {
//            Logger.d(TAG,"取消分享："+platform.getName());
            if(null != mShareListener){
                mShareListener.onShareCancel(platform, i);
            }
        }
    };

    /**
     * 是否安装微信
     * @param mContext
     * @return
     */
    public static boolean isInstallWeChat(Context mContext){
        return isInstalledApp(mContext,PACKAGE_WECHAT);
    }

    /**
     * 是否安装QQ
     * @param mContext
     * @return
     */
    public static boolean isInstallQQ(Context mContext){
        if(isInstalledApp(mContext,PACKAGE_QQ)){
            return true;
        }

        if(isInstalledApp(mContext,PACKAGE_QQ_HD)){
            return true;
        }

        if(isInstalledApp(mContext,PACKAGE_QQ_LITE)){
            return true;
        }

        if(isInstalledApp(mContext,PACKAGE_QQ_INTERNATIONAL)){
            return true;
        }

        return false;
    }

    /**
     * 是否安装QQ空间客户端（注意：QQ空间分享也必须走QQ客户端）
     * @param mContext
     * @return
     */
    public static boolean isInstallQQZone(Context mContext){
        if(isInstalledApp(mContext,PACKAGE_QQ_ZONE)){
            return true;
        }

        return false;
    }

    /**
     * 是否安装新浪微博
     * @param mContext
     * @return
     */
    public static boolean isInstallSina(Context mContext){

        if(isInstalledApp(mContext,PACKAGE_SINA)){
            return true;
        }

        if(isInstalledApp(mContext,PACKAGE_SINA_4G)){
            return true;
        }

        if(isInstalledApp(mContext,PACKAGE_SINA_HD)){
            return true;
        }

        return false;
    }

    /**
     * 设置分享回调监听器
     * @param mShareListener
     */
    public void setShareListener(SharePlatformActionListener mShareListener){
        this.mShareListener = mShareListener;
    }

    /**
     * 判断是否安装指定包名的APP
     *
     * @param mContext 上下文
     * @param packageName 包路径
     * @return
     */
    private static boolean isInstalledApp(Context mContext, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }

        try {
            ApplicationInfo info =
                    mContext.getPackageManager().getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static final String TAG = ShareSDKHelper.class.getSimpleName();

    /**
     * 新浪微博APP包名
     *
     * com.sina.weibo:新浪微博(一般手机版本)
     * com.sina.weibog3:新浪微博4G版
     * com.sina.weibotab:新浪微博HD版
     *
     */
    private final static String PACKAGE_SINA = "com.sina.weibo";

    private final static String PACKAGE_SINA_4G = "com.sina.weibog3";

    private final static String PACKAGE_SINA_HD = "com.sina.weibotab";

    /**
     * 微信APP包名
     */
    private final static String PACKAGE_WECHAT = "com.tencent.mm";

    /**
     * QQAPP包名
     *
     * com.tencent.mobileqq : QQ
     * com.tencent.mobileqqi : QQ国际版
     * com.tencent.qqlite : QQ轻聊版
     * com.tencent.minihd.qq :QQ HD版本
     * com.qzone:QQ空间
     *
     */
    private final static String PACKAGE_QQ = "com.tencent.mobileqq";

    private final static String PACKAGE_QQ_HD = "com.tencent.minihd.qq";

    private final static String PACKAGE_QQ_LITE = "com.tencent.qqlite";

    private final static String PACKAGE_QQ_INTERNATIONAL = "com.tencent.mobileqqi";

    private final static String PACKAGE_QQ_ZONE = "com.qzone";
}
