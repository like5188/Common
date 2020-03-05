package com.like.common.view.dragview.view.photo

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.like.common.util.GlideUtils
import com.like.common.view.dragview.view.util.ViewHelper
import com.like.common.view.dragview.view.util.delay1000Millis
import com.like.common.view.dragview.view.util.delay100Millis

class CustomPhotoView(context: Context) : FrameLayout(context) {
    private val TAG = CustomPhotoView::class.java.simpleName
    private val mGlideUtils: GlideUtils by lazy { GlideUtils(context) }
    private val mViewHelper: ViewHelper by lazy {
        ViewHelper(this)
    }

    fun show(imageUrl: String, thumbImageUrl: String = "") {
        mGlideUtils.hasCached(imageUrl, hasCached = { url, isCached ->
            if (isCached) {// 如果有原图缓存，就直接显示原图，不显示缩略图了。
                mViewHelper.removeProgressBar()
                mViewHelper.removeThumbnailImageView()
                mViewHelper.addPhotoView()
                mGlideUtils.display(imageUrl, mViewHelper.mPhotoView)
                Log.v(TAG, "从缓存中获取了图片：${imageUrl}")
            } else {// 如果没有原图缓存
                mViewHelper.addProgressBar()
                if (thumbImageUrl.isNotEmpty()) {// 如果有缩略图
                    mViewHelper.addThumbnailImageView()
                    mGlideUtils.display(thumbImageUrl, mViewHelper.mThumbnailImageView, object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            delay1000Millis {
                                mViewHelper.removeProgressBar()
                                mViewHelper.removeThumbnailImageView()
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
            delay1000Millis {
                mViewHelper.removeProgressBar()
                Toast.makeText(context, "图片地址为空！", Toast.LENGTH_SHORT).show()
            }
            return
        }
        delay1000Millis {
            mViewHelper.addPhotoView()
            mGlideUtils.display(imageUrl, mViewHelper.mPhotoView, object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    mViewHelper.removeProgressBar()
                    mViewHelper.removePhotoView()
                    Toast.makeText(context, "获取图片失败！", Toast.LENGTH_SHORT).show()
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    delay100Millis {
                        // 防闪烁
                        mViewHelper.removeProgressBar()
                        mViewHelper.removeThumbnailImageView()
                    }
                    Log.v(TAG, "从网络获取了图片：${imageUrl}")
                    return false
                }
            })
        }
    }

}