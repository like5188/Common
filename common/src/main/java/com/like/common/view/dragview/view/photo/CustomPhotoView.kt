package com.like.common.view.dragview.view.photo

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.like.common.util.GlideUtils
import com.like.common.util.onGlobalLayoutListener
import com.like.common.view.dragview.entity.DragInfo
import com.like.common.view.dragview.view.BaseDragView
import com.like.common.view.dragview.view.util.ViewFactory
import com.like.common.view.dragview.view.util.postDelayed
import kotlin.math.abs


class CustomPhotoView(context: Context, info: DragInfo) : BaseDragView(context, info) {
    private val TAG = CustomPhotoView::class.java.simpleName
    private val mGlideUtils: GlideUtils by lazy { GlideUtils(context) }
    private val mViewFactory: ViewFactory by lazy {
        ViewFactory(this)
    }

    init {
        onGlobalLayoutListener {
            setData(info)
            show(info.imageUrl, info.thumbImageUrl)
            enter()
        }
    }

    fun show(imageUrl: String, thumbImageUrl: String = "") {
        mGlideUtils.hasCached(imageUrl, hasCached = { url, isCached ->
            if (isCached) {// 如果有原图缓存，就直接显示原图，不显示缩略图了。
                mViewFactory.removeProgressBar()
                mViewFactory.removeThumbnailImageView()
                mViewFactory.addPhotoView()
                mGlideUtils.display(imageUrl, mViewFactory.mPhotoView)
                Log.v(TAG, "从缓存中获取了图片：${imageUrl}")
            } else {// 如果没有原图缓存
                mViewFactory.addProgressBar()
                if (thumbImageUrl.isNotEmpty()) {// 如果有缩略图
                    mViewFactory.addThumbnailImageView()
                    mGlideUtils.display(thumbImageUrl, mViewFactory.mThumbnailImageView, object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            postDelayed(1000) {
                                mViewFactory.removeProgressBar()
                                mViewFactory.removeThumbnailImageView()
                                Toast.makeText(context, "获取缩略图失败！", Toast.LENGTH_SHORT).show()
                            }
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            showNetworkImage(imageUrl)
                            return false
                        }
                    })
                } else {// 如果没有缩略图
                    showNetworkImage(imageUrl)
                }
            }
        })
    }

    private fun showNetworkImage(imageUrl: String) {
        if (imageUrl.isEmpty()) {
            postDelayed(1000) {
                mViewFactory.removeProgressBar()
                Toast.makeText(context, "图片地址为空！", Toast.LENGTH_SHORT).show()
            }
            return
        }
        postDelayed(1000) {
            mViewFactory.addPhotoView()
            mGlideUtils.display(imageUrl, mViewFactory.mPhotoView, object : RequestListener<Drawable> {
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
                    Log.v(TAG, "从网络获取了图片：${imageUrl}")
                    return false
                }
            })
        }
    }

    override fun handleMoveEvent(event: MotionEvent, dx: Float, dy: Float): Boolean {
        return abs(dy) > abs(dx)
    }

    override fun onDestroy() {
    }

}