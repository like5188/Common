package com.like.common.view.pwdedittext.style;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * 密码框样式接口
 */
public interface IStyle {
    /**
     * 绘制密码框及其文本掩码
     *
     * @param canvas    画布
     * @param width     密码框的宽度
     * @param height    密码框的高度
     * @param pwdLength 密码长度
     * @param text      当前已经输入的文本内容
     * @param textPaint 画密码的画笔
     * @param rectPaint 画密码框的画笔
     */
    void onDraw(Canvas canvas, int width, int height, int pwdLength, String text, Paint textPaint, Paint rectPaint);

}
