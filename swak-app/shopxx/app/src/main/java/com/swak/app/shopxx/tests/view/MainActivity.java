package com.swak.app.shopxx.tests.view;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.material_design_iconic_typeface_library.MaterialDesignIconic;
import com.swak.app.core.base.BaseActivity;
import com.swak.app.core.net.exception.ResponseException;
import com.swak.app.core.tools.DimenTools;
import com.swak.app.core.tools.ImageTools;
import com.swak.app.core.tools.StatusBarTools;
import com.swak.app.core.view.image.CircleTransform;
import com.swak.app.core.view.reside.ResideLayout;
import com.swak.app.shopxx.R;
import com.swak.app.shopxx.tests.bean.BannerBean;
import com.swak.app.shopxx.tests.bean.FriendBean;
import com.swak.app.shopxx.tests.contract.MainContract;
import com.swak.app.shopxx.tests.presenter.MainPresenter;

import java.util.List;

import butterknife.BindView;

/**
 * 显示主界面
 */
public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.View {

    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.desc)
    TextView mDesc;
    @BindView(R.id.all)
    TextView mAll;
    @BindView(R.id.fuli)
    TextView mFuli;
    @BindView(R.id.android)
    TextView mAndroid;
    @BindView(R.id.ios)
    TextView mIos;
    @BindView(R.id.video)
    TextView mVideo;
    @BindView(R.id.front)
    TextView mFront;
    @BindView(R.id.resource)
    TextView mResource;
    @BindView(R.id.app)
    TextView mApp;
    @BindView(R.id.more)
    TextView mMore;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    @BindView(R.id.about)
    TextView mAbout;
    @BindView(R.id.theme)
    TextView mTheme;
    @BindView(R.id.menu)
    RelativeLayout mMenu;
    @BindView(R.id.icon)
    ImageView mIcon;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.container)
    FrameLayout mContainer;
    @BindView(R.id.resideLayout)
    ResideLayout mResideLayout;

    private FragmentManager fragmentManager;
    private String currentFragmentTag;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        StatusBarTools.immersive(this);
        fragmentManager = this.getSupportFragmentManager();
        setIconDrawable(mAll, MaterialDesignIconic.Icon.gmi_view_comfy);
        setIconDrawable(mFuli, MaterialDesignIconic.Icon.gmi_mood);
        setIconDrawable(mAndroid, MaterialDesignIconic.Icon.gmi_android);
        setIconDrawable(mIos, MaterialDesignIconic.Icon.gmi_apple);
        setIconDrawable(mVideo, MaterialDesignIconic.Icon.gmi_collection_video);
        setIconDrawable(mFront, MaterialDesignIconic.Icon.gmi_language_javascript);
        setIconDrawable(mResource, MaterialDesignIconic.Icon.gmi_account);
        setIconDrawable(mApp, MaterialDesignIconic.Icon.gmi_apps);
        setIconDrawable(mAbout, MaterialDesignIconic.Icon.gmi_account);
        setIconDrawable(mTheme, MaterialDesignIconic.Icon.gmi_palette);
        setIconDrawable(mMore, MaterialDesignIconic.Icon.gmi_more);

        // 背景色
        mMenu.setBackgroundColor(this.getResources().getColor(R.color.crimson));
        // 头像、名称
        Glide.with(this.getContext()).load(R.drawable.stars).dontAnimate()
                .transform(new CircleTransform()).into(mAvatar);
        mDesc.setText("李锋");
    }

    private void initData() {
        presenter.getBannerData();
        presenter.getFriendData();
        presenter.getZipExecuteData();
        presenter.getOrderExecuteData();
    }

    @Override
    public void onBannerSuccess(List<BannerBean> data) {
        Log.e("banner", "success");
    }

    @Override
    public void onBannerError(ResponseException e) {
        Log.e("banner", "error");
    }

    @Override
    public void onFriendSuccess(List<FriendBean> data) {
        Log.e("friend", "success");
    }

    @Override
    public void onFriendError(ResponseException e) {
        Log.e("friend", "error");
    }

    @Override
    public void onZipExecuteSuccess(String data) {
        Log.e("ZipExecute", "success");
    }

    @Override
    public void onOrderExecuteSuccess(List<FriendBean> data) {
        Log.e("OrderExecute", "success");
    }

    private void setIconDrawable(TextView view, IIcon icon) {
        view.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(this)
                        .icon(icon)
                        .color(Color.WHITE)
                        .sizeDp(16),
                null, null, null);
        view.setCompoundDrawablePadding(DimenTools.dp2px(this, 10));
    }
}