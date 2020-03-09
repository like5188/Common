package com.like.common.view.dragview.view.video

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
import com.like.common.view.dragview.view.util.HttpProxyCacheServerFactory
import com.like.common.view.dragview.view.util.ViewFactory
import com.like.common.view.dragview.view.util.postDelayed

/**
 * 封装了缩略图、进度条、VideoView、拖动操作
 */
class CustomVideoView(context: Context, info: DragInfo) : BaseDragView(context, info) {
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
            mVideoView.setOnCompletionListener {
                mVideoView.resume()// 重新播放
            }
            mVideoView.setOnErrorListener { _, _, _ ->
                postDelayed(1000) {
                    removeProgressBar()
                    removeVideoView()
                    Toast.makeText(context, "播放视频失败！", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }
    }

    init {
        onPreDrawListener {
            enter()
            play(info.videoUrl, info.thumbImageUrl)
        }
    }

    fun play(videoUrl: String, thumbImageUrl: String = "") {
        if (thumbImageUrl.isNotEmpty()) {
            mViewFactory.addThumbnailImageView()
            mViewFactory.addProgressBar()
            mGlideUtils.display(thumbImageUrl, mViewFactory.mThumbnailImageView, object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    postDelayed(1000) {
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
            postDelayed(1000) {
                mViewFactory.removeProgressBar()
                Toast.makeText(context, "视频地址为空！", Toast.LENGTH_SHORT).show()
            }
            return
        }
        val proxyUrl = HttpProxyCacheServerFactory.getProxy(context)?.getProxyUrl(videoUrl)
        if (proxyUrl.isNullOrEmpty()) {
            postDelayed(1000) {
                mViewFactory.removeProgressBar()
                Toast.makeText(context, "视频地址无效！", Toast.LENGTH_SHORT).show()
            }
            return
        }
        mViewFactory.mVideoView.setVideoPath(proxyUrl)
        mViewFactory.addVideo()
    }

    override fun handleMoveEvent(event: MotionEvent, dx: Float, dy: Float): Boolean {
        return true
    }

    override fun onDestroy() {
        mViewFactory.mVideoView.stopPlayback()
    }

}