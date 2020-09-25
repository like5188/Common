package com.like.common.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.createBitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@SuppressLint("MissingPermission")
@TargetApi(Build.VERSION_CODES.KITKAT)
object ImageUtils {
    private val TAG = ImageUtils::class.java.simpleName

    /**
     * 从 Drawable 中提取颜色
     */
    fun getColor(drawable: Drawable?, target: Target, defaultColor: Int): Int {
        drawable ?: return defaultColor
        return getColor(drawable2Bitmap(drawable), target, defaultColor)
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
    @JvmStatic
    fun bytes2Bitmap(bytes: ByteArray?): Bitmap? = if (bytes != null && bytes.isNotEmpty()) {
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } else null

    /**
     * Bitmap转换到Byte[]
     */
    @JvmStatic
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
    @JvmStatic
    fun string2Bytes(imageBase64String: String): ByteArray = Base64.decode(imageBase64String, Base64.DEFAULT)

    /**
     * byte[]转换成Base64字符串
     */
    @JvmStatic
    fun bytes2String(imageData: ByteArray?): String = Base64.encodeToString(imageData, Base64.DEFAULT)

    /**
     * Base64后的image数据转换成Bitmap
     */
    @JvmStatic
    fun string2Bitmap(imageBase64String: String): Bitmap? = bytes2Bitmap(string2Bytes(imageBase64String))

    /**
     * byte[]转换成Base64字符串
     */
    @JvmStatic
    fun bitmap2String(bitmap: Bitmap): String = bytes2String(bitmap2Bytes(bitmap))

    /**
     * 将Bitmap转化为Drawable
     */
    @JvmStatic
    fun bitmap2Drawable(context: Context, bitmap: Bitmap): Drawable = BitmapDrawable(context.resources, bitmap)

    /**
     * 将Drawable转化为Bitmap
     */
    @JvmStatic
    fun drawable2Bitmap(drawable: Drawable): Bitmap? = when (drawable) {
        is BitmapDrawable -> drawable.bitmap
        is NinePatchDrawable -> {
            val bitmap = createBitmap(
                    drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(),
                    if (drawable.getOpacity() != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight())
            drawable.draw(canvas)
            bitmap
        }
        else -> null
    }

    /**
     * 转换成带倒影的图片
     */
    @JvmStatic
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
    @JvmStatic
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
     * 质量压缩，并存储到磁盘。（宽高及内存大小都不变、只改变磁盘大小）。通过quality进行压缩。
     *
     * 适用于上传图片，不适合作为缩略图。不会减少图片的像素，它是在保持像素的前提下改变图片的位深及透明度等，来达到压缩图片的目的。
     * 图片的长，宽，像素都不变，所占内存大小（width*height*一个像素的所占用的字节数）也不会变的。
     * 但是bytes.length是随着quality变小而变小，这样适合去传递二进制的图片数据。
     * 注意：由于png是无损压缩，所以设置quality无效；此方法是通过修改图片的其它比如透明度等属性，使得图片大小变化而已，所以它就无法无限压缩，到达一个值之后就不会继续变小了。
     *
     * @param bitmap            源图片资源
     * @param outFileMaxSize    压缩后的文件最大尺寸  单位:KB
     * @param outFile           压缩后的文件
     */
    @JvmStatic
    fun compressByQualityAndStore(context: Context, bitmap: Bitmap?, outFileMaxSize: Int, outFile: File) {
        if (null == bitmap || bitmap.isRecycled || outFileMaxSize <= 0) return
        if (outFile.isDirectory) {
            throw IllegalArgumentException("outFile参数不能为目录，只能为文件类型。")
        }

        logOrigin(context, bitmap)

        var quality = 100
        ByteArrayOutputStream().use {
            // 注意：这里不能设置为CompressFormat.PNG，因为png图片是无损的，不能进行压缩。bytes.length不会变化。
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)

            // 循环判断压缩后图片是否超过限制大小
            while (it.toByteArray().size / 1024 > outFileMaxSize && quality > 0) {
                quality -= 10
                // 清空baos
                it.reset()
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
            }
        }

        store(bitmap, outFile, quality)
        logCompress(bitmap, outFile)
    }

    /**
     * 采样率压缩（会减小宽高、内存大小、磁盘大小）。通过设置BitmapFactory.Options.inSampleSize，来减小图片的分辨率，进而减小图片所占用的磁盘空间和内存大小。。
     * 注意：该方法只能让宽和高这二者之一达到目标值。
     *
     * 相比直接使用scale方法压缩，效率较高，解析速度快。
     * 但是采样率inSampleSize的取值只能是2的次方数(例如:inSampleSize=15,实际取值为8;
     * inSampleSize=17,实际取值为16;实际取值会往2的次方结算),因此该方法不能精确的指定图片的大小
     *
     * @param imagePath
     * @param reqWidth  px
     * @param reqHeight px
     * @return
     */
    @JvmStatic
    fun scaleByOptions(context: Context, imagePath: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        if (imagePath.isEmpty() || reqWidth <= 0 || reqHeight <= 0) return null

        logOrigin(context, BitmapFactory.decodeFile(imagePath))

        val options = BitmapFactory.Options()
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imagePath, options)
        options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight)//设置缩放比例
        options.inJustDecodeBounds = false
        // 得到的图片的宽或者高会比期望值大一点。
        val compressBitmap = BitmapFactory.decodeFile(imagePath, options)
        logCompress(context, compressBitmap)
        return compressBitmap
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
     * 缩放压缩（会减小宽高、内存大小、磁盘大小）。通过Matrix进行缩放，通过减少图片的像素来降低图片的磁盘空间大小和内存大小，可以用于缓存缩略图。
     * 会保持原图的宽高比。
     * 由于是在原bitmap的基础之上生成的,占内存,效率低
     *
     * @param bitmap
     * @param maxSize 所占内存的最大值。KB
     */
    @JvmStatic
    fun scaleByMatrix(context: Context, bitmap: Bitmap?, maxSize: Int): Bitmap? {
        if (null == bitmap || bitmap.isRecycled || maxSize <= 0) return null

        val r = bitmap.height.toFloat() / bitmap.width.toFloat()
        // 这里/4是因为默认采用的Config.ARGB_8888格式。此时大小=宽*高*4(ARGB_8888格式每个像素占用的空间为4 bytes)
        val newWidth = Math.sqrt(maxSize * 1024.0 / 4 / r).toInt()
        val newHeight = (newWidth * r).toInt()
        return scaleByMatrix(context, bitmap, newWidth, newHeight)
    }

