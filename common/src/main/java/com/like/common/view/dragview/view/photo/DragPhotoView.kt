package com.like.common.view.dragview.view.photo

import android.content.Context
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.like.common.util.onGlobalLayoutListener
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView
import kotlin.math.abs

class DragPhotoView(context: Context, dragInfos: List<DragInfo>, selectedPosition: Int) : BaseDragView(context, dragInfos[selectedPosition]) {
    private var mDownX = 0f
    private var mDownY = 0f
    private var mLastX = 0f
    private var mLastY = 0f
    private val mPhotoViews = mutableListOf<CustomPhotoView>()

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

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        var intercepted = false
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = event.x
                mDownY = event.y
                intercepted = false
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - mLastX
                val dy = event.y - mLastY
                intercepted = if (abs(dx) > abs(dy)) {
                    false
                } else {
                    event.pointerCount == 1 && scaleX == 1f && scaleY == 1f
                }
                mLastX = event.x
                mLastY = event.y
            }
            MotionEvent.ACTION_UP -> {
                intercepted = false
            }
        }
        return intercepted
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_MOVE -> {
                updateProperties(event.x - mDownX, event.y - mDownY)
            }
        }
        return true
    }

    override fun onDestroy() {
        mPhotoViews.clear()
    }

}
