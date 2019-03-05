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


import cn.sharesdk.tencent.qq.QQWebShareAdapter;

/**
 * QQ授权页面自定义
 */
public class MyQQWebShareAdapter extends QQWebShareAdapter {

    public void onCreate() {
        // 设置页面以Dialog的方式展示
//        getActivity().setTheme(android.R.style.Theme_Dialog);
//        getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate();

        // 修改页面标题
//		getTitleLayout().getTvTitle().setText(R.string.qzone_customer_share_style);

        // 下面的代码可以拦截webview加载的页面地址，但是添加后，分享操作将可能无法正确执行
//		getWebBody().setWebViewClient(new WebViewClient() {
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				System.out.println("=========== " + url);
//				return super.shouldOverrideUrlLoading(view, url);
//			}
//		});

    }

}
