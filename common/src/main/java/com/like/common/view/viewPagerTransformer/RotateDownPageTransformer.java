package com.like.common.view.viewPagerTransformer;

import android.support.v4.view.ViewPager;
import android.view.View;

public class RotateDownPageTransformer implements ViewPager.PageTransformer {

	private static final float ROT_MAX = 20.0f;

	public void transformPage(View view, float position) {
		if (position < -1) { // [-Infinity,-1)
			// This page is way off-screen to the left.
			view.setRotation(ROT_MAX * -1);
			view.setPivotX(view.getWidth());
			view.setPivotY(view.getHeight());

		} else if (position <= 1) { // [-1,1]

			if (position < 0)// [0，-1]
			{
				view.setPivotX(view.getWidth() * (0.5f + 0.5f * (-position)));
				view.setPivotY(view.getHeight());
				view.setRotation(ROT_MAX * position);
			} else// [1,0]
			{
				view.setPivotX(view.getWidth() * 0.5f * (1 - position));
				view.setPivotY(view.getHeight());
				view.setRotation(ROT_MAX * position);
			}
		} else { // (1,+Infinity]
			// This page is way off-screen to the right.
			view.setRotation(ROT_MAX);
			view.setPivotX(view.getWidth() * 0);
			view.setPivotY(view.getHeight());
		}
	}
}
