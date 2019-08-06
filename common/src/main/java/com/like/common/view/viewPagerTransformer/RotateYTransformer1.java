package com.like.common.view.viewPagerTransformer;

import androidx.viewpager.widget.ViewPager;
import android.view.View;

public class RotateYTransformer1 implements ViewPager.PageTransformer {
	private static final float MAX_ROTATE = 35f;

	@Override
	public void transformPage(View view, float position) {
		view.setPivotY(view.getHeight() / 2);

		if (position < -1) { // [-Infinity,-1)
			// This page is way off-screen to the left.
			view.setRotationY(-1 * MAX_ROTATE);
			view.setPivotX(view.getWidth());
		} else if (position <= 1) { // [-1,1]
			// Modify the default slide transition to shrink the page as well
			view.setRotationY(position * MAX_ROTATE);

			if (position < 0)// [0,-1]
			{
//				dragPhotoView.setPivotX(dragPhotoView.getWidth() * (0.5f + 0.5f * (-position)));
				view.setPivotX(view.getWidth());
			} else// [1,0]
			{
//				dragPhotoView.setPivotX(dragPhotoView.getWidth() * 0.5f * (1 - position));
				view.setPivotX(0);
			}

			// Scale the page down (between MIN_SCALE and 1)
		} else { // (1,+Infinity]
			// This page is way off-screen to the right.
			view.setRotationY(1 * MAX_ROTATE);
			view.setPivotX(0);
		}
	}
}
