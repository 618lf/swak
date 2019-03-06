package com.veni.tools.view.itoast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.veni.tools.FutileTools;
import com.veni.tools.R;

/**
 * Toast的封装
 * 关闭系统通知Toast 将无法使用
 */
/*
* ToastTool 显示特殊字体 PCap Terminal.otf 为assets下的字体库
  ToastTool.Config.getInstance()
 .setTextColor(Color.GREEN)
 .setToastTypeface(Typeface.createFromAsset(getAssets(), "PCap Terminal.otf"))
 .apply();
 ToastTool.custom(MainActivity.this, R.string.custom_message, getResources().getDrawable(R.drawable.laptop512),
 Color.BLACK, Toast.LENGTH_SHORT, true, true).show();
 ToastTool.Config.reset()*/
@SuppressLint("InflateParams")
public class ToastTool {
    @ColorInt
    private static int DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
    @ColorInt
    private static int ERROR_COLOR = Color.parseColor("#FD4C5B");
    @ColorInt
    private static int INFO_COLOR = Color.parseColor("#3F51B5");
    @ColorInt
    private static int SUCCESS_COLOR = Color.parseColor("#388E3C");
    @ColorInt
    private static int WARNING_COLOR = Color.parseColor("#FFA900");
    @ColorInt
    private static int NORMAL_COLOR = Color.parseColor("#808080");

    private static final Typeface LOADED_TOAST_TYPEFACE = Typeface.create("sans-serif-condensed", Typeface.NORMAL);
    private static Typeface currentTypeface = LOADED_TOAST_TYPEFACE;
    private static int textSize = 16; // in SP

    private static boolean tintIcon = true;

    private static IToast currentToast;

    private static Toast mToast;

    private ToastTool() {
        // avoiding instantiation
    }

    //*******************************************普通 使用ApplicationContext 方法*********************

    public static void normal(@NonNull String message) {
        normal(FutileTools.getContext(), message).show();
    }

    public static void normal(@StringRes int message) {
        normal(FutileTools.getContext(), message).show();
    }

    public static void info(@NonNull String message) {
        info(FutileTools.getContext(), message).show();
    }

    public static void info(@StringRes int message) {
        info(FutileTools.getContext(), message).show();
    }

    public static void success(@NonNull String message) {
        success(FutileTools.getContext(), message).show();
    }

    public static void success(@StringRes int message) {
        success(FutileTools.getContext(), message).show();
    }

    public static void error(@NonNull String message) {
        error(FutileTools.getContext(), message).show();
    }

    public static void error(@StringRes int message) {
        error(FutileTools.getContext(), message).show();
    }

    public static void warning(@NonNull String message) {
        warning(FutileTools.getContext(), message).show();
    }

    public static void warning(@StringRes int message) {
        warning(FutileTools.getContext(), message).show();
    }

    //===========================================使用ApplicationContext 方法=========================


    //*******************************************常规方法********************************************
    @CheckResult
    public static IToast normal(@NonNull Context context, @StringRes int message) {
        return normal(context, context.getString(message), Toast.LENGTH_SHORT, null, false);
    }

    @CheckResult
    public static IToast normal(@NonNull Context context, @NonNull CharSequence message) {
        return normal(context, message, Toast.LENGTH_SHORT, null, false);
    }

    @CheckResult
    public static IToast normal(@NonNull Context context, @StringRes int message, Drawable icon) {
        return normal(context, context.getString(message), Toast.LENGTH_SHORT, icon, true);
    }

    @CheckResult
    public static IToast normal(@NonNull Context context, @NonNull CharSequence message, Drawable icon) {
        return normal(context, message, Toast.LENGTH_SHORT, icon, true);
    }

    @CheckResult
    public static IToast normal(@NonNull Context context, @StringRes int message, int duration) {
        return normal(context, context.getString(message), duration, null, false);
    }

    @CheckResult
    public static IToast normal(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return normal(context, message, duration, null, false);
    }

