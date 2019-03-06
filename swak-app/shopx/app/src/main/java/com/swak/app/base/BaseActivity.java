package com.swak.app.base;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;

import com.gw.swipeback.SwipeBackLayout;
import com.veni.tools.BuildConfig;
import com.veni.tools.base.ActivityBase;
import com.veni.tools.base.BasePresenter;
import com.veni.tools.base.TUtil;
import com.veni.tools.baserx.RxManager;
import com.veni.tools.interfaces.AntiShake;

import butterknife.ButterKnife;

/**
 * 作者：kkan on 2017/01/30
 * 当前类注释:
 * 基类Activity
 */
public abstract class BaseActivity<T extends BasePresenter> extends ActivityBase {

    public T mPresenter;//Presenter 对象
    protected RxManager mRxManager;//Rxjava管理
    protected SwipeBackLayout swipeBackLayout;//侧滑退出
    protected String TAG;
    protected AntiShake antiShake;//防止重复点击

    /*********************子类实现*****************************/
    //获取布局文件
    public abstract int getLayoutId();

    //初始化view
    public abstract void initView(Bundle savedInstanceState);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeSetcontentView();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        ButterKnife.bind(this);
        doAfterSetcontentView();
        this.initPresenter();
        this.initView(savedInstanceState);
    }

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    public void initPresenter() {
        if (mPresenter != null) {
            ((BasePresenter) mPresenter).setVM(this);
        }
    }


    /**
     * 设置layout前配置
     */
    private void doBeforeSetcontentView() {
        mRxManager = new RxManager();
        antiShake = new AntiShake();
        // 设置竖屏(总导致失败)
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 设置layout后配置
     */
    private void doAfterSetcontentView() {
        mPresenter = TUtil.getT(this, 0);
        if (mPresenter != null) {
            mPresenter.mContext = this;
        }
        TAG = context.getClass().getSimpleName();
        setSwipeBackLayout(-1);
    }

    /**
     * 支持滑动返回
     * -1 关闭 0左滑 1 右滑 2 上滑 3下滑
     * direction 滑动方向
     * activity style 必须设置Theme.Swipe.Back.NoActionBar
     */
    public void setSwipeBackLayout(int issupswipebace/*,boolean isSwipeFromEdge*/) {
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

    @Override
    protected void onResume() {
        super.onResume();
        //debug版本不统计crash
        if (!BuildConfig.LOG_DEBUG) {
            //统计
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //debug版本不统计crash
        if (!BuildConfig.LOG_DEBUG) {
            //统计
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        if (mRxManager != null) {
            mRxManager.clear();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
