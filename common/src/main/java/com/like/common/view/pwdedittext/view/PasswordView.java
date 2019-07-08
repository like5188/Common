package com.like.common.view.pwdedittext.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.like.common.view.pwdedittext.style.IStyle;

/**
 * 密码输入框
 */
public class PasswordView extends View {
    /**
     * 密码文本画笔
     */
    private Paint mTextPaint;
    /**
     * 密码框画笔
     */
    private Paint mRectPaint;
    /**
     * 密码长度
     */
    private int mPwdLength;
    /**
     * 当前已经输入的文本内容
     */
    private String mText;
    /**
     * 密码框样式
     */
    private IStyle mStyle;
    /**
     * 密码文本的高度
     */
    private int mTextHeight;
    /**
     * 文本距离密码框的顶部间隙
     */
    private int mTextPaddingTop;
    /**
     * 文本距离密码框的底部间隙
     */
    private int mTextPaddingBottom;

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化密码画笔
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        // 初始化密码框画笔
        mRectPaint = new Paint();
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setAntiAlias(true);
    }

    /**
     * 设置密码框样式
     *
     * @param style
     * @return
     */
    public PasswordView setStyle(IStyle style) {
        mStyle = style;
        return this;
    }

    /**
     * 设置密码文本的尺寸，单位px
     *
     * @param fontSize
     * @return
     */
    public PasswordView setFontSize(int fontSize) {
        mTextPaint.setTextSize(fontSize);
        mTextHeight = (int) mTextPaint.getTextSize();
        return this;
    }

    /**
     * 设置密码文本的颜色
     *
     * @param fontColor
     * @return
     */
    public PasswordView setFontColor(int fontColor) {
        mTextPaint.setColor(fontColor);
        return this;
    }

    /**
     * 设置边框颜色
     *
     * @param color
     * @return
     */
    public PasswordView setRectColor(int color) {
        mRectPaint.setColor(color);
        return this;
    }

    /**
     * 设置密码长度
     *
     * @param length
     * @return
     */
    public PasswordView setPwdLength(int length) {
        mPwdLength = length;
        return this;
    }

    /**
     * 设置文本距离密码框的顶部间隙，单位px
     *
     * @param paddingTop
     * @return
     */
    public PasswordView setTextPaddingTop(int paddingTop) {
        mTextPaddingTop = paddingTop;
        return this;
    }

    /**
     * 设置文本距离密码框的底部间隙，单位px
     *
     * @param paddingBottom
     * @return
     */
    public PasswordView setTextPaddingBottom(int paddingBottom) {
        mTextPaddingBottom = paddingBottom;
        return this;
    }

    /**
     * 设置文本
     *
     * @param text
     */
    public void setText(String text) {
        mText = text;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画密码框和密码
        mStyle.onDraw(canvas, getWidth(), getHeight(), mPwdLength, mText, mTextPaint, mRectPaint);
    }

    /**
     * @see View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, measureHeight(heightMeasureSpec));
    }

    /**
     * Determines the height of this dragPhotoView
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the dragPhotoView, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            result = mTextPaddingTop + mTextPaddingBottom + mTextHeight;
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

}

