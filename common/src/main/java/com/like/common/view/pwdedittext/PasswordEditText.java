package com.like.common.view.pwdedittext;

import android.content.Context;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.like.common.R;
import com.like.common.util.DimensionUtils;
import com.like.common.util.SoftKeyboardUtils;
import com.like.common.view.pwdedittext.style.IStyle;
import com.like.common.view.pwdedittext.style.WholeStyle;
import com.like.common.view.pwdedittext.textshap.CircleShape;
import com.like.common.view.pwdedittext.view.PasswordView;

/**
 * 密码输入框
 */
public class PasswordEditText extends FrameLayout {
    private PasswordView mPasswordView;
    private EditText mEditText;
    /**
     * 密码长度
     */
    private int mPwdLength = 6;
    /**
     * 输入结束监听
     */
    private OnInputFinishListener mOnInputFinishListener;

    public PasswordEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_password_edittext, this, true);
        mEditText = (EditText) findViewById(R.id.et_password);
        mPasswordView = (PasswordView) findViewById(R.id.passwordView);
        initEditText();
        initPasswordView();
        setOnClickListener(v -> {
            // 显示软键盘
            SoftKeyboardUtils.INSTANCE.show(mEditText);
        });
    }

    private void initEditText() {
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPasswordView.setText(s.toString());
                if (s.toString().length() == mPwdLength && mOnInputFinishListener != null) {
                    new Thread(() -> {
                        SystemClock.sleep(200);// 延迟一下，避免最后一个字符没有画完，影响体验
                        mOnInputFinishListener.onInputFinish(s.toString());
                    }).start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        InputFilter[] filters = new InputFilter[]{new InputFilter.LengthFilter(mPwdLength)};
        mEditText.setFilters(filters);
    }

    /**
     * 设置PasswordView
     */
    private void initPasswordView() {
        mPasswordView.setStyle(new WholeStyle(true, DimensionUtils.dp2px(getContext(), 3f), new CircleShape()))
                .setFontSize(DimensionUtils.sp2px(getContext(), 10f))
                .setFontColor(Color.BLACK)
                .setRectColor(Color.LTGRAY)
                .setPwdLength(mPwdLength)
                .setTextPaddingTop(DimensionUtils.dp2px(getContext(), 15f))
                .setTextPaddingBottom(DimensionUtils.dp2px(getContext(), 15f));
    }

    /**
     * 设置密码框样式，默认为{@link WholeStyle}
     *
     * @param style
     */
    public void setStyle(IStyle style) {
        mPasswordView.setStyle(style);
    }

    /**
     * 设置密码文本的尺寸，默认为10sp
     *
     * @param fontSize
     */
    public void setFontSize(int fontSize) {
        mPasswordView.setFontSize(fontSize);
    }

    /**
     * 设置密码掩盖圆点的颜色，默认为Color.BLACK
     *
     * @param fontColor
     */
    public void setFontColor(int fontColor) {
        mPasswordView.setFontColor(fontColor);
    }

    /**
     * 设置边框颜色，默认为Color.LTGRAY
     */
    public void setRectColor(int color) {
        mPasswordView.setRectColor(color);
    }

    /**
     * 设置密码长度，默认为6
     *
     * @param length
     */
    public void setPwdLength(int length) {
        mPwdLength = length;
        InputFilter[] filters = new InputFilter[]{new InputFilter.LengthFilter(length)};
        mEditText.setFilters(filters);
        mPasswordView.setPwdLength(length);
    }

    /**
     * 设置文本距离密码框的顶部间隙，默认为15dp
     *
     * @param paddingTop 单位dp
     */
    public void setTextPaddingTop(int paddingTop) {
        mPasswordView.setTextPaddingTop(DimensionUtils.dp2px(getContext(), paddingTop));
    }

    /**
     * 设置文本距离密码框的底部间隙，默认为15dp
     *
     * @param paddingBottom 单位dp
     */
    public void setTextPaddingBottom(int paddingBottom) {
        mPasswordView.setTextPaddingBottom(DimensionUtils.dp2px(getContext(), paddingBottom));
    }

    public interface OnInputFinishListener {
        /**
         * 密码输入结束监
         *
         * @param password
         */
        void onInputFinish(String password);
    }

    /**
     * 设置输入完成监听
     *
     * @param onInputFinishListener
     */
    public void addOnInputFinishListener(OnInputFinishListener onInputFinishListener) {
        this.mOnInputFinishListener = onInputFinishListener;
    }

}
