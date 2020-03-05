package com.like.common.view.dragview.view.video

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.widget.FrameLayout
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.like.common.util.GlideUtils
import com.like.common.view.dragview.view.util.HttpProxyCacheServerFactory
import com.like.common.view.dragview.view.util.ViewHelper
import com.like.common.view.dragview.view.util.delay1000Millis


/**
 * 封装了缩略图、进度条、VideoView
 */
class CustomVideoView(context: Context) : FrameLayout(context) {
    private val mGlideUtils: GlideUtils by lazy { GlideUtils(context) }
    private val mViewHelper: ViewHelper by lazy { ViewHelper(this) }

    fun play(videoUrl: String, thumbImageUrl: String = "") {
        if (thumbImageUrl.isNotEmpty()) {
            mViewHelper.addThumbnailImageView()
            mViewHelper.addProgressBar()

            mViewHelper.mThumbnailImageView.setImageBitmap(getThumbnail(videoUrl))
            playVideo(videoUrl)

//            mGlideUtils.display(thumbImageUrl, mViewHelper.mThumbnailImageView, object : RequestListener<Drawable> {
//                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
//                    delay1000Millis {
//                        mViewHelper.removeProgressBar()
//                        mViewHelper.removeThumbnailImageView()
//                        Toast.makeText(context, "获取视频缩略图失败！", Toast.LENGTH_SHORT).show()
//                    }
//                    return false
//                }
//
//                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                    playVideo(videoUrl)
//                    return false
//                }
//            })
        } else {
            mViewHelper.addProgressBar()
            playVideo(videoUrl)
        }
    }

    private fun playVideo(videoUrl: String) {
        if (videoUrl.isEmpty()) {
            delay1000Millis {
                mViewHelper.removeProgressBar()
                Toast.makeText(context, "视频地址为空！", Toast.LENGTH_SHORT).show()
            }
            return
        }
        val proxy = HttpProxyCacheServerFactory.getProxy(context)
        proxy?.getProxyUrl(videoUrl)?.let {
            mViewHelper.mVideoView.setVideoPath(it)
            mViewHelper.addVideo()
        }
    }

    fun stop() {
        mViewHelper.mVideoView.stopPlayback()
    }

    /**
     * 根据视频网络地址获取第一帧图片
     */
    fun getThumbnail(videoUrl: String): Bitmap? {
        var bitmap: Bitmap? = null
        val retriever = MediaMetadataRetriever()
        try {
            // 根据网络视频的url获取第一帧--亲测可用。但是这个方法获取本地视频的第一帧，不可用，还没找到方法解决。
            retriever.setDataSource(videoUrl, HashMap())
            // 获得第一帧图片
            bitmap = retriever.frameAtTime
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }
        return bitmap
    }

}