package com.like.common.view.banner;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.like.common.util.DimensionUtils;
import com.like.common.util.ImageUtils;

import java.util.List;

/**
 * 小圆点指示器控制器
 */
public class DotIndicatorController implements IIndicatorController {
    private Context mContext;
    private int mPreSelectedPosition = 0;
    private LinearLayout mIndicatorContainer;
    private int mNormalIndicatorResId;
    private List<Integer> mSelectedIndicatorResIds;
    private int mIndicatorPadding;
    private int mIndicatorCount;

    /**
     * @param context
     * @param indicatorContainer      指示器的容器
     * @param normalIndicatorResId    正常状态的指示器图片id
     * @param selectedIndicatorResIds 选中状态的指示器图片id，可以为多个，比如每个选中状态对应一种颜色。
     * @param indicatorPadding        指示器之间的间隔，默认10dp
     */
    public DotIndicatorController(Context context, LinearLayout indicatorContainer, int indicatorCount, int normalIndicatorResId, List<Integer> selectedIndicatorResIds, int indicatorPadding) {
        mContext = context;
        mIndicatorContainer = indicatorContainer;
        mIndicatorCount = indicatorCount;
        mNormalIndicatorResId = normalIndicatorResId;
        mSelectedIndicatorResIds = selectedIndicatorResIds;
        mIndicatorPadding = indicatorPadding <= 0 ? DimensionUtils.dp2px(context, 10) : indicatorPadding;
        checkArgumentValidity();
        init();
    }

    /**
     * 检查参数的有效性
     */
    private void checkArgumentValidity() {
        if (mIndicatorContainer == null) {
            throw new IllegalArgumentException("container不能为null");
        }
        if (mNormalIndicatorResId <= 0) {
            throw new IllegalArgumentException("normalIndicatorResId无效");
        }
        if (mSelectedIndicatorResIds.size() <= 0) {
            throw new IllegalArgumentException("selectedIndicatorResIds无效");
        }
        if (mIndicatorPadding <= 0) {
            throw new IllegalArgumentException("indicatorPadding无效");
        }
        if (mIndicatorCount <= 0) {
            throw new IllegalArgumentException("indicatorCount必须大于0");
        }
    }

    private void init() {
        mIndicatorContainer.removeAllViews();
        for (int i = 0; i < mIndicatorCount; i++) {
            // 加载指示器图片
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);// 设置指示器宽高
            ImageView iv = new ImageView(mContext);
            if (i == 0) {
                iv.setBackgroundResource(mSelectedIndicatorResIds.get(0));
                params.setMargins(0, 0, 0, 0);// 设置指示器边距
            } else {
                iv.setBackgroundResource(mNormalIndicatorResId);
                params.setMargins(mIndicatorPadding, 0, 0, 0);// 设置指示器边距
            }
            iv.setLayoutParams(params);
            mIndicatorContainer.addView(iv);
        }
    }

    @Override
    public void select(int position) {
        mIndicatorContainer.getChildAt(mPreSelectedPosition).setBackgroundResource(mNormalIndicatorResId);
        int selectResId = 0;
        if (position >= mSelectedIndicatorResIds.size()) {
            selectResId = mSelectedIndicatorResIds.get(mSelectedIndicatorResIds.size() - 1);
        } else {
            selectResId = mSelectedIndicatorResIds.get(position);
        }
        mIndicatorContainer.getChildAt(position).setBackgroundResource(selectResId);
        mPreSelectedPosition = position;
    }

    @Override
    public void destroy() {
        for (int i = 0; i < mIndicatorContainer.getChildCount(); i++) {
            ImageView imageView = (ImageView) mIndicatorContainer.getChildAt(i);
            ImageUtils.releaseImageViewResource(imageView);
        }
    }
}
