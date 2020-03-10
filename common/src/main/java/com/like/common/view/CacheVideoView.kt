package com.like.common.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import com.danikula.videocache.HttpProxyCacheServer
import com.like.common.util.GlideUtils
import com.like.common.view.dragview.view.util.HttpProxyCacheServerFactory
import com.like.common.view.dragview.view.util.ViewFactory
import com.like.common.view.dragview.view.util.postDelayed

/**
 * 封装了缩略图、进度条、VideoView
 */
class CacheVideoView(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {
    private val mGlideUtils: GlideUtils by lazy { GlideUtils(context) }
    private val mViewFactory: ViewFactory by lazy {
        ViewFactory(this).apply {
            mVideoView.setOnPreparedListener {
                mVideoView.start()
                postDelayed(100) {
                    // 防闪烁
                    removeProgressBar()
                    removeThumbnailImageView()
                }
            }
            mVideoView.setOnErrorListener { _, _, _ ->
                removeProgressBar()
                removeVideoView()
                Toast.makeText(context, "播放视频失败！", Toast.LENGTH_SHORT).show()
                true
            }
            mVideoView.setMediaController(MediaController(context))
        }
    }
    private val mHttpProxyCacheServer: HttpProxyCacheServer by lazy {
        HttpProxyCacheServerFactory.getProxy(context)
    }

    fun play(videoUrl: String, thumbnailUtl: String) {
        if (mHttpProxyCacheServer.isCached(videoUrl)) {
            // 如果已经缓存，就不显示缩略图了，直接播放
            playVideo(videoUrl)
        } else {
            showThumbnail(thumbnailUtl)
            playVideo(videoUrl)
        }
    }

    private fun showThumbnail(url: String) {
        if (url.isEmpty()) {
            return
        }
        mViewFactory.addThumbnailImageView()
        mViewFactory.addProgressBar()
        mGlideUtils.display(url, mViewFactory.mThumbnailImageView)
    }

    private fun playVideo(url: String) {
        if (url.isEmpty()) {
            mViewFactory.removeProgressBar()
            Toast.makeText(context, "视频地址为空！", Toast.LENGTH_SHORT).show()
            return
        }
        val proxyUrl = mHttpProxyCacheServer.getProxyUrl(url)
        if (proxyUrl.isNullOrEmpty()) {
            mViewFactory.removeProgressBar()
            Toast.makeText(context, "视频地址无效！", Toast.LENGTH_SHORT).show()
            return
        }
        mViewFactory.addProgressBar()
        mViewFactory.mVideoView.setVideoPath(proxyUrl)
        mViewFactory.addVideo()
    }

    fun stop() {
        mViewFactory.mVideoView.stopPlayback()
    }

    fun getVideoView(): VideoView {
        return mViewFactory.mVideoView
    }

}