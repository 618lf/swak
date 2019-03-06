package com.swak.app.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.swak.app.R;
import com.swak.app.core.ui.BaseApplication;
import com.swak.app.core.ui.Toasts;
import com.swak.app.core.ui.share.ShareActivity;

import java.util.HashMap;

/**
 * 展示本地/服务器网页共通界面
 */
public class WebPageActivity extends ShareActivity implements
        View.OnClickListener {

    /**
     * 打开的网址KEY
     */
    public final static String LINK_URL = "linkURL";

    /**
     * 默认网页标题KEY
     */
    public final static String TITLE = "title";

    /**
     * 是否显示分享按钮
     */
    public final static String SHOW_SHARE = "isShowShare";

    /**
     * 是否允许缩放
     */
    public final static String IS_CAN_ZOOM = "isCanZoom";

    /**
     * 网页地址
     */
    protected String linkURL = "http://git.oschina.net/zftlive";

    /**
     * 网页标题
     */
    protected String mCurrentTile = "";

    /**
     * 是否显示分享按钮
     */
    private boolean isShowShare = true;

    /**
     * 是否允许缩放
     */
    private boolean isCanZoom = true;

    /**
     * 网页加载是否发生错误
     */
    private boolean isError = false;

    private TextView tv_webview_title, tv_error_msg;
    private ProgressBar pb_web_load_progress;
    private RelativeLayout rl_webview;
    private WebView mWebView;
    private LinearLayout ll_reload;

    @Override
    public int bindLayout() {
        return R.layout.activity_webpage;
    }

    @Override
    public void initParams(Bundle parms) {
        //linkURL = parms.getString(LINK_URL);
        //mCurrentTile = parms.getString(TITLE);
        //isShowShare = parms.getBoolean(SHOW_SHARE, false);
        //isCanZoom = parms.getBoolean(IS_CAN_ZOOM, true);
    }

    @Override
    public void initView(View view) {
        //初始化标题栏
        mWindowTitle.initBackTitleBar("", Gravity.LEFT | Gravity.CENTER_VERTICAL);
        // 设置分享按钮是否显示
        ImageButton mShareBtn = mWindowTitle.getShareImageButton();
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 分享界面
                openSharePannel(R.drawable.ic_launcher, mCurrentTile, mCurrentTile, linkURL, null);
            }
        });
        mShareBtn.setVisibility(isShowShare ? view.VISIBLE : View.GONE);
        // 标题栏、错误提示信息
        tv_webview_title = mWindowTitle.getTitleTextView();
        tv_error_msg = (TextView) findViewById(R.id.tv_error_msg);
        // 加载进度
        pb_web_load_progress = mWindowTitle.getLoadProgressBar();
        // webview、重新加载
        rl_webview = (RelativeLayout) findViewById(R.id.rl_webview);
        mWebView = (WebView) findViewById(R.id.webview);
        ll_reload = (LinearLayout) findViewById(R.id.ll_reload);
        ll_reload.setOnClickListener(this);
        isError = false;
    }

    @Override
    public void doBusiness(Context mContext) {

        // 初始化Webview配置
        WebSettings settings = mWebView.getSettings();
        // 设置是否支持Javascript
        settings.setJavaScriptEnabled(true);
        // 是否支持缩放
        settings.setSupportZoom(isCanZoom);
        // settings.setBuiltInZoomControls(true);
        // if (Build.VERSION.SDK_INT >= 3.0)
        // settings.setDisplayZoomControls(false);
        // 是否显示缩放按钮
        // settings.setDisplayZoomControls(false);
        // 提高渲染优先级
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 设置页面自适应手机屏幕
        settings.setUseWideViewPort(true);
        // WebView自适应屏幕大小
        settings.setLoadWithOverviewMode(true);
        // 加载url前，设置不加载图片WebViewClient-->onPageFinished加载图片
        // settings.setBlockNetworkImage(true);
        // 设置网页编码
        settings.setDefaultTextEncodingName("UTF-8");

        mWebView.setWebChromeClient(new UIWebChromeClient());
        mWebView.setWebViewClient(new UIWebViewClient());
        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Log.e(TAG, "onDownloadStart-->url:" + url);
                Log.e(TAG, "onDownloadStart-->userAgent:" + userAgent);
                Log.e(TAG, "onDownloadStart-->contentDisposition:"
                        + contentDisposition);
                Log.e(TAG, "onDownloadStart-->mimetype:" + mimetype);
                Log.e(TAG, "onDownloadStart-->contentLength:" + contentLength);

                // 调用系统浏览器下载
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        mWebView.loadUrl(linkURL);
    }

    @Override
    protected void onDestroy() {
        mWebView.stopLoading();
        // 先移除附属关系
        rl_webview.removeView(mWebView);
        mWebView.removeAllViews();
        mWebView.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int switchId = v.getId();
        if (R.id.ll_reload == switchId) {
            mWebView.reload();
            isError = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 看自己业务需要，是否需要该回调
     */
    @Override
    protected void onShareItemClick(String platform) {
        Toasts.show("点击" + platform);
    }

    /**
     * 看自己业务需要，是否需要该回调
     */
    @Override
    protected void onShareSuccess(String platform, int code, HashMap<String, Object> hashMap) {
        Toasts.show("分享成功" + platform);
    }

    /**
     * 看自己业务需要，是否需要该回调
     */
    @Override
    protected void onShareFailure(String platform, int code, Throwable throwable) {
        Toasts.show("分享失败" + platform);
    }

    /**
     * 看自己业务需要，是否需要该回调
     */
    @Override
    protected void onShareCancel(String platform, int code) {
        Toasts.show("取消分享" + platform);
    }

    class UIWebChromeClient extends WebChromeClient {
        /*** 页面加载进度 **/
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            //不管正常还是发生错误都会进入，需要isError标志位区分
            if (newProgress == 100) {
                pb_web_load_progress.setVisibility(View.GONE);

                //如果发生错误显示错误提示
                ll_reload.setVisibility(isError ? View.VISIBLE : View.GONE);
                mWebView.setVisibility(isError ? View.GONE : View.VISIBLE);

                //设置提示信息
                if (isError) {
                    if (!BaseApplication.isNetworkReady()) {
                        tv_error_msg.setText("亲，网络木有打开哦~");
                    } else {
                        tv_error_msg.setText("点击刷新");
                    }
                }
            } else {
                pb_web_load_progress.setVisibility(View.VISIBLE);
                pb_web_load_progress.setProgress(newProgress);
            }
        }

        /**
         * 获取到网页标题回调函数
         */
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mCurrentTile = title;
            tv_webview_title.setText(mCurrentTile);
        }
    }

    class UIWebViewClient extends WebViewClient {

        /**
         * 控制网页的链接跳转打开方式（拦截URL，当前界面打开网页所有连接）
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            linkURL = url;
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i(TAG, "onPageStarted--->url=" + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 加载完毕后，开始加载图片
            // view.getSettings().setBlockNetworkImage(false);

            // 标题(防止某些机型不调用 onReceivedTitle方法 )
            if (view.getTitle() != null) {
                mCurrentTile = view.getTitle();
                tv_webview_title.setText(mCurrentTile);
            }
            Log.i(TAG, "onPageFinished--->url=" + url);
        }

        /***
         * 让浏览器支持访问https请求
         */
        @SuppressLint("NewApi")
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            handler.proceed();
            super.onReceivedSslError(view, handler, error);
            Log.i(TAG, "onReceivedSslError--->" + "加载数据失败");
            isError = true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.i(TAG, "onReceivedError--->" + "加载数据失败，错误码：" + errorCode
                    + "\n 原因描述：" + description);
            isError = true;
        }
    }
}