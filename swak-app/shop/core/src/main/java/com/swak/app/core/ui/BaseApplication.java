package com.swak.app.core.ui;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.swak.app.core.exception.AppCrashHandler;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 基础的应用
 */
public class BaseApplication extends Application {

    /**
     * 对外提供整个应用生命周期的Context
     **/
    static Context instance;

    /**
     * 整个应用全局可访问数据集合
     **/
    private static Map<String, Object> gloableData = new HashMap<String, Object>();

     /**
     * 寄存整个应用Activity
     **/
    private final Stack<WeakReference<Activity>> activitys = new Stack<WeakReference<Activity>>();

    /**
     * 日志输出标志
     **/
    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 对外提供 Application Context
     *
     * @return
     */
    public static Context me() {
        return instance;
    }

    /**
     * 系统初始化
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    /**
     * 初始化工作
     */
    protected void init() {

        // 注册崩溃异常处理类
        AppCrashHandler.getInstance().register(this);

        // 初始化资源
        this.resetResources();
    }

    /**
     * 保证APP字体不受用户调整手机字体大小而影响，确保布局不会变形，或者sp使用dp替代
     */
    protected void resetResources() {
        Resources res = super.getResources();
        if (res != null) {
            android.content.res.Configuration cfg = res.getConfiguration();
            if (cfg != null && cfg.fontScale != 1.0f) {
                cfg.fontScale = 1.0f;
                res.updateConfiguration(cfg, res.getDisplayMetrics());
            }
        }
    }

    /**
     * 获取网络是否已连接
     *
     * @return
     */
    public static boolean isNetworkReady() {
        try {
            ConnectivityManager connectivity =
                    (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 往Application放置数据（最大不允许超过5个）
     *
     * @param strKey   存放属性Key
     * @param strValue 数据对象
     */
    public static void assignData(String strKey, Object strValue) {
        if (gloableData.size() > 5) {
            throw new RuntimeException("超过允许最大数");
        }
        gloableData.put(strKey, strValue);
    }

    /**
     * 从Applcaiton中取数据
     *
     * @param strKey 存放数据Key
     * @return 对应Key的数据对象
     */
    public static Object gainData(String strKey) {
        return gloableData.get(strKey);
    }

    /*
     * 从Application中移除数据
     */
    public static void removeData(String key) {
        if (gloableData.containsKey(key)) gloableData.remove(key);
    }

    /**
     * 将Activity压入Application栈
     *
     * @param task 将要压入栈的Activity对象
     */
    public void pushTask(WeakReference<Activity> task) {
        activitys.push(task);
    }

    /**
     * 将传入的Activity对象从栈中移除
     *
     * @param task
     */
    public void removeTask(WeakReference<Activity> task) {
        activitys.remove(task);
    }

    /**
     * 根据指定位置从栈中移除Activity
     *
     * @param taskIndex Activity栈索引
     */
    public void removeTask(int taskIndex) {
        if (activitys.size() > taskIndex)
            activitys.remove(taskIndex);
    }

    /**
     * 将栈中Activity移除至栈顶
     */
    public void removeToTop() {
        int end = activitys.size();
        int start = 1;
        for (int i = end - 1; i >= start; i--) {
            Activity mActivity = activitys.get(i).get();
            if (null != mActivity && !mActivity.isFinishing()) {
                mActivity.finish();
            }
        }
    }

    /**
     * 移除全部（用于整个应用退出）
     */
    public void removeAll() {
        //finish所有的Activity
        for (WeakReference<Activity> task : activitys) {
            Activity mActivity = task.get();
            if (null != mActivity && !mActivity.isFinishing()) {
                mActivity.finish();
            }
        }
    }

    /**
     * 退出整个APP，关闭所有activity/清除缓存等等
     */
    public void exit() {
        this.removeAll();
    }
}
