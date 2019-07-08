package com.like.common.view.pwdedittext.textshap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

/**
 * 星形的密码掩码
 */
public class StarShape implements ITextShape {
    @Override
    public void onDraw(Canvas canvas, int width, int height, int pwdLength, String text, Paint textPaint) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        int rectWidth = width / pwdLength;// 计算每个密码宽度

        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int baseline = (height - fontMetrics.bottom - fontMetrics.top) / 2;
        // 下面这行是实现水平居中，drawText方法中的第二个参数改为传入x方向的中心点
        textPaint.setTextAlign(Paint.Align.CENTER);

        for (int i = 0; i < text.length(); i++) {
            int cx = rectWidth / 2 + rectWidth * i;
            canvas.drawText("*", cx, baseline, textPaint);
        }
    }
}
