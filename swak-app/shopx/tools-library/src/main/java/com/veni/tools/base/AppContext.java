package com.veni.tools.base;

import android.app.Application;
import android.content.Context;

import com.veni.tools.BuildConfig;
import com.veni.tools.CrashLogTools;
import com.veni.tools.FutileTools;
import com.veni.tools.LogTools;

/**
 * 应用程序启动
 */
public class AppContext extends Application {

    /**
     * 对外提供整个应用生命周期的Context
     **/
    static Context instance;

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
        this.init();
    }

    /**
     * 初始化工作
     */
    protected void init() {

        // 初始化常用工具类
        FutileTools.init(this);

        // 程序奔溃日志
        CrashLogTools.init(this);

        // 日志
        LogTools.init(this, BuildConfig.LOG_DEBUG, false);
    }
}