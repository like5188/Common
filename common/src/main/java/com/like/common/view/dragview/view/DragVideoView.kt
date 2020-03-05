package com.like.common.view.dragview.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.VideoView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.like.common.util.RxJavaUtils
import com.like.common.util.StorageUtils
import com.like.common.util.onGlobalLayoutListener
import com.like.common.view.dragview.entity.DragInfo
import java.io.File
import java.net.URL

class DragVideoView(context: Context, info: DragInfo) : BaseDragView(context, info) {
    private val TAG = DragVideoView::class.java.simpleName
    /**
     * 网络视频缓存目录
     */
    private val networkVideoCacheDir = StorageUtils.InternalStorageHelper.getCacheDir(context)
    private val thumbnailImageView: ImageView by lazy {
        ImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }
    }
    private val progressBar: ProgressBar by lazy {
        ProgressBar(context, null, android.R.attr.progressBarStyleInverse).apply {
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(CENTER_IN_PARENT)
            }
        }
    }
    private val videoView: VideoView by lazy {
        VideoView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
                addRule(CENTER_IN_PARENT)
            }
            setZOrderOnTop(true)// 避免闪屏

            setOnPreparedListener { mediaPlayer ->
                try {
                    mediaPlayer?.let {
                        mediaPlayer.isLooping = true
                        mediaPlayer.start()
                        delay100Millis {
                            // 防闪烁
                            removeView(progressBar)
                            removeView(thumbnailImageView)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            setOnErrorListener { _, _, _ ->
                delay1000Millis {
                    removeView(progressBar)
                    removeView(this@apply)
                    Toast.makeText(context, "解析视频数据失败！", Toast.LENGTH_SHORT).show()
                }
                true
            }
        }
    }

    init {
        if (info.thumbImageUrl.isNotEmpty()) {
            addView(thumbnailImageView)
            addView(progressBar)
            mGlideUtils.display(info.thumbImageUrl, thumbnailImageView, object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    delay1000Millis {
                        removeView(progressBar)
                        removeView(thumbnailImageView)
                        Toast.makeText(context, "获取视频缩略图失败！", Toast.LENGTH_SHORT).show()
                    }
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    playVideo(info)
                    return false
                }
            })
        } else {
            addView(progressBar)
            playVideo(info)
        }

        onGlobalLayoutListener {
            enter()
        }

    }

    private fun playVideo(info: DragInfo) {
        if (info.videoUrl.isEmpty()) {
            delay1000Millis {
                removeView(progressBar)
                Toast.makeText(context, "视频地址为空！", Toast.LENGTH_SHORT).show()
            }
            return
        }
        try {
            val url = URL(info.videoUrl)// 根据此处是否抛异常来判断是网络地址还是本地地址。
            // 网络地址
            val fileName = info.videoUrl.split("/").last()
            val file = File(networkVideoCacheDir, fileName)
            if (file.exists()) {
                delay1000Millis {
                    videoView.setVideoPath(file.path)
                    addView(videoView)
                    Log.v(TAG, "从网络视频的缓存中获取了视频：$file")
                }
            } else {// 下载视频
                RxJavaUtils.runIoAndUpdate(
                        {
                            try {
                                if (networkVideoCacheDir.isDirectory && !networkVideoCacheDir.exists()) {
                                    networkVideoCacheDir.mkdirs()
                                }
                                file.writeBytes(url.readBytes())
                                file.path
                            } catch (e: Exception) {
                                e.printStackTrace()
                                ""
                            }
                        },
                        { filePath ->
                            delay1000Millis {
                                videoView.setVideoPath(filePath)
                                addView(videoView)
                                Log.d(TAG, "从网络获取了视频：$filePath")
                            }
                        },
                        { throwable ->
                            delay1000Millis {
                                removeView(progressBar)
                                Toast.makeText(context, "下载网络视频失败！", Toast.LENGTH_SHORT).show()
                            }
                        }
                )
            }
        } catch (e: Exception) {
            // 本地地址
            val file = File(info.videoUrl)
            if (file.exists()) {// 本地视频
                delay1000Millis {
                    videoView.setVideoPath(file.path)
                    addView(videoView)
                    Log.i(TAG, "从本地获取了视频：$file")
                }
            } else {
                delay1000Millis {
                    removeView(progressBar)
                    Toast.makeText(context, "获取本地视频数据失败！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        // 当scale == 1时才能drag
        if (scaleX == 1f && scaleY == 1f) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    onActionDown(event)
                }
                MotionEvent.ACTION_MOVE -> {
                    // 单手指按下，并在Y方向上拖动了一段距离
                    if (event.pointerCount == 1) {
                        setCanvasTranslationX(event.x - mDownX)
                        setCanvasTranslationY(event.y - mDownY)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    onActionUp(event)
                }
            }
        }
        return true
    }

    override fun onDestroy() {
        videoView.stopPlayback()
    }

}