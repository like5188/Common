package com.like.common.util

import android.graphics.Color
import androidx.annotation.ColorInt
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView


/**
 * 一个TextView空间内设置不同颜色的文字，并使得该部分的文字有着单独的点击事件
 */
object ClickTextViewSpanUtils {

    fun setSpan(textView: TextView, content: String, clickTextViewModelList: List<ClickTextViewModel>, maxLines: Int = 0) {
        if (clickTextViewModelList.isEmpty()) {
            textView.text = content
            return
        }

        textView.text = content
        textView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                textView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val realText = if (maxLines != 0 && textView.lineCount > maxLines) {
                    val lineEndIndex = textView.layout.getLineEnd(maxLines - 1)// 设置第maxLines行打省略号
                    "${textView.text.subSequence(0, lineEndIndex - 1)}..."
                } else {
                    content
                }

                val ss = SpannableString(realText)
                clickTextViewModelList.forEach {
                    if (it.start < realText.length) {
                        val start = it.start
                        val end = if (it.end > realText.length) realText.length else it.end
                        ss.setSpan(ClickTextViewSpan(it.color, it.clickListener), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                textView.text = ss

                textView.movementMethod = LinkMovementMethod.getInstance() // 不设置就没有点击事件
                textView.highlightColor = Color.TRANSPARENT // 设置点击后的颜色为透明
                textView.setOnTouchListener(LinkMovementMethodOverride())// 固定 TextView 行数的时候，点击 ClickableSpan 文本会出现滚动现象
            }
        })
    }

    data class ClickTextViewModel(val start: Int, val end: Int, @ColorInt val color: Int, val clickListener: (() -> Unit)? = null)

    class LinkMovementMethodOverride : View.OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val widget = v as TextView
            val text = widget.text
            if (text is Spanned) {

                val action = event.action
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                    var x = event.x.toInt()
                    var y = event.y.toInt()

                    x -= widget.totalPaddingLeft
                    y -= widget.totalPaddingTop

                    x += widget.scrollX
                    y += widget.scrollY

                    val layout = widget.layout
                    val line = layout.getLineForVertical(y)
                    val off = layout.getOffsetForHorizontal(line, x.toFloat())

                    val link = text.getSpans(off, off,
                            ClickableSpan::class.java)

                    if (link.isNotEmpty()) {
                        if (action == MotionEvent.ACTION_UP) {
                            link[0].onClick(widget)
                        } else if (action == MotionEvent.ACTION_DOWN) {
                            // Selection only works on Spannable text. In our case setSelection doesn't work on spanned text
                            //Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                        }
                        return true
                    }
                }

            }

            return false
        }

    }

    /**
     * 一个TextView空间内设置不同颜色的文字，并使得该部分的文字有着单独的点击事件
     */
    class ClickTextViewSpan(@ColorInt val textColor: Int, private val clickListener: (() -> Unit)? = null) : ClickableSpan() {
        override fun onClick(widget: View?) {
            clickListener?.invoke()
        }

        override fun updateDrawState(ds: TextPaint?) {
            super.updateDrawState(ds)
            ds?.color = textColor// 设置可以点击文本部分的颜色
            ds?.isUnderlineText = false // 设置该文本部分是否显示超链接形式的下划线
        }
    }

}

