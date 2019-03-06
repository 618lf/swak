package com.swak.app.ui.main;

import android.content.Context;
import android.os.Bundle;

import com.swak.app.R;
import com.swak.app.base.BaseActivity;
import com.veni.tools.LogTools;

public class Launcher extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_launcher;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        LogTools.d("launcher init");
    }
}
