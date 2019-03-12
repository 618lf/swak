package com.swak.app.core.tools;

import android.content.Intent;
import android.os.Handler;

/**
 * 延迟 工具
 *
 * @Author:         李锋
 * @Date:     2019/3/10 9:41
 * @Version:        1.0
 */
public class DelayTools {

    /**
     * 延迟 毫秒 执行任务
     * 默认 2000
     * @param run
     */
    public static void postDelayed(Runnable run) {
        postDelayed(run, 2000);
    }

    /**
     * 延迟 毫秒 执行任务
     *
     * @param run
     * @param delayMillis
     */
    public static void postDelayed(Runnable run, int delayMillis) {
        new Handler().postDelayed(run, delayMillis);
    }
}
