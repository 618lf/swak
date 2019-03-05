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

package com.swak.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.swak.app.core.Settings;
import com.swak.app.core.bean.AdapterModelBean;
import com.swak.app.core.ui.Alerts;
import com.swak.app.core.ui.BaseActivity;
import com.swak.app.core.ui.adapter.BaseMAdapter;
import com.swak.app.core.utils.PhoneKits;

import java.util.ArrayList;
import java.util.List;

/**
 * Sample列表集合界面--自动收集AndroidManifest.xml配置
 * <per>
 * <intent-filter>
 * <action android:name="android.intent.action.MAIN" />
 * <category android:name="com.zftlive.android.SAMPLE_CODE" />
 * </intent-filter>
 * </per>
 * 的Activity
 *
 * @author 曾繁添
 * @version 1.0
 */
public class MainActivity extends BaseActivity {

    private ListView mListView;
    public final static String SAMPLE_CODE = "com.zftlive.android.SAMPLE_CODE";

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @SuppressLint("NewApi")
    @Override
    public void initView(View view) {
        mListView = (ListView) findViewById(R.id.lv_demos);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ActivityItem data = (ActivityItem) parent.getItemAtPosition(position);
                Intent intent = data.intent;
                getOperation().forward(intent.getComponent().getClassName(), LEFT_RIGHT);
            }
        });

        //构造适配器
        DemoActivityAdapter mAdapter = new DemoActivityAdapter(this);
        mAdapter.addItem(getListData());
        mListView.setAdapter(mAdapter);

        //初始化带返回按钮的标题栏
        String strCenterTitle = getResources().getString(R.string.MainActivity);
        mWindowTitle.initBackTitleBar(strCenterTitle, Gravity.CENTER);
        mWindowTitle.getDoneImageButton().setVisibility(View.INVISIBLE);
        mWindowTitle.getBackImageButton().setImageResource(R.drawable.anl_common_nav_menu_white_n);

        //是否Root
        Toast.makeText(mActivity, "当前手机root状态：" + PhoneKits.isRootSystem(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void doBusiness(Context mContext) {
        //获取运行环境
        boolean isEmulator = Settings.isEmulator(getContext());
        Alerts.toastLong("当前运行环境：" + (isEmulator ? "模拟器" + "(" + Settings.OS_VERSION + ")" : (Settings.MODEL_NUMBER + "(" + Settings.OS_VERSION + ")")));
    }

    /**
     * Actionbar点击[左侧图标]关闭事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;
        }
        return true;
    }

    private boolean isQuit;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (isQuit == false) {
                isQuit = true;
                Toast.makeText(getBaseContext(), "再按一次退出", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isQuit = false;
                    }
                }, 2000);
            } else {
                ((ShopApplication) getApplication()).exit();
            }
        }
        return true;
    }

    /**
     * 初始化列表数据
     *
     * @return
     */
    protected List<ActivityItem> getListData() {
        List<ActivityItem> mListViewData = new ArrayList<>();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(SAMPLE_CODE);
        List<ResolveInfo> mActivityList = getPackageManager().queryIntentActivities(mainIntent, 0);
        for (int i = 0; i < mActivityList.size(); i++) {
            ResolveInfo info = mActivityList.get(i);
            String label = info.loadLabel(getPackageManager()) != null ? info.loadLabel(getPackageManager()).toString() : info.activityInfo.name;
            ActivityItem temp = new ActivityItem();
            temp.title = label;
            temp.intent = buildIntent(info.activityInfo.applicationInfo.packageName, info.activityInfo.name);
            mListViewData.add(temp);
        }

        return mListViewData;
    }

    /**
     * 构建每一个Item点击Intent
     *
     * @param packageName
     * @param componentName
     * @return
     */
    protected Intent buildIntent(String packageName, String componentName) {
        Intent result = new Intent();
        result.setClassName(packageName, componentName);
        return result;
    }

    /**
     * 列表适配器
     */
    protected class DemoActivityAdapter extends BaseMAdapter {

        public DemoActivityAdapter(Activity mContext) {
            super(mContext);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Holder mHolder = null;
            if (null == convertView) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_main_list_item, null);
                mHolder = new Holder();
                mHolder.label = (TextView) convertView.findViewById(R.id.tv_label);
                convertView.setTag(mHolder);
            } else {
                mHolder = (Holder) convertView.getTag();
            }

            //设置数据
            ActivityItem data = (ActivityItem) getItem(position);
            mHolder.label.setText(data.title);
            return convertView;
        }

        class Holder {
            TextView label;
        }
    }

    class ActivityItem extends AdapterModelBean {
        String title;
        Intent intent;

        public ActivityItem() {
        }

        public ActivityItem(String title, Intent intent) {
            this.title = title;
            this.intent = intent;
        }
    }
}
