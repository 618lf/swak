package com.swak.app;

import android.content.Intent;

import com.swak.app.core.service.NetworkStateService;
import com.swak.app.core.ui.BaseApplication;

/**
 * 启动应用程序
 */
public class ShopApplication extends BaseApplication {

    /**
     * 启动程序，监听网络变化
     */
    @Override
    public void onCreate() {
        super.onCreate();

        //启动Service
        Intent mIntent = new Intent(this, NetworkStateService.class);
        startService(mIntent);
    }

    /**
     * 退出APP时手动调用
     */
    @Override
    public void exit() {
        try {
            //停止网络监听
            Intent mIntent = new Intent(this, NetworkStateService.class);
            stopService(mIntent);

            //关闭所有Activity
            super.exit();

            //退出进程
            System.exit(0);
        } catch (Exception e) {
        }
    }
}
