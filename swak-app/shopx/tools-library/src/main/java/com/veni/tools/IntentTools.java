package com.veni.tools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.File;

/**
 * Created by kkan on 2016/1/24.
 * Intent相关
 * getInstallAppIntent         : 获取安装App(支持7.0)的Intent
 * getUninstallAppIntent       : 获取卸载App的Intent
 * getLaunchAppItent           : 获取打开App的Intent
 * getAppInfoIntent            : 获取App信息的Intent
 * getIntentByPackageName      : 根据包名获取Intent
 * getComponentNameIntent      : 获取其他应用的Intent
 *
 * getShareInfoIntent          : 获取分享的Intent
 * getCallIntent               : 获取拨号的Intent
 * getOpenCameraIntent         : 获取打开照程序界面的Intent
 * getImagePickerIntent        : 获取[跳转至相册选择界面,并跳转至裁剪界面，可以指定是否缩放裁剪区域]的Intent
 * getCameraIntent             : 获取[跳转至相册选择界面,并跳转至裁剪界面，默认可缩放裁剪区域]的Intent
 * getCropImageIntent          : 获取[跳转至裁剪界面]的Intent
 */
public class IntentTools {

    /**
     * 获取安装App(支持7.0)的Intent
     *
     * @param context
     * @param filePath
     * @return Intent
     */
    public static Intent getInstallAppIntent(Context context, String filePath) {
        //apk文件的本地路径
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return null;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri contentUri = FileTools.getUriForFile(context, apkfile);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        return intent;
    }

    /**
     * 获取卸载App的Intent
     *
     * @param packageName 包名
     * @return Intent
     */
    public static Intent getUninstallAppIntent(String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 获取打开App的Intent
     *
     * @param context     上下文
     * @param packageName 包名
     * @return Intent
     */
    public static Intent getLaunchAppIntent(Context context, String packageName) {
        return getIntentByPackageName(context, packageName);
    }

    /**
     * 根据包名获取Intent
     *
     * @param context     上下文
     * @param packageName 包名
     * @return Intent
     */
    private static Intent getIntentByPackageName(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    /**
     * 获取App信息的Intent
     *
     * @param packageName 包名
     * @return Intent
     */
    public static Intent getAppInfoIntent(String packageName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        return intent.setData(Uri.parse("package:" + packageName));
    }

    /**
     * 获取App信息分享的Intent
     *
     * @param info 分享信息
     * @return Intent
     */
    public static Intent getShareInfoIntent(String info) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        return intent.putExtra(Intent.EXTRA_TEXT, info);
    }

    /**
     * 获取拨号的Intent
     *
     * @param phone 电话号码
     * @return Intent
     */
    public static Intent getCallIntent(String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }



    /**
     * 获取打开照程序界面的Intent
     */
    public static Intent getOpenCameraIntent() {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    /**
     * 获取跳转至相册选择界面的Intent
     */
    public static Intent getImagePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        return intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "rx_bigimage_layout/*");
    }

    /**
     * 获取[跳转至相册选择界面,并跳转至裁剪界面，默认可缩放裁剪区域]的Intent
     */
    public static Intent getImagePickerIntent(int outputX, int outputY, Uri fromFileURI,
                                              Uri saveFileURI) {
        return getImagePickerIntent(1, 1, outputX, outputY, true, fromFileURI, saveFileURI);
    }

    /**
     * 获取[跳转至相册选择界面,并跳转至裁剪界面，默认可缩放裁剪区域]的Intent
     */
    public static Intent getImagePickerIntent(int aspectX, int aspectY, int outputX, int outputY, Uri fromFileURI,
                                              Uri saveFileURI) {
        return getImagePickerIntent(aspectX, aspectY, outputX, outputY, true, fromFileURI, saveFileURI);
    }

