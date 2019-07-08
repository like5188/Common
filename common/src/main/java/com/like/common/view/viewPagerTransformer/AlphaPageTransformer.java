package com.like.common.view.viewPagerTransformer;

import android.support.v4.view.ViewPager;
import android.view.View;

public class AlphaPageTransformer implements ViewPager.PageTransformer {
    private static final float DEFAULT_MIN_ALPHA = 0.5f;

    @Override
    public void transformPage(View view, float position) {
        view.setScaleX(0.999f);// hack

        if (position < -1) { // [-Infinity,-1)
            view.setAlpha(DEFAULT_MIN_ALPHA);
        } else if (position <= 1) { // [-1,1]

            if (position < 0) // [0，-1]
            { // [1,min]
                float factor = DEFAULT_MIN_ALPHA + (1 - DEFAULT_MIN_ALPHA) * (1 + position);
                view.setAlpha(factor);
            } else// [1，0]
            {
                // [min,1]
                float factor = DEFAULT_MIN_ALPHA + (1 - DEFAULT_MIN_ALPHA) * (1 - position);
                view.setAlpha(factor);
            }
        } else { // (1,+Infinity]
            view.setAlpha(DEFAULT_MIN_ALPHA);
        }
    }
}
