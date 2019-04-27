package com.swak.app.core.tools;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * com.afollestad.material-dialogs:core
 * <p>
 * 使用此dialog
 */
public class DialogTools {

    /**
     * Dialog 构建器
     *
     * @param context
     * @return
     */
    public static MaterialDialog.Builder builder(Context context) {
        return new MaterialDialog.Builder(context);
    }
}