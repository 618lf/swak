package com.swak.app.core.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 手机相关的工具
 */
public class PhoneKits {

    private static final String TAG = "ToolPhone";

    private final static int kSystemRootStateUnknow = -1;
    private final static int kSystemRootStateDisable = 0;
    private final static int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;

    /**
     * 是否Root手机
     * @return
     */
    public static boolean isRootSystem() {
        if (systemRootState == kSystemRootStateEnable) {
            return true;
        } else if (systemRootState == kSystemRootStateDisable) {

            return false;
        }
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                f = new File(kSuSearchPaths[i] + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        systemRootState = kSystemRootStateDisable;
        return false;
    }

    /**
     * 得到子控件child在父控件或者更高父控件parentId中的坐标位置
     * @param child
     * @param parentId
     * @param location 存放坐标信息，需初始化为length=2
     */
    public static void getViewLocationInAncestor(View child, int parentId, int[] location) {
        if (child == null) {
            throw new IllegalArgumentException("child must not be null!");
        }
        if (parentId == 0) {
            throw new IllegalArgumentException("parentId must not be 0!");
        }
        ViewGroup parent = (ViewGroup) child.getParent();
        location[0] = child.getLeft();
        location[1] = child.getTop();
        while (parent.getId() != parentId) {
            ViewGroup topParent = (ViewGroup) parent.getParent();
            if (topParent == null) {
                location[0] = location[1] = 0;
                return;
            }
            location[0] = location[0] + parent.getLeft() - topParent.getScrollX();
            location[1] = location[1] + parent.getTop() - topParent.getScrollY();

            parent = topParent;
        }
    }

    /**
     * 得到子控件child在父控件或者更高父控件parent中的内容区域
     * @param child
     * @param parent
     * @param r 存放内容区域信息，不能为null
     */
    public static void getViewBoundsInAncestor(View child, View parent, Rect r) {
        if (r == null || child == null || parent == null) {
            throw new IllegalArgumentException("r must not be null!");
        }
        ViewGroup p = (ViewGroup) child.getParent();
        r.set(0, 0, child.getWidth(), child.getHeight());
        r.offset(child.getLeft() - p.getScrollX(), child.getTop() - p.getScrollY());
        while (p != parent) {
            ViewGroup topParent = (ViewGroup) p.getParent();
            if (topParent == null) {
                r.set(0, 0, child.getWidth(), child.getHeight());
                return;
            }
            r.offset(p.getLeft() - topParent.getScrollX(), p.getTop() - topParent.getScrollY());

            p = topParent;
        }
    }

    /**
     * 获取区域范围Rect(指定区域大小宽,指定区域大小高)在屏幕可见的View集合(可用于扩展竖向的Scrollview以及其他固定区域块容器)
     * @param parent
     * @param regionW 标准检测区域的宽度
     * @param regionH 标准检测区域的高度
     * @return
     */
    public static ArrayList<View> getVisiableViewsInParent(ViewGroup parent, int regionW, int regionH) {
        ArrayList<View> visiableChilds = new ArrayList<>();
        int childCount = 0;
        ViewGroup parentView = parent;
        if (parent == null || parent.getWindowToken() == null || (childCount = parent.getChildCount()) == 0) {
            return visiableChilds;
        }

        //当前屏幕宽度和控件宽度取最小值
        int mScreenWidth = parent.getContext().getResources().getDisplayMetrics().widthPixels;
        Rect rect = new Rect(0, 0, regionW, regionH);
        if ((parent instanceof ScrollView || parent instanceof HorizontalScrollView) && parent.getChildCount() > 0) {
            View view = parent.getChildAt(0);
            if (view instanceof ViewGroup) {
                parent = (ViewGroup) view;
                childCount = parent.getChildCount();
            }
        } else if (parent.getParent() instanceof ScrollView || parent.getParent() instanceof HorizontalScrollView) {
            parentView = (ViewGroup) parent.getParent();
        }
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (child == null) {
                continue;
            }

            Rect childRect = new Rect(0, 0, child.getWidth(), child.getHeight());
            getViewBoundsInAncestor(child, parentView, childRect);
            if (Rect.intersects(rect, childRect)) {
                visiableChilds.add(child);
            }
        }

        return visiableChilds;
    }

