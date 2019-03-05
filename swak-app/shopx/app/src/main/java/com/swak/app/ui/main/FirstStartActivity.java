package com.swak.app.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;

import com.swak.app.R;
import com.swak.app.api.AppConstant;
import com.swak.app.base.BaseActivity;
import com.veni.tools.ACache;
import com.veni.tools.SPTools;
import com.veni.tools.StatusBarTools;
import com.veni.tools.base.ActivityJumpOptionsTool;

import butterknife.BindView;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGALocalImageSize;

/**
 * 作者：kkan on 2017/11/30
 * 当前类注释:
 * 引导页 activity
 */
public class FirstStartActivity extends BaseActivity {

    @BindView(R.id.banner_guide_background)
    BGABanner mBackgroundBanner;
    @BindView(R.id.banner_guide_foreground)
    BGABanner mForegroundBanner;

    @Override
    public int getLayoutId() {
        return R.layout.activity_first_start;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        //设置进入主页的监听
        setListener();
        //设置图片
        processLogic();
    }

    private void setListener() {
        /*
         * 设置进入按钮和跳过按钮控件资源 id 及其点击事件
         * 如果进入按钮和跳过按钮有一个不存在的话就传 0
         * 在 BGABanner 里已经帮开发者处理了防止重复点击事件
         * 在 BGABanner 里已经帮开发者处理了「跳过按钮」和「进入按钮」的显示与隐藏
         */

        mForegroundBanner.setEnterSkipViewIdAndDelegate(R.id.btn_guide_enter, R.id.tv_guide_skip, new BGABanner.GuideDelegate() {
            @Override
            public void onClickEnterOrSkip() {

                //设置标识表示app不再是第一次启动,我为了测试,数据有效期为一天,实际可以将ACache.TIME_DAY去掉,也可以用SPTools,对应的也的做出相应修改
                ACache.get(getContext()).put(AppConstant.FIRST_TIME, "true", ACache.TIME_DAY);
                SPTools.put(getContext(), AppConstant.FIRST_TIME, false);
                //MainActivity.startAction(context);
            }
        });
    }

    private void processLogic() {
        // Bitmap 的宽高在 maxWidth maxHeight 和 minWidth minHeight 之间
        BGALocalImageSize localImageSize = new BGALocalImageSize(720, 1280, 320, 640);
        // 设置数据源
        mBackgroundBanner.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.mipmap.uoko_guide_background_1, R.mipmap.uoko_guide_background_2, R.mipmap.uoko_guide_background_3);
        mForegroundBanner.setData(localImageSize, ImageView.ScaleType.CENTER_CROP,
                R.mipmap.uoko_guide_foreground_1, R.mipmap.uoko_guide_foreground_2, R.mipmap.uoko_guide_foreground_3);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 如果开发者的引导页主题是透明的，需要在界面可见时给背景 Banner 设置一个白色背景，避免滑动过程中两个 Banner 都设置透明度后能看到 Launcher
//        mBackgroundBanner.setBackgroundResource(android.R.color.white);
    }

    /**
     * 启动入口
     */
    public static void startAction(Context context) {
        new ActivityJumpOptionsTool().setContext(context)
                .setClass(FirstStartActivity.class)
                .setFinish(true)
                .customAnim()
                .start();
    }
}
