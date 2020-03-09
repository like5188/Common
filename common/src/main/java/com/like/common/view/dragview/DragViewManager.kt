package com.like.common.view.dragview

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.DragPhotoViewActivity
import com.like.common.view.dragview.view.DragVideoViewActivity
import java.util.*

/**
 * 预览图片、视频，并可以拖动
 *
 * view.getLocationInWindow(location);// 获取在当前窗口内的绝对坐标
 * view.getLocationOnScreen(location);// 获取在整个屏幕内的绝对坐标(包括了通知栏)
 * location [0]--->x坐标,location [1]--->y坐标
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
            val location = IntArray(2)
            it.dragView.getLocationOnScreen(location)
            list.add(
                    DragInfo(location[0].toFloat(),
                            location[1].toFloat(),
                            it.dragView.width.toFloat(),
                            it.dragView.height.toFloat(),
                            thumbImageUrl = it.thumbImageUrl,
                            imageUrl = it.url)
            )
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
        val location = IntArray(2)
        data.dragView.getLocationOnScreen(location)
        val intent = Intent(activity, DragVideoViewActivity::class.java)
        intent.putExtra(DragVideoViewActivity.KEY_DATA_FOR_PREVIEW_VIDEO,
                DragInfo(originLeft = location[0].toFloat(),
                        originTop = location[1].toFloat(),
                        originWidth = data.dragView.width.toFloat(),
                        originHeight = data.dragView.height.toFloat(),
                        thumbImageUrl = data.thumbImageUrl,
                        videoUrl = data.url)
        )
        activity.startActivity(intent)
        activity.overridePendingTransition(0, 0)
    }

    /**
     * @param dragView          原始的显示缩略图的ImageView
     * @param thumbImageUrl     缩略图的url
     * @param url               原图或者视频的url
     */
    data class DragInfoTemp(val dragView: ImageView, val thumbImageUrl: String, val url: String)
}