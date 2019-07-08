package com.like.common.view.pwdedittext.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.like.common.view.pwdedittext.textshap.ITextShape;

/**
 * 密码框是一个整体，即画一个外边框和中间的多根分割线
 */
public class WholeStyle implements IStyle {
    /**
     * 是否需要圆角
     */
    private boolean mHasRound;
    /**
     * 圆角半径，单位px
     */
    private int mRoundRadius;
    /**
     * 各种形状的掩码接口
     */
    private ITextShape mTextShape;

    /**
     * @param hasRound    四个角是否为圆角，默认为false
     * @param roundRadius 设置了四个角为圆角时，设置圆角的大小，单位px，默认为3px
     * @param textShape    密码的形状
     */
    public WholeStyle(boolean hasRound, int roundRadius, ITextShape textShape) {
        mHasRound = hasRound;
        mRoundRadius = roundRadius;
        mTextShape = textShape;
    }

    @Override
    public void onDraw(Canvas canvas, int width, int height, int pwdLength, String text, Paint textPaint, Paint rectPaint) {
        // 画圆角密码外框
        float left = 0;
        float top = 0;
        float right = width;
        float bottom = height;
        RectF rect = new RectF(left, top, right, bottom);
        if (mHasRound)
            canvas.drawRoundRect(rect, mRoundRadius, mRoundRadius, rectPaint);
        else
            canvas.drawRect(rect, rectPaint);
        // 画分割线，每条线4个点
        int rectWidth = width / pwdLength;// 计算每个密码宽度
        int length = (pwdLength - 1) * 4;
        float[] points = new float[length];
        for (int i = 0; i < length; i += 4) {
            int j = (i / 4 + 1);
            float x = rectWidth * j;
            points[i] = x;// 起点x坐标
            points[i + 1] = 0;// 起点y坐标
            points[i + 2] = x;// 终点x坐标
            points[i + 3] = height;// 终点y坐标
        }
        canvas.drawLines(points, rectPaint);
        // 绘制密码
        if (mTextShape != null)
            mTextShape.onDraw(canvas, width, height, pwdLength, text, textPaint);
    }
}
