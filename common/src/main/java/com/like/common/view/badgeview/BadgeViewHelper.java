package com.like.common.view.badgeview;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.view.View;

/**
 * 未读消息数量显示的徽章视图，注意，target需要有ViewGroup的parent。
 */
public class BadgeViewHelper {
    private BadgeView badgeView;

    public BadgeViewHelper(Context context, View target) {
        badgeView = new BadgeView(context);
        badgeView.setTargetView(target);
    }

    public void setTextColor(@ColorInt int color) {
        badgeView.setBadgeTextColor(color);
    }

    /**
     * @param size 单位sp
     */
    public void setTextSize(int size) {
        badgeView.setBadgeTextSize(size);
    }

    public void setBackgroundColor(@ColorInt int color) {
        badgeView.setBadgeBackgroundColor(color);
    }

    public void setMessageCount(String messageCount) {
        badgeView.setBadgeCount(messageCount);
    }
}
