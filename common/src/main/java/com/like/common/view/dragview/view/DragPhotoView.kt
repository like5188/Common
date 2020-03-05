package com.like.common.view.dragview.view

import android.content.Context
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.like.common.util.onGlobalLayoutListener
import com.like.common.view.dragview.entity.DragInfo

class DragPhotoView(context: Context, dragInfos: List<DragInfo>, selectedPosition: Int) : BaseDragView(context, dragInfos[selectedPosition]) {
    private val mPhotoViews = mutableListOf<CustomPhotoView>()
    private var isFirstMove = true
    private var mDownX = 0f
    private var mDownY = 0f

    init {
        dragInfos.forEach {
            mPhotoViews.add(CustomPhotoView(context))
        }

        fun select(position: Int) {
            val selectedInfo = dragInfos[position]
            setData(selectedInfo)
            mPhotoViews[position].show(selectedInfo.imageUrl, selectedInfo.thumbImageUrl)
        }

        addView(CustomPhotoViewPager(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            adapter = CustomPhotoViewPagerAdapter(mPhotoViews)
            addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    select(position)
                }
            })
            currentItem = selectedPosition
        })

        onGlobalLayoutListener {
            select(selectedPosition)
            enter()
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        // 当scale == 1时才能drag
        if (scaleX == 1f && scaleY == 1f) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    mDownX = event.x
                    mDownY = event.y
                    isFirstMove = true
                }
                MotionEvent.ACTION_MOVE -> {
                    // ViewPager的事件
                    if (isFirstMove && event.y - mDownY <= 0 && getCanvasTranslationY() == 0f && getCanvasTranslationX() != 0f) {
                        return super.dispatchTouchEvent(event)
                    }

                    // 单手指按下
                    if (event.pointerCount == 1) {
                        setCanvasTranslationX(event.x - mDownX)
                        val transitionY = if (isFirstMove && event.y - mDownY <= 0) {
                            0f
                        } else {
                            isFirstMove = false
                            event.y - mDownY
                        }
                        setCanvasTranslationY(transitionY)
                        setCanvasScale(calcCanvasScaleByCanvasTranslationY(transitionY))
                        setCanvasBackgroundAlpha(calcCanvasBackgroundAlphaByCanvasTranslationY(transitionY))
                        return true
                    }

                    // 防止下拉的时候双手缩放
                    if (getCanvasTranslationY() >= 0f && getCanvasScale() < 0.95f) {
                        return true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    isFirstMove = true
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onDestroy() {
        mPhotoViews.clear()
    }

}
