package com.like.common.view.pwdedittext.textshap;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;

/**
 * 圆点形状的密码掩码
 */
public class CircleShape implements ITextShape {
    @Override
    public void onDraw(Canvas canvas, int width, int height, int pwdLength, String text, Paint textPaint) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        int rectWidth = width / pwdLength;// 计算每个密码宽度
        for (int i = 0; i < text.length(); i++) {
            int cx = rectWidth / 2 + rectWidth * i;
            int cy = height / 2;
            canvas.drawCircle(cx, cy, textPaint.getTextSize() / 2, textPaint);
        }
    }
}
