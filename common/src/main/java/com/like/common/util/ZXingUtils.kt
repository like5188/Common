package com.like.common.util

import android.content.Context
import android.graphics.*
import android.view.View
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.shouzhong.scanner.IViewFinder
import java.util.*

object ZXingUtils {

    /**
     * 根据条形码字符串生成条形码图片
     */
    fun createBarCode(content: String, w: Int, h: Int): Bitmap? {
        if (content.isEmpty()) {
            return null
        }
        if (w <= 0 || h <= 0) {
            return null
        }
        // 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        val matrix = MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, w, h)
        //矩阵的宽度
        val matrixWidth = matrix.width
        //矩阵的高度
        val matrixHeight = matrix.height
        //矩阵像素数组
        val pixels = IntArray(matrixWidth * matrixHeight)
        //双重循环遍历每一个矩阵点
        for (y in 0 until matrixHeight) {
            (0 until matrixWidth).filter { matrix.get(it, y) }
                    .forEach {
                        //设置矩阵像素点的值
                        pixels[y * matrixWidth + it] = Color.parseColor("#ff303030")
                    }
        }
        //根据颜色数组来创建位图
        /**
         * 此函数创建位图的过程可以简单概括为为:更加width和height创建空位图，
         * 然后用指定的颜色数组colors来从左到右从上至下一次填充颜色。
         * config是一个枚举，可以用它来指定位图“质量”。
         */
        val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)
        //将生成的条形码返回给调用者
        return bitmap
    }

    /**
     * 生成二维码图片
     */
    fun createQRCode(content: String, w: Int, h: Int, logo: Bitmap? = null): Bitmap? {
        if (content.isEmpty()) {
            return null
        }
        if (w <= 0 || h <= 0) {
            return null
        }
        /*偏移量*/
        var offsetX = w / 2
        var offsetY = h / 2
        /*生成logo*/
        var logoBitmap: Bitmap? = null
        if (logo != null) {
            val matrix = Matrix()
            val scaleFactor = Math.min(w * 1.0f / 5 / logo.width, h * 1.0f / 5 / logo.height)
            matrix.postScale(scaleFactor, scaleFactor)
            logoBitmap = Bitmap.createBitmap(logo, 0, 0, logo.width, logo.height, matrix, true)
        }
        /*如果log不为null,重新计算偏移量*/
        var logoW = 0
        var logoH = 0
        if (logoBitmap != null) {
            logoW = logoBitmap.width
            logoH = logoBitmap.height
            offsetX = (w - logoW) / 2
            offsetY = (h - logoH) / 2
        }
        /*指定为UTF-8*/
        val hints = Hashtable<EncodeHintType, Any?>()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8"
        //容错级别
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        //设置空白边距的宽度
        hints[EncodeHintType.MARGIN] = 0
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        var matrix: BitMatrix? = null
        return try {
            matrix = MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, w, h, hints)
            // 二维矩阵转为一维像素数组,也就是一直横着排了
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    if (x >= offsetX && x < offsetX + logoW && y >= offsetY && y < offsetY + logoH) {
                        var pixel = logoBitmap!!.getPixel(x - offsetX, y - offsetY)
                        if (pixel == 0) {
                            pixel = if (matrix[x, y]) {
                                -0x1000000
                            } else {
                                -0x1
                            }
                        }
                        pixels[y * w + x] = pixel
                    } else {
                        if (matrix[x, y]) {
                            pixels[y * w + x] = -0x1000000
                        } else {
                            pixels[y * w + x] = -0x1
                        }
                    }
                }
            }
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, w, 0, 0, w, h)
            }
        } catch (e: WriterException) {
            print(e)
            null
        }
    }

    /**
     * 配合'com.shouzhong:Scanner:1.1.2-beta1'扫描器库使用的默认扫描界面。
     *
     * @param widthRatio        扫码框宽度占view总宽度的比例
     * @param heightWidthRatio  扫码框的高宽比。一般二维码条形码取值1f，身份证取值0.63f
     * @param laserColor        扫描线颜色
     * @param maskColor         阴影颜色
     * @param borderColor       边框颜色
     * @param borderStrokeWidth 边框宽度
     * @param borderLineLength  边框长度
     */
    class DefaultViewFinder(
            context: Context?,
            private val widthRatio: Float = 0.8f,
            private val heightWidthRatio: Float = 0.63f,
            private val laserColor: Int = -0xff7a89,
            private val maskColor: Int = 0x60000000,
            private val borderColor: Int = -0xff7a89,
            private val borderStrokeWidth: Int = 12,
            private val borderLineLength: Int = 72
    ) : View(context), IViewFinder {
        private var framingRect: Rect? = null //扫码框所占区域
        private val leftOffset = -1 //扫码框相对于左边的偏移量，若为负值，则扫码框会水平居中
        private val topOffset = -1 //扫码框相对于顶部的偏移量，若为负值，则扫码框会竖直居中
        // 扫描线
        private val laserPaint: Paint by lazy {
            Paint().apply {
                color = laserColor
                style = Paint.Style.FILL
            }
        }
        // 阴影遮盖画笔
        private val maskPaint: Paint by lazy {
            Paint().apply {
                color = maskColor
            }
        }
        // 边框画笔
        private val borderPaint: Paint by lazy {
            Paint().apply {
                color = borderColor
                style = Paint.Style.STROKE
                strokeWidth = borderStrokeWidth.toFloat()
                isAntiAlias = true
            }
        }

        private var position = 0

        init {
            setWillNotDraw(false) //需要进行绘制
        }

        override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
            updateFramingRect()
        }

        override fun onDraw(canvas: Canvas) {
            if (getFramingRect() == null) {
                return
            }
            drawViewFinderMask(canvas)
            drawViewFinderBorder(canvas)
            drawLaser(canvas)
        }

        private fun drawLaser(canvas: Canvas) {
            val framingRect = getFramingRect() ?: return
            val top = framingRect.top + 10f + position
            canvas.drawRect(
                    framingRect.left + 10f,
                    top,
                    framingRect.right - 10f,
                    top + 5,
                    laserPaint
            )
            position = if (framingRect.bottom - framingRect.top - 25 < position) 0 else position + 2
            //区域刷新
            postInvalidateDelayed(
                    20,
                    framingRect.left + 10,
                    framingRect.top + 10,
                    framingRect.right - 10,
                    framingRect.bottom - 10
            )
        }

        /**
         * 绘制扫码框四周的阴影遮罩
         */
        private fun drawViewFinderMask(canvas: Canvas) {
            val width: Int = canvas.width
            val height: Int = canvas.height
            val framingRect = getFramingRect() ?: return
            canvas.drawRect(0f, 0f, width.toFloat(), framingRect.top.toFloat(), maskPaint) //扫码框顶部阴影
            canvas.drawRect(
                    0f,
                    framingRect.top.toFloat(),
                    framingRect.left.toFloat(),
                    framingRect.bottom.toFloat(),
                    maskPaint
            ) //扫码框左边阴影
            canvas.drawRect(
                    framingRect.right.toFloat(),
                    framingRect.top.toFloat(),
                    width.toFloat(),
                    framingRect.bottom.toFloat(),
                    maskPaint
            ) //扫码框右边阴影
            canvas.drawRect(
                    0f,
                    framingRect.bottom.toFloat(),
                    width.toFloat(),
                    height.toFloat(),
                    maskPaint
            ) //扫码框底部阴影
        }

        /**
         * 绘制扫码框的边框
         */
        private fun drawViewFinderBorder(canvas: Canvas) {
            val framingRect = getFramingRect() ?: return
            // Top-left corner
            val path = Path()
            path.moveTo(framingRect.left.toFloat(), framingRect.top.toFloat() + borderLineLength)
            path.lineTo(framingRect.left.toFloat(), framingRect.top.toFloat())
            path.lineTo(framingRect.left.toFloat() + borderLineLength, framingRect.top.toFloat())
            canvas.drawPath(path, borderPaint)
            // Top-right corner
            path.moveTo(framingRect.right.toFloat(), framingRect.top.toFloat() + borderLineLength)
            path.lineTo(framingRect.right.toFloat(), framingRect.top.toFloat())
            path.lineTo(framingRect.right.toFloat() - borderLineLength, framingRect.top.toFloat())
            canvas.drawPath(path, borderPaint)
            // Bottom-right corner
            path.moveTo(framingRect.right.toFloat(), framingRect.bottom.toFloat() - borderLineLength)
            path.lineTo(framingRect.right.toFloat(), framingRect.bottom.toFloat())
            path.lineTo(framingRect.right.toFloat() - borderLineLength, framingRect.bottom.toFloat())
            canvas.drawPath(path, borderPaint)
            // Bottom-left corner
            path.moveTo(framingRect.left.toFloat(), framingRect.bottom.toFloat() - borderLineLength)
            path.lineTo(framingRect.left.toFloat(), framingRect.bottom.toFloat())
            path.lineTo(framingRect.left.toFloat() + borderLineLength, framingRect.bottom.toFloat())
            canvas.drawPath(path, borderPaint)
        }

        /**
         * 设置framingRect的值（扫码框所占的区域）
         */
        @Synchronized
        private fun updateFramingRect() {
            val viewSize = Point(width, height)
            val width = (width * widthRatio).toInt()
            val height = (heightWidthRatio * width).toInt()
            val left = if (leftOffset < 0) {
                (viewSize.x - width) / 2 //水平居中
            } else {
                leftOffset
            }
            val top = if (topOffset < 0) {
                (viewSize.y - height) / 2 //竖直居中
            } else {
                topOffset
            }
            framingRect = Rect(left, top, left + width, top + height)
        }

        override fun getFramingRect(): Rect? {
            return framingRect
        }

    }

}