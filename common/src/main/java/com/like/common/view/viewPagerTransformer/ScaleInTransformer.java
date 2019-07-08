package com.like.common.view.viewPagerTransformer;

import android.support.v4.view.ViewPager;
import android.view.View;

public class ScaleInTransformer implements ViewPager.PageTransformer {
	private static final float DEFAULT_MIN_SCALE = 0.85f;

	@Override
	public void transformPage(View view, float position) {
		int pageWidth = view.getWidth();
		int pageHeight = view.getHeight();

		view.setPivotY(pageHeight / 2);
		view.setPivotX(pageWidth / 2);
		if (position < -1) { // [-Infinity,-1)
								// This page is way off-screen to the left.
			view.setScaleX(DEFAULT_MIN_SCALE);
			view.setScaleY(DEFAULT_MIN_SCALE);
			view.setPivotX(pageWidth);
		} else if (position <= 1) { // [-1,1]
									// Modify the default slide transition to
									// shrink the page as well
			if (position < 0) // 1-2:1[0,-1] ;2-1:1[-1,0]
			{

				float scaleFactor = (1 + position) * (1 - DEFAULT_MIN_SCALE) + DEFAULT_MIN_SCALE;
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				view.setPivotX(pageWidth * (0.5f + (0.5f * -position)));

			} else // 1-2:2[1,0] ;2-1:2[0,1]
			{
				float scaleFactor = (1 - position) * (1 - DEFAULT_MIN_SCALE) + DEFAULT_MIN_SCALE;
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);
				view.setPivotX(pageWidth * ((1 - position) * 0.5f));
			}

		} else { // (1,+Infinity]
			view.setPivotX(0);
			view.setScaleX(DEFAULT_MIN_SCALE);
			view.setScaleY(DEFAULT_MIN_SCALE);
		}
	}
}
