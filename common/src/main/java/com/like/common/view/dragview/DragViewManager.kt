package com.like.common.view.dragview

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.widget.ImageView
import com.like.common.view.dragview.activity.DragPhotoViewActivity
import com.like.common.view.dragview.activity.DragVideoViewActivity
import com.like.common.view.dragview.entity.DragInfo
import java.util.*

/**
 * 预览图片、视频，并可以拖动。仿微信朋友圈效果
 */
object DragViewManager {

    /**
     * 预览图片
     *
     * @param activity
     * @param data
     * @param curClickPosition 当前点击的图片位置
     */
    fun previewImage(activity: Activity, data: List<DragInfoTemp>, curClickPosition: Int) {
        val list = ArrayList<DragInfo>()

        data.forEach {
            val rect = Rect()
            it.originImageView.getGlobalVisibleRect(rect)
            list.add(DragInfo(rect, it.thumbUrl, it.url))
        }

        val intent = Intent(activity, DragPhotoViewActivity::class.java)
        intent.putExtra(DragPhotoViewActivity.KEY_CUR_CLICK_POSITION, curClickPosition)
        intent.putParcelableArrayListExtra(DragPhotoViewActivity.KEY_DATA_FOR_PREVIEW_IMAGE, list)
        activity.startActivity(intent)
        // 去掉默认的切换效果
        activity.overridePendingTransition(0, 0)
    }

    /**
     * 预览视频
     *
     * @param activity
     * @param data
     */
    fun previewVideo(activity: Activity, data: DragInfoTemp) {
        val rect = Rect()
        data.originImageView.getGlobalVisibleRect(rect)
        val intent = Intent(activity, DragVideoViewActivity::class.java)
        intent.putExtra(
            DragVideoViewActivity.KEY_DATA_FOR_PREVIEW_VIDEO,
            DragInfo(rect, data.thumbUrl, data.url)
        )
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
    }

    /**
     * @param originImageView   原始的显示缩略图的 ImageView
     * @param thumbUrl          缩略图的 url
     * @param url               原图或者视频的 url
     */
    data class DragInfoTemp(val originImageView: ImageView, val thumbUrl: String, val url: String)
}