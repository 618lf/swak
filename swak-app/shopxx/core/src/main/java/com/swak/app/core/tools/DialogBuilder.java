package com.swak.app.core.tools;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.swak.app.core.R;
import com.swak.app.core.listeners.OnDelayListener;
import com.swak.app.core.view.progressing.sprite.SpriteContainer;
import com.swak.app.core.view.progressing.style.ChasingDots;
import com.swak.app.core.view.progressing.style.Circle;
import com.swak.app.core.view.progressing.style.CubeGrid;
import com.swak.app.core.view.progressing.style.DoubleBounce;
import com.swak.app.core.view.progressing.style.FadingCircle;
import com.swak.app.core.view.progressing.style.FoldingCube;
import com.swak.app.core.view.progressing.style.MultiplePulse;
import com.swak.app.core.view.progressing.style.MultiplePulseRing;
import com.swak.app.core.view.progressing.style.ThreeBounce;
import com.swak.app.core.view.progressing.style.WanderingCubes;
import com.swak.app.core.view.progressing.style.Wave;

/**
 * 作者：kkan on 2017/12/04 10:36
 * 当前类注释:
 * Dialog构造器
 */
public class DialogBuilder {

    private Context context;

    private Dialog dialog;

    private int themeResId = R.style.NormalDialogStyle;//style

    private boolean cancelable = false;//点击边缘是否可以消失

    private boolean baseviewisloading = false;//true 显示 加载dialog

    private CharSequence dialog_title = "";//标题

    private CharSequence dialog_message = "";//信息

    private Drawable messageDrawable;//drawable

    private int drawableseat = 1;//drawable 位置

    private long canceltime = 0;//自动消失的时间

    private CharSequence dialog_leftstring = "";//左边文字

    private int leftcolor = R.color.dodgerblue;//颜色

    private View.OnClickListener leftlistener = null;//监听

    private CharSequence dialog_rightstring = "";//右边文字

    private int rightcolor = R.color.gray;//颜色

    private View.OnClickListener rightlistener = null;//监听

    /**
     * 上下文
     */
    public DialogBuilder(Context context) {
        this.context = context;
    }

    /**
     * 弹窗
     */
    private DialogBuilder setDialog(Dialog dialog) {
        this.dialog = dialog;
        return this;
    }

    /**
     * 主题
     */
    public DialogBuilder setThemeResId(@StyleRes int themeResId) {
        if (themeResId != 0) {
            this.themeResId = themeResId;
        }
        return this;
    }

    /**
     * 点击边缘是否消失
     */
    public DialogBuilder setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    /**
     * 自动消失的时间
     */
    public DialogBuilder setCanceltime(long canceltime) {
        if (canceltime != 0) {
            this.canceltime = canceltime;
        }
        return this;
    }

    /**
     * 标题
     */
    public DialogBuilder setDialog_title(CharSequence dialog_title) {
        this.dialog_title = dialog_title;
        return this;
    }

    /**
     * 消息内容
     */
    public DialogBuilder setDialog_message(CharSequence dialog_message) {
        this.dialog_message = dialog_message;
        return this;
    }

    /**
     * 子页面
     */
    private DialogBuilder setMessageView(Drawable messageDrawable) {
        this.messageDrawable = messageDrawable;
        return this;
    }

    /**
     * 子页面位置
     * 0 left, 1 top,2 right,3 bottom
     */
    public DialogBuilder setDrawableseat(int drawableseat) {
        this.drawableseat = drawableseat;
        return this;
    }

    public Drawable getMessageView() {
        return messageDrawable;
    }

    /**
     * 左边的文字
     */
    public DialogBuilder setDialog_Left(CharSequence dialog_leftstring) {
        if (dialog_leftstring != null && !dialog_leftstring.equals("")) {
            this.dialog_leftstring = dialog_leftstring;
        }
        return this;
    }

    /**
     * 左边的文字颜色
     */
    public DialogBuilder setLeftcolor(int leftcolor) {
        if (leftcolor != 0) {
            this.leftcolor = leftcolor;
        }
        return this;
    }

