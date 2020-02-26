package com.like.common.view.toolbar;

import android.content.Context;

import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.view.ActionProvider;
import androidx.appcompat.widget.ActionMenuView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.like.common.R;
import com.like.common.databinding.ToolbarCustomViewBinding;
import com.like.common.view.badgeview.BadgeViewHelper;

/**
 * 替换Toolbar的menu为自定义视图
 */
public class CustomActionProvider extends ActionProvider {
    private ToolbarCustomViewBinding mBinding;
    private BadgeViewHelper badgeViewHelper;

    public CustomActionProvider(Context context) {
        super(context);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.toolbar_custom_view, null, false);
        badgeViewHelper = new BadgeViewHelper(context, mBinding.cl);
    }

    @Override
    public View onCreateActionView() {
        return mBinding.getRoot();
    }

    void setOnClickListener(final View.OnClickListener clickListener) {
        mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onClick(view);
                }
            }
        });
    }

    void hide() {
        mBinding.getRoot().setVisibility(View.GONE);
    }

    void show() {
        mBinding.getRoot().setVisibility(View.VISIBLE);
    }

    void setName(String name) {
        mBinding.tvTitle.setVisibility(View.VISIBLE);
        mBinding.tvTitle.setText(name);
    }

    void setMargin(final int leftAndRightMargin, final int topAndBottomMargin) {
        mBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mBinding.getRoot().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mBinding.getRoot().getLayoutParams();
                lp.width = mBinding.getRoot().getWidth() + leftAndRightMargin * 2;
                lp.height = mBinding.getRoot().getHeight() + topAndBottomMargin * 2;
            }
        });
    }

    void setMessageMargin(int left, int top, int right, int bottom) {
        mBinding.cl.setPadding(mBinding.cl.getPaddingLeft() + left,
                mBinding.cl.getPaddingTop() + top,
                mBinding.cl.getPaddingRight() + right,
                mBinding.cl.getPaddingBottom() + bottom);
    }

    void setTextColor(@ColorInt int color) {
        mBinding.tvTitle.setTextColor(color);
    }

    /**
     * @param size 单位sp
     */
    void setTextSize(float size) {
        mBinding.tvTitle.setTextSize(size);
    }

    String getName() {
        return mBinding.tvTitle.getText().toString();
    }

    void setIcon(@DrawableRes int iconResId) {
        mBinding.iv.setVisibility(View.VISIBLE);
        mBinding.iv.setImageResource(iconResId);
    }

    void setMessageCount(String messageCount) {
        badgeViewHelper.setMessageCount(messageCount);
    }
}
