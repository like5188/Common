package com.like.common.view.dragview.activity

import android.os.Bundle
import android.widget.FrameLayout
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.photo.CustomPhotoView
import com.like.common.view.dragview.view.photo.CustomPhotoViewPager
import com.like.common.view.dragview.view.photo.CustomPhotoViewPagerAdapter

class DragPhotoViewActivity : BaseDragViewActivity() {
    companion object {
        const val KEY_CUR_CLICK_POSITION = "key_cur_click_position"
        const val KEY_DATA_FOR_PREVIEW_IMAGE = "key_data_for_preview_image"
    }

    private val mPhotoViews = mutableListOf<CustomPhotoView>()
    private var mSelectedPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(KEY_DATA_FOR_PREVIEW_IMAGE)) {
            val infos: List<DragInfo>? = intent.getParcelableArrayListExtra(KEY_DATA_FOR_PREVIEW_IMAGE)
            infos?.let {
                mSelectedPosition = intent.getIntExtra(KEY_CUR_CLICK_POSITION, 0)
                it.forEach {
                    mPhotoViews.add(CustomPhotoView(this, it))
                }
                val vp = CustomPhotoViewPager(this).apply {
                    layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
                }
                vp.adapter = CustomPhotoViewPagerAdapter(mPhotoViews)
                vp.currentItem = mSelectedPosition
                setContentView(vp)
            }
        }

    }

    override fun onBackPressed() {
        mPhotoViews[mSelectedPosition].exitAnimation()
    }
}