    /**
     * 左边的文字点击事件
     */
    public DialogBuilder setLeftlistener(View.OnClickListener leftlistener) {
        if (leftlistener != null) {
            this.leftlistener = leftlistener;
        }
        return this;
    }

    /**
     * 右边的文字
     */
    public DialogBuilder setDialog_Right(CharSequence dialog_rightstring) {
        if (dialog_rightstring != null && !dialog_rightstring.equals("")) {
            this.dialog_rightstring = dialog_rightstring;
        }
        return this;
    }

    /**
     * 右边的文字颜色
     */
    public DialogBuilder setRightcolor(int rightcolor) {
        if (rightcolor != 0) {
            this.rightcolor = rightcolor;
        }
        return this;
    }

    /**
     * 右边的文字点击事件
     */
    public DialogBuilder setRightlistener(View.OnClickListener rightlistener) {
        if (rightlistener != null) {
            this.rightlistener = rightlistener;
        }
        return this;
    }

    /**
     * 加载对话框
     */
    public DialogBuilder setLoadingView(int color) {
        if (messageDrawable == null) {
            geCircle(color);
        }
        setLoadingView();
        return this;
    }

    /**
     * 加载对话框
     */
    public DialogBuilder setLoadingView() {
        baseviewisloading = true;
        setMessageView(messageDrawable);
        return this;
    }


    public DialogBuilder show() {
        if (dialog != null) {
            dialog.show();
            if (messageDrawable != null && messageDrawable instanceof SpriteContainer) {
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (messageDrawable != null && messageDrawable instanceof SpriteContainer) {
                            ((SpriteContainer) messageDrawable).stop();
                        }
                    }
                });
                ((SpriteContainer) messageDrawable).start();
            }
            if (canceltime != 0) {
                FutileTools.delayToDo(canceltime, new OnDelayListener() {
                    @Override
                    public void doSomething() {
                        dismissDialog();
                    }
                });
            }
        }
        return this;
    }

    public void dismissDialog() {
        if (context == null || isDestroyedCompatible()) {
            return;
        }
        if (dialog != null) {
            dialog.cancel();
            if (messageDrawable != null && messageDrawable instanceof SpriteContainer) {
                ((SpriteContainer) messageDrawable).stop();
            }
        }
        dialog = null;
        context = null;
    }

    /**
     * 判断页面是否已经被销毁（异步回调时使用）
     */
    private boolean isDestroyedCompatible() {
        if (Build.VERSION.SDK_INT >= 17) {
            return isDestroyedCompatible17();
        } else {
            return ((Activity) context).isFinishing();
        }
    }

    @TargetApi(17)
    private boolean isDestroyedCompatible17() {
        return ((Activity) context).isDestroyed();
    }

    public DialogBuilder builder() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new Dialog(context, themeResId);
        View mDialogView;
        if (baseviewisloading) {
            mDialogView = getLoading_Dialog();
        } else {
            mDialogView = getDefault_Dialog();
            int width = DeviceTools.getScreenWidth(context);
            setDialogSeat_Width((int) (width * 0.80));
        }
        dialog.setContentView(mDialogView);
