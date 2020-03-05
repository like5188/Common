package com.like.common.view.dragview.view.video

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import android.widget.Toast
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.like.common.util.GlideUtils
import com.like.common.view.dragview.view.util.HttpProxyCacheServerFactory
import com.like.common.view.dragview.view.util.ViewFactory
import com.like.common.view.dragview.view.util.delay1000Millis
import com.like.common.view.dragview.view.util.delay100Millis


/**
 * 封装了缩略图、进度条、VideoView
 */
class CustomVideoView(context: Context) : FrameLayout(context) {
    private val mGlideUtils: GlideUtils by lazy { GlideUtils(context) }
    private val mViewFactory: ViewFactory by lazy {
        ViewFactory(this).apply {
            mVideoView.setOnPreparedListener {
                mVideoView.start()
                delay100Millis {
                    // 防闪烁
                    removeProgressBar()
                    removeThumbnailImageView()
                }
            }
            mVideoView.setOnCompletionListener {
                mVideoView.resume()// 重新播放
            }
            mVideoView.setOnErrorListener { _, _, _ ->
                delay1000Millis {
                    removeProgressBar()
                    removeVideoView()
                    Toast.makeText(context, "解析视频数据失败！", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }
    }

    fun play(videoUrl: String, thumbImageUrl: String = "") {
        if (thumbImageUrl.isNotEmpty()) {
            mViewFactory.addThumbnailImageView()
            mViewFactory.addProgressBar()
            mGlideUtils.display(thumbImageUrl, mViewFactory.mThumbnailImageView, object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    delay1000Millis {
                        mViewFactory.removeProgressBar()
                        mViewFactory.removeThumbnailImageView()
                        Toast.makeText(context, "获取视频缩略图失败！", Toast.LENGTH_SHORT).show()
                    }
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    playVideo(videoUrl)
                    return false
                }
            })
        } else {
            mViewFactory.addProgressBar()
            playVideo(videoUrl)
        }
    }

    private fun playVideo(videoUrl: String) {
        if (videoUrl.isEmpty()) {
            delay1000Millis {
                mViewFactory.removeProgressBar()
                Toast.makeText(context, "视频地址为空！", Toast.LENGTH_SHORT).show()
            }
            return
        }
        val proxy = HttpProxyCacheServerFactory.getProxy(context)
        proxy?.getProxyUrl(videoUrl)?.let {
            mViewFactory.mVideoView.setVideoPath(it)
            mViewFactory.addVideo()
        }
    }

    fun stop() {
        mViewFactory.mVideoView.stopPlayback()
    }

}