package com.swak.app.ui.main;

import android.content.Context;
import android.os.Bundle;

import com.swak.app.R;
import com.swak.app.base.BaseActivity;
import com.swak.app.model.PersonalBean;
import com.swak.app.ui.main.contract.MainContract;
import com.swak.app.ui.main.presenter.MainPresenter;
import com.veni.tools.base.ActivityJumpOptionsTool;

import java.util.List;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

    }

    @Override
    public void returnVersionData(List<PersonalBean> data) {
    }

    @Override
    public void onErrorSuccess(int code, String message, boolean isSuccess, boolean showTips) {
    }

    /**
     * 启动入口
     */
    public static void startAction(Context context) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(MainActivity.class)
                .setEnterResId(0)
                .setActionTag(ActivityJumpOptionsTool.Type.CLEAR_TASK)
                .customAnim()
                .start();
    }
}
