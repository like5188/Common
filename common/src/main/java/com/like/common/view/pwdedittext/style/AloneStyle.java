package com.like.common.view.pwdedittext.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.like.common.view.pwdedittext.textshap.CircleShape;
import com.like.common.view.pwdedittext.textshap.ITextShape;

/**
 * 密码框样子为多个独立的框，即画多个框
 */
public class AloneStyle implements IStyle {
    /**
     * 每个密码框间隔，单位px
     */
    private int mPwdRectSpacing;
    /**
     * 各种形状的掩码接口
     */
    private ITextShape mTextShap;

    /**
     * @param pwdRectSpacing 每个密码框间隔,单位px，默认为5px
     * @param textShape       密码的形状{@link CircleShape}
     */
    public AloneStyle(int pwdRectSpacing, ITextShape textShape) {
        mPwdRectSpacing = pwdRectSpacing;
        mTextShap = textShape;
    }

    @Override
    public void onDraw(Canvas canvas, int width, int height, int pwdLength, String text, Paint textPaint, Paint rectPaint) {
        // 计算每个密码宽度
        int rectWidth = (width - mPwdRectSpacing * (pwdLength - 1)) / pwdLength;
        // 绘制几个独立的密码框
        for (int i = 0; i < pwdLength; i++) {
            int left = (rectWidth + mPwdRectSpacing) * i;
            int top = 2;
            int right = left + rectWidth;
            int bottom = height - top;
            Rect rect = new Rect(left, top, right, bottom);
            canvas.drawRect(rect, rectPaint);
        }
        // 绘制密码
        if (mTextShap != null)
            mTextShap.onDraw(canvas, width, height, pwdLength, text, textPaint);
    }
}
