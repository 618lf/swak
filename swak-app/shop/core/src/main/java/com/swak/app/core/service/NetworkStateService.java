package com.swak.app.core.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.swak.app.core.ui.Toasts;

/**
 * 网络状态监控
 */
public class NetworkStateService extends Service {

    /**
     * 订阅网络状态变化广播
     */
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    /**
     * 保证Service不杀死
     * http://blog.csdn.net/primer_programer/article/details/25987439
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 取消广播订阅
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    /**
     * 网络状态变化广播接收器
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent
                    .getAction())) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    onNetworkChange(info.getTypeName());
                } else {
                    onNoNetwork();
                }
            }
        }
    };

    /**
     * 没有网络回调
     */
    public void onNoNetwork() {
        Toasts.show("OMG 木有网络了~~");
    }

    /**
     * 网络发生变化回调函数
     *
     * @param networkType 当前网络类型
     */
    public void onNetworkChange(String networkType) {
        Toasts.show("当前网络：" + networkType);
    }
}
