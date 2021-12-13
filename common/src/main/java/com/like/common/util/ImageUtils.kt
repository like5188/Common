package com.like.common.util

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.createBitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@TargetApi(Build.VERSION_CODES.KITKAT)
object ImageUtils {
    private val TAG = ImageUtils::class.java.simpleName

    /**
     * 从 Drawable 中提取颜色
     */
    fun getColor(drawable: Drawable?, target: Target, defaultColor: Int): Int {
        drawable ?: return defaultColor
        return getColor(drawable.toBitmap(), target, defaultColor)
    }

    /**
     * 从 Bitmap 中提取颜色
     */
    fun getColor(bitmap: Bitmap?, target: Target, defaultColor: Int): Int {
        bitmap ?: return defaultColor
        val palette = Palette.from(bitmap).generate()
        return when (target) {
            Target.DARK_MUTED -> palette.getDarkMutedColor(defaultColor)
            Target.DARK_VIBRANT -> palette.getDarkVibrantColor(defaultColor)
            Target.LIGHT_MUTED -> palette.getLightMutedColor(defaultColor)
            Target.LIGHT_VIBRANT -> palette.getLightVibrantColor(defaultColor)
            Target.MUTED -> palette.getMutedColor(defaultColor)
            Target.VIBRANT -> palette.getVibrantColor(defaultColor)
            else -> defaultColor
        }
    }

    /**
     * Byte[]转换到Bitmap
     */
    fun bytes2Bitmap(bytes: ByteArray?): Bitmap? = if (bytes != null && bytes.isNotEmpty()) {
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } else null

