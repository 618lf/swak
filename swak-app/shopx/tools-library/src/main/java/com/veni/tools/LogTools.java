package com.veni.tools;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kkan on 2016/1/24.
 * Log管理
 */
public class LogTools {


    @NonNull
    private static SimpleDateFormat getLOG_FORMAT() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 日志的输出格式
    }

    @NonNull
    private static SimpleDateFormat getFILE_SUFFIX() {
        return new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
    }

    private static Boolean LOG_SWITCH = true; // 日志文件总开关
    private static Boolean LOG_TO_FILE = false; // 日志写入文件开关
    private static String LOG_TAG = "TAG"; // 默认的tag
    private static char LOG_TYPE = 'v';// 输入日志类型，v代表输出所有信息,w则只输出警告...
    private static int LOG_SAVE_DAYS = 7;// sd卡中日志文件的最多保存天数
    private static String LOG_FILE_PATH; // 日志文件保存路径
    private static String LOG_FILE_NAME;// 日志文件保存名称

    public static void init(Context context, boolean logswitch, boolean logtofile) { // 在Application中初始化
        LOG_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + File.separator + context.getPackageName();
        LOG_FILE_NAME = "Log";
        LOG_SWITCH = logswitch;
        LOG_TO_FILE = logtofile;
    }

    /****************************
     * Warn
     *********************************/
    public static void w(Object msg) {
        w(LOG_TAG, msg);
    }

    public static void w(String tag, Object msg) {
        w(tag, msg, null);
    }

    public static void w(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'w');
    }

    /***************************
     * Error
     ********************************/
    public static void e(Object msg) {
        e(LOG_TAG, msg);
    }

    public static void e(String tag, Object msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'e');
    }

    /***************************
     * Debug
     ********************************/
    public static void d(Object msg) {
        d(LOG_TAG, msg);
    }

    public static void d(String tag, Object msg) {// 调试信息
        d(tag, msg, null);
    }

    public static void d(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'd');
    }

    /****************************
     * Info
     *********************************/
    public static void i(Object msg) {
        i(LOG_TAG, msg);
    }

    public static void i(String tag, Object msg) {
        i(tag, msg, null);
    }

    public static void i(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'i');
    }

    /**************************
     * Verbose
     ********************************/
    public static void v(Object msg) {
        v(LOG_TAG, msg);
    }

    public static void v(String tag, Object msg) {
        v(tag, msg, null);
    }

    public static void v(String tag, Object msg, Throwable tr) {
        log(tag, msg.toString(), tr, 'v');
    }

    /**
     * 根据tag, msg和等级，输出日志
     *
     * @param tag
     * @param msg
     * @param level
     */
    private static void log(String tag, String msg, Throwable tr, char level) {
        if (LOG_SWITCH) {
            if ('e' == level && ('e' == LOG_TYPE || 'v' == LOG_TYPE)) { // 输出错误信息
                Log.e(tag, msg, tr);
            } else if ('w' == level && ('w' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.w(tag, msg, tr);
            } else if ('d' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.d(tag, msg, tr);
            } else if ('i' == level && ('d' == LOG_TYPE || 'v' == LOG_TYPE)) {
                Log.i(tag, msg, tr);
            } else {
                Log.v(tag, msg, tr);
            }
            if (LOG_TO_FILE)
                log2File(String.valueOf(level), tag, msg + tr == null ? "" : "\n" + Log.getStackTraceString(tr));
        }
    }

    /**
     * 打开日志文件并写入日志
     *
     * @return
     **/
    private synchronized static void log2File(String mylogtype, String tag, String text) {
        Date nowtime = new Date();
        String date = getFILE_SUFFIX().format(nowtime);
        String dateLogContent = getLOG_FORMAT().format(nowtime) + ":" + mylogtype + ":" + tag + ":" + text; // 日志输出格式
        File destDir = new File(LOG_FILE_PATH);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        File file = new File(LOG_FILE_PATH, LOG_FILE_NAME + date);
        FileWriter filerWriter = null;
        BufferedWriter bufWriter = null;
        try {
            filerWriter = new FileWriter(file, true);
            bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(dateLogContent);
            bufWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileTools.closeIO(filerWriter,bufWriter);
        }
    }

    /**
     * 删除指定的日志文件
     */
    public static void delFile() {// 删除日志文件
        String needDelFiel = getFILE_SUFFIX().format(getDateBefore());
        File file = new File(LOG_FILE_PATH, needDelFiel + LOG_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 得到LOG_SAVE_DAYS天前的日期
     *
     * @return
     */
    private static Date getDateBefore() {
        Date nowtime = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowtime);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - LOG_SAVE_DAYS);
        return now.getTime();
    }

    public static void saveLogFile(String message) {
        File fileDir = new File(FileTools.getRootPath() + File.separator + FutileTools.getContext().getPackageName());
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(fileDir, TimeTools.getCurrentDate(TimeTools.dateFormatYMD_f) + ".txt");
        PrintStream ps = null;
        try {
            if (file.exists()) {
                ps = new PrintStream(new FileOutputStream(file, true));
                ps.append("\n\n\n").append(TimeTools.getCurrentDate(TimeTools.dateFormatYMDHMS)).append("\n").append(message);// 往文件里写入字符串
            } else {
                ps = new PrintStream(new FileOutputStream(file));
                file.createNewFile();
                ps.println(TimeTools.getCurrentDate(TimeTools.dateFormatYMDHMS) + "\n" + message);// 往文件里写入字符串
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileTools.closeIO(ps);
        }
    }
}
