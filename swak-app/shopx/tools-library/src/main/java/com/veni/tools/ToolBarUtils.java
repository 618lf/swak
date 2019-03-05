package com.veni.tools;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * 作者：kkan on 2017/12/19
 * 当前类注释:
 */

public class ToolBarUtils {

    public static final int TITLE_MODE_LEFT = -1;
    public static final int TITLE_MODE_CENTER = -2;
    public static final int TITLE_MODE_NONE = -3;
    private int titleMode = TITLE_MODE_CENTER;
    private static ToolBarUtils toolBarUtils = null;

    public static ToolBarUtils getToolBarUtils() {
        if (toolBarUtils == null) {
            toolBarUtils = new ToolBarUtils();
        }
        return toolBarUtils;
    }

    /**
     * @param homeAsUpEnabled 是否可点击
     */
    public void onCreateCustomToolBar(Context context, Toolbar toolbarBaseTb, boolean homeAsUpEnabled) {
        ((AppCompatActivity) context).setSupportActionBar(toolbarBaseTb);
        toolbarBaseTb.setTitleTextColor(ContextCompat.getColor(context,
                R.color.primary_text_default_material_dark));
        if (homeAsUpEnabled) {
            ActionBar actionBar = ((AppCompatActivity) context).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setDisplayUseLogoEnabled(false);
                actionBar.setHomeAsUpIndicator(R.drawable.icon_previous);
            }
        }
    }
    public void setTitletext(Toolbar toolbarBaseTb, TextView toolbarTvTitle, CharSequence toolbarstring) {
        setTitletext(toolbarBaseTb, toolbarTvTitle,toolbarstring, titleMode);
    }
    public void setTitletext(Toolbar toolbarBaseTb, TextView toolbarTvTitle, CharSequence title, int mode) {
        this.titleMode = mode;
        if (titleMode == TITLE_MODE_NONE) {
            if (toolbarBaseTb != null) {
                toolbarBaseTb.setTitle("");
            }
            if (toolbarTvTitle != null) {
                toolbarTvTitle.setText("");
            }
        } else if (titleMode == TITLE_MODE_LEFT) {
            if (toolbarBaseTb != null) {
                toolbarBaseTb.setTitle(title);
            }
            if (toolbarTvTitle != null) {
                toolbarTvTitle.setText("");
            }
        } else if (titleMode == TITLE_MODE_CENTER) {
            if (toolbarBaseTb != null) {
                toolbarBaseTb.setTitle("");
            }
            if (toolbarTvTitle != null) {
                toolbarTvTitle.setText(title);
            }
        }
    }

    /**
     * Toolbar添加子布局
     *
     * @param layoutResId
     */
    public void setToolbarCustomView(Context context, Toolbar toolbarBaseTb, TextView toolbarTvTitle, @LayoutRes int layoutResId) {
        try {
            LayoutInflater.from(context).inflate(layoutResId, toolbarBaseTb, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        toolbarTvTitle.setVisibility(View.GONE);
    }

    public void setToolbarCustomView(Toolbar toolbarBaseTb, TextView toolbarTvTitle, View view) {
        if (toolbarBaseTb == null || view == null) {
            return;
        }
        toolbarBaseTb.addView(view, view.getLayoutParams());
        toolbarTvTitle.setVisibility(View.GONE);
    }
}
