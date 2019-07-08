package com.like.common.util;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.util.Collections;
import java.util.List;

/**
 * 高亮显示文本的工具类
 */
public class HighLightUtils {

    /**
     * 把text文本全部高亮显示
     *
     * @param text
     * @param color
     * @return
     */
    public static SpannableStringBuilder getHighLightString(String text, int color) {
        SpannableStringBuilder ssb = null;
        if (!TextUtils.isEmpty(text)) {
            ssb = getPartHighLightString(text, 0, text.length(), color);
        }
        return ssb;
    }

    /**
     * 把text文本的连续部分高亮显示
     *
     * @param text
     * @param start
     * @param end
     * @param color
     * @return
     */
    public static SpannableStringBuilder getPartHighLightString(String text, int start, int end, int color) {
        SpannableStringBuilder ssb = null;
        if (!TextUtils.isEmpty(text)) {
            if (start >= 0 && end <= text.length()) {
                try {
                    ssb = new SpannableStringBuilder(text);
                    ssb.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ssb;
    }


    /**
     * 把text文本按照highlightPositionList集合给的位置高亮显示
     *
     * @param text
     * @param highlightPositionList 需要高亮显示字符的位置集合
     * @param color
     * @return
     */
    public static SpannableStringBuilder getHashHighLightString(String text, List<Integer> highlightPositionList, int color) {
        SpannableStringBuilder ssb = null;
        if (!TextUtils.isEmpty(text)) {
            if (Collections.max(highlightPositionList) < text.length()) {
                try {
                    ssb = new SpannableStringBuilder(text);
                    for (Integer i : highlightPositionList) {
                        // 必须重复创建新的ForegroundColorSpan，否则后面的就会覆盖前面的了
                        ssb.setSpan(new ForegroundColorSpan(color), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ssb;
    }
}
