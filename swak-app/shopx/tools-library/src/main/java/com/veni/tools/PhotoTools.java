package com.veni.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 相册 相机 图片工具类
 * Created by kkan on 2017/8/9.
 */

public class PhotoTools {


    /**
     * 使用方式:
     *
     * 1.获取拍照原图Bitmap:  PhotoTools.onInitPhoneResult(this,requestCode,resultCode,data,"init.jpg")
     *   参数列表：上下文环境，请求码，响应码，返回的数据，图片名称
     *   注：请求码，响应码，返回的数据是onActivityResult（）方法的参数
     * 2.获取一定压缩比例的原图
     *   （1）方式一：拍照保存：PhotoTools.onInitPhoneResult(this,requestCode,resultCode,data,"init.jpg")
     *                将拍照保存路径下的照片进行处理  PhotoTools.getBitmapOption(Environment.getExternalStorageDirectory() + "/phone/init.jpg", 20))
     *                参数：拍照保存的全路径，缩放比列（20表示缩放比为原图的1/20）
     *   （2）方式二：将现有的bitmap进行压缩处理：
     *                将bitmap保存：PhotoTools.saveBitmapFile(bitmap,filePath);
     *                   参数：有效的bitmap，保存的全路径
     *                将保存路径下的bitmap进行压缩：PhotoTools.getBitmapOption(filePath, 20))
     *                   参数：保存的全路径，缩放比列
     * 3.保存一个bitmap:PhotoTools.saveBitmapFile(bitmap,filePath);
     *                   参数：有效的bitmap，保存的全路径
     * 4.缩放一个一个路径下的bitmap：PhotoTools.getBitmapOption(filePath, 20))
     *                   参数：保存的全路径，缩放比列
     * 5.获取一个拍照缩略图：
     *                拍照：PhotoTools.getAbbreviationsPhone(this);
     *                在onActivityResult（）中PhoneUtils.onAbbreviationsPhoneResult(this,requestCode,resultCode,data)
     *                   参数：上下文，请求码，响应码，返回数据
     * 6.获取相册选择的单个图片：
     *   （1）：得到一个图片的bitmap：
     *          调用相册：PhotoTools.getAlbumPhone(上下文环境);
     *          在onActivityResult（）中PhoneUtils.onmyPhoneResult(this,requestCode,resultCode,data);得到bitmap
     *   （2）：得到一个图片的file:
     *          调用相册：PhotoTools.getAlbumPhone(上下文环境);
     *          在onActivityResult（）中PhoneUtils.onmyPhoneFileResult(this,requestCode,resultCode,data);得到file
     * 7.获取拍照剪切图片：
     *      调用PhoneUtils.getSheraPack(this,"cut.jpg");
     *      在在onActivityResult（）中监听PHONE_CODE_SHEAR_PACK响应码调用Uri packUri =PhotoTools.onInitCutPhoneResult(this,requestCode,resultCode,data,"cut.jpg");获得拍照的原图
     *      在调用PhoneUtils.crop(this,packUri)进去截图
     *      在在onActivityResult（）中监听PHONE_CODE__CUT截图完毕响应码  Bitmap photo=data.getParcelableExtra("data");
     *      获得phone这个截图后的bitmap
     * 8.获取相册剪切图片：
     *      调用PhoneUtils.getSheraMyPhone(this);进入相册请求图片
     *      在在onActivityResult（）中监听PHONE_CODE__SHEAR_MYPHONE响应码调用Uri myUri=PhotoTools.onCutmyPhoneResult(this,requestCode,resultCode,data);;获得相册图片uri
     *      在调用PhoneUtils.crop(this,myUri)进去截图
     *      在在onActivityResult（）中监听PHONE_CODE__CUT截图完毕响应码  Bitmap photo=data.getParcelableExtra("data");
     *      获得phone这个截图后的bitmap
     * 9.选择相册多张图片：
     *
     */

