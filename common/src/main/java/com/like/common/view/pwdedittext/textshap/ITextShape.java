package com.like.common.view.pwdedittext.textshap;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 各种形状的密码接口
 */
public interface ITextShape {
    /**
     * 绘制密码文本
     *
     * @param canvas    画布
     * @param width     密码框的宽度
     * @param height    密码框的高度
     * @param pwdLength 密码长度
     * @param text      当前已经输入的文本内容
     * @param textPaint 画密码的画笔
     */
    void onDraw(Canvas canvas, int width, int height, int pwdLength, String text, Paint textPaint);
}
