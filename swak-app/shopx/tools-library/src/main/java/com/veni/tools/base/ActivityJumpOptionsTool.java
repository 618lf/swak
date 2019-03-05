package com.veni.tools.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.veni.tools.LogTools;
import com.veni.tools.R;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：kkan on 2017/12/22
 * 当前类注释:
 * Activity跳转管理类
 */

public class ActivityJumpOptionsTool {
    public enum Type {
        NEW_TASK,
        CLEAR_TASK
    }

    private Context context;
    private int enterResId = R.anim.dock_right_enter;//进入动画
    private int exitResId = R.anim.dock_left_exit;//退出动画
    private int bitmapid = R.drawable.set;//新的activity将通过这个bitmap渐变拉伸出现，新的activity初始大小就是这个bitmap的大小
    private int startWidth = 0;//新activity出现的初始X坐标，这个坐标是相对于source的左上角X坐标
    private int startHeight = 0;//新activity出现的初始Y坐标，这个坐标相对于source的左上角Y坐标
    private Intent intent;
    private Class<?> clas;
    private Bundle bundle;
    private View view;//用户确定新activity启动的初始坐标
    private String actionString = "";//view动画的标识
    private ActivityOptionsCompat options = null;

    private Type actionTag = Type.NEW_TASK;//intent标识

    /**
     *  设置上次下文
     */
    public ActivityJumpOptionsTool setContext(Context context) {
        this.context = context;
        return this;
    }

    /**
     *  设置跳转目标Class
     */
    public ActivityJumpOptionsTool setClass(Class<?> clas) {
        this.clas = clas;
        return this;
    }

    /**
     *  设置跳转参数
     */
    public ActivityJumpOptionsTool setBundle(String key, Object value) {
        if (this.bundle == null) {
            this.bundle = new Bundle();
        }
        if (value == null) {
            return this;
        }
        if (value instanceof String && value.toString().length() > 0) {
            bundle.putString(key, (String) value);
        } else if (value instanceof Integer) {
            bundle.putInt(key, (Integer) value);
        } else if (value instanceof Double) {
            bundle.putDouble(key, (Double) value);
        } else if (value instanceof Boolean) {
            bundle.putBoolean(key, (Boolean) value);
        } else if (value.getClass().isArray() && Array.getLength(value) > 0) {
            List<Object> list = (List<Object>) value;
            Object object = list.get(0);
            if (object instanceof String) {
                bundle.putStringArrayList(key, (ArrayList<String>) value);
            }
        } else if (value instanceof Serializable) {
            bundle.putSerializable(key, (Serializable) value);
        } else {
            bundle.putString(key, value + "");
        }

        return this;
    }

    /**
     * 设置跳转intent的flag
     */
    public ActivityJumpOptionsTool setActionTag(@NonNull Type actionTag) {
        this.actionTag = actionTag;
        return this;
    }

    /**
     * 是否关闭act
     * @param finish ture 关闭
     * @return
     */
    public ActivityJumpOptionsTool setFinish(boolean finish) {
        if(finish){
            actionTag = ActivityJumpOptionsTool.Type.CLEAR_TASK;
        }
        return this;
    }

    /**
     * 设置进入动画
     */
    public ActivityJumpOptionsTool setEnterResId(int enterResId) {
        this.enterResId = enterResId;
        return this;
    }

    /**
     * 设置退出动画
     */
    public ActivityJumpOptionsTool setExitResId(int exitResId) {
        this.exitResId = exitResId;
        return this;
    }

    /**
     * 设置显示进入退出动画的view
     */
    public ActivityJumpOptionsTool setView(View view) {
        this.view = view;
        return this;
    }

    public ActivityJumpOptionsTool setStartWidth(int startWidth) {
        this.startWidth = startWidth;
        return this;
    }

    public ActivityJumpOptionsTool setStartHeight(int startHeight) {
        this.startHeight = startHeight;
        return this;
    }

    public ActivityJumpOptionsTool setBitmapid(int bitmapid) {
        this.bitmapid = bitmapid;
        return this;
    }

    /**
     * view动画的标识
     */
    public ActivityJumpOptionsTool setActionString(String actionString) {
        this.actionString = actionString;
        return this;
    }

