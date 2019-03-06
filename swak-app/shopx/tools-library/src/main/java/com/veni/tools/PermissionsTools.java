package com.veni.tools;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kkan on 2017/8/18.
 * 权限请求操作工具类
 * addPermission               : 添加权限
 * initPermission              : 请求权限
 */
  /*
     * 注册权限申请回调
     *
     * @param requestCode  申请码
     * @param permissions  申请的权限
     * @param grantResults 结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogTools.e(TAG,"requestCode"+requestCode);
        LogTools.e(TAG,"permissions"+JsonTools.toJson(permissions));
        LogTools.e(TAG,"grantResults--"+ JsonTools.toJson(grantResults));
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //权限通过
                } else {PERMISSION_DENIED
                    //权限拒绝
                }
            }
        }
    }*/
public class PermissionsTools {

    public static PermissionsTools.Builder with(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {

        private Activity mActivity;
        private List<String> permissionList;

        public Builder(@NonNull Activity activity) {
            mActivity = activity;
            permissionList = new ArrayList<>();
        }

        /**
         * Determine whether <em>you</em> have been granted a particular permission.
         *
         * @param permission 申请的权限.
         * @return 允许这个权限返回{@link PackageManager#PERMISSION_GRANTED} ,
         * 拒绝权限返回 {@link PackageManager#PERMISSION_DENIED}.
         * @see PackageManager#checkPermission(String, String)
         */
        public Builder addPermission(@NonNull String permission) {
            if (!permissionList.contains(permission)) {
                permissionList.add(permission);
            }
            return this;
        }

        public List<String> initPermission() {
            List<String> list = new ArrayList<>();
            for (String permission : permissionList) {
                if (ActivityCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                    list.add(permission);
                }
            }
            if (list.size() > 0) {
                ActivityCompat.requestPermissions(mActivity, list.toArray(new String[list.size()]), 1);
            }
            return list;
        }

    }


}