//        dialog.setCancelable(cancelable);
        dialog.setCanceledOnTouchOutside(cancelable);

        if (baseviewisloading) {
            setDialogSeat_Gravity(Gravity.CENTER);
        } else {
            int width = DeviceTools.getScreenWidth(context);
            setDialogSeat_Width((int) (width * 0.80));
        }
        setDialog(dialog);
        return this;
    }

    private View getDefault_Dialog() {
        View mDialogView = View.inflate(context, R.layout.dialog_default, null);

        TextView dialogTitle = mDialogView.findViewById(R.id.dialog_title);
        TextView dialogMessageTv = mDialogView.findViewById(R.id.dialog_message_tv);
        TextView dialogLeftbtn = mDialogView.findViewById(R.id.dialog_leftbtn);
        TextView dialogLineV = mDialogView.findViewById(R.id.dialog_line_v);
        TextView dialogLineH = mDialogView.findViewById(R.id.dialog_line_h);
        TextView dialogRightbtn = mDialogView.findViewById(R.id.dialog_rightbtn);

        if (dialog_title != null && !dialog_title.equals("")) {
            dialogTitle.setVisibility(View.VISIBLE);
            dialogTitle.setText(dialog_title);
        }
        if (messageDrawable != null) {
            dialogMessageTv.setCompoundDrawables(drawableseat == 0 ? messageDrawable : null
                    , drawableseat == 1 ? messageDrawable : null
                    , drawableseat == 2 ? messageDrawable : null
                    , drawableseat == 3 ? messageDrawable : null);
            dialogMessageTv.setCompoundDrawablePadding(10);
        }
        dialogMessageTv.setText(dialog_message);
        if (dialog_leftstring != null && !dialog_leftstring.equals("")) {
            dialogLeftbtn.setVisibility(View.VISIBLE);
            dialogLeftbtn.setText(dialog_leftstring);
            dialogLeftbtn.setTextColor(ContextCompat.getColor(context, leftcolor));
            dialogLeftbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (leftlistener != null) {
                        leftlistener.onClick(view);
                    }
                }
            });
        }
        if (dialog_rightstring != null && !dialog_rightstring.equals("")) {
            dialogRightbtn.setVisibility(View.VISIBLE);
            dialogRightbtn.setText(dialog_rightstring);
            dialogRightbtn.setTextColor(ContextCompat.getColor(context, rightcolor));
            dialogRightbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    if (rightlistener != null) {
                        rightlistener.onClick(view);
                    }
                }
            });
        }
        if (dialogLeftbtn.getVisibility() == View.VISIBLE
                && dialogRightbtn.getVisibility() == View.VISIBLE) {
            dialogLineV.setVisibility(View.VISIBLE);
        } else if (dialogLeftbtn.getVisibility() == View.GONE
                && dialogRightbtn.getVisibility() == View.GONE) {
            dialogLineH.setVisibility(View.GONE);
        }
        return mDialogView;
    }

    private View getLoading_Dialog() {
        View loadingdialog = View.inflate(context, R.layout.dialog_loading, null);
        TextView loading_tv = loadingdialog.findViewById(R.id.loading_tv);
        if (messageDrawable != null) {
//            ImageView loading_iv = new ImageView(context);
//            loading_iv.setImageDrawable(messageDrawable);
            loading_tv.setCompoundDrawables(drawableseat == 0 ? messageDrawable : null
                    , drawableseat == 1 ? messageDrawable : null
                    , drawableseat == 2 ? messageDrawable : null
                    , drawableseat == 3 ? messageDrawable : null);
            loading_tv.setCompoundDrawablePadding(10);
        }
        loading_tv.setText(dialog_message);
        return loadingdialog;
    }

    private DialogBuilder setDialogSeat_Gravity(int gravity) {
        setDialogSeat(gravity, -234, -234);
        return this;
    }

    private DialogBuilder setDialogSeat_Width(int width) {
        setDialogSeat(Gravity.CENTER, width, -234);
        return this;
    }

    /**
     * 设置dialog位置 ViewGroup.LayoutParams.MATCH_PARENT
     * int width = DeviceTools.getScreenWidth(context);
     * (int) (width * 0.80)
     *
     * @param gravity Gravity.CENTER
     */
    private DialogBuilder setDialogSeat(int gravity, int width, int height) {
        if (dialog != null) {
            Window dialogWindow = dialog.getWindow();
            if (dialogWindow != null) {
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                if (width != -234) {
                    lp.width = width;
                }
                if (height != -234) {
                    lp.height = height;
                }
                dialogWindow.setAttributes(lp);
                dialogWindow.setGravity(gravity);
            }
        }
        return this;
    }

    /**
     * 加载动画
     * 圆圈 菊花
     */
    public DialogBuilder geCircle(int color) {
        messageDrawable = null;
        if (context != null) {
            Circle circle = new Circle();
            //left 组件在容器X轴上的起点
            // top 组件在容器Y轴上的起点
            // right 组件的长度
            // bottom 组件的高度
            circle.setBounds(0, 0, 70, 90);
            circle.setColor(context.getResources().getColor(color));
            messageDrawable = circle;
        }
        return this;
    }

    /**
     * 加载动画
     * 三点上下晃动
     */
    public DialogBuilder getThreeBounce(int color) {
        messageDrawable = null;
        if (context != null) {
            ThreeBounce threeBounce = new ThreeBounce();
            threeBounce.setBounds(0, 0, 70, 90);
            threeBounce.setColor(context.getResources().getColor(color));
            messageDrawable = threeBounce;
        }
        return this;
    }

    /**
     * 加载动画
     * 两点上下晃动
     */
    public DialogBuilder getDoubleBounce(int color) {
        messageDrawable = null;
        if (context != null) {
            DoubleBounce doubleBounce = new DoubleBounce();
            doubleBounce.setBounds(0, 0, 70, 90);
            doubleBounce.setColor(context.getResources().getColor(color));
            messageDrawable = doubleBounce;
        }
        return this;
    }

    /**
     * 加载动画
     * 波浪
     */
    public DialogBuilder getWave(int color) {
        messageDrawable = null;
        if (context != null) {
            Wave wave = new Wave();
            wave.setBounds(0, 0, 70, 90);
            wave.setColor(context.getResources().getColor(color));
            messageDrawable = wave;
        }
        return this;
    }

    /**
     * 加载动画
     * 对角旋转正方体
     */
    public DialogBuilder getWanderingCubes(int color) {
        messageDrawable = null;
        if (context != null) {
            WanderingCubes wanderingCubes = new WanderingCubes();
            wanderingCubes.setBounds(0, 0, 70, 90);
            wanderingCubes.setColor(context.getResources().getColor(color));
            messageDrawable = wanderingCubes;
        }
        return this;
    }

    /**
     * 加载动画
     * 点追逐
     */
    public DialogBuilder geChasingDots(int color) {
        messageDrawable = null;
        if (context != null) {
            ChasingDots chasingDots = new ChasingDots();
            chasingDots.setBounds(0, 0, 70, 90);
            chasingDots.setColor(context.getResources().getColor(color));
            messageDrawable = chasingDots;
        }
        return this;
    }

    /**
     * 加载动画
     * 正方体网格
     */
    public DialogBuilder geCubeGrid(int color) {
        messageDrawable = null;
        if (context != null) {
            CubeGrid cubeGrid = new CubeGrid();
            cubeGrid.setBounds(0, 0, 70, 90);
            cubeGrid.setColor(context.getResources().getColor(color));
            messageDrawable = cubeGrid;
        }
        return this;
    }

    /**
     * 加载动画
     * 衰退圆圈
     */
    public DialogBuilder geFadingCircle(int color) {
        messageDrawable = null;
        if (context != null) {
            FadingCircle fadingCircle = new FadingCircle();
            fadingCircle.setBounds(0, 0, 70, 90);
            fadingCircle.setColor(context.getResources().getColor(color));
            messageDrawable = fadingCircle;
        }
        return this;
    }

    /**
     * 加载动画
     * 折叠正方体
     */
    public DialogBuilder geFoldingCube(int color) {
        messageDrawable = null;
        if (context != null) {
            FoldingCube foldingCube = new FoldingCube();
            foldingCube.setBounds(0, 0, 70, 90);
            foldingCube.setColor(context.getResources().getColor(color));
            messageDrawable = foldingCube;
        }
        return this;
    }


    /**
     * 加载动画
     * 复杂脉冲
     */
    public DialogBuilder geMultiplePulse(int color) {
        messageDrawable = null;
        if (context != null) {
            MultiplePulse multiplePulse = new MultiplePulse();
            multiplePulse.setBounds(0, 0, 70, 90);
            multiplePulse.setColor(context.getResources().getColor(color));
            messageDrawable = multiplePulse;
        }
        return this;
    }

    /**
     * 加载动画
     * 复杂环形脉冲
     */
    public DialogBuilder geMultiplePulseRing(int color) {
        messageDrawable = null;
        if (context != null) {
            MultiplePulseRing multiplePulseRing = new MultiplePulseRing();
            multiplePulseRing.setBounds(0, 0, 70, 90);
            multiplePulseRing.setColor(context.getResources().getColor(color));
            messageDrawable = multiplePulseRing;
        }
        return this;
    }
}