    @CheckResult
    public static IToast normal(@NonNull Context context, @StringRes int message, int duration,
                                Drawable icon) {
        return normal(context, context.getString(message), duration, icon, true);
    }

    @CheckResult
    public static IToast normal(@NonNull Context context, @NonNull CharSequence message, int duration,
                                Drawable icon) {
        return normal(context, message, duration, icon, true);
    }

    @CheckResult
    public static IToast normal(@NonNull Context context, @StringRes int message, int duration,
                                Drawable icon, boolean withIcon) {
        return custom(context, context.getString(message), icon, NORMAL_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static IToast normal(@NonNull Context context, @NonNull CharSequence message, int duration,
                                Drawable icon, boolean withIcon) {
        return custom(context, message, icon, NORMAL_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static IToast warning(@NonNull Context context, @StringRes int message) {
        return warning(context, context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static IToast warning(@NonNull Context context, @NonNull CharSequence message) {
        return warning(context, message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static IToast warning(@NonNull Context context, @StringRes int message, int duration) {
        return warning(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static IToast warning(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return warning(context, message, duration, true);
    }

    @CheckResult
    public static IToast warning(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), getDrawable(context, R.drawable.ic_error_outline_white_48dp),
                WARNING_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static IToast warning(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, getDrawable(context, R.drawable.ic_error_outline_white_48dp),
                WARNING_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static IToast info(@NonNull Context context, @StringRes int message) {
        return info(context, context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static IToast info(@NonNull Context context, @NonNull CharSequence message) {
        return info(context, message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static IToast info(@NonNull Context context, @StringRes int message, int duration) {
        return info(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static IToast info(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return info(context, message, duration, true);
    }

    @CheckResult
    public static IToast info(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), getDrawable(context, R.drawable.ic_info_outline_white_48dp),
                INFO_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static IToast info(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, getDrawable(context, R.drawable.ic_info_outline_white_48dp),
                INFO_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static IToast success(@NonNull Context context, @StringRes int message) {
        return success(context, context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static IToast success(@NonNull Context context, @NonNull CharSequence message) {
        return success(context, message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static IToast success(@NonNull Context context, @StringRes int message, int duration) {
        return success(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static IToast success(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return success(context, message, duration, true);
    }

    @CheckResult
    public static IToast success(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), getDrawable(context, R.drawable.ic_check_white_48dp),
                SUCCESS_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static IToast success(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, getDrawable(context, R.drawable.ic_check_white_48dp),
                SUCCESS_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static IToast error(@NonNull Context context, @StringRes int message) {
        return error(context, context.getString(message), Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static IToast error(@NonNull Context context, @NonNull CharSequence message) {
        return error(context, message, Toast.LENGTH_SHORT, true);
    }

    @CheckResult
    public static IToast error(@NonNull Context context, @StringRes int message, int duration) {
        return error(context, context.getString(message), duration, true);
    }

    @CheckResult
    public static IToast error(@NonNull Context context, @NonNull CharSequence message, int duration) {
        return error(context, message, duration, true);
    }

    @CheckResult
    public static IToast error(@NonNull Context context, @StringRes int message, int duration, boolean withIcon) {
        return custom(context, context.getString(message), getDrawable(context, R.drawable.ic_clear_white_48dp),
                ERROR_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static IToast error(@NonNull Context context, @NonNull CharSequence message, int duration, boolean withIcon) {
        return custom(context, message, getDrawable(context, R.drawable.ic_clear_white_48dp),
                ERROR_COLOR, duration, withIcon, true);
    }

    @CheckResult
    public static IToast custom(@NonNull Context context, @StringRes int message, Drawable icon,
                                int duration, boolean withIcon) {
        return custom(context, context.getString(message), icon, -1, duration, withIcon, false);
    }

    @CheckResult
    public static IToast custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
                                int duration, boolean withIcon) {
        return custom(context, message, icon, -1, duration, withIcon, false);
    }

    @CheckResult
    public static IToast custom(@NonNull Context context, @StringRes int message, @DrawableRes int iconRes,
                                @ColorInt int tintColor, int duration,
                                boolean withIcon, boolean shouldTint) {
        return custom(context, context.getString(message), getDrawable(context, iconRes),
                tintColor, duration, withIcon, shouldTint);
    }

    @CheckResult
    public static IToast custom(@NonNull Context context, @NonNull CharSequence message, @DrawableRes int iconRes,
                                @ColorInt int tintColor, int duration,
                                boolean withIcon, boolean shouldTint) {
        return custom(context, message, getDrawable(context, iconRes),
                tintColor, duration, withIcon, shouldTint);
    }

    @CheckResult
    public static IToast custom(@NonNull Context context, @StringRes int message, Drawable icon,
                                @ColorInt int tintColor, int duration,
                                boolean withIcon, boolean shouldTint) {
        return custom(context, context.getString(message), icon, tintColor, duration,
                withIcon, shouldTint);
    }
    //===========================================常规方法============================================

    //*******************************************内需方法********************************************

    /**
     * @param context    上下文
     * @param message    提示信息
     * @param icon       提示信息 图片
     * @param tintColor  提示信息颜色
     * @param duration   提示时长
     * @param withIcon   是否开启提示图片
     * @param shouldTint 是否显示提示颜色
     * @return IToast
     */
    @SuppressLint("ShowIToast")
    @CheckResult
    public static IToast custom(@NonNull Context context, @NonNull CharSequence message, Drawable icon,
                                @ColorInt int tintColor, int duration,
                                boolean withIcon, boolean shouldTint) {
//        if (currentToast != null) {
//            currentToast.cancel();
//            currentToast = null;
//        }
        currentToast = ToastFactory.getInstance(context);
        final View toastLayout = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.rx_toast_layout, null);
        final ImageView toastIcon = toastLayout.findViewById(R.id.toast_icon);
        final TextView toastTextView = toastLayout.findViewById(R.id.toast_text);
        Drawable drawableFrame;

        if (shouldTint) {
            drawableFrame = tint9PatchDrawableFrame(context, tintColor);
        } else {
            drawableFrame = tint9PatchDrawableFrame(context, NORMAL_COLOR);
//            drawableFrame = getDrawable(context, R.drawable.toast_frame);
        }
        setBackground(toastLayout, drawableFrame);

        if (withIcon) {
            if (icon == null)
                throw new IllegalArgumentException("Avoid passing 'icon' as null if 'withIcon' is set to true");
            if (tintIcon)
                icon = tintIcon(icon, DEFAULT_TEXT_COLOR);
            setBackground(toastIcon, icon);
        } else {
            toastIcon.setVisibility(View.GONE);
        }

        toastTextView.setText(message);
        toastTextView.setTextColor(DEFAULT_TEXT_COLOR);
        toastTextView.setTypeface(currentTypeface);
        toastTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        currentToast.setView(toastLayout);
        currentToast.setDuration(duration);
        return currentToast;
    }

    private static Drawable tintIcon(@NonNull Drawable drawable, @ColorInt int tintColor) {
        drawable.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    private static Drawable tint9PatchDrawableFrame(@NonNull Context context, @ColorInt int tintColor) {
//        final NinePatchDrawable toastDrawable = (NinePatchDrawable) getDrawable(context, R.drawable.toast_frame);
        Drawable toastDrawable = getDrawable(context, R.drawable.corners30_gray);
        DrawableCompat.setTint(toastDrawable, tintColor);
        return tintIcon(toastDrawable, tintColor);
    }

    private static void setBackground(@NonNull View view, Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            view.setBackground(drawable);
        else
            view.setBackgroundDrawable(drawable);
    }

    public static Drawable getDrawable(@NonNull Context context, @DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return context.getDrawable(id);
        else
            return context.getResources().getDrawable(id);
    }
    //===========================================内需方法============================================

    public static class Config {
        @ColorInt
        private int DEFAULT_TEXT_COLOR = ToastTool.DEFAULT_TEXT_COLOR;
        @ColorInt
        private int ERROR_COLOR = ToastTool.ERROR_COLOR;
        @ColorInt
        private int INFO_COLOR = ToastTool.INFO_COLOR;
        @ColorInt
        private int SUCCESS_COLOR = ToastTool.SUCCESS_COLOR;
        @ColorInt
        private int WARNING_COLOR = ToastTool.WARNING_COLOR;

        private Typeface typeface = ToastTool.currentTypeface;
        private int textSize = ToastTool.textSize;

        private boolean tintIcon = ToastTool.tintIcon;

        private Config() {
            // avoiding instantiation
        }

        @CheckResult
        public static Config getInstance() {
            return new Config();
        }

        public static void reset() {
            ToastTool.DEFAULT_TEXT_COLOR = Color.parseColor("#FFFFFF");
            ToastTool.ERROR_COLOR = Color.parseColor("#FD4C5B");
            ToastTool.INFO_COLOR = Color.parseColor("#3F51B5");
            ToastTool.SUCCESS_COLOR = Color.parseColor("#388E3C");
            ToastTool.WARNING_COLOR = Color.parseColor("#FFA900");
            ToastTool.currentTypeface = LOADED_TOAST_TYPEFACE;
            ToastTool.textSize = 16;
            ToastTool.tintIcon = true;
        }

        @CheckResult
        public Config setTextColor(@ColorInt int textColor) {
            DEFAULT_TEXT_COLOR = textColor;
            return this;
        }

        @CheckResult
        public Config setErrorColor(@ColorInt int errorColor) {
            ERROR_COLOR = errorColor;
            return this;
        }

        @CheckResult
        public Config setInfoColor(@ColorInt int infoColor) {
            INFO_COLOR = infoColor;
            return this;
        }

        @CheckResult
        public Config setSuccessColor(@ColorInt int successColor) {
            SUCCESS_COLOR = successColor;
            return this;
        }

        @CheckResult
        public Config setWarningColor(@ColorInt int warningColor) {
            WARNING_COLOR = warningColor;
            return this;
        }

        @CheckResult
        public Config setToastTypeface(@NonNull Typeface typeface) {
            this.typeface = typeface;
            return this;
        }

        @CheckResult
        public Config setTextSize(int sizeInSp) {
            this.textSize = sizeInSp;
            return this;
        }

        @CheckResult
        public Config tintIcon(boolean tintIcon) {
            this.tintIcon = tintIcon;
            return this;
        }

        public void apply() {
            ToastTool.DEFAULT_TEXT_COLOR = DEFAULT_TEXT_COLOR;
            ToastTool.ERROR_COLOR = ERROR_COLOR;
            ToastTool.INFO_COLOR = INFO_COLOR;
            ToastTool.SUCCESS_COLOR = SUCCESS_COLOR;
            ToastTool.WARNING_COLOR = WARNING_COLOR;
            ToastTool.currentTypeface = typeface;
            ToastTool.textSize = textSize;
            ToastTool.tintIcon = tintIcon;
        }
    }

    //*********************************系统Toast 替代方法*******************************

    /**
     * Toast 替代方法 ：立即显示无需等待
     *
     * @param msg 要显示的字符串
     */
    public static void showToast(@NonNull String msg) {
        showToast(FutileTools.getContext(), msg, Toast.LENGTH_SHORT);
    }

    public static void showToast(@StringRes int resId) {
        showToast(FutileTools.getContext().getString(resId));
    }

    /**
     * Toast 替代方法 ：立即显示无需等待
     *
     * @param context  实体
     * @param resId    String资源ID
     * @param duration 显示时长
     */
    public static void showToast(@NonNull Context context, @StringRes int resId, int duration) {
        showToast(context, context.getString(resId), duration);
    }
    //===========================================系统Toast 替代方法======================================

    /**
     * Toast 替代方法 ：立即显示无需等待
     *
     * @param context  实体
     * @param msg      要显示的字符串
     * @param duration 显示时长
     */
    public static void showToast(@NonNull Context context, @NonNull String msg, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration);
        } else {
            mToast.cancel();
            mToast = Toast.makeText(context, msg, duration);
        }
        mToast.show();
    }
}