    /**
     * 获取[跳转至相册选择界面,并跳转至裁剪界面，可以指定是否缩放裁剪区域]的Intent
     *
     * @param aspectX     裁剪框尺寸比例X
     * @param aspectY     裁剪框尺寸比例Y
     * @param outputX     输出尺寸宽度
     * @param outputY     输出尺寸高度
     * @param canScale    是否可缩放
     * @param fromFileURI 文件来源路径URI
     * @param saveFileURI 输出文件路径URI
     */
    public static Intent getImagePickerIntent(int aspectX, int aspectY, int outputX, int outputY, boolean canScale,
                                              Uri fromFileURI, Uri saveFileURI) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(fromFileURI, "rx_bigimage_layout/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX <= 0 ? 1 : aspectX);
        intent.putExtra("aspectY", aspectY <= 0 ? 1 : aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", canScale);
        // 图片剪裁不足黑边解决
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFileURI);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 去除人脸识别
        return intent.putExtra("noFaceDetection", true);
    }

    /**
     * 获取[跳转至相册选择界面,并跳转至裁剪界面，默认可缩放裁剪区域]的Intent
     */
    public static Intent getCameraIntent(Uri saveFileURI) {
        Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        return mIntent.putExtra(MediaStore.EXTRA_OUTPUT, saveFileURI);
    }

    /**
     * 获取[跳转至裁剪界面,默认可缩放]的Intent
     */
    public static Intent getCropImageIntent(int outputX, int outputY, Uri fromFileURI,
                                            Uri saveFileURI) {
        return getCropImageIntent(1, 1, outputX, outputY, true, fromFileURI, saveFileURI);
    }

    /**
     * 获取[跳转至裁剪界面,默认可缩放]的Intent
     */
    public static Intent getCropImageIntent(int aspectX, int aspectY, int outputX, int outputY, Uri fromFileURI,
                                            Uri saveFileURI) {
        return getCropImageIntent(aspectX, aspectY, outputX, outputY, true, fromFileURI, saveFileURI);
    }


    /**
     * 获取[跳转至裁剪界面]的Intent
     */
    public static Intent getCropImageIntent(int aspectX, int aspectY, int outputX, int outputY, boolean canScale,
                                            Uri fromFileURI, Uri saveFileURI) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(fromFileURI, "rx_bigimage_layout/*");
        intent.putExtra("crop", "true");
        // X方向上的比例
        intent.putExtra("aspectX", aspectX <= 0 ? 1 : aspectX);
        // Y方向上的比例
        intent.putExtra("aspectY", aspectY <= 0 ? 1 : aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", canScale);
        // 图片剪裁不足黑边解决
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        // 需要将读取的文件路径和裁剪写入的路径区分，否则会造成文件0byte
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFileURI);
        // true-->返回数据类型可以设置为Bitmap，但是不能传输太大，截大图用URI，小图用Bitmap或者全部使用URI
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 取消人脸识别功能
        intent.putExtra("noFaceDetection", true);
        return intent;
    }


    /**
     * 获取其他应用的Intent
     *
     * @param packageName 包名
     * @param className   全类名
     * @return Intent
     */
    public static Intent getComponentNameIntent(String packageName, String className) {
        return getComponentNameIntent(packageName, className, null);
    }

    /**
     * 获取其他应用的Intent
     *
     * @param packageName 包名
     * @param className   全类名
     * @return Intent
     */
    public static Intent getComponentNameIntent(String packageName, String className, Bundle bundle) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (bundle != null) intent.putExtras(bundle);
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /**
     * 获取应用详情页面具体设置 intent
     *
     * @return Intent
     */
    public static Intent getAppDetailsSettingsIntent(Context mContext) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", mContext.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", mContext.getPackageName());
        }
        return localIntent;
    }

    /**
     * 获取应用详情页面具体设置 intent
     *
     * @param packageName 包名
     * @return intent
     */
    public static Intent getAppDetailsSettingsIntent(String packageName) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", packageName, null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", packageName);
        }
        return localIntent;
    }
}
