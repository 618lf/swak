package com.veni.tools.view.mixed;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.veni.tools.R;

import java.util.HashSet;

/**
 * 作者：kkan on 2017/12/08
 * 当前类注释:
 * 图文混排
 */

public class GlideImageGeter implements Html.ImageGetter {

    private HashSet<GifDrawable> gifDrawables;
    private final Context mContext;
    private final TextView mTextView;
    private  RequestOptions requestOptions;

    public void recycle() {
        for (GifDrawable gifDrawable : gifDrawables) {
            gifDrawable.setCallback(null);
            gifDrawable.recycle();
        }
        gifDrawables.clear();
    }

    public GlideImageGeter(Context context, TextView textView) {
        this.mContext = context;
        this.mTextView = textView;
        gifDrawables = new HashSet<>();
        mTextView.setTag(R.id.img_tag);
    }

    @Override
    public Drawable getDrawable(String url) {
        UrlDrawable urlDrawable = new UrlDrawable();
        RequestBuilder load = Glide.with(mContext).load(url).apply(getOptions());
        Target target = new DrawableTarget(urlDrawable);
        load.into(target);
        return urlDrawable;
    }

    private RequestOptions getOptions() {
        if(requestOptions==null){
            requestOptions= new RequestOptions()
                    //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
                    .fitCenter()
                    //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
                    .centerCrop()
                    .priority(Priority.HIGH)// 当前线程的优先级
                    .skipMemoryCache(true);//跳过内存缓存
        }
        return requestOptions;
    }

    private class DrawableTarget extends SimpleTarget<Drawable> {

        private final UrlDrawable urlDrawable;

        private DrawableTarget(UrlDrawable urlDrawable) {
            this.urlDrawable = urlDrawable;
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
            if (resource instanceof GifDrawable) {
                GifDrawable gifDrawable = (GifDrawable) resource;
                int w = getScreenSize(mContext).x;
                int hh = gifDrawable.getIntrinsicHeight();
                int ww = gifDrawable.getIntrinsicWidth();
                int high = hh * (w - 50) / ww;
                Rect rect = new Rect(20, 20, w - 30, high);
                gifDrawable.setBounds(rect);
                urlDrawable.setBounds(rect);
                urlDrawable.setDrawable(gifDrawable);
                gifDrawables.add(gifDrawable);
                gifDrawable.setCallback(mTextView);
                gifDrawable.setLoopCount(GifDrawable.LOOP_FOREVER);
                gifDrawable.start();
                mTextView.setText(mTextView.getText());
                mTextView.invalidate();
            } else {
                int w = getScreenSize(mContext).x;
                int hh = resource.getIntrinsicHeight();
                int ww = resource.getIntrinsicWidth();
                int high = hh * (w - 40) / ww;
                Rect rect = new Rect(20, 20, w - 20, high);
                resource.setBounds(rect);
                urlDrawable.setBounds(rect);
                urlDrawable.setDrawable(resource);
                mTextView.setText(mTextView.getText());
                mTextView.invalidate();
            }
        }
    }

    /**
     * 获取屏幕尺寸
     *
     * @param context 上下文
     * @return 屏幕尺寸像素值，下标为0的值为宽，下标为1的值为高
     */
    private Point getScreenSize(Context context) {

        // 获取屏幕宽高
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point screenSize = new Point();
        wm.getDefaultDisplay().getSize(screenSize);
        return screenSize;
    }
}
