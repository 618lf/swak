package com.swak.app.core.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;

import com.swak.app.core.exception.ExceptionHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringKits {

    /**
     * 获取UUID
     *
     * @return 32UUID小写字符串
     */
    public static String gainUUID() {
        String strUUID = UUID.randomUUID().toString();
        strUUID = strUUID.replaceAll("-", "").toLowerCase();
        return strUUID;
    }

    /**
     * 判断字符串是否非空非null
     *
     * @param strParm
     *            需要判断的字符串
     * @return 真假
     */
    public static boolean isNoBlankAndNoNull(String strParm) {
        return !((strParm == null) || (strParm.equals("")));
    }

    /**
     * 将字符首字母转成大写
     * @param str
     * @return
     */
    public static String capitalFirst(String str){
        if(TextUtils.isEmpty(str)) return "";

        if(1 == str.length()) return str.toUpperCase();

        StringBuilder sb = new StringBuilder();
        String first = str.substring(0, 1);
        sb.append(first.toUpperCase());
        sb.append(str.substring(1, str.length()));
        return sb.toString();
    }

    /**
     * 将流转成字符串
     *
     * @param is
     *            输入流
     * @return
     * @throws Exception
     */
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * 将文件转成字符串
     *
     * @param file
     *            文件
     * @return
     * @throws Exception
     */
    public static String getStringFromFile(File file) throws Exception {
        FileInputStream fin = new FileInputStream(file);
        String ret = convertStreamToString(fin);
        // Make sure you close all streams.
        fin.close();
        return ret;
    }

    /**
     * 字符全角化
     *
     * @param input
     * @return
     */
    public static String ToSBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 判断是否含有中文
     * @param str 目标字符串
     * @return
     */
    public static boolean isContainChinese(String str) {
        try {
            Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher m = p.matcher(str);
            if (m.find()) {
                return true;
            }
        } catch (Exception e) {
            ExceptionHandler.process(e);
        }
        return false;
    }

    /**
     * 判断是否是Color的格式，即#后3、6、8位16进制的格式，返回true后使用str要做trim()过滤空格
     * @param str
     * @return 是否是颜色格式
     */
    public static boolean isColor(String str) {
        if(TextUtils.isEmpty(str)) {
            return false;
        }
        str = str.trim();
        if(!str.startsWith("#") || (str.length() != 4 && str.length() != 7 && str.length() != 9)) {
            return false;
        }

        String pattern = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3}|[A-Fa-f0-9]{8})$";
        return Pattern.matches(pattern, str);
    }

    /**
     * 根据字符串获取对应的颜色值
     * @param color 目标颜色值
     * @return
     */
    public static int getColor(String color){
        return getColor(color,"");
    }

    /**
     * 根据字符串获取对应的颜色值
     * @param color 目标颜色值
     * @param defaultColor 默认颜色
     * @return
     */
    public static int getColor(String color,String defaultColor){
        if(isColor(color)){
            return Color.parseColor(color);
        }
        if(isColor(defaultColor)){
            return Color.parseColor(defaultColor);
        }
        return -1;
    }

    /**
     * 获取颜色值
     * @param mContex 上下文
     * @param colorResId 颜色资源id（R.color.white）
     * @return
     */
    public static int getColor(Context mContex, int colorResId){
        return mContex.getResources().getColor(colorResId);
    }
}