    /**
     * 缩放压缩（会减小宽高、内存大小、磁盘大小）。通过Matrix进行缩放。
     * 不会保持原图的宽高比，但是会按照指定的宽高输出（如果指定的宽高比和原图不一致，会变形）。
     * 由于是在原bitmap的基础之上生成的,占内存,效率低
     *
     * @param bitmap
     * @param reqWidth  目标的宽度 px
     * @param reqHeight 目标的高度 px
     * @return
     */
    @JvmStatic
    fun scaleByMatrix(context: Context, bitmap: Bitmap?, reqWidth: Int, reqHeight: Int): Bitmap? {
        if (null == bitmap || bitmap.isRecycled || reqWidth <= 0 || reqHeight <= 0) return null

        logOrigin(context, bitmap)
        // 最后一个参数filter：如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响，我们这里是缩小图片，所以直接设置为false
        val compressBitmap = Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, true)
        logCompress(context, compressBitmap)
        return compressBitmap
    }

    /**
     * 旋转图片文件到正常角度
     */
    @JvmStatic
    fun rotateBitmap(imagePath: String): Bitmap? {
        if (imagePath.isEmpty()) return null
        val bitmap = BitmapFactory.decodeFile(imagePath)
        // 获取图片文件被旋转的角度
        var degree = 0
        try {
            val exifInterface = ExifInterface(imagePath)
            val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (degree != 0) {
            // 旋转图片 动作
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            // 创建新的图片
            return createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
        return bitmap
    }

    @JvmStatic
    fun getBitmapSizeMB(bitmap: Bitmap?): Double = DoubleFormatUtils.formatTwoDecimals(getBitmapSize(bitmap) / 1024 / 1024.0).toDouble()

    @JvmStatic
    fun getBitmapSizeKB(bitmap: Bitmap?): Double = DoubleFormatUtils.formatTwoDecimals(getBitmapSize(bitmap) / 1024.0).toDouble()

    /**
     * 得到bitmap占用的内存大小
     */
    @JvmStatic
    fun getBitmapSize(bitmap: Bitmap?): Int {
        if (null == bitmap || bitmap.isRecycled) return 0
        return bitmap.allocationByteCount
    }

    /**
     * 把Bitmap存储到本地磁盘
     */
    @JvmStatic
    fun store(bitmap: Bitmap, outFile: File) {
        store(bitmap, outFile, 100)
    }

    /**
     * 把Bitmap按照指定quality压缩，并存储到本地磁盘
     */
    private fun store(bitmap: Bitmap, outFile: File, quality: Int) {
        if (outFile.isDirectory) return
        try {
            if (!outFile.exists()) outFile.createNewFile()
            FileOutputStream(outFile).use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun logOrigin(context: Context, bitmap: Bitmap?) {
        if (null == bitmap || bitmap.isRecycled) {
            Log.i(TAG, "原图：$bitmap")
        } else {
            val file = File(context.externalCacheDir, "cache1.jpg")
            store(bitmap, file)
            Log.v(TAG, "原图：${bitmap.width} X ${bitmap.height}，所占内存大小：${getBitmapSizeKB(bitmap)}KB ${getBitmapSizeMB(bitmap)}MB，所占磁盘大小：${getFileSizeKB(file)}KB ${getFileSizeMB(file)}MB")
            file.delete()
        }
    }

    private fun logCompress(context: Context, bitmap: Bitmap?) {
        if (null == bitmap || bitmap.isRecycled) {
            Log.d(TAG, "缩略图：$bitmap")
        } else {
            val file = File(context.externalCacheDir, "cache2.jpg")
            store(bitmap, file)
            Log.w(TAG, "缩略图：${bitmap.width} X ${bitmap.height}，所占内存大小：${getBitmapSizeKB(bitmap)}KB ${getBitmapSizeMB(bitmap)}MB，所占磁盘大小：${getFileSizeKB(file)}KB ${getFileSizeMB(file)}MB")
            file.delete()
        }
    }

    private fun logCompress(bitmap: Bitmap?, file: File) {
        if (null == bitmap || bitmap.isRecycled) {
            Log.d(TAG, "缩略图：$bitmap")
        } else {
            Log.w(TAG, "缩略图：${bitmap.width} X ${bitmap.height}，所占内存大小：${getBitmapSizeKB(bitmap)}KB ${getBitmapSizeMB(bitmap)}MB，所占磁盘大小：${getFileSizeKB(file)}KB ${getFileSizeMB(file)}MB")
        }
    }

    fun getFileSizeMB(file: File): Double = DoubleFormatUtils.formatTwoDecimals(file.length() / 1024 / 1024.0).toDouble()

    fun getFileSizeKB(file: File): Double = DoubleFormatUtils.formatTwoDecimals(file.length() / 1024.0).toDouble()

    /**
     * 根据视频网络地址获取第一帧图片
     */
    suspend fun getThumbnail(videoUrl: String): Bitmap? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            // 根据网络视频的url获取第一帧--亲测可用。但是这个方法获取本地视频的第一帧，不可用，还没找到方法解决。
            retriever.setDataSource(videoUrl, HashMap())
            // 获得第一帧图片
            retriever.frameAtTime
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        } finally {
            retriever.release()
        }
    }
}