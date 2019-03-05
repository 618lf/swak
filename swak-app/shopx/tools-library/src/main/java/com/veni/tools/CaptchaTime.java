package com.veni.tools;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * 作者：kkan on 2017/12/19
 * 当前类注释:
 * 验证码倒计时
 */

public class CaptchaTime extends CountDownTimer {
    private TextView codeTv;
    private boolean countisfinish = true;

    /**
     * @param codeTv         验证码 TextView
     * @param millisInFuture 倒计时 分钟
     */
    public CaptchaTime(TextView codeTv, long millisInFuture) {
        super(millisInFuture * 1000, 1000);
        this.codeTv = codeTv;
        start();
    }

    public boolean isfinish() {
        return countisfinish;
    }

    @Override
    public void onFinish() {
        codeTv.setEnabled(true);
        codeTv.setText("重新获取");
        countisfinish = true;

    }

    @Override
    public void onTick(long millisUntilFinished) {
        codeTv.setEnabled(false);
        countisfinish = false;
        codeTv.setText(millisUntilFinished / 1000 + "s" + "后重发");
    }
}
