package com.like.common.view.dragview.view.photo

import android.content.Context
import android.view.MotionEvent
import android.widget.Toast
import coil.Coil
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.like.common.util.onPreDrawListener
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView
import com.like.common.view.dragview.view.util.ViewFactory
import kotlin.math.abs

/**
 * 封装了缩略图、进度条、PhotoView
 */
class CustomPhotoView(context: Context, info: DragInfo, enterAnimation: Boolean = true) : BaseDragView(context, info) {
    private val mViewFactory: ViewFactory by lazy {
        ViewFactory(this)
    }

    init {
        onPreDrawListener {
            show(info.imageUrl, info.thumbnailUrl)
            if (enterAnimation) {
                enterAnimation()
            }
        }
    }

    private fun show(originUrl: String, thumbnailUrl: String) {
        val request = ImageRequest.Builder(context.applicationContext)
                .data(originUrl)
                .networkCachePolicy(CachePolicy.DISABLED)
                .target(
                        onStart = {
                        },
                        onError = {
                            showThumbnail(thumbnailUrl)
                            showOriginImage(originUrl)
                        },
                        onSuccess = {// 如果已经缓存，就不显示缩略图了，直接显示原图
                            showOriginImage(originUrl)
                        })
                .build()
        Coil.imageLoader(context.applicationContext).enqueue(request)
    }

    private fun showThumbnail(url: String) {
        if (url.isEmpty()) {
            return
        }
        mViewFactory.addThumbnailImageView()
        mViewFactory.addProgressBar()
        mViewFactory.mThumbnailImageView.load(url)
    }

    private fun showOriginImage(url: String) {
        if (url.isEmpty()) {
            mViewFactory.removeProgressBar()
            Toast.makeText(context, "图片地址为空！", Toast.LENGTH_SHORT).show()
            return
        }
        val request = ImageRequest.Builder(context.applicationContext)
                .data(url)
                .target(
                        onStart = {
                            mViewFactory.addPhotoView()
                        },
                        onError = {
                            mViewFactory.removeProgressBar()
                            mViewFactory.removePhotoView()
                            Toast.makeText(context, "获取图片失败！", Toast.LENGTH_SHORT).show()
                        },
                        onSuccess = {
                            mViewFactory.mPhotoView.setImageDrawable(it)
                            postDelayed({
                                // 防闪烁
                                mViewFactory.removeProgressBar()
                                mViewFactory.removeThumbnailImageView()
                            }, 100)
                        })
                .build()
        Coil.imageLoader(context.applicationContext).enqueue(request)
    }

    override fun handleMoveEvent(event: MotionEvent, dx: Float, dy: Float): Boolean {
        return abs(dy) > abs(dx)
    }

    override fun onDestroy() {
    }

}