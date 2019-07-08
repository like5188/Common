package com.like.common.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * 单位转换的工具类
 */
public class DimensionUtils {
    private DimensionUtils() {
        // 不允许直接构造此类，也不允许反射构造此类
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        Context applicationContext = context.getApplicationContext();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, applicationContext.getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        Context applicationContext = context.getApplicationContext();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, applicationContext.getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static int px2dp(Context context, float pxVal) {
        Context applicationContext = context.getApplicationContext();
        return (int) (pxVal / applicationContext.getResources().getDisplayMetrics().density);
    }

    /**
     * px转sp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static int px2sp(Context context, float pxVal) {
        Context applicationContext = context.getApplicationContext();
        return (int) (pxVal / applicationContext.getResources().getDisplayMetrics().scaledDensity);
    }

}
