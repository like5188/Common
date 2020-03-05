package com.like.common.view.dragview.view.video

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
import com.like.common.util.RxJavaUtils
import com.like.common.util.StorageUtils
import com.like.common.view.dragview.view.util.ViewHelper
import com.like.common.view.dragview.view.util.delay1000Millis
import java.io.File
import java.net.URL

/**
 * 封装了缩略图、进度条、VideoView
 */
class CustomVideoView(context: Context) : FrameLayout(context) {
    private val TAG = CustomVideoView::class.java.simpleName
    private val mGlideUtils: GlideUtils by lazy { GlideUtils(context) }
    private val mNetworkVideoCacheDir = StorageUtils.InternalStorageHelper.getCacheDir(context)
    private val mViewHelper: ViewHelper by lazy {
        ViewHelper(this)
    }

    fun play(videoUrl: String, thumbImageUrl: String = "") {
        if (thumbImageUrl.isNotEmpty()) {
            mViewHelper.addThumbnailImageView()
            mViewHelper.addProgressBar()
            mGlideUtils.display(thumbImageUrl, mViewHelper.mThumbnailImageView, object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    delay1000Millis {
                        mViewHelper.removeProgressBar()
                        mViewHelper.removeThumbnailImageView()
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
        try {
            val url = URL(videoUrl)// 根据此处是否抛异常来判断是网络地址还是本地地址。
            // 网络地址
            val fileName = videoUrl.split("/").last()
            val file = File(mNetworkVideoCacheDir, fileName)
            if (file.exists()) {
                delay1000Millis {
                    mViewHelper.mVideoView.setVideoPath(file.path)
                    mViewHelper.addVideo()
                    Log.v(TAG, "从网络视频的缓存中获取了视频：$file")
                }
            } else {// 下载视频
                RxJavaUtils.runIoAndUpdate(
                        {
                            try {
                                if (mNetworkVideoCacheDir.isDirectory && !mNetworkVideoCacheDir.exists()) {
                                    mNetworkVideoCacheDir.mkdirs()
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
                                mViewHelper.mVideoView.setVideoPath(filePath)
                                mViewHelper.addVideo()
                                Log.d(TAG, "从网络获取了视频：$filePath")
                            }
                        },
                        { throwable ->
                            delay1000Millis {
                                mViewHelper.removeProgressBar()
                                Toast.makeText(context, "下载网络视频失败！", Toast.LENGTH_SHORT).show()
                            }
                        }
                )
            }
        } catch (e: Exception) {
            delay1000Millis {
                mViewHelper.removeProgressBar()
                Toast.makeText(context, "视频地址无效！", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun stop() {
        mViewHelper.mVideoView.stopPlayback()
    }

}