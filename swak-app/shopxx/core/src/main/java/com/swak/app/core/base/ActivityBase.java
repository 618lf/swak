package com.swak.app.core.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.swak.app.core.tools.ActivityTools;
import com.swak.app.core.tools.AntiShake;
import com.swak.app.core.tools.DialogBuilder;
import com.swak.app.core.view.swipeback.SwipeBackLayout;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Base Activity
 */
public abstract class ActivityBase extends AppCompatActivity {

    protected WeakReference<Activity> context;
    private Unbinder unbinder;
    protected AntiShake antiShake;//防止重复点击
    protected String TAG;
    protected DialogBuilder dialogBuilder;
    protected SwipeBackLayout swipeBackLayout;

    /*********************子类实现 -- start *****************************/
    //获取布局文件
    public abstract int getLayoutId();

    //初始化view
    public abstract void initView(Bundle savedInstanceState);

    /*********************子类实现 -- end *****************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 防止重复点击
        antiShake = new AntiShake();

        // 设置布局
        setContentView(getLayoutId());

        // actvity 弱引用
        context = new WeakReference<Activity>(this);

        // 把actvity放到栈中管理
        ActivityTools.getActivityTool().addActivity(context);
        unbinder = ButterKnife.bind(this);
        TAG = context.getClass().getSimpleName();

        // 子类初始化
        _initView(savedInstanceState);
    }

    // 方便子类确定初始化顺序
    protected void _initView(Bundle savedInstanceState) {
        initView(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
        //关闭Dialog
        destroyDialogBuilder();
        //移除栈中的actvity
        ActivityTools.getActivityTool().finishActivity(context);
    }

    /**
     * 获取当前Activity，也可以直接使用内部成员变量mActivity
     *
     * @return
     */
    protected Activity getContext() {
        if (null != context)
            return context.get();
        else
            return null;
    }

    /**
     * 创建 Dialog Builder
     *
     * @return
     */
    public DialogBuilder creatDialogBuilder() {
        destroyDialogBuilder();
        dialogBuilder = new DialogBuilder(context.get());
        return dialogBuilder;
    }

    /**
     * 销毁 Dialog Builder
     */
    public void destroyDialogBuilder() {
        if (dialogBuilder != null) {
            dialogBuilder.dismissDialog();
            dialogBuilder = null;
        }
    }

    /**
     * 支持滑动返回
     * -1 关闭 0左滑 1 右滑 2 上滑 3下滑
     * direction 滑动方向
     * activity style 必须设置Theme.Swipe.Back.NoActionBar
     */
    protected void setSwipeBackLayout(int issupswipebace) {
        int direction = -1;
        switch (issupswipebace) {
            case 0:
                direction = SwipeBackLayout.FROM_LEFT;
                break;
            case 1:
                direction = SwipeBackLayout.FROM_RIGHT;
                break;
            case 2:
                direction = SwipeBackLayout.FROM_TOP;
                break;
            case 3:
                direction = SwipeBackLayout.FROM_BOTTOM;
                break;
        }
        if (direction != -1) {
            if (swipeBackLayout == null) {
                swipeBackLayout = new SwipeBackLayout(this);
                swipeBackLayout.attachToActivity(this);
                swipeBackLayout.setMaskAlpha(125);
                swipeBackLayout.setSwipeBackFactor(0.5f);
            }
            swipeBackLayout.setDirectionMode(direction);
            swipeBackLayout.setSwipeFromEdge(false);//是否只能从边缘滑动退出
        }
    }
}
