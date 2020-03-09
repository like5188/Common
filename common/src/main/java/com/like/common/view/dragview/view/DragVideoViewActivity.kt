package com.like.common.view.dragview.view

import android.os.Bundle
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.video.CustomVideoView

class DragVideoViewActivity : BaseDragViewActivity() {
    companion object {
        const val KEY_DATA_FOR_PREVIEW_VIDEO = "key_data_for_preview_video"
    }

    private var mBaseDragView: BaseDragView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(KEY_DATA_FOR_PREVIEW_VIDEO)) {
            val info: DragInfo? = intent.getParcelableExtra(KEY_DATA_FOR_PREVIEW_VIDEO)
            info?.let {
                CustomVideoView(this, it).apply {
                    mBaseDragView = this
                    setContentView(this)
                }
            }
        }
    }

    override fun onBackPressed() {
        mBaseDragView?.exit()
    }
}