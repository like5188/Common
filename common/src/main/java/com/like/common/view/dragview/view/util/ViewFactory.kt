package com.like.common.view.dragview.view.util

import android.R
import android.content.Context
import android.view.Gravity
import android.widget.*
import com.github.chrisbanes.photoview.PhotoView

/**
 * 提供[com.like.common.view.dragview.view.photo.CustomPhotoView]和[com.like.common.view.dragview.view.video.CustomVideoView]需要的View
 */
class ViewFactory(private val mParent: FrameLayout) {
    private val mContext: Context = mParent.context
    private val mProgressBar: ProgressBar by lazy {
        ProgressBar(mContext, null, R.attr.progressBarStyleInverse).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT).apply {
                gravity = Gravity.CENTER
            }
        }
    }
    val mThumbnailImageView: ImageView by lazy {
        ImageView(mContext).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
                gravity = Gravity.CENTER
            }
        }
    }
    val mPhotoView: PhotoView by lazy {
        PhotoView(mContext).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
                gravity = Gravity.CENTER
            }
        }
    }
    val mVideoView: VideoView by lazy {
        VideoView(mContext).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT).apply {
                gravity = Gravity.CENTER
            }
            setZOrderOnTop(true)// 避免闪屏
        }
    }

    fun addThumbnailImageView() {
        if (!containsThumbnailImageView()) {
            mParent.addView(mThumbnailImageView)
        }
    }

    fun addProgressBar() {
        if (!containsProgressBar()) {
            mParent.addView(mProgressBar)
        }
    }

    fun addPhotoView() {
        if (!containsPhotoView()) {
            mParent.addView(mPhotoView)
        }
    }

    fun addVideo() {
        if (!containsVideoView()) {
            mParent.addView(mVideoView)
        }
    }

    fun removeThumbnailImageView() {
        mParent.removeView(mThumbnailImageView)
    }

    fun removeProgressBar() {
        mParent.removeView(mProgressBar)
    }

    fun removePhotoView() {
        mParent.removeView(mPhotoView)
    }

    fun removeVideoView() {
        mParent.removeView(mVideoView)
    }

    private fun containsThumbnailImageView() = (0 until mParent.childCount).any { mParent.getChildAt(it) == mThumbnailImageView }

    private fun containsProgressBar() = (0 until mParent.childCount).any { mParent.getChildAt(it) == mProgressBar }

    private fun containsPhotoView() = (0 until mParent.childCount).any { mParent.getChildAt(it) == mPhotoView }

    private fun containsVideoView() = (0 until mParent.childCount).any { mParent.getChildAt(it) == mVideoView }
}