    /**
     *  获取区域范围Rect(Math.min(屏幕宽度,容器View的宽度),容器View的高度)在屏幕可见的View集合(一般用于横向滑动的控件HorScrollView)
     * @param parent
     * @return
     */
    public static ArrayList<View> getVisiableViewsInParent(ViewGroup parent) {
        if (parent == null || parent.getWindowToken() == null) {
            return  new ArrayList<View>();
        }
        //当前屏幕宽度和控件宽度取最小值
        int mScreenWidth = parent.getContext().getResources().getDisplayMetrics().widthPixels;
        return getVisiableViewsInParent(parent,Math.min(mScreenWidth,parent.getWidth()), parent.getBottom());
    }

    /**
     * 设置透明状态栏
     *
     * @param activity 当前Activity
     * @return
     */
    public static void setStatusBarBackgroundColor(@NonNull Activity activity, @ColorInt int bgColor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(bgColor);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }

        return;
    }

    /**
     * 获取系统状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 直接呼叫指定的号码(需要<uses-permission android:name="android.permission.CALL_PHONE"/>权限)
     *
     * @param mContext    上下文Context
     * @param phoneNumber 需要呼叫的手机号码
     */
    public static void callPhone(Context mContext, String phoneNumber) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent call = new Intent(Intent.ACTION_CALL, uri);
        mContext.startActivity(call);
    }

    /**
     * 跳转至拨号界面
     *
     * @param mContext    上下文Context
     * @param phoneNumber 需要呼叫的手机号码
     */
    public static void toCallPhoneActivity(Context mContext, String phoneNumber) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent call = new Intent(Intent.ACTION_DIAL, uri);
        mContext.startActivity(call);
    }

    /**
     * 直接调用短信API发送信息(设置监听发送和接收状态)
     *
     * @param strPhone      手机号码
     * @param strMsgContext 短信内容
     */
    public static void sendMessage(final Context mContext, final String strPhone,
                                   final String strMsgContext) {

        // 处理返回的发送状态
        String SENT_SMS_ACTION = "SENT_SMS_ACTION";
        Intent sentIntent = new Intent(SENT_SMS_ACTION);
        PendingIntent sendIntent = PendingIntent.getBroadcast(mContext, 0, sentIntent, 0);
        // register the Broadcast Receivers
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(mContext, "短信发送成功", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        break;
                }
            }
        }, new IntentFilter(SENT_SMS_ACTION));

        // 处理返回的接收状态
        String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
        // create the deilverIntent parameter
        Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
        PendingIntent backIntent = PendingIntent.getBroadcast(mContext, 0, deliverIntent, 0);
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context _context, Intent _intent) {
                Toast.makeText(mContext, strPhone + "已经成功接收", Toast.LENGTH_SHORT).show();
            }
        }, new IntentFilter(DELIVERED_SMS_ACTION));

        // 拆分短信内容（手机短信长度限制）
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> msgList = smsManager.divideMessage(strMsgContext);
        for (String text : msgList) {
            smsManager.sendTextMessage(strPhone, null, text, sendIntent, backIntent);
        }
    }

    /**
     * 跳转至发送短信界面(自动设置接收方的号码)
     *
     * @param mContext      Activity
     * @param strPhone      手机号码
     * @param strMsgContext 短信内容
     */
    public static void toSendMessageActivity(Context mContext, String strPhone, String strMsgContext) {
        if (PhoneNumberUtils.isGlobalPhoneNumber(strPhone)) {
            Uri uri = Uri.parse("smsto:" + strPhone);
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
            sendIntent.putExtra("sms_body", strMsgContext);
            mContext.startActivity(sendIntent);
        }
    }

    /**
     * 跳转至联系人选择界面
     *
     * @param mContext    上下文
     * @param requestCode 请求返回区分代码
     */
    public static void toChooseContactsList(Activity mContext, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mContext.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取选择的联系人的手机号码
     *
     * @param mContext   上下文
     * @param resultCode 请求返回Result状态区分代码
     * @param data       onActivityResult返回的Intent
     * @return
     */
    public static String getChoosedPhoneNumber(Activity mContext, int resultCode, Intent data) {
        // 返回结果
        String phoneResult = "";
        if (Activity.RESULT_OK == resultCode) {
            Uri uri = data.getData();
            Cursor mCursor = mContext.managedQuery(uri, null, null, null, null);
            mCursor.moveToFirst();

            int phoneColumn = mCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
            int phoneNum = mCursor.getInt(phoneColumn);
            if (phoneNum > 0) {
                // 获得联系人的ID号
                int idColumn = mCursor.getColumnIndex(ContactsContract.Contacts._ID);
                String contactId = mCursor.getString(idColumn);
                // 获得联系人的电话号码的cursor;
                Cursor phones =
                        mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null,
                                null);
                if (phones.moveToFirst()) {
                    // 遍历所有的电话号码
                    for (; !phones.isAfterLast(); phones.moveToNext()) {
                        int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                        int phone_type = phones.getInt(typeindex);
                        String phoneNumber = phones.getString(index);
                        switch (phone_type) {
                            case 2:
                                phoneResult = phoneNumber;
                                break;
                        }
                    }
                    if (!phones.isClosed()) {
                        phones.close();
                    }
                }
            }
            // 关闭游标
            mCursor.close();
        }

        return phoneResult;
    }

    /**
     * 获取跳转至拍照程序界面的Intent
     */
    public static Intent gainToCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        return intent;
    }

    /**
     * 跳转至拍照程序界面
     *
     * @param mContext    上下文
     * @param requestCode 请求返回Result区分代码
     */
    public static void toCameraActivity(Activity mContext, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mContext.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取跳转至相册选择界面的Intent
     */
    public static Intent gainToImagePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("image/*");
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        return intent;
    }

    /**
     * 跳转至相册选择界面
     *
     * @param mContext    上下文
     * @param requestCode
     */
    public static void toImagePickerActivity(Activity mContext, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setType("image/*");
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        mContext.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取[跳转至相册选择界面,并跳转至裁剪界面，默认可缩放裁剪区域]的Intent
     */
    public static Intent gainToImagePickerIntent(int outputX, int outputY, Uri fromFileURI,
                                                 Uri saveFileURI) {
        return gainToImagePickerIntent(outputX, outputY, true, fromFileURI, saveFileURI);
    }

    /**
     * 跳转至相册选择界面,并跳转至裁剪界面，默认可缩放裁剪区域
     *
     * @param mContext    上下文
     * @param requestCode 请求返回Result区分代码
     * @param outputX     输出尺寸宽度
     * @param outputY     输出尺寸高度
     * @param fromFileURI 文件来源路径URI
     * @param saveFileURI 输出文件路径URI
     */
    public static void toImagePickerActivity(Activity mContext, int requestCode, int outputX,
                                             int outputY, Uri fromFileURI, Uri saveFileURI) {
        toImagePickerActivity(mContext, requestCode, outputX, outputY, true, fromFileURI, saveFileURI);
    }

    /**
     * 获取[跳转至相册选择界面,并跳转至裁剪界面，可以指定是否缩放裁剪区域]的Intent
     */
    public static Intent gainToImagePickerIntent(int outputX, int outputY, boolean canScale,
                                                 Uri fromFileURI, Uri saveFileURI) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setDataAndType(fromFileURI, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", canScale);
        // 图片剪裁不足黑边解决
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFileURI);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 去除人脸识别
        intent.putExtra("noFaceDetection", true);
        return intent;
    }

    /**
     * 跳转至相册选择界面,并跳转至裁剪界面，可以指定是否缩放裁剪区域
     *
     * @param mContext    上下文
     * @param requestCode 请求返回Result区分代码
     * @param outputX     输出尺寸宽度
     * @param outputY     输出尺寸高度
     * @param canScale    是否可缩放
     * @param fromFileURI 文件来源路径URI
     * @param saveFileURI 输出文件路径URI
     */
    public static void toImagePickerActivity(Activity mContext, int requestCode, int outputX,
                                             int outputY, boolean canScale, Uri fromFileURI, Uri saveFileURI) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setDataAndType(fromFileURI, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", canScale);
        // 图片剪裁不足黑边解决
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFileURI);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 去除人脸识别
        intent.putExtra("noFaceDetection", true);
        mContext.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取[跳转至相册选择界面,并跳转至裁剪界面，默认可缩放裁剪区域]的Intent
     */
    public static Intent gainToCameraIntent(Uri saveFileURI) {
        Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, saveFileURI);
        return mIntent;
    }

    /**
     * 跳转至拍照程序界面，并且指定存储路径
     *
     * @param mContext    上下文
     * @param requestCode 请求返回Result区分代码
     * @param extraOutput 拍照存储路径URI
     */
    public static void toCameraActivity(Activity mContext, int requestCode, Uri extraOutput) {
        Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mIntent.putExtra(MediaStore.EXTRA_OUTPUT, extraOutput);
        mContext.startActivityForResult(mIntent, requestCode);
    }

    /**
     * 获取[跳转至裁剪界面,默认可缩放]的Intent
     */
    public static Intent gainToCropImageIntent(int outputX, int outputY, Uri fromFileURI,
                                               Uri saveFileURI) {
        return gainToCropImageIntent(outputX, outputY, true, fromFileURI, saveFileURI);
    }

    /**
     * 跳转至裁剪界面,默认可缩放
     *
     * @param mContext    启动裁剪界面的Activity
     * @param fromFileURI 需要裁剪的图片URI
     * @param saveFileURI 输出文件路径URI
     * @param outputX     输出尺寸宽度
     * @param outputY     输出尺寸高度
     * @param requestCode 请求返回Result区分代码
     */
    public static void toCropImageActivity(Activity mContext, Uri fromFileURI, Uri saveFileURI,
                                           int outputX, int outputY, int requestCode) {
        toCropImageActivity(mContext, fromFileURI, saveFileURI, outputX, outputY, true, requestCode);
    }

    /**
     * 获取[跳转至裁剪界面]的Intent
     */
    public static Intent gainToCropImageIntent(int outputX, int outputY, boolean canScale,
                                               Uri fromFileURI, Uri saveFileURI) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.setDataAndType(fromFileURI, "image/*");
        intent.putExtra("crop", "true");
        // X方向上的比例
        intent.putExtra("aspectX", 1);
        // Y方向上的比例
        intent.putExtra("aspectY", 1);
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
     * 跳转至裁剪界面
     *
     * @param mContext    启动裁剪界面的Activity
     * @param fromFileURI 需要裁剪的图片URI
     * @param saveFileURI 输出文件路径URI
     * @param outputX     输出尺寸宽度
     * @param outputY     输出尺寸高度
     * @param canScale    是否可缩放
     * @param requestCode 请求返回Result区分代码
     */
    public static void toCropImageActivity(Activity mContext, Uri fromFileURI, Uri saveFileURI,
                                           int outputX, int outputY, boolean canScale, int requestCode) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.setDataAndType(fromFileURI, "image/*");
        intent.putExtra("crop", "true");
        // X方向上的比例
        intent.putExtra("aspectX", 1);
        // Y方向上的比例
        intent.putExtra("aspectY", 1);
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
        mContext.startActivityForResult(intent, requestCode);
    }

    /**
     * 获得选中相册的图片
     *
     * @param mContext 上下文
     * @param data     onActivityResult返回的Intent
     * @return
     */
    public static Bitmap getChoosedImage(Activity mContext, Intent data) {
        if (data == null) {
            return null;
        }
        Bitmap bm = null;
        // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = mContext.getContentResolver();
        try {
            Uri originalUri = data.getData(); // 获得图片的uri
            bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); // 显得到bitmap图片
        } catch (Exception e) {
            Log.e("ToolPhone", e.getMessage());
        }
        return bm;
    }

    /**
     * 获得选中相册的图片路径
     *
     * @param mContext 上下文
     * @param data     onActivityResult返回的Intent
     * @return
     */
    public static String getChoosedImagePath(Activity mContext, Intent data) {
        if (data == null) {
            return null;
        }
        String path = "";
        // 外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口
        ContentResolver resolver = mContext.getContentResolver();
        // 此处的用于判断接收的Activity是不是你想要的那个
        try {
            Uri originalUri = data.getData(); // 获得图片的uri
            // 这里开始的第二部分，获取图片的路径：
            String[] proj = {MediaStore.Images.Media.DATA};
            // 好像是android多媒体数据库的封装接口，具体的看Android文档
            Cursor cursor = mContext.managedQuery(originalUri, proj, null, null, null);
            // 按我个人理解 这个是获得用户选择的图片的索引值
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            // 将光标移至开头 ，这个很重要，不小心很容易引起越界
            cursor.moveToFirst();
            // 最后根据索引值获取图片路径
            path = new String(cursor.getString(column_index));
            Log.d(TAG, path);
            // 不用了关闭游标，4.0以上的版本会自动关闭
            if (cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return path;
    }

    /**
     * 获取拍照之后的照片文件（JPG格式）
     *
     * @param mContext 上下文
     * @param data     onActivityResult回调返回的数据
     * @param filePath 文件存储路径（文件目录+文件名）
     * @param quality  文件存储质量（0-100）
     * @return
     */
    public static File getTakePictureFile(Activity mContext, Intent data, String filePath, int quality) {
        // 数据合法性校验
        if (data == null) {
            return null;
        }
        Bundle extras = data.getExtras();
        if (extras == null) {
            return null;
        }

        // 保存图片
        Bitmap photo = extras.getParcelable("data");
        File file = new File(filePath);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            photo.compress(Bitmap.CompressFormat.JPEG, quality, stream);// (0-100)压缩文件
            stream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    /**
     * 调用本地浏览器打开一个网页
     *
     * @param mContext   上下文
     * @param strSiteUrl 网页地址
     */
    public static void openWebSite(Context mContext, String strSiteUrl) {
        Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strSiteUrl));
        mContext.startActivity(webIntent);
    }

    /**
     * 跳转至系统设置界面
     *
     * @param mContext 上下文
     */
    public static void toSettingActivity(Context mContext) {
        Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
        mContext.startActivity(settingsIntent);
    }

    /**
     * 跳转至WIFI设置界面
     *
     * @param mContext 上下文
     */
    public static void toWIFISettingActivity(Context mContext) {
        Intent wifiSettingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        mContext.startActivity(wifiSettingsIntent);
    }

    /**
     * 启动本地应用打开PDF
     *
     * @param mContext 上下文
     * @param filePath 文件路径
     */
    public static void openPDFFile(Context mContext, String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "未检测到可打开PDF相关软件", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 启动本地应用打开PDF
     *
     * @param mContext 上下文
     * @param filePath 文件路径
     */
    public static void openWordFile(Context mContext, String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                Uri path = Uri.fromFile(file);
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(path, "application/msword");
                mContext.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, "未检测到可打开Word文档相关软件", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 调用WPS打开office文档 http://bbs.wps.cn/thread-22349340-1-1.html
     *
     * @param mContext 上下文
     * @param filePath 文件路径
     */
    public static void openOfficeByWPS(Context mContext, String filePath) {

        try {

            // 文件存在性检查
            File file = new File(filePath);
            if (!file.exists()) {
                Toast.makeText(mContext, filePath + "文件路径不存在", Toast.LENGTH_SHORT).show();
                return;
            }

            // 检查是否安装WPS
            String wpsPackageEng = "cn.wps.moffice_eng";// 普通版与英文版一样
            // String wpsActivity = "cn.wps.moffice.documentmanager.PreStartActivity";
            String wpsActivity2 = "cn.wps.moffice.documentmanager.PreStartActivity2";// 默认第三方程序启动

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setClassName(wpsPackageEng, wpsActivity2);

            Uri uri = Uri.fromFile(new File(filePath));
            intent.setData(uri);
            mContext.startActivity(intent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "本地未安装WPS", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(mContext, "打开文档失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 判断是否安装指定包名的APP
     *
     * @param mContext    上下文
     * @param packageName 包路径
     * @return
     */
    public static boolean isInstalledApp(Context mContext, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }

        try {
            ApplicationInfo info =
                    mContext.getPackageManager().getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 判断是否存在指定的Activity
     *
     * @param mContext    上下文
     * @param packageName 包名
     * @param className   activity全路径类名
     * @return
     */
    public static boolean isExistActivity(Context mContext, String packageName, String className) {

        Boolean result = true;
        Intent intent = new Intent();
        intent.setClassName(packageName, className);

        if (mContext.getPackageManager().resolveActivity(intent, 0) == null) {
            result = false;
        } else if (intent.resolveActivity(mContext.getPackageManager()) == null) {
            result = false;
        } else {
            List<ResolveInfo> list = mContext.getPackageManager().queryIntentActivities(intent, 0);
            if (list.size() == 0) {
                result = false;
            }
        }

        return result;
    }

    /**
     * 打开软键盘
     *
     * @param mContext 上下文
     * @param mView    触发软键盘的View
     */
    public static void openSoftKeyboard(Context mContext, View mView) {
        if (null == mContext || null == mView) return;
        InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        mInputMethodManager.showSoftInput(mView, 0);
    }

    /**
     * 关闭/隐藏软键盘
     *
     * @param mContext 上下文
     * @param mView    触发软键盘的View
     */
    public static void closeSoftKeyboard(Context mContext, View mView) {
        if (null == mContext || null == mView) return;
        InputMethodManager mInputMethodManager = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(mView.getWindowToken(), 0);
    }

}
