package com.veni.tools.interfaces;

import android.support.design.widget.AppBarLayout;

/**
 * 作者：kkan on 2017/12/15
 * 当前类注释:
 * AppBarLayout 滚动状态监听
 */

public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {
    public static final int EXPANDED = -1;//展开状态
    public static final int COLLAPSED = -2;//折叠状态
    public static final int IDLE = -3;//中间状态

    private int mCurrentState = IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (mCurrentState != EXPANDED) {
                onStateChanged(appBarLayout, EXPANDED);
            }
            mCurrentState = EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != COLLAPSED) {
                onStateChanged(appBarLayout, COLLAPSED);
            }
            mCurrentState = COLLAPSED;
        } else {
            if (mCurrentState != IDLE) {
                onStateChanged(appBarLayout, IDLE);
            }
            mCurrentState = IDLE;
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, int state);

}
