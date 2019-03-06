package com.veni.tools.view.mixed;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * 作者：kkan on 2017/12/08
 * 当前类注释:
 */

public class UrlDrawable extends BitmapDrawable implements Drawable.Callback{
    private Drawable drawable;

    @SuppressWarnings("deprecation")
    public UrlDrawable() {
    }

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null)
            drawable.draw(canvas);
    }

    public Drawable getDrawable() {
        return drawable;
    }
    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        scheduleSelf(what, when);
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        unscheduleSelf(what);
    }
    @Override
    public void invalidateDrawable(Drawable who) {
        invalidateSelf();
    }
    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
