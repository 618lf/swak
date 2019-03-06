package com.swak.app.core.ui.dialog;

import android.app.Activity;
import android.view.Gravity;
import android.widget.TextView;

import com.swak.app.core.R;
import com.swak.app.core.ui.BaseDialog;

public class LoadingDialog extends BaseDialog {

    /**
     * 消息TextView
     */
    private TextView tvMsg;

    public LoadingDialog(Activity context, String strMessage) {
        this(context, R.style.Anl_CustomProgressDialog, strMessage);
    }

    public LoadingDialog(Activity context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(R.layout.anl_common_loading_dialog);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        tvMsg = (TextView) this.findViewById(R.id.tv_msg);
        setMessage(strMessage);
    }

    /**
     * 设置进度条消息
     *
     * @param strMessage 消息文本
     */
    public void setMessage(String strMessage) {
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
    }
}
