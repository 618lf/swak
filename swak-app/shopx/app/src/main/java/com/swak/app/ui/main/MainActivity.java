package com.swak.app.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.swak.app.R;
import com.swak.app.api.AppConstant;
import com.swak.app.base.BaseActivity;
import com.swak.app.model.PersonalBean;
import com.swak.app.ui.main.contract.MainContract;
import com.swak.app.ui.main.presenter.MainPresenter;
import com.veni.tools.ACache;
import com.veni.tools.ActivityTools;
import com.veni.tools.LogTools;
import com.veni.tools.StatusBarTools;
import com.veni.tools.base.ActivityJumpOptionsTool;
import com.veni.tools.interfaces.OnNoFastClickListener;

import java.util.List;

import butterknife.BindView;

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    @BindView(R.id.main_nav_view)
    NavigationView mainNavView;
    @BindView(R.id.main_drawer)
    DrawerLayout mainDrawer;
    @BindView(R.id.main_bottom_navigation)
    AHBottomNavigation mainBottomNavigation;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // 设置沉侵状态栏,标题栏不是通用的  标题栏高度需要在每个Fragment或者侧滑菜单中设置
        StatusBarTools.immersive(this);
        //初始化底部按钮
        this.initBottomNavigation();
        //初始化侧滑菜单
        this.initDrawerLayout();
        //设置底部默认选中按钮
        mainBottomNavigation.setCurrentItem(0, true);
    }

    private int mainposition = -1;
    private HomeFragment homeFragment;
    private TradeFragment tradeFragment;

    /**
     * 初始化底部按钮
     */
    private void initBottomNavigation() {
        //Create items
        AHBottomNavigationItem homepage = new AHBottomNavigationItem(R.string.homepage, R.mipmap.ic_main_homepage, android.R.color.white);
        AHBottomNavigationItem tradepage = new AHBottomNavigationItem(R.string.trade, R.mipmap.ic_main_trade, android.R.color.white);
        AHBottomNavigationItem vippage = new AHBottomNavigationItem(R.string.personal, R.mipmap.ic_main_personal, android.R.color.white);
        // Add items
        mainBottomNavigation.addItem(homepage);
        mainBottomNavigation.addItem(tradepage);
        mainBottomNavigation.addItem(vippage);
        //始终显示文字
        mainBottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        // Set background color
        mainBottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        // 禁用CoordinatorLayout内部的转换
        mainBottomNavigation.setBehaviorTranslationEnabled(false);
        // 启用FloatingActionButton的转换
//        mainBottomNavigation.manageFloatingActionButtonBehavior(floatingActionButton);

        // Change colors
        mainBottomNavigation.setAccentColor(ContextCompat.getColor(getContext(), R.color.google_green));
        mainBottomNavigation.setInactiveColor(ContextCompat.getColor(getContext(), R.color.google_blue));
        //强制着色绘图(例如，对于带有图标的字体有用)
        mainBottomNavigation.setForceTint(true);
        // 在导航栏下显示颜色(API 21+)
        // Don't forget these lines in your style-v21
        // <item name="android:windowTranslucentNavigation">true</item>
        // <item name="android:fitsSystemWindows">true</item>
//        mainBottomNavigation.setTranslucentNavigationEnabled(true);

        mainBottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                AHBottomNavigationItem selecetitem = mainBottomNavigation.getItem(position);
                String title = selecetitem.getTitle(getContext());
                LogTools.d("selecte:" + title);
                if (mainposition != position) {
                    mainposition = position;
                    SwitchTo(selecetitem);
                }
                return true;
            }
        });
    }

    /**
     * 底部按钮点击事件
     */
    private void SwitchTo(AHBottomNavigationItem selecetitem) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anl_slide_left_in, R.anim.anl_slide_right_out);//Fragment切换动画效果2
        hideFragments(transaction);
        String title = selecetitem.getTitle(getContext());
        switch (title) {
            case "首页"://首页
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.main_framelayout, homeFragment);
                } else {
                    transaction.show(homeFragment);
                }
                break;
            case "购物"://购物
                if (tradeFragment == null) {
                    tradeFragment = new TradeFragment();
                    transaction.add(R.id.main_framelayout, tradeFragment);
                } else {
                    transaction.show(tradeFragment);
                }
                break;

            case "我"://我

                break;
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (tradeFragment != null) {
            transaction.hide(tradeFragment);
        }
    }

    /**
     * 侧滑菜单
     * inflateHeaderView 进来的布局要宽一些
     */
    private void initDrawerLayout() {
        mainNavView.inflateHeaderView(R.layout.activity_main_nav);
        View headerView = mainNavView.getHeaderView(0);
        FrameLayout mainNavHeadLayout = headerView.findViewById(R.id.main_nav_head_layout);
        ImageView mainNavBgView = headerView.findViewById(R.id.main_nav_bg_view);

        headerView.findViewById(R.id.main_nav_community).setOnClickListener(mListener);
        headerView.findViewById(R.id.main_nav_scan_address).setOnClickListener(mListener);
        headerView.findViewById(R.id.main_nav_feedback).setOnClickListener(mListener);
        headerView.findViewById(R.id.main_nav_exit).setOnClickListener(mListener);

        //增加状态栏的高度
        ViewGroup.LayoutParams lp = mainNavHeadLayout.getLayoutParams();
        lp.height += StatusBarTools.getStatusBarHeight(getContext());//增高
        mainNavHeadLayout.setLayoutParams(lp);
    }

    @Override
    public void returnVersionData(List<PersonalBean> data) {
    }

    @Override
    public void onErrorSuccess(int code, String message, boolean isSuccess, boolean showTips) {
    }

    /**
     * 侧滑菜单点击事件
     */
    private OnNoFastClickListener mListener = new OnNoFastClickListener() {
        @Override
        protected void onNoDoubleClick(final View view) {
            mainDrawer.closeDrawer(GravityCompat.START);
            mainDrawer.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String value = ACache.get(getContext()).getAsString(AppConstant.PatternlockOK);
                    if (value == null) {
                        // PatternlockActivity.startAction(context);
                    }
                    switch (view.getId()) {
                        case R.id.main_nav_community: // 圈子
                            break;
                        case R.id.main_nav_scan_address: // 关于我们
                            break;
                        case R.id.main_nav_feedback: // 问题反馈
                            break;
                        case R.id.main_nav_exit:
                            creatDialogBuilder().setDialog_title("温馨提示")
                                    .setDialog_message("是否退出应用?")
                                    .setDialog_Left("退出")
                                    .setLeftlistener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ActivityTools.getActivityTool().appExit(getContext(), false);
                                        }
                                    })
                                    .setDialog_Right("取消")
                                    .builder().show();

                            break;
                        default:
                            break;
                    }
                }
            }, 100);
        }
    };

    /**
     * 启动入口
     */
    public static void startAction(Context context) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(MainActivity.class)
                .setEnterResId(0)
                .setActionTag(ActivityJumpOptionsTool.Type.CLEAR_TASK)
                .customAnim()
                .start();
    }
}
