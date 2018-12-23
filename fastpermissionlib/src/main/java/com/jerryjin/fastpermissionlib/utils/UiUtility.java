package com.jerryjin.fastpermissionlib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;

import com.jerryjin.fastpermissionlib.abs.widget.Activity;

public class UiUtility {

    private static int STATUS_BAR_HEIGHT = -1;

    /**
     * Convert dips to pixels
     *
     * @param context Context
     * @param dp      dip(dp) values
     * @return Converted pixels.
     */
    public static float dp2px(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static float px2dp(Context context, float pixels) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float scale = displayMetrics.density;
        return pixels / scale;
    }

    public static float sp2px(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    public static float px2sp(Context context, float pixels) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float scale = displayMetrics.scaledDensity;
        return pixels / scale;
    }

    public static float getTextWidth(Paint paint, @NonNull String string) {
        return paint.measureText(string);
    }

    public static float getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return fontMetrics.descent - fontMetrics.ascent;
    }

    /**
     * Get status bar height.
     *
     * @param activity Activity
     * @return The height of current status bar.
     */
    public static int getStatusBarHeight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && STATUS_BAR_HEIGHT == -1) {
            try {
                final Resources res = activity.getResources();
                // 尝试获取status_bar_height这个属性的Id对应的资源int值
                int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId <= 0) {
                    Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                    Object object = clazz.newInstance();
                    resourceId = Integer.parseInt(clazz.getField("status_bar_height")
                            .get(object).toString());
                }


                // 如果拿到了就直接调用获取值
                if (resourceId > 0)
                    STATUS_BAR_HEIGHT = res.getDimensionPixelSize(resourceId);

                // 如果还是未拿到
                if (STATUS_BAR_HEIGHT <= 0) {
                    // 通过Window拿取
                    Rect rectangle = new Rect();
                    Window window = activity.getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                    STATUS_BAR_HEIGHT = rectangle.top;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return STATUS_BAR_HEIGHT;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        //int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        //int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        return displayMetrics.heightPixels;
    }
}
