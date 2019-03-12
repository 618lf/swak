package com.swak.app.shopxx.tests.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.swak.app.core.base.BaseActivity;
import com.swak.app.core.net.exception.ResponseException;
import com.swak.app.core.tools.ACache;
import com.swak.app.core.tools.ActivityTools;
import com.swak.app.core.tools.DelayTools;
import com.swak.app.core.tools.ImageTools;
import com.swak.app.core.tools.NetWorkTools;
import com.swak.app.core.view.itoast.ToastTool;
import com.swak.app.shopxx.R;
import com.swak.app.shopxx.tests.Constants;
import com.swak.app.shopxx.tests.bean.SplashBean;
import com.swak.app.shopxx.tests.contract.SplashContract;
import com.swak.app.shopxx.tests.presenter.SplashPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGALocalImageSize;

/**
 * 启动界面
 */
public class SplashActivity extends BaseActivity<SplashPresenter> implements SplashContract.View {

    @BindView(R.id.banner_guide_container)
    RelativeLayout banner_guide_container;
    @BindView(R.id.banner_guide_background)
    BGABanner banner_guide_background;
    @BindView(R.id.banner_guide_foreground)
    BGABanner banner_guide_foreground;
    @BindView(R.id.tv_guide_skip)
    TextView tv_guide_skip;
    @BindView(R.id.btn_guide_enter)
    Button btn_guide_enter;
    @BindView(R.id.defalut_splash_image)
    ImageView defalut_splash_image;

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    /**
     * 判断是否有网络，加载缓存的图片或者默认的图片或者从网络获取图片
     *
     * @param savedInstanceState
     */
    @Override
    public void initView(Bundle savedInstanceState) {
        Boolean isfirst = ACache.get(this.getContext()).getAsBoolean(Constants.FIRST_TIME);
        if (!NetWorkTools.isConnected(this)
                || (isfirst != null && !isfirst)) {
            btn_guide_enter.setVisibility(View.VISIBLE);
            defalut_splash_image.setVisibility(View.VISIBLE);
            ImageTools.load(this.getContext(), R.drawable.original_splash_girl, defalut_splash_image);
            DelayTools.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ActivityTools.me().jumpActivity(getContext(), MainActivity.class);
                }
            }, 3000);
        } else {
            banner_guide_container.setVisibility(View.VISIBLE);
            setListener();
            this.presenter.getSplashPic();
        }
    }

    @Override
    public void onSplashSuccess(List<SplashBean> datas) {
        processLogic(datas);
    }

    @Override
    public void onSplashError(ResponseException e) {
        ToastTool.error("获取数据失败！");
    }

    // 添加监听
    private void setListener() {
        /**
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */
        banner_guide_foreground.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {
                ACache.get(getContext()).put(Constants.FIRST_TIME, "false");
                ActivityTools.me().jumpActivity(getContext(), MainActivity.class);
            }
        });
    }

    // 处理数据
    private void processLogic(List<SplashBean> datas) {
        // Bitmap 的宽高在 maxWidth maxHeight 和 minWidth minHeight 之间
        BGALocalImageSize localImageSize = new BGALocalImageSize(720, 1280, 320, 640);

        // 设置数据源
        banner_guide_background.setAdapter(new BGABanner.Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                ImageTools.load(getContext(), model, itemView);
            }
        });
        List<String> models = new ArrayList<>();
        for (SplashBean splash : datas) {
            models.add(splash.getUrl());
        }
        banner_guide_background.setData(models, null);
        banner_guide_foreground.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.drawable.uoko_guide_foreground_1,
                R.drawable.uoko_guide_foreground_2,
                R.drawable.uoko_guide_foreground_3);
    }
}