    //拍照获取缩略图的请求码
    public final static int PHONE_CODE_ABBPHONE=0;
    //拍照获取相册图片的请求码
    public final static int PHONE_CODE_ALBUNMPHONE=1;
    //拍照获原图图的请求码
    public final static int PHONE_CODE_INITPACKPHONE=2;
    //拍照剪切图片请求码
    public final static int PHONE_CODE_SHEAR_PACK=3;
    //相册剪切图片请求码
    public final static int PHONE_CODE__SHEAR_MYPHONE=4;
    //剪切请求码
    public final static int PHONE_CODE__CUT=5;

    /**
     * 调用系统相机获取拍照缩略图
     */
    public static void getAbbreviationsPhone(Activity activity){
        //为安全起见，拿到sdcard是否可用的状态码
        String state = Environment.getExternalStorageState();
        //如果sd卡可用
        if (state.equals(Environment.MEDIA_MOUNTED)){ //如果可用
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            activity.startActivityForResult(intent,PHONE_CODE_ABBPHONE);
        }else {
            Toast.makeText(activity,"sdcard状态异常", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 调用系统相册获取获取单张图片uri
     */
    public static void getAlbumPhone(Activity activity){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        activity.startActivityForResult(intent,PHONE_CODE_ALBUNMPHONE);
    }

    /**
     * 调用相机获取剪切图
     */
    public static void getSheraPack(Activity activity, String fileName){
        //先验证手机是否有sdcard
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                //将拍摄的照片保存在一个指定好的文件下
                File dir= new File(Environment.getExternalStorageDirectory() + "/DCIM/Cut");
                if (!dir.exists()) dir.mkdirs();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(dir, fileName);
                Uri u = Uri.fromFile(f);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                activity.startActivityForResult(intent, PHONE_CODE_SHEAR_PACK);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "没有找到储存目录", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity, "sdcard状态异常", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 调用相册获取剪切图
     */
    public static void getSheraMyPhone(Activity activity){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        activity.startActivityForResult(intent,PHONE_CODE__SHEAR_MYPHONE);
    }

    /**
     * 调用本地相机获取拍照原图
     * filename 图片文件名 xxx.jpg
     * 返回值 图片的路径
     * 如果失败返回空
     */
    public static void getInitPhone(Activity activity, String fileName) {
        //先验证手机是否有sdcard
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                //将拍摄的照片保存在一个指定好的文件下
                File dir= new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/");
                if (!dir.exists()) dir.mkdirs();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(dir, fileName);
                Uri u = Uri.fromFile(f);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                activity.startActivityForResult(intent, PHONE_CODE_INITPACKPHONE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, "没有找到储存目录", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity, "sdcard状态异常", Toast.LENGTH_LONG).show();
        }
    }

    /**
     *  onActivityResult拍照后返回原图
     */
    public static Bitmap onInitPhoneResult(Activity activity, int requestCode, int resultCode, Intent data, String fileName){
        try {

            File dir= new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");//将要保存图片的路径
            if (!dir.exists()) dir.mkdirs();
            File f=new File(dir,fileName);
            Uri u = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), f.getAbsolutePath(), null, null));
            return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), u);
        } catch (FileNotFoundException ignored) {
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * 获取 保存的文件路径
     */
    public static String getPhotoPath(String fileName, int type){
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/"+(type==0?"Camera":"Cut")+"/"+fileName);
        if (file.exists() && file.isFile()) {
            return file.getPath();
        }
        return "";
    }

    /**
     * 删除单个文件
     *
     */
    public static boolean deleteFile(String fileName, int type) {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/"+(type==0?"Camera":"Cut")+"/"+fileName);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }
    /**
     *  onActivityResult拍照后返回原图Uri, 剪切常用
     */
    public static Uri onInitCutPhoneResult(Activity activity, int requestCode, int resultCode, Intent data, String fileName){

        File dir= new File(Environment.getExternalStorageDirectory() + "/DCIM/Cut");//将要保存图片的路径
        if (!dir.exists()) dir.mkdirs();
        Uri u = null;
        try {
            u = Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), dir.getAbsolutePath()+"/"+fileName, null, null));
        } catch (FileNotFoundException ignored) {
        }
        return u;
    }

    /**
     *  onActivityResult拍照后返回缩略图的bitmap
     */
    public static Bitmap onAbbreviationsPhoneResult(Activity activity, int requestCode, int resultCode, Intent data){
        Bitmap phone = null;
        if (data.getData() != null|| data.getExtras() != null){
            Uri uri =data.getData();
            if (uri != null) {
                phone = BitmapFactory.decodeFile(uri.getPath());
                if (phone!=null){
                    return phone;
                }
            }
        }
        if (phone == null) {
            Bundle bundle =data.getExtras();
            if (bundle != null){
                phone =(Bitmap) bundle.get("data");
                if (phone!=null){
                    return phone;
                }
            } else {
                Toast.makeText(activity.getApplicationContext(), "找不到图片", Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    /**
     *  onActivityResult相册返回的bitmap
     */
    public static Bitmap onmyPhoneResult(Activity activity, int requestCode, int resultCode, Intent data){
        Bitmap phone = null;
        Uri uri = data.getData();
        if (uri!=null){
            try {
                phone = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                if (phone!=null){
                    return phone;
                }
            } catch (IOException ignored) {
            }
        }else {
            Toast.makeText(activity.getApplicationContext(), "找不到该图片", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     *  onActivityResult相册返回的uri  截图多用
     */
    public static Uri onCutmyPhoneResult(Activity activity, int requestCode, int resultCode, Intent data){
        Bitmap phone = null;
        Uri uri = data.getData();
        if (uri!=null){
            return uri;
        }else {
            Toast.makeText(activity.getApplicationContext(), "找不到该图片", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     *  onActivityResult相册返回的file
     */
    public static File onmyPhoneFileResult(Activity activity, int requestCode, int resultCode, Intent data){
        Bitmap phone = null;
        Uri uri = data.getData();
        if (uri!=null){
            return uriToFile(activity,uri);
        }else {
            Toast.makeText(activity.getApplicationContext(), "找不到该图片", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * 将bitmap保存到指定的文件路径下
     * filepath:"/mnt/sdcard/pic/01.jpg"
     */
    public static String saveBitmapFile(Bitmap bitmap, String filename, int type){
        File dir= new File(Environment.getExternalStorageDirectory() + "/DCIM/"+(type==0?"Camera":"Cut"));//将要保存图片的路径
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, filename);
        BufferedOutputStream bos =null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.close();
            return file.getPath();
        } catch (IOException ignored) {
        } finally {
            if (bos != null) {
                try {
//                    bos.flush();
                    bos.close();
                } catch (IOException ignored) {
                }
            }
        }
        return "";
    }

    /**
     *  将指定路径下的图片缩小到原来的几分之一 返回bitmap.当inSampleSize=2时，表示缩小1/2
     */
    public static Bitmap getBitmapOption(String filePath, int inSampleSize){
//        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(filePath,options);
    }

    /**
     * uri转file
     */
    public static File uriToFile(Activity activity, Uri uri){

        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor actualimagecursor = activity.managedQuery(uri, proj, null, null, null);

        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        actualimagecursor.moveToFirst();

        String img_path = actualimagecursor.getString(actual_image_column_index);

        File file = new File(img_path);
        return file;
    }

    /**
     * bitmap转uri
     */
    public static Uri bitmapToUri(Activity activity, Bitmap bitmap){
        return  Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, null,null));
    }

    /**
     * uri转bitmap
     */
    public static Bitmap uriToBitmap(Activity activity, Uri uri){
        Bitmap bitmap=null;
        try {
            bitmap= MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        } catch (IOException e) {

        }
        return bitmap;
    }

    /**
     * 剪切图片,比列和尺寸可根据情况自行更改
     */
    public static void crop(Activity activity, Uri uri, int outputX, int outputY) {

        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String url=getPath(activity,uri);
            intent.setDataAndType(Uri.fromFile(new File(url)), "image/*");
        }else{
            intent.setDataAndType(uri, "image/*");
        }
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", outputX);
        intent.putExtra("aspectY", outputY);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        activity.startActivityForResult(intent, PHONE_CODE__CUT);
    }

    //以下是关键，原本uri返回的是file:///...来着的，android4.4返回的是content:///...
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
