package com.like.common.util

import android.content.Context
import android.graphics.PointF
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import coil.load
import coil.request.GetRequest
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.like.common.R
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.luck.picture.lib.tools.MediaUtils
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView
import java.io.File

/**
 * 使用 Coil 实现的 用于 [com.luck.picture.lib.PictureSelector] 的图片加载引擎。
 *
 * @author：luck
 * @date：2019-11-13 17:02
 * @describe：Glide加载引擎
 */
class CoilEngine private constructor() : ImageEngine {
    /**
     * 加载图片
     *
     * @param context
     * @param url
     * @param imageView
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        imageView.load(File(url))
    }

    /**
     * 加载网络图片适配长图方案
     * # 注意：此方法只有加载网络图片才会回调
     *
     * @param context
     * @param url
     * @param imageView
     * @param longImageView
     * @param callback      网络图片加载回调监听
     */
    override fun loadImage(context: Context, url: String, imageView: ImageView, longImageView: SubsamplingScaleImageView, callback: OnImageCompleteCallback) {
        val request = ImageRequest.Builder(context.applicationContext)
                .data(url)
                .target(
                        onStart = {
                            callback.onShowLoading()
                        },
                        onError = {
                            callback.onHideLoading()
                        },
                        onSuccess = {
                            callback.onHideLoading()
                            val resource = it.toBitmap()
                            val eqLongImage = MediaUtils.isLongImg(resource.width, resource.height)
                            longImageView.visibility = if (eqLongImage) View.VISIBLE else View.GONE
                            imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
                            if (eqLongImage) {
                                // 加载长图
                                longImageView.isQuickScaleEnabled = true
                                longImageView.isZoomEnabled = true
                                longImageView.isPanEnabled = true
                                longImageView.setDoubleTapZoomDuration(100)
                                longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                                longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                                longImageView.setImage(ImageSource.bitmap(resource), ImageViewState(0f, PointF(0f, 0f), 0))
                            } else {
                                // 普通图片
                                imageView.setImageBitmap(resource)
                            }
                        })
                .build()
        CoilImageLoaderFactory.createImageLoader(context).enqueue(request)
    }

    /**
     * 加载网络图片适配长图方案
     * # 注意：此方法只有加载网络图片才会回调
     *
     * @param context
     * @param url
     * @param imageView
     * @param longImageView
     * @ 已废弃
     */
    override fun loadImage(context: Context, url: String,
                           imageView: ImageView,
                           longImageView: SubsamplingScaleImageView) {
    }

    /**
     * 加载相册目录
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
        imageView.load(File(url)) {
            crossfade(true)
            placeholder(R.drawable.picture_image_placeholder)
            size(180, 180)
            transformations(RoundedCornersTransformation(8f))
        }
    }

    /**
     * 加载gif
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadAsGifImage(context: Context, url: String, imageView: ImageView) {
        imageView.load(File(url), CoilImageLoaderFactory.createGifImageLoader(context))
    }

    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        imageView.load(File(url)) {
            crossfade(true)
            placeholder(R.drawable.picture_image_placeholder)
            size(200, 200)
        }
    }

    companion object {
        private var instance: CoilEngine? = null
        fun create(): CoilEngine? {
            if (null == instance) {
                synchronized(CoilEngine::class.java) {
                    if (null == instance) {
                        instance = CoilEngine()
                    }
                }
            }
            return instance
        }
    }
}