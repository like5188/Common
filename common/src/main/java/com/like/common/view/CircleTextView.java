package com.like.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;

/**
 * 圆形TextView
 */
public class CircleTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint mBgPaint = new Paint();

    PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    public CircleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mBgPaint.setColor(Color.RED);
        mBgPaint.setAntiAlias(true);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int max = Math.max(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(max, max);
    }

    @Override
    public void setBackgroundColor(int color) {
        mBgPaint.setColor(color);
    }

    /**
     * 设置通知个数显示
     *
     * @param number
     */
    public void setTextNumber(int number) {
        if (number <= 99) {
            String text = number + "";
            setText(text);
        } else {
            // 变成小圆点
            String text = "";
            setTextSize(5f);
            setText(text);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.setDrawFilter(pfd);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, Math.max(getWidth(), getHeight()) / 2, mBgPaint);
        super.draw(canvas);
    }
}
