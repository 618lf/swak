package com.veni.tools.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.veni.tools.R;

/**
 * 作者：kkan on 2018/05/23
 * 当前类注释:
 * 带有删除按钮的EditText
 */

public class ClearableEditText extends EditText
        implements EditText.OnFocusChangeListener {

    public static final String TAG = "ClearableEditText";

    public ClearableEditText(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private Drawable mClearDrawable;

    private Drawable mLeftDrawable;
    /**
     * Right Drawable 是否可见
     */
    private boolean mIsClearVisible;

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int
            defStyleRes) {

        Drawable drawables[] = getCompoundDrawables();
        mClearDrawable = drawables[2]; // Right Drawable;
        mLeftDrawable = drawables[0]; // Left Drawable;

        final Resources.Theme theme = context.getTheme();

        TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.ClearableEditText,
                defStyleAttr, defStyleRes);

        int rightDrawableColor = a.getColor(R.styleable.ClearableEditText_right_drawable_color,
                Color.WHITE);
        int leftDrawableColor = a.getColor(R.styleable.ClearableEditText_left_drawable_color,
                Color.WHITE);

        a.recycle();

        // 给mRightDrawable上色
        if (mClearDrawable != null) {
            DrawableCompat.setTint(mClearDrawable, rightDrawableColor);
        }
        if (mLeftDrawable != null) {
            DrawableCompat.setTint(mLeftDrawable, leftDrawableColor);
        }

        setOnFocusChangeListener(this);

        // 添加TextChangedListener
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged " + s);

                setClearDrawableVisible(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 第一次隐藏
        setClearDrawableVisible(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // error drawable 不显示 && clear drawable 显示 && action up
        if (getError() == null && mIsClearVisible && event.getAction() == MotionEvent.ACTION_UP) {

            float x = event.getX();
            if (x >= getWidth() - getTotalPaddingRight() && x <= getWidth() - getPaddingRight()) {
//点击清除按钮
                clearText();
            }
        }

        return super.onTouchEvent(event);
    }


    /**
     * 清空输入框
     */
    private void clearText() {
        if (getText().length() > 0) {
            setText("");
        }
    }


    /**
     * 设置Right Drawable是否可见
     *
     * @param isVisible true for visible , false for invisible
     */
    public void setClearDrawableVisible(boolean isVisible) {

        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                isVisible ? mClearDrawable : null, getCompoundDrawables()[3]);

        mIsClearVisible = isVisible;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(TAG, "getTotalPaddingTop = " + getTotalPaddingTop());
        Log.d(TAG, "getExtendedPaddingTop = " + getExtendedPaddingTop());

        // error drawable 不显示的时候
        if (getError() == null) {
            if (hasFocus) {
                if (getText().length() > 0) {
                    setClearDrawableVisible(true);
                }
            } else {
                setClearDrawableVisible(false);
            }
        }
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        if (error != null) {
            setClearDrawableVisible(true);
        }
        super.setError(error, icon);
    }
}
