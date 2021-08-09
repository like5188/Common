package com.like.common.util

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.like.common.R
import com.luck.picture.lib.config.PictureMimeType
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
        imageView.load(getUriByUrl(url))
    }

    /**
     * 加载网络图片适配长图方案
     * # 注意：此方法只有加载网络图片才会回调
     *
     * @param context
     * @param url
     * @param imageView
     * @param longImageView
     * @param callback      网络图片加载回调监听 {link after version 2.5.1 Please use the #OnImageCompleteCallback#}
     */
    override fun loadImage(
        context: Context,
        url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView,
        callback: OnImageCompleteCallback
    ) {
        imageView.load(getUriByUrl(url)) {
            target(
                onStart = {
                    callback.onShowLoading()
                },
                onError = {
                    callback.onHideLoading()
                },
                onSuccess = {
                    callback.onHideLoading()
                    val eqLongImage = MediaUtils.isLongImg(it.intrinsicWidth, it.intrinsicHeight)
                    longImageView.visibility = if (eqLongImage) View.VISIBLE else View.GONE
                    imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
                    if (eqLongImage) {
                        // 加载长图
                        with(longImageView) {
                            isQuickScaleEnabled = true
                            isZoomEnabled = true
                            isPanEnabled = true
                            setDoubleTapZoomDuration(100)
                            setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                            setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                            setImage(ImageSource.bitmap((it as BitmapDrawable).bitmap), ImageViewState(0f, PointF(0f, 0f), 0))
                        }
                    } else {
                        // 普通图片
                        imageView.load(it)
                    }
                })
        }
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
    override fun loadImage(
        context: Context, url: String,
        imageView: ImageView,
        longImageView: SubsamplingScaleImageView
    ) {
    }

    /**
     * 加载相册目录
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
        imageView.load(getUriByUrl(url)) {
            size(180, 180)
            crossfade(true)
            placeholder(R.drawable.picture_image_placeholder)
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
        imageView.load(getUriByUrl(url), CoilImageLoaderFactory.createGifImageLoader(context))
    }

    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        imageView.load(getUriByUrl(url)) {
            crossfade(true)
            placeholder(R.drawable.picture_image_placeholder)
            size(200, 200)
        }
    }

    private fun getUriByUrl(url: String): Uri? {
        return try {
            if (PictureMimeType.isContent(url) || url.startsWith("http") || url.startsWith("https")) {
                Uri.parse(url)
            } else {
                Uri.fromFile(File(url))
            }
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        val instance: CoilEngine by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { CoilEngine() }
    }
}
