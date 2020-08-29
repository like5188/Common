package com.like.common.util

import android.content.Context
import android.os.Build
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import coil.fetch.VideoFrameFileFetcher
import coil.fetch.VideoFrameUriFetcher

object CoilImageLoaderFactory {
    fun createImageLoader(context: Context): ImageLoader = Coil.imageLoader(context.applicationContext)

    /*
    imageView.load(File(url), CoilImageLoaderFactory.createGifImageLoader(context))
     */
    fun createGifImageLoader(context: Context): ImageLoader = ImageLoader.Builder(context.applicationContext)
            .componentRegistry {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder())
                } else {
                    add(GifDecoder())
                }
            }
            .build()

    fun createSvgImageLoader(context: Context): ImageLoader = ImageLoader.Builder(context.applicationContext)
            .componentRegistry {
                add(SvgDecoder(context.applicationContext))
            }
            .build()

    /*
    imageView.load(File("/path/to/video.mp4"), CoilImageLoaderFactory.createVideoFrameImageLoader(context)) {
        videoFrameMillis(1000)
    }
     */
    fun createVideoFrameImageLoader(context: Context): ImageLoader = ImageLoader.Builder(context)
            .componentRegistry {
                add(VideoFrameFileFetcher(context.applicationContext))
                add(VideoFrameUriFetcher(context.applicationContext))
            }
            .build()
}