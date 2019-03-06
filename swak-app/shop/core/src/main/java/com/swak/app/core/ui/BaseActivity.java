package com.swak.app.core.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.swak.app.core.IActivity;
import com.swak.app.core.R;
import com.swak.app.core.exception.ExceptionHandler;
import com.swak.app.core.model.DTO;

import java.lang.ref.WeakReference;

/**
 * 基础的 Activity
 */
public abstract class BaseActivity extends AppCompatActivity implements IActivity {

    /*** 整个应用Applicaiton **/
    private BaseApplication application = null;
    /**
     * 当前Activity的弱引用，防止内存泄露
     **/
    private WeakReference<Activity> mContextWR = null;
    /**
     * 当前Activity渲染的视图View
     **/
    private ViewGroup mContentView = null;
    /**
     * 动画类型
     **/
    private int mAnimationType = NONE;
    /**
     * 是否运行截屏
     **/
    private boolean isCanScreenshot = true;
    /**
     * 共通操作
     **/
    protected BaseActivity mActivity;
    /**
     * 标题栏
     **/
    protected WindowTitle mWindowTitle;
    /**
     * 共通操作
     **/
    protected Operation mOperation = null;
    /**
     * Activity宿主共享数据，供Fragment之间数据传递
     **/
    protected DTO<String, Object> mHostSharedData;
    /**
     * 日志输出标志
     **/
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "BaseActivity-->onCreate()");

        mActivity = this;

        // 获取应用Application
        if (getApplicationContext() instanceof BaseApplication) {
            application = (BaseApplication) getApplicationContext();
        }

        if (null != savedInstanceState) {
            mHostSharedData = (DTO<String, Object>) savedInstanceState.getSerializable(HOST_SHARE_DATA);
        }

        //需要在setContentView之前配置window的一些属性
        config(savedInstanceState);

        // 设置渲染视图View
        mContentView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.anl_activity_base_container, null);
        setContentView(mContentView);

        mWindowTitle = (WindowTitle) findViewById(R.id.ll_page_title);

        // 将当前Activity压入栈
        mContextWR = new WeakReference<Activity>(mActivity);
        if (null != application) {
            application.pushTask(mContextWR);
        }

        // 实例化共通操作
        mOperation = new Operation(mActivity);

        // 初始化参数
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mAnimationType = bundle.getInt(ANIMATION_TYPE, NONE);
        } else {
            bundle = new Bundle();
        }
        initParams(bundle);

        View mView = bindView();
        if (null == mView) {
            int layoutResId = bindLayout();
            if (0 != layoutResId && -1 != layoutResId) {
                mView = LayoutInflater.from(mActivity).inflate(layoutResId, mContentView, false);
            }
        }
        ViewGroup mContent = (ViewGroup) findViewById(R.id.fl_page_content);
        if (null != mView) {
            mContent.addView(mView);
        }

        // 初始化控件
        initView(mContentView);

        // 业务操作
        doBusiness(mActivity);

        // 是否可以截屏
        if (!isCanScreenshot) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    /**
     * 可以配置是否截图，Windows的相关Flags等
     *
     * @param savedInstanceState
     */
    @Override
    public void config(Bundle savedInstanceState) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "BaseActivity-->onSaveInstanceState()");
        if (null != mHostSharedData) {
            outState.putSerializable(HOST_SHARE_DATA, mHostSharedData);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "BaseActivity-->onRestoreInstanceState()");
        Object value = savedInstanceState.getSerializable(HOST_SHARE_DATA);
        if (null != value) {
            mHostSharedData = (DTO<String, Object>) value;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "BaseActivity-->onNewIntent()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "BaseActivity-->onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "BaseActivity-->onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "BaseActivity-->onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "BaseActivity-->onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "BaseActivity-->onStop()");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "BaseActivity-->onDestroy()");
        if (null != application) {
            application.removeTask(mContextWR);
        }
        //共通隐藏软键盘
        hideSoftInputFromWindow();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //让Fragment可以消费
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置状态栏背景色
     *
     * @return
     */
    public void setStatusBarBackground(@ColorInt int bgColor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(bgColor);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        ViewGroup parent = (ViewGroup) findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }

        return;
    }

    /**
     * 设置是否可截屏
     *
     * @param isCanScreenshot
     */
    public void setCanScreenshot(boolean isCanScreenshot) {
        this.isCanScreenshot = isCanScreenshot;
    }

    /**
     * 添加Fragment之间共享数据
     *
     * @param strKey
     * @param value
     */
    public void addFragmentShareData(String strKey, Object value) {
        if (null == mHostSharedData) {
            mHostSharedData = new DTO<String, Object>();
        }
        mHostSharedData.put(strKey, value);
    }

    /**
     * 获取Fragment存储的共享数据
     *
     * @param strKey
     * @return
     */
    public Object gainFragmentShareData(String strKey) {
        if (null == mHostSharedData) return null;
        return mHostSharedData.get(strKey);
    }

    /**
     * 获取标题栏
     *
     * @return
     */
    public WindowTitle getWindowTitle() {
        return mWindowTitle;
    }

    /**
     * 隐藏软键盘
     */
    public void hideSoftInputFromWindow() {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 拉起软键盘
     */
    public void pullUpSoftKeyboard(final View input) {
        if (null == input) return;
        //自动呼出软键盘
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                input.setFocusable(true);
                input.setFocusableInTouchMode(true);
                //请求获得焦点
                input.requestFocus();
                //调用系统输入法
                InputMethodManager inputManager = (InputMethodManager) input
                        .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(input, 0);
            }
        }, 300);
    }

    /**
     * 获取当前Activity，也可以直接使用内部成员变量mActivity
     *
     * @return
     */
    protected Activity getContext() {
        if (null != mContextWR)
            return mContextWR.get();
        else
            return null;
    }

    /**
     * 获取共通操作机能
     */
    public Operation getOperation() {
        return this.mOperation;
    }

    /**
     * 往当前界面最顶层增加蒙层、顶级View
     *
     * @param mMaskView
     */
    public void addMaskView(final View mMaskView) {
        addMaskView(mMaskView, null);
    }

    /**
     * 往当前界面最顶层增加蒙层、顶级View
     *
     * @param mMaskView          需要追加的view
     * @param iMaskClickListener 点击回调监听器
     */
    public void addMaskView(final View mMaskView, final IMaskClickListener iMaskClickListener) {
        final ViewGroup mRoot = (ViewGroup) findViewById(R.id.rl_page_container);
        if (null == mRoot || null == mMaskView) return;

        IMaskClickListener innerMaskClickListener = new IMaskClickListener() {
            @Override
            public void onClick(View v) {
                mRoot.removeView(mMaskView);
                if (null != iMaskClickListener) {
                    iMaskClickListener.onClick(v);
                }
            }
        };
        addMaskView(mMaskView, innerMaskClickListener);
    }

    /**
     * 往当前界面最顶层增加蒙层、顶级View
     *
     * @param layoutId           布局文件
     * @param iMaskClickListener 点击回调监听器
     */
    public void addMaskView(final int layoutId, final IMaskClickListener iMaskClickListener) {
        final ViewGroup mRoot = (ViewGroup) findViewById(R.id.rl_page_container);
        if (null == mRoot || layoutId == -1 || layoutId == 0) return;

        final View mMaskView = getLayoutInflater().inflate(layoutId, mRoot, false);
        IMaskClickListener innerMaskClickListener = new IMaskClickListener() {
            @Override
            public void onClick(View v) {
                mRoot.removeView(mMaskView);
                if (null != iMaskClickListener) {
                    iMaskClickListener.onClick(v);
                }
            }
        };
        addMaskView(mMaskView, innerMaskClickListener);
    }

    /**
     * 往当前界面最顶层增加蒙层、顶级View
     *
     * @param mRoot              追加view的根视图
     * @param mMaskView          需要追加的view
     * @param iMaskClickListener 点击回调监听器
     */
    public void addMaskView(ViewGroup mRoot, View mMaskView, IMaskClickListener iMaskClickListener) {
        if (null == mRoot || null == mMaskView) return;
        mMaskView.setOnClickListener(iMaskClickListener);
        mRoot.addView(mMaskView);
    }

    /**
     * 附加第一个Fragment到Activity,不加入回退栈
     *
     * @param fragment
     */
    public void attachFirstFragment(Fragment fragment) {
        if (null == fragment) return;
        //先移除所有Fragment
        FragmentManager manager = getSupportFragmentManager();
        boolean pop = true;
        while (pop) {
            try {
                pop = manager.popBackStackImmediate();
            } catch (Exception e) {
                ExceptionHandler.process(e);
            }
        }
        //依附Fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fl_page_content, fragment);
        ft.commitAllowingStateLoss();
    }

    /**
     * 切换Fragment，会加入回退栈
     *
     * @param fragment
     */
    public void switchFragment(Fragment fragment) {
        if (null == fragment) return;
        hideSoftInputFromWindow();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.fl_page_content, fragment);
        ft.addToBackStack(fragment.getClass().getName());
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        //回退Fragment或者activity的finish
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        //确保关闭对话框
        mOperation.closeLoading();
        int mAnimIn = 0;
        int mAnimOut = 0;
        switch (mAnimationType) {
            //左进右出
            case LEFT_RIGHT:
                mAnimIn = R.anim.anl_slide_left_in;
                mAnimOut = R.anim.anl_slide_right_out;
                break;
            //上进下出
            case TOP_BOTTOM:
                mAnimIn = R.anim.anl_push_up_in;
                mAnimOut = R.anim.anl_push_bottom_out;
                break;
            case FADE_IN_OUT:
                mAnimIn = R.anim.anl_fade_in;
                mAnimOut = R.anim.anl_fade_out;
                break;
            default:
                break;
        }

        if (mAnimIn != 0 && mAnimOut != 0) {
            overridePendingTransition(mAnimIn, mAnimOut);
        }

        mAnimationType = NONE;
    }

    @Override
    public void initParams(Bundle parms) {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
