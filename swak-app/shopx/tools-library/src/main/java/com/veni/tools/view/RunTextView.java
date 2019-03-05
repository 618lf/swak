package com.veni.tools.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug;

/**
 * TextView 跑马灯效果
 * @author  by kkan on 2016/6/28.
 */
public class RunTextView extends android.support.v7.widget.AppCompatTextView {
    public RunTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public RunTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RunTextView(Context context) {
        super(context);
    }

    /**
     * 当前并没有焦点，我只是欺骗了Android系统
     */
    @Override
    @ViewDebug.ExportedProperty(category = "focus")
    public boolean isFocused() {
        return true;
    }
}
