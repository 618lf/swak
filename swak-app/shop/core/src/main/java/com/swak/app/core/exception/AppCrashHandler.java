package com.swak.app.core.exception;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Environment;

import com.swak.app.core.Logger;
import com.swak.app.core.Settings;
import com.swak.app.core.asm.PropertyCompat;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AppCrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = AppCrashHandler.class.getSimpleName();
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT1 = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final AppCrashHandler sMyCrashHandler = new AppCrashHandler();
    private Thread.UncaughtExceptionHandler mOldHandler;
    private Context mContext;

    public static AppCrashHandler getInstance() {
        return sMyCrashHandler;
    }

    public void register(Context context) {
        if (context != null) {
            mOldHandler = Thread.getDefaultUncaughtExceptionHandler();
            if (mOldHandler != this) {
                Thread.setDefaultUncaughtExceptionHandler(this);
            }
            mContext = context;
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Logger.e(TAG, "uncaughtException", ex);
        PrintWriter writer = null;
        try {
            Date date = new Date();
            String dateStr = SIMPLE_DATE_FORMAT1.format(date);

            File file = new File(Environment.getExternalStorageDirectory(), String.format(Settings.SDCARD_CRASH_LOG_DIR + "CrashLog_%s_%s.log", dateStr, android.os.Process.myPid()));
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (file.exists()) {
                file.delete();
            }

            writer = new PrintWriter(file);

            writer.println("Date:" + SIMPLE_DATE_FORMAT.format(date));
            writer.println("----------------------------------------System Infomation-----------------------------------");

            String packageName = mContext.getPackageName();
            writer.println("AppPkgName:" + packageName);
            try {
                PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(packageName, 0);
                writer.println("VersionCode:" + packageInfo.versionCode);
                writer.println("VersionName:" + packageInfo.versionName);
                writer.println("Debug:" + (0 != (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE)));
            } catch (Exception e) {
                writer.println("VersionCode:-1");
                writer.println("VersionName:null");
                writer.println("Debug:Unkown");
            }

            writer.println("PName:" + getProcessName());

            try {
                writer.println("imei:" + getIMEI(mContext));
            } catch (Exception e) {
            }

            writer.println("Board:" + PropertyCompat.get("ro.product.board", "unknown"));
            writer.println("ro.bootloader:" + PropertyCompat.get("ro.bootloader", "unknown"));
            writer.println("ro.product.brand:" + PropertyCompat.get("ro.product.brand", "unknown"));
            writer.println("ro.product.cpu.abi:" + PropertyCompat.get("ro.product.cpu.abi", "unknown"));
            writer.println("ro.product.cpu.abi2:" + PropertyCompat.get("ro.product.cpu.abi2", "unknown"));
            writer.println("ro.product.device:" + PropertyCompat.get("ro.product.device", "unknown"));
            writer.println("ro.build.display.id:" + PropertyCompat.get("ro.build.display.id", "unknown"));
            writer.println("ro.build.fingerprint:" + PropertyCompat.get("ro.build.fingerprint", "unknown"));
            writer.println("ro.hardware:" + PropertyCompat.get("ro.hardware", "unknown"));
            writer.println("ro.build.host:" + PropertyCompat.get("ro.build.host", "unknown"));
            writer.println("ro.build.id:" + PropertyCompat.get("ro.build.id", "unknown"));
            writer.println("ro.product.manufacturer:" + PropertyCompat.get("ro.product.manufacturer", "unknown"));
            writer.println("ro.product.model:" + PropertyCompat.get("ro.product.model", "unknown"));
            writer.println("ro.product.name:" + PropertyCompat.get("ro.product.name", "unknown"));
            writer.println("gsm.version.baseband:" + PropertyCompat.get("gsm.version.baseband", "unknown"));
            writer.println("ro.build.tags:" + PropertyCompat.get("ro.build.tags", "unknown"));
            writer.println("ro.build.type:" + PropertyCompat.get("ro.build.type", "unknown"));
            writer.println("ro.build.user:" + PropertyCompat.get("ro.build.user", "unknown"));
            writer.println("ro.build.version.codename:" + PropertyCompat.get("ro.build.version.codename", "unknown"));
            writer.println("ro.build.version.incremental:" + PropertyCompat.get("ro.build.version.incremental", "unknown"));
            writer.println("ro.build.version.release:" + PropertyCompat.get("ro.build.version.release", "unknown"));
            writer.println("ro.build.version.sdk:" + PropertyCompat.get("ro.build.version.sdk", "unknown"));
            writer.println("\n\n\n----------------------------------Exception---------------------------------------\n\n");
            writer.println("----------------------------Exception message:" + ex.getLocalizedMessage() + "\n");
            writer.println("----------------------------Exception StackTrace:");
            ex.printStackTrace(writer);
        } catch (Throwable e) {
            Logger.e(TAG, "记录uncaughtException", e);
        } finally {
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (Exception e) {
            }

            if (mOldHandler != null) {
                mOldHandler.uncaughtException(thread, ex);
            }
        }
    }

    private String getIMEI(Context mContext) {
        return "test";
    }

    public String getProcessName() {
        ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == android.os.Process.myPid()) {
                return info.processName;
            }
        }
        return null;
    }
}
