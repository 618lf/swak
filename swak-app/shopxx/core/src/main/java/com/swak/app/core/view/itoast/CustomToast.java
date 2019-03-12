package com.swak.app.core.view.itoast;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.swak.app.core.tools.ImageTools;

/**
 * 作者：kkan on 2018/11/23 9:39
 * <p>
 * 当前类注释:
 */
public class CustomToast implements IToast {

    private final static String TAG = "CustomToast";
    private final Object mLock;
    private static final Handler sHandler ;
    private static final int MSG_SHOW = 0;
    private static final int MSG_DISMISS = 1;

    static {
        sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case MSG_SHOW:
                        ((CustomToast) message.obj).showView();
                        return true;
                    case MSG_DISMISS:
                        ((CustomToast) message.obj).hideView();
                        return true;
                }
                return false;
            }
        });
    }

    private WindowManager mWindowManager;

    private long mDuration;

    private View mView;
    private View nextView;

    private WindowManager.LayoutParams mParams;

    private Context mContext;

    /**
     * 参照Toast源码TN()写
     *
     * @param context
     */
    public CustomToast(Context context) {
        mLock = new Object();
        mContext = context;

        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.windowAnimations = android.R.style.Animation_Toast;
        mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mParams.setTitle("Toast");
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager
                .LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        // 默认居中
//        mParams.gravity = Gravity.CENTER;
        mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        mParams.y = ImageTools.dipToPx(context, 64);
    }

    /**
     * Set the location at which the notification should appear on the screen.
     *
     * @param gravity
     * @param xOffset
     * @param yOffset
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public IToast setGravity(int gravity, int xOffset, int yOffset) {
        // We can resolve the Gravity here by using the Locale for getting
        // the layout direction
        final int finalGravity;
        if (Build.VERSION.SDK_INT >= 14) {
            final Configuration config = nextView.getContext().getResources().getConfiguration();
            finalGravity = Gravity.getAbsoluteGravity(gravity, config.getLayoutDirection());
        } else {
            finalGravity = gravity;
        }
        mParams.gravity = finalGravity;
        if ((finalGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
            mParams.horizontalWeight = 1.0f;
        }
        if ((finalGravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
            mParams.verticalWeight = 1.0f;
        }
        mParams.y = yOffset;
        mParams.x = xOffset;
        return this;
    }

    @Override
    public IToast setDuration(int duration) {
        if (duration == Toast.LENGTH_SHORT) {
            mDuration = 2000;
        } else if (duration == Toast.LENGTH_LONG) {
            mDuration = 3500;
        } else if (duration < 0) {
            mDuration = 0;
        }else{
            mDuration = duration;
        }
        return this;
    }

    /**
     * 不能和{@link #setText(String)}一起使用，要么{@link #setText(String)} 要么{@link #setView(View)}
     *
     * @param view 传入view
     * @return 自身对象
     */
    @Override
    public IToast setView(View view) {
        nextView = view;
        return this;
    }

    @Override
    public IToast setMargin(float horizontalMargin, float verticalMargin) {
        mParams.horizontalMargin = horizontalMargin;
        mParams.verticalMargin = verticalMargin;
        return this;
    }

    /**
     * 不能和{@link #setView(View)}一起使用，要么{@link #setText(String)} 要么{@link #setView(View)}
     *
     * @param text 字符串
     * @return 自身对象
     */
    public IToast setText(String text) {
        // 模拟Toast的布局文件 com.android.internal.R.layout.transient_notification
        // 虽然可以手动用java写，但是不同厂商系统，这个布局的设置好像是不同的，因此我们自己获取原生Toast的view进行配置
        View view = Toast.makeText(mContext, text, Toast.LENGTH_SHORT).getView();
        if (view != null) {
            TextView tv = (TextView) view.findViewById(android.R.id.message);
            tv.setText(text);
            setView(view);
        }
        return this;
    }

    @Override
    public void show() {
        handleshow();
    }

    @Override
    public void cancel() {
        sHandler.removeCallbacksAndMessages("");
        handleTimeout();
    }


    private void handleshow() {
        synchronized (mLock) {
            sHandler.removeCallbacksAndMessages("");
            sHandler.sendMessage(sHandler.obtainMessage(MSG_SHOW, CustomToast.this));
            sHandler.sendMessageDelayed(sHandler.obtainMessage(MSG_DISMISS, CustomToast.this), mDuration);
        }
    }


    private void handleTimeout() {
        synchronized (mLock) {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_DISMISS, CustomToast.this));
        }
    }

    final void showView() {
        if (nextView != null) {
            if (mView != null&&mView.getParent() != null) {
                mWindowManager.removeView(mView);
                mView=null;
            }
            mWindowManager.addView(nextView, mParams);
            mView=nextView;
        }
    }

    final void hideView() {
        if (mView != null&&mView.getParent() != null) {
            mWindowManager.removeView(mView);
        }
    }

}
