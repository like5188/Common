package com.like.common.view.viewPagerTransformer;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class RotateUpPageTransformer implements ViewPager.PageTransformer {

	private static final float DEFAULT_MAX_ROTATE = 15.0f;

	@Override
	public void transformPage(View view, float position) {
		if (position < -1) { // [-Infinity,-1)
			// This page is way off-screen to the left.
			view.setRotation(DEFAULT_MAX_ROTATE);
			view.setPivotX(view.getWidth());
			view.setPivotY(0);

		} else if (position <= 1) // a页滑动至b页 ； a页从 0.0 ~ -1 ；b页从1 ~ 0.0
		{ // [-1,1]
			// Modify the default slide transition to shrink the page as well
			if (position < 0)// [0，-1]
			{
				view.setPivotX(view.getWidth() * (0.5f + 0.5f * (-position)));
				view.setPivotY(0);
				view.setRotation(-DEFAULT_MAX_ROTATE * position);
			} else// [1,0]
			{
				view.setPivotX(view.getWidth() * 0.5f * (1 - position));
				view.setPivotY(0);
				view.setRotation(-DEFAULT_MAX_ROTATE * position);
			}
		} else { // (1,+Infinity]
			// This page is way off-screen to the right.
			// ViewHelper.setRotation(dragPhotoView, ROT_MAX);
			view.setRotation(-DEFAULT_MAX_ROTATE);
			view.setPivotX(0);
			view.setPivotY(0);
		}
	}
}