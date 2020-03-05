package com.like.common.view.dragview.view

import android.R
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView
import com.like.common.util.GlideUtils

class CustomPhotoView(context: Context) : FrameLayout(context) {
    private val TAG = CustomPhotoView::class.java.simpleName
    private val mGlideUtils: GlideUtils by lazy { GlideUtils(context) }
    private val mThumbnailImageView: ImageView by lazy {
        ImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                gravity = Gravity.CENTER
            }
        }
    }
    private val mProgressBar: ProgressBar by lazy {
        ProgressBar(context, null, R.attr.progressBarStyleInverse).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
        }
    }
    private val mPhotoView: PhotoView by lazy {
        PhotoView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                gravity = Gravity.CENTER
            }
        }
    }

    fun show(imageUrl: String, thumbImageUrl: String = "") {
        mGlideUtils.hasCached(imageUrl, hasCached = { url, isCached ->
            if (isCached) {// 如果有原图缓存，就直接显示原图，不显示缩略图了。
                removeProgressBar()
                removeThumbnailImageView()
                addPhotoView()
                mGlideUtils.display(imageUrl, mPhotoView)
                Log.v(TAG, "从缓存中获取了图片：${imageUrl}")
            } else {// 如果没有原图缓存
                if (thumbImageUrl.isNotEmpty()) {
                    addThumbnailImageView()
                    addProgressBar()
                    mGlideUtils.display(thumbImageUrl, mThumbnailImageView, object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            delay1000Millis {
                                removeProgressBar()
                                removeThumbnailImageView()
                                Toast.makeText(context, "获取缩略图失败！", Toast.LENGTH_SHORT).show()
                            }
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            showOriginImage(imageUrl)
                            return false
                        }
                    })
                } else {
                    addProgressBar()
                    showOriginImage(imageUrl)
                }
            }
        })
    }

    private fun showOriginImage(imageUrl: String) {
        if (imageUrl.isEmpty()) {
            delay1000Millis {
                removeProgressBar()
                Toast.makeText(context, "图片地址为空！", Toast.LENGTH_SHORT).show()
            }
            return
        }
        delay1000Millis {
            addPhotoView()
            mGlideUtils.display(imageUrl, mPhotoView, object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    removeProgressBar()
                    removePhotoView()
                    Toast.makeText(context, "获取图片失败！", Toast.LENGTH_SHORT).show()
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    delay100Millis {
                        // 防闪烁
                        removeProgressBar()
                        removeThumbnailImageView()
                    }
                    Log.v(TAG, "从网络获取了图片：${imageUrl}")
                    return false
                }
            })
        }
    }

    private fun containsThumbnailImageView() = (0 until childCount).any { getChildAt(it) == mThumbnailImageView }

    private fun containsProgressBar() = (0 until childCount).any { getChildAt(it) == mProgressBar }

    private fun containsPhotoView() = (0 until childCount).any { getChildAt(it) == mPhotoView }

    private fun addThumbnailImageView() {
        if (!containsThumbnailImageView()) {
            addView(mThumbnailImageView)
        }
    }

    private fun addProgressBar() {
        if (!containsProgressBar()) {
            addView(mProgressBar)
        }
    }

    private fun addPhotoView() {
        if (!containsPhotoView()) {
            addView(mPhotoView)
        }
    }

    fun removeThumbnailImageView() {
        removeView(mThumbnailImageView)
    }

    fun removeProgressBar() {
        removeView(mProgressBar)
    }

    fun removePhotoView() {
        removeView(mPhotoView)
    }

    private fun delay1000Millis(action: () -> Unit) {
        postDelayed(action, 1000)
    }

    private fun delay100Millis(action: () -> Unit) {
        postDelayed(action, 100)
    }
}