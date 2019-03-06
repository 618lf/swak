package com.veni.tools.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.veni.tools.ActivityTools;
import com.veni.tools.ToolBarUtils;

import java.lang.ref.WeakReference;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * 基类Activity
 */
public class ActivityBase extends AppCompatActivity {

    protected WeakReference<Activity> context;
    protected AlertDialogBuilder dialogBuilder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = new WeakReference<Activity>(this);
        // 把actvity放到栈中管理
        ActivityTools.getActivityTool().addActivity(context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭Dialog
        destroyDialogBuilder();
        //移除栈中的actvity
        ActivityTools.getActivityTool().finishActivity(context);
    }

    /**
     * Toolbar设置为可点击
     */
    protected void onCreateCustomToolBar(Toolbar toolbarBaseTb) {
        onCreateCustomToolBar(toolbarBaseTb, true);
    }

    /**
     * Toolbar是否可点击
     *
     * @param homeAsUpEnabled 是否可点击
     */
    protected void onCreateCustomToolBar(Toolbar toolbarBaseTb, boolean homeAsUpEnabled) {
        ToolBarUtils.getToolBarUtils().onCreateCustomToolBar(context.get(), toolbarBaseTb, homeAsUpEnabled);
    }

    /**
     * 获取默认Dialog
     */
    public AlertDialogBuilder creatDialogBuilder() {
        destroyDialogBuilder();
        dialogBuilder = new AlertDialogBuilder(context.get());
        return dialogBuilder;
    }

    public void destroyDialogBuilder() {
        if (dialogBuilder != null) {
            dialogBuilder.dismissDialog();
            dialogBuilder = null;
        }
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
}
