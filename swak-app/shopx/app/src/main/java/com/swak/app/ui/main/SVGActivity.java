package com.swak.app.ui.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.jaredrummler.android.widget.AnimatedSvgView;
import com.swak.app.R;
import com.swak.app.api.AppConstant;
import com.swak.app.base.BaseActivity;
import com.swak.app.model.ModelSVG;
import com.swak.app.model.PersonalBean;
import com.swak.app.ui.main.contract.SVGContract;
import com.swak.app.ui.main.presenter.SVGPresenter;
import com.veni.tools.ACache;
import com.veni.tools.LogTools;
import com.veni.tools.SPTools;
import com.veni.tools.StatusBarTools;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SVGActivity extends BaseActivity<SVGPresenter> implements SVGContract.View {

    @BindView(R.id.animated_svg_view)
    AnimatedSvgView mSvgView;
    @BindView(R.id.app_name)
    ImageView mAppName;

    private boolean svgisok = false;
    private boolean chikcisok = false;
    private Handler checkhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAppName.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_svg;
    }

    /*启用MVP一定要设置这句*/
    @Override
    public void initPresenter() {
        mPresenter.setVM(this);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        //设置沉侵状态栏
        StatusBarTools.immersive(this);
        //选择启动的SVG动画
        setSvg(ModelSVG.values()[0]);
        //启用的网络接口
        //checkUpdate();
    }

    private void setSvg(final ModelSVG modelSvg) {
        mSvgView.setGlyphStrings(modelSvg.glyphs);
        mSvgView.setFillColors(modelSvg.colors);
        mSvgView.setViewportSize(modelSvg.width, modelSvg.height);
        mSvgView.setTraceResidueColor(0x32000000);
        mSvgView.setTraceColors(modelSvg.colors);
        mSvgView.rebuildGlyphData();
        mSvgView.setOnStateChangeListener(new AnimatedSvgView.OnStateChangeListener() {
            @Override
            public void onStateChange(int state) {
                if (AnimatedSvgView.STATE_FINISHED == state) {
                    svgisok = true;
                    isfirstin();
                }
            }
        });
        mSvgView.start();
    }

    /**
     * 检查是否有新版本，如果有就升级
     */
    private void checkUpdate() {
        mRxManager.add(Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Message msg = checkhandler.obtainMessage();
                        checkhandler.sendMessage(msg);
                        mPresenter.checkVersion("1");
                    }
                }));

    }

    /**
     * 第一次打开app
     */
    private void isfirstin() {
        //if (!svgisok || !chikcisok) {
        //    return;
        //}
        // 第一次打开app
        String isfirst = ACache.get(getContext()).getAsString(AppConstant.FIRST_TIME);
        boolean isfirstsp = (Boolean) SPTools.get(getContext(), AppConstant.FIRST_TIME, true);
        if (isfirst == null || isfirstsp) {
            FirstStartActivity.startAction(getContext());
        } else {
            MainActivity.startAction(getContext());
        }
    }

    /**
     * 校验版本的回调
     *
     * @param data
     */
    @Override
    public void returnVersionData(List<PersonalBean> data) {

    }

    /**
     * 校验版本错误
     *
     * @param code
     * @param message
     * @param isSuccess
     * @param showTips
     */
    @Override
    public void onErrorSuccess(int code, String message, boolean isSuccess, boolean showTips) {
        chikcisok = true;
        isfirstin();
    }
}