    /**
     * Bitmap转换到Byte[]
     */
    fun bitmap2Bytes(bitmap: Bitmap?): ByteArray? {
        if (null == bitmap || bitmap.isRecycled) return null

        ByteArrayOutputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)//将bitmap放入字节数组流中
            it.flush()//将bos流缓存在内存中的数据全部输出，清空缓存
            return it.toByteArray()
        }
    }

    /**
     * Base64后的image数据转换成byte[]
     */
    fun string2Bytes(imageBase64String: String): ByteArray = Base64.decode(imageBase64String, Base64.DEFAULT)

    /**
     * byte[]转换成Base64字符串
     */
    fun bytes2String(imageData: ByteArray?): String = Base64.encodeToString(imageData, Base64.DEFAULT)

    /**
     * Base64后的image数据转换成Bitmap
     */
    fun string2Bitmap(imageBase64String: String): Bitmap? = bytes2Bitmap(string2Bytes(imageBase64String))

    /**
     * byte[]转换成Base64字符串
     */
    fun bitmap2String(bitmap: Bitmap): String = bytes2String(bitmap2Bytes(bitmap))

    /**
     * 转换成带倒影的图片
     */
    fun getReflectionBitmapWithOrigin(bitmap: Bitmap): Bitmap {
        val reflectionGap = 4
        val w = bitmap.width
        val h = bitmap.height

        val matrix = Matrix()
        matrix.preScale(1f, -1f)


        val bitmapWithReflection = createBitmap(w, h + h / 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmapWithReflection)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val defaultPaint = Paint()
        canvas.drawRect(0f, h.toFloat(), w.toFloat(), (h + reflectionGap).toFloat(), defaultPaint)

        val reflectionImage = createBitmap(bitmap, 0, h / 2, w, h / 2, matrix, false)
        canvas.drawBitmap(reflectionImage, 0f, (h + reflectionGap).toFloat(), null)

        val paint = Paint()
        val shader = LinearGradient(
            0f,
            bitmap.height.toFloat(),
            0f,
            (bitmapWithReflection.height + reflectionGap).toFloat(), 0x70ffffff,
            0x00ffffff,
            Shader.TileMode.CLAMP
        )
        paint.shader = shader
        // Set the Transfer mode to be porter duff and destination in
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0f, h.toFloat(), w.toFloat(), (bitmapWithReflection.height + reflectionGap).toFloat(), paint)

        return bitmapWithReflection
    }

    /**
     * 释放图片资源的方法
     *
     * @param imageView
     */
    fun releaseImageViewResource(imageView: ImageView?) {
        imageView ?: return
        val drawable = imageView.drawable
        if (drawable is BitmapDrawable) {
            var bitmap: Bitmap? = drawable.bitmap
            if (bitmap != null && !bitmap.isRecycled) {
                bitmap.recycle()
                bitmap = null
            }
        }
        System.gc()
    }

    /**
     * 质量压缩。（宽高及内存大小都不变、只改变磁盘大小）。
     * 通过 quality 逐渐减小循环进行压缩，直到达到目标尺寸或者 quality 小于等于 0 为止。
     * 使用场景：将图片压缩后将图片上传到服务器，或者保存到本地，根据实际需求
     *
     * 不会减少图片的像素，它是在保持像素的前提下改变图片的位深及透明度等，来达到压缩图片的目的。
     * 图片的长，宽，像素都不变，所占内存大小（width*height*一个像素的所占用的字节数）也不会变的。
     * 但是bytes.length是随着quality变小而变小，这样适合去传递二进制的图片数据。
     * 注意：由于png是无损压缩，所以设置quality无效；此方法是通过修改图片的其它比如透明度等属性，使得图片大小变化而已，所以它就无法无限压缩，到达一个值之后就不会继续变小了。
     *
     * @param bitmap            源图片资源
     * @param maxSize           压缩后的文件最大尺寸  单位:KB
     * @return 压缩后的图片数据
     */
    suspend fun compressByQuality(context: Context, bitmap: Bitmap?, maxSize: Int): ByteArray? = withContext(Dispatchers.IO) {
        if (null == bitmap || bitmap.isRecycled || maxSize <= 0) return@withContext null
        logOrigin(context, bitmap)

        var quality = 100
        ByteArrayOutputStream().use {
            // 注意：这里不能设置为CompressFormat.PNG，因为png图片是无损的，不能进行压缩。bytes.length不会变化。
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)

            // 循环判断压缩后图片是否超过限制大小
            while (it.toByteArray().size / 1024 > maxSize && quality > 0) {
                quality -= 10
                // 清空baos
                it.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
            }

            it.toByteArray().apply {
                logCompress(bitmap, this)
            }
        }
    }

    /**
     * 采样率压缩（会减小宽高、内存大小、磁盘大小）。
     * 通过设置 BitmapFactory.Options.inSampleSize，来减小图片的分辨率，进而减小图片所占用的磁盘空间和内存大小。
     * 注意：该方法只能让宽和高这二者之一达到目标值。
     *
     * 相比直接使用 scale 方法压缩，效率较高，解析速度快。
     * 但是采样率 inSampleSize 的取值只能是 2 的次方数(例如:inSampleSize = 15,实际取值为 8;
     * inSampleSize = 17,实际取值为 16;实际取值会往 2 的次方结算),因此该方法不能精确的指定图片的大小
     *
     * @param imagePath
     * @param reqWidth  px
     * @param reqHeight px
     * @return
     */
    suspend fun compressByInSampleSize(context: Context, imagePath: String, reqWidth: Int, reqHeight: Int): Bitmap? =
        withContext(Dispatchers.IO) {
            if (imagePath.isEmpty() || reqWidth <= 0 || reqHeight <= 0) return@withContext null

            logOrigin(context, BitmapFactory.decodeFile(imagePath))

            val options = BitmapFactory.Options()
            // 开始读入图片，当inJustDecodeBounds设置为true的时候，BitmapFactory通过decodeXXXX解码图片时，将会返回空(null)的Bitmap对象，这样可以避免Bitmap的内存分配，但是它可以返回Bitmap的宽度、高度以及MimeType。
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)
            options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight)//设置缩放比例
            options.inJustDecodeBounds = false
            // 得到的图片的宽或者高会比期望值大一点。
            BitmapFactory.decodeFile(imagePath, options)?.apply {
                logCompress(context, this)
            }
        }

    /**
     * 计算采样率
     *
     * @param srcWidth  原图的宽度 px
     * @param srcHeight 原图的高度 px
     * @param reqWidth  目标的宽度 px
     * @param reqHeight 目标的高度 px
     */
    private fun calculateInSampleSize(srcWidth: Int, srcHeight: Int, reqWidth: Int, reqHeight: Int): Int {
        var inSampleSize = 1
        if (srcHeight > reqHeight || srcWidth > reqWidth) {
            val halfHeight = srcHeight / 2
            val halfWidth = srcWidth / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * 缩放压缩（会减小宽高、内存大小、磁盘大小）。
     * 通过 Matrix 进行缩放，通过减少图片的像素来降低图片的磁盘空间大小和内存大小，可以用于缓存缩略图。
     * 会保持原图的宽高比。
     * 由于是在原 bitmap 的基础之上生成的,占内存,效率低
     *
     * @param bitmap
     * @param maxSize 所占内存的最大值。KB
     */
    suspend fun compressByMatrix(context: Context, bitmap: Bitmap?, maxSize: Int): Bitmap? = withContext(Dispatchers.IO) {
        if (null == bitmap || bitmap.isRecycled || maxSize <= 0) return@withContext null

        val r = bitmap.height.toFloat() / bitmap.width.toFloat()
        // 这里/4是因为默认采用的Config.ARGB_8888格式。此时大小=宽*高*4(ARGB_8888 格式每个像素占用的空间为 4 bytes；RGB_565 是 2 bytes)
        val newWidth = Math.sqrt(maxSize * 1024.0 / 4 / r).toInt()
        val newHeight = (newWidth * r).toInt()
        compressByMatrix(context, bitmap, newWidth, newHeight)
    }

    /**
     * 缩放压缩（会减小宽高、内存大小、磁盘大小）。
     * 通过 Matrix 进行缩放。
     * 不会保持原图的宽高比，但是会按照指定的宽高输出（如果指定的宽高比和原图不一致，会变形）。
     * 由于是在原 bitmap 的基础之上生成的,占内存,效率低
     *
     * @param bitmap
     * @param reqWidth  目标的宽度 px
     * @param reqHeight 目标的高度 px
     * @return
     */
    suspend fun compressByMatrix(context: Context, bitmap: Bitmap?, reqWidth: Int, reqHeight: Int): Bitmap? = withContext(Dispatchers.IO) {
        if (null == bitmap || bitmap.isRecycled || reqWidth <= 0 || reqHeight <= 0) return@withContext null

        logOrigin(context, bitmap)
        // 最后一个参数filter：如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true).apply {
            logCompress(context, this)
        }
    }

    /**
     * 旋转图片文件到正常角度
     */
    suspend fun rotateBitmap(imagePath: String): Bitmap? = withContext(Dispatchers.IO) {
        if (imagePath.isEmpty()) return@withContext null
        val bitmap = BitmapFactory.decodeFile(imagePath)
        // 获取图片文件被旋转的角度
        val degree = try {
            when (ExifInterface(imagePath).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: IOException) {
            e.printStackTrace()
            0
        }
        if (degree != 0) {
            // 旋转图片 动作
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            // 创建新的图片
            createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }

    fun getBitmapSizeMB(bitmap: Bitmap?): Double = getBitmapSize(bitmap) / 1024.0 / 1024.0

    fun getBitmapSizeKB(bitmap: Bitmap?): Double = getBitmapSize(bitmap) / 1024.0

    /**
     * 得到bitmap占用的内存大小
     */
    fun getBitmapSize(bitmap: Bitmap?): Int {
        if (null == bitmap || bitmap.isRecycled) return 0
        return bitmap.allocationByteCount
    }

    /**
     * 把 Bitmap 按照指定 quality 压缩，并存储到本地磁盘
     */
    suspend fun store(bitmap: Bitmap, outFile: File, quality: Int = 100): Boolean = withContext(Dispatchers.IO) {
        if (outFile.isDirectory) return@withContext false
        try {
            if (!outFile.exists()) outFile.createNewFile()
            FileOutputStream(outFile).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
                return@withContext true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext false
    }


    /**
     * 把图片数据存储到本地磁盘
     */
    suspend fun store(data: ByteArray, outFile: File): Boolean = withContext(Dispatchers.IO) {
        if (outFile.isDirectory) return@withContext false
        try {
            if (!outFile.exists()) outFile.createNewFile()
            FileOutputStream(outFile).use {
                it.write(data)
                return@withContext true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext false
    }

    /**
     * 根据视频网络地址获取第一帧图片
     */
    suspend fun getThumbnail(videoUrl: String?): Bitmap? = withContext(Dispatchers.IO) {
        if (videoUrl.isNullOrEmpty()) return@withContext null
        try {
            MediaMetadataRetriever().use {
                // 根据网络视频的url获取第一帧--亲测可用。但是这个方法获取本地视频的第一帧，不可用，还没找到方法解决。
                it.setDataSource(videoUrl, HashMap())
                // 获得第一帧图片
                return@withContext it.frameAtTime
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        return@withContext null
    }

    fun getFileLengthMB(file: File): Double = file.length() / 1024.0 / 1024.0

    fun getFileLengthKB(file: File): Double = file.length() / 1024.0

    private suspend fun logOrigin(context: Context, bitmap: Bitmap?) {
        if (null == bitmap || bitmap.isRecycled) {
            Log.i(TAG, "原图：$bitmap")
        } else {
            val file = File(context.externalCacheDir, "cache1.jpg")
            store(bitmap, file)
            Log.v(
                TAG,
                "原图：${bitmap.width} X ${bitmap.height}，所占内存大小：${getBitmapSizeKB(bitmap).maximumFractionDigits(2)}KB ${
                    getBitmapSizeMB(bitmap).maximumFractionDigits(
                        2
                    )
                }MB，所占磁盘大小：${
                    getFileLengthKB(file).maximumFractionDigits(2)
                }KB ${getFileLengthMB(file).maximumFractionDigits(2)}MB"
            )
            file.delete()
        }
    }

    private suspend fun logCompress(context: Context, bitmap: Bitmap?) {
        if (null == bitmap || bitmap.isRecycled) {
            Log.d(TAG, "缩略图：$bitmap")
        } else {
            val file = File(context.externalCacheDir, "cache2.jpg")
            store(bitmap, file)
            Log.w(
                TAG,
                "缩略图：${bitmap.width} X ${bitmap.height}，所占内存大小：${getBitmapSizeKB(bitmap).maximumFractionDigits(2)}KB ${
                    getBitmapSizeMB(
                        bitmap
                    ).maximumFractionDigits(2)
                }MB，所占磁盘大小：${
                    getFileLengthKB(file).maximumFractionDigits(2)
                }KB ${getFileLengthMB(file).maximumFractionDigits(2)}MB"
            )
            file.delete()
        }
    }

    private fun logCompress(bitmap: Bitmap?, data: ByteArray) {
        if (null == bitmap || bitmap.isRecycled) {
            Log.d(TAG, "缩略图：$bitmap")
        } else {
            Log.w(
                TAG,
                "缩略图：${bitmap.width} X ${bitmap.height}，所占内存大小：${getBitmapSizeKB(bitmap).maximumFractionDigits(2)}KB ${
                    getBitmapSizeMB(
                        bitmap
                    ).maximumFractionDigits(2)
                }MB，所占磁盘大小：${
                    (data.size / 1024.0).maximumFractionDigits(2)
                }KB ${(data.size / 1024.0 / 1024.0).maximumFractionDigits(2)}MB"
            )
        }
    }
}