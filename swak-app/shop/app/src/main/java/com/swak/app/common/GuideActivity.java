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

package com.swak.app.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.swak.app.R;
import com.swak.app.core.ui.BaseActivity;
import com.swak.app.widget.ImageIndicatorView;

/**
 * 引导界面
 *
 * @author 曾繁添
 * @version 1.0
 */
public class GuideActivity extends BaseActivity {

    private ImageIndicatorView imageIndicatorView;

    @Override
    public int bindLayout() {
        return R.layout.activity_guide;
    }

    @SuppressLint("NewApi")
    @Override
    public void initView(View view) {
        imageIndicatorView = (ImageIndicatorView) findViewById(R.id.guide_indicate_view);
        //滑动监听器
        imageIndicatorView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener() {
            @Override
            public void onPosition(int position, int totalCount) {
                if (position + 1 == totalCount) {
                    imageIndicatorView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 300);
                }
            }
        });

        //隐藏标题栏
        mWindowTitle.hiddeTitleBar();
    }

    @Override
    public void doBusiness(Context mContext) {

        final Integer[] resArray = new Integer[]{R.drawable.guide01, R.drawable.guide02, R.drawable.guide03};
        imageIndicatorView.setupLayoutByDrawable(resArray);
        imageIndicatorView.setIndicateStyle(ImageIndicatorView.INDICATE_USERGUIDE_STYLE);
        imageIndicatorView.show();
    }
}
