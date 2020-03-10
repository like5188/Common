package com.like.common.view.dragview.view.photo

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.like.common.util.GlideUtils
import com.like.common.util.onPreDrawListener
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView
import com.like.common.view.dragview.view.util.ViewFactory
import com.like.common.view.dragview.view.util.postDelayed
import kotlin.math.abs

/**
 * 封装了缩略图、进度条、PhotoView
 */
class CustomPhotoView(context: Context, info: DragInfo, enterAnimation: Boolean = true) : BaseDragView(context, info) {
    private val mGlideUtils: GlideUtils by lazy { GlideUtils(context) }
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
        mGlideUtils.hasCached(originUrl, hasCached = { url, isCached ->
            if (isCached) {// 如果已经缓存，就不显示缩略图了，直接显示原图
                showOriginImage(originUrl)
            } else {
                showThumbnail(thumbnailUrl)
                showOriginImage(originUrl)
            }
        })
    }

    private fun showThumbnail(url: String) {
        if (url.isEmpty()) {
            return
        }
        mViewFactory.addThumbnailImageView()
        mViewFactory.addProgressBar()
        mGlideUtils.display(url, mViewFactory.mThumbnailImageView)
    }

    private fun showOriginImage(url: String) {
        if (url.isEmpty()) {
            mViewFactory.removeProgressBar()
            Toast.makeText(context, "图片地址为空！", Toast.LENGTH_SHORT).show()
            return
        }
        mViewFactory.addPhotoView()
        mGlideUtils.display(url, mViewFactory.mPhotoView, object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                mViewFactory.removeProgressBar()
                mViewFactory.removePhotoView()
                Toast.makeText(context, "获取图片失败！", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                postDelayed(100) {
                    // 防闪烁
                    mViewFactory.removeProgressBar()
                    mViewFactory.removeThumbnailImageView()
                }
                return false
            }
        })
    }

    override fun handleMoveEvent(event: MotionEvent, dx: Float, dy: Float): Boolean {
        return abs(dy) > abs(dx)
    }

    override fun onDestroy() {
    }

}