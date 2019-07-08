package com.like.common.view.viewPagerTransformer;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class RotateYTransformer2 implements ViewPager.PageTransformer {
    private Context mContext;
    private static final float MIN_ALPHA = 0.1f;
    private float mRotate;

    public RotateYTransformer2(Context context) {
        mContext = context.getApplicationContext();
    }

    @Override
    public void transformPage(View view, float position) {
        Log.e("TAG", "RotateYTransformer2 position = " + position);

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics metric = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metric);
            if (metric.widthPixels > 480) {
                mRotate = 7f;
            } else {
                mRotate = 15f;
            }
        }
        view.setPivotY(view.getHeight() / 2);

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setRotationY(1 * mRotate);
            view.setPivotX(0);
        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            view.setRotationY(-position * mRotate);

            if (position < 0)// [0,-1]
            {
                view.setPivotX(0);

                float factor = MIN_ALPHA + (1 - MIN_ALPHA) * (1 + position);
                view.setAlpha(factor);
            } else// [1,0]
            {
                view.setPivotX(view.getWidth());

                float factor = MIN_ALPHA + (1 - MIN_ALPHA) * (1 - position);
                view.setAlpha(factor);
            }

            // Scale the page down (between MIN_SCALE and 1)
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setRotationY(-1 * mRotate);
            view.setPivotX(view.getWidth());
        }
    }
}
