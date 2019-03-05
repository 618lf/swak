package com.swak.app.core.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.swak.app.core.R;

public class WindowTitle extends RelativeLayout implements View.OnClickListener {

    /**
     * 根布局
     */
    private ViewGroup mTitleLayout;

    /**
     * 上下文
     */
    protected Context mContext;

    public WindowTitle(Context context) {
        this(context, null);
    }

    public WindowTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        this.mContext = context;
        mTitleLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.anl_common_title_bar, this, true);
        //初始化标题栏
        initLeftContent(getTitleContainerLeft());
        initCenterContent(getTitleContainerCenter());
        initRightContent(getTitleContainerRight());
    }


    /**
     * 初始化只有标题+居中样式的标题栏
     *
     * @param strTitle 标题名称
     */
    public void initCenterTitleBar(String strTitle) {
        initBackTitleBar(strTitle, Gravity.CENTER);
        getBackImageButton().setVisibility(View.GONE);
    }

    /**
     * 初始化返回按钮+居中标题
     *
     * @param strTitle 标题名称
     */
    public void initBackCenterTitleBar(String strTitle) {
        initBackTitleBar(strTitle, Gravity.CENTER);
        getDoneImageButton().setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化返回按钮+标题左对齐
     *
     * @param strTitle 标题名称
     */
    public void initBackTitleBar(String strTitle) {
        initBackTitleBar(strTitle, Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }

    /**
     * 初始化返回按钮+指定标题文本对齐方式
     *
     * @param strTitle 标题名称
     * @param mGravity 标题文本对其方式 Gravity.LEFT|Gravity.CENTER_VERTICAL
     */
    public void initBackTitleBar(String strTitle, int mGravity) {
        // 设置标题
        TextView mTitleText = (TextView) findViewById(R.id.tv_window_title);
        if (null != mTitleText) {
            mTitleText.setText(strTitle);
            mTitleText.setGravity(mGravity);
        }

        // 设置点击事件
        View mBackBtn = findViewById(R.id.ib_back);
        if (null != mBackBtn) {
            mBackBtn.setVisibility(View.VISIBLE);
            mBackBtn.setOnClickListener(this);
        }
    }

    /**
     * 初始化标题栏(居中标题)
     *
     * @param title
     */
    public void initCenterTitle(String title) {
        initBackTitleBar(title, Gravity.CENTER);
        getBackImageButton().setVisibility(View.GONE);
    }

    /**
     * 初始化标题栏(居中标题+背景色)
     *
     * @param title
     */
    public void initCenterTitle(String title, @ColorRes int colorResId) {
        initBackTitleBar(title, Gravity.CENTER);
        getBackImageButton().setVisibility(View.GONE);
        setBackgroundColor(getResources().getColor(colorResId));
    }

    /**
     * 初始化带返回键的标题栏(返回键+居中标题+右侧按钮)
     *
     * @param title
     */
    public void initBackCenterTitle(String title, String rightBtnText) {
        initBackCenterTitle(title);
        getDoneButton().setVisibility(View.VISIBLE);
        getDoneButton().setText(rightBtnText);
    }

    /**
     * 初始化带返回键的标题栏(返回键+居中标题+右侧按钮+背景色)
     *
     * @param title
     */
    public void initBackCenterTitle(String title, String rightBtnText, @ColorRes int colorResId) {
        initBackCenterTitle(title);
        getDoneButton().setVisibility(View.VISIBLE);
        getDoneButton().setText(rightBtnText);
        setBackgroundColor(getResources().getColor(colorResId));
    }

    /**
     * 初始化带返回键的标题栏(返回键+居中标题)
     *
     * @param title
     */
    public void initBackCenterTitle(String title) {
        initBackTitleBar(title, Gravity.CENTER);
        getDoneImageButton().setVisibility(View.INVISIBLE);
    }

    /**
     * 初始化带返回键的标题栏(返回键+居中标题+背景色)
     *
     * @param title
     */
    public void initBackCenterTitle(String title, @ColorRes int colorResId) {
        initBackTitleBar(title, Gravity.CENTER);
        getDoneImageButton().setVisibility(View.INVISIBLE);
        setBackgroundColor(getResources().getColor(colorResId));
    }

    /**
     * 初始化标题栏右侧[完成/提交]按钮
     *
     * @param strBtnText     按钮显示文本
     * @param mClickListener 点击监听事件
     */
    public void initRightDoneBtn(String strBtnText, View.OnClickListener mClickListener) {
        Button mDoneBtn = (Button) findViewById(R.id.btn_done);
        if (null != mDoneBtn) {
            mDoneBtn.setVisibility(View.VISIBLE);
            mDoneBtn.setText(strBtnText);
            mDoneBtn.setOnClickListener(mClickListener);
        }
    }


    /**
     * 获得winTitle根布局
     */
    public ViewGroup getWindowTitleLayout() {
        return mTitleLayout;
    }


    /**
     * 设置标题
     *
     * @param strTitle 标题文本
     */
    public void setWindowTitle(CharSequence strTitle) {
        setWindowTitle(strTitle, Gravity.LEFT | Gravity.CENTER_VERTICAL);
    }

    /**
     * 设置标题
     *
     * @param strTitle 标题文本
     */
    public void setWindowTitle(CharSequence strTitle, String textColor) {
        setWindowTitle(strTitle, Gravity.LEFT | Gravity.CENTER_VERTICAL, textColor);
    }

    /**
     * 设置标题以及文本对齐方式
     *
     * @param strTitle      标题文本
     * @param mTitleGravity 标题文本对齐方式
     */
    public void setWindowTitle(CharSequence strTitle, int mTitleGravity) {
        setWindowTitle(strTitle, mTitleGravity, "#666666");
    }

    /**
     * 设置标题以及文本对齐方式
     *
     * @param strTitle      标题文本
     * @param mTitleGravity 标题文本对齐方式
     */
    public void setWindowTitle(CharSequence strTitle, int mTitleGravity, String textColor) {
        // 标题
        TextView mTitleText = (TextView) findViewById(R.id.tv_window_title);
        if (null == mTitleText) return;
        mTitleText.setGravity(mTitleGravity);
        mTitleText.setText(strTitle);
        if (!TextUtils.isEmpty(textColor)) {
            mTitleText.setTextColor(Color.parseColor(textColor));
        }
    }

    /**
     * 设置标题栏底部线
     *
     * @param visibility 是否可见
     */
    public void setButtomLine(int visibility) {
        View mLineView = findViewById(R.id.title_buttom_line);
        if (null != mLineView) {
            mLineView.setVisibility(visibility);
        }
    }

    /**
     * 设置标题栏背景颜色
     *
     * @param strColor 背景颜色，如：#FFFFFF
     */
    public void setTitleBarBgColor(String strColor) {
        if (TextUtils.isEmpty(strColor)) return;
        setTitleBarBgColor(Color.parseColor(strColor));
    }

    /**
     * 设置标题栏背景颜色
     *
     * @param mResId 背景资源文件-->mContext.getResources().getColor(R.color.actionbar_bg)
     */
    public void setTitleBarBgColor(int mResId) {
        View mTitleBarContainer = findViewById(R.id.rl_title_container);
        if (null != mTitleBarContainer) {
            mTitleBarContainer.setBackgroundColor(mResId);
        }
    }

    /**
     * 设置标题栏背景颜色
     *
     * @param mResId 背景资源文件-->R.color.actionbar_bg/R.drawable.actionbar_bg
     */
    public void setTitleBgWithResColor(int mResId) {
        View mTitleBarContainer = findViewById(R.id.rl_title_container);
        if (null != mTitleBarContainer) {
            mTitleBarContainer.setBackgroundResource(mResId);
        }
    }

    /**
     * 设置标题栏背景图片
     *
     * @param strResName 图片资源文件名称，如：actionbar_bg
     */
    public void setTitleBarBg(String strResName) {
        if (TextUtils.isEmpty(strResName)) return;
        String packageName = getContext().getPackageName();
        int resId = getContext().getResources().getIdentifier(strResName, "drawable", packageName);
        setTitleBarBg(resId);
    }

    /**
     * 设置标题栏背景图片
     *
     * @param mResId 图片资源id，如：R.drawable.actionbar_bg
     */
    public void setTitleBarBg(int mResId) {
        View mTitleBarContainer = findViewById(R.id.rl_title_container);
        if (null != mTitleBarContainer) {
            mTitleBarContainer.setBackgroundResource(mResId);
        }
    }

    /**
     * 隐藏标题栏右侧[完成/提交]按钮
     */
    public void hiddenRightDoneBtn() {
        hiddenRightDoneBtn(View.GONE);
    }

    /**
     * 隐藏标题栏右侧[完成/提交]按钮
     *
     * @param mViewStatus 按钮的状态 View.GONE/View.INVISIBLE
     */
    public void hiddenRightDoneBtn(int mViewStatus) {
        Button mDoneBtn = (Button) findViewById(R.id.btn_done);
        if (null != mDoneBtn) {
            mDoneBtn.setVisibility(mViewStatus);
        }
    }

    /**
     * 隐藏标题栏左侧[返回]按钮
     */
    public void hiddenLeftBackBtn() {
        hiddenLeftBackBtn(View.GONE);
    }

    /**
     * 隐藏标题栏左侧[返回]按钮
     *
     * @param mViewStatus 按钮的状态 View.GONE/View.INVISIBLE
     */
    public void hiddenLeftBackBtn(int mViewStatus) {
        ImageButton mBackBtn = (ImageButton) findViewById(R.id.ib_back);
        if (null != mBackBtn) {
            mBackBtn.setVisibility(mViewStatus);
        }
    }

    /**
     * 隐藏标题栏
     */
    public void hiddeTitleBar() {
        // 标题栏容器
        View mTitleBarContainer = findViewById(R.id.rl_title_container);
        if (null != mTitleBarContainer) {
            mTitleBarContainer.setVisibility(View.GONE);
        }
        setVisibility(View.GONE);
    }

    /**
     * 获取标题栏中间按钮容器，可以自行控制左按钮和布局
     *
     * @return
     */
    public LinearLayout getTitleContainerCenter() {
        return (LinearLayout) findViewById(R.id.ll_center_content);
    }

    /**
     * 获取标题栏左侧按钮容器，可以自行控制左按钮和布局
     *
     * @return
     */
    public LinearLayout getTitleContainerLeft() {
        return (LinearLayout) findViewById(R.id.ll_left_btns);
    }

    /**
     * 获取标题栏右侧侧按钮容器，可以自行控制右按钮和布局
     *
     * @return
     */
    public LinearLayout getTitleContainerRight() {
        return (LinearLayout) findViewById(R.id.ll_right_btns);
    }

    /**
     * 获取标题栏文本控件
     *
     * @return
     */
    public TextView getTitleTextView() {
        return (TextView) findViewById(R.id.tv_window_title);
    }

    /**
     * 获取进度条控件
     *
     * @return
     */
    public ProgressBar getLoadProgressBar() {
        return (ProgressBar) findViewById(R.id.pb_load_progress);
    }

    /**
     * 获取右侧[完成]按钮控件
     *
     * @return
     */
    public Button getDoneButton() {
        return (Button) findViewById(R.id.btn_done);
    }

    /**
     * 获取右侧[完成]图片按钮控件
     *
     * @return
     */
    public ImageButton getDoneImageButton() {
        return (ImageButton) findViewById(R.id.iv_done);
    }

    /**
     * 获取右侧[分享]图片按钮控件
     *
     * @return
     */
    public ImageButton getShareImageButton() {
        return (ImageButton) findViewById(R.id.iv_share);
    }

    /**
     * 获取右侧[收藏]图片按钮控件
     *
     * @return
     */
    public ImageButton getFavImageButton() {
        return (ImageButton) findViewById(R.id.iv_fav);
    }

    /**
     * 获取左侧[返回]图片按钮控件
     *
     * @return
     */
    public ImageButton getBackImageButton() {
        return (ImageButton) findViewById(R.id.ib_back);
    }


    protected void initLeftContent(ViewGroup parent) {

    }

    protected void initCenterContent(ViewGroup parent) {

    }

    protected void initRightContent(ViewGroup parent) {

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.ib_back) {
            if (null != mContext && mContext instanceof Activity) {
                ((Activity) mContext).finish();
            }
        }
    }
}