    private void getIntent() {
        if (context != null && clas != null) {
            intent = new Intent(context, clas);
            addintentflag(actionTag);
            if (bundle != null) intent.putExtras(bundle);
        }
    }

    /**
     * 启动activity
     */
    public void start(int requestCode) {
        getIntent();
        if (context == null || options == null || intent == null) {
            return;
        }
        addintentflag(Type.NEW_TASK);
        ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, options.toBundle());

    }

    /**
     * 启动activity
     */
    public void start() {
        getIntent();
        if (context == null || options == null || intent == null) {
            return;
        }
        ActivityCompat.startActivity(context, intent, options.toBundle());
        if (actionTag == Type.CLEAR_TASK) {
            ((Activity) context).finish();
        }
    }

    private void addintentflag(Type actionTag) {
        if (actionTag == Type.CLEAR_TASK) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else if (actionTag == Type.NEW_TASK) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    /**
     * 设置自定义的Activity动画，出入的是动画资源的id。
     * makeCustomAnimation
     * enterId：进入动画的资源ID
     * exitId：退出动画的资源ID
     */
    public ActivityJumpOptionsTool customAnim() {
        this.options = ActivityOptionsCompat.makeCustomAnimation(context,
                enterResId, exitResId);
        return this;
    }

    /**
     * 新的Activity从某个位置以某个大小出现，然后慢慢拉伸渐变到整个屏幕
     * makeScaleUpAnimation
     * source：一个view对象，用户确定新activity启动的初始坐标
     * startX：新activity出现的初始X坐标，这个坐标是相对于source的左上角X坐标
     * startY：新activity出现的初始Y坐标，这个坐标相对于source的左上角Y坐标
     * width：新activity初始的宽度
     * height：新activity初始的高度
     */
    public ActivityJumpOptionsTool scaleUpAnim() {
        this.options = ActivityOptionsCompat.makeScaleUpAnimation(view,
                view.getWidth() / 2, view.getHeight() / 2, startWidth, startHeight);
        return this;
    }

    /**
     * 一个bitmap慢慢从某个位置拉伸渐变新的activity
     * makeThumbnailScaleUpAnimation
     * source：一个view对象，用来确定起始坐标
     * thumbnail：一个bitmap对象，新的activity将通过这个bitmap渐变拉伸出现，新的activity初始大小就是这个bitmap的大小
     * startX：新activity初始的X坐标，相对于source左上角的X来说的
     * startY：新的activity初始的Y坐标，相对于source左上角Y坐标来说的
     */

    public ActivityJumpOptionsTool thumbNailScaleAnim() {
        Bitmap bitmap;
        if (bitmapid == 0) {
//            if (view instanceof ImageView) {
            view.setDrawingCacheEnabled(true);
            bitmap = view.getDrawingCache();
//            } else {
//                bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round);
//            }
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), bitmapid);
        }
        this.options = ActivityOptionsCompat.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0);
        return this;
    }

    /**
     * 原始activity中的一个view随着新activity的慢慢启动而移动到新的activity中，实现补间动画
     * makeSceneTransitionAnimation
     * activity：当前activity的对象
     * sharedElement：一个view对象，用来和新的activity中的一个view对象产生动画
     * sharedElemetId:新的activity中的view的Id，这个view是用来和原始activity中的view产生动画的
     * <p>
     * 在初始化view设置
     * ViewCompat.setTransitionName(iv_a,"options5");
     */
    public ActivityJumpOptionsTool screenTransitAnim() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return screenTransitAnimByPair(new Pair<>(view, actionString));
        } else {
            return scaleUpAnim();
        }
    }

    /**
     * 原始activity中的一个view随着新activity的慢慢启动而移动到新的activity中，实现补间动画
     * makeSceneTransitionAnimation
     * activity：当前的activity
     * sharedElements：Pair对象，上面的一个方法是实现单一view的动画，这里可以有多个view对象进行动画
     * <p>
     * 在初始化view设置
     * ViewCompat.setTransitionName(iv_a,"options5");
     */
    public ActivityJumpOptionsTool screenTransitAnimByPair(Pair<View, String>... views) {
        this.options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, views);
        return this;
    }
}
