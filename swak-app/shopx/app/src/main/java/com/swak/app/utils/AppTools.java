package com.swak.app.utils;


import android.content.Context;

import com.swak.app.api.AppConstant;
import com.swak.app.model.UserBean;
import com.veni.tools.DataTools;
import com.veni.tools.EncryptTools;
import com.veni.tools.FutileTools;
import com.veni.tools.JsonTools;
import com.veni.tools.LogTools;
import com.veni.tools.SPTools;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * 作者：kkan on 2018/02/26
 * 当前类注释:
 * 依赖中没有的或者需要经常更改的Tools
 */

public class AppTools {

    private static final String TAG = AppTools.class.getSimpleName();
    /**
     * AES 密钥
     */
    public static final String SECRETKEY = "jingtum2017tudou";

    /**
     * SP  用户数据
     */
    public static final String USERDATA = "user_data";


    public static String getToken(){
        return (String) SPTools.get(FutileTools.getContext(), AppConstant.KEY_ACCESS_TOKEN, "");
    }

    public static void saveToken(Context context, String token){
        SPTools.put(context, AppConstant.KEY_ACCESS_TOKEN, token);
    }

    public static void saveUserBean(Context context, String userdata) {
        SPTools.put(context, USERDATA, userdata);
    }

    public static UserBean getUserBean(Context context) {
        String value = (String) SPTools.get(context, USERDATA, "");
        UserBean userBean = JsonTools.parseObject(AppTools.desAESCode(value), UserBean.class);
        return userBean == null ? new UserBean() : userBean;
    }


    public static String encAESCode(HashMap<String, Object> param) {
        String content = JsonTools.toJson(param);
        LogTools.d(TAG, "加密前数据-->" + content);
        if (content == null) {
            return "";
        }
        byte[] conb = content.getBytes();
        byte[] secreb = SECRETKEY.getBytes();
        String encryptResultStr = EncryptTools.encryptAES2HexString(conb, secreb);
        LogTools.d(TAG, "加密后-->" + encryptResultStr);
        return encryptResultStr;
    }

    public static String desAESCode(String content) {
        if (DataTools.isEmpty(content)) {
            LogTools.d(TAG, "解密字符为空");
            return "";
        }
        LogTools.d(TAG, "解密前json数据--->" + content);
        byte[] secreb = SECRETKEY.getBytes();
        byte[] decryptResult = EncryptTools.decryptHexStringAES(content, secreb);
        String decryptString = null;
        try {
            decryptString = new String(decryptResult, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
        }
        LogTools.d(TAG, "解密后json数据--->" + decryptString);
        return decryptString;
    }
}
