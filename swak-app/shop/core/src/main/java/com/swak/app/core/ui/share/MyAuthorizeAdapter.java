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


import cn.sharesdk.framework.authorize.AuthorizeAdapter;

/**
 * 授权页面自定义
 */
public class MyAuthorizeAdapter extends AuthorizeAdapter {

    public void onCreate() {
        // 隐藏标题栏右部的ShareSDK Logo
        hideShareSDKLogo();

//		TitleLayout llTitle = getTitleLayout();
//		llTitle.getTvTitle().setText("xxxx");

//		String platName = getPlatformName();
//		if ("SinaWeibo".equals(platName)
//				|| "TencentWeibo".equals(platName)) {
//			initUi(platName);
//			interceptPlatformActionListener(platName);
//			return;
//		}
//
//		// 使弹出动画失效，只能在onCreate中调用，否则无法起作用
//		if ("KaiXin".equals(platName)) {
//			disablePopUpAnimation();
//		}
//
//		// 下面的代码演示如何设置自定义的授权页面打开动画
//		if ("Douban".equals(platName)) {
//			stopFinish = true;
//			disablePopUpAnimation();
//			View rv = (View) getBodyView().getParent();
//			TranslateAnimation ta = new TranslateAnimation(
//					Animation.RELATIVE_TO_SELF, -1,
//					Animation.RELATIVE_TO_SELF, 0,
//					Animation.RELATIVE_TO_SELF, 0,
//					Animation.RELATIVE_TO_SELF, 0);
//			ta.setDuration(500);
//			rv.setAnimation(ta);
//		}
    